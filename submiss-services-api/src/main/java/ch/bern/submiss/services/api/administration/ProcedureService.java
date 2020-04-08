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

import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.dto.ProcedureHistoryDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.List;
import java.util.Set;


/**
 * The Interface ProcedureService.
 */
public interface ProcedureService {

  /**
   * Read procedure.
   *
   * @param processType the process type
   * @param proccess the proccess
   * @param tenant the tenant
   * @return the procedure history DTO
   */
  ProcedureHistoryDTO readProcedure(String processType, Process proccess, String tenant);

  /**
   * Read procedures by tenant.
   *
   * @return the list
   */
  List<ProcedureHistoryDTO> readProceduresByTenant();

  /**
   * Read procedure by id.
   *
   * @param id the id
   * @return the procedure history DTO
   */
  ProcedureHistoryDTO readProcedureById(String id);

  /**
   * Insert new procedure.
   *
   * @param dto the dto
   * @return the error
   */
  Set<ValidationError> insertNewProcedure(ProcedureHistoryDTO dto);
  
}
