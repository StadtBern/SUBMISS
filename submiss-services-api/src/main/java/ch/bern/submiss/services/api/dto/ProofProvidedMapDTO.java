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
 * The Class ProofProvidedMapDTO.
 */
public class ProofProvidedMapDTO {

  /** The submittent id. */
  String submittentId;

  /** The company id. */
  String companyId;

  /** The is proof provided. */
  boolean isProofProvided;

  /**
   * Gets the submittent id.
   *
   * @return the submittent id
   */
  public String getSubmittentId() {
    return submittentId;
  }

  /**
   * Sets the submittent id.
   *
   * @param submittentId the new submittent id
   */
  public void setSubmittentId(String submittentId) {
    this.submittentId = submittentId;
  }

  /**
   * Gets the company id.
   *
   * @return the company id
   */
  public String getCompanyId() {
    return companyId;
  }

  /**
   * Sets the company id.
   *
   * @param companyId the new company id
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /**
   * Gets the is proof provided.
   *
   * @return the is proof provided
   */
  public boolean getIsProofProvided() {
    return isProofProvided;
  }

  /**
   * Sets the is proof provided.
   *
   * @param isProofProvided the new is proof provided
   */
  public void setIsProofProvided(boolean isProofProvided) {
    this.isProofProvided = isProofProvided;
  }
}
