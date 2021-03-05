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

import ch.bern.submiss.services.api.dto.NachtragDTO;
import ch.bern.submiss.services.impl.model.NachtragEntity;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class NachtragDTOMapper {

  public static final NachtragDTOMapper INSTANCE = Mappers.getMapper(NachtragDTOMapper.class);

  private final OfferDTOMapper offerDTOMapper = Mappers.getMapper(OfferDTOMapper.class);

  public NachtragDTO toNachtragDTO(NachtragEntity entity) {
    if (entity == null) {
      return null;
    }

    NachtragDTO nachtragDTO = new NachtragDTO();

    nachtragDTO.setId(entity.getId());
    nachtragDTO.setVersion(entity.getVersion());
    nachtragDTO.setCreatedOn(entity.getCreatedOn());
    nachtragDTO.setUpdatedOn(entity.getUpdatedOn());
    nachtragDTO.setCreatedBy(entity.getCreatedBy());
    nachtragDTO.setUpdatedBy(entity.getUpdatedBy());
    nachtragDTO.setNotes(entity.getNotes());
    nachtragDTO.setGrossAmount(entity.getGrossAmount());
    nachtragDTO.setDiscount(entity.getDiscount());
    nachtragDTO.setIsDiscountPercentage(entity.getIsDiscountPercentage());
    nachtragDTO.setVat(entity.getVat());
    nachtragDTO.setDiscount2(entity.getDiscount2());
    nachtragDTO.setIsDiscount2Percentage(entity.getIsDiscount2Percentage());
    nachtragDTO.setIsVatPercentage(entity.getIsVatPercentage());
    nachtragDTO.setAmount(entity.getAmount());
    nachtragDTO.setOffer(offerDTOMapper.toOfferDTO(entity.getOffer()));
    nachtragDTO.setNachtragName(entity.getNachtragName());
    nachtragDTO.setNachtragDate(entity.getNachtragDate());
    nachtragDTO.setClosed(entity.getClosed());
    nachtragDTO.setTitle(entity.getTitle());
    nachtragDTO.setDiscountDescription(entity.getDiscountDescription());
    nachtragDTO.setBuildingCosts(entity.getBuildingCosts());
    nachtragDTO.setIsBuildingCostsPercentage(entity.getIsBuildingCostsPercentage());
    nachtragDTO.setBuildingCostsValue(entity.getBuildingCostsValue());
    nachtragDTO.setVatValue(entity.getVatValue());
    nachtragDTO.setDiscount1Value(entity.getDiscount1Value());
    nachtragDTO.setDiscount2Value(entity.getDiscount2Value());
    nachtragDTO.setAmountInclusive(entity.getAmountInclusive());
    return nachtragDTO;
  }

  public List<NachtragDTO> toNachtragDTO(List<NachtragEntity> entityList) {
    if (entityList == null) {
      return null;
    }

    List<NachtragDTO> list = new ArrayList<NachtragDTO>(entityList.size());
    for (NachtragEntity nachtragEntity : entityList) {
      list.add(toNachtragDTO(nachtragEntity));
    }

    return list;
  }

  public NachtragEntity toNachtrag(NachtragDTO dto) {
    if (dto == null) {
      return null;
    }

    NachtragEntity nachtragEntity = new NachtragEntity();

    nachtragEntity.setId(dto.getId());
    nachtragEntity.setVersion(dto.getVersion());
    nachtragEntity.setCreatedOn(dto.getCreatedOn());
    nachtragEntity.setUpdatedOn(dto.getUpdatedOn());
    nachtragEntity.setCreatedBy(dto.getCreatedBy());
    nachtragEntity.setUpdatedBy(dto.getUpdatedBy());
    nachtragEntity.setNotes(dto.getNotes());
    nachtragEntity.setGrossAmount(dto.getGrossAmount());
    nachtragEntity.setDiscount(dto.getDiscount());
    nachtragEntity.setIsDiscountPercentage(dto.getIsDiscountPercentage());
    nachtragEntity.setVat(dto.getVat());
    nachtragEntity.setDiscount2(dto.getDiscount2());
    nachtragEntity.setIsDiscount2Percentage(dto.getIsDiscount2Percentage());
    nachtragEntity.setIsVatPercentage(dto.getIsVatPercentage());
    nachtragEntity.setAmount(dto.getAmount());
    nachtragEntity.setOffer(offerDTOMapper.toOffer(dto.getOffer()));
    nachtragEntity.setNachtragName(dto.getNachtragName());
    nachtragEntity.setNachtragDate(dto.getNachtragDate());
    nachtragEntity.setClosed(dto.getClosed());
    nachtragEntity.setTitle(dto.getTitle());
    nachtragEntity.setDiscountDescription(dto.getDiscountDescription());
    nachtragEntity.setBuildingCosts(dto.getBuildingCosts());
    nachtragEntity.setIsBuildingCostsPercentage(dto.getIsBuildingCostsPercentage());
    nachtragEntity.setBuildingCostsValue(dto.getBuildingCostsValue());
    nachtragEntity.setVatValue(dto.getVatValue());
    nachtragEntity.setDiscount1Value(dto.getDiscount1Value());
    nachtragEntity.setDiscount2Value(dto.getDiscount2Value());
    nachtragEntity.setAmountInclusive(dto.getAmountInclusive());
    return nachtragEntity;
  }

  public List<NachtragEntity> toNachtrag(List<NachtragDTO> dtoList) {
    if (dtoList == null) {
      return null;
    }

    List<NachtragEntity> list = new ArrayList<NachtragEntity>(dtoList.size());
    for (NachtragDTO nachtragDTO : dtoList) {
      list.add(toNachtrag(nachtragDTO));
    }

    return list;
  }
}
