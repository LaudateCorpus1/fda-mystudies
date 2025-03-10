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

package com.harvard.studyappmodule.activitybuilder;

import android.content.Context;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.ActivityObj;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.Steps;
import com.harvard.studyappmodule.custom.result.StepRecordCustom;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.List;
import java.util.Map;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

public class ActivityBuilder extends OrderedTask {

  private static boolean branching;
  private static DbServiceSubscriber dbServiceSubscriber;
  private static String identifier;
  private static RealmList<Steps> activityQuestionStep;
  private static Context context;

  private ActivityBuilder(String identifier, List<Step> steps) {
    super(identifier, steps);
  }

  public static ActivityBuilder create(
      Context contextParam,
      String identifierParam,
      List<Step> steps,
      ActivityObj activityParam,
      boolean branchingObj,
      DbServiceSubscriber dbServiceSubscriberParam) {
    identifier = identifierParam;
    activityQuestionStep = activityParam.getSteps();
    branching = branchingObj;
    dbServiceSubscriber = dbServiceSubscriberParam;
    context = contextParam;
    return new ActivityBuilder(identifierParam, steps);
  }

  @Override
  public Step getStepAfterStep(Step previousStep, TaskResult taskResult) {

    if (branching) {
      Steps stepsData = null;
      for (int i = 0; i < activityQuestionStep.size(); i++) {
        if (previousStep == null) {
          return steps.get(0);
        } else if (previousStep
            .getIdentifier()
            .equalsIgnoreCase(activityQuestionStep.get(i).getKey())) {
          stepsData = activityQuestionStep.get(i);
        }
      }

      if (stepsData.getResultType().equalsIgnoreCase("textScale")
          || stepsData.getResultType().equalsIgnoreCase("imageChoice")
          || stepsData.getResultType().equalsIgnoreCase("textChoice")
          || stepsData.getResultType().equalsIgnoreCase("boolean")) {

        if (stepsData != null
            && stepsData.getDestinations().size() == 1
            && stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("")
            && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
          return null;
        } else if (stepsData != null
            && stepsData.getDestinations().size() == 1
            && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
          for (int i = 0; i < steps.size(); i++) {
            if (steps
                .get(i)
                .getIdentifier()
                .equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
              return steps.get(i);
            }
          }
        } else if (stepsData != null) {
          Map<String, StepResult> map = taskResult.getResults();
          String destination = "";
          String answer = getAnswerForResultTypeSet1(map, stepsData);

          for (int j = 0; j < stepsData.getDestinations().size(); j++) {
            if (stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase(answer)) {
              destination = stepsData.getDestinations().get(j).getDestination();
            }
          }
          for (int k = 0; k < steps.size(); k++) {
            if (steps.get(k).getIdentifier().equalsIgnoreCase(destination)) {
              return steps.get(k);
            }
          }

          // if destination doesn't satisfy
          if (previousStep == null) {
            return steps.get(0);
          }

          if (destination.equalsIgnoreCase("")) {
            return null;
          }

          int nextIndex = steps.indexOf(previousStep) + 1;

          if (nextIndex < steps.size()) {
            return steps.get(nextIndex);
          }
        }
      } else if (stepsData.getResultType().equalsIgnoreCase("scale")
          || stepsData.getResultType().equalsIgnoreCase("continuousScale")
          || stepsData.getResultType().equalsIgnoreCase("numeric")
          || stepsData.getResultType().equalsIgnoreCase("timeInterval")
          || stepsData.getResultType().equalsIgnoreCase("height")) {
        if (stepsData != null
            && stepsData.getDestinations().size() == 1
            && stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("")
            && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
          return null;
        } else if (stepsData != null
            && stepsData.getDestinations().size() == 1
            && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
          for (int i = 0; i < steps.size(); i++) {
            if (steps
                .get(i)
                .getIdentifier()
                .equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
              return steps.get(i);
            }
          }
        } else if (stepsData != null) {
          Map<String, StepResult> map = taskResult.getResults();
          String destination = "";
          String answer = getAnswerForResultTypeSet2(map, stepsData);
          for (int j = 0; j < stepsData.getDestinations().size(); j++) {
            if (answer.equalsIgnoreCase("")) {
              if (stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase(answer)) {
                destination = stepsData.getDestinations().get(j).getDestination();
              }
            } else if (!stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase("")) {
              double condition =
                  Double.parseDouble(stepsData.getDestinations().get(j).getCondition());
              if (stepsData.getResultType().equalsIgnoreCase("timeInterval")) {
                condition =
                    Double.parseDouble(stepsData.getDestinations().get(j).getCondition()) / 3600d;
              }
              double answerDouble = Double.parseDouble(answer);
              destination = getDestination(condition, answerDouble, stepsData, j, destination);
            }
          }
          for (int k = 0; k < steps.size(); k++) {
            if (steps.get(k).getIdentifier().equalsIgnoreCase(destination)) {
              return steps.get(k);
            }
          }

          // if destination doesn't satisfy
          if (previousStep == null) {
            return steps.get(0);
          }

          if (destination.equalsIgnoreCase("")) {
            return null;
          }

          int nextIndex = steps.indexOf(previousStep) + 1;

          if (nextIndex < steps.size()) {
            return steps.get(nextIndex);
          }
        }
      } else {

        if (previousStep == null) {
          return steps.get(0);
        }

        if (stepsData != null
            && stepsData.getDestinations().size() == 1
            && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))
            && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
          return null;
        } else if (stepsData != null
            && stepsData.getDestinations().size() == 1
            && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
          for (int i = 0; i < steps.size(); i++) {
            if (steps
                .get(i)
                .getIdentifier()
                .equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
              return steps.get(i);
            }
          }
        } else {

          int nextIndex = steps.indexOf(previousStep) + 1;

          if (nextIndex < steps.size()) {
            return steps.get(nextIndex);
          }
        }
      }

    } else {
      if (previousStep == null) {
        return steps.get(0);
      }

      int nextIndex = steps.indexOf(previousStep) + 1;

      if (nextIndex < steps.size()) {
        return steps.get(nextIndex);
      }
    }

    return null;
  }

  private String getDestination(
      double condition, double answerDouble, Steps stepsData, int j, String destinationParam) {
    String destination = destinationParam;
    if (stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("e")) {
      if (answerDouble == condition) {
        destination = stepsData.getDestinations().get(j).getDestination();
      }
    } else if (stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("gt")) {
      if (answerDouble > condition) {
        destination = stepsData.getDestinations().get(j).getDestination();
      }
    } else if (stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("lt")) {
      if (answerDouble < condition) {
        destination = stepsData.getDestinations().get(j).getDestination();
      }
    } else if (stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("gte")) {
      if (answerDouble >= condition) {
        destination = stepsData.getDestinations().get(j).getDestination();
      }
    } else if (stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("lte")) {
      if (answerDouble <= condition) {
        destination = stepsData.getDestinations().get(j).getDestination();
      }
    } else if (stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("ne")) {
      if (answerDouble != condition) {
        destination = stepsData.getDestinations().get(j).getDestination();
      }
    }
    return destination;
  }

  private String getAnswer(Map.Entry<String, StepResult> pair) {
    String answer = "";
    try {
      StepResult stepResult = pair.getValue();
      Object o = stepResult.getResults().get("answer");
      if (o instanceof Object[]) {
        Object[] objects = (Object[]) o;
        if (objects[0] instanceof String) {
          answer = "" + ((String) objects[0]);
        } else if (objects[0] instanceof Integer) {
          answer = "" + ((int) objects[0]);
        }
      } else {
        answer = "" + stepResult.getResults().get("answer");
      }
    } catch (Exception e) {
      answer = "";
      Logger.log(e);
    }
    if (answer == null || answer.equalsIgnoreCase("null")) {
      answer = "";
    }
    return answer;
  }

  private String getAnswerForResultTypeSet1(Map<String, StepResult> map, Steps stepsData) {
    String answer = "";

    for (Map.Entry<String, StepResult> pair : map.entrySet()) {
      if (pair.getKey().equalsIgnoreCase(stepsData.getKey())) {
        answer = getAnswer(pair);
        if (!answer.equalsIgnoreCase("")) {
          if (stepsData.getResultType().equalsIgnoreCase("imageChoice")
              || stepsData.getResultType().equalsIgnoreCase("textChoice")) {
            Realm realm = AppController.getRealmobj(context);
            StepRecordCustom stepRecordCustom =
                dbServiceSubscriber.getResultFromDB(identifier + "_" + pair.getKey(), realm);
            for (int j = 0; j < stepRecordCustom.getTextChoices().size(); j++) {
              if (stepRecordCustom.getTextChoices().get(j).getValue().equalsIgnoreCase(answer)) {
                answer = stepRecordCustom.getTextChoices().get(j).getText();
                break;
              }
            }
            dbServiceSubscriber.closeRealmObj(realm);
          }
        }
        break;
      }
    }
    return answer;
  }

  /**
   * This is to get answer for result type set1 and part of the answer for result type set2.
   *
   * @param map
   * @param stepsData
   * @return
   */
  private String getAnswerForResultTypeSet2(Map<String, StepResult> map, Steps stepsData) {
    String answer = "";

    for (Map.Entry<String, StepResult> pair : map.entrySet()) {
      if (pair.getKey().equalsIgnoreCase(stepsData.getKey())) {
        answer = getAnswer(pair);
        break;
      }
    }
    return answer;
  }

  @Override
  public Step getStepBeforeStep(Step step, TaskResult taskResult) {

    if (branching) {
      String identifier = "";
      for (int i = 0; i < activityQuestionStep.size(); i++) {
        for (int k = 0; k < activityQuestionStep.get(i).getDestinations().size(); k++) {
          if (activityQuestionStep
              .get(i)
              .getDestinations()
              .get(k)
              .getDestination()
              .equalsIgnoreCase(step.getIdentifier())) {
            Map<String, StepResult> map = taskResult.getResults();
            for (Map.Entry<String, StepResult> pair : map.entrySet()) {
              if (pair.getKey().equalsIgnoreCase(activityQuestionStep.get(i).getKey())) {
                if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textScale")
                    || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("imageChoice")
                    || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textChoice")
                    || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("boolean")) {
                  try {
                    if (pair.getValue() != null) {
                      String answer = getAnswer(pair);
                      if (!answer.equalsIgnoreCase("")) {
                        if (activityQuestionStep
                            .get(i)
                            .getResultType()
                            .equalsIgnoreCase("imageChoice")
                            || activityQuestionStep
                            .get(i)
                            .getResultType()
                            .equalsIgnoreCase("textChoice")) {
                          Realm realm = AppController.getRealmobj(context);
                          StepRecordCustom stepRecordCustom =
                              dbServiceSubscriber.getResultFromDB(
                                  identifier + "_" + activityQuestionStep.get(i).getKey(), realm);
                          for (int j = 0; j < stepRecordCustom.getTextChoices().size(); j++) {
                            if (stepRecordCustom
                                .getTextChoices()
                                .get(j)
                                .getValue()
                                .equalsIgnoreCase(answer)) {
                              answer = stepRecordCustom.getTextChoices().get(j).getText();
                              break;
                            }
                          }
                          dbServiceSubscriber.closeRealmObj(realm);
                        }
                      }
                      if (activityQuestionStep
                          .get(i)
                          .getDestinations()
                          .get(k)
                          .getCondition()
                          .equalsIgnoreCase(answer)) {
                        identifier = activityQuestionStep.get(i).getKey();
                      }
                    }
                  } catch (Exception e) {
                    Logger.log(e);
                    int nextIndex = steps.indexOf(step) - 1;

                    if (nextIndex >= 0) {
                      return steps.get(nextIndex);
                    }
                  }
                } else if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("scale")
                    || activityQuestionStep
                    .get(i)
                    .getResultType()
                    .equalsIgnoreCase("continuousScale")
                    || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("numeric")
                    || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("timeInterval")
                    || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("height")) {
                  try {
                    if (pair.getValue() != null) {
                      String answer = getAnswer(pair);
                      identifier = getidentifier(answer, activityQuestionStep, i, k);
                    }
                  } catch (Exception e) {
                    Logger.log(e);
                    int nextIndex = steps.indexOf(step) - 1;

                    if (nextIndex >= 0) {
                      return steps.get(nextIndex);
                    }
                  }
                }
              }
            }
          }
        }
      }
      for (int j = 0; j < steps.size(); j++) {
        if (steps.get(j).getIdentifier().equalsIgnoreCase(identifier)) {
          return steps.get(j);
        }
      }
    } else {
      int nextIndex = steps.indexOf(step) - 1;

      if (nextIndex >= 0) {
        return steps.get(nextIndex);
      }

      return null;
    }

    int nextIndex = steps.indexOf(step) - 1;

    if (nextIndex >= 0) {
      return steps.get(nextIndex);
    }

    return null;
  }

  private String getidentifier(String answer, RealmList<Steps> activityQuestionStep, int i, int k) {
    String identifier = "";
    if (answer.equalsIgnoreCase("")) {
      if (activityQuestionStep
          .get(i)
          .getDestinations()
          .get(k)
          .getCondition()
          .equalsIgnoreCase(answer)) {
        identifier = activityQuestionStep.get(i).getKey();
      }
    } else if (!activityQuestionStep
        .get(i)
        .getDestinations()
        .get(k)
        .getCondition()
        .equalsIgnoreCase("")) {
      double condition =
          Double.parseDouble(activityQuestionStep.get(i).getDestinations().get(k).getCondition());
      double answerDouble = Double.parseDouble(answer);
      if (activityQuestionStep
          .get(i)
          .getDestinations()
          .get(k)
          .getOperator()
          .equalsIgnoreCase("e")) {
        if (answerDouble == condition) {
          identifier = activityQuestionStep.get(i).getKey();
        }
      } else if (activityQuestionStep
          .get(i)
          .getDestinations()
          .get(k)
          .getOperator()
          .equalsIgnoreCase("gt")) {
        if (answerDouble > condition) {
          identifier = activityQuestionStep.get(i).getKey();
        }
      } else if (activityQuestionStep
          .get(i)
          .getDestinations()
          .get(k)
          .getOperator()
          .equalsIgnoreCase("lt")) {
        if (answerDouble < condition) {
          identifier = activityQuestionStep.get(i).getKey();
        }
      } else if (activityQuestionStep
          .get(i)
          .getDestinations()
          .get(k)
          .getOperator()
          .equalsIgnoreCase("gte")) {
        if (answerDouble >= condition) {
          identifier = activityQuestionStep.get(i).getKey();
        }
      } else if (activityQuestionStep
          .get(i)
          .getDestinations()
          .get(k)
          .getOperator()
          .equalsIgnoreCase("lte")) {
        if (answerDouble <= condition) {
          identifier = activityQuestionStep.get(i).getKey();
        }
      } else if (activityQuestionStep
          .get(i)
          .getDestinations()
          .get(k)
          .getOperator()
          .equalsIgnoreCase("ne")) {
        if (answerDouble != condition) {
          identifier = activityQuestionStep.get(i).getKey();
        }
      }
    }
    return identifier;
  }
}