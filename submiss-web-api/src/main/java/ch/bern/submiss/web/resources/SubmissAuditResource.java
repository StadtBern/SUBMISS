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

import ch.bern.submiss.services.api.administration.SubmissAuditService;
import ch.bern.submiss.services.api.dto.AuditLogDTO;
import ch.bern.submiss.services.api.dto.PaginationDTO;
import ch.bern.submiss.web.forms.AuditFilterForm;
import ch.bern.submiss.web.mappers.AudtLogFilterMapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Audit Resource Class.
 */
@Path("/audit")
@Singleton
public class SubmissAuditResource {

  /**
   * The submiss audit service.
   */
  @OsgiService
  @Inject
  SubmissAuditService submissAuditService;

  /**
   * Gets the audit logs.
   *
   * @param auditFilterForm the audit filter form
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @param levelIdOption the level id option
   * @return the audit logs
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/logs/{page}/{pageItems}/{sortColumn}/{sortType}/{levelIdOption}")
  public Response getAuditLogs(AuditFilterForm auditFilterForm, @PathParam("page") int page,
    @PathParam("pageItems") int pageItems, @PathParam("sortColumn") String sortColumn,
    @PathParam("sortType") String sortType, @PathParam("levelIdOption") String levelIdOption) {

    submissAuditService.auditLogSecurityCheck();

    AuditLogDTO searchDTO = AudtLogFilterMapper.INSTANCE.toAuditDTO(auditFilterForm);

    List<?> auditDTOList = submissAuditService.getAuditLogs(page, pageItems, sortColumn, sortType,
      levelIdOption, searchDTO);

    if (auditDTOList != null) {
      PaginationDTO paginationDTO = new PaginationDTO(
        submissAuditService.auditLogCount(searchDTO, levelIdOption), auditDTOList);
      return Response.ok(paginationDTO).build();
    } else {
      return Response.status(Status.NOT_FOUND).build();
    }
  }

  /**
   * Run security check before loading Verlauf.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadAuditLogs")
  public Response loadAuditLogs() {
    submissAuditService.auditLogSecurityCheck();
    return Response.ok().build();
  }
}
