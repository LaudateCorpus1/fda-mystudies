package com.fdahpstudydesigner.mapper;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.common.AuditLogEvent;
// import com.google.cloud.healthcare.fdamystudies.common.CommonApplicationPropertyConfig;
// import com.fdahpstudydesigner.common.MobilePlatform;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
// TODO:[Aswini] remove CommonApplicationPropertyConfig.java file dependency
public final class AuditEventMapper {

  private AuditEventMapper() {}

  private static final String APP_ID = "appId";

  private static final String MOBILE_PLATFORM = "mobilePlatform";

  private static final String CORRELATION_ID = "correlationId";

  private static final String USER_ID = "userId";

  public static AuditLogEventRequest fromHttpServletRequest(HttpServletRequest request) {
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(getValue(request, APP_ID));
    auditRequest.setCorrelationId(getValue(request, CORRELATION_ID));
    auditRequest.setUserId(getValue(request, USER_ID));
    auditRequest.setUserIp(getUserIP(request));
    return auditRequest;
  }

  private static String getValue(HttpServletRequest request, String name) {
    String value = request.getHeader(name);
    if (StringUtils.isEmpty(value)) {
      value = getCookieValue(request, name);
    }
    return value;
  }

  private static String getUserIP(HttpServletRequest request) {
    return StringUtils.defaultIfEmpty(
        request.getHeader("X-FORWARDED-FOR"), request.getRemoteAddr());
  }

  private static String getCookieValue(HttpServletRequest req, String cookieName) {
    if (req != null && req.getCookies() != null) {
      return Arrays.stream(req.getCookies())
          .filter(c -> c.getName().equals(cookieName))
          .findFirst()
          .map(Cookie::getValue)
          .orElse(null);
    }
    /* if (req != null && req.getCookies() != null) {
      for (Cookie cookie : req.getCookies()) {
        if (cookie.getName().equals(cookieName)) {
          return cookie.getValue();
        } else {
          return null;
        }
      }
    }*/
    return null;
  }

  public static AuditLogEventRequest fromAuditLogEventEnumAndCommonPropConfig(
      AuditLogEvent eventEnum, AuditLogEventRequest auditRequest) {
    auditRequest.setEventCode(eventEnum.getEventCode());
    if (eventEnum.getSource().getValue().isEmpty()) {
      auditRequest.setSource(eventEnum.getSource().getValue());
    }
    if (eventEnum.getDestination().getValue().isEmpty()) {
      auditRequest.setDestination(eventEnum.getDestination().getValue());
    }
    if (eventEnum.getUserAccessLevel().getValue().isEmpty()) {
      auditRequest.setUserAccessLevel(eventEnum.getUserAccessLevel().getValue());
    }
    if (eventEnum.getResourceServer().getValue().isEmpty()) {
      auditRequest.setResourceServer(eventEnum.getResourceServer().getValue());
    }
    auditRequest.setOccured(new Timestamp(Instant.now().toEpochMilli()));
    return auditRequest;
  }
}
