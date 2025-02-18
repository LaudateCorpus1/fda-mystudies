/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.bean;

public class PushNotificationBean {

  private String customStudyId = "";
  private String notificationId = "";
  private String notificationSubType = "Announcement";
  private String notificationText = "";
  private String notificationTitle = "";
  private String notificationType = "ST";
  private String appId = "";
  private String deviceType = "";

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  public String getNotificationSubType() {
    return notificationSubType;
  }

  public void setNotificationSubType(String notificationSubType) {
    this.notificationSubType = notificationSubType;
  }

  public String getNotificationText() {
    return notificationText;
  }

  public void setNotificationText(String notificationText) {
    this.notificationText = notificationText;
  }

  public String getNotificationTitle() {
    return notificationTitle;
  }

  public void setNotificationTitle(String notificationTitle) {
    this.notificationTitle = notificationTitle;
  }

  public String getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(String notificationType) {
    this.notificationType = notificationType;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  @Override
  public String toString() {
    return "PushNotificationBean [customStudyId="
        + customStudyId
        + ", notificationId="
        + notificationId
        + ", notificationSubType="
        + notificationSubType
        + ", notificationText="
        + notificationText
        + ", notificationTitle="
        + notificationTitle
        + ", notificationType="
        + notificationType
        + ", appId="
        + appId
        + "]";
  }
}
