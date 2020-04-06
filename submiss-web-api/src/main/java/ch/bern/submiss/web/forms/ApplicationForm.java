/*
 *  Submiss, eProcurement system for managing tenders
 *  Copyright (C) 2019 Stadt Bern
 *  Licensed under the EUPL, Version 1.2 or - as soon as they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at:
 *  https://joinup.ec.europa.eu/collection/eupl
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the Licence for the specific language governing permissions and limitations
 *  under the Licence.
 */

package ch.bern.submiss.web.forms;

import java.util.Date;

/**
 * The Class ApplicationForm.
 */
public class ApplicationForm {
  
  /** The application id. */
  private String applicationId;
  
  /** The application date. */
  private Date applicationDate;
  
  /** The application information. */
  private String applicationInformation;
  
  /**
   * Gets the application id.
   *
   * @return the application id
   */
  public String getApplicationId() {
    return applicationId;
  }

  /**
   * Sets the application id.
   *
   * @param applicationId the new application id
   */
  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Gets the application date.
   *
   * @return the application date
   */
  public Date getApplicationDate() {
    return applicationDate;
  }
  
  /**
   * Sets the application date.
   *
   * @param applicationDate the new application date
   */
  public void setApplicationDate(Date applicationDate) {
    this.applicationDate = applicationDate;
  }
  
  /**
   * Gets the application information.
   *
   * @return the application information
   */
  public String getApplicationInformation() {
    return applicationInformation;
  }
  
  /**
   * Sets the application information.
   *
   * @param applicationInformation the new application information
   */
  public void setApplicationInformation(String applicationInformation) {
    this.applicationInformation = applicationInformation;
  }
  
}
