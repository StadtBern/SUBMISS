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

import ch.bern.submiss.services.api.dto.AwardEvaluationDocumentDTO;
import java.io.IOException;
import java.util.List;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SuitabilityDocumentDTO;

public interface ImportExportFileService {
  
  

  /**
   * Exports criteria into byte [] as .xlsx file
   * @param submissionId 
   * @param type 
   * @param 
   * @return the byte[]
   */
  byte[] exportTableOfCriteria(String submissionId, List<OfferDTO> offers, String type);
  
  /**
   * Exports criteria into byte [] as .eml file.
   * @param submissionId 
   * @param type 
   * @param 
   * @return the byte[]
   */
  byte[] exportTableOfCriteriaAsEml(String submissionId, List<OfferDTO> offerCriterionDTOs,
      String type);
  
  /**
   * imports fileId pointer to uploaded file.
   * @param type 
   * 
   * @param
   * @return the byte[]
   */
  byte[] importTableOfCriteria(String submissionId, String fileId, String type) throws IOException;
  
  
  /**
   * imports fileId pointer to uploaded file.
   * @param type 
   * 
   * @param
   * @return the byte[]
   */
  String checkImportedDocumentForChanges(String submissionId, String fileId, String type) throws IOException;

  /**
   * Generates filename with timestamp based on type. 
   * @param type
   * @return
   */
  String generateExportedFilename(String type, boolean isMail);

  /**
   * Export document of criteria.
   *
   * @param submissionId the submission id
   * @param offers the offers
   * @param type the type
   * @return the byte[]
   */
  byte[] exportDocumentOfCriteria(String submissionId, List<SuitabilityDocumentDTO> offers, String type);

  /**
   * Export the award evaluation document.
   *
   * @param submissionId the submission id
   * @param awardEvaluationOffers the awardEvaluationOffers
   * @param type the type
   *
   * @return the byte[]
   */
  byte[] exportAwardEvaluationDocument(String submissionId,
    List<AwardEvaluationDocumentDTO> awardEvaluationOffers, String type);
}
