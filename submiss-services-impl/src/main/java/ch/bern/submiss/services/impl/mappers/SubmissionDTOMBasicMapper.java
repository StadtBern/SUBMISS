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
import ch.bern.submiss.services.impl.model.LegalHearingTerminateEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.SubmissionCancelEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;

@Mapper(uses = {ProjectMapper.class, CompanyMapper.class, MasterListValueHistoryMapper.class,
    MasterListValueMapper.class, SubmittentDTOMapper.class})
public abstract class SubmissionDTOMBasicMapper {
  /** The project mapper. */
  private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

  /** The company mapper. */
  private final CompanyMapper companyMapper = Mappers.getMapper(CompanyMapper.class);

  /** The master list value mapper. */
  private final MasterListValueMapper masterListValueMapper =
      Mappers.getMapper(MasterListValueMapper.class);
  
  /** The Constant INSTANCE. */
  public static final SubmissionDTOMBasicMapper INSTANCE = Mappers.getMapper(SubmissionDTOMBasicMapper.class);

  public SubmissionDTO toBasicSubmissionDTO(SubmissionEntity entity) {

    if (entity == null) {
      return null;
    }

    SubmissionDTO submissionDTO = new SubmissionDTO();

    MasterListValueHistoryDTO reasonFreeAward = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO workType = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO procedure = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO processType = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO operatingCostFormula = new MasterListValueHistoryDTO();
    MasterListValueHistoryDTO priceFormula = new MasterListValueHistoryDTO();
    submissionDTO.setOperatingCostFormula(operatingCostFormula);
    submissionDTO.setPriceFormula(priceFormula);
    submissionDTO.setProcedure(procedure);
    submissionDTO.setProcessType(processType);
    submissionDTO.setWorkType(workType);
    submissionDTO.setReasonFreeAward(reasonFreeAward);

    procedure
        .setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getProcedure()));
    processType
        .setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getProcessType()));
    workType.setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getWorkType()));
    reasonFreeAward.setMasterListValueId(
        masterListValueMapper.toMasterListValueDTO(entity.getReasonFreeAward()));
    submissionDTO.setId(entity.getId());
    submissionDTO.setProject(projectMapper.toProjectDTO(entity.getProject()));
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
    submissionDTO.setSecondOfferOpeningDate(entity.getSecondOfferOpeningDate());
    submissionDTO.setFirstLoggedBy(entity.getFirstLoggedBy());
    submissionDTO.setSecondLoggedBy(entity.getSecondLoggedBy());
    submissionDTO.setNotes(entity.getNotes());
    submissionDTO.setIsServiceTender(entity.getIsServiceTender());
    submissionDTO.setIsGekoEntry(entity.getIsGekoEntry());
    submissionDTO.setMaxGrade(entity.getMaxGrade());
    submissionDTO.setMinGrade(entity.getMinGrade());
    submissionDTO.setAboveThreshold(entity.getAboveThreshold());
    submissionDTO.setAwardMaxGrade(entity.getAwardMaxGrade());
    submissionDTO.setAwardMinGrade(entity.getAwardMinGrade());
    submissionDTO.setAddedAwardRecipients(entity.getAddedAwardRecipients());
    submissionDTO.setEvaluationThrough(entity.getEvaluationThrough());

    submissionDTO.setIsPmDepartmentNameUpdated(entity.getIsPmDepartmentNameUpdated());
    submissionDTO.setIsPmExternalUpdated(entity.getIsPmExternalUpdated());
    submissionDTO.setIsProcedureUpdated(entity.getIsProcedureUpdated());
    submissionDTO.setIsGattTwoUpdated(entity.getIsGattTwoUpdated());
    submissionDTO.setLegalHearingTerminate(entityToLegalHearingTerminateDTO(entity));
    submissionDTO.setExclusionDeadline(entity.getExclusionDeadline());
    submissionDTO.setStatus(entity.getStatus());
    submissionDTO.setFirstLevelExclusionDate(entity.getFirstLevelExclusionDate());
    submissionDTO.setExaminationIsLocked(entity.getExaminationIsLocked());
    submissionDTO.setAwardIsLocked(entity.getAwardIsLocked());
    submissionDTO.setExaminationLockedBy(entity.getExaminationLockedBy());
    submissionDTO.setAwardIsLocked(entity.getAwardIsLocked());
    submissionDTO.setExaminationLockedTime(entity.getExaminationLockedTime());
    submissionDTO.setAwardLockedBy(entity.getAwardLockedBy());
    submissionDTO.setAwardLockedTime(entity.getAwardLockedTime());
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

    submissionDTO.setCustomPriceFormula(entity.getCustomPriceFormula());
    submissionDTO.setCustomOperatingCostFormula(entity.getCustomOperatingCostFormula());
    submissionDTO.setIsGekoEntryByManualAward(entity.getIsGekoEntryByManualAward());
    submissionDTO.setVersion(entity.getVersion());
    submissionDTO.setCreatedBy(entity.getCreatedBy());
    submissionDTO.setCreatedOn(entity.getCreatedOn());
    submissionDTO.setUpdatedOn(entity.getUpdatedOn());
    submissionDTO.setNoAwardTender(entity.getNoAwardTender());
    submissionDTO.setPassingApplicants(entity.getPassingApplicants());
    submissionDTO.setIsNachtragCreated(entity.getIsNachtragCreated());
    return submissionDTO;
  
  }
  
  public List<SubmissionDTO> toBasicSubmissionDTOList(List<SubmissionEntity> submissionEntityList) {
    List<SubmissionDTO> submissionDTOList = new ArrayList<>();
    if (submissionEntityList != null) {
      for (SubmissionEntity submissionEntity : submissionEntityList) {
        submissionDTOList.add(toBasicSubmissionDTO(submissionEntity));
      }
    }
    return submissionDTOList;
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
}
