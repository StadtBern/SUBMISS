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
import ch.bern.submiss.services.api.constants.LogibStatus;
import ch.bern.submiss.services.api.constants.ProofStatus;

public class CompanyFilterDTO {

  private String companyName;
  private String companyTel;
  private String postCode;
  private String workTypes;
  private String apprenticeFactor;
  private LogibStatus logib;
  private String tlp;
  private ProofStatus proofStatus;
  private Date certificateDate;
  private String fiftyPlusFactor;


  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyTel() {
    return companyTel;
  }

  public void setCompanyTel(String companyTel) {
    this.companyTel = companyTel;
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public String getWorkTypes() {
    return workTypes;
  }

  public void setWorkTypes(String workTypes) {
    this.workTypes = workTypes;
  }

  public String getApprenticeFactor() {
    return apprenticeFactor;
  }

  public void setApprenticeFactor(String apprenticeFactor) {
    this.apprenticeFactor = apprenticeFactor;
  }

  public LogibStatus getLogib() {
    return logib;
  }

  public void setLogib(LogibStatus logib) {
    this.logib = logib;
  }

  public String getTlp() {
    return tlp;
  }

  public void setTlp(String tlp) {
    this.tlp = tlp;
  }

  public ProofStatus getProofStatus() {
    return proofStatus;
  }

  public void setProofStatus(ProofStatus proofStatus) {
    this.proofStatus = proofStatus;
  }


  public Date getCertificateDate() {
    return certificateDate;
  }

  public void setCertificateDate(Date certificateDate) {
    this.certificateDate = certificateDate;
  }

  public String getFiftyPlusFactor() {
    return fiftyPlusFactor;
  }

  public void setFiftyPlusFactor(String fiftyPlusFactor) {
    this.fiftyPlusFactor = fiftyPlusFactor;
  }
}
