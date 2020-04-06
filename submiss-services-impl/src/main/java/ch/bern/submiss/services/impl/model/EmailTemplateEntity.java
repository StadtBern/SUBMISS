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

package ch.bern.submiss.services.impl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The Class EmailTemplateEntity.
 */
@Entity
@Table(name = "SUB_EMAIL_TEMPLATE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class EmailTemplateEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  /** The short code. */
  @Column(name = "SHORT_CODE")
  private String shortCode;

  /** The workflow related. */
  @Column(name = "WORKFLOW_RELATED")
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
