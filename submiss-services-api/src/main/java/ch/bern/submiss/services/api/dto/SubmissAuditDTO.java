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

package ch.bern.submiss.services.api.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonView;
import ch.bern.submiss.services.api.util.View;

/**
 * The Class AuditCompanyDTO.
 */
public class SubmissAuditDTO {

  /** The id. */
  @JsonView(View.Public.class)
  private String id;

  /** The userName. */
  @JsonView(View.Public.class)
  private String userName;

  /** The shortDescription. */
  @JsonView(View.Public.class)
  private String shortDescription;

  /** The created on. */
  @JsonView(View.Public.class)
  private Date createdOn;

  /** The additional info. */
  @JsonView(View.Public.class)
  private String resourceKey;



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
   * Gets the user name.
   *
   * @return the user name
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Sets the user name.
   *
   * @param userName the new user name
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Gets the short description.
   *
   * @return the short description
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Sets the short description.
   *
   * @param shortDescription the new short description
   */
  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public Date getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the resource key.
   *
   * @return the resource key
   */
  public String getResourceKey() {
    return resourceKey;
  }

  /**
   * Sets the resource key.
   *
   * @param resourceKey the new resource key
   */
  public void setResourceKey(String resourceKey) {
    this.resourceKey = resourceKey;
  }
}
