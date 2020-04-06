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

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonView;
import ch.bern.submiss.services.api.util.View;

/**
 * The Class TenderStatusHistoryDTO.
 */
public class TenderStatusHistoryDTO {

  /** The id. */
  @JsonView(View.Internal.class)
  private String id;

  /** The tender id. */
  @JsonView(View.Internal.class)
  private SubmissionDTO tenderId;

  /** The on date. */
  @JsonView(View.Public.class)
  private Timestamp onDate;

  /** The status id. */
  @JsonView(View.Public.class)
  private String statusId;

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
   * Gets the tender id.
   *
   * @return the tender id
   */
  public SubmissionDTO getTenderId() {
    return tenderId;
  }

  /**
   * Sets the tender id.
   *
   * @param tenderId the new tender id
   */
  public void setTenderId(SubmissionDTO tenderId) {
    this.tenderId = tenderId;
  }

  /**
   * Gets the on date.
   *
   * @return the on date
   */
  public Timestamp getOnDate() {
    return onDate;
  }

  /**
   * Sets the on date.
   *
   * @param onDate the new on date
   */
  public void setOnDate(Timestamp onDate) {
    this.onDate = onDate;
  }

  /**
   * Gets the status id.
   *
   * @return the status id
   */
  public String getStatusId() {
    return statusId;
  }

  /**
   * Sets the status id.
   *
   * @param statusId the new status id
   */
  public void setStatusId(String statusId) {
    this.statusId = statusId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TenderStatusHistoryDTO [id=" + id + ", onDate=" + onDate + ", statusId=" + statusId
        + "]";
  }

}
