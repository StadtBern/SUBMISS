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
import java.util.Map;

/**
 * The Class ReportResultsDTO.
 */
public class ReportBaseResultsDTO extends AbstractStammdatenDTO {

  /**
   * The start date.
   */
  private Date startDate;

  /**
   * The end date.
   */
  private Date endDate;

  private Map<String, String> searchedCriteria;

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
   * @return the searchedCriteria
   */
  public Map<String, String> getSearchedCriteria() {
    return searchedCriteria;
  }

  /**
   * @param searchedCriteria the searchedCriteria to set
   */
  public void setSearchedCriteria(Map<String, String> searchedCriteria) {
    this.searchedCriteria = searchedCriteria;
  }

}
