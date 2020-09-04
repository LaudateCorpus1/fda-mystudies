/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusResponse;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.dao.FdaEaUserDetailsDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.sql.Timestamp;
import java.time.Instant;
import javax.transaction.Transactional;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FdaEaUserDetailsServiceImpl implements FdaEaUserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsServiceImpl.class);

  @Autowired private AuthInfoBOService authInfoService;

  @Autowired private UserAppDetailsService userAppDetailsService;

  @Autowired private FdaEaUserDetailsDao userDetailsDao;

  @Autowired private UserManagementUtil userManagementUtil;

  @Override
  @Transactional
  public UserDetailsEntity saveUser(UserDetailsEntity userDetails) throws SystemException {
    logger.info("FdaEaUserDetailsServiceImpl saveUser() - starts");
    UserDetailsEntity daoResp = null;
    try {
      if (userDetails != null) {
        daoResp = userDetailsDao.saveUser(userDetails);
        AuthInfoEntity authInfo = new AuthInfoEntity();
        authInfo.setApp(daoResp.getApp());
        authInfo.setUserDetails(daoResp);
        authInfo.setCreated(Timestamp.from(Instant.now()));
        authInfoService.save(authInfo);
        UserAppDetailsEntity userAppDetails = new UserAppDetailsEntity();
        userAppDetails.setApp(daoResp.getApp());
        userAppDetails.setCreated(Timestamp.from(Instant.now()));
        userAppDetails.setUserDetails(daoResp);
        userAppDetailsService.save(userAppDetails);
      }
      logger.info("FdaEaUserDetailsServiceImpl saveUser() - ends");
    } catch (Exception e) {
      logger.error("FdaEaUserDetailsServiceImpl saveUser(): ", e);
      throw new SystemException();
    }
    return daoResp;
  }

  @Override
  public UserDetailsEntity loadUserDetailsByUserId(String userId) throws SystemException {
    // call dao layer to get the user details using userId
    logger.info("FdaEaUserDetailsServiceImpl loadUserDetailsByUserId() - starts");
    UserDetailsEntity daoResp = null;
    if (userId != null) {
      daoResp = userDetailsDao.loadUserDetailsByUserId(userId);
    }
    logger.info("FdaEaUserDetailsServiceImpl loadUserDetailsByUserId() - ends");
    return daoResp;
  }

  @Override
  public boolean verifyCode(String code, UserDetailsEntity participantDetails) {
    logger.info("FdaEaUserDetailsServiceImpl verifyCode() - starts");
    boolean result = code == null || participantDetails == null;
    if (result) {
      throw new IllegalArgumentException();
    }
    if (code.equals(participantDetails.getEmailCode())
        && Timestamp.from(Instant.now()).before(participantDetails.getCodeExpireDate())) {
      return true;
    } else {
      logger.info("FdaEaUserDetailsServiceImpl verifyCode() - ends");
      return false;
    }
  }

  @Override
  public boolean updateStatus(UserDetailsEntity participantDetails) {
    logger.info("FdaEaUserDetailsServiceImpl updateStatus() - starts");
    UserDetailsEntity userDetails = SerializationUtils.clone(participantDetails);
    userDetails.setEmailCode(null);
    userDetails.setCodeExpireDate(null);
    userDetails.setStatus(AppConstants.EMAILID_VERIFIED_STATUS);
    boolean status = userDetailsDao.updateStatus(userDetails);

    if (status) {
      UpdateEmailStatusRequest updateEmailStatusRequest = new UpdateEmailStatusRequest();
      updateEmailStatusRequest.setStatus(UserAccountStatus.ACTIVE.getStatus());
      UpdateEmailStatusResponse updateStatusResponse =
          userManagementUtil.updateUserInfoInAuthServer(
              updateEmailStatusRequest, participantDetails.getUserId());
      if (updateStatusResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        status = false; // rolling back in registration server and returning false.
        boolean rollbackStatus = userDetailsDao.updateStatus(participantDetails);
        if (!rollbackStatus) {
          logger.error("Failed to rollback email status.");
        }
      }
    }
    return status;
  }
}
