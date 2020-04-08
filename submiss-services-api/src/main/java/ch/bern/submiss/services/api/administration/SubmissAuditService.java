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

import ch.bern.submiss.services.api.dto.AuditLogDTO;
import java.util.List;

/**
 * The Interface AuditService.
 */
public interface SubmissAuditService {

  /**
   * Gets company logs.
   *
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @param levelIdOption the level id option
   * @param auditLogDTO the audit log DTO
   * @return the company logs.
   */
  List<?> getAuditLogs(int page, int pageItems, String sortColumn, String sortType,
    String levelIdOption, AuditLogDTO auditLogDTO);

  /**
   * Audit log count.
   *
   * @param auditLogDTO the audit log DTO
   * @param levelIdOption the level id option
   * @return the long
   */
  long auditLogCount(AuditLogDTO auditLogDTO, String levelIdOption);

  /**
   * Security check for Verlauf.
   */
  void auditLogSecurityCheck();
}
