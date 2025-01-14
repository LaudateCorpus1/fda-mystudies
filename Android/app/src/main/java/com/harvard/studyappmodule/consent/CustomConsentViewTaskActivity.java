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

package com.harvard.studyappmodule.consent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.harvard.R;
import com.harvard.eligibilitymodule.ComprehensionFailureActivity;
import com.harvard.eligibilitymodule.ComprehensionSuccessActivity;
import com.harvard.eligibilitymodule.CustomViewTaskActivity;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.StudyFragment;
import com.harvard.studyappmodule.StudyModulePresenter;
import com.harvard.studyappmodule.consent.model.ComprehensionCorrectAnswers;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.custom.StepSwitcherCustom;
import com.harvard.studyappmodule.enroll.EnrollData;
import com.harvard.studyappmodule.events.EnrollIdEvent;
import com.harvard.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.studyappmodule.events.UpdateEligibilityConsentStatusEvent;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.studyappmodule.studymodel.StudyUpdate;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.GetPreferenceEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.NetworkChangeReceiver;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantConsentDatastoreConfigEvent;
import com.harvard.webservicemodule.events.ParticipantEnrollmentDatastoreConfigEvent;
import com.harvard.webservicemodule.events.StudyDatastoreConfigEvent;
import io.github.lucasfsc.html2pdf.Html2Pdf;
import io.realm.Realm;
import io.realm.RealmList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.CipherInputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;
import org.researchstack.backbone.ui.step.layout.StepLayout;

public class CustomConsentViewTaskActivity extends AppCompatActivity
    implements StepCallbacks,
        ApiCall.OnAsyncRequestComplete,
        NetworkChangeReceiver.NetworkChangeCallback {
  private static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
  private static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
  private static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
  private static final String STUDYID = "ViewTaskActivity.studyID";
  private static final String ENROLLID = "ViewTaskActivity.enrollID";
  private static final String PDFTITLE = "ViewTaskActivity.pdfTitle";
  private static final String ELIGIBILITY = "ViewTaskActivity.eligibility";
  public static final String TYPE = "ViewTaskActivity.type";
  private CustomFirebaseAnalytics analyticsInstance;
  private NetworkChangeReceiver networkChangeReceiver;

  private StepSwitcherCustom root;
  private static final String FILE_FOLDER = "FDA_PDF";
  private static final int STUDY_UPDATES = 205;
  private boolean click = true;
  private boolean completionAdherenceStatus = true;
  private static final int ENROLL_ID_RESPONSECODE = 100;

  private Step currentStep;
  private Task task;
  private TaskResult taskResult;
  private Step nextStep;
  private String studyId;
  private String enrollId;
  private String pdfTitle;
  private String eligibility;
  public static final String CONSENT = "consent";
  private String type;
  private String participantId = "";
  private String hashToken = "";
  private String siteId = "";
  private int score = 0;
  private int passScore = 0;
  private Consent consent;
  private RealmList<ComprehensionCorrectAnswers> comprehensionCorrectAnswers;
  private Step previousStep;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
  private static final int GET_PREFERENCES = 2016;
  private static final int UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE = 101;
  private String enrolledDate;
  private String dataShared;
  private EligibilityConsent eligibilityConsent;
  private StudyList studyList;
  private String pdfPath;
  String sharingConsent = "Not Applicable";
  private String encode;

  public static Intent newIntent(
      Context context,
      Task task,
      String studyId,
      String enrollId,
      String pdfTitle,
      String eligibility,
      String type) {
    Intent intent = new Intent(context, CustomConsentViewTaskActivity.class);
    intent.putExtra(STUDYID, studyId);
    intent.putExtra(ENROLLID, enrollId);
    intent.putExtra(PDFTITLE, pdfTitle);
    intent.putExtra(ELIGIBILITY, eligibility);
    intent.putExtra(TYPE, type);
    return intent;
  }

  @SuppressLint("WrongViewCast")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setResult(RESULT_CANCELED);
    super.setContentView(R.layout.stepswitchercustom);

    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    Toolbar toolbar = (Toolbar) findViewById(org.researchstack.backbone.R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    root = (StepSwitcherCustom) findViewById(R.id.container);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
    studyList = dbServiceSubscriber.getStudiesDetails(getIntent().getStringExtra(STUDYID), realm);

    if (savedInstanceState == null) {

      studyId = getIntent().getStringExtra(STUDYID);
      pdfTitle = getIntent().getStringExtra(PDFTITLE);

      eligibilityConsent = dbServiceSubscriber.getConsentMetadata(studyId, realm);
      consent = eligibilityConsent.getConsent();
      networkChangeReceiver = new NetworkChangeReceiver(this);
      ConsentBuilder consentBuilder = new ConsentBuilder();
      List<Step> consentstep =
          consentBuilder.createsurveyquestion(
              CustomConsentViewTaskActivity.this, consent, pdfTitle);

      task = new OrderedTask(CONSENT, consentstep);
      enrollId = getIntent().getStringExtra(ENROLLID);

      eligibility = getIntent().getStringExtra(ELIGIBILITY);
      type = getIntent().getStringExtra(TYPE);
      taskResult = new TaskResult(task.getIdentifier());
      taskResult.setStartDate(new Date());
    } else {
      studyId = (String) savedInstanceState.getSerializable(STUDYID);
      pdfTitle = (String) savedInstanceState.getSerializable(PDFTITLE);

      eligibilityConsent = dbServiceSubscriber.getConsentMetadata(studyId, realm);
      consent = eligibilityConsent.getConsent();
      ConsentBuilder consentBuilder = new ConsentBuilder();
      List<Step> consentstep =
          consentBuilder.createsurveyquestion(
              CustomConsentViewTaskActivity.this, consent, pdfTitle);
      task = new OrderedTask(CONSENT, consentstep);

      enrollId = (String) savedInstanceState.getSerializable(ENROLLID);

      eligibility = (String) savedInstanceState.getSerializable(ELIGIBILITY);
      type = (String) savedInstanceState.getSerializable(TYPE);
      taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
      currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
    }

    task.validateParameters();

    if (currentStep == null) {
      currentStep = task.getStepAfterStep(null, taskResult);
    }

    comprehensionCorrectAnswers = consent.getComprehension().getCorrectAnswers();
    passScore = Integer.parseInt(consent.getComprehension().getPassScore());

    showStep(currentStep);
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }

  protected Step getCurrentStep() {
    return currentStep;
  }

  protected void showNextStep() {
    boolean shownext = true;
    if (shownext) {
      nextStep = task.getStepAfterStep(currentStep, taskResult);
      if (calcPassScore(currentStep, taskResult)) {
        score = score + 1;
      }
      if (nextStep == null) {
        saveAndFinish();
      } else {
        Bundle eventProperties = new Bundle();
        eventProperties.putString(
            CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
            getString(R.string.custom_consent_view_next));
        analyticsInstance.logEvent(CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
        String checkIdentifier;
        if (consent.getSharing().getTitle().equalsIgnoreCase("")
            && consent.getSharing().getText().equalsIgnoreCase("")
            && consent.getSharing().getShortDesc().equalsIgnoreCase("")
            && consent.getSharing().getLongDesc().equalsIgnoreCase("")) {
          checkIdentifier = "review";
        } else {
          checkIdentifier = "sharing";
        }

        if (consent.getComprehension().getQuestions().size() > 0
            && nextStep.getIdentifier().equalsIgnoreCase(checkIdentifier)) {
          if (score >= passScore) {
            Intent intent = new Intent(this, ComprehensionSuccessActivity.class);
            startActivityForResult(intent, 123);
          } else {
            Intent intent = new Intent(this, ComprehensionFailureActivity.class);
            intent.putExtra("enrollId", enrollId);
            intent.putExtra("studyId", studyId);
            intent.putExtra("title", pdfTitle);
            intent.putExtra("eligibility", eligibility);
            intent.putExtra("type", type);
            startActivity(intent);
            finish();
          }
        } else {
          if (nextStep.getIdentifier().equalsIgnoreCase("review")
              && !currentStep.getIdentifier().equalsIgnoreCase("sharing")) {
            if (score >= passScore) {
              Intent intent = new Intent(this, ComprehensionSuccessActivity.class);
              startActivityForResult(intent, 123);
            } else {
              Intent intent = new Intent(this, ComprehensionFailureActivity.class);
              intent.putExtra("enrollId", enrollId);
              intent.putExtra("studyId", studyId);
              intent.putExtra("title", pdfTitle);
              intent.putExtra("eligibility", eligibility);
              intent.putExtra("type", type);
              startActivity(intent);
              finish();
            }
          } else {
            showStep(nextStep);
          }
        }
      }
    } else {
      Toast.makeText(this, "You can't join study without sharing your data", Toast.LENGTH_SHORT)
          .show();
      finish();
    }
  }

  private boolean calcPassScore(Step currentStep, TaskResult taskResult) {
    ArrayList<String> answer = new ArrayList<>();
    for (int i = 0; i < comprehensionCorrectAnswers.size(); i++) {
      if (comprehensionCorrectAnswers
          .get(i)
          .getKey()
          .equalsIgnoreCase(currentStep.getIdentifier())) {
        Map<String, StepResult> map = taskResult.getResults();
        for (Map.Entry<String, StepResult> pair : map.entrySet()) {
          if (pair.getKey().equalsIgnoreCase(currentStep.getIdentifier())) {
            try {
              StepResult stepResult = pair.getValue();
              Object o = stepResult.getResults().get("answer");
              if (o instanceof Object[]) {
                Object[] objects = (Object[]) o;
                for (int j = 0; j < objects.length; j++) {
                  if (objects[j] instanceof String) {
                    for (int k = 0;
                        k < comprehensionCorrectAnswers.get(i).getAnswer().size();
                        k++) {
                      if (((String) objects[j])
                          .equalsIgnoreCase(
                              comprehensionCorrectAnswers.get(i).getAnswer().get(k).getAnswer())) {
                        answer.add("" + ((String) objects[j]));
                      }
                    }
                  }
                }
                if (comprehensionCorrectAnswers.get(i).getEvaluation().equalsIgnoreCase("all")) {
                  if (objects.length == comprehensionCorrectAnswers.get(i).getAnswer().size()
                      && answer.size() >= comprehensionCorrectAnswers.get(i).getAnswer().size()) {
                    return true;
                  }
                } else {
                  if (answer.size() > 0) {
                    for (int k = 0; k < answer.size(); k++) {
                      boolean correctAnswer = false;
                      for (int j = 0;
                          j < comprehensionCorrectAnswers.get(i).getAnswer().size();
                          j++) {
                        if (answer
                            .get(k)
                            .equalsIgnoreCase(
                                comprehensionCorrectAnswers
                                    .get(i)
                                    .getAnswer()
                                    .get(j)
                                    .getAnswer())) {
                          correctAnswer = true;
                        }
                      }
                      if (!correctAnswer) {
                        return false;
                      }
                    }
                    return true;
                  }
                }
              }
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        }
      }
    }
    return false;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 123) {
      showStep(nextStep);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.survey_menu, menu);
    MenuItem item = menu.findItem(R.id.action_settings);
    SpannableString s = new SpannableString(item.getTitle());
    s.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, s.length(), 0);
    item.setTitle(s);
    return true;
  }

  protected void showPreviousStep() {
    Bundle eventProperties = new Bundle();
    eventProperties.putString(
        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
        getString(R.string.custom_consent_view_back));
    analyticsInstance.logEvent(CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
    previousStep = task.getStepBeforeStep(currentStep, taskResult);
    if (previousStep == null) {
      finish();
    } else {
      if (currentStep.getIdentifier().equalsIgnoreCase("sharing")
          || ((currentStep.getIdentifier().equalsIgnoreCase("review")
              && !previousStep.getIdentifier().equalsIgnoreCase("sharing")))) {
        finish();
      } else {
        if (calcPassScore(previousStep, taskResult)) {
          score = score - 1;
        }
        showStep(previousStep);
      }
    }
  }

  private void showStep(Step step) {
    int currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult).getCurrent();
    int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();

    StepLayout stepLayout = getLayoutForStep(step);
    stepLayout.getLayout().getId();
    stepLayout
        .getLayout()
        .setTag(org.researchstack.backbone.R.id.rsb_step_layout_id, step.getIdentifier());
    root.show(
        stepLayout,
        newStepPosition >= currentStepPosition
            ? StepSwitcherCustom.SHIFT_LEFT
            : StepSwitcherCustom.SHIFT_RIGHT);

    currentStep = step;
    AppController.getHelperHideKeyboard(this);
  }

  protected StepLayout getLayoutForStep(Step step) {
    // Change the title on the activity
    String title = task.getTitleForStep(this, step);
    setActionBarTitle(title);

    // Get result from the TaskResult, can be null
    StepResult result = taskResult.getStepResult(step.getIdentifier());

    // Return the Class & constructor
    StepLayout stepLayout = createLayoutFromStep(step);
    stepLayout.initialize(step, result);
    stepLayout.setCallbacks(this);

    return stepLayout;
  }

  @NonNull
  private StepLayout createLayoutFromStep(Step step) {
    try {
      Class cls = step.getStepLayoutClass();
      Constructor constructor = cls.getConstructor(Context.class);
      return (StepLayout) constructor.newInstance(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void saveAndFinish() {

    taskResult.setEndDate(new Date());
    if (click) {
      click = false;
      new Handler()
          .postDelayed(
              new Runnable() {
                @Override
                public void run() {
                  click = true;
                }
              },
              3000);
      AppController.getHelperProgressDialog()
          .showProgress(CustomConsentViewTaskActivity.this, "", "", false);
      if (getIntent().getStringExtra(TYPE) != null
          && getIntent().getStringExtra(TYPE).equalsIgnoreCase("update")) {
        Studies studies =
            dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYID), realm);
        if (studies != null) {
          participantId = studies.getParticipantId();
          hashToken = studies.getHashedToken();
          siteId = studies.getSiteId();
        }
        completionAdherenceStatus = false;
        getStudySate();

      } else {
        enrollId();
      }
    }
  }

  private void enrollId() {

    HashMap<String, String> params = new HashMap<>();
    params.put("studyId", getIntent().getStringExtra(STUDYID));
    params.put("token", getIntent().getStringExtra(ENROLLID));

    HashMap<String, String> header = new HashMap<>();
    header.put(
        "userId",
        SharedPreferenceHelper.readPreference(
            CustomConsentViewTaskActivity.this, getString(R.string.userid), ""));
    header.put(
        "Authorization",
        "Bearer "
            + SharedPreferenceHelper.readPreference(
                CustomConsentViewTaskActivity.this, getString(R.string.auth), ""));

    ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent =
        new ParticipantEnrollmentDatastoreConfigEvent(
            "post_json",
            Urls.ENROLL_ID,
            ENROLL_ID_RESPONSECODE,
            CustomConsentViewTaskActivity.this,
            EnrollData.class,
            params,
            header,
            null,
            false,
            CustomConsentViewTaskActivity.this);
    EnrollIdEvent enrollIdEvent = new EnrollIdEvent();
    enrollIdEvent.setParticipantEnrollmentDatastoreConfigEvent(
        participantEnrollmentDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performEnrollId(enrollIdEvent);
  }

  public void updateuserpreference() {
    Studies studies = dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYID), realm);
    ConsentDocumentData consentDocumentData =
        dbServiceSubscriber.getConsentDocumentFromDB(getIntent().getStringExtra(STUDYID), realm);
    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(this, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(this, getResources().getString(R.string.userid), ""));

    JSONObject jsonObject = new JSONObject();

    JSONArray studieslist = new JSONArray();
    JSONObject studiestatus = new JSONObject();
    try {
      if (studies != null) {
        studiestatus.put("studyId", studies.getStudyId());
        studiestatus.put("status", StudyFragment.IN_PROGRESS);
      } else {
        studiestatus.put("studyId", getIntent().getStringExtra(STUDYID));
        studiestatus.put("status", StudyFragment.IN_PROGRESS);
      }
      if (participantId != null && !participantId.equalsIgnoreCase("")) {
        studiestatus.put("participantId", participantId);
      }
      if (hashToken != null && !hashToken.equalsIgnoreCase("")) {
        studiestatus.put("hashToken", hashToken);
      }
      if (siteId != null && !siteId.equalsIgnoreCase("")) {
        studiestatus.put("siteId", siteId);
      }
      if (completionAdherenceStatus) {
        studiestatus.put("completion", "0");
        studiestatus.put("adherence", "0");
      } else {
        completionAdherenceStatus = true;
      }
      studiestatus.put("userStudyVersion", consentDocumentData.getConsent().getVersion());
    } catch (JSONException e) {
      Logger.log(e);
    }

    studieslist.put(studiestatus);
    try {
      jsonObject.put("studies", studieslist);
    } catch (JSONException e) {
      Logger.log(e);
    }
    ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent =
        new ParticipantEnrollmentDatastoreConfigEvent(
            "post_object",
            Urls.UPDATE_STUDY_PREFERENCE,
            UPDATE_USERPREFERENCE_RESPONSECODE,
            this,
            LoginData.class,
            null,
            header,
            jsonObject,
            false,
            this);
    UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
    updatePreferenceEvent.setParticipantEnrollmentDatastoreConfigEvent(
        participantEnrollmentDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE) {
      LoginData loginData = (LoginData) response;
      if (loginData != null) {
        getStudyUpdateFomWS();
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT)
            .show();
      }
    } else if (responseCode == STUDY_UPDATES) {
      StudyUpdate studyUpdate = (StudyUpdate) response;
      AppController.getHelperProgressDialog().dismissDialog();

      AppController.getHelperSharedPreference()
          .writePreference(
              CustomConsentViewTaskActivity.this,
              getResources().getString(R.string.studyStatus),
              StudyFragment.IN_PROGRESS);

      Study study = dbServiceSubscriber.getStudyListFromDB(realm);
      dbServiceSubscriber.updateStudyWithStudyId(
          this, getIntent().getStringExtra(STUDYID), study, studyUpdate.getCurrentVersion());
      dbServiceSubscriber.updateStudyPreferenceVersionDB(
          this, getIntent().getStringExtra(STUDYID), studyUpdate.getCurrentVersion());

      dbServiceSubscriber.updateStudyPreferenceDB(
          this,
          getIntent().getStringExtra(STUDYID),
          StudyFragment.IN_PROGRESS,
          enrolledDate,
          participantId,
          siteId,
          hashToken,
          AppController.getHelperSharedPreference()
              .readPreference(
                  CustomConsentViewTaskActivity.this,
                  getResources().getString(R.string.studyVersion),
                  ""));
      dbServiceSubscriber.savePdfData(this, getIntent().getStringExtra(STUDYID), pdfPath);
      Intent resultIntent = new Intent();
      resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
      resultIntent.putExtra(TYPE, type);
      resultIntent.putExtra("PdfPath", pdfPath);
      setResult(RESULT_OK, resultIntent);
      finish();
    } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
      LoginData loginData = (LoginData) response;
      if (loginData != null) {
        getStudySate();

      } else {
        Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT)
            .show();
      }
    } else if (responseCode == GET_PREFERENCES) {
      StudyData studies = (StudyData) response;
      if (studies != null) {
        for (int i = 0; i < studies.getStudies().size(); i++) {
          if (getIntent()
              .getStringExtra(STUDYID)
              .equalsIgnoreCase(studies.getStudies().get(i).getStudyId())) {
            enrolledDate = studies.getStudies().get(i).getEnrolledDate();
            if (studies.getStudies().get(i).getDataSharingPermission() != null) {
              dataShared = studies.getStudies().get(i).getDataSharingPermission();
            }
          }
        }
        genarateConsentPdf();
      } else {
        Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT)
            .show();
      }
    } else if (responseCode == ENROLL_ID_RESPONSECODE) {
      EnrollData enrollData = (EnrollData) response;
      if (enrollData.getCode() == 200) {
        participantId = enrollData.getParticipantId();
        hashToken = enrollData.getHashedToken();
        siteId = enrollData.getSiteId();
        updateuserpreference();

      } else {
        Toast.makeText(this, enrollData.getMessage(), Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void getFile(String s) {
    File file = new File(s, FILE_FOLDER);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  private void genarateConsentPdf() {

    String filepath = "";
    try {

      try {
        StepResult result = taskResult.getStepResult("sharing");
        if (result != null) {
          JSONObject resultObj = new JSONObject(result.getResults());
          sharingConsent = resultObj.get("answer").toString();
        }
      } catch (Exception e) {
        Logger.log(e);
      }

      getFile("/data/data/" + getPackageName() + "/files/");

      StringBuilder docBuilder = new StringBuilder();
      if (eligibilityConsent != null
          && eligibilityConsent.getConsent() != null
          && eligibilityConsent.getConsent().getReview() != null
          && eligibilityConsent.getConsent().getReview().getReviewHTML() != null
          && !eligibilityConsent.getConsent().getReview().getReviewHTML().equalsIgnoreCase("")) {
        new StringBuilder("<br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
        String title = studyList.getTitle();
        docBuilder.append(
            String.format(
                "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>",
                title));
        docBuilder.append("</div><br>");

        docBuilder.append(
            Html.fromHtml(eligibilityConsent.getConsent().getReview().getReviewHTML().toString())
                .toString());
      } else if (eligibilityConsent != null
          && eligibilityConsent.getConsent() != null
          && eligibilityConsent.getConsent().getVisualScreens() != null) {

        if (eligibilityConsent.getConsent().getVisualScreens().size() > 0) {
          // Create our HTML to show the user and have them accept or decline.
          docBuilder =
              new StringBuilder("<br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
          String title = studyList.getTitle();
          docBuilder.append(
              String.format(
                  "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>",
                  title));

          docBuilder.append("</div><br>");
          for (int i = 0; i < eligibilityConsent.getConsent().getVisualScreens().size(); i++) {
            docBuilder.append(
                "<div>  <h4>"
                    + eligibilityConsent.getConsent().getVisualScreens().get(i).getTitle()
                    + "<h4> </div>");
            docBuilder.append(
                "<div>"
                    + Html.fromHtml(
                            eligibilityConsent.getConsent().getVisualScreens().get(i).getHtml())
                        .toString()
                    + "</div>");
          }
        } else {
          docBuilder.append("");
        }
      } else {
        docBuilder.append("");
      }
      StringBuilder agreeBuilder =
          new StringBuilder("<br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
      String participant = getResources().getString(R.string.participant);
      agreeBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", participant));
      String detail = getResources().getString(R.string.agree_participate_research_study);
      agreeBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));

      String formResult =
          new Gson()
              .toJson(
                  taskResult
                      .getStepResult(getResources().getString(R.string.signature_form_step))
                      .getResults());
      JSONObject formResultObj = new JSONObject(formResult);
      JSONObject fullNameObj = formResultObj.getJSONObject("First Name");
      JSONObject fullNameResult = fullNameObj.getJSONObject("results");

      JSONObject lastNameObj = formResultObj.getJSONObject("Last Name");
      JSONObject lastNameResult = lastNameObj.getJSONObject("results");
      String firstName = fullNameResult.getString("answer");
      String lastName = lastNameResult.getString("answer");
      String signatureDate =
          (String)
              taskResult
                  .getStepResult("Signature")
                  .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

      String signatureBase64 =
          (String)
              taskResult
                  .getStepResult("Signature")
                  .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

      docBuilder.append("<div style=\"page-break-after: always\">&nbsp;</div> ");
      docBuilder.append(String.format("<p>%1$s</p>", detail));
      docBuilder.append("<table width=\"100%\"><tr>");
      docBuilder.append("<td style=\"vertical-align:bottom\">");
      docBuilder.append(firstName).append(" ").append(lastName);
      docBuilder.append("</td>");
      docBuilder.append("<td style=\"align:centre\">");
      docBuilder
          .append("<img src=\"data:image/png;base64,")
          .append(signatureBase64)
          .append("\" alt=\"Red dot\" width=\"100\" height=\"100\" />");
      docBuilder.append("</td>");
      docBuilder.append("<td style=\"vertical-align:bottom\">");
      docBuilder.append(signatureDate);
      docBuilder.append("</td>");
      docBuilder.append("</tr>");

      docBuilder.append("<tr>");
      docBuilder.append("<td>(");
      docBuilder.append(getResources().getString(R.string.participans_name));
      docBuilder.append(")</td>");
      docBuilder.append("<td>(");
      docBuilder.append(getResources().getString(R.string.participants_signature));
      docBuilder.append(")</td>");
      docBuilder.append("<td>(");
      docBuilder.append(getResources().getString(R.string.date));
      docBuilder.append(")</td>");
      docBuilder.append("</tr>");
      docBuilder.append("</table>");
      docBuilder.append("<p><br/></p>");

      final String timeStamp = AppController.getDateFormatForConsentPdf();
      final File file = new File("/data/data/" + getPackageName() + "/files/", timeStamp + ".pdf");
      Html2Pdf converter =
          new Html2Pdf.Companion.Builder()
              .context(this)
              .html(docBuilder.toString())
              .file(file)
              .build();
      converter.convertToPdf(
          new Html2Pdf.OnCompleteConversion() {
            @Override
            public void onSuccess() {
              // encrypt the genarated pdf
              File encryptFile =
                  AppController.generateEncryptedConsentPdf(
                      "/data/data/" + getPackageName() + "/files/", timeStamp);

              file.delete();
              updateEligibilityConsent(encryptFile.getAbsolutePath());
            }

            @Override
            public void onFailed() {
              AppController.getHelperProgressDialog().dismissDialog();
              Toast.makeText(
                      CustomConsentViewTaskActivity.this,
                      R.string.pdf_consent_error,
                      Toast.LENGTH_SHORT)
                  .show();
            }
          });
    } catch (Exception e) {
      Logger.log(e);
      AppController.getHelperProgressDialog().dismissDialog();
      Toast.makeText(
              CustomConsentViewTaskActivity.this, R.string.pdf_consent_error, Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void getStudyUpdateFomWS() {
    AppController.getHelperProgressDialog()
        .showProgress(CustomConsentViewTaskActivity.this, "", "", false);
    GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
    DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
    HashMap<String, String> header = new HashMap();

    String url =
        Urls.STUDY_UPDATES
            + "?studyId="
            + getIntent().getStringExtra(STUDYID)
            + "&studyVersion="
            + studyList.getStudyVersion();
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            STUDY_UPDATES,
            CustomConsentViewTaskActivity.this,
            StudyUpdate.class,
            null,
            header,
            null,
            false,
            this);

    getUserStudyListEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
  }

  private void updateEligibilityConsent(String pdfPath) {
    this.pdfPath = pdfPath;
    HashMap headerparams = new HashMap();
    headerparams.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(CustomConsentViewTaskActivity.this, getString(R.string.auth), ""));
    headerparams.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(CustomConsentViewTaskActivity.this, getString(R.string.userid), ""));

    EligibilityConsent eligibilityConsent =
        dbServiceSubscriber.getConsentMetadata(getIntent().getStringExtra(STUDYID), realm);
    JSONObject body = new JSONObject();
    try {
      body.put("studyId", getIntent().getStringExtra(STUDYID));
      body.put("siteId", siteId);
      body.put("eligibility", true);
      if (encode != null && !encode.isEmpty()) {
        Log.e("check", "updateEligibilityConsent: " + encode.length());
        body.put("dataSharingScreenShot", convertFileToString(encode));
      }

      JSONObject consentbody = new JSONObject();
      consentbody.put("version", eligibilityConsent.getConsent().getVersion());
      consentbody.put("status", "Completed");
      consentbody.put("pdf", convertFileToString(pdfPath));

      body.put("consent", consentbody);

      String dataSharing =
          AppController.getHelperSharedPreference()
              .readPreference(this, "DataSharingScreen" + studyList.getTitle(), "");
      if (dataSharing.equalsIgnoreCase("false")) {
        if (!((consent.getSharing().getTitle().equalsIgnoreCase("")
            && consent.getSharing().getText().equalsIgnoreCase("")
            && consent.getSharing().getShortDesc().equalsIgnoreCase("")
            && consent.getSharing().getLongDesc().equalsIgnoreCase("")))) {
          sharingConsent = dataShared;
        }
      }
      body.put("sharing", sharingConsent);
    } catch (JSONException e) {
      Logger.log(e);
    }

    ParticipantConsentDatastoreConfigEvent participantConsentDatastoreConfigEvent =
        new ParticipantConsentDatastoreConfigEvent(
            "post_object",
            Urls.UPDATE_ELIGIBILITY_CONSENT,
            UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE,
            CustomConsentViewTaskActivity.this,
            LoginData.class,
            null,
            headerparams,
            body,
            false,
            CustomConsentViewTaskActivity.this);
    UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent =
        new UpdateEligibilityConsentStatusEvent();
    updateEligibilityConsentStatusEvent.setParticipantConsentDatastoreConfigEvent(
        participantConsentDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performUpdateEligibilityConsent(updateEligibilityConsentStatusEvent);
  }

  private String convertFileToString(String filepath) {
    CipherInputStream cis = AppController.generateDecryptedConsentPdf(filepath);
    byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
    return Base64.encodeToString(byteArray, Base64.DEFAULT);
  }

  private void getStudySate() {
    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(
                    CustomConsentViewTaskActivity.this,
                    getResources().getString(R.string.auth),
                    ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(
                CustomConsentViewTaskActivity.this, getResources().getString(R.string.userid), ""));
    header.put("deviceType", android.os.Build.MODEL);
    header.put("deviceOS", Build.VERSION.RELEASE);
    header.put("mobilePlatform", "ANDROID");
    ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent =
        new ParticipantEnrollmentDatastoreConfigEvent(
            "get",
            Urls.STUDY_STATE,
            GET_PREFERENCES,
            CustomConsentViewTaskActivity.this,
            StudyData.class,
            null,
            header,
            null,
            false,
            this);
    GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
    getPreferenceEvent.setParticipantEnrollmentDatastoreConfigEvent(
        participantEnrollmentDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performGetUserPreference(getPreferenceEvent);
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      AppController.getHelperSessionExpired(this, errormsg);
    } else if (responseCode == UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE) {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    } else if (responseCode == ENROLL_ID_RESPONSECODE) {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    } else if (responseCode == GET_PREFERENCES) {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onPause() {
    hideKeyboard();
    super.onPause();
    if (networkChangeReceiver != null) {
      unregisterReceiver(networkChangeReceiver);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      notifyStepOfBackPress();
      return true;
    } else if (item.getItemId() == R.id.action_settings) {
      Bundle eventProperties = new Bundle();
      eventProperties.putString(
          CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
          getString(R.string.custom_consent_view_exit));
      analyticsInstance.logEvent(CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    notifyStepOfBackPress();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(EXTRA_TASK, task);
    outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
    outState.putSerializable(EXTRA_STEP, currentStep);
    outState.putSerializable(STUDYID, studyId);
    outState.putSerializable(ENROLLID, enrollId);
    outState.putSerializable(PDFTITLE, pdfTitle);
    outState.putSerializable(ELIGIBILITY, eligibility);
    outState.putSerializable(TYPE, type);
  }

  private void notifyStepOfBackPress() {
    StepLayout currentStepLayout =
        (StepLayout) findViewById(org.researchstack.backbone.R.id.rsb_current_step);
    currentStepLayout.isBackEventConsumed();
  }

  @Override
  public void onSaveStep(int action, Step step, StepResult result) {
    onSaveStepResult(step.getIdentifier(), result);
    onExecuteStepAction(action);
  }

  protected void onSaveStepResult(String id, StepResult result) {
    taskResult.setStepResultForStepIdentifier(id, result);
    if (id.equalsIgnoreCase("sharing")) {
      Log.e("HTTPRequest", "onSaveStepResult: sharing");
      shareFunctionality();
    }
  }

  protected void onExecuteStepAction(int action) {
    if (action == StepCallbacks.ACTION_NEXT) {
      showNextStep();
    } else if (action == StepCallbacks.ACTION_PREV) {
      showPreviousStep();
    } else if (action == StepCallbacks.ACTION_END) {
      showConfirmExitDialog();
    } else if (action == StepCallbacks.ACTION_NONE) {
      // Used when onSaveInstanceState is called of a view. No action is taken.
    } else {
      throw new IllegalArgumentException(
          "Action with value "
              + action
              + " is invalid. "
              + "See StepCallbacks for allowable arguments");
    }
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (imm.isActive() && imm.isAcceptingText()) {
      imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
  }

  private void showConfirmExitDialog() {
    AlertDialog alertDialog =
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
            .setTitle(R.string.exit_message)
            .setMessage(org.researchstack.backbone.R.string.lorem_medium)
            .setPositiveButton(
                R.string.end_task,
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Bundle eventProperties = new Bundle();
                    eventProperties.putString(
                        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                        getString(R.string.custom_consent_view_end_task));
                    analyticsInstance.logEvent(
                        CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                    CustomConsentViewTaskActivity.this.finish();
                  }
                })
            .setNegativeButton(
                getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    Bundle eventProperties = new Bundle();
                    eventProperties.putString(
                        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                        getString(R.string.custom_consent_view_cancel));
                    analyticsInstance.logEvent(
                        CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                  }
                })
            .create();
    alertDialog.show();
  }

  @Override
  public void onCancelStep() {
    AlertDialog alertDialog =
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
            .setMessage(
                "Sorry, this study does not allow you to proceed for the selection you just made. Click OK to quit enrolling for the study or Cancel to change your selection.")
            .setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Bundle eventProperties = new Bundle();
                    eventProperties.putString(
                        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                        getString(R.string.custom_consent_view_ok));
                    analyticsInstance.logEvent(
                        CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                    setResult(12345);
                    finish();
                  }
                })
            .setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Bundle eventProperties = new Bundle();
                    eventProperties.putString(
                        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                        getString(R.string.custom_consent_view_cancel));
                    analyticsInstance.logEvent(
                        CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                    dialog.dismiss();
                  }
                })
            .create();
    alertDialog.show();
  }

  public void setActionBarTitle(String title) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(title);
    }
  }

  private void shareFunctionality() {

    Bitmap returnedBitmap =
        Bitmap.createBitmap(root.getWidth(), root.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    Drawable bgDrawable = root.getBackground();
    if (bgDrawable != null) {
      bgDrawable.draw(canvas);
    } else {
      canvas.drawColor(Color.WHITE);
    }
    root.draw(canvas);
    encode = AppController.getStringBase64(returnedBitmap);
    try {
      StringBuilder docBuilder = new StringBuilder();
      final String timeStamp = AppController.getDateFormatForConsentPdf();
      final File file = new File("/data/data/" + getPackageName() + "/files/", timeStamp + ".pdf");
      docBuilder
          .append("<img src=\"data:image/png;base64,")
          .append(encode)
          .append("\" alt=\"Red dot\" width=\"700\" height=\"1000\" />");
      Html2Pdf converter =
          new Html2Pdf.Companion.Builder()
              .context(this)
              .html(docBuilder.toString())
              .file(file)
              .build();
      converter.convertToPdf(
          new Html2Pdf.OnCompleteConversion() {
            @Override
            public void onSuccess() {
              // encrypt the genarated pdf
              File encryptFile =
                  AppController.generateEncryptedConsentPdf(
                      "/data/data/" + getPackageName() + "/files/", timeStamp);

              file.delete();
              encode = encryptFile.getAbsolutePath();
            }

            @Override
            public void onFailed() {
              AppController.getHelperProgressDialog().dismissDialog();
              Toast.makeText(
                      CustomConsentViewTaskActivity.this,
                      R.string.pdf_consent_error,
                      Toast.LENGTH_SHORT)
                  .show();
            }
          });
    } catch (Exception e) {
      Logger.log(e);
      AppController.getHelperProgressDialog().dismissDialog();
      Toast.makeText(
              CustomConsentViewTaskActivity.this, R.string.pdf_consent_error, Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      if (SharedPreferenceHelper.readPreference(
              CustomConsentViewTaskActivity.this, "offlineEnroll", "")
          .equalsIgnoreCase("")) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog =
            new androidx.appcompat.app.AlertDialog.Builder(
                CustomConsentViewTaskActivity.this, R.style.Style_Dialog_Rounded_Corner);
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
                SharedPreferenceHelper.writePreference(
                    CustomConsentViewTaskActivity.this, "offlineEnroll", "occured");
                dialogInterface.dismiss();
              }
            });
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
        dialog.show();
      }
    } else {
      SharedPreferenceHelper.writePreference(
          CustomConsentViewTaskActivity.this, "offlineEnroll", "");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(networkChangeReceiver, intentFilter);
  }
}
