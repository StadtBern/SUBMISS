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
import ch.bern.submiss.services.api.administration.RuleService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyOfferDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CompanySearchDTO;
import ch.bern.submiss.services.api.dto.PaginationDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.api.util.View;
import ch.bern.submiss.web.forms.CompanyForm;
import ch.bern.submiss.web.forms.CompanyProofForm;
import ch.bern.submiss.web.forms.CompanySearchForm;
import ch.bern.submiss.web.mappers.CompanyMapper;
import ch.bern.submiss.web.mappers.CompanyProofFormMapper;
import ch.bern.submiss.web.mappers.CompanySearchMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.fasterxml.jackson.annotation.JsonView;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for Company.
 */
@Path("/company")
@Singleton
public class CompanyResource {

  /**
   * Constants for error messages
   */
  private static final String COMPANY_EMAIL = "companyEmail";
  private static final String COMPANY_TEL = "companyTel";
  private static final String COMPANY_ADMIN_NOTES_ERROR_FIELD = "companyAdminNotesErrorField";
  private static final String COMPANY_ADMIN_NOTES = "companyAdminNotes";
  private static final String COMPANY_NOTES_ERROR_FIELD = "companyNotesErrorField";
  private static final String COMPANY_NOTES = "companyNotes";
  private static final String COMPANY_NAME = "companyName";
  private static final String COMPANY_ADDRESS1 = "companyAddress1";
  private static final String COMPANY_POST_CODE = "companyPostCode";
  private static final String COMPANY_LOCATION = "companyLocation";
  private static final String INFO_ORIGIN = "infoOrigin";
  private static final String PHONE_REGEX = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$";

  @Inject
  protected UserAdministrationService userAdministrationService;
  @OsgiService
  @Inject
  private CompanyService companyService;
  @OsgiService
  @Inject
  private RuleService ruleService;

  /**
   * Get a company based on it's UUID.
   *
   * @param id the company UUID
   * @return the company's details
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Response getCompanyById(@PathParam("id") String id) {
    CompanyDTO company = companyService.getCompanyById(id);
    return (company != null) ? Response.ok(company).build()
      : Response.status(Status.NOT_FOUND).build();
  }

  /**
   * Retrieve sorted all companies that satisfy fields from searchForm.
   *
   * @return The list of companies
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/search/{page}/{pageItems}/{sortColumn}/{sortType}")
  @JsonView(View.Public.class)
  public Response search(CompanySearchForm searchForm, @PathParam("page") int page,
    @PathParam("pageItems") int pageItems, @PathParam("sortColumn") String sortColumn,
    @PathParam("sortType") String sortType) {
    CompanySearchDTO searchDTO = CompanySearchMapper.INSTANCE.toCompanySearchDTO(searchForm);

    List<CompanyDTO> companyDTOList =
      companyService.search(searchDTO, page, pageItems, sortColumn, sortType);

    PaginationDTO paginationDTO =
      new PaginationDTO(companyService.companyCount(searchDTO), companyDTOList);

    return Response.ok(paginationDTO).build();
  }

  /**
   * Deletes a company.
   *
   * @param id the UUID of the project to be deleted
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Response deleteCompany(@PathParam("id") String id) {
    companyService.companyDeleteSecurityCheck();
    Set<ValidationError> errors = validationCompanyParticipate(id);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }

    Set<ValidationError> validationErrors = validationDate(id);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }

    companyService.deleteCompany(id);
    return Response.ok().build();
  }

  /**
   * Searches for company entries that contain a word that starts with the query parameter in the
   * given column.
   *
   * @param column the column to search in
   * @param query the query to search for
   * @param archived the archived value
   * @return a distinct list of the column values
   */
  @POST
  @Path("/fullTextSearch/{column}/{query}/{archived}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> fullTextSearch(@PathParam("column") String column,
    @PathParam("query") String query, @PathParam("archived") boolean archived) {
    return companyService.fullTextSearch(column, query, archived);
  }

  /**
   * @param company the company to be created
   * @return the UUID of the created company
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(@Valid CompanyForm company) {

    companyService.companyCreateSecurityCheck();

    Set<ValidationError> errors = validation(company);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }

    String id = companyService.createCompany(CompanyMapper.INSTANCE.toCompanyDTO(company));
    CompanyForm form = new CompanyForm();
    form.setId(id);
    return Response.ok(form).build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/update/{archivedPrevious}")
  public Response update(@Valid CompanyForm company,
    @PathParam("archivedPrevious") Boolean archivedPrevious) {
    companyService.companyUpdateSecurityCheck();
    Set<ValidationError> errors = validation(company);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    try {
      ValidationError isValid = companyService
        .updateCompany(CompanyMapper.INSTANCE.toCompanyDTO(company), archivedPrevious);
      if (isValid != null) {
        errors.add(isValid);
        return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
      } else {
        return Response.ok().build();
      }
    } catch (OptimisticLockException exception) {
      errors.add(new ValidationError("optimisticLockErrorField",
        ValidationMessages.OPTIMISTIC_LOCK));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
  }

  /**
   * Find a offer based on it's submittent through CompanyId.
   *
   * @param id the company UUID
   * @return <ul>
   * <li>200 (OK) - If the company exists, and the company as data.</li>
   * <li>404 (NotFound) - If the company does not exist.</li>
   * </ul>
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("offers/{id}")
  public Response getOfferByCompanyId(@PathParam("id") String id) {
    List<CompanyOfferDTO> offer = companyService.getOfferByCompanyId(id, false);
    return (offer != null) ? Response.ok(offer).build() : Response.status(Status.NOT_FOUND).build();
  }

  /**
   * Find a proofs through CompanyId.
   *
   * @param id the company UUID
   * @return <ul>
   * <li>200 (OK) - If the company exists, and the company as data.</li>
   * <li>404 (NotFound) - If the company does not exist.</li>
   * </ul>
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("proofs/{id}")
  public Response getProofByCompanyId(@PathParam("id") String id) {
    List<CompanyProofDTO> proof = companyService.getProofByCompanyId(id);
    return (proof != null) ? Response.ok(proof).build() : Response.status(Status.NOT_FOUND).build();
  }

  /**
   * Update Company's Proofs
   *
   * @return 200 (OK) - If the proofs are updated.
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/proofs/update")
  public Response update(@Valid List<CompanyProofForm> proofs) {
    companyService.companyProofsSecurityCheck();
    Set<ValidationError> errors = validationProof(proofs);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    try {
      ValidationError isValid =
        companyService.updateProofs(CompanyProofFormMapper.INSTANCE.toCompanyProofDTO(proofs));
      if (isValid != null) {
        errors.add(isValid);
        return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
      } else {
        return Response.ok().build();
      }
    } catch (OptimisticLockException exception) {
      errors.add(new ValidationError("optimisticLockErrorField",
        ValidationMessages.OPTIMISTIC_LOCK));
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(errors).build();
    }
  }

  /**
   * Calculate the expiration date of proof list.
   *
   * @param companyId the companyId
   * @return the expiration date of proof list
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/expiration/{companyId}")
  public Response calculateExpirationDate(@PathParam("companyId") String companyId) {
    Date date = companyService.calculateExpirationDate(companyId);
    return Response.ok(date).build();
  }

  /**
   * Get the company templates.
   *
   * @param companyId the companyId
   * @return the company templates
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getTemplates/{companyId}")
  public Response getCompanyTemplates(@PathParam("companyId") String companyId) {
    CompanyDTO companyDTO = companyService.getCompanyById(companyId);
    return Response.ok(ruleService.getCompanyAllowedTemplates(companyDTO)).build();
  }

  /**
   * Get the user allowed templates.
   *
   * @return the user allowed templates
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getUserAllowedTemplates")
  public Response getUserAllowedTemplates() {
    return Response.ok(ruleService.getUserAllowedTemplates()).build();
  }

  /**
   * Check if telephone is unique.
   *
   * @param companyForm the companyForm
   * @return true if telephone is unique
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/telephone")
  public Response findIfTelephoneUnique(@Valid CompanyForm companyForm) {
    return Response
      .ok(companyService.findIfTelephoneUnique(companyForm.getCompanyTel(), companyForm.getId()))
      .build();
  }

  /*---- Helper methods ----*/

  private Set<ValidationError> validationCompanyParticipate(String companyId) {
    Set<ValidationError> companyErrors = new HashSet<>();
    if (companyId != null) {
      boolean participate = companyService.findIfCompanyParticipate(companyId);
      if (participate) {
        companyErrors.add(new ValidationError("companyParticipate",
          ValidationMessages.COMPANY_PARTICIPATE_IN_OFFER_MESSAGE));
      }
    }
    return companyErrors;
  }

  private Set<ValidationError> validationDate(String companyId) {
    Set<ValidationError> companyErrors = new HashSet<>();
    if (companyId != null) {
      CompanyDTO company = companyService.getCompanyById(companyId);
      if (!company.getCanBeDeleted()) {
        companyErrors.add(new ValidationError("expiredDeletionDate",
          ValidationMessages.EXPIRED_VALIDITY_PERIOD_AFTER_CREATION));
      }
    }
    return companyErrors;
  }

  private Set<ValidationError> validationProof(List<CompanyProofForm> companyProof) {
    Set<ValidationError> errors = new HashSet<>();
    for (CompanyProofForm company : companyProof) {
      if (company.getProofDate() != null && company.getRequired()) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (company.getProofDate().after(now)) {
          errors.add(new ValidationError("companyProofDate",
            ValidationMessages.COMPANY_PROOF_DATE_INVALID_TODAY_MESSAGE));
          break;
        }
      }
    }
    return errors;
  }

  private Set<ValidationError> validation(CompanyForm company) {
    Set<ValidationError> errors = new HashSet<>();
    /* Mandatory error message */
    mandatoryValidation(errors, company);
    /* Fax checks and error messages */
    faxValidation(errors, company);
    /* Origin checks and error messages */
    originValidation(errors, company);
    /* Company Name checks and error messages */
    companyNameValidation(errors, company);
    /* Website checks and error messages */
    websiteValidation(errors, company);
    /* Vat checks and error messages */
    vatValidation(errors, company);
    /* Location checks and error messages */
    locationValidation(errors, company);
    /* Address1 checks and error messages */
    address1Validation(errors, company);
    /* Address2 checks and error messages */
    address2Validation(errors, company);
    /* Telephone checks and error messages */
    telephoneValidation(errors, company);
    /* Post Code checks and error messages */
    postCodeValidation(errors, company);
    /* Bemerkung FaBe checks and error messages */
    adminNotesValidation(errors, company);
    /* Allgemeine Bemerkungen checks and error messages */
    notesValidation(errors, company);
    /* Email checks and error messages */
    emailValidation(errors, company);
    return errors;
  }

  private void mandatoryValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isBlank(company.getCompanyName()) || StringUtils.isBlank(company.getAddress1())
      || StringUtils.isBlank(company.getPostCode()) || StringUtils.isBlank(company.getLocation())
      || company.getCountry() == null || StringUtils.isBlank(company.getCompanyTel())
      || company.getCompanyTel().equals(company.getCountry().getTelPrefix())
      || StringUtils.isBlank(company.getCompanyEmail()) || company.getWorkTypes() == null
      || company.getWorkTypes().isEmpty() || StringUtils.isBlank(company.getOriginIndication())
      || company.getModificationDate() == null) {

      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.MANDATORY_MESSAGE));

      if (StringUtils.isBlank(company.getCompanyName())) {
        errors.add(new ValidationError(COMPANY_NAME, ValidationMessages.MANDATORY_MESSAGE));
      }

      if (StringUtils.isBlank(company.getAddress1())) {
        errors.add(new ValidationError(COMPANY_ADDRESS1, ValidationMessages.MANDATORY_MESSAGE));
      }

      if (StringUtils.isBlank(company.getPostCode())) {
        errors.add(new ValidationError(COMPANY_POST_CODE, ValidationMessages.MANDATORY_MESSAGE));
      }

      if (StringUtils.isBlank(company.getCompanyTel()) || (company.getCountry() != null
        && company.getCompanyTel().equals(company.getCountry().getTelPrefix()))) {
        errors.add(new ValidationError(COMPANY_TEL, ValidationMessages.MANDATORY_MESSAGE));
      }

      if (StringUtils.isBlank(company.getCompanyEmail())) {
        errors.add(new ValidationError(COMPANY_EMAIL, ValidationMessages.MANDATORY_MESSAGE));
      }

      if (StringUtils.isBlank(company.getLocation())) {
        errors.add(new ValidationError(COMPANY_LOCATION, ValidationMessages.MANDATORY_MESSAGE));
      }

      if (company.getCountry() == null) {
        errors.add(new ValidationError("countryName", ValidationMessages.MANDATORY_MESSAGE));
      }

      if (company.getWorkTypes() == null || company.getWorkTypes().isEmpty()) {
        errors.add(new ValidationError("companyWorkTypes",
          ValidationMessages.MANDATORY_MESSAGE));
      }

      if (company.getModificationDate() == null) {
        errors.add(new ValidationError("modificationDate",
          ValidationMessages.MANDATORY_MESSAGE));
      }

      if (StringUtils.isBlank(company.getOriginIndication())) {
        errors.add(new ValidationError(INFO_ORIGIN, ValidationMessages.MANDATORY_MESSAGE));
      }
    }
  }

  private void faxValidation(Set<ValidationError> errors, CompanyForm company) {
    if (company.getCompanyFax() != null) {
      if (company.getCompanyFax().length() > 20) {
        errors.add(new ValidationError("companyFaxErrorFieldMax",
          ValidationMessages.COMPANY_FAX_LENGTH_ERROR));
        errors.add(new ValidationError("companyFax",
          ValidationMessages.COMPANY_FAX_LENGTH_ERROR));
      }

      Pattern pattern = Pattern.compile(PHONE_REGEX);
      Matcher matcher = pattern.matcher(company.getCompanyFax());

      if (!matcher.matches() && StringUtils.isNotBlank(company.getCompanyFax())) {
        errors.add(new ValidationError("companyFaxErrorFieldInvalid",
          ValidationMessages.COMPANY_FAX_NUMBER_ERROR));
        errors.add(new ValidationError("companyFax",
          ValidationMessages.COMPANY_FAX_NUMBER_ERROR));
      }
    }
  }

  private void originValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getOriginIndication())
      && company.getOriginIndication().length() > 50) {
      errors.add(new ValidationError("companyOriginIndicationErrorFieldInvalid",
        ValidationMessages.COMPANY_ORIGIN_MAX_NUMBER_ERROR_MESSAGE));
      errors.add(new ValidationError(INFO_ORIGIN,
        ValidationMessages.COMPANY_ORIGIN_MAX_NUMBER_ERROR_MESSAGE));
    }
  }

  private void companyNameValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getCompanyName())
      && company.getCompanyName().length() > 100) {
      errors.add(new ValidationError("companyNameErrorFieldInvalid",
        ValidationMessages.COMPANY_NAME_MAX_SIZE_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_NAME,
        ValidationMessages.COMPANY_NAME_MAX_SIZE_ERROR_MESSAGE));
    }
  }

  private void websiteValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getCompanyWeb()) && company.getCompanyWeb().length() > 100) {
      errors.add(new ValidationError("companyWebsiteErrorFieldInvalid",
        ValidationMessages.WEBSITE_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("companyWeb",
        ValidationMessages.WEBSITE_MAX_SIZE_MESSAGE));
    }
  }

  private void vatValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getNewVatId()) && company.getNewVatId().length() > 100) {
      errors.add(new ValidationError("companyNewVatErrorFieldInvalid",
        ValidationMessages.COMPANY_VAT_MAX_SIZE_ERROR_MESSAGE));
      errors.add(new ValidationError("newVatId",
        ValidationMessages.COMPANY_VAT_MAX_SIZE_ERROR_MESSAGE));
    }
  }

  private void locationValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getLocation()) && company.getLocation().length() > 50) {
      errors.add(new ValidationError("companyLocationErrorFieldInvalid",
        ValidationMessages.COMPANY_LOCATION_MAX_SIZE_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_LOCATION,
        ValidationMessages.COMPANY_LOCATION_MAX_SIZE_ERROR_MESSAGE));
    }
  }

  private void address1Validation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getAddress1()) && company.getAddress1().length() > 100) {
      errors.add(new ValidationError("companyAddress1ErrorFieldInvalid",
        ValidationMessages.COMPANY_ADDRESS1_MAX_SIZE_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_ADDRESS1,
        ValidationMessages.COMPANY_ADDRESS1_MAX_SIZE_ERROR_MESSAGE));
    }
  }

  private void address2Validation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getAddress2()) && company.getAddress2().length() > 100) {
      errors.add(new ValidationError("companyAddress2ErrorFieldInvalid",
        ValidationMessages.COMPANY_ADDRESS2_MAX_SIZE_ERROR_MESSAGE));
      errors.add(new ValidationError("address2",
        ValidationMessages.COMPANY_ADDRESS2_MAX_SIZE_ERROR_MESSAGE));
    }
  }

  private void telephoneValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getCompanyTel())) {
      if (company.getCompanyTel().length() > 20) {
        errors.add(new ValidationError("companyTelErrorFieldMax",
          ValidationMessages.TELEPHONE_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError(COMPANY_TEL,
          ValidationMessages.TELEPHONE_MAX_SIZE_MESSAGE));
      }

      Pattern pattern = Pattern.compile(PHONE_REGEX);
      Matcher matcher = pattern.matcher(company.getCompanyTel());

      if (!matcher.matches()) {
        errors.add(new ValidationError("companyTelErrorFieldInvalid",
          ValidationMessages.TELEPHONE_INVALID_MESSAGE));
        errors.add(new ValidationError(COMPANY_TEL,
          ValidationMessages.TELEPHONE_INVALID_MESSAGE));
      }
    }
  }

  private void postCodeValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getPostCode()) && (company.getPostCode().length() > 10)) {
      errors.add(new ValidationError("companyPostCodeErrorField",
        ValidationMessages.COMPANY_MAX_SIZE_POSTCODE_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_POST_CODE,
        ValidationMessages.COMPANY_MAX_SIZE_POSTCODE_ERROR_MESSAGE));
    }
  }

  private void adminNotesValidation(Set<ValidationError> errors, CompanyForm company) {
    TenantDTO tenant = userAdministrationService.getTenant();
    /*
     * company.getNoteAdmin() should not be empty
     * if checkbox Rücksprache mit der Fachstelle Beschaffungswesen
     * is checked
     */
    if (company.getConsultAdmin() && StringUtils.isBlank(company.getNoteAdmin())
      && !tenant.getName().equals(LookupValues.TENANT_KANTON_BERN)) {
      errors.add(new ValidationError(COMPANY_ADMIN_NOTES_ERROR_FIELD,
        ValidationMessages.COMPANY_ADMIN_NOTES_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_ADMIN_NOTES,
        ValidationMessages.COMPANY_ADMIN_NOTES_ERROR_MESSAGE));
    }

    /*
     * company.getNoteAdmin().length() should be between 10 and 1000
     * if checkbox Rücksprache mit der Fachstelle Beschaffungswesen
     * is checked
     */
    if (company.getConsultAdmin() && StringUtils.isNotBlank(company.getNoteAdmin())
      && (company.getNoteAdmin().length() > 1000 || company.getNoteAdmin().length() < 10)
      && !tenant.getName().equals(LookupValues.TENANT_KANTON_BERN)) {
      errors.add(new ValidationError(COMPANY_ADMIN_NOTES_ERROR_FIELD,
        ValidationMessages.COMPANY_MAX_SIZE_ADMIN_NOTES_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_ADMIN_NOTES,
        ValidationMessages.COMPANY_MAX_SIZE_ADMIN_NOTES_ERROR_MESSAGE));
    }

    if (!company.getConsultAdmin() && StringUtils.isNotBlank(company.getNoteAdmin())
      && company.getNoteAdmin().length() > 1000) {
      errors.add(new ValidationError(COMPANY_ADMIN_NOTES_ERROR_FIELD,
        ValidationMessages.COMPANY_ADMIN_NOTES_LENGTH_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_ADMIN_NOTES,
        ValidationMessages.COMPANY_ADMIN_NOTES_LENGTH_ERROR_MESSAGE));
    }
  }

  private void notesValidation(Set<ValidationError> errors, CompanyForm company) {
    /*
     * company.getNotes() should not be empty
     * if checkbox Rücksprache mit KAIO / FaBe
     * is checked
     */
    if (company.getConsultKaio() && StringUtils.isBlank(company.getNotes())) {
      errors.add(new ValidationError(COMPANY_NOTES_ERROR_FIELD,
        ValidationMessages.COMPANY_NOTES_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_NOTES,
        ValidationMessages.COMPANY_NOTES_ERROR_MESSAGE));
    }

    /*
     * company.getNotes().length() should be between 10 and 1000
     * if checkbox Rücksprache mit KAIO / FaBe
     * is checked
     */
    if (company.getConsultKaio() && StringUtils.isNotBlank(company.getNotes())
      && (company.getNotes().length() > 1000 || company.getNotes().length() < 10)) {
      errors.add(new ValidationError(COMPANY_NOTES_ERROR_FIELD,
        ValidationMessages.COMPANY_NOTES_MAX_SIZE_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_NOTES,
        ValidationMessages.COMPANY_NOTES_MAX_SIZE_ERROR_MESSAGE));
    }

    /*
     * company.getNotes().length() should be less than 1000
     * if Allgemeine Bemerkungen is filled
     * AND checkbox Rücksprache mit KAIO / FaBe
     * is not checked
     */
    if (!company.getConsultKaio() && StringUtils.isNotBlank(company.getNotes())
      && (company.getNotes().length() > 1000)) {
      errors.add(new ValidationError(COMPANY_NOTES_ERROR_FIELD,
        ValidationMessages.COMPANY_NOTES_LENGTH_ERROR_MESSAGE));
      errors.add(new ValidationError(COMPANY_NOTES,
        ValidationMessages.COMPANY_NOTES_LENGTH_ERROR_MESSAGE));
    }
  }

  private void emailValidation(Set<ValidationError> errors, CompanyForm company) {
    if (StringUtils.isNotBlank(company.getCompanyEmail())) {
      // Check if the email contains @ to be valid
      String regex = "^(.+)@(.+)$";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(company.getCompanyEmail());
      if (!matcher.matches()) {
        errors.add(new ValidationError("emailFormatErrorField",
          ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
        errors.add(new ValidationError(COMPANY_EMAIL,
          ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
      }
      if (company.getCompanyEmail().length() > 100) {
        errors.add(new ValidationError("emailMaxSizeErrorField",
          ValidationMessages.EMAIL_MAX_SIZE_ERROR_MESSAGE));
        errors.add(new ValidationError(COMPANY_EMAIL,
          ValidationMessages.EMAIL_MAX_SIZE_ERROR_MESSAGE));
      }
    }
  }

  /**
   * Run security check before loading Company Create form.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadCompanyCreate")
  public Response loadCompanyCreate() {
    companyService.companyCreateSecurityCheck();
    return Response.ok().build();
  }

  /**
   * Run security check before loading Company Offers.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadCompanyOffers")
  public Response loadCompanyOffers() {
    companyService.companyOffersSecurityCheck();
    return Response.ok().build();
  }

  /**
   * Run security check before loading Company Proofs.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadCompanyProofs")
  public Response loadCompanyProofs() {
    companyService.companyProofsSecurityCheck();
    return Response.ok().build();
  }

  /**
   * Run security check before loading Company Update.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadCompanyUpdate")
  public Response loadCompanyUpdate() {
    companyService.companyUpdateSecurityCheck();
    return Response.ok().build();
  }

  /**
   * Run security check before loading Company Update.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadCompany")
  public Response loadCompany() {
    companyService.companyViewSecurityCheck();
    return Response.ok().build();
  }

  /**
   * Run security check before loading Company Search.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadCompanySearch")
  public Response loadCompanySearch() {
    companyService.companySearchSecurityCheck();
    return Response.ok().build();
  }
}
