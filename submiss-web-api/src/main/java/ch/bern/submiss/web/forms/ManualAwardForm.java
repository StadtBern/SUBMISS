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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class ManualAwardForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManualAwardForm {
  
  /** The submission id. */
  private String submissionId;
  
  /** The offer ids. */
  private List<String> offerIds;

  /**
   * Gets the submission id.
   *
   * @return the submission id
   */
  public String getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission id.
   *
   * @param submissionId the new submission id
   */
  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
  }

  /**
   * Gets the offer ids.
   *
   * @return the offer ids
   */
  public List<String> getOfferIds() {
    return offerIds;
  }

  /**
   * Sets the offer ids.
   *
   * @param offerIds the new offer ids
   */
  public void setOfferIds(List<String> offerIds) {
    this.offerIds = offerIds;
  }

}
