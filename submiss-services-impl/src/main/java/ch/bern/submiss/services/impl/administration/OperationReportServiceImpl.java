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

package ch.bern.submiss.services.impl.administration;

import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.OperationReportService;
import ch.bern.submiss.services.api.administration.SubmissionCancelService;
import ch.bern.submiss.services.api.administration.TenderStatusHistoryService;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.GeKoResultDTO;
import ch.bern.submiss.services.api.dto.MasterListValueDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.OperationReportDTO;
import ch.bern.submiss.services.api.dto.OperationReportResultsDTO;
import ch.bern.submiss.services.api.dto.ReportBaseDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.GEKO_CRITERIA;
import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.impl.mappers.CustomSubmissionMapper;
import ch.bern.submiss.services.impl.mappers.GeKoResultMapper;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmissionAwardInfoEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.google.common.collect.Table;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class OperationReportServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {OperationReportService.class})
@Singleton
public class OperationReportServiceImpl extends ReportBaseServiceImpl implements
  OperationReportService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(OperationReportServiceImpl.class.getName());

  /**
   * The Constant GEKO_HEADER_COLUMNS.
   */
  private static final String[] GEKO_HEADER_COLUMNS = {
    "Direktion", "Abteilung", "Objekt", "Projektname", "Arbeitsgattung",
    "Beschreibung zur Arbeitsgattung", "Verfahren", "GATT/WTO", "Publikation Zuschlag",
    "Publikation Absicht freihändige Vergabe", "Publikation", "Eingabetermin 1",
    "Bewerbungsöffnung",
    "Eingabetermin 2", "Offertöffnung", "Verfügungsdatum 1.Stufe", "BeKo-Sitzung",
    "Verfügungsdatum",
    "Zuschlagsempfänger", "S/O-Aufträge exkl.MWST", "E-Aufträge exkl.MWST", "F-Aufträge exkl.MWST",
    "Intern/Extern", "Bemerkungsfeld"
  };

  /**
   * The Constant CHARACTER_WIDTH.
   */
  private static final int CHARACTER_WIDTH = 256;

  /**
   * The Constant SANS_SERIF.
   */
  private static final String SANS_SERIF = "SansSerif";

  /**
   * The Constant FONT_SIZE_SMALL.
   */
  private static final int FONT_SIZE_SMALL = 11;

  /**
   * The Constant FONT_SIZE_BIG.
   */
  private static final int FONT_SIZE_BIG = 14;

  /**
   * The Constant OPEN.
   */
  private static final String OPEN = "OPEN";

  /**
   * The Constant SELECTIVE.
   */
  private static final String SELECTIVE = "SELECTIVE";

  /**
   * The Constant INVITATION.
   */
  private static final String INVITATION = "INVITATION";

  /**
   * The Constant INTERN.
   */
  private static final String INTERN = "intern";

  /**
   * The Constant EXTERN.
   */
  private static final String EXTERN = "extern";

  /**
   * The Constant GEKO_TITLE.
   */
  private static final String GEKO_TITLE = "Geschäftskontrolle";

  /**
   * The Constant MAXCOLS.
   */
  private static final String MAXCOLS = "maxCols";

  /**
   * The Constant SELECTED_COLS.
   */
  private static final String SELECTED_COLS = "selectedCols";

  /**
   * The template bean.
   */
  @Inject
  protected TemplateBean templateBean;
  /**
   * The offer service.
   */
  @Inject
  private OfferService offerService;

  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;

  /**
   * The tender status history service.
   */
  @Inject
  private TenderStatusHistoryService tenderStatusHistoryService;
  /**
   * The submission cancel service.
   */
  @Inject
  private SubmissionCancelService submissionCancelService;

  @Override
  public BooleanBuilder getWhereClause(QSubmissionEntity qSubmissionEntity,
    ReportBaseDTO reportBaseDTO, List<String> completedSubmissionsIDs) {

    LOGGER.log(Level.CONFIG,
      "Executing method getWhereClause, Parameters: qSubmissionEntity: {0}, "
        + "reportBaseDTO: {1}, completedSubmissionsIDs: {2}",
      new Object[]{qSubmissionEntity, reportBaseDTO, completedSubmissionsIDs});

    OperationReportDTO operationReportDTO = (OperationReportDTO) reportBaseDTO;
    BooleanBuilder whereClause = super.getWhereClause(qSubmissionEntity, operationReportDTO,
      getSubmissionIds(operationReportDTO));

    // gets GeKo entries
    whereClause.and(qSubmissionEntity.isGekoEntry.isTrue());

    // Date criteria
    Calendar cal = Calendar.getInstance();
    if (operationReportDTO.getEndDate() != null) {
      Date endDate = getCalDate(operationReportDTO.getEndDate());
      cal.setTime(operationReportDTO.getEndDate());
      // get SUBMISSION_CREATED oldest  status after the given date
      Predicate predicateEnd = getExcludedCreatedSubmissionAfterDatePredicate(endDate.getTime());
      whereClause.and(qSubmissionEntity.id.notIn(getNotAvailableDateIds(predicateEnd)));
    }

    if (operationReportDTO.getStartDate() != null) {
      Date startDate = getCalDate(operationReportDTO.getStartDate());
      // get AWARD_NOTICES_CREATED or PROCEDURE_CANCELED latest status before the given date
      Predicate predicateStart = getExcludedPredicate(startDate.getTime());
      whereClause.and(qSubmissionEntity.id.notIn(getNotAvailableDateIds(predicateStart)));
    }

    // if Beschreibung criteria is set, add it to where clause
    if (checkIfNotEmpty(operationReportDTO.getDescription())) {
      whereClause.and(qSubmissionEntity.description.contains(operationReportDTO.getDescription()));
    }

    // if GATT / WTO criteria is set, add it to where clause
    if (operationReportDTO.getGattWto() != null) {
      whereClause.and(qSubmissionEntity.gattTwo.eq(operationReportDTO.getGattWto()));
    }

    // --- FROM SEARCH DATES
    // if Publikation Zuschlag von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getPublicationDateAwardFrom(), whereClause, 1, Boolean.TRUE,
      Boolean.FALSE);

    // if Publikation von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getPublicationDateFrom(), whereClause, 2, Boolean.TRUE,
      Boolean.FALSE);

    // if Bewerbungsöffnung von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getApplicationOpeningDateFrom(), whereClause, 3, Boolean.TRUE,
      Boolean.FALSE);
    if (operationReportDTO.getApplicationOpeningDateFrom() != null) {
      whereClause.and(submission.status.in(TenderStatus.OFFER_OPENING_CLOSED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED.getValue(),
        TenderStatus.AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.AWARD_EVALUATION_CLOSED.getValue(),
        TenderStatus.MANUAL_AWARD_COMPLETED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
        TenderStatus.AWARD_NOTICES_CREATED.getValue(),
        TenderStatus.CONTRACT_CREATED.getValue(),
        TenderStatus.PROCEDURE_COMPLETED.getValue(),
        TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // if Offertöffnung von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getOfferOpeningDateFrom(), whereClause, 4, Boolean.TRUE,
      Boolean.FALSE);
    if (operationReportDTO.getOfferOpeningDateFrom() != null) {
      whereClause.and(submission.status.notIn(TenderStatus.SUBMISSION_CREATED.getValue(),
        TenderStatus.APPLICANTS_LIST_CREATED.getValue(),
        TenderStatus.APPLICATION_OPENING_STARTED.getValue()));
    }

    // if BeKo-Sitzung von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getCommissionProcurementProposalDateFrom(), whereClause, 5,
      Boolean.TRUE, Boolean.FALSE);
    if (operationReportDTO.getCommissionProcurementProposalDateFrom() != null) {
      whereClause
        .and(submission.status.in(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
          TenderStatus.AWARD_NOTICES_CREATED.getValue(), TenderStatus.CONTRACT_CREATED.getValue(),
          TenderStatus.PROCEDURE_COMPLETED.getValue(), TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // if Publikation Absicht freihändige Vergabe von criteria is set, add it to the
    // where clause
    addDateCheck(operationReportDTO.getPublicationDateDirectAwardFrom(), whereClause, 6,
      Boolean.TRUE, Boolean.FALSE);

    // if Eingabetermin 1 von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getFirstDeadlineFrom(), whereClause, 7, Boolean.TRUE,
      Boolean.FALSE);
    if (operationReportDTO.getFirstDeadlineFrom() != null) {
      whereClause.and(submission.status.in(TenderStatus.OFFER_OPENING_CLOSED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED.getValue(),
        TenderStatus.AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.AWARD_EVALUATION_CLOSED.getValue(),
        TenderStatus.MANUAL_AWARD_COMPLETED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
        TenderStatus.AWARD_NOTICES_CREATED.getValue(),
        TenderStatus.CONTRACT_CREATED.getValue(),
        TenderStatus.PROCEDURE_COMPLETED.getValue(),
        TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // if Eingabetermin 2 von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getSecondDeadlineFrom(), whereClause, 8, Boolean.TRUE,
      Boolean.FALSE);
    if (operationReportDTO.getSecondDeadlineFrom() != null) {
      whereClause.and(submission.status.notIn(TenderStatus.SUBMISSION_CREATED.getValue(),
        TenderStatus.APPLICANTS_LIST_CREATED.getValue(),
        TenderStatus.APPLICATION_OPENING_STARTED.getValue()));
    }

    // if Verfügungsdatum 1.Stufe von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getFirstLevelavailableDateFrom(), whereClause, 9, Boolean.TRUE,
      Boolean.TRUE);

    // if Verfügungsdatum von criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getLevelavailableDateFrom(), whereClause, 10, Boolean.TRUE,
      Boolean.TRUE);
    if (operationReportDTO.getLevelavailableDateFrom() != null) {
      whereClause.and(submission.status.in(TenderStatus.AWARD_NOTICES_CREATED.getValue(),
        TenderStatus.CONTRACT_CREATED.getValue(),
        TenderStatus.PROCEDURE_COMPLETED.getValue(),
        TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // --- TO SEARCH DATES
    // if Publikation Zuschlag bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getPublicationDateAwardTo(), whereClause, 1, Boolean.FALSE,
      Boolean.FALSE);

    // if Publikation bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getPublicationDateTo(), whereClause, 2, Boolean.FALSE,
      Boolean.FALSE);

    // if Bewerbungsöffnung bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getApplicationOpeningDateTo(), whereClause, 3, Boolean.FALSE,
      Boolean.FALSE);
    if (operationReportDTO.getApplicationOpeningDateTo() != null) {
      whereClause.and(submission.status.in(TenderStatus.OFFER_OPENING_CLOSED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED.getValue(),
        TenderStatus.AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.AWARD_EVALUATION_CLOSED.getValue(),
        TenderStatus.MANUAL_AWARD_COMPLETED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
        TenderStatus.AWARD_NOTICES_CREATED.getValue(),
        TenderStatus.CONTRACT_CREATED.getValue(),
        TenderStatus.PROCEDURE_COMPLETED.getValue(),
        TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // if Offertöffnung bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getOfferOpeningDateTo(), whereClause, 4, Boolean.FALSE,
      Boolean.FALSE);
    if (operationReportDTO.getOfferOpeningDateTo() != null) {
      whereClause.and(submission.status.notIn(TenderStatus.SUBMISSION_CREATED.getValue(),
        TenderStatus.APPLICANTS_LIST_CREATED.getValue(),
        TenderStatus.APPLICATION_OPENING_STARTED.getValue()));
    }

    // if BeKo-Sitzung bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getCommissionProcurementProposalDateTo(), whereClause, 5,
      Boolean.FALSE, Boolean.FALSE);
    if (operationReportDTO.getCommissionProcurementProposalDateTo() != null) {
      whereClause
        .and(submission.status.in(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
          TenderStatus.AWARD_NOTICES_CREATED.getValue(), TenderStatus.CONTRACT_CREATED.getValue(),
          TenderStatus.PROCEDURE_COMPLETED.getValue(), TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // if Publikation Absicht freihändige Vergabe bis criteria is set, add it to the
    // where clause
    addDateCheck(operationReportDTO.getPublicationDateDirectAwardTo(), whereClause, 6,
      Boolean.FALSE, Boolean.FALSE);

    // if Eingabetermin 1 bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getFirstDeadlineTo(), whereClause, 7, Boolean.FALSE,
      Boolean.FALSE);
    if (operationReportDTO.getFirstDeadlineTo() != null) {
      whereClause.and(submission.status.in(TenderStatus.OFFER_OPENING_CLOSED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue(),
        TenderStatus.FORMAL_EXAMINATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED.getValue(),
        TenderStatus.AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue(),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        TenderStatus.AWARD_EVALUATION_CLOSED.getValue(),
        TenderStatus.MANUAL_AWARD_COMPLETED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
        TenderStatus.AWARD_NOTICES_CREATED.getValue(),
        TenderStatus.CONTRACT_CREATED.getValue(),
        TenderStatus.PROCEDURE_COMPLETED.getValue(),
        TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // if Eingabetermin 2 bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getSecondDeadlineTo(), whereClause, 8, Boolean.FALSE,
      Boolean.FALSE);
    if (operationReportDTO.getSecondDeadlineTo() != null) {
      whereClause.and(submission.status.notIn(TenderStatus.SUBMISSION_CREATED.getValue(),
        TenderStatus.APPLICANTS_LIST_CREATED.getValue(),
        TenderStatus.APPLICATION_OPENING_STARTED.getValue()));
    }

    // if Verfügungsdatum 1.Stufe bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getFirstLevelavailableDateTo(), whereClause, 9, Boolean.FALSE,
      Boolean.TRUE);

    // if Verfügungsdatum bis criteria is set, add it to the where clause
    addDateCheck(operationReportDTO.getLevelavailableDateTo(), whereClause, 10, Boolean.FALSE,
      Boolean.TRUE);
    if (operationReportDTO.getLevelavailableDateTo() != null) {
      whereClause.and(submission.status.in(TenderStatus.AWARD_NOTICES_CREATED.getValue(),
        TenderStatus.CONTRACT_CREATED.getValue(),
        TenderStatus.PROCEDURE_COMPLETED.getValue(),
        TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    // if Intern/Extern criteria is set, add it to where clause
    if (operationReportDTO.getIntern() != null) {
      List<String> internDepartmentIds = new JPAQueryFactory(em)
        .select(qDepartmentHistoryEntity.departmentId.id).from(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.internal.in(operationReportDTO.getIntern())).fetch();
      whereClause.and(qSubmissionEntity.project.department.id.in(internDepartmentIds));
    }

    // if Zuschlagsempfänger criteria is set, add it to where clause
    if (checkIfNotEmpty(operationReportDTO.getAwardCompany())) {

      List<String> awardCompanySubmissionIds =
        new JPAQueryFactory(em).select(qOfferEntity.submittent.submissionId.id).from(qOfferEntity)
          .where(qOfferEntity.submittent.companyId.companyName
            .in(operationReportDTO.getAwardCompany()).and(qOfferEntity.isAwarded.isTrue())
            .and(qOfferEntity.submittent.submissionId.id.isNotNull()))
          .fetch();
      whereClause.and(submission.id.in(awardCompanySubmissionIds)
        .and(submission.status.in(TenderStatus.AWARD_NOTICES_CREATED.getValue(),
          TenderStatus.CONTRACT_CREATED.getValue(), TenderStatus.PROCEDURE_COMPLETED.getValue(),
          TenderStatus.PROCEDURE_CANCELED.getValue())));
    }

    // if Submissionsbemerkungen criteria is set, add it to where clause
    if (checkIfNotEmpty(operationReportDTO.getNotes())) {
      whereClause
        .and(qSubmissionEntity.notes.in(operationReportDTO.getNotes()).and(submission.status.in(
          TenderStatus.OFFER_OPENING_CLOSED.getValue(),
          TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue(),
          TenderStatus.FORMAL_EXAMINATION_STARTED.getValue(),
          TenderStatus.FORMAL_AUDIT_COMPLETED.getValue(),
          TenderStatus.AWARD_EVALUATION_STARTED.getValue(),
          TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue(),
          TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
          TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
          TenderStatus.AWARD_EVALUATION_CLOSED.getValue(),
          TenderStatus.MANUAL_AWARD_COMPLETED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
          TenderStatus.AWARD_NOTICES_CREATED.getValue(), TenderStatus.CONTRACT_CREATED.getValue(),
          TenderStatus.PROCEDURE_COMPLETED.getValue(),
          TenderStatus.PROCEDURE_CANCELED.getValue())));

    }

    // if Freihandvergabe criteria is set, add it to where clause
    addReasonFreeAwardCriteria(operationReportDTO.getReasonFreeAwards(), whereClause);
    // if Verfahrensabbruch criteria is set, add it to where clause
    addCancellationReasonCriteria(operationReportDTO.getCancelationreasons(), whereClause);
    if (checkIfNotEmpty(operationReportDTO.getCancelationreasons())) {
      whereClause.and(submission.status.in(TenderStatus.PROCEDURE_CANCELED.getValue()));
    }
    return whereClause;
  }

  /**
   * Adds the reason free award criteria to the where clause.
   *
   * @param reasonFreeAwardHistoryIds the reason free award history ids
   * @param whereClause the where clause
   */
  private void addReasonFreeAwardCriteria(List<String> reasonFreeAwardHistoryIds,
    BooleanBuilder whereClause) {

    LOGGER.log(Level.CONFIG,
      "Executing method addReasonFreeAwardCriteria, Parameters: reasonFreeAwardHistoryIds: {0}, whereClause: {1}",
      new Object[]{reasonFreeAwardHistoryIds, whereClause});

    if (checkIfNotEmpty(reasonFreeAwardHistoryIds)) {
      List<String> reasonFreeAwardIds =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.masterListValueId.id)
          .from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.id.in(reasonFreeAwardHistoryIds)).fetch();
      whereClause.and(submission.reasonFreeAward.id.in(reasonFreeAwardIds));
    }
  }

  /**
   * Adds the cancellation reason criteria to the where clause.
   *
   * @param cancellationReasonHistoryIds the cancellation reason history ids
   * @param whereClause the where clause
   */
  private void addCancellationReasonCriteria(List<String> cancellationReasonHistoryIds,
    BooleanBuilder whereClause) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCancellationReasonCriteria, Parameters: cancellationReasonHistoryIds: {0}, whereClause: {1}",
      new Object[]{cancellationReasonHistoryIds, whereClause});

    if (checkIfNotEmpty(cancellationReasonHistoryIds)) {
      List<String> cancellationReasonIds =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.masterListValueId.id)
          .from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.id.in(cancellationReasonHistoryIds)).fetch();
      whereClause.and(submission.id.in(
        submissionCancelService.getSubmissionIdsByCancellationReasonIds(cancellationReasonIds)));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.impl.administration.ReportBaseServiceImpl#filter(ch.bern.submiss.
   * services.api.dto.ReportBaseDTO)
   */
  @Override
  public List<OfferEntity> filter(ReportBaseDTO reportBaseDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method filter, Parameters: reportBaseDTO: {0}",
      reportBaseDTO);

    OperationReportDTO operationReportDTO = (OperationReportDTO) reportBaseDTO;

    return super.filter(operationReportDTO);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OperationReportService#search(ch.bern.submiss.
   * services.api.dto.OperationReportDTO)
   */
  @Override
  public OperationReportResultsDTO search(OperationReportDTO operationReportDTO, int page,
    int pageItems, String sortColumn, String sortType) {

    LOGGER.log(Level.CONFIG, "Executing method search, Parameters: operationReportDTO: {0}, "
        + "page: {1}, pageItems: {2}, sortColumn: {3}, sortType: {4}",
      new Object[]{operationReportDTO, page, pageItems, sortColumn, sortType});

    List<GeKoResultDTO> geKoResultDTOs = getGeKoResultDTOs(operationReportDTO, page, pageItems,
      sortColumn, sortType);
    OperationReportResultsDTO results = new OperationReportResultsDTO();
    results.setGeKoResultDTOs(geKoResultDTOs);
    // Setting reports searched criteria
    Map<String, String> searchCriteriaMap = getOperationReportSearchCriteria(operationReportDTO);
    results.setSearchedCriteria(searchCriteriaMap);
    return results;
  }

  /**
   * Gets the operation report search criteria.
   *
   * @param operationReportDTO the operation report DTO
   * @return the operation report search criteria
   */
  public Map<String, String> getOperationReportSearchCriteria(
    OperationReportDTO operationReportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOperationReportSearchCriteria, Parameters: operationReportDTO: {0}",
      operationReportDTO);

    // Report base search criteria
    Map<String, String> searchCriteriaMap = getReportSearchCriteria(operationReportDTO);
    // Dates criteria
    addDateSearchCriteria(operationReportDTO.getPublicationDateFrom(),
      operationReportDTO.getPublicationDateTo(), LookupValues.PUBLICATION, searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getPublicationDateAwardFrom(),
      operationReportDTO.getPublicationDateAwardTo(), LookupValues.PUBLICATION_AWARD,
      searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getApplicationOpeningDateFrom(),
      operationReportDTO.getApplicationOpeningDateTo(), LookupValues.APPLICATION_OPENING,
      searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getOfferOpeningDateFrom(),
      operationReportDTO.getOfferOpeningDateTo(), LookupValues.OFFER_OPENING, searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getCommissionProcurementProposalDateFrom(),
      operationReportDTO.getCommissionProcurementProposalDateTo(),
      LookupValues.COMMISION_PROCUREMENT_PROPOSAL, searchCriteriaMap);

    addDateSearchCriteria(operationReportDTO.getPublicationDateDirectAwardFrom(),
      operationReportDTO.getPublicationDateDirectAwardTo(), LookupValues.PUBLICATION_DIRECT_AWARD,
      searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getFirstDeadlineFrom(),
      operationReportDTO.getFirstDeadlineTo(), LookupValues.FIRST_DEADLINE, searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getSecondDeadlineFrom(),
      operationReportDTO.getSecondDeadlineTo(), LookupValues.SECOND_DEADLINE, searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getFirstLevelavailableDateFrom(),
      operationReportDTO.getFirstLevelavailableDateTo(), LookupValues.FIRST_LEVEL_AVAILABLE,
      searchCriteriaMap);
    addDateSearchCriteria(operationReportDTO.getLevelavailableDateFrom(),
      operationReportDTO.getLevelavailableDateTo(), LookupValues.LEVEL_AVAILABLE,
      searchCriteriaMap);

    // Zuschlagsempfänger
    addTextSearchCriteria(operationReportDTO.getAwardCompany(), LookupValues.AWARD_COMPANY,
      searchCriteriaMap);

    // Submissionsbemerkungen
    addTextSearchCriteria(operationReportDTO.getNotes(), LookupValues.NOTES, searchCriteriaMap);

    // Beschreibung
    addTextSearchCriteria(operationReportDTO.getDescription(), LookupValues.DESCRIPTION,
      searchCriteriaMap);

    // GATT/WTO
    addTextSearchCriteria(
      booleanToString(operationReportDTO.getGattWto(), LookupValues.YES, LookupValues.NO),
      LookupValues.GATT_WTO, searchCriteriaMap);

    // INTERN/EXTERN
    addTextSearchCriteria(
      booleanToString(operationReportDTO.getIntern(), LookupValues.INTERN, LookupValues.EXTERN),
      LookupValues.INTERN_EXTERN, searchCriteriaMap);
    // Verfahrensabbruch
    addMasterListSearchCriteria(operationReportDTO.getCancelationreasons(),
      LookupValues.CANCEL_REASON, searchCriteriaMap);

    // Freihandvergabe
    addMasterListSearchCriteria(operationReportDTO.getReasonFreeAwards(), LookupValues.FREE_REASON,
      searchCriteriaMap);

    return searchCriteriaMap;
  }

  /**
   * Adds the master list search criteria.
   *
   * @param searchedInputList the searched input list
   * @param keyValue the key value
   * @param searchCriteriaMap the search criteria map
   */
  private void addMasterListSearchCriteria(List<String> searchedInputList, String keyValue,
    Map<String, String> searchCriteriaMap) {

    LOGGER.log(Level.CONFIG,
      "Executing method addMasterListSearchCriteria, Parameters: searchedInputList: {0}, "
        + "keyValue: {1}, searchCriteriaMap: {2}",
      new Object[]{searchedInputList, keyValue, searchCriteriaMap});

    if (checkIfNotEmpty(searchedInputList)) {
      List<MasterListValueHistoryEntity> searchedObjectsList = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.id.in(searchedInputList))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();
      List<String> list = new ArrayList<>();
      for (MasterListValueHistoryEntity object : searchedObjectsList) {
        String name = "";
        name = object.getValue1() + LookupValues.SPACE + getValue(object.getValue2());
        list.add(name);
      }
      String searchedObjects = StringUtils.join(list, " / ");
      searchCriteriaMap.put(keyValue, searchedObjects);
    }
  }

  /**
   * Gets the value. If value2 of master list is empty, get empty string in concat.
   *
   * @param value the value
   * @return the value
   */
  private String getValue(String value) {

    LOGGER.log(Level.CONFIG, "Executing method getValue, Parameters: value: {0}",
      value);

    if (value == null) {
      value = StringUtils.EMPTY;
    }
    return value;
  }

  /**
   * Adds the text search criteria.
   *
   * @param value the value
   * @param keyValue the key value
   * @param searchCriteriaMap the search criteria map
   */
  private void addTextSearchCriteria(String value, String keyValue,
    Map<String, String> searchCriteriaMap) {

    LOGGER.log(Level.CONFIG, "Executing method addTextSearchCriteria, Parameters: value: {0}, "
        + "keyValue: {1}, searchCriteriaMap: {2}",
      new Object[]{value, keyValue, searchCriteriaMap});

    if (value != null) {
      searchCriteriaMap.put(keyValue, value);
    }
  }

  /**
   * Converts boolean to string.
   *
   * @param isTrue the is true
   * @param isTrueString the is true string
   * @param isFalseString the is false string
   * @return the string
   */
  private String booleanToString(Boolean isTrue, String isTrueString, String isFalseString) {

    LOGGER.log(Level.CONFIG,
      "Executing method booleanToString, Parameters: isTrue: {0}, "
        + "isTrueString: {1}, isFalseString: {2}",
      new Object[]{isTrue, isTrueString, isFalseString});

    String booleanString = null;
    if (isTrue != null) {
      if (isTrue) {
        booleanString = isTrueString;
      } else {
        booleanString = isFalseString;
      }
    }
    return booleanString;
  }

  /**
   * Gets the date search criteria.
   *
   * @param dateFrom the date from
   * @param dateTo the date to
   * @return the date search criteria
   */
  private String getDateSearchCriteria(Date dateFrom, Date dateTo) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDateSearchCriteria, Parameters: dateFrom: {0}, "
        + "dateTo: {1}",
      new Object[]{dateFrom, dateTo});

    String dateCriteria = null;
    String dateFromString = null;
    String dateToString = null;
    if (dateFrom != null && dateTo != null) {
      dateFromString = dateToString(dateFrom);
      dateToString = dateToString(dateTo);
      dateCriteria = dateFromString + LookupValues.SCORE + dateToString;
    } else if (dateFrom == null && dateTo != null) {
      dateToString = dateToString(dateTo);
      dateCriteria = LookupValues.BIS + LookupValues.SPACE + dateToString;
    } else if (dateFrom != null) {
      dateFromString = dateToString(dateFrom);
      dateCriteria = LookupValues.FROM + LookupValues.SPACE + dateFromString;
    }
    return dateCriteria;
  }

  /**
   * Adds the date search criteria.
   *
   * @param dateFrom the date from
   * @param dateTo the date to
   * @param keyValue the key value
   * @param searchCriteriaMap the search criteria map
   */
  private void addDateSearchCriteria(Date dateFrom, Date dateTo, String keyValue,
    Map<String, String> searchCriteriaMap) {

    LOGGER.log(Level.CONFIG, "Executing method addDateSearchCriteria, Parameters: "
        + "dateFrom: {0}, dateTo: {1}, keyValue: {2}, searchCriteriaMap: {3}",
      new Object[]{dateFrom, dateTo, keyValue, searchCriteriaMap});

    String dateCriteria = getDateSearchCriteria(dateFrom, dateTo);
    addTextSearchCriteria(dateCriteria, keyValue, searchCriteriaMap);
  }

  /**
   * Converts Date to string.
   *
   * @param date the date
   * @return the string
   */
  private String dateToString(Date date) {

    LOGGER.log(Level.CONFIG,
      "Executing method dateToString, Parameters: date: {0}",
      date);

    if (date != null) {
      LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LookupValues.DATE_FORMAT);
      return localDate.format(formatter);
    }
    return null;
  }

  /**
   * Gets the GeKo result DTOs.
   *
   * @param operationReportDTO the operation report DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return the GeKo result DTOs
   */
  private List<GeKoResultDTO> getGeKoResultDTOs(OperationReportDTO operationReportDTO, int page,
    int pageItems, String sortColumn, String sortType) {

    LOGGER.log(Level.CONFIG,
      "Executing method getGeKoResultDTOs, Parameters: operationReportDTO: {0}, "
        + "page: {1}, pageItems: {2}, sortColumn: {3}, sortType: {4}",
      new Object[]{operationReportDTO, page, pageItems, sortColumn, sortType});

    // get all information we need from the cacheBean
    Table<String, String, MasterListValueHistoryDTO> activeSd = cacheBean.getActiveSD();
    Table<String, String, List<MasterListValueHistoryDTO>> historySd = cacheBean.getHistorySd();
    Map<String, DepartmentHistoryDTO> activeDepartments = cacheBean.getActiveDepartmentHistorySD();
    Map<String, List<DepartmentHistoryDTO>> historyDepartments = cacheBean.getHistoryDepartments();
    Map<String, DirectorateHistoryDTO> activeDirectorates =
      cacheBean.getActiveDirectorateHistorySD();
    Map<String, List<DirectorateHistoryDTO>> historyDirectorates =
      cacheBean.getHistoryDirectorates();

    List<SubmissionEntity> submissionentities = filterSubmissionEntity(operationReportDTO, page,
      pageItems, sortColumn, sortType);
    List<SubmissionDTO> submissionDTOs = CustomSubmissionMapper.INSTANCE.toCustomSubmissionDTOList(
      submissionentities, activeSd, historySd, activeDepartments, historyDepartments,
      activeDirectorates, historyDirectorates);
    List<GeKoResultDTO> geKoResultDTOs =
      GeKoResultMapper.INSTANCE.submissionDTOtoGeKoResultDTO(submissionDTOs);
    for (GeKoResultDTO geKoResultDTO : geKoResultDTOs) {
      if (checkIfNotEmpty(geKoResultDTO.getStatus())) {
        // Get level available date
        getAvailableDate(geKoResultDTO);
        String status = geKoResultDTO.getStatus();
        // If status has been cancelled, retrieve the previous status before the cancel.
        // Proceed with the checks depending the submission status before cancellation.
        if (geKoResultDTO.getStatus().equals(TenderStatus.PROCEDURE_CANCELED.getValue())) {
          if (!geKoResultDTO.getSubmissionCancel().isEmpty()) {
            // Save the cancellation date of the submission.
            geKoResultDTO.setLevelavailableDate(
              geKoResultDTO.getSubmissionCancel().get(0).getAvailableDate());
          }
          geKoResultDTO.setStatus(
            tenderStatusHistoryService.getSubmissionStatusBeforeCancel(geKoResultDTO.getId()));
        }
        checkStatus(geKoResultDTO, status);
        // Set list of Submittents
        setSubmittentNamesList(geKoResultDTO);
        // Also, check with submission status before cancel and the current status of submission.
        if (isAwardNoticesCreated(geKoResultDTO.getStatus())
          || status.equals(TenderStatus.PROCEDURE_CANCELED.getValue())) {
          // Set calculation fields
          setSoCalculationList(geKoResultDTO);
        }
        getCommentField(geKoResultDTO, status);
      }
    }
    return geKoResultDTOs;
  }

  /**
   * Gets comment field.
   *
   * @param geKoResultDTO the GeKo result DTO
   * @param status the status
   */
  private void getCommentField(GeKoResultDTO geKoResultDTO, String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCommentField, Parameters: geKoResultDTO: {0}, "
        + "status: {1}",
      new Object[]{geKoResultDTO, status});

    if (geKoResultDTO != null) {
      boolean isSth = Boolean.FALSE;
      StringBuilder commentField = new StringBuilder();
      if (checkIfNotEmpty(geKoResultDTO.getNotes())) {// notes are present
        commentField.append(geKoResultDTO.getNotes());
        isSth = Boolean.TRUE;
      }

      if (status.equals(TenderStatus.PROCEDURE_CANCELED.getValue())) {
        // check and append cancellation worTypes if they exist
        getCancelatationDTOWorkTypes(geKoResultDTO, isSth, commentField);
        isSth = Boolean.TRUE;
      }

      if (isAfterAwardCompleted(geKoResultDTO.getStatus())
        && geKoResultDTO.getReasonFreeAward() != null
        && geKoResultDTO.getAboveThreshold() != null && geKoResultDTO.getAboveThreshold()) {
        // check and append reason free award if exists
        getReasonFreeAward(geKoResultDTO, isSth, commentField);
        isSth = Boolean.TRUE;
      }

      // check and append freezeCloseSubmission text if exists
      commentField.append(getfreezeCloseSubmission(geKoResultDTO, isSth));
      geKoResultDTO.setCommentField(commentField.toString());

    }
  }

  /**
   * Clear fields.
   *
   * @param geKoResultDTO the GeKo result DTO
   */
  private void clearFields(GeKoResultDTO geKoResultDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method clearFields, Parameters: geKoResultDTO: {0}",
      geKoResultDTO);

    geKoResultDTO.setSecondDeadline(null);
    geKoResultDTO.setOfferOpeningDate(null);
    geKoResultDTO.setFirstDeadline(null);
    geKoResultDTO.setApplicationOpeningDate(null);
    geKoResultDTO.setNotes(null);
    geKoResultDTO.setFirstLevelavailableDate(null);
    geKoResultDTO.setLevelavailableDate(null);
    geKoResultDTO.setCommissionProcurementProposalDate(null);
  }

  /**
   * Check status.
   *
   * @param geKoResultDTO the GeKo result DTO
   * @param previousStatus the previous status
   */
  private void checkStatus(GeKoResultDTO geKoResultDTO, String previousStatus) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkStatus, Parameters: geKoResultDTO: {0}, "
        + "previousStatus: {1}",
      new Object[]{geKoResultDTO, previousStatus});

    // According to GeKo Matrix
    if (geKoResultDTO != null && checkIfNotEmpty(geKoResultDTO.getStatus())) {
      String status = geKoResultDTO.getStatus();
      if (isStatusBeforeApplicationOpeningClosed(status)) {
        clearFields(geKoResultDTO);
      } else { // status greater or equal to 40: APPLICATION_OPENING_CLOSED
        // Verfugung - status not greater or equal to 260
        if (!isAwardNoticesCreated(status)) {
          if (!previousStatus.equals(TenderStatus.PROCEDURE_CANCELED.getValue())) {
            geKoResultDTO.setLevelavailableDate(null);
          }
          // BeKo Sitzung - status not greater or equal to 230
          if (isNotBeKoClosed(status)) {
            geKoResultDTO.setCommissionProcurementProposalDate(null);
            // Bewerbungsoffnung - status not greater or equal to 120
            if (isNotOfferOpeningClosed(status)) {
              geKoResultDTO.setSecondDeadline(null);
              geKoResultDTO.setOfferOpeningDate(null);
              if (geKoResultDTO.getFirstDeadline() == null) {
                geKoResultDTO.setCommentField(null);
              }
              // Verfügungsdatum 1.Stufe - status not greater or equal to 70
              if (!isAfterAward(status)) {
                geKoResultDTO.setFirstLevelavailableDate(null);
              }
            }
          }
        } else if (status.equals(TenderStatus.PROCEDURE_CANCELED.getValue())
          && !geKoResultDTO.getSubmissionCancel()
          .isEmpty()) { // Verfahrensabbruch - status equal to 290
          geKoResultDTO
            .setLevelavailableDate(geKoResultDTO.getSubmissionCancel().get(0).getAvailableDate());
        }
      }
    }
  }

  /**
   * Gets freezeCloseSubmission text.
   *
   * @param geKoResultDTO the GeKo result DTO
   * @param isSth true if GeKoResult has notes field in order to process the produced string
   * @return the string to be shown
   */
  private String getfreezeCloseSubmission(GeKoResultDTO geKoResultDTO, boolean isSth) {

    LOGGER.log(Level.CONFIG,
      "Executing method getfreezeCloseSubmission, Parameters: geKoResultDTO: {0}, "
        + "isSth: {1}",
      new Object[]{geKoResultDTO, isSth});

    boolean freezeCloseSubmission = Boolean.FALSE;
    StringBuilder commentField = new StringBuilder();
    List<SubmissionAwardInfoEntity> submissionAwardInfoEntities =
      getSubmissionAwardInfoEntities(geKoResultDTO);

    for (SubmissionAwardInfoEntity submissionAwardInfoEntity : submissionAwardInfoEntities) {
      if (submissionAwardInfoEntity.getFreezeCloseSubmission() != null
        && submissionAwardInfoEntity.getFreezeCloseSubmission().booleanValue()) {
        freezeCloseSubmission = Boolean.TRUE;
        break;
      }
    }

    if (
      !geKoResultDTO.getSubmissionCancel().isEmpty() && geKoResultDTO.getSubmissionCancel() != null
        && (geKoResultDTO.getSubmissionCancel().get(0).getFreezeCloseSubmission() != null
        && geKoResultDTO.getSubmissionCancel().get(0).getFreezeCloseSubmission())
        || freezeCloseSubmission) {
      if (isSth) {
        commentField.append(LookupValues.SEMICOLON);
      }
      commentField.append(LookupValues.LAUFENDES_BESCHWERDENVERFAHREN);
    }
    return commentField.toString();
  }

  /**
   * Gets CancelationDTO workTypes.
   *
   * @param geKoResultDTO the ge ko result DTO
   * @param isSth true if GeKoResult has notes field in order to process the produced string
   * @param commentField the comment field
   * @return the string to be shown
   */
  private String getCancelatationDTOWorkTypes(GeKoResultDTO geKoResultDTO, boolean isSth,
    StringBuilder commentField) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCancelatationDTOWorkTypes, Parameters: geKoResultDTO: {0}, "
        + "isSth: {1}, commentField: {2}",
      new Object[]{geKoResultDTO, isSth, commentField});

    if (!geKoResultDTO.getSubmissionCancel().isEmpty()
      && !geKoResultDTO.getSubmissionCancel().get(0).getWorkTypes().isEmpty()) {
      List<String> workTypeIds = new ArrayList<>();
      List<String> names = new ArrayList<>();

      for (MasterListValueDTO masterListValueDTO : geKoResultDTO.getSubmissionCancel().get(0)
        .getWorkTypes()) {
        workTypeIds.add(masterListValueDTO.getId());
      }

      List<MasterListValueHistoryEntity> masterListValueEntities = new JPAQuery<>(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId.id.in(workTypeIds)
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetch();
      for (MasterListValueHistoryEntity masterListValueHistoryEntity : masterListValueEntities) {
        StringBuilder value = new StringBuilder();
        value.append(" ").append(masterListValueHistoryEntity.getValue1()).append(" ÖBV");
        names.add(value.toString());
      }

      if (isSth) {
        commentField.append(LookupValues.SEMICOLON);
      }
      commentField.append("Verfahrensabbruch Art. 29 Abs.");
      commentField.append(StringUtils.join(names, " und Abs."));
    }
    return commentField.toString();
  }

  /**
   * Gets the reason free award.
   *
   * @param geKoResultDTO the GeKo result DTO
   * @param isSth the is sth
   * @param commentField the comment field
   * @return the reason free award
   */
  private String getReasonFreeAward(GeKoResultDTO geKoResultDTO, boolean isSth,
    StringBuilder commentField) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReasonFreeAward, Parameters: geKoResultDTO: {0}, "
        + "isSth: {1}, commentField: {2}",
      new Object[]{geKoResultDTO, isSth, commentField});

    if (geKoResultDTO.getReasonFreeAward() != null) {

      if (isSth) {
        commentField.append(LookupValues.SEMICOLON);
      }
      commentField.append(geKoResultDTO.getReasonFreeAward().getValue1());
      if (geKoResultDTO.getReasonFreeAward().getValue2() != null) {
        commentField.append("  ").append(geKoResultDTO.getReasonFreeAward().getValue2());
      }
    }
    return commentField.toString();
  }

  /**
   * Gets the submission award info entities.
   *
   * @param geKoResultDTO the ge ko result DTO
   * @return the submission award info entities
   */
  private List<SubmissionAwardInfoEntity> getSubmissionAwardInfoEntities(
    GeKoResultDTO geKoResultDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionAwardInfoEntities, Parameters: geKoResultDTO: {0}",
      geKoResultDTO);

    return new JPAQueryFactory(em).select(qSubmissionAwardInfoEntity)
      .from(qSubmissionAwardInfoEntity)
      .where(qSubmissionAwardInfoEntity.submission.id.eq(geKoResultDTO.getId())).fetch();
  }

  /**
   * Gets the submittent name.
   *
   * @param submittentNames the submittent names
   * @return the submittent name
   */
  private String getSubmittentName(List<String> submittentNames) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmittentName, Parameters: submittentNames: {0}",
      submittentNames);

    String submittentName = null;
    if (!submittentNames.isEmpty()) {
      submittentName = StringUtils.join(submittentNames, " / ");
    }
    return submittentName;
  }

  /**
   * Sets list of submittent names.
   *
   * @param geKoResultDTO the new submittent names list
   */
  private void setSubmittentNamesList(GeKoResultDTO geKoResultDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSubmittentNamesList, Parameters: geKoResultDTO: {0}",
      geKoResultDTO);

    List<String> submittentNames = new ArrayList<>();

    if (geKoResultDTO != null && checkIfNotEmpty(geKoResultDTO.getStatus())) {

      if (!isAwardNoticesCreated(geKoResultDTO.getStatus())) {
        geKoResultDTO.setSubmittentNames(null);
        geKoResultDTO.setSubmittentName(null);
      } else {
        List<OfferDTO> offerDTOs = offerService.getOffersBySubmission(geKoResultDTO.getId());
        for (OfferDTO offerDTO : offerDTOs) {
          if (offerDTO != null && offerDTO.getIsAwarded() != null && offerDTO.getIsAwarded()) {
            submittentNames.add(offerDTO.getSubmittent().getSubmittentNameArgeSub());
          }
        }

        if (!submittentNames.isEmpty()) {
          geKoResultDTO.setSubmittentNames(submittentNames);
          geKoResultDTO.setSubmittentName(getSubmittentName(submittentNames));
        }
      }
    }
  }

  /**
   * Gets the sum calculation.
   *
   * @param sumList the sum list
   * @return the sum calculation
   */
  private String getSumCalculation(List<BigDecimal> sumList) {

    LOGGER.log(Level.CONFIG, "Executing method getSumCalculation, Parameters: sumList: {0}",
      sumList);

    List<String> amountString = amountForamtterList(sumList);
    String submittentName = null;

    if (!sumList.isEmpty() && !amountString.isEmpty()) {
      submittentName = StringUtils.join(amountString, " / ");
    }
    return submittentName;
  }

  /**
   * Formats amount list.
   *
   * @param sumList the list of string amount
   * @return the formated string amount list
   */
  private List<String> amountForamtterList(List<BigDecimal> sumList) {

    LOGGER.log(Level.CONFIG,
      "Executing method amountForamtterList, Parameters: sumList: {0}",
      sumList);

    List<String> amountString = new ArrayList<>();

    for (BigDecimal amount : sumList) {
      amountString.add(amountForamtter(amount.toString()));
    }
    return amountString;
  }

  /**
   * Formats amount.
   *
   * @param number the string amount
   * @return the formated string amount
   */
  private String amountForamtter(String number) {

    LOGGER.log(Level.CONFIG,
      "Executing method amountForamtter, Parameters: number: {0}",
      number);

    if (number.length() > 6) {
      double amount = Double.parseDouble(number);
      DecimalFormat formatter = new DecimalFormat("#,###.00");
      number = formatter.format(amount).replace(",", "'");
    }
    return number;
  }

  /**
   * Gets the sum calculation total.
   *
   * @param sumList the sum list
   * @return the sum calculation total
   */
  private BigDecimal getSumCalculationTotal(List<BigDecimal> sumList) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSumCalculationTotal, Parameters: "
        + "sumList: {0}",
      sumList);

    BigDecimal sumTotal = BigDecimal.ZERO;

    if (!sumList.isEmpty()) {
      for (BigDecimal sum : sumList) {
        sumTotal = sumTotal.add(sum);
      }
    }
    return sumTotal;
  }

  /**
   * Sets list of submittent names.
   *
   * @param geKoResultDTO the new so calculation list
   */
  private void setSoCalculationList(GeKoResultDTO geKoResultDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSoCalculationList, Parameters: geKoResultDTO: {0}",
      geKoResultDTO);

    if (geKoResultDTO != null) {
      List<OfferDTO> offerDTOs = offerService.getOffersBySubmission(geKoResultDTO.getId());
      Process process = geKoResultDTO.getProcess();
      List<BigDecimal> soSumList = new ArrayList<>();
      List<BigDecimal> eSumList = new ArrayList<>();
      List<BigDecimal> fSumList = new ArrayList<>();

      for (OfferDTO offerDTO : offerDTOs) {
        if (offerDTO != null && offerDTO.getIsAwarded() != null && offerDTO.getIsAwarded()) {
          if (process.equals(Process.SELECTIVE) || process.equals(Process.OPEN)) {
            soSumList.add(nulltoZeroDecimal(offerDTO.getAmount())
              .add(nulltoZeroDecimal(offerDTO.getOperatingCostsAmount())));
          } else if (process.equals(Process.INVITATION)) {
            eSumList.add(nulltoZeroDecimal(offerDTO.getAmount())
              .add(nulltoZeroDecimal(offerDTO.getOperatingCostsAmount())));
          } else {
            fSumList.add(nulltoZeroDecimal(offerDTO.getAmount())
              .add(nulltoZeroDecimal(offerDTO.getOperatingCostsAmount())));
          }
        }
      }
      geKoResultDTO.setSoSumList(soSumList);
      geKoResultDTO.seteSumList(eSumList);
      geKoResultDTO.setfSumList(fSumList);
      geKoResultDTO.setSoCalculation(getSumCalculationTotal(soSumList));
      geKoResultDTO.seteCalculation(getSumCalculationTotal(eSumList));
      geKoResultDTO.setfCalculation(getSumCalculationTotal(fSumList));
      geKoResultDTO.setSoSum(getSumCalculation(soSumList));
      geKoResultDTO.seteSum(getSumCalculation(eSumList));
      geKoResultDTO.setfSum(getSumCalculation(fSumList));

    }
  }

  /**
   * Gets the search criteria as parameters.
   *
   * @param geKoResults the ge ko results
   * @param selectedColumns the selected columns
   * @param sumAmount the sum amount
   * @return the search criteria as parameters
   */
  private Map<String, Object> getSearchCriteriaAsParameters(OperationReportResultsDTO geKoResults,
    String selectedColumns, String sumAmount) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSearchCriteriaAsParameters, Parameters: geKoResults: {0}, "
        + "selectedColums: {1}, sumAmount: {2}",
      new Object[]{geKoResults, selectedColumns, sumAmount});

    StringBuilder sb = new StringBuilder();
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(LookupValues.IS_COLUMN_SELECTED, selectedColumns);
    Map<String, String> searchedCriteriaMap = geKoResults.getSearchedCriteria();

    for (Map.Entry<String, String> searchedCriteria : searchedCriteriaMap.entrySet()) {
      parameters.put(searchedCriteria.getKey(), searchedCriteria.getValue());
      if (searchedCriteria.getValue() != null) {
        sb.append(LookupValues.SPACE + "<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">"
          + searchedCriteria.getKey() + "</style>").append(": ")
          .append(searchedCriteria.getValue());
      }
    }

    parameters.put(LookupValues.ZEITRAUM,
      getDateSearchCriteria(geKoResults.getStartDate(), geKoResults.getEndDate()));

    setCalculationParameter(parameters, geKoResults.getGeKoResultDTOs(), sumAmount);

    parameters.put(LookupValues.SEARCHEDCRITERIA, sb.toString());
    parameters.put(LookupValues.DATASOUURCE,
      new JRBeanCollectionDataSource(geKoResults.getGeKoResultDTOs()));
    return parameters;
  }

  /**
   * Sets the calculation parameter.
   *
   * @param parameters the parameters
   * @param geKoResultDTOs the ge ko result DT os
   * @param sumAmount the sum amount
   */
  private void setCalculationParameter(Map<String, Object> parameters,
    List<GeKoResultDTO> geKoResultDTOs, String sumAmount) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCalculationParameter, Parameters: parameters: {0}, "
        + "geKoResultDTOs: {1}, sumAmount: {2}",
      new Object[]{parameters, geKoResultDTOs, sumAmount});

    BigDecimal soSum = new BigDecimal(BigDecimal.ZERO.intValue());
    BigDecimal eSum = new BigDecimal(BigDecimal.ZERO.intValue());
    BigDecimal fSum = new BigDecimal(BigDecimal.ZERO.intValue());

    for (GeKoResultDTO geKoResultDTO : geKoResultDTOs) {
      if (geKoResultDTO != null) {
        soSum = soSum.add(getCalculation(geKoResultDTO.getSoCalculation()));
        fSum = fSum.add(getCalculation(geKoResultDTO.getfCalculation()));
        eSum = eSum.add(getCalculation(geKoResultDTO.geteCalculation()));
      } else {
        LOGGER.log(Level.WARNING, LookupValues.NPE_EXCEPTION);
      }
    }
    parameters.put(LookupValues.SO_AMOUNT, soSum);
    parameters.put(LookupValues.E_AMOUNT, eSum);
    parameters.put(LookupValues.F_AMOUNT, fSum);
    parameters.put(LookupValues.SUM_AMOUNTS, boldAmounts(sumAmount));
    String totalAmount = soSum.add(eSum).add(fSum).toString();
    parameters.put(LookupValues.TOTAL_AMOUNT, amountForamtter(totalAmount));
  }

  /**
   * Bold amounts.
   *
   * @param sumAmount the sum amount
   * @return the string
   */
  private String boldAmounts(String sumAmount) {

    LOGGER.log(Level.CONFIG,
      "Executing method boldAmounts, Parameters: sumAmount: {0}",
      sumAmount);

    sumAmount = replaceSO(sumAmount); // fix so label value
    // spaces between amounts
    if (sumAmount != null && sumAmount.contains(LookupValues.E_LABEL)
      && (sumAmount.contains(LookupValues.SO_LABEL) || sumAmount
      .contains(LookupValues.SO_LABEL2))) {
      sumAmount = sumAmount
        .replaceAll(LookupValues.E_LABEL, LookupValues.SPACE + LookupValues.E_LABEL);
    }

    if (sumAmount != null && sumAmount.contains(LookupValues.F_LABEL) && (
      sumAmount.contains(LookupValues.E_LABEL)
        || sumAmount.contains(LookupValues.SO_LABEL) || sumAmount
        .contains(LookupValues.SO_LABEL2))) {
      sumAmount = sumAmount
        .replaceAll(LookupValues.F_LABEL, LookupValues.SPACE + LookupValues.F_LABEL);
    }

    // bold labels
    sumAmount = boldValueIfExists(sumAmount, LookupValues.SO_LABEL);
    sumAmount = boldValueIfExists(sumAmount, LookupValues.E_LABEL);
    sumAmount = boldValueIfExists(sumAmount, LookupValues.F_LABEL);
    return sumAmount;
  }

  /**
   * Bold value if exists.
   *
   * @param givenValue the given value
   * @param value the value
   * @return the string
   */
  private String boldValueIfExists(String givenValue, String value) {

    LOGGER.log(Level.CONFIG,
      "Executing method boldValueIfExists, Parameters: givenValue: {0}, "
        + "value: {1}",
      new Object[]{givenValue, value});

    if (givenValue != null && givenValue.contains(value)) {
      givenValue = givenValue.replaceAll(value,
        "<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">" + value + "</style>");
    }
    return givenValue;
  }

  /**
   * Replace SO.
   *
   * @param value the value
   * @return the string
   */
  private String replaceSO(String value) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceSO, Parameters: value: {0}",
      value);

    if (value != null && value.contains(LookupValues.SO_LABEL2)) {
      value = value.replaceAll(LookupValues.SO_LABEL2, LookupValues.SO_LABEL);
    }
    return value;
  }

  /**
   * Gets the calculation.
   *
   * @param value the value
   * @return the calculation
   */
  private BigDecimal getCalculation(BigDecimal value) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCalculation, Parameters: value: {0}",
      value);

    if (value == null) {
      value = new BigDecimal(BigDecimal.ZERO.intValue());
    }
    return value;
  }

  /**
   * Gets available date from AwardInfoEntity.
   *
   * @param geKoResultDTO the ge ko result DTO
   */
  private void getAvailableDate(GeKoResultDTO geKoResultDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAvailableDate, Parameters: geKoResultDTO: {0}",
      geKoResultDTO);

    if (geKoResultDTO != null && geKoResultDTO.getId() != null) {
      List<SubmissionAwardInfoEntity> submissionAwardInfoEntities = new JPAQueryFactory(em)
        .select(qSubmissionAwardInfoEntity).from(qSubmissionAwardInfoEntity)
        .where(qSubmissionAwardInfoEntity.submission.id.eq(geKoResultDTO.getId())).fetch();
      for (SubmissionAwardInfoEntity submissionAwardInfoEntity : submissionAwardInfoEntities) {
        if (submissionAwardInfoEntity.getLevel() == 1) {
          geKoResultDTO.setFirstLevelavailableDate(submissionAwardInfoEntity.getAvailableDate());
        } else {
          geKoResultDTO.setLevelavailableDate(submissionAwardInfoEntity.getAvailableDate());
        }
      }
    }
  }

  /**
   * Gets the submission IDs
   *
   * @param reportBaseDTO the reportBaseDTO
   * @return the list with the submission IDs
   */
  @Override
  public List<String> getSubmissionIds(ReportBaseDTO reportBaseDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionIds, Parameters: reportBaseDTO: {0}",
      reportBaseDTO);

    return super.getSubmissionIds((OperationReportDTO) reportBaseDTO);
  }

  /**
   * Check if not empty.
   *
   * @param value the value
   * @return true, if successful
   */
  private boolean checkIfNotEmpty(String value) {
    return value != null && value.length() > 0;
  }

  /**
   * Add date check to where clause.
   *
   * @param dateFrom the search input date
   * @param whereClause the BooleanBuilder
   * @param caseNum the case of field
   * @param isFromDate true if from date is filled
   * @param isExternalField true if a entity is different from submission
   */
  private void addDateCheck(Date dateFrom, BooleanBuilder whereClause, int caseNum,
    boolean isFromDate, boolean isExternalField) {

    LOGGER.log(Level.CONFIG,
      "Executing method addDateCheck, Parameters: dateFrom: {0}, "
        + "whereClause: {1}, caseNum: {2}, isFromDate: {3}, isExternalField: {4}",
      new Object[]{dateFrom, whereClause, caseNum, isFromDate, isExternalField});

    if (dateFrom != null) {
      JPAQuery<String> query;
      if (isExternalField) {
        query = new JPAQueryFactory(em).select(qSubmissionAwardInfoEntity.submission.id)
          .from(qSubmissionAwardInfoEntity);
      } else {
        query = new JPAQueryFactory(em).select(submission.id).from(submission);
      }
      List<String> submissionsIds =
        query.where(getDatePredicateRight(caseNum, dateFrom, isFromDate)).fetch();
      whereClause.and(submission.id.in(submissionsIds));
    }
  }

  /**
   * Gets predicate condition for from Date according to the filled searched field.
   *
   * @param caseNum the case of field
   * @param searchDate the search input date
   * @param isFromDate true if from date search field is filled
   * @return the predicate right
   */
  private Predicate getDatePredicateRight(int caseNum, Date searchDate, boolean isFromDate) {

    LOGGER.log(Level.CONFIG, "Executing method getDatePredicateRight, Parameters: "
        + "caseNum: {0}, searchDate: {1}, isFromDate: [2}",
      new Object[]{caseNum, searchDate, isFromDate});
    Predicate right;

    switch (caseNum) {
      case 1: // Publikation Zuschlag
        right = getPredicateRight(isFromDate,
          submission.publicationDateAward.after(searchDate)
            .or(submission.publicationDateAward.eq(searchDate)),
          submission.publicationDateAward.before(getCalDate(searchDate))
            .or(submission.publicationDateAward.eq(getCalDate(searchDate))));
        break;
      case 2: // Publikation
        right = getPredicateRight(isFromDate,
          submission.publicationDate.after(searchDate)
            .or(submission.publicationDate.eq(searchDate)),
          submission.publicationDate.before(getCalDate(searchDate))
            .or(submission.publicationDate.eq(getCalDate(searchDate))));
        break;
      case 3: // Bewerbungsöffnung
        right = getPredicateRight(isFromDate,
          submission.applicationOpeningDate.after(searchDate)
            .or(submission.applicationOpeningDate.eq(searchDate)),
          submission.applicationOpeningDate.before(getCalDate(searchDate))
            .or(submission.applicationOpeningDate.eq(getCalDate(searchDate))));
        break;
      case 4: // Offertöffnung
        right = getPredicateRight(isFromDate,
          submission.offerOpeningDate.after(searchDate)
            .or(submission.offerOpeningDate.eq(searchDate)),
          submission.offerOpeningDate.before(getCalDate(searchDate))
            .or(submission.offerOpeningDate.eq(getCalDate(searchDate))));
        break;
      case 5: // BeKo-Sitzung
        right = getPredicateRight(isFromDate,
          submission.commissionProcurementProposalDate.after(searchDate)
            .or(submission.commissionProcurementProposalDate.eq(searchDate)),
          submission.commissionProcurementProposalDate.before(getCalDate(searchDate))
            .or(submission.commissionProcurementProposalDate.eq(getCalDate(searchDate))));
        break;
      case 6: // Publikation Absicht freihändige Vergabe

        right = getPredicateRight(isFromDate,
          submission.publicationDateDirectAward.after(searchDate)
            .or(submission.publicationDateDirectAward.eq(searchDate)),
          submission.publicationDateDirectAward.before(getCalDate(searchDate))
            .or(submission.publicationDateDirectAward.eq(getCalDate(searchDate))));
        break;
      case 7: // Eingabetermin 1
        right = getPredicateRight(isFromDate,
          submission.firstDeadline.after(searchDate).or(submission.firstDeadline.eq(searchDate)),
          submission.firstDeadline.before(getCalDate(searchDate))
            .or(submission.firstDeadline.eq(getCalDate(searchDate))));
        break;
      case 8: // Eingabetermin 2

        right = getPredicateRight(isFromDate,
          submission.secondDeadline.after(searchDate)
            .or(submission.secondDeadline.eq(searchDate)),
          submission.secondDeadline.before(getCalDate(searchDate))
            .or(submission.secondDeadline.eq(getCalDate(searchDate))));
        break;
      case 9: // Verfügungsdatum 1.Stufe
        right = getPredicateRight(isFromDate,
          qSubmissionAwardInfoEntity.availableDate.after(searchDate)
            .or(qSubmissionAwardInfoEntity.availableDate.eq(searchDate))
            .and(qSubmissionAwardInfoEntity.level.in(1)),
          qSubmissionAwardInfoEntity.availableDate.before(getCalDate(searchDate))
            .or(qSubmissionAwardInfoEntity.availableDate.eq(getCalDate(searchDate)))
            .and(qSubmissionAwardInfoEntity.level.in(1)));
        break;
      case 10: // Verfügungsdatum
        right = getPredicateRight(isFromDate,
          qSubmissionAwardInfoEntity.availableDate.after(searchDate)
            .or(qSubmissionAwardInfoEntity.availableDate.eq(searchDate))
            .and(qSubmissionAwardInfoEntity.level.in(0, 2)),
          qSubmissionAwardInfoEntity.availableDate.before(getCalDate(searchDate))
            .or(qSubmissionAwardInfoEntity.availableDate.eq(getCalDate(searchDate)))
            .and(qSubmissionAwardInfoEntity.level.in(0, 2)));
        break;

      default:
        right = null;
        break;
    }
    return right;
  }

  /**
   * Gets the predicate right.
   *
   * @param isFromDate the is from date
   * @param predicateFrom the predicate from
   * @param predicateTo the predicate to
   * @return the predicate right
   */
  private Predicate getPredicateRight(boolean isFromDate, Predicate predicateFrom,
    Predicate predicateTo) {

    LOGGER.log(Level.CONFIG,
      "Executing method getPredicateRight, Parameters: isFromDate: {0}, "
        + "predicateFrom: [1}, predicateTo: {2}",
      new Object[]{isFromDate, predicateFrom, predicateTo});

    Predicate right;

    if (isFromDate) {
      right = predicateFrom;
    } else {
      right = predicateTo;
    }
    return right;
  }

  /**
   * Gets the cal date.
   *
   * @param dateTo the date to
   * @return the cal date
   */
  private Date getCalDate(Date dateTo) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCalDate, Parameters: dateTo: {0}",
      dateTo);

    // setting hours-minutes-seconds till date end
    // in order to include all records from that day
    Calendar cal = Calendar.getInstance();
    cal.setTime(dateTo);
    cal.set(Calendar.HOUR, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    return cal.getTime();
  }

  /**
   * Gets the number of GeKo results.
   *
   * @param operationReportDTO the operationReportDTO
   * @return the number of GeKo results
   */
  @Override
  public Long getNumberOfOperationReportResults(OperationReportDTO operationReportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNumberOfOperationReportResults, Parameters: operationReportDTO: {0}",
      operationReportDTO);

    operationReportSecurityCheck();

    return filterCount(operationReportDTO);
  }

  /**
   * Null to zero decimal.
   *
   * @param amount the amount
   * @return the big decimal amount
   */
  private BigDecimal nulltoZeroDecimal(BigDecimal amount) {

    LOGGER.log(Level.CONFIG,
      "Executing method nulltoZeroDecimal, Parameters: amount: {0}",
      amount);

    if (amount == null) {
      amount = BigDecimal.ZERO;
    }
    return amount;
  }

  /**
   * Generate the report.
   *
   * @param operationReportResultsDTO the operationReportResultsDTO
   * @param selectedColumns the selected columns
   * @param caseFormat the file format
   * @param sumAmount the sum amount
   * @return the byte[]
   */
  @Override
  public byte[] generateReport(OperationReportResultsDTO operationReportResultsDTO,
    String selectedColumns, String caseFormat, String sumAmount) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateReport, Parameters: operationReportResultsDTO: {0}, "
        + "selectedColumns: {1}, caseFormat: {2}, sumAmount: {3}",
      new Object[]{operationReportResultsDTO, selectedColumns, caseFormat, sumAmount});

    operationReportSecurityCheck();

    byte[] report = null;
    try {
      report = report(operationReportResultsDTO, selectedColumns, caseFormat, sumAmount);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return report;
  }

  /**
   * Generate report.
   *
   * @param operationReportResultsDTO the operation report results DTO
   * @param selectedColumns the selected colums
   * @param caseFormat the case format
   * @param sumAmount the sum amount
   * @return the byte[]
   */
  private byte[] report(OperationReportResultsDTO operationReportResultsDTO, String selectedColumns,
    String caseFormat, String sumAmount) {

    LOGGER.log(Level.CONFIG,
      "Executing method report, Parameters: operationReportResultsDTO: {0}, "
        + "selectedColumns: {1}, caseFormat: {2}, sumAmount: {3}",
      new Object[]{operationReportResultsDTO, selectedColumns, caseFormat, sumAmount});

    /* Setting the parameters of report */
    Map<String, Object> parameters =
      getSearchCriteriaAsParameters(operationReportResultsDTO, selectedColumns, sumAmount);

    if (caseFormat.equals(LookupValues.EXCEL_FORMAT)) {
      /* Generate Excel report */
      return createGeKoExcel(operationReportResultsDTO, selectedColumns, parameters);
    } else if (caseFormat.equals(LookupValues.PDF_FORMAT)) {
      /* Generate PDF report */
      return createGeKoPdf(operationReportResultsDTO, parameters);
    }
    return new byte[0];
  }

  /**
   * Gets the report file name.
   *
   * @param caseFormat the file format
   * @return the file name
   */
  @Override
  public String getReportFileName(String caseFormat) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportFileName, Parameters: caseFormat: {0}",
      caseFormat);

    StringBuilder fileName = new StringBuilder();
    LocalDateTime now = LocalDateTime.now();
    fileName.append(LookupValues.GEKO).append(LookupValues.UNDER_SCORE)
      .append(now.format(DateTimeFormatter.ofPattern(LookupValues.DATE_FORMAT)))
      .append(LookupValues.UNDER_SCORE).append(now.getHour()).append(now.getMinute())
      .append(now.getSecond()).append(caseFormat);
    return fileName.toString();
  }

  /**
   * Create the GeKo Pdf with Jasper.
   *
   * @param operationReportResultsDTO the operation report results DTO
   * @param parameters the parameters
   * @return the byte[]
   */
  private byte[] createGeKoPdf(OperationReportResultsDTO operationReportResultsDTO, Map<String, Object> parameters) {

    LOGGER.log(Level.CONFIG, "Executing method export, Parameters: "
        + "operationReportResultsDTO: {0}, parameters: {1}",
      new Object[]{operationReportResultsDTO, parameters});

    /* Get Report Template */
    InputStream inputStream = getReportTemplate(LookupValues.GEKO_TEMPLATE);

    if (inputStream != null) {
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
        InputStream reportStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
          new JRBeanCollectionDataSource(operationReportResultsDTO.getGeKoResultDTOs()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfExporter(jasperPrint, outputStream);
        return outputStream.toByteArray();
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    return new byte[0];
  }

  /**
   * Pdf exporter.
   *
   * @param jasperPrint the jasper print
   * @param outputStream the output stream
   */
  private void pdfExporter(JasperPrint jasperPrint, ByteArrayOutputStream outputStream) {

    LOGGER.log(Level.CONFIG,
      "Executing method pdfExporter, Parameters: jasperPrint: {0}, "
        + "outputStream: {1}",
      new Object[]{jasperPrint, outputStream});

    JRPdfExporter exporter = new JRPdfExporter();
    SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
    exporter.setConfiguration(configuration);
    try {
      exporter.exportReport();
    } catch (JRException e) {
      e.getMessage();
    }
  }

  /**
   * Gets the not available date ids.
   *
   * @param predicate the predicate
   * @return the not available date ids
   */
  private List<String> getNotAvailableDateIds(Predicate predicate) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNotAvailableDateIds, Parameters: predicate: {0}",
      predicate);

    return new JPAQueryFactory(em).select(qTenderStatusHistoryEntity.tenderId.id)
      .from(qTenderStatusHistoryEntity).where(predicate).fetch();
  }

  /**
   * Validate the errors.
   *
   * @param operationReportDTO the operationReportDTO
   * @return the list with the errors
   */
  @Override
  public Set<ValidationError> validate(OperationReportDTO operationReportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method validate, Parameters: operationReportDTO: {0}",
      operationReportDTO);

    operationReportSecurityCheck();

    Set<ValidationError> errors = new HashSet<>();
    if (operationReportDTO != null) {

      // Check for future dates
      if (isFutureDate(operationReportDTO.getStartDate(), operationReportDTO.getEndDate())) {
        errors.add(new ValidationError("futureDateErrorField", LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getPublicationDateAwardFrom(),
        operationReportDTO.getPublicationDateAwardTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.PUBLICATION_AWARD.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getPublicationDateDirectAwardFrom(),
        operationReportDTO.getPublicationDateDirectAwardTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.PUBLICATION_DIRECT_AWARD.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }

      if (isFutureDate(operationReportDTO.getPublicationDateFrom(),
        operationReportDTO.getPublicationDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.PUBLICATION.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getApplicationOpeningDateFrom(),
        operationReportDTO.getApplicationOpeningDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.APPLICATION_OPENING.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getOfferOpeningDateFrom(),
        operationReportDTO.getOfferOpeningDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.OFFER_OPENING.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }

      if (isFutureDate(operationReportDTO.getCommissionProcurementProposalDateFrom(),
        operationReportDTO.getCommissionProcurementProposalDateTo())) {
        errors.add(
          new ValidationError(GEKO_CRITERIA.COMMISION_PROCUREMENT_PROPOSAL.getFutureErrorField(),
            LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getFirstDeadlineFrom(),
        operationReportDTO.getFirstDeadlineTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.FIRST_DEADLINE.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getSecondDeadlineFrom(),
        operationReportDTO.getSecondDeadlineTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.SECOND_DEADLINE.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getFirstLevelavailableDateFrom(),
        operationReportDTO.getFirstLevelavailableDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.FIRST_LEVEL_AVAILABLE.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }
      if (isFutureDate(operationReportDTO.getLevelavailableDateFrom(),
        operationReportDTO.getLevelavailableDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.LEVEL_AVAILABLE.getFutureErrorField(),
          LookupValues.FUTURE_ERROR));
      }

      // Check for earlier end date than start date
      if (isEndDateBeforeStartDate(operationReportDTO.getStartDate(),
        operationReportDTO.getEndDate())) {
        errors.add(new ValidationError("startDateAfterEndDateErrorField",
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getPublicationDateAwardFrom(),
        operationReportDTO.getPublicationDateAwardTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.PUBLICATION_AWARD.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getPublicationDateDirectAwardFrom(),
        operationReportDTO.getPublicationDateDirectAwardTo())) {
        errors.add(
          new ValidationError(GEKO_CRITERIA.PUBLICATION_DIRECT_AWARD.getDateOrderErrorField(),
            LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }

      if (isEndDateBeforeStartDate(operationReportDTO.getPublicationDateFrom(),
        operationReportDTO.getPublicationDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.PUBLICATION.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getApplicationOpeningDateFrom(),
        operationReportDTO.getApplicationOpeningDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.APPLICATION_OPENING.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getOfferOpeningDateFrom(),
        operationReportDTO.getOfferOpeningDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.OFFER_OPENING.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }

      if (isEndDateBeforeStartDate(operationReportDTO.getCommissionProcurementProposalDateFrom(),
        operationReportDTO.getCommissionProcurementProposalDateTo())) {
        errors.add(new ValidationError(
          GEKO_CRITERIA.COMMISION_PROCUREMENT_PROPOSAL.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getFirstDeadlineFrom(),
        operationReportDTO.getFirstDeadlineTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.FIRST_DEADLINE.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getSecondDeadlineFrom(),
        operationReportDTO.getSecondDeadlineTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.SECOND_DEADLINE.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getFirstLevelavailableDateFrom(),
        operationReportDTO.getFirstLevelavailableDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.FIRST_LEVEL_AVAILABLE.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }
      if (isEndDateBeforeStartDate(operationReportDTO.getLevelavailableDateFrom(),
        operationReportDTO.getLevelavailableDateTo())) {
        errors.add(new ValidationError(GEKO_CRITERIA.LEVEL_AVAILABLE.getDateOrderErrorField(),
          LookupValues.END_DATE_BEFORE_START_DATE_ERROR));
      }

    }
    return errors;
  }

  /**
   * Checks if it's a future date.
   *
   * @param dateFrom the date from
   * @param dateTo the date to
   * @return true, if future date
   */
  private boolean isFutureDate(Date dateFrom, Date dateTo) {

    LOGGER.log(Level.CONFIG,
      "Executing method isFutureDate, Parameters: dateFrom: {0}, dateTo: {1}",
      new Object[]{dateFrom, dateTo});

    Date today = new Date();
    return (dateFrom != null && dateFrom.after(today)) || (dateTo != null && dateTo.after(today));
  }

  /**
   * Checks if end date before start date.
   *
   * @param dateFrom the date from
   * @param dateTo the date to
   * @return true, if end date before start date
   */
  private boolean isEndDateBeforeStartDate(Date dateFrom, Date dateTo) {

    LOGGER.log(Level.CONFIG,
      "Executing method isEndDateBeforeStartDate, Parameters: dateFrom: {0}, dateTo: {1}",
      new Object[]{dateFrom, dateTo});

    return dateFrom != null && dateTo != null && dateFrom.after(dateTo);
  }

  /**
   * Gets the excluded predicate.
   *
   * @param date the date
   * @return the excluded predicate
   */
  private Predicate getExcludedPredicate(long date) {

    LOGGER.log(Level.CONFIG,
      "Executing method getExcludedPredicate, Parameter: date: {0}",
      date);

    // get AWARD_NOTICES_CREATED or PROCEDURE_CANCELED latest status before the given date
    // get max date of Status History, exclude the PROCEDURE_COMPLETED Status
    return qTenderStatusHistoryEntity.onDate.before(new java.sql.Timestamp(date))
      .and(qTenderStatusHistoryEntity.onDate
        .in(JPAExpressions.select(qTenderStatusHistoryEntity.onDate.max())
          .from(qTenderStatusHistoryEntity).where(
            qTenderStatusHistoryEntity.statusId.notIn(TenderStatus.PROCEDURE_COMPLETED.getValue()))
          .groupBy(qTenderStatusHistoryEntity.tenderId)))
      .and(qTenderStatusHistoryEntity.statusId.in(TenderStatus.AWARD_NOTICES_CREATED.getValue(),
        TenderStatus.PROCEDURE_CANCELED.getValue()));
  }

  /**
   * Gets the excluded created submission after date predicate.
   *
   * @param date the date
   * @return the excluded created submission after date predicate
   */
  private Predicate getExcludedCreatedSubmissionAfterDatePredicate(long date) {

    LOGGER.log(Level.CONFIG,
      "Executing method getExcludedCreatedSubmissionAfterDatePredicate, Parameter: "
        + "date: {0}",
      date);

    // get SUBMISSION_CREATED oldest status after  the given date
    return qTenderStatusHistoryEntity.onDate.after(new java.sql.Timestamp(date))
      .and(qTenderStatusHistoryEntity.onDate
        .in(JPAExpressions.select(qTenderStatusHistoryEntity.onDate.min())
          .from(qTenderStatusHistoryEntity).groupBy(qTenderStatusHistoryEntity.tenderId)))
      .and(qTenderStatusHistoryEntity.statusId.in(TenderStatus.SUBMISSION_CREATED.getValue()));
  }

  /**
   * Checks if is award notices created.
   *
   * @param status the status
   * @return true, if is award notices created
   */
  private boolean isAwardNoticesCreated(String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method isAwardNoticesCreated, Parameter: "
        + "status: {0}",
      status);

    return status.equals(TenderStatus.AWARD_NOTICES_CREATED.getValue())
      || status.equals(TenderStatus.CONTRACT_CREATED.getValue())
      || status.equals(TenderStatus.PROCEDURE_COMPLETED.getValue())
      || status.equals(TenderStatus.PROCEDURE_CANCELED.getValue());
  }

  /**
   * Checks if is status before application opening closed.
   *
   * @param status the status
   * @return true, if is status before application opening closed
   */
  private boolean isStatusBeforeApplicationOpeningClosed(String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method isStatusBeforeApplicationOpeningClosed, Parameter: "
        + "status: {0}",
      status);

    return status.equals(TenderStatus.SUBMISSION_CREATED.getValue())
      || status.equals(TenderStatus.APPLICANTS_LIST_CREATED.getValue())
      || status.equals(TenderStatus.APPLICATION_OPENING_STARTED.getValue());
  }

  /**
   * Checks if is not BeKo closed.
   *
   * @param status the status
   * @return true, if is not be ko closed
   */
  private boolean isNotBeKoClosed(String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method isNotBeKoClosed, Parameter: "
        + "status: {0}",
      status);

    return !status.equals(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue())
      && !status.equals(TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue())
      && !status.equals(TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue());
  }


  /**
   * Checks if is after award.
   *
   * @param status the status
   * @return true, if is after award
   */
  private boolean isAfterAward(String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method isAfterAward, Parameter: "
        + "status: {0}",
      status);

    return status.equals(TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED.getValue())
      || status.equals(TenderStatus.SUBMITTENT_LIST_CREATED.getValue())
      || status.equals(TenderStatus.SUBMITTENTLIST_CHECK.getValue())
      || status.equals(TenderStatus.SUBMITTENTLIST_CHECKED.getValue())
      || status.equals(TenderStatus.OFFER_OPENING_STARTED.getValue())
      || status.equals(TenderStatus.OFFER_OPENING_CLOSED.getValue());
  }

  /**
   * Checks if is after award completed.
   *
   * @param status the status
   * @return true, if is after award completed
   */
  private boolean isAfterAwardCompleted(String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method isAfterAwardCompleted, Parameter: "
        + "status: {0}",
      status);

    return status.equals(TenderStatus.MANUAL_AWARD_COMPLETED.getValue())
      || status.equals(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue())
      || status.equals(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue())
      || status.equals(TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue())
      || status.equals(TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue())
      || status.equals(TenderStatus.AWARD_NOTICES_CREATED.getValue())
      || status.equals(TenderStatus.CONTRACT_CREATED.getValue())
      || status.equals(TenderStatus.PROCEDURE_COMPLETED.getValue())
      || status.equals(TenderStatus.PROCEDURE_CANCELED.getValue());
  }


  /**
   * Checks if is not offer opening closed.
   *
   * @param status the status
   * @return true, if is not offer opening closed
   */
  private boolean isNotOfferOpeningClosed(String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method isNotOfferOpeningClosed, Parameter: "
        + "status: {0}",
      status);

    return !status.equals(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue())
      && !status.equals(TenderStatus.MANUAL_AWARD_COMPLETED.getValue())
      && !status.equals(TenderStatus.AWARD_EVALUATION_CLOSED.getValue())
      && !status.equals(TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue())
      && !status
      .equals(TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue())
      && !status.equals(TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue())
      && !status.equals(TenderStatus.AWARD_EVALUATION_STARTED.getValue())
      && !status.equals(TenderStatus.FORMAL_AUDIT_COMPLETED.getValue())
      && !status.equals(TenderStatus.FORMAL_EXAMINATION_STARTED.getValue())
      && !status.equals(TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue())
      && !status.equals(TenderStatus.OFFER_OPENING_CLOSED.getValue());
  }


  /**
   * Calculate the report results
   *
   * @param operationReportDTO the operationReportDTO
   * @return the results
   */
  @Override
  public OperationReportResultsDTO calculateReportResults(OperationReportDTO operationReportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateReportResults, Parameter: "
        + "operationReportDTO: {0}",
      operationReportDTO);

    List<SubmissionEntity> submissionentities =
      filterSubmissionEntity(operationReportDTO, 0, 0, null, null);
    OperationReportResultsDTO results = new OperationReportResultsDTO();
    List<BigDecimal> soSumList = new ArrayList<>();
    List<BigDecimal> eSumList = new ArrayList<>();
    List<BigDecimal> fSumList = new ArrayList<>();
    for (SubmissionEntity submission : submissionentities) {
      if (isAwardNoticesCreated(submission.getStatus())) {
        List<OfferDTO> offerDTOs = offerService.getOffersBySubmission(submission.getId());
        for (OfferDTO offerDTO : offerDTOs) {
          Process process = submission.getProcess();
          if (offerDTO != null && offerDTO.getIsAwarded() != null && offerDTO.getIsAwarded()) {
            if (process.equals(Process.SELECTIVE) || process.equals(Process.OPEN)) {
              soSumList.add(nulltoZeroDecimal(offerDTO.getAmount())
                .add(nulltoZeroDecimal(offerDTO.getOperatingCostsAmount())));
            } else if (process.equals(Process.INVITATION)) {
              eSumList.add(nulltoZeroDecimal(offerDTO.getAmount())
                .add(nulltoZeroDecimal(offerDTO.getOperatingCostsAmount())));
            } else {
              fSumList.add(nulltoZeroDecimal(offerDTO.getAmount())
                .add(nulltoZeroDecimal(offerDTO.getOperatingCostsAmount())));
            }
          }
        }
      }
    }
    results.setSoSum(calculateAmountList(soSumList));
    results.seteSum(calculateAmountList(eSumList));
    results.setfSum(calculateAmountList(fSumList));
    results.setTotal(calculateTotalAmount(soSumList, eSumList, fSumList));
    results.setSearchedCriteria(getOperationReportSearchCriteria(operationReportDTO));
    return results;
  }

  /**
   * Calculate amount list.
   *
   * @param amountList the amount list
   * @return the string
   */
  private String calculateAmountList(List<BigDecimal> amountList) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateAmountList, Parameter: "
        + "amountList: {0}",
      amountList);

    BigDecimal result = amountList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    return amountForamtter(result.toString());
  }

  /**
   * Calculate total amount.
   *
   * @param soSumList the so sum list
   * @param eSumList the e sum list
   * @param fSumList the f sum list
   * @return the string
   */
  private String calculateTotalAmount(List<BigDecimal> soSumList, List<BigDecimal> eSumList,
    List<BigDecimal> fSumList) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateTotalAmount, Parameters: soSumList: {0}, "
        + "eSumList: {1}, fSumList: {2}",
      new Object[]{soSumList, eSumList, fSumList});

    List<BigDecimal> total =
      Stream.concat(Stream.concat(soSumList.stream(), eSumList.stream()), fSumList.stream())
        .collect(Collectors.toList());
    BigDecimal result = total.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    return amountForamtter(result.toString());
  }

  /**
   * Create the GeKo Excel with POI.
   *
   * @param operationReportResultsDTO the operationReportResultsDTO
   * @param selectedColumns the selectedColumns
   * @param parameters the parameters
   * @return the byte[]
   */
  private byte[] createGeKoExcel(OperationReportResultsDTO operationReportResultsDTO,
    String selectedColumns, Map<String, Object> parameters) {

    LOGGER.log(Level.CONFIG,
      "Executing method createGeKoExcel, Parameters: operationReportResultsDTO: {0}, "
        + "selectedColumns: {1}, parameters: {2}",
      new Object[]{operationReportResultsDTO, selectedColumns, parameters});

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("gekoReport");

      fillWithCalculationValues(operationReportResultsDTO, parameters);

      int currentRowNum = 0;

      // Get the maxCols details
      Map<String, Object> geKoMaxColsDetails = getGeKoMaxCols(selectedColumns);

      // The number of maxCols in the document
      int maxCols = (int) geKoMaxColsDetails.get(MAXCOLS);

      setColumnWidth(sheet, maxCols);

      currentRowNum = setDocumentTitle(sheet, workbook, currentRowNum, operationReportResultsDTO,
        geKoMaxColsDetails);

      setDocumentColumnHeaders(sheet, workbook, geKoMaxColsDetails, currentRowNum);

      insertDocumentData(sheet, workbook, geKoMaxColsDetails, currentRowNum + 1,
        operationReportResultsDTO.getGeKoResultDTOs());

      setGeKoPrintSettings(sheet);

      workbook.write(bos);
      bos.close();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return bos.toByteArray();
  }

  private void fillWithCalculationValues(OperationReportResultsDTO operationReportResultsDTO,
    Map<String, Object> parameters) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillWithCalculationValues, Parameters: operationReportResultsDTO: {0}, "
        + "parameters: {1}",
      new Object[]{operationReportResultsDTO, parameters});

    operationReportResultsDTO.setTotal((String) parameters.get(LookupValues.TOTAL_AMOUNT));
    operationReportResultsDTO.setSoSum(amountForamtter(parameters.get(LookupValues.SO_AMOUNT).toString()));
    operationReportResultsDTO.seteSum(amountForamtter(parameters.get(LookupValues.E_AMOUNT).toString()));
    operationReportResultsDTO.setfSum(amountForamtter(parameters.get(LookupValues.F_AMOUNT).toString()));
  }

  /**
   * Get GeKo max columns.
   *
   * @param selectedColumns the selectedColumns
   * @return the maxCols
   */
  private Map<String, Object> getGeKoMaxCols(String selectedColumns) {

    LOGGER.log(Level.CONFIG,
      "Executing method getGeKoMaxCols, Parameters: selectedColumns: {0}",
      selectedColumns);

    Map<String, Object> geKoMaxColsDetails = new HashMap<>();

    // default case with 7 columns
    if (selectedColumns.equals("0")) {
      geKoMaxColsDetails.put(MAXCOLS, 7);
      // adding the selected columns
    } else {
      // remove 0 from String
      selectedColumns = selectedColumns.substring(3);
      // if all columns are selected remove the unnecessary option 10
      if (selectedColumns.contains("10")) {
        // remove 10 from String
        selectedColumns = selectedColumns.substring(4);
      }

      String[] selectedColumnsSplt = selectedColumns.split(", ");
      int[] selectedColumnsInt = Arrays.stream(selectedColumnsSplt).mapToInt(Integer::parseInt)
        .toArray();

      // the real index for the column number starting counting from 0 is the current column number - 4
      for (int i = 0; i < selectedColumnsInt.length; i++) {
        selectedColumnsInt[i] -= 4;
      }

      geKoMaxColsDetails.put(MAXCOLS, 7 + selectedColumnsInt.length);
      geKoMaxColsDetails.put(SELECTED_COLS, selectedColumnsInt);
    }
    return geKoMaxColsDetails;
  }

  /**
   * Set the column width.
   *
   * @param sheet the sheet
   * @param maxCols the maxCols
   */
  private void setColumnWidth(XSSFSheet sheet, int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method setColumnWidth, Parameters: sheet: {0}, "
        + "maxCols: {1}",
      new Object[]{sheet, maxCols});

    for (int i = 0; i < maxCols; i++) {
      sheet.setColumnWidth(i, 24 * CHARACTER_WIDTH);
    }
  }

  /**
   * Set the GeKo title.
   *
   * @param sheet the sheet
   * @param workbook the workbook
   */
  private int setDocumentTitle(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    OperationReportResultsDTO operationReportResultsDTO, Map<String, Object> geKoMaxColsDetails) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentTitle, Parameters: sheet: {0}, "
        + "workbook: {1}",
      new Object[]{sheet, workbook});

    //---- FONT STYLES ----

    // Document title font
    XSSFFont titleFont = setFontStyle(workbook, SANS_SERIF, FONT_SIZE_BIG, true);
    // Document subTitle font
    XSSFFont subTitleFont = setFontStyle(workbook, SANS_SERIF, FONT_SIZE_SMALL, true);
    // Document subTitleValue font
    XSSFFont subTitleValueFont = setFontStyle(workbook, SANS_SERIF, FONT_SIZE_SMALL, false);

    // Document title style
    CellStyle titleStyle = setTitleStyle(workbook, titleFont);
    // Document subTitle style
    CellStyle subTitleStyle = setSubTitleStyle(workbook, subTitleFont);
    // Document subTitleValue style
    CellStyle subTitleValueStyle = setSubTitleValueStyle(workbook, subTitleValueFont);

    //---- DOCUMENT TITLE VALUES ----

    // The document title
    Row row = sheet.createRow(startingIndex);
    Cell cell = row.createCell(0);
    cell.setCellValue(GEKO_TITLE);
    cell.setCellStyle(titleStyle);

    // The field Zeitraum
    if (getDateSearchCriteria(operationReportResultsDTO.getStartDate(),
      operationReportResultsDTO.getEndDate()) != null) {
      String zeitraumValue = getDateSearchCriteria(operationReportResultsDTO.getStartDate(),
        operationReportResultsDTO.getEndDate());
      startingIndex++;
      row = sheet.createRow(startingIndex);

      cell = row.createCell(0);
      cell.setCellValue(LookupValues.ZEITRAUM + ":");
      cell.setCellStyle(subTitleStyle);

      cell = row.createCell(1);
      cell.setCellValue(zeitraumValue);
      cell.setCellStyle(subTitleValueStyle);
    }

    // The field Gesamttotal
    if (operationReportResultsDTO.getTotal() != null) {
      String total = operationReportResultsDTO.getTotal();
      startingIndex++;
      row = sheet.createRow(startingIndex);

      cell = row.createCell(0);
      cell.setCellValue(LookupValues.GESAMTTOTAL + LookupValues.COLON);
      cell.setCellStyle(subTitleStyle);

      cell = row.createCell(1);
      cell.setCellValue(total);
      cell.setCellStyle(subTitleValueStyle);
    }

    if (isSOAmountSelected(geKoMaxColsDetails) || isEAmountSelected(geKoMaxColsDetails)
      || isFAmountSelected(geKoMaxColsDetails)) {
      startingIndex++;
      row = sheet.createRow(startingIndex);
      int cellNum = 0;

      if (isSOAmountSelected(geKoMaxColsDetails)) {
        cell = row.createCell(cellNum);
        cell.setCellValue(LookupValues.SO_LABEL + LookupValues.COLON);
        cell.setCellStyle(subTitleStyle);
        cellNum++;

        cell = row.createCell(cellNum);
        cell.setCellValue(operationReportResultsDTO.getSoSum());
        cell.setCellStyle(subTitleValueStyle);
        cellNum++;
      }

      if (isEAmountSelected(geKoMaxColsDetails)) {
        cell = row.createCell(cellNum);
        cell.setCellValue(LookupValues.E_LABEL + LookupValues.COLON);
        cell.setCellStyle(subTitleStyle);
        cellNum++;

        cell = row.createCell(cellNum);
        cell.setCellValue(operationReportResultsDTO.geteSum());
        cell.setCellStyle(subTitleValueStyle);
        cellNum++;
      }

      if (isFAmountSelected(geKoMaxColsDetails)) {
        cell = row.createCell(cellNum);
        cell.setCellValue(LookupValues.F_LABEL + LookupValues.COLON);
        cell.setCellStyle(subTitleStyle);
        cellNum++;

        cell = row.createCell(cellNum);
        cell.setCellValue(operationReportResultsDTO.getfSum());
        cell.setCellStyle(subTitleValueStyle);
      }
    }

    // The Searched Criteria field(s)
    if (operationReportResultsDTO.getSearchedCriteria() != null) {
      Map<String, String> searchedCriteria = operationReportResultsDTO.getSearchedCriteria();
      // Every criterion takes 2 cells, one for the key and one for the value of the searchedCriteria map
      int totalCellNum = searchedCriteria.size() * 2;
      // The current cell num
      int currentCellNum = 0;
      // Get the keys of searchedCriteria map
      List<String> searchedKeys = new ArrayList<>(searchedCriteria.keySet());

      startingIndex++;
      row = sheet.createRow(startingIndex);

      for (int i = 0; i < totalCellNum; i++) {
        if (i % 2 == 0) {
          // the keys must be bold
          cell = row.createCell(i);
          cell.setCellValue(searchedKeys.get(currentCellNum) + LookupValues.COLON);
          cell.setCellStyle(subTitleStyle);
          currentCellNum++;
        } else {
          // the values must be normal text
          cell = row.createCell(i);
          cell.setCellValue(searchedCriteria.get(searchedKeys.get(currentCellNum - 1)));
          cell.setCellStyle(subTitleValueStyle);
        }
      }
    }

    return startingIndex + 2;
  }

  /**
   * Set the GeKo header column section.
   *
   * @param sheet the sheet
   * @param workbook the workbook
   * @param geKoMaxColsDetails the geKoMaxColsDetails
   * @param startingIndex the startingIndex
   */
  private void setDocumentColumnHeaders(XSSFSheet sheet,
    XSSFWorkbook workbook, Map<String, Object> geKoMaxColsDetails, int startingIndex) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentColumnHeaders, Parameters: sheet: {0}, "
        + "workbook: {1}, maxCols: {2}, startingIndex: {3}",
      new Object[]{sheet, workbook, geKoMaxColsDetails, startingIndex});

    // Document header font
    XSSFFont headerFont = setFontStyle(workbook, SANS_SERIF, FONT_SIZE_SMALL, true);

    // Document header style
    XSSFCellStyle headerStyle = setHeaderStyle(workbook, headerFont);

    Row row = sheet.createRow(startingIndex);
    row.setHeightInPoints((float) 40); // approximately 40px
    // create the default columns
    setDefaultColumnHeaders(row, headerStyle);
    // add extra columns if selected
    setSelectedColumnHeaders(geKoMaxColsDetails, row, headerStyle);
  }

  /**
   * Set the default header column section.
   *
   * @param row the row
   * @param headerStyle the headerStyle
   */
  private void setDefaultColumnHeaders(Row row, CellStyle headerStyle) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDefaultColumnHeaders, Parameters: row: {0}, "
        + "headerStyle: {1}",
      new Object[]{row, headerStyle});

    for (int i = 0; i < 7; i++) {
      Cell cell = row.createCell(i);
      cell.setCellValue(GEKO_HEADER_COLUMNS[i]);
      cell.setCellStyle(headerStyle);
    }
  }

  /**
   * Set the selected header column section if any.
   *
   * @param geKoMaxColsDetails the geKoMaxColsDetails
   * @param row the row
   * @param headerStyle the headerStyle
   */
  private void setSelectedColumnHeaders(Map<String, Object> geKoMaxColsDetails,
    Row row, CellStyle headerStyle) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSelectedColumnHeaders, Parameters: geKoMaxColsDetails: {0}, "
        + "row: {1}, headerStyle: {2}",
      new Object[]{geKoMaxColsDetails, row, headerStyle});

    // The number of maxCols in the document
    int maxCols = (int) geKoMaxColsDetails.get(MAXCOLS);

    if (maxCols > 7) {
      int[] selectedCols = (int[]) geKoMaxColsDetails.get(SELECTED_COLS);
      for (int i = 0; i < selectedCols.length; i++) {
        Cell cell = row.createCell(7 + i);
        cell.setCellValue(GEKO_HEADER_COLUMNS[selectedCols[i]]);
        cell.setCellStyle(headerStyle);
      }
    }
  }

  private boolean isSOAmountSelected(Map<String, Object> geKoMaxColsDetails) {

    LOGGER.log(Level.CONFIG,
      "Executing method isSOAmountSelected, Parameters: geKoMaxColsDetails: {0}",
      geKoMaxColsDetails);

    // The number of maxCols in the document
    int maxCols = (int) geKoMaxColsDetails.get(MAXCOLS);
    boolean isSelected = false;

    if (maxCols > 7) {
      int[] selectedCols = (int[]) geKoMaxColsDetails.get(SELECTED_COLS);
      for (int selectedCol: selectedCols) {
        if (GEKO_HEADER_COLUMNS[selectedCol].equals(LookupValues.SO_LABEL)) {
          isSelected = true;
        }
      }
    }
    return isSelected;
  }

  private boolean isEAmountSelected(Map<String, Object> geKoMaxColsDetails) {

    LOGGER.log(Level.CONFIG,
      "Executing method isEAmountSelected, Parameters: geKoMaxColsDetails: {0}",
      geKoMaxColsDetails);

    // The number of maxCols in the document
    int maxCols = (int) geKoMaxColsDetails.get(MAXCOLS);
    boolean isSelected = false;

    if (maxCols > 7) {
      int[] selectedCols = (int[]) geKoMaxColsDetails.get(SELECTED_COLS);
      for (int selectedCol: selectedCols) {
        if (GEKO_HEADER_COLUMNS[selectedCol].equals(LookupValues.E_LABEL)) {
          isSelected = true;
        }
      }
    }
    return isSelected;
  }

  private boolean isFAmountSelected(Map<String, Object> geKoMaxColsDetails) {

    LOGGER.log(Level.CONFIG,
      "Executing method isFAmountSelected, Parameters: geKoMaxColsDetails: {0}",
      geKoMaxColsDetails);

    // The number of maxCols in the document
    int maxCols = (int) geKoMaxColsDetails.get(MAXCOLS);
    boolean isSelected = false;

    if (maxCols > 7) {
      int[] selectedCols = (int[]) geKoMaxColsDetails.get(SELECTED_COLS);
      for (int selectedCol: selectedCols) {
        if (GEKO_HEADER_COLUMNS[selectedCol].equals(LookupValues.F_LABEL)) {
          isSelected = true;
        }
      }
    }
    return isSelected;
  }

  /**
   * Inserting the data to GeKo Excel document.
   *
   * @param sheet the sheet
   * @param workbook the workbook
   * @param geKoMaxColsDetails the geKoMaxColsDetails
   * @param startingIndex the startingIndex
   * @param geKoResults the geKoResults
   */
  private void insertDocumentData(XSSFSheet sheet,
    XSSFWorkbook workbook, Map<String, Object> geKoMaxColsDetails, int startingIndex,
    List<GeKoResultDTO> geKoResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertDocumentData, Parameters: sheet: {0}, "
        + "workbook: {1}, geKoMaxColsDetails: {2}, startingIndex: {3}, geKoResults: {4}",
      new Object[]{sheet, workbook, geKoMaxColsDetails, startingIndex, geKoResults});

    // Document data font
    XSSFFont dataFont = setFontStyle(workbook, SANS_SERIF, FONT_SIZE_SMALL, false);

    // Document data style
    CellStyle dataStyle = setDataStyle(workbook, dataFont);

    // i is counting the rows
    for (int i = 0; i < geKoResults.size(); i++) {
      Row row = sheet.createRow(startingIndex + i);
      // add data for default columns
      insertDefaultColumnData(i, geKoResults, row, dataStyle);
      // add data for selected columns
      insertSelectedColumnData(i, geKoMaxColsDetails, geKoResults, row, dataStyle);
    }
  }

  /**
   * Inserting the data to default columns.
   *
   * @param i the index i
   * @param geKoResults the geKoResults
   * @param row the row
   * @param dataStyle the dataStyle
   */
  private void insertDefaultColumnData(int i, List<GeKoResultDTO> geKoResults,
    Row row, CellStyle dataStyle) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertDefaultColumnData, Parameters: i: {0}, "
        + "geKoResults: {1}, row: {2}, dataStyle: {3}",
      new Object[]{i, geKoResults, row, dataStyle});

    // j is counting the default columns
    for (int j = 0; j < 7; j++) {
      Cell cell = row.createCell(j);
      cell.setCellStyle(dataStyle);

      switch (j) {
        case 0:
          if (geKoResults.get(i).getProject() != null) {
            cell.setCellValue(
              geKoResults.get(i).getProject().getDepartment().getDirectorate().getShortName());
          }
          break;
        case 1:
          if (geKoResults.get(i).getProject() != null) {
            cell.setCellValue(geKoResults.get(i).getProject().getDepartment().getName());
          }
          break;
        case 2:
          if (geKoResults.get(i).getProject() != null
            && geKoResults.get(i).getProject().getObjectName() != null) {
            String value = geKoResults.get(i).getProject().getObjectName().getValue1()
              + (geKoResults.get(i).getProject().getObjectName().getValue2() != null
              ? " " + geKoResults.get(i).getProject().getObjectName().getValue2()
              : StringUtils.EMPTY);
            cell.setCellValue(value);
          }
          break;
        case 3:
          if (geKoResults.get(i).getProject() != null) {
            cell.setCellValue(geKoResults.get(i).getProject().getProjectName());
          }
          break;
        case 4:
          if (geKoResults.get(i).getWorkType() != null) {
            String value = geKoResults.get(i).getWorkType().getValue1()
              + " " + geKoResults.get(i).getWorkType().getValue2();
            cell.setCellValue(value);
          }
          break;
        case 5:
          if (geKoResults.get(i).getDescription() != null) {
            cell.setCellValue(geKoResults.get(i).getDescription());
          }
          break;
        case 6:
          if (geKoResults.get(i).getProcess() != null) {
            String value;
            if (geKoResults.get(i).getProcess().toString().equals(OPEN)) {
              value = "O";
            } else if (geKoResults.get(i).getProcess().toString().equals(SELECTIVE)) {
              value = "S";
            } else if (geKoResults.get(i).getProcess().toString().equals(INVITATION)) {
              value = "E";
            } else {
              value = "F";
            }
            cell.setCellValue(value);
          }
          break;
        default:
          break;
      }
    }
  }

  /**
   * Inserting the data to selected columns.
   *
   * @param i the index i
   * @param geKoMaxColsDetails the geKoMaxColsDetails
   * @param geKoResults the geKoResults
   * @param row the row
   * @param dataStyle the dataStyle
   */
  private void insertSelectedColumnData(int i, Map<String, Object> geKoMaxColsDetails,
    List<GeKoResultDTO> geKoResults, Row row, CellStyle dataStyle) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertSelectedColumnData, Parameters: i: {0}, "
        + "geKoMaxColsDetails: {1}, geKoResults: {2}, row: {3}, dataStyle: {4}",
      new Object[]{i, geKoMaxColsDetails, geKoResults, row, dataStyle});

    // The number of maxCols in the document
    int maxCols = (int) geKoMaxColsDetails.get(MAXCOLS);

    if (maxCols > 7) {
      int[] selectedCols = (int[]) geKoMaxColsDetails.get(SELECTED_COLS);

      // k is counting the selected columns
      for (int k = 0; k < selectedCols.length; k++) {
        Cell cell = row.createCell(7 + k);
        cell.setCellStyle(dataStyle);

        switch (selectedCols[k]) {
          case 7:
            if (geKoResults.get(i).getGattTwo() != null) {
              String value = geKoResults.get(i).getGattTwo() ? "ja" : "nein";
              cell.setCellValue(value);
            }
            break;
          case 8:
            if (geKoResults.get(i).getPublicationDateAward() != null) {
              cell.setCellValue(
                SubmissConverter.convertToSwissDate(geKoResults.get(i).getPublicationDateAward()));
            }
            break;
          case 9:
            if (geKoResults.get(i).getPublicationDateDirectAward() != null) {
              cell.setCellValue(SubmissConverter
                .convertToSwissDate(geKoResults.get(i).getPublicationDateDirectAward()));
            }
            break;
          case 10:
            if (geKoResults.get(i).getPublicationDate() != null) {
              cell.setCellValue(
                SubmissConverter.convertToSwissDate(geKoResults.get(i).getPublicationDate()));
            }
            break;
          case 11:
            if (geKoResults.get(i).getFirstDeadline() != null) {
              cell.setCellValue(
                SubmissConverter.convertToSwissDate(geKoResults.get(i).getFirstDeadline()));
            }
            break;
          case 12:
            if (geKoResults.get(i).getApplicationOpeningDate() != null) {
              cell.setCellValue(SubmissConverter
                .convertToSwissDate(geKoResults.get(i).getApplicationOpeningDate()));
            }
            break;
          case 13:
            if (geKoResults.get(i).getSecondDeadline() != null) {
              cell.setCellValue(
                SubmissConverter.convertToSwissDate(geKoResults.get(i).getSecondDeadline()));
            }
            break;
          case 14:
            if (geKoResults.get(i).getOfferOpeningDate() != null) {
              cell.setCellValue(
                SubmissConverter.convertToSwissDate(geKoResults.get(i).getOfferOpeningDate()));
            }
            break;
          case 15:
            if (geKoResults.get(i).getFirstLevelavailableDate() != null) {
              cell.setCellValue(SubmissConverter
                .convertToSwissDate(geKoResults.get(i).getFirstLevelavailableDate()));
            }
            break;
          case 16:
            if (geKoResults.get(i).getCommissionProcurementProposalDate() != null) {
              cell.setCellValue(SubmissConverter
                .convertToSwissDate(geKoResults.get(i).getCommissionProcurementProposalDate()));
            }
            break;
          case 17:
            if (geKoResults.get(i).getLevelavailableDate() != null) {
              cell.setCellValue(
                SubmissConverter.convertToSwissDate(geKoResults.get(i).getLevelavailableDate()));
            }
            break;
          case 18:
            if (geKoResults.get(i).getSubmittentName() != null) {
              cell.setCellValue(geKoResults.get(i).getSubmittentName());
            }
            break;
          case 19:
            if (geKoResults.get(i).getSoSum() != null) {
              cell.setCellValue(geKoResults.get(i).getSoSum());
            }
            break;
          case 20:
            if (geKoResults.get(i).geteSum() != null) {
              cell.setCellValue(geKoResults.get(i).geteSum());
            }
            break;
          case 21:
            if (geKoResults.get(i).getfSum() != null) {
              cell.setCellValue(geKoResults.get(i).getfSum());
            }
            break;
          case 22:
            if (geKoResults.get(i).getProject() != null) {
              String value = geKoResults.get(i).getProject().getDepartment().isInternal()
                ? INTERN : EXTERN;
              cell.setCellValue(value);
            }
            break;
          case 23:
            if (geKoResults.get(i).getCommentField() != null) {
              cell.setCellValue(geKoResults.get(i).getCommentField());
            }
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   * Set the font style.
   *
   * @param workbook the workbook
   * @param fontName the fontName
   * @param fontSize the fontSize
   * @param isBold isBold
   * @return the font style
   */
  private XSSFFont setFontStyle(XSSFWorkbook workbook, String fontName, int fontSize,
    boolean isBold) {

    LOGGER.log(Level.CONFIG,
      "Executing method setFontStyle, Parameters: workbook: {0}, "
        + "fontName: {1}, fontSize: {2}, isBold: {3}",
      new Object[]{workbook, fontName, fontSize, isBold});

    XSSFFont fontStyle = workbook.createFont();
    fontStyle.setFontName(fontName);
    fontStyle.setFontHeight(fontSize);
    fontStyle.setBold(isBold);
    fontStyle.setColor(IndexedColors.BLACK.index);

    return fontStyle;
  }

  /**
   * Set the title style.
   *
   * @param workbook the workbook
   * @param titleFont the titleFont
   * @return the titleStyle
   */
  private CellStyle setTitleStyle(XSSFWorkbook workbook, XSSFFont titleFont) {

    LOGGER.log(Level.CONFIG,
      "Executing method setTitleStyle, Parameters: workbook: {0}, "
        + "titleFont: {1}",
      new Object[]{workbook, titleFont});

    CellStyle titleStyle = workbook.createCellStyle();
    titleStyle.setLocked(true);
    titleStyle.setAlignment(HorizontalAlignment.LEFT);
    titleStyle.setFont(titleFont);

    return titleStyle;
  }

  /**
   * Set the subTitle style.
   *
   * @param workbook the workbook
   * @param subTitleFont the subTitleFont
   * @return the subTitleStyle
   */
  private CellStyle setSubTitleStyle(XSSFWorkbook workbook, XSSFFont subTitleFont) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSubTitleStyle, Parameters: workbook: {0}, "
        + "subTitleFont: {1}",
      new Object[]{workbook, subTitleFont});

    CellStyle subTitleStyle = workbook.createCellStyle();
    subTitleStyle.setLocked(true);
    subTitleStyle.setAlignment(HorizontalAlignment.LEFT);
    subTitleStyle.setVerticalAlignment(VerticalAlignment.TOP);
    subTitleStyle.setWrapText(true);
    subTitleStyle.setFont(subTitleFont);

    return subTitleStyle;
  }

  /**
   * Set the subTitleValue style.
   *
   * @param workbook the workbook
   * @param subTitleValueFont the subTitleValueFont
   * @return the subTitleValueStyle
   */
  private CellStyle setSubTitleValueStyle(XSSFWorkbook workbook, XSSFFont subTitleValueFont) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSubTitleValueStyle, Parameters: workbook: {0}, "
        + "subTitleValueFont: {1}",
      new Object[]{workbook, subTitleValueFont});

    CellStyle subTitleValueStyle = workbook.createCellStyle();
    subTitleValueStyle.setLocked(true);
    subTitleValueStyle.setAlignment(HorizontalAlignment.LEFT);
    subTitleValueStyle.setVerticalAlignment(VerticalAlignment.TOP);
    subTitleValueStyle.setWrapText(true);
    subTitleValueStyle.setFont(subTitleValueFont);

    return subTitleValueStyle;
  }

  /**
   * Set the header style.
   *
   * @param workbook the workbook
   * @param headerFont the headerFont
   * @return the headerStyle
   */
  private XSSFCellStyle setHeaderStyle(XSSFWorkbook workbook, XSSFFont headerFont) {

    LOGGER.log(Level.CONFIG,
      "Executing method setHeaderStyle, Parameters: workbook: {0}, "
        + "headerFont: {1}",
      new Object[]{workbook, headerFont});

    XSSFCellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setLocked(true);
    headerStyle.setAlignment(HorizontalAlignment.LEFT);
    headerStyle.setWrapText(true);
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(235,241,222)));
    headerStyle.setFont(headerFont);

    return headerStyle;
  }

  /**
   * Set the data style.
   *
   * @param workbook the workbook
   * @param dataFont the dataFont
   * @return the dataStyle
   */
  private CellStyle setDataStyle(XSSFWorkbook workbook, XSSFFont dataFont) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDataStyle, Parameters: workbook: {0}, "
        + "dataFont: {1}",
      new Object[]{workbook, dataFont});

    CellStyle dataStyle = workbook.createCellStyle();
    dataStyle.setLocked(false);
    dataStyle.setAlignment(HorizontalAlignment.LEFT);
    dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
    dataStyle.setWrapText(true);
    dataStyle.setFont(dataFont);

    return dataStyle;
  }

  /**
   * Set the print settings for GeKo.
   *
   * @param sheet the sheet
   */
  private void setGeKoPrintSettings(XSSFSheet sheet) {

    LOGGER.log(Level.CONFIG,
      "Executing method setGeKoPrintSettings, Parameters: sheet: {0}",
      sheet);

    sheet.setFitToPage(true);
    PrintSetup ps = sheet.getPrintSetup();
    ps.setLandscape(true);
    ps.setPaperSize(PrintSetup.A3_PAPERSIZE);
    ps.setFitWidth((short) 1);
    ps.setFitHeight((short) 0);
  }

  @Override
  public void operationReportSecurityCheck() {

    LOGGER.log(Level.CONFIG, "Executing method operationReportSecurityCheck");

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.GENERATE_OPERATIONS_REPORT.getValue(), null);
  }
}

