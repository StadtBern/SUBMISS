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

/**
 * The Class ExclusionReasonDTO.
 */
public class ExclusionReasonDTO {

  /** The exclusion reason. */
  private MasterListValueHistoryDTO exclusionReason;

  /** The reason exists. */
  private Boolean reasonExists;

  /**
   * Gets the exclusion reason.
   *
   * @return the exclusion reason
   */
  public MasterListValueHistoryDTO getExclusionReason() {
    return exclusionReason;
  }

  /**
   * Sets the exclusion reason.
   *
   * @param exclusionReason the new exclusion reason
   */
  public void setExclusionReason(MasterListValueHistoryDTO exclusionReason) {
    this.exclusionReason = exclusionReason;
  }

  /**
   * Gets the reason exists.
   *
   * @return the reason exists
   */
  public Boolean getReasonExists() {
    return reasonExists;
  }

  /**
   * Sets the reason exists.
   *
   * @param reasonExists the new reason exists
   */
  public void setReasonExists(Boolean reasonExists) {
    this.reasonExists = reasonExists;
  }


}
