/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.harvard.storagemodule;

import android.content.Context;

import com.harvard.AppConfig;
import com.harvard.notificationmodule.PendingIntents;
import com.harvard.notificationmodule.model.NotificationDb;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.events.DatabaseEvent;
import com.harvard.studyappmodule.SurveyActivitiesFragment;
import com.harvard.studyappmodule.activitybuilder.model.ActivityRun;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.ActivityObj;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.Steps;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.ActivityListData;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.custom.result.StepRecordCustom;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.studyappmodule.studymodel.ConsentPDF;
import com.harvard.studyappmodule.studymodel.ConsentPdfData;
import com.harvard.studyappmodule.studymodel.DashboardData;
import com.harvard.studyappmodule.studymodel.MotivationalNotification;
import com.harvard.studyappmodule.studymodel.NotificationData;
import com.harvard.studyappmodule.studymodel.NotificationDbResources;
import com.harvard.studyappmodule.studymodel.PendingIntentsResources;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyHome;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.studyappmodule.studymodel.StudyResource;
import com.harvard.studyappmodule.studymodel.StudyUpdate;
import com.harvard.studyappmodule.studymodel.StudyUpdateListdata;
import com.harvard.usermodule.model.Apps;
import com.harvard.usermodule.webservicemodel.Activities;
import com.harvard.usermodule.webservicemodel.ActivityData;
import com.harvard.usermodule.webservicemodel.ActivityRunPreference;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.usermodule.webservicemodel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.researchstack.backbone.task.Task;

public class DbServiceSubscriber {

  public static final String INSERT_AND_UPDATE_OPERATION = "insertAndUpdate";
  public static final String TYPE_COPY = "copy";
  public static final String TYPE_COPY_UPDATE = "copyAndUpdate";
  private Realm realm;

  public void updateStudyPreference(
          Context context, Studies studies, double completion, double adherence) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    studies.setCompletion((int) completion);
    studies.setAdherence((int) adherence);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void insert(Context context, DatabaseEvent event) {
    if (event.getOperation().equals(INSERT_AND_UPDATE_OPERATION)) {
      realm = AppController.getRealmobj(context);
      realm.beginTransaction();
      if (event.getType().equals(TYPE_COPY)) {
        realm.copyToRealm((RealmObject) event.getE());
      } else {
        realm.copyToRealmOrUpdate((RealmObject) event.getE());
      }
      realm.commitTransaction();
      closeRealmObj(realm);
    }
  }

  public ActivityListData getActivities(String studyId, Realm realm) {
    return realm.where(ActivityListData.class).equalTo("studyId", studyId).findFirst();
  }

  public MotivationalNotification getMotivationalNotification(String studyId, Realm realm) {
    return realm.where(MotivationalNotification.class).equalTo("studyId", studyId).findFirst();
  }

  public void saveMotivationalNotificationToDB(
          Context context, MotivationalNotification motivationalNotification) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(motivationalNotification);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void deleteMotivationalNotification(Context context, final String studyId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<MotivationalNotification> results =
                  realm.where(MotivationalNotification.class).equalTo("studyId", studyId).findAll();
          results.deleteAllFromRealm();
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteActivityWsList(
          Context context, ActivityListData activityListData, String activityId) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    for (int i = 0; i < activityListData.getActivities().size(); i++) {
      if (activityListData.getActivities().get(i).getActivityId().equals(activityId)) {
        activityListData.getActivities().remove(i);
        break;
      }
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void addActivityWsList(
          Context context, ActivityListData activityListData, ActivitiesWS activitiesWS) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    activityListData.getActivities().add(activitiesWS);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public ActivitiesWS getActivityItem(String studyId, String activityId, Realm realm) {
    ActivityListData activityListData = getActivities(studyId, realm);
    ActivitiesWS activitiesWS = null;
    if (activityListData != null && activityListData.getActivities() != null) {
      for (int i = 0; i < activityListData.getActivities().size(); i++) {
        if (activityListData.getActivities().get(i).getActivityId().equalsIgnoreCase(activityId)) {
          activitiesWS = activityListData.getActivities().get(i);
        }
      }
    }
    return activitiesWS;
  }

  public NotificationDb getNotificationDb(
          String activityId, String studyId, String type, Realm realm) {
    return realm
            .where(NotificationDb.class)
            .equalTo("activityId", activityId)
            .equalTo("studyId", studyId)
            .equalTo("type", type)
            .findFirst();
  }

  public RealmResults<NotificationDb> getNotificationDbByDate(Date startDate, Realm realm) {
    return realm
            .where(NotificationDb.class)
            .lessThanOrEqualTo("dateTime", startDate)
            .greaterThanOrEqualTo("endDateTime", startDate)
            .findAll();
  }

  public RealmResults<NotificationDb> getNotificationDbByCurrentDate(Realm realm) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    return realm
            .where(NotificationDb.class)
            .greaterThanOrEqualTo("dateTime", new Date())
            .lessThanOrEqualTo("dateTime", calendar.getTime())
            .findAll();
  }

  public RealmResults<NotificationDbResources> getNotificationDbResourcesByDate(
          Date startDate, Realm realm) {
    return realm
            .where(NotificationDbResources.class)
            .lessThanOrEqualTo("dateTime", startDate)
            .findAll();
  }

  public RealmResults<NotificationDbResources> getNotificationDbResources(
          String activityId, String studyId, String type, Realm realm) {
    try {
      return realm
              .where(NotificationDbResources.class)
              .equalTo("activityId", activityId)
              .equalTo("studyId", studyId)
              .equalTo("type", type)
              .sort("id", Sort.ASCENDING)
              .findAll();
    } catch (Exception e) {
      Logger.log(e);
      return null;
    }
  }

  // getUserPreference
  public StudyData getStudyPreference(Realm realm) {
    return realm.where(StudyData.class).findFirst();
  }

  public ActivityData getActivityPreference(String studyId, Realm realm) {
    return realm.where(ActivityData.class).equalTo("studyId", studyId).findFirst();
  }

  public Activities getActivityPreferenceBySurveyId(String studyId, String surveyId, Realm realm) {
    ActivityData activityData = getActivityPreference(studyId, realm);
    Activities activities = null;
    if (activityData != null && activityData.getActivities() != null) {
      for (int i = 0; i < activityData.getActivities().size(); i++) {
        if (activityData.getActivities().get(i).getStudyId().equalsIgnoreCase(studyId)
                && activityData.getActivities().get(i).getActivityId().equalsIgnoreCase(surveyId)) {
          activities = activityData.getActivities().get(i);
          break;
        }
      }
    }
    return activities;
  }

  public ActivityRun getActivityRunFromDB(
          String activityID, String studyId, Date currentDate, Realm realm) {
    return realm
            .where(ActivityRun.class)
            .equalTo("studyId", studyId)
            .equalTo("activityId", activityID)
            .greaterThanOrEqualTo("startDate", currentDate)
            .lessThanOrEqualTo("endDate", currentDate)
            .findFirst();
  }

  public RealmResults<ActivityRun> getAllActivityRunforDate(
          String activityID, String studyId, Date date, Realm realm) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 1);
    calendar.set(Calendar.AM_PM, Calendar.AM);

    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(date);
    calendar1.set(Calendar.HOUR, 11);
    calendar1.set(Calendar.MINUTE, 59);
    calendar1.set(Calendar.SECOND, 59);
    calendar1.set(Calendar.MILLISECOND, 99);
    calendar1.set(Calendar.AM_PM, Calendar.PM);

    return realm
            .where(ActivityRun.class)
            .equalTo("studyId", studyId)
            .equalTo("activityId", activityID)
            .greaterThanOrEqualTo("startDate", calendar.getTime())
            .lessThanOrEqualTo("endDate", calendar1.getTime())
            .findAll();
  }

  public int getActivityRunForStatsAndCharts(
          String activityID, String studyId, Date date, Realm realm) {
    RealmResults<ActivityRun> activityRuns = getAllActivityRunFromDB(studyId, activityID, realm);
    int run = 1;
    boolean available = false;
    for (int i = 0; i < activityRuns.size(); i++) {
      run = activityRuns.get(i).getRunId();
      if ((activityRuns.get(i).getStartDate().equals(date)
              || (date.after(activityRuns.get(i).getStartDate()))
              && ((activityRuns.get(i).getEndDate().equals(date)
              || date.before(activityRuns.get(i).getEndDate()))))) {
        available = true;
      }
      if (date.before(activityRuns.get(i).getEndDate())) {
        if (!available) {
          run = run - 1;
        }
        break;
      }
    }
    if (run <= 0) {
      run = 1;
    }
    return run;
  }

  public ActivityRun getPreviousActivityRunFromDB(
          String activityID, String studyId, Date currentDate, Realm realm) {
    return realm
            .where(ActivityRun.class)
            .equalTo("studyId", studyId)
            .equalTo("activityId", activityID)
            .lessThanOrEqualTo("endDate", currentDate)
            .sort("runId", Sort.DESCENDING)
            .findAll()
            .first(null);
  }

  public RealmResults<ActivityRun> getAllActivityRunFromDB(
          String studyId, String activityID, Realm realm) {
    return realm
            .where(ActivityRun.class)
            .equalTo("studyId", studyId)
            .equalTo("activityId", activityID)
            .sort("runId", Sort.ASCENDING)
            .findAll();
  }

  public void updateActivityRunToDB(Context context, String activityID, String studyId, int runId) {
    realm = AppController.getRealmobj(context);
    ActivityRun activityRun =
            realm
                    .where(ActivityRun.class)
                    .equalTo("studyId", studyId)
                    .equalTo("activityId", activityID)
                    .equalTo("runId", runId)
                    .findFirst();
    realm.beginTransaction();
    activityRun.setCompleted(true);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void saveStudyListToDB(Context context, Study study) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(study);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void savePendingIntentId(Context context, PendingIntents pendingIntents) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealm(pendingIntents);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void savePendingIntentIdResources(
          Context context, PendingIntentsResources pendingIntents) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealm(pendingIntents);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public RealmResults<PendingIntents> getPendingIntentId(Realm realm) {
    return realm.where(PendingIntents.class).findAll();
  }

  public RealmResults<PendingIntents> getPendingIntentIdByIds(
          Realm realm, String activityId, String studyId) {
    return realm
            .where(PendingIntents.class)
            .equalTo("studyId", studyId)
            .equalTo("activityId", activityId)
            .findAll();
  }

  public RealmResults<PendingIntentsResources> getPendingIntentIdResources(Realm realm) {
    return realm.where(PendingIntentsResources.class).findAll();
  }

  public RealmResults<PendingIntentsResources> getPendingIntentIdResourcesByIds(
          Realm realm, String activityId, String studyId) {
    return realm
            .where(PendingIntentsResources.class)
            .equalTo("studyId", studyId)
            .equalTo("activityId", activityId)
            .findAll();
  }

  public StudyData getStudyPreferences(Realm realm) {
    return realm.where(StudyData.class).findFirst();
  }

  // replaced saveUserPreferencesToDB
  public void saveStudyPreferencesToDB(Context context, StudyData studies) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(studies);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  // updateUserPreferenceToDb
  public void updateStudyPreferenceToDb(
          Context context,
          String lastUpdatedStudyId,
          String lastUpdatedStatusStatus) {
    realm = AppController.getRealmobj(context);
    Studies studies = realm.where(Studies.class).equalTo("studyId", lastUpdatedStudyId).findFirst();
    realm.beginTransaction();
    if (studies == null) {
      studies = new Studies();
      studies.setStudyId(lastUpdatedStudyId);
      studies.setStatus(lastUpdatedStatusStatus);

      StudyData studyData = getStudyPreferencesListFromDB(realm);
      if (studyData.getStudies() == null) {
        studyData.setStudies(new RealmList<Studies>());
      }
      studyData.getStudies().add(studies);
    } else {
      studies.setStatus(lastUpdatedStatusStatus);
    }

    realm.copyToRealmOrUpdate(studies);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateStudyPreferenceVersion(Context context, String version, Studies studies) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    if (studies != null) {
      studies.setVersion(version);
    }

    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateActivityPreferenceVersion(
          Context context, String version, Activities activities) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    if (activities != null) {
      activities.setActivityVersion(version);
    }

    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateNotificationToDb(Context context, NotificationDb notificationDb) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    if (notificationDb != null) {
      realm.copyToRealm(notificationDb);
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateNotificationToDb(Context context, NotificationDbResources notificationDb) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    if (notificationDb != null) {
      realm.copyToRealm(notificationDb);
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public Study getStudyListFromDB(Realm realm) {
    return realm.where(Study.class).findFirst();
  }

  // getUserPreferencesListFromDB
  public StudyData getStudyPreferencesListFromDB(Realm realm) {
    return realm.where(StudyData.class).findFirst();
  }

  private ActivityData getActivityPreferencesListFromDB(String studyId, Realm realm) {
    return realm.where(ActivityData.class).equalTo("studyId", studyId).findFirst();
  }

  public StudyList getStudiesDetails(String studyId, Realm realm) {
    return realm.where(StudyList.class).equalTo("studyId", studyId).findFirst();
  }

  public void saveStudyInfoToDB(Context context, StudyHome studyHome) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(studyHome);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void saveStudyDashboardToDB(Context context, DashboardData dashboardData) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(dashboardData);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void saveActivityState(ActivitiesWS activitiesWS, Realm realm) {
    realm.beginTransaction();
    activitiesWS.setState(SurveyActivitiesFragment.DELETE);
    realm.commitTransaction();
  }

  public DashboardData getDashboardDataFromDB(String studyId, Realm realm) {
    return realm.where(DashboardData.class).equalTo("studyId", studyId).findFirst();
  }

  public void deleteStudyInfoFromDb(Context context, final String studyId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<StudyHome> rows =
                  realm.where(StudyHome.class).equalTo("mStudyId", studyId).findAll();
          for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getInfo() != null) {
              rows.get(i).getInfo().deleteAllFromRealm();
            }
            if (rows.get(i).getAnchorDate() != null) {
              if (rows.get(i).getAnchorDate().getQuestionInfo() != null) {
                rows.get(i).getAnchorDate().getQuestionInfo().deleteFromRealm();
              }
              rows.get(i).getAnchorDate().deleteFromRealm();
            }
            if (rows.get(i).getBranding() != null) {
              rows.get(i).getBranding().deleteFromRealm();
            }
            if (rows.get(i).getWithdrawalConfig() != null) {
              rows.get(i).getWithdrawalConfig().deleteFromRealm();
            }
            rows.deleteFromRealm(i);
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteActivityRunsFromDb(
          Context context, final String activityId, final String studyId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<ActivityRun> rows =
                  realm
                          .where(ActivityRun.class)
                          .equalTo("activityId", activityId)
                          .equalTo("studyId", studyId)
                          .findAll();
          rows.deleteAllFromRealm();
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteActivityRunsFromDbByStudyID(Context context, final String studyId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<ActivityRun> rows =
                  realm.where(ActivityRun.class).equalTo("studyId", studyId).findAll();
          rows.deleteAllFromRealm();
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteResponseFromDb(final String studyId, Realm realm) {
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<StepRecordCustom> rows =
                  realm.where(StepRecordCustom.class).equalTo("studyId", studyId).findAll();
          if (rows != null) {
            while (rows.size() > 0) {
              if (rows.get(0).getTextChoices() != null) {
                rows.get(0).getTextChoices().deleteAllFromRealm();
              }
              rows.deleteFromRealm(0);
            }
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
  }

  public void deleteActivityObjectFromDb(
          Context context, final String activityId, final String studyId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<ActivityObj> rows =
                  realm
                          .where(ActivityObj.class)
                          .equalTo("surveyId", activityId)
                          .equalTo("studyId", studyId)
                          .findAll();
          for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getSteps() != null) {
              for (int j = 0; j < rows.get(i).getSteps().size(); j++) {
                if (rows.get(i).getSteps().get(j).getDestinations() != null) {
                  rows.get(i).getSteps().get(j).getDestinations().deleteAllFromRealm();
                }
                if (rows.get(i).getSteps().get(j).getSteps() != null) {
                  for (int k = 0; k < rows.get(i).getSteps().get(j).getSteps().size(); k++) {
                    rows.get(i)
                            .getSteps()
                            .get(j)
                            .getSteps()
                            .get(k)
                            .getFormat()
                            .deleteFromRealm();
                  }
                  rows.get(i).getSteps().get(j).getSteps().deleteAllFromRealm();
                }
                if (rows.get(i).getSteps().get(j).getFormat() != null) {
                  if (rows.get(i).getSteps().get(j).getFormat().getImageChoices() != null) {
                    rows.get(i)
                            .getSteps()
                            .get(j)
                            .getFormat()
                            .getImageChoices()
                            .deleteAllFromRealm();
                  }
                  if (rows.get(i).getSteps().get(j).getFormat().getTextChoices() != null) {
                    rows.get(i)
                            .getSteps()
                            .get(j)
                            .getFormat()
                            .getTextChoices()
                            .deleteAllFromRealm();
                  }
                  rows.get(i).getSteps().get(j).getFormat().deleteFromRealm();
                }
              }
              rows.get(i).getSteps().deleteAllFromRealm();
              rows.get(i).getSteps().deleteAllFromRealm();
            }
            rows.get(i).getMetadata().deleteFromRealm();
          }
          rows.deleteAllFromRealm();
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteAllRun(Context context, final RealmResults<ActivityRun> activityRuns) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          activityRuns.deleteAllFromRealm();
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteResponseDataFromDb(Context context, final String taskId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<StepRecordCustom> rows =
                  realm.where(StepRecordCustom.class).equalTo("taskId", taskId).findAll();
          for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getTextChoices() != null) {
              rows.get(i).getTextChoices().deleteAllFromRealm();
            }
          }
          rows.deleteAllFromRealm();
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public StudyHome getStudyInfoListFromDB(String studyId, Realm realm) {
    return realm.where(StudyHome.class).equalTo("mStudyId", studyId).findFirst();
  }

  public StepRecordCustom getResultFromDB(String taskStepID, Realm realm) {
    return realm.where(StepRecordCustom.class).equalTo("taskStepID", taskStepID).findFirst();
  }

  public StepRecordCustom getSurveyResponseFromDB(String activityID, String stepId, Realm realm) {
    return realm
            .where(StepRecordCustom.class)
            .equalTo("activityID", activityID)
            .equalTo("stepId", stepId)
            .findFirst();
  }

  public EligibilityConsent getConsentMetadata(String studyId, Realm realm) {
    return realm.where(EligibilityConsent.class).equalTo("studyId", studyId).findFirst();
  }

  public NotificationData getNotificationFromDB(Realm realm) {
    return realm.where(NotificationData.class).findFirst();
  }

  public void saveConsentDocumentToDB(Context context, ConsentDocumentData consentDocumentData) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(consentDocumentData);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public StudyUpdate getStudyUpdateById(String studyId, Realm realm) {
    return realm.where(StudyUpdate.class).equalTo("studyId", studyId).findFirst();
  }

  public void saveStudyUpdateListdataToDB(
          Context context, StudyUpdateListdata studyUpdateListdata) {
    realm = AppController.getRealmobj(context);
    StudyUpdateListdata studyUpdateListdata1 = realm.where(StudyUpdateListdata.class).findFirst();
    if (studyUpdateListdata1 == null) {
      realm.beginTransaction();
      realm.copyToRealmOrUpdate(studyUpdateListdata);
      realm.commitTransaction();
    } else {
      realm.beginTransaction();
      boolean available = false;
      if (studyUpdateListdata.getStudyUpdates() != null) {
        for (int i = 0; i < studyUpdateListdata1.getStudyUpdates().size(); i++) {
          if (studyUpdateListdata
                  .getStudyUpdates()
                  .get(0)
                  .getStudyId()
                  .equalsIgnoreCase(studyUpdateListdata1.getStudyUpdates().get(i).getStudyId())) {
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .setCurrentVersion(
                            studyUpdateListdata.getStudyUpdates().get(0).getCurrentVersion());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .setMessage(studyUpdateListdata.getStudyUpdates().get(0).getMessage());

            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getStudyUpdateData()
                    .setActivities(
                            studyUpdateListdata
                                    .getStudyUpdates()
                                    .get(0)
                                    .getStudyUpdateData()
                                    .isActivities());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getStudyUpdateData()
                    .setConsent(
                            studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().isConsent());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getStudyUpdateData()
                    .setInfo(
                            studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().isInfo());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getStudyUpdateData()
                    .setResources(
                            studyUpdateListdata
                                    .getStudyUpdates()
                                    .get(0)
                                    .getStudyUpdateData()
                                    .isResources());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getStudyUpdateData()
                    .setStatus(
                            studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().getStatus());

            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getUpdates()
                    .setActivities(
                            studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isActivities());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getUpdates()
                    .setStatus(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().getStatus());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getUpdates()
                    .setResources(
                            studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isResources());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getUpdates()
                    .setInfo(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isInfo());
            studyUpdateListdata1
                    .getStudyUpdates()
                    .get(i)
                    .getUpdates()
                    .setConsent(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isConsent());

            available = true;
            break;
          }
        }
      }
      if (!available) {
        studyUpdateListdata1.getStudyUpdates().add(studyUpdateListdata.getStudyUpdates().get(0));
      }
      realm.commitTransaction();
    }
    closeRealmObj(realm);
  }

  public NotificationDb updateNotificationNumber(NotificationDb notificationsDb, Realm realm) {
    realm.beginTransaction();
    int id = notificationsDb.getId();
    id++;
    notificationsDb.setId(id);
    realm.commitTransaction();
    return notificationsDb;
  }

  public NotificationDbResources updateNotificationNumberResources(
          NotificationDbResources notificationsDb, Realm realm) {
    realm.beginTransaction();
    int id = notificationsDb.getId();
    id++;
    notificationsDb.setId(id);
    realm.commitTransaction();
    return notificationsDb;
  }

  public StudyUpdate getStudyUpdatedataFromDB(String studyId, Realm realm) {
    return realm.where(StudyUpdate.class).equalTo("studyId", studyId).findFirst();
  }

  public ConsentDocumentData getConsentDocumentFromDB(String studyId, Realm realm) {
    return realm.where(ConsentDocumentData.class).equalTo("mStudyId", studyId).findFirst();
  }

  // UpdateUserPreferenceDB
  public void updateStudyPreferenceDB(
          Context context,
          String studyId,
          String status,
          String enrolleddate,
          String participantId,
          String siteId,
          String hashToken,
          String version) {
    realm = AppController.getRealmobj(context);
    Studies studies = realm.where(Studies.class).equalTo("studyId", studyId).findFirst();
    realm.beginTransaction();
    if (studies != null) {
      studies.setStatus(status);
      studies.setEnrolledDate(enrolleddate);
      studies.setVersion(version);
      studies.setParticipantId(participantId);
      studies.setSiteId(siteId);
      studies.setHashedToken(hashToken);
    } else {
      Studies studies1 = new Studies();
      studies1.setStudyId(studyId);
      studies1.setStatus(status);
      studies1.setVersion(version);
      studies1.setEnrolledDate(enrolleddate);
      studies1.setParticipantId(participantId);
      studies1.setParticipantId(participantId);
      studies1.setSiteId(siteId);
      studies1.setHashedToken(hashToken);
      StudyData studyData = getStudyPreferencesListFromDB(realm);
      studyData.getStudies().add(studies1);
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateStudyPreferenceVersionDB(Context context, String studyId, String version) {
    realm = AppController.getRealmobj(context);
    Studies studies = realm.where(Studies.class).equalTo("studyId", studyId).findFirst();
    realm.beginTransaction();
    if (studies != null) {
      studies.setVersion(version);
    } else {
      Studies studies1 = new Studies();
      studies1.setStudyId(studyId);
      studies1.setVersion(version);
      StudyData studyData = getStudyPreferencesListFromDB(realm);
      studyData.getStudies().add(studies1);
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateStudyWithddrawnDB(Context context, String studyId, String status) {
    realm = AppController.getRealmobj(context);
    Studies studies = realm.where(Studies.class).equalTo("studyId", studyId).findFirst();
    realm.beginTransaction();
    if (studies != null) {
      studies.setStatus(status);
      studies.setAdherence(0);
      studies.setCompletion(0);
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateStudyWithStudyId(Context context, String studyId, Study study, String version) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    for (int i = 0; i < study.getStudies().size(); i++) {
      if (study.getStudies().get(i).getStudyId().equalsIgnoreCase(studyId)) {
        study.getStudies().get(i).setStudyVersion(version);
        break;
      }
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void savePdfData(Context context, String studyId, String pdfPath) {
    realm = AppController.getRealmobj(context);
    ConsentPdfData consentPdfData;
    consentPdfData = realm.where(ConsentPdfData.class).equalTo("StudyId", studyId).findFirst();
    realm.beginTransaction();
    if (consentPdfData == null) {
      consentPdfData = new ConsentPdfData();
      consentPdfData.setStudyId(studyId);
    }
    consentPdfData.setPdfPath(pdfPath);
    realm.copyToRealmOrUpdate(consentPdfData);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  // updateUserPreferenceActivityDB
  public void updateActivityPreferenceDB(
          Context context,
          String activityId,
          String studyId,
          int runId,
          String status,
          int totalRun,
          int completedRun,
          int missedRun,
          String activityVersion) {
    realm = AppController.getRealmobj(context);
    Activities activities = getActivityPreferenceBySurveyId(studyId, activityId, realm);
    realm.beginTransaction();
    ActivityRunPreference activityRunPreference = new ActivityRunPreference();
    if (activities != null) {
      activities.setStatus(status);
      activities.setActivityRunId("" + runId);
      activities.getActivityRun().setCompleted(completedRun);
      activities.getActivityRun().setMissed(missedRun);
      activities.getActivityRun().setTotal(totalRun);
      activities.setActivityVersion(activityVersion);
    } else {
      Activities activities1 = new Activities();
      activities1.setStatus(status);
      activities1.setStudyId(studyId);
      activities1.setActivityId(activityId);
      activities1.setActivityRunId("" + runId);
      activities1.setActivityVersion(activityVersion);
      activityRunPreference.setTotal(totalRun);
      activityRunPreference.setCompleted(completedRun);
      activityRunPreference.setMissed(missedRun);
      activities1.setActivityRun(activityRunPreference);
      ActivityData activityData = getActivityPreferencesListFromDB(studyId, realm);
      if (activityData != null && activityData.getActivities() != null) {
        activityData.getActivities().add(activities1);
      } else if (activityData != null) {
        RealmList<Activities> activities2 = new RealmList<>();
        activities2.add(activities1);
        activityData.setActivities(activities2);
      } else {
        activityData = new ActivityData();
        RealmList<Activities> activities2 = new RealmList<>();
        activities2.add(activities1);
        activityData.setActivities(activities2);
        realm.copyToRealm(activityData);
      }
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public Studies getStudies(String studyId, Realm realm) {
    return realm.where(Studies.class).equalTo("studyId", studyId).findFirst();
  }

  public void deleteDb(Context context) {
    try {
      realm = AppController.getRealmobj(context);
      Apps apps = getApps(realm);
      Apps tempApps = null;
      realm.beginTransaction();
      if (apps != null) {
        tempApps = realm.copyFromRealm(apps);
      }
      realm.deleteAll();
      realm.commitTransaction();
      if (tempApps != null) {
        saveApps(context, tempApps);
      }
      closeRealmObj(realm);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public StepRecordCustom getSavedSteps(Task task, Realm realm) {
    return realm
            .where(StepRecordCustom.class)
            .equalTo("taskId", task.getIdentifier())
            .sort("id", Sort.DESCENDING)
            .findAll()
            .first(null);
  }

  public RealmResults<StepRecordCustom> getStepRecordCustom(Task task, Realm realm) {
    return realm.where(StepRecordCustom.class).equalTo("taskId", task.getIdentifier()).findAll();
  }

  public StepRecordCustom getStepRecordCustomById(
          String taskResultId, String stepResultId, Realm realm) {
    return realm
            .where(StepRecordCustom.class)
            .equalTo("taskStepID", taskResultId + "_" + stepResultId)
            .findFirst();
  }

  public Number getStepRecordCustomId(Realm realm) {
    return realm.where(StepRecordCustom.class).max("id");
  }

  public void updateStepRecord(Context context, StepRecordCustom stepRecordCustom) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(stepRecordCustom);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void updateActivityState(Context context, ActivityData activityData) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityData);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public ActivityObj getActivityBySurveyId(String studyId, String surveyId, Realm realm) {
    return realm
            .where(ActivityObj.class)
            .equalTo("studyId", studyId)
            .equalTo("surveyId", surveyId)
            .findFirst();
  }

  public Steps getSteps(String identifier, Realm realm) {
    return realm.where(Steps.class).equalTo("key", identifier).findFirst();
  }

  public Steps updateSteps(Steps step, String identifier, Realm realm) {
    Steps steps = new Steps();
    realm.beginTransaction();
    steps.setKey(identifier);
    steps.setDestinations(step.getDestinations());
    steps.setResultType(step.getResultType());
    steps.setText(step.getText());
    steps.setType(step.getType());
    steps.setFormat(step.getFormat());
    steps.setGroupName(step.getGroupName());
    steps.setRepeatable(step.isRepeatable());
    steps.setRepeatableText(step.getRepeatableText());
    steps.setSkippable(step.isSkippable());
    steps.setTitle(step.getTitle());
    realm.commitTransaction();
    return steps;
  }

  public void saveActivity(Context context, ActivityObj activityObj) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealm(activityObj);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void saveNotification(NotificationData notificationData, Realm realm) {
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(notificationData);
    realm.commitTransaction();
  }

  public RealmList<StudyList> saveStudyStatusToStudyList(
          RealmList<StudyList> studyListArrayList, Realm realm) {
    StudyData studyData = getStudyPreference(realm);
    if (studyData != null) {
      realm.beginTransaction();
      RealmList<Studies> userPreferenceStudies = studyData.getStudies();
      if (userPreferenceStudies != null) {
        for (int i = 0; i < userPreferenceStudies.size(); i++) {
          for (int j = 0; j < studyListArrayList.size(); j++) {
            if (userPreferenceStudies
                    .get(i)
                    .getStudyId()
                    .equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
              studyListArrayList.get(j).setStudyStatus(userPreferenceStudies.get(i).getStatus());
            }
          }
        }
      }
      realm.commitTransaction();
    }
    return studyListArrayList;
  }

  public RealmList<StudyList> clearStudyList(RealmList<StudyList> studyListArrayList, Realm realm) {
    realm.beginTransaction();
    studyListArrayList.clear();
    realm.commitTransaction();
    return studyListArrayList;
  }

  public RealmList<StudyList> updateStudyList(
          RealmList<StudyList> studyListArrayList, ArrayList<StudyList> studyLists, Realm realm) {
    realm.beginTransaction();
    studyListArrayList.addAll(studyLists);
    realm.commitTransaction();
    return studyListArrayList;
  }

  public ConsentPdfData getPdfPath(String studyId, Realm realm) {
    return realm.where(ConsentPdfData.class).equalTo("StudyId", studyId).findFirst();
  }

  public RealmResults<StepRecordCustom> getResult(
          String activityId, String key, Date startDate, Date endDate, Realm realm) {
    Calendar calendar = Calendar.getInstance();
    if (startDate != null) {
      calendar.setTime(startDate);
    } else {
      calendar.setTime(new Date());
    }
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.AM_PM, Calendar.AM);

    Calendar calendar1 = Calendar.getInstance();
    if (endDate != null) {
      calendar1.setTime(endDate);
    } else {
      calendar1.setTime(new Date());
    }
    calendar1.set(Calendar.HOUR, 11);
    calendar1.set(Calendar.MINUTE, 59);
    calendar1.set(Calendar.SECOND, 59);
    calendar1.set(Calendar.MILLISECOND, 0);
    calendar1.set(Calendar.AM_PM, Calendar.PM);

    return realm
            .where(StepRecordCustom.class)
            .equalTo("activityID", activityId)
            .equalTo("stepId", key)
            .sort("completed", Sort.ASCENDING)
            .findAll();
  }

  public RealmResults<StepRecordCustom> getResultForStat(
          String activityId, String key, Date startDate, Date endDate, Realm realm) {
    Calendar calendar = Calendar.getInstance();
    if (startDate != null) {
      calendar.setTime(startDate);
    } else {
      calendar.setTime(new Date());
    }
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.AM_PM, Calendar.AM);

    Calendar calendar1 = Calendar.getInstance();
    if (endDate != null) {
      calendar1.setTime(endDate);
    } else {
      calendar1.setTime(new Date());
    }
    calendar1.set(Calendar.HOUR, 11);
    calendar1.set(Calendar.MINUTE, 59);
    calendar1.set(Calendar.SECOND, 59);
    calendar1.set(Calendar.MILLISECOND, 0);
    calendar1.set(Calendar.AM_PM, Calendar.PM);

    return realm
            .where(StepRecordCustom.class)
            .equalTo("activityID", activityId)
            .equalTo("stepId", key)
            .greaterThanOrEqualTo("started", calendar.getTime())
            .lessThanOrEqualTo("completed", calendar1.getTime())
            .findAll();
  }

  public void saveResourceList(Context context, StudyResource studyResourceData) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(studyResourceData);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void deleteResourcesFromDb(Context context, final String studyId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<StudyResource> rows =
                  realm.where(StudyResource.class).equalTo("mStudyId", studyId).findAll();

          for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getResources() != null) {
              rows.get(i).getResources().deleteAllFromRealm();
            }
            rows.deleteFromRealm(i);
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public Studies getParticipantId(String studyId, Realm realm) {
    return realm.where(Studies.class).equalTo("studyId", studyId).findFirst();
  }

  public RealmResults<Studies> getAllStudyIds(Realm realm) {
    return realm.where(Studies.class).equalTo("status", "enrolled").findAll();
  }

  public StudyList getStudyTitle(String studyId, Realm realm) {
    return realm.where(StudyList.class).equalTo("studyId", studyId).findFirst();
  }

  public StudyHome getWithdrawalType(String studyId, Realm realm) {
    return realm.where(StudyHome.class).equalTo("mStudyId", studyId).findFirst();
  }

  public void deleteStudyResourceDuplicateRow(Context context, final String studyId) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        StudyResource root =
                realm.where(StudyResource.class).equalTo("mStudyId", studyId).findFirst();
        if (root != null) {
          if (root.getResources() != null) {
            root.getResources().deleteAllFromRealm();
          }
          root.deleteFromRealm();
        }
      }
    });
    closeRealmObj(realm);
  }

  public StudyResource getStudyResource(String studyId, Realm realm) {
    return realm.where(StudyResource.class).equalTo("mStudyId", studyId).findFirst();
  }

  public void saveConsentPdf(Context context, ConsentPDF consentPdf) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(consentPdf);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public ConsentPDF getConsentPdf(String studyId, Realm realm) {
    return realm.where(ConsentPDF.class).equalTo("studyId", studyId).findFirst();
  }

  // get unique id
  public int getUniqueID(Realm realm) {
    try {
      return realm.where(OfflineData.class).max("number").intValue();
    } catch (Exception e) {
      return 0;
    }
  }

  // save offline data
  public void saveOfflineData(Context context, OfflineData offlineData) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(offlineData);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public RealmResults<OfflineData> getOfflineData(Realm realm) {
    try {
      return realm.where(OfflineData.class).findAll();
    } catch (Exception e) {
      Logger.log(e);
    }
    return null;
  }

  // remove data from perticular index
  public void deleteOfflineDataRow(Context context, final int index) {
    realm = AppController.getRealmobj(context);

    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        OfflineData offlineData =
                realm.where(OfflineData.class).equalTo("number", index).findFirst();
        if (offlineData != null) {
          offlineData.deleteFromRealm();
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteActivityDataRow(Context context, final String studyId) {
    realm = AppController.getRealmobj(context);

    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        ActivityData activityData = getActivityPreference(studyId, realm);
        try {
          if (activityData != null) {

            for (int i = 0; i < activityData.getActivities().size(); i++) {
              if (activityData.getActivities().get(i).getActivityRun() != null) {
                activityData.getActivities().get(i).getActivityRun().deleteFromRealm();
              }
              activityData.getActivities().get(i).deleteFromRealm();
            }
            activityData.deleteFromRealm();
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  public void deleteActivityWsData(Context context, final String studyId) {
    realm = AppController.getRealmobj(context);

    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        ActivityListData activityListDataDB = getActivities(studyId, realm);
        try {
          if (activityListDataDB != null) {

            if (activityListDataDB.getActivities() != null) {
              for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {
                activityListDataDB.getActivities().get(i).deleteFromRealm();
              }
            }
            if (activityListDataDB.getAnchorDate() != null) {
              activityListDataDB.getAnchorDate().deleteFromRealm();
            }
            activityListDataDB.deleteFromRealm();
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  // remove data from 0th row
  public void removeOfflineData(Context context) {
    realm = AppController.getRealmobj(context);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        try {
          RealmResults<OfflineData> results = getOfflineData(realm);
          results.deleteFromRealm(0);
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    });
    closeRealmObj(realm);
  }

  // save UserProfile Data
  public void saveUserProfileData(Context context, UserProfileData userProfileData) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(userProfileData);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void saveApps(Context context, Apps apps) {
    realm = AppController.getRealmobj(context);
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(apps);
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public Apps getApps(Realm realm) {
    return realm.where(Apps.class).equalTo("appId", AppConfig.APP_ID_VALUE).findFirst();
  }

  public void deleteUserProfileDataDuplicateRow(Context context) {
    try {
      realm = AppController.getRealmobj(context);
      realm.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
          UserProfileData root =
                  realm.where(UserProfileData.class).equalTo("id", 1).findFirst();
          if (root != null) {
            if (root.getProfile() != null) {
              root.getProfile().deleteFromRealm();
            }
            if (root.getSettings() != null) {
              root.getSettings().deleteFromRealm();
            }
            root.deleteFromRealm();
          }
        }
      });
      closeRealmObj(realm);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public UserProfileData getUserProfileData(Realm realm) {
    return realm.where(UserProfileData.class).findFirst();
  }

  public OfflineData getUserIdOfflineData(String userId, Realm realm) {
    return realm.where(OfflineData.class).equalTo("userProfileId", userId).findFirst();
  }

  public OfflineData getStudyIdOfflineData(String studyId, Realm realm) {
    return realm.where(OfflineData.class).equalTo("studyId", studyId).findFirst();
  }

  public Resource getResource(String resourceId, Realm realm) {
    return realm.where(Resource.class).equalTo("resourcesId", resourceId).findFirst();
  }

  public OfflineData getActivityIdOfflineData(String activityId, Realm realm) {
    try {
      return realm.where(OfflineData.class).equalTo("activityId", activityId).findFirst();
    } catch (Exception e) {
      Logger.log(e);
    }
    return null;
  }

  public ActivitiesWS getActivityObj(String activityId, String studyId, Realm realm) {
    ActivityListData activityListData =
            realm.where(ActivityListData.class).equalTo("studyId", studyId).findFirst();
    if (activityListData != null) {
      return activityListData.getActivities().where().equalTo("activityId", activityId).findFirst();
    } else {
      return null;
    }
  }

  public void updateActivitiesWsVersion(
      String activityId, String studyId, Realm realm, final String version) {
    final ActivitiesWS activitiesWS = getActivityObj(activityId, studyId, realm);
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        if (activitiesWS != null) {
          activitiesWS.setActivityVersion(version);
        }
      }
    });
  }

  public void deleteStepRecord(Context context, String stepId) {
    realm = AppController.getRealmobj(context);
    StepRecordCustom stepRecordCustom = realm.where(StepRecordCustom.class)
        .equalTo("stepId", stepId).findFirst();

    realm.beginTransaction();
    if (stepRecordCustom != null) {
      stepRecordCustom.deleteFromRealm();
    }
    realm.commitTransaction();
    closeRealmObj(realm);
  }

  public void saveActivityStartTime(ActivitiesWS activitiesWS, Realm realm, String startTime) {
    realm.beginTransaction();
    activitiesWS.setStartTime(startTime);
    realm.commitTransaction();
  }

  public void saveActivityEndTime(ActivitiesWS activitiesWS, Realm realm, String endTime) {
    realm.beginTransaction();
    activitiesWS.setEndTime(endTime);
    realm.commitTransaction();
  }

  public void closeRealmObj(Realm realm) {
    if (realm != null && !realm.isClosed()) {
      realm.close();
      AppController.clearDBfile();
    }
  }
}
