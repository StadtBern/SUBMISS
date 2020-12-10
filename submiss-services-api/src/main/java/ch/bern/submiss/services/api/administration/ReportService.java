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

package ch.bern.submiss.services.api.administration;

import ch.bern.submiss.services.api.dto.ReportDTO;
import ch.bern.submiss.services.api.dto.ReportResultsDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Map;
import java.util.Set;


/**
 * The Interface ReportService.
 */
public interface ReportService {

  /**
   * Generate report.
   *
   * @param reportResults the report results
   * @return the byte[]
   */
  byte[] generateReport(ReportResultsDTO reportResults);

  /**
   * Gets the report results.
   *
   * @param reportDTO the report DTO
   * @return the report results DTO
   */
  ReportResultsDTO getReportResults(ReportDTO reportDTO);
  
  /**
   * Gets the report file name.
   *
   * @param totalizationBy the totalization by
   * @return the report file name
   */
  String getReportFileName(String totalizationBy);

  /**
   * Gets the report search criteria.
   *
   * @param reportDTO the report DTO
   * @return the report search criteria
   */
  Map<String, String> getReportSearchCriteria(ReportDTO reportDTO);

  /**
   * Gets the number of results by report.
   *
   * @param reportDTO the report DTO
   * @return the number of results by report
   */
  Long getNumberOfResultsByReport(ReportDTO reportDTO);

  /**
   * Security check for Report.
   */
  void reportSecurityCheck();

  /**
   * Checks the report results number in order to proceed with generating the report.
   * If report results are greater than maximum results
   * returns the maximum results
   * else returns NULL.
   *
   * @param reportDTO the reportDTO
   * @return the results
   */
  Long proceedToResults(ReportDTO reportDTO);

  /**
   * Report validation.
   *
   * @param reportDTO the reportDTO
   * @return the error
   */
  Set<ValidationError> validate(ReportDTO reportDTO);
}
