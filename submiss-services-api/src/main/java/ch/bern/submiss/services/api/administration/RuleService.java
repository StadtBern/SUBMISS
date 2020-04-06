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

import java.util.List;
import java.util.Map;

import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.DocumentDTO;
import ch.bern.submiss.services.api.dto.EmailTemplateTenantDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;

/**
 * The Interface RuleService.
 */
public interface RuleService {

  
  /**
   * Run project template rules.
   *
   * @param submissionDTO the submission DTO
   * @param companyDTO the company DTO
   * @param documentDTO the document DTO
   * @param offerDTO the offer DTO
   * @param placeholderMappings the placeholder mappings
   * @param ruleCode the rule code
   */
  void runProjectTemplateRules(SubmissionDTO submissionDTO, CompanyDTO companyDTO,
      DocumentDTO documentDTO, OfferDTO offerDTO, Map<String, String> placeholderMappings,
      String ruleCode);

  /**
   * Gets the project allowed templates.
   *
   * @param submissionDTO the submission DTO
   * @return the project allowed templates
   */
  List<MasterListValueHistoryDTO> getProjectAllowedTemplates(SubmissionDTO submissionDTO); 
  
  /**
   * Gets the project upload allowed templates.
   *
   * @return the project upload allowed templates
   */
  List<MasterListValueHistoryDTO> getProjectUploadAllowedTemplates(); 
  
  /**
   * Gets the company allowed templates.
   *
   * @param companyDTO the company DTO
   * @return the company allowed templates
   */
  List<MasterListValueHistoryDTO> getCompanyAllowedTemplates(CompanyDTO companyDTO); 
  
  /**
   * Gets the company upload allowed templates.
   *
   * @return the company allowed templates
   */
  List<MasterListValueHistoryDTO> getCompanyUploadAllowedTemplates();
   
  /**
   * Replace submission placeholders.
   *
   * @param submissionDTO the submission DTO
   * @param placeholderMappings the placeholder mappings
   */
  void replaceSubmissionPlaceholders(SubmissionDTO submissionDTO,Map<String, String> placeholderMappings);
  
  /**
   * Gets the project department templates.
   *
   * @param submissionDTO the submission DTO
   * @return the project department templates
   */
  List<MasterListValueHistoryDTO> getProjectDepartmentTemplates(SubmissionDTO submissionDTO);

  /**
   * Retrieve project email templates.
   *
   * @param submissionDTO the submission DTO
   * @param companyDTO the company DTO
   * @return the list
   */
  List<EmailTemplateTenantDTO> retrieveProjectEmailTemplates(SubmissionDTO submissionDTO, CompanyDTO companyDTO);

  /**
   * Gets the user allowed templates.
   *
   * @return the user allowed templates
   */
  List<MasterListValueHistoryDTO> getUserAllowedTemplates(); 

}
