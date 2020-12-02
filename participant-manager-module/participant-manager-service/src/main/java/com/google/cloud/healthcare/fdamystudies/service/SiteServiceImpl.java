/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CLOSE_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DEACTIVATED;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DEFAULT_PERCENTAGE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_REGEX;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.INACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.IN_PROGRESS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.STATUS_ACTIVE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.YET_TO_ENROLL;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.ENROLLMENT_TARGET_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.INVITATION_EMAIL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.INVITATION_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORTED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_EMAIL_ADDED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_DISABLED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_ENABLED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_ACTIVATED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_DECOMMISSIONED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_PARTICIPANT_REGISTRY_VIEWED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ConsentHistory;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.Enrollment;
import com.google.cloud.healthcare.fdamystudies.beans.ImportParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantRequest;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteDetails;
import com.google.cloud.healthcare.fdamystudies.beans.SiteDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentResponse;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.SiteStatus;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.ParticipantMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.SiteMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.StudyMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.EnrolledInvitedCount;
import com.google.cloud.healthcare.fdamystudies.model.InviteParticipantEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteCount;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteUserInfo;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudySiteInfo;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.InviteParticipantsEmailRepository;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyConsentRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SiteServiceImpl implements SiteService {

  private static final String CREATED = "created";

  private static final int EMAIL_ADDRESS_COLUMN = 1;

  private XLogger logger = XLoggerFactory.getXLogger(SiteServiceImpl.class.getName());

  @Autowired private SiteRepository siteRepository;

  @Autowired private LocationRepository locationRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private StudyPermissionRepository studyPermissionRepository;

  @Autowired private AppPermissionRepository appPermissionRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private StudyConsentRepository studyConsentRepository;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private AppPropertyConfig appPropertyConfig;

  @Autowired private EmailService emailService;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Autowired private InviteParticipantsEmailRepository invitedParticipantsEmailRepository;

  @Override
  @Transactional
  public SiteResponse addSite(SiteRequest siteRequest) {
    logger.entry("begin addSite()");

    Optional<UserRegAdminEntity> optUser = userRegAdminRepository.findById(siteRequest.getUserId());
    UserRegAdminEntity userRegAdmin =
        optUser.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));

    Optional<LocationEntity> optLocation = locationRepository.findById(siteRequest.getLocationId());
    LocationEntity location =
        optLocation.orElseThrow(() -> new ErrorCodeException(ErrorCode.LOCATION_NOT_FOUND));

    if (location.getStatus().equals(INACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.CANNOT_ADD_SITE_FOR_DECOMMISSIONED_lOCATION);
    }

    Optional<StudyEntity> optStudyEntity = studyRepository.findById(siteRequest.getStudyId());
    StudyEntity study =
        optStudyEntity.orElseThrow(() -> new ErrorCodeException(ErrorCode.STUDY_NOT_FOUND));

    if (OPEN_STUDY.equalsIgnoreCase(study.getType())) {
      throw new ErrorCodeException(ErrorCode.CANNOT_ADD_SITE_FOR_OPEN_STUDY);
    }

    if (DEACTIVATED.equalsIgnoreCase(study.getStatus())) {
      throw new ErrorCodeException(ErrorCode.CANNOT_ADD_SITE_FOR_DEACTIVATED_STUDY);
    }

    List<SiteEntity> sitesList =
        siteRepository.findByLocationIdAndStudyId(
            siteRequest.getLocationId(), siteRequest.getStudyId());
    if (CollectionUtils.isNotEmpty(sitesList)) {
      throw new ErrorCodeException(ErrorCode.SITE_EXISTS);
    }

    if (!userRegAdmin.isSuperAdmin()
        && !isEditPermissionAllowedForStudy(siteRequest.getStudyId(), siteRequest.getUserId())) {
      throw new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED);
    }

    SiteResponse siteResponse =
        saveSiteWithSitePermissions(siteRequest.getUserId(), location, study);
    logger.exit(
        String.format(
            "Site %s added to locationId=%s and studyId=%s",
            siteResponse.getSiteId(), siteRequest.getLocationId(), siteRequest.getStudyId()));
    return new SiteResponse(siteResponse.getSiteId(), MessageCode.ADD_SITE_SUCCESS);
  }

  private SiteResponse saveSiteWithSitePermissions(
      String userId, LocationEntity location, StudyEntity study) {
    logger.entry("saveSiteWithStudyPermission()");

    SiteEntity site = new SiteEntity();
    site.setCreatedBy(userId);
    site.setStatus(SiteStatus.ACTIVE.value());
    site.setStudy(study);
    site.setLocation(location);

    site = siteRepository.save(site);

    siteRepository.addSitePermissions(study.getId(), site.getId());

    logger.exit(String.format("saved siteId=%s", site.getId()));
    return SiteMapper.toSiteResponse(site);
  }

  @Override
  @Transactional
  public ParticipantResponse addNewParticipant(
      ParticipantDetailRequest participant, String userId, AuditLogEventRequest auditRequest) {
    logger.entry("begin addNewParticipant()");

    Optional<SiteEntity> optSite = siteRepository.findById(participant.getSiteId());

    if (!optSite.isPresent() || !optSite.get().getStatus().equals(ACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    SiteEntity site = optSite.get();
    ErrorCode errorCode = validationForAddNewParticipant(participant, userId, site);
    if (errorCode != null) {
      throw new ErrorCodeException(errorCode);
    }

    ParticipantRegistrySiteEntity participantRegistrySite =
        ParticipantMapper.fromParticipantRequest(participant, site);
    participantRegistrySite.setCreatedBy(userId);
    participantRegistrySite.setEnrollmentTokenExpiry(
        new Timestamp(
            Instant.now()
                .plus(appPropertyConfig.getEnrollmentTokenExpiryInHours(), ChronoUnit.HOURS)
                .toEpochMilli()));
    participantRegistrySite =
        participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);
    ParticipantResponse response =
        new ParticipantResponse(
            MessageCode.ADD_PARTICIPANT_SUCCESS, participantRegistrySite.getId(), Boolean.TRUE);

    auditRequest.setAppId(site.getStudy().getAppId());
    auditRequest.setStudyId(site.getStudyId());
    auditRequest.setUserId(userId);
    auditRequest.setSiteId(site.getId());
    auditRequest.setParticipantId(participantRegistrySite.getId());

    Map<String, String> map = Collections.singletonMap("site_id", site.getId());
    participantManagerHelper.logEvent(PARTICIPANT_EMAIL_ADDED, auditRequest, map);

    logger.exit(String.format("participantRegistrySiteId=%s", participantRegistrySite.getId()));
    return response;
  }

  private ErrorCode validationForAddNewParticipant(
      ParticipantDetailRequest participant, String userId, SiteEntity site) {

    Optional<UserRegAdminEntity> optUserRegAdminEntity = validateUserId(userId);

    if (!optUserRegAdminEntity.get().isSuperAdmin()) {
      Optional<SitePermissionEntity> optSitePermission =
          sitePermissionRepository.findByUserIdAndSiteId(userId, participant.getSiteId());

      if (!optSitePermission.isPresent()
          || !optSitePermission.get().getCanEdit().equals(Permission.EDIT)) {
        return ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED;
      }
    }

    if (site.getStudy() != null && OPEN_STUDY.equals(site.getStudy().getType())) {
      return ErrorCode.OPEN_STUDY;
    }

    List<ParticipantRegistrySiteEntity> registryList =
        participantRegistrySiteRepository.findByStudyIdAndEmail(
            site.getStudy().getId(), participant.getEmail());

    if (CollectionUtils.isNotEmpty(registryList)) {
      for (ParticipantRegistrySiteEntity participantRegistrySite : registryList) {
        if (!participantRegistrySite
                .getOnboardingStatus()
                .equals(OnboardingStatus.DISABLED.getCode())
            || participantRegistrySite.getSite().equals(site)) {
          return ErrorCode.USER_EMAIL_EXIST;
        }
      }
    }
    return null;
  }

  @Override
  public ParticipantRegistryResponse getParticipants(
      String userId,
      String siteId,
      String onboardingStatus,
      String[] excludeEnrollmentStatus,
      AuditLogEventRequest auditRequest,
      Integer page,
      Integer limit) {
    logger.info("getParticipants()");
    Optional<SiteEntity> optSite = siteRepository.findById(siteId);

    if (!optSite.isPresent()) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_FOUND);
    }

    ParticipantRegistryDetail participantRegistryDetail = null;
    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!optUserRegAdminEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    if (optUserRegAdminEntity.get().isSuperAdmin()) {
      participantRegistryDetail =
          ParticipantMapper.fromSite(optSite.get(), Permission.EDIT, siteId);
      participantRegistryDetail.setStudyPermission(Permission.EDIT.value());
    } else {
      Optional<SitePermissionEntity> optSitePermission =
          sitePermissionRepository.findByUserIdAndSiteId(userId, siteId);

      if (!optSitePermission.isPresent()
          || Permission.NO_PERMISSION
              == Permission.fromValue(optSitePermission.get().getCanEdit().value())) {
        throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
      }
      participantRegistryDetail =
          ParticipantMapper.fromSite(optSite.get(), optSitePermission.get().getCanEdit(), siteId);

      Optional<StudyPermissionEntity> studyPermissionOpt =
          studyPermissionRepository.findByStudyIdAndUserId(
              participantRegistryDetail.getStudyId(), userId);
      if (studyPermissionOpt.isPresent()) {
        participantRegistryDetail.setStudyPermission(studyPermissionOpt.get().getEdit().value());
      }
    }

    Map<String, Long> statusWithCountMap =
        getOnboardingStatusWithCount(siteId, excludeEnrollmentStatus);
    participantRegistryDetail.setCountByStatus(statusWithCountMap);

    Page<ParticipantRegistrySiteEntity> participantRegistrySitesPage = null;
    Long totalParticipantsCount = null;
    List<ParticipantRegistrySiteEntity> participantRegistrySites = null;

    if (StringUtils.isEmpty(onboardingStatus)) {
      totalParticipantsCount = participantRegistrySiteRepository.countbysiteId(siteId);

      if (page != null && limit != null) {
        participantRegistrySitesPage =
            participantRegistrySiteRepository.findBySiteIdForPage(
                siteId, PageRequest.of(page, limit, Sort.by(CREATED).descending()));
        participantRegistrySites = participantRegistrySitesPage.getContent();
      } else {
        participantRegistrySites = participantRegistrySiteRepository.findBySiteId(siteId);
      }
    } else {
      totalParticipantsCount =
          participantRegistrySiteRepository.countBySiteIdAndStatus(siteId, onboardingStatus);

      if (page != null && limit != null) {
        participantRegistrySitesPage =
            participantRegistrySiteRepository.findBySiteIdAndStatusForPage(
                siteId,
                onboardingStatus,
                PageRequest.of(page, limit, Sort.by(CREATED).descending()));
        participantRegistrySites = participantRegistrySitesPage.getContent();
      } else {
        participantRegistrySites =
            participantRegistrySiteRepository.findBySiteIdAndStatus(siteId, onboardingStatus);
      }
    }

    addRegistryParticipants(
        participantRegistryDetail, participantRegistrySites, excludeEnrollmentStatus);

    ParticipantRegistryResponse participantRegistryResponse =
        new ParticipantRegistryResponse(
            MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS, participantRegistryDetail);

    participantRegistryResponse.setTotalParticipantCount(totalParticipantsCount);
    auditRequest.setSiteId(siteId);
    auditRequest.setStudyId(optSite.get().getStudyId());
    auditRequest.setAppId(optSite.get().getStudy().getAppId());
    auditRequest.setUserId(userId);

    Map<String, String> map = Collections.singletonMap("site_id", siteId);
    participantManagerHelper.logEvent(SITE_PARTICIPANT_REGISTRY_VIEWED, auditRequest, map);

    logger.exit(String.format("message=%s", participantRegistryResponse.getMessage()));
    return participantRegistryResponse;
  }

  private Map<String, Long> getOnboardingStatusWithCount(
      String siteId, String[] excludeEnrollmentStatus) {

    List<ParticipantRegistrySiteCount> statusCount = null;
    if (ArrayUtils.isEmpty(excludeEnrollmentStatus)) {
      statusCount =
          (List<ParticipantRegistrySiteCount>)
              CollectionUtils.emptyIfNull(
                  participantRegistrySiteRepository.findStatusCountBySiteId(siteId));
    } else {
      statusCount =
          (List<ParticipantRegistrySiteCount>)
              CollectionUtils.emptyIfNull(
                  participantRegistrySiteRepository.findStatusCountBySiteIdAndStatus(
                      siteId, excludeEnrollmentStatus));
    }

    Map<String, Long> statusWithCountMap = new HashMap<>();
    for (OnboardingStatus onboardingStatus : OnboardingStatus.values()) {
      statusWithCountMap.put(onboardingStatus.getCode(), (long) 0);
    }

    long total = 0;
    for (ParticipantRegistrySiteCount count : statusCount) {
      total += count.getCount();
      statusWithCountMap.put(count.getOnboardingStatus(), count.getCount());
    }

    statusWithCountMap.put(OnboardingStatus.ALL.getCode(), total);
    return statusWithCountMap;
  }

  private void addRegistryParticipants(
      ParticipantRegistryDetail participantRegistryDetail,
      List<ParticipantRegistrySiteEntity> participantRegistrySites,
      String[] excludeEnrollmentStatus) {
    List<String> registryIds =
        CollectionUtils.emptyIfNull(participantRegistrySites)
            .stream()
            .map(ParticipantRegistrySiteEntity::getId)
            .collect(Collectors.toList());

    List<ParticipantStudyEntity> participantStudies = new ArrayList<>();
    // Check not empty for Ids to avoid SQLSyntaxErrorException
    if (CollectionUtils.isNotEmpty(registryIds)) {
      participantStudies =
          (List<ParticipantStudyEntity>)
              CollectionUtils.emptyIfNull(
                  participantStudyRepository.findParticipantsByParticipantRegistrySite(
                      registryIds));
    }

    for (ParticipantRegistrySiteEntity participantRegistrySite : participantRegistrySites) {
      ParticipantDetail participant = new ParticipantDetail();
      participant =
          ParticipantMapper.toParticipantDetails(
              participantStudies, participantRegistrySite, participant);
      if (!ArrayUtils.contains(excludeEnrollmentStatus, participant.getEnrollmentStatus())) {
        participantRegistryDetail.getRegistryParticipants().add(participant);
      }
    }
  }

  private boolean isEditPermissionAllowedForStudy(String studyId, String userId) {
    logger.entry("isEditPermissionAllowed()");
    Optional<StudyPermissionEntity> optStudyPermissionEntity =
        studyPermissionRepository.findByStudyIdAndUserId(studyId, userId);
    StudyPermissionEntity studyPermission =
        optStudyPermissionEntity.orElseThrow(
            () -> new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED));

    logger.exit(String.format("edit permission=%s", studyPermission.getEdit()));
    return studyPermission.getEdit() == Permission.EDIT;
  }

  @Override
  @Transactional
  public SiteStatusResponse toggleSiteStatus(
      String userId, String siteId, AuditLogEventRequest auditRequest) {
    logger.entry("toggleSiteStatus()");

    Optional<UserRegAdminEntity> optUser = userRegAdminRepository.findById(userId);
    if (!optUser.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    UserRegAdminEntity user = optUser.get();

    validateDecommissionSiteRequest(userId, siteId, user);

    Optional<SiteEntity> optSiteEntity = siteRepository.findById(siteId);
    auditRequest.setUserId(userId);
    auditRequest.setSiteId(siteId);
    auditRequest.setStudyId(optSiteEntity.get().getStudyId());

    Map<String, String> map = Collections.singletonMap("site_id", siteId);

    SiteEntity site = optSiteEntity.get();

    if (SiteStatus.DEACTIVE == SiteStatus.fromValue(site.getStatus())) {
      checkPreConditionsForSiteActivate(site);

      site.setStatus(SiteStatus.ACTIVE.value());
      site = siteRepository.saveAndFlush(site);

      participantManagerHelper.logEvent(SITE_ACTIVATED_FOR_STUDY, auditRequest, map);

      logger.exit(String.format(" Site status changed to ACTIVE for siteId=%s", site.getId()));
      return new SiteStatusResponse(
          site.getId(), site.getStatus(), MessageCode.RECOMMISSION_SITE_SUCCESS);
    }

    site.setStatus(SiteStatus.DEACTIVE.value());
    siteRepository.saveAndFlush(site);

    updateSitePermissions(siteId);

    deactivateYetToEnrollParticipants(siteId);

    participantManagerHelper.logEvent(SITE_DECOMMISSIONED_FOR_STUDY, auditRequest, map);

    logger.exit(String.format("Site status changed to DEACTIVE for siteId=%s", site.getId()));
    return new SiteStatusResponse(
        site.getId(), site.getStatus(), MessageCode.DECOMMISSION_SITE_SUCCESS);
  }

  private void checkPreConditionsForSiteActivate(SiteEntity site) {
    Optional<LocationEntity> optLocation = locationRepository.findById(site.getLocation().getId());
    if (optLocation.get().getStatus().equals(CommonConstants.INACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.LOCATION_DECOMMISSIONED);
    }

    Optional<StudyEntity> optStudyEntity = studyRepository.findById(site.getStudyId());
    if (optStudyEntity.get().getStatus().equals(DEACTIVATED)) {
      throw new ErrorCodeException(ErrorCode.CANNOT_ACTIVATE_SITE_FOR_DEACTIVATED_STUDY);
    }
  }

  private void validateDecommissionSiteRequest(
      String userId, String siteId, UserRegAdminEntity user) {

    StudyEntity study = null;
    if (user.isSuperAdmin()) {
      Optional<SiteEntity> optSite = siteRepository.findById(siteId);
      if (optSite.isPresent()) {
        SiteEntity site = optSite.get();
        study = site.getStudy();
      }
    } else {
      Optional<SiteEntity> optSite = siteRepository.findById(siteId);
      if (!optSite.isPresent()) {
        throw new ErrorCodeException(ErrorCode.SITE_NOT_FOUND);
      }

      study = optSite.get().getStudy();
      if (!isEditPermissionAllowedForStudy(study.getId(), userId)) {
        throw new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED);
      }
    }

    if (OPEN.equalsIgnoreCase(study.getType())) {
      throw new ErrorCodeException(ErrorCode.CANNOT_DECOMMISSION_SITE_FOR_OPEN_STUDY);
    }

    List<String> status = Arrays.asList(IN_PROGRESS, STATUS_ACTIVE);
    Optional<Long> optParticipantStudyCount =
        participantStudyRepository.findByStudySiteIdAndStatus(status, study.getId(), siteId);

    if (optParticipantStudyCount.isPresent()
        && optParticipantStudyCount.get() > 0
        && study.getStatus().equals(CommonConstants.STATUS_ACTIVE)) {
      throw new ErrorCodeException(ErrorCode.CANNOT_DECOMMISSION_SITE_FOR_ENROLLED_ACTIVE_STATUS);
    }
  }

  private void updateSitePermissions(String siteId) {

    List<SitePermissionEntity> sitePermissions =
        (List<SitePermissionEntity>)
            CollectionUtils.emptyIfNull(sitePermissionRepository.findBySiteId(siteId));

    List<String> studyIds =
        sitePermissions
            .stream()
            .distinct()
            .map(studyId -> studyId.getStudy().getId())
            .collect(Collectors.toList());

    List<String> siteAdminIds =
        sitePermissions
            .stream()
            .distinct()
            .map(urAdminId -> urAdminId.getUrAdminUser().getId())
            .collect(Collectors.toList());

    List<StudyPermissionEntity> studyPermissions =
        (List<StudyPermissionEntity>)
            CollectionUtils.emptyIfNull(
                studyPermissionRepository.findByByUserIdsAndStudyIds(siteAdminIds, studyIds));

    List<String> studyAdminIds =
        studyPermissions
            .stream()
            .distinct()
            .map(studyAdminId -> studyAdminId.getUrAdminUser().getId())
            .collect(Collectors.toList());

    List<String> appIds =
        sitePermissions
            .stream()
            .distinct()
            .map(appId -> appId.getApp().getId())
            .collect(Collectors.toList());

    List<AppPermissionEntity> appPermissions =
        (List<AppPermissionEntity>)
            CollectionUtils.emptyIfNull(
                appPermissionRepository.findByUserIdsAndAppIds(siteAdminIds, appIds));

    List<String> appAdminIds =
        appPermissions
            .stream()
            .distinct()
            .map(appAdminId -> appAdminId.getUrAdminUser().getId())
            .collect(Collectors.toList());

    for (SitePermissionEntity sitePermission : sitePermissions) {
      if (!(studyAdminIds.contains(sitePermission.getUrAdminUser().getId())
          || appAdminIds.contains(sitePermission.getUrAdminUser().getId()))) {
        sitePermissionRepository.delete(sitePermission);
      }
    }
  }

  private void deactivateYetToEnrollParticipants(String siteId) {

    List<ParticipantRegistrySiteEntity> participantRegistrySites =
        participantRegistrySiteRepository.findBySiteId(siteId);

    if (CollectionUtils.isNotEmpty(participantRegistrySites)) {
      for (ParticipantRegistrySiteEntity participantRegistrySite : participantRegistrySites) {
        String onboardingStatusCode = participantRegistrySite.getOnboardingStatus();
        if (OnboardingStatus.NEW.getCode().equals(onboardingStatusCode)
            || OnboardingStatus.INVITED.getCode().equals(onboardingStatusCode)) {
          participantRegistrySite.setOnboardingStatus(OnboardingStatus.DISABLED.getCode());
          participantRegistrySite.setDisabledDate(new Timestamp(Instant.now().toEpochMilli()));
        }
        participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);
      }
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ParticipantDetailResponse getParticipantDetails(
      String participantRegistrySiteId, String userId, Integer page, Integer limit) {
    logger.entry("begin getParticipantDetails()");

    Optional<UserRegAdminEntity> optSuperAdmin = userRegAdminRepository.findById(userId);
    UserRegAdminEntity user =
        optSuperAdmin.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));

    Optional<ParticipantRegistrySiteEntity> optParticipantRegistry =
        participantRegistrySiteRepository.findById(participantRegistrySiteId);

    ErrorCode errorCode = validateParticipantDetailsRequest(optParticipantRegistry, userId);
    if (errorCode != null) {
      throw new ErrorCodeException(errorCode);
    }

    ParticipantDetail participantDetail =
        ParticipantMapper.toParticipantDetailsResponse(optParticipantRegistry.get());
    if (!user.isSuperAdmin()) {
      Optional<SitePermissionEntity> optSitePermission =
          sitePermissionRepository.findByUserIdAndSiteId(
              userId, optParticipantRegistry.get().getSite().getId());
      participantDetail.setSitePermission(optSitePermission.get().getCanEdit().value());
    } else {
      participantDetail.setSitePermission(Permission.EDIT.value());
    }

    List<ParticipantStudyEntity> participantsEnrollments =
        participantStudyRepository.findParticipantsEnrollment(participantRegistrySiteId);

    ParticipantDetailResponse participantDetailResponse = new ParticipantDetailResponse();
    if (CollectionUtils.isEmpty(participantsEnrollments)) {
      Enrollment enrollment = new Enrollment(null, "-", YET_TO_ENROLL, "-");
      participantDetail.getEnrollments().add(enrollment);
    } else {
      ParticipantMapper.addEnrollments(
          participantDetail, participantsEnrollments, participantDetail.getOnboardingStatus());
      List<String> participantStudyIds =
          participantsEnrollments
              .stream()
              .map(ParticipantStudyEntity::getId)
              .collect(Collectors.toList());

      List<StudyConsentEntity> studyConsents = null;
      if (page != null && limit != null) {
        Page<StudyConsentEntity> consentHistoryPage =
            studyConsentRepository.findByParticipantRegistrySiteIdForPagination(
                participantStudyIds, PageRequest.of(page, limit, Sort.by("created").descending()));
        studyConsents = consentHistoryPage.getContent();
      } else {
        studyConsents = studyConsentRepository.findByParticipantRegistrySiteId(participantStudyIds);
      }

      List<ConsentHistory> consentHistories =
          studyConsents.stream().map(ConsentMapper::toConsentHistory).collect(Collectors.toList());
      participantDetail.getConsentHistory().addAll(consentHistories);

      Long participantConsentCount =
          studyConsentRepository.countByParticipantRegistrySiteId(participantStudyIds);
      participantDetailResponse.setTotalConsentHistoryCount(participantConsentCount);
    }

    logger.exit(
        String.format(
            "total enrollments=%d, and consentHistories=%d",
            participantDetail.getEnrollments().size(),
            participantDetail.getConsentHistory().size()));

    return new ParticipantDetailResponse(
        MessageCode.GET_PARTICIPANT_DETAILS_SUCCESS,
        participantDetail,
        participantDetailResponse.getTotalConsentHistoryCount());
  }

  private ErrorCode validateParticipantDetailsRequest(
      Optional<ParticipantRegistrySiteEntity> optParticipantRegistry, String userId) {
    if (!optParticipantRegistry.isPresent()) {
      logger.exit(ErrorCode.PARTICIPANT_REGISTRY_SITE_NOT_FOUND);
      return ErrorCode.PARTICIPANT_REGISTRY_SITE_NOT_FOUND;
    }

    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!optUserRegAdminEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }
    if (!optUserRegAdminEntity.get().isSuperAdmin()) {
      Optional<SitePermissionEntity> sitePermission =
          sitePermissionRepository.findSitePermissionByUserIdAndSiteId(
              userId, optParticipantRegistry.get().getSite().getId());

      Optional<SitePermissionEntity> studyPermission =
          sitePermissionRepository.findSitePermissionByUserIdAndSiteId(
              userId, optParticipantRegistry.get().getSite().getId());

      if (!sitePermission.isPresent()) {
        logger.exit(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
        return ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED;
      }
    }
    return null;
  }

  @Override
  @Transactional
  public InviteParticipantResponse inviteParticipants(
      InviteParticipantRequest inviteParticipantRequest, AuditLogEventRequest auditRequest) {
    logger.entry("begin inviteParticipants()");

    SiteUserInfo siteUserInfo =
        siteRepository.getSiteUserDetails(
            inviteParticipantRequest.getUserId(), inviteParticipantRequest.getSiteId());

    if (StringUtils.isEmpty(siteUserInfo.getSiteId())
        || !ACTIVE_STATUS.equals(siteUserInfo.getStatus())) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    if (StringUtils.isEmpty(siteUserInfo.getUserId())) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    if (!siteUserInfo.getSuperAdmin()) {
      if (Permission.EDIT != Permission.fromValue(siteUserInfo.getPermission())) {
        throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
      }
    }

    List<ParticipantRegistrySiteEntity> participantsList =
        participantRegistrySiteRepository.findByIds(inviteParticipantRequest.getIds());
    auditRequest.setUserId(inviteParticipantRequest.getUserId());
    auditRequest.setStudyId(siteUserInfo.getStudyId());
    List<ParticipantRegistrySiteEntity> invitedParticipants =
        findEligibleParticipantsAndInvite(participantsList, auditRequest);

    participantsList.removeAll(invitedParticipants);
    List<String> failedParticipantIds =
        participantsList
            .stream()
            .map(ParticipantRegistrySiteEntity::getId)
            .collect(Collectors.toList());

    List<String> invitedParticipantIds =
        invitedParticipants
            .stream()
            .map(ParticipantRegistrySiteEntity::getId)
            .collect(Collectors.toList());

    logger.exit(
        String.format(
            "%d invited and %d failed participants",
            invitedParticipantIds.size(), failedParticipantIds.size()));
    return new InviteParticipantResponse(
        MessageCode.PARTICIPANTS_INVITED_SUCCESS, invitedParticipantIds, failedParticipantIds);
  }

  private List<ParticipantRegistrySiteEntity> findEligibleParticipantsAndInvite(
      List<ParticipantRegistrySiteEntity> participants, AuditLogEventRequest auditRequest) {

    List<ParticipantRegistrySiteEntity> invitedParticipants = new ArrayList<>();
    for (ParticipantRegistrySiteEntity participantRegistrySiteEntity : participants) {
      OnboardingStatus onboardingStatus =
          OnboardingStatus.fromCode(participantRegistrySiteEntity.getOnboardingStatus());
      if (OnboardingStatus.DISABLED == onboardingStatus
          || OnboardingStatus.ENROLLED == onboardingStatus) {
        continue;
      }

      String token = RandomStringUtils.randomAlphanumeric(8);
      participantRegistrySiteEntity.setEnrollmentToken(token);
      participantRegistrySiteEntity.setEnrollmentTokenUsed(false);
      participantRegistrySiteEntity.setInvitationDate(new Timestamp(Instant.now().toEpochMilli()));

      if (OnboardingStatus.NEW == onboardingStatus) {
        participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.INVITED.getCode());
      }

      participantRegistrySiteEntity.setInvitationCount(
          participantRegistrySiteEntity.getInvitationCount() + 1);
      participantRegistrySiteEntity.setEnrollmentTokenExpiry(
          new Timestamp(
              Instant.now()
                  .plus(appPropertyConfig.getEnrollmentTokenExpiryInHours(), ChronoUnit.HOURS)
                  .toEpochMilli()));

      InviteParticipantEntity inviteParticipantsEmail =
          SiteMapper.toInviteParticipantEntity(auditRequest);
      inviteParticipantsEmail.setParticipantRegistrySite(participantRegistrySiteEntity.getId());
      inviteParticipantsEmail.setAppId(participantRegistrySiteEntity.getStudy().getAppId());

      invitedParticipantsEmailRepository.saveAndFlush(inviteParticipantsEmail);

      participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity);
      invitedParticipants.add(participantRegistrySiteEntity);
    }
    return invitedParticipants;
  }

  @Override
  @Transactional
  public ImportParticipantResponse importParticipants(
      String userId,
      String siteId,
      MultipartFile multipartFile,
      AuditLogEventRequest auditRequest) {
    logger.entry("begin importParticipants()");

    if (!(StringUtils.endsWith(multipartFile.getOriginalFilename(), ".xlsx")
        || StringUtils.endsWith(multipartFile.getOriginalFilename(), ".xls"))) {
      throw new ErrorCodeException(ErrorCode.INVALID_FILE_UPLOAD);
    }

    // Validate site type, status and access permission
    Optional<SiteEntity> optSite = siteRepository.findById(siteId);

    if (!optSite.isPresent() || !optSite.get().getStatus().equals(ACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    SiteEntity siteEntity = optSite.get();
    auditRequest.setSiteId(siteId);
    auditRequest.setUserId(userId);
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());

    Map<String, String> map = Collections.singletonMap("site_id", siteEntity.getId());

    if (siteEntity.getStudy() != null && OPEN_STUDY.equals(siteEntity.getStudy().getType())) {
      participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
      throw new ErrorCodeException(ErrorCode.OPEN_STUDY);
    }

    Optional<UserRegAdminEntity> optUserRegAdminEntity = validateUserId(userId);

    if (!optUserRegAdminEntity.get().isSuperAdmin()) {
      Optional<SitePermissionEntity> optSitePermission =
          sitePermissionRepository.findSitePermissionByUserIdAndSiteId(userId, siteId);

      if (!optSitePermission.isPresent()
          || !optSitePermission.get().getCanEdit().value().equals(Permission.EDIT.value())) {
        participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
        throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
      }
    }

    // iterate and save valid email id's
    try (Workbook workbook =
        WorkbookFactory.create(new BufferedInputStream(multipartFile.getInputStream()))) {

      Sheet sheet = workbook.getSheetAt(0);
      Row row = sheet.getRow(0);
      String columnName = row.getCell(EMAIL_ADDRESS_COLUMN).getStringCellValue();
      if (!"Email Address".equalsIgnoreCase(columnName)) {
        participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
        throw new ErrorCodeException(ErrorCode.DOCUMENT_NOT_IN_PRESCRIBED_FORMAT);
      }

      Iterator<Row> rows = sheet.rowIterator();
      Set<String> invalidEmails = new HashSet<>();
      Set<String> validEmails = new HashSet<>();

      // Skip headers row
      rows.next();
      while (rows.hasNext()) {
        Row r = rows.next();

        String email = r.getCell(EMAIL_ADDRESS_COLUMN).getStringCellValue();
        if (StringUtils.isBlank(email) || !Pattern.matches(EMAIL_REGEX, email)) {
          invalidEmails.add(email);
          continue;
        }
        validEmails.add(email);
      }

      ImportParticipantResponse importParticipantResponse =
          saveImportParticipant(validEmails, userId, siteEntity);
      importParticipantResponse.getInvalidEmails().addAll(invalidEmails);

      ParticipantManagerEvent participantManagerEvent =
          importParticipantResponse.getInvalidEmails().isEmpty()
                  && importParticipantResponse.getDuplicateEmails().isEmpty()
              ? PARTICIPANTS_EMAIL_LIST_IMPORTED
              : PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED;
      participantManagerHelper.logEvent(participantManagerEvent, auditRequest, map);

      return importParticipantResponse;
    } catch (EncryptedDocumentException | IOException | InvalidFormatException e) {
      participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
      throw new ErrorCodeException(ErrorCode.FAILED_TO_IMPORT_PARTICIPANTS);
    }
  }

  private ImportParticipantResponse saveImportParticipant(
      Set<String> emails, String userId, SiteEntity siteEntity) {

    List<ParticipantRegistrySiteEntity> participantRegistrySiteEntities =
        (List<ParticipantRegistrySiteEntity>)
            CollectionUtils.emptyIfNull(
                participantRegistrySiteRepository.findByStudyIdAndEmails(
                    siteEntity.getStudy().getId(), emails));

    List<String> participantRegistryEmails =
        (List<String>)
            CollectionUtils.emptyIfNull(
                participantRegistrySiteEntities
                    .stream()
                    .distinct()
                    .map(ParticipantRegistrySiteEntity::getEmail)
                    .collect(Collectors.toList()));

    List<String> newEmails =
        (List<String>)
            CollectionUtils.removeAll(new ArrayList<String>(emails), participantRegistryEmails);

    List<ParticipantDetail> savedParticipants = new ArrayList<>();
    for (String email : newEmails) {
      ParticipantDetail participantDetail = new ParticipantDetail();
      participantDetail.setEmail(email);
      ParticipantRegistrySiteEntity participantRegistrySite =
          ParticipantMapper.fromParticipantDetail(participantDetail, siteEntity);
      participantRegistrySite.setCreatedBy(userId);
      participantRegistrySite.setEnrollmentTokenExpiry(
          new Timestamp(
              Instant.now()
                  .plus(appPropertyConfig.getEnrollmentTokenExpiryInHours(), ChronoUnit.HOURS)
                  .toEpochMilli()));
      participantRegistrySite =
          participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);
      participantDetail.setId(participantRegistrySite.getId());
      participantDetail.setNewlyCreatedUser(Boolean.TRUE);
      savedParticipants.add(participantDetail);
    }

    logger.exit(
        String.format(
            "%d duplicates email found and %d new emails saved",
            participantRegistryEmails.size(), newEmails.size()));
    return new ImportParticipantResponse(
        MessageCode.IMPORT_PARTICIPANT_SUCCESS, savedParticipants, participantRegistryEmails);
  }

  @Override
  @Transactional
  public ParticipantStatusResponse updateOnboardingStatus(
      ParticipantStatusRequest participantStatusRequest, AuditLogEventRequest auditRequest) {
    logger.entry("begin updateOnboardingStatus()");

    Optional<SiteEntity> optSite = siteRepository.findById(participantStatusRequest.getSiteId());

    if (!optSite.isPresent() || !optSite.get().getStatus().equals(ACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    Optional<UserRegAdminEntity> optUserRegAdminEntity =
        validateUserId(participantStatusRequest.getUserId());

    if (!optUserRegAdminEntity.get().isSuperAdmin()) {
      Optional<SitePermissionEntity> optSitePermission =
          sitePermissionRepository.findByUserIdAndSiteId(
              participantStatusRequest.getUserId(), participantStatusRequest.getSiteId());

      if (!optSitePermission.isPresent()
          || !optSitePermission.get().getCanEdit().value().equals(Permission.EDIT.value())) {
        throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
      }
    }

    OnboardingStatus onboardingStatus =
        OnboardingStatus.fromCode(participantStatusRequest.getStatus());
    if (onboardingStatus == null) {
      throw new ErrorCodeException(ErrorCode.INVALID_ONBOARDING_STATUS);
    }

    List<ParticipantRegistrySiteEntity> participantregistryList =
        participantRegistrySiteRepository.findByIds(participantStatusRequest.getIds());

    Timestamp disabledTimestamp =
        OnboardingStatus.DISABLED.equals(onboardingStatus)
            ? new Timestamp(Instant.now().toEpochMilli())
            : null;

    if (!OnboardingStatus.NEW.equals(onboardingStatus)) {
      participantRegistrySiteRepository.updateOnboardingStatus(
          participantStatusRequest.getStatus(),
          participantStatusRequest.getIds(),
          disabledTimestamp);
    } else {
      List<String> emails =
          participantregistryList
              .stream()
              .map(ParticipantRegistrySiteEntity::getEmail)
              .collect(Collectors.toList());

      Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
          participantRegistrySiteRepository.findExistingRecord(optSite.get().getStudyId(), emails);

      if (optParticipantRegistrySite.isPresent()) {
        throw new ErrorCodeException(ErrorCode.CANNOT_ENABLE_PARTICIPANT);
      }

      participantRegistrySiteRepository.updateOnboardingStatus(
          participantStatusRequest.getStatus(),
          participantStatusRequest.getIds(),
          disabledTimestamp);
    }

    SiteEntity site = optSite.get();

    auditRequest.setSiteId(site.getId());
    auditRequest.setUserId(participantStatusRequest.getUserId());
    auditRequest.setStudyId(site.getStudyId());
    auditRequest.setAppId(site.getStudy().getAppId());

    Map<String, String> map = Collections.singletonMap("site_id", optSite.get().getId());
    if (participantStatusRequest.getStatus().equals(OnboardingStatus.DISABLED.getCode())) {
      participantManagerHelper.logEvent(PARTICIPANT_INVITATION_DISABLED, auditRequest, map);
    } else if (participantStatusRequest.getStatus().equals(OnboardingStatus.NEW.getCode())) {
      participantManagerHelper.logEvent(PARTICIPANT_INVITATION_ENABLED, auditRequest, map);
    }
    logger.exit(
        String.format(
            "Onboarding status changed to %s for %d participants in Site %s",
            participantStatusRequest.getStatus(),
            participantStatusRequest.getIds().size(),
            participantStatusRequest.getSiteId()));
    return new ParticipantStatusResponse(MessageCode.UPDATE_STATUS_SUCCESS);
  }

  @Override
  @Transactional(readOnly = true)
  public SiteDetailsResponse getSites(String userId, Integer limit, Integer offset) {
    logger.entry("getSites(userId)");

    Optional<UserRegAdminEntity> optUser = userRegAdminRepository.findById(userId);
    if (optUser.isPresent() && optUser.get().isSuperAdmin()) {
      List<StudyDetails> studies = getSitesForSuperAdmin(limit, offset);
      return new SiteDetailsResponse(studies, MessageCode.GET_SITES_SUCCESS);
    }

    List<String> studyIds = studyRepository.findStudyIds(limit, offset, userId);

    List<StudySiteInfo> studySiteDetails = siteRepository.getStudySiteDetails(userId, studyIds);

    if (CollectionUtils.isEmpty(studySiteDetails)) {
      throw new ErrorCodeException(ErrorCode.NO_SITES_FOUND);
    }

    List<EnrolledInvitedCount> enrolledInvitedCountList =
        siteRepository.getEnrolledInvitedCountByUserId(userId);

    Map<String, EnrolledInvitedCount> enrolledInvitedCountMap =
        CollectionUtils.emptyIfNull(enrolledInvitedCountList)
            .stream()
            .collect(Collectors.toMap(EnrolledInvitedCount::getSiteId, Function.identity()));

    List<EnrolledInvitedCount> enrolledInvitedCountListForOpenStudy =
        studyRepository.getInvitedEnrolledCountForOpenStudyForStudies(userId);

    Map<String, EnrolledInvitedCount> enrolledInvitedCountMapOfOpenStudy =
        CollectionUtils.emptyIfNull(enrolledInvitedCountListForOpenStudy)
            .stream()
            .collect(Collectors.toMap(EnrolledInvitedCount::getSiteId, Function.identity()));

    enrolledInvitedCountMap.putAll(enrolledInvitedCountMapOfOpenStudy);

    Map<String, StudyDetails> studiesMap = new LinkedHashMap<>();

    for (StudySiteInfo studySiteInfo : studySiteDetails) {
      if (!studiesMap.containsKey(studySiteInfo.getStudyId())) {
        studiesMap.put(studySiteInfo.getStudyId(), StudyMapper.toStudyDetails(studySiteInfo));
      }

      StudyDetails studyDetail = studiesMap.get(studySiteInfo.getStudyId());
      studyDetail.setStudyPermission(studySiteInfo.getEditPermission());
      if (StringUtils.isNotEmpty(studySiteInfo.getSiteId())) {
        prepareSiteDetails(enrolledInvitedCountMap, studyDetail, studySiteInfo);
      }

      studyDetail.setSitesCount((long) studyDetail.getSites().size());
    }

    List<StudyDetails> studies = studiesMap.values().stream().collect(Collectors.toList());
    return new SiteDetailsResponse(studies, MessageCode.GET_SITES_SUCCESS);
  }

  private List<StudyDetails> getSitesForSuperAdmin(Integer limit, Integer offset) {

    List<StudySiteInfo> studySiteDetails = studyRepository.getStudySiteDetails(limit, offset);

    List<EnrolledInvitedCount> enrolledInvitedCountList = siteRepository.getEnrolledInvitedCount();

    Map<String, StudyDetails> studiesMap = new LinkedHashMap<>();

    Map<String, EnrolledInvitedCount> enrolledInvitedCountMap =
        CollectionUtils.emptyIfNull(enrolledInvitedCountList)
            .stream()
            .collect(Collectors.toMap(EnrolledInvitedCount::getSiteId, Function.identity()));

    List<EnrolledInvitedCount> enrolledCountList = siteRepository.findEnrolledCountForOpenStudy();

    Map<String, EnrolledInvitedCount> enrolledCountMap =
        CollectionUtils.emptyIfNull(enrolledCountList)
            .stream()
            .collect(Collectors.toMap(EnrolledInvitedCount::getSiteId, Function.identity()));

    if (CollectionUtils.isNotEmpty(studySiteDetails)) {
      for (StudySiteInfo studySiteInfo : studySiteDetails) {
        if (!studiesMap.containsKey(studySiteInfo.getStudyId())) {
          studiesMap.put(studySiteInfo.getStudyId(), StudyMapper.toStudyDetails(studySiteInfo));
        }
        StudyDetails studyDetail = studiesMap.get(studySiteInfo.getStudyId());
        if (StringUtils.isNotEmpty(studySiteInfo.getSiteId())) {
          addSites(enrolledInvitedCountMap, studySiteInfo, studyDetail, enrolledCountMap);
        }

        studyDetail.setStudyPermission(Permission.EDIT.value());
        studyDetail.setSitesCount((long) studyDetail.getSites().size());
      }
    }
    return studiesMap.values().stream().collect(Collectors.toList());
  }

  private void addSites(
      Map<String, EnrolledInvitedCount> enrolledInvitedCountMap,
      StudySiteInfo studySiteInfo,
      StudyDetails studyDetail,
      Map<String, EnrolledInvitedCount> enrolledInvitedCountMapForOpenStudy) {

    EnrolledInvitedCount enrolledInvitedCount =
        enrolledInvitedCountMap.get(studySiteInfo.getSiteId());

    Long invitedCount = 0L;
    Long enrolledCount = 0L;
    if (enrolledInvitedCount != null) {
      invitedCount = enrolledInvitedCount.getInvitedCount();
      enrolledCount = enrolledInvitedCount.getEnrolledCount();
    }

    SiteDetails site = new SiteDetails();
    site.setId(studySiteInfo.getSiteId());
    site.setName(studySiteInfo.getSiteName());

    String studyType = studySiteInfo.getStudyType();
    if (studyType.equals(OPEN_STUDY)) {
      EnrolledInvitedCount enrolledInvitedCountForOpenStudy =
          enrolledInvitedCountMapForOpenStudy.get(studySiteInfo.getSiteId());
      site.setEnrolled(
          enrolledInvitedCountForOpenStudy != null
              ? enrolledInvitedCountForOpenStudy.getEnrolledCount()
              : 0L);
      site.setInvited((studySiteInfo.getTargetEnrollment()));
    } else if (studyType.equals(CLOSE_STUDY)) {
      site.setInvited(invitedCount);
      site.setEnrolled(enrolledCount);
    }

    if (site.getInvited() != 0 && site.getInvited() >= site.getEnrolled()) {
      Double percentage =
          (Double.valueOf(site.getEnrolled()) * 100) / Double.valueOf(site.getInvited());
      site.setEnrollmentPercentage(percentage);
    } else if (site.getInvited() != 0
        && site.getEnrolled() >= site.getInvited()
        && studyType.equals(OPEN_STUDY)) {
      site.setEnrollmentPercentage(DEFAULT_PERCENTAGE);
    }
    studyDetail.getSites().add(site);
  }

  private void prepareSiteDetails(
      Map<String, EnrolledInvitedCount> enrolledInvitedCountMap,
      StudyDetails studyDetail,
      StudySiteInfo studySiteInfo) {

    Long invitedCount = 0L;
    Long enrolledCount = 0L;

    if (enrolledInvitedCountMap.containsKey(studySiteInfo.getSiteId())) {
      EnrolledInvitedCount enrolledInvitedCount =
          enrolledInvitedCountMap.get(studySiteInfo.getSiteId());

      invitedCount = enrolledInvitedCount.getInvitedCount();
      enrolledCount = enrolledInvitedCount.getEnrolledCount();
    }

    if (OPEN_STUDY.equals(studySiteInfo.getStudyType())) {
      invitedCount = studySiteInfo.getTargetEnrollment();
    }

    SiteDetails siteDetails = new SiteDetails();
    siteDetails.setId(studySiteInfo.getSiteId());
    siteDetails.setName(studySiteInfo.getSiteName());
    siteDetails.setInvited(invitedCount);
    siteDetails.setEnrolled(enrolledCount);

    if (siteDetails.getInvited() != 0 && siteDetails.getInvited() >= siteDetails.getEnrolled()) {
      Double percentage =
          (Double.valueOf(siteDetails.getEnrolled()) * 100)
              / Double.valueOf(siteDetails.getInvited());
      siteDetails.setEnrollmentPercentage(percentage);
    } else if (siteDetails.getInvited() != 0
        && siteDetails.getEnrolled() >= siteDetails.getInvited()
        && studySiteInfo.getStudyType().equals(OPEN_STUDY)) {
      siteDetails.setEnrollmentPercentage(DEFAULT_PERCENTAGE);
    }

    studyDetail.getSites().add(siteDetails);
  }

  @Override
  @Transactional
  public UpdateTargetEnrollmentResponse updateTargetEnrollment(
      UpdateTargetEnrollmentRequest enrollmentRequest, AuditLogEventRequest auditRequest) {
    logger.entry("updateTargetEnrollment()");

    Optional<UserRegAdminEntity> optUserRegAdminEntity =
        userRegAdminRepository.findById(enrollmentRequest.getUserId());
    if (!optUserRegAdminEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    StudyEntity study = null;
    if (optUserRegAdminEntity.get().isSuperAdmin()) {
      Optional<StudyEntity> optStudy = studyRepository.findById(enrollmentRequest.getStudyId());
      study = optStudy.orElseThrow(() -> new ErrorCodeException(ErrorCode.STUDY_NOT_FOUND));
    } else {

      Optional<SitePermissionEntity> optSitePermission =
          sitePermissionRepository.findByUserAdminIdAndStudyId(
              enrollmentRequest.getUserId(), enrollmentRequest.getStudyId());

      if (!optSitePermission.isPresent()) {
        throw new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED);
      }

      if (Permission.VIEW == optSitePermission.get().getCanEdit()) {
        throw new ErrorCodeException(ErrorCode.STUDY_PERMISSION_ACCESS_DENIED);
      }
      study = optSitePermission.get().getStudy();
    }

    if (CLOSE_STUDY.equalsIgnoreCase(study.getType())) {
      throw new ErrorCodeException(ErrorCode.CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_CLOSE_STUDY);
    }

    Optional<SiteEntity> optSiteEntity =
        siteRepository.findByStudyId(enrollmentRequest.getStudyId());
    if (!optSiteEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_FOUND);
    }

    SiteEntity site = optSiteEntity.get();
    if (SiteStatus.DEACTIVE == SiteStatus.fromValue(site.getStatus())) {
      throw new ErrorCodeException(
          ErrorCode.CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_DECOMMISSIONED_SITE);
    }

    site.setTargetEnrollment(enrollmentRequest.getTargetEnrollment());
    siteRepository.saveAndFlush(site);

    auditRequest.setUserId(enrollmentRequest.getUserId());
    auditRequest.setStudyId(enrollmentRequest.getStudyId());
    auditRequest.setSiteId(site.getId());

    Map<String, String> map = Collections.singletonMap("site_id", site.getId());
    participantManagerHelper.logEvent(ENROLLMENT_TARGET_UPDATED, auditRequest, map);

    logger.exit(
        String.format(
            "target enrollment changed to %d for siteId=%s",
            site.getTargetEnrollment(), site.getId()));
    return new UpdateTargetEnrollmentResponse(
        site.getId(), MessageCode.TARGET_ENROLLMENT_UPDATE_SUCCESS);
  }

  private Optional<UserRegAdminEntity> validateUserId(String userId) {
    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!optUserRegAdminEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }
    return optUserRegAdminEntity;
  }

  @Override
  @Transactional
  public void sendInvitationEmail() {

    List<InviteParticipantEntity> listOfInvitedParticipants =
        invitedParticipantsEmailRepository.findAllWithStatusZero();

    Set<String> uniqueRecords = new HashSet<>();
    for (InviteParticipantEntity invitedParticipants : listOfInvitedParticipants) {

      if (isDuplicateEntity(invitedParticipants, uniqueRecords)) {
        continue;
      }

      int updatedRows =
          invitedParticipantsEmailRepository.updateStatus(
              invitedParticipants.getStudy(),
              invitedParticipants.getParticipantRegistrySite(),
              invitedParticipants.getAppId(),
              1);

      if (updatedRows == 0) {
        // this record may be taken by another service instance
        continue;
      }

      Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySiteEntity =
          participantRegistrySiteRepository.findById(
              invitedParticipants.getParticipantRegistrySite());

      Optional<StudyEntity> optStudy =
          studyRepository.findByStudyId(invitedParticipants.getStudy());

      if (!optParticipantRegistrySiteEntity.isPresent() || !optStudy.isPresent()) {
        logger.warn(
            "Participant registry or study not found for invited participants so deleting this record from invite participant table");
        invitedParticipantsEmailRepository.deleteByParticipantRegistryIdAndStudyInfoId(
            invitedParticipants.getStudy(),
            invitedParticipants.getParticipantRegistrySite(),
            invitedParticipants.getAppId());

        continue;
      }

      ParticipantRegistrySiteEntity participantRegistrySiteEntity =
          optParticipantRegistrySiteEntity.get();

      Map<String, String> templateArgs = new HashMap<>();
      templateArgs.put("study name", optStudy.get().getName());
      templateArgs.put("org name", appPropertyConfig.getOrgName());
      templateArgs.put("enrolment token", participantRegistrySiteEntity.getEnrollmentToken());
      templateArgs.put("contact email address", appPropertyConfig.getContactEmail());
      EmailRequest emailRequest =
          new EmailRequest(
              appPropertyConfig.getFromEmail(),
              new String[] {participantRegistrySiteEntity.getEmail()},
              null,
              null,
              appPropertyConfig.getParticipantInviteSubject(),
              appPropertyConfig.getParticipantInviteBody(),
              templateArgs);
      EmailResponse emailResponse = emailService.sendMimeMail(emailRequest);

      Map<String, String> map =
          Collections.singletonMap("site_id", participantRegistrySiteEntity.getSite().getId());
      AuditLogEventRequest auditRequest = SiteMapper.prepareAuditlogRequest(invitedParticipants);
      auditRequest.setSiteId(participantRegistrySiteEntity.getSite().getId());
      auditRequest.setStudyId(participantRegistrySiteEntity.getSite().getStudyId());

      if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER
          .getMessage()
          .equals(emailResponse.getMessage())) {
        invitedParticipantsEmailRepository.deleteByParticipantRegistryIdAndStudyInfoId(
            invitedParticipants.getStudy(),
            invitedParticipants.getParticipantRegistrySite(),
            invitedParticipants.getAppId());

        participantManagerHelper.logEvent(INVITATION_EMAIL_SENT, auditRequest, map);

      } else {
        invitedParticipantsEmailRepository.updateStatus(
            invitedParticipants.getStudy(),
            invitedParticipants.getParticipantRegistrySite(),
            invitedParticipants.getAppId(),
            0);

        participantManagerHelper.logEvent(INVITATION_EMAIL_FAILED, auditRequest, map);
      }
    }
  }

  public boolean isDuplicateEntity(
      InviteParticipantEntity invitedParticipants, Set<String> uniqueRecords) {
    String key =
        invitedParticipants.getStudy()
            + "_"
            + invitedParticipants.getParticipantRegistrySite()
            + "_"
            + invitedParticipants.getAppId();

    return !uniqueRecords.add(key);
  }
}