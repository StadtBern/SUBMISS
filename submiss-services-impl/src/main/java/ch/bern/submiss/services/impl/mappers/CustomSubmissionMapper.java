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

package ch.bern.submiss.services.impl.mappers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.google.common.collect.Table;

import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.LegalHearingTerminateDTO;
import ch.bern.submiss.services.api.dto.MasterListValueDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissionCancelDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.model.LegalHearingTerminateEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.SubmissionCancelEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;
import ch.bern.submiss.services.impl.model.TenderStatusHistoryEntity;

/**
 * Mapper for Submission.
 */
@Mapper(uses = {ProjectMapper.class,CustomProjectMapper.class, CompanyMapper.class, MasterListValueHistoryMapper.class,
    MasterListValueMapper.class, SubmittentDTOMapper.class})
public abstract class CustomSubmissionMapper {
	 
  
  
  
	
  /** The project mapper. */
  private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
  
  private final CustomProjectMapper customProjectMapper = Mappers.getMapper(CustomProjectMapper.class);

  /** The company mapper. */
  private final CompanyMapper companyMapper = Mappers.getMapper(CompanyMapper.class);

  /** The master list value mapper. */
  private final MasterListValueMapper masterListValueMapper =
      Mappers.getMapper(MasterListValueMapper.class);

  /** The Constant INSTANCE. */
  public static final CustomSubmissionMapper INSTANCE = Mappers.getMapper(CustomSubmissionMapper.class);

  /**
   * To submission.
   *
   * @param dto the dto
   * @return the submission entity
   */
  public SubmissionEntity toCustomSubmission(SubmissionDTO dto) {
    if (dto == null) {
      return null;
    }

    SubmissionEntity submissionEntity = new SubmissionEntity();

    submissionEntity
        .setWorkType(masterListValueMapper.toMasterListValue(dtoWorkTypeMasterListValueId(dto)));
    submissionEntity.setReasonFreeAward(
        masterListValueMapper.toMasterListValue(dtoReasonFreeAwardMasterListValueId(dto)));
    submissionEntity.setProcessType(
        masterListValueMapper.toMasterListValue(dtoProcessTypeMasterListValueId(dto)));
    submissionEntity
        .setProcedure(masterListValueMapper.toMasterListValue(dtoProcedureMasterListValueId(dto)));
    submissionEntity.setId(dto.getId());
    submissionEntity.setProject(projectMapper.toProject(dto.getProject()));
    submissionEntity.setDescription(dto.getDescription());
    submissionEntity.setProcess(dto.getProcess());
    submissionEntity.setCostEstimate(dto.getCostEstimate());
    submissionEntity.setGattTwo(dto.getGattTwo());
    submissionEntity.setPublicationDate(dto.getPublicationDate());
    submissionEntity.setPublicationDateDirectAward(dto.getPublicationDateDirectAward());
    submissionEntity.setPublicationDateAward(dto.getPublicationDateAward());
    submissionEntity.setPmDepartmentName(dto.getPmDepartmentName());
    submissionEntity.setPmExternal(companyMapper.toCompany(dto.getPmExternal()));
    submissionEntity.setIsLocked(dto.getIsLocked());
    submissionEntity.setConstructionPermit(dto.getConstructionPermit());
    submissionEntity.setLoanApproval(dto.getLoanApproval());
    submissionEntity.setFirstDeadline(dto.getFirstDeadline());
    submissionEntity.setSecondDeadline(dto.getSecondDeadline());
    submissionEntity.setApplicationOpeningDate(dto.getApplicationOpeningDate());
    submissionEntity.setOfferOpeningDate(dto.getOfferOpeningDate());
    submissionEntity.setFirstLoggedBy(dto.getFirstLoggedBy());
    submissionEntity.setSecondLoggedBy(dto.getSecondLoggedBy());
    submissionEntity.setNotes(dto.getNotes());
    
    submissionEntity.setIsServiceTender(dto.getIsServiceTender());
    submissionEntity.setIsGekoEntry(dto.getIsGekoEntry());


    submissionEntity.setSubmittents(dtoToSubmittentEntity(dto));
    submissionEntity.setMaxGrade(dto.getMaxGrade());
    submissionEntity.setMinGrade(dto.getMinGrade());
    submissionEntity.setAboveThreshold(dto.getAboveThreshold());
    submissionEntity.setAwardMaxGrade(dto.getAwardMaxGrade());
    submissionEntity.setAwardMinGrade(dto.getAwardMinGrade());
    submissionEntity
        .setCommissionProcurementProposalBusiness(dto.getCommissionProcurementProposalBusiness());
    submissionEntity
        .setCommissionProcurementProposalDate(dto.getCommissionProcurementProposalDate());
    submissionEntity
        .setCommissionProcurementProposalObject(dto.getCommissionProcurementProposalObject());
    submissionEntity.setCommissionProcurementProposalPreRemarks(
        dto.getCommissionProcurementProposalPreRemarks());
    submissionEntity.setCommissionProcurementProposalReservation(
        dto.getCommissionProcurementProposalReservation());
    submissionEntity.setCommissionProcurementProposalSuitabilityAuditDropdown(
        dto.getCommissionProcurementProposalSuitabilityAuditDropdown());
    submissionEntity.setCommissionProcurementProposalSuitabilityAuditText(
        dto.getCommissionProcurementProposalSuitabilityAuditText());
    submissionEntity.setCommissionProcurementProposalReasonGiven(
        dto.getCommissionProcurementProposalReasonGiven());
    submissionEntity.setAddedAwardRecipients(dto.getAddedAwardRecipients());
    submissionEntity.setEvaluationThrough(dto.getEvaluationThrough());
    submissionEntity.setOperatingCostFormula(
        masterListValueMapper.toMasterListValue(dtoOperatingCostFormulaMasterListValueId(dto)));
    submissionEntity.setPriceFormula(
        masterListValueMapper.toMasterListValue(dtoPriceFormulaMasterListValueId(dto)));
    submissionEntity.setCommissionProcurementDecisionRecommendation(
        dto.getCommissionProcurementDecisionRecommendation());
    submissionEntity.setSubmissionCancel(dtoToSubmissionCancelEntity(dto));

    submissionEntity.setIsPmDepartmentNameUpdated(dto.getIsPmDepartmentNameUpdated());
    submissionEntity.setIsPmExternalUpdated(dto.getIsPmExternalUpdated());
    submissionEntity.setIsProcedureUpdated(dto.getIsProcedureUpdated());
    submissionEntity.setIsGattTwoUpdated(dto.getIsGattTwoUpdated());
    submissionEntity.setCreatedOn(dto.getCreatedOn());
    submissionEntity.setCreatedBy(dto.getCreatedBy());
    submissionEntity.setLegalHearingTerminate(dtoToLegalHearingTerminateEntity(dto));
    submissionEntity.setCustomPriceFormula(dto.getCustomPriceFormula());
    submissionEntity.setCustomOperatingCostFormula(dto.getCustomOperatingCostFormula());

    return submissionEntity;
  }

  /**
   * To submission DTO.
   *
   * @param entity the entity
   * @return the submission DTO
   */
  public SubmissionDTO toCustomSubmissionDTO(SubmissionEntity entity,@Context Table<String,String,MasterListValueHistoryDTO> activeSd,
		  @Context Table<String,String,List<MasterListValueHistoryDTO>>  historySd, @Context  Map<String,DepartmentHistoryDTO>  activeDepartments, @Context Map<String,List<DepartmentHistoryDTO>>  historyDepartments,
		  @Context Map<String, DirectorateHistoryDTO> activeDirectorates,
		  @Context Map<String, List<DirectorateHistoryDTO>> historyDirectorates
		  ) {
    if (entity == null) {
      return null;
    }

    SubmissionDTO submissionDTO = new SubmissionDTO();

    MasterListValueHistoryDTO reasonFreeAward = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO operatingCostFormula = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO priceFormula = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO workType = setValuesOfMasterListHistoryDTO(entity.getRecentWorkTypeHistory());
    MasterListValueHistoryDTO procedure = setValuesOfMasterListHistoryDTO(entity.getRecentProcedureHistory());
    MasterListValueHistoryDTO processType = setValuesOfMasterListHistoryDTO(entity.getRecentProcessTypeHistory());
    
    submissionDTO.setProcedure(procedure);
    submissionDTO.setProcessType(processType);
    submissionDTO.setWorkType(workType);
    submissionDTO.setReasonFreeAward(reasonFreeAward);
    submissionDTO.setOperatingCostFormula(operatingCostFormula);
    submissionDTO.setPriceFormula(priceFormula);

    procedure
        .setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getProcedure()));
    processType
        .setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getProcessType()));
    workType.setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getWorkType()));
    reasonFreeAward.setMasterListValueId(
        masterListValueMapper.toMasterListValueDTO(entity.getReasonFreeAward()));
    submissionDTO.setId(entity.getId());
    
    submissionDTO.setProject(customProjectMapper.toCustomProjectDTO(entity.getProject()));
    submissionDTO.setDescription(entity.getDescription());
    submissionDTO.setProcess(entity.getProcess());
    submissionDTO.setCostEstimate(entity.getCostEstimate());
    submissionDTO.setGattTwo(entity.getGattTwo());
    submissionDTO.setPublicationDate(entity.getPublicationDate());
    submissionDTO.setPublicationDateDirectAward(entity.getPublicationDateDirectAward());
    submissionDTO.setPublicationDateAward(entity.getPublicationDateAward());
    submissionDTO.setPmDepartmentName(entity.getPmDepartmentName());
    submissionDTO.setPmExternal(companyMapper.toCompanyDTO(entity.getPmExternal()));
    submissionDTO.setIsLocked(entity.getIsLocked());
    submissionDTO.setConstructionPermit(entity.getConstructionPermit());
    submissionDTO.setLoanApproval(entity.getLoanApproval());
    submissionDTO.setFirstDeadline(entity.getFirstDeadline());
    submissionDTO.setSecondDeadline(entity.getSecondDeadline());
    submissionDTO.setApplicationOpeningDate(entity.getApplicationOpeningDate());
    submissionDTO.setOfferOpeningDate(entity.getOfferOpeningDate());
    submissionDTO.setFirstLoggedBy(entity.getFirstLoggedBy());
    submissionDTO.setSecondLoggedBy(entity.getSecondLoggedBy());
    submissionDTO.setNotes(entity.getNotes());
    submissionDTO.setStatus(entity.getStatus());
    submissionDTO.setIsServiceTender(entity.getIsServiceTender());
    submissionDTO.setIsGekoEntry(entity.getIsGekoEntry());

    submissionDTO.setSubmittents(entityToSubmittentDTO(entity));
    submissionDTO.setMaxGrade(entity.getMaxGrade());
    submissionDTO.setMinGrade(entity.getMinGrade());
    submissionDTO.setAboveThreshold(entity.getAboveThreshold());
    submissionDTO.setAwardMaxGrade(entity.getAwardMaxGrade());
    submissionDTO.setAwardMinGrade(entity.getAwardMinGrade());
    submissionDTO.setAddedAwardRecipients(entity.getAddedAwardRecipients());
    submissionDTO.setEvaluationThrough(entity.getEvaluationThrough());
    submissionDTO.setCommissionProcurementProposalBusiness(
        entity.getCommissionProcurementProposalBusiness());
    submissionDTO
        .setCommissionProcurementProposalDate(entity.getCommissionProcurementProposalDate());
    submissionDTO
        .setCommissionProcurementProposalObject(entity.getCommissionProcurementProposalObject());
    submissionDTO.setCommissionProcurementProposalPreRemarks(
        entity.getCommissionProcurementProposalPreRemarks());
    submissionDTO.setCommissionProcurementProposalReservation(
        entity.getCommissionProcurementProposalReservation());
    submissionDTO.setCommissionProcurementProposalSuitabilityAuditDropdown(
        entity.getCommissionProcurementProposalSuitabilityAuditDropdown());
    submissionDTO.setCommissionProcurementProposalSuitabilityAuditText(
        entity.getCommissionProcurementProposalSuitabilityAuditText());
    submissionDTO.setCommissionProcurementProposalReasonGiven(
        entity.getCommissionProcurementProposalReasonGiven());
    operatingCostFormula.setMasterListValueId(
        masterListValueMapper.toMasterListValueDTO(entity.getOperatingCostFormula()));
    priceFormula
        .setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getPriceFormula()));
    submissionDTO.setCommissionProcurementDecisionRecommendation(
        entity.getCommissionProcurementDecisionRecommendation());
    submissionDTO.setSubmissionCancel(entityToSubmissionCancelDTO(entity));

    submissionDTO.setIsPmDepartmentNameUpdated(entity.getIsPmDepartmentNameUpdated());
    submissionDTO.setIsPmExternalUpdated(entity.getIsPmExternalUpdated());
    submissionDTO.setIsProcedureUpdated(entity.getIsProcedureUpdated());
    submissionDTO.setIsGattTwoUpdated(entity.getIsGattTwoUpdated());
    submissionDTO.setLegalHearingTerminate(entityToLegalHearingTerminateDTO(entity));
    submissionDTO.setCustomPriceFormula(entity.getCustomPriceFormula());
    submissionDTO.setCustomOperatingCostFormula(entity.getCustomOperatingCostFormula());
    
    setSD(entity, submissionDTO, activeSd, historySd,activeDepartments,historyDepartments,activeDirectorates,historyDirectorates);
    return submissionDTO;
  }

  private MasterListValueHistoryDTO setValuesOfMasterListHistoryDTO(
      MasterListValueHistoryEntity historyEntity) {
    MasterListValueHistoryDTO historyDTO = new MasterListValueHistoryDTO();
    historyDTO.setValue1(historyEntity.getValue1());
    historyDTO.setValue2(historyEntity.getValue2());
    historyDTO.setTenant(TenantMapper.INSTANCE.toTenantDTO(historyEntity.getTenant()));
    historyDTO.setFromDate(historyEntity.getFromDate());
    historyDTO.setToDate(historyEntity.getToDate());
    historyDTO.setModifiedOn(historyEntity.getModifiedOn());
    historyDTO.setInternalVersion(historyEntity.getInternalVersion());
    historyDTO.setShortCode(historyEntity.getShortCode());
    return historyDTO;
  }

  /**
   * Dto to submittent entity.
   *
   * @param dto the dto
   * @return the list
   */
  private List<SubmittentEntity> dtoToSubmittentEntity(SubmissionDTO dto) {
    if (dto == null) {
      return Collections.emptyList();
    }
    List<SubmittentEntity> submittentEntityList = new ArrayList<>();
    List<SubmittentDTO> submittentDTOList = dto.getSubmittents();
    if (submittentDTOList == null) {
      return Collections.emptyList();
    }

    for (SubmittentDTO list : submittentDTOList) {
      SubmittentEntity submittentEntity = new SubmittentEntity();
      if (list.getCompanyId() != null) {
        submittentEntity.setCompanyId(companyMapper.toCompany(list.getCompanyId()));
      }
      if (list.getExistsExclusionReasons() != null) {
        submittentEntity.setExistsExclusionReasons(list.getExistsExclusionReasons());
      }
      if (list.getFormalExaminationFulfilled() != null) {
        submittentEntity.setFormalExaminationFulfilled(list.getFormalExaminationFulfilled());
      }
      if (list.getId() != null) {
        submittentEntity.setId(list.getId());
      }
      if (list.getJointVentures() != null && !list.getJointVentures().isEmpty()) {
        submittentEntity.setJointVentures(companyMapper.toCompany(list.getJointVentures()));
      }
      if (list.getSubcontractors() != null && !list.getSubcontractors().isEmpty()) {
        submittentEntity.setSubcontractors(companyMapper.toCompany(list.getSubcontractors()));
      }
      submittentEntityList.add(submittentEntity);
    }

    return submittentEntityList;
  }

  /**
   * Dto work type master list value id.
   *
   * @param submissionDTO the submission DTO
   * @return the master list value DTO
   */
  private MasterListValueDTO dtoWorkTypeMasterListValueId(SubmissionDTO submissionDTO) {

    if (submissionDTO == null) {
      return null;
    }
    MasterListValueHistoryDTO workType = submissionDTO.getWorkType();
    if (workType == null) {
      return null;
    }
    MasterListValueDTO masterListValueId = workType.getMasterListValueId();
    if (masterListValueId == null) {
      return null;
    }
    return masterListValueId;
  }

  /**
   * Dto reason free award master list value id.
   *
   * @param submissionDTO the submission DTO
   * @return the master list value DTO
   */
  private MasterListValueDTO dtoReasonFreeAwardMasterListValueId(SubmissionDTO submissionDTO) {

    if (submissionDTO == null) {
      return null;
    }
    MasterListValueHistoryDTO reasonFreeAward = submissionDTO.getReasonFreeAward();
    if (reasonFreeAward == null) {
      return null;
    }
    MasterListValueDTO masterListValueId = reasonFreeAward.getMasterListValueId();
    if (masterListValueId == null) {
      return null;
    }
    return masterListValueId;
  }

  /**
   * Dto operating cost formula master list value id.
   *
   * @param submissionDTO the submission DTO
   * @return the master list value DTO
   */
  private MasterListValueDTO dtoOperatingCostFormulaMasterListValueId(SubmissionDTO submissionDTO) {

    if (submissionDTO == null) {
      return null;
    }
    MasterListValueHistoryDTO operatingCostFormula = submissionDTO.getOperatingCostFormula();
    if (operatingCostFormula == null) {
      return null;
    }
    MasterListValueDTO masterListValueId = operatingCostFormula.getMasterListValueId();
    if (masterListValueId == null) {
      return null;
    }
    return masterListValueId;
  }

  /**
   * Dto price formula master list value id.
   *
   * @param submissionDTO the submission DTO
   * @return the master list value DTO
   */
  private MasterListValueDTO dtoPriceFormulaMasterListValueId(SubmissionDTO submissionDTO) {

    if (submissionDTO == null) {
      return null;
    }
    MasterListValueHistoryDTO priceFormula = submissionDTO.getPriceFormula();
    if (priceFormula == null) {
      return null;
    }
    MasterListValueDTO masterListValueId = priceFormula.getMasterListValueId();
    if (masterListValueId == null) {
      return null;
    }
    return masterListValueId;
  }

  /**
   * Dto process type master list value id.
   *
   * @param submissionDTO the submission DTO
   * @return the master list value DTO
   */
  private MasterListValueDTO dtoProcessTypeMasterListValueId(SubmissionDTO submissionDTO) {

    if (submissionDTO == null) {
      return null;
    }
    MasterListValueHistoryDTO processType = submissionDTO.getProcessType();
    if (processType == null) {
      return null;
    }
    MasterListValueDTO masterListValueId = processType.getMasterListValueId();
    if (masterListValueId == null) {
      return null;
    }
    return masterListValueId;
  }

  /**
   * Dto procedure master list value id.
   *
   * @param submissionDTO the submission DTO
   * @return the master list value DTO
   */
  private MasterListValueDTO dtoProcedureMasterListValueId(SubmissionDTO submissionDTO) {

    if (submissionDTO == null) {
      return null;
    }
    MasterListValueHistoryDTO procedure = submissionDTO.getProcedure();
    if (procedure == null) {
      return null;
    }
    MasterListValueDTO masterListValueId = procedure.getMasterListValueId();
    if (masterListValueId == null) {
      return null;
    }
    return masterListValueId;
  }

  /**
   * Mapper for the submission cancel entity (we can not use the auto generated, because it will
   * cause reursion)
   *
   * @param dto the dto
   * @return the list
   */
  private List<SubmissionCancelEntity> dtoToSubmissionCancelEntity(SubmissionDTO dto) {
    if (dto == null) {
      return Collections.emptyList();
    }
    List<SubmissionCancelEntity> submissionCancelEntityList = new ArrayList<>();
    List<SubmissionCancelDTO> submissionCancelDTOList = dto.getSubmissionCancel();
    if (submissionCancelDTOList == null) {
      return Collections.emptyList();
    }

    for (SubmissionCancelDTO sdto : submissionCancelDTOList) {
      SubmissionCancelEntity submissionCancelEntity = new SubmissionCancelEntity();

      submissionCancelEntity.setId(sdto.getId());
      submissionCancelEntity.setAvailableDate(sdto.getAvailableDate());
      submissionCancelEntity.setFreezeCloseSubmission(sdto.getFreezeCloseSubmission());
      submissionCancelEntity.setObjectNameRead(sdto.getObjectNameRead());
      submissionCancelEntity.setProjectNameRead(sdto.getProjectNameRead());
      submissionCancelEntity.setWorkingClassRead(sdto.getWorkingClassRead());
      submissionCancelEntity.setDescriptionRead(sdto.getDescriptionRead());
      submissionCancelEntity.setReason(sdto.getReason());
      submissionCancelEntity.setCreatedBy(sdto.getCreatedBy());
      submissionCancelEntity.setCreatedOn(sdto.getCreatedOn());
      submissionCancelEntity.setUpdatedBy(sdto.getUpdatedBy());
      submissionCancelEntity.setUpdatedOn(sdto.getUpdatedOn());
      submissionCancelEntity.setCancelledBy(sdto.getCancelledBy());
      submissionCancelEntity.setCancelledOn(sdto.getCancelledOn());
      Set<MasterListValueEntity> set =
          masterListValueDTOSetToMasterListValueEntitySet(sdto.getWorkTypes());
      if (set != null) {
        submissionCancelEntity.setWorkTypes(set);
      }
      submissionCancelEntityList.add(submissionCancelEntity);
    }

    return submissionCancelEntityList;
  }

  private List<LegalHearingTerminateEntity> dtoToLegalHearingTerminateEntity(SubmissionDTO dto) {
    if (dto == null) {
      return Collections.emptyList();
    }
    List<LegalHearingTerminateEntity> legalHearingTerminateEntities = new ArrayList<>();
    List<LegalHearingTerminateDTO> legalHearingTerminateDTOs = dto.getLegalHearingTerminate();
    if (legalHearingTerminateDTOs == null) {
      return Collections.emptyList();
    }
    for (LegalHearingTerminateDTO ldto : legalHearingTerminateDTOs) {
      LegalHearingTerminateEntity legalHearingTerminateEntity = new LegalHearingTerminateEntity();
      legalHearingTerminateEntity.setId(ldto.getId());
      legalHearingTerminateEntity.setReason(ldto.getReason());
      legalHearingTerminateEntity.setDeadline(ldto.getDeadline());
      Set<MasterListValueEntity> set =
          masterListValueDTOSetToMasterListValueEntitySet(ldto.getTerminationReason());
      if (set != null) {
        legalHearingTerminateEntity.setTerminationReason(set);
      }
      legalHearingTerminateEntities.add(legalHearingTerminateEntity);
    }
    return legalHearingTerminateEntities;
  }


  /**
   * Master list value DTO set to master list value entity set.
   *
   * @param set the set
   * @return the sets the
   */
  protected Set<MasterListValueEntity> masterListValueDTOSetToMasterListValueEntitySet(
      Set<MasterListValueDTO> set) {
    if (set == null) {
      return Collections.emptySet();
    }

    Set<MasterListValueEntity> mset = new HashSet<>();
    for (MasterListValueDTO masterListValueDTO : set) {
      mset.add(masterListValueMapper.toMasterListValue(masterListValueDTO));
    }

    return mset;
  }

  /**
   * Mapper for the submission cancel entity (we can not use the auto generated, because it will
   * cause reursion)
   *
   * @param entity the entity
   * @return the list
   */
  private List<SubmittentDTO> entityToSubmittentDTO(SubmissionEntity entity) {
    if (entity == null) {
      return Collections.emptyList();
    }
    List<SubmittentDTO> submittentDTOList = new ArrayList<>();
    List<SubmittentEntity> submittentEntityList = entity.getSubmittents();
    if (submittentEntityList == null) {
      return Collections.emptyList();
    }

    for (SubmittentEntity list : submittentEntityList) {
      SubmittentDTO submittentDTO = new SubmittentDTO();
      if (list.getCompanyId() != null) {
        submittentDTO.setCompanyId(companyMapper.toCompanyDTO(list.getCompanyId()));
      }
      if (list.getExistsExclusionReasons() != null) {
        submittentDTO.setExistsExclusionReasons(list.getExistsExclusionReasons());
      }
      if (list.getFormalExaminationFulfilled() != null) {
        submittentDTO.setFormalExaminationFulfilled(list.getFormalExaminationFulfilled());
      }
      if (list.getId() != null) {
        submittentDTO.setId(list.getId());
      }
      if (!list.getJointVentures().isEmpty()) {
        submittentDTO.setJointVentures(companyMapper.toCompanyDTO(list.getJointVentures()));
      }
      if (!list.getSubcontractors().isEmpty()) {
        submittentDTO.setSubcontractors(companyMapper.toCompanyDTO(list.getSubcontractors()));
      }
      submittentDTOList.add(submittentDTO);
    }

    return submittentDTOList;
  }

  /**
   * To submission DTO list.
   *
   * @param submissionEntityList the submission entity list
   * @return the list
   */
  public List<SubmissionDTO> toCustomSubmissionDTOList(List<SubmissionEntity> submissionEntityList,
		  @Context Table<String,String,MasterListValueHistoryDTO> activeSd,
		  @Context Table<String,String,List<MasterListValueHistoryDTO>>  historySd,
		  @Context  Map<String,DepartmentHistoryDTO>  activeDepartmens, 
		  @Context Map<String,List<DepartmentHistoryDTO>>  historyDepartments,
		  @Context Map<String, DirectorateHistoryDTO> activeDirectorates,
		  @Context Map<String, List<DirectorateHistoryDTO>> historyDirectorates
		 

		  )
		  {
    List<SubmissionDTO> submissionDTOList = new ArrayList<>();
    if (submissionEntityList != null) {
      for (SubmissionEntity submissionEntity : submissionEntityList) {
        submissionDTOList.add(toCustomSubmissionDTO(submissionEntity,activeSd,historySd,activeDepartmens,historyDepartments,activeDirectorates,historyDirectorates));
      }
    }
    return submissionDTOList;
  }

  /**
   * Sets the SD.
   *
   * @param submissionEntity the submission entity
   * @param submissionDTO the submission DTO
   * @param activeSd the active sd
   * @param historySd the history sd
   * @param activeDepartments the active departments
   * @param historyDepartments the history departments
   * @param activeDirectorates the active directorates
   * @param historyDirectorates the history directorates
   */
  /**This function is used for Historization purposes, UC160 */
  public void setSD(SubmissionEntity submissionEntity, @MappingTarget SubmissionDTO submissionDTO,
		  @Context Table<String,String,MasterListValueHistoryDTO> activeSd,
		  @Context Table<String,String,List<MasterListValueHistoryDTO>>  historySd,
		  @Context  Map<String,DepartmentHistoryDTO>  activeDepartments, 
		  @Context Map<String,List<DepartmentHistoryDTO>>  historyDepartments,@Context Map<String, DirectorateHistoryDTO> activeDirectorates,
		  @Context Map<String, List<DirectorateHistoryDTO>> historyDirectorates			

		  ) {

		Timestamp date = null;
	    TenderStatusHistoryEntity currentStatus = submissionEntity.getTenderStatusHistory().iterator().next();
	    if (currentStatus.getStatusId().equals(TenderStatus.PROCEDURE_CANCELED.getValue())
				|| currentStatus.getStatusId().equals(TenderStatus.PROCEDURE_COMPLETED.getValue())) {
			date = currentStatus.getOnDate();
		}

	    Iterator<TenderStatusHistoryEntity> itr = submissionEntity.getTenderStatusHistory().iterator();
		TenderStatusHistoryEntity creationStatus = itr.next();
		while (itr.hasNext()) {
			creationStatus = itr.next();
		}
		Timestamp creationDate = creationStatus.getOnDate();

		Timestamp objectDate= null;
		if (submissionEntity.getProcess() == Process.SELECTIVE
				&& (LookupValues.getSubmissstatuses()
						.indexOf(TenderStatus.fromValue(currentStatus.getStatusId())) >= LookupValues
								.getSubmissstatuses().indexOf(TenderStatus.APPLICATION_OPENING_CLOSED))
				|| (submissionEntity.getProcess() != Process.SELECTIVE && LookupValues.getSubmissstatuses()
						.indexOf(TenderStatus.fromValue(currentStatus.getStatusId())) >= LookupValues
								.getSubmissstatuses().indexOf(TenderStatus.OFFER_OPENING_CLOSED))) {
			objectDate = currentStatus.getOnDate();
		}
		
		if(submissionEntity.getProcedure() != null) {
			submissionDTO
			.setProcedure(getValue(CategorySD.PROCESS_PM.getValue(), submissionEntity.getProcedure().getId(),date,creationDate,activeSd,historySd,null));
		}
		if(submissionEntity.getProcessType() != null) {
			submissionDTO.setProcessType(
					getValue(CategorySD.PROCESS_TYPE.getValue(), submissionEntity.getProcessType().getId(),date,creationDate,activeSd,historySd,null));
		}
		if(submissionEntity.getWorkType() != null) {
			submissionDTO.setWorkType(getValue(CategorySD.WORKTYPE.getValue(), submissionEntity.getWorkType().getId(),date,creationDate,activeSd,historySd,null));

		}
		if(submissionEntity.getReasonFreeAward() != null) {
			submissionDTO.setReasonFreeAward(
					getValue(CategorySD.NEGOTIATION_REASON.getValue(), submissionEntity.getReasonFreeAward().getId(),date,creationDate,activeSd,historySd,null));
		}
		if(submissionEntity.getOperatingCostFormula() != null) {
			submissionDTO.setOperatingCostFormula(getValue(CategorySD.CALCULATION_FORMULA.getValue(),
					submissionEntity.getOperatingCostFormula().getId(),date,creationDate,activeSd,historySd,null));
		}
		if(submissionEntity.getPriceFormula() != null) {
			submissionDTO.setPriceFormula(
					getValue(CategorySD.CALCULATION_FORMULA.getValue(), submissionEntity.getPriceFormula().getId(),date,creationDate,activeSd,historySd,null));
		}
		
	if(submissionEntity.getProject().getObjectName() != null) {
			submissionDTO.getProject().setObjectName(
					getValue(CategorySD.OBJECT.getValue(), submissionEntity.getProject().getObjectName().getId(),date,creationDate,activeSd,historySd,objectDate));
		}
		
	if (submissionEntity.getProject().getDepartment() != null) {
		DepartmentHistoryDTO departmentDTO = 
				getValue(submissionEntity.getProject().getDepartment().getId(), activeDepartments,historyDepartments,date);
		submissionDTO.getProject().setDepartment(departmentDTO);
		if(departmentDTO !=null) {
			DirectorateHistoryDTO directorate=getValueDirectorate(departmentDTO.getDirectorate().getDirectorateId().getId(), activeDirectorates,historyDirectorates,date);
			submissionDTO.getProject().getDepartment().setDirectorate(directorate);

		}
		
		
		
	}

	}
  
	 /**
 	 * Gets the value.
 	 *
 	 * @param category the category
 	 * @param masterListValueEntityId the master list value entity id
 	 * @param closedOrCanceledDate the closed or canceled date
 	 * @param creationDate the creation date
 	 * @param activeSd the active sd
 	 * @param historySd the history sd
 	 * @param objectDate the object date
 	 * @return the value
 	 */
  	/**This function is used for Historization purposes, UC160 */
 	public MasterListValueHistoryDTO getValue(String category, String masterListValueEntityId, Timestamp closedOrCanceledDate,Timestamp creationDate,
			 Table<String,String,MasterListValueHistoryDTO> activeSd,
			Table<String, String, List<MasterListValueHistoryDTO>> historySd, Timestamp objectDate) {
		List<MasterListValueHistoryDTO> masterListValueHistoryList = historySd.get(category, masterListValueEntityId);
		if (category.equals(CategorySD.WORKTYPE.getValue())) {

			return getValueByDateType(creationDate, masterListValueHistoryList);
		} else if (category.equals(CategorySD.OBJECT.getValue())) {
			if (objectDate == null) {
				return activeSd.get(category, masterListValueEntityId);

			} else {

				return getValueByDateType(objectDate, masterListValueHistoryList);
			}
		} else {
			if (closedOrCanceledDate == null) {
				return activeSd.get(category, masterListValueEntityId);
			} else {

				return getValueByDateType(closedOrCanceledDate, masterListValueHistoryList);
			}
		}

	}

	/**
	 * Gets the value by date type.
	 *
	 * @param referenceDate the reference date
	 * @param masterListValueHistoryList the master list value history list
	 * @return the value by date type
	 */
	private MasterListValueHistoryDTO getValueByDateType(Timestamp referenceDate,
			List<MasterListValueHistoryDTO> masterListValueHistoryList) {
		for (MasterListValueHistoryDTO masterListValueHistoryDTO : masterListValueHistoryList) {
			if (masterListValueHistoryDTO.getFromDate().compareTo(referenceDate) < 0
					&& (masterListValueHistoryDTO.getToDate() == null
							|| masterListValueHistoryDTO.getToDate().compareTo(referenceDate) > 0)) {
				return masterListValueHistoryDTO;
			}
		}
		return null;
	}
	 
	  /**
  	 * Gets the value.
  	 *
  	 * @param departmentId the department id
  	 * @param activeDepartments the active departments
  	 * @param historyDepartments the history departments
  	 * @param date the date
  	 * @return the value
  	 */
  	public DepartmentHistoryDTO getValue(String departmentId, Map<String,DepartmentHistoryDTO>  activeDepartments, 
			  Map<String,List<DepartmentHistoryDTO>>  historyDepartments,Timestamp date) {
		    if (date == null) {
		      return activeDepartments.get(departmentId);
		    } else {
		      List<DepartmentHistoryDTO> departmentHistoryList = historyDepartments.get(departmentId);
		      for (DepartmentHistoryDTO departmentDTOfromList : departmentHistoryList) {
		        if (departmentDTOfromList.getFromDate().compareTo(date) < 0
		            && (departmentDTOfromList.getToDate() == null
		                || departmentDTOfromList.getToDate().compareTo(date) > 0)) {
		          return departmentDTOfromList;
		        }
		      }
		    }
		    return activeDepartments.get(departmentId);

		  }
	  
	  /**
  	 * Gets the value directorate.
  	 *
  	 * @param directorateId the directorate id
  	 * @param activeDirectorates the active directorates
  	 * @param historyDirectorates the history directorates
  	 * @param date the date
  	 * @return the value directorate
  	 */
  	public DirectorateHistoryDTO getValueDirectorate(String directorateId, Map<String,DirectorateHistoryDTO>  activeDirectorates, 
			  Map<String,List<DirectorateHistoryDTO>>  historyDirectorates,Timestamp date) {
		    if (date == null) {
		      return activeDirectorates.get(directorateId);
		    } else {
		      List<DirectorateHistoryDTO> directorateHistoryList = historyDirectorates.get(directorateId);
		      for (DirectorateHistoryDTO directoratefromList : directorateHistoryList) {
		        if (directoratefromList.getFromDate().compareTo(date) < 0
		            && (directoratefromList.getToDate() == null
		                || directoratefromList.getToDate().compareTo(date) > 0)) {
		          return directoratefromList;
		        }
		      }
		    }
		    return activeDirectorates.get(directorateId);

		  }
  
  
  /**
   * Mapper for the submission cancel entity (we can not use the auto generated, because it will
   * cause reursion)
   *
   * @param entity the entity
   * @return the list
   */
  private List<SubmissionCancelDTO> entityToSubmissionCancelDTO(SubmissionEntity entity) {
    if (entity == null) {
      return Collections.emptyList();
    }
    List<SubmissionCancelDTO> submissionCancelDTOList = new ArrayList<>();
    List<SubmissionCancelEntity> submissionCancelEntityList = entity.getSubmissionCancel();
    if (submissionCancelEntityList == null) {
      return Collections.emptyList();
    }

    for (SubmissionCancelEntity sentity : submissionCancelEntityList) {
      SubmissionCancelDTO submissionCancelDTO = new SubmissionCancelDTO();
      submissionCancelDTO.setId(sentity.getId());
      submissionCancelDTO.setAvailableDate(sentity.getAvailableDate());
      submissionCancelDTO.setFreezeCloseSubmission(sentity.getFreezeCloseSubmission());
      submissionCancelDTO.setObjectNameRead(sentity.getObjectNameRead());
      submissionCancelDTO.setProjectNameRead(sentity.getProjectNameRead());
      submissionCancelDTO.setWorkingClassRead(sentity.getWorkingClassRead());
      submissionCancelDTO.setDescriptionRead(sentity.getDescriptionRead());
      submissionCancelDTO.setReason(sentity.getReason());
      submissionCancelDTO.setCreatedBy(sentity.getCreatedBy());
      submissionCancelDTO.setCreatedOn(sentity.getCreatedOn());
      submissionCancelDTO.setUpdatedBy(sentity.getUpdatedBy());
      submissionCancelDTO.setUpdatedOn(sentity.getUpdatedOn());
      submissionCancelDTO.setCancelledBy(sentity.getCancelledBy());
      submissionCancelDTO.setCancelledOn(sentity.getCancelledOn());
      Set<MasterListValueDTO> set =
          masterListValueEntitySetToMasterListValueDTOSet(sentity.getWorkTypes());
      if (set != null) {
        submissionCancelDTO.setWorkTypes(set);
      }
      submissionCancelDTOList.add(submissionCancelDTO);
    }

    return submissionCancelDTOList;
  }

  private List<LegalHearingTerminateDTO> entityToLegalHearingTerminateDTO(SubmissionEntity entity) {
    if (entity == null) {
      return Collections.emptyList();
    }
    List<LegalHearingTerminateDTO> legalHearingTerminateDTOs = new ArrayList<>();
    List<LegalHearingTerminateEntity> legalHearingTerminateEntities =
        entity.getLegalHearingTerminate();
    if (legalHearingTerminateEntities == null) {
      return Collections.emptyList();
    }
    for (LegalHearingTerminateEntity lentity : legalHearingTerminateEntities) {
      LegalHearingTerminateDTO ldto = new LegalHearingTerminateDTO();
      ldto.setDeadline(lentity.getDeadline());
      ldto.setId(lentity.getId());
      ldto.setReason(lentity.getReason());
      Set<MasterListValueDTO> set =
          masterListValueEntitySetToMasterListValueDTOSet(lentity.getTerminationReason());
      if (set != null) {
        ldto.setTerminationReason(set);
      }
      legalHearingTerminateDTOs.add(ldto);
    }
    return legalHearingTerminateDTOs;
  }

  /**
   * Master list value entity set to master list value DTO set.
   *
   * @param set the set
   * @return the sets the
   */
  protected Set<MasterListValueDTO> masterListValueEntitySetToMasterListValueDTOSet(
      Set<MasterListValueEntity> set) {
    if (set == null) {
      return Collections.emptySet();
    }

    Set<MasterListValueDTO> mset = new HashSet<>();
    for (MasterListValueEntity masterListValueEntity : set) {
      mset.add(masterListValueMapper.toMasterListValueDTO(masterListValueEntity));
    }

    return mset;
  }
}
