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

import java.util.Date;
import ch.bern.submiss.services.api.constants.LogibStatus;
import ch.bern.submiss.services.api.constants.ProofStatus;

/**
 * The Class CompanyFilterForm.
 */
public class CompanyFilterForm {

  /**
   * The company name.
   */
  private String companyName;

  /**
   * The company tel.
   */
  private String companyTel;

  /**
   * The post code.
   */
  private String postCode;

  /**
   * The work types.
   */
  private String workTypes;

  /**
   * The apprentice factor.
   */
  private String apprenticeFactor;

  /**
   * The logib.
   */
  private LogibStatus logib;

  /**
   * The tlp.
   */
  private String tlp;

  /**
   * The proof status.
   */
  private ProofStatus proofStatus;

  /**
   * The certificate date.
   */
  private Date certificateDate;

  /**
   * The fiftyPlusFactor.
   */
  private String fiftyPlusFactor;

  /**
   * Gets the company name.
   *
   * @return the company name
   */
  public String getCompanyName() {
    return companyName;
  }

  /**
   * Sets the company name.
   *
   * @param companyName the new company name
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  /**
   * Gets the company tel.
   *
   * @return the company tel
   */
  public String getCompanyTel() {
    return companyTel;
  }

  /**
   * Sets the company tel.
   *
   * @param companyTel the new company tel
   */
  public void setCompanyTel(String companyTel) {
    this.companyTel = companyTel;
  }

  /**
   * Gets the post code.
   *
   * @return the post code
   */
  public String getPostCode() {
    return postCode;
  }

  /**
   * Sets the post code.
   *
   * @param postCode the new post code
   */
  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  /**
   * Gets the work types.
   *
   * @return the work types
   */
  public String getWorkTypes() {
    return workTypes;
  }

  /**
   * Sets the work types.
   *
   * @param workTypes the new work types
   */
  public void setWorkTypes(String workTypes) {
    this.workTypes = workTypes;
  }

  /**
   * Gets the apprentice factor.
   *
   * @return the apprentice factor
   */
  public String getApprenticeFactor() {
    return apprenticeFactor;
  }

  /**
   * Sets the apprentice factor.
   *
   * @param apprenticeFactor the new apprentice factor
   */
  public void setApprenticeFactor(String apprenticeFactor) {
    this.apprenticeFactor = apprenticeFactor;
  }

  /**
   * Gets the logib.
   *
   * @return the logib
   */
  public LogibStatus getLogib() {
    return logib;
  }

  /**
   * Sets the logib.
   *
   * @param logib the new logib
   */
  public void setLogib(LogibStatus logib) {
    this.logib = logib;
  }

  /**
   * Gets the tlp.
   *
   * @return the tlp
   */
  public String getTlp() {
    return tlp;
  }

  /**
   * Sets the tlp.
   *
   * @param tlp the new tlp
   */
  public void setTlp(String tlp) {
    this.tlp = tlp;
  }

  /**
   * Gets the proof status.
   *
   * @return the proof status
   */
  public ProofStatus getProofStatus() {
    return proofStatus;
  }

  /**
   * Sets the proof status.
   *
   * @param proofStatus the new proof status
   */
  public void setProofStatus(ProofStatus proofStatus) {
    this.proofStatus = proofStatus;
  }

  /**
   * Gets the certificate date.
   *
   * @return the certificate date
   */
  public Date getCertificateDate() {
    return certificateDate;
  }

  /**
   * Sets the certificate date.
   *
   * @param certificateDate the new certificate date
   */
  public void setCertificateDate(Date certificateDate) {
    this.certificateDate = certificateDate;
  }

  /**
   * Gets the fiftyPlus factor.
   */
  public String getFiftyPlusFactor() {
    return fiftyPlusFactor;
  }

  /**
   * Sets the fiftyPlus factor.
   */
  public void setFiftyPlusFactor(String fiftyPlusFactor) {
    this.fiftyPlusFactor = fiftyPlusFactor;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CompanyFilterForm [companyName=" + companyName + ", companyTel=" + companyTel
      + ", postCode=" + postCode + ", workTypes=" + workTypes + ", apprenticeFactor="
      + apprenticeFactor + ", fiftyPlusFactor=" + fiftyPlusFactor
      + ", logib=" + logib + ", tlp=" + tlp + ", proofStatus=" + proofStatus
      + ", certificateDate=" + certificateDate + "]";
  }
}
