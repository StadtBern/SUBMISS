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

import java.util.List;

/**
 * The Class OperationReportResultsDTO.
 */
public class OperationReportResultsDTO extends ReportBaseResultsDTO {

  /**
   * The geKoResultDTOs.
   */
  private List<GeKoResultDTO> geKoResultDTOs;

  /**
   * The total.
   */
  private String total;

  /**
   * The so sum.
   */
  private String soSum;

  /**
   * The e sum.
   */
  private String eSum;

  /**
   * The sum.
   */
  private String fSum;

  /**
   * Gets the ge ko result DT os.
   *
   * @return the ge ko result DT os
   */
  public List<GeKoResultDTO> getGeKoResultDTOs() {
    return geKoResultDTOs;
  }

  /**
   * Sets the ge ko result DT os.
   *
   * @param geKoResultDTOs the new ge ko result DT os
   */
  public void setGeKoResultDTOs(List<GeKoResultDTO> geKoResultDTOs) {
    this.geKoResultDTOs = geKoResultDTOs;
  }

  /**
   * Gets the total.
   *
   * @return the total
   */
  public String getTotal() {
    return total;
  }

  /**
   * Sets the total.
   *
   * @param total the new total
   */
  public void setTotal(String total) {
    this.total = total;
  }

  /**
   * Gets the so sum.
   *
   * @return the so sum
   */
  public String getSoSum() {
    return soSum;
  }

  /**
   * Sets the so sum.
   *
   * @param soSum the new so sum
   */
  public void setSoSum(String soSum) {
    this.soSum = soSum;
  }

  /**
   * Gets the e sum.
   *
   * @return the e sum
   */
  public String geteSum() {
    return eSum;
  }

  /**
   * Sets the e sum.
   *
   * @param eSum the new e sum
   */
  public void seteSum(String eSum) {
    this.eSum = eSum;
  }

  /**
   * Gets the f sum.
   *
   * @return the f sum
   */
  public String getfSum() {
    return fSum;
  }

  /**
   * Sets the f sum.
   *
   * @param fSum the new f sum
   */
  public void setfSum(String fSum) {
    this.fSum = fSum;
  }


}
