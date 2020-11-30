/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.EnrolledInvitedCount;
import com.google.cloud.healthcare.fdamystudies.model.EnrolledInvitedCountForStudy;
import com.google.cloud.healthcare.fdamystudies.model.LocationIdStudyNamesPair;
import com.google.cloud.healthcare.fdamystudies.model.StudyAppDetails;
import com.google.cloud.healthcare.fdamystudies.model.StudyCount;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfo;
import com.google.cloud.healthcare.fdamystudies.model.StudyParticipantDetails;
import com.google.cloud.healthcare.fdamystudies.model.StudySiteInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface StudyRepository extends JpaRepository<StudyEntity, String> {

  @Query("SELECT study from StudyEntity study where study.id=:studyId")
  public Optional<StudyEntity> findByStudyId(String studyId);

  @Query("SELECT study from StudyEntity study where study.app.id in (:appIds)")
  public List<StudyEntity> findByAppIds(@Param("appIds") List<String> appIds);

  @Query(
      value =
          "SELECT s.location_id AS locationId, GROUP_CONCAT(DISTINCT si.name SEPARATOR ',') AS studyNames from sites s, study_info si where s.study_id=si.id AND s.location_id in (:locationIds) GROUP BY s.location_id",
      nativeQuery = true)
  public List<LocationIdStudyNamesPair> getStudyNameLocationIdPairs(List<String> locationIds);

  @Query(
      value =
          "SELECT GROUP_CONCAT(DISTINCT si.name SEPARATOR ',') from sites s, study_info si "
              + "where s.study_id=si.id AND s.location_id = :locationId",
      nativeQuery = true)
  public String getStudyNamesByLocationId(String locationId);

  @Query("SELECT study FROM StudyEntity study where study.app.id = :appInfoId")
  public List<StudyEntity> findByAppId(String appInfoId);

  @Query(
      value =
          "SELECT app.id AS appId, IFNULL(COUNT(study.id), 0) AS COUNT "
              + "FROM study_info study, app_info app "
              + "WHERE app.id = study.app_info_id AND (select count(si.id) from sites si where si.study_id=study.id) >0 "
              + "GROUP BY app.id",
      nativeQuery = true)
  public List<AppCount> findAppStudiesCount();

  @Query(
      value =
          "SELECT studyId, SUM(target_invited_count) AS COUNT "
              + "FROM ( "
              + "SELECT study.id AS studyId, SUM(site.target_enrollment) AS target_invited_count "
              + "FROM study_info study, sites site "
              + "WHERE study.id=site.study_id AND study.type='OPEN' "
              + "GROUP BY study.id UNION ALL "
              + "SELECT study.id AS studyId, COUNT(prs.onboarding_status) AS target_invited_count "
              + "FROM study_info study, participant_registry_site prs "
              + "WHERE study.id=prs.study_info_id AND prs.id NOT IN ( "
              + "SELECT psi.participant_registry_site_id "
              + "FROM participant_study_info psi, participant_registry_site pr "
              + "WHERE STATUS IN ('yetToJoin','notEligible') AND pr.id=psi.participant_registry_site_id AND prs.site_id=psi.site_id) AND prs.onboarding_status='I' AND study.type='CLOSE' "
              + "GROUP BY study.id "
              + ") rstAlias "
              + "GROUP BY studyId ",
      nativeQuery = true)
  public List<StudyCount> findInvitedCountByStudyId();

  @Query(
      value =
          "SELECT study.id AS studyId, COUNT(psi.site_id) AS COUNT "
              + "FROM study_info study, participant_study_info psi "
              + "WHERE study.id=psi.study_info_id AND psi.status='inProgress' "
              + "GROUP BY study.id ",
      nativeQuery = true)
  public List<StudyCount> findEnrolledCountByStudyId();

  @Query(
      value =
          "SELECT invites.study_info_id AS studyId, invites.invitedCount, IFNULL(enrolled.enrolledCount, 0) AS enrolledCount "
              + "FROM ( "
              + "SELECT prs.study_info_id, COUNT(prs.onboarding_status) AS invitedCount "
              + "FROM participant_registry_site prs, sites_permissions sp "
              + "WHERE prs.site_id=sp.site_id AND prs.id NOT IN ( "
              + "SELECT psi.participant_registry_site_id "
              + "FROM participant_study_info psi, participant_registry_site pr "
              + "WHERE STATUS IN ('yetToJoin','notEligible') AND pr.id=psi.participant_registry_site_id AND prs.site_id=psi.site_id) AND prs.onboarding_status='I' AND sp.ur_admin_user_id =:userId "
              + "GROUP BY prs.study_info_id) AS invites "
              + "LEFT JOIN ( "
              + "SELECT ps.study_info_id, COUNT(ps.study_info_id) AS enrolledCount "
              + "FROM participant_study_info ps, sites_permissions sp "
              + "WHERE ps.site_id=sp.site_id AND ps.status='inProgress' AND sp.ur_admin_user_id =:userId "
              + "GROUP BY ps.study_info_id) AS enrolled ON invites.study_info_id=enrolled.study_info_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCountForStudy> getEnrolledInvitedCountByUserId(
      @Param("userId") String userId);

  @Query(
      value =
          "SELECT created_time AS createdTimestamp, study_id AS studyId, custom_id AS customId, name AS studyName, type AS type,"
              + " logo_image_url AS logoImageUrl, edit AS edit, study_permission AS studyPermission "
              + "FROM( "
              + "SELECT si.created_time, sp.study_id, si.custom_id, si.name, si.type, si.logo_image_url, sp.edit, TRUE as study_permission "
              + "FROM study_permissions sp, study_info si "
              + "WHERE si.id=sp.study_id AND sp.ur_admin_user_id =:userId AND sp.study_id IN (SELECT sp.study_id FROM sites_permissions sp WHERE  sp.ur_admin_user_id =:userId) "
              + "UNION ALL "
              + "SELECT DISTINCT si.created_time, sp.study_id, si.custom_id, si.name, si.type, si.logo_image_url, null,FALSE as study_permission "
              + "FROM sites_permissions sp, study_info si, sites s "
              + "WHERE si.id=sp.study_id AND s.id=sp.site_id AND s.status=1 AND sp.ur_admin_user_id =:userId AND sp.study_id NOT IN ( "
              + "SELECT st.study_id "
              + "FROM study_permissions st "
              + "WHERE st.ur_admin_user_id =:userId)) rstAlias GROUP BY created_time,study_id,custom_id,name,type,logo_image_url,edit,study_permission "
              + "ORDER BY created_time DESC  LIMIT :limit OFFSET :offset ",
      nativeQuery = true)
  public List<StudyInfo> getStudyDetails(
      @Param("userId") String userId, Integer limit, Integer offset);

  @Query(
      value =
          "SELECT study_id AS studyId, count(site_id) AS count "
              + "FROM sites_permissions "
              + "WHERE ur_admin_user_id =:userId "
              + "GROUP BY study_id ",
      nativeQuery = true)
  public List<StudyCount> getSiteCount(@Param("userId") String userId);

  @Query(
      value =
          "SELECT distinct invites.study_id AS studyId, invites.invitedCount , IFNULL(enrolled.enrolledCount, 0) AS enrolledCount "
              + "FROM ( "
              + "SELECT si.study_id, si.target_enrollment AS invitedCount "
              + "FROM sites si, study_info st, sites_permissions sp "
              + "WHERE si.study_id=st.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.study_id=si.study_id AND st.type='OPEN' "
              + ") AS invites "
              + "LEFT JOIN ( "
              + "SELECT ps.study_info_id, COUNT(ps.study_info_id) AS enrolledCount "
              + "FROM participant_study_info ps, sites_permissions sp "
              + "WHERE ps.site_id=sp.site_id AND ps.status='inProgress' AND sp.ur_admin_user_id =:userId "
              + "GROUP BY ps.study_info_id) AS enrolled ON invites.study_id=enrolled.study_info_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCountForStudy> getInvitedEnrolledCountForOpenStudy(
      @Param("userId") String userId);

  @Query(
      value =
          "SELECT DISTINCT si.id AS studyId, si.name AS studyName, si.custom_id AS customStudyId,si.type AS studyType, "
              + "ai.id AS appId,ai.app_name AS appName,ai.custom_app_id AS customAppId, "
              + "st.target_enrollment AS targetEnrollment, stp.edit AS edit "
              + "FROM app_info ai, study_info si, sites st,sites_permissions stp "
              + "WHERE ai.id=si.app_info_id AND st.study_id=si.id AND si.id=:studyId AND stp.study_id=si.id AND stp.ur_admin_user_id=:userId ",
      nativeQuery = true)
  public Optional<StudyAppDetails> getStudyParticipant(String studyId, String userId);

  @Query(
      value =
          "SELECT DISTINCT si.id AS studyId, si.name AS studyName, si.custom_id AS customStudyId,si.type AS studyType, "
              + "ai.id AS appId,ai.app_name AS appName,ai.custom_app_id AS customAppId, "
              + "st.target_enrollment AS targetEnrollment "
              + "FROM app_info ai, study_info si, sites st "
              + "WHERE ai.id=si.app_info_id AND st.study_id=si.id AND si.id=:studyId ",
      nativeQuery = true)
  public Optional<StudyAppDetails> getStudyParticipantForSuperAdmin(String studyId);

  @Query(
      value =
          "SELECT prs.created_time AS createdTime, prs.email AS email, psi.enrolled_time AS enrolledDate, psi.status AS enrolledStatus "
              + ",prs.site_id AS siteId, prs.onboarding_status AS onboardingStatus, "
              + "loc.name AS locationName, loc.custom_id AS locationCustomId, "
              + "prs.invitation_time AS invitedDate, prs.id AS participantId "
              + "FROM participant_registry_site prs "
              + "LEFT JOIN participant_study_info psi ON prs.id=psi.participant_registry_site_id "
              + "LEFT JOIN sites si ON si.id=prs.site_id "
              + "LEFT JOIN locations loc ON loc.id=si.location_id "
              + "WHERE prs.study_info_id=:studyId "
              + "ORDER BY prs.created_time, prs.email DESC",
      nativeQuery = true)
  public List<StudyParticipantDetails> getStudyParticipantDetails(String studyId);

  @Query(
      value =
          "SELECT distinct invites.study_id AS siteId, invites.invitedCount , IFNULL(enrolled.enrolledCount, 0) AS enrolledCount "
              + "FROM ( "
              + "SELECT si.study_id, si.target_enrollment AS invitedCount "
              + "FROM sites si, study_info st, sites_permissions sp "
              + "WHERE si.study_id=st.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.study_id=si.study_id AND st.type='OPEN' "
              + ") AS invites "
              + "LEFT JOIN ( "
              + "SELECT ps.study_info_id, COUNT(ps.study_info_id) AS enrolledCount "
              + "FROM participant_study_info ps, sites_permissions sp "
              + "WHERE ps.site_id=sp.site_id AND ps.status='inProgress' AND sp.ur_admin_user_id =:userId "
              + "GROUP BY ps.study_info_id) AS enrolled ON invites.study_id=enrolled.study_info_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCount> getInvitedEnrolledCountForOpenStudyForStudies(
      @Param("userId") String userId);

  @Query(
      value =
          "SELECT DISTINCT study.studyCreatedTimeStamp AS studyCreatedTimeStamp, site.siteCreatedTimeStamp AS siteCreatedTimeStamp, study.studyId AS studyId, site.siteId AS siteId, IFNULL(site.targetEnrollment, 0) AS targetEnrollment, "
              + "site.siteName AS siteName,study.customId AS customId,study.studyName AS studyName, study.studyType AS studyType, study.customAppId AS customAppId, study.appId AS appId, study.appName AS appName, "
              + "study.logoImageUrl AS logoImageUrl,study.studyStatus AS studyStatus "
              + "FROM ( "
              + "SELECT stu.id AS studyId, stu.created_time AS studyCreatedTimeStamp, stu.custom_id AS customId, stu.name AS studyName, stu.type AS studyType, "
              + "stu.logo_image_url AS logoImageUrl,stu.status AS studyStatus, ai.custom_app_id AS customAppId, ai.id AS appId, ai.app_name AS appName "
              + "FROM study_info stu, app_info ai "
              + "WHERE stu.app_info_id = ai.id ORDER BY stu.created_time DESC  LIMIT :limit OFFSET :offset) AS study "
              + "LEFT JOIN ( "
              + "SELECT si.created_time AS siteCreatedTimeStamp, si.id AS siteId, si.study_id AS studyId, si.target_enrollment AS targetEnrollment, loc.name AS siteName "
              + "FROM sites si, locations loc, study_info stu "
              + "WHERE stu.id=si.study_id AND loc.id=si.location_id) AS site ON study.studyId= site.studyId ",
      nativeQuery = true)
  public List<StudySiteInfo> getStudySiteDetails(Integer limit, Integer offset);

  @Query(
      value = "SELECT * FROM study_info ORDER BY created_time DESC LIMIT :limit OFFSET :offset ",
      nativeQuery = true)
  public List<StudyEntity> findAll(Integer limit, Integer offset);

  @Query(
      value =
          "SELECT id AS studyId  FROM study_info where id IN "
              + "(SELECT study_id from study_permissions where ur_admin_user_id=:userId UNION ALL "
              + "SELECT study_id from sites_permissions  where ur_admin_user_id=:userId) "
              + "ORDER BY created_time DESC LIMIT :limit OFFSET :offset ",
      nativeQuery = true)
  public List<String> findStudyIds(Integer limit, Integer offset, String userId);
}
