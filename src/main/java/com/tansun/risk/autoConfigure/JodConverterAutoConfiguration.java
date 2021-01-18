/*
 * Copyright 2004 - 2012 Mirko Nasato and contributors
 *           2016 - 2017 Simon Braconnier and contributors
 *
 * This file is part of JODConverter - Java OpenDocument Converter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*


package com.tansun.risk.autoConfigure;

import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnClass({DocumentConverter.class})
@ConditionalOnProperty(
  prefix = "jodconverter",
  name = "enabled",
  havingValue = "true",
  matchIfMissing = false
)
@EnableConfigurationProperties(JodConverterProperties.class)
public class JodConverterAutoConfiguration {

  private final JodConverterProperties properties;

  public JodConverterAutoConfiguration(final JodConverterProperties properties) {
    this.properties = properties;
  }

  // Creates the OfficeManager bean.
  private OfficeManager createOfficeManager() {

    final LocalOfficeManager.Builder builder = LocalOfficeManager.builder();

    if (!StringUtils.isBlank(properties.getPortNumbers())) {
      final Set<Integer> iports = new HashSet<>();
      for (final String portNumber : StringUtils.split(properties.getPortNumbers(), ", ")) {
        iports.add(NumberUtils.toInt(portNumber, 2002));
      }
      builder.portNumbers(ArrayUtils.toPrimitive(iports.toArray(new Integer[iports.size()])));
    }

    builder.officeHome(properties.getOfficeHome());
    builder.workingDir(properties.getWorkingDir());
    builder.templateProfileDir(properties.getTemplateProfileDir());
    builder.killExistingProcess(properties.isKillExistingProcess());
    builder.processTimeout(properties.getProcessTimeout());
    builder.processRetryInterval(properties.getProcessRetryInterval());
    builder.taskExecutionTimeout(properties.getTaskExecutionTimeout());
    builder.maxTasksPerProcess(properties.getMaxTasksPerProcess());
    builder.taskQueueTimeout(properties.getTaskQueueTimeout());

    // Starts the manager
    return builder.build();
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  @ConditionalOnMissingBean
  public OfficeManager officeManager() {

    return createOfficeManager();
  }

  // Must appear after the OfficeManager bean creation. Do not reorder this class by name.
  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(OfficeManager.class)
  public DocumentConverter jodConverter(final OfficeManager officeManager) {

    return LocalConverter.make(officeManager);
  }
}
*/
