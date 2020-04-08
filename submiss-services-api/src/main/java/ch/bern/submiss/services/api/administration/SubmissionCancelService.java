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

import ch.bern.submiss.services.api.dto.SubmissionCancelDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Interface SubmissionCancelService.
 */
public interface SubmissionCancelService {
  
  /**
   * Gets the submission cancel entity by submission id.
   *
   * @param submissionId the submission id
   * @return the by submission id
   */
  SubmissionCancelDTO getBySubmissionId(String submissionId);

  /**
   * Updates the SubmissionCancelEntity with cancelled_on and cancelled_by values.
   *
   * @param submissionCancelId the submissionCancelId
   * @param submissionCancelVersion the submissionCancelVersion
   */
  Set<ValidationError> cancellingSubmission(String submissionCancelId, Long submissionCancelVersion);

  /**
   * Creates a submission cancel entity.
   *
   * @param submissionCancel the SubmissionCancelDTO
   * @return the error
   */
  Set<ValidationError> createSubmissionCancel(SubmissionCancelDTO submissionCancel);

  /**
   * Updates the submission cancel entity.
   *
   * @param submissionCancel the SubmissionCancelDTO
   * @return the error
   */
  Set<ValidationError> updateSubmissionCancel(SubmissionCancelDTO submissionCancel);

  /**
   * Gets the available date of the submission cancel entity by submission id.
   *
   * @param submissionId the submission id
   * @return the available date
   */
  Date getAvailableDateBySubmissionId(String submissionId);
  
  /**
   * Checks if the Verfahrensabbruch document has been generated for all tenderers.
   *
   * @param submissionId the submission id
   * @return a boolean indicating whether the Verfahrensabbruch
   * 	     document has been generated for all tenderers
   */ 
  boolean cancellationDocumentCreated(String submissionId);

  /**
   * Gets the submission ids by their cancellation reason ids.
   *
   * @param cancellationReasonIds the cancellation reason ids
   * @return the submission ids by their cancellation reason ids
   */
  List<String> getSubmissionIdsByCancellationReasonIds(List<String> cancellationReasonIds);
}
