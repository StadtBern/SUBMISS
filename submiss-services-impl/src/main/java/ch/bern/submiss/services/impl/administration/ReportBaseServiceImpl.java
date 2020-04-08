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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ch.bern.submiss.services.api.administration.ReportBaseService;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ReportBaseDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.model.DepartmentEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.QDepartmentEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QJointVentureEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;
import ch.bern.submiss.services.impl.model.QSubmissionAwardInfoEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.QTenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;

/**
 * The Class ReportBaseServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {ReportBaseService.class})
@Singleton
public class ReportBaseServiceImpl extends BaseService implements ReportBaseService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(ReportBaseServiceImpl.class.getName());

  /**
   * The version service.
   */
  @OsgiService
  @Inject
  protected VersionService versionService;

  /**
   * The document service.
   */
  @OsgiService
  @Inject
  protected DocumentService documentService;

  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;

  /**
   * The q master list value entity.
   */
  QMasterListValueEntity qMasterListValueEntity = QMasterListValueEntity.masterListValueEntity;

  /**
   * The submission.
   */
  QSubmissionEntity submission = QSubmissionEntity.submissionEntity;

  /**
   * The project.
   */
  QProjectEntity project = QProjectEntity.projectEntity;

  /**
   * The object name.
   */
  QMasterListValueEntity objectName = QMasterListValueEntity.masterListValueEntity;

  /**
   * The object name history.
   */
  QMasterListValueHistoryEntity objectNameHistory =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;

  /**
   * The q department entity.
   */
  QDepartmentEntity qDepartmentEntity = QDepartmentEntity.departmentEntity;

  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;

  /**
   * The q directorate history entity.
   */
  QDirectorateHistoryEntity qDirectorateHistoryEntity =
    QDirectorateHistoryEntity.directorateHistoryEntity;

  /**
   * The q joint venture entity.
   */
  QJointVentureEntity qJointVentureEntity = QJointVentureEntity.jointVentureEntity;

  /**
   * The q submission award info entity.
   */
  QSubmissionAwardInfoEntity qSubmissionAwardInfoEntity =
    QSubmissionAwardInfoEntity.submissionAwardInfoEntity;

  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;

  /**
   * The q tender status history entity.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity =
    QTenderStatusHistoryEntity.tenderStatusHistoryEntity;

  /**
   * Gets the submission ids.
   *
   * @param reportBaseDTO the report base DTO
   * @return the submission ids
   */
  public List<String> getSubmissionIds(ReportBaseDTO reportBaseDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionIds, Parameters: reportBaseDTO: {0}",
      reportBaseDTO);

    return new JPAQueryFactory(em).select(qTenderStatusHistoryEntity.tenderId.id)
      .from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.onDate
        .in(JPAExpressions.select(qTenderStatusHistoryEntity.onDate.max())
          .from(qTenderStatusHistoryEntity).groupBy(qTenderStatusHistoryEntity.tenderId)))
      .fetch();
  }

  /**
   * Gets the where clause.
   *
   * @param qSubmissionEntity the q submission entity
   * @param reportBaseDTO the report base DTO
   * @param completedSubmissionsIDs the completed submissions I ds
   * @return the where clause
   */
  public BooleanBuilder getWhereClause(QSubmissionEntity qSubmissionEntity,
    ReportBaseDTO reportBaseDTO, List<String> completedSubmissionsIDs) {

    LOGGER.log(Level.CONFIG,
      "Executing method getWhereClause, Parameters: qSubmissionEntity: {0}, "
        + "reportBaseDTO: {1}, completedSubmissionsIDs: {2}",
      new Object[]{qSubmissionEntity, reportBaseDTO, completedSubmissionsIDs});

    BooleanBuilder whereClause = new BooleanBuilder();

    security.securityCheckProjectSearch(getUser(), project, qSubmissionEntity, whereClause);

    whereClause.and(qSubmissionEntity.id.in(completedSubmissionsIDs));

    if (reportBaseDTO.getObjects() != null
      && CollectionUtils.isNotEmpty(reportBaseDTO.getObjects())) {
      List<String> objectHistoryMasterListValuesIDs =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.masterListValueId.id)
          .from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.id.in(reportBaseDTO.getObjects())).fetch();

      List<String> objectsIDs = new JPAQueryFactory(em).select(objectName.id).from(objectName)
        .where(objectName.id.in(objectHistoryMasterListValuesIDs)).fetch();
      whereClause.and(qSubmissionEntity.project.objectName.id.in(objectsIDs));
    }
    if (checkIfNotEmpty(reportBaseDTO.getProjects())) {
      whereClause.and(qSubmissionEntity.project.id.in(reportBaseDTO.getProjects()));
    }
    if (CollectionUtils.isNotEmpty(reportBaseDTO.getWorkTypes())) {
      List<String> workTypeHistoryIDs = new ArrayList<>();
      for (MasterListValueHistoryDTO workTypeDTO : reportBaseDTO.getWorkTypes()) {
        workTypeHistoryIDs.add(workTypeDTO.getId());
      }
      List<String> workTypeHistoryEntitiesIDs =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.masterListValueId.id)
          .from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.id.in(workTypeHistoryIDs)).fetch();
      List<MasterListValueEntity> workTypeEntities =
        new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(qMasterListValueEntity.id.in(workTypeHistoryEntitiesIDs)).fetch();
      whereClause.and(qSubmissionEntity.workType.in(workTypeEntities));
    }
    if (checkIfNotEmpty(reportBaseDTO.getProcedures())) {
      List<Process> procedures = new ArrayList<>();
      for (String procedure : reportBaseDTO.getProcedures()) {
        procedures.add(getProcedure(procedure));
      }
      whereClause.and(qSubmissionEntity.process.in(procedures));
    }
    if (checkIfNotEmpty(reportBaseDTO.getDirectorates())
      && !checkIfNotEmpty(reportBaseDTO.getDepartments())) {
      //DepartmentHistory table change
      List<String> directorateIds = new JPAQueryFactory(em)
        .select(qDirectorateHistoryEntity.directorateId.id).from(qDirectorateHistoryEntity)
        .where(qDirectorateHistoryEntity.id.in(reportBaseDTO.getDirectorates()))
        .fetch();

      List<String> departmentIDs = new JPAQueryFactory(em)
        .select(qDepartmentHistoryEntity.departmentId.id).from(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.directorateEnity.id.in(directorateIds))
        .fetch();
      List<DepartmentEntity> departmentEntities = new JPAQueryFactory(em).select(qDepartmentEntity)
        .from(qDepartmentEntity).where(qDepartmentEntity.id.in(departmentIDs)).fetch();
      whereClause.and(qSubmissionEntity.project.department.in(departmentEntities));
    } else if (checkIfNotEmpty(reportBaseDTO.getDepartments())) {
      List<String> departmentIDs = new JPAQueryFactory(em)
        .select(qDepartmentHistoryEntity.departmentId.id).from(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.id.in(reportBaseDTO.getDepartments())).fetch();
      List<DepartmentEntity> departmentEntities = new JPAQueryFactory(em).select(qDepartmentEntity)
        .from(qDepartmentEntity).where(qDepartmentEntity.id.in(departmentIDs)).fetch();
      whereClause.and(qSubmissionEntity.project.department.in(departmentEntities));
    }
    return whereClause;
  }

  /**
   * Check if not empty.
   *
   * @param list the list
   * @return true, if successful
   */
  public boolean checkIfNotEmpty(List<String> list) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkIfNotEmpty, Parameters: list: {0}",
      list);

    return list != null && CollectionUtils.isNotEmpty(list);
  }

  /**
   * Filter.
   *
   * @param reportBaseDTO the report base DTO
   * @return the list
   */
  public List<OfferEntity> filter(ReportBaseDTO reportBaseDTO) {

    LOGGER.log(Level.CONFIG, "Executing method filter, Parameters: reportBaseDTO: {0}",
      reportBaseDTO);

    JPAQuery<OfferEntity> query = new JPAQuery<>(em);
    query.select(qOfferEntity).from(qOfferEntity)
      .rightJoin(qOfferEntity.submittent, qSubmittentEntity)
      .rightJoin(qSubmittentEntity.submissionId, submission)
      .rightJoin(submission.project, project).rightJoin(submission.project.objectName, objectName)
      .leftJoin(objectName.masterListValueHistory, objectNameHistory)
      .leftJoin(submission.project.department, qDepartmentEntity)
      .leftJoin(qDepartmentEntity.department, qDepartmentHistoryEntity)
      .where(getWhereClause(submission, reportBaseDTO, getSubmissionIds(reportBaseDTO)));
    return query.fetch();
  }

  /**
   * Filter submission entity.
   *
   * @param reportBaseDTO the report base DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return the list
   */
  public List<SubmissionEntity> filterSubmissionEntity(ReportBaseDTO reportBaseDTO, int page, int pageItems,
      String sortColumn, String sortType) {

    LOGGER.log(Level.CONFIG,
      "Executing method filterSubmissionEntity, Parameters: reportBaseDTO: {0}, "
        + "page: {1}, pageItems: {2}, sortColumn: {3}, sortType: {4}",
      new Object[]{reportBaseDTO, page, pageItems, sortColumn, sortType});

    JPAQuery<SubmissionEntity> query = createQuery(reportBaseDTO, page, pageItems, sortColumn, sortType);
    return query.fetch();
  }

  /**
   * Creates the query.
   *
   * @param reportBaseDTO the report base DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return the JPA query
   */
  private JPAQuery<SubmissionEntity> createQuery(ReportBaseDTO reportBaseDTO, int page, int pageItems,
      String sortColumn, String sortType) {

    LOGGER.log(Level.CONFIG,
      "Executing method createQuery, Parameters: reportBaseDTO: {0}, page: {1}, "
        + "pageItems: {2}, sortColumn: {3}, sortType: {4}",
      new Object[]{reportBaseDTO, pageItems, sortColumn, sortType});

    JPAQuery<SubmissionEntity> query = new JPAQuery<>(em);
    if (pageItems == -1 || pageItems == 0) {
      query.select(submission).from(submission).rightJoin(submission.project, project)
      .rightJoin(submission.project.objectName, objectName)
      .rightJoin(objectName.masterListValueHistory, objectNameHistory)
      .rightJoin(submission.project.department, qDepartmentEntity)
      .leftJoin(qDepartmentEntity.department, qDepartmentHistoryEntity)
      .leftJoin(qDepartmentHistoryEntity.directorateEnity.directorate, qDirectorateHistoryEntity)
      .where(objectNameHistory.toDate.isNull(), qDepartmentHistoryEntity.toDate.isNull(),
        qDirectorateHistoryEntity.toDate.isNull(),
        getWhereClause(submission, reportBaseDTO, getSubmissionIds(reportBaseDTO)));
    } else {
      query.select(submission).from(submission).rightJoin(submission.project, project)
          .rightJoin(submission.project.objectName, objectName)
          .rightJoin(objectName.masterListValueHistory, objectNameHistory)
          .rightJoin(submission.project.department, qDepartmentEntity)
          .leftJoin(qDepartmentEntity.department, qDepartmentHistoryEntity)
          .leftJoin(qDepartmentHistoryEntity.directorateEnity.directorate,
              qDirectorateHistoryEntity)
          .where(objectNameHistory.toDate.isNull(), qDepartmentHistoryEntity.toDate.isNull(),
              qDirectorateHistoryEntity.toDate.isNull(),
              getWhereClause(submission, reportBaseDTO, getSubmissionIds(reportBaseDTO)))
          .offset((page - 1) * pageItems).limit(pageItems);
    }

    if (sortColumn != null) {
      if (sortColumn.equals("id")) {
        query.orderBy(sortType.equals("asc") ? submission.id.asc() : submission.id.desc());
      } else if (sortColumn.equals("directorate")) {
        query.orderBy(sortType.equals("asc") ? qDirectorateHistoryEntity.name.asc()
            : qDirectorateHistoryEntity.name.desc());
      } else if (sortColumn.equals("department")) {
        query.orderBy(sortType.equals("asc") ? project.department.department.any().name.asc()
            : project.department.department.any().name.desc());
      } else if (sortColumn.equals("objectName")) {
        query.orderBy(sortType.equals("asc") ? objectNameHistory.value1.asc()
            : objectNameHistory.value1.desc());
      } else if (sortColumn.equals("projectName")) {
        query.orderBy(
            sortType.equals("asc") ? project.projectName.asc() : project.projectName.desc());
      } else if (sortColumn.equals("workType")) {
        query.orderBy(
            sortType.equals("asc") ? submission.workType.masterListValueHistory.any().value1.asc()
                : submission.workType.masterListValueHistory.any().value1.desc());
      } else if (sortColumn.equals("process")) {
        query
            .orderBy(sortType.equals("asc") ? submission.process.asc() : submission.process.desc());
      } else if (sortColumn.equals("gattTwo")) {
        query
            .orderBy(sortType.equals("asc") ? submission.gattTwo.asc() : submission.gattTwo.desc());
      } else if (sortColumn.equals("publicationDateAward")) {
        query.orderBy(sortType.equals("asc") ? submission.publicationDateAward.asc()
            : submission.publicationDateAward.desc());
      } else if (sortColumn.equals("publicationDateDirectAward")) {
        query.orderBy(sortType.equals("asc") ? submission.publicationDateDirectAward.asc()
            : submission.publicationDateDirectAward.desc());
      } else if (sortColumn.equals("publicationDate")) {
        query.orderBy(sortType.equals("asc") ? submission.publicationDate.asc()
            : submission.publicationDate.desc());
      } else if (sortColumn.equals("firstDeadline")) {
        query.orderBy(sortType.equals("asc") ? submission.firstDeadline.asc()
            : submission.firstDeadline.desc());
      } else if (sortColumn.equals("applicationOpeningDate")) {
        query.orderBy(sortType.equals("asc") ? submission.applicationOpeningDate.asc()
            : submission.applicationOpeningDate.desc());
      } else if (sortColumn.equals("secondDeadline")) {
        query.orderBy(sortType.equals("asc") ? submission.secondDeadline.asc()
            : submission.secondDeadline.desc());
      } else if (sortColumn.equals("offerOpeningDate")) {
        query.orderBy(sortType.equals("asc") ? submission.offerOpeningDate.asc()
            : submission.offerOpeningDate.desc());
      } else if (sortColumn.equals("firstLevelavailableDate")) {
        query.orderBy(sortType.equals("asc") ? submission.submissionCancel.any().availableDate.asc()
            : submission.submissionCancel.any().availableDate.desc());
      } else if (sortColumn.equals("commissionProcurementProposalDate")) {
        query.orderBy(sortType.equals("asc") ? submission.commissionProcurementProposalDate.asc()
            : submission.commissionProcurementProposalDate.desc());
      } else if (sortColumn.equals("levelavailableDate")) {
        query.orderBy(sortType.equals("asc") ? submission.submissionCancel.any().availableDate.asc()
            : submission.submissionCancel.any().availableDate.desc());
      } else if (sortColumn.equals("internal")) {
        query.orderBy(
            sortType.equals("asc") ? submission.project.department.department.any().internal.asc()
                : submission.project.department.department.any().internal.desc());
      } else if (sortColumn.equals("description")) {
        query.orderBy(
            sortType.equals("asc") ? submission.description.asc() : submission.description.desc());
      } else if (sortColumn.equals("submittentName")) {
        query.orderBy(sortType.equals("asc")
            ? new CaseBuilder().when(submission.submittents.any().offer.isAwarded.isTrue())
                .then(submission.submittents.any().companyId.companyName).otherwise("").asc()
            : new CaseBuilder().when(submission.submittents.any().offer.isAwarded.isTrue())
                .then(submission.submittents.any().companyId.companyName).otherwise("").desc());
      }
    }
    return query;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.ReportBaseService#getReportSearchCriteria(ch.bern.
   * submiss.services.api.dto.ReportBaseDTO)
   */
  @Override
  public Map<String, String> getReportSearchCriteria(ReportBaseDTO reportBaseDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportSearchCriteria, Parameters: reportBaseDTO: {0}",
      reportBaseDTO);

    Map<String, String> searchCriteriaMap = new HashMap<>();
    if (checkIfNotEmpty(reportBaseDTO.getObjects())) {
      List<MasterListValueHistoryEntity> searchedObjectsList = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.id.in(reportBaseDTO.getObjects()))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();
      List<String> objectsNameList = new ArrayList<>();
      for (MasterListValueHistoryEntity object : searchedObjectsList) {
        objectsNameList.add(object.getValue1());
      }
      String searchedObjects = StringUtils.join(objectsNameList, " / ");
      searchCriteriaMap.put("Objekt", searchedObjects);
    }
    if (checkIfNotEmpty(reportBaseDTO.getProjects())) {
      List<String> searchedProjectsList = new JPAQueryFactory(em).select(project.projectName)
        .from(project).where(project.id.in(reportBaseDTO.getProjects()))
        .orderBy(project.projectName.asc()).fetch();
      String searchedProjects = StringUtils.join(searchedProjectsList, " / ");
      searchCriteriaMap.put("Projektname", searchedProjects);
    }
    if (CollectionUtils.isNotEmpty(reportBaseDTO.getWorkTypes())) {
      List<String> workTypeList = new ArrayList<>();
      for (MasterListValueHistoryDTO workTypeDTO : reportBaseDTO.getWorkTypes()) {
        workTypeList.add(getWorkTypeName(workTypeDTO));
      }
      String searchedWorkedTypes = StringUtils.join(workTypeList, " / ");
      searchCriteriaMap.put("Arbeitsgattung", searchedWorkedTypes);
    }
    if (checkIfNotEmpty(reportBaseDTO.getProcedures())) {
      List<String> searchedProceduresList = new ArrayList<>();
      for (String procedure : reportBaseDTO.getProcedures()) {
        searchedProceduresList.add(getProcedureName(procedure));
      }
      String searchedProcedures = StringUtils.join(searchedProceduresList, " / ");
      searchCriteriaMap.put("Verfahren", searchedProcedures);
    }
    if (checkIfNotEmpty(reportBaseDTO.getDirectorates())) {
      List<String> searchedDirectoratesList = new JPAQueryFactory(em)
        .select(qDirectorateHistoryEntity.name).from(qDirectorateHistoryEntity)
        .where(qDirectorateHistoryEntity.id.in(reportBaseDTO.getDirectorates())).fetch();
      String searchedDirectorates = StringUtils.join(searchedDirectoratesList, " / ");
      searchCriteriaMap.put("Direktion", searchedDirectorates);
    }
    if (checkIfNotEmpty(reportBaseDTO.getDepartments())) {
      List<String> searchedDepartmentsList = new JPAQueryFactory(em)
        .select(qDepartmentHistoryEntity.name).from(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.id.in(reportBaseDTO.getDepartments())).fetch();
      String searchedDepartments = StringUtils.join(searchedDepartmentsList, " / ");
      searchCriteriaMap.put("Abteilung", searchedDepartments);
    }
    return searchCriteriaMap;
  }

  /**
   * Gets the work type name.
   *
   * @param workTypeDTO the work type DTO
   * @return the work type name
   */
  private String getWorkTypeName(MasterListValueHistoryDTO workTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getWorkTypeName, Parameters: workTypeDTO: {0}",
      workTypeDTO);

    StringBuilder workTypeName = new StringBuilder();
    workTypeName.append(workTypeDTO.getValue1());
    if (workTypeDTO.getValue2() != null && !StringUtils.isEmpty(workTypeDTO.getValue2())) {
      workTypeName.append(" " + workTypeDTO.getValue2());
    }
    return workTypeName.toString();
  }

  /**
   * Finds and returns the Process Name in German.
   *
   * @param procedure the procedure
   * @return the procedure name
   */
  private String getProcedureName(String procedure) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProcedureName, Parameters: procedure: {0}",
      procedure);

    String processName = StringUtils.EMPTY;
    switch (procedure) {
      case "SELECTIVE":
        processName = "Selektiv";
        break;
      case "OPEN":
        processName = "Offen";
        break;
      case "INVITATION":
        processName = "Einladung";
        break;
      case "NEGOTIATED_PROCEDURE":
        processName = "Freihändig";
        break;
      case "NEGOTIATED_PROCEDURE_WITH_COMPETITION":
        processName = "Freihändig mit Konkurrenz";
        break;
      default:
        break;
    }
    return processName;
  }

  /**
   * Finds and returns the Process.
   *
   * @param procedure the procedure
   * @return the procedure
   */
  private Process getProcedure(String procedure) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProcedure, Parameters: procedure: {0}",
      procedure);

    Process process = null;
    switch (procedure) {
      case "SELECTIVE":
        process = Process.SELECTIVE;
        break;
      case "OPEN":
        process = Process.OPEN;
        break;
      case "INVITATION":
        process = Process.INVITATION;
        break;
      case "NEGOTIATED_PROCEDURE":
        process = Process.NEGOTIATED_PROCEDURE;
        break;
      case "NEGOTIATED_PROCEDURE_WITH_COMPETITION":
        process = Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION;
        break;
      default:
        break;
    }
    return process;
  }

  /**
   * Filter count.
   *
   * @param reportBaseDTO the report base DTO
   * @return the long
   */
  public Long filterCount(ReportBaseDTO reportBaseDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method filterCount, Parameters: reportBaseDTO: {0}",
      reportBaseDTO);

    JPAQuery<SubmissionEntity> query = createQuery(reportBaseDTO, 0, 0, null, null);
    return query.fetchCount();
  }

  /**
   * Getting the Template of the report.
   *
   * @param filename the filename
   * @return the report template
   */
  public InputStream getReportTemplate(String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportTemplate, Parameters: filename: {0}",
      filename);

    InputStream inputStream = null;
    HashMap<String, String> attributesMap = new HashMap<>();
    List<NodeDTO> templateList = null;
    attributesMap.put(LookupValues.REPORT_TEMPLATE_ATTRIBUTE_NAME, filename);
    templateList =
      documentService.getNodeByAttributes(LookupValues.REPORT_TEMPLATE_FOLDER_ID, attributesMap);
    if (!templateList.isEmpty()) {
      inputStream =
        new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));
    }
    return inputStream;
  }
}
