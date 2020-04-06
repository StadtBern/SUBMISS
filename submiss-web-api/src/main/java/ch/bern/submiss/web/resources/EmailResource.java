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

package ch.bern.submiss.web.resources;

import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.EmailService;
import ch.bern.submiss.services.api.administration.RuleService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.EmailTemplate;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.constants.TextType;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.EmailDTO;
import ch.bern.submiss.services.api.dto.EmailTemplateTenantDTO;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.api.util.View;
import ch.bern.submiss.web.forms.EmailAttributesForm;
import ch.bern.submiss.web.forms.EmailTemplateTenantForm;
import ch.bern.submiss.web.mappers.EmailTemplateTenantFormMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for submission.
 */
@Path("/email")
@Singleton
public class EmailResource {

  /**
   * The Constant EMAIL_DESCRIPTION.
   */
  private static final String EMAIL_DESCRIPTION = "emailDescription";
  /**
   * The Constant SUBJECT.
   */
  private static final String SUBJECT = "subject";
  /**
   * The Constant PROJECT_PART.
   */
  private static final String PROJECT_PART = "projectPart";
  /**
   * The Constant COMPANY_PART.
   */
  private static final String COMPANY_PART = "companyPart";
  /**
   * The Constant EMPTY_MANDATORY_FIELD.
   */
  private static final String EMPTY_MANDATORY_FIELD = "emptyMandatoryField";
  /**
   * The Constant DESCRIPTION_ERROR_FIELD.
   */
  private static final String DESCRIPTION_ERROR_FIELD = "descriptionErrorField";
  /**
   * The Constant SUBJECT_ERROR_FIELD.
   */
  private static final String SUBJECT_ERROR_FIELD = "subjectErrorField";
  /**
   * The Constant CONTENT_ERROR_FIELD.
   */
  private static final String CONTENT_ERROR_FIELD = "contentErrorField";
  /**
   * The Constant CONTENT.
   */
  private static final String CONTENT = "content";
  /**
   * The Constant TO_RECEIVER_ERROR_FIELD.
   */
  private static final String TO_RECEIVER_ERROR_FIELD = "TOReceiverErrorField";
  /**
   * The Constant BCC_RECEIVER_ERROR_FIELD.
   */
  private static final String BCC_RECEIVER_ERROR_FIELD = "BCCReceiverErrorField";
  /**
   * The Constant CC_RECEIVER_ERROR_FIELD.
   */
  private static final String CC_RECEIVER_ERROR_FIELD = "CCReceiverErrorField";
  /**
   * The Constant RECEIVER.
   */
  private static final String RECEIVER = "receiver";
  @Inject
  protected SubmissTaskService taskService;
  /**
   * The email service.
   */
  @OsgiService
  @Inject
  private EmailService emailService;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;
  @Inject
  private RuleService ruleService;
  @Inject
  private CompanyService companyService;

  /**
   * Open email default client.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/{id}")
  @JsonView(View.Public.class)
  public Response openEMailDefaultClient(@Valid EmailTemplateTenantDTO template,
    @PathParam("id") String id) {
    Set<ValidationError> errors = validation(template);
    if (!errors.isEmpty()) {
      return Response.ok(ValidationMessages.MANDATORY_ERROR_MESSAGE).build();
    }
    return Response.ok(emailService.openEMailDefaultClient(id, template)).build();
  }


  /**
   * Retrieve email templates.
   *
   * @param id the id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/retrieve/{id}")
  @JsonView(View.Public.class)
  public Response retrieveEmailTemplates(@PathParam("id") String id) {
    SubmissionDTO submissionDTO = submissionService.getSubmissionById(id);
    CompanyDTO companyDTO = companyService.getCompanyById(id);
    if (submissionDTO != null) {
      submissionDTO.setCurrentState(
        TenderStatus.fromValue(submissionService.getCurrentStatusOfSubmission(id)));
    }
    return Response.ok(EmailTemplateTenantFormMapper.INSTANCE.toEmailTemplateTenantForms(
      ruleService.retrieveProjectEmailTemplates(submissionDTO, companyDTO))).build();
  }

  /**
   * Open proof request E mail template.
   *
   * @param companyId the company id
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/proof/{companyId}")
  public Response openProofRequestEMailTemplate(@PathParam("companyId") String companyId) {
    EmailDTO email = emailService.openProofRequestEMailTemplate(companyId);
    /*if ((email.getBody() + email.getMailTo()).length() > 2000) {
      return Response.ok(ValidationMessages.MANDATORY_ERROR_MESSAGE).build();
    }*/
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, 7);
    taskService.settleTask(null, companyId, TaskTypes.PROOF_REQUEST, null, cal.getTime());
    return Response.ok(email).build();
  }

  /**
   * Open company E mail default client.
   *
   * @param template the template
   * @param ids the ids
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public Response openCompanyEMailDefaultClient(@Valid EmailTemplateTenantDTO template,
    @QueryParam("id") List<String> ids) {
    Set<ValidationError> errors = validation(template);
    if (!errors.isEmpty()) {
      return Response.ok(ValidationMessages.MANDATORY_ERROR_MESSAGE).build();
    }
    return Response.ok(emailService.openCompanyEMailDefaultClient(ids, template)).build();
  }

  /**
   * Validation.
   *
   * @param template the template
   * @return the sets the
   */
  private Set<ValidationError> validation(EmailTemplateTenantDTO template) {
    Set<ValidationError> errors = new HashSet<>();
    if (template == null) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
    }

    return errors;
  }

  /**
   * Open specific E mail template.
   *
   * @param submissTaskDTO the submiss task DTO
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/task")
  public Response openSpecificEMailTemplate(@Valid SubmissTaskDTO submissTaskDTO) {
    return Response.ok(emailService.openSpecificEMailTemplate(submissTaskDTO)).build();
  }

  /**
   * Saves the email template entry.
   *
   * @param emailTemplateForm the email template form
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveEmailTemplateEntry")
  public Response saveEmailTemplateEntry(@Valid EmailTemplateTenantForm emailTemplateTenant) {
    Set<ValidationError> errors;
    errors = validateEmailTemplateTenant(emailTemplateTenant);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    emailService.saveEmailEntry(
      EmailTemplateTenantFormMapper.INSTANCE.toEmailTemplateTenantDTO(emailTemplateTenant));
    return Response.ok().build();
  }

  /**
   * Function to validate the email template
   */
  private Set<ValidationError> validateEmailTemplateTenant(
    EmailTemplateTenantForm emailTemplateTenant) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if the mandatory fields are filled out.
    if (StringUtils.isBlank(emailTemplateTenant.getDescription())
      || StringUtils.isBlank(emailTemplateTenant.getSubject())
      || emailTemplateTenant.getAvailablePart() == null) {
      errors.add(
        new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(emailTemplateTenant.getDescription())) {
        errors.add(
          new ValidationError(EMAIL_DESCRIPTION, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(emailTemplateTenant.getSubject())) {
        errors.add(new ValidationError(SUBJECT, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (emailTemplateTenant.getAvailablePart() == null) {
        errors.add(new ValidationError(PROJECT_PART, ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(COMPANY_PART, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    // Check the description.
    if (StringUtils.isNotBlank(emailTemplateTenant.getDescription())) {
      // Check the description length.
      if (emailTemplateTenant.getDescription().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError(EMAIL_DESCRIPTION,
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      // Check if the description and availablePart combination is unique.
      if (emailTemplateTenant.getAvailablePart() != null
        && !emailService.isDescriptionUnique(emailTemplateTenant.getDescription(),
        emailTemplateTenant.getId(), emailTemplateTenant.getAvailablePart())) {
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
          ValidationMessages.UNIQUE_DESCRIPTION_AND_AVAILABLE_PART));
        errors.add(new ValidationError(EMAIL_DESCRIPTION,
          ValidationMessages.UNIQUE_DESCRIPTION_AND_AVAILABLE_PART));
        errors.add(new ValidationError(PROJECT_PART,
          ValidationMessages.UNIQUE_DESCRIPTION_AND_AVAILABLE_PART));
        errors.add(new ValidationError(COMPANY_PART,
          ValidationMessages.UNIQUE_DESCRIPTION_AND_AVAILABLE_PART));
      }
    }
    // Check the subject length.
    if (StringUtils.isNotBlank(emailTemplateTenant.getSubject())
      && emailTemplateTenant.getSubject().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(
        new ValidationError(SUBJECT_ERROR_FIELD, ValidationMessages.SUBJECT_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError(SUBJECT, ValidationMessages.SUBJECT_MAX_SIZE_MESSAGE));
    }
    // Check the content length.
    if (StringUtils.isNotBlank(emailTemplateTenant.getContent())
      && emailTemplateTenant.getContent().length() > TextType.LONG_TEXT.getValue()) {
      errors.add(
        new ValidationError(CONTENT_ERROR_FIELD, ValidationMessages.CONTENT_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError(CONTENT, ValidationMessages.CONTENT_MAX_SIZE_MESSAGE));
    }
    int index = 0;
    // Check the receiver text length.
    for (EmailAttributesForm emailAttribute : emailTemplateTenant.getAttributes()) {
      if (StringUtils.isNotBlank(emailAttribute.getReciever())
        && emailAttribute.getReciever().length() > TextType.MIDDLE_TEXT.getValue()) {
        if (emailAttribute.getSendType().equals(EmailTemplate.SEND_TYPE.TO)) {
          errors.add(
            new ValidationError(TO_RECEIVER_ERROR_FIELD,
              ValidationMessages.TO_RECEIVER_ERROR_MESSAGE));
          errors.add(
            new ValidationError(RECEIVER + index, ValidationMessages.TO_RECEIVER_ERROR_MESSAGE));
        } else if (emailAttribute.getSendType().equals(EmailTemplate.SEND_TYPE.CC)) {
          errors.add(
            new ValidationError(CC_RECEIVER_ERROR_FIELD,
              ValidationMessages.CC_RECEIVER_ERROR_MESSAGE));
          errors.add(
            new ValidationError(RECEIVER + index, ValidationMessages.CC_RECEIVER_ERROR_MESSAGE));
        } else if (emailAttribute.getSendType().equals(EmailTemplate.SEND_TYPE.BCC)) {
          errors.add(
            new ValidationError(BCC_RECEIVER_ERROR_FIELD,
              ValidationMessages.BCC_RECEIVER_ERROR_MESSAGE));
          errors.add(
            new ValidationError(RECEIVER + index, ValidationMessages.BCC_RECEIVER_ERROR_MESSAGE));
        }
      }
      index++;
    }
    return errors;
  }
}
