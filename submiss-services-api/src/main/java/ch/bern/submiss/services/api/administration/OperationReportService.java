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

import ch.bern.submiss.services.api.dto.OperationReportDTO;
import ch.bern.submiss.services.api.dto.OperationReportResultsDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Set;

/**
 * The Interface OperationReportService.
 */
public interface OperationReportService {

  /**
   * Search.
   *
   * @param operationReportDTO the OperationReport DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return the OperationReportResults DTO
   */
  OperationReportResultsDTO search(OperationReportDTO operationReportDTO, int page, int pageItems,
      String sortColumn, String sortType);

  /**
   * Gets the number of operation report results.
   *
   * @param operationReportDTO the operation report DTO
   * @return the number of operation report results
   */
  Long getNumberOfOperationReportResults(OperationReportDTO operationReportDTO);

  /**
   * Validates operation form.
   *
   * @param operationReportDTO the OperationReport DTO
   * @return the validation error in case of existing error
   */
  Set<ValidationError> validate(OperationReportDTO operationReportDTO);

  /**
   * Generate report.
   *
   * @param operationReportResultsDTO the report results
   * @param selectedColums the selected colums
   * @param caseFormat the case format
   * @param sumAmount the sum amount
   * @return the byte[]
   */
  byte[] generateReport(OperationReportResultsDTO operationReportResultsDTO, String selectedColums,
      String caseFormat, String sumAmount);

  /**
   * Gets report's file name according to the case format. The case format could be excel or pdf.
   *
   * @param caseFormat the case format
   * @return the file name
   */
  String getReportFileName(String caseFormat);


  /**
   * Calculate report results.
   *
   * @param operationReportDTO the operation report DTO
   * @return the operation report results DTO
   */
  OperationReportResultsDTO calculateReportResults(OperationReportDTO operationReportDTO);

  /**
   * Security check for Operation Report.
   */
  void operationReportSecurityCheck();

  /**
   * Checks the operation report results number in order to proceed with generating the report.
   * If report results are greater than maximum results
   * returns the maximum results,
   * if report results are 0 returns 0
   * else returns NULL.
   *
   * @param operationReportDTO the operationReportDTO
   * @return the results
   */
  Long proceedToOperationResults(OperationReportDTO operationReportDTO);
}
