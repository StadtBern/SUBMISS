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

import ch.bern.submiss.services.api.administration.SDCountryService;
import ch.bern.submiss.services.api.constants.TextType;
import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.CountryHistoryForm;
import ch.bern.submiss.web.mappers.CountryHistoryMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/sd/country")
@Singleton
public class SDCountryResource {

  private static final String COUNTRY_NAME = "countryName";

  private static final String TEL_PREFIX = "telPrefix";

  @OsgiService
  @Inject
  private SDCountryService sDCountryService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<CountryHistoryDTO> readAll() {
    return sDCountryService.readAll();
  }

  /**
   * Save country entry.
   *
   * @param countryHistoryForm the country history form
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveCountryEntry")
  public Response saveCountryEntry(@Valid CountryHistoryForm countryHistoryForm) {
    Set<ValidationError> errors;
    errors = validateCountry(countryHistoryForm);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If there are no errors, proceed with saving/updating the country history entry.
    sDCountryService
        .saveCountryEntry(CountryHistoryMapper.INSTANCE.toCountryHistoryDTO(countryHistoryForm));
    return Response.ok().build();
  }

  /** Function to validate the type country */
  private Set<ValidationError> validateCountry(CountryHistoryForm countryHistoryForm) {
    // Using regex. The regex code accepts a telephone/fax number. The characters can be numbers,
    // parentheses(), hyphens, periods & may contain the plus sign (+) in the beginning and can
    // contain whitespaces in between.
    String phoneRegex = "^\\+?[0-9. ()-]{0,50}$";
    Pattern phonePattern = Pattern.compile(phoneRegex);

    Set<ValidationError> errors = new HashSet<>();
    // Check if the mandatory fields are filled out.
    if (StringUtils.isBlank(countryHistoryForm.getCountryName())
        || StringUtils.isBlank(countryHistoryForm.getTelPrefix())) {
      errors.add(
          new ValidationError("emptyMandatoryField", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(countryHistoryForm.getCountryName())) {
        errors.add(new ValidationError(COUNTRY_NAME, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(countryHistoryForm.getTelPrefix())) {
        errors.add(new ValidationError(TEL_PREFIX, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    // Check the country name.
    if (StringUtils.isNotBlank(countryHistoryForm.getCountryName())) {
      // Check the country name length.
      if (countryHistoryForm.getCountryName().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(
            new ValidationError(COUNTRY_NAME, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("descriptionErrorField",
            ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      // Check if the country name is unique.
      if (!sDCountryService.isCountryNameUnique(countryHistoryForm.getCountryName(),
          countryHistoryForm.getId())) {
        errors.add(new ValidationError(COUNTRY_NAME, ValidationMessages.SAME_DESCRIPTION));
        errors
            .add(new ValidationError("descriptionErrorField", ValidationMessages.SAME_DESCRIPTION));
      }
    }
    // Check the country short name length.
    if (StringUtils.isNotBlank(countryHistoryForm.getCountryShortName())
        && countryHistoryForm.getCountryShortName().length() > TextType.SHORT_TEXT.getValue()) {
      errors.add(
          new ValidationError("countryShortName", ValidationMessages.ISO_CODE_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("countryShortNameErrorField",
          ValidationMessages.ISO_CODE_MAX_SIZE_MESSAGE));
    }
    // Check the telephone prefix.
    if (StringUtils.isNotBlank(countryHistoryForm.getTelPrefix())) {
      // Check the telephone prefix length.
      if (countryHistoryForm.getTelPrefix().length() > 4) {
        errors.add(new ValidationError(TEL_PREFIX, ValidationMessages.TEL_PREFIX_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("telPrefixErrorField",
            ValidationMessages.TEL_PREFIX_MAX_SIZE_MESSAGE));
      }
      Matcher matcher = phonePattern.matcher(countryHistoryForm.getTelPrefix());
      // Check if the telephone prefix is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError(TEL_PREFIX, ValidationMessages.TEL_PREFIX_INVALID_MESSAGE));
        errors.add(new ValidationError("telPrefixErrorField",
            ValidationMessages.TEL_PREFIX_INVALID_MESSAGE));
      }
    }
    return errors;
  }

}
