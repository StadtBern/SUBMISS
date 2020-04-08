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

import ch.bern.submiss.services.api.administration.EmailService;
import ch.bern.submiss.services.api.administration.ProcedureService;
import ch.bern.submiss.services.api.administration.SDCountryService;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDDirectorateService;
import ch.bern.submiss.services.api.administration.SDLogibService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.TextType;
import ch.bern.submiss.services.api.dto.MasterListDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProcedureHistoryDTO;
import ch.bern.submiss.services.api.dto.SettingsDTO;
import ch.bern.submiss.services.api.dto.SignatureCopyDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.MasterListValueHistoryForm;
import ch.bern.submiss.web.forms.ProcedureHistoryForm;
import ch.bern.submiss.web.forms.UnregisteredUserForm;
import ch.bern.submiss.web.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.web.mappers.ProcedureHistoryMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Class SDResource.
 */
@Path("/sd")
@Singleton
public class SDResource {

  /** The Constant TEXT_VALUE_2. */
  private static final String TEXT_VALUE_2 = "textValue2";

  /** The Constant DESCRIPTION_ERROR_FIELD. */
  private static final String DESCRIPTION_ERROR_FIELD = "descriptionErrorField";

  /** The Constant INPUT_VALUE_2. */
  private static final String INPUT_VALUE_2 = "inputValue2";

  /** The Constant INPUT_VALUE_1. */
  private static final String INPUT_VALUE_1 = "inputValue1";

  /** The Constant EMPTY_MANDATORY_FIELD. */
  private static final String EMPTY_MANDATORY_FIELD = "emptyMandatoryField";

  /** The Constant FORMULA. */
  private static final String FORMULA = "formula";

  private static final String S1 = "S1";
  private static final String S2 = "S2";
  private static final String S3 = "S3";
  private static final String S4 = "S4";
  private static final String S7 = "S7";
  private static final String S10 = "S10";
  private static final String S11 = "S11";

  /** The Constant INPUT_VALUE. */
  private static final String INPUT_VALUE = "inputValue";

  /** The Constant VALUE_ERROR_FIELD. */
  private static final String VALUE_ERROR_FIELD = "valueErrorField";

  /** The Constant ARTICLE. */
  private static final String ARTICLE = "article";

  /** The s D service. */
  @OsgiService
  @Inject
  private SDService sDService;

  /** The s D department service. */
  @OsgiService
  @Inject
  private SDDepartmentService sDDepartmentService;

  /** The s D directorate service. */
  @OsgiService
  @Inject
  private SDDirectorateService sDDirectorateService;

  /** The s D country service. */
  @OsgiService
  @Inject
  private SDCountryService sDCountryService;

  /** The s D logib service. */
  @OsgiService
  @Inject
  private SDLogibService sDLogibService;

  /** The s D proof service. */
  @OsgiService
  @Inject
  private SDProofService sDProofService;

  /** The email service. */
  @OsgiService
  @Inject
  private EmailService emailService;
  
  @Inject
  private ProcedureService procedureService;

  /**
   * Gets the master list history by type.
   *
   * @param type the type
   * @return the master list history by type
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/masterList/{type}")
  public List<MasterListValueHistoryDTO> getMasterListHistoryByType(@PathParam("type") String type) {
    return sDService.getMasterListHistoryByType(type);
  }

  /**
   * Gets the master list types.
   *
   * @return the master list types
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getMasterListTypes")
  public List<MasterListDTO> getMasterListTypes() {
    return sDService.getMasterListTypes();
  }

  /**
   * Gets the master list type data.
   *
   * @param type the master list type
   * @return the master list type data
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getMasterListTypeData/{type}")
  public List<MasterListTypeDataDTO> getMasterListTypeData(@PathParam("type") String type) {
    return sDService.getMasterListTypeData(type);
  }

  /**
   * Gets master list type data entry information.
   *
   * @param <T> the generic type
   * @param entryId the master list type data entry id
   * @param type the master list type
   * @return the entry information
   */
  @SuppressWarnings("unchecked")
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getTypeDataEntryById/{entryId}/{type}")
  public <T> T getTypeDataEntryById(@PathParam("entryId") String entryId,
      @PathParam("type") String type) {
    if (type.equals(CategorySD.DEPARTMENT.getValue())) {
      return (T) sDDepartmentService.getDepartmentEntryById(entryId);
    } else if (type.equals(CategorySD.DIRECTORATE.getValue())) {
      return (T) sDDirectorateService.getDirectorateEntryById(entryId);
    } else if (type.equals(CategorySD.COUNTRY.getValue())) {
      return (T) sDCountryService.getCountryEntryById(entryId);
    } else if (type.equals(CategorySD.LOGIB.getValue())) {
      return (T) sDLogibService.getLogibEntryById(entryId);
    } else if (type.equals(CategorySD.PROOFS.getValue())) {
      return (T) sDProofService.getProofEntryById(entryId);
    } else if (type.equals(CategorySD.EMAIL_TEMPLATE.getValue())) {
      return (T) emailService.getEmailEntryById(entryId);
    } else {
      return (T) sDService.getSDEntryById(entryId);
    }
  }

  /**
   * Gets the signatures by directorate id.
   *
   * @param directorateId the directorate id
   * @return the signatures by directorate id
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getSignaturesByDirectorateId/{id}")
  public List<SignatureProcessTypeDTO> getSignaturesByDirectorateId(@PathParam("id") String directorateId) {
    return sDService.getSignaturesByDirectorateId(directorateId);
  }

  /**
   * Save SD entry.
   *
   * @param type the master list value history type
   * @param sdHistoryForm the master list value history form
   * @param isValueChanged the isValueChanged
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveSDEntry/{type}/{isValueChanged}")
  public Response saveSDEntry(@PathParam("type") String type,
    @Valid MasterListValueHistoryForm sdHistoryForm,
    @PathParam("isValueChanged") boolean isValueChanged) {
    sDService.sdSecurityCheck();
    Set<ValidationError> errors = validateSDEntry(type, sdHistoryForm, isValueChanged);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If there are no errors proceed with saving/updating the master list value history entry.
    Set<ValidationError> optimisticLockErrors = sDService.saveSDEntry(
      MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(sdHistoryForm), type);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  private Set<ValidationError> validateSDEntry(String type,
    MasterListValueHistoryForm sdHistoryForm, boolean isValueChanged) {
    Set<ValidationError> errors = new HashSet<>();
    if (type.equals(CategorySD.CANCEL_REASON.getValue())
      || type.equals(CategorySD.EXCLUSION_CRITERION.getValue())
      || type.equals(CategorySD.NEGOTIATION_REASON.getValue())) {
      errors = validateCancelReasonExclusionCriterionNegotiationReason(sdHistoryForm, type, isValueChanged);
    } else if (type.equals(CategorySD.WORKTYPE.getValue())) {
      errors = validateWorkType(sdHistoryForm, isValueChanged);
    } else if (type.equals(CategorySD.CALCULATION_FORMULA.getValue())) {
      errors = validateCalculationFormula(sdHistoryForm, isValueChanged);
    } else if (type.equals(CategorySD.ILO.getValue())
      || type.equals(CategorySD.OBJECT.getValue())
      || type.equals(CategorySD.PROCESS_PM.getValue())
      || type.equals(CategorySD.SETTLEMENT_TYPE.getValue())) {
      errors = validateILOObjectProcessPMSettlementType(sdHistoryForm, type, isValueChanged);
    } else if (type.equals(CategorySD.VAT_RATE.getValue())) {
      errors = validateVatRate(sdHistoryForm, isValueChanged);
    } else if (type.equals(CategorySD.PROCESS_TYPE.getValue())) {
      errors = validateProcessType(sdHistoryForm);
    } else if (type.equals(CategorySD.SETTINGS.getValue())) {
      errors = validateSettings(sdHistoryForm);
    }
    return errors;
  }

  /**
   * Function to validate the type cancel reason or the type exclusion criterion or the type
   * negotiation reason
   */
  private Set<ValidationError> validateCancelReasonExclusionCriterionNegotiationReason(
      MasterListValueHistoryForm sdHistoryForm, String type, boolean isValueChanged) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if mandatory fields are empty.
    if (StringUtils.isBlank(sdHistoryForm.getValue2())
        || StringUtils.isBlank(sdHistoryForm.getValue1())) {
      errors.add(
          new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(sdHistoryForm.getValue2())) {
        errors.add(new ValidationError(TEXT_VALUE_2, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(sdHistoryForm.getValue1())) {
        errors.add(new ValidationError(ARTICLE, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    // Check the description.
    if (StringUtils.isNotBlank(sdHistoryForm.getValue2())) {
      // Check the description length.
      if (sdHistoryForm.getValue2().length() > 500) {
        errors.add(new ValidationError(TEXT_VALUE_2, ValidationMessages.DESCRIPTION_500_MESSAGE));
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
            ValidationMessages.DESCRIPTION_500_MESSAGE));
      }
      // Check if the description is unique.
      boolean validationCheck = (StringUtils.isBlank(sdHistoryForm.getId()))
        // validation check for creating a sd entry
        ? !sDService.isDescriptionUnique(sdHistoryForm.getValue2(), type, sdHistoryForm.getId())
        // validation check for editing a sd entry
        : isValueChanged && !sDService.isDescriptionUnique(sdHistoryForm.getValue2(), type, sdHistoryForm.getId())
          && sdHistoryForm.getVersion() == 0;
      if (validationCheck) {
        errors.add(new ValidationError(TEXT_VALUE_2, ValidationMessages.SAME_DESCRIPTION));
        errors
            .add(new ValidationError(DESCRIPTION_ERROR_FIELD, ValidationMessages.SAME_DESCRIPTION));
      }
    }
    // Check the article length.
    if (StringUtils.isNotBlank(sdHistoryForm.getValue1())
        && sdHistoryForm.getValue1().length() > 30) {
      if (type.equals(CategorySD.EXCLUSION_CRITERION.getValue())) {
        errors.add(new ValidationError(ARTICLE, ValidationMessages.LITERA_MAX_SIZE_MESSAGE));
        errors.add(
            new ValidationError("articleErrorField", ValidationMessages.LITERA_MAX_SIZE_MESSAGE));
      } else {
        errors.add(new ValidationError(ARTICLE, ValidationMessages.ARTICLE_MAX_SIZE_MESSAGE));
        errors.add(
            new ValidationError("articleErrorField", ValidationMessages.ARTICLE_MAX_SIZE_MESSAGE));
      }
    }
    return errors;
  }

  /** Function to validate the type work type */
  private Set<ValidationError> validateWorkType(MasterListValueHistoryForm sdHistoryForm,
    boolean isValueChanged) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if mandatory fields are empty.
    if (StringUtils.isBlank(sdHistoryForm.getValue1())
        || StringUtils.isBlank(sdHistoryForm.getValue2())) {
      errors.add(
          new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(sdHistoryForm.getValue2())) {
        errors.add(new ValidationError(INPUT_VALUE_2, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(sdHistoryForm.getValue1())) {
        errors
            .add(new ValidationError("workTypeNumber", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    // Check the description.
    if (StringUtils.isNotBlank(sdHistoryForm.getValue2())) {
      // Check the description length.
      if (sdHistoryForm.getValue2().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(
            new ValidationError(INPUT_VALUE_2, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
            ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      // Check if the description is unique.
      boolean validationCheck = (StringUtils.isBlank(sdHistoryForm.getId()))
        // validation check for creating a sd entry
        ? !sDService.isDescriptionUnique(sdHistoryForm.getValue2(), CategorySD.WORKTYPE.getValue(),
        sdHistoryForm.getId())
        // validation check for editing a sd entry
        : isValueChanged && !sDService
          .isDescriptionUnique(sdHistoryForm.getValue2(), CategorySD.WORKTYPE.getValue(),
            sdHistoryForm.getId()) && sdHistoryForm.getVersion() == 0;
      if (validationCheck) {
        errors.add(new ValidationError(INPUT_VALUE_2, ValidationMessages.SAME_DESCRIPTION));
        errors
            .add(new ValidationError(DESCRIPTION_ERROR_FIELD, ValidationMessages.SAME_DESCRIPTION));
      }
    }
    // Check the work type number length.
    if (StringUtils.isNotBlank(sdHistoryForm.getValue1())
        && sdHistoryForm.getValue1().length() > TextType.SHORT_TEXT.getValue()) {
      errors.add(
          new ValidationError("workTypeNumber", ValidationMessages.WK_NUMBER_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("workTypeNumberErrorField",
          ValidationMessages.WK_NUMBER_MAX_SIZE_MESSAGE));
    }
    return errors;
  }

  /** Function to validate the calculation formula */
  private Set<ValidationError> validateCalculationFormula(
      MasterListValueHistoryForm sdHistoryForm, boolean isValueChanged) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if mandatory fields are empty.
    if (StringUtils.isBlank(sdHistoryForm.getValue1())
        || StringUtils.isBlank(sdHistoryForm.getValue2())) {
      errors.add(
          new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(sdHistoryForm.getValue1())) {
        errors.add(new ValidationError(INPUT_VALUE_1, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(sdHistoryForm.getValue2())) {
        errors.add(new ValidationError(FORMULA, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    // Check the description.
    if (StringUtils.isNotBlank(sdHistoryForm.getValue1())) {
      // Check the description length.
      if (sdHistoryForm.getValue1().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(
            new ValidationError(INPUT_VALUE_1, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
            ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      // Check if the description is unique.
      boolean validationCheck = (StringUtils.isBlank(sdHistoryForm.getId()))
        // validation check for creating a sd entry
        ? !sDService.isDescriptionUnique(sdHistoryForm.getValue1(),
        CategorySD.CALCULATION_FORMULA.getValue(), sdHistoryForm.getId())
        // validation check for editing a sd entry
        : isValueChanged && !sDService.isDescriptionUnique(sdHistoryForm.getValue1(),
          CategorySD.CALCULATION_FORMULA.getValue(), sdHistoryForm.getId())
          && sdHistoryForm.getVersion() == 0;
      if (validationCheck) {
        errors.add(new ValidationError(INPUT_VALUE_1, ValidationMessages.SAME_DESCRIPTION));
        errors
            .add(new ValidationError(DESCRIPTION_ERROR_FIELD, ValidationMessages.SAME_DESCRIPTION));
      }
    }
    // Check the formula.
    if (StringUtils.isNotBlank(sdHistoryForm.getValue2())) {
      // Check the formula length.
      if (sdHistoryForm.getValue2().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(new ValidationError(FORMULA, ValidationMessages.FORMULA_MAX_SIZE_MESSAGE));
        errors.add(
            new ValidationError("formulaErrorField", ValidationMessages.FORMULA_MAX_SIZE_MESSAGE));
      }
      // Check if the formula is valid.
      if (!sDService.isFormulaValid(sdHistoryForm.getValue2())) {
        errors.add(new ValidationError(FORMULA, ValidationMessages.FORMULA_INVALID_MESSAGE));
        errors.add(
            new ValidationError("formulaErrorField", ValidationMessages.FORMULA_INVALID_MESSAGE));
      }
    }
    return errors;
  }

  /**
   * Function to validate the type ILO or the type Object or the type ProcessPM or the type
   * SettlementType
   */
  private Set<ValidationError> validateILOObjectProcessPMSettlementType(
      MasterListValueHistoryForm sdHistoryForm, String type, boolean isValueChanged) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if mandatory field is empty.
    if (StringUtils.isBlank(sdHistoryForm.getValue1())) {
      errors.add(
          new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(INPUT_VALUE_1, ValidationMessages.MANDATORY_ERROR_MESSAGE));
    } else {
      // Check the description length.
      if (sdHistoryForm.getValue1().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(
            new ValidationError(INPUT_VALUE_1, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
            ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      // Check if the description is unique.
      boolean validationCheck = (StringUtils.isBlank(sdHistoryForm.getId()))
        // validation check for creating a sd entry
        ? !sDService.isDescriptionUnique(sdHistoryForm.getValue1(), type, sdHistoryForm.getId())
        // validation check for editing a sd entry
        : isValueChanged && !sDService
          .isDescriptionUnique(sdHistoryForm.getValue1(), type, sdHistoryForm.getId())
          && sdHistoryForm.getVersion() == 0;
      if (validationCheck) {
        errors.add(new ValidationError(INPUT_VALUE_1, ValidationMessages.SAME_DESCRIPTION));
        errors
            .add(new ValidationError(DESCRIPTION_ERROR_FIELD, ValidationMessages.SAME_DESCRIPTION));
      }
    }
    return errors;
  }

  /** Function to validate the type vat rate */
  private Set<ValidationError> validateVatRate(MasterListValueHistoryForm sdHistoryForm,
    boolean isValueChanged) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if mandatory fields are empty.
    if (StringUtils.isBlank(sdHistoryForm.getValue1())
        || StringUtils.isBlank(sdHistoryForm.getValue2())) {
      errors.add(
          new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(sdHistoryForm.getValue1())) {
        errors.add(new ValidationError(INPUT_VALUE_1, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(sdHistoryForm.getValue2())) {
        errors.add(
            new ValidationError("vatRatePercentage", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    // Check the description.
    if (StringUtils.isNotBlank(sdHistoryForm.getValue1())) {
      // Check the description length.
      if (sdHistoryForm.getValue1().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(
            new ValidationError(INPUT_VALUE_1, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
            ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      // Check if the description is unique.
      boolean validationCheck = (StringUtils.isBlank(sdHistoryForm.getId()))
        // validation check for creating a sd entry
        ? !sDService.isDescriptionUnique(sdHistoryForm.getValue1(), CategorySD.VAT_RATE.getValue(),
        sdHistoryForm.getId())
        // validation check for editing a sd entry
        : isValueChanged && !sDService
          .isDescriptionUnique(sdHistoryForm.getValue1(), CategorySD.VAT_RATE.getValue(),
            sdHistoryForm.getId()) && sdHistoryForm.getVersion() == 0;
      if (validationCheck) {
        errors.add(new ValidationError(INPUT_VALUE_1, ValidationMessages.SAME_DESCRIPTION));
        errors
            .add(new ValidationError(DESCRIPTION_ERROR_FIELD, ValidationMessages.SAME_DESCRIPTION));
      }
    }
    return errors;
  }

  /** Function to validate the type process type */
  private Set<ValidationError> validateProcessType(MasterListValueHistoryForm sdHistoryForm) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if mandatory field is empty.
    if (StringUtils.isBlank(sdHistoryForm.getValue1())) {
      errors.add(
          new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(INPUT_VALUE_1, ValidationMessages.MANDATORY_ERROR_MESSAGE));
    } else {
      if (sdHistoryForm.getValue1().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(
            new ValidationError(INPUT_VALUE_1, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError(DESCRIPTION_ERROR_FIELD,
            ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
    }
    return errors;
  }
  	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProccessDataEntryById/{entryId}")
	public ProcedureHistoryDTO getProccessDataEntryById(@PathParam("entryId") String entryId) {
		return procedureService.readProcedureById(entryId);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateProcedureValue")
	public Response updateProcedureValue(ProcedureHistoryForm form) {
    Set<ValidationError> validationErrors = validation(form);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }
    Set<ValidationError> optimisticLockErrors = procedureService
      .insertNewProcedure(ProcedureHistoryMapper.INSTANCE.toProcedureHistoryDTO(form));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Procedure validation.
   *
   * @param form the form
   * @return the errors
   */
	private Set<ValidationError> validation(ProcedureHistoryForm form) {
		Set<ValidationError> errors = new HashSet<>();
		if (form != null) {
			if (StringUtils.isBlank(form.getValue())) {
				errors.add(new ValidationError("mandatoryErrorField", "mandatory_error_message"));
				errors.add(new ValidationError("valuefield", "mandatory_error_message"));
			} else {
				try {
					Float.parseFloat(form.getValue());
				} catch (NumberFormatException e) {
					errors.add(new ValidationError("invalidNumberErrorField", "invalid_number_error_message"));
					errors.add(new ValidationError("valuefield", "invalid_number_error_message"));
				}
			}
		}
		return errors;
	}

	  @GET
	  @Consumes(MediaType.APPLICATION_JSON)
	  @Produces(MediaType.APPLICATION_JSON)
	  @Path("/getSignaturesCopiesBySignatureId/{id}")
	  public List<SignatureCopyDTO> getSignaturesCopiesBySignatureId(@PathParam("id") String signatureId) {
	    return sDService.getSignatureCopiesBySignatureId(signatureId);
	  }
	  
	  @GET
	  @Consumes(MediaType.APPLICATION_JSON)
	  @Produces(MediaType.APPLICATION_JSON)
	  @Path("/getSignatureById/{id}")
	  public SignatureProcessTypeDTO getSignatureId(@PathParam("id") String signatureId) {
	    return sDService.retrieveSignatureById(signatureId);
	  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/updateSignature/")
  public Response updateSignature(SignatureProcessTypeDTO signatureProcessTypeDTO) {
    Set<ValidationError> optimisticLockErrors =
      sDService.updateSignatureProcessEntitled(signatureProcessTypeDTO);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/updateSignatureCopies/")
  public Response updateSignatureCopies(SignatureProcessTypeDTO signatureProcessTypeDTO) {
    Set<ValidationError> optimisticLockErrors =
      sDService.updateSignatureCopies(signatureProcessTypeDTO);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /** Function to validate the type settings */
  private Set<ValidationError> validateSettings(MasterListValueHistoryForm sdHistoryForm) {
    Set<ValidationError> errors = new HashSet<>();
    if ((sdHistoryForm.getShortCode().equals(S1) || sdHistoryForm.getShortCode().equals(S2))
        && StringUtils.isNotBlank(sdHistoryForm.getValue2())
        && sdHistoryForm.getValue2().length() > TextType.SHORT_TEXT.getValue()) {
      errors.add(new ValidationError(INPUT_VALUE, ValidationMessages.INPUT_VALUE_SIZE_SHORT));
      errors.add(new ValidationError(VALUE_ERROR_FIELD, ValidationMessages.INPUT_VALUE_SIZE_SHORT));
    } else if ((sdHistoryForm.getShortCode().equals(S3) || sdHistoryForm.getShortCode().equals(S4))
        && StringUtils.isNotBlank(sdHistoryForm.getValue2())) {
      if (sdHistoryForm.getValue2().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(new ValidationError(INPUT_VALUE, ValidationMessages.INPUT_VALUE_SIZE_MIDDLE));
        errors.add(
            new ValidationError(VALUE_ERROR_FIELD, ValidationMessages.INPUT_VALUE_SIZE_MIDDLE));
      }
      // Using regex to check if a given email is valid (contains @).
      String emailRegex = "^(.+)@(.+)$";
      Pattern emailPattern = Pattern.compile(emailRegex);
      Matcher matcher = emailPattern.matcher(sdHistoryForm.getValue2());
      // Check if the given email is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError(INPUT_VALUE, ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
        errors.add(
            new ValidationError(VALUE_ERROR_FIELD, ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
      }
    } else if (sdHistoryForm.getShortCode().equals(S7)
        && StringUtils.isNotBlank(sdHistoryForm.getValue2())
        && sdHistoryForm.getValue2().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError(INPUT_VALUE, ValidationMessages.INPUT_VALUE_SIZE_MIDDLE));
      errors
          .add(new ValidationError(VALUE_ERROR_FIELD, ValidationMessages.INPUT_VALUE_SIZE_MIDDLE));
    } else if ((sdHistoryForm.getShortCode().equals(S10)
        || sdHistoryForm.getShortCode().equals(S11))
        && StringUtils.isNotBlank(sdHistoryForm.getValue2())) {
      if (sdHistoryForm.getValue2().length() > 20) {
        errors.add(new ValidationError(INPUT_VALUE, ValidationMessages.INPUT_VALUE_SIZE_TWENTY));
        errors.add(
            new ValidationError(VALUE_ERROR_FIELD, ValidationMessages.INPUT_VALUE_SIZE_TWENTY));
      }
      // Using regex. The regex code accepts a telephone/fax number. The characters can be numbers,
      // parentheses(), hyphens, periods & may contain the plus sign (+) in the beginning and can
      // contain whitespaces in between.
      String phoneRegex = "^\\+?[0-9. ()-]{0,50}$";
      Pattern phonePattern = Pattern.compile(phoneRegex);
      Matcher matcher = phonePattern.matcher(sdHistoryForm.getValue2());
      // Check if the given telephone number is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError(INPUT_VALUE, ValidationMessages.TELEPHONE_INVALID_MESSAGE));
        errors.add(
            new ValidationError(VALUE_ERROR_FIELD, ValidationMessages.TELEPHONE_INVALID_MESSAGE));
      }
    }
    return errors;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/uploadSelectedImage/{uploadedImageId}")
  public Response uploadImage(@PathParam("uploadedImageId") String uploadedImageId) {
    byte[] image = sDService.uploadImage(uploadedImageId);

    if (image.length == 0) {
      // In case of an empty byte array, return a bad request.
      Set<ValidationError> errors = new HashSet<>();
      errors.add(
          new ValidationError("invalidImageErrorField", ValidationMessages.INVALID_IMAGE_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    } else {
      // In any other case, return the image.
      List<String> tenantOrDepartmentLogo = new ArrayList<>();
      tenantOrDepartmentLogo.add(new String(Base64.encodeBase64(image), StandardCharsets.UTF_8));
      return Response.ok(tenantOrDepartmentLogo).build();
    }
  }

  @PUT
  @Path("/settings")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public SettingsDTO getUserSettings(@Valid UnregisteredUserForm unregisteredUserForm) {
    return sDService.getUserSettings(unregisteredUserForm.getTenantId(),
        unregisteredUserForm.getGroupName());
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getMasterListValueHistoryDataBySubmission/{id}/{type}")
  public List<MasterListValueHistoryDTO> getMasterListValueHistoryDataBySubmission(@PathParam("id") String submissionId,
	      @PathParam("type") String category) {
    return sDService.getMasterListValueHistoryDataBySubmission(submissionId,category);
  }

  /**
   * Gets the tenant logo.
   *
   * @return the tenant logo
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getTenantLogo")
  public Response getTenantLogo() {
    List<byte[]> tenantLogo = new ArrayList<>();
    tenantLogo.add(sDService.getTenantLogo());
    return Response.ok(tenantLogo).build();
  }

  /**
   * Gets the name of the master list type.
   *
   * @param type the master list type
   * @return the name
   */
  @GET
  @Path("/getNameOfMasterListType/{type}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getNameOfMasterListType(@PathParam("type") String type) {
    List<String> result = new ArrayList<>();
    result.add(sDService.getNameOfMasterListType(type));
    return Response.ok(result).build();
  }

  /**
   * Gets the current Master List Value History Entries by type for the given submission.
   *
   * @param submissionId the submission id
   * @param typeName the master list type name
   * @return the current Master List Value History Entries by type
   */
  @GET
  @Path("/getCurrentMLVHEntriesForSubmission/{submissionId}/{typeName}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCurrentMLVHEntriesForSubmission(@PathParam("submissionId") String submissionId,
      @PathParam("typeName") String typeName) {
    return Response.ok(sDService.getCurrentMLVHEntriesForSubmission(submissionId, typeName)).build();
  }

  /**
   * Run security check before loading Stammdaten.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadSD")
  public Response loadSD() {
    sDService.sdSecurityCheck();
    return Response.ok().build();
  }
}
