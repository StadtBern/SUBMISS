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

import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.OfferSubcriterionDTO;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferCriterionEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.OfferSubcriterionEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SubmittentDTOMapper.class, OfferCriterionDTOMapper.class, CriterionDTOMapper.class,
  OfferSubcriterionDTOMapper.class})
public abstract class OfferDTOWithCriteriaMapper {

  public static final OfferDTOWithCriteriaMapper INSTANCE =
    Mappers.getMapper(OfferDTOWithCriteriaMapper.class);
  private final SubmittentDTOMapper submittentDTOMapper =
    Mappers.getMapper(SubmittentDTOMapper.class);

  public OfferEntity toOffer(OfferDTO dto) {
    if (dto == null) {
      return null;
    }

    OfferEntity offerEntity = new OfferEntity();

    offerEntity.setId(dto.getId());
    offerEntity.setVersion(dto.getVersion());
    offerEntity.setSubmittent(submittentDTOMapper.toSubmittent(dto.getSubmittent()));
    offerEntity.setIsAwarded(dto.getIsAwarded());
    offerEntity.setOfferDate(dto.getOfferDate());
    offerEntity.setIsPartOffer(dto.getIsPartOffer());
    offerEntity.setIsExcludedFromProcess(dto.getIsExcludedFromProcess());
    offerEntity.setIsVariant(dto.getIsVariant());
    if (dto.getSettlement() != null) {
      offerEntity.setSettlement(MasterListValueMapper.INSTANCE
        .toMasterListValue(dto.getSettlement().getMasterListValueId()));
    }
    offerEntity.setGrossAmount(dto.getGrossAmount());
    offerEntity.setGrossAmountCorrected(dto.getGrossAmountCorrected());
    offerEntity.setIsCorrected(dto.getIsCorrected());
    offerEntity.setDiscount(dto.getDiscount());
    offerEntity.setIsDiscountPercentage(dto.getIsDiscountPercentage());
    offerEntity.setVat(dto.getVat());
    offerEntity.setDiscount2(dto.getDiscount2());
    offerEntity.setIsDiscount2Percentage(dto.getIsDiscount2Percentage());
    offerEntity.setDiscount2Days(dto.getDiscount2Days());
    offerEntity.setPriceIncrease(dto.getPriceIncrease());
    offerEntity.setModifiedOn(dto.getModifiedOn());
    offerEntity.setNotes(dto.getNotes());
    offerEntity.setRank(dto.getRank());
    offerEntity.setVariantNotes(dto.getVariantNotes());
    offerEntity.setIsEmptyOffer(dto.getIsEmptyOffer());
    offerEntity.setApplicationDate(dto.getApplicationDate());
    offerEntity.setIsVatPercentage(dto.getIsVatPercentage());
    offerEntity.setBuildingCosts(dto.getBuildingCosts());
    offerEntity.setIsBuildingCostsPercentage(dto.getIsBuildingCostsPercentage());
    offerEntity.setAncilliaryAmountGross(dto.getAncilliaryAmountGross());
    offerEntity.setIsAncilliaryAmountPercentage(dto.getIsAncilliaryAmountPercentage());
    offerEntity.setAncilliaryAmountVat(dto.getAncilliaryAmountVat());
    offerEntity.setOperatingCostGross(dto.getOperatingCostGross());
    offerEntity.setOperatingCostNotes(dto.getOperatingCostNotes());
    offerEntity.setOperatingCostGrossCorrected(dto.getOperatingCostGrossCorrected());
    offerEntity.setIsOperatingCostCorrected(dto.getIsOperatingCostCorrected());
    offerEntity.setOperatingCostDiscount(dto.getOperatingCostDiscount());
    offerEntity.setIsOperatingCostDiscountPercentage(dto.getIsOperatingCostDiscountPercentage());
    offerEntity.setOperatingCostDiscount2(dto.getOperatingCostDiscount2());
    offerEntity.setIsOperatingCostDiscount2Percentage(dto.getIsOperatingCostDiscount2Percentage());
    offerEntity.setOperatingCostVat(dto.getOperatingCostVat());
    offerEntity.setOperatingCostIsVatPercentage(dto.getOperatingCostIsVatPercentage());
    offerEntity.setIsDefaultOffer(dto.getIsDefaultOffer());
    offerEntity.setFromMigration(dto.getFromMigration());
    offerEntity.setMigratedProject(dto.getMigratedProject());
    offerEntity.setMigratedSubmission(dto.getMigratedSubmission());
    offerEntity.setMigratedDepartment(dto.getMigratedDepartment());
    offerEntity.setMigreatedPM(dto.getMigreatedPM());
    offerEntity.setAmount(dto.getAmount());
    offerEntity.setDiscountInPercentage(dto.getDiscountInPercentage());
    offerEntity.setDiscount2InPercentage(dto.getDiscount2InPercentage());
    offerEntity.setOperatingCostsInPercentage(dto.getOperatingCostsInPercentage());
    offerEntity.setBuildingCostsInPercentage(dto.getBuildingCostsInPercentage());
    offerEntity.setqExTotalGrade(dto.getqExTotalGrade());
    offerEntity.setqExStatus(dto.getqExStatus());
    offerEntity.setqExExaminationIsFulfilled(dto.getqExExaminationIsFulfilled());
    offerEntity.setqExSuitabilityNotes(dto.getqExSuitabilityNotes());
    offerEntity.setOfferCriteria(dtoToOfferCriterionEntity(dto));
    offerEntity.setOfferSubcriteria(dtoToOfferSubcriterionEntity(dto));
    offerEntity.setAwardRank(dto.getAwardRank());
    offerEntity.setAwardTotalScore(dto.getAwardTotalScore());
    offerEntity.setOperatingCostsAmount(dto.getOperatingCostsAmount());
    offerEntity.setqExRank(dto.getqExRank());
    offerEntity.setCreatedOn(dto.getCreatedOn());
    offerEntity.setCreatedBy(dto.getCreatedBy());
    offerEntity.setExcludedFirstLevel(dto.getExcludedFirstLevel());
    offerEntity.setExclusionReason(dto.getExclusionReason());
    offerEntity.setExclusionReasonFirstLevel(dto.getExclusionReasonFirstLevel());
    offerEntity.setExcludedByPassingApplicants(dto.getExcludedByPassingApplicants());
    return offerEntity;
  }

  public List<OfferEntity> toOffer(List<OfferDTO> dtoList) {
    if (dtoList == null) {
      return Collections.emptyList();
    }

    List<OfferEntity> list = new ArrayList<>();
    for (OfferDTO offerDTO : dtoList) {
      list.add(toOffer(offerDTO));
    }

    return list;
  }

  private List<OfferCriterionEntity> dtoToOfferCriterionEntity(OfferDTO dto) {
    if (dto == null) {
      return Collections.emptyList();
    }
    List<OfferCriterionEntity> offerCriterionEntities = new ArrayList<>();
    List<OfferCriterionDTO> offerCriterionDTOs = dto.getOfferCriteria();
    if (offerCriterionDTOs == null) {
      return Collections.emptyList();
    }
    for (OfferCriterionDTO offerCriterionDTO : offerCriterionDTOs) {
      OfferCriterionEntity offerCriterionEntity = new OfferCriterionEntity();
      if (offerCriterionDTO.getCriterion() != null) {
        offerCriterionEntity.setCriterion(
          CriterionDTOMapper.INSTANCE.toCriterion(offerCriterionDTO.getCriterion()));
      }
      if (offerCriterionDTO.getGrade() != null) {
        offerCriterionEntity.setGrade(offerCriterionDTO.getGrade());
      }
      if (offerCriterionDTO.getId() != null) {
        offerCriterionEntity.setId(offerCriterionDTO.getId());
      }
      if (offerCriterionDTO.getIsFulfilled() != null) {
        offerCriterionEntity.setIsFulfilled(offerCriterionDTO.getIsFulfilled());
      }
      if (offerCriterionDTO.getScore() != null) {
        offerCriterionEntity.setScore(offerCriterionDTO.getScore());
      }
      if (offerCriterionDTO.getCreatedOn() != null) {
        offerCriterionEntity.setCreatedOn(offerCriterionDTO.getCreatedOn());
      }
      if (offerCriterionDTO.getUpdatedOn() != null) {
        offerCriterionEntity.setUpdatedOn(offerCriterionDTO.getUpdatedOn());
      }
      if (offerCriterionDTO.getVersion() != null) {
        offerCriterionEntity.setVersion(offerCriterionDTO.getVersion());
      }
      offerCriterionEntities.add(offerCriterionEntity);
    }
    return offerCriterionEntities;
  }

  private List<OfferSubcriterionEntity> dtoToOfferSubcriterionEntity(OfferDTO dto) {
    if (dto == null) {
      return Collections.emptyList();
    }
    List<OfferSubcriterionEntity> offerSubcriterionEntities = new ArrayList<>();
    List<OfferSubcriterionDTO> offerSubcriterionDTOs = dto.getOfferSubcriteria();
    if (offerSubcriterionDTOs == null) {
      return Collections.emptyList();
    }
    for (OfferSubcriterionDTO offerSubcriterionDTO : offerSubcriterionDTOs) {
      OfferSubcriterionEntity offerSubcriterionEntity = new OfferSubcriterionEntity();
      if (offerSubcriterionDTO.getSubcriterion() != null) {
        offerSubcriterionEntity.setSubriterion(
          SubcriterionDTOMapper.INSTANCE.toSubcriterion(offerSubcriterionDTO.getSubcriterion()));
      }
      if (offerSubcriterionDTO.getGrade() != null) {
        offerSubcriterionEntity.setGrade(offerSubcriterionDTO.getGrade());
      }
      if (offerSubcriterionDTO.getId() != null) {
        offerSubcriterionEntity.setId(offerSubcriterionDTO.getId());
      }
      if (offerSubcriterionDTO.getScore() != null) {
        offerSubcriterionEntity.setScore(offerSubcriterionDTO.getScore());
      }
      offerSubcriterionEntities.add(offerSubcriterionEntity);
    }
    return offerSubcriterionEntities;
  }

  public OfferDTO toOfferDTO(OfferEntity entity) {
    if (entity == null) {
      return null;
    }

    OfferDTO offerDTO = new OfferDTO();

    offerDTO.setId(entity.getId());
    offerDTO.setVersion(entity.getVersion());
    offerDTO.setSubmittent(submittentDTOMapper.toSubmittentDTO(entity.getSubmittent()));
    offerDTO.setIsAwarded(entity.getIsAwarded());
    offerDTO.setOfferDate(entity.getOfferDate());
    offerDTO.setIsPartOffer(entity.getIsPartOffer());
    offerDTO.setIsExcludedFromProcess(entity.getIsExcludedFromProcess());
    offerDTO.setIsVariant(entity.getIsVariant());
    if (entity.getSettlement() != null && entity.getSettlement()
      .getMasterListValueHistory() != null) {
      for (MasterListValueHistoryEntity settlement : entity.getSettlement()
        .getMasterListValueHistory()) {
        if (settlement.getToDate() == null) {
          // Get latest settlement value (toDate property is null).
          offerDTO.setSettlement(
            MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(settlement));
          break;
        }
      }
    }
    offerDTO.setGrossAmount(entity.getGrossAmount());
    offerDTO.setGrossAmountCorrected(entity.getGrossAmountCorrected());
    offerDTO.setIsCorrected(entity.getIsCorrected());
    offerDTO.setDiscount(entity.getDiscount());
    offerDTO.setIsDiscountPercentage(entity.getIsDiscountPercentage());
    offerDTO.setVat(entity.getVat());
    offerDTO.setDiscount2(entity.getDiscount2());
    offerDTO.setIsDiscount2Percentage(entity.getIsDiscount2Percentage());
    offerDTO.setDiscount2Days(entity.getDiscount2Days());
    offerDTO.setPriceIncrease(entity.getPriceIncrease());
    offerDTO.setModifiedOn(entity.getModifiedOn());
    offerDTO.setNotes(entity.getNotes());
    offerDTO.setRank(entity.getRank());
    offerDTO.setVariantNotes(entity.getVariantNotes());
    offerDTO.setIsEmptyOffer(entity.getIsEmptyOffer());
    offerDTO.setApplicationDate(entity.getApplicationDate());
    offerDTO.setIsVatPercentage(entity.getIsVatPercentage());
    offerDTO.setBuildingCosts(entity.getBuildingCosts());
    offerDTO.setIsBuildingCostsPercentage(entity.getIsBuildingCostsPercentage());
    offerDTO.setAncilliaryAmountGross(entity.getAncilliaryAmountGross());
    offerDTO.setIsAncilliaryAmountPercentage(entity.getIsAncilliaryAmountPercentage());
    offerDTO.setAncilliaryAmountVat(entity.getAncilliaryAmountVat());
    offerDTO.setOperatingCostGross(entity.getOperatingCostGross());
    offerDTO.setOperatingCostNotes(entity.getOperatingCostNotes());
    offerDTO.setOperatingCostGrossCorrected(entity.getOperatingCostGrossCorrected());
    offerDTO.setIsOperatingCostCorrected(entity.getIsOperatingCostCorrected());
    offerDTO.setOperatingCostDiscount(entity.getOperatingCostDiscount());
    offerDTO.setIsOperatingCostDiscountPercentage(entity.getIsOperatingCostDiscountPercentage());
    offerDTO.setOperatingCostDiscount2(entity.getOperatingCostDiscount2());
    offerDTO.setIsOperatingCostDiscount2Percentage(entity.getIsOperatingCostDiscount2Percentage());
    offerDTO.setOperatingCostVat(entity.getOperatingCostVat());
    offerDTO.setOperatingCostIsVatPercentage(entity.getOperatingCostIsVatPercentage());
    offerDTO.setMigreatedPM(entity.getMigreatedPM());
    offerDTO.setMigratedDepartment(entity.getMigratedDepartment());
    offerDTO.setFromMigration(entity.getFromMigration());
    offerDTO.setMigratedSubmission(entity.getMigratedSubmission());
    offerDTO.setMigratedProject(entity.getMigratedProject());
    offerDTO.setAmount(entity.getAmount());
    offerDTO.setDiscountInPercentage(entity.getDiscountInPercentage());
    offerDTO.setDiscount2InPercentage(entity.getDiscount2InPercentage());
    offerDTO.setOperatingCostsInPercentage(entity.getOperatingCostsInPercentage());
    offerDTO.setBuildingCostsInPercentage(entity.getBuildingCostsInPercentage());
    offerDTO.setIsDefaultOffer(entity.getIsDefaultOffer());
    offerDTO.setqExTotalGrade(entity.getqExTotalGrade());
    offerDTO.setqExStatus(entity.getqExStatus());
    offerDTO.setqExExaminationIsFulfilled(entity.getqExExaminationIsFulfilled());
    offerDTO.setqExSuitabilityNotes(entity.getqExSuitabilityNotes());
    offerDTO.setOfferCriteria(entityToOfferCriterionDTO(entity));
    offerDTO.setOfferSubcriteria(entityToOfferSubcriterionDTO(entity));
    offerDTO.setAwardRank(entity.getAwardRank());
    offerDTO.setAwardTotalScore(entity.getAwardTotalScore());
    offerDTO.setOperatingCostsAmount(entity.getOperatingCostsAmount());
    offerDTO.setqExRank(entity.getqExRank());
    offerDTO.setCreatedBy(entity.getCreatedBy());
    offerDTO.setCreatedOn(entity.getCreatedOn());
    offerDTO.setExcludedFirstLevel(entity.getExcludedFirstLevel());
    offerDTO.setExclusionReason(entity.getExclusionReason());
    offerDTO.setExclusionReasonFirstLevel(entity.getExclusionReasonFirstLevel());
    offerDTO.setExcludedByPassingApplicants(entity.getExcludedByPassingApplicants());
    // Return a set of MasterListValueHistoryDTO instead of MasterListValueDTOs.
    // Convert MasterListValueEntity set to MasterListValueHistoryDTO set.
    offerDTO.setExclusionReasons(
      masterListValueEntitySetToMasterListValueDTOSet(entity.getExclusionReasons()));
    offerDTO.setExclusionReasonsFirstLevel(
      masterListValueEntitySetToMasterListValueDTOSet(entity.getExclusionReasonsFirstLevel()));
    return offerDTO;
  }

  public List<OfferDTO> toOfferDTO(List<OfferEntity> offerList) {
    if (offerList == null) {
      return Collections.emptyList();
    }

    List<OfferDTO> list = new ArrayList<>();
    for (OfferEntity offerEntity : offerList) {
      list.add(toOfferDTO(offerEntity));
    }

    return list;
  }

  private List<OfferCriterionDTO> entityToOfferCriterionDTO(OfferEntity entity) {
    if (entity == null) {
      return Collections.emptyList();
    }
    List<OfferCriterionDTO> offerCriterionDTOs = new ArrayList<>();
    List<OfferCriterionEntity> offerCriterionEntities = entity.getOfferCriteria();
    if (offerCriterionEntities == null) {
      return Collections.emptyList();
    }
    for (OfferCriterionEntity offerCriterionEntity : offerCriterionEntities) {
      OfferCriterionDTO offerCriterionDTO = new OfferCriterionDTO();
      if (offerCriterionEntity.getCriterion() != null) {
        offerCriterionDTO.setCriterion(
          CriterionDTOMapper.INSTANCE.toCriterionDTO(offerCriterionEntity.getCriterion()));
      }
      if (offerCriterionEntity.getGrade() != null) {
        offerCriterionDTO.setGrade(offerCriterionEntity.getGrade());
      }
      if (offerCriterionEntity.getId() != null) {
        offerCriterionDTO.setId(offerCriterionEntity.getId());
      }
      if (offerCriterionEntity.getIsFulfilled() != null) {
        offerCriterionDTO.setIsFulfilled(offerCriterionEntity.getIsFulfilled());
      }
      if (offerCriterionEntity.getScore() != null) {
        offerCriterionDTO.setScore(offerCriterionEntity.getScore());
      }
      if (offerCriterionEntity.getCreatedOn() != null) {
        offerCriterionDTO.setCreatedOn(offerCriterionEntity.getCreatedOn());
      }
      if (offerCriterionEntity.getUpdatedOn() != null) {
        offerCriterionDTO.setUpdatedOn(offerCriterionEntity.getUpdatedOn());
      }
      if (offerCriterionEntity.getVersion() != null) {
        offerCriterionDTO.setVersion(offerCriterionEntity.getVersion());
      }
      offerCriterionDTOs.add(offerCriterionDTO);
    }
    return offerCriterionDTOs;
  }

  private List<OfferSubcriterionDTO> entityToOfferSubcriterionDTO(OfferEntity entity) {
    if (entity == null) {
      return Collections.emptyList();
    }
    List<OfferSubcriterionDTO> offerSubcriterionDTOs = new ArrayList<>();
    List<OfferSubcriterionEntity> offerCriterionEntities = entity.getOfferSubcriteria();
    if (offerCriterionEntities == null) {
      return Collections.emptyList();
    }
    for (OfferSubcriterionEntity offerSubcriterionEntity : offerCriterionEntities) {
      OfferSubcriterionDTO offerSubcriterionDTO = new OfferSubcriterionDTO();
      if (offerSubcriterionEntity.getSubcriterion() != null) {
        offerSubcriterionDTO.setSubcriterion(SubcriterionDTOMapper.INSTANCE
          .toSubcriterionDTO(offerSubcriterionEntity.getSubcriterion()));
      }
      if (offerSubcriterionEntity.getGrade() != null) {
        offerSubcriterionDTO.setGrade(offerSubcriterionEntity.getGrade());
      }
      if (offerSubcriterionEntity.getId() != null) {
        offerSubcriterionDTO.setId(offerSubcriterionEntity.getId());
      }
      if (offerSubcriterionEntity.getScore() != null) {
        offerSubcriterionDTO.setScore(offerSubcriterionEntity.getScore());
      }
      offerSubcriterionDTOs.add(offerSubcriterionDTO);
    }
    return offerSubcriterionDTOs;
  }

  /**
   * Master list value entity set to master list value DTO set.
   *
   * @param set the set
   * @return the sets the
   */
  protected Set<MasterListValueHistoryDTO> masterListValueEntitySetToMasterListValueDTOSet(
    Set<MasterListValueEntity> set) {
    if (set == null) {
      return Collections.emptySet();
    }
    Set<MasterListValueHistoryDTO> mset = new HashSet<>();
    for (MasterListValueEntity masterListValueEntity : set) {
      mset.addAll(masterListValueHistoryEntitySetToMasterListValueHistoryDTOSet(
        masterListValueEntity.getMasterListValueHistory()));
    }

    return mset;
  }

  /**
   * Master list value history entity set to master list value history DTO set.
   *
   * @param set the set
   * @return the sets the
   */
  protected Set<MasterListValueHistoryDTO> masterListValueHistoryEntitySetToMasterListValueHistoryDTOSet(
    Set<MasterListValueHistoryEntity> set) {
    if (set == null) {
      return Collections.emptySet();
    }

    Set<MasterListValueHistoryDTO> mset = new HashSet<>();
    for (MasterListValueHistoryEntity masterListValueEntity : set) {
      mset.add(
        MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(masterListValueEntity));
    }

    return mset;
  }
}
