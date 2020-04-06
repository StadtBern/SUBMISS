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

import java.util.Set;
import ch.bern.submiss.services.api.constants.LogibStatus;
import ch.bern.submiss.services.api.constants.ProofStatus;

/**
 * The Class CompanySearchDTO.
 */
public class CompanySearchDTO {

  /** The company name. */
  // TDDO: add more fields according to UCs that are not impelemented yet
  private String companyName;

  /** The post code. */
  private String postCode;

  /** The location. */
  private String location;

  /** The work types. */
  private Set<MasterListValueHistoryDTO> workTypes;

  /** The country id. */
  private String countryId;

  /** The company tel. */
  private String companyTel;

  /** The proof status. */
  private ProofStatus proofStatus;

  /** The add info. */
  private String addInfo;

  /** The notes. */
  private String notes;

  /** The ilo. */
  private MasterListValueHistoryDTO ilo;

  /** The archived. */
  private Boolean archived;

  /** The logib. */
  private Boolean logib;

  /** The logib argib. */
  private Boolean logibArgib;

  /** The tlp. */
  private Boolean tlp;

  /** The filter. */
  private CompanyFilterDTO filter;

  /** The logib status. */
  private LogibStatus logibStatus;

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
   * Gets the location.
   *
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets the location.
   *
   * @param location the new location
   */
  public void setLocation(String location) {
    this.location = location;
  }


  /**
   * Gets the work types.
   *
   * @return the work types
   */
  public Set<MasterListValueHistoryDTO> getWorkTypes() {
    return workTypes;
  }

  /**
   * Sets the work types.
   *
   * @param workTypes the new work types
   */
  public void setWorkTypes(Set<MasterListValueHistoryDTO> workTypes) {
    this.workTypes = workTypes;
  }

  /**
   * Gets the country id.
   *
   * @return the country id
   */
  public String getCountryId() {
    return countryId;
  }

  /**
   * Sets the country id.
   *
   * @param countryId the new country id
   */
  public void setCountryId(String countryId) {
    this.countryId = countryId;
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
   * Gets the adds the info.
   *
   * @return the adds the info
   */
  public String getAddInfo() {
    return addInfo;
  }

  /**
   * Sets the adds the info.
   *
   * @param addInfo the new adds the info
   */
  public void setAddInfo(String addInfo) {
    this.addInfo = addInfo;
  }

  /**
   * Gets the notes.
   *
   * @return the notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Sets the notes.
   *
   * @param notes the new notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Gets the ilo.
   *
   * @return the ilo
   */
  public MasterListValueHistoryDTO getIlo() {
    return ilo;
  }

  /**
   * Sets the ilo.
   *
   * @param ilo the new ilo
   */
  public void setIlo(MasterListValueHistoryDTO ilo) {
    this.ilo = ilo;
  }

  /**
   * Gets the archived.
   *
   * @return the archived
   */
  public Boolean getArchived() {
    return archived;
  }

  /**
   * Sets the archived.
   *
   * @param archived the new archived
   */
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  /**
   * Gets the logib.
   *
   * @return the logib
   */
  public Boolean getLogib() {
    return logib;
  }

  /**
   * Sets the logib.
   *
   * @param logib the new logib
   */
  public void setLogib(Boolean logib) {
    this.logib = logib;
  }

  /**
   * Gets the logib argib.
   *
   * @return the logib argib
   */
  public Boolean getLogibArgib() {
    return logibArgib;
  }

  /**
   * Sets the logib argib.
   *
   * @param logibArgib the new logib argib
   */
  public void setLogibArgib(Boolean logibArgib) {
    this.logibArgib = logibArgib;
  }

  /**
   * Gets the tlp.
   *
   * @return the tlp
   */
  public Boolean getTlp() {
    return tlp;
  }

  /**
   * Sets the tlp.
   *
   * @param tlp the new tlp
   */
  public void setTlp(Boolean tlp) {
    this.tlp = tlp;
  }

  /**
   * Gets the filter.
   *
   * @return the filter
   */
  public CompanyFilterDTO getFilter() {
    return filter;
  }

  /**
   * Sets the filter.
   *
   * @param filter the new filter
   */
  public void setFilter(CompanyFilterDTO filter) {
    this.filter = filter;
  }

  /**
   * Gets the logib status.
   *
   * @return the logib status
   */
  public LogibStatus getLogibStatus() {
    return logibStatus;
  }

  /**
   * Sets the logib status.
   *
   * @param logibStatus the new logib status
   */
  public void setLogibStatus(LogibStatus logibStatus) {
    this.logibStatus = logibStatus;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CompanySearchDTO [companyName=" + companyName + ", postCode=" + postCode + ", location="
        + location + ", countryId=" + countryId + ", companyTel=" + companyTel + ", proofStatus="
        + proofStatus + ", addInfo=" + addInfo + ", notes=" + notes + ",archived=" + archived
        + ", logib=" + logib + ", logibArgib=" + logibArgib + ", tlp=" + tlp + "]";
  }



}
