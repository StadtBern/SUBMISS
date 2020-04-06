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

import ch.bern.submiss.services.api.constants.EmailTemplate;

/**
 * The Class EmailAttributesForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class EmailAttributesForm {

  /** The id. */
  private String id;

  /** The send type. */
  private EmailTemplate.SEND_TYPE sendType;

  /** The reciever role. */
  private String recieverRole;

  /** The department. */
  private DepartmentForm department;

  /** The reciever. */
  private String reciever;

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
   * Gets the send type.
   *
   * @return the send type
   */
  public EmailTemplate.SEND_TYPE getSendType() {
    return sendType;
  }

  /**
   * Sets the send type.
   *
   * @param sendType the new send type
   */
  public void setSendType(EmailTemplate.SEND_TYPE sendType) {
    this.sendType = sendType;
  }

  /**
   * Gets the reciever role.
   *
   * @return the reciever role
   */
  public String getRecieverRole() {
    return recieverRole;
  }

  /**
   * Sets the reciever role.
   *
   * @param recieverRole the new reciever role
   */
  public void setRecieverRole(String recieverRole) {
    this.recieverRole = recieverRole;
  }

  /**
   * Gets the department.
   *
   * @return the department
   */
  public DepartmentForm getDepartment() {
    return department;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(DepartmentForm department) {
    this.department = department;
  }

  /**
   * Gets the reciever.
   *
   * @return the reciever
   */
  public String getReciever() {
    return reciever;
  }

  /**
   * Sets the reciever.
   *
   * @param reciever the new reciever
   */
  public void setReciever(String reciever) {
    this.reciever = reciever;
  }

}
