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

import ch.bern.submiss.services.api.administration.CriterionService;
import ch.bern.submiss.services.api.administration.ImportExportFileService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SubDocumentService;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.api.dto.SuitabilityDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.api.util.View;
import ch.bern.submiss.web.forms.AwardAssessForm;
import ch.bern.submiss.web.forms.AwardForm;
import ch.bern.submiss.web.forms.CriterionForm;
import ch.bern.submiss.web.forms.ExaminationForm;
import ch.bern.submiss.web.forms.OfferForm;
import ch.bern.submiss.web.forms.SubcriterionForm;
import ch.bern.submiss.web.forms.SuitabilityForm;
import ch.bern.submiss.web.mappers.AwardAssessFormMapper;
import ch.bern.submiss.web.mappers.AwardFormMapper;
import ch.bern.submiss.web.mappers.CriterionFormMapper;
import ch.bern.submiss.web.mappers.ExaminationFormMapper;
import ch.bern.submiss.web.mappers.SubcriterionFormMapper;
import ch.bern.submiss.web.mappers.SuitabilityFormMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for criterion.
 */
@Path("/examination")
@Singleton
public class CriterionResource {

  /**
   * The Constant SUB_CRITERION_ERROR_FIELD.
   */
  private static final String SUB_CRITERION_ERROR_FIELD = "subcriterionErrorField";
  /**
   * The Constant CRITERION_ERROR_FIELD.
   */
  private static final String CRITERION_ERROR_FIELD = "criterionErrorField";
  /**
   * The Constant GENERAL_ERROR_FIELD.
   */
  private static final String GENERAL_ERROR_FIELD = "generalErrorField";
  /**
   * The Constant AWARD_EVALUATION_CLOSE_ERROR_FIELD.
   */
  private static final String AWARD_EVALUATION_CLOSE_ERROR_FIELD = "awardEvaluationCloseErrorField";
  /**
   * The maximum criterion text length
   */
  private static final int MAX_CRITERION_TEXT = 50;
  /**
   * The maximum sub-criterion text length
   */
  private static final int MAX_SUBCRITERION_TEXT = 50;
  /**
   * The Constant AWARD_MAX_GRADE.
   */
  private static final String AWARD_MAX_GRADE = "awardMaxGrade";
  /**
   * The Constant AWARD_MIN_GRADE.
   */
  private static final String AWARD_MIN_GRADE = "awardMinGrade";
  /**
   * The Constant PRICE_FORMULA.
   */
  private static final String PRICE_FORMULA = "priceFormula";
  /**
   * The Constant OPERATING_COST_FORMULA.
   */
  private static final String OPERATING_COST_FORMULA = "operatingCostFormula";
  /**
   * The criterion service.
   */
  @OsgiService
  @Inject
  private CriterionService criterionService;
  /**
   * The s D service.
   */
  @OsgiService
  @Inject
  private SDService sDService;
  /**
   * The sub document service.
   */
  @OsgiService
  @Inject
  private SubDocumentService subDocumentService;
  /**
   * The ImportExportFileService service.
   */
  @OsgiService
  @Inject
  private ImportExportFileService importExportFileService;

  /**
   * Adds the criterion to submission.
   *
   * @param criterion the criterion form
   * @return the UUID of the created criterion.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/criterion/add")
  public Response addCriterionToSubmission(@Valid CriterionForm criterion) {
    // Check for changes by other users and optimistic locking errors
    Set<ValidationError> optimisticLockErrors = criterionService
      .examinationCheckForChangesByOtherUsers(criterion.getSubmission(),
        criterion.getPageRequestedOn(), criterion.getSubmissionVersion());
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }
    // Check for validation errors
    boolean criteriaWeightingLimit = false;
    if (criterion.getSubmission() != null && criterion.getWeighting() != null
      && criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
      criteriaWeightingLimit = criterionService.checkEvaluatedCriteriaWeightingLimit(
        criterion.getSubmission(), criterion.getWeighting());
    }
    if (criterion.getSubmission() != null && criterion.getWeighting() != null
      && (criterion.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE) || criterion
      .getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))) {
      criteriaWeightingLimit = criterionService
        .checkAwardCriteriaWeightingLimit(criterion.getSubmission(), criterion.getWeighting());
    }
    Set<ValidationError> errors = validation(criterion, criteriaWeightingLimit);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If no errors occurred proceed with adding the criterion
    String id = criterionService
      .addCriterionToSubmission(CriterionFormMapper.INSTANCE.toCriterionDTO(criterion));
    CriterionForm form = new CriterionForm();
    form.setId(id);
    return Response.ok(form).build();
  }

  /**
   * Deletes a criterion.
   *
   * @param id the UUID of the criterion to be deleted.
   * @param pageRequestedOn the pageRequestedOn
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/criterion/{id}/{pageRequestedOn}")
  public Response deleteCriterion(@PathParam("id") String id, @PathParam("pageRequestedOn") long pageRequestedOn) {
    Set<ValidationError> optimisticLockErrors =
      criterionService.deleteCriterion(id, new Timestamp(pageRequestedOn));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Shows criteria.
   *
   * @param submissionId the UUID of the submission.
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/submission/{submissionId}")
  public Response readCriteriaOfSubmission(@PathParam("submissionId") String submissionId) {
    List<CriterionDTO> criterionDTOs = criterionService.readCriteriaOfSubmission(submissionId);
    return Response.ok(criterionDTOs).build();
  }

  /**
   * Updates a list of criteria.
   *
   * @param examination the examination
   * @return the response
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/criterion/update")
  public Response updateCriterion(@Valid ExaminationForm examination) {
    // Check for changes by other users and optimistic locking errors
    Set<ValidationError> optimisticLockErrors = criterionService
      .examinationCheckForChangesByOtherUsers(examination.getSubmissionId(),
        examination.getPageRequestedOn(), examination.getSubmissionVersion());
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }
    // Check for validation errors
    int criterionLimit = 0;
    if (examination.getCriterion() != null) {
      criterionLimit = criterionService.checkExaminationCriterionWeightingLimit(
        CriterionFormMapper.INSTANCE.toCriterionDTO(examination.getCriterion()));
    }
    Set<ValidationError> errors = examinationValidation(examination, criterionLimit);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If no errors occurred proceed with updating the criterion
    criterionService.updateCriterion(ExaminationFormMapper.INSTANCE.toExaminationDTO(examination));
    return Response.ok().build();
  }

  /**
   * Updates a list of award criteria.
   *
   * @param award the award
   * @return the response
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/awardCriterion/update")
  public Response updateAwardCriterion(@Valid AwardForm award) {
    criterionService.awardEvaluationEditSecurityCheck(award.getSubmissionId());
    // Check for changes by other users and optimistic locking errors
    Set<ValidationError> optimisticLockErrors = criterionService
      .awardCheckForChangesByOtherUsers(award.getSubmissionId(), award.getPageRequestedOn());
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }
    // Check for validation errors
    int criterionLimit = 0;
    if (award.getCriterion() != null) {
      criterionLimit = criterionService.checkAwardCriterionWeightingLimit(
        CriterionFormMapper.INSTANCE.toCriterionDTO(award.getCriterion()));
    }
    Set<ValidationError> errors = awardValidation(award, criterionLimit);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    /*
     * if at least one offer has zero Betrag, then no calculation can be made, so exit and inform
     * for the error
     */
    boolean zeroBetrag =
      criterionService.updateAwardCriterion(AwardFormMapper.INSTANCE.toAwardDTO(award));
    if (zeroBetrag) {
      Set<ValidationError> errorsForBetrag = new HashSet<>();
      errorsForBetrag.add(new ValidationError(GENERAL_ERROR_FIELD, ValidationMessages.ZERO_BETRAG));
      return Response.status(Response.Status.BAD_REQUEST).entity(errorsForBetrag).build();
    } else {
      return Response.ok().build();
    }
  }

  /**
   * Validation.
   *
   * @param criterion the criterion
   * @param criteriaWeightingLimit the criteria weighting limit
   * @return the sets the
   */
  private Set<ValidationError> validation(CriterionForm criterion, boolean criteriaWeightingLimit) {

    Set<ValidationError> errors = new HashSet<>();
    if (criterion.getCriterionText() == null || criterion.getCriterionText().length() == 0) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.CRITERION_MANDATORY_MESSAGE));
      errors.add(new ValidationError("criterionText",
        ValidationMessages.CRITERION_MANDATORY_MESSAGE));
    } else if (criterion.getCriterionText().length() > 50) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
      errors.add(new ValidationError("criterionText",
        ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
    }
    if (criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)
      || criterion.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
      || criterion.getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
      if (criterion.getWeighting() == null) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.CRITERION_MANDATORY_MESSAGE));
        errors.add(new ValidationError(ValidationMessages.WEIGHTING,
          ValidationMessages.CRITERION_MANDATORY_MESSAGE));
      } else {
        if (!criteriaWeightingLimit) {
          if (criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
            errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
              ValidationMessages.EVALUATED_CRITERION_WEIGHTING_LIMIT));
            errors.add(new ValidationError(ValidationMessages.WEIGHTING,
              ValidationMessages.EVALUATED_CRITERION_WEIGHTING_LIMIT));
          } else {
            errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
              ValidationMessages.AWARD_CRITERION_WEIGHTING_LIMIT));
            errors.add(new ValidationError(ValidationMessages.WEIGHTING,
              ValidationMessages.AWARD_CRITERION_WEIGHTING_LIMIT));
          }

        }
        if (criterion.getWeighting().doubleValue() == LookupValues.ZERO_DOUBLE) {
          errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
            ValidationMessages.CRITERION_ZERO_ERROR_MESSAGE));
          errors.add(new ValidationError(ValidationMessages.WEIGHTING,
            ValidationMessages.CRITERION_ZERO_ERROR_MESSAGE));
        }
      }
    }
    return errors;
  }


  /**
   * Adds the subcriterion to criterion.
   *
   * @param submissionId the submissionId
   * @param submissionVersion the submissionVersion
   * @param subcriterion the subcriterion
   * @return the UUID of the created subcriterion.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{submissionId}/{submissionVersion}/subcriterion/add")
  public Response addSubcriterionToCriterion(@PathParam("submissionId") String submissionId,
    @PathParam("submissionVersion") Long submissionVersion, @Valid SubcriterionForm subcriterion) {
    // Check for changes by other users and optimistic locking errors
    Set<ValidationError> optimisticLockErrors = criterionService
      .examinationCheckForChangesByOtherUsers(submissionId, subcriterion.getPageRequestedOn(), submissionVersion);
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }
    // Check for validation errors
    boolean subcriteriaWeightingLimit = false;
    if (subcriterion.getCriterion() != null && subcriterion.getWeighting() != null) {
      subcriteriaWeightingLimit = criterionService
        .checkSubcriteriaWeightingLimit(subcriterion.getCriterion(), subcriterion.getWeighting());
    }
    Set<ValidationError> subcriterionErrors =
      subcriterionValidation(subcriterion, subcriteriaWeightingLimit);
    if (!subcriterionErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(subcriterionErrors).build();
    }
    // If no errors occurred proceed with adding the sub criterion
    String id = criterionService.addSubcriterionToCriterion(SubcriterionFormMapper.INSTANCE
      .toSubcriterionDTO(subcriterion));
    SubcriterionForm form = new SubcriterionForm();
    form.setId(id);
    return Response.ok(form).build();
  }

  /**
   * Examination validation.
   *
   * @param examination the examination
   * @param criterionLimit the criterion limit
   * @return the sets the
   */
  private Set<ValidationError> examinationValidation(ExaminationForm examination,
    int criterionLimit) {
    Set<ValidationError> errors = new HashSet<>();
    if (criterionLimit == 1) {
      errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
    }
    if (criterionLimit == 2) {
      errors.add(new ValidationError(CRITERION_ERROR_FIELD,
        ValidationMessages.EVALUATED_CRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.EVALUATED_CRITERION_WEIGHTING_LIMIT));
    }
    if (criterionLimit == 3) {
      errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(CRITERION_ERROR_FIELD,
        ValidationMessages.EVALUATED_CRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.EVALUATED_CRITERION_WEIGHTING_LIMIT));
    }
    if (examination != null) {
      // The number of passing applicants should not be less than 0 or greater than the number of total applicants.
      if (examination.getPassingApplicants() != null && (examination.getPassingApplicants() <= 0
        || examination.getPassingApplicants() > examination.getTotalApplicants())) {
        errors.add(new ValidationError(ValidationMessages.PASSING_APPLICANTS,
          ValidationMessages.PASSING_APPLICANTS_ERROR_MESSAGE));
        errors.add(new ValidationError(ValidationMessages.PASSING_APPLICANTS_ERROR_FIELD,
          ValidationMessages.PASSING_APPLICANTS_ERROR_MESSAGE));
      }
      // max grade should not be less than 0 or greater than 5.
      if (examination.getMaxGrade() != null && (examination.getMaxGrade().doubleValue() <= 0
        || examination.getMaxGrade().doubleValue() > 5)) {
        errors.add(new ValidationError(LookupValues.MAX_GRADE,
          ValidationMessages.MAXGRADE_ERROR_MESSAGE));
        errors.add(new ValidationError("maxGradeErrorField",
          ValidationMessages.MAXGRADE_ERROR_MESSAGE));
      }
      // min grade should not be less than 0 and greater than 5.
      if (examination.getMinGrade() != null && (examination.getMinGrade().doubleValue() < 0
        || examination.getMinGrade().doubleValue() > 5)) {
        errors.add(new ValidationError(LookupValues.MIN_GRADE,
          ValidationMessages.MINGRADE_ERROR_MESSAGE));
        errors.add(new ValidationError("minGradeErrorField",
          ValidationMessages.MINGRADE_ERROR_MESSAGE));
      }
      // min grade should be less than max grade (when max grade is valid).
      if (examination.getMinGrade() != null && examination.getMaxGrade() != null
        && ((examination.getMaxGrade().doubleValue() > 0
        && examination.getMaxGrade().doubleValue() <= 5)
        && (examination.getMinGrade().doubleValue() >= examination.getMaxGrade()
        .doubleValue()))) {
        errors.add(new ValidationError(LookupValues.MIN_GRADE,
          ValidationMessages.MINGRADE_ERROR_MESSAGE));
        errors.add(new ValidationError("minGradeErrorField",
          ValidationMessages.MINGRADE_ERROR_MESSAGE));
      }
      // Make min grade and max grade mandatory fields once evaluated criteria are added.
      if (examination.getMinGrade() == null || examination.getMaxGrade() == null) {
        boolean evaluatedCriterionExists = false;
        for (CriterionForm criterion : examination.getCriterion()) {
          if (criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
            evaluatedCriterionExists = true;
            break;
          }
        }
        if (evaluatedCriterionExists) {
          if (examination.getMinGrade() == null && examination.getMaxGrade() != null) {
            errors.add(new ValidationError(LookupValues.MIN_GRADE,
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
          } else if (examination.getMaxGrade() == null && examination.getMinGrade() != null) {
            errors.add(new ValidationError(LookupValues.MAX_GRADE,
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
          } else {
            errors.add(new ValidationError(LookupValues.MIN_GRADE,
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
            errors.add(new ValidationError(LookupValues.MAX_GRADE,
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
          }
          errors.add(new ValidationError(GENERAL_ERROR_FIELD,
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
      }
      // Check mandatory fields for criterion update.
      if (examination.getCriterion() != null) {
        for (CriterionForm criterion : examination.getCriterion()) {
          if (criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)
            || criterion.getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
            // Check if criterion text and weighting (where mandatory) have been given.
            if (criterion.getCriterionText() == null || criterion.getCriterionText().length() == 0
              || (criterion.getWeighting() == null
              && criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE))) {
              errors.add(new ValidationError(CRITERION_ERROR_FIELD,
                ValidationMessages.CRITERION_MANDATORY_MESSAGE));
            }
            // Check criterion text length.
            if (criterion.getCriterionText() != null
              && criterion.getCriterionText().length() > MAX_CRITERION_TEXT) {
              errors.add(new ValidationError(CRITERION_ERROR_FIELD,
                ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
            }
            // Check if criterion weighting is zero.
            if (criterion.getWeighting() != null
              && criterion.getWeighting().doubleValue() == LookupValues.ZERO_DOUBLE) {
              errors.add(new ValidationError(CRITERION_ERROR_FIELD,
                ValidationMessages.CRITERION_ZERO_ERROR_MESSAGE));
            }
            if (criterion.getSubcriterion() != null) {
              for (SubcriterionForm subcriterion : criterion.getSubcriterion()) {
                // Check if sub-criterion text and weighting have been given.
                if (subcriterion.getSubcriterionText() == null
                  || subcriterion.getSubcriterionText().length() == 0
                  || subcriterion.getWeighting() == null) {
                  errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
                    ValidationMessages.SUBCRITERION_MANDATORY_MESSAGE));
                }
                // Check sub-criterion text length.
                if (subcriterion.getSubcriterionText() != null
                  && subcriterion.getSubcriterionText().length() > MAX_SUBCRITERION_TEXT) {
                  errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
                    ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
                }
                // Check if sub-criterion weighting is zero.
                if (subcriterion.getWeighting() != null
                  && subcriterion.getWeighting().doubleValue() == LookupValues.ZERO_DOUBLE) {
                  errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
                    ValidationMessages.SUBCRITERION_ZERO_ERROR_MESSAGE));
                }
              }
            }
          }
        }
      }
    }
    return errors;
  }

  /**
   * Award validation.
   *
   * @param award the award
   * @param criterionLimit the criterion limit
   * @return the sets the
   */
  private Set<ValidationError> awardValidation(AwardForm award, int criterionLimit) {
    Set<ValidationError> errors = new HashSet<>();
    if (criterionLimit == 1) {
      errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
    }
    if (criterionLimit == 2) {
      errors.add(new ValidationError(CRITERION_ERROR_FIELD,
        ValidationMessages.AWARD_CRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.AWARD_CRITERION_WEIGHTING_LIMIT));
    }
    if (criterionLimit == 3) {
      errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(CRITERION_ERROR_FIELD,
        ValidationMessages.AWARD_CRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
      errors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.AWARD_CRITERION_WEIGHTING_LIMIT));
    }
    if (award != null) {
      if (award.getAwardMaxGrade() != null && award.getAwardMaxGrade().doubleValue() <= 0) {
        errors.add(new ValidationError(AWARD_MAX_GRADE,
          ValidationMessages.AWARD_MAXGRADE_ERROR_MESSAGE));
        errors.add(new ValidationError("awardMaxGradeErrorField",
          ValidationMessages.AWARD_MAXGRADE_ERROR_MESSAGE));
      } else if (award.getAwardMaxGrade() != null && award.getAwardMaxGrade().doubleValue() > 5) {
        errors.add(new ValidationError(AWARD_MAX_GRADE,
          ValidationMessages.AWARD_MAXGRADE_MAXIMUM_ERROR_MESSAGE));
        errors.add(new ValidationError("awardMaxGradeErrorField",
          ValidationMessages.AWARD_MAXGRADE_MAXIMUM_ERROR_MESSAGE));
      } else if (award.getAwardMaxGrade() == null) {
        errors.add(new ValidationError(AWARD_MAX_GRADE,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(GENERAL_ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (award.getAwardMinGrade() != null && award.getAwardMaxGrade() != null
        && (award.getAwardMinGrade().doubleValue() >= award.getAwardMaxGrade().doubleValue())) {
        errors.add(new ValidationError(AWARD_MIN_GRADE,
          ValidationMessages.AWARD_MINGRADE_ERROR_MESSAGE));
        errors.add(new ValidationError("awardMinGradeErrorField",
          ValidationMessages.AWARD_MINGRADE_ERROR_MESSAGE));
      } else if (award.getAwardMinGrade() != null
        && award.getAwardMinGrade().doubleValue() < -999) {
        errors.add(new ValidationError(AWARD_MIN_GRADE,
          ValidationMessages.AWARD_MINGRADE_MINIMUM_ERROR_MESSAGE));
        errors.add(new ValidationError("awardMinGradeErrorField",
          ValidationMessages.AWARD_MINGRADE_MINIMUM_ERROR_MESSAGE));
      } else if (award.getAwardMinGrade() == null) {
        errors.add(new ValidationError(AWARD_MIN_GRADE,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(GENERAL_ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (award.getEvaluationThrough() != null && award.getEvaluationThrough().length() > 500) {
        errors.add(new ValidationError("evaluationThrough",
          ValidationMessages.EVALUATION_THROUGH_ERROR_MESSAGE));
        errors.add(
          new ValidationError("evaluationThroughErrorField",
            ValidationMessages.EVALUATION_THROUGH_ERROR_MESSAGE));
      } else if (award.getEvaluationThrough() == null
        || award.getEvaluationThrough().length() == 0) {
        errors.add(new ValidationError("evaluationThrough",
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(GENERAL_ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (award.getAddedAwardRecipients() != null
        && (award.getAddedAwardRecipients().doubleValue() < 0
        || award.getAddedAwardRecipients().doubleValue() > criterionService
        .calculateActiveSubmittentsOfSubmission(award.getSubmissionId()))) {
        errors.add(new ValidationError("addedAwardRecipients",
          ValidationMessages.ADDED_AWARD_RECIPIENTS_ERROR_MESSAGE));
        errors.add(new ValidationError("addedAwardRecipientsErrorField",
          ValidationMessages.ADDED_AWARD_RECIPIENTS_ERROR_MESSAGE));
      } else if (award.getAddedAwardRecipients() == null) {
        errors.add(new ValidationError("addedAwardRecipients",
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(GENERAL_ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if ((award.getPriceFormula() == null
        || award.getPriceFormula().getMasterListValueId() == null)
        && award.getCustomPriceFormula() == null) {
        errors.add(new ValidationError(PRICE_FORMULA,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(GENERAL_ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if ((award.getOperatingCostFormula() == null
        || award.getOperatingCostFormula().getMasterListValueId() == null)
        && award.getCustomOperatingCostFormula() == null) {
        errors.add(new ValidationError(OPERATING_COST_FORMULA,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(GENERAL_ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (award.getCriterion() != null) {
        for (CriterionForm criterion : award.getCriterion()) {
          if (criterion.getCriterionText() == null || criterion.getCriterionText().length() == 0
            || (criterion.getWeighting() == null
            && (criterion.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
            || criterion.getCriterionType()
            .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)))) {
            errors.add(new ValidationError(CRITERION_ERROR_FIELD,
              ValidationMessages.CRITERION_MANDATORY_MESSAGE));
          }
          if (criterion.getCriterionText() != null
            && criterion.getCriterionText().length() > 50) {
            errors.add(new ValidationError(CRITERION_ERROR_FIELD,
              ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
          }
          if (criterion.getWeighting() != null
            && criterion.getWeighting().doubleValue() == LookupValues.ZERO_DOUBLE) {
            errors.add(new ValidationError(CRITERION_ERROR_FIELD,
              ValidationMessages.CRITERION_ZERO_ERROR_MESSAGE));
          }
          if (criterion.getSubcriterion() != null) {
            for (SubcriterionForm subcriterion : criterion.getSubcriterion()) {
              if (subcriterion.getSubcriterionText() == null
                || subcriterion.getSubcriterionText().length() == 0
                || subcriterion.getWeighting() == null) {
                errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
                  ValidationMessages.SUBCRITERION_MANDATORY_MESSAGE));
              }
              if (subcriterion.getSubcriterionText() != null
                && subcriterion.getSubcriterionText().length() > 50) {
                errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
                  ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
              }
              if (subcriterion.getWeighting() != null
                && subcriterion.getWeighting().doubleValue() == LookupValues.ZERO_DOUBLE) {
                errors.add(new ValidationError(SUB_CRITERION_ERROR_FIELD,
                  ValidationMessages.SUBCRITERION_ZERO_ERROR_MESSAGE));
              }
            }
          }
        }
      }
      errors.addAll(priceFormulaInvalid(award));
      errors.addAll(operatingCostFormulaInvalid(award));
    }
    return errors;
  }

  /**
   * Checks if the price formula produces a grade that exceeds the award maximum grade or is smaller
   * than the award minimum grade.
   *
   * @param award the award form
   * @return errors
   */
  private Set<ValidationError> priceFormulaInvalid(AwardForm award) {
    Set<ValidationError> errors = new HashSet<>();
    boolean minErrorFound = false;
    boolean maxErrorFound = false;
    if (award.getPriceFormula() != null || award.getCustomPriceFormula() != null) {
      String expression = null;
      // Get the price formula.
      if (award.getPriceFormula() != null) {
        expression = award.getPriceFormula().getValue2();
      } else {
        expression = award.getCustomPriceFormula();
      }
      BigDecimal minAmount = null;
      // Get the smallest amount of all the not excluded offers.
      for (OfferForm offer : award.getOffers()) {
        if ((offer.getAmount() != null
          && (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess()))
          && (minAmount == null || offer.getAmount().doubleValue() < minAmount.doubleValue())) {
          minAmount = offer.getAmount();
        }
      }
      // Implementing grade calculation for every price offer criterion, to check if a grade exceeds
      // the award maximum grade or is smaller than the award minimum grade.
      for (OfferForm offer : award.getOffers()) {
        // If the formula produces grades with both minimum and maximum value errors, exit the
        // loop.
        if (minErrorFound && maxErrorFound) {
          break;
        }
        if (offer.getAmount() != null
          && (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess())) {
          for (OfferCriterionDTO offerCriterion : offer.getOfferCriteria()) {
            if (offerCriterion.getCriterion().getCriterionType()
              .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
              BigDecimal grade =
                criterionService.calculateGrade(offer.getAmount(), minAmount, expression);
              if (award.getAwardMaxGrade() != null
                && grade.compareTo(award.getAwardMaxGrade()) > 0 && !maxErrorFound) {
                errors.add(new ValidationError(PRICE_FORMULA,
                  ValidationMessages.PRICE_FORMULA_MAX_GRADE_ERROR));
                errors.add(new ValidationError("priceMaxGradeErrorField",
                  ValidationMessages.PRICE_FORMULA_MAX_GRADE_ERROR));
                maxErrorFound = true;
              }
              if (award.getAwardMinGrade() != null
                && grade.compareTo(award.getAwardMinGrade()) < 0 && !minErrorFound) {
                errors.add(new ValidationError(PRICE_FORMULA,
                  ValidationMessages.PRICE_FORMULA_MIN_GRADE_ERROR));
                errors.add(new ValidationError("priceMinGradeErrorField",
                  ValidationMessages.PRICE_FORMULA_MIN_GRADE_ERROR));
                minErrorFound = true;
              }
              // If invalid grade has been found exit the loop.
              if (maxErrorFound || minErrorFound) {
                break;
              }
            }
          }
        }
      }
    }
    // Return errors (if found).
    return errors;
  }

  /**
   * Checks if the operating cost formula produces a grade that exceeds the award maximum grade or
   * is smaller than the award minimum grade.
   *
   * @param award the award form
   * @return errors
   */
  private Set<ValidationError> operatingCostFormulaInvalid(AwardForm award) {
    Set<ValidationError> errors = new HashSet<>();
    boolean minErrorFound = false;
    boolean maxErrorFound = false;
    if (award.getOperatingCostFormula() != null || award.getCustomOperatingCostFormula() != null) {
      String expression = null;
      // Get the operating cost formula.
      if (award.getOperatingCostFormula() != null) {
        expression = award.getOperatingCostFormula().getValue2();
      } else {
        expression = award.getCustomOperatingCostFormula();
      }
      BigDecimal minAmount = null;
      // Get the smallest operating costs amount of all the not excluded offers, who have operating
      // costs.
      for (OfferForm offer : award.getOffers()) {
        if ((offer.getOperatingCostsAmount() != null
          && (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess()))
          && (minAmount == null
          || offer.getOperatingCostsAmount().doubleValue() < minAmount.doubleValue())) {
          minAmount = offer.getOperatingCostsAmount();
        }
      }
      // Implementing grade calculation for every operating cost offer criterion, to check if a
      // grade exceeds the award maximum grade or is smaller than the award minimum grade.
      for (OfferForm offer : award.getOffers()) {
        // If the formula produces grades with both minimum and maximum value errors, exit the
        // loop.
        if (minErrorFound && maxErrorFound) {
          break;
        }
        if (offer.getOperatingCostsAmount() != null
          && (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess())) {
          for (OfferCriterionDTO offerCriterion : offer.getOfferCriteria()) {
            if (offerCriterion.getCriterion().getCriterionType()
              .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
              BigDecimal grade = criterionService.calculateGrade(offer.getOperatingCostsAmount(),
                minAmount, expression);
              if (award.getAwardMaxGrade() != null
                && grade.compareTo(award.getAwardMaxGrade()) > 0 && !maxErrorFound) {
                errors.add(new ValidationError(OPERATING_COST_FORMULA,
                  ValidationMessages.OP_COST_FORMULA_MAX_GRADE_ERROR));
                errors.add(new ValidationError("opCostMaxGradeErrorField",
                  ValidationMessages.OP_COST_FORMULA_MAX_GRADE_ERROR));
                maxErrorFound = true;
              }
              if (award.getAwardMinGrade() != null
                && grade.compareTo(award.getAwardMinGrade()) < 0 && !minErrorFound) {
                errors.add(new ValidationError(OPERATING_COST_FORMULA,
                  ValidationMessages.OP_COST_FORMULA_MIN_GRADE_ERROR));
                errors.add(new ValidationError("opCostMinGradeErrorField",
                  ValidationMessages.OP_COST_FORMULA_MIN_GRADE_ERROR));
                minErrorFound = true;
              }
              // If invalid grade has been found exit the loop.
              if (maxErrorFound || minErrorFound) {
                break;
              }
            }
          }
        }
      }
    }
    // Return errors (if found).
    return errors;
  }

  /**
   * Subcriterion validation.
   *
   * @param subcriterion the subcriterion
   * @param subcriteriaWeightingLimit the subcriteria weighting limit
   * @return the sets the
   */
  private Set<ValidationError> subcriterionValidation(SubcriterionForm subcriterion,
    boolean subcriteriaWeightingLimit) {
    Set<ValidationError> subcriterionErrors = new HashSet<>();
    if (subcriterion.getSubcriterionText() == null
      || subcriterion.getSubcriterionText().length() == 0) {
      subcriterionErrors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.SUBCRITERION_MANDATORY_MESSAGE));
      subcriterionErrors.add(new ValidationError("subcriterionText",
        ValidationMessages.SUBCRITERION_MANDATORY_MESSAGE));
    } else if (subcriterion.getSubcriterionText().length() > 50) {
      subcriterionErrors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
      subcriterionErrors.add(new ValidationError("subcriterionText",
        ValidationMessages.CRITERION_MAX_SIZE_TITLE_MESSAGE));
    }
    if (subcriterion.getWeighting() == null) {
      subcriterionErrors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.SUBCRITERION_MANDATORY_MESSAGE));
      subcriterionErrors.add(new ValidationError(ValidationMessages.WEIGHTING,
        ValidationMessages.SUBCRITERION_MANDATORY_MESSAGE));
    } else {
      if (!subcriteriaWeightingLimit) {
        subcriterionErrors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
        subcriterionErrors.add(new ValidationError(ValidationMessages.WEIGHTING,
          ValidationMessages.SUBCRITERION_WEIGHTING_LIMIT));
      }
      if (subcriterion.getWeighting().doubleValue() == LookupValues.ZERO_DOUBLE) {
        subcriterionErrors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.SUBCRITERION_ZERO_ERROR_MESSAGE));
        subcriterionErrors.add(new ValidationError(ValidationMessages.WEIGHTING,
          ValidationMessages.SUBCRITERION_ZERO_ERROR_MESSAGE));
      }
    }
    return subcriterionErrors;
  }

  /**
   * Shows subcriteria.
   *
   * @param criterionId the UUID of the criterion.
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/subcriterion/{criterionId}")
  public Response readSubcriteriaOfCriterion(@PathParam("criterionId") String criterionId) {
    List<SubcriterionDTO> subcriterionDTOs =
      criterionService.readSubcriteriaOfCriterion(criterionId);
    return Response.ok(subcriterionDTOs).build();
  }

  /**
   * Deletes a subcriterion.
   *
   * @param id the UUID of the subcriterion to be deleted.
   * @param pageRequestedOn the pageRequestedOn
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/subcriterion/{id}/{pageRequestedOn}")
  public Response deleteSubcriterion(@PathParam("id") String id, @PathParam("pageRequestedOn") long pageRequestedOn) {
    Set<ValidationError> optimisticLockErrors =
      criterionService.deleteSubcriterion(id, new Timestamp(pageRequestedOn));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Checks if operating cost award criterion can be added.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/submission/operatingCostAwardCriterion/{submissionId}")
  public Response canOperatingCostAwardCriterionBeAdded(
    @PathParam("submissionId") String submissionId) {
    boolean answer = criterionService.canOperatingCostAwardCriterionBeAdded(submissionId);
    return Response.ok(answer).build();
  }

  /**
   * Returns the calculation formulas for the award evaluation.
   *
   * @param tenantId the tenant id
   * @return Master List Value History DTOs
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/submission/calculationFormulas/{tenantId}")
  public Response getCalculationFormulas(@PathParam("tenantId") String tenantId) {
    List<MasterListValueHistoryDTO> formulas =
      sDService.masterListValueHistoryQuery(CategorySD.CALCULATION_FORMULA, tenantId);
    return Response.ok(formulas).build();
  }

  /**
   * Return list of offers with criteria.
   *
   * @param submissionId the UUID of the submission
   * @param type The type of criteria to be returned
   * @return the offer criteria
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/qualification/{submissionId}/{type}/offerCriteria")
  public Response getOfferCriteria(@PathParam("submissionId") String submissionId,
    @PathParam("type") String type) {
    List<OfferDTO> offerCriterionDTOs = criterionService.getOfferCriteria(submissionId, type);
    return Response.ok(offerCriterionDTOs).build();
  }

  /**
   * Update the list of offers with criteria.
   *
   * @param suitabilityForms the suitability forms
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/qualification/{submissionId}/{pageRequestedOn}/offerCriteria")
  public Response updateOfferCriteria(@Valid List<SuitabilityForm> suitabilityForms,
    @PathParam("submissionId") String submissionId,
    @PathParam("pageRequestedOn") long pageRequestedOn) {
    List<SuitabilityDTO> suitabilityDTOs =
      SuitabilityFormMapper.INSTANCE.toSuitabilityDTO(suitabilityForms);
    // Check for changes by other users and optimistic locking errors
    Set<ValidationError> optimisticLockErrors = criterionService
      .suitabilityCheckForChangesByOtherUsers(submissionId, new Timestamp(pageRequestedOn),
        suitabilityDTOs);
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }
    // Check for validation errors
    List<String> notesAreMandatory =
      criterionService.checkSubcriteriaWeightingLimit(suitabilityDTOs);
    Set<ValidationError> errors = validateSuitabilityForm(notesAreMandatory, suitabilityDTOs);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If no errors occurred proceed with updating the criteria
    optimisticLockErrors = criterionService.updateOfferCriteria(suitabilityDTOs);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Update the list of offer criteria for the award process.
   *
   * @param awardAssessForms the award assess forms
   * @param submissionId the submissionId
   * @param pageRequestedOn the pageRequestedOn
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/awardAssess/{submissionId}/{pageRequestedOn}/update")
  public Response updateOfferCriteriaAward(@Valid List<AwardAssessForm> awardAssessForms,
    @PathParam("submissionId") String submissionId, @PathParam("pageRequestedOn") long pageRequestedOn) {
    criterionService.awardEvaluationEditSecurityCheck(submissionId);
    // Check for changes by other users and optimistic locking errors
    Set<ValidationError> optimisticLockErrors = criterionService
      .checkForChangesByOtherUsers(submissionId, new Timestamp(pageRequestedOn));
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }
    criterionService.updateOfferCriteriaAward(
      AwardAssessFormMapper.INSTANCE.toAwardAssessDTO(awardAssessForms));
    return Response.ok().build();
  }

  /**
   * Validate suitability form.
   *
   * @param notesAreMandatory the notes are mandatory
   * @param suitabilityDTOs the suitability DT os
   * @return the sets the
   */
  private Set<ValidationError> validateSuitabilityForm(List<String> notesAreMandatory,
    List<SuitabilityDTO> suitabilityDTOs) {
    Set<ValidationError> errors = new HashSet<>();
    for (SuitabilityDTO suitabilityDTO : suitabilityDTOs) {
      for (String id : notesAreMandatory) {
        if (suitabilityDTO.getOfferId().equals(id)
          && StringUtils.isBlank(suitabilityDTO.getqExSuitabilityNotes())) {
          errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
          errors.add(new ValidationError(suitabilityDTO.getOfferId(),
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
      }
      if (StringUtils.isNotBlank(suitabilityDTO.getqExSuitabilityNotes()) && (StringUtils
        .length(suitabilityDTO.getqExSuitabilityNotes()) < LookupValues.LARGE_TEXT_MIN
        || StringUtils
        .length(suitabilityDTO.getqExSuitabilityNotes()) > LookupValues.LARGE_TEXT_MAX)) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.SUITABILITY_TEXT_ERROR_MESSAGE));
        errors.add(new ValidationError(suitabilityDTO.getOfferId(),
          ValidationMessages.SUITABILITY_TEXT_ERROR_MESSAGE));
      }
    }
    return errors;
  }


  /**
   * Award evaluation close no errors.
   *
   * @param criterionForms the criterion forms
   * @param submissionId the submission id
   * @param awardMinGrade the award minimum grade
   * @param awardMaxGrade the award maximum grade
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/awardEvaluationCloseNoErrors/{submissionId}/{awardMinGrade}/{awardMaxGrade}")
  public Response awardEvaluationCloseNoErrors(@Valid List<CriterionForm> criterionForms,
    @PathParam("submissionId") String submissionId,
    @PathParam("awardMinGrade") BigDecimal awardMinGrade,
    @PathParam("awardMaxGrade") BigDecimal awardMaxGrade) {
    boolean invalidGradeExists = false;
    boolean nullGradeExists = false;
    // Check if invalid grades exist.
    invalidGradeExists =
      criterionService.existInvalidGrade(submissionId, awardMinGrade, awardMaxGrade);
    // If there are no invalid grades, check if there are empty grade fields.
    if (!invalidGradeExists) {
      nullGradeExists = criterionService.existNullGrade(submissionId);
    }
    Set<ValidationError> errors =
      validateAwardEvaluationClose(CriterionFormMapper.INSTANCE.toCriterionDTO(criterionForms),
        invalidGradeExists, nullGradeExists, submissionId);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * Validate award evaluation close.
   *
   * @param criterionDTOs the criterion DTOs
   * @param nullGradeExists the null grade exists
   * @param submissionId the submission id
   * @return validation errors
   */
  private Set<ValidationError> validateAwardEvaluationClose(List<CriterionDTO> criterionDTOs,
    boolean invalidGradeExists, boolean nullGradeExists, String submissionId) {
    Set<ValidationError> errors = new HashSet<>();
    if (invalidGradeExists) {
      errors.add(new ValidationError(AWARD_EVALUATION_CLOSE_ERROR_FIELD,
        ValidationMessages.AWARD_CRITERION_INVALID_GRADE_ERROR_MESSAGE));
    } else if (nullGradeExists) {
      errors.add(new ValidationError(AWARD_EVALUATION_CLOSE_ERROR_FIELD,
        ValidationMessages.AWARD_CRITERION_NULL_GRADE_ERROR_MESSAGE));
    }
    for (CriterionDTO criterionDTO : criterionDTOs) {
      if (criterionDTO.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
        && !criterionDTO.getSubcriterion().isEmpty()) {
        List<SubcriterionDTO> subcriterionDTOs = criterionDTO.getSubcriterion();
        double sum = 0;
        for (SubcriterionDTO subcriterionDTO : subcriterionDTOs) {
          sum += subcriterionDTO.getWeighting();
        }
        if (sum < 100) {
          errors.add(new ValidationError(AWARD_EVALUATION_CLOSE_ERROR_FIELD,
            ValidationMessages.AWARD_SUBCRITERION_WEIGHTING_SUM_NOT_ACHIEVED_ERROR_MESSAGE));
          break;
        }
      }
    }
    // Check if the Zuschlagsbewertung document has been created.
    if (!subDocumentService.documentExists(submissionId, Template.ZUSCHLAGSBEWERTUNG)) {
      errors.add(new ValidationError("noAwardDocumentField", ValidationMessages.NO_AWARD_DOCUMENT));
    }
    return errors;
  }

  /**
   * Proof if the given calculation formula is valid and if it is valid then update the price of
   * operating costs field accordingly and perform all calculations with the new formula.
   *
   * @param calculationFormula the calculation formula
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateCustomCalculationFormula/{isPrice}/{submissionId}")
  public Response updateCustomCalculationFormula(@Valid String calculationFormula,
    @PathParam("isPrice") boolean isPrice, @PathParam("submissionId") String submissionId) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if a formula has been given.
    if (StringUtils.isBlank(calculationFormula)) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.EMPTY_FORMULA_MESSAGE));
      errors.add(new ValidationError("formula",
        ValidationMessages.EMPTY_FORMULA_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    errors =
      criterionService.updateCustomCalculationFormula(calculationFormula, isPrice, submissionId);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    } else {
      return Response.ok().build();
    }
  }


  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/export/{submissionId}/{type}")
  public Response export(@PathParam("submissionId") String submissionId,
    @PathParam("type") String type) {

    List<OfferDTO> offerCriterionDTOs = criterionService.getOfferCriteria(submissionId, type);

    if (offerCriterionDTOs != null) {
      byte[] content = importExportFileService
        .exportTableOfCriteria(submissionId, offerCriterionDTOs, type);

      ResponseBuilder response = Response.ok(content);

      if (content != null && content.length != 0) {
        String fileName = importExportFileService.generateExportedFilename(type, Boolean.FALSE);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
      }
      return response.build();
    }
    return null;
  }


  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/exporteml/{submissionId}/{type}")
  public Response exportEml(@PathParam("submissionId") String submissionId,
    @PathParam("type") String type) {

    List<OfferDTO> offerCriterionDTOs = criterionService.getOfferCriteria(submissionId, type);

    if (offerCriterionDTOs != null) {
      byte[] content = importExportFileService
        .exportTableOfCriteriaAsEml(submissionId, offerCriterionDTOs, type);

      ResponseBuilder response = Response.ok(content);

      if (content != null && content.length != 0) {
        String fileName = importExportFileService.generateExportedFilename(type, Boolean.TRUE);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
      }
      return response.build();
    }
    return null;
  }


  /**
   * Upload xlsx document.
   *
   * @param submissionId the submission id
   * @param fileId the uploaded file id
   * @param type the type
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/import/{submissionId}/{uploadedFileId}/{type}")
  public Response uploadDocument(@PathParam("submissionId") String submissionId,
    @PathParam("uploadedFileId") String fileId, @PathParam("type") String type) {
    try {
      importExportFileService.importTableOfCriteria(submissionId, fileId, type);
    } catch (IOException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
    return Response.ok(null).build();
  }

  /**
   * Upload xlsx document.
   *
   * @param submissionId the submission id
   * @param fileId the uploaded file id
   * @param type the type
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/checkforchanges/{submissionId}/{uploadedFileId}/{type}")
  public Response checkDockument(@PathParam("submissionId") String submissionId,
    @PathParam("uploadedFileId") String fileId, @PathParam("type") String type) {
    String warning = "";
    try {
      warning = importExportFileService.checkImportedDocumentForChanges(submissionId, fileId, type);
    } catch (IOException e) {
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(new ValidationError("", e.getMessage())).build();
    }
    if (warning.isEmpty()) {
      return Response.status(Response.Status.OK).entity(new ValidationError("false", warning))
        .build();
    } else {
      // Send reload message, and only there are warnings, post them. 
      return Response.status(Response.Status.OK)
        .entity(new ValidationError("true", warning.equals("changedWithNoWarnings") ? "" : warning))
        .build();
    }
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/qualification/{submissionId}/{type}/view/{all}")
  @JsonView(View.Public.class)
  public Response getExaminationSubmittentListWithCriteria(
    @PathParam("submissionId") String submissionId, @PathParam("type") String type,
    @PathParam("all") Boolean all) {
    List<OfferDTO> offerCriterionDTOs =
      criterionService.getExaminationSubmittentListWithCriteria(submissionId, type, all, false);
    return Response.ok(offerCriterionDTOs).build();
  }

  /**
   * Check if examination is already locked by another user.
   *
   * @param submissionId the submissionId
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/examinationLockedByAnotherUser/{submissionId}")
  public Response examinationLockedByAnotherUser(@PathParam("submissionId") String submissionId) {
    Set<ValidationError> optimisticLockErrors =
      criterionService.examinationLockedByAnotherUser(submissionId);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Check if award is already locked by another user.
   *
   * @param submissionId the submissionId
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/awardLockedByAnotherUser/{submissionId}")
  public Response awardLockedByAnotherUser(@PathParam("submissionId") String submissionId) {
    Set<ValidationError> optimisticLockErrors =
      criterionService.awardLockedByAnotherUser(submissionId);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Check if award form is already updated by another user.
   *
   * @param submissionId the submissionId
   * @param pageRequestedOn the pageRequestedOn
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/award/optimisticLock/{submissionId}/{pageRequestedOn}")
  public Response checkAwardOptimisticLock(@PathParam("submissionId") String submissionId,
    @PathParam("pageRequestedOn") long pageRequestedOn) {
    if (!criterionService
      .awardCheckForChangesByOtherUsers(submissionId, new Timestamp(pageRequestedOn)).isEmpty()) {
      // returns custom response with OptimisticLockExceptionMapper
      throw new OptimisticLockException();
    }
    return Response.ok().build();
  }

  /**
   * Check if examination form is already updated by another user.
   *
   * @param submissionId the submissionId
   * @param pageRequestedOn the pageRequestedOn
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/optimisticLock/{submissionId}/{pageRequestedOn}/{submissionVersion}")
  public Response checkExaminationOptimisticLock(@PathParam("submissionId") String submissionId,
    @PathParam("pageRequestedOn") long pageRequestedOn,
    @PathParam("submissionVersion") Long submissionVersion) {
    if (!criterionService
      .examinationCheckForChangesByOtherUsers(submissionId,
        new Timestamp(pageRequestedOn), submissionVersion).isEmpty()) {
      // returns custom response with OptimisticLockExceptionMapper
      throw new OptimisticLockException();
    }
    return Response.ok().build();
  }

  /**
   * Run security check before loading Zuschlagsbewertung form.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadAwardEvaluation/{submissionId}")
  public Response loadAwardEvaluation(@PathParam("submissionId") String submissionId) {
    criterionService.awardEvaluationViewSecurityCheck(submissionId);
    return Response.ok().build();
  }
}
