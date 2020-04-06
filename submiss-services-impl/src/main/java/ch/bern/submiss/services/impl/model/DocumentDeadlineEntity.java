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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class DocumentDeadlineEntity.
 */
@Entity
@Table(name = "SUB_DOCUMENT_DEADLINE")
public class DocumentDeadlineEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  /** The version id. */
  @Column(name = "FK_VERSION")
  private String versionId;

  /** The deadline. */
  @Column(name = "DEADLINE")
  private Date deadline;

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
   * Gets the version id.
   *
   * @return the version id
   */
  public String getVersionId() {
    return versionId;
  }

  /**
   * Sets the version id.
   *
   * @param versionId the new version id
   */
  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }

  /**
   * Gets the deadline.
   *
   * @return the deadline
   */
  public Date getDeadline() {
    return deadline;
  }

  /**
   * Sets the deadline.
   *
   * @param deadline the new deadline
   */
  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }
}
