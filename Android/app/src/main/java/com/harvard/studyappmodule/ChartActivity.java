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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.activitybuilder.model.ActivityRun;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.custom.result.StepRecordCustom;
import com.harvard.studyappmodule.studymodel.ChartDataSource;
import com.harvard.studyappmodule.studymodel.DashboardData;
import com.harvard.studyappmodule.studymodel.RunChart;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.CustomMarkerView;
import com.harvard.utils.Logger;
import com.harvard.utils.NetworkChangeReceiver;
import com.harvard.utils.TempGraphHelper;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class ChartActivity extends AppCompatActivity
    implements NetworkChangeReceiver.NetworkChangeCallback {
  private LinearLayout chartlayout;
  private String[] day = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
  private String[] monthfull = {
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  };
  private String[] month = {"JAN", ".", ".", "APR", ".", ".", "JUL", ".", ".", "OCT", ".", "DEC"};

  private LinearLayout linearLayout1;
  private LinearLayout.LayoutParams layoutParams;

  private String dateType = "day";
  private static final String MONTH = "month";
  private static final String DAY = "day";
  private static final String WEEK = "week";
  private static final String YEAR = "year";
  private static final String RUN = "run";

  private DashboardData dashboardData;
  private ArrayList<String> fromDayVals = new ArrayList<>();
  private ArrayList<String> dateTypeArray = new ArrayList<>();
  private ArrayList<String> toDayVals = new ArrayList<>();
  RelativeLayout shareBtn;

  private Date starttime;
  private Date endtime;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private static final int PERMISSION_REQUEST_CODE = 2000;
  private CustomFirebaseAnalytics analyticsInstance;
  private NetworkChangeReceiver networkChangeReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chart);

    dbServiceSubscriber = new DbServiceSubscriber();
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    realm = AppController.getRealmobj(this);
    networkChangeReceiver = new NetworkChangeReceiver(this);
    dashboardData =
        dbServiceSubscriber.getDashboardDataFromDB(getIntent().getStringExtra("studyId"), realm);
    chartlayout = (LinearLayout) findViewById(R.id.chartlayout);
    RelativeLayout backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    shareBtn = (RelativeLayout) findViewById(R.id.mShareBtn);
    shareBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.chart_actvity_share));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            screenshotWritingPermission();
          }
        });
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.chart_actvity_back));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });
    if (dashboardData != null) {
      for (int i = 0; i < dashboardData.getDashboard().getCharts().size(); i++) {
        LinearLayout linearLayout = new LinearLayout(ChartActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        layoutParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(ChartActivity.this);
        LinearLayout.LayoutParams layoutParams =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(10, 10, 10, 10);
        textView.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        textView.setTextColor(Color.BLACK);
        textView.setText(dashboardData.getDashboard().getCharts().get(i).getDisplayName());

        final LineChart chart = new LineChart(ChartActivity.this);
        chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
        chart.setMarkerView(mv);

        final List<String> filteredXValues = new ArrayList<>();
        final List<Entry> entryList = new ArrayList<>();

        // Keep track fo maxMoleDiameter for TempGraphHelper.updateLineChart();
        float maxValue = 0;

        final ChartDataSource chartDataSource =
            dashboardData.getDashboard().getCharts().get(i).getDataSource();
        final RealmResults<StepRecordCustom> stepRecordCustomList;
        stepRecordCustomList =
            dbServiceSubscriber.getResult(
                getIntent().getStringExtra("studyId")
                    + "_STUDYID_"
                    + chartDataSource.getActivity().getActivityId(),
                chartDataSource.getKey(),
                null,
                null,
                realm);

        // Offset each xPosition by one to compensate for
        if (!chartDataSource.getTimeRangeType().equalsIgnoreCase("runs")
            && !chartDataSource.getTimeRangeType().equalsIgnoreCase("hours_of_day")) {
          String type;
          if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_week")) {
            type = WEEK;
          } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_month")) {
            type = MONTH;
          } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("weeks_of_month")) {
            type = MONTH;
          } else {
            type = YEAR;
          }
          addTimeLayout(
              type,
              chartDataSource.getTimeRangeType(),
              chart,
              stepRecordCustomList,
              filteredXValues,
              entryList,
              null,
              i,
              dashboardData
                  .getDashboard()
                  .getCharts()
                  .get(i)
                  .getConfiguration()
                  .getSettings()
                  .get(0)
                  .getBarColor(),
              chartDataSource.getActivity().getActivityId());

          for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
            if (stepRecordCustomList.get(j).getCompleted().before(endtime)
                && stepRecordCustomList.get(j).getCompleted().after(starttime)) {
              JSONObject jsonObject;
              String answer = "";
              String data = "";
              try {
                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);

                String[] id = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                if (activityObj.getType().equalsIgnoreCase("task")) {
                  JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                  answer = answerjson.getString("duration");
                  answer = Double.toString(Integer.parseInt(answer) / 60f);
                  data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                } else {
                  answer = jsonObject.getString("answer");
                  data = "";
                }

                if (answer == null || answer.equalsIgnoreCase("")) {
                  answer = "0";
                }
              } catch (JSONException e) {
                Logger.log(e);
                if (answer.equalsIgnoreCase("")) {
                  answer = "0";
                }
              }
              if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_week")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                filteredXValues.clear();
                for (int k = 0; k < day.length; k++) {
                  String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                  if (s.equalsIgnoreCase(day[k])) {
                    entryList.add(new Entry(Float.parseFloat(answer), k, data));
                  }
                  filteredXValues.add(day[k]);
                }
              } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_month")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d");
                int month = starttime.getMonth();
                int year = starttime.getYear();
                filteredXValues.clear();
                for (int k = 1; k <= numberOfDaysInMonth(month, year); k++) {
                  String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                  if (s.equalsIgnoreCase("" + k)) {
                    entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
                  }
                  if (k % 5 == 0) {
                    filteredXValues.add("" + k);
                  } else {
                    filteredXValues.add("");
                  }
                }
              } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("weeks_of_month")) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.SUNDAY);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                filteredXValues.clear();
                int weekno = numberOfWeeksInMonth(simpleDateFormat.format(starttime));
                for (int k = 1; k <= weekno; k++) {
                  cal.setTime(stepRecordCustomList.get(j).getCompleted());
                  int week = cal.get(Calendar.WEEK_OF_MONTH);
                  if (k == week) {
                    entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
                  }
                  filteredXValues.add("W" + k);
                }
              } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("months_of_year")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                filteredXValues.clear();
                for (int k = 0; k < month.length; k++) {
                  String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                  if (s.equalsIgnoreCase(monthfull[k])) {
                    entryList.add(new Entry(Float.parseFloat(answer), k, data));
                  }
                  filteredXValues.add(month[k]);
                }
              }
            }
          }
        } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("runs")) {
          linearLayout1 = new LinearLayout(ChartActivity.this);
          SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
          dateType = RUN;
          dateTypeArray.add(RUN);
          fromDayVals.add(simpleDateFormat.format(new Date()));
          toDayVals.add(simpleDateFormat.format(new Date()));
          filteredXValues.clear();
          ArrayList<RunChart> runCharts = new ArrayList<>();
          List<List<RunChart>> lists = new ArrayList<>();
          RealmResults<ActivityRun> activityRuns =
              dbServiceSubscriber.getAllActivityRunFromDB(
                  getIntent().getStringExtra("studyId"),
                  chartDataSource.getActivity().getActivityId(),
                  realm);
          for (int k = 0; k < activityRuns.size(); k++) {
            lists.clear();
            Date runCompletedDate = null;
            String runAnswer = null;
            String runAnswerData = null;
            for (int l = 0; l < stepRecordCustomList.size(); l++) {
              if (stepRecordCustomList.get(l).taskId.contains("_")) {
                String[] taskId = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
                String runId =
                    taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());
                JSONObject jsonObject;
                String answer = "";
                String data = "";
                try {
                  jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                  String[] id = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                  ActivitiesWS activityObj =
                      dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                  if (activityObj.getType().equalsIgnoreCase("task")) {
                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                    answer = answerjson.getString("duration");
                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                  } else {
                    answer = jsonObject.getString("answer");
                    data = "";
                  }
                  if (answer == null || answer.equalsIgnoreCase("")) {
                    answer = "0";
                  }
                } catch (JSONException e) {
                  Logger.log(e);
                  if (answer.equalsIgnoreCase("")) {
                    answer = "0";
                  }
                }

                if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {

                  runAnswer = answer;
                  runAnswerData = data;
                  runCompletedDate = stepRecordCustomList.get(l).completed;
                  break;
                }
              }
            }
            RunChart runChart = new RunChart();
            runChart.setCompletedDate(runCompletedDate);
            runChart.setResult(runAnswer);
            runChart.setResultData(runAnswerData);
            runChart.setRunId("" + activityRuns.get(k).getRunId());
            runChart.setStartDate(activityRuns.get(k).getStartDate());
            runChart.setEnddDate(activityRuns.get(k).getEndDate());
            runCharts.add(runChart);
          }
          // new chart
          lists = split(runCharts, 5);
          filteredXValues.clear();
          entryList.clear();
          if (lists.size() > 0) {
            for (int l = 0; l < lists.get(0).size(); l++) {
              if (lists.get(0).get(l).getResult() != null) {
                entryList.add(
                    new Entry(
                        Float.parseFloat(lists.get(0).get(l).getResult()),
                        Integer.parseInt(lists.get(0).get(l).getRunId()) - 1,
                        lists.get(0).get(l).getResultData()));
              }
              filteredXValues.add("" + (Integer.parseInt(lists.get(0).get(l).getRunId())));
            }
          }
          addTimeLayoutRuns(
              lists,
              chartDataSource.getTimeRangeType(),
              chart,
              stepRecordCustomList,
              filteredXValues,
              entryList,
              activityRuns,
              i,
              dashboardData
                  .getDashboard()
                  .getCharts()
                  .get(i)
                  .getConfiguration()
                  .getSettings()
                  .get(0)
                  .getBarColor(),
              chartDataSource.getActivity().getActivityId(),
              0);
        } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("hours_of_day")) {
          final RealmResults<ActivityRun> activityRuns =
              dbServiceSubscriber.getAllActivityRunforDate(
                  chartDataSource.getActivity().getActivityId(),
                  getIntent().getStringExtra("studyId"),
                  new Date(),
                  realm);
          addTimeLayout(
              DAY,
              chartDataSource.getTimeRangeType(),
              chart,
              stepRecordCustomList,
              filteredXValues,
              entryList,
              activityRuns,
              i,
              dashboardData
                  .getDashboard()
                  .getCharts()
                  .get(i)
                  .getConfiguration()
                  .getSettings()
                  .get(0)
                  .getBarColor(),
              chartDataSource.getActivity().getActivityId());
          filteredXValues.clear();
          for (int k = 0; k < activityRuns.size(); k++) {
            for (int l = 0; l < stepRecordCustomList.size(); l++) {
              if (stepRecordCustomList.get(l).getCompleted().before(endtime)
                  && stepRecordCustomList.get(l).getCompleted().after(starttime)) {
                if (stepRecordCustomList.get(l).taskId.contains("_")) {
                  String[] taskId = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
                  String runId =
                      taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());

                  JSONObject jsonObject;
                  String answer = "";
                  String data = "";
                  try {
                    jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                    String[] id = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                    ActivitiesWS activityObj =
                        dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                    if (activityObj.getType().equalsIgnoreCase("task")) {
                      JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                      answer = answerjson.getString("duration");
                      answer = Double.toString(Integer.parseInt(answer) / 60f);
                      data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                    } else {
                      answer = jsonObject.getString("answer");
                      data = "";
                    }
                    if (answer == null || answer.equalsIgnoreCase("")) {
                      answer = "0";
                    }
                  } catch (JSONException e) {
                    Logger.log(e);
                    if (answer.equalsIgnoreCase("")) {
                      answer = "0";
                    }
                  }

                  if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {
                    entryList.add(new Entry(Float.parseFloat(answer), k, data));
                  }
                }
              }
            }
            filteredXValues.add("" + (k + 1));
          }
        }
        // Update chart w/ our data
        TempGraphHelper.updateLineChart(
            chart,
            (int) maxValue,
            entryList,
            filteredXValues,
            dashboardData
                .getDashboard()
                .getCharts()
                .get(i)
                .getConfiguration()
                .getSettings()
                .get(0)
                .getBarColor());

        // Move to "end" of chart
        linearLayout.addView(textView);
        if (linearLayout1 != null) {
          linearLayout.addView(linearLayout1);
        }
        linearLayout.addView(chart);
        chartlayout.addView(linearLayout);
      }
    }
  }

  private void screenshotWritingPermission() {
    // checking the permissions
    if ((ActivityCompat.checkSelfPermission(
                ChartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        || (ActivityCompat.checkSelfPermission(
                ChartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)) {
      String[] permission =
          new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
          };
      if (!hasPermissions(permission)) {
        ActivityCompat.requestPermissions(
            (Activity) ChartActivity.this, permission, PERMISSION_REQUEST_CODE);
      } else {
        // sharing pdf creating
        shareFunctionality();
      }
    } else {
      // sharing pdf creating
      shareFunctionality();
    }
  }

  public boolean hasPermissions(String[] permissions) {
    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.M && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(ChartActivity.this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
          Toast.makeText(
                  ChartActivity.this,
                  getResources().getString(R.string.permission_enable_message_screenshot),
                  Toast.LENGTH_LONG)
              .show();
        } else {
          shareFunctionality();
        }
        break;
    }
  }

  private void shareFunctionality() {

    Bitmap returnedBitmap =
        Bitmap.createBitmap(
            chartlayout.getWidth(), chartlayout.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    Drawable bgDrawable = chartlayout.getBackground();
    if (bgDrawable != null) {
      bgDrawable.draw(canvas);
    } else {
      canvas.drawColor(Color.WHITE);
    }
    chartlayout.draw(canvas);

    saveBitmap(returnedBitmap);
  }

  private void saveBitmap(Bitmap bitmap) {
    String root;
    if (Build.VERSION.SDK_INT < VERSION_CODES.Q) {
      root = Environment.getExternalStorageDirectory().getAbsolutePath();
    } else {
      root = getExternalFilesDir(getString(R.string.app_name)).getAbsolutePath();
    }
    File dir = new File(root + "/Android/FDA/Screenshot");
    dir.mkdirs();
    String fname = getIntent().getStringExtra("studyName")
        .replace("/", "\u2215") + "_Chart.png";
    File file = new File(dir, fname);
    if (file.exists()) {
      file.delete();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      Logger.log(e);
    }
    sendMail(file, fname.split("\\.")[0]);
  }

  public void sendMail(File file, String subject) {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setData(Uri.parse("mailto:"));
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    shareIntent.setType("text/plain");
    Uri fileUri =
        FileProvider.getUriForFile(
            ChartActivity.this, getString(R.string.FileProvider_authorities), file);
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
    startActivity(shareIntent);
  }

  public <RunChart extends Object> List<List<RunChart>> split(
      ArrayList<RunChart> list, int targetSize) {
    List<List<RunChart>> lists = new ArrayList<List<RunChart>>();
    for (int i = 0; i < list.size(); i += targetSize) {
      lists.add(list.subList(i, Math.min(i + targetSize, list.size())));
    }
    return lists;
  }

  public static int numberOfDaysInMonth(int month, int year) {
    Calendar monthStart = new GregorianCalendar(year, month, 1);
    return monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  public int numberOfWeeksInMonth(String monthval) {
    SimpleDateFormat format = new SimpleDateFormat("MMM");
    Date date = null;
    try {
      date = format.parse(monthval);
    } catch (ParseException e) {
      Logger.log(e);
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    int start = c.get(Calendar.WEEK_OF_MONTH);
    c.add(Calendar.MONTH, 1);
    c.add(Calendar.DATE, -1);
    int end = c.get(Calendar.WEEK_OF_MONTH);

    return (end - start + 1);
  }

  private void addTimeLayoutRuns(
      final List<List<RunChart>> lists,
      String charttype,
      final LineChart chart,
      final RealmResults<StepRecordCustom> stepRecordCustomList,
      final List<String> filteredXValues,
      final List<Entry> entryList,
      final RealmResults<ActivityRun> activityRuns,
      int position,
      final String barColor,
      String activityId,
      int index) {
    if (lists.size() > 0) {
      linearLayout1 = new LinearLayout(ChartActivity.this);
      linearLayout1.setGravity(Gravity.CENTER);
      linearLayout1.setLayoutParams(layoutParams);
      final TextView textView1 = new TextView(ChartActivity.this);
      LinearLayout.LayoutParams layoutParams1 =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutParams1.gravity = Gravity.CENTER_HORIZONTAL;
      layoutParams1.setMargins(50, 10, 50, 10);
      textView1.setLayoutParams(layoutParams1);
      textView1.setPadding(10, 10, 10, 10);
      textView1.setTextColor(Color.BLACK);
      textView1.setGravity(View.TEXT_ALIGNMENT_CENTER);

      final ArrayList<String> runtxt = new ArrayList<>();

      SimpleDateFormat runchartdate =
          AppController.getDateFormatForDashboardAndChartCurrentDayOut();
      for (int i = 0; i < lists.size(); i++) {
        runtxt.add(
            runchartdate.format(lists.get(i).get(0).getStartDate())
                + "-"
                + runchartdate.format(lists.get(i).get(lists.get(i).size() - 1).getEnddDate()));
      }

      textView1.setText(runtxt.get(0).toString());

      textView1.setTag(charttype);
      textView1.setTag(R.string.charttag, activityId);
      textView1.setTag(R.string.runchartindex, 0);
      textView1.setTag(R.string.runchartmaxindex, lists.size() - 1);

      final ImageView rightArrow = new ImageView(ChartActivity.this);
      rightArrow.setImageResource(R.drawable.arrow2_right);
      rightArrow.setPadding(10, 10, 10, 10);
      rightArrow.setTag(position);

      final ImageView leftArrow = new ImageView(ChartActivity.this);
      leftArrow.setTag(position);

      leftArrow.setImageResource(R.drawable.arrow2_left);
      leftArrow.setPadding(10, 10, 10, 10);

      leftArrow.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  getString(R.string.chart_actvity_left_arrow));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
              if (Integer.parseInt("" + textView1.getTag(R.string.runchartindex)) > 0) {
                textView1.setTag(
                    R.string.runchartindex,
                    Integer.parseInt("" + textView1.getTag(R.string.runchartindex)) - 1);
                textView1.setText(
                    runtxt.get(Integer.parseInt("" + textView1.getTag(R.string.runchartindex))));
                refreshdataRun(
                    filteredXValues,
                    entryList,
                    lists,
                    Integer.parseInt("" + textView1.getTag(R.string.runchartindex)),
                    chart,
                    barColor);
              }
            }
          });

      rightArrow.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  getString(R.string.chart_actvity_right_arrow));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
              if (Integer.parseInt("" + textView1.getTag(R.string.runchartindex))
                  < Integer.parseInt("" + textView1.getTag(R.string.runchartmaxindex))) {
                textView1.setTag(
                    R.string.runchartindex,
                    Integer.parseInt("" + textView1.getTag(R.string.runchartindex)) + 1);
                textView1.setText(
                    runtxt.get(Integer.parseInt("" + textView1.getTag(R.string.runchartindex))));
                refreshdataRun(
                    filteredXValues,
                    entryList,
                    lists,
                    Integer.parseInt("" + textView1.getTag(R.string.runchartindex)),
                    chart,
                    barColor);
              }
            }
          });

      linearLayout1.addView(leftArrow);
      linearLayout1.addView(textView1);
      linearLayout1.addView(rightArrow);
    }
  }

  private void refreshdataRun(
      List<String> filteredXValues,
      List<Entry> entryList,
      List<List<RunChart>> lists,
      int index,
      LineChart chart,
      String barColor) {
    chart.clear();
    filteredXValues.clear();
    entryList.clear();
    for (int l = 0; l < lists.get(index).size(); l++) {
      if (lists.get(index).get(l).getResult() != null) {
        entryList.add(
            new Entry(
                Float.parseFloat(lists.get(index).get(l).getResult()),
                Integer.parseInt(lists.get(index).get(l).getRunId())
                    - Integer.parseInt(lists.get(index).get(0).getRunId()),
                lists.get(index).get(0).getResultData()));
      }
      filteredXValues.add("" + (Integer.parseInt(lists.get(index).get(l).getRunId())));
    }
    TempGraphHelper.updateLineChart(chart, 0, entryList, filteredXValues, barColor);
  }

  public void addTimeLayout(
      String time,
      String charttype,
      final LineChart chart,
      final RealmResults<StepRecordCustom> stepRecordCustomList,
      final List<String> filteredXValues,
      final List<Entry> entryList,
      final RealmResults<ActivityRun> activityRuns,
      int position,
      final String barColor,
      final String activityId) {
    linearLayout1 = new LinearLayout(ChartActivity.this);
    linearLayout1.setGravity(Gravity.CENTER);
    linearLayout1.setLayoutParams(layoutParams);
    final TextView textView1 = new TextView(ChartActivity.this);
    LinearLayout.LayoutParams layoutParams1 =
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    layoutParams1.gravity = Gravity.CENTER_HORIZONTAL;
    layoutParams1.setMargins(50, 10, 50, 10);
    textView1.setLayoutParams(layoutParams1);
    textView1.setPadding(10, 10, 10, 10);
    textView1.setTextColor(Color.BLACK);
    textView1.setGravity(View.TEXT_ALIGNMENT_CENTER);
    if (time.equalsIgnoreCase(DAY)) {
      setDay(textView1);
    } else if (time.equalsIgnoreCase(WEEK)) {
      setWeek(textView1);
    } else if (time.equalsIgnoreCase(MONTH)) {
      setMonth(textView1);
    } else {
      setYear(textView1);
    }

    textView1.setTag(charttype);
    textView1.setTag(R.string.charttag, activityId);

    final ImageView rightArrow = new ImageView(ChartActivity.this);
    rightArrow.setImageResource(R.drawable.arrow2_right);
    rightArrow.setPadding(10, 10, 10, 10);
    rightArrow.setTag(position);

    final ImageView leftArrow = new ImageView(ChartActivity.this);
    leftArrow.setTag(position);

    leftArrow.setImageResource(R.drawable.arrow2_left);
    leftArrow.setPadding(10, 10, 10, 10);

    if (dashboardData.getDashboard().getCharts().get(position).isScrollable()) {
      rightArrow.setVisibility(View.VISIBLE);
      leftArrow.setVisibility(View.VISIBLE);
    } else {
      rightArrow.setVisibility(View.GONE);
      leftArrow.setVisibility(View.GONE);
    }

    leftArrow.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.chart_actvity_left_arrow));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(DAY)) {
              try {
                SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();

                Date selectedStartDAte =
                    simpleDateFormat.parse(fromDayVals.get((int) leftArrow.getTag()));
                Date selectedEndDate =
                    simpleDateFormat.parse(toDayVals.get((int) leftArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, -1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, -1);
                fromDayVals.set(
                    (int) leftArrow.getTag(), simpleDateFormat.format(calendarStart.getTime()));
                toDayVals.set(
                    (int) leftArrow.getTag(), simpleDateFormat.format(calendarEnd.getTime()));
                SimpleDateFormat dateFormatForDashboardAndChartCurrentDayOut =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                textView1.setText(
                    dateFormatForDashboardAndChartCurrentDayOut.format(calendarStart.getTime()));
                refreshchartdata(
                    "" + textView1.getTag(),
                    calendarStart.getTime(),
                    calendarEnd.getTime(),
                    chart,
                    stepRecordCustomList,
                    filteredXValues,
                    entryList,
                    activityRuns,
                    barColor,
                    "" + textView1.getTag(R.string.charttag));
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(WEEK)) {
              try {
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte =
                    dateFormatForApi.parse(fromDayVals.get((int) leftArrow.getTag()));
                Date selectedEndDate =
                    dateFormatForApi.parse(toDayVals.get((int) leftArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, -7);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, -7);
                fromDayVals.set(
                    (int) leftArrow.getTag(), dateFormatForApi.format(calendarStart.getTime()));
                toDayVals.set(
                    (int) leftArrow.getTag(), dateFormatForApi.format(calendarEnd.getTime()));
                SimpleDateFormat simpleDateFormat =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                textView1.setText(
                    simpleDateFormat.format(calendarStart.getTime())
                        + " - "
                        + simpleDateFormat.format(calendarEnd.getTime()));
                refreshchartdata(
                    "" + textView1.getTag(),
                    calendarStart.getTime(),
                    calendarEnd.getTime(),
                    chart,
                    stepRecordCustomList,
                    filteredXValues,
                    entryList,
                    activityRuns,
                    barColor,
                    "" + textView1.getTag(R.string.charttag));
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(MONTH)) {
              try {
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte =
                    dateFormatForApi.parse(fromDayVals.get((int) leftArrow.getTag()));
                Date selectedEndDate =
                    dateFormatForApi.parse(toDayVals.get((int) leftArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.MONTH, -1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.MONTH, -1);
                fromDayVals.set(
                    (int) leftArrow.getTag(), dateFormatForApi.format(calendarStart.getTime()));
                toDayVals.set(
                    (int) leftArrow.getTag(), dateFormatForApi.format(calendarEnd.getTime()));
                SimpleDateFormat dateFormatForChartAndStat =
                    AppController.getDateFormatForChartAndStat();
                textView1.setText(dateFormatForChartAndStat.format(calendarStart.getTime()));
                refreshchartdata(
                    "" + textView1.getTag(),
                    calendarStart.getTime(),
                    calendarEnd.getTime(),
                    chart,
                    stepRecordCustomList,
                    filteredXValues,
                    entryList,
                    activityRuns,
                    barColor,
                    "" + textView1.getTag(R.string.charttag));
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(YEAR)) {
              try {
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte =
                    dateFormatForApi.parse(fromDayVals.get((int) leftArrow.getTag()));
                Date selectedEndDate =
                    dateFormatForApi.parse(toDayVals.get((int) leftArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.YEAR, -1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.YEAR, -1);
                fromDayVals.set(
                    (int) leftArrow.getTag(), dateFormatForApi.format(calendarStart.getTime()));
                toDayVals.set(
                    (int) leftArrow.getTag(), dateFormatForApi.format(calendarEnd.getTime()));
                SimpleDateFormat simpleDateFormat = AppController.getDateFormatYearFormat();
                textView1.setText(simpleDateFormat.format(calendarStart.getTime()));
                refreshchartdata(
                    "" + textView1.getTag(),
                    calendarStart.getTime(),
                    calendarEnd.getTime(),
                    chart,
                    stepRecordCustomList,
                    filteredXValues,
                    entryList,
                    activityRuns,
                    barColor,
                    "" + textView1.getTag(R.string.charttag));
              } catch (ParseException e) {
                Logger.log(e);
              }
            }
          }
        });

    rightArrow.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.chart_actvity_right_arrow));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(DAY)) {
              try {
                SimpleDateFormat simpleDateFormat =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte =
                    dateFormatForApi.parse(fromDayVals.get((int) rightArrow.getTag()));
                Date selectedEndDate =
                    dateFormatForApi.parse(toDayVals.get((int) rightArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, 1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, 1);
                if (!calendarStart.getTime().after(new Date())) {
                  fromDayVals.set(
                      (int) rightArrow.getTag(), dateFormatForApi.format(calendarStart.getTime()));
                  toDayVals.set(
                      (int) rightArrow.getTag(), dateFormatForApi.format(calendarEnd.getTime()));

                  textView1.setText(simpleDateFormat.format(calendarStart.getTime()));
                  refreshchartdata(
                      "" + textView1.getTag(),
                      calendarStart.getTime(),
                      calendarEnd.getTime(),
                      chart,
                      stepRecordCustomList,
                      filteredXValues,
                      entryList,
                      activityRuns,
                      barColor,
                      "" + textView1.getTag(R.string.charttag));
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(WEEK)) {
              try {
                SimpleDateFormat simpleDateFormat =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte =
                    dateFormatForApi.parse(fromDayVals.get((int) rightArrow.getTag()));
                Date selectedEndDate =
                    dateFormatForApi.parse(toDayVals.get((int) rightArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, 7);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, 7);
                if (!calendarStart.getTime().after(new Date())) {
                  fromDayVals.set(
                      (int) rightArrow.getTag(), dateFormatForApi.format(calendarStart.getTime()));
                  toDayVals.set(
                      (int) rightArrow.getTag(), dateFormatForApi.format(calendarEnd.getTime()));

                  if (calendarEnd.getTime().after(new Date())) {
                    textView1.setText(
                        simpleDateFormat.format(calendarStart.getTime())
                            + " - "
                            + simpleDateFormat.format(new Date()));
                    refreshchartdata(
                        "" + textView1.getTag(),
                        calendarStart.getTime(),
                        new Date(),
                        chart,
                        stepRecordCustomList,
                        filteredXValues,
                        entryList,
                        activityRuns,
                        barColor,
                        "" + textView1.getTag(R.string.charttag));
                  } else {
                    textView1.setText(
                        simpleDateFormat.format(calendarStart.getTime())
                            + " - "
                            + simpleDateFormat.format(calendarEnd.getTime()));
                    refreshchartdata(
                        "" + textView1.getTag(),
                        calendarStart.getTime(),
                        calendarEnd.getTime(),
                        chart,
                        stepRecordCustomList,
                        filteredXValues,
                        entryList,
                        activityRuns,
                        barColor,
                        "" + textView1.getTag(R.string.charttag));
                  }
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(MONTH)) {
              try {
                SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
                SimpleDateFormat dateFormatForChartAndStat =
                    AppController.getDateFormatForChartAndStat();
                Date selectedStartDAte =
                    simpleDateFormat.parse(fromDayVals.get((int) rightArrow.getTag()));
                Date selectedEndDate =
                    simpleDateFormat.parse(toDayVals.get((int) rightArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.MONTH, 1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.MONTH, 1);
                if (!calendarStart.getTime().after(new Date())) {
                  fromDayVals.set(
                      (int) rightArrow.getTag(), simpleDateFormat.format(calendarStart.getTime()));
                  toDayVals.set(
                      (int) rightArrow.getTag(), simpleDateFormat.format(calendarEnd.getTime()));

                  textView1.setText(dateFormatForChartAndStat.format(calendarStart.getTime()));
                  refreshchartdata(
                      "" + textView1.getTag(),
                      calendarStart.getTime(),
                      calendarEnd.getTime(),
                      chart,
                      stepRecordCustomList,
                      filteredXValues,
                      entryList,
                      activityRuns,
                      barColor,
                      "" + textView1.getTag(R.string.charttag));
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(YEAR)) {
              try {
                SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatYearFormat();
                Date selectedStartDAte =
                    simpleDateFormat.parse(fromDayVals.get((int) rightArrow.getTag()));
                Date selectedEndDate =
                    simpleDateFormat.parse(toDayVals.get((int) rightArrow.getTag()));
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.YEAR, 1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.YEAR, 1);
                if (!calendarStart.getTime().after(new Date())) {
                  fromDayVals.set(
                      (int) rightArrow.getTag(), simpleDateFormat.format(calendarStart.getTime()));
                  toDayVals.set(
                      (int) rightArrow.getTag(), simpleDateFormat.format(calendarEnd.getTime()));

                  textView1.setText(dateFormatForApi.format(calendarStart.getTime()));
                  refreshchartdata(
                      "" + textView1.getTag(),
                      calendarStart.getTime(),
                      calendarEnd.getTime(),
                      chart,
                      stepRecordCustomList,
                      filteredXValues,
                      entryList,
                      activityRuns,
                      barColor,
                      "" + textView1.getTag(R.string.charttag));
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            }
          }
        });

    linearLayout1.addView(leftArrow);
    linearLayout1.addView(textView1);
    linearLayout1.addView(rightArrow);
  }

  private void refreshchartdata(
      String tag,
      Date startTime,
      Date endtime,
      final LineChart chart,
      RealmResults<StepRecordCustom> stepRecordCustomList,
      List<String> filteredXValues,
      List<Entry> entryList,
      RealmResults<ActivityRun> activityRuns,
      final String barColor,
      final String activityId) {
    chart.clear();
    entryList.clear();
    filteredXValues.clear();
    if (!tag.equalsIgnoreCase("hours_of_day")) {
      if (tag.equalsIgnoreCase("days_of_week")) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
        for (int k = 0; k < day.length; k++) {
          for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
            if (stepRecordCustomList.get(j).getCompleted().before(endtime)
                && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
              JSONObject jsonObject;
              String answer = "";
              String data = "";
              try {
                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                String[] id = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                if (activityObj.getType().equalsIgnoreCase("task")) {
                  JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                  answer = answerjson.getString("duration");
                  answer = Double.toString(Integer.parseInt(answer) / 60f);
                  data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                } else {
                  answer = jsonObject.getString("answer");
                  data = "";
                }
              } catch (JSONException e) {
                Logger.log(e);
              }
              String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());

              if (s.equalsIgnoreCase(day[k])) {
                entryList.add(new Entry(Float.parseFloat(answer), k, data));
              }
            }
          }
          filteredXValues.add(day[k]);
        }
      } else if (tag.equalsIgnoreCase("days_of_month")) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d");
        int month = startTime.getMonth();
        int year = startTime.getYear();
        for (int k = 1; k <= numberOfDaysInMonth(month, year); k++) {
          for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
            if (stepRecordCustomList.get(j).getCompleted().before(endtime)
                && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
              JSONObject jsonObject;
              String answer = "";
              String data = "";
              try {
                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                String[] id = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                if (activityObj.getType().equalsIgnoreCase("task")) {
                  JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                  answer = answerjson.getString("duration");
                  answer = Double.toString(Integer.parseInt(answer) / 60f);
                  data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                } else {
                  answer = jsonObject.getString("answer");
                  data = "";
                }
              } catch (JSONException e) {
                Logger.log(e);
              }

              String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
              if (s.equalsIgnoreCase("" + k)) {
                entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
              }
            }
          }
          if (k % 5 == 0) {
            filteredXValues.add("" + k);
          } else {
            filteredXValues.add("");
          }
        }
      } else if (tag.equalsIgnoreCase("weeks_of_month")) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
        for (int k = 1; k <= numberOfWeeksInMonth(simpleDateFormat.format(startTime)); k++) {
          for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
            if (stepRecordCustomList.get(j).getCompleted().before(endtime)
                && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
              JSONObject jsonObject;
              String answer = "";
              String data = "";
              try {
                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                String[] id = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                if (activityObj.getType().equalsIgnoreCase("task")) {
                  JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                  answer = answerjson.getString("duration");
                  answer = Double.toString(Integer.parseInt(answer) / 60f);
                  data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                } else {
                  answer = jsonObject.getString("answer");
                  data = "";
                }
              } catch (JSONException e) {
                Logger.log(e);
              }
              cal.setTime(stepRecordCustomList.get(j).getCompleted());
              int week = cal.get(Calendar.WEEK_OF_MONTH);
              if (k == week) {
                entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
              }
            }
          }
          filteredXValues.add("W" + k);
        }
      } else if (tag.equalsIgnoreCase("months_of_year")) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
        for (int k = 0; k < month.length; k++) {
          for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
            if (stepRecordCustomList.get(j).getCompleted().before(endtime)
                && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
              JSONObject jsonObject;
              String answer = "";
              String data = "";
              try {
                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                String[] id = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                if (activityObj.getType().equalsIgnoreCase("task")) {
                  JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                  answer = answerjson.getString("duration");
                  answer = Double.toString(Integer.parseInt(answer) / 60f);
                  data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                } else {
                  answer = jsonObject.getString("answer");
                  data = "";
                }
              } catch (JSONException e) {
                Logger.log(e);
              }
              String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
              if (s.equalsIgnoreCase(monthfull[k])) {
                entryList.add(new Entry(Float.parseFloat(answer), k, data));
              }
            }
          }
          filteredXValues.add(month[k]);
        }
      }
    } else if (tag.equalsIgnoreCase("hours_of_day")) {
      activityRuns =
          dbServiceSubscriber.getAllActivityRunforDate(
              activityId, getIntent().getStringExtra("studyId"), startTime, realm);
      for (int k = 0; k < activityRuns.size(); k++) {
        for (int l = 0; l < stepRecordCustomList.size(); l++) {
          if (stepRecordCustomList.get(l).getCompleted().before(endtime)
              && stepRecordCustomList.get(l).getCompleted().after(startTime)) {
            if (stepRecordCustomList.get(l).taskId.contains("_")) {
              String[] taskId = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
              String runId =
                  taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());

              JSONObject jsonObject;
              String answer = "";
              String data = "";
              try {
                jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                String[] id = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                if (activityObj.getType().equalsIgnoreCase("task")) {
                  JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                  answer = answerjson.getString("duration");
                  answer = Double.toString(Integer.parseInt(answer) / 60f);
                  data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                } else {
                  answer = jsonObject.getString("answer");
                  data = "";
                }
                if (answer == null || answer.equalsIgnoreCase("")) {
                  answer = "0";
                }
              } catch (JSONException e) {
                Logger.log(e);
                if (answer.equalsIgnoreCase("")) {
                  answer = "0";
                }
              }

              if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {
                entryList.add(new Entry(Float.parseFloat(answer), k, data));
              }
            }
          }
        }
        filteredXValues.add("" + (k + 1));
      }
    } else {
      activityRuns =
          dbServiceSubscriber.getAllActivityRunFromDB(
              getIntent().getStringExtra("studyId"), activityId, realm);
      for (int k = 0; k < activityRuns.size(); k++) {
        for (int l = 0; l < stepRecordCustomList.size(); l++) {
          if (stepRecordCustomList.get(l).getCompleted().before(endtime)
              && stepRecordCustomList.get(l).getCompleted().after(startTime)) {
            if (stepRecordCustomList.get(l).taskId.contains("_")) {
              String[] taskId = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
              String runId =
                  taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());

              JSONObject jsonObject;
              String answer = "";
              String data = "";
              try {
                jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                String[] id = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
                if (activityObj.getType().equalsIgnoreCase("task")) {
                  JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                  answer = answerjson.getString("duration");
                  answer = Double.toString(Integer.parseInt(answer) / 60f);
                  data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                } else {
                  answer = jsonObject.getString("answer");
                  data = "";
                }
              } catch (JSONException e) {
                Logger.log(e);
              }

              if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {
                entryList.add(new Entry(Float.parseFloat(answer), k, data));
              }
            }
          }
        }
        filteredXValues.add("" + (k + 1));
      }
    }

    // Update chart w/ our data
    TempGraphHelper.updateLineChart(chart, 0, entryList, filteredXValues, barColor);
  }

  private void setDay(TextView textView1) {
    SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    String fromDayVal = dateFormatForApi.format(calendar.getTime());
    fromDayVals.add(fromDayVal);
    starttime = calendar.getTime();

    Calendar calendar1 = Calendar.getInstance();
    calendar1.set(Calendar.HOUR_OF_DAY, 23);
    calendar1.set(Calendar.MINUTE, 59);
    calendar1.set(Calendar.SECOND, 59);
    calendar1.set(Calendar.MILLISECOND, 999);
    String toDayVal = dateFormatForApi.format(calendar1.getTime());
    toDayVals.add(toDayVal);
    endtime = calendar1.getTime();
    SimpleDateFormat simpleDateFormat =
        AppController.getDateFormatForDashboardAndChartCurrentDayOut();
    textView1.setText(simpleDateFormat.format(calendar.getTime()));
    dateType = DAY;
    dateTypeArray.add(DAY);
  }

  private void setWeek(TextView textView1) {
    SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    String fromDayVal = dateFormatForApi.format(calendar.getTime());
    starttime = calendar.getTime();
    fromDayVals.add(fromDayVal);
    SimpleDateFormat simpleDateFormat =
        AppController.getDateFormatForDashboardAndChartCurrentDayOut();
    textView1.setText(
        simpleDateFormat.format(calendar.getTime()) + " - " + simpleDateFormat.format(new Date()));
    calendar.add(Calendar.DATE, 6);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    String toDayVal = dateFormatForApi.format(calendar.getTime());
    endtime = calendar.getTime();
    toDayVals.add(toDayVal);

    dateType = WEEK;
    dateTypeArray.add(WEEK);
  }

  private void setMonth(TextView textView1) {
    SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
    Calendar calendar = Calendar.getInstance(); // this takes current date
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    String fromDayVal = simpleDateFormat.format(calendar.getTime());
    starttime = calendar.getTime();
    fromDayVals.add(fromDayVal);
    SimpleDateFormat dateFormatForChartAndStat = AppController.getDateFormatForChartAndStat();
    textView1.setText(dateFormatForChartAndStat.format(calendar.getTime()));

    calendar.add(Calendar.MONTH, 1);
    calendar.add(Calendar.DATE, -1);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    String toDayVal = simpleDateFormat.format(new Date());
    toDayVal = simpleDateFormat.format(calendar.getTime());
    endtime = calendar.getTime();
    toDayVals.add(toDayVal);

    dateType = MONTH;
    dateTypeArray.add(MONTH);
  }

  private void setYear(TextView textView1) {
    SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, 1); // this takes current date
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    String fromDayVal = simpleDateFormat.format(calendar.getTime());
    starttime = calendar.getTime();
    fromDayVals.add(fromDayVal);

    SimpleDateFormat dateFormatYearFormat = AppController.getDateFormatYearFormat();
    textView1.setText(dateFormatYearFormat.format(calendar.getTime()));

    calendar.add(Calendar.YEAR, 1);
    calendar.add(Calendar.DATE, -1);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    String toDayVal = simpleDateFormat.format(new Date());
    toDayVal = simpleDateFormat.format(calendar.getTime());
    endtime = calendar.getTime();
    toDayVals.add(toDayVal);
    dateType = YEAR;
    dateTypeArray.add(YEAR);
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }

  @Override
  public void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(networkChangeReceiver, intentFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (networkChangeReceiver != null) {
      unregisterReceiver(networkChangeReceiver);
    }
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      shareBtn.setClickable(false);
      shareBtn.setAlpha(0.3F);
    } else {
      shareBtn.setClickable(true);
      shareBtn.setAlpha(1F);
    }
  }
}
