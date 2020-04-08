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

import ch.bern.submiss.services.api.constants.EmailTemplate;
import ch.bern.submiss.services.api.dto.EmailDTO;
import ch.bern.submiss.services.api.dto.EmailTemplateTenantDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import java.util.List;

/**
 * The Interface EmailService.
 */
public interface EmailService {

  /**
   * Sent email.
   *
   * @param submissionId the submission id
   * @param template 
   */
  String openEMailDefaultClient(String submissionId, EmailTemplateTenantDTO template);
  
  /**
   * Check submittent list.
   *
   * @param submissionId the submission id
   */
  String checkedSubmittentListEmail(String submissionId);

  /**
   * Open proof request E mail template.
   *
   * @param companyId the company id
   */
  EmailDTO openProofRequestEMailTemplate(String companyId);

  /**
   * Email template entities to master list type data.
   *
   * @return the type data list
   */
  List<MasterListTypeDataDTO> emailToTypeData();

  /**
   * Gets the email template tenant entry by id.
   *
   * @param entryId the entry id
   * @return the email template tenant entry
   */
  EmailTemplateTenantDTO getEmailEntryById(String entryId);

  /**
   * Retrieve email templates.
   *
   * @param templateCodes the template codes
   * @return the list
   */
  List<EmailTemplateTenantDTO> retrieveEmailTemplates(List<String> templateCodes);

  /**
   * Open company E mail default client.
   *
   * @param ids the ids
   * @param template the template
   * @return the string
   */
  String openCompanyEMailDefaultClient(List<String> ids, EmailTemplateTenantDTO template);

  /**
   * Open specific E mail template.
   *
   * @param submissTaskDTO the submiss task DTO
   * @return the string
   */
  String openSpecificEMailTemplate(SubmissTaskDTO submissTaskDTO);

  /**
   * Checks if the description is unique.
   *
   * @param description the description
   * @param id the email template id
   * @param availablePart the available part
   * @return true, if description is unique
   */
  boolean isDescriptionUnique(String description, String id,
      EmailTemplate.AVAILABLE_PART availablePart);

  /**
   * Saves the email entry.
   *
   * @param emailTemplateTenantDTO the email template tenant DTO
   */
  void saveEmailEntry(EmailTemplateTenantDTO emailTemplateTenantDTO);

  /**
   * Gets the non-migrated (not workflow related) active email templates.
   *
   * @param availablePart the available part
   * @return the non-migrated active email templates
   */
  List<EmailTemplateTenantDTO> getNonMigratedEmailTemplates(
      EmailTemplate.AVAILABLE_PART availablePart);
  
  String openUserEmailTemplate(Boolean acceptUser, String userId);

  /**
   * Security check for Emails.
   */
  void emailSecurityCheck();
}
