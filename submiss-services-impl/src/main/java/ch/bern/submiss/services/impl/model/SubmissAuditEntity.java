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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class SubmissAuditEntity.
 */
@MappedSuperclass
public abstract class SubmissAuditEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  /** The user name. */
  @Column(name = "userName")
  private String userName;

  /** The short description. */
  @Column(name = "shortDescription")
  private String shortDescription;

  /** The created on. */
  @Column(name = "createdOn")
  private Date createdOn;

  /** The task resource. */
  @Column(name = "resource_key")
  private String resourceKey;

  /** The translation. */
  @Column(name = "translation")
  private String translation;

  /** The tenant id. */
  @Column(name = "tenant_id")
  private String tenantId;

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

  /**
   * Gets the translation.
   *
   * @return the translation
   */
  public String getTranslation() {
    return translation;
  }

  /**
   * Sets the translation.
   *
   * @param translation the new translation
   */
  public void setTranslation(String translation) {
    this.translation = translation;
  }

  /**
   * Gets the tenant id.
   *
   * @return the tenant id
   */
  public String getTenantId() {
    return tenantId;
  }

  /**
   * Sets the tenant id.
   *
   * @param tenantId the new tenant id
   */
  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
