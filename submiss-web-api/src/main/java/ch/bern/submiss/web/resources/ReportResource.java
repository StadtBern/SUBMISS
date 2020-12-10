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

import ch.bern.submiss.services.api.administration.OperationReportService;
import ch.bern.submiss.services.api.administration.ReportService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.dto.OperationReportDTO;
import ch.bern.submiss.services.api.dto.OperationReportResultsDTO;
import ch.bern.submiss.services.api.dto.PaginationDTO;
import ch.bern.submiss.services.api.dto.ReportDTO;
import ch.bern.submiss.services.api.dto.ReportResultsDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.OperationReportForm;
import ch.bern.submiss.web.forms.ReportForm;
import ch.bern.submiss.web.mappers.OperationReportMapper;
import ch.bern.submiss.web.mappers.ReportMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for report.
 */
@Path("/report")
@Singleton
public class ReportResource {

  /**
   * The report service.
   */
  @OsgiService
  @Inject
  private ReportService reportService;

  /**
   * The report service.
   */
  @OsgiService
  @Inject
  private OperationReportService operationReportService;

  /**
   * The Sd service.
   */
  @Inject
  private SDService sDService;

  /**
   * Generate report.
   *
   * @param reportForm the report form
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/generate")
  public Response generateReport(ReportForm reportForm) {
    ReportDTO reportDTO = ReportMapper.INSTANCE.toReportDTO(reportForm);
    if (reportDTO != null) {
      ReportResultsDTO reportResults = reportService.getReportResults(reportDTO);
      byte[] content = reportService.generateReport(reportResults);

      ResponseBuilder response = Response.ok(content);
      if (content != null && content.length != 0) {
        String fileName = reportService.getReportFileName(reportResults.getTotalizationBy());
        response.header("Content-Disposition", "attachment; filename=" + fileName);
      }
      return response.build();
    }
    return null;
  }

  /**
   * Download operation report.
   *
   * @param operationReportForm the operation report form
   * @param selectedColumns the list of columns that should be shown in the export
   * @param caseFormat the document format
   * @param sumAmount the sum amount
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/operationExport/{selectedColumns}/{caseFormat}/{sumAmount}")
  public Response downloadOperationReport(OperationReportForm operationReportForm,
    @PathParam("selectedColumns") String selectedColumns,
    @PathParam("caseFormat") String caseFormat, @PathParam("sumAmount") String sumAmount) {

    OperationReportDTO operationReportDTO =
      OperationReportMapper.INSTANCE.toOperationReportDTO(operationReportForm);
    if (operationReportDTO != null) {
      OperationReportResultsDTO operationReportResultsDTO =
        operationReportService.search(operationReportDTO, 1, -1, null, null);
      operationReportResultsDTO.setStartDate(operationReportDTO.getStartDate());
      operationReportResultsDTO.setEndDate(operationReportDTO.getEndDate());

      byte[] content = operationReportService.generateReport(operationReportResultsDTO,
        selectedColumns, caseFormat, sumAmount);
      ResponseBuilder response = Response.ok(content);
      if (content != null && content.length != 0) {
        String fileName = operationReportService.getReportFileName(caseFormat);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
      }
      return response.build();
    }
    return null;
  }

  /**
   * Generate operation report.
   *
   * @param operationReportForm the operation report form
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/operationGenerate/{page}/{pageItems}/{sortColumn}/{sortType}")
  public Response generateOperationReport(OperationReportForm operationReportForm,
    @PathParam("page") int page,
    @PathParam("pageItems") int pageItems, @PathParam("sortColumn") String sortColumn,
    @PathParam("sortType") String sortType) {
    OperationReportDTO operationReportDTO =
      OperationReportMapper.INSTANCE.toOperationReportDTO(operationReportForm);
    if (operationReportDTO != null) {
      OperationReportResultsDTO reportResults =
        operationReportService.search(operationReportDTO, page, pageItems, sortColumn, sortType);
      reportResults.setStartDate(operationReportDTO.getStartDate());
      reportResults.setEndDate(operationReportDTO.getEndDate());
      PaginationDTO paginationDTO = new PaginationDTO(
        operationReportService.getNumberOfOperationReportResults(operationReportDTO),
        reportResults.getGeKoResultDTOs());
      return Response.ok(paginationDTO).build();
    }
    return null;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/operation/results")
  public Response getOperationReportCalculationResults(OperationReportForm operationReportForm) {
    OperationReportDTO operationReportDTO =
      OperationReportMapper.INSTANCE.toOperationReportDTO(operationReportForm);
    OperationReportResultsDTO reportResults =
      operationReportService.calculateReportResults(operationReportDTO);
    return Response.ok(reportResults).build();
  }

  /**
   * Validate the report.
   *
   * @param reportForm the report form
   * @return the response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/validateForm")
  public Response validateForm(ReportForm reportForm) {
    ReportDTO reportDTO = ReportMapper.INSTANCE.toReportDTO(reportForm);
    Set<ValidationError> errors = reportService.validate(reportDTO);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok(reportService.proceedToResults(reportDTO)).build();
  }

  /**
   * Validate the operation report.
   *
   * @param operationReportForm the operationReportForm
   * @return the response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/validateOperationForm")
  public Response validateForm(OperationReportForm operationReportForm) {
    OperationReportDTO operationReportDTO = OperationReportMapper.INSTANCE
      .toOperationReportDTO(operationReportForm);
    Set<ValidationError> errors = operationReportService.validate(operationReportDTO);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok(operationReportService.proceedToOperationResults(operationReportDTO))
      .build();
  }

  /**
   * Run security check before loading Report values from Stammdaten.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadReport/{type}")
  public Response loadReport(@PathParam("type") String type) {
    if (type.equals("report.generateOperationReport")) {
      operationReportService.operationReportSecurityCheck();
    } else if (type.equals("report.generateReport")) {
      reportService.reportSecurityCheck();
    }
    return Response.ok().build();
  }
}
