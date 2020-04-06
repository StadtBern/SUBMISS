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


import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyProofForm {

  private CompanyDTO companyId;

  private ProofHistoryDTO proof;

  private Timestamp proofDate;

  private Boolean required;

  private String modUser;

  private Timestamp modDate;

  private boolean hasChanged;

  public CompanyDTO getCompanyId() {
    return companyId;
  }

  public void setCompanyId(CompanyDTO companyId) {
    this.companyId = companyId;
  }

  public ProofHistoryDTO getProof() {
    return proof;
  }

  public void setProof(ProofHistoryDTO proof) {
    this.proof = proof;
  }

  public Timestamp getProofDate() {
    return proofDate;
  }

  public void setProofDate(Timestamp proofDate) {
    this.proofDate = proofDate;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public String getModUser() {
    return modUser;
  }

  public void setModUser(String modUser) {
    this.modUser = modUser;
  }

  public Timestamp getModDate() {
    return modDate;
  }

  public void setModDate(Timestamp modDate) {
    this.modDate = modDate;
  }

  public boolean isHasChanged() {
    return hasChanged;
  }

  public void setHasChanged(boolean hasChanged) {
    this.hasChanged = hasChanged;
  }

}
