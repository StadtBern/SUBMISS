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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Class ReportResultsDTO.
 */
public class ReportResultsDTO extends AbstractDTO {

  /** The submissions. */
  private List<SubmissionDTO> submissions;

  /** The totalization by. */
  private String totalizationBy;

  /** The start date. */
  private Date startDate;

  /** The end date. */
  private Date endDate;

  /** The searched companies. */
  private Set<CompanyDTO> searchedCompanies;

  /** The searched criteria. */
  private Map<String, String> searchedCriteria;

  /**
   * Gets the submissions.
   *
   * @return the submissions
   */
  public List<SubmissionDTO> getSubmissions() {
    return submissions;
  }

  /**
   * Sets the submissions.
   *
   * @param submissions the new submissions
   */
  public void setSubmissions(List<SubmissionDTO> submissions) {
    this.submissions = submissions;
  }

  /**
   * Gets the totalization by.
   *
   * @return the totalization by
   */
  public String getTotalizationBy() {
    return totalizationBy;
  }

  /**
   * Sets the totalization by.
   *
   * @param totalizationBy the new totalization by
   */
  public void setTotalizationBy(String totalizationBy) {
    this.totalizationBy = totalizationBy;
  }

  /**
   * Gets the start date.
   *
   * @return the start date
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date.
   *
   * @param startDate the new start date
   */
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the end date.
   *
   * @return the end date
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date.
   *
   * @param endDate the new end date
   */
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  /**
   * Gets the searched criteria.
   *
   * @return the searchedCriteria
   */
  public Map<String, String> getSearchedCriteria() {
    return searchedCriteria;
  }

  /**
   * Sets the searched criteria.
   *
   * @param searchedCriteria the searchedCriteria to set
   */
  public void setSearchedCriteria(Map<String, String> searchedCriteria) {
    this.searchedCriteria = searchedCriteria;
  }

  /**
   * Gets the searched companies.
   *
   * @return the searched companies
   */
  public Set<CompanyDTO> getSearchedCompanies() {
    return searchedCompanies;
  }

  /**
   * Sets the searched companies.
   *
   * @param searchedCompanies the new searched companies
   */
  public void setSearchedCompanies(Set<CompanyDTO> searchedCompanies) {
    this.searchedCompanies = searchedCompanies;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ReportResultsDTO [ totalizationBy=" + totalizationBy + ", startDate=" + startDate
        + ", endDate=" + endDate + ", searchedCriteria=" + searchedCriteria + "]";
  }

}
