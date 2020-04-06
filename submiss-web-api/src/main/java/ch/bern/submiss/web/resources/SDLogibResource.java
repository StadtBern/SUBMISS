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

import ch.bern.submiss.services.api.administration.SDLogibService;
import ch.bern.submiss.services.api.dto.LogibHistoryDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.LogibHistoryForm;
import ch.bern.submiss.web.mappers.LogibHistoryFormMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.HashSet;
import java.util.Set;
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
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/sd/logib")
@Singleton
public class SDLogibResource {

  /** The Constant EMPTY_MANDATORY_FIELD. */
  private static final String EMPTY_MANDATORY_FIELD = "emptyMandatoryField";

  @OsgiService
  @Inject
  private SDLogibService sDLogibService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/logib")
  public LogibHistoryDTO readLogib() {
    return sDLogibService.readLogib();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/logibArgib")
  public LogibHistoryDTO readLogibArgib() {
    return sDLogibService.readLogibArgib();
  }

  /**
   * Save logib entry.
   *
   * @param logibHistoryForm the logib history form
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveLogibEntry")
  public Response saveSDEntry(@Valid LogibHistoryForm logibHistoryForm) {
    Set<ValidationError> errors = validateLogib(logibHistoryForm);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If there are no errors, proceed with updating the logib history entry.
    sDLogibService
        .saveLogibEntry(LogibHistoryFormMapper.INSTANCE.toLogibHistoryDTO(logibHistoryForm));
    return Response.ok().build();
  }

  /** Function to validate the type logib */
  private Set<ValidationError> validateLogib(LogibHistoryForm logibHistoryForm) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if mandatory fields are empty.
    if (logibHistoryForm.getWorkerNumber() == null || logibHistoryForm.getMenNumber() == null
        || logibHistoryForm.getWomenNumber() == null) {
      errors.add(
          new ValidationError(EMPTY_MANDATORY_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (logibHistoryForm.getWorkerNumber() == null) {
        errors.add(new ValidationError("workerNumber", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (logibHistoryForm.getMenNumber() == null) {
        errors.add(new ValidationError("menNumber", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (logibHistoryForm.getWomenNumber() == null) {
        errors.add(new ValidationError("womenNumber", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    return errors;
  }

}
