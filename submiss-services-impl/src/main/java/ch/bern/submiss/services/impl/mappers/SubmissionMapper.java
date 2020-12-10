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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ch.bern.submiss.services.api.dto.LegalHearingTerminateDTO;
import ch.bern.submiss.services.api.dto.MasterListValueDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissionCancelDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.impl.model.LegalHearingTerminateEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.SubmissionCancelEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;

/**
 * Mapper for Submission.
 */
@Mapper(uses = {ProjectMapper.class, CompanyMapper.class, MasterListValueHistoryMapper.class,
    MasterListValueMapper.class, SubmittentDTOMapper.class, SubmissionDTOMBasicMapper.class})
public abstract class SubmissionMapper {

  /** The project mapper. */
  private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

  /** The company mapper. */
  private final CompanyMapper companyMapper = Mappers.getMapper(CompanyMapper.class);

  /** The master list value mapper. */
  private final MasterListValueMapper masterListValueMapper =
      Mappers.getMapper(MasterListValueMapper.class);
  
  private final SubmissionDTOMBasicMapper submissionDTOMBasicMapper =
      Mappers.getMapper(SubmissionDTOMBasicMapper.class);

  /** The Constant INSTANCE. */
  public static final SubmissionMapper INSTANCE = Mappers.getMapper(SubmissionMapper.class);

  /**
   * To submission.
   *
   * @param dto the dto
   * @return the submission entity
   */
  public SubmissionEntity toSubmission(SubmissionDTO dto) {
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
    submissionEntity.setExclusionDeadline(dto.getExclusionDeadline());
    submissionEntity.setStatus(dto.getStatus());
    submissionEntity.setFirstLevelExclusionDate(dto.getFirstLevelExclusionDate());
    submissionEntity.setCustomPriceFormula(dto.getCustomPriceFormula());
    submissionEntity.setCustomOperatingCostFormula(dto.getCustomOperatingCostFormula());
    submissionEntity.setExaminationIsLocked(dto.getExaminationIsLocked());
    submissionEntity.setExaminationLockedBy(dto.getExaminationLockedBy());
    submissionEntity.setAwardIsLocked(dto.getAwardIsLocked());
    submissionEntity.setExaminationLockedTime(dto.getExaminationLockedTime());
    submissionEntity.setAwardLockedBy(dto.getAwardLockedBy());
    submissionEntity.setAwardLockedTime(dto.getAwardLockedTime());

    submissionEntity.setIsGekoEntryByManualAward(dto.getIsGekoEntryByManualAward());
    submissionEntity.setSubmittentListCheckedBy(dto.getSubmittentListCheckedBy());
    submissionEntity.setSubmittentListCheckedOn(dto.getSubmittentListCheckedOn());
    submissionEntity.setVersion(dto.getVersion());
    submissionEntity.setUpdatedOn(dto.getUpdatedOn());
    submissionEntity.setNoAwardTender(dto.getNoAwardTender());
    submissionEntity.setPassingApplicants(dto.getPassingApplicants());

    return submissionEntity;
  }

  /**
   * To submission DTO.
   *
   * @param entity the entity
   * @return the submission DTO
   */
  public SubmissionDTO toSubmissionDTO(SubmissionEntity entity) {

    if (entity == null) {
      return null;
    }

    SubmissionDTO submissionDTO = submissionDTOMBasicMapper.toBasicSubmissionDTO(entity);
    submissionDTO.setSubmittents(entityToSubmittentDTO(entity));

    return submissionDTO;

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
  public Set<MasterListValueEntity> masterListValueDTOSetToMasterListValueEntitySet(
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
      if (list.getJointVentures() != null && !list.getJointVentures().isEmpty()) {
        submittentDTO.setJointVentures(companyMapper.toCompanyDTO(list.getJointVentures()));
      }
      if (list.getSubcontractors() != null && !list.getSubcontractors().isEmpty()) {
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
  public List<SubmissionDTO> toSubmissionDTOList(List<SubmissionEntity> submissionEntityList) {
    List<SubmissionDTO> submissionDTOList = new ArrayList<>();
    if (submissionEntityList != null) {
      for (SubmissionEntity submissionEntity : submissionEntityList) {
        submissionDTOList.add(toSubmissionDTO(submissionEntity));
      }
    }
    return submissionDTOList;
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
