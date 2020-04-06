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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The Class EmailTemplateForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class EmailTemplateForm {

  /** The id. */
  private String id;

  /** The short code. */
  private String shortCode;

  /** The workflow related. */
  private Boolean workflowRelated;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the short code.
   *
   * @return the short code
   */
  public String getShortCode() {
    return shortCode;
  }

  /**
   * Sets the short code.
   *
   * @param shortCode the new short code
   */
  public void setShortCode(String shortCode) {
    this.shortCode = shortCode;
  }

  /**
   * Gets the workflow related.
   *
   * @return the workflow related
   */
  public Boolean getWorkflowRelated() {
    return workflowRelated;
  }

  /**
   * Sets the workflow related.
   *
   * @param workflowRelated the new workflow related
   */
  public void setWorkflowRelated(Boolean workflowRelated) {
    this.workflowRelated = workflowRelated;
  }
}
