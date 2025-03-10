/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
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
 */

package com.harvard.studyappmodule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.harvard.R;
import com.harvard.notificationmodule.model.NotificationDb;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.studyappmodule.studymodel.Notification;
import com.harvard.studyappmodule.studymodel.NotificationData;
import com.harvard.studyappmodule.studymodel.NotificationDbResources;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.webservicemodel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.StudyDatastoreConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {
  private static final int NOTIFICATIONS = 100;
  private RelativeLayout backBtn;
  private RecyclerView studyRecyclerView;
  private AppCompatTextView title;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private CustomFirebaseAnalytics analyticsInstance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_notification);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    AppController.getHelperSharedPreference()
        .writePreference(this, getString(R.string.notification), "false");
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvent();
    getNotification();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    studyRecyclerView = (RecyclerView) findViewById(R.id.studyRecyclerView);
  }

  private void setTextForView() {
    title.setText(getResources().getString(R.string.notifictions));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(this, "bold"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvent() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.notification_back));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });
  }

  private void setRecyclearView(RealmList<Notification> notifications) {

    studyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    studyRecyclerView.setNestedScrollingEnabled(false);
    NotificationListAdapter notificationListAdapter =
        new NotificationListAdapter(this, notifications, realm);
    studyRecyclerView.setAdapter(notificationListAdapter);
  }

  private void getNotification() {
    AppController.getHelperProgressDialog().showProgress(NotificationActivity.this, "", "", false);
    GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
    HashMap<String, String> header = new HashMap();
    String verificationTime = "";
    UserProfileData userProfileData = dbServiceSubscriber.getUserProfileData(realm);
    if (userProfileData != null && userProfileData.getProfile() != null)
      verificationTime = userProfileData.getProfile().getVerificationTime();
    String url = Urls.NOTIFICATIONS + "?skip=0" + "&verificationTime=" + verificationTime;
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            NOTIFICATIONS,
            this,
            NotificationData.class,
            null,
            header,
            null,
            false,
            this);

    getUserStudyListEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (response != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.MILLISECOND, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.HOUR, 0);
      RealmResults<NotificationDb> notificationsDb =
          dbServiceSubscriber.getNotificationDbByDate(new Date(), realm);
      RealmResults<NotificationDbResources> notificationsDbResources =
          dbServiceSubscriber.getNotificationDbResourcesByDate(new Date(), realm);
      RealmList<Notification> notifications = new RealmList<>();
      if (notificationsDb != null) {
        for (int i = 0; i < notificationsDb.size(); i++) {
          Notification notification1 = new Notification();
          notification1.setTitle(notificationsDb.get(i).getTitle());
          if (notificationsDb.get(i).getType().equalsIgnoreCase("resources")) {
            notification1.setSubtype("Resource");
          } else {
            notification1.setSubtype("Activity");
          }
          notification1.setStudyId(notificationsDb.get(i).getStudyId());
          notification1.setMessage(notificationsDb.get(i).getDescription());
          notification1.setDate(
              AppController.getDateFormatForApi().format(notificationsDb.get(i).getDateTime()));
          notification1.setType("Study");
          notification1.setAudience("");
          notifications.add(notification1);
        }
      }
      if (notificationsDbResources != null) {
        for (int i = 0; i < notificationsDbResources.size(); i++) {
          Notification notification1 = new Notification();
          notification1.setTitle(notificationsDbResources.get(i).getTitle());
          if (notificationsDbResources.get(i).getType().equalsIgnoreCase("resources")) {
            notification1.setSubtype("Resource");
          } else {
            notification1.setSubtype("Activity");
          }
          notification1.setStudyId(notificationsDbResources.get(i).getStudyId());
          notification1.setMessage(notificationsDbResources.get(i).getDescription());
          notification1.setDate(
              AppController.getDateFormatForApi()
                  .format(notificationsDbResources.get(i).getDateTime()));
          notification1.setType("Study");
          notification1.setAudience("");
          notifications.add(notification1);
        }
      }
      NotificationData notification = (NotificationData) response;
      for (int i = 0; i < notification.getNotifications().size(); i++) {
        if (notification.getNotifications().get(i).getType().equalsIgnoreCase("Study")) {

          Study study = dbServiceSubscriber.getStudyListFromDB(realm);
          if (study != null) {
            RealmList<StudyList> studyListArrayList = study.getStudies();
            for (int j = 0; j < studyListArrayList.size(); j++) {
              if (notification
                  .getNotifications()
                  .get(i)
                  .getStudyId()
                  .equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                if (studyListArrayList
                    .get(j)
                    .getStudyStatus()
                    .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                  notifications.add(notification.getNotifications().get(i));
                }
              }
            }
          }
        } else {
          notifications.add(notification.getNotifications().get(i));
        }
      }
      Collections.sort(
          notifications,
          new Comparator<Notification>() {
            public int compare(Notification o1, Notification o2) {
              try {
                return AppController.getDateFormatForApi()
                    .parse(o2.getDate())
                    .compareTo(AppController.getDateFormatForApi().parse(o1.getDate()));
              } catch (ParseException e) {
                Logger.log(e);
              }
              return -1;
            }
          });
      setRecyclearView(notifications);
      dbServiceSubscriber.saveNotification(notification, realm);
    } else {
      Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(NotificationActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(NotificationActivity.this, errormsg);
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.MILLISECOND, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.HOUR, 0);
      RealmResults<NotificationDb> notificationsDb =
          dbServiceSubscriber.getNotificationDbByDate(new Date(), realm);
      RealmResults<NotificationDbResources> notificationsDbResources =
          dbServiceSubscriber.getNotificationDbResourcesByDate(new Date(), realm);
      RealmList<Notification> notifications = new RealmList<>();
      if (notificationsDb != null) {
        for (int i = 0; i < notificationsDb.size(); i++) {
          Notification notification1 = new Notification();
          notification1.setTitle(notificationsDb.get(i).getTitle());
          if (notificationsDb.get(i).getType().equalsIgnoreCase("resources")) {
            notification1.setSubtype("Resource");
          } else {
            notification1.setSubtype("Activity");
          }
          notification1.setStudyId(notificationsDb.get(i).getStudyId());
          notification1.setMessage(notificationsDb.get(i).getDescription());
          notification1.setDate(
              AppController.getDateFormatForApi().format(notificationsDb.get(i).getDateTime()));
          notification1.setType("Study");
          notification1.setAudience("");
          notifications.add(notification1);
        }
      }
      if (notificationsDbResources != null) {
        for (int i = 0; i < notificationsDbResources.size(); i++) {
          Notification notification1 = new Notification();
          notification1.setTitle(notificationsDbResources.get(i).getTitle());
          if (notificationsDbResources.get(i).getType().equalsIgnoreCase("resources")) {
            notification1.setSubtype("Resource");
          } else {
            notification1.setSubtype("Activity");
          }
          notification1.setStudyId(notificationsDbResources.get(i).getStudyId());
          notification1.setMessage(notificationsDbResources.get(i).getDescription());
          notification1.setDate(
              AppController.getDateFormatForApi()
                  .format(notificationsDbResources.get(i).getDateTime()));
          notification1.setType("Study");
          notification1.setAudience("");
          notifications.add(notification1);
        }
      }
      NotificationData notification = dbServiceSubscriber.getNotificationFromDB(realm);
      if (notification != null) {
        if (!AppController.getHelperSharedPreference()
            .readPreference(this, getString(R.string.userid), "")
            .equalsIgnoreCase("")) {
          if (!AppController.isNetworkAvailable(this)) {
            AppController.offlineAlart(this);
          }
        } else {
          androidx.appcompat.app.AlertDialog.Builder alertDialog =
              new androidx.appcompat.app.AlertDialog.Builder(
                  NotificationActivity.this, R.style.Style_Dialog_Rounded_Corner);
          alertDialog.setTitle("              You are offline");
          alertDialog.setMessage("You are offline. Kindly check the internet connection.");
          alertDialog.setCancelable(false);
          alertDialog.setPositiveButton(
              "OK",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  Bundle eventProperties = new Bundle();
                  //          eventProperties.putString(
                  //              CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  //              getString(R.string.app_update_next_time_ok));
                  //          analyticsInstance.logEvent(
                  //              CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK,
                  // eventProperties);
                  dialogInterface.dismiss();
                }
              });
          final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
          dialog.show();
        }
        for (int i = 0; i < notification.getNotifications().size(); i++) {
          if (notification.getNotifications().get(i).getType().equalsIgnoreCase("Study")) {
            Study study = dbServiceSubscriber.getStudyListFromDB(realm);
            if (study != null) {
              RealmList<StudyList> studyListArrayList = study.getStudies();
              for (int j = 0; j < studyListArrayList.size(); j++) {
                if (notification
                    .getNotifications()
                    .get(i)
                    .getStudyId()
                    .equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                  if (studyListArrayList
                      .get(j)
                      .getStudyStatus()
                      .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                    notifications.add(notification.getNotifications().get(i));
                  }
                }
              }
            }
          } else {
            notifications.add(notification.getNotifications().get(i));
          }
        }
        Collections.sort(
            notifications,
            new Comparator<Notification>() {
              public int compare(Notification o1, Notification o2) {
                try {
                  return AppController.getDateFormatForApi()
                      .parse(o1.getDate())
                      .compareTo(AppController.getDateFormatForApi().parse(o2.getDate()));
                } catch (ParseException e) {
                  Logger.log(e);
                }
                return -1;
              }
            });
        setRecyclearView(notifications);
      } else {
        if (!AppController.isNetworkAvailable(this)) {
          androidx.appcompat.app.AlertDialog.Builder alertDialog =
              new androidx.appcompat.app.AlertDialog.Builder(
                  NotificationActivity.this, R.style.Style_Dialog_Rounded_Corner);
          alertDialog.setTitle("              You are offline");
          alertDialog.setMessage("You are offline. Kindly check the internet connection.");
          alertDialog.setCancelable(false);
          alertDialog.setPositiveButton(
              "OK",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  Bundle eventProperties = new Bundle();
                  //          eventProperties.putString(
                  //              CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  //              getString(R.string.app_update_next_time_ok));
                  //          analyticsInstance.logEvent(
                  //              CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK,
                  // eventProperties);
                  dialogInterface.dismiss();
                }
              });
          final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
          dialog.show();
        }
        Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }
}
