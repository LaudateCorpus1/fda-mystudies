/*
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.dao;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_COMPREHENSION_TEST_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_COMPREHENSION_TEST_SECTION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_CONTENT_NEW_VERSION_PUBLISHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_DOCUMENT_NEW_VERSION_PUBLISHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_DEACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_LAUNCHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_PAUSED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESUMED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_REVIEW_AND_E_CONSENT_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_REVIEW_AND_E_CONSENT_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_SETTINGS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_SETTINGS_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.UPDATES_PUBLISHED_TO_STUDY;

import com.fdahpstudydesigner.bean.AnswerOption;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.DynamicBean;
import com.fdahpstudydesigner.bean.DynamicFrequencyBean;
import com.fdahpstudydesigner.bean.EffectivePeriod;
import com.fdahpstudydesigner.bean.EnableWhenBranching;
import com.fdahpstudydesigner.bean.Extension;
import com.fdahpstudydesigner.bean.FHIRQuestionnaire;
import com.fdahpstudydesigner.bean.Identifier;
import com.fdahpstudydesigner.bean.Initial;
import com.fdahpstudydesigner.bean.ItemsQuestionnaire;
import com.fdahpstudydesigner.bean.SearchQuestionnaireFhirBean;
import com.fdahpstudydesigner.bean.StudyIdBean;
import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bean.StudyPageBean;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.ActiveTaskListBo;
import com.fdahpstudydesigner.bo.ActiveTaskMasterAttributeBo;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.Checklist;
import com.fdahpstudydesigner.bo.ComprehensionTestQuestionBo;
import com.fdahpstudydesigner.bo.ComprehensionTestResponseBo;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.ConsentMasterInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.EligibilityTestBo;
import com.fdahpstudydesigner.bo.FormBo;
import com.fdahpstudydesigner.bo.FormMappingBo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.QuestionConditionBranchBo;
import com.fdahpstudydesigner.bo.QuestionReponseTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseSubTypeBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnaireCustomScheduleBo;
import com.fdahpstudydesigner.bo.QuestionnairesFrequenciesBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.ReferenceTablesBo;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyActivityVersionBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPageBo;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.StudyActiveTasksService;
import com.fdahpstudydesigner.service.StudyExportImportService;
import com.fdahpstudydesigner.service.StudyQuestionnaireService;
import com.fdahpstudydesigner.util.ConsentManagementAPIs;
import com.fdahpstudydesigner.util.CustomMultipartFile;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.ImageUtility;
import com.fdahpstudydesigner.util.ServletContextHolder;
import com.fdahpstudydesigner.util.SessionObject;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.gson.Gson;
import com.itextpdf.html2pdf.HtmlConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyDAOImpl implements StudyDAO {
  private static final String EXPORT = "/Export/";

  private static XLogger logger = XLoggerFactory.getXLogger(StudyDAOImpl.class.getName());

  @Autowired private HttpServletRequest request;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @Autowired private AuditLogDAO auditLogDAO;

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private StudyExportImportService studyExportImportService;

  @Autowired private ConsentManagementAPIs consentApis;

  @Autowired private StudyQuestionnaireService studyQuestionnaireService;

  @Autowired private StudyActiveTasksService studyActiveTasksService;

  @Autowired private com.fdahpstudydesigner.util.FhirHealthcareAPIs fhirHealthcareAPIs;

  private static final String FHIR_STORES = "/fhirStores/";

  private static final String DATASET_PATH = "projects/%s/locations/%s/datasets/%s";

  public static final String QUESTIONNAIRE_TYPE = "Questionnaire";

  public static final String DATE_FORMAT_RESPONSE_MOBILE = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";

  public static final String DATE_FORMAT_RESPONSE_FHIR = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

  protected static final Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();

  String projectId = configMap.get("dataProjectId");

  String regionId = configMap.get("regionId");

  public static final String fhirEnabled = configMap.get("enableFhirAPI");

  HibernateTemplate hibernateTemplate;
  private Query query = null;
  String queryString = "";
  private Transaction transaction = null;

  public StudyDAOImpl() {
    // Unused
  }

  @Override
  public String checkActiveTaskTypeValidation(String studyId) {
    logger.entry("begin checkActiveTaskTypeValidation()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    List<String> taskNameList = null;
    try {
      taskNameList =
          Arrays.asList(
              FdahpStudyDesignerConstants.TOWER_OF_HANOI,
              FdahpStudyDesignerConstants.SPATIAL_SPAN_MEMORY);
      session = hibernateTemplate.getSessionFactory().openSession();
      String searchQuery =
          "select count(*) from active_task a"
              + " where a.study_id=:studyId"
              + " and a.task_type_id"
              + " in(select c.active_task_list_id from active_task_list c"
              + " where a.active=1 and c.task_name in(:taskNameList));";
      BigInteger count =
          (BigInteger)
              session
                  .createSQLQuery(searchQuery)
                  .setString("studyId", studyId)
                  .setParameterList("taskNameList", taskNameList)
                  .uniqueResult();
      if ((count != null) && (count.intValue() > 0)) {
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - checkActiveTaskTypeValidation() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("checkActiveTaskTypeValidation() - Ends");
    return message;
  }

  @Override
  public int comprehensionTestQuestionOrder(String studyId) {
    logger.entry("begin comprehensionTestQuestionOrder()");
    Session session = null;
    int count = 0;
    ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              "From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=:studyId"
                  + " and CTQBO.active=1 order by CTQBO.sequenceNo desc");
      query.setString("studyId", studyId);
      query.setMaxResults(1);
      comprehensionTestQuestionBo = (ComprehensionTestQuestionBo) query.uniqueResult();
      if (comprehensionTestQuestionBo != null) {
        count = comprehensionTestQuestionBo.getSequenceNo() + 1;
      } else {
        count = count + 1;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - comprehensionTestQuestionOrder() - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("comprehensionTestQuestionOrder() - Ends");
    return count;
  }

  @Override
  public int consentInfoOrder(String studyId) {
    logger.entry("begin consentInfoOrder()");
    Session session = null;
    int count = 1;
    ConsentInfoBo consentInfoBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              "From ConsentInfoBo CIB where CIB.studyId=:studyId"
                  + " and CIB.active=1 order by CIB.sequenceNo DESC");
      query.setString("studyId", studyId);
      query.setMaxResults(1);
      consentInfoBo = ((ConsentInfoBo) query.uniqueResult());
      if (consentInfoBo != null) {
        count = consentInfoBo.getSequenceNo() + 1;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - consentInfoOrder() - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("consentInfoOrder() - Ends");
    return count;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String deleteComprehensionTestQuestion(
      String questionId, String studyId, SessionObject sessionObject) {
    logger.entry("begin deleteComprehensionTestQuestion()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    String searchQuery = "";
    ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
    StudySequenceBo studySequence = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
      searchQuery =
          "From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=:studyId"
              + " and CTQBO.active=1 order by CTQBO.sequenceNo asc";
      comprehensionTestQuestionList =
          session.createQuery(searchQuery).setString("studyId", studyId).list();
      if ((comprehensionTestQuestionList != null) && !comprehensionTestQuestionList.isEmpty()) {
        boolean isValue = false;
        for (ComprehensionTestQuestionBo comprehensionTestQuestion :
            comprehensionTestQuestionList) {
          if (comprehensionTestQuestion.getId().equals(questionId)) {
            isValue = true;
          }
          if (isValue && !comprehensionTestQuestion.getId().equals(questionId)) {
            comprehensionTestQuestion.setSequenceNo(comprehensionTestQuestion.getSequenceNo() - 1);
            session.update(comprehensionTestQuestion);
          }
        }
      }
      comprehensionTestQuestionBo =
          (ComprehensionTestQuestionBo) session.get(ComprehensionTestQuestionBo.class, questionId);
      if (comprehensionTestQuestionBo != null) {
        comprehensionTestQuestionBo.setActive(false);
        comprehensionTestQuestionBo.setModifiedBy(sessionObject.getUserId());
        comprehensionTestQuestionBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        session.saveOrUpdate(comprehensionTestQuestionBo);
        message = FdahpStudyDesignerConstants.SUCCESS;
        if (comprehensionTestQuestionBo.getStudyId() != null) {
          studySequence =
              (StudySequenceBo)
                  session
                      .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                      .setString(
                          FdahpStudyDesignerConstants.STUDY_ID,
                          comprehensionTestQuestionBo.getStudyId())
                      .uniqueResult();
          if (studySequence != null) {
            if (studySequence.isComprehensionTest()) {
              studySequence.setComprehensionTest(false);
            }
            session.saveOrUpdate(studySequence);

            updateStudyToDraftStatus(studyId, sessionObject, session);
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteComprehensionTestQuestion() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteComprehensionTestQuestion() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String deleteConsentInfo(
      String consentInfoId, String studyId, SessionObject sessionObject, String customStudyId) {
    logger.entry("begin deleteConsentInfo()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    int count = 0;
    ConsentInfoBo consentInfo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      List<ConsentInfoBo> consentInfoList = null;
      String searchQuery =
          "From ConsentInfoBo CIB where CIB.studyId=:studyId"
              + " and CIB.active=1 order by CIB.sequenceNo asc";
      consentInfoList = session.createQuery(searchQuery).setString("studyId", studyId).list();
      if ((consentInfoList != null) && !consentInfoList.isEmpty()) {
        boolean isValue = false;
        for (ConsentInfoBo consentInfoBo : consentInfoList) {
          if (consentInfoBo.getId().equals(consentInfoId)) {
            isValue = true;
          }
          if (isValue && !consentInfoBo.getId().equals(consentInfoId)) {
            consentInfoBo.setSequenceNo(consentInfoBo.getSequenceNo() - 1);
            consentInfoBo.setModifiedBy(sessionObject.getUserId());
            consentInfoBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            session.update(consentInfoBo);
          }
        }
        StudySequenceBo studySequence =
            (StudySequenceBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                    .uniqueResult();
        if (studySequence != null) {
          studySequence.setConsentEduInfo(false);
          studySequence.seteConsent(false);
          studySequence.setComprehensionTest(false);
          session.saveOrUpdate(studySequence);
        }
      }
      String deleteQuery =
          "Update ConsentInfoBo CIB set CIB.active=0,CIB.modifiedBy=:userId"
              + ",CIB.modifiedOn=:currentDateTime where CIB.id= :consentInfoId";
      query = session.createQuery(deleteQuery);
      query.setString("userId", sessionObject.getUserId());
      query.setString("currentDateTime", FdahpStudyDesignerUtil.getCurrentDateTime());
      query.setString("consentInfoId", consentInfoId);
      count = query.executeUpdate();

      updateStudyToDraftStatus(studyId, sessionObject, session);

      if (count > 0) {
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
      if (consentInfoId != null) {
        consentInfo = getConsentInfoById(consentInfoId);
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteConsentInfo() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteConsentInfo() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String deleteEligibilityTestQusAnsById(
      String eligibilityTestId, String studyId, SessionObject sessionObject, String customStudyId) {
    logger.entry("begin deleteEligibilityTestQusAnsById()");
    Session session = null;
    Integer eligibilityDeleteResult = 0;
    Transaction trans = null;
    String result = FdahpStudyDesignerConstants.FAILURE;
    String reorderQuery;
    EligibilityTestBo eligibilityTestBo;
    List<EligibilityTestBo> eligibilityTestBos;
    StringBuilder sb = null;
    StudyBo studyBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      trans = session.beginTransaction();
      eligibilityTestBo =
          (EligibilityTestBo)
              session
                  .getNamedQuery("EligibilityTestBo.findById")
                  .setString("eligibilityTestId", eligibilityTestId)
                  .uniqueResult();
      studyBo = this.getStudyById(String.valueOf(studyId), sessionObject.getUserId());

      if ((null != studyBo)
          && studyBo.getStatus().contains(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH)) {
        session.delete(eligibilityTestBo);
        eligibilityDeleteResult = 1;
      } else {
        eligibilityDeleteResult =
            session
                .getNamedQuery("EligibilityTestBo.deleteById")
                .setString("eligibilityTestId", eligibilityTestId)
                .executeUpdate();

        updateStudyToDraftStatus(studyId, sessionObject, session);
      }
      sb = new StringBuilder();
      sb.append(
          "select id FROM EligibilityTestBo  WHERE sequenceNo > :sequenceNo AND active = true AND eligibilityId = :eligibilityId");
      eligibilityTestBos =
          session
              .createQuery(sb.toString())
              .setInteger("sequenceNo", eligibilityTestBo.getSequenceNo())
              .setString("eligibilityId", eligibilityTestBo.getEligibilityId())
              .list();
      if ((eligibilityDeleteResult > 0) && !eligibilityTestBos.isEmpty()) {
        reorderQuery =
            "update EligibilityTestBo set sequenceNo=sequenceNo-1 where id in (:eligibilityTestBosList)";
        eligibilityDeleteResult =
            session
                .createQuery(reorderQuery)
                .setParameterList("eligibilityTestBosList", eligibilityTestBos)
                .executeUpdate();
      }
      if (eligibilityDeleteResult > 0) {
        result = FdahpStudyDesignerConstants.SUCCESS;
      }
      if (StringUtils.isNotEmpty(eligibilityTestId)) {
        StudySequenceBo studySequence =
            (StudySequenceBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                    .uniqueResult();
        if ((studySequence != null) && studySequence.isEligibility()) {
          studySequence.setEligibility(false);
          session.update(studySequence);
        }
      }

      trans.commit();
    } catch (Exception e) {
      if (null != trans) {
        trans.rollback();
      }
      logger.error("StudyDAOImpl - deleteEligibilityTestQusAnsById - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteEligibilityTestQusAnsById() - Ends");
    return result;
  }

  private void updateStudyToDraftStatus(
      String studyId, SessionObject sessionObject, Session session) {
    queryString =
        "Update StudyBo set "
            + "hasStudyDraft = 1"
            + " , modifiedBy = :userId"
            + " , modifiedOn = now() where id = :studyId";

    session
        .createQuery(queryString)
        .setParameter("userId", sessionObject.getUserId())
        .setParameter("studyId", studyId)
        .executeUpdate();
  }

  @Override
  public boolean deleteLiveStudy(String customStudyId) {
    logger.entry("begin deleteLiveStudy()");
    boolean flag = false;
    Session session = null;
    StudyBo liveStudyBo = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    // String subQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      liveStudyBo =
          (StudyBo)
              session
                  .getNamedQuery("getStudyLiveVersion")
                  .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId)
                  .uniqueResult();
      if (liveStudyBo != null) {
        // deleting the live study
        message =
            deleteStudyByIdOrCustomstudyId(
                session, transaction, liveStudyBo.getId().toString(), "");

        // once live study deleted successfully, reseting the new study
        if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          session
              .createSQLQuery("DELETE FROM study_version WHERE custom_study_id=:customStudyId")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
          session
              .createSQLQuery(
                  "DELETE FROM study_activity_version WHERE custom_study_id=:customStudyId")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
          session
              .createSQLQuery(
                  "UPDATE active_task set is_live=0 WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId) ")
              .setParameter("customStudyId", customStudyId)
              .executeUpdate();
          session
              .createSQLQuery(
                  "UPDATE questionnaires set is_live=0 WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)")
              .setParameter("customStudyId", customStudyId)
              .executeUpdate();
          session
              .createSQLQuery(
                  "UPDATE consent set is_live=0 WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
          session
              .createSQLQuery(
                  "UPDATE consent_info set is_live=0 WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
          session
              .createSQLQuery("UPDATE studies set is_live=0 WHERE custom_study_id =:customStudyId")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
          flag = true;
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteLiveStudy() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteLiveStudy() - Ends");
    return flag;
  }

  @Override
  public String deleteOverviewStudyPageById(String studyId, String pageId) {
    logger.entry("begin deleteOverviewStudyPageById()");
    Session session = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    int count = 0;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(pageId)) {
        query =
            session.createQuery(
                "delete from StudyPageBo where studyId=:studyId and pageId=:pageId");
        query.setString("studyId", studyId);
        query.setString("pageId", pageId);
        count = query.executeUpdate();
        if (count > 0) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteOverviewStudyPageById() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteOverviewStudyPageById() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String deleteResourceInfo(
      String resourceInfoId, boolean resourceVisibility, String studyId, SessionObject sesOb) {
    logger.entry("begin deleteResourceInfo()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    int resourceCount = 0;
    Query resourceQuery = null;
    Query notificationQuery = null;
    List<ResourceBO> resourceBOList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String searchQuery =
          "From ResourceBO RBO where RBO.studyId=:studyId"
              + " and RBO.status=1 order by RBO.sequenceNo asc";
      resourceBOList = session.createQuery(searchQuery).setString("studyId", studyId).list();
      if ((resourceBOList != null) && !resourceBOList.isEmpty()) {
        boolean isValue = false;
        for (ResourceBO resourceBO : resourceBOList) {
          if (resourceBO.getId().equals(resourceInfoId)) {
            isValue = true;
          }
          if (isValue && !resourceBO.getId().equals(resourceInfoId)) {
            resourceBO.setSequenceNo(resourceBO.getSequenceNo() - 1);
            session.update(resourceBO);
          }
        }
      }

      String deleteQuery = " UPDATE ResourceBO RBO SET status = false  WHERE id = :resourceInfoId ";
      resourceQuery = session.createQuery(deleteQuery).setString("resourceInfoId", resourceInfoId);
      resourceCount = resourceQuery.executeUpdate();

      StudySequenceBo studySequence =
          (StudySequenceBo)
              session
                  .getNamedQuery("getStudySequenceByStudyId")
                  .setString("studyId", studyId)
                  .uniqueResult();
      studySequence.setMiscellaneousResources(false);
      session.saveOrUpdate(studySequence);
      updateStudyToDraftStatus(studyId, sesOb, session);

      if (!resourceVisibility && (resourceCount > 0)) {
        String deleteNotificationQuery =
            " UPDATE NotificationBO NBO set NBO.notificationStatus = 1 WHERE NBO.resourceId = :resourceInfoId ";
        notificationQuery =
            session
                .createQuery(deleteNotificationQuery)
                .setString("resourceInfoId", resourceInfoId);
        notificationQuery.executeUpdate();
      }
      transaction.commit();
      message = FdahpStudyDesignerConstants.SUCCESS;
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteResourceInfo() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteResourceInfo() - Ends");
    return message;
  }

  @Override
  public boolean deleteStudyByCustomStudyId(String customStudyId) {
    logger.entry("begin deleteStudyByCustomStudyId()");
    Session session = null;
    boolean falg = false;
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      message = deleteStudyByIdOrCustomstudyId(session, transaction, "", customStudyId);
      if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
        falg = true;
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteStudyByCustomStudyId() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteStudyByCustomStudyId() - Ends");
    return falg;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String deleteStudyByIdOrCustomstudyId(
      Session session, Transaction transaction, String studyId, String customStudyId) {
    logger.entry("begin deleteStudyByIdOrCustomstudyId()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    List<StudyBo> studyBOList = null;
    // String subQuery = "";
    String studyCustomQuery = "";
    List<Integer> idList = null;
    List<String> studyIdList = new ArrayList<>();
    try {
      studyIdList = this.getStudyIdAsList(studyId);

      if (StringUtils.isNotEmpty(customStudyId)) {
        studyCustomQuery = " FROM StudyBo SBO WHERE SBO.customStudyId =:customStudyId";
      } else {
        studyCustomQuery = " FROM StudyBo SBO WHERE SBO.id in(:studyIdList)";
      }
      query = session.createQuery(studyCustomQuery);

      if (StringUtils.isNotEmpty(customStudyId)) {
        query.setParameter("customStudyId", customStudyId);
      } else {
        query.setParameterList("studyIdList", studyIdList);
      }

      studyBOList = query.list();
      if ((studyBOList != null) && !studyBOList.isEmpty()) {
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT page_id FROM study_page WHERE page_id is not null and study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId) ";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT page_id FROM study_page WHERE page_id is not null and study_id in (:studyIdList)";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM study_page WHERE page_id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "select id from eligibility_test e where e.eligibility_id in(select id from eligibility where study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "select id from eligibility_test e where e.eligibility_id in(select id from eligibility where study_id in (:studyIdList))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM eligibility_test WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM eligibility WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString = "SELECT id FROM eligibility WHERE study_id in (:studyIdList) ";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM eligibility WHERE id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM consent WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString = "SELECT id FROM consent WHERE study_id in (:studyIdList)";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM consent WHERE id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "select r.id from comprehension_test_response r where r.comprehension_test_question_id in(select id from comprehension_test_question c where c.study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "select r.id from comprehension_test_response r where r.comprehension_test_question_id in(select id from comprehension_test_question c where c.study_id in (:studyIdList))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM comprehension_test_response WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM comprehension_test_question WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT id FROM comprehension_test_question WHERE study_id in (:studyIdList)";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM comprehension_test_question WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM consent_info WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString = "SELECT id FROM consent_info WHERE study_id in (:studyIdList)";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM consent_info WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT active_task_id FROM active_task_attrtibutes_values WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId) )";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT active_task_id FROM active_task_attrtibutes_values WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in (:studyIdList))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery(
                  "DELETE FROM active_task_attrtibutes_values WHERE active_task_id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT active_task_id FROM active_task_frequencies WHERE active_task_id IN (SELECT id FROM active_task WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT active_task_id FROM active_task_frequencies WHERE active_task_id IN (SELECT id FROM active_task WHERE study_id in (:studyIdList))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery(
                  "DELETE FROM active_task_frequencies WHERE active_task_id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT active_task_id FROM active_task_custom_frequencies WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT active_task_id FROM active_task_custom_frequencies WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in (:studyIdList))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery(
                  "DELETE FROM active_task_custom_frequencies WHERE active_task_id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM active_task WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString = "SELECT id FROM active_task WHERE study_id in (:studyIdList)";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM active_task WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        /** Questionnarie Part Start * */
        // Form Step Start .....

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM questions WHERE id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT id FROM questions WHERE id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in (:studyIdList))))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM questions WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT response_sub_type_value_id FROM response_sub_type_value WHERE response_type_id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT response_sub_type_value_id FROM response_sub_type_value WHERE response_type_id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList))))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery(
                  "DELETE FROM response_sub_type_value WHERE response_sub_type_value_id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT response_type_id FROM response_type_value WHERE questions_response_type_id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT response_type_id FROM response_type_value WHERE questions_response_type_id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList))))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM response_type_value WHERE response_type_id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        // form_mapping deletion
        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in (:studyIdList)))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM form_mapping WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT form_id FROM form WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT form_id FROM form WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in (:studyIdList)))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM form WHERE form_id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        // Form Step End......

        // Instruction Step Start .....

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM instructions WHERE id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Instruction' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT id FROM instructions WHERE id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Instruction' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList)))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM instructions WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }
        // Instruction Step End......

        // Question Step End......

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM questions WHERE id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT id FROM questions WHERE id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList)))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM questions WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT response_sub_type_value_id FROM response_sub_type_value WHERE response_type_id IN(SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT response_sub_type_value_id FROM response_sub_type_value WHERE response_type_id IN(SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList)))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery(
                  "DELETE FROM response_sub_type_value WHERE response_sub_type_value_id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT response_type_id FROM response_type_value WHERE questions_response_type_id IN(SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT response_type_id FROM response_type_value WHERE questions_response_type_id IN(SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList)))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM response_type_value WHERE response_type_id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        // Question Step End......
        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT step_id FROM questionnaires_steps WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT step_id FROM questionnaires_steps WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList) )";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM questionnaires_steps WHERE step_id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM questionnaires_frequencies WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT id FROM questionnaires_frequencies WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM questionnaires_frequencies WHERE id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM questionnaires_custom_frequencies WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString =
              "SELECT id FROM questionnaires_custom_frequencies WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (:studyIdList))";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM questionnaires_custom_frequencies WHERE id in(:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString = "SELECT id FROM questionnaires WHERE study_id in (:studyIdList)";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }
        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM questionnaires WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        idList = null;
        queryString = "";
        if (StringUtils.isNotEmpty(customStudyId)) {
          queryString =
              "SELECT id FROM resources WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)";
          idList =
              session.createSQLQuery(queryString).setString("customStudyId", customStudyId).list();
        } else {
          queryString = "SELECT id FROM resources WHERE study_id in (:studyIdList)";
          idList =
              session
                  .createSQLQuery(queryString)
                  .setParameterList("studyIdList", studyIdList)
                  .list();
        }

        if ((idList != null) && !idList.isEmpty()) {
          session
              .createSQLQuery("DELETE FROM resources WHERE id in (:idList)")
              .setParameterList("idList", idList)
              .executeUpdate();
        }

        if (StringUtils.isNotEmpty(customStudyId)) {
          session
              .createSQLQuery(
                  "DELETE FROM notification_history WHERE notification_id in(SELECT notification_id FROM notification WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId))")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
        } else {
          session
              .createSQLQuery(
                  "DELETE FROM notification_history WHERE notification_id in(SELECT notification_id FROM notification WHERE study_id in (:studyIdList))")
              .setParameterList("studyIdList", studyIdList)
              .executeUpdate();
        }

        if (StringUtils.isNotEmpty(customStudyId)) {
          session
              .createSQLQuery(
                  "DELETE FROM notification WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
        } else {
          session
              .createSQLQuery("DELETE FROM notification WHERE study_id in (:studyIdList)")
              .setParameterList("studyIdList", studyIdList)
              .executeUpdate();
        }

        if (StringUtils.isNotEmpty(customStudyId)) {
          session
              .createSQLQuery(
                  "DELETE FROM study_checklist WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
        } else {
          session
              .createSQLQuery("DELETE FROM study_checklist WHERE study_id in (:studyIdList)")
              .setParameterList("studyIdList", studyIdList)
              .executeUpdate();
        }

        if (StringUtils.isNotEmpty(customStudyId)) {
          session
              .createSQLQuery(
                  "DELETE FROM study_permission WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
        } else {
          session
              .createSQLQuery("DELETE FROM study_permission WHERE study_id in (:studyIdList)")
              .setParameterList("studyIdList", studyIdList)
              .executeUpdate();
        }
        if (StringUtils.isNotEmpty(customStudyId)) {
          session
              .createSQLQuery(
                  "DELETE FROM study_sequence WHERE study_id in (SELECT id FROM studies WHERE custom_study_id=:customStudyId)")
              .setParameter("customStudyId", customStudyId)
              .executeUpdate();
        } else {
          session
              .createSQLQuery("DELETE FROM study_sequence WHERE study_id in (:studyIdList)")
              .setParameterList("studyIdList", studyIdList)
              .executeUpdate();
        }

        if (StringUtils.isNotEmpty(customStudyId)) {
          session
              .createSQLQuery("DELETE FROM study_version WHERE custom_study_id=:customStudyId")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
          session
              .createSQLQuery("DELETE FROM studies WHERE custom_study_id=:customStudyId")
              .setString("customStudyId", customStudyId)
              .executeUpdate();

          session
              .createSQLQuery("DELETE FROM anchordate_type WHERE custom_study_id=:customStudyId")
              .setString("customStudyId", customStudyId)
              .executeUpdate();
        } else {
          session
              .createSQLQuery("DELETE FROM studies WHERE id in (:studyIdList)")
              .setParameterList("studyIdList", studyIdList)
              .executeUpdate();
        }
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteStudyByIdOrCustomstudyId() - ERROR ", e);
    }
    logger.exit("deleteStudyByIdOrCustomstudyId() - Ends");
    return message;
  }

  @Override
  public int eligibilityTestOrderCount(String eligibilityId) {
    logger.entry("begin eligibilityTestOrderCount");
    Session session = null;
    int count = 1;
    EligibilityTestBo eligibilityTestBo = null;
    StringBuilder sb = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      sb = new StringBuilder();
      sb.append(
          "From EligibilityTestBo ETB where ETB.eligibilityId=:eligibilityId and ETB.active=1 order by ETB.sequenceNo DESC");
      query =
          session
              .createQuery(sb.toString())
              .setString("eligibilityId", eligibilityId)
              .setMaxResults(1);
      eligibilityTestBo = ((EligibilityTestBo) query.uniqueResult());
      if (eligibilityTestBo != null) {
        count = eligibilityTestBo.getSequenceNo() + 1;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - eligibilityTestOrderCount - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("eligibilityTestOrderCount - Ends");
    return count;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<StudyBo> getAllStudyList() {
    logger.entry("begin getAllStudyList()");
    Session session = null;
    List<StudyBo> studyBOList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              " FROM StudyBo SBO WHERE SBO.version = 0 AND SBO.status <> :deActivateStatus");
      query.setParameter("deActivateStatus", FdahpStudyDesignerConstants.STUDY_DEACTIVATED);
      studyBOList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getAllStudyList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getAllStudyList() - Ends");
    return studyBOList;
  }

  @Override
  public Checklist getchecklistInfo(String studyId) {
    logger.entry("begin getchecklistInfo()");
    Checklist checklist = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .getNamedQuery("getchecklistInfo")
              .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId);
      checklist = (Checklist) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getchecklistInfo() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getchecklistInfo() - Ends");
    return checklist;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ComprehensionTestQuestionBo getComprehensionTestQuestionById(String questionId) {
    logger.entry("begin getComprehensionTestQuestionById()");
    ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
    Session session = null;
    List<ComprehensionTestResponseBo> comprehensionTestResponsList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      comprehensionTestQuestionBo =
          (ComprehensionTestQuestionBo) session.get(ComprehensionTestQuestionBo.class, questionId);
      if (null != comprehensionTestQuestionBo) {
        String searchQuery =
            "From ComprehensionTestResponseBo CRBO where CRBO.comprehensionTestQuestionId=:id ORDER BY CRBO.sequenceNumber";
        query = session.createQuery(searchQuery);
        query.setString("id", comprehensionTestQuestionBo.getId());
        comprehensionTestResponsList = query.list();
        comprehensionTestQuestionBo.setResponseList(comprehensionTestResponsList);
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getComprehensionTestQuestionById() - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getComprehensionTestQuestionById() - Ends");
    return comprehensionTestQuestionBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ComprehensionTestQuestionBo> getComprehensionTestQuestionList(String studyId) {
    logger.entry("begin getComprehensionTestQuestionList()");
    Session session = null;
    List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              "From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=:studyId"
                  + " and CTQBO.active=1 order by CTQBO.sequenceNo asc");
      query.setString("studyId", studyId);
      comprehensionTestQuestionList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getComprehensionTestQuestionList() - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getComprehensionTestQuestionList() - Ends");
    return comprehensionTestQuestionList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ComprehensionTestResponseBo> getComprehensionTestResponseList(
      String comprehensionQuestionId) {
    logger.entry("begin deleteComprehensionTestQuestion()");
    Session session = null;
    List<ComprehensionTestResponseBo> comprehensionTestResponseList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              "From ComprehensionTestResponseBo CTRBO where CTRBO.comprehensionTestQuestionId=:questionId");
      query.setString("questionId", comprehensionQuestionId);
      comprehensionTestResponseList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - deleteComprehensionTestQuestion() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteComprehensionTestQuestion() - Ends");
    return comprehensionTestResponseList;
  }

  @Override
  public ConsentBo getConsentDetailsByStudyId(String studyId) {
    logger.entry("begin getConsentDetailsByStudyId()");
    ConsentBo consentBo = null;
    Session session = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.createQuery("from ConsentBo CBO where CBO.studyId=:studyId");
      query.setString("studyId", studyId);
      consentBo = (ConsentBo) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getConsentDetailsByStudyId() :: Ends");
    return consentBo;
  }

  @Override
  public ConsentInfoBo getConsentInfoById(String consentInfoId) {
    logger.entry("begin getConsentInfoById()");
    ConsentInfoBo consentInfoBo = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      consentInfoBo = (ConsentInfoBo) session.get(ConsentInfoBo.class, consentInfoId);
      if (consentInfoBo != null) {
        consentInfoBo.setDisplayTitle(
            StringUtils.isEmpty(consentInfoBo.getDisplayTitle())
                ? ""
                : consentInfoBo
                    .getDisplayTitle()
                    .replaceAll("&#34;", "\"")
                    .replaceAll("&#39;", "\'"));
        consentInfoBo.setBriefSummary(
            StringUtils.isEmpty(consentInfoBo.getBriefSummary())
                ? ""
                : consentInfoBo
                    .getBriefSummary()
                    .replaceAll("&#34;", "\"")
                    .replaceAll("&#39;", "\'"));
        consentInfoBo.setElaborated(
            StringUtils.isEmpty(consentInfoBo.getElaborated())
                ? ""
                : consentInfoBo
                    .getElaborated()
                    .replaceAll("&#34;", "\"")
                    .replaceAll("&#39;", "\'"));
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getConsentInfoById() - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getConsentInfoById() - Ends");
    return consentInfoBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ConsentInfoBo> getConsentInfoDetailsListByStudyId(String studyId) {
    logger.entry("begin getConsentInfoDetailsListByStudyId()");
    Session session = null;
    Query query = null;
    List<ConsentInfoBo> consentInfoBoList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              " from ConsentInfoBo CIBO where CIBO.studyId=:studyId and CIBO.active=1 ORDER BY CIBO.sequenceNo ");
      query.setString("studyId", studyId);
      consentInfoBoList = query.list();
      if ((null != consentInfoBoList) && (consentInfoBoList.size() > 0)) {
        for (ConsentInfoBo consentInfoBo : consentInfoBoList) {
          consentInfoBo.setDisplayTitle(
              consentInfoBo
                  .getDisplayTitle()
                  .replaceAll("<", "&#60;")
                  .replaceAll(">", "&#62;")
                  .replaceAll("/", "&#47;")
                  .replaceAll("'", "&#39;")
                  .replaceAll("\"", "&#34;"));
          consentInfoBo.setElaborated(
              consentInfoBo
                  .getElaborated()
                  .replaceAll("&#39;", "'")
                  .replaceAll("&#34;", "&quot;")
                  .replaceAll("em>", "i>")
                  .replaceAll(
                      "<a", "<a target=\"_blank\" style=\"text-decoration:underline;color:blue;\"")
                  .replaceAll("'", "\\\\'"));
        }
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getConsentInfoDetailsListByStudyId() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getConsentInfoDetailsListByStudyId() :: Ends");
    return consentInfoBoList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ConsentInfoBo> getConsentInfoList(String studyId) {
    logger.entry("begin getConsentInfoList()");
    List<ConsentInfoBo> consentInfoList = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)) {
        String searchQuery =
            "From ConsentInfoBo CIB where CIB.studyId=:studyId and CIB.active=1 order by CIB.sequenceNo asc";
        query = session.createQuery(searchQuery);
        query.setString("studyId", studyId);
        consentInfoList = query.list();
      }

    } catch (Exception e) {
      logger.error("StudyDAOImpl - getConsentInfoList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getConsentInfoList() - Ends");
    return consentInfoList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ConsentMasterInfoBo> getConsentMasterInfoList() {
    logger.entry("begin getConsentMasterInfoList()");
    Session session = null;
    List<ConsentMasterInfoBo> consentMasterInfoList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.createQuery("From ConsentMasterInfoBo CMIB");
      consentMasterInfoList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getConsentMasterInfoList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getConsentMasterInfoList() - Ends");
    return consentMasterInfoList;
  }

  public String getErrorBasedonAction(StudySequenceBo studySequenceBo) {
    String message = FdahpStudyDesignerConstants.SUCCESS;
    if (studySequenceBo != null) {
      if (!studySequenceBo.isBasicInfo()) {
        message = FdahpStudyDesignerConstants.BASICINFO_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isSettingAdmins()) {
        message = FdahpStudyDesignerConstants.SETTING_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isOverView()) {
        message = FdahpStudyDesignerConstants.OVERVIEW_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isEligibility()) {
        message = FdahpStudyDesignerConstants.ELIGIBILITY_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isConsentEduInfo()) {
        message = FdahpStudyDesignerConstants.CONSENTEDUINFO_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isComprehensionTest()) {
        message = FdahpStudyDesignerConstants.COMPREHENSIONTEST_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.iseConsent()) {
        message = FdahpStudyDesignerConstants.ECONSENT_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isStudyExcQuestionnaries()) {
        message = FdahpStudyDesignerConstants.STUDYEXCQUESTIONNARIES_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isStudyExcActiveTask()) {
        message = FdahpStudyDesignerConstants.STUDYEXCACTIVETASK_ERROR_MSG;
        return message;
      } else if (!studySequenceBo.isMiscellaneousResources()) {
        message = FdahpStudyDesignerConstants.RESOURCES_ERROR_MSG;
        return message;
      }
    }
    return message;
  }

  @Override
  public StudyIdBean getLiveVersion(String customStudyId) {
    logger.entry("begin getLiveVersion()");
    Session session = null;
    StudyVersionBo studyVersionBo = null;
    String consentStudyId = null;
    StudyIdBean studyIdBean = new StudyIdBean();
    String activetaskStudyId = null;
    String questionnarieStudyId = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(customStudyId)) {
        query =
            session
                .getNamedQuery("getStudyByCustomStudyId")
                .setString("customStudyId", customStudyId);
        query.setMaxResults(1);
        studyVersionBo = (StudyVersionBo) query.uniqueResult();
        if (studyVersionBo != null) {
          queryString =
              "SELECT s.study_id FROM active_task s where s.custom_study_id=:customStudyId and is_live =1";
          activetaskStudyId =
              (String)
                  session
                      .createSQLQuery(queryString)
                      .setString("customStudyId", customStudyId)
                      .setMaxResults(1)
                      .uniqueResult();

          queryString =
              "SELECT s.study_id FROM questionnaires s where s.custom_study_id=:customStudyId and is_live =1";
          questionnarieStudyId =
              (String)
                  session
                      .createSQLQuery(queryString)
                      .setString("customStudyId", customStudyId)
                      .setMaxResults(1)
                      .uniqueResult();

          queryString =
              "SELECT s.study_id FROM consent s where s.custom_study_id=:customStudyId and round(s.version, 1) =:consentVersion";
          consentStudyId =
              (String)
                  session
                      .createSQLQuery(queryString)
                      .setString("customStudyId", customStudyId)
                      .setFloat("consentVersion", studyVersionBo.getConsentVersion())
                      .setMaxResults(1)
                      .uniqueResult();

          if (consentStudyId == null) {
            queryString =
                "SELECT s.study_id FROM consent_info s where s.custom_study_id=:customStudyId and round(s.version, 1) =:consentVersion";
            consentStudyId =
                (String)
                    session
                        .createSQLQuery(queryString)
                        .setString("customStudyId", customStudyId)
                        .setFloat("consentVersion", studyVersionBo.getConsentVersion())
                        .setMaxResults(1)
                        .uniqueResult();
          }

          studyIdBean.setActivetaskStudyId(activetaskStudyId);
          studyIdBean.setQuestionnarieStudyId(questionnarieStudyId);
          studyIdBean.setConsentStudyId(consentStudyId);
        }
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getLiveVersion() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getLiveVersion() - Ends");
    return studyIdBean;
  }

  @Override
  public NotificationBO getNotificationByResourceId(String resourseId) {
    logger.entry("begin getNotificationByResourceId()");
    Session session = null;
    String queryString = null;
    NotificationBO notificationBO = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      queryString = " FROM NotificationBO NBO WHERE NBO.resourceId =:resourseId ";
      query = session.createQuery(queryString);
      query.setString("resourseId", resourseId);
      notificationBO = (NotificationBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getNotificationByResourceId - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getNotificationByResourceId - Ends");
    return notificationBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<StudyPageBo> getOverviewStudyPagesById(String studyId, String userId) {
    logger.entry("begin getOverviewStudyPagesById()");
    Session session = null;
    List<StudyPageBo> studyPageBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)) {
        query =
            session.createQuery(
                "from StudyPageBo where studyId=:studyId order by createdOn, CASE WHEN sequenceNumber IS NULL THEN 1 ELSE 0 END, sequenceNumber");
        query.setString("studyId", studyId);
        studyPageBo = query.list();
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getOverviewStudyPagesById() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getOverviewStudyPagesById() - Ends");
    return studyPageBo;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public HashMap<String, List<ReferenceTablesBo>> getreferenceListByCategory() {
    logger.entry("begin getreferenceListByCategory()");
    Session session = null;
    List<ReferenceTablesBo> allReferenceList = null;
    List<ReferenceTablesBo> categoryList = new ArrayList<>();
    List<ReferenceTablesBo> researchSponserList = new ArrayList<>();
    HashMap<String, List<ReferenceTablesBo>> referenceMap = new HashMap<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.createQuery("from ReferenceTablesBo order by category asc,id asc");
      allReferenceList = query.list();
      if ((allReferenceList != null) && !allReferenceList.isEmpty()) {
        for (ReferenceTablesBo referenceTablesBo : allReferenceList) {
          if (StringUtils.isNotEmpty(referenceTablesBo.getCategory())) {
            switch (referenceTablesBo.getCategory()) {
              case FdahpStudyDesignerConstants.REFERENCE_TYPE_CATEGORIES:
                categoryList.add(referenceTablesBo);
                break;
              case FdahpStudyDesignerConstants.REFERENCE_TYPE_RESEARCH_SPONSORS:
                researchSponserList.add(referenceTablesBo);
                break;

              default:
                break;
            }
          }
        }
        referenceMap = new HashMap<>();
        if (!categoryList.isEmpty()) {
          referenceMap.put(FdahpStudyDesignerConstants.REFERENCE_TYPE_CATEGORIES, categoryList);
        }
        if (!researchSponserList.isEmpty()) {
          referenceMap.put(
              FdahpStudyDesignerConstants.REFERENCE_TYPE_RESEARCH_SPONSORS, researchSponserList);
        }
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getreferenceListByCategory() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getreferenceListByCategory() - Ends");
    return referenceMap;
  }

  @Override
  public ResourceBO getResourceInfo(String resourceInfoId) {
    logger.entry("begin getResourceInfo()");
    ResourceBO resourceBO = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getResourceInfo").setString("resourceInfoId", resourceInfoId);
      resourceBO = (ResourceBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getResourceInfo() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getResourceInfo() - Ends");
    return resourceBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ResourceBO> getResourceList(String studyId) {
    logger.entry("begin getResourceList()");
    List<ResourceBO> resourceBOList = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String searchQuery =
          " FROM ResourceBO RBO WHERE RBO.studyId=:studyId"
              + " AND RBO.status = 1 AND RBO.studyProtocol = false ORDER BY RBO.createdOn ASC ";
      query = session.createQuery(searchQuery);
      query.setString("studyId", studyId);
      resourceBOList = query.list();

      if ((resourceBOList != null) && !resourceBOList.isEmpty()) {
        int sequenceNo = 1;
        for (ResourceBO rBO : resourceBOList) {
          if ((rBO.getSequenceNo() == null) || rBO.getSequenceNo().equals(0)) {
            rBO.setSequenceNo(sequenceNo);
            session.update(rBO);
            sequenceNo++;
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - getResourceList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getResourceList() - Ends");
    return resourceBOList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<NotificationBO> getSavedNotification(String studyId) {
    logger.entry("begin getSavedNotification()");
    List<NotificationBO> notificationSavedList = null;
    Session session = null;
    String searchQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      searchQuery =
          " FROM NotificationBO NBO WHERE NBO.studyId=:studyId"
              + " AND NBO.notificationAction = 0 AND NBO.notificationType='ST' AND NBO.notificationSubType='Announcement' ";
      query = session.createQuery(searchQuery);
      query.setString("studyId", studyId);
      notificationSavedList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getSavedNotification() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getSavedNotification() - Ends");
    return notificationSavedList;
  }

  @Override
  public StudyBo getStudyById(String studyId, String userId) {
    logger.entry("begin getStudyById()");
    Session session = null;
    StudyBo studyBo = null;
    StudySequenceBo studySequenceBo = null;
    StudyPermissionBO permissionBO = null;
    StudyVersionBo studyVersionBo = null;
    StudyBo liveStudyBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)) {
        studyBo =
            (StudyBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                    .setString("id", studyId)
                    .uniqueResult();
        studySequenceBo =
            (StudySequenceBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                    .uniqueResult();
        permissionBO =
            (StudyPermissionBO)
                session
                    .getNamedQuery("getStudyPermissionById")
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                    .setString("userId", userId)
                    .uniqueResult();
        if (studySequenceBo != null) {
          studyBo.setStudySequenceBo(studySequenceBo);
        }
        if (permissionBO != null) {
          studyBo.setViewPermission(permissionBO.isViewPermission());
        }
        if (studyBo != null) {
          // To get latest version of study, consent version ,
          // activity version
          query =
              session
                  .getNamedQuery("getStudyByCustomStudyId")
                  .setString("customStudyId", studyBo.getCustomStudyId());
          query.setMaxResults(1);
          studyVersionBo = (StudyVersionBo) query.uniqueResult();
          if (studyVersionBo != null) {
            studyVersionBo.setStudyLVersion(" V" + studyVersionBo.getStudyVersion());
            studyVersionBo.setConsentLVersion(" (V" + studyVersionBo.getConsentVersion() + ")");
            studyVersionBo.setActivityLVersion(" (V" + studyVersionBo.getActivityVersion() + ")");
            studyBo.setStudyVersionBo(studyVersionBo);
          }
          // To get the live version of study by passing customstudyId
          liveStudyBo =
              (StudyBo)
                  session
                      .createQuery("FROM StudyBo where customStudyId=:customStudyId and live=1")
                      .setParameter("customStudyId", studyBo.getCustomStudyId())
                      .uniqueResult();
          if (liveStudyBo != null) {
            studyBo.setLiveStudyBo(liveStudyBo);
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyById() - Ends");
    return studyBo;
  }

  @Override
  public EligibilityBo getStudyEligibiltyByStudyId(String studyId) {
    logger.entry("begin getStudyEligibiltyByStudyId()");
    Session session = null;
    EligibilityBo eligibilityBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)) {
        query =
            session
                .getNamedQuery("getEligibiltyByStudyId")
                .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        eligibilityBo = (EligibilityBo) query.uniqueResult();
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyEligibiltyByStudyId() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyEligibiltyByStudyId() - Ends");
    return eligibilityBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<StudyListBean> getStudyList(String userId) {
    logger.entry("begin getStudyList()");
    Session session = null;
    List<StudyListBean> studyListBeans = null;
    String name = "";
    List<ReferenceTablesBo> referenceTablesBos = null;
    StudyBo liveStudy = null;
    StudyBo studyBo = null;
    try {

      session = hibernateTemplate.getSessionFactory().openSession();

      if (StringUtils.isNotEmpty(userId)) {

        query = session.getNamedQuery("getUserById").setString("userId", userId);
        UserBO userBO = (UserBO) query.uniqueResult();

        if (userBO.getRoleId().equals("1")) {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.StudyListBean(s.id,s.customStudyId,s.name,s.category,s.researchSponsor,user.firstName, user.lastName,s.status,s.createdOn,s.appId)"
                      + " from StudyBo s, UserBO user"
                      + " where user.userId = s.createdBy"
                      + " and s.version=0"
                      + " order by s.createdOn desc");

        } else {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.StudyListBean(s.id,s.customStudyId,s.name,s.category,s.researchSponsor,user.firstName, user.lastName,p.viewPermission,s.status,s.createdOn,s.appId)"
                      + " from StudyBo s,StudyPermissionBO p, UserBO user"
                      + " where s.id=p.studyId"
                      + " and user.userId = s.createdBy"
                      + " and s.version=0"
                      + " and p.userId=:impValue"
                      + " order by s.createdOn desc");
          query.setString(FdahpStudyDesignerConstants.IMP_VALUE, userId);
        }
        studyListBeans = query.list();

        if ((studyListBeans != null) && !studyListBeans.isEmpty()) {
          for (StudyListBean bean : studyListBeans) {
            if (StringUtils.isNotEmpty(name)) {
              bean.setProjectLeadName(name);
            }
            if (StringUtils.isNotEmpty(bean.getCategory())
                && StringUtils.isNotEmpty(bean.getResearchSponsor())) {
              // get the Category, Research Sponsor name of the
              // study from categoryIds
              query =
                  session
                      .createQuery("from ReferenceTablesBo where id in(:category)")
                      .setParameter("category", bean.getCategory());
              referenceTablesBos = query.list();
              if ((referenceTablesBos != null) && !referenceTablesBos.isEmpty()) {
                bean.setCategory(referenceTablesBos.get(0).getValue());
              }
            }
            if (StringUtils.isNotEmpty(bean.getCustomStudyId())) {
              liveStudy =
                  (StudyBo)
                      session
                          .createQuery("from StudyBo where customStudyId=:studyId and live=1")
                          .setParameter("studyId", bean.getCustomStudyId())
                          .uniqueResult();
              if (liveStudy != null) {
                bean.setLiveStudyId(liveStudy.getId());
              } else {
                bean.setLiveStudyId(null);
              }
            }
            // if is there any change in study then edit with dot
            // will come
            if ((bean.getId() != null) && (bean.getLiveStudyId() != null)) {
              studyBo =
                  (StudyBo)
                      session
                          .createQuery("from StudyBo where id=:id")
                          .setParameter("id", bean.getId())
                          .uniqueResult();
              if (studyBo.getHasStudyDraft() == 1) {
                bean.setFlag(true);
              }
            }
            // if is there any team lead in that study
            if (bean.getId() != null) {
              String userInfo =
                  (String)
                      session
                          .createQuery(
                              "SELECT  u.firstName from StudyPermissionBO s , UserBO u where s.studyId=:id"
                                  + " and s.userId=u.userId and s.projectLead=1")
                          .setParameter("id", bean.getId())
                          .setMaxResults(1)
                          .uniqueResult();
              if (StringUtils.isNotEmpty(userInfo)) {
                bean.setProjectLeadName(userInfo);
              } else {
                bean.setProjectLeadName("None");
              }
            }
            if (userBO.getRoleId().equals("1")) {
              bean.setViewPermission(true);
            }
          }
        }
      }

    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyList() - Ends");
    return studyListBeans;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<StudyListBean> getStudyListByUserId(String userId) {
    logger.entry("begin getStudyListByUserId()");
    Session session = null;
    List<StudyListBean> studyListBeans = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(userId)) {
        query =
            session.createQuery(
                "select new com.fdahpstudydesigner.bean.StudyListBean(s.id,s.customStudyId,s.name,p.viewPermission)"
                    + " from StudyBo s,StudyPermissionBO p"
                    + " where s.id=p.studyId"
                    + " and s.version = 0"
                    + " and p.userId=:impValue");
        query.setParameter("impValue", userId);
        studyListBeans = query.list();
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyListByUserId() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyListByUserId() - Ends");
    return studyListBeans;
  }

  @Override
  public StudyBo getStudyLiveStatusByCustomId(String customStudyId) {
    logger.entry("begin getStudyLiveStatusByCustomId()");
    StudyBo studyLive = null;
    Session session = null;
    String searchQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      searchQuery = "FROM StudyBo SBO WHERE SBO.customStudyId = :customStudyId AND SBO.live = 1";
      query = session.createQuery(searchQuery);
      query.setParameter("customStudyId", customStudyId);
      studyLive = (StudyBo) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyLiveStatusByCustomId() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyLiveStatusByCustomId() - Ends");
    return studyLive;
  }

  @Override
  public ResourceBO getStudyProtocol(String studyId) {
    logger.entry("begin getStudyProtocol()");
    ResourceBO studyprotocol = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              " FROM ResourceBO RBO WHERE RBO.studyId=:studyId AND RBO.studyProtocol = true ");
      query.setParameter("studyId", studyId);
      studyprotocol = (ResourceBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyProtocol() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyProtocol() - Ends");
    return studyprotocol;
  }

  @SuppressWarnings("unchecked")
  public List<Integer> getSuperAdminUserIds() {
    logger.entry("begin getSuperAdminUserIds()");
    Session session = null;
    List<Integer> superAdminUserIds = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createSQLQuery(
              "SELECT u.user_id FROM users u WHERE u.user_id in (SELECT upm.user_id FROM user_permission_mapping upm WHERE upm.permission_id = (SELECT up.permission_id FROM user_permissions up WHERE up.permissions = 'ROLE_SUPERADMIN'))");
      superAdminUserIds = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getSuperAdminUserIds() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getSuperAdminUserIds() - Ends");
    return superAdminUserIds;
  }

  @Override
  public String markAsCompleted(
      String studyId,
      String markCompleted,
      boolean flag,
      SessionObject sesObj,
      String customStudyId) {
    logger.entry("begin markAsCompleted()");
    String msg = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    int count = 0;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      /**
       * match the markCompleted flag and complete the resource/notification/consent/consent review
       * /checkList/activeTaskList/questionnaire/comprehenstionTest section of study before launch
       */
      if (markCompleted.equals(FdahpStudyDesignerConstants.NOTIFICATION)) {
        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET miscellaneousNotification = :flag WHERE studyId =:studyId ");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();

      } else if (markCompleted.equals(FdahpStudyDesignerConstants.RESOURCE)) {
        if (flag) {
          auditLogDAO.updateDraftToEditedStatus(
              session,
              transaction,
              sesObj.getUserId(),
              FdahpStudyDesignerConstants.DRAFT_STUDY,
              studyId);
        }

        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET miscellaneousResources = :flag WHERE studyId = :studyId ");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();
      } else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.CONESENT)) {
        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET consentEduInfo = :flag WHERE studyId =:studyId ");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();

        auditLogDAO.updateDraftToEditedStatus(
            session,
            transaction,
            sesObj.getUserId(),
            FdahpStudyDesignerConstants.DRAFT_CONSCENT,
            studyId);

      } else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.CONESENT_REVIEW)) {
        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET eConsent =:flag WHERE studyId =:studyId ");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();

        auditLogDAO.updateDraftToEditedStatus(
            session,
            transaction,
            sesObj.getUserId(),
            FdahpStudyDesignerConstants.DRAFT_CONSCENT,
            studyId);

      } else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.CHECK_LIST)) {
        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET checkList = :flag WHERE studyId = :studyId");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();
      } else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTIVETASK_LIST)) {
        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET studyExcActiveTask =:flag WHERE studyId = :studyId");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();

        auditLogDAO.updateDraftToEditedStatus(
            session,
            transaction,
            sesObj.getUserId(),
            FdahpStudyDesignerConstants.DRAFT_ACTIVETASK,
            studyId);

      } else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTIONNAIRE)) {
        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET studyExcQuestionnaries =:flag WHERE studyId =:studyId ");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();

        auditLogDAO.updateDraftToEditedStatus(
            session,
            transaction,
            sesObj.getUserId(),
            FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE,
            studyId);

      } else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPREHENSION_TEST)) {
        query =
            session.createQuery(
                " UPDATE StudySequenceBo SET comprehensionTest =:flag WHERE studyId =:studyId ");
        query.setParameter("flag", flag);
        query.setParameter("studyId", studyId);
        count = query.executeUpdate();

        auditLogDAO.updateDraftToEditedStatus(
            session,
            transaction,
            sesObj.getUserId(),
            FdahpStudyDesignerConstants.DRAFT_CONSCENT,
            studyId);
      }
      if (count > 0) {
        msg = FdahpStudyDesignerConstants.SUCCESS;
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - markAsCompleted() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("markAsCompleted() - Ends");
    return msg;
  }

  @Override
  public String reOrderComprehensionTestQuestion(
      String studyId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("begin reOrderComprehensionTestQuestion()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    Query query = null;
    int count = 0;
    ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
    StudySequenceBo studySequence = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String updateQuery = "";
      query =
          session.createQuery(
              "From ComprehensionTestQuestionBo CTB where CTB.studyId=:studyId and CTB.sequenceNo =:oldOrderNumber and CTB.active=1");
      query.setParameter("studyId", studyId);
      query.setParameter("oldOrderNumber", oldOrderNumber);
      comprehensionTestQuestionBo = (ComprehensionTestQuestionBo) query.uniqueResult();
      if (comprehensionTestQuestionBo != null) {
        if (oldOrderNumber < newOrderNumber) {
          updateQuery =
              "update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=CTB.sequenceNo-1 where CTB.studyId=:studyId and CTB.sequenceNo <= :newOrderNumber and CTB.sequenceNo > :oldOrderNumber and CTB.active=1";
          query = session.createQuery(updateQuery);
          query.setParameter("studyId", studyId);
          query.setParameter("newOrderNumber", newOrderNumber);
          query.setParameter("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session.createQuery(
                    "update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=:newOrderNumber where CTB.id=:id and CTB.active=1");
            query.setParameter("newOrderNumber", newOrderNumber);
            query.setParameter("id", comprehensionTestQuestionBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        } else if (oldOrderNumber > newOrderNumber) {
          updateQuery =
              "update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=CTB.sequenceNo+1 where CTB.studyId=:studyId and CTB.sequenceNo >=:newOrderNumber and CTB.sequenceNo < :oldOrderNumber and CTB.active=1";
          query = session.createQuery(updateQuery);
          query.setParameter("studyId", studyId);
          query.setParameter("newOrderNumber", newOrderNumber);
          query.setParameter("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session.createQuery(
                    "update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=:newOrderNumber where CTB.id= :id and CTB.active=1");
            query.setParameter("newOrderNumber", newOrderNumber);
            query.setParameter("id", comprehensionTestQuestionBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
        if (comprehensionTestQuestionBo.getStudyId() != null) {
          studySequence =
              (StudySequenceBo)
                  session
                      .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                      .setString(
                          FdahpStudyDesignerConstants.STUDY_ID,
                          comprehensionTestQuestionBo.getStudyId())
                      .uniqueResult();
          if (studySequence != null) {
            if (studySequence.isComprehensionTest()) {
              studySequence.setComprehensionTest(false);
            }
            session.saveOrUpdate(studySequence);
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - reOrderComprehensionTestQuestion() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("reOrderComprehensionTestQuestion() - Ends");
    return message;
  }

  @Override
  public String reOrderConsentInfoList(String studyId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("begin reOrderConsentInfoList()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    Query query = null;
    int count = 0;
    ConsentInfoBo consentInfoBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String updateQuery = "";
      query =
          session.createQuery(
              "From ConsentInfoBo CIB where CIB.studyId=:studyId and CIB.sequenceNo =:oldOrderNumber and CIB.active=1");
      query.setParameter("studyId", studyId);
      query.setParameter("oldOrderNumber", oldOrderNumber);
      consentInfoBo = (ConsentInfoBo) query.uniqueResult();
      if (consentInfoBo != null) {
        if (oldOrderNumber < newOrderNumber) {
          updateQuery =
              "update ConsentInfoBo CIBO set CIBO.sequenceNo=CIBO.sequenceNo-1 where CIBO.studyId=:studyId and CIBO.sequenceNo <= :newOrderNumber and CIBO.sequenceNo > :oldOrderNumber and CIBO.active=1";
          query = session.createQuery(updateQuery);
          query.setParameter("studyId", studyId);
          query.setParameter("newOrderNumber", newOrderNumber);
          query.setParameter("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session.createQuery(
                    "update ConsentInfoBo C set C.sequenceNo=:newOrderNumber where C.id= :id and C.active=1");
            query.setParameter("newOrderNumber", newOrderNumber);
            query.setParameter("id", consentInfoBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        } else if (oldOrderNumber > newOrderNumber) {
          updateQuery =
              "update ConsentInfoBo CIBO set CIBO.sequenceNo=CIBO.sequenceNo+1 where CIBO.studyId=:studyId and CIBO.sequenceNo >=:newOrderNumber and CIBO.sequenceNo < :oldOrderNumber and CIBO.active=1";
          query = session.createQuery(updateQuery);
          query.setParameter("studyId", studyId);
          query.setParameter("newOrderNumber", newOrderNumber);
          query.setParameter("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session.createQuery(
                    "update ConsentInfoBo C set C.sequenceNo=:newOrderNumber where C.id=:id and C.active=1");
            query.setParameter("newOrderNumber", newOrderNumber);
            query.setParameter("id", consentInfoBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
        if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          StudySequenceBo studySequence =
              (StudySequenceBo)
                  session
                      .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                      .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                      .uniqueResult();
          if (studySequence != null) {
            studySequence.setConsentEduInfo(false);
            if (studySequence.iseConsent()) {
              studySequence.seteConsent(false);
            }
            session.saveOrUpdate(studySequence);
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - reOrderConsentInfoList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("reOrderConsentInfoList() - Ends");
    return message;
  }

  @Override
  public String reorderEligibilityTestQusAns(
      String eligibilityId, int oldOrderNumber, int newOrderNumber, String studyId) {
    logger.entry("begin reorderEligibilityTestQusAns");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    Query hibQuery = null;
    int count = 0;
    EligibilityTestBo eligibilityTest = null;
    StringBuilder updatenewOrderQuery;
    Transaction trans = null;
    StringBuilder updateQuery = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      trans = session.beginTransaction();
      hibQuery =
          session
              .getNamedQuery("EligibilityTestBo.findByEligibilityIdAndSequenceNo")
              .setString("eligibilityId", eligibilityId)
              .setInteger("sequenceNo", oldOrderNumber);
      eligibilityTest = (EligibilityTestBo) hibQuery.uniqueResult();
      if (eligibilityTest != null) {
        if (oldOrderNumber < newOrderNumber) {
          updateQuery = new StringBuilder();
          updateQuery
              .append(
                  "UPDATE EligibilityTestBo ETB set ETB.sequenceNo=ETB.sequenceNo-1 WHERE ETB.eligibilityId=:eligibilityId")
              .append(" AND ETB.sequenceNo <= :newOrderNumber")
              .append(" AND ETB.sequenceNo > :oldOrderNumber")
              .append(" AND ETB.active = true");
          hibQuery = session.createQuery(updateQuery.toString());
          hibQuery.setParameter("eligibilityId", eligibilityId);
          hibQuery.setParameter("newOrderNumber", newOrderNumber);
          hibQuery.setParameter("oldOrderNumber", oldOrderNumber);
          count = hibQuery.executeUpdate();
        } else if (oldOrderNumber > newOrderNumber) {
          updateQuery = new StringBuilder();
          updateQuery
              .append(
                  "UPDATE EligibilityTestBo ETB set ETB.sequenceNo=ETB.sequenceNo+1 WHERE ETB.eligibilityId= :eligibilityId")
              .append(" AND ETB.sequenceNo >= :newOrderNumber")
              .append(" AND ETB.sequenceNo < :oldOrderNumber")
              .append(" AND ETB.active = true");
          hibQuery = session.createQuery(updateQuery.toString());
          hibQuery.setParameter("eligibilityId", eligibilityId);
          hibQuery.setParameter("newOrderNumber", newOrderNumber);
          hibQuery.setParameter("oldOrderNumber", oldOrderNumber);
          count = hibQuery.executeUpdate();
        }
        updatenewOrderQuery = new StringBuilder();
        updatenewOrderQuery
            .append("UPDATE EligibilityTestBo ETB set ETB.sequenceNo=:newOrderNumber")
            .append(" WHERE ETB.id=:id");
        if (count > 0) {
          hibQuery = session.createQuery(updatenewOrderQuery.toString());
          hibQuery.setParameter("newOrderNumber", newOrderNumber);
          hibQuery.setParameter("id", eligibilityTest.getId());
          count = hibQuery.executeUpdate();
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
        if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          StudySequenceBo studySequence =
              (StudySequenceBo)
                  session
                      .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                      .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                      .uniqueResult();
          if ((studySequence != null) && studySequence.isEligibility()) {
            studySequence.setEligibility(false);
            session.update(studySequence);
          }
        }
      }
      trans.commit();
    } catch (Exception e) {
      if (null != trans) {
        trans.rollback();
      }
      logger.error("StudyDAOImpl - reorderEligibilityTestQusAns - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("reorderEligibilityTestQusAns - Ends");
    return message;
  }

  @Override
  public String reOrderResourceList(String studyId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("begin reOrderResourceList()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    Query query = null;
    int count = 0;
    ResourceBO resourceBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String updateQuery = "";
      query =
          session.createQuery(
              "From ResourceBO RBO where RBO.studyId=:studyId and RBO.sequenceNo =:oldOrderNumber and RBO.status=1");
      query.setParameter("studyId", studyId);
      query.setParameter("oldOrderNumber", oldOrderNumber);
      resourceBo = (ResourceBO) query.uniqueResult();
      if (resourceBo != null) {
        if (oldOrderNumber < newOrderNumber) {
          updateQuery =
              "update ResourceBO RBO set RBO.sequenceNo=RBO.sequenceNo-1 where RBO.studyId=:studyId and RBO.sequenceNo <= :newOrderNumber and RBO.sequenceNo > :oldOrderNumber and RBO.status=1";
          query = session.createQuery(updateQuery);
          query.setParameter("studyId", studyId);
          query.setParameter("newOrderNumber", newOrderNumber);
          query.setParameter("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session.createQuery(
                    "update ResourceBO RBO set RBO.sequenceNo= :newOrderNumber where RBO.id=:id and RBO.status=1");
            query.setParameter("newOrderNumber", newOrderNumber);
            query.setParameter("id", resourceBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        } else if (oldOrderNumber > newOrderNumber) {
          updateQuery =
              "update ResourceBO RBO set RBO.sequenceNo=RBO.sequenceNo+1 where RBO.studyId= :studyId and RBO.sequenceNo >= :newOrderNumber and RBO.sequenceNo < :oldOrderNumber and RBO.status=1";
          query = session.createQuery(updateQuery);
          query.setParameter("studyId", studyId);
          query.setParameter("newOrderNumber", newOrderNumber);
          query.setParameter("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session.createQuery(
                    "update ResourceBO RBO set RBO.sequenceNo= :newOrderNumber where RBO.id=:id and RBO.status=1");
            query.setParameter("newOrderNumber", newOrderNumber);
            query.setParameter("id", resourceBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
        if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          StudySequenceBo studySequence =
              (StudySequenceBo)
                  session
                      .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                      .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                      .uniqueResult();
          if (studySequence != null) {
            studySequence.setMiscellaneousResources(false);
            session.saveOrUpdate(studySequence);
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - reOrderResourceList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("reOrderResourceList() - Ends");
    return message;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public boolean resetDraftStudyByCustomStudyId(
      String customStudyId, String action, SessionObject sesObj) {
    logger.entry("begin resetDraftStudyByCustomStudyId()");
    Session session = null;
    boolean flag = false;
    StudyBo liveStudyBo = null;
    List<StudyBo> draftDatas = null;
    List<StudyPageBo> studyPageBo = null;
    EligibilityBo eligibilityBo = null;
    List<ResourceBO> resourceBOList = null;
    List<QuestionnaireBo> questionnaires = null;
    QuestionReponseTypeBo questionReponseTypeBo = null;
    List<String> studyIdList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      liveStudyBo =
          (StudyBo)
              session
                  .getNamedQuery("getStudyLiveVersion")
                  .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId)
                  .uniqueResult();
      if (liveStudyBo != null) {
        // create new Study and made it draft
        StudyBo studyDreaftBo = SerializationUtils.clone(liveStudyBo);
        studyDreaftBo.setVersion(0f);
        studyDreaftBo.setId(null);
        if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
          studyDreaftBo.setLive(5);
          studyDreaftBo.setThumbnailImage(null);
        } else {
          studyDreaftBo.setCustomStudyId(null);
          studyDreaftBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          studyDreaftBo.setCreatedBy(sesObj.getUserId());
          studyDreaftBo.setModifiedOn(null);
          studyDreaftBo.setModifiedBy(null);
          studyDreaftBo.setLive(0);
        }
        studyDreaftBo.setHasActivetaskDraft(0);
        studyDreaftBo.setHasQuestionnaireDraft(0);
        studyDreaftBo.setHasConsentDraft(0);
        studyDreaftBo.setHasStudyDraft(0);
        studyDreaftBo.setStatus(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH);
        session.save(studyDreaftBo);

        // Study Permission
        String permissionQuery =
            "select s.user_id,s.view_permission"
                + " from study_permission s, user_permission_mapping u, user_permissions p"
                + " where s.study_id= :id"
                + " and s.user_id=u.user_id"
                + " and u.permission_id = p.permission_id"
                + " and p.permissions='ROLE_SUPERADMIN'";
        Iterator iterator =
            session
                .createSQLQuery(permissionQuery)
                .setParameter("id", liveStudyBo.getId())
                .list()
                .iterator();
        while (iterator.hasNext()) {
          Object[] objects = (Object[]) iterator.next();
          if (objects != null) {
            Integer superadminId = (Integer) objects[0];
            StudyPermissionBO studyPermissionBO = new StudyPermissionBO();
            studyPermissionBO.setUserId((String) objects[0]);
            studyPermissionBO.setViewPermission((Boolean) objects[1]);
            studyPermissionBO.setStudyId(studyDreaftBo.getId());
            studyPermissionBO.setStudyPermissionId(null);
            session.save(studyPermissionBO);
            if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)
                && !superadminId.equals(sesObj.getUserId())) {
              studyPermissionBO = new StudyPermissionBO();
              studyPermissionBO.setUserId(sesObj.getUserId());
              studyPermissionBO.setViewPermission((Boolean) objects[1]);
              studyPermissionBO.setStudyId(studyDreaftBo.getId());
              studyPermissionBO.setStudyPermissionId(null);
              session.save(studyPermissionBO);
            }
          }
        }
        // studySequence
        StudySequenceBo studySequenceBo = new StudySequenceBo();
        studySequenceBo.setStudyId(studyDreaftBo.getId());
        session.save(studySequenceBo);

        // Over View
        query = session.createQuery("from StudyPageBo where studyId= :id ");
        query.setParameter("id", liveStudyBo.getId());
        studyPageBo = query.list();
        if ((studyPageBo != null) && !studyPageBo.isEmpty()) {
          for (StudyPageBo pageBo : studyPageBo) {
            StudyPageBo subPageBo = SerializationUtils.clone(pageBo);
            subPageBo.setStudyId(studyDreaftBo.getId());
            if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
              subPageBo.setImagePath(null);
            }
            subPageBo.setPageId(null);
            session.save(subPageBo);
          }
        }

        // Eligibility
        query =
            session
                .getNamedQuery("getEligibiltyByStudyId")
                .setString(FdahpStudyDesignerConstants.STUDY_ID, liveStudyBo.getId());
        eligibilityBo = (EligibilityBo) query.uniqueResult();
        if (eligibilityBo != null) {
          EligibilityBo bo = SerializationUtils.clone(eligibilityBo);
          bo.setStudyId(studyDreaftBo.getId());
          bo.setId(null);
          session.save(bo);
          List<EligibilityTestBo> eligibilityTestList = null;
          eligibilityTestList =
              session
                  .getNamedQuery("EligibilityTestBo.findByEligibilityId")
                  .setString(FdahpStudyDesignerConstants.ELIGIBILITY_ID, eligibilityBo.getId())
                  .list();
          if ((eligibilityTestList != null) && !eligibilityTestList.isEmpty()) {
            for (EligibilityTestBo eligibilityTestBo : eligibilityTestList) {
              EligibilityTestBo newEligibilityTestBo = SerializationUtils.clone(eligibilityTestBo);
              newEligibilityTestBo.setId(null);
              newEligibilityTestBo.setEligibilityId(bo.getId());
              newEligibilityTestBo.setUsed(false);
              if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
                newEligibilityTestBo.setStatus(false);
              }
              session.save(newEligibilityTestBo);
            }
          }
        }

        // resources
        String searchQuery =
            " FROM ResourceBO RBO WHERE RBO.studyId=:id AND RBO.status = 1 ORDER BY RBO.createdOn";
        query = session.createQuery(searchQuery);
        query.setParameter("id", liveStudyBo.getId());
        resourceBOList = query.list();
        if ((resourceBOList != null) && !resourceBOList.isEmpty()) {
          for (ResourceBO bo : resourceBOList) {
            ResourceBO resourceBO = SerializationUtils.clone(bo);
            resourceBO.setStudyId(studyDreaftBo.getId());
            if (StringUtils.isNotEmpty(bo.getPdfUrl()) && StringUtils.isNotEmpty(bo.getPdfName())) {
              resourceBO.setAction(false);
            }
            resourceBO.setId(null);
            if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
              resourceBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              resourceBO.setCreatedBy(sesObj.getUserId());
              resourceBO.setModifiedBy(null);
              resourceBO.setModifiedOn(null);
            } else {
              resourceBO.setPdfName(null);
              resourceBO.setPdfUrl(null);
            }
            session.save(resourceBO);
          }
        }
        // Questionarries
        query =
            session.createQuery(
                " From QuestionnaireBo QBO WHERE QBO.live=1 and QBO.active=1 and QBO.customStudyId= :customStudyId order by QBO.createdDate");
        query.setParameter("customStudyId", customStudyId);
        questionnaires = query.list();
        if ((questionnaires != null) && !questionnaires.isEmpty()) {
          for (QuestionnaireBo questionnaireBo : questionnaires) {
            boolean draftFlag = false;
            QuestionnaireBo newQuestionnaireBo = SerializationUtils.clone(questionnaireBo);
            newQuestionnaireBo.setId(null);
            newQuestionnaireBo.setStudyId(studyDreaftBo.getId());
            newQuestionnaireBo.setVersion(questionnaireBo.getVersion());
            if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
              newQuestionnaireBo.setLive(5);
            } else {
              newQuestionnaireBo.setCustomStudyId(null);
              newQuestionnaireBo.setLive(0);
              newQuestionnaireBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
              newQuestionnaireBo.setCreatedBy(sesObj.getUserId());
              newQuestionnaireBo.setModifiedBy(null);
              newQuestionnaireBo.setModifiedDate(null);
              // newQuestionnaireBo.setShortTitle(null);
              newQuestionnaireBo.setStatus(false);
            }
            newQuestionnaireBo.setVersion(0f);
            session.save(newQuestionnaireBo);
            /** Schedule Purpose creating draft Start * */
            if (StringUtils.isNotEmpty(questionnaireBo.getFrequency())) {
              if (questionnaireBo
                  .getFrequency()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                searchQuery =
                    "From QuestionnaireCustomScheduleBo QCSBO where QCSBO.questionnairesId=:id";
                List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleList =
                    session
                        .createQuery(searchQuery)
                        .setString("id", questionnaireBo.getId())
                        .list();
                if ((questionnaireCustomScheduleList != null)
                    && !questionnaireCustomScheduleList.isEmpty()) {
                  for (QuestionnaireCustomScheduleBo customScheduleBo :
                      questionnaireCustomScheduleList) {
                    QuestionnaireCustomScheduleBo newCustomScheduleBo =
                        SerializationUtils.clone(customScheduleBo);
                    newCustomScheduleBo.setQuestionnairesId(newQuestionnaireBo.getId());
                    newCustomScheduleBo.setId(null);
                    newCustomScheduleBo.setUsed(false);
                    session.save(newCustomScheduleBo);
                  }
                }
              } else {
                searchQuery =
                    "From QuestionnairesFrequenciesBo QFBO where QFBO.questionnairesId=:id";
                List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList =
                    session
                        .createQuery(searchQuery)
                        .setString("id", questionnaireBo.getId())
                        .list();
                if ((questionnairesFrequenciesList != null)
                    && !questionnairesFrequenciesList.isEmpty()) {
                  for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo :
                      questionnairesFrequenciesList) {
                    QuestionnairesFrequenciesBo newQuestionnairesFrequenciesBo =
                        SerializationUtils.clone(questionnairesFrequenciesBo);
                    newQuestionnairesFrequenciesBo.setQuestionnairesId(newQuestionnaireBo.getId());
                    newQuestionnairesFrequenciesBo.setId(null);
                    session.save(newQuestionnairesFrequenciesBo);
                  }
                }
              }
            }
            /** Schedule Purpose creating draft End * */

            /** Content purpose creating draft Start * */
            List<Integer> destinationList = new ArrayList<>();
            Map<Integer, String> destionationMapList = new HashMap<>();

            List<QuestionnairesStepsBo> existedQuestionnairesStepsBoList = null;
            List<QuestionnairesStepsBo> newQuestionnairesStepsBoList = new ArrayList<>();
            List<QuestionResponseSubTypeBo> existingQuestionResponseSubTypeList = new ArrayList<>();
            List<QuestionResponseSubTypeBo> newQuestionResponseSubTypeList = new ArrayList<>();
            query =
                session
                    .getNamedQuery("getQuestionnaireStepList")
                    .setString("questionnaireId", questionnaireBo.getId());
            existedQuestionnairesStepsBoList = query.list();
            if ((existedQuestionnairesStepsBoList != null)
                && !existedQuestionnairesStepsBoList.isEmpty()) {
              for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
                String destionStep = questionnairesStepsBo.getDestinationStep();
                if (destionStep.equals(String.valueOf(0))) {
                  destinationList.add(-1);
                } else {
                  for (int i = 0; i < existedQuestionnairesStepsBoList.size(); i++) {
                    if ((existedQuestionnairesStepsBoList.get(i).getStepId() != null)
                        && destionStep.equals(
                            existedQuestionnairesStepsBoList.get(i).getStepId())) {
                      destinationList.add(i);
                      break;
                    }
                  }
                }
                destionationMapList.put(
                    questionnairesStepsBo.getSequenceNo(), questionnairesStepsBo.getStepId());
              }
              for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
                if (StringUtils.isNotEmpty(questionnairesStepsBo.getStepType())) {
                  QuestionnairesStepsBo newQuestionnairesStepsBo =
                      SerializationUtils.clone(questionnairesStepsBo);
                  newQuestionnairesStepsBo.setQuestionnairesId(newQuestionnaireBo.getId());
                  newQuestionnairesStepsBo.setStepId(null);
                  if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
                    newQuestionnairesStepsBo.setCreatedOn(
                        FdahpStudyDesignerUtil.getCurrentDateTime());
                    newQuestionnairesStepsBo.setCreatedBy(sesObj.getUserId());
                    newQuestionnairesStepsBo.setModifiedBy(null);
                    newQuestionnairesStepsBo.setModifiedOn(null);
                    newQuestionnairesStepsBo.setStatus(false);
                  }
                  session.save(newQuestionnairesStepsBo);
                  if (questionnairesStepsBo
                      .getStepType()
                      .equalsIgnoreCase(FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
                    InstructionsBo instructionsBo =
                        (InstructionsBo)
                            session
                                .getNamedQuery("getInstructionStep")
                                .setString("id", questionnairesStepsBo.getInstructionFormId())
                                .uniqueResult();
                    if (instructionsBo != null) {
                      InstructionsBo newInstructionsBo = SerializationUtils.clone(instructionsBo);
                      newInstructionsBo.setId(null);
                      if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
                        newInstructionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                        newInstructionsBo.setCreatedBy(sesObj.getUserId());
                        newInstructionsBo.setModifiedBy(null);
                        newInstructionsBo.setModifiedOn(null);
                        newInstructionsBo.setStatus(false);
                      }
                      session.save(newInstructionsBo);

                      // updating new InstructionId
                      newQuestionnairesStepsBo.setInstructionFormId(newInstructionsBo.getId());
                    }
                  } else if (questionnairesStepsBo
                      .getStepType()
                      .equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
                    QuestionsBo questionsBo =
                        (QuestionsBo)
                            session
                                .getNamedQuery("getQuestionStep")
                                .setString("stepId", questionnairesStepsBo.getInstructionFormId())
                                .uniqueResult();
                    if (questionsBo != null) {
                      boolean questionDraftFlag = false;
                      // Question response subType
                      List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
                          session
                              .getNamedQuery("getQuestionSubResponse")
                              .setString("responseTypeId", questionsBo.getId())
                              .list();

                      List<QuestionConditionBranchBo> questionConditionBranchList =
                          session
                              .getNamedQuery("getQuestionConditionBranchList")
                              .setString("questionId", questionsBo.getId())
                              .list();

                      // Question response Type
                      questionReponseTypeBo =
                          (QuestionReponseTypeBo)
                              session
                                  .getNamedQuery("getQuestionResponse")
                                  .setString("questionsResponseTypeId", questionsBo.getId())
                                  .uniqueResult();

                      QuestionsBo newQuestionsBo = SerializationUtils.clone(questionsBo);
                      newQuestionsBo.setId(null);
                      if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
                        newQuestionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                        newQuestionsBo.setCreatedBy(sesObj.getUserId());
                        newQuestionsBo.setModifiedBy(null);
                        newQuestionsBo.setModifiedOn(null);
                        newQuestionsBo.setStatus(false);
                      }
                      session.save(newQuestionsBo);

                      // Question response Type
                      if (questionReponseTypeBo != null) {
                        QuestionReponseTypeBo newQuestionReponseTypeBo =
                            SerializationUtils.clone(questionReponseTypeBo);
                        newQuestionReponseTypeBo.setResponseTypeId(null);
                        newQuestionReponseTypeBo.setQuestionsResponseTypeId(newQuestionsBo.getId());
                        session.save(newQuestionReponseTypeBo);
                      }

                      // Question Condition branching
                      // logic
                      if ((questionConditionBranchList != null)
                          && !questionConditionBranchList.isEmpty()) {
                        for (QuestionConditionBranchBo questionConditionBranchBo :
                            questionConditionBranchList) {
                          QuestionConditionBranchBo newQuestionConditionBranchBo =
                              SerializationUtils.clone(questionConditionBranchBo);
                          newQuestionConditionBranchBo.setConditionId(null);
                          newQuestionConditionBranchBo.setQuestionId(newQuestionsBo.getId());
                          session.save(newQuestionConditionBranchBo);
                        }
                      }

                      // Question response subType
                      if ((questionResponseSubTypeList != null)
                          && !questionResponseSubTypeList.isEmpty()) {
                        existingQuestionResponseSubTypeList.addAll(questionResponseSubTypeList);

                        for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                            questionResponseSubTypeList) {
                          if (StringUtils.isNotEmpty(questionResponseSubTypeBo.getImage())
                              && StringUtils.isNotEmpty(
                                  questionResponseSubTypeBo.getSelectedImage())) {
                            draftFlag = true;
                            questionDraftFlag = true;
                          }
                          QuestionResponseSubTypeBo newQuestionResponseSubTypeBo =
                              SerializationUtils.clone(questionResponseSubTypeBo);
                          newQuestionResponseSubTypeBo.setResponseSubTypeValueId(null);
                          newQuestionResponseSubTypeBo.setResponseTypeId(newQuestionsBo.getId());
                          if (!action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
                            newQuestionResponseSubTypeBo.setImage(null);
                            newQuestionResponseSubTypeBo.setSelectedImage(null);
                          }
                          newQuestionResponseSubTypeBo.setDestinationStepId(null);
                          session.save(newQuestionResponseSubTypeBo);
                          newQuestionResponseSubTypeList.add(newQuestionResponseSubTypeBo);
                        }
                      }
                      if (questionDraftFlag) {
                        newQuestionnairesStepsBo.setStatus(false);
                        newQuestionsBo.setStatus(false);
                        session.update(newQuestionsBo);
                      }
                      // updating new InstructionId
                      newQuestionnairesStepsBo.setInstructionFormId(newQuestionsBo.getId());
                    }
                  } else if (questionnairesStepsBo
                      .getStepType()
                      .equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
                    FormBo formBo =
                        (FormBo)
                            session
                                .getNamedQuery("getFormBoStep")
                                .setString("stepId", questionnairesStepsBo.getInstructionFormId())
                                .uniqueResult();
                    if (formBo != null) {
                      boolean formDraftFlag = false;
                      FormBo newFormBo = SerializationUtils.clone(formBo);
                      newFormBo.setFormId(null);
                      session.save(newFormBo);

                      List<FormMappingBo> formMappingBoList =
                          session
                              .getNamedQuery("getFormByFormId")
                              .setString("formId", formBo.getFormId())
                              .list();
                      if ((formMappingBoList != null) && !formMappingBoList.isEmpty()) {
                        for (FormMappingBo formMappingBo : formMappingBoList) {
                          FormMappingBo newMappingBo = SerializationUtils.clone(formMappingBo);
                          newMappingBo.setFormId(newFormBo.getFormId());
                          newMappingBo.setId(null);

                          QuestionsBo questionsBo =
                              (QuestionsBo)
                                  session
                                      .getNamedQuery("getQuestionByFormId")
                                      .setString("formId", formMappingBo.getQuestionId())
                                      .uniqueResult();
                          if (questionsBo != null) {
                            boolean questionFlag = false;
                            // Question response
                            // subType
                            List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
                                session
                                    .getNamedQuery("getQuestionSubResponse")
                                    .setString("responseTypeId", questionsBo.getId())
                                    .list();

                            // Question response
                            // Type
                            questionReponseTypeBo =
                                (QuestionReponseTypeBo)
                                    session
                                        .getNamedQuery("getQuestionResponse")
                                        .setString("questionsResponseTypeId", questionsBo.getId())
                                        .uniqueResult();

                            QuestionsBo newQuestionsBo = SerializationUtils.clone(questionsBo);
                            newQuestionsBo.setId(null);
                            if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
                              newQuestionsBo.setCreatedOn(
                                  FdahpStudyDesignerUtil.getCurrentDateTime());
                              newQuestionsBo.setCreatedBy(sesObj.getUserId());
                              newQuestionsBo.setModifiedBy(null);
                              newQuestionsBo.setModifiedOn(null);
                              newQuestionsBo.setStatus(false);
                            }
                            session.save(newQuestionsBo);

                            // Question response
                            // Type
                            if (questionReponseTypeBo != null) {
                              QuestionReponseTypeBo newQuestionReponseTypeBo =
                                  SerializationUtils.clone(questionReponseTypeBo);
                              newQuestionReponseTypeBo.setResponseTypeId(null);
                              newQuestionReponseTypeBo.setQuestionsResponseTypeId(
                                  newQuestionsBo.getId());
                              session.save(newQuestionReponseTypeBo);
                            }

                            // Question response
                            // subType
                            if ((questionResponseSubTypeList != null)
                                && !questionResponseSubTypeList.isEmpty()) {
                              for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                                  questionResponseSubTypeList) {
                                if (StringUtils.isNotEmpty(questionResponseSubTypeBo.getImage())
                                    && StringUtils.isNotEmpty(
                                        questionResponseSubTypeBo.getSelectedImage())) {
                                  draftFlag = true;
                                  formDraftFlag = true;
                                  questionFlag = true;
                                }
                                QuestionResponseSubTypeBo newQuestionResponseSubTypeBo =
                                    SerializationUtils.clone(questionResponseSubTypeBo);
                                newQuestionResponseSubTypeBo.setResponseSubTypeValueId(null);
                                newQuestionResponseSubTypeBo.setResponseTypeId(
                                    newQuestionsBo.getId());
                                if (!action.equalsIgnoreCase(
                                    FdahpStudyDesignerConstants.COPY_STUDY)) {
                                  newQuestionResponseSubTypeBo.setImage(null);
                                  newQuestionResponseSubTypeBo.setSelectedImage(null);
                                }
                                session.save(newQuestionResponseSubTypeBo);
                              }
                            }

                            // adding questionId
                            newMappingBo.setQuestionId(newQuestionsBo.getId());
                            session.save(newMappingBo);
                            if (questionFlag) {
                              newQuestionsBo.setStatus(false);
                              session.update(newQuestionsBo);
                            }
                          }
                        }
                      }
                      // updating new formId
                      if (formDraftFlag) {
                        newQuestionnairesStepsBo.setStatus(false);
                      }
                      newQuestionnairesStepsBo.setInstructionFormId(newFormBo.getFormId());
                    }
                  }
                  session.update(newQuestionnairesStepsBo);
                  newQuestionnairesStepsBoList.add(newQuestionnairesStepsBo);
                }
              }
            }
            if ((destinationList != null) && !destinationList.isEmpty()) {
              for (int i = 0; i < destinationList.size(); i++) {
                String desId = String.valueOf(0);
                if (destinationList.get(i) != -1) {
                  desId = newQuestionnairesStepsBoList.get(destinationList.get(i)).getStepId();
                }
                newQuestionnairesStepsBoList.get(i).setDestinationStep(desId);
                session.update(newQuestionnairesStepsBoList.get(i));
              }
            }
            List<Integer> sequenceSubTypeList = new ArrayList<>();
            List<String> destinationResList = new ArrayList<>();
            if ((existingQuestionResponseSubTypeList != null)
                && !existingQuestionResponseSubTypeList.isEmpty()) {
              for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                  existingQuestionResponseSubTypeList) {
                if (StringUtils.isEmpty(questionResponseSubTypeBo.getDestinationStepId())) {
                  sequenceSubTypeList.add(null);
                } else if ((questionResponseSubTypeBo.getDestinationStepId() != null)
                    && questionResponseSubTypeBo.getDestinationStepId().equals(String.valueOf(0))) {
                  sequenceSubTypeList.add(-1);
                } else {
                  if ((existedQuestionnairesStepsBoList != null)
                      && !existedQuestionnairesStepsBoList.isEmpty()) {
                    for (QuestionnairesStepsBo questionnairesStepsBo :
                        existedQuestionnairesStepsBoList) {
                      if ((questionResponseSubTypeBo.getDestinationStepId() != null)
                          && questionResponseSubTypeBo
                              .getDestinationStepId()
                              .equals(questionnairesStepsBo.getStepId())) {
                        sequenceSubTypeList.add(questionnairesStepsBo.getSequenceNo());
                        break;
                      }
                    }
                  }
                }
              }
            }
            if ((sequenceSubTypeList != null) && !sequenceSubTypeList.isEmpty()) {
              for (int i = 0; i < sequenceSubTypeList.size(); i++) {
                String desId = null;
                if (sequenceSubTypeList.get(i) == null) {
                  desId = null;
                } else if (sequenceSubTypeList.get(i).equals(-1)) {
                  desId = String.valueOf(0);
                } else {
                  for (QuestionnairesStepsBo questionnairesStepsBo : newQuestionnairesStepsBoList) {
                    if (sequenceSubTypeList.get(i).equals(questionnairesStepsBo.getSequenceNo())) {
                      desId = questionnairesStepsBo.getStepId();
                      break;
                    }
                  }
                }
                destinationResList.add(desId);
              }
              for (int i = 0; i < destinationResList.size(); i++) {
                newQuestionResponseSubTypeList
                    .get(i)
                    .setDestinationStepId(destinationResList.get(i));
                session.update(newQuestionResponseSubTypeList.get(i));
              }
            }
            if (draftFlag) {
              newQuestionnaireBo.setStatus(false);
              session.update(newQuestionnaireBo);
            }
            /** Content purpose creating draft End * */
          }
        } // If Questionarries updated flag -1 then update End
        // ActiveTasks
        List<ActiveTaskBo> activeTasks = null;
        query =
            session.createQuery(
                "SELECT ATB FROM ActiveTaskBo ATB where ATB.active IS NOT NULL and ATB.active=1 and ATB.live =1 and customStudyId=:customStudyId order by id");
        query.setString("customStudyId", customStudyId);
        activeTasks = query.list();
        if ((activeTasks != null) && !activeTasks.isEmpty()) {
          for (ActiveTaskBo activeTaskBo : activeTasks) {
            ActiveTaskBo newActiveTaskBo = SerializationUtils.clone(activeTaskBo);
            newActiveTaskBo.setId(null);
            newActiveTaskBo.setStudyId(studyDreaftBo.getId());
            newActiveTaskBo.setVersion(activeTaskBo.getVersion());
            if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
              newActiveTaskBo.setLive(5);
            } else {
              newActiveTaskBo.setCustomStudyId(null);
              newActiveTaskBo.setLive(0);
              newActiveTaskBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
              newActiveTaskBo.setCreatedBy(sesObj.getUserId());
              newActiveTaskBo.setModifiedBy(null);
              newActiveTaskBo.setModifiedDate(null);
              newActiveTaskBo.setAction(false);
            }
            newActiveTaskBo.setVersion(0f);
            session.save(newActiveTaskBo);
            /** Schedule Purpose creating draft Start * */
            if (StringUtils.isNotEmpty(activeTaskBo.getFrequency())) {
              if (activeTaskBo
                  .getFrequency()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                searchQuery = "From ActiveTaskCustomScheduleBo QCSBO where QCSBO.activeTaskId=:id";
                List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleList =
                    session.createQuery(searchQuery).setString("id", activeTaskBo.getId()).list();
                if ((activeTaskCustomScheduleList != null)
                    && !activeTaskCustomScheduleList.isEmpty()) {
                  for (ActiveTaskCustomScheduleBo customScheduleBo : activeTaskCustomScheduleList) {
                    ActiveTaskCustomScheduleBo newCustomScheduleBo =
                        SerializationUtils.clone(customScheduleBo);
                    newCustomScheduleBo.setActiveTaskId(newActiveTaskBo.getId());
                    newCustomScheduleBo.setUsed(false);
                    newCustomScheduleBo.setId(null);
                    session.save(newCustomScheduleBo);
                  }
                }
              } else {
                searchQuery = "From ActiveTaskFrequencyBo QFBO where QFBO.activeTaskId=:id";
                List<ActiveTaskFrequencyBo> activeTaskFrequenciesList =
                    session.createQuery(searchQuery).setString("id", activeTaskBo.getId()).list();
                if ((activeTaskFrequenciesList != null) && !activeTaskFrequenciesList.isEmpty()) {
                  for (ActiveTaskFrequencyBo activeTaskFrequenciesBo : activeTaskFrequenciesList) {
                    ActiveTaskFrequencyBo newFrequenciesBo =
                        SerializationUtils.clone(activeTaskFrequenciesBo);
                    newFrequenciesBo.setActiveTaskId(newActiveTaskBo.getId());
                    newFrequenciesBo.setId(null);
                    session.save(newFrequenciesBo);
                  }
                }
              }
            }
            /** Schedule Purpose creating draft End * */

            /** Content Purpose creating draft Start * */
            query =
                session
                    .getNamedQuery("getAttributeListByActiveTAskId")
                    .setString("activeTaskId", activeTaskBo.getId());
            List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBoList = query.list();
            if ((activeTaskAtrributeValuesBoList != null)
                && !activeTaskAtrributeValuesBoList.isEmpty()) {
              for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo :
                  activeTaskAtrributeValuesBoList) {
                ActiveTaskAtrributeValuesBo newActiveTaskAtrributeValuesBo =
                    SerializationUtils.clone(activeTaskAtrributeValuesBo);
                newActiveTaskAtrributeValuesBo.setActiveTaskId(newActiveTaskBo.getId());
                newActiveTaskAtrributeValuesBo.setAttributeValueId(null);
                session.save(newActiveTaskAtrributeValuesBo);
              }
            }
            /** Content Purpose creating draft End * */
          }
        } // Active TAsk End

        // Consent updated update Start
        query =
            session.createQuery(
                "From ConsentBo CBO WHERE CBO.live =1 and customStudyId=:customStudyId order by CBO.createdOn DESC");
        query.setString("customStudyId", customStudyId);
        ConsentBo consentBo = (ConsentBo) query.uniqueResult();
        if (consentBo != null) {
          ConsentBo newConsentBo = SerializationUtils.clone(consentBo);
          newConsentBo.setId(null);
          newConsentBo.setStudyId(studyDreaftBo.getId());
          newConsentBo.setVersion(0f);
          if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
            newConsentBo.setLive(5);
          } else {
            newConsentBo.setCustomStudyId(null);
            newConsentBo.setLive(0);
            newConsentBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            newConsentBo.setCreatedBy(sesObj.getUserId());
            newConsentBo.setModifiedBy(null);
            newConsentBo.setModifiedOn(null);
          }
          newConsentBo.setVersion(0f);
          session.save(newConsentBo);

          // Comprehension test Start
          if (StringUtils.isNotEmpty(consentBo.getNeedComprehensionTest())
              && consentBo
                  .getNeedComprehensionTest()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
            List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
            List<String> comprehensionIds = new ArrayList<>();
            List<ComprehensionTestResponseBo> comprehensionTestResponseList = null;
            query =
                session.createQuery(
                    "From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=:studyId and CTQBO.active=1 order by CTQBO.sequenceNo asc");
            query.setString("studyId", consentBo.getStudyId());
            comprehensionTestQuestionList = query.list();
            if ((comprehensionTestQuestionList != null)
                && !comprehensionTestQuestionList.isEmpty()) {
              for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
                  comprehensionTestQuestionList) {
                comprehensionIds.add(comprehensionTestQuestionBo.getId());
              }
              comprehensionTestResponseList =
                  session
                      .createQuery(
                          "From ComprehensionTestResponseBo CTRBO "
                              + "where CTRBO.comprehensionTestQuestionId IN (:comprehensionIds) order by comprehensionTestQuestionId")
                      .setParameterList("comprehensionIds", comprehensionIds)
                      .list();
              for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
                  comprehensionTestQuestionList) {
                ComprehensionTestQuestionBo newComprehensionTestQuestionBo =
                    SerializationUtils.clone(comprehensionTestQuestionBo);
                newComprehensionTestQuestionBo.setStudyId(studyDreaftBo.getId());
                newComprehensionTestQuestionBo.setId(null);
                newComprehensionTestQuestionBo.setCreatedOn(
                    FdahpStudyDesignerUtil.getCurrentDateTime());
                newComprehensionTestQuestionBo.setCreatedBy(sesObj.getUserId());
                newComprehensionTestQuestionBo.setModifiedBy(null);
                newComprehensionTestQuestionBo.setModifiedOn(null);
                session.save(newComprehensionTestQuestionBo);
                if ((comprehensionTestResponseList != null)
                    && !comprehensionTestResponseList.isEmpty()) {
                  for (ComprehensionTestResponseBo comprehensionTestResponseBo :
                      comprehensionTestResponseList) {
                    if (comprehensionTestQuestionBo
                        .getId()
                        .equals(comprehensionTestResponseBo.getComprehensionTestQuestionId())) {
                      ComprehensionTestResponseBo newComprehensionTestResponseBo =
                          SerializationUtils.clone(comprehensionTestResponseBo);
                      newComprehensionTestResponseBo.setId(null);
                      newComprehensionTestResponseBo.setComprehensionTestQuestionId(
                          newComprehensionTestQuestionBo.getId());
                      session.save(newComprehensionTestResponseBo);
                    }
                  }
                }
              }
            }
          }
          // Comprehension test End
        }

        query =
            session.createQuery(
                " From ConsentInfoBo CBO WHERE CBO.live =1 and CBO.active=1 and customStudyId=:customStudyId order by CBO.sequenceNo DESC");
        query.setString("customStudyId", customStudyId);
        List<ConsentInfoBo> consentInfoBoList = query.list();
        if ((consentInfoBoList != null) && !consentInfoBoList.isEmpty()) {
          for (ConsentInfoBo consentInfoBo : consentInfoBoList) {
            ConsentInfoBo newConsentInfoBo = SerializationUtils.clone(consentInfoBo);
            newConsentInfoBo.setId(null);
            newConsentInfoBo.setStudyId(studyDreaftBo.getId());
            newConsentInfoBo.setVersion(0f);
            if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
              newConsentInfoBo.setLive(5);
            } else {
              newConsentInfoBo.setLive(0);
              newConsentInfoBo.setCustomStudyId(null);
              newConsentInfoBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              newConsentInfoBo.setCreatedBy(sesObj.getUserId());
              newConsentInfoBo.setModifiedBy(null);
              newConsentInfoBo.setModifiedOn(null);
              newConsentInfoBo.setStatus(false);
            }
            newConsentInfoBo.setVersion(0f);
            session.save(newConsentInfoBo);
          }
        }
        // Consent updated update End
        // checklist save
        Checklist checklist = new Checklist();
        checklist.setStudyId(studyDreaftBo.getId());
        checklist.setCustomStudyId(
            action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)
                ? customStudyId
                : null);
        session.save(checklist);
        flag = true;
        if (flag && action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
          draftDatas =
              session
                  .getNamedQuery("getStudyDraftVersion")
                  .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId)
                  .list();
          if ((draftDatas != null) && !draftDatas.isEmpty()) {
            studyIdList = new ArrayList<>();
            for (StudyBo study : draftDatas) {
              studyIdList.add(study.getId());
            }
            deleteStudyByIdOrCustomstudyId(
                session, transaction, StringUtils.join(studyIdList, ","), "");
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - resetDraftStudyByCustomStudyId() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("resetDraftStudyByCustomStudyId() - Ends");
    return flag;
  }

  @Override
  public int resourceOrder(String studyId) {
    logger.entry("begin resourceOrder()");
    Session session = null;
    int count = 1;
    ResourceBO resourceBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              "From ResourceBO RBO where RBO.studyId=:studyId and RBO.studyProtocol = false and RBO.status=1 order by RBO.sequenceNo DESC");
      query.setString("studyId", studyId);
      query.setMaxResults(1);
      resourceBo = ((ResourceBO) query.uniqueResult());
      if (resourceBo != null) {
        count = resourceBo.getSequenceNo() + 1;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - resourceOrder() - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("resourceOrder() - Ends");
    return count;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ResourceBO> resourcesSaved(String studyId) {
    logger.entry("begin resourcesSaved()");
    List<ResourceBO> resourceBOList = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      String searchQuery =
          "FROM ResourceBO RBO WHERE RBO.studyId=:studyId AND RBO.action=0 AND RBO.status=1 AND RBO.studyProtocol=0";
      query = session.createQuery(searchQuery);
      query.setString("studyId", studyId);
      resourceBOList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - resourcesSaved() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("resourcesSaved() - Ends");
    return resourceBOList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ResourceBO> resourcesWithAnchorDate(String studyId) {
    logger.entry("begin resourcesWithAnchorDate()");
    List<ResourceBO> resourceList = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      String searchQuery =
          " FROM ResourceBO RBO WHERE RBO.studyId= :studyId AND RBO.resourceType = 1 AND RBO.status = 1 ";
      query = session.createQuery(searchQuery);
      query.setString("studyId", studyId);
      resourceList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - resourcesWithAnchorDate() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("resourcesWithAnchorDate() - Ends");
    return resourceList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ConsentBo saveOrCompleteConsentReviewDetails(
      ConsentBo consentBo, SessionObject sesObj, String customStudyId) {
    logger.entry("INFO: StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: Starts");
    Session session = null;
    StudySequenceBo studySequence = null;
    List<ConsentInfoBo> consentInfoList = null;
    String content = "";
    String activitydetails = "";
    String activity = "";
    StudyVersionBo studyVersionBo = null;
    Map<String, String> values = new HashMap<>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      // check whether the consentinfo is saved for this study or not, if
      // not update
      if (consentBo.getId() != null) {
        consentBo.setModifiedOn(
            FdahpStudyDesignerUtil.getFormattedDate(
                FdahpStudyDesignerUtil.getCurrentDateTime(),
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss"));
        consentBo.setModifiedBy(sesObj.getUserId());
      } else {
        consentBo.setCreatedOn(
            FdahpStudyDesignerUtil.getFormattedDate(
                FdahpStudyDesignerUtil.getCurrentDateTime(),
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss"));
        consentBo.setCreatedBy(sesObj.getUserId());
      }

      // get the review content based on the version, studyId and visual
      // step
      if ((consentBo != null)
          && StringUtils.isNotEmpty(consentBo.getConsentDocType())
          && consentBo.getConsentDocType().equalsIgnoreCase("Auto")) {
        query =
            session.createQuery(
                " from ConsentInfoBo CIBO where CIBO.studyId=:studyId and CIBO.active=1 order by CIBO.sequenceNo asc");
        query.setString("studyId", consentBo.getStudyId());
        consentInfoList = query.list();
        if ((consentInfoList != null) && (consentInfoList.size() > 0)) {
          for (ConsentInfoBo consentInfo : consentInfoList) {

            content +=
                "&lt;span style=&#34;font-size:20px;&#34;&gt;&lt;strong&gt;"
                    + consentInfo.getDisplayTitle()
                    + "&lt;/strong&gt;&lt;/span&gt;&lt;br/&gt;"
                    + "&lt;span style=&#34;display: block; overflow-wrap: break-word; width: 100%;&#34;&gt;"
                    + consentInfo.getElaborated()
                    + "&lt;/span&gt;&lt;br/&gt;";
          }
          consentBo.setConsentDocContent(content);
        }
      }

      if ((consentBo.getType() != null)
          && consentBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
        studySequence =
            (StudySequenceBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, consentBo.getStudyId())
                    .uniqueResult();
        if (studySequence != null) {
          studySequence.seteConsent(false);
        } else {
          studySequence = new StudySequenceBo();
          studySequence.seteConsent(false);
          studySequence.setStudyId(consentBo.getStudyId());
        }
        session.saveOrUpdate(studySequence);
      }
      session.saveOrUpdate(consentBo);
      auditRequest.setStudyId(customStudyId);
      StudyBo studyBo = getStudyById(String.valueOf(consentBo.getStudyId()), sesObj.getUserId());
      auditRequest.setStudyVersion(studyBo.getVersion().toString());
      auditRequest.setAppId(studyBo.getAppId());
      if ((customStudyId != null) && !customStudyId.isEmpty()) {
        if (consentBo.getType() != null) {
          if (consentBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            auditLogEventHelper.logEvent(STUDY_REVIEW_AND_E_CONSENT_SAVED_OR_UPDATED, auditRequest);
            activity = "Study consentReview saved.";
            activitydetails =
                "Content saved for Consent Review Section. (Study ID = " + customStudyId + ")";
          } else {
            query =
                session
                    .getNamedQuery("getStudyByCustomStudyId")
                    .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId);
            query.setMaxResults(1);
            studyVersionBo = (StudyVersionBo) query.uniqueResult();
            if (studyVersionBo != null) {
              values.put(
                  "datasharing_consent_setting",
                  Jsoup.parse(consentBo.getConsentDocContent()).text());
              values.put(
                  "consent_document_version", String.valueOf(studyVersionBo.getConsentVersion()));
              auditLogEventHelper.logEvent(
                  STUDY_REVIEW_AND_E_CONSENT_MARKED_COMPLETE, auditRequest, values);
              activity = "Study consentReview marked as completed.";
              activitydetails =
                  "Consent Review section successfully checked for minimum content completeness and marked 'Completed'.  (Study ID = "
                      + customStudyId
                      + ", Consent Document Version = "
                      + studyVersionBo.getConsentVersion()
                      + ")";
            }
          }
        } else if (consentBo.getComprehensionTest() != null) {
          if (consentBo
              .getComprehensionTest()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            auditLogEventHelper.logEvent(
                STUDY_COMPREHENSION_TEST_SECTION_SAVED_OR_UPDATED, auditRequest);
            activity = "Study comprehension test saved.";
            activitydetails =
                "Content saved for Comprehension test Section. (Study ID = " + customStudyId + ")";
          } else {
            query =
                session
                    .getNamedQuery("getStudyByCustomStudyId")
                    .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId);
            query.setMaxResults(1);
            studyVersionBo = (StudyVersionBo) query.uniqueResult();
            if (studyVersionBo != null) {
              auditLogEventHelper.logEvent(
                  STUDY_COMPREHENSION_TEST_SECTION_MARKED_COMPLETE, auditRequest);
              activity = "Study comprehension test marked as completed.";
              activitydetails =
                  "Comprehension Test section successfully checked for minimum content completeness and marked 'Completed'.  (Study ID = "
                      + customStudyId
                      + ")";
            }
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("INFO: StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: Ends");
    return consentBo;
  }

  @Override
  public String saveOrDoneChecklist(Checklist checklist) {
    logger.entry("begin saveOrDoneChecklist()");
    Session session = null;
    String checklistId = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (checklist.getChecklistId() == null) {
        checklistId = (String) session.save(checklist);
      } else {
        session.update(checklist);
        checklistId = checklist.getChecklistId();
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrDoneChecklist() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrDoneChecklist() - Ends");
    return checklistId;
  }

  @Override
  public ComprehensionTestQuestionBo saveOrUpdateComprehensionTestQuestion(
      ComprehensionTestQuestionBo comprehensionTestQuestionBo) {
    logger.entry("begin saveOrUpdateComprehensionTestQuestion()");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.saveOrUpdate(comprehensionTestQuestionBo);
      if ((StringUtils.isNotEmpty(comprehensionTestQuestionBo.getId()))
          && (comprehensionTestQuestionBo.getResponseList() != null)
          && !comprehensionTestQuestionBo.getResponseList().isEmpty()) {
        String deleteQuery =
            "delete from comprehension_test_response where comprehension_test_question_id=:id";
        session
            .createSQLQuery(deleteQuery)
            .setString("id", comprehensionTestQuestionBo.getId())
            .executeUpdate();
        for (ComprehensionTestResponseBo comprehensionTestResponseBo :
            comprehensionTestQuestionBo.getResponseList()) {
          if ((comprehensionTestResponseBo.getResponseOption() != null)
              && !comprehensionTestResponseBo.getResponseOption().isEmpty()) {
            if (StringUtils.isEmpty(comprehensionTestResponseBo.getComprehensionTestQuestionId())) {
              comprehensionTestResponseBo.setComprehensionTestQuestionId(
                  comprehensionTestQuestionBo.getId());
            }
            session.save(comprehensionTestResponseBo);
          }
        }
      }

      StudySequenceBo studySequence =
          (StudySequenceBo)
              session
                  .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                  .setString(
                      FdahpStudyDesignerConstants.STUDY_ID,
                      comprehensionTestQuestionBo.getStudyId())
                  .uniqueResult();

      if (studySequence != null) {
        studySequence.setComprehensionTest(false);
        session.save(studySequence);
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrUpdateComprehensionTestQuestion() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateComprehensionTestQuestion() - Ends");
    return comprehensionTestQuestionBo;
  }

  @Override
  public ConsentInfoBo saveOrUpdateConsentInfo(
      ConsentInfoBo consentInfoBo, SessionObject sesObj, String customStudyId) {
    logger.entry("begin saveOrUpdateConsentInfo()");
    Session session = null;
    StudySequenceBo studySequence = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (consentInfoBo.getType() != null) {
        studySequence =
            (StudySequenceBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, consentInfoBo.getStudyId())
                    .uniqueResult();
        if (consentInfoBo
            .getType()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
          consentInfoBo.setStatus(false);
          if (studySequence != null) {
            studySequence.setConsentEduInfo(false);
            if (studySequence.iseConsent()) {
              studySequence.seteConsent(false);
            }
            if (studySequence.isComprehensionTest()) {
              studySequence.setComprehensionTest(false);
            }
          } else {
            studySequence = new StudySequenceBo();
            studySequence.setConsentEduInfo(false);
            studySequence.setStudyId(consentInfoBo.getStudyId());
          }
        } else if (consentInfoBo
            .getType()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
          consentInfoBo.setStatus(true);
          studySequence.setConsentEduInfo(false);
          if (studySequence.iseConsent()) {
            studySequence.seteConsent(false);
          }
          if (studySequence.isComprehensionTest()) {
            studySequence.setComprehensionTest(false);
          }
        }
        session.saveOrUpdate(studySequence);
      }
      session.saveOrUpdate(consentInfoBo);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrUpdateConsentInfo() - Error", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateConsentInfo() - Ends");
    return consentInfoBo;
  }

  @Override
  public String saveOrUpdateEligibilityTestQusAns(
      EligibilityTestBo eligibilityTestBo,
      String studyId,
      SessionObject sesObj,
      String customStudyId) {
    logger.entry("begin saveOrUpdateEligibilityTestQusAns");
    Session session = null;
    String eligibilityTestId = null;
    Transaction trans = null;
    EligibilityTestBo saveEligibilityTestBo;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      trans = session.beginTransaction();
      StudySequenceBo studySequence =
          (StudySequenceBo)
              session
                  .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                  .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId)
                  .uniqueResult();
      if ((studySequence != null) && studySequence.isEligibility()) {
        studySequence.setEligibility(false);
        session.update(studySequence);
      }
      if (eligibilityTestBo
          .getType()
          .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {

        eligibilityTestBo.setStatus(true);
      }
      if (StringUtils.isNotEmpty(eligibilityTestBo.getId())) {
        saveEligibilityTestBo =
            (EligibilityTestBo) session.get(EligibilityTestBo.class, eligibilityTestBo.getId());
        session.evict(saveEligibilityTestBo);
        eligibilityTestBo.setUsed(saveEligibilityTestBo.isUsed());
        session.update(eligibilityTestBo);
      } else {
        session.save(eligibilityTestBo);
      }
      eligibilityTestId = eligibilityTestBo.getId();
      trans.commit();
    } catch (Exception e) {
      if (null != trans) {
        trans.rollback();
      }
      logger.error("StudyDAOImpl - saveOrUpdateEligibilityTestQusAns - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateEligibilityTestQusAns - Ends");
    return eligibilityTestId;
  }

  @Override
  public String saveOrUpdateOverviewStudyPages(StudyPageBean studyPageBean, SessionObject sesObj) {
    logger.entry("begin saveOrUpdateOverviewStudyPages()");
    Session session = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    int titleLength = 0;
    StudySequenceBo studySequence = null;
    StudyBo studyBo = null;
    List<String> pageIdList = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(studyPageBean.getStudyId())) {
        studyBo =
            (StudyBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                    .setString("id", studyPageBean.getStudyId())
                    .uniqueResult();
        if (studyBo != null) {
          studyBo.setMediaLink(studyPageBean.getMediaLink());
          session.update(studyBo);
        }
        // fileArray based on pageId will save/update into particular
        // location
        titleLength = studyPageBean.getTitle().length;
        if (titleLength > 0) {
          // delete the pages which are deleted from front end
          String pageIdArr = null;
          for (int j = 0; j < studyPageBean.getPageId().length; j++) {
            if (FdahpStudyDesignerUtil.isNotEmpty(studyPageBean.getPageId()[j])) {
              pageIdList.add(studyPageBean.getPageId()[j]);
            }
          }
          if (!pageIdList.isEmpty()) {
            session
                .createQuery(
                    "delete from StudyPageBo where studyId=:studyId and pageId not in (:pageIdList)")
                .setString("studyId", studyPageBean.getStudyId())
                .setParameterList("pageIdList", pageIdList)
                .executeUpdate();
          } else {
            session
                .createQuery("delete from StudyPageBo where studyId= :studyId")
                .setString("studyId", studyPageBean.getStudyId())
                .executeUpdate();
          }
          for (int i = 0; i < titleLength; i++) {
            StudyPageBo studyPageBo = null;
            if (FdahpStudyDesignerUtil.isNotEmpty(studyPageBean.getPageId()[i])) {
              studyPageBo =
                  (StudyPageBo)
                      session
                          .createQuery("from StudyPageBo SPB where SPB.pageId=:pageId")
                          .setString("pageId", studyPageBean.getPageId()[i])
                          .uniqueResult();
            }

            if (studyPageBo == null) {
              studyPageBo = new StudyPageBo();
            }

            if (FdahpStudyDesignerUtil.isNotEmpty(studyPageBean.getPageId()[i])) {
              studyPageBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              studyPageBo.setModifiedBy(studyPageBean.getUserId());
            } else {
              studyPageBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              studyPageBo.setCreatedBy(studyPageBean.getUserId());
            }
            studyPageBo.setStudyId(
                FdahpStudyDesignerUtil.isEmpty(studyPageBean.getStudyId())
                    ? null
                    : studyPageBean.getStudyId());
            studyPageBo.setTitle(
                FdahpStudyDesignerUtil.isEmpty(studyPageBean.getTitle()[i])
                    ? null
                    : studyPageBean.getTitle()[i]);
            studyPageBo.setDescription(
                FdahpStudyDesignerUtil.isEmpty(studyPageBean.getDescription()[i])
                    ? null
                    : studyPageBean.getDescription()[i]);
            studyPageBo.setImagePath(
                FdahpStudyDesignerUtil.isEmpty(studyPageBean.getImagePath()[i])
                    ? null
                    : studyPageBean.getImagePath()[i]);
            session.saveOrUpdate(studyPageBo);
          }
          studySequence =
              (StudySequenceBo)
                  session
                      .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                      .setString(FdahpStudyDesignerConstants.STUDY_ID, studyPageBean.getStudyId())
                      .uniqueResult();
          if (studySequence != null) {
            if ((studyPageBean.getActionType() != null)
                && studyPageBean
                    .getActionType()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)
                && !studySequence.isOverView()) {
              studySequence.setOverView(true);
            } else if ((studyPageBean.getActionType() != null)
                && studyPageBean
                    .getActionType()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.SAVE_BUTTON)) {
              studySequence.setOverView(false);
            }
            session.update(studySequence);
          }
          message =
              auditLogDAO.updateDraftToEditedStatus(
                  session,
                  transaction,
                  studyPageBean.getUserId(),
                  FdahpStudyDesignerConstants.DRAFT_STUDY,
                  studyPageBean.getStudyId());
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrUpdateOverviewStudyPages() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateOverviewStudyPages() - Ends");
    return message;
  }

  @Override
  public String saveOrUpdateResource(ResourceBO resourceBO) {
    logger.entry("begin saveOrUpdateResource()");
    Session session = null;
    String resourceId = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (null == resourceBO.getId()) {
        resourceId = (String) session.save(resourceBO);
      } else {
        session.update(resourceBO);
        resourceId = resourceBO.getId();
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrUpdateResource() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateResource() - Ends");
    return resourceId;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String saveOrUpdateStudy(StudyBo studyBo, SessionObject sessionObject) {
    logger.entry("begin saveOrUpdateStudy()");
    Session session = null;
    String message = FdahpStudyDesignerConstants.SUCCESS;
    StudyPermissionBO studyPermissionBO = null;
    String studyId = null;
    String userId = null;
    StudySequenceBo studySequenceBo = null;
    StudyBo dbStudyBo = null;
    List<String> userSuperAdminList = null;
    StudyBuilderAuditEvent auditLogEvent = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      userId = studyBo.getUserId();
      String appId = "";
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      String fileName = "";
      if ((studyBo.getFile() != null) && !studyBo.getFile().isEmpty()) {
        if (FdahpStudyDesignerUtil.isNotEmpty(studyBo.getThumbnailImage())) {
          fileName =
              studyBo
                  .getThumbnailImage()
                  .replace(
                      "."
                          + studyBo.getThumbnailImage()
                              .split("\\.")[studyBo.getThumbnailImage().split("\\.").length - 1],
                      "");
        } else {
          fileName =
              FdahpStudyDesignerUtil.getStandardFileName(
                  "STUDY", studyBo.getName(), studyBo.getCustomStudyId());
        }
        studyBo.setThumbnailImage(
            fileName + "." + FilenameUtils.getExtension(studyBo.getFile().getOriginalFilename()));
      }

      if (StringUtils.isEmpty(studyBo.getId())) {
        studyBo.setCreatedBy(studyBo.getUserId());
        //        studyBo.setAppId(studyBo.getAppId());
        studyBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        studyId = (String) session.save(studyBo);

        studyPermissionBO = new StudyPermissionBO();
        studyPermissionBO.setUserId(userId);
        studyPermissionBO.setStudyId(studyId);
        studyPermissionBO.setViewPermission(true);
        session.save(studyPermissionBO);

        // give permission to all super admin Start
        query =
            session
                .createSQLQuery(
                    "Select upm.user_id from user_permission_mapping upm where upm.permission_id =:superAdminId")
                .setInteger("superAdminId", FdahpStudyDesignerConstants.ROLE_SUPERADMIN);
        userSuperAdminList = query.list();
        if ((userSuperAdminList != null) && !userSuperAdminList.isEmpty()) {
          for (String superAdminId : userSuperAdminList) {
            if ((null != userId) && !userId.equals(superAdminId)) {
              studyPermissionBO = new StudyPermissionBO();
              studyPermissionBO.setUserId(superAdminId);
              studyPermissionBO.setStudyId(studyId);
              studyPermissionBO.setViewPermission(true);
              session.save(studyPermissionBO);
            }
          }
        }
        // give permission to all super admin End

        // creating table to keep track of each section of study
        // completed or not
        studySequenceBo = new StudySequenceBo();
        studySequenceBo.setStudyId(studyId);
        session.save(studySequenceBo);

        // Phase2a code Start
        // create one record in anchordate_type table to give user to use enrollmentdate
        AnchorDateTypeBo anchorDateTypeBo = new AnchorDateTypeBo();
        anchorDateTypeBo.setCustomStudyId(studyBo.getCustomStudyId());
        anchorDateTypeBo.setStudyId(studyId);
        anchorDateTypeBo.setName(FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE);
        anchorDateTypeBo.setHasAnchortypeDraft(1);
        session.save(anchorDateTypeBo);
        // Phase2a code End
      } else {
        dbStudyBo =
            (StudyBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                    .setString("id", studyBo.getId())
                    .uniqueResult();
        if (dbStudyBo != null) {

          if (StringUtils.isNotEmpty(dbStudyBo.getDestinationCustomStudyId())
              && StringUtils.isEmpty(dbStudyBo.getCustomStudyId())) {

            if (dbStudyBo.getDestinationCustomStudyId().contains("@")) {
              String[] copyCustomIdArray = dbStudyBo.getDestinationCustomStudyId().split("@");
              String customId = "";
              if (copyCustomIdArray[1].contains("COPY")) {
                customId = copyCustomIdArray[0];
                int isLive =
                    copyCustomIdArray[1].contains(FdahpStudyDesignerConstants.PUBLISHED_VERSION)
                        ? 1
                        : 0;
                StudyBo study =
                    (StudyBo)
                        session
                            .createQuery(
                                "From StudyBo SBO WHERE SBO.live=:isLive AND customStudyId=:customStudyId")
                            .setString("customStudyId", customId)
                            .setInteger("isLive", isLive)
                            .uniqueResult();
                if (study != null) {
                  moveOrCopyCloudStorage(session, study, false, false, studyBo.getCustomStudyId());
                }
              } else if (copyCustomIdArray[1].equalsIgnoreCase("EXPORT")) {
                moveOrCopyCloudStorageForExportStudy(
                    session,
                    dbStudyBo,
                    false,
                    false,
                    studyBo.getCustomStudyId(),
                    dbStudyBo.getDestinationCustomStudyId());
              }
            } else {
              StudyBo study =
                  (StudyBo)
                      session
                          .createQuery(
                              "From StudyBo SBO WHERE SBO.live=:isLive AND customStudyId=:customStudyId")
                          .setString("customStudyId", dbStudyBo.getDestinationCustomStudyId())
                          .setInteger("isLive", 0)
                          .uniqueResult();
              if (study != null) {
                moveOrCopyCloudStorage(session, study, false, false, studyBo.getCustomStudyId());
              }
            }

          } else if (!dbStudyBo.getCustomStudyId().equals(studyBo.getCustomStudyId())) {
            moveOrCopyCloudStorage(session, dbStudyBo, true, false, studyBo.getCustomStudyId());
          }

          dbStudyBo.setCustomStudyId(studyBo.getCustomStudyId());
          dbStudyBo.setName(studyBo.getName());
          dbStudyBo.setFullName(studyBo.getFullName());
          dbStudyBo.setCategory(studyBo.getCategory());
          dbStudyBo.setResearchSponsor(studyBo.getResearchSponsor());
          dbStudyBo.setTentativeDuration(studyBo.getTentativeDuration());
          dbStudyBo.setTentativeDurationWeekmonth(studyBo.getTentativeDurationWeekmonth());
          dbStudyBo.setDescription(studyBo.getDescription());
          dbStudyBo.setStudyTagLine(studyBo.getStudyTagLine());
          dbStudyBo.setStudyWebsite(studyBo.getStudyWebsite());
          dbStudyBo.setInboxEmailAddress(studyBo.getInboxEmailAddress());
          dbStudyBo.setType(studyBo.getType());
          dbStudyBo.setThumbnailImage(studyBo.getThumbnailImage());
          dbStudyBo.setModifiedBy(studyBo.getUserId());
          dbStudyBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          if (studyBo.getAppId() != null
              && dbStudyBo.getAppId() != null
              && !dbStudyBo.getAppId().equals(studyBo.getAppId())) {
            studySequenceBo =
                (StudySequenceBo)
                    session
                        .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                        .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId())
                        .uniqueResult();
            studySequenceBo.setSettingAdmins(false);
            session.update(studySequenceBo);
          }
          dbStudyBo.setAppId(studyBo.getAppId());
          session.update(dbStudyBo);

          String searchQuery = "From AnchorDateTypeBo where studyId=:id";
          List<AnchorDateTypeBo> anchorDateTypeBoList =
              session.createQuery(searchQuery).setString("id", studyBo.getId()).list();

          if (CollectionUtils.isNotEmpty(anchorDateTypeBoList)) {
            for (AnchorDateTypeBo anchorDateTypeBo : anchorDateTypeBoList) {
              anchorDateTypeBo.setCustomStudyId(studyBo.getCustomStudyId());
              session.update(anchorDateTypeBo);
            }
          }
        }
      }
      if ((studyBo.getFile() != null) && !studyBo.getFile().isEmpty()) {
        BufferedImage newBi = ImageIO.read(new ByteArrayInputStream(studyBo.getFile().getBytes()));
        BufferedImage resizedImage = ImageUtility.resizeImage(newBi, 225, 225);
        String extension = FilenameUtils.getExtension(studyBo.getFile().getOriginalFilename());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, extension, baos);
        baos.flush();

        studyBo.setFile(
            new CustomMultipartFile(
                baos.toByteArray(), studyBo.getFile().getOriginalFilename(), extension));
        FdahpStudyDesignerUtil.saveImage(
            studyBo.getFile(),
            fileName,
            FdahpStudyDesignerConstants.STUDTYLOGO,
            studyBo.getCustomStudyId());
      }
      studySequenceBo =
          (StudySequenceBo)
              session
                  .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                  .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId())
                  .uniqueResult();
      auditRequest.setStudyId(studyBo.getCustomStudyId());
      auditRequest.setStudyVersion(studyBo.getVersion().toString());
      auditRequest.setAppId(studyBo.getAppId());
      if (studySequenceBo != null) {
        if (StringUtils.isNotEmpty(studyBo.getButtonText())
            && studyBo
                .getButtonText()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
          studySequenceBo.setBasicInfo(true);
          auditLogEvent = STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(studyBo.getButtonText())
            && studyBo.getButtonText().equalsIgnoreCase(FdahpStudyDesignerConstants.SAVE_BUTTON)) {
          auditLogEvent = STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED;
          studySequenceBo.setBasicInfo(false);
        }
        session.update(studySequenceBo);
      }
      message =
          auditLogDAO.updateDraftToEditedStatus(
              session,
              transaction,
              studyBo.getUserId(),
              FdahpStudyDesignerConstants.DRAFT_STUDY,
              studyBo.getId());

      auditLogEventHelper.logEvent(auditLogEvent, auditRequest);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrUpdateSubAdmin() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateSubAdmin() - Ends");
    return message;
  }

  @Override
  public String saveOrUpdateStudyEligibilty(
      EligibilityBo eligibilityBo, SessionObject sesObj, String customStudyId) {
    logger.entry("begin saveOrUpdateStudyEligibilty()");
    String result = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    StudySequenceBo studySequence = null;
    EligibilityBo eligibilityBoUpdate = null;
    Boolean updateFlag = false;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (null != eligibilityBo) {
        if (StringUtils.isNotEmpty(eligibilityBo.getId())) {
          eligibilityBoUpdate =
              (EligibilityBo)
                  session
                      .getNamedQuery("getEligibiltyById")
                      .setString("id", eligibilityBo.getId())
                      .uniqueResult();
          eligibilityBoUpdate.setEligibilityMechanism(eligibilityBo.getEligibilityMechanism());
          eligibilityBoUpdate.setInstructionalText(eligibilityBo.getInstructionalText());
          eligibilityBoUpdate.setModifiedOn(eligibilityBo.getModifiedOn());
          eligibilityBoUpdate.setModifiedBy(eligibilityBo.getModifiedBy());
          updateFlag = true;
        } else {
          eligibilityBoUpdate = eligibilityBo;
        }

        studySequence =
            (StudySequenceBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, eligibilityBo.getStudyId())
                    .uniqueResult();
        if (studySequence != null) {
          if ((eligibilityBo.getActionType() != null)
              && ("mark").equals(eligibilityBo.getActionType())
              && !studySequence.isEligibility()) {
            studySequence.setEligibility(true);
          } else if ((eligibilityBo.getActionType() != null)
              && !("mark").equals(eligibilityBo.getActionType())) {
            studySequence.setEligibility(false);
          }
          if (StringUtils.isEmpty(eligibilityBo.getId())) {
            session.save(eligibilityBoUpdate);
          } else {
            session.update(eligibilityBoUpdate);
          }
        }

        result =
            auditLogDAO.updateDraftToEditedStatus(
                session,
                transaction,
                (updateFlag ? eligibilityBo.getModifiedBy() : eligibilityBo.getCreatedBy()),
                FdahpStudyDesignerConstants.DRAFT_STUDY,
                eligibilityBo.getStudyId());
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrUpdateStudyEligibilty() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateStudyEligibilty() - Ends");
    return result;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public String saveOrUpdateStudySettings(StudyBo studyBo, SessionObject sesObj) {
    logger.entry("begin saveOrUpdateStudySettings()");
    String result = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    StudySequenceBo studySequence = null;
    StudyBo study = null;

    Map<String, String> values = new HashMap<>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (null != studyBo) {
        if (studyBo.getId() != null) {
          study =
              (StudyBo)
                  session
                      .createQuery("from StudyBo where id=:id")
                      .setString("id", studyBo.getId())
                      .uniqueResult();
          studySequence =
              (StudySequenceBo)
                  session
                      .createQuery("from StudySequenceBo where studyId=:id")
                      .setString("id", studyBo.getId())
                      .uniqueResult();
          if ((study != null) && (studySequence != null)) {
            // validation of anchor date
            updateAnchordateForEnrollmentDate(study, studyBo, session, transaction);
            // validation of anchor date
            study.setPlatform(studyBo.getPlatform());
            study.setEnrollingParticipants(studyBo.getEnrollingParticipants());
            study.setModifiedBy(studyBo.getUserId());
            study.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            // Phase2a code Start(adding enrollment date as anchor date(yes/no))
            study.setEnrollmentdateAsAnchordate(studyBo.isEnrollmentdateAsAnchordate());
            // Phase2a code end
            session.saveOrUpdate(study);

            values.put("enrollment_setting", String.valueOf(study.getEnrollingParticipants()));

            // setting true to setting admins
            // setting and admins section of Study completed or not
            // updated in study sequence table(to keep track of each
            // section of study completed or not)
            if (StringUtils.isNotEmpty(studyBo.getButtonText())
                && studyBo
                    .getButtonText()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)
                && !studySequence.isSettingAdmins()) {
              studySequence.setSettingAdmins(true);
            } else if (StringUtils.isNotEmpty(studyBo.getButtonText())
                && studyBo
                    .getButtonText()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.SAVE_BUTTON)) {
              studySequence.setSettingAdmins(false);
            }
            session.update(studySequence);

            // Phase2a code Start(adding enrollment date as anchor date(yes/no))
            if (studyBo.isEnrollmentdateAsAnchordate()) {
              session
                  .createSQLQuery(
                      "UPDATE anchordate_type set has_anchortype_draft=1 where study_id=:id and has_anchortype_draft=0 and name=:enrollment")
                  .setString("id", study.getId())
                  .setString("enrollment", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                  .executeUpdate();
            } else {
              session
                  .createSQLQuery(
                      "UPDATE anchordate_type set has_anchortype_draft=0 where study_id=:id and has_anchortype_draft=1 and name=:enrollment")
                  .setString("id", study.getId())
                  .setString("enrollment", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                  .executeUpdate();
            }
          }
        }

        result =
            auditLogDAO.updateDraftToEditedStatus(
                session,
                transaction,
                studyBo.getUserId(),
                FdahpStudyDesignerConstants.DRAFT_STUDY,
                studyBo.getId());

        if (study != null) {
          auditRequest.setStudyId(study.getCustomStudyId());
          auditRequest.setStudyVersion(study.getVersion().toString());
          auditRequest.setAppId(study.getAppId());
          if (studyBo
              .getButtonText()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            auditLogEventHelper.logEvent(STUDY_SETTINGS_MARKED_COMPLETE, auditRequest, values);

          } else {
            auditLogEventHelper.logEvent(STUDY_SETTINGS_SAVED_OR_UPDATED, auditRequest);
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveOrUpdateStudySettings() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateStudySettings() - Ends");
    return result;
  }

  public Integer saveOverviewStudyPageById(String studyId) {
    Integer pageId = 0;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(studyId)) {
        StudyPageBo studyPageBo = new StudyPageBo();
        studyPageBo.setStudyId(studyId);
        pageId = (Integer) session.save(studyPageBo);
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - deleteOverviewStudyPageById() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("deleteOverviewStudyPageById() - Ends");

    return pageId;
  }

  @Override
  public String saveResourceNotification(NotificationBO notificationBO, boolean notiFlag) {
    logger.entry("begin saveResourceNotification()");
    Session session = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (!notiFlag) {
        session.save(notificationBO);
      } else {
        session.update(notificationBO);
      }
      transaction.commit();
      message = FdahpStudyDesignerConstants.SUCCESS;
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveResourceNotification() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveResourceNotification() - Ends");
    return message;
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public String studyDraftCreation(
      StudyBo studyBo, Session session, AuditLogEventRequest auditRequest, String userId) {
    logger.entry("begin studyDraftCreation()");
    List<StudyPageBo> studyPageBo = null;
    List<StudyPermissionBO> studyPermissionList = null;
    EligibilityBo eligibilityBo = null;
    List<ResourceBO> resourceBOList = null;
    StudyVersionBo studyVersionBo = null;
    StudyVersionBo newstudyVersionBo = null;
    boolean flag = true;
    String message = FdahpStudyDesignerConstants.FAILURE;
    List<QuestionnaireBo> questionnaires = null;
    List<ActiveTaskBo> activeTasks = null;
    String searchQuery = "";
    QuestionReponseTypeBo questionReponseTypeBo = null;
    List<String> objectList = null;
    List<String> questionnarieShorttitleList = null;
    List<AnchorDateTypeBo> anchorDateTypeList = null;
    Map<Integer, Integer> anchordateoldnewMapList = new HashMap<>();
    try {
      if (studyBo != null) {
        logger.info("StudyDAOImpl - studyDraftCreation() getStudyByCustomStudyId- Starts");
        // if already launch and study hasStudyDraft()==1 , then update
        // and create draft version , otherwise not
        query =
            session
                .getNamedQuery("getStudyByCustomStudyId")
                .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
        query.setMaxResults(1);
        studyVersionBo = (StudyVersionBo) query.uniqueResult();
        logger.info("StudyDAOImpl - studyDraftCreation() getStudyByCustomStudyId- Ends");
        if ((studyVersionBo != null) && (studyBo.getHasStudyDraft().equals(0))) {
          flag = false;
        }
        if (flag) {
          // version update in study_version table
          if (studyVersionBo != null) {
            logger.info("StudyDAOImpl - studyDraftCreation() updateStudyVersion- Starts");
            // update all studies to archive (live as 2)
            // pass customstudyId and making all study status belongs to same customstudyId
            // as 2(archive)
            query =
                session
                    .getNamedQuery("updateStudyVersion")
                    .setString(
                        FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
            query.executeUpdate();
            logger.info("StudyDAOImpl - studyDraftCreation() updateStudyVersion- Ends");

            newstudyVersionBo = SerializationUtils.clone(studyVersionBo);
            newstudyVersionBo.setStudyVersion(studyVersionBo.getStudyVersion() + 0.1f);
            if (studyBo.getHasConsentDraft().equals(1)) {
              newstudyVersionBo.setConsentVersion(
                  Float.valueOf(String.format("%.02f", studyVersionBo.getConsentVersion() + 0.1f)));
            }
            newstudyVersionBo.setVersionId(null);
            session.save(newstudyVersionBo);
          } else {
            newstudyVersionBo = new StudyVersionBo();
            newstudyVersionBo.setCustomStudyId(studyBo.getCustomStudyId());
            newstudyVersionBo.setActivityVersion(1.0f);
            newstudyVersionBo.setConsentVersion(1.0f);
            newstudyVersionBo.setStudyVersion(1.0f);
            session.save(newstudyVersionBo);
          }
          auditRequest.setStudyVersion(String.valueOf(newstudyVersionBo.getStudyVersion()));

          // create new Study and made it draft study
          StudyBo studyDreaftBo = SerializationUtils.clone(studyBo);
          if (newstudyVersionBo != null) {
            studyDreaftBo.setVersion(newstudyVersionBo.getStudyVersion());
            studyDreaftBo.setLive(1);
          }
          studyDreaftBo.setId(null);
          session.save(studyDreaftBo);

          // clone of Study Permission

          studyPermissionList =
              session
                  .createQuery("from StudyPermissionBO where studyId=:id")
                  .setString("id", studyBo.getId())
                  .list();
          if (studyPermissionList != null) {
            logger.info("StudyDAOImpl - studyDraftCreation() StudyPermissionBO- Starts");
            for (StudyPermissionBO permissionBO : studyPermissionList) {
              StudyPermissionBO studyPermissionBO = SerializationUtils.clone(permissionBO);
              studyPermissionBO.setStudyId(studyDreaftBo.getId());
              studyPermissionBO.setStudyPermissionId(null);
              session.save(studyPermissionBO);
            }
            logger.info("StudyDAOImpl - studyDraftCreation() StudyPermissionBO- Ends");
          }

          // clone of Study Sequence
          StudySequenceBo studySequence =
              (StudySequenceBo)
                  session
                      .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                      .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId())
                      .uniqueResult();
          StudySequenceBo newStudySequenceBo = SerializationUtils.clone(studySequence);
          newStudySequenceBo.setStudyId(studyDreaftBo.getId());
          newStudySequenceBo.setStudySequenceId(null);
          session.save(newStudySequenceBo);

          // clone of Over View section
          query = session.createQuery("from StudyPageBo where studyId=:id");
          query.setString("id", studyBo.getId());
          studyPageBo = query.list();
          if ((studyPageBo != null) && !studyPageBo.isEmpty()) {
            for (StudyPageBo pageBo : studyPageBo) {
              StudyPageBo subPageBo = SerializationUtils.clone(pageBo);
              subPageBo.setStudyId(studyDreaftBo.getId());
              subPageBo.setPageId(null);
              session.save(subPageBo);
            }
          }

          // clone of Eligibility
          query =
              session
                  .getNamedQuery("getEligibiltyByStudyId")
                  .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
          eligibilityBo = (EligibilityBo) query.uniqueResult();
          if (eligibilityBo != null) {
            EligibilityBo bo = SerializationUtils.clone(eligibilityBo);
            bo.setStudyId(studyDreaftBo.getId());
            bo.setId(null);
            session.save(bo);
            List<EligibilityTestBo> eligibilityTestList = null;
            eligibilityTestList =
                session
                    .getNamedQuery("EligibilityTestBo.findByEligibilityId")
                    .setString(FdahpStudyDesignerConstants.ELIGIBILITY_ID, eligibilityBo.getId())
                    .list();
            if ((eligibilityTestList != null) && !eligibilityTestList.isEmpty()) {
              List<String> eligibilityTestIds = new ArrayList<>();
              for (EligibilityTestBo eligibilityTestBo : eligibilityTestList) {
                eligibilityTestIds.add(eligibilityTestBo.getId());
                EligibilityTestBo newEligibilityTestBo =
                    SerializationUtils.clone(eligibilityTestBo);
                newEligibilityTestBo.setId(null);
                newEligibilityTestBo.setEligibilityId(bo.getId());
                newEligibilityTestBo.setUsed(true);
                session.save(newEligibilityTestBo);
              }
              if (!eligibilityTestIds.isEmpty()) {
                session
                    .createSQLQuery(
                        "UPDATE eligibility_test set is_used='Y' where id in(:eligibilityTestIds)")
                    .setParameterList("eligibilityTestIds", eligibilityTestIds)
                    .executeUpdate();
              }
            }
          }

          // clone of resources
          searchQuery =
              " FROM ResourceBO RBO WHERE RBO.studyId=:id AND RBO.status = 1 ORDER BY RBO.createdOn DESC ";
          query = session.createQuery(searchQuery);
          query.setString("id", studyBo.getId());
          resourceBOList = query.list();
          if ((resourceBOList != null) && !resourceBOList.isEmpty()) {
            logger.info("StudyDAOImpl - studyDraftCreation() ResourceBO- Starts");
            for (ResourceBO bo : resourceBOList) {
              ResourceBO resourceBO = SerializationUtils.clone(bo);
              resourceBO.setStudyId(studyDreaftBo.getId());
              resourceBO.setId(null);
              session.save(resourceBO);
            }
            logger.info("StudyDAOImpl - studyDraftCreation() ResourceBO- Ends");
          }

          // If Questionnaire updated flag -1 then update(clone)
          if ((studyVersionBo == null)
              || ((studyBo.getHasQuestionnaireDraft() != null)
                  && studyBo.getHasQuestionnaireDraft().equals(1))) {
            // Questionarries
            query =
                session
                    .getNamedQuery("getQuestionariesByStudyId")
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
            questionnaires = query.list();
            if ((questionnaires != null) && !questionnaires.isEmpty()) {
              // short title taking updating to archived which
              // have change start
              questionnarieShorttitleList = new ArrayList<>();
              for (QuestionnaireBo questionnaireBo : questionnaires) {
                if ((questionnaireBo.getIsChange() != null)
                    && questionnaireBo.getIsChange().equals(1)) {
                  questionnarieShorttitleList.add(questionnaireBo.getShortTitle());
                }
              }
              if ((questionnarieShorttitleList != null) && !questionnarieShorttitleList.isEmpty()) {
                logger.info(
                    "StudyDAOImpl - studyDraftCreation() Questionnarie update is_live=2- Starts");
                queryString =
                    "update questionnaires SET is_live=2 where short_title IN (:questionnarieShorttitleList) and is_live=1 and custom_study_id= :customStudyId";
                query = session.createSQLQuery(queryString);
                query.setParameterList("questionnarieShorttitleList", questionnarieShorttitleList);
                query.setString("customStudyId", studyBo.getCustomStudyId());
                query.executeUpdate();
                logger.info(
                    "StudyDAOImpl - studyDraftCreation() Questionnarie update is_live=2- Ends");
              }
              // short title taking updating to archived which
              // have change end
              for (QuestionnaireBo questionnaireBo : questionnaires) {
                logger.info("StudyDAOImpl - studyDraftCreation() Questionnarie creation- Starts");
                // creating in study Activity version
                StudyActivityVersionBo studyActivityVersionBo = new StudyActivityVersionBo();
                studyActivityVersionBo.setCustomStudyId(studyBo.getCustomStudyId());
                studyActivityVersionBo.setStudyVersion(newstudyVersionBo.getStudyVersion());
                studyActivityVersionBo.setActivityType("Q");
                studyActivityVersionBo.setShortTitle(questionnaireBo.getShortTitle());
                // is there any change in questionnarie
                if ((questionnaireBo.getIsChange() != null)
                    && questionnaireBo.getIsChange().equals(1)) {
                  Float questionnarieversion = questionnaireBo.getVersion();
                  QuestionnaireBo newQuestionnaireBo = SerializationUtils.clone(questionnaireBo);
                  newQuestionnaireBo.setId(null);
                  newQuestionnaireBo.setStudyId(studyDreaftBo.getId());
                  newQuestionnaireBo.setCreatedBy(null);
                  newQuestionnaireBo.setModifiedBy(null);
                  newQuestionnaireBo.setModifiedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
                  if (studyVersionBo == null) {
                    newQuestionnaireBo.setVersion(1.0f);
                    questionnaireBo.setVersion(1.0f);
                  } else {
                    if (questionnarieversion.equals(0f)) {
                      questionnaireBo.setVersion(1.0f);
                      newQuestionnaireBo.setVersion(1.0f);
                    } else {
                      newQuestionnaireBo.setVersion(questionnaireBo.getVersion() + 0.1f);
                      questionnaireBo.setVersion(questionnaireBo.getVersion() + 0.1f);
                    }
                  }
                  newQuestionnaireBo.setLive(1);
                  newQuestionnaireBo.setCustomStudyId(studyBo.getCustomStudyId());
                  session.save(newQuestionnaireBo);
                  questionnaireBo.setIsChange(0);
                  questionnaireBo.setLive(0);
                  session.update(questionnaireBo);
                  /** Schedule Purpose creating draft Start * */
                  if (StringUtils.isNotEmpty(questionnaireBo.getFrequency())) {
                    if (questionnaireBo
                        .getFrequency()
                        .equalsIgnoreCase(
                            FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                      searchQuery =
                          "From QuestionnaireCustomScheduleBo QCSBO where QCSBO.questionnairesId=:id";
                      List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleList =
                          session
                              .createQuery(searchQuery)
                              .setString("id", questionnaireBo.getId())
                              .list();
                      if ((questionnaireCustomScheduleList != null)
                          && !questionnaireCustomScheduleList.isEmpty()) {
                        logger.info(
                            "StudyDAOImpl - studyDraftCreation() Questionnarie manual schedule update - Starts");
                        for (QuestionnaireCustomScheduleBo customScheduleBo :
                            questionnaireCustomScheduleList) {
                          QuestionnaireCustomScheduleBo newCustomScheduleBo =
                              SerializationUtils.clone(customScheduleBo);
                          newCustomScheduleBo.setQuestionnairesId(newQuestionnaireBo.getId());
                          newCustomScheduleBo.setId(null);
                          session.save(newCustomScheduleBo);
                        }
                        // updating draft version of
                        // schecule to Yes
                        session
                            .createQuery(
                                "UPDATE QuestionnaireCustomScheduleBo set used=true where questionnairesId=:id")
                            .setString("id", questionnaireBo.getId())
                            .executeUpdate();
                        logger.info(
                            "StudyDAOImpl - studyDraftCreation() Questionnarie manual schedule update - Ends");
                      }
                    } else {
                      searchQuery =
                          "From QuestionnairesFrequenciesBo QFBO where QFBO.questionnairesId=:id";
                      List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList =
                          session
                              .createQuery(searchQuery)
                              .setString("id", questionnaireBo.getId())
                              .list();
                      if ((questionnairesFrequenciesList != null)
                          && !questionnairesFrequenciesList.isEmpty()) {
                        logger.info(
                            "StudyDAOImpl - studyDraftCreation() Questionnarie except manual schedule other update - Starts");
                        for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo :
                            questionnairesFrequenciesList) {
                          QuestionnairesFrequenciesBo newQuestionnairesFrequenciesBo =
                              SerializationUtils.clone(questionnairesFrequenciesBo);
                          newQuestionnairesFrequenciesBo.setQuestionnairesId(
                              newQuestionnaireBo.getId());
                          newQuestionnairesFrequenciesBo.setId(null);
                          session.save(newQuestionnairesFrequenciesBo);
                        }
                        logger.info(
                            "StudyDAOImpl - studyDraftCreation() Questionnarie except manual schedule other update - Ends");
                      }
                    }
                  }
                  /** Schedule Purpose creating draft End * */
                  /** Content purpose creating draft Start * */
                  List<Integer> destinationList = new ArrayList<>();
                  Map<Integer, String> destionationMapList = new HashMap<>();

                  List<QuestionnairesStepsBo> existedQuestionnairesStepsBoList = null;
                  List<QuestionnairesStepsBo> newQuestionnairesStepsBoList = new ArrayList<>();
                  List<QuestionResponseSubTypeBo> existingQuestionResponseSubTypeList =
                      new ArrayList<>();
                  List<QuestionResponseSubTypeBo> newQuestionResponseSubTypeList =
                      new ArrayList<>();

                  List<QuestionReponseTypeBo> existingQuestionResponseTypeList = new ArrayList<>();
                  List<QuestionReponseTypeBo> newQuestionResponseTypeList = new ArrayList<>();

                  query =
                      session
                          .getNamedQuery("getQuestionnaireStepSequenceNo")
                          .setString("questionnairesId", questionnaireBo.getId());
                  existedQuestionnairesStepsBoList = query.list();
                  if ((existedQuestionnairesStepsBoList != null)
                      && !existedQuestionnairesStepsBoList.isEmpty()) {
                    for (QuestionnairesStepsBo questionnairesStepsBo :
                        existedQuestionnairesStepsBoList) {
                      String destionStep = questionnairesStepsBo.getDestinationStep();
                      if (destionStep.equals(String.valueOf(0))) {
                        destinationList.add(-1);
                      } else {
                        for (int i = 0; i < existedQuestionnairesStepsBoList.size(); i++) {
                          if ((existedQuestionnairesStepsBoList.get(i).getStepId() != null)
                              && destionStep.equals(
                                  existedQuestionnairesStepsBoList.get(i).getStepId())) {
                            destinationList.add(i);
                            break;
                          }
                        }
                      }
                      destionationMapList.put(
                          questionnairesStepsBo.getSequenceNo(), questionnairesStepsBo.getStepId());
                    }
                    for (QuestionnairesStepsBo questionnairesStepsBo :
                        existedQuestionnairesStepsBoList) {
                      if (StringUtils.isNotEmpty(questionnairesStepsBo.getStepType())) {
                        QuestionnairesStepsBo newQuestionnairesStepsBo =
                            SerializationUtils.clone(questionnairesStepsBo);
                        newQuestionnairesStepsBo.setQuestionnairesId(newQuestionnaireBo.getId());
                        newQuestionnairesStepsBo.setStepId(null);
                        session.save(newQuestionnairesStepsBo);
                        if (questionnairesStepsBo
                            .getStepType()
                            .equalsIgnoreCase(FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
                          logger.info(
                              "StudyDAOImpl - studyDraftCreation() Questionnarie instruction step - Starts");
                          InstructionsBo instructionsBo =
                              (InstructionsBo)
                                  session
                                      .getNamedQuery("getInstructionStep")
                                      .setString("id", questionnairesStepsBo.getInstructionFormId())
                                      .uniqueResult();
                          if (instructionsBo != null) {
                            InstructionsBo newInstructionsBo =
                                SerializationUtils.clone(instructionsBo);
                            newInstructionsBo.setId(null);
                            session.save(newInstructionsBo);

                            // updating new
                            // InstructionId
                            newQuestionnairesStepsBo.setInstructionFormId(
                                newInstructionsBo.getId());
                          }
                          logger.info(
                              "StudyDAOImpl - studyDraftCreation() Questionnarie instruction step - Ends");
                        } else if (questionnairesStepsBo
                            .getStepType()
                            .equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
                          logger.info(
                              "StudyDAOImpl - studyDraftCreation() Questionnarie Qustion step - Starts");
                          QuestionsBo questionsBo =
                              (QuestionsBo)
                                  session
                                      .getNamedQuery("getQuestionStep")
                                      .setString(
                                          "stepId", questionnairesStepsBo.getInstructionFormId())
                                      .uniqueResult();
                          if (questionsBo != null) {
                            // Question response
                            // subType
                            List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
                                session
                                    .getNamedQuery("getQuestionSubResponse")
                                    .setString("responseTypeId", questionsBo.getId())
                                    .list();
                            List<QuestionConditionBranchBo> questionConditionBranchList =
                                session
                                    .getNamedQuery("getQuestionConditionBranchList")
                                    .setString("questionId", questionsBo.getId())
                                    .list();

                            // Question response
                            // Type
                            questionReponseTypeBo =
                                (QuestionReponseTypeBo)
                                    session
                                        .getNamedQuery("getQuestionResponse")
                                        .setString("questionsResponseTypeId", questionsBo.getId())
                                        .setMaxResults(1)
                                        .uniqueResult();

                            QuestionsBo newQuestionsBo = SerializationUtils.clone(questionsBo);
                            newQuestionsBo.setId(null);
                            session.save(newQuestionsBo);

                            // Question response
                            // Type
                            if (questionReponseTypeBo != null) {

                              QuestionReponseTypeBo newQuestionReponseTypeBo =
                                  SerializationUtils.clone(questionReponseTypeBo);
                              newQuestionReponseTypeBo.setResponseTypeId(null);
                              newQuestionReponseTypeBo.setQuestionsResponseTypeId(
                                  newQuestionsBo.getId());
                              newQuestionReponseTypeBo.setOtherDestinationStepId(null);
                              session.save(newQuestionReponseTypeBo);

                              if ((questionReponseTypeBo.getOtherType() != null)
                                  && StringUtils.isNotEmpty(questionReponseTypeBo.getOtherType())
                                  && questionReponseTypeBo.getOtherType().equals("on")) {
                                existingQuestionResponseTypeList.add(questionReponseTypeBo);
                                newQuestionResponseTypeList.add(newQuestionReponseTypeBo);
                              }
                            }

                            // Question Condition
                            // branching logic
                            if ((questionConditionBranchList != null)
                                && !questionConditionBranchList.isEmpty()) {
                              for (QuestionConditionBranchBo questionConditionBranchBo :
                                  questionConditionBranchList) {
                                QuestionConditionBranchBo newQuestionConditionBranchBo =
                                    SerializationUtils.clone(questionConditionBranchBo);
                                newQuestionConditionBranchBo.setConditionId(null);
                                newQuestionConditionBranchBo.setQuestionId(newQuestionsBo.getId());
                                session.save(newQuestionConditionBranchBo);
                              }
                            }

                            // Question response
                            // subType
                            if ((questionResponseSubTypeList != null)
                                && !questionResponseSubTypeList.isEmpty()) {
                              existingQuestionResponseSubTypeList.addAll(
                                  questionResponseSubTypeList);

                              for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                                  questionResponseSubTypeList) {
                                QuestionResponseSubTypeBo newQuestionResponseSubTypeBo =
                                    SerializationUtils.clone(questionResponseSubTypeBo);
                                newQuestionResponseSubTypeBo.setResponseSubTypeValueId(null);
                                newQuestionResponseSubTypeBo.setResponseTypeId(
                                    newQuestionsBo.getId());
                                newQuestionResponseSubTypeBo.setDestinationStepId(null);
                                session.save(newQuestionResponseSubTypeBo);
                                newQuestionResponseSubTypeList.add(newQuestionResponseSubTypeBo);
                              }
                            }

                            // updating new
                            // InstructionId
                            newQuestionnairesStepsBo.setInstructionFormId(newQuestionsBo.getId());
                          }
                          logger.info(
                              "StudyDAOImpl - studyDraftCreation() Questionnarie Qustion step - Ends");
                        } else if (questionnairesStepsBo
                            .getStepType()
                            .equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
                          logger.info(
                              "StudyDAOImpl - studyDraftCreation() Questionnarie Form step - Starts");
                          FormBo formBo =
                              (FormBo)
                                  session
                                      .getNamedQuery("getFormBoStep")
                                      .setString(
                                          "stepId", questionnairesStepsBo.getInstructionFormId())
                                      .uniqueResult();
                          if (formBo != null) {
                            FormBo newFormBo = SerializationUtils.clone(formBo);
                            newFormBo.setFormId(null);
                            session.save(newFormBo);

                            List<FormMappingBo> formMappingBoList =
                                session
                                    .getNamedQuery("getFormByFormId")
                                    .setString("formId", formBo.getFormId())
                                    .list();
                            if ((formMappingBoList != null) && !formMappingBoList.isEmpty()) {
                              for (FormMappingBo formMappingBo : formMappingBoList) {
                                FormMappingBo newMappingBo =
                                    SerializationUtils.clone(formMappingBo);
                                newMappingBo.setFormId(newFormBo.getFormId());
                                newMappingBo.setId(null);

                                QuestionsBo questionsBo =
                                    (QuestionsBo)
                                        session
                                            .getNamedQuery("getQuestionByFormId")
                                            .setString("formId", formMappingBo.getQuestionId())
                                            .uniqueResult();
                                if (questionsBo != null) {
                                  // Question
                                  // response
                                  // subType
                                  List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
                                      session
                                          .getNamedQuery("getQuestionSubResponse")
                                          .setString("responseTypeId", questionsBo.getId())
                                          .list();

                                  // Question
                                  // response
                                  // Type
                                  questionReponseTypeBo =
                                      (QuestionReponseTypeBo)
                                          session
                                              .getNamedQuery("getQuestionResponse")
                                              .setString(
                                                  "questionsResponseTypeId", questionsBo.getId())
                                              .setMaxResults(1)
                                              .uniqueResult();

                                  QuestionsBo newQuestionsBo =
                                      SerializationUtils.clone(questionsBo);
                                  newQuestionsBo.setId(null);
                                  session.save(newQuestionsBo);

                                  // Question
                                  // response
                                  // Type
                                  if (questionReponseTypeBo != null) {
                                    QuestionReponseTypeBo newQuestionReponseTypeBo =
                                        SerializationUtils.clone(questionReponseTypeBo);
                                    newQuestionReponseTypeBo.setResponseTypeId(null);
                                    newQuestionReponseTypeBo.setQuestionsResponseTypeId(
                                        newQuestionsBo.getId());
                                    session.save(newQuestionReponseTypeBo);
                                  }

                                  // Question
                                  // response
                                  // subType
                                  if ((questionResponseSubTypeList != null)
                                      && !questionResponseSubTypeList.isEmpty()) {
                                    for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                                        questionResponseSubTypeList) {
                                      QuestionResponseSubTypeBo newQuestionResponseSubTypeBo =
                                          SerializationUtils.clone(questionResponseSubTypeBo);
                                      newQuestionResponseSubTypeBo.setResponseSubTypeValueId(null);
                                      newQuestionResponseSubTypeBo.setResponseTypeId(
                                          newQuestionsBo.getId());
                                      session.save(newQuestionResponseSubTypeBo);
                                    }
                                  }

                                  // adding
                                  // questionId
                                  newMappingBo.setQuestionId(newQuestionsBo.getId());
                                  session.save(newMappingBo);
                                }
                              }
                            }
                            // updating new formId
                            newQuestionnairesStepsBo.setInstructionFormId(newFormBo.getFormId());
                          }
                          logger.info(
                              "StudyDAOImpl - studyDraftCreation() Questionnarie Form step - Ends");
                        }
                        session.update(newQuestionnairesStepsBo);
                        newQuestionnairesStepsBoList.add(newQuestionnairesStepsBo);
                      }
                    }
                  }
                  if ((destinationList != null) && !destinationList.isEmpty()) {
                    for (int i = 0; i < destinationList.size(); i++) {
                      String desId = String.valueOf(0);
                      if (destinationList.get(i) != -1) {
                        desId =
                            newQuestionnairesStepsBoList.get(destinationList.get(i)).getStepId();
                      }
                      newQuestionnairesStepsBoList.get(i).setDestinationStep(desId);
                      session.update(newQuestionnairesStepsBoList.get(i));
                    }
                  }
                  List<Integer> sequenceSubTypeList = new ArrayList<>();
                  List<String> destinationResList = new ArrayList<>();
                  if ((existingQuestionResponseSubTypeList != null)
                      && !existingQuestionResponseSubTypeList.isEmpty()) {
                    for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                        existingQuestionResponseSubTypeList) {
                      if (StringUtils.isEmpty(questionResponseSubTypeBo.getDestinationStepId())) {
                        sequenceSubTypeList.add(null);
                      } else if ((questionResponseSubTypeBo.getDestinationStepId() != null)
                          && questionResponseSubTypeBo
                              .getDestinationStepId()
                              .equals(String.valueOf(0))) {
                        sequenceSubTypeList.add(-1);
                      } else {
                        if ((existedQuestionnairesStepsBoList != null)
                            && !existedQuestionnairesStepsBoList.isEmpty()) {
                          for (QuestionnairesStepsBo questionnairesStepsBo :
                              existedQuestionnairesStepsBoList) {
                            if ((questionResponseSubTypeBo.getDestinationStepId() != null)
                                && questionResponseSubTypeBo
                                    .getDestinationStepId()
                                    .equals(questionnairesStepsBo.getStepId())) {
                              sequenceSubTypeList.add(questionnairesStepsBo.getSequenceNo());
                              break;
                            }
                          }
                        }
                      }
                    }
                  }
                  if ((sequenceSubTypeList != null) && !sequenceSubTypeList.isEmpty()) {
                    for (int i = 0; i < sequenceSubTypeList.size(); i++) {
                      String desId = null;
                      if (sequenceSubTypeList.get(i) == null) {
                        desId = null;
                      } else if (sequenceSubTypeList.get(i).equals(-1)) {
                        desId = String.valueOf(0);

                      } else {
                        for (QuestionnairesStepsBo questionnairesStepsBo :
                            newQuestionnairesStepsBoList) {
                          if (sequenceSubTypeList
                              .get(i)
                              .equals(questionnairesStepsBo.getSequenceNo())) {
                            desId = questionnairesStepsBo.getStepId();
                            break;
                          }
                        }
                      }
                      destinationResList.add(desId);
                    }
                    for (int i = 0; i < destinationResList.size(); i++) {
                      newQuestionResponseSubTypeList
                          .get(i)
                          .setDestinationStepId(destinationResList.get(i));
                      session.update(newQuestionResponseSubTypeList.get(i));
                    }
                  }

                  // for other type , update the destination in questionresponsetype table
                  /** start * */
                  List<Integer> sequenceTypeList = new ArrayList<>();
                  List<String> destinationResTypeList = new ArrayList<>();
                  if ((existingQuestionResponseTypeList != null)
                      && !existingQuestionResponseTypeList.isEmpty()) {
                    for (QuestionReponseTypeBo questionResponseTypeBo :
                        existingQuestionResponseTypeList) {
                      if (StringUtils.isEmpty(questionResponseTypeBo.getOtherDestinationStepId())) {
                        sequenceTypeList.add(null);
                      } else if ((questionResponseTypeBo.getOtherDestinationStepId() != null)
                          && questionResponseTypeBo
                              .getOtherDestinationStepId()
                              .equals(String.valueOf(0))) {
                        sequenceTypeList.add(-1);
                      } else {
                        if ((existedQuestionnairesStepsBoList != null)
                            && !existedQuestionnairesStepsBoList.isEmpty()) {
                          for (QuestionnairesStepsBo questionnairesStepsBo :
                              existedQuestionnairesStepsBoList) {
                            if ((questionResponseTypeBo.getOtherDestinationStepId() != null)
                                && questionResponseTypeBo
                                    .getOtherDestinationStepId()
                                    .equals(questionnairesStepsBo.getStepId())) {
                              sequenceTypeList.add(questionnairesStepsBo.getSequenceNo());
                              break;
                            }
                          }
                        }
                      }
                    }
                  }
                  if ((sequenceTypeList != null) && !sequenceTypeList.isEmpty()) {
                    for (int i = 0; i < sequenceTypeList.size(); i++) {
                      String desId = null;
                      if (sequenceTypeList.get(i) == null) {
                        desId = null;

                      } else if (sequenceTypeList.get(i).equals(-1)) {
                        desId = String.valueOf(0);

                      } else {
                        for (QuestionnairesStepsBo questionnairesStepsBo :
                            newQuestionnairesStepsBoList) {
                          if (sequenceTypeList
                              .get(i)
                              .equals(questionnairesStepsBo.getSequenceNo())) {
                            desId = questionnairesStepsBo.getStepId();
                            break;
                          }
                        }
                      }
                      destinationResTypeList.add(desId);
                    }
                    for (int i = 0; i < destinationResTypeList.size(); i++) {
                      newQuestionResponseTypeList
                          .get(i)
                          .setOtherDestinationStepId(destinationResTypeList.get(i));
                      session.update(newQuestionResponseTypeList.get(i));
                    }
                  }
                  /** * end ** */
                  studyActivityVersionBo.setActivityVersion(newQuestionnaireBo.getVersion());
                  /** Content purpose creating draft End * */
                } else {
                  studyActivityVersionBo.setActivityVersion(questionnaireBo.getVersion());
                }
                session.save(studyActivityVersionBo);
                logger.info("StudyDAOImpl - studyDraftCreation() Questionnarie creation- Ends");
              }
              // Executing draft version to 0
            } // If Questionarries updated flag -1 then update End
          } // In Questionnarie change or not

          // which are already in live those are deleted in draft to
          // making update those questionnarie to archived and make it
          // inactive(status=0)

          StringBuilder subString = new StringBuilder();
          subString.append("select CONCAT('");
          subString.append("',shortTitle,'");
          subString.append(
              "') from QuestionnaireBo where active=0 and studyId=:id and shortTitle is NOT NULL");
          query = session.createQuery(subString.toString());
          query.setString("id", studyBo.getId());
          objectList = query.list();

          if (objectList != null && !objectList.isEmpty()) {
            try {
              String currentDateTime = FdahpStudyDesignerUtil.getCurrentDateTime();
              String subQuery =
                  "update questionnaires SET is_live=2,modified_date=:currentTime, "
                      + " active=0 where short_title IN(:objectList) and is_live=1 and custom_study_id=:custStudyId";
              query = session.createSQLQuery(subQuery);
              query.setParameter("currentTime", currentDateTime);
              query.setParameterList("objectList", objectList);
              query.setParameter("custStudyId", studyBo.getCustomStudyId());
              query.executeUpdate();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }

          // In ActiveTask change or not Start
          // is there any change doing clone of active task
          if ((studyVersionBo == null)
              || ((studyBo.getHasActivetaskDraft() != null)
                  && studyBo.getHasActivetaskDraft().equals(1))) {
            // update all ActiveTasks to archive (live as 2)
            query =
                session
                    .getNamedQuery("updateStudyActiveTaskVersion")
                    .setString(
                        FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
            query.executeUpdate();

            // ActiveTasks
            query =
                session
                    .getNamedQuery("ActiveTaskBo.getActiveTasksByByStudyId")
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
            activeTasks = query.list();
            if ((activeTasks != null) && !activeTasks.isEmpty()) {
              for (ActiveTaskBo activeTaskBo : activeTasks) {
                Float activeTaskversion = activeTaskBo.getVersion();
                ActiveTaskBo newActiveTaskBo = SerializationUtils.clone(activeTaskBo);
                newActiveTaskBo.setId(null);
                newActiveTaskBo.setStudyId(studyDreaftBo.getId());
                if (studyVersionBo == null) {
                  newActiveTaskBo.setVersion(1.0f);
                  activeTaskBo.setVersion(1.0f);
                } else if ((activeTaskBo.getIsChange() != null)
                    && activeTaskBo.getIsChange().equals(1)) {
                  if (activeTaskversion.equals(0f)) {
                    activeTaskBo.setVersion(1.0f);
                    newActiveTaskBo.setVersion(1.0f);
                  } else {
                    newActiveTaskBo.setVersion(activeTaskBo.getVersion() + 0.1f);
                    activeTaskBo.setVersion(activeTaskBo.getVersion() + 0.1f);
                  }
                } else {
                  newActiveTaskBo.setVersion(activeTaskBo.getVersion());
                  activeTaskBo.setVersion(activeTaskBo.getVersion());
                }
                newActiveTaskBo.setLive(1);
                newActiveTaskBo.setCustomStudyId(studyBo.getCustomStudyId());
                session.save(newActiveTaskBo);

                /** Schedule Purpose creating draft Start * */
                if (StringUtils.isNotEmpty(activeTaskBo.getFrequency())) {
                  if (activeTaskBo
                      .getFrequency()
                      .equalsIgnoreCase(
                          FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                    searchQuery =
                        "From ActiveTaskCustomScheduleBo QCSBO where QCSBO.activeTaskId=:id";
                    List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleList =
                        session
                            .createQuery(searchQuery)
                            .setString("id", activeTaskBo.getId())
                            .list();
                    if ((activeTaskCustomScheduleList != null)
                        && !activeTaskCustomScheduleList.isEmpty()) {
                      for (ActiveTaskCustomScheduleBo customScheduleBo :
                          activeTaskCustomScheduleList) {
                        ActiveTaskCustomScheduleBo newCustomScheduleBo =
                            SerializationUtils.clone(customScheduleBo);
                        newCustomScheduleBo.setActiveTaskId(newActiveTaskBo.getId());
                        newCustomScheduleBo.setId(null);
                        session.save(newCustomScheduleBo);
                        session.update(customScheduleBo);
                      }
                      // updating draft version of
                      // schedule to Yes
                      session
                          .createQuery(
                              "UPDATE ActiveTaskCustomScheduleBo set used=true where activeTaskId=:id")
                          .setString("id", activeTaskBo.getId())
                          .executeUpdate();
                    }
                  } else {
                    searchQuery = "From ActiveTaskFrequencyBo QFBO where QFBO.activeTaskId=:id";
                    List<ActiveTaskFrequencyBo> activeTaskFrequenciesList =
                        session
                            .createQuery(searchQuery)
                            .setString("id", activeTaskBo.getId())
                            .list();
                    if ((activeTaskFrequenciesList != null)
                        && !activeTaskFrequenciesList.isEmpty()) {
                      for (ActiveTaskFrequencyBo activeTaskFrequenciesBo :
                          activeTaskFrequenciesList) {
                        ActiveTaskFrequencyBo newFrequenciesBo =
                            SerializationUtils.clone(activeTaskFrequenciesBo);
                        newFrequenciesBo.setActiveTaskId(newActiveTaskBo.getId());
                        newFrequenciesBo.setId(null);
                        session.save(newFrequenciesBo);
                      }
                    }
                  }
                }
                /** Schedule Purpose creating draft End * */

                /** Content Purpose creating draft Start * */
                query =
                    session
                        .getNamedQuery("getAttributeListByActiveTAskId")
                        .setString("activeTaskId", activeTaskBo.getId());
                List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBoList = query.list();
                if ((activeTaskAtrributeValuesBoList != null)
                    && !activeTaskAtrributeValuesBoList.isEmpty()) {
                  for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo :
                      activeTaskAtrributeValuesBoList) {
                    ActiveTaskAtrributeValuesBo newActiveTaskAtrributeValuesBo =
                        SerializationUtils.clone(activeTaskAtrributeValuesBo);
                    newActiveTaskAtrributeValuesBo.setActiveTaskId(newActiveTaskBo.getId());
                    newActiveTaskAtrributeValuesBo.setAttributeValueId(null);
                    session.save(newActiveTaskAtrributeValuesBo);
                  }
                }
                /** Content Purpose creating draft End * */
              }
              // Executing draft version to 0
              session
                  .createQuery("UPDATE ActiveTaskBo set live=0, isChange = 0 where studyId=:id")
                  .setString("id", studyBo.getId())
                  .executeUpdate();
            } // Active TAsk End
          } // In ActiveTask change or not
          // Activities End

          // If Consent updated flag -1 then update
          query = session.createQuery("from ConsentBo CBO where CBO.studyId=:id");
          query.setString("id", studyBo.getId());
          ConsentBo consentBo = (ConsentBo) query.uniqueResult();

          if ((studyVersionBo == null) || studyBo.getHasConsentDraft().equals(1)) {
            // update all consentBo to archive (live as 2)
            query =
                session
                    .getNamedQuery("updateStudyConsentVersion")
                    .setString(
                        FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
            query.executeUpdate();

            // update all consentInfoBo to archive (live as 2)
            query =
                session
                    .getNamedQuery("updateStudyConsentInfoVersion")
                    .setString(
                        FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
            query.executeUpdate();

            Map<String, String> values = new HashMap<>();

            // If Consent updated flag -1 then update
            if (consentBo != null) {
              ConsentBo newConsentBo = SerializationUtils.clone(consentBo);
              newConsentBo.setId(null);
              newConsentBo.setStudyId(studyDreaftBo.getId());
              newConsentBo.setVersion(newstudyVersionBo.getConsentVersion());
              newConsentBo.setLive(1);
              newConsentBo.setCustomStudyId(studyBo.getCustomStudyId());
              if (newstudyVersionBo.getConsentVersion() == 1) {
                newConsentBo.setEnrollAgain(true);
              }
              session.save(newConsentBo);
              values.put("consent_document_version", String.valueOf(newConsentBo.getVersion()));
              auditLogEventHelper.logEvent(
                  STUDY_CONSENT_DOCUMENT_NEW_VERSION_PUBLISHED, auditRequest, values);
              if (newstudyVersionBo.getConsentVersion() == 1 || consentBo.getEnrollAgain()) {
                consentBo.setEnrollAgain(false);
                session.save(consentBo);
              }

              saveUnsignedDocumentOnCloud(studyBo, newConsentBo, userId);
            }

            query =
                session
                    .getNamedQuery("getConsentInfoByStudyId")
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
            List<ConsentInfoBo> consentInfoBoList = query.list();
            if ((consentInfoBoList != null) && !consentInfoBoList.isEmpty()) {
              for (ConsentInfoBo consentInfoBo : consentInfoBoList) {
                ConsentInfoBo newConsentInfoBo = SerializationUtils.clone(consentInfoBo);
                newConsentInfoBo.setId(null);
                newConsentInfoBo.setStudyId(studyDreaftBo.getId());
                newConsentInfoBo.setVersion(newstudyVersionBo.getConsentVersion());
                newConsentInfoBo.setCustomStudyId(studyBo.getCustomStudyId());
                newConsentInfoBo.setLive(1);
                session.save(newConsentInfoBo);
                values.put("consent_version", String.valueOf(newConsentInfoBo.getVersion()));
                auditLogEventHelper.logEvent(
                    STUDY_CONSENT_CONTENT_NEW_VERSION_PUBLISHED, auditRequest, values);
              }
            }
          }

          if (consentBo != null) {
            // Comprehension test Start
            if (StringUtils.isNotEmpty(consentBo.getNeedComprehensionTest())
                && consentBo
                    .getNeedComprehensionTest()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
              List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
              List<String> comprehensionIds = new ArrayList<>();
              List<ComprehensionTestResponseBo> comprehensionTestResponseList = null;
              query =
                  session.createQuery(
                      "From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=:id and CTQBO.active=1 order by CTQBO.sequenceNo asc");
              query.setString("id", studyBo.getId());
              comprehensionTestQuestionList = query.list();
              if ((comprehensionTestQuestionList != null)
                  && !comprehensionTestQuestionList.isEmpty()) {
                for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
                    comprehensionTestQuestionList) {
                  comprehensionIds.add(comprehensionTestQuestionBo.getId());
                }
                comprehensionTestResponseList =
                    session
                        .createQuery(
                            "From ComprehensionTestResponseBo CTRBO "
                                + "where CTRBO.comprehensionTestQuestionId IN (:comprehensionIds) order by comprehensionTestQuestionId")
                        .setParameterList("comprehensionIds", comprehensionIds)
                        .list();
                for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
                    comprehensionTestQuestionList) {
                  ComprehensionTestQuestionBo newComprehensionTestQuestionBo =
                      SerializationUtils.clone(comprehensionTestQuestionBo);
                  newComprehensionTestQuestionBo.setStudyId(studyDreaftBo.getId());
                  newComprehensionTestQuestionBo.setId(null);
                  session.save(newComprehensionTestQuestionBo);
                  if ((comprehensionTestResponseList != null)
                      && !comprehensionTestResponseList.isEmpty()) {
                    for (ComprehensionTestResponseBo comprehensionTestResponseBo :
                        comprehensionTestResponseList) {
                      if (comprehensionTestQuestionBo
                          .getId()
                          .equals(comprehensionTestResponseBo.getComprehensionTestQuestionId())) {
                        ComprehensionTestResponseBo newComprehensionTestResponseBo =
                            SerializationUtils.clone(comprehensionTestResponseBo);
                        newComprehensionTestResponseBo.setId(null);
                        newComprehensionTestResponseBo.setComprehensionTestQuestionId(
                            newComprehensionTestQuestionBo.getId());
                        session.save(newComprehensionTestResponseBo);
                      }
                    }
                  }
                }
              }
            }
            // Comprehension test End
          }

          // updating the edited study to draft
          if ((studyDreaftBo != null) && (studyDreaftBo.getId() != null)) {
            studyBo.setVersion(0f);
            studyBo.setHasActivetaskDraft(0);
            studyBo.setHasQuestionnaireDraft(0);
            studyBo.setHasConsentDraft(0);
            studyBo.setHasStudyDraft(0);
            studyBo.setLive(0);
            session.update(studyBo);

            // Updating Notification and Resources
            session
                .createQuery(
                    "UPDATE NotificationBO set customStudyId=:customStudyId where studyId=:id")
                .setString("customStudyId", studyBo.getCustomStudyId())
                .setString("id", studyBo.getId())
                .executeUpdate();
            session
                .createQuery("UPDATE Checklist set customStudyId=:customStudyId where studyId=:id")
                .setString("customStudyId", studyBo.getCustomStudyId())
                .setString("id", studyBo.getId())
                .executeUpdate();
          }
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - studyDraftCreation() - ERROR ", e);
      e.printStackTrace();
    }
    logger.exit("studyDraftCreation() - Ends");

    return message;
  }

  /**
   * saves unsigned document in cloud storage
   *
   * @param studyBo
   * @param newConsentBo
   * @throws Exception
   */
  private void saveUnsignedDocumentOnCloud(StudyBo studyBo, ConsentBo newConsentBo, String userId)
      throws Exception {
    logger.entry("begin saveUnsignedDocumentOnCloud()");
    Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();
    String enabled = configMap.get("enableConsentManagementAPI");

    try {
      if (StringUtils.isNotEmpty(enabled) && Boolean.valueOf(enabled)) {

        // appending study name to unsigned document
        StringBuilder docBuilder =
            new StringBuilder("<br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
        docBuilder.append(
            String.format(
                "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>",
                studyBo.getName()));

        docBuilder.append("</div><br>");
        docBuilder.append(newConsentBo.getConsentDocContent());

        String consentDoc =
            StringUtils.isEmpty(newConsentBo.getConsentDocContent())
                ? ""
                : StringEscapeUtils.escapeHtml4(
                    StringEscapeUtils.unescapeHtml4(
                        docBuilder
                            .toString()
                            .replaceAll("&#34;", "'")
                            .replaceAll("em>", "i>")
                            .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'")));

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(Jsoup.parse(consentDoc).wholeText(), byteStream);

        String gcsUri =
            FdahpStudyDesignerUtil.saveFile(
                newConsentBo.getVersion()
                    + "_"
                    + new SimpleDateFormat("MMddyyyyHHmmss").format(new Date())
                    + ".pdf",
                byteStream.toByteArray(),
                studyBo.getCustomStudyId() + "/" + "unsignedDocuments");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("StudyId", studyBo.getCustomStudyId());

        // Create Dataset in Google Healthcare API
        try {
          consentApis.createDatasetInHealthcareAPI(studyBo.getCustomStudyId());
        } catch (Exception e) {
          if (e.getMessage().contains("already exists")) {
            logger.error(
                "StudyDAOImpl - Create Dataset in Google Healthcare API - ERROR ", e.getMessage());
          }
        }

        // Create Consent store in Google Healthcare API
        try {
          consentApis.consentStoreGet(
              "CONSENT_" + studyBo.getCustomStudyId(), studyBo.getCustomStudyId());
        } catch (Exception e) {
          if (e.getMessage().contains("not exist")) {
            consentApis.createConsentStore(
                "CONSENT_" + studyBo.getCustomStudyId(), studyBo.getCustomStudyId());
          }
        }

        consentApis.createConsentArtifact(
            metadata,
            userId,
            String.format("%.1f", newConsentBo.getVersion()),
            gcsUri,
            studyBo.getCustomStudyId(),
            "CONSENT_" + studyBo.getCustomStudyId());
      }

    } catch (IOException e) {
      logger.error("StudyDAOImpl - saveUnsignedDocumentOnCloud() - ERROR ", e.getMessage());
    }
    logger.exit("saveUnsignedDocumentOnCloud() - Ends");
  }

  @SuppressWarnings("unchecked")
  @Override
  public String updateStudyActionOnAction(String studyId, String buttonText, SessionObject sesObj) {
    logger.entry("begin updateStudyActionOnAction()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    StudyBo studyBo = null;
    List<Integer> objectList = null;
    String activitydetails = "";
    String activity = "";
    StudyBo liveStudy = null;
    StudyVersionBo studyVersionBo = null;
    NotificationBO notificationBO = null;
    StudyBuilderAuditEvent auditLogEvent = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(buttonText)) {
        studyBo =
            (StudyBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                    .setString("id", studyId)
                    .uniqueResult();
        if (studyBo != null) {
          auditRequest.setStudyId(studyBo.getCustomStudyId());
          auditRequest.setAppId(studyBo.getAppId());
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)
              || buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
            studyBo.setStudyPreActiveFlag(false);
            studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_ACTIVE);
            studyBo.setStudylunchDate(FdahpStudyDesignerUtil.getCurrentDateTime());
            session.update(studyBo);
            // getting Questionnaries based on StudyId
            query =
                session.createQuery(
                    "select ab.id"
                        + " from QuestionnairesFrequenciesBo a,QuestionnaireBo ab"
                        + " where a.questionnairesId=ab.id"
                        + " and ab.studyId=:impValue"
                        + " and ab.frequency=:frequencyTime and a.isLaunchStudy=1"
                        + " and active=1"
                        + " and ab.shortTitle NOT IN(SELECT shortTitle from QuestionnaireBo WHERE active=1 AND live=1 AND customStudyId=:customStudyId) ");
            query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyId);
            query.setParameter(
                "frequencyTime", FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME);
            query.setParameter("customStudyId", studyBo.getCustomStudyId());
            objectList = query.list();
            if ((objectList != null) && !objectList.isEmpty()) {
              query =
                  session
                      .createSQLQuery(
                          "update questionnaires ab SET ab.study_lifetime_start= :launchDate where id IN (:objectList)")
                      .setString("launchDate", studyBo.getStudylunchDate())
                      .setParameterList("objectList", objectList);
              query.executeUpdate();
            }
            // getting activeTasks based on StudyId
            query =
                session.createQuery(
                    "select ab.id"
                        + " from ActiveTaskFrequencyBo a,ActiveTaskBo ab"
                        + " where a.activeTaskId=ab.id"
                        + " and ab.studyId=:impValue"
                        + " and ab.frequency=:frequencyTime and a.isLaunchStudy=1"
                        + " and active=1"
                        + " and ab.shortTitle NOT IN (SELECT shortTitle from ActiveTaskBo WHERE active=1 AND live=1 AND customStudyId=:customStudyId) ");
            query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyId);
            query.setParameter(
                "frequencyTime", FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME);
            query.setParameter("customStudyId", studyBo.getCustomStudyId());
            objectList = query.list();
            if ((objectList != null) && !objectList.isEmpty()) {
              query =
                  session
                      .createSQLQuery(
                          "update active_task ab SET ab.active_task_lifetime_start= :launchDate where id IN (:objectList)")
                      .setString("launchDate", studyBo.getStudylunchDate())
                      .setParameterList("objectList", objectList);
              query.executeUpdate();
            }
            message = FdahpStudyDesignerConstants.SUCCESS;
            // StudyDraft version creation
            message = studyDraftCreation(studyBo, session, auditRequest, sesObj.getUserId());
            if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
              if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)) {
                // notification text --
                auditLogEvent = STUDY_LAUNCHED;
                // notification sent to gateway
                notificationBO = new NotificationBO();
                notificationBO.setStudyId(studyBo.getId());
                notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
                String platform = FdahpStudyDesignerUtil.getStudyPlatform(studyBo);
                notificationBO.setPlatform(platform);
                if (StringUtils.isNotEmpty(studyBo.getAppId())) {
                  notificationBO.setAppId(studyBo.getAppId());
                }
                notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_GT);
                notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
                notificationBO.setNotificationScheduleType(
                    FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
                notificationBO.setNotificationStatus(false);
                notificationBO.setCreatedBy(sesObj.getUserId());
                notificationBO.setNotificationText(
                    FdahpStudyDesignerConstants.NOTIFICATION_UPCOMING_OR_ACTIVE_TEXT);
                notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
                notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
                notificationBO.setScheduleTimestamp(
                    FdahpStudyDesignerUtil.getTimeStamp(
                        notificationBO.getScheduleDate(), notificationBO.getScheduleTime()));
                notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                notificationBO.setNotificationDone(true);
                session.save(notificationBO);
              } else {
                // notification text --
                activity = "Study Updates Published.";
                activitydetails =
                    "Study version updated successfully. (Study ID = "
                        + studyBo.getCustomStudyId()
                        + ", Status = version updates)";
                auditLogEvent = UPDATES_PUBLISHED_TO_STUDY;
              }
              // Update resource startdate and time based on
              // customStudyId
              session
                  .createQuery(
                      "UPDATE NotificationBO set scheduleDate=:currentDate, scheduleTime = :currentTime, scheduleTimestamp=:currentTimestamp where customStudyId=:customStudyId and scheduleDate IS NULL and scheduleTime IS NULL and notificationType= :notiSt and notificationSubType= :subType and notificationScheduleType=:immedidate ")
                  .setString("currentDate", FdahpStudyDesignerUtil.getCurrentDate())
                  .setString("currentTime", FdahpStudyDesignerUtil.getCurrentTime())
                  .setString(
                      "currentTimestamp",
                      FdahpStudyDesignerUtil.getTimeStamp(
                              FdahpStudyDesignerUtil.getCurrentDate(),
                              FdahpStudyDesignerUtil.getCurrentTime())
                          .toString())
                  .setString("customStudyId", studyBo.getCustomStudyId())
                  .setString("notiSt", FdahpStudyDesignerConstants.NOTIFICATION_ST)
                  .setString("subType", FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_RESOURCE)
                  .setString("immedidate", FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE)
                  .executeUpdate();

              // Update activity startdate and time based on
              // customStudyId
              session
                  .createQuery(
                      "UPDATE NotificationBO set scheduleDate=:currentDate, scheduleTime = :currentTime, scheduleTimestamp=:currentTimestamp where customStudyId=:customStudyId and scheduleDate IS NULL and scheduleTime IS NULL and notificationType=:notiSt and notificationSubType=:subType and notificationScheduleType=:immedidate")
                  .setString("currentDate", FdahpStudyDesignerUtil.getCurrentDate())
                  .setString("currentTime", FdahpStudyDesignerUtil.getCurrentTime())
                  .setString(
                      "currentTimestamp",
                      FdahpStudyDesignerUtil.getTimeStamp(
                              FdahpStudyDesignerUtil.getCurrentDate(),
                              FdahpStudyDesignerUtil.getCurrentTime())
                          .toString())
                  .setString("customStudyId", studyBo.getCustomStudyId())
                  .setString("notiSt", FdahpStudyDesignerConstants.NOTIFICATION_ST)
                  .setString("subType", FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ACTIVITY)
                  .setString("immedidate", FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE)
                  .executeUpdate();

            } else {
              throw new IllegalStateException("Error in creating in Study draft");
            }
          } else {
            liveStudy =
                (StudyBo)
                    session
                        .getNamedQuery("getStudyLiveVersion")
                        .setString(
                            FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId())
                        .uniqueResult();
            if (liveStudy != null) {
              liveStudy.setStudyPreActiveFlag(false);
              query =
                  session
                      .getNamedQuery("getStudyByCustomStudyId")
                      .setString(
                          FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
              query.setMaxResults(1);
              studyVersionBo = (StudyVersionBo) query.uniqueResult();
              if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_PAUSE)) {
                // notification text --
                if (studyVersionBo != null) {
                  auditLogEvent = STUDY_PAUSED;
                }
                notificationBO = new NotificationBO();
                notificationBO.setStudyId(liveStudy.getId());
                notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
                String platform = FdahpStudyDesignerUtil.getStudyPlatform(studyBo);
                notificationBO.setPlatform(platform);
                if (StringUtils.isNotEmpty(studyBo.getAppId())) {
                  notificationBO.setAppId(studyBo.getAppId());
                }
                notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
                notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
                notificationBO.setNotificationScheduleType(
                    FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
                notificationBO.setNotificationStatus(false);
                notificationBO.setCreatedBy(sesObj.getUserId());
                notificationBO.setNotificationText(
                    FdahpStudyDesignerConstants.NOTIFICATION_PAUSE_TEXT.replace(
                        "$customId", studyBo.getName()));
                notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
                notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
                notificationBO.setScheduleTimestamp(
                    FdahpStudyDesignerUtil.getTimeStamp(
                        notificationBO.getScheduleDate(), notificationBO.getScheduleTime()));
                notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                notificationBO.setNotificationDone(true);
                session.save(notificationBO);

                liveStudy.setStatus(FdahpStudyDesignerConstants.STUDY_PAUSED);
                studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_PAUSED);
                studyBo.setStudyPreActiveFlag(false);
              } else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_RESUME)) {
                // notification text --
                auditLogEvent = STUDY_RESUMED;
                notificationBO = new NotificationBO();
                notificationBO.setStudyId(liveStudy.getId());
                notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
                String platform = FdahpStudyDesignerUtil.getStudyPlatform(studyBo);
                notificationBO.setPlatform(platform);
                if (StringUtils.isNotEmpty(studyBo.getAppId())) {
                  notificationBO.setAppId(studyBo.getAppId());
                }
                notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
                notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
                notificationBO.setNotificationScheduleType(
                    FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
                notificationBO.setNotificationStatus(false);
                notificationBO.setCreatedBy(sesObj.getUserId());
                notificationBO.setNotificationText(
                    FdahpStudyDesignerConstants.NOTIFICATION_RESUME_TEXT.replace(
                        "$customId", studyBo.getName()));
                notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
                notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
                notificationBO.setScheduleTimestamp(
                    FdahpStudyDesignerUtil.getTimeStamp(
                        notificationBO.getScheduleDate(), notificationBO.getScheduleTime()));
                notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                notificationBO.setNotificationDone(true);
                session.save(notificationBO);
                liveStudy.setStatus(FdahpStudyDesignerConstants.STUDY_ACTIVE);
                studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_ACTIVE);
                studyBo.setStudyPreActiveFlag(false);
              } else if (buttonText.equalsIgnoreCase(
                  FdahpStudyDesignerConstants.ACTION_DEACTIVATE)) {
                // notification text --
                liveStudy.setStatus(FdahpStudyDesignerConstants.STUDY_DEACTIVATED);
                auditLogEvent = STUDY_DEACTIVATED;
                notificationBO = new NotificationBO();
                notificationBO.setStudyId(liveStudy.getId());
                notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
                String platform = FdahpStudyDesignerUtil.getStudyPlatform(studyBo);
                notificationBO.setPlatform(platform);
                if (StringUtils.isNotEmpty(studyBo.getAppId())) {
                  notificationBO.setAppId(studyBo.getAppId());
                }
                notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
                notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
                notificationBO.setNotificationScheduleType(
                    FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
                notificationBO.setNotificationStatus(false);
                notificationBO.setCreatedBy(sesObj.getUserId());
                notificationBO.setNotificationText(
                    FdahpStudyDesignerConstants.NOTIFICATION_DEACTIVATE_TEXT.replace(
                        "$customId", studyBo.getName()));
                notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
                notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
                notificationBO.setScheduleTimestamp(
                    FdahpStudyDesignerUtil.getTimeStamp(
                        notificationBO.getScheduleDate(), notificationBO.getScheduleTime()));
                notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                notificationBO.setNotificationDone(true);
                session.save(notificationBO);
                studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_DEACTIVATED);
                studyBo.setStudyPreActiveFlag(false);
                session.update(studyBo);
              }
              session.update(liveStudy);
              message = FdahpStudyDesignerConstants.SUCCESS;
              auditRequest.setStudyVersion(liveStudy.getVersion().toString());
            }
          }
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            session.update(studyBo);
          }
        }
        auditLogEventHelper.logEvent(auditLogEvent, auditRequest);
      }
      transaction.commit();
    } catch (Exception e) {
      message = FdahpStudyDesignerConstants.FAILURE;
      transaction.rollback();
      logger.error("StudyDAOImpl - updateStudyActionOnAction() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("updateStudyActionOnAction() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String validateActivityComplete(String studyId, String action) {
    logger.entry("begin validateActivityComplete()");
    Session session = null;
    boolean questionnarieFlag = true;
    boolean activeTaskEmpty = false;
    boolean questionnarieEmpty = false;
    boolean activeTaskFlag = true;
    List<ActiveTaskBo> completedactiveTasks = null;
    List<QuestionnaireBo> completedquestionnaires = null;
    StudySequenceBo studySequence = null;
    String message = FdahpStudyDesignerConstants.SUCCESS;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(action)) {
        // For checking active task or questionnaire done or not
        query =
            session
                .getNamedQuery("ActiveTaskBo.getActiveTasksByByStudyIdDone")
                .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        completedactiveTasks = query.list();
        /* //
        query =
            session
                .getNamedQuery("ActiveTaskBo.getActiveTasksByByStudyIdDone")
                .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        completedactiveTasks = query.list();*/
        query =
            session
                .getNamedQuery("getQuestionariesByStudyIdDone")
                .setString(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        completedquestionnaires = query.list();
        studySequence =
            (StudySequenceBo)
                session
                    .getNamedQuery("getStudySequenceByStudyId")
                    .setString("studyId", studyId)
                    .uniqueResult();
        if (((completedactiveTasks != null) && !completedactiveTasks.isEmpty())) {
          for (ActiveTaskBo activeTaskBo : completedactiveTasks) {
            if (!activeTaskBo.isAction()) {
              activeTaskFlag = false;
              break;
            }
          }
        } else {
          activeTaskEmpty = true;
        }
        if ((completedquestionnaires != null) && !completedquestionnaires.isEmpty()) {
          for (QuestionnaireBo questionnaireBo : completedquestionnaires) {
            if ((questionnaireBo.getStatus() != null) && !questionnaireBo.getStatus()) {
              questionnarieFlag = false;
              break;
            }
          }
        } else {
          questionnarieEmpty = true;
        }
        // questionnarieFlag, activeTaskFlag will be true, then only
        // will allow to mark as complete
        if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTIVITY_TYPE_QUESTIONNAIRE)) {
          if (!questionnarieFlag) {
            message = FdahpStudyDesignerConstants.MARK_AS_COMPLETE_DONE_ERROR_MSG;
          } else if (studySequence.isStudyExcActiveTask()
              && (questionnarieEmpty && activeTaskEmpty)) {
            message = FdahpStudyDesignerConstants.ACTIVEANDQUESSIONAIREEMPTY_ERROR_MSG;
          }
        } else {
          if (!activeTaskFlag) {
            message = FdahpStudyDesignerConstants.MARK_AS_COMPLETE_DONE_ERROR_MSG;
          } else if (studySequence.isStudyExcQuestionnaries()
              && (questionnarieEmpty && activeTaskEmpty)) {
            message = FdahpStudyDesignerConstants.ACTIVEANDQUESSIONAIREEMPTY_ERROR_MSG;
          }
        }
      } else {
        message = FdahpStudyDesignerConstants.ACTIVEANDQUESSIONAIREEMPTY_ERROR_MSG;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - validateActivityComplete() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateActivityComplete() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  public String validateDateForStudyAction(StudyBo studyBo, String buttonText) {
    boolean resourceFlag = true;
    boolean activitiesFalg = true;
    boolean questionarriesFlag = true;
    boolean notificationFlag = true;
    List<DynamicBean> dynamicList = null;
    List<DynamicFrequencyBean> dynamicFrequencyList = null;
    List<NotificationBO> notificationBOs = null;
    List<ResourceBO> resourceBOList = null;
    String searchQuery = "";
    Session session = null;
    String message = FdahpStudyDesignerConstants.SUCCESS;
    List<String> frequencyList = null;

    try {
      frequencyList =
          Arrays.asList(
              FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME,
              FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE);
      session = hibernateTemplate.getSessionFactory().openSession();

      if (!buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
        // getting based on custom start date resource list
        searchQuery =
            " FROM ResourceBO RBO WHERE RBO.studyId=:id AND RBO.status = 1 AND RBO.startDate IS NOT NULL ORDER BY RBO.createdOn DESC ";
        query = session.createQuery(searchQuery);
        query.setString("id", studyBo.getId());
        resourceBOList = query.list();
        if ((resourceBOList != null) && !resourceBOList.isEmpty()) {
          for (ResourceBO resourceBO : resourceBOList) {
            boolean flag = false;
            String currentDate = FdahpStudyDesignerUtil.getCurrentDate();
            if (currentDate.equalsIgnoreCase(resourceBO.getStartDate())) {
              flag = true;
            } else {
              flag =
                  FdahpStudyDesignerUtil.compareDateWithCurrentDateResource(
                      resourceBO.getStartDate(), "yyyy-MM-dd");
            }
            if (!flag) {
              resourceFlag = false;
              break;
            }
          }
        }
      }
      if (!resourceFlag) {
        message = FdahpStudyDesignerConstants.RESOURCE_DATE_ERROR_MSG;
        return message;
      } else {
        // getting activeTasks based on StudyId
        query =
            session.createQuery(
                "select new com.fdahpstudydesigner.bean.DynamicBean(a.frequencyDate, a.frequencyTime)"
                    + " from ActiveTaskFrequencyBo a,ActiveTaskBo ab"
                    + " where a.activeTaskId=ab.id"
                    + " and ab.active IS NOT NULL"
                    + " and ab.active=1"
                    + " and ab.studyId=:impValue"
                    + " and ab.frequency=:frquencyTime and a.isLaunchStudy=false"
                    + " and a.frequencyDate IS NOT NULL"
                    + " and a.frequencyTime IS NOT NULL"
                    + " and ab.shortTitle NOT IN (SELECT shortTitle from ActiveTaskBo WHERE active=1 AND live=1 AND customStudyId=:customStudyId )");
        query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
        query.setParameter("frquencyTime", FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME);
        query.setParameter("customStudyId", studyBo.getCustomStudyId());

        dynamicList = query.list();
        if ((dynamicList != null) && !dynamicList.isEmpty()) {
          // checking active task which have scheduled for One time
          // expired or not
          for (DynamicBean obj : dynamicList) {
            if ((obj.getDateTime() != null)
                && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
                    obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
              activitiesFalg = false;
              break;
            }
          }
        }

        query =
            session.createQuery(
                "select new com.fdahpstudydesigner.bean.DynamicBean(ab.activeTaskLifetimeStart, a.frequencyTime)"
                    + " from ActiveTaskFrequencyBo a,ActiveTaskBo ab"
                    + " where a.activeTaskId=ab.id"
                    + " and ab.active IS NOT NULL"
                    + " and ab.active=1"
                    + " and ab.studyId=:impValue"
                    + " and ab.frequency not in (:frequencyList)"
                    + " and ab.activeTaskLifetimeStart IS NOT NULL"
                    + " and a.frequencyTime IS NOT NULL"
                    + " and ab.shortTitle NOT IN (SELECT shortTitle from ActiveTaskBo WHERE active=1 AND live=1 AND customStudyId=:customStudyId)");
        query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
        query.setParameterList("frequencyList", frequencyList);
        query.setParameter("customStudyId", studyBo.getCustomStudyId());
        dynamicList = query.list();
        if ((dynamicList != null) && !dynamicList.isEmpty()) {
          // checking active task which have scheduled not in One
          // time,Manually Schedule expired or not
          for (DynamicBean obj : dynamicList) {
            if ((obj.getDateTime() != null)
                && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
                    obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
              activitiesFalg = false;
              break;
            }
          }
        }

        query =
            session.createQuery(
                "select new com.fdahpstudydesigner.bean.DynamicFrequencyBean(a.frequencyStartDate, a.frequencyStartTime)"
                    + " from ActiveTaskCustomScheduleBo a,ActiveTaskBo ab"
                    + " where a.activeTaskId=ab.id"
                    + " and ab.active IS NOT NULL"
                    + " and ab.active=1"
                    + " and ab.studyId=:impValue"
                    + " and ab.frequency=:manualSchedule and a.frequencyStartDate IS NOT NULL"
                    + " and a.frequencyStartTime IS NOT NULL"
                    + " and a.used=false");
        query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
        query.setParameter(
            "manualSchedule", FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE);
        dynamicFrequencyList = query.list();
        if ((dynamicFrequencyList != null) && !dynamicFrequencyList.isEmpty()) {
          for (DynamicFrequencyBean obj : dynamicFrequencyList) {
            // checking active task which have scheduled for
            // "Manually Schedule" expired or not
            if ((obj.getStartDate() != null) && (obj.getTime() != null)) {
              String dateTime = obj.getStartDate() + " " + obj.getTime();
              if (!FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
                  dateTime, "yyyy-MM-dd HH:mm")) {
                activitiesFalg = false;
                break;
              }
            }
          }
        }
      }
      if (!activitiesFalg) {
        message = FdahpStudyDesignerConstants.ACTIVETASK_DATE_ERROR_MSG;
        return message;
      } else {
        // getting Questionnaires based on StudyId
        query =
            session.createQuery(
                "select new com.fdahpstudydesigner.bean.DynamicBean(a.frequencyDate, a.frequencyTime)"
                    + " from QuestionnairesFrequenciesBo a,QuestionnaireBo ab"
                    + " where a.questionnairesId=ab.id"
                    + " and ab.active=1"
                    + " and ab.studyId=:impValue"
                    + " and ab.frequency=:frequencyTime and a.frequencyDate IS NOT NULL"
                    + " and a.frequencyTime IS NOT NULL"
                    + " and ab.shortTitle NOT IN(SELECT shortTitle from QuestionnaireBo WHERE active=1 AND live=1 AND customStudyId=:customStudyId)");
        query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
        query.setParameter("frequencyTime", FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME);
        query.setParameter("customStudyId", studyBo.getCustomStudyId());
        dynamicList = query.list();
        if ((dynamicList != null) && !dynamicList.isEmpty()) {
          for (DynamicBean obj : dynamicList) {
            // checking Questionnaires which have scheduled for one
            // time expired or not
            if ((obj.getDateTime() != null)
                && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
                    obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
              questionarriesFlag = false;
              break;
            }
          }
        }

        query =
            session.createQuery(
                "select new com.fdahpstudydesigner.bean.DynamicBean(ab.studyLifetimeStart, a.frequencyTime)"
                    + " from QuestionnairesFrequenciesBo a,QuestionnaireBo ab"
                    + " where a.questionnairesId=ab.id"
                    + " and ab.active=1"
                    + " and ab.studyId=:impValue"
                    + " and ab.frequency not in (:frequencyList)"
                    + " and ab.studyLifetimeStart IS NOT NULL"
                    + " and a.frequencyTime IS NOT NULL"
                    + " and ab.shortTitle NOT IN(SELECT shortTitle from QuestionnaireBo WHERE active=1 AND live=1 AND customStudyId= :customStudyId)");
        query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
        query.setParameterList("frequencyList", frequencyList);
        query.setParameter("customStudyId", studyBo.getCustomStudyId());
        dynamicList = query.list();
        if ((dynamicList != null) && !dynamicList.isEmpty()) {
          // checking Questionnaires which have scheduled not in one
          // time,manually schedule expired or not
          for (DynamicBean obj : dynamicList) {
            if ((obj.getDateTime() != null)
                && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
                    obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
              questionarriesFlag = false;
              break;
            }
          }
        }

        query =
            session.createQuery(
                "select new com.fdahpstudydesigner.bean.DynamicFrequencyBean(a.frequencyStartDate, a.frequencyStartTime)"
                    + " from QuestionnaireCustomScheduleBo a,QuestionnaireBo ab"
                    + " where a.questionnairesId=ab.id"
                    + " and ab.active=1"
                    + " and ab.studyId=:impValue"
                    + " and ab.frequency=:manualSchedule and a.frequencyStartDate IS NOT NULL"
                    + " and a.frequencyStartTime IS NOT NULL"
                    + " and a.used=false");
        query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
        query.setParameter(
            "manualSchedule", FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE);
        dynamicFrequencyList = query.list();
        if ((dynamicFrequencyList != null) && !dynamicFrequencyList.isEmpty()) {
          // checking Questionnaires which have scheduled
          // "manually schedule" expired or not
          for (DynamicFrequencyBean obj : dynamicFrequencyList) {
            if ((obj.getStartDate() != null) && (obj.getTime() != null)) {
              String dateTime = obj.getStartDate() + " " + obj.getTime();
              if (!FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
                  dateTime, "yyyy-MM-dd HH:mm")) {
                questionarriesFlag = false;
                break;
              }
            }
          }
        }
      }
      if (!questionarriesFlag) {
        message = FdahpStudyDesignerConstants.QUESTIONNARIES_ERROR_MSG;
        return message;
      } else {
        // getting based on start date notification list
        searchQuery =
            " FROM NotificationBO RBO WHERE RBO.studyId=:studyId"
                + " AND RBO.scheduleDate IS NOT NULL AND RBO.scheduleTime IS NOT NULL"
                + " AND RBO.notificationType='ST' AND RBO.notificationSubType='Announcement' AND RBO.notificationScheduleType='notImmediate' "
                + " AND RBO.notificationSent=0 AND RBO.notificationStatus=0 ";
        query = session.createQuery(searchQuery);
        query.setString("studyId", studyBo.getId());
        notificationBOs = query.list();
        if ((notificationBOs != null) && !notificationBOs.isEmpty()) {
          // checking notification expired or not
          for (NotificationBO notificationBO : notificationBOs) {
            if (!FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
                notificationBO.getScheduleDate() + " " + notificationBO.getScheduleTime(),
                "yyyy-MM-dd HH:mm:ss")) {
              notificationFlag = false;
              break;
            }
          }
        }
      }
      if (!notificationFlag
          && buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)) {
        message = FdahpStudyDesignerConstants.NOTIFICATION_ERROR_MSG;

        return message;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - updateStudyActionOnAction() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }

    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String validateEligibilityTestKey(
      String eligibilityTestId, String shortTitle, String eligibilityId) {
    logger.entry("begin getStudyVersionInfo()");
    Session session = null;
    List<EligibilityTestBo> eligibilityTestBos;
    String result = FdahpStudyDesignerConstants.FAILURE;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .getNamedQuery("EligibilityTestBo.validateShortTitle")
              .setString("shortTitle", shortTitle)
              .setString("eligibilityTestId", eligibilityTestId)
              .setString("eligibilityId", eligibilityId);
      eligibilityTestBos = query.list();
      if (eligibilityTestBos.isEmpty()) {
        result = FdahpStudyDesignerConstants.SUCCESS;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyVersionInfo() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyVersionInfo() - Ends");
    return result;
  }

  @Override
  public String validateStudyAction(String studyId, String buttonText) {
    logger.entry("begin validateStudyAction() - Ends");
    String message = FdahpStudyDesignerConstants.SUCCESS;
    Session session = null;
    StudyBo studyBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(buttonText) && StringUtils.isNotEmpty(studyId)) {

        studyBo =
            (StudyBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                    .setString("id", studyId)
                    .uniqueResult();

        if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)
            || buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
          // Date validation
          message = validateDateForStudyAction(studyBo, buttonText);
          return message;
        }
      } else {
        message = "Action is missing";
      }

    } catch (Exception e) {
      logger.error("StudyDAOImpl - validateStudyAction() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateStudyAction() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean validateStudyId(String customStudyId) {
    logger.entry("begin validateStudyId()");
    boolean flag = false;
    Session session = null;
    List<StudyBo> studyBos = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      studyBos =
          session
              .getNamedQuery("StudyBo.getStudyBycustomStudyId")
              .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId)
              .list();
      if ((studyBos != null) && !studyBos.isEmpty()) {
        flag = true;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - validateStudyId() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateStudyId() - Ends");
    return flag;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<EligibilityTestBo> viewEligibilityTestQusAnsByEligibilityId(String eligibilityId) {
    logger.entry("begin viewEligibilityTestQusAnsByEligibilityId");
    Session session = null;
    List<EligibilityTestBo> eligibilityTestList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      eligibilityTestList =
          session
              .getNamedQuery("EligibilityTestBo.findByEligibilityId")
              .setString("eligibilityId", eligibilityId)
              .list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - viewEligibilityTestQusAnsByEligibilityId - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("viewEligibilityTestQusAnsByEligibilityId - Ends");
    return eligibilityTestList;
  }

  @Override
  public EligibilityTestBo viewEligibilityTestQusAnsById(String eligibilityTestId) {
    logger.entry("begin viewEligibilityTestQusAnsById");
    Session session = null;
    EligibilityTestBo eligibilityTest = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      eligibilityTest =
          (EligibilityTestBo)
              session
                  .getNamedQuery("EligibilityTestBo.findById")
                  .setString("eligibilityTestId", eligibilityTestId)
                  .uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - viewEligibilityTestQusAnsById - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("viewEligibilityTestQusAnsById - Ends");
    return eligibilityTest;
  }

  @Override
  public Boolean isAnchorDateExistForEnrollment(String studyId, String customStudyId) {
    logger.entry("begin isAnchorDateExistForEnrollment");
    Session session = null;
    Boolean isExist = false;
    String searchQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      // checking in the questionnaire step anchor date is selected or not
      searchQuery =
          "select count(*) from questionnaires qr"
              + " where qr.anchor_date_id IS NOT NULL "
              + "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name=:enrollmentDate and t.custom_study_id=:customStudyId) "
              + "and qr.custom_study_id=:customStudyId and qr.active=1";
      BigInteger count =
          (BigInteger)
              session
                  .createSQLQuery(searchQuery)
                  .setString(
                      "enrollmentDate", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                  .setString("customStudyId", customStudyId)
                  .uniqueResult();
      if (count.intValue() > 0) {
        isExist = true;
      } else {
        // activetask target anchordate
        searchQuery =
            "select count(*) from active_task qr"
                + " where qr.anchor_date_id IS NOT NULL "
                + "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name=:enrollmentDate and t.custom_study_id=:customStudyId) "
                + "and qr.custom_study_id=:customStudyId and qr.active=1";
        BigInteger subCount =
            (BigInteger)
                session
                    .createSQLQuery(searchQuery)
                    .setString(
                        "enrollmentDate", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                    .setString("customStudyId", customStudyId)
                    .uniqueResult();
        if ((subCount != null) && (subCount.intValue() > 0)) {
          isExist = true;
        } else {
          searchQuery =
              "select count(*) from resources qr"
                  + " where qr.anchor_date_id IS NOT NULL "
                  + "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name=:enrollmentDate and t.custom_study_id=:customStudyId) "
                  + "and qr.custom_study_id=:customStudyId and qr.status=1";
          BigInteger sub1Count =
              (BigInteger)
                  session
                      .createSQLQuery(searchQuery)
                      .setString(
                          "enrollmentDate", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                      .setString("customStudyId", customStudyId)
                      .uniqueResult();
          if ((sub1Count != null) && (sub1Count.intValue() > 0)) {
            isExist = true;
          }
        }
      }
    } catch (Exception e) {

    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("isAnchorDateExistForEnrollment - Ends");
    return isExist;
  }

  @Override
  public Boolean isAnchorDateExistForEnrollmentDraftStudy(String studyId, String customStudyId) {
    logger.entry("begin isAnchorDateExistForEnrollmentDraftStudy");
    Session session = null;
    Boolean isExist = false;
    String searchQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      // checking in the questionnaire step anchor date is selected or not
      searchQuery =
          "select count(*) from questionnaires qr"
              + " where qr.anchor_date_id IS NOT NULL "
              + "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name=:enrollmentDate and t.custom_study_id=:customStudyId) "
              + "and qr.study_id=:studyId and qr.active=1";
      BigInteger count =
          (BigInteger)
              session
                  .createSQLQuery(searchQuery)
                  .setString(
                      "enrollmentDate", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                  .setString("customStudyId", customStudyId)
                  .setString("studyId", studyId)
                  .uniqueResult();
      if (count.intValue() > 0) {
        isExist = true;
      } else {
        // activetask target anchordate
        searchQuery =
            "select count(*) from active_task qr"
                + " where qr.anchor_date_id IS NOT NULL "
                + "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name=:enrollmentDate and t.custom_study_id=:customStudyId) "
                + "and qr.study_id=:studyId and qr.active=1";
        BigInteger subCount =
            (BigInteger)
                session
                    .createSQLQuery(searchQuery)
                    .setString(
                        "enrollmentDate", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                    .setString("customStudyId", customStudyId)
                    .setString("studyId", studyId)
                    .uniqueResult();
        if ((subCount != null) && (subCount.intValue() > 0)) {
          isExist = true;
        } else {
          searchQuery =
              "select count(*) from resources qr"
                  + " where qr.anchor_date_id IS NOT NULL "
                  + "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name=:enrollmentDate and t.custom_study_id=:customStudyId) "
                  + "and qr.study_id=:studyId and qr.status=1";
          BigInteger sub1Count =
              (BigInteger)
                  session
                      .createSQLQuery(searchQuery)
                      .setString(
                          "enrollmentDate", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                      .setString("customStudyId", customStudyId)
                      .setString("studyId", studyId)
                      .uniqueResult();
          if ((sub1Count != null) && (sub1Count.intValue() > 0)) {
            isExist = true;
          }
        }
      }
    } catch (Exception e) {

    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("isAnchorDateExistForEnrollmentDraftStudy - Ends");
    return isExist;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String updateAnchordateForEnrollmentDate(
      StudyBo oldStudy, StudyBo updatedStudy, Session session, Transaction transaction) {
    logger.entry("begin isAnchorDateExistForEnrollmentDraftStudy");
    Boolean isAnchorUsed = false;
    String searchQuery = "";
    String message = FdahpStudyDesignerConstants.FAILURE;
    List<Integer> anchorIds = new ArrayList<Integer>();
    List<Integer> anchorExistIds = new ArrayList<Integer>();

    try {
      if (oldStudy.isEnrollmentdateAsAnchordate() && !updatedStudy.isEnrollmentdateAsAnchordate()) {

        anchorIds =
            session
                .createSQLQuery(
                    "select t.id from anchordate_type t "
                        + " where t.name=:enrollmentDate and t.custom_study_id=:customStudyId ")
                .setString("enrollmentDate", FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE)
                .setString("customStudyId", oldStudy.getCustomStudyId())
                .list();
        if (!anchorIds.isEmpty() && (anchorIds.size() > 0)) {

          searchQuery =
              "select q.id from questionnaires q where q.schedule_type=:scheduleTypeDate and q.anchor_date_id in (:anchorIds)";
          anchorExistIds =
              session
                  .createSQLQuery(searchQuery)
                  .setString(
                      "scheduleTypeDate", FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE)
                  .setParameterList("anchorIds", anchorIds)
                  .list();
          if (!anchorExistIds.isEmpty() && (anchorExistIds.size() > 0)) {
            isAnchorUsed = true;
          } else {
            searchQuery =
                "select q.id from active_task q where q.schedule_type=:scheduleTypeDate and q.anchor_date_id in (:anchorIds)";
            anchorExistIds =
                session
                    .createSQLQuery(searchQuery)
                    .setString(
                        "scheduleTypeDate", FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE)
                    .setParameterList("anchorIds", anchorIds)
                    .list();
            if (!anchorExistIds.isEmpty() && (anchorExistIds.size() > 0)) {
              isAnchorUsed = true;
            } else {
              searchQuery = "select q.id from resources q where q.anchor_date_id in (:anchorIds)";
              anchorExistIds =
                  session
                      .createSQLQuery(searchQuery)
                      .setParameterList("anchorIds", anchorIds)
                      .list();
              if (!anchorExistIds.isEmpty() && (anchorExistIds.size() > 0)) {
                isAnchorUsed = true;
              }
            }
          }
          if (isAnchorUsed) {

            StudySequenceBo studySequence =
                (StudySequenceBo)
                    session
                        .getNamedQuery("getStudySequenceByStudyId")
                        .setString("studyId", oldStudy.getId())
                        .uniqueResult();
            if (studySequence != null) {
              int count1 =
                  session
                      .createSQLQuery(
                          "update questionnaires set status=0,anchor_date_id=null, "
                              + " modified_by=:modifiedBy , modified_date=:currentDateTime where active=1 and anchor_date_id in (:anchorIds)")
                      .setString("modifiedBy", updatedStudy.getModifiedBy())
                      .setString("currentDateTime", FdahpStudyDesignerUtil.getCurrentDateTime())
                      .setParameterList("anchorIds", anchorIds)
                      .executeUpdate();
              if (count1 > 0) {
                studySequence.setStudyExcQuestionnaries(false);

                auditLogDAO.updateDraftToEditedStatus(
                    session,
                    transaction,
                    updatedStudy.getModifiedBy(),
                    FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE,
                    oldStudy.getId());
              }
              int count2 =
                  session
                      .createSQLQuery(
                          "update active_task set action=0 ,anchor_date_id=null, modified_by=:modifiedBy,modified_date=:currentDateTime where active=1 and anchor_date_id in (:anchorIds) ")
                      .setString("modifiedBy", updatedStudy.getModifiedBy())
                      .setString("currentDateTime", FdahpStudyDesignerUtil.getCurrentDateTime())
                      .setParameterList("anchorIds", anchorIds)
                      .executeUpdate();
              if (count2 > 0) {
                studySequence.setStudyExcActiveTask(false);

                auditLogDAO.updateDraftToEditedStatus(
                    session,
                    transaction,
                    updatedStudy.getModifiedBy(),
                    FdahpStudyDesignerConstants.DRAFT_ACTIVETASK,
                    oldStudy.getId());
              }
              int count3 =
                  session
                      .createSQLQuery(
                          "update resources set action=0,anchor_date_id=null "
                              + " where status=1 and anchor_date_id in (:anchorIds)")
                      .setParameterList("anchorIds", anchorIds)
                      .executeUpdate();

              if (count3 > 0) {
                studySequence.setMiscellaneousResources(false);

                auditLogDAO.updateDraftToEditedStatus(
                    session,
                    transaction,
                    updatedStudy.getModifiedBy(),
                    FdahpStudyDesignerConstants.DRAFT_STUDY,
                    oldStudy.getId());
              }
              session.saveOrUpdate(studySequence);
              message = FdahpStudyDesignerConstants.SUCCESS;
            }
          }
        }
      }
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - isAnchorDateExistForEnrollmentDraftStudy - ERROR ", e);
    }
    logger.exit("isAnchorDateExistForEnrollmentDraftStudy - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean validateAppId(
      String customStudyId, String appId, String studyType, String dbCustomStudyId) {
    logger.entry("begin validateAppId()");
    boolean flag = false;
    Session session = null;
    List<StudyBo> studyBos = null;
    String searchQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (!studyType.isEmpty() && !appId.isEmpty()) {
        if (studyType.equalsIgnoreCase(FdahpStudyDesignerConstants.STUDY_TYPE_GT)) {
          if (StringUtils.isNotEmpty(customStudyId)) {
            searchQuery =
                "From StudyBo WHERE appId=:appId and type=:studyType and customStudyId!=:customStudyId ";
            studyBos =
                session
                    .createQuery(searchQuery)
                    .setString("appId", appId)
                    .setString("studyType", FdahpStudyDesignerConstants.STUDY_TYPE_SD)
                    .setString("customStudyId", customStudyId)
                    .list();
          } else {
            searchQuery = "From StudyBo WHERE appId=:appId and type=:studyType ";
            studyBos =
                session
                    .createQuery(searchQuery)
                    .setString("appId", appId)
                    .setString("studyType", FdahpStudyDesignerConstants.STUDY_TYPE_SD)
                    .list();
          }
        } else {
          if (StringUtils.isNotEmpty(dbCustomStudyId)) {
            searchQuery = " From StudyBo WHERE appId=:appId and customStudyId!=:customStudyId ";
            studyBos =
                session
                    .createQuery(searchQuery)
                    .setString("appId", appId)
                    .setString("customStudyId", dbCustomStudyId)
                    .list();
          } else if (StringUtils.isNotEmpty(customStudyId)) {
            searchQuery = " From StudyBo WHERE appId=:appId and customStudyId!=:customStudyId ";
            studyBos =
                session
                    .createQuery(searchQuery)
                    .setString("appId", appId)
                    .setString("customStudyId", customStudyId)
                    .list();
          } else {
            searchQuery = "From StudyBo WHERE appId=:appId ";
            studyBos = session.createQuery(searchQuery).setString("appId", appId).list();
          }
        }
      }

      if ((studyBos != null) && !studyBos.isEmpty()) {
        flag = true;
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - validateAppId() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateAppId() - Ends");
    return flag;
  }

  @Override
  public StudyPermissionBO getStudyPermissionBO(String studyId, String userId) {
    logger.entry("begin getStudyPermissionBO()");
    Session session = null;
    StudyPermissionBO studyPermissionBO = null;
    String searchQuery = "";
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      searchQuery = "From StudyPermissionBO WHERE studyId=:studyId and userId=:userId ";
      query = session.createQuery(searchQuery);
      query.setString("studyId", studyId);
      query.setString("userId", userId);
      studyPermissionBO = (StudyPermissionBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyPermissionBO() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyPermissionBO() - Ends");
    return studyPermissionBO;
  }

  @Override
  public StudyBo getStudyByLatestVersion(String customStudyId) {
    logger.entry("begin getStudyByLatestVersion()");
    Session session = null;
    StudyBo studyBo = null;
    String searchQuery = "";
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      searchQuery = "From StudyBo s WHERE s.customStudyId=:customStudyId ORDER BY s.version DESC";
      query =
          session
              .createQuery(searchQuery)
              .setString("customStudyId", customStudyId)
              .setMaxResults(1);
      studyBo = (StudyBo) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyByLatestVersion() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyByLatestVersion() - Ends");
    return studyBo;
  }

  @Override
  public String getStudyCategory(String id) {
    logger.entry("begin getStudyCategory()");
    Session session = null;
    String searchQuery = "";
    Query query = null;
    String studyCatagory = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      searchQuery = " SELECT r.value From ReferenceTablesBo r WHERE r.id=:id ";
      query = session.createQuery(searchQuery);
      query.setString("id", id);
      studyCatagory = (String) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyCategory() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyCategory() - Ends");
    return studyCatagory;
  }

  @Override
  public Integer getEligibilityType(String studyId) {
    logger.entry("begin getEligibilityType()");
    Session session = null;
    String searchQuery = "";
    Query query = null;
    Integer eligibilityType = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      searchQuery = "SELECT e.eligibilityMechanism From EligibilityBo e WHERE e.studyId=:studyId";
      query = session.createQuery(searchQuery);
      query.setString("studyId", studyId);
      eligibilityType = (Integer) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getEligibilityType() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getEligibilityType() - Ends");
    return eligibilityType;
  }

  @Override
  public StudyBo getStudy(String id) {
    logger.entry("begin getStudy()");
    Session session = null;
    StudyBo study = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      Query query = session.getNamedQuery("getStudy").setString("id", id);
      study = (StudyBo) query.uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudy() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudy() - Ends");
    return study;
  }

  public List<String> getStudyIdAsList(String statusId) {
    List<String> outputList = new ArrayList<>();
    if (!StringUtils.isEmpty(statusId)) {
      if (statusId.contains(",")) {
        String[] statusIdArray = statusId.split(",");
        for (int i = 0; i < statusIdArray.length; i++) {
          outputList.add(statusIdArray[i]);
        }
      } else {
        outputList.add(statusId);
      }
    }
    return outputList;
  }

  public List<String> convertAllStatusIdAsList(String input) {
    List<String> outputList = new ArrayList<>();
    if (!StringUtils.isEmpty(input)) {
      if (input.contains(",")) {
        String[] statusIdArray = input.split(",");
        for (int i = 0; i < statusIdArray.length; i++) {
          outputList.add(statusIdArray[i]);
        }
      } else {
        outputList.add(input);
      }
    }
    return outputList;
  }

  @Override
  public boolean validateStudyActions(String studyId) {
    logger.entry("begin validateStudyAction()");
    String message = FdahpStudyDesignerConstants.SUCCESS;
    Session session = null;
    StudySequenceBo studySequenceBo = null;
    StudyBo studyBo = null;
    boolean markedAsCompleted = false;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)) {
        studyBo =
            (StudyBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                    .setString("id", studyId)
                    .uniqueResult();
        studySequenceBo =
            (StudySequenceBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
                    .setString(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId())
                    .uniqueResult();

        // 1-all validation mark as completed
        if (studySequenceBo != null) {

          markedAsCompleted = getErrorForAction(studySequenceBo);
          if (markedAsCompleted) {
            return markedAsCompleted;
          } else {
            markedAsCompleted = false;
          }
        }

      } else {
        message = "Action is missing";
      }

    } catch (Exception e) {
      logger.error("StudyDAOImpl - validateStudyAction() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateStudyAction() - Ends");
    return markedAsCompleted;
  }

  public boolean getErrorForAction(StudySequenceBo studySequenceBo) {
    boolean completed = false;
    if (studySequenceBo != null) {
      if (studySequenceBo.isBasicInfo()
          && studySequenceBo.isSettingAdmins()
          && studySequenceBo.isOverView()
          && studySequenceBo.isEligibility()
          && studySequenceBo.isConsentEduInfo()
          && studySequenceBo.isComprehensionTest()
          && studySequenceBo.iseConsent()
          && studySequenceBo.isStudyExcQuestionnaries()
          && studySequenceBo.isStudyExcActiveTask()
          && studySequenceBo.isMiscellaneousResources()
          && studySequenceBo.isMiscellaneousNotification()) {
        completed = true;
        return completed;
      }
    }
    return completed;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ConsentBo> getConsentList(String customStudyId) {
    List<ConsentBo> consentBoList = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String searchQuery =
          " FROM ConsentBo CBO WHERE CBO.customStudyId=:customStudyId ORDER BY CBO.version desc ";
      query = session.createQuery(searchQuery);
      query.setString("customStudyId", customStudyId);
      consentBoList = query.list();
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - getConsentList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("StudyDAOImpl - getConsentList() - Ends");
    return consentBoList;
  }

  @Override
  public StudySequenceBo getStudySequenceByStudyId(String studyId) {

    logger.info("StudyDAOImpl - getStudy() - Starts");
    Session session = null;
    StudySequenceBo studySequence = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      studySequence =
          (StudySequenceBo)
              session
                  .getNamedQuery("getStudySequenceByStudyId")
                  .setString("studyId", studyId)
                  .uniqueResult();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudy() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - getStudy() - Ends");
    return studySequence;
  }

  @Override
  public List<AnchorDateTypeBo> getAnchorDateDetails(String studyId, String customStudyId) {
    logger.info("StudyDAOImpl - getStudy() - Starts");
    Session session = null;
    List<AnchorDateTypeBo> anchorDateTypeBoList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      String searchQuery = "From AnchorDateTypeBo where studyId=:studyId";
      anchorDateTypeBoList = session.createQuery(searchQuery).setString("studyId", studyId).list();
      if (CollectionUtils.isEmpty(anchorDateTypeBoList)) {
        searchQuery = "From AnchorDateTypeBo where customStudyId=:customStudyId";
        anchorDateTypeBoList =
            session.createQuery(searchQuery).setString("customStudyId", customStudyId).list();
      }

    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudy() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - getStudy() - Ends");
    return anchorDateTypeBoList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ComprehensionTestResponseBo> getComprehensionTestResponseList(
      List<String> comprehensionTestQuestionIds) {
    logger.info("StudyDAOImpl - getComprehensionTestResponseList() - Starts");
    Session session = null;
    List<ComprehensionTestResponseBo> comprehensionTestResponseList = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (CollectionUtils.isNotEmpty(comprehensionTestQuestionIds)) {
        query =
            session.createQuery(
                "From ComprehensionTestResponseBo CTRBO where CTRBO.comprehensionTestQuestionId in (:comprehensionTestQuestionIds)");
        query.setParameterList("comprehensionTestQuestionIds", comprehensionTestQuestionIds);
        comprehensionTestResponseList = query.list();
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getComprehensionTestResponseList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    return comprehensionTestResponseList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void cloneStudy(StudyBo studyBo, SessionObject sessionObject, String copyVersion) {
    logger.info("StudyDAOImpl - cloneStudy() - Starts");
    Session session = null;
    StudyPermissionBO studyPermissionBO = null;
    String studyId = null;
    String userId = null;
    List<String> userSuperAdminList = null;

    try {
      userId = sessionObject.getUserId();
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      String oldStudyId = studyBo.getId();
      studyBo.setId(null);
      studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH);
      studyBo.setStudylunchDate(null);
      studyBo.setAppId(null);
      studyBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      studyBo.setDestinationCustomStudyId(studyBo.getCustomStudyId() + "@COPY" + copyVersion);
      studyBo.setEnrollingParticipants(FdahpStudyDesignerConstants.YES);
      studyBo.setCustomStudyId(null);
      studyBo.setExportSignedUrl(null);
      studyBo.setName("Copy of " + studyBo.getName());
      studyBo.setLive(0);
      studyBo.setVersion(0f);
      studyId = (String) session.save(studyBo);

      studyPermissionBO = new StudyPermissionBO();
      studyPermissionBO.setUserId(userId);
      studyPermissionBO.setStudyId(studyId);
      studyPermissionBO.setViewPermission(true);
      session.save(studyPermissionBO);

      // give permission to all super admin Start
      query =
          session
              .createSQLQuery(
                  "Select upm.user_id from user_permission_mapping upm where upm.permission_id =:superAdminId")
              .setInteger("superAdminId", FdahpStudyDesignerConstants.ROLE_SUPERADMIN);
      userSuperAdminList = query.list();
      if ((userSuperAdminList != null) && !userSuperAdminList.isEmpty()) {
        for (String superAdminId : userSuperAdminList) {
          if ((null != userId) && !userId.equals(superAdminId)) {
            studyPermissionBO = new StudyPermissionBO();
            studyPermissionBO.setUserId(superAdminId);
            studyPermissionBO.setStudyId(studyId);
            studyPermissionBO.setViewPermission(true);
            session.save(studyPermissionBO);
          }
        }
      }

      /*  StudySequenceBo studySequenceBo = getStudySequenceByStudyId(oldStudyId);
      studySequenceBo.setStudySequenceId(null);
      studySequenceBo.setStudyId(studyId);
      studySequenceBo.setBasicInfo(false);
      session.save(studySequenceBo);*/

      StudySequenceBo studySequenceBo = new StudySequenceBo();
      studySequenceBo.setStudyId(studyId);
      session.save(studySequenceBo);

      List<StudyPageBo> studyPageList = getOverviewStudyPagesById(oldStudyId, studyBo.getUserId());
      if (CollectionUtils.isNotEmpty(studyPageList)) {
        for (StudyPageBo studyPageBo : studyPageList) {
          studyPageBo.setPageId(null);
          studyPageBo.setStudyId(studyBo.getId());
          studyPageBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          session.save(studyPageBo);
        }
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - cloneStudy() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - cloneStudy() - Ends");
  }

  @Override
  public void cloneEligibility(EligibilityBo eligibilityBo, String studyId) {
    logger.info("StudyDAOImpl - cloneEligibility() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      String oldEligibiltyId = eligibilityBo.getId();
      eligibilityBo.setId(null);
      eligibilityBo.setStudyId(studyId);
      eligibilityBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      session.save(eligibilityBo);

      List<EligibilityTestBo> eligibilityBoList =
          viewEligibilityTestQusAnsByEligibilityId(oldEligibiltyId);

      if (CollectionUtils.isNotEmpty(eligibilityBoList)) {
        for (EligibilityTestBo eligibilityTestBo : eligibilityBoList) {
          eligibilityTestBo.setId(null);
          eligibilityTestBo.setEligibilityId(eligibilityBo.getId());
          eligibilityTestBo.setUsed(false);
          session.save(eligibilityTestBo);
        }
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - cloneEligibility() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - cloneEligibility() - Ends");
  }

  @Override
  public void cloneComprehensionTest(
      ComprehensionTestQuestionBo comprehensionTestQuestionBo, String studyId) {
    logger.info("StudyDAOImpl - cloneComprehensionTest() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      String oldComprehensionTestQuestionBoId = comprehensionTestQuestionBo.getId();
      comprehensionTestQuestionBo.setId(null);
      comprehensionTestQuestionBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      session.save(comprehensionTestQuestionBo);

      List<ComprehensionTestResponseBo> comprehensionTestResponseBoList =
          getComprehensionTestResponseList(oldComprehensionTestQuestionBoId);

      if (CollectionUtils.isNotEmpty(comprehensionTestResponseBoList)) {
        for (ComprehensionTestResponseBo comprehensionTestResponseBo :
            comprehensionTestResponseBoList) {
          comprehensionTestResponseBo.setId(null);
          comprehensionTestResponseBo.setComprehensionTestQuestionId(
              comprehensionTestQuestionBo.getId());
          session.save(comprehensionTestResponseBo);
        }
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - cloneComprehensionTest() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - cloneComprehensionTest() - Ends");
  }

  @Override
  public void cloneConsent(ConsentBo consentBo, String studyId) {
    logger.info("StudyDAOImpl - cloneConsent() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      consentBo.setId(null);
      consentBo.setCustomStudyId(null);
      consentBo.setLive(0);
      consentBo.setStudyId(studyId);
      consentBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      consentBo.setVersion(0f);
      session.save(consentBo);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - cloneConsent() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - cloneConsent() - Ends");
  }

  @Override
  public void cloneConsentInfo(ConsentInfoBo consentInfoBo, String studyId) {

    logger.info("StudyDAOImpl - cloneConsentInfo() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      consentInfoBo.setId(null);
      consentInfoBo.setStudyId(studyId);
      consentInfoBo.setVersion(0f);
      consentInfoBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      session.save(consentInfoBo);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - cloneConsentInfo() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - cloneConsentInfo() - Ends");
  }

  @Override
  public void saveStudyActiveTask(ActiveTaskBo activeTaskBo) {
    logger.info("StudyDAOImpl - saveStudyActiveTask() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.save(activeTaskBo);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("StudyDAOImpl - saveStudyActiveTask() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - saveStudyActiveTask() - Ends");
  }

  @Override
  public void saveActiveTaskAtrributeValuesBo(
      ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo) {
    logger.info("StudyDAOImpl - saveActiveTaskAtrributeValuesBo() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.save(activeTaskAtrributeValuesBo);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("StudyDAOImpl - saveActiveTaskAtrributeValuesBo() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - saveActiveTaskAtrributeValuesBo() - Ends");
  }

  @Override
  public void saveActiveTaskCustomScheduleBo(
      ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo) {
    logger.info("StudyDAOImpl - saveActiveTaskCustomScheduleBo() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.save(activeTaskCustomScheduleBo);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("StudyDAOImpl - saveActiveTaskCustomScheduleBo() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - saveActiveTaskCustomScheduleBo() - Ends");
  }

  @Override
  public void saveActiveTaskFrequencyBo(ActiveTaskFrequencyBo activeTaskFrequencyBo) {
    logger.info("StudyDAOImpl - saveActiveTaskFrequencyBo() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.save(activeTaskFrequencyBo);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("StudyDAOImpl - saveActiveTaskFrequencyBo() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - saveActiveTaskFrequencyBo() - Ends");
  }

  @Override
  public String saveExportFilePath(String studyId, String destinationCustomId, String signedUrl) {
    logger.entry("begin saveExportFilePath()");
    Session session = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    StudyBo studyBo = null;

    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      if (StringUtils.isNotEmpty(studyId)) {
        studyBo =
            (StudyBo)
                session
                    .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                    .setString("id", studyId)
                    .uniqueResult();
      }

      if (studyBo != null) {
        studyBo.setDestinationCustomStudyId(destinationCustomId + "@Export");
        studyBo.setExportSignedUrl(signedUrl);
        studyBo.setExportTime(new Timestamp(Instant.now().toEpochMilli()));
        session.update(studyBo);
        message = FdahpStudyDesignerConstants.SUCCESS;
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - saveExportFilePath() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveExportFilePath() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  public void moveOrCopyCloudStorage(
      Session session,
      StudyBo studyBo,
      boolean delete,
      boolean oldFilePath,
      String newCustomStudyId) {
    if (studyBo.getThumbnailImage() != null) {
      FdahpStudyDesignerUtil.copyOrMoveImage(
          studyBo.getThumbnailImage(),
          FdahpStudyDesignerConstants.STUDTYLOGO,
          studyBo.getCustomStudyId(),
          delete,
          oldFilePath,
          newCustomStudyId);
    }

    List<QuestionnairesStepsBo> questionnaireStepsList =
        session
            .createQuery(
                "From QuestionnairesStepsBo where questionnairesId IN (SELECT q.id from QuestionnaireBo q where studyId=:studyId)")
            .setString("studyId", studyBo.getId())
            .list();
    List<String> questionIds = new ArrayList();
    for (QuestionnairesStepsBo questionnaireSteps : questionnaireStepsList) {
      if (questionnaireSteps.getStepType().equals("Form")) {
        List<String> questionIdList =
            session
                .createQuery("SELECT questionId FROM FormMappingBo where formId =:formId")
                .setString("formId", questionnaireSteps.getInstructionFormId())
                .list();
        questionIds.addAll(questionIdList);
      } else if (questionnaireSteps.getStepType().equals("Question")) {
        questionIds.add(questionnaireSteps.getInstructionFormId());
      }
    }
    if (!CollectionUtils.isEmpty(questionIds)) {

      List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
          session
              .createQuery(
                  "From QuestionResponseSubTypeBo WHERE responseTypeId IN (:responseTypeId)")
              .setParameterList("responseTypeId", questionIds)
              .list();

      for (QuestionResponseSubTypeBo questionResponseSubType : questionResponseSubTypeList) {

        if (questionResponseSubType.getSelectedImage() != null) {
          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseSubType.getSelectedImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              studyBo.getCustomStudyId(),
              delete,
              oldFilePath,
              newCustomStudyId);
        }

        if (questionResponseSubType.getImage() != null) {
          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseSubType.getImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              studyBo.getCustomStudyId(),
              delete,
              oldFilePath,
              newCustomStudyId);
        }
      }

      List<QuestionReponseTypeBo> questionResponseTypeList =
          session
              .createQuery(
                  "From QuestionReponseTypeBo WHERE questionsResponseTypeId IN (:responseTypeId)")
              .setParameterList("responseTypeId", questionIds)
              .list();

      for (QuestionReponseTypeBo questionResponseType : questionResponseTypeList) {
        if (questionResponseType.getMinImage() != null) {
          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseType.getMinImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              studyBo.getCustomStudyId(),
              delete,
              oldFilePath,
              newCustomStudyId);
        }

        if (questionResponseType.getMaxImage() != null) {

          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseType.getMaxImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              studyBo.getCustomStudyId(),
              delete,
              oldFilePath,
              newCustomStudyId);
        }
      }
    }
    List<StudyPageBo> studyPageBoList =
        session
            .createQuery("from StudyPageBo where studyId=:studyId")
            .setString("studyId", studyBo.getId())
            .list();

    for (StudyPageBo studyPageBo : studyPageBoList) {

      if (studyPageBo.getImagePath() != null) {
        FdahpStudyDesignerUtil.copyOrMoveImage(
            studyPageBo.getImagePath(),
            FdahpStudyDesignerConstants.STUDTYPAGES,
            studyBo.getCustomStudyId(),
            delete,
            oldFilePath,
            newCustomStudyId);
      }
    }

    List<ResourceBO> resourceBoList =
        session
            .createQuery("from ResourceBO where studyId=:studyId")
            .setString("studyId", studyBo.getId())
            .list();

    for (ResourceBO resourceBo : resourceBoList) {

      if (resourceBo.getPdfUrl() != null) {
        FdahpStudyDesignerUtil.copyOrMoveImage(
            resourceBo.getPdfUrl(),
            FdahpStudyDesignerConstants.RESOURCEPDFFILES,
            studyBo.getCustomStudyId(),
            delete,
            oldFilePath,
            newCustomStudyId);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void moveOrCopyCloudStorageForExportStudy(
      Session session,
      StudyBo studyBo,
      boolean delete,
      boolean oldFilePath,
      String newCustomStudyId,
      String oldCustomStudyId) {
    if (studyBo.getThumbnailImage() != null) {
      FdahpStudyDesignerUtil.copyOrMoveImage(
          studyBo.getThumbnailImage(),
          FdahpStudyDesignerConstants.STUDTYLOGO,
          oldCustomStudyId,
          delete,
          oldFilePath,
          newCustomStudyId);
    }

    List<QuestionnairesStepsBo> questionnaireStepsList =
        session
            .createQuery(
                "From QuestionnairesStepsBo where questionnairesId IN (SELECT q.id from QuestionnaireBo q where studyId=:studyId)")
            .setString("studyId", studyBo.getId())
            .list();
    List<String> questionIds = new ArrayList();
    for (QuestionnairesStepsBo questionnaireSteps : questionnaireStepsList) {
      if (questionnaireSteps.getStepType().equals("Form")) {
        List<String> questionIdList =
            session
                .createQuery("SELECT questionId FROM FormMappingBo where formId =:formId")
                .setString("formId", questionnaireSteps.getInstructionFormId())
                .list();
        questionIds.addAll(questionIdList);
      } else if (questionnaireSteps.getStepType().equals("Question")) {
        questionIds.add(questionnaireSteps.getInstructionFormId());
      }
    }
    if (!CollectionUtils.isEmpty(questionIds)) {

      List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
          session
              .createQuery(
                  "From QuestionResponseSubTypeBo WHERE responseTypeId IN (:responseTypeId)")
              .setParameterList("responseTypeId", questionIds)
              .list();

      for (QuestionResponseSubTypeBo questionResponseSubType : questionResponseSubTypeList) {

        if (questionResponseSubType.getSelectedImage() != null) {
          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseSubType.getSelectedImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              oldCustomStudyId,
              delete,
              oldFilePath,
              newCustomStudyId);
        }

        if (questionResponseSubType.getImage() != null) {
          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseSubType.getImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              oldCustomStudyId,
              delete,
              oldFilePath,
              newCustomStudyId);
        }
      }

      List<QuestionReponseTypeBo> questionResponseTypeList =
          session
              .createQuery(
                  "From QuestionReponseTypeBo WHERE questionsResponseTypeId IN (:responseTypeId)")
              .setParameterList("responseTypeId", questionIds)
              .list();

      for (QuestionReponseTypeBo questionResponseType : questionResponseTypeList) {
        if (questionResponseType.getMinImage() != null) {
          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseType.getMinImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              oldCustomStudyId,
              delete,
              oldFilePath,
              newCustomStudyId);
        }

        if (questionResponseType.getMaxImage() != null) {

          FdahpStudyDesignerUtil.copyOrMoveImage(
              questionResponseType.getMaxImage(),
              FdahpStudyDesignerConstants.QUESTIONNAIRE,
              oldCustomStudyId,
              delete,
              oldFilePath,
              newCustomStudyId);
        }
      }
    }
    List<StudyPageBo> studyPageBoList =
        session
            .createQuery("from StudyPageBo where studyId=:studyId")
            .setString("studyId", studyBo.getId())
            .list();

    for (StudyPageBo studyPageBo : studyPageBoList) {

      if (studyPageBo.getImagePath() != null) {
        FdahpStudyDesignerUtil.copyOrMoveImage(
            studyPageBo.getImagePath(),
            FdahpStudyDesignerConstants.STUDTYPAGES,
            oldCustomStudyId,
            delete,
            oldFilePath,
            newCustomStudyId);
      }
    }

    List<ResourceBO> resourceBoList =
        session
            .createQuery("from ResourceBO where studyId=:studyId")
            .setString("studyId", studyBo.getId())
            .list();

    for (ResourceBO resourceBo : resourceBoList) {

      if (resourceBo.getPdfUrl() != null) {
        FdahpStudyDesignerUtil.copyOrMoveImage(
            resourceBo.getPdfUrl(),
            FdahpStudyDesignerConstants.RESOURCEPDFFILES,
            oldCustomStudyId,
            delete,
            oldFilePath,
            newCustomStudyId);
      }
    }
  }

  @Override
  public String cloneAnchorDateBo(
      AnchorDateTypeBo anchorDateTypeBo, String studyId, Map<String, String> anchorDateMap) {
    logger.info("StudyDAOImpl - cloneAnchorDateBo() - Starts");
    Session session = null;
    String anchorDateTypeId = "";
    try {
      String oldAnchorDateId = anchorDateTypeBo.getId();
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      anchorDateTypeBo.setId(null);
      anchorDateTypeBo.setStudyId(studyId);
      anchorDateTypeBo.setCustomStudyId(null);
      anchorDateTypeId = (String) session.save(anchorDateTypeBo);
      anchorDateMap.put(oldAnchorDateId, anchorDateTypeId);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - cloneAnchorDateBo() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyDAOImpl - cloneAnchorDateBo() - Ends");
    return anchorDateTypeId;
  }

  @Override
  public List<ConsentBo> getConsentListForStudy(
      String studyId, String customStudyId, String copyVersion) {
    List<ConsentBo> consentBoList = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (copyVersion.equals(FdahpStudyDesignerConstants.WORKING_VERSION)) {
        String searchQuery =
            " FROM ConsentBo CBO WHERE CBO.studyId=:studyId ORDER BY CBO.version desc ";
        query = session.createQuery(searchQuery).setString("studyId", studyId);
      } else {
        String searchQuery =
            " FROM ConsentBo CBO WHERE CBO.customStudyId=:customStudyId AND CBO.version IN "
                + " (SELECT MAX(version) FROM ConsentBo WHERE customStudyId=:customStudyId) ";
        query = session.createQuery(searchQuery).setString("customStudyId", customStudyId);
      }
      consentBoList = query.list();
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - getConsentList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getConsentListForStudy() - Ends");
    return consentBoList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void giveStudyPermission(String studyId, String userId) {
    logger.info("StudyDAOImpl - giveStudyPermission() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      StudyPermissionBO studyPermissionBO = new StudyPermissionBO();
      studyPermissionBO.setUserId(userId);
      studyPermissionBO.setStudyId(studyId);
      studyPermissionBO.setViewPermission(true);
      session.save(studyPermissionBO);

      // give permission to all super admin Start
      query =
          session
              .createSQLQuery(
                  "Select upm.user_id from user_permission_mapping upm where upm.permission_id =:superAdminId")
              .setInteger("superAdminId", FdahpStudyDesignerConstants.ROLE_SUPERADMIN);
      List<String> userSuperAdminList = query.list();
      if ((userSuperAdminList != null) && !userSuperAdminList.isEmpty()) {
        for (String superAdminId : userSuperAdminList) {
          if ((null != userId) && !userId.equals(superAdminId)) {
            studyPermissionBO = new StudyPermissionBO();
            studyPermissionBO.setUserId(superAdminId);
            studyPermissionBO.setStudyId(studyId);
            studyPermissionBO.setViewPermission(true);
            session.save(studyPermissionBO);
          }
        }
      }

      StudyBo studyBo =
          (StudyBo)
              session
                  .getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
                  .setString("id", studyId)
                  .uniqueResult();
      studyBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      studyBo.setCreatedBy(userId);
      session.save(studyBo);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - giveStudyPermission() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("StudyDAOImpl - giveStudyPermission() - Ends");
  }

  @Override
  public List<ComprehensionTestResponseBo> getComprehensionTestResponses(
      String comprehensionTestQuestionId) {
    logger.info("StudyDAOImpl - getComprehensionTestResponseList() - Starts");
    Session session = null;
    List<ComprehensionTestResponseBo> comprehensionTestResponseList = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(comprehensionTestQuestionId)) {
        query =
            session.createQuery(
                "From ComprehensionTestResponseBo CTRBO where CTRBO.comprehensionTestQuestionId=:comprehensionTestQuestionId order by CTRBO.sequenceNumber");
        query.setString("comprehensionTestQuestionId", comprehensionTestQuestionId);
        comprehensionTestResponseList = query.list();
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getComprehensionTestResponseList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    return comprehensionTestResponseList;
  }

  @SuppressWarnings("unchecked")
  public void getResourcesFromStorage(Session session, StudyBo studyBo) throws Exception {
    ServletContext context = ServletContextHolder.getServletContext();

    if (context != null) {

      writeSqlFileToLocalExport(studyBo, context);

      if (studyBo.getThumbnailImage() != null) {

        File directoryImage =
            new File(
                context.getRealPath("/")
                    + EXPORT
                    + studyBo.getCustomStudyId()
                    + "/"
                    + FdahpStudyDesignerConstants.STUDTYLOGO);
        if (!directoryImage.exists()) {
          directoryImage.mkdir();
        }

        writeToFileExport(
            studyBo.getCustomStudyId(),
            studyBo.getThumbnailImage(),
            context,
            FdahpStudyDesignerConstants.STUDTYLOGO);
      }

      List<QuestionnairesStepsBo> questionnaireStepsList =
          session
              .createQuery(
                  "From QuestionnairesStepsBo where questionnairesId IN (SELECT q.id from QuestionnaireBo q where studyId=:studyId)")
              .setString("studyId", studyBo.getId())
              .list();
      List<String> questionIds = new ArrayList();
      for (QuestionnairesStepsBo questionnaireSteps : questionnaireStepsList) {
        if (questionnaireSteps.getStepType().equals("Form")) {
          List<String> questionIdList =
              session
                  .createQuery("SELECT questionId FROM FormMappingBo where formId =:formId")
                  .setString("formId", questionnaireSteps.getInstructionFormId())
                  .list();
          questionIds.addAll(questionIdList);
        } else if (questionnaireSteps.getStepType().equals("Question")) {
          questionIds.add(questionnaireSteps.getInstructionFormId());
        }
      }
      if (!CollectionUtils.isEmpty(questionIds)) {

        List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
            session
                .createQuery(
                    "From QuestionResponseSubTypeBo WHERE responseTypeId IN (:responseTypeId)")
                .setParameterList("responseTypeId", questionIds)
                .list();

        for (QuestionResponseSubTypeBo questionResponseSubType : questionResponseSubTypeList) {

          if (questionResponseSubType.getSelectedImage() != null) {
            File directoryImage =
                new File(
                    context.getRealPath("/")
                        + EXPORT
                        + studyBo.getCustomStudyId()
                        + "/"
                        + FdahpStudyDesignerConstants.QUESTIONNAIRE);
            if (!directoryImage.exists()) {
              directoryImage.mkdir();
            }

            writeToFileExport(
                studyBo.getCustomStudyId(),
                questionResponseSubType.getSelectedImage(),
                context,
                FdahpStudyDesignerConstants.QUESTIONNAIRE);
          }

          if (questionResponseSubType.getImage() != null) {
            File directoryImage =
                new File(
                    context.getRealPath("/")
                        + EXPORT
                        + studyBo.getCustomStudyId()
                        + "/"
                        + FdahpStudyDesignerConstants.QUESTIONNAIRE);
            if (!directoryImage.exists()) {
              directoryImage.mkdir();
            }

            writeToFileExport(
                studyBo.getCustomStudyId(),
                questionResponseSubType.getImage(),
                context,
                FdahpStudyDesignerConstants.QUESTIONNAIRE);
          }
        }

        List<QuestionReponseTypeBo> questionResponseTypeList =
            session
                .createQuery(
                    "From QuestionReponseTypeBo WHERE questionsResponseTypeId IN (:responseTypeId)")
                .setParameterList("responseTypeId", questionIds)
                .list();

        for (QuestionReponseTypeBo questionResponseType : questionResponseTypeList) {
          if (questionResponseType.getMinImage() != null) {
            File directoryImage =
                new File(
                    context.getRealPath("/")
                        + EXPORT
                        + studyBo.getCustomStudyId()
                        + "/"
                        + FdahpStudyDesignerConstants.QUESTIONNAIRE);
            if (!directoryImage.exists()) {
              directoryImage.mkdir();
            }

            writeToFileExport(
                studyBo.getCustomStudyId(),
                questionResponseType.getMinImage(),
                context,
                FdahpStudyDesignerConstants.QUESTIONNAIRE);
          }

          if (questionResponseType.getMaxImage() != null) {
            File directoryImage =
                new File(
                    context.getRealPath("/")
                        + EXPORT
                        + studyBo.getCustomStudyId()
                        + "/"
                        + FdahpStudyDesignerConstants.QUESTIONNAIRE);
            if (!directoryImage.exists()) {
              directoryImage.mkdir();
            }

            writeToFileExport(
                studyBo.getCustomStudyId(),
                questionResponseType.getMaxImage(),
                context,
                FdahpStudyDesignerConstants.QUESTIONNAIRE);
          }
        }
      }
      List<StudyPageBo> studyPageBoList =
          session
              .createQuery("from StudyPageBo where studyId=:studyId")
              .setString("studyId", studyBo.getId())
              .list();

      for (StudyPageBo studyPageBo : studyPageBoList) {

        if (studyPageBo.getImagePath() != null) {
          File directoryImage =
              new File(
                  context.getRealPath("/")
                      + EXPORT
                      + studyBo.getCustomStudyId()
                      + "/"
                      + FdahpStudyDesignerConstants.STUDTYPAGES);
          if (!directoryImage.exists()) {
            directoryImage.mkdir();
          }

          writeToFileExport(
              studyBo.getCustomStudyId(),
              studyPageBo.getImagePath(),
              context,
              FdahpStudyDesignerConstants.STUDTYPAGES);
        }
      }

      List<ResourceBO> resourceBoList =
          session
              .createQuery("from ResourceBO where studyId=:studyId")
              .setString("studyId", studyBo.getId())
              .list();

      for (ResourceBO resourceBo : resourceBoList) {

        if (resourceBo.getPdfUrl() != null) {
          File directoryImage =
              new File(
                  context.getRealPath("/")
                      + EXPORT
                      + studyBo.getCustomStudyId()
                      + "/"
                      + FdahpStudyDesignerConstants.RESOURCEPDFFILES);
          if (!directoryImage.exists()) {
            directoryImage.mkdir();
          }

          writeToFileExport(
              studyBo.getCustomStudyId(),
              resourceBo.getPdfUrl(),
              context,
              FdahpStudyDesignerConstants.RESOURCEPDFFILES);
        }
      }

      FileOutputStream fos =
          new FileOutputStream(
              context.getRealPath("/") + EXPORT + studyBo.getCustomStudyId() + ".zip");

      ZipOutputStream zos = new ZipOutputStream(fos);
      addDirToZipArchive(
          zos, new File(context.getRealPath("/") + EXPORT + studyBo.getCustomStudyId()), null);
      zos.flush();
      fos.flush();
      zos.close();
      fos.close();

      removeDir(new File(context.getRealPath("/") + EXPORT + studyBo.getCustomStudyId()));

      FdahpStudyDesignerUtil.uplaodZip(
          context.getRealPath("/") + EXPORT + studyBo.getCustomStudyId() + ".zip",
          studyBo.getCustomStudyId());
    }
  }

  public void writeSqlFileToLocalExport(StudyBo studyBo, ServletContext context)
      throws FileNotFoundException, IOException {
    Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();
    byte[] exportSqlBytes = studyBo.getExportSqlByte();
    String fileName =
        studyBo.getId()
            + "_"
            + map.get("release.version")
            + "_"
            + studyExportImportService.getCRC32Checksum(exportSqlBytes)
            + ".sql";

    File directoryOfExport = new File(context.getRealPath("/") + "/Export");
    if (!directoryOfExport.exists()) {
      directoryOfExport.mkdir();
    }
    File directory = new File(context.getRealPath("/") + EXPORT + studyBo.getCustomStudyId());
    if (!directory.exists()) {
      directory.mkdir();
    }

    File sqlFile =
        new File(context.getRealPath("/") + EXPORT + studyBo.getCustomStudyId() + "/" + fileName);
    FileOutputStream fosExportSql = new FileOutputStream(sqlFile);
    fosExportSql.write(exportSqlBytes);
    fosExportSql.close();
  }

  public void writeToFileExport(
      String customId, String fileName, ServletContext context, String underDirectory)
      throws FileNotFoundException, IOException {
    byte[] studyImageArray =
        FdahpStudyDesignerUtil.getResource(
            FdahpStudyDesignerConstants.STUDIES
                + "/"
                + customId
                + "/"
                + underDirectory
                + "/"
                + fileName);

    if (studyImageArray != null) {
      File convFile =
          new File(
              context.getRealPath("/") + EXPORT + customId + "/" + underDirectory + "/" + fileName);
      FileOutputStream fos = new FileOutputStream(convFile);
      fos.write(studyImageArray);
      fos.close();
    }
  }

  public static void removeDir(File dir) {
    try {

      if (dir.isDirectory()) {
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
          for (File aFile : files) {
            System.gc();
            Thread.sleep(2000);
            FileDeleteStrategy.FORCE.delete(aFile);
          }
        }
        dir.delete();
      } else {
        dir.delete();
      }
    } catch (Exception e) {
      logger.error("removeDir failed", e);
    }
  }

  public static void addDirToZipArchive(
      ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
    if (fileToZip == null || !fileToZip.exists()) {
      return;
    }

    String zipEntryName = fileToZip.getName();
    if (parrentDirectoryName != null && !parrentDirectoryName.isEmpty()) {
      zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
    }

    if (fileToZip.isDirectory()) {
      for (File file : fileToZip.listFiles()) {
        addDirToZipArchive(zos, file, zipEntryName);
      }
    } else {
      byte[] buffer = new byte[1024];
      FileInputStream fis = new FileInputStream(fileToZip);
      zos.putNextEntry(new ZipEntry(zipEntryName));
      int length;
      while ((length = fis.read(buffer)) > 0) {
        zos.write(buffer, 0, length);
      }
      zos.closeEntry();
      fis.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ConsentInfoBo> getConsentInfoList(
      String studyId, String customStudyId, String copyVersion) {
    logger.entry("begin getConsentInfoList()");
    List<ConsentInfoBo> consentInfoList = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)
          && copyVersion.equals(FdahpStudyDesignerConstants.WORKING_VERSION)) {
        String searchQuery =
            "From ConsentInfoBo CIB where CIB.studyId=:studyId and CIB.active=1 order by CIB.sequenceNo asc";
        query = session.createQuery(searchQuery);
        query.setString("studyId", studyId);
      } else {
        String searchQuery =
            "From ConsentInfoBo CIB where CIB.customStudyId=:customStudyId and CIB.active=1 AND CIB.version IN "
                + "(SELECT MAX(version) FROM ConsentInfoBo WHERE customStudyId=:customStudyId) order by CIB.sequenceNo asc";
        query = session.createQuery(searchQuery);
        query.setString("customStudyId", customStudyId);
      }
      consentInfoList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getConsentInfoList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getConsentInfoList() - Ends");
    return consentInfoList;
  }

  @Override
  public String deleteById(String studyId, AuditLogEventRequest auditRequest) {
    logger.entry("begin studydeleteById()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Query query = null;
    Session session = null;
    try {
      StudyBo studyBo = getStudy(studyId);
      if (studyBo != null && studyBo.getCustomStudyId() != null) {
        auditRequest.setStudyId(studyBo.getCustomStudyId());
        if (studyBo.getAppId() != null) {
          auditRequest.setAppId(studyBo.getAppId());
        }
      }
      session = hibernateTemplate.getSessionFactory().openSession();
      Transaction transaction = session.beginTransaction();

      query =
          session
              .createSQLQuery(" delete FROM study_page WHERE study_id=:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(" DELETE  from study_permission WHERE study_id=:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(" DELETE  FROM consent WHERE study_id=:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(
                  " delete FROM active_task_custom_frequencies WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in (SELECT id FROM studies WHERE id=:studyId))")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(
                  " delete FROM active_task_attrtibutes_values WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in (SELECT id FROM studies WHERE id=:studyId) )")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      //  active_task_frequencies
      query =
          session
              .createSQLQuery(
                  " delete  FROM active_task_frequencies WHERE active_task_id IN (SELECT id FROM active_task WHERE study_id in (SELECT id FROM studies WHERE id=:studyId))")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(" delete FROM active_task WHERE study_id=:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(
                  "delete FROM questionnaires_frequencies WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE id=:studyId))")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(
                  " DELETE  FROM questionnaires_steps WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in (SELECT id FROM studies WHERE id=:studyId))")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(" delete from questionnaires WHERE study_id=:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery("DELETE FROM consent_info WHERE study_id=:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(
                  "delete from comprehension_test_response  where comprehension_test_question_id in(select id from comprehension_test_question  where study_id in (SELECT id FROM studies WHERE id=:studyId))")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery("delete from comprehension_test_question WHERE study_id=:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery(
                  "delete  from eligibility_test  where eligibility_id in(select id from eligibility where study_id in (SELECT id FROM studies WHERE id=:studyId))")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery("delete from eligibility  WHERE study_id =:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();

      query =
          session
              .createSQLQuery(
                  "delete FROM resources WHERE study_id in (SELECT id FROM studies WHERE id=:studyId)")
              .setParameter("studyId", studyId);
      query.executeUpdate();
      query =
          session
              .createSQLQuery("delete from studies  WHERE id =:studyId")
              .setParameter("studyId", studyId);
      query.executeUpdate();

      transaction.commit();
      message = FdahpStudyDesignerConstants.SUCCESS;
    } catch (Exception e) {
      if (null != transaction) {
        transaction.rollback();
      }
      logger.error("StudyDAOImpl - deleteStudyById() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("deleteStudyById() - Ends");
    return message;
  }

  @Override
  public void processToFHIR(String id, String studyId, String buttonText) throws Exception {

    logger.entry("processToFHIR start: ");
    Session session = hibernateTemplate.getSessionFactory().openSession();
    transaction = session.beginTransaction();
    StudyBo study =
        (StudyBo)
            session
                .getNamedQuery("getStudyLiveVersion")
                .setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyId)
                .uniqueResult();
    List<QuestionnaireBo> questionnaires =
        studyQuestionnaireService.getStudyQuestionnairesByStudyId(studyId, true);

    List<ActiveTaskBo> activeTasks =
        studyActiveTasksService.getStudyActiveTasksByStudyId(studyId, true);

    logger.debug("processToFHIR questionnaires activeTasks list fetched sucessfully");
    fhirStatusForDeletedQuestionnaires(id, studyId, questionnaires, session, buttonText);
    fhirStatusForDeletedActiveTask(id, studyId, activeTasks, session, buttonText);

    formatToQuestionnaireType(studyId, session, study, questionnaires);

    formatToActiveTaskFhir(studyId, session, study, activeTasks);

    logger.entry("processToFHIR Ends: ");
  }

  private void fhirStatusForDeletedActiveTask(
      String studyId,
      String customStudyId,
      List<ActiveTaskBo> activeTasks,
      Session session,
      String buttonText)
      throws GoogleJsonResponseException {

    String resourceId = null;
    String didResourceId = null;

    String datasetPathforFHIR = String.format(DATASET_PATH, projectId, regionId, customStudyId);
    List<ActiveTaskBo> deletedActiveTasks = null;
    List<ActiveTaskBo> activeTasksDeleted = null;
    List<String> liveQuestionnaireIdList = new ArrayList<>();
    for (ActiveTaskBo activeTaskBo : activeTasks) {
      String liveQuestionnaire = activeTaskBo.getShortTitle();
      liveQuestionnaireIdList.add(liveQuestionnaire);
    }
    String searchQuery = "";
    if (liveQuestionnaireIdList.isEmpty()) {
      searchQuery =
          "From ActiveTaskBo ABO WHERE ABO.studyId =:studyId " + " and ABO.active=0 and ABO.live=0";
      query = session.createQuery(searchQuery).setString("studyId", studyId);
    } else {
      searchQuery =
          "From ActiveTaskBo ABO WHERE ABO.studyId =:studyId "
              + " and ABO.active=0 and ABO.live=0 AND ABO.shortTitle NOT IN (SELECT ab.shortTitle from ActiveTaskBo ab WHERE ab.active=1 AND ab.live=1 AND ab.customStudyId=:customStudyId )";
      query =
          session
              .createQuery(searchQuery)
              .setString("studyId", studyId)
              .setString("customStudyId", customStudyId);
    }

    deletedActiveTasks = query.list();

    if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_DEACTIVATE)) {
      activeTasksDeleted = activeTasks;
    } else {
      activeTasksDeleted = deletedActiveTasks;
    }

    for (ActiveTaskBo deletedTask : activeTasksDeleted) {
      String searchQuestionnaireJson =
          fhirHealthcareAPIs.fhirResourceSearchPost(
              datasetPathforFHIR
                  + FHIR_STORES
                  + "FHIR_"
                  + customStudyId
                  + "/fhir/"
                  + QUESTIONNAIRE_TYPE,
              "identifier=" + deletedTask.getShortTitle());
      SearchQuestionnaireFhirBean searchQuestionFhirResponseBean =
          new Gson().fromJson(searchQuestionnaireJson, SearchQuestionnaireFhirBean.class);

      if (fhirEnabled.contains("did")) {
        String searchDidQuestionnaireJson =
            fhirHealthcareAPIs.fhirResourceSearchPost(
                datasetPathforFHIR
                    + FHIR_STORES
                    + "DID_"
                    + customStudyId
                    + "/fhir/"
                    + QUESTIONNAIRE_TYPE,
                "identifier=" + deletedTask.getShortTitle());
        SearchQuestionnaireFhirBean searchQuestionDidResponseBean =
            new Gson().fromJson(searchDidQuestionnaireJson, SearchQuestionnaireFhirBean.class);
        if (searchQuestionDidResponseBean != null && searchQuestionDidResponseBean.getTotal() > 0) {
          didResourceId =
              String.valueOf(searchQuestionDidResponseBean.getEntry().get(0).getResource().getId());
        }
      }

      if (searchQuestionFhirResponseBean != null && searchQuestionFhirResponseBean.getTotal() > 0) {
        resourceId =
            String.valueOf(searchQuestionFhirResponseBean.getEntry().get(0).getResource().getId());

        String statusOfQuestionnaire =
            String.valueOf(
                searchQuestionFhirResponseBean.getEntry().get(0).getResource().getStatus());
        if (!statusOfQuestionnaire.equals("retired")) {
          final String RESOURCE_NAME =
              datasetPathforFHIR
                  + FHIR_STORES
                  + "FHIR_"
                  + customStudyId
                  + "/fhir/"
                  + QUESTIONNAIRE_TYPE
                  + "/"
                  + resourceId;
          String data = "[{\"op\": \"replace\", \"path\": \"/status\", \"value\": \"retired\"}]";
          fhirHealthcareAPIs.fhirResourcePatch(RESOURCE_NAME, data);

          if (fhirEnabled.contains("did") && null != didResourceId) {
            final String DID_RESOURCE_NAME =
                datasetPathforFHIR
                    + FHIR_STORES
                    + "DID_"
                    + customStudyId
                    + "/fhir/"
                    + QUESTIONNAIRE_TYPE
                    + "/"
                    + didResourceId;
            String didData =
                "[{\"op\": \"replace\", \"path\": \"/status\", \"value\": \"retired\"}]";
            fhirHealthcareAPIs.fhirResourcePatch(DID_RESOURCE_NAME, didData);
          }
        }
      }
    }
  }

  public void formatToActiveTaskFhir(
      String studyId, Session session, StudyBo study, List<ActiveTaskBo> activeTasks)
      throws Exception {
    for (ActiveTaskBo activeTask : activeTasks) {
      /*if (!activeTask.getTaskTypeId().equals("9")
      || !activeTask
          .getTaskTypeId()
          .equals(
              "10")) { */
      // for passive and demographics type we will not be creating the resource
      List<ItemsQuestionnaire> listOfItems = new LinkedList<>();
      String datasetPathforFHIR = String.format(DATASET_PATH, projectId, regionId, studyId);
      String resourceId = null;
      String didResourceId = null;

      createFhirStore(datasetPathforFHIR, "FHIR_", studyId);

      float versionIdOfSubmittedforActiveTask = 0.0f;
      String identifierValue = activeTask.getShortTitle();
      String searchQuestionnaireJson =
          fhirHealthcareAPIs.fhirResourceSearchPost(
              datasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId + "/fhir/" + QUESTIONNAIRE_TYPE,
              "identifier=" + identifierValue);
      SearchQuestionnaireFhirBean searchQuestionFhirResponseBean =
          new Gson().fromJson(searchQuestionnaireJson, SearchQuestionnaireFhirBean.class);
      if (searchQuestionFhirResponseBean != null && searchQuestionFhirResponseBean.getTotal() > 0) {
        resourceId =
            String.valueOf(searchQuestionFhirResponseBean.getEntry().get(0).getResource().getId());
        versionIdOfSubmittedforActiveTask =
            Float.valueOf(
                searchQuestionFhirResponseBean.getEntry().get(0).getResource().getVersion());
      }

      if (fhirEnabled.contains("did")) {
        createFhirStore(datasetPathforFHIR, "DID_", studyId);

        String searchDidQuestionnaireJson =
            fhirHealthcareAPIs.fhirResourceSearchPost(
                datasetPathforFHIR + FHIR_STORES + "DID_" + studyId + "/fhir/" + QUESTIONNAIRE_TYPE,
                "identifier=" + identifierValue);
        SearchQuestionnaireFhirBean searchDidQuestionFhirResponseBean =
            new Gson().fromJson(searchDidQuestionnaireJson, SearchQuestionnaireFhirBean.class);
        if (searchDidQuestionFhirResponseBean != null
            && searchDidQuestionFhirResponseBean.getTotal() > 0) {
          didResourceId =
              String.valueOf(
                  searchDidQuestionFhirResponseBean.getEntry().get(0).getResource().getId());
        }
      }

      if (versionIdOfSubmittedforActiveTask < activeTask.getVersion()) {
        List<String> taskMasterAttrIdList = new ArrayList<>();
        List<ActiveTaskMasterAttributeBo> activeTaskMaterList = null;
        ActiveTaskListBo taskDto = null;
        ItemsQuestionnaire activeTaskQuestionnaire = new ItemsQuestionnaire();
        activeTaskQuestionnaire.setLinkId(activeTask.getShortTitle());
        activeTaskQuestionnaire.setDefinition("taskConfiguration");
        activeTaskQuestionnaire.setType("group");

        List<ActiveTaskAtrributeValuesBo> activeTaskAttrtibuteValuesList =
            session
                .createQuery(
                    "from ActiveTaskAtrributeValuesBo AV"
                        + " where AV.activeTaskId=:activeTaskId"
                        + " and AV.activeTaskMasterAttrId in (select ATMADTO.masterId"
                        + " from ActiveTaskMasterAttributeBo ATMADTO"
                        + " where ATMADTO.attributeType=:attributeType)"
                        + " ORDER BY AV.activeTaskMasterAttrId")
                .setString("activeTaskId", activeTask.getId())
                .setString("attributeType", "configure_type")
                .list();

        if ((activeTaskAttrtibuteValuesList != null) && !activeTaskAttrtibuteValuesList.isEmpty()) {

          for (ActiveTaskAtrributeValuesBo attributeDto : activeTaskAttrtibuteValuesList) {
            taskMasterAttrIdList.add(attributeDto.getActiveTaskMasterAttrId());
          }

          if (!taskMasterAttrIdList.isEmpty()) {
            activeTaskMaterList =
                session
                    .createQuery(
                        " from ActiveTaskMasterAttributeBo ATMADTO"
                            + " where ATMADTO.masterId in (:taskMasterAttrIdList)")
                    .setParameterList("taskMasterAttrIdList", taskMasterAttrIdList)
                    .list();

            if ((activeTaskMaterList != null) && !activeTaskMaterList.isEmpty()) {
              taskDto =
                  (ActiveTaskListBo)
                      session
                          .createQuery(
                              "from ActiveTaskListBo ATDTO"
                                  + " where ATDTO.activeTaskListId=:activeTaskListId")
                          .setString("activeTaskListId", activeTaskMaterList.get(0).getTaskTypeId())
                          .uniqueResult();
            }
          }
        }
        List<ItemsQuestionnaire> itemsForActiveTask = new ArrayList<>();
        for (ActiveTaskAtrributeValuesBo attributeDto : activeTaskAttrtibuteValuesList) {
          for (ActiveTaskMasterAttributeBo masterAttributeDto : activeTaskMaterList) {
            if (attributeDto.getActiveTaskMasterAttrId().equals(masterAttributeDto.getMasterId())
                && taskDto.getActiveTaskListId().equals(masterAttributeDto.getTaskTypeId())) {

              switch (taskDto.getType()) {
                case "fetalKickCounter":
                  this.fetalKickCounterDetails(
                      attributeDto, masterAttributeDto, itemsForActiveTask);
                  break;
                case "towerOfHanoi":
                  ItemsQuestionnaire towerOfHanoi = new ItemsQuestionnaire();
                  towerOfHanoi.setLinkId(masterAttributeDto.getAttributeName());
                  towerOfHanoi.setText(masterAttributeDto.getDisplayName());
                  towerOfHanoi.setDefinition(attributeDto.getAttributeVal());
                  towerOfHanoi.setType("integer");
                  itemsForActiveTask.add(towerOfHanoi);
                  break;
                case "spatialSpanMemory":
                  this.spatialSpanMemoryDetails(
                      attributeDto, masterAttributeDto, itemsForActiveTask);
                  break;
                  /*case "stepTest":
                    this.stepTestDetails(attributeDto, masterAttributeDto, itemsForActiveTask);
                    break;
                  case "runTest":
                    this.runTestDetails(attributeDto, masterAttributeDto, itemsForActiveTask);
                    break;
                  case "walkTest":
                    this.walkTestDetails(attributeDto, masterAttributeDto, itemsForActiveTask);
                    break;*/
                default:
                  break;
              }
            }
          }
        }

        activeTaskQuestionnaire.setItem(itemsForActiveTask);
        listOfItems.add(activeTaskQuestionnaire);
        FHIRQuestionnaire fhirQuestionnaire = new FHIRQuestionnaire();
        fhirQuestionnaire.setName(activeTask.getShortTitle());
        fhirQuestionnaire.setTitle(activeTask.getDisplayName());
        fhirQuestionnaire.setResourceType(QUESTIONNAIRE_TYPE);
        String lastModifiedDate =
            FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                activeTask.getModifiedDate(),
                FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN);
        fhirQuestionnaire.setDate(
            FdahpStudyDesignerUtil.convertDateToOtherFormat(
                lastModifiedDate, DATE_FORMAT_RESPONSE_MOBILE, DATE_FORMAT_RESPONSE_FHIR));
        fhirQuestionnaire.setStatus("active");
        fhirQuestionnaire.setVersion(String.valueOf(activeTask.getVersion()));
        List<Extension> extensionsForSchedule = new ArrayList<>();
        extensionsForSchedule =
            formatScheduleAndStudyMetaData(activeTask, extensionsForSchedule, study, session);
        fhirQuestionnaire.setExtension(extensionsForSchedule);

        if (StringUtils.isNotEmpty(activeTask.getScheduleType())
            && !activeTask.getScheduleType().equalsIgnoreCase("AnchorDate")) {
          EffectivePeriod effectivePeriod = new EffectivePeriod();
          effectivePeriod = getTimeDetailsOfActiveTask(session, activeTask);

          effectivePeriod.setStart(
              StringUtils.isNotBlank(effectivePeriod.getStart())
                  ? FdahpStudyDesignerUtil.convertDateToOtherFormat(
                      effectivePeriod.getStart(),
                      DATE_FORMAT_RESPONSE_MOBILE,
                      DATE_FORMAT_RESPONSE_FHIR)
                  : null);
          effectivePeriod.setEnd(
              StringUtils.isNotBlank(effectivePeriod.getEnd())
                  ? FdahpStudyDesignerUtil.convertDateToOtherFormat(
                      effectivePeriod.getEnd(),
                      DATE_FORMAT_RESPONSE_MOBILE,
                      DATE_FORMAT_RESPONSE_FHIR)
                  : "");

          fhirQuestionnaire.setEffectivePeriod(effectivePeriod);
        }

        List<Identifier> identifiers = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setValue(activeTask.getShortTitle());
        identifier.setUse("official");
        Map<String, Object> identifierType = new HashedMap();
        query =
            session.createQuery("From ActiveTaskListBo ac where ac.activeTaskListId=:taskTypeId");
        query.setString("taskTypeId", activeTask.getTaskTypeId());
        query.setMaxResults(1);
        ActiveTaskListBo activeTaskListBo = (ActiveTaskListBo) query.uniqueResult();
        identifierType.put("text", "activeTask_" + activeTaskListBo.getType());
        identifier.setType(identifierType);
        identifiers.add(identifier);
        fhirQuestionnaire.setIdentifier(identifiers);
        fhirQuestionnaire.setItem(listOfItems);

        final String FHIR_DATASET_NAME = datasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId;
        final String DID_DATASET_NAME = datasetPathforFHIR + FHIR_STORES + "DID_" + studyId;

        if (resourceId != null) {
          fhirQuestionnaire.setId(resourceId);
          fhirHealthcareAPIs.fhirResourceUpdate(
              FHIR_DATASET_NAME,
              QUESTIONNAIRE_TYPE,
              new Gson().toJson(fhirQuestionnaire),
              resourceId);
          if (fhirEnabled.contains("did") && didResourceId != null) {
            fhirQuestionnaire.setId(didResourceId);
            fhirHealthcareAPIs.fhirResourceUpdate(
                DID_DATASET_NAME,
                QUESTIONNAIRE_TYPE,
                new Gson().toJson(fhirQuestionnaire),
                didResourceId);
          }
        } else {
          fhirHealthcareAPIs.fhirResourceCreate(
              FHIR_DATASET_NAME, QUESTIONNAIRE_TYPE, new Gson().toJson(fhirQuestionnaire));
          if (fhirEnabled.contains("did")) {
            fhirHealthcareAPIs.fhirResourceCreate(
                DID_DATASET_NAME, QUESTIONNAIRE_TYPE, new Gson().toJson(fhirQuestionnaire));
          }
        }
      }
    }
  }

  private EffectivePeriod getTimeDetailsOfActiveTask(Session session, ActiveTaskBo activeTask) {
    String startDateTime = "";
    String endDateTime = "";
    EffectivePeriod effectivePeriod = new EffectivePeriod();
    try {
      startDateTime =
          StringUtils.isEmpty(activeTask.getActiveTaskLifetimeStart())
              ? ""
              : activeTask.getActiveTaskLifetimeStart()
                  + " "
                  + FdahpStudyDesignerConstants.DEFAULT_MIN_TIME;

      if (StringUtils.isEmpty(activeTask.getActiveTaskLifetimeEnd())) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        String startdate = "";
        if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())) {
          startdate = activeTask.getActiveTaskLifetimeStart();
        } else {
          startdate = activeTask.getModifiedDate();
          startdate = startdate.substring(0, Math.min(startdate.length(), 10));
        }

        LocalDate dateTime = LocalDate.parse(startdate, formatter);
        dateTime = dateTime.plusYears(3);
        String threeYearsAfterString = dateTime.format(formatter);
        System.out.println(threeYearsAfterString);
        endDateTime = threeYearsAfterString;
      }

      endDateTime =
          StringUtils.isEmpty(activeTask.getActiveTaskLifetimeEnd())
              ? StringUtils.isNotEmpty(endDateTime)
                  ? endDateTime + " " + FdahpStudyDesignerConstants.DEFAULT_MAX_TIME
                  : " "
              : activeTask.getActiveTaskLifetimeEnd()
                  + " "
                  + FdahpStudyDesignerConstants.DEFAULT_MAX_TIME;
      if (StringUtils.isNotEmpty(activeTask.getFrequency())) {
        if ((activeTask
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME))
            || (activeTask
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_WEEKLY))
            || (activeTask
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MONTHLY))) {

          ActiveTaskFrequencyBo activeTaskFrequency =
              (ActiveTaskFrequencyBo)
                  session
                      .createQuery(
                          "from ActiveTaskFrequencyBo ATFDTO"
                              + " where ATFDTO.activeTaskId=:activeTaskId"
                              + " ORDER BY ATFDTO.frequencyTime")
                      .setString("activeTaskId", activeTask.getId())
                      .uniqueResult();
          if ((activeTaskFrequency != null)
              && StringUtils.isNotEmpty(activeTaskFrequency.getFrequencyTime())) {
            if (activeTaskFrequency.getIsLaunchStudy()
                && activeTaskFrequency.getIsStudyLifeTime()) {
              startDateTime =
                  activeTask.getActiveTaskLifetimeStart()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            } else if (null != activeTaskFrequency.getFrequencyDate()) {
              startDateTime =
                  activeTaskFrequency.getFrequencyDate()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            }
            if (!activeTask
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)
                && !activeTaskFrequency.getIsStudyLifeTime()
                && null != activeTask.getActiveTaskLifetimeEnd()) {
              endDateTime =
                  activeTask.getActiveTaskLifetimeEnd()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            }
          }

          effectivePeriod.setStart(
              StringUtils.isEmpty(startDateTime)
                  ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      activeTask.getModifiedDate(),
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                  : FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      startDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));

          effectivePeriod.setEnd(
              StringUtils.isEmpty(endDateTime)
                  ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      activeTask.getModifiedDate(),
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                  : FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      endDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));

        } else if (activeTask
            .getFrequency()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {

          List<ActiveTaskFrequencyBo> activeTaskFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskFrequencyBo ATFDTO"
                          + " where ATFDTO.activeTaskId=:activeTaskId"
                          + " ORDER BY ATFDTO.frequencyTime")
                  .setString("activeTaskId", activeTask.getId())
                  .list();
          if ((activeTaskFrequencyList != null)
              && !activeTaskFrequencyList.isEmpty()
              && activeTask.getActiveTaskLifetimeEnd() != null
              && activeTask.getActiveTaskLifetimeStart() != null) {
            startDateTime =
                activeTask.getActiveTaskLifetimeStart()
                    + " "
                    + activeTaskFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                activeTask.getActiveTaskLifetimeEnd()
                    + " "
                    + FdahpStudyDesignerConstants.DEFAULT_MAX_TIME;
          }

          if (null != activeTask.getActiveTaskLifetimeStart()) {
            effectivePeriod.setStart(
                FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                    startDateTime,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }

          if (null != activeTask.getActiveTaskLifetimeEnd()) {
            effectivePeriod.setEnd(
                FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                    endDateTime,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          effectivePeriod.setStart(
              StringUtils.isEmpty(startDateTime)
                  ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      activeTask.getModifiedDate(),
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                  : FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      startDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));

          effectivePeriod.setEnd(
              StringUtils.isEmpty(endDateTime)
                  ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      activeTask.getModifiedDate(),
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                  : FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      endDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));

        } else if (activeTask
            .getFrequency()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<ActiveTaskCustomScheduleBo> activeTaskCustomFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskCustomScheduleBo ATCFDTO"
                          + " where ATCFDTO.activeTaskId=:activeTaskId"
                          + " ORDER BY ATCFDTO.frequencyStartDate, ATCFDTO.frequencyStartTime")
                  .setString("activeTaskId", activeTask.getId())
                  .list();
          if ((activeTaskCustomFrequencyList != null) && !activeTaskCustomFrequencyList.isEmpty()) {
            String startDate = "";
            String endDate = "";
            if (activeTaskCustomFrequencyList.get(0).getFrequencyStartDate() != null
                && activeTaskCustomFrequencyList.get(0).getFrequencyEndDate() != null) {
              startDate = activeTaskCustomFrequencyList.get(0).getFrequencyStartDate();
              endDate = activeTaskCustomFrequencyList.get(0).getFrequencyEndDate();
            }
            for (ActiveTaskCustomScheduleBo customFrequency : activeTaskCustomFrequencyList) {
              if (null != startDate
                  && customFrequency.getFrequencyStartDate() != null
                  && FdahpStudyDesignerConstants.SDF_DATE
                      .parse(startDate)
                      .after(
                          FdahpStudyDesignerConstants.SDF_DATE.parse(
                              customFrequency.getFrequencyStartDate()))) {
                startDate = customFrequency.getFrequencyStartDate();
              }

              if (null != endDate
                  && customFrequency.getFrequencyEndDate() != null
                  && FdahpStudyDesignerConstants.SDF_DATE
                      .parse(endDate)
                      .before(
                          FdahpStudyDesignerConstants.SDF_DATE.parse(
                              customFrequency.getFrequencyEndDate()))) {
                endDate = customFrequency.getFrequencyEndDate();
              }
            }

            String frequencyStartTime =
                activeTaskCustomFrequencyList.get(0).getFrequencyStartTime();
            if (!frequencyStartTime.matches(
                "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
              frequencyStartTime = frequencyStartTime + ":00";
            }
            String frequencyEndTime =
                activeTaskCustomFrequencyList
                    .get(activeTaskCustomFrequencyList.size() - 1)
                    .getFrequencyEndTime();
            if (!frequencyEndTime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
              frequencyEndTime = frequencyEndTime + ":00";
            }
            if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
              startDateTime = startDate + " " + frequencyStartTime;
              endDateTime = endDate + " " + frequencyEndTime;
            }
            if (StringUtils.isNotBlank(startDate)) {
              effectivePeriod.setStart(
                  FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      startDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            } else if (StringUtils.isNotBlank(endDate)) {
              effectivePeriod.setStart(
                  FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      endDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            }

            if (StringUtils.isNotBlank(endDate)) {
              effectivePeriod.setEnd(
                  FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      endDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            }
            effectivePeriod.setStart(
                StringUtils.isEmpty(startDateTime)
                    ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                        activeTask.getModifiedDate(),
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                    : FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                        startDateTime,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));

            effectivePeriod.setEnd(
                StringUtils.isEmpty(endDateTime)
                    ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                        activeTask.getModifiedDate(),
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                    : FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                        endDateTime,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
        }
      }

    } catch (Exception e) {
      logger.error(
          "ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    return effectivePeriod;
  }

  private void walkTestDetails(
      ActiveTaskAtrributeValuesBo attributeDto,
      ActiveTaskMasterAttributeBo masterAttributeDto,
      List<ItemsQuestionnaire> itemsForActiveTask) {

    ItemsQuestionnaire walkTestItem = new ItemsQuestionnaire();
    switch (masterAttributeDto.getAttributeName().trim()) {
      case FdahpStudyDesignerConstants.LENGTH_OF_WALK:
        walkTestItem.setLinkId(masterAttributeDto.getAttributeName());
        walkTestItem.setText(masterAttributeDto.getDisplayName());
        walkTestItem.setDefinition(attributeDto.getAttributeVal());
        walkTestItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.LENGTH_OF_REST_WALK:
        walkTestItem.setLinkId(masterAttributeDto.getAttributeName());
        walkTestItem.setText(masterAttributeDto.getDisplayName());
        walkTestItem.setDefinition(attributeDto.getAttributeVal());
        walkTestItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.AUDIO_PROMPT_INTERVAL_WALK:
        walkTestItem.setLinkId(masterAttributeDto.getAttributeName());
        walkTestItem.setText(masterAttributeDto.getDisplayName());
        walkTestItem.setDefinition(attributeDto.getAttributeVal());
        walkTestItem.setType("integer");
        break;
    }
    itemsForActiveTask.add(walkTestItem);
  }

  private void runTestDetails(
      ActiveTaskAtrributeValuesBo attributeDto,
      ActiveTaskMasterAttributeBo masterAttributeDto,
      List<ItemsQuestionnaire> itemsForActiveTask) {
    ItemsQuestionnaire runTestItem = new ItemsQuestionnaire();
    switch (masterAttributeDto.getAttributeName().trim()) {
      case FdahpStudyDesignerConstants.LENGTH_OF_WALK_RUN:
        runTestItem.setLinkId(masterAttributeDto.getAttributeName());
        runTestItem.setText(masterAttributeDto.getDisplayName());
        runTestItem.setDefinition(attributeDto.getAttributeVal());
        runTestItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.LENGTH_OF_REST_RUN:
        runTestItem.setLinkId(masterAttributeDto.getAttributeName());
        runTestItem.setText(masterAttributeDto.getDisplayName());
        runTestItem.setDefinition(attributeDto.getAttributeVal());
        runTestItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.AUDIO_PROMPT_INTERVAL_RUN:
        runTestItem.setLinkId(masterAttributeDto.getAttributeName());
        runTestItem.setText(masterAttributeDto.getDisplayName());
        runTestItem.setDefinition(attributeDto.getAttributeVal());
        runTestItem.setType("integer");
        break;
    }
    itemsForActiveTask.add(runTestItem);
  }

  private void stepTestDetails(
      ActiveTaskAtrributeValuesBo attributeDto,
      ActiveTaskMasterAttributeBo masterAttributeDto,
      List<ItemsQuestionnaire> itemsForActiveTask) {
    // TODO Auto-generated method stub
    ItemsQuestionnaire stepTestItem = new ItemsQuestionnaire();
    switch (masterAttributeDto.getAttributeName().trim()) {
      case FdahpStudyDesignerConstants.LENGTH_OF_WALK_STEP:
        stepTestItem.setLinkId(masterAttributeDto.getAttributeName());
        stepTestItem.setText(masterAttributeDto.getDisplayName());
        stepTestItem.setDefinition(attributeDto.getAttributeVal());
        stepTestItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.LENGTH_OF_REST_STEP:
        stepTestItem.setLinkId(masterAttributeDto.getAttributeName());
        stepTestItem.setText(masterAttributeDto.getDisplayName());
        stepTestItem.setDefinition(attributeDto.getAttributeVal());
        stepTestItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.AUDIO_PROMPT_INTERVAL_STEP:
        stepTestItem.setLinkId(masterAttributeDto.getAttributeName());
        stepTestItem.setText(masterAttributeDto.getDisplayName());
        stepTestItem.setDefinition(attributeDto.getAttributeVal());
        stepTestItem.setType("integer");
        break;
    }
  }

  private List<Extension> formatScheduleAndStudyMetaData(
      ActiveTaskBo activeTask,
      List<Extension> extensionsForSchedule,
      StudyBo study,
      Session session) {
    // TODO Auto-generated method stub
    Extension extensionForSchedule = new Extension();
    extensionForSchedule.setUrl("Schedule");
    LinkedList<Extension> extensionForScheduleData = new LinkedList<>();
    Extension extensionForScheduleType = new Extension();
    extensionForScheduleType.setUrl("Schedule Type");
    extensionForScheduleType.setValueString(activeTask.getScheduleType());
    Extension extensionForScheduleOption = new Extension();
    extensionForScheduleOption.setUrl("Schedule Option");
    extensionForScheduleOption.setValueString(activeTask.getFrequency());
    if (activeTask.getAnchorDateId() != null) {
      AnchorDateTypeBo dateTypeBo =
          (AnchorDateTypeBo)
              session
                  .getNamedQuery("getAnchorDateTypeById")
                  .setString("anchorDateTypeId", activeTask.getAnchorDateId())
                  .uniqueResult();
      Extension extensionForAnchorDate = new Extension();
      extensionForAnchorDate.setUrl("Anchor Date");
      extensionForAnchorDate.setValueString(dateTypeBo.getName());
      extensionForScheduleData.add(extensionForAnchorDate);
    }
    extensionForScheduleData.add(extensionForScheduleOption);
    extensionForScheduleData.add(extensionForScheduleType);
    extensionForSchedule.setExtension(extensionForScheduleData);
    extensionsForSchedule.add(extensionForSchedule);

    Extension extensionForStudy = new Extension();
    extensionForStudy.setUrl("StudyMetaData");
    LinkedList<Extension> extensionForStudyData = new LinkedList<>();
    Extension extensionForStudyID = new Extension();
    extensionForStudyID.setUrl("StudyID");
    extensionForStudyID.setValueString(study.getCustomStudyId());
    Extension extensionForStudyName = new Extension();
    extensionForStudyName.setUrl("StudyName");
    extensionForStudyName.setValueString(study.getName());
    Extension extensionForStudyVersion = new Extension();
    extensionForStudyVersion.setUrl("StudyVersion");
    extensionForStudyVersion.setValueString(String.valueOf(study.getVersion()));
    extensionForStudyData.add(extensionForStudyID);
    extensionForStudyData.add(extensionForStudyName);
    extensionForStudyData.add(extensionForStudyVersion);
    extensionForStudy.setExtension(extensionForStudyData);
    extensionsForSchedule.add(extensionForStudy);
    return extensionsForSchedule;
  }

  private void spatialSpanMemoryDetails(
      ActiveTaskAtrributeValuesBo attributeDto,
      ActiveTaskMasterAttributeBo masterAttributeDto,
      List<ItemsQuestionnaire> itemsForActiveTask) {
    ItemsQuestionnaire spatialSpanItem = new ItemsQuestionnaire();
    switch (masterAttributeDto.getAttributeName().trim()) {
      case FdahpStudyDesignerConstants.SSM_INITIAL:
        spatialSpanItem.setLinkId(masterAttributeDto.getAttributeName());
        spatialSpanItem.setText(masterAttributeDto.getDisplayName());
        spatialSpanItem.setDefinition(attributeDto.getAttributeVal());
        spatialSpanItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.SSM_MINIMUM:
        spatialSpanItem.setLinkId(masterAttributeDto.getAttributeName());
        spatialSpanItem.setText(masterAttributeDto.getDisplayName());
        spatialSpanItem.setDefinition(attributeDto.getAttributeVal());
        spatialSpanItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.SSM_MAXIMUM:
        spatialSpanItem.setLinkId(masterAttributeDto.getAttributeName());
        spatialSpanItem.setText(masterAttributeDto.getDisplayName());
        spatialSpanItem.setDefinition(attributeDto.getAttributeVal());
        spatialSpanItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.SSM_PLAY_SPEED:
        spatialSpanItem.setLinkId(masterAttributeDto.getAttributeName());
        spatialSpanItem.setText(masterAttributeDto.getDisplayName());
        spatialSpanItem.setDefinition(attributeDto.getAttributeVal());
        spatialSpanItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.SSM_MAX_TEST:
        spatialSpanItem.setLinkId(masterAttributeDto.getAttributeName());
        spatialSpanItem.setText(masterAttributeDto.getDisplayName());
        spatialSpanItem.setDefinition(attributeDto.getAttributeVal());
        spatialSpanItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.SSM_MAX_CONSECUTIVE_FAILURES:
        // spatialSpanItem.setLinkId(masterAttributeDto.getAttributeName());
        spatialSpanItem.setLinkId("Maximum_Consecutive_Failures_spatial");
        spatialSpanItem.setText(masterAttributeDto.getDisplayName());
        spatialSpanItem.setDefinition(attributeDto.getAttributeVal());
        spatialSpanItem.setType("integer");
        break;
      case FdahpStudyDesignerConstants.SSM_REQUIRE_REVERSAL:
        spatialSpanItem.setLinkId(masterAttributeDto.getAttributeName());
        spatialSpanItem.setText(masterAttributeDto.getDisplayName());
        spatialSpanItem.setDefinition(
            StringUtils.isNotEmpty(attributeDto.getAttributeVal())
                    && attributeDto.getAttributeVal().equalsIgnoreCase("Y")
                ? "true"
                : "false");
        spatialSpanItem.setType("boolean");
        break;
    }
    itemsForActiveTask.add(spatialSpanItem);
  }

  private void fetalKickCounterDetails(
      ActiveTaskAtrributeValuesBo attributeDto,
      ActiveTaskMasterAttributeBo masterAttributeDto,
      List<ItemsQuestionnaire> itemsForActiveTask) {
    // TODO Auto-generated method stub
    if (masterAttributeDto.getAttributeName().equals("duration_kick_count_fetal")) {
      ItemsQuestionnaire fetalKick = new ItemsQuestionnaire();
      fetalKick.setLinkId(masterAttributeDto.getAttributeName());
      fetalKick.setText(masterAttributeDto.getDisplayName());
      fetalKick.setDefinition(attributeDto.getAttributeVal());
      fetalKick.setType("integer");
      itemsForActiveTask.add(fetalKick);
    }
  }

  public void formatToQuestionnaireType(
      String studyId, Session session, StudyBo study, List<QuestionnaireBo> questionnaires)
      throws Exception {

    logger.entry("StudyDaoImpl - formatToQuestionnaireType() - Ends");
    for (QuestionnaireBo questionnaireBo : questionnaires) {
      List<ItemsQuestionnaire> listOfItems = new LinkedList<>();
      String datasetPathforFHIR = String.format(DATASET_PATH, projectId, regionId, studyId);
      String resourceId = null;
      String didResourceId = null;

      createFhirStore(datasetPathforFHIR, "FHIR_", studyId);

      float versionIdOfSubmittedResponse = 0.0f;
      String identifierValue = questionnaireBo.getShortTitle();
      String searchQuestionnaireJson =
          fhirHealthcareAPIs.fhirResourceSearchPost(
              datasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId + "/fhir/" + QUESTIONNAIRE_TYPE,
              "identifier=" + identifierValue);
      SearchQuestionnaireFhirBean searchQuestionFhirResponseBean =
          new Gson().fromJson(searchQuestionnaireJson, SearchQuestionnaireFhirBean.class);

      if (searchQuestionFhirResponseBean != null && searchQuestionFhirResponseBean.getTotal() > 0) {
        resourceId =
            String.valueOf(searchQuestionFhirResponseBean.getEntry().get(0).getResource().getId());

        versionIdOfSubmittedResponse =
            Float.valueOf(
                searchQuestionFhirResponseBean.getEntry().get(0).getResource().getVersion());
      }

      if (fhirEnabled.contains("did")) {
        createFhirStore(datasetPathforFHIR, "DID_", studyId);

        String searchDidQuestionnaireJson =
            fhirHealthcareAPIs.fhirResourceSearchPost(
                datasetPathforFHIR + FHIR_STORES + "DID_" + studyId + "/fhir/" + QUESTIONNAIRE_TYPE,
                "identifier=" + identifierValue);
        SearchQuestionnaireFhirBean searchDidQuestionFhirResponseBean =
            new Gson().fromJson(searchDidQuestionnaireJson, SearchQuestionnaireFhirBean.class);
        if (searchDidQuestionFhirResponseBean != null
            && searchDidQuestionFhirResponseBean.getTotal() > 0) {
          didResourceId =
              String.valueOf(
                  searchDidQuestionFhirResponseBean.getEntry().get(0).getResource().getId());
        }
      }

      if (versionIdOfSubmittedResponse < questionnaireBo.getVersion()) {
        List<QuestionnairesStepsBo> existedQuestionnairesStepsBoList = null;
        query =
            session
                .getNamedQuery("getQuestionnaireStepList")
                .setString("questionnaireId", questionnaireBo.getId());
        existedQuestionnairesStepsBoList = query.list();
        for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
          List<EnableWhenBranching> enableWhenBranchinglist = new ArrayList<>();
          ItemsQuestionnaire items = new ItemsQuestionnaire();
          items.setLinkId(questionnairesStepsBo.getStepShortTitle());
          logger.debug("firststep", questionnairesStepsBo.getStepShortTitle());
          QuestionsBo questionsBo =
              (QuestionsBo)
                  session
                      .getNamedQuery("getQuestionStep")
                      .setString("stepId", questionnairesStepsBo.getInstructionFormId())
                      .uniqueResult();
          // branching when enabled
          updateQuestionaireForBranching(
              session,
              questionnaireBo,
              questionnairesStepsBo,
              enableWhenBranchinglist,
              questionsBo);
          if (!questionnaireBo.getBranching()) {
            logger.info("Branching is not enabled");
          }
          logger.info("Branching exit here", enableWhenBranchinglist);
          if (enableWhenBranchinglist.size() > 2) {
            items.setEnableBehavior("any");
          }
          if (questionnairesStepsBo.getStepType().equalsIgnoreCase("Question")
              && null != questionsBo) {
            items.setDefinition(FdahpStudyDesignerConstants.QUESTIONSTEP_ACTIVITY);
            items.setEnableWhen(enableWhenBranchinglist);

            logger.debug("enabledvalue", items);
            items = toQuestionDetails(session, questionnairesStepsBo, items, questionsBo);
          } else if (questionnairesStepsBo.getStepType().equalsIgnoreCase("Instruction")) {
            items.setDefinition(FdahpStudyDesignerConstants.INSTRUCTION_ACTIVITY);
            InstructionsBo instructionsBo =
                (InstructionsBo)
                    session
                        .getNamedQuery("getInstructionStep")
                        .setString("id", questionnairesStepsBo.getInstructionFormId())
                        .uniqueResult();
            items.setText(instructionsBo.getInstructionTitle());
            items.setType("display");
          } else if (questionnairesStepsBo.getStepType().equalsIgnoreCase("Form")) {
            List<ItemsQuestionnaire> listOfitemsForForm = new ArrayList<>();
            items.setDefinition(FdahpStudyDesignerConstants.FORMSTEP_ACTIVITY);
            items.setEnableWhen(enableWhenBranchinglist);
            items.setType("group");
            boolean repeatableForm =
                questionnairesStepsBo.getRepeatable().equalsIgnoreCase("Yes") ? true : false;
            items.setRepeats(repeatableForm);
            FormBo formBo =
                (FormBo)
                    session
                        .getNamedQuery("getFormBoStep")
                        .setString("stepId", questionnairesStepsBo.getInstructionFormId())
                        .uniqueResult();
            if (formBo != null) {
              List<FormMappingBo> formMappingBoList =
                  session
                      .getNamedQuery("getFormByFormId")
                      .setString("formId", formBo.getFormId())
                      .list();

              for (FormMappingBo formMappingBo : formMappingBoList) {
                ItemsQuestionnaire itemsForForm = new ItemsQuestionnaire();
                QuestionsBo questionsBoForForm =
                    (QuestionsBo)
                        session
                            .getNamedQuery("getQuestionByFormId")
                            .setString("formId", formMappingBo.getQuestionId())
                            .uniqueResult();
                itemsForForm.setLinkId(questionsBoForForm.getShortTitle());
                itemsForForm =
                    toQuestionDetails(
                        session, questionnairesStepsBo, itemsForForm, questionsBoForForm);
                listOfitemsForForm.add(itemsForForm);
              }
            }
            items.setItem(listOfitemsForForm);
          }
          listOfItems.add(items);
        }
        FHIRQuestionnaire fhirQuestionnaire =
            toFHIRQuestionnaire(session, listOfItems, study, questionnaireBo);

        final String FHIR_DATASET_NAME = datasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId;
        final String DID_DATASET_NAME = datasetPathforFHIR + FHIR_STORES + "DID_" + studyId;

        logger.debug("fhirQuestionnaire : " + new Gson().toJson(fhirQuestionnaire));
        if (resourceId != null) {
          fhirQuestionnaire.setId(resourceId);
          fhirHealthcareAPIs.fhirResourceUpdate(
              FHIR_DATASET_NAME,
              QUESTIONNAIRE_TYPE,
              new Gson().toJson(fhirQuestionnaire),
              resourceId);
          if (fhirEnabled.contains("did") && didResourceId != null) {
            fhirQuestionnaire.setId(didResourceId);
            fhirHealthcareAPIs.fhirResourceUpdate(
                DID_DATASET_NAME,
                QUESTIONNAIRE_TYPE,
                new Gson().toJson(fhirQuestionnaire),
                didResourceId);
          }
        } else {
          fhirHealthcareAPIs.fhirResourceCreate(
              FHIR_DATASET_NAME, QUESTIONNAIRE_TYPE, new Gson().toJson(fhirQuestionnaire));
          if (fhirEnabled.contains("did")) {
            fhirHealthcareAPIs.fhirResourceCreate(
                DID_DATASET_NAME, QUESTIONNAIRE_TYPE, new Gson().toJson(fhirQuestionnaire));
          }
        }
      }
    }
    logger.exit("StudyDaoImpl - formatToQuestionnaireType() - Ends");
  }
  /**
   * this method update for QuestionnaireResponseForBranching
   *
   * @param session
   * @param questionnaireBo
   * @param questionnairesStepsBo
   * @param enableWhenBranchinglist
   * @param questionsBo
   */
  private void updateQuestionaireForBranching(
      Session session,
      QuestionnaireBo questionnaireBo,
      QuestionnairesStepsBo questionnairesStepsBo,
      List<EnableWhenBranching> enableWhenBranchinglist,
      QuestionsBo questionsBo) {
    if (questionnaireBo.getBranching() /* && questionsBo != null*/) {
      logger.debug("Branching is enabled: " + questionnaireBo.getBranching());
      // list of destination step list
      // EnableWhenBranching enableWhenBranching = new EnableWhenBranching();
      List<QuestionResponseSubTypeBo> destinationList = destinationAllList(questionnairesStepsBo);
      for (QuestionResponseSubTypeBo reponsestep : destinationList) {
        QuestionnairesStepsBo questionstep = getQuestionStep(reponsestep.getResponseTypeId());
        QuestionsBo questionBo =
            (QuestionsBo)
                session
                    .getNamedQuery("getQuestionStep")
                    .setString("stepId", reponsestep.getResponseTypeId())
                    .uniqueResult();
        if (questionBo != null) {
          QuestionnairesStepsBo callingStep =
              studyQuestionnaireService.getenabledValues(questionBo);
          int responseType = questionBo.getResponseType();
          // when Choice based condition then
          if ((callingStep.getQuestionReponseTypeBo() != null)
              && (callingStep.getQuestionReponseTypeBo().getFormulaBasedLogic() != null)
              && callingStep
                  .getQuestionReponseTypeBo()
                  .getFormulaBasedLogic()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.NO)
              && getChoiceBased(responseType)) {
            if (reponsestep != null && reponsestep.getDestinationStepId() != null) {
              EnableWhenBranching enableWhenBranching = new EnableWhenBranching();
              enableWhenBranching.setQuestion(questionstep.getStepShortTitle());
              enableWhenBranching.setOperator("equals");
              getResponseTypeValue(callingStep, responseType, reponsestep, enableWhenBranching);
              enableWhenBranchinglist.add(enableWhenBranching);
            }
            //  }
          }
          // when formula based condition then
          if ((callingStep.getQuestionReponseTypeBo() != null)
              && (callingStep.getQuestionReponseTypeBo().getFormulaBasedLogic() != null)
              && callingStep
                  .getQuestionReponseTypeBo()
                  .getFormulaBasedLogic()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
            logger.info("Fromula based Branching");
            int responseFormulaType = questionBo.getResponseType();
            /*for (QuestionResponseSubTypeBo QResposneSubType :
            callingStep.getQuestionResponseSubTypeList()) {*/
            if (reponsestep != null && reponsestep.getDestinationStepId() != null) {
              EnableWhenBranching enableWhenBranching = new EnableWhenBranching();
              enableWhenBranching.setQuestion(questionstep.getStepShortTitle());
              String operator = "";
              for (QuestionConditionBranchBo Qcb : callingStep.getQuestionConditionBranchBoList()) {
                operator = Qcb.getInputTypeValue();
                break;
              }
              enableWhenBranching.setOperator(operator);
              getResponseFormulaTypeValue(responseFormulaType, reponsestep, enableWhenBranching);
              enableWhenBranchinglist.add(enableWhenBranching);
            }
            // }
          }
        }
      } // for loop end here
      // for default destination step
      if (questionnairesStepsBo.getStepId() != null
          && StringUtils.isNotBlank(questionnairesStepsBo.getStepId())) {
        List<QuestionnairesStepsBo> stepsBo =
            getdefaultQuestionStep(questionnairesStepsBo.getStepId());
        for (QuestionnairesStepsBo bo : stepsBo) {
          EnableWhenBranching enableWhenBranching = new EnableWhenBranching();
          enableWhenBranching.setQuestion(bo.getStepShortTitle());
          enableWhenBranching.setOperator("equals");
          enableWhenBranching.setAnswerString("defaultStep");
          enableWhenBranchinglist.add(enableWhenBranching);
        }
      }
      // ends here default
      // questions other inludes start here
      if (questionnairesStepsBo.getStepId() != null
          && StringUtils.isNotBlank(questionnairesStepsBo.getStepId())) {
        QuestionReponseTypeBo reponseTypeBo = null;
        reponseTypeBo = getDestinationFromQuestionResponse(questionnairesStepsBo.getStepId());
        if (reponseTypeBo != null && reponseTypeBo.getQuestionsResponseTypeId() != null) {
          QuestionnairesStepsBo stepsBo =
              getQuestionnaireStep(reponseTypeBo.getQuestionsResponseTypeId());
          if (stepsBo != null) {
            EnableWhenBranching enableWhenBranching = new EnableWhenBranching();
            enableWhenBranching.setQuestion(stepsBo.getStepShortTitle());
            enableWhenBranching.setOperator("equals");
            enableWhenBranching.setAnswerString(reponseTypeBo.getOtherText());
            enableWhenBranchinglist.add(enableWhenBranching);
          }
        }
      }
      //// questions other inludes ends here
    }
  }

  private QuestionnairesStepsBo getQuestionnaireStep(String questionsResponseTypeId) {

    Session session = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      if (questionsResponseTypeId != null) {
        String searchQuery =
            "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:instructionFormId ";

        questionnairesStepsBo =
            (QuestionnairesStepsBo)
                session
                    .createQuery(searchQuery)
                    .setString("instructionFormId", questionsResponseTypeId)
                    .uniqueResult();
      }
    } catch (Exception e) {
      logger.error("getDestinationFromQuestionResponse() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getDestinationFromQuestionResponse() - Ends");
    return questionnairesStepsBo;
  }

  private QuestionReponseTypeBo getDestinationFromQuestionResponse(String stepId) {

    Session session = null;
    QuestionReponseTypeBo questionReponseTypeBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      if (stepId != null) {
        String searchQuery =
            "From QuestionReponseTypeBo QSBO where QSBO.otherDestinationStepId=:stepId ";

        questionReponseTypeBo =
            (QuestionReponseTypeBo)
                session.createQuery(searchQuery).setString("stepId", stepId).uniqueResult();
      }
    } catch (Exception e) {
      logger.error("getDestinationFromQuestionResponse() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getDestinationFromQuestionResponse() - Ends");
    return questionReponseTypeBo;
  }

  private List<QuestionnairesStepsBo> getdefaultQuestionStep(String destinationStep) {
    Session session = null;
    List<QuestionnairesStepsBo> questionnairesStepsBos = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      if (destinationStep != null) {
        String searchQuery =
            "From QuestionnairesStepsBo QSBO where QSBO.destinationStep=:destinationStep ";

        questionnairesStepsBos =
            session.createQuery(searchQuery).setString("destinationStep", destinationStep).list();
      }
    } catch (Exception e) {
      logger.error("getdestinationShortTitle() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getdestinationShortTitle() - Ends");
    return questionnairesStepsBos;
  }

  private QuestionnairesStepsBo getQuestionStep(String responseTypeId) {

    Session session = null;
    // List<QuestionResponseSubTypeBo> questionResponseSubTypelist = new ArrayList<>();
    QuestionnairesStepsBo questionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      if (responseTypeId != null) {
        String searchQuery =
            "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:responseTypeId ";

        questionnairesStepsBo =
            (QuestionnairesStepsBo)
                session
                    .createQuery(searchQuery)
                    .setString("responseTypeId", responseTypeId)
                    .uniqueResult();
      }
    } catch (Exception e) {
      logger.error("getdestinationShortTitle() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getdestinationShortTitle() - Ends");
    return questionnairesStepsBo;
  }

  private void getResponseFormulaTypeValue(
      int responseFormulaType,
      QuestionResponseSubTypeBo QResposneSubType,
      EnableWhenBranching enableWhenBranching) {
    switch (responseFormulaType) {
      case 1: // scale
        if (QResposneSubType.getValue().equalsIgnoreCase("true")) {
          enableWhenBranching.setAnswerBoolean(true);
        } else enableWhenBranching.setAnswerBoolean(false);
        break;
      case 2: // continuous scale
        if (QResposneSubType.getValue().equalsIgnoreCase("true")) {
          enableWhenBranching.setAnswerBoolean(true);
        } else enableWhenBranching.setAnswerBoolean(false);
        break;
      case 3: // text scale
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 4: // value picker
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 5: // image choice
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 6: // text choice
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 7: // boolean
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 8: // numeric
        if (QResposneSubType.getValue().equalsIgnoreCase("true")) {
          enableWhenBranching.setAnswerBoolean(true);
        } else enableWhenBranching.setAnswerBoolean(false);
        break;
      case 9: // time of the day
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 10: // date
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 11: // text
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 12: // email
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 13: // time interval
        if (QResposneSubType.getValue().equalsIgnoreCase("true")) {
          enableWhenBranching.setAnswerBoolean(true);
        } else enableWhenBranching.setAnswerBoolean(false);
        break;
      case 14: // height
        if (QResposneSubType.getValue().equalsIgnoreCase("true")) {
          enableWhenBranching.setAnswerBoolean(true);
        } else enableWhenBranching.setAnswerBoolean(false);
        break;
      case 15: // location
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      default:
        break;
    }
  }

  private void getResponseTypeValue(
      QuestionnairesStepsBo callingStep,
      int responseType,
      QuestionResponseSubTypeBo QResposneSubType,
      EnableWhenBranching enableWhenBranching) {
    switch (responseType) {
      case 1: // scale
        enableWhenBranching.setAnswerDecimal(0.0);
        break;
      case 2: // continuous scale
        enableWhenBranching.setAnswerDecimal(0.0);
        break;
      case 3: // text scale
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 4: // value picker
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 5: // image choice
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 6: // text choice
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 7: // boolean
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 8: // numeric
        String type =
            callingStep.getQuestionReponseTypeBo().getStyle().equalsIgnoreCase("Decimal")
                ? "decimal"
                : "integer";
        if (type == "decimal") {
          enableWhenBranching.setAnswerDecimal(0.0);
        } else enableWhenBranching.setAnswerInteger(0);
        break;
      case 9: // time of the day
        enableWhenBranching.setAnswerTime(QResposneSubType.getText());
        break;
      case 10: // date
        String typeForDate =
            callingStep.getQuestionReponseTypeBo().getStyle().equalsIgnoreCase("Date")
                ? "date"
                : "dateTime";
        if (typeForDate == "date") {
          enableWhenBranching.setAnswerDate(QResposneSubType.getText());
        } else enableWhenBranching.setAnswerDateTime("");
        break;
      case 11: // text
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 12: // email
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      case 13: // time interval
        enableWhenBranching.setAnswerDecimal(0.0);
        break;
      case 14: // height
        enableWhenBranching.setAnswerQuantity(0.0);
        break;
      case 15: // location
        enableWhenBranching.setAnswerString(QResposneSubType.getText());
        break;
      default:
        break;
    }
  }

  private boolean getChoiceBased(int Rtype) {
    return Rtype == 3
        || Rtype == 4
        || Rtype == 5
        || Rtype == 7
        || Rtype == 6
        || Rtype == 9
        || Rtype == 10
        || Rtype == 11
        || Rtype == 12
        || Rtype == 15;
  }

  public List<QuestionResponseSubTypeBo> destinationAllList(
      QuestionnairesStepsBo questionnairesStepsBo) {

    Session session = null;
    List<QuestionResponseSubTypeBo> questionResponseSubTypelist = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      if (questionnairesStepsBo != null && questionnairesStepsBo.getStepId() != null) {
        String searchQuery =
            "From QuestionResponseSubTypeBo QSBO where QSBO.destinationStepId=:destinationStep ";

        questionResponseSubTypelist =
            session
                .createQuery(searchQuery)
                .setString("destinationStep", questionnairesStepsBo.getStepId())
                .list();
      }
    } catch (Exception e) {
      logger.error("getdestinationShortTitle() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getdestinationShortTitle() - Ends");
    return questionResponseSubTypelist;
  }

  public FHIRQuestionnaire toFHIRQuestionnaire(
      Session session,
      List<ItemsQuestionnaire> listOfItems,
      StudyBo study,
      QuestionnaireBo questionnaireBo)
      throws ParseException {
    FHIRQuestionnaire fhirQuestionnaire = new FHIRQuestionnaire();
    fhirQuestionnaire.setName(questionnaireBo.getShortTitle());
    fhirQuestionnaire.setTitle(questionnaireBo.getTitle());
    String lastModifiedDate =
        FdahpStudyDesignerUtil.getFormattedDateTimeZone(
            questionnaireBo.getModifiedDate(),
            FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
            FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN);
    fhirQuestionnaire.setDate(
        FdahpStudyDesignerUtil.convertDateToOtherFormat(
            lastModifiedDate, DATE_FORMAT_RESPONSE_MOBILE, DATE_FORMAT_RESPONSE_FHIR));
    fhirQuestionnaire.setStatus("active");
    fhirQuestionnaire.setVersion(String.valueOf(questionnaireBo.getVersion()));
    List<Extension> extensionsForSchedule = new ArrayList<>();
    extensionsForSchedule =
        formatScheduleAndStudyMetaData(questionnaireBo, extensionsForSchedule, study, session);
    fhirQuestionnaire.setExtension(extensionsForSchedule);

    if (StringUtils.isNotEmpty(questionnaireBo.getScheduleType())
        && !questionnaireBo.getScheduleType().equalsIgnoreCase("AnchorDate")) {
      EffectivePeriod effectivePeriod = new EffectivePeriod();
      effectivePeriod = getTimeDetailsOfQuestionnaire(session, questionnaireBo);

      // test
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

      if (StringUtils.isNotBlank(effectivePeriod.getStart())
          && StringUtils.isNotBlank(effectivePeriod.getEnd())
          && sdf.parse(effectivePeriod.getStart()).before(sdf.parse(effectivePeriod.getEnd()))) {

        effectivePeriod.setStart(
            StringUtils.isNotBlank(effectivePeriod.getStart())
                ? FdahpStudyDesignerUtil.convertDateToOtherFormat(
                    effectivePeriod.getStart(),
                    DATE_FORMAT_RESPONSE_MOBILE,
                    DATE_FORMAT_RESPONSE_FHIR)
                : null);
        effectivePeriod.setEnd(
            StringUtils.isNotBlank(effectivePeriod.getEnd())
                ? FdahpStudyDesignerUtil.convertDateToOtherFormat(
                    effectivePeriod.getEnd(),
                    DATE_FORMAT_RESPONSE_MOBILE,
                    DATE_FORMAT_RESPONSE_FHIR)
                : null);
      } else {
        effectivePeriod.setStart(
            StringUtils.isNotBlank(effectivePeriod.getStart())
                ? FdahpStudyDesignerUtil.convertDateToOtherFormat(
                    effectivePeriod.getStart(),
                    DATE_FORMAT_RESPONSE_MOBILE,
                    DATE_FORMAT_RESPONSE_FHIR)
                : null);
        effectivePeriod.setEnd(
            StringUtils.isNotBlank(effectivePeriod.getEnd())
                ? FdahpStudyDesignerUtil.convertDateToOtherFormat(
                    effectivePeriod.getEnd(),
                    DATE_FORMAT_RESPONSE_MOBILE,
                    DATE_FORMAT_RESPONSE_FHIR)
                : null);
      }

      fhirQuestionnaire.setEffectivePeriod(effectivePeriod);
    }

    List<Identifier> identifiers = new ArrayList<>();
    Identifier identifier = new Identifier();
    identifier.setValue(questionnaireBo.getShortTitle());
    identifier.setUse("official");
    Map<String, Object> identifierType = new HashedMap();
    identifierType.put("text", "questionnaire");
    identifier.setType(identifierType);
    identifiers.add(identifier);
    fhirQuestionnaire.setIdentifier(identifiers);
    fhirQuestionnaire.setItem(listOfItems);
    fhirQuestionnaire.setResourceType(QUESTIONNAIRE_TYPE);
    return fhirQuestionnaire;
  }

  private void fhirStatusForDeletedQuestionnaires(
      String studyId,
      String customStudyId,
      List<QuestionnaireBo> questionnaires,
      Session session,
      String buttonText)
      throws GoogleJsonResponseException {

    String resourceId = null;
    String didResourceId = null;

    String datasetPathforFHIR = String.format(DATASET_PATH, projectId, regionId, customStudyId);
    List<QuestionnaireBo> deletedQuestionnaires = null;
    List<QuestionnaireBo> questionnairesDeactivate = null;
    List<String> liveQuestionnaireIdList = new ArrayList<>();
    for (QuestionnaireBo questionnaireBo : questionnaires) {
      String liveQuestionnaire = questionnaireBo.getShortTitle();
      liveQuestionnaireIdList.add(liveQuestionnaire);
    }
    String searchQuery = "";
    if (liveQuestionnaireIdList.isEmpty()) {
      searchQuery =
          "From QuestionnaireBo QBO WHERE QBO.studyId =:studyId "
              + " and QBO.active=0 and QBO.live=0";
      query = session.createQuery(searchQuery).setString("studyId", studyId);
    } else {
      searchQuery =
          "From QuestionnaireBo QBO WHERE QBO.studyId =:studyId "
              + " and QBO.active=0 and QBO.live=0 AND QBO.shortTitle NOT IN (SELECT qb.shortTitle from QuestionnaireBo qb WHERE qb.active=1 AND qb.live=1 AND qb.customStudyId=:customStudyId )";
      query =
          session
              .createQuery(searchQuery)
              .setString("studyId", studyId)
              .setString("customStudyId", customStudyId);
    }

    deletedQuestionnaires = query.list();

    if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_DEACTIVATE)) {
      questionnairesDeactivate = questionnaires;
    } else {
      questionnairesDeactivate = deletedQuestionnaires;
    }

    for (QuestionnaireBo deletedQuestionnaire : questionnairesDeactivate) {
      String searchQuestionnaireJson =
          fhirHealthcareAPIs.fhirResourceSearchPost(
              datasetPathforFHIR
                  + FHIR_STORES
                  + "FHIR_"
                  + customStudyId
                  + "/fhir/"
                  + QUESTIONNAIRE_TYPE,
              "identifier=" + deletedQuestionnaire.getShortTitle());
      SearchQuestionnaireFhirBean searchQuestionFhirResponseBean =
          new Gson().fromJson(searchQuestionnaireJson, SearchQuestionnaireFhirBean.class);

      if (fhirEnabled.contains("did")) {
        String searchDidQuestionnaireJson =
            fhirHealthcareAPIs.fhirResourceSearchPost(
                datasetPathforFHIR
                    + FHIR_STORES
                    + "DID_"
                    + customStudyId
                    + "/fhir/"
                    + QUESTIONNAIRE_TYPE,
                "identifier=" + deletedQuestionnaire.getShortTitle());
        SearchQuestionnaireFhirBean searchQuestionDidResponseBean =
            new Gson().fromJson(searchDidQuestionnaireJson, SearchQuestionnaireFhirBean.class);
        if (searchQuestionDidResponseBean != null && searchQuestionDidResponseBean.getTotal() > 0) {
          didResourceId =
              String.valueOf(searchQuestionDidResponseBean.getEntry().get(0).getResource().getId());
        }
      }

      if (searchQuestionFhirResponseBean != null && searchQuestionFhirResponseBean.getTotal() > 0) {
        resourceId =
            String.valueOf(searchQuestionFhirResponseBean.getEntry().get(0).getResource().getId());

        String statusOfQuestionnaire =
            String.valueOf(
                searchQuestionFhirResponseBean.getEntry().get(0).getResource().getStatus());
        if (!statusOfQuestionnaire.equals("retired")) {
          final String RESOURCE_NAME =
              datasetPathforFHIR
                  + FHIR_STORES
                  + "FHIR_"
                  + customStudyId
                  + "/fhir/"
                  + QUESTIONNAIRE_TYPE
                  + "/"
                  + resourceId;
          String data = "[{\"op\": \"replace\", \"path\": \"/status\", \"value\": \"retired\"}]";
          fhirHealthcareAPIs.fhirResourcePatch(RESOURCE_NAME, data);
          if (fhirEnabled.contains("did") && null != didResourceId) {
            final String DID_RESOURCE_NAME =
                datasetPathforFHIR
                    + FHIR_STORES
                    + "DID_"
                    + customStudyId
                    + "/fhir/"
                    + QUESTIONNAIRE_TYPE
                    + "/"
                    + didResourceId;
            String didData =
                "[{\"op\": \"replace\", \"path\": \"/status\", \"value\": \"retired\"}]";
            fhirHealthcareAPIs.fhirResourcePatch(DID_RESOURCE_NAME, didData);
          }
        }
      }
    }
  }

  public List<Extension> formatScheduleAndStudyMetaData(
      QuestionnaireBo questionnaireBo,
      List<Extension> extensionsForSchedule,
      StudyBo study,
      Session session) {
    Extension extensionForSchedule = new Extension();
    extensionForSchedule.setUrl("Schedule");
    LinkedList<Extension> extensionForScheduleData = new LinkedList<>();
    Extension extensionForScheduleType = new Extension();
    extensionForScheduleType.setUrl("Schedule Type");
    extensionForScheduleType.setValueString(questionnaireBo.getScheduleType());
    Extension extensionForScheduleOption = new Extension();
    extensionForScheduleOption.setUrl("Schedule Option");
    extensionForScheduleOption.setValueString(questionnaireBo.getFrequency());
    if (questionnaireBo.getAnchorDateId() != null) {
      AnchorDateTypeBo dateTypeBo =
          (AnchorDateTypeBo)
              session
                  .getNamedQuery("getAnchorDateTypeById")
                  .setString("anchorDateTypeId", questionnaireBo.getAnchorDateId())
                  .uniqueResult();
      Extension extensionForAnchorDate = new Extension();
      extensionForAnchorDate.setUrl("Anchor Date");
      extensionForAnchorDate.setValueString(dateTypeBo.getName());
      extensionForScheduleData.add(extensionForAnchorDate);
    }
    extensionForScheduleData.add(extensionForScheduleOption);
    extensionForScheduleData.add(extensionForScheduleType);
    extensionForSchedule.setExtension(extensionForScheduleData);
    extensionsForSchedule.add(extensionForSchedule);

    Extension extensionForStudy = new Extension();
    extensionForStudy.setUrl("StudyMetaData");
    LinkedList<Extension> extensionForStudyData = new LinkedList<>();
    Extension extensionForStudyID = new Extension();
    extensionForStudyID.setUrl("StudyID");
    extensionForStudyID.setValueString(study.getCustomStudyId());
    Extension extensionForStudyName = new Extension();
    extensionForStudyName.setUrl("StudyName");
    extensionForStudyName.setValueString(study.getName());
    Extension extensionForStudyVersion = new Extension();
    extensionForStudyVersion.setUrl("StudyVersion");
    extensionForStudyVersion.setValueString(String.valueOf(study.getVersion()));
    extensionForStudyData.add(extensionForStudyID);
    extensionForStudyData.add(extensionForStudyName);
    extensionForStudyData.add(extensionForStudyVersion);
    extensionForStudy.setExtension(extensionForStudyData);
    extensionsForSchedule.add(extensionForStudy);
    return extensionsForSchedule;
  }

  public void createFhirStore(String datasetPath, String storePrefix, String studyId)
      throws Exception {

    try {
      fhirHealthcareAPIs.fhirStoreGet(datasetPath + FHIR_STORES + storePrefix + studyId);
    } catch (Exception e) {
      if (e instanceof GoogleJsonResponseException
          && ((GoogleJsonResponseException) e).getStatusCode() == 404
          && ((GoogleJsonResponseException) e).getStatusMessage().equals("Not Found")) {
        fhirHealthcareAPIs.fhirStoreCreate(datasetPath, storePrefix + studyId);
      }
    }
  }

  public ItemsQuestionnaire toQuestionDetails(
      Session session,
      QuestionnairesStepsBo questionnairesStepsBo,
      ItemsQuestionnaire items,
      QuestionsBo questionsBo) {

    List<AnswerOption> answerOptions = new ArrayList<>();
    List<Initial> initials = new ArrayList<>();
    if (questionsBo.getQuestion().equalsIgnoreCase("null")) {
      items.setText("");
    } else {
      items.setText(questionsBo.getQuestion());
    }

    boolean skippable =
        questionnairesStepsBo.getSkiappable().equalsIgnoreCase("Yes") ? true : false;
    items.setRequired(skippable);
    QuestionReponseTypeBo questionReponseTypeBo = null;
    questionReponseTypeBo =
        (QuestionReponseTypeBo)
            session
                .getNamedQuery("getQuestionResponse")
                .setString("questionsResponseTypeId", questionsBo.getId())
                .setMaxResults(1)
                .uniqueResult();
    List<Extension> extensionForMinMaxValue = new ArrayList<>();
    switch (questionsBo.getResponseType()) {
      case 1: // scale
        // After discussion with BA, we will not store all option values, only min, max and decimal.
        // answerOptions = this.formatQuestionScaleDetails(questionReponseTypeBo, items);
        extensionForMinMaxValue = setMinMaxValue(questionReponseTypeBo, questionsBo);
        items.setExtension(extensionForMinMaxValue);
        items.setType("choice");
        break;
      case 2: // continuous scale
        // After discussion with BA, we will not store all option values, only min, max and decimal.
        //  answerOptions = this.formatQuestionContinuousScaleDetails(questionReponseTypeBo);
        extensionForMinMaxValue = setMinMaxValue(questionReponseTypeBo, questionsBo);
        items.setExtension(extensionForMinMaxValue);
        items.setType("choice");
        break;
      case 3: // text scale
        answerOptions =
            this.formatQuestionChoiceDetails(questionReponseTypeBo, questionsBo, session);
        extensionForMinMaxValue = setStepSlider(questionReponseTypeBo, questionsBo);
        items.setExtension(extensionForMinMaxValue);
        items.setType("choice");
        break;
      case 4: // value picker
        answerOptions =
            this.formatQuestionChoiceDetails(questionReponseTypeBo, questionsBo, session);
        items.setType("choice");
        break;
      case 5: // image choice
        answerOptions =
            this.formatQuestionChoiceDetails(questionReponseTypeBo, questionsBo, session);
        items.setType("choice");
        break;
      case 6: // text choice
        answerOptions =
            this.formatQuestionTextChoiceDetails(
                questionnairesStepsBo, questionReponseTypeBo, questionsBo, session);

        if (StringUtils.isNoneEmpty(questionReponseTypeBo.getOtherIncludeText())
            && questionReponseTypeBo.getOtherIncludeText().equalsIgnoreCase("yes")) {
          items.setType("open-choice");
        } else {
          items.setType("choice");
        }

        break;
      case 7: // boolean
        answerOptions =
            this.formatQuestionChoiceDetails(questionReponseTypeBo, questionsBo, session);
        items.setType("choice");
        break;
      case 8: // numeric
        if (!questionReponseTypeBo.getPlaceholder().isEmpty() && answerOptions.isEmpty()) {
          initials = setPlaceholderValues(questionReponseTypeBo, initials);
        }
        extensionForMinMaxValue = setMinMaxValue(questionReponseTypeBo, questionsBo);
        items.setExtension(extensionForMinMaxValue);
        String type =
            questionReponseTypeBo.getStyle().equalsIgnoreCase("Decimal") ? "decimal" : "integer";
        items.setType(type);
        break;
      case 9: // time of the day
        items.setType("time");
        break;
      case 10: // date
        String typeForDate =
            questionReponseTypeBo.getStyle().equalsIgnoreCase("Date") ? "date" : "dateTime";
        items.setType(typeForDate);
        break;
      case 11: // text
        if (!questionReponseTypeBo.getPlaceholder().isEmpty() && answerOptions.isEmpty()) {
          initials = setPlaceholderValues(questionReponseTypeBo, initials);
        }
        items.setType("string");
        List<Extension> textExtensions = new ArrayList<>();
        if (questionReponseTypeBo.getValidationRegex() != null) {
          Extension textExtension = new Extension();
          textExtension.setUrl("http://hl7.org/fhir/StructureDefinition/regex");
          textExtension.setValueString(questionReponseTypeBo.getValidationRegex());
          textExtensions.add(textExtension);
        }
        items.setExtension(textExtensions);
        break;
      case 12: // email
        if (!questionReponseTypeBo.getPlaceholder().isEmpty() && answerOptions.isEmpty()) {
          initials = setPlaceholderValues(questionReponseTypeBo, initials);
        }
        items.setType("url");
        break;
      case 13: // time interval
        items.setType("time");
        break;
      case 14: // height
        if (!questionReponseTypeBo.getPlaceholder().isEmpty() && answerOptions.isEmpty()) {
          initials = setPlaceholderValues(questionReponseTypeBo, initials);
        }
        items.setType("decimal");
        break;
      case 15: // location
        items.setType("string");
        break;
      default:
        break;
    }

    if (!answerOptions.isEmpty()) {
      items.setAnswerOption(answerOptions);
    }
    if (!initials.isEmpty()) {
      items.setInitial(initials);
    }
    return items;
  }

  private List<AnswerOption> formatQuestionTextChoiceDetails(
      QuestionnairesStepsBo questionnairesStepsBo,
      QuestionReponseTypeBo questionReponseTypeBo,
      QuestionsBo questionsBo,
      Session session) {
    List<AnswerOption> options = new ArrayList<>();
    // get the response level attributes values of an
    // questions other inludes
    QuestionReponseTypeBo reponseTypeBo = null;
    logger.info(
        "StudyQuestionnaireDAOImpl - getQuestionnaireStep() - questionsResponseTypeId:"
            + questionsBo.getId());
    query =
        session
            .getNamedQuery("getQuestionResponse")
            .setString("questionsResponseTypeId", questionsBo.getId());
    query.setMaxResults(1);
    reponseTypeBo = (QuestionReponseTypeBo) query.uniqueResult();
    //// questions other inludes ends here
    List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
        session
            .getNamedQuery("getQuestionSubResponse")
            .setString("responseTypeId", questionsBo.getId())
            .list();
    if ((questionResponseSubTypeList != null) && !questionResponseSubTypeList.isEmpty()) {
      for (QuestionResponseSubTypeBo subType : questionResponseSubTypeList) {
        AnswerOption option = new AnswerOption();
        if (questionReponseTypeBo.getSelectionStyle() != null
            && questionReponseTypeBo.getSelectionStyle().equals("Multiple")) {
          List<Extension> extensionsForTextChoice = new ArrayList<>();
          Extension extension = new Extension();
          extension.setUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive");
          extension.setValueBoolean(subType.getExclusive().equals("Yes"));
          extensionsForTextChoice.add(extension);
          option.setExtension(extensionsForTextChoice);
        }

        option.setValueString(subType.getText());
        options.add(option);
      }
    }
    if (reponseTypeBo != null && StringUtils.isNotEmpty(reponseTypeBo.getOtherText())) {
      AnswerOption option = new AnswerOption();
      if (reponseTypeBo.getSelectionStyle() != null
          && reponseTypeBo.getSelectionStyle().equals("Multiple")) {
        List<Extension> extensionsForTextChoice = new ArrayList<>();
        Extension extension = new Extension();
        extension.setUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive");
        extension.setValueBoolean(reponseTypeBo.getOtherExclusive().equals("Yes"));
        extensionsForTextChoice.add(extension);
        option.setExtension(extensionsForTextChoice);
      }
      option.setValueString(reponseTypeBo.getOtherText());
      options.add(option);
    }
    return options;
  }

  private List<Extension> setStepSlider(
      QuestionReponseTypeBo questionReponseTypeBo, QuestionsBo questionsBo) {
    List<Extension> minMaxValueExtension = new ArrayList<>();
    if (questionsBo.getResponseType().equals(3)) {
      Extension extensionForSliderStepValue = new Extension();
      extensionForSliderStepValue.setUrl("DefaultSliderValue");
      extensionForSliderStepValue.setValueInteger(questionReponseTypeBo.getStep());
      // extensionForSliderStepValue.setValueString(questionReponseTypeBo.getDefaultValue());
      minMaxValueExtension.add(extensionForSliderStepValue);
    }
    return minMaxValueExtension;
  }

  private List<Extension> setMinMaxValue(
      QuestionReponseTypeBo questionReponseTypeBo, QuestionsBo questionsBo) {
    List<Extension> minMaxValueExtension = new ArrayList<>();
    Extension extensionForMinValue = new Extension();
    Extension extensionForMaxValue = new Extension();

    extensionForMinValue.setUrl("http://hl7.org/fhir/StructureDefinition/minValue");
    extensionForMaxValue.setUrl("http://hl7.org/fhir/StructureDefinition/maxValue");
    if ((questionReponseTypeBo.getStyle() != null
            && questionReponseTypeBo.getStyle().equalsIgnoreCase("Decimal"))
        || (questionsBo.getResponseType().equals(2)
            && questionReponseTypeBo.getMaxFractionDigits() != 0)) {
      extensionForMinValue.setValueDecimal(Double.parseDouble(questionReponseTypeBo.getMinValue()));
      extensionForMaxValue.setValueDecimal(Double.parseDouble(questionReponseTypeBo.getMaxValue()));
    } else {
      extensionForMinValue.setValueInteger(Integer.parseInt(questionReponseTypeBo.getMinValue()));
      extensionForMaxValue.setValueInteger(Integer.parseInt(questionReponseTypeBo.getMaxValue()));
    }

    if (questionReponseTypeBo.getMaxFractionDigits() != null) {
      Extension extensionForMaxDecimalPlaces = new Extension();
      extensionForMaxDecimalPlaces.setUrl(
          "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces");
      extensionForMaxDecimalPlaces.setValueInteger(questionReponseTypeBo.getMaxFractionDigits());
      minMaxValueExtension.add(extensionForMaxDecimalPlaces);
    }

    if (questionsBo.getResponseType().equals(1)) {
      Integer stepSize =
          (Integer.parseInt(questionReponseTypeBo.getMaxValue())
                  - Integer.parseInt(questionReponseTypeBo.getMinValue()))
              / questionReponseTypeBo.getStep();
      Extension extensionForSliderStepValue = new Extension();
      extensionForSliderStepValue.setUrl(
          "http://hl7.org/fhir/StructureDefinition/questionnaire-sliderStepValue");
      extensionForSliderStepValue.setValueInteger(stepSize);
      minMaxValueExtension.add(extensionForSliderStepValue);
    }
    if (questionsBo.getResponseType().equals(1) || questionsBo.getResponseType().equals(2)) {
      if (questionReponseTypeBo.getDefaultValue() != null) {
        Extension extensionFordefaultSlide = new Extension();
        extensionFordefaultSlide.setUrl("DefaultSliderValue");
        extensionFordefaultSlide.setValueString(questionReponseTypeBo.getDefaultValue());
        minMaxValueExtension.add(extensionFordefaultSlide);
      }
      if (questionReponseTypeBo.getMaxDescription() != null) {
        Extension extensionForMaxDescription = new Extension();
        extensionForMaxDescription.setUrl("Description for maximum value");
        extensionForMaxDescription.setValueString(questionReponseTypeBo.getMaxDescription());
        minMaxValueExtension.add(extensionForMaxDescription);
      }
      if (questionReponseTypeBo.getMinDescription() != null) {
        Extension extensionForMinDescription = new Extension();
        extensionForMinDescription.setUrl("Description for minimum value");
        extensionForMinDescription.setValueString(questionReponseTypeBo.getMinDescription());
        minMaxValueExtension.add(extensionForMinDescription);
      }
    }

    minMaxValueExtension.add(extensionForMinValue);
    minMaxValueExtension.add(extensionForMaxValue);
    return minMaxValueExtension;
  }

  private List<AnswerOption> formatQuestionContinuousScaleDetails(
      QuestionReponseTypeBo questionReponseTypeBo) {
    List<AnswerOption> options = new ArrayList<>();
    Double minValue = Double.valueOf(questionReponseTypeBo.getMinValue());
    Double maxValue = Double.valueOf(questionReponseTypeBo.getMaxValue());
    int fractionDigits = questionReponseTypeBo.getMaxFractionDigits();
    double n;
    if (fractionDigits == 0) {
      n = 1;
    } else if (fractionDigits == 1) {
      n = 0.1;
    } else if (fractionDigits == 2) {
      n = 0.01;
    } else if (fractionDigits == 3) { // 1.0 to 2.0
      n = 0.001;
    } else {
      n = 0.0001;
    }
    double count = 0;
    for (double i = minValue; i < maxValue; i = count) {
      AnswerOption option = new AnswerOption();
      if (count == 0.0) {
        count = i;
      } else {
        count = i + n;
      }
      count = Math.round(count * Math.pow(10, fractionDigits)) / Math.pow(10, fractionDigits);
      option.setValueString(String.valueOf(count));
      options.add(option);
    }
    return options;
  }

  private List<AnswerOption> formatQuestionChoiceDetails(
      QuestionReponseTypeBo questionReponseTypeBo, QuestionsBo questionsBo, Session session) {
    List<AnswerOption> options = new ArrayList<>();
    List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
        session
            .getNamedQuery("getQuestionSubResponse")
            .setString("responseTypeId", questionsBo.getId())
            .list();
    if ((questionResponseSubTypeList != null) && !questionResponseSubTypeList.isEmpty()) {
      for (QuestionResponseSubTypeBo subType : questionResponseSubTypeList) {
        AnswerOption option = new AnswerOption();
        if (questionReponseTypeBo.getSelectionStyle() != null
            && questionReponseTypeBo.getSelectionStyle().equals("Multiple")) {
          List<Extension> extensionsForTextChoice = new ArrayList<>();
          Extension extension = new Extension();
          extension.setUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive");
          extension.setValueBoolean(subType.getExclusive().equals("Yes"));
          extensionsForTextChoice.add(extension);
          option.setExtension(extensionsForTextChoice);
        }

        option.setValueString(subType.getText());
        options.add(option);
      }
    }
    return options;
  }

  public List<Initial> setPlaceholderValues(
      QuestionReponseTypeBo questionReponseTypeBo, List<Initial> initials) {
    Initial initial = new Initial();
    initial.setValueString(questionReponseTypeBo.getPlaceholder());
    initials.add(initial);
    return initials;
  }

  private List<AnswerOption> formatQuestionScaleDetails(
      QuestionReponseTypeBo questionReponseTypeBo, ItemsQuestionnaire items) {
    Integer minValue = Integer.valueOf(questionReponseTypeBo.getMinValue());
    Integer maxValue = Integer.valueOf(questionReponseTypeBo.getMaxValue());
    Integer step = questionReponseTypeBo.getStep();
    Integer stepSize = (maxValue - minValue) / step;
    List<AnswerOption> options = new ArrayList<>();
    for (int i = 0; i <= step; i++) {
      AnswerOption option = new AnswerOption();
      int total = (stepSize * i) + minValue;
      option.setValueInteger(total);
      options.add(option);
    }
    return options;
  }

  public EffectivePeriod getTimeDetailsOfQuestionnaire(
      Session session, QuestionnaireBo questionaire) {
    EffectivePeriod effectivePeriod = new EffectivePeriod();
    String startDateTime = "";
    String endDateTime = "";
    try {
      startDateTime =
          questionaire.getStudyLifetimeStart() + " " + FdahpStudyDesignerConstants.DEFAULT_MIN_TIME;

      if (StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

        // String startdate = questionaire.getStudyLifetimeStart();
        String startdate = "";
        if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())) {
          startdate = questionaire.getStudyLifetimeStart();
        } else {
          startdate = questionaire.getModifiedDate();
          startdate = startdate.substring(0, Math.min(startdate.length(), 10));
        }
        LocalDate dateTime = LocalDate.parse(startdate, formatter);
        dateTime = dateTime.plusYears(3);
        String threeYearsAfterString = dateTime.format(formatter);
        System.out.println(threeYearsAfterString);
        endDateTime = threeYearsAfterString;
      }

      endDateTime =
          StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())
              ? endDateTime + " " + FdahpStudyDesignerConstants.DEFAULT_MAX_TIME
              : questionaire.getStudyLifetimeEnd()
                  + " "
                  + FdahpStudyDesignerConstants.DEFAULT_MAX_TIME;
      if (StringUtils.isNotEmpty(questionaire.getFrequency())) {
        if ((questionaire
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME))
            || (questionaire
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_WEEKLY))
            || (questionaire
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MONTHLY))) {

          QuestionnairesFrequenciesBo questionnairesFrequency =
              (QuestionnairesFrequenciesBo)
                  session
                      .createQuery(
                          "from QuestionnairesFrequenciesBo QFDTO"
                              + " where QFDTO.questionnairesId=:quesRespId")
                      .setString("quesRespId", questionaire.getId())
                      .uniqueResult();
          if ((questionnairesFrequency != null)
              && StringUtils.isNotEmpty(questionnairesFrequency.getFrequencyTime())) {
            startDateTime =
                questionaire.getStudyLifetimeStart()
                    + " "
                    + questionnairesFrequency.getFrequencyTime();
            if (!questionaire
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)
                && !questionnairesFrequency.getIsStudyLifeTime()) {
              endDateTime =
                  questionaire.getStudyLifetimeEnd()
                      + " "
                      + questionnairesFrequency.getFrequencyTime();
            }
          }

          // test
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

          if (StringUtils.isNotBlank(questionaire.getStudyLifetimeStart())
              && StringUtils.isNotBlank(questionaire.getStudyLifetimeEnd())
              && sdf.parse(questionaire.getStudyLifetimeStart())
                  .before(sdf.parse(questionaire.getStudyLifetimeEnd()))) {

            if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())) {
              effectivePeriod.setStart(
                  FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      startDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            } else {
              effectivePeriod.setStart("");
            }

            if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())) {
              effectivePeriod.setEnd(
                  FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      endDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            }

          } else {
            effectivePeriod.setStart(
                StringUtils.isNotBlank(startDateTime)
                    ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                        startDateTime,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                    : null);
            effectivePeriod.setEnd(
                StringUtils.isNotBlank(endDateTime)
                    ? FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                        endDateTime,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                        FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)
                    : null);
          }

        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {

          List<QuestionnairesFrequenciesBo> questionnairesFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesBo QFDTO"
                          + " where QFDTO.questionnairesId=:quesRespId"
                          + " ORDER BY QFDTO.frequencyTime")
                  .setString("quesRespId", questionaire.getId())
                  .list();
          if ((questionnairesFrequencyList != null) && !questionnairesFrequencyList.isEmpty()) {
            startDateTime =
                questionaire.getStudyLifetimeStart()
                    + " "
                    + questionnairesFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                questionaire.getStudyLifetimeEnd()
                    + " "
                    + FdahpStudyDesignerConstants.DEFAULT_MAX_TIME;
          }

          if (StringUtils.isNotBlank(questionaire.getStudyLifetimeStart())) {
            effectivePeriod.setStart(
                FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                    startDateTime,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }

          if (StringUtils.isNotBlank(questionaire.getStudyLifetimeEnd())) {
            effectivePeriod.setEnd(
                FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                    endDateTime,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                    FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }

        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<QuestionnaireCustomScheduleBo> questionnaireCustomFrequencyList =
              session
                  .createQuery(
                      "from QuestionnaireCustomScheduleBo QCFDTO"
                          + " where QCFDTO.questionnairesId=:quesResId"
                          + " ORDER BY QCFDTO.frequencyStartDate, QCFDTO.frequencyStartTime")
                  .setString("quesResId", questionaire.getId())
                  .list();
          if ((questionnaireCustomFrequencyList != null)
              && !questionnaireCustomFrequencyList.isEmpty()) {

            String startDate = questionnaireCustomFrequencyList.get(0).getFrequencyStartDate();
            String endDate = questionnaireCustomFrequencyList.get(0).getFrequencyEndDate();

            for (QuestionnaireCustomScheduleBo customFrequency : questionnaireCustomFrequencyList) {
              if (null != startDate
                  && FdahpStudyDesignerConstants.SDF_DATE
                      .parse(startDate)
                      .after(
                          FdahpStudyDesignerConstants.SDF_DATE.parse(
                              customFrequency.getFrequencyStartDate()))) {
                startDate = customFrequency.getFrequencyStartDate();
              }

              if (null != endDate
                  && FdahpStudyDesignerConstants.SDF_DATE
                      .parse(endDate)
                      .before(
                          FdahpStudyDesignerConstants.SDF_DATE.parse(
                              customFrequency.getFrequencyEndDate()))) {
                endDate = customFrequency.getFrequencyEndDate();
              }
            }

            String frequencyStartTime =
                questionnaireCustomFrequencyList.get(0).getFrequencyStartTime();
            if (!frequencyStartTime.matches(
                "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
              frequencyStartTime = frequencyStartTime + ":00";
            }
            String frequencyEndTime =
                questionnaireCustomFrequencyList
                    .get(questionnaireCustomFrequencyList.size() - 1)
                    .getFrequencyEndTime();
            if (!frequencyEndTime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
              frequencyEndTime = frequencyEndTime + ":00";
            }

            startDateTime = startDate + " " + frequencyStartTime;
            endDateTime = endDate + " " + frequencyEndTime;

            if (StringUtils.isNotBlank(startDate)) {
              effectivePeriod.setStart(
                  FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      startDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            } else {
              effectivePeriod.setStart("");
            }

            if (StringUtils.isNotBlank(endDate)) {
              effectivePeriod.setEnd(
                  FdahpStudyDesignerUtil.getFormattedDateTimeZone(
                      endDateTime,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_PATTERN,
                      FdahpStudyDesignerConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error(
          "ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    return effectivePeriod;
  }
}
