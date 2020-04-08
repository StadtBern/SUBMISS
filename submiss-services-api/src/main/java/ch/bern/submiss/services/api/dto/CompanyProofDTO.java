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

/**
 * The Class CompanyProofDTO.
 */
public class CompanyProofDTO {

  /** The company id. */
  private CompanyDTO companyId;

  /** The proof. */
  private ProofHistoryDTO proof;

  /** The proof date. */
  private Timestamp proofDate;

  /** The required. */
  private Boolean required;

  /** The mod user. */
  private String modUser;

  /** The mod date. */
  private Timestamp modDate;

  /** The has changed. */
  private boolean hasChanged;

  /** The is valid. */
  private boolean isInvalid;

  /**
   * Gets the company id.
   *
   * @return the company id
   */
  public CompanyDTO getCompanyId() {
    return companyId;
  }

  /**
   * Sets the company id.
   *
   * @param companyId the new company id
   */
  public void setCompanyId(CompanyDTO companyId) {
    this.companyId = companyId;
  }

  /**
   * Gets the proof.
   *
   * @return the proof
   */
  public ProofHistoryDTO getProof() {
    return proof;
  }

  /**
   * Sets the proof.
   *
   * @param proof the new proof
   */
  public void setProof(ProofHistoryDTO proof) {
    this.proof = proof;
  }

  /**
   * Gets the proof date.
   *
   * @return the proof date
   */
  public Timestamp getProofDate() {
    return proofDate;
  }

  /**
   * Sets the proof date.
   *
   * @param proofDate the new proof date
   */
  public void setProofDate(Timestamp proofDate) {
    this.proofDate = proofDate;
  }

  /**
   * Gets the required.
   *
   * @return the required
   */
  public Boolean getRequired() {
    return required;
  }

  /**
   * Sets the required.
   *
   * @param required the new required
   */
  public void setRequired(Boolean required) {
    this.required = required;
  }

  /**
   * Gets the mod user.
   *
   * @return the mod user
   */
  public String getModUser() {
    return modUser;
  }

  /**
   * Sets the mod user.
   *
   * @param modUser the new mod user
   */
  public void setModUser(String modUser) {
    this.modUser = modUser;
  }

  /**
   * Gets the mod date.
   *
   * @return the mod date
   */
  public Timestamp getModDate() {
    return modDate;
  }

  /**
   * Sets the mod date.
   *
   * @param modDate the new mod date
   */
  public void setModDate(Timestamp modDate) {
    this.modDate = modDate;
  }

  /**
   * Checks if is checks for changed.
   *
   * @return true, if is checks for changed
   */
  public boolean isHasChanged() {
    return hasChanged;
  }

  /**
   * Sets the checks for changed.
   *
   * @param hasChanged the new checks for changed
   */
  public void setHasChanged(boolean hasChanged) {
    this.hasChanged = hasChanged;
  }

  /**
   * Get the isInvalid value.
   *
   * @return true if proof is valid
   */
  public boolean isInvalid() {
    return isInvalid;
  }

  /**
   * Sets the isInvalid value.
   *
   * @param invalid true if proof is invalid
   */
  public void setInvalid(boolean invalid) {
    isInvalid = invalid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CompanyProofDTO [ proofDate=" + proofDate + ", required=" + required + ", modUser="
        + modUser + ", modDate=" + modDate + ", hasChanged=" + hasChanged + "]";
  }


}
