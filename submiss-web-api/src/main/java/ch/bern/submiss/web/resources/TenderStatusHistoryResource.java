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

import ch.bern.submiss.services.api.administration.TenderStatusHistoryService;
import ch.bern.submiss.services.api.dto.TenderStatusHistoryDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/tenderStatusHistory")
@Singleton
public class TenderStatusHistoryResource {

  /** The tender status history service. */
  @OsgiService
  @Inject
  private TenderStatusHistoryService tenderStatusHistoryService;

  /**
   * Gets the submission statuses.
   *
   * @param submissionId the submission id
   * @return the submission statuses
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getSubmissionStatuses/{submissionId}")
  public Response getSubmissionStatuses(@PathParam("submissionId") String submissionId) {
    List<TenderStatusHistoryDTO> statusDTOs =
        tenderStatusHistoryService.getSubmissionStatuses(submissionId);
    List<Integer> statuses = new ArrayList<>();
    for (TenderStatusHistoryDTO statusDTO : statusDTOs) {
      statuses.add(Integer.valueOf(statusDTO.getStatusId()));
    }
    return Response.ok(statuses).build();
  }
}
