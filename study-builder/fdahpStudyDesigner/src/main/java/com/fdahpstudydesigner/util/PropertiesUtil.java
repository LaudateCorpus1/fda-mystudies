/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.util;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class PropertiesUtil {

  private static XLogger logger = XLoggerFactory.getXLogger(PropertiesUtil.class.getName());

  // Constructs a Properties class using the provided `properties_file` and substitutes in
  // environment variables.
  // Expects environment variables to be in the form ${VAR_NAME}.
  public static Properties makePropertiesWithEnvironmentVariables(String properties_file) {
    Properties properties = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try {
      properties.load(loader.getResourceAsStream(properties_file));
    } catch (IOException ex) {
      logger.error("Unable to access " + properties_file, ex);
    }
    Properties env_properties = new Properties();
    for (String name : properties.stringPropertyNames()) {
      env_properties.setProperty(name, resolveValueWithEnvVars(properties.getProperty(name)));
    }
    return env_properties;
  }

  // Substitutes an environment variable into the provided `value` if one exists.
  // Otherwise substitutes an empty string.
  // Expects environment variables to be in the form ${VAR_NAME}.
  private static String resolveValueWithEnvVars(String value) {
    Pattern p = Pattern.compile("\\$\\{(\\w+)\\}");
    Matcher m = p.matcher(value);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String envVarName = m.group(1);
      String envVarValue = System.getenv(envVarName);
      m.appendReplacement(sb, null == envVarValue ? "" : Matcher.quoteReplacement(envVarValue));
    }
    m.appendTail(sb);
    return sb.toString();
  }
}
