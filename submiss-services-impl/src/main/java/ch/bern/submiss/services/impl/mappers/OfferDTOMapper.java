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
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for Offer.
 */
@Mapper(uses = {SubmittentDTOMapper.class})
public abstract class OfferDTOMapper {

  /**
   * The Constant INSTANCE.
   */
  public static final OfferDTOMapper INSTANCE = Mappers.getMapper(OfferDTOMapper.class);
  /**
   * The submittent DTO mapper.
   */
  private final SubmittentDTOMapper submittentDTOMapper = Mappers
    .getMapper(SubmittentDTOMapper.class);
  /**
   * The custom submittent mapper.
   */
  private final CustomSubmittentMapper customSubmittentMapper = Mappers
    .getMapper(CustomSubmittentMapper.class);

  /**
   * To offer.
   *
   * @param dto the dto
   * @return the offer entity
   */
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
    offerEntity.setOperatingCostsAmount(dto.getOperatingCostsAmount());
    offerEntity.setqExRank(dto.getqExRank());
    offerEntity.setAwardRecipientFreeTextField(dto.getAwardRecipientFreeTextField());
    offerEntity.setAwardRank(dto.getAwardRank());
    offerEntity.setAwardTotalScore(dto.getAwardTotalScore());
    offerEntity.setMigratedProcedure(dto.getMigratedProcedure());
    offerEntity.setCreatedOn(dto.getCreatedOn());
    offerEntity.setCreatedBy(dto.getCreatedBy());
    offerEntity.setUpdatedOn(dto.getUpdatedOn());
    offerEntity.setUpdatedBy(dto.getUpdatedBy());
    offerEntity.setApplicationInformation(dto.getApplicationInformation());
    offerEntity.setExcludedFirstLevel(dto.getExcludedFirstLevel());
    offerEntity.setNachtragSubmittent(dto.getNachtragSubmittent());
    offerEntity.setManualAmount(dto.getManualAmount());
    return offerEntity;
  }

  /**
   * To offer.
   *
   * @param dtoList the dto list
   * @return the list
   */
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

  /**
   * Coverts a offer entity to an offer DTO via custom SubmittentMapper.
   *
   * @param entity the Offer entity
   * @return the Offer DTO list
   */
  public OfferDTO toCustomOfferDTO(OfferEntity entity) {

    OfferDTO offerDTO = toOfferDTO(entity);
    if (offerDTO != null) {
      offerDTO.setSubmittent(customSubmittentMapper.toSubmittentDTO(entity.getSubmittent()));
    }
    return offerDTO;
  }

  /**
   * To offer DTO.
   *
   * @param entity the entity
   * @return the offer DTO
   */
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
    offerDTO.setOperatingCostsAmount(entity.getOperatingCostsAmount());
    offerDTO.setqExRank(entity.getqExRank());
    offerDTO.setAwardRecipientFreeTextField(entity.getAwardRecipientFreeTextField());
    offerDTO.setAwardRank(entity.getAwardRank());
    offerDTO.setAwardTotalScore(entity.getAwardTotalScore());
    offerDTO.setMigratedProcedure(entity.getMigratedProcedure());
    offerDTO.setCreatedOn(entity.getCreatedOn());
    offerDTO.setCreatedBy(entity.getCreatedBy());
    offerDTO.setUpdatedOn(entity.getUpdatedOn());
    offerDTO.setUpdatedBy(entity.getUpdatedBy());
    offerDTO.setApplicationInformation(entity.getApplicationInformation());
    offerDTO.setExclusionReason(entity.getExclusionReason());
    // Return a set of MasterListValueHistoryDTO instead of MasterListValueDTOs.
    // Convert MasterListValueEntity set to MasterListValueHistoryDTO set.
    offerDTO.setExclusionReasons(
      masterListValueEntitySetToMasterListValueDTOSet(entity.getExclusionReasons()));
    offerDTO.setExclusionReasonsFirstLevel(
      masterListValueEntitySetToMasterListValueDTOSet(entity.getExclusionReasonsFirstLevel()));
    offerDTO.setExcludedFirstLevel(entity.getExcludedFirstLevel());
    offerDTO.setNachtragSubmittent(entity.getNachtragSubmittent());
    offerDTO.setManualAmount(entity.getManualAmount());
    return offerDTO;
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

  /**
   * To offer DTO.
   *
   * @param offerList the offer list
   * @return the list
   */
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

  /**
   * Converts a listo of offerentities to OfferDTO list via custom mapping
   *
   * @param offerList the offer list
   * @return the OfferDTO list
   */

  public List<OfferDTO> toCustomOfferDTO(List<OfferEntity> offerList) {
    if (offerList == null) {
      return Collections.emptyList();
    }
    List<OfferDTO> list = new ArrayList<>();
    for (OfferEntity offerEntity : offerList) {
      list.add(toCustomOfferDTO(offerEntity));
    }

    return list;
  }
}
