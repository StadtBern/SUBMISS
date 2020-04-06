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

import ch.bern.submiss.services.api.dto.ExclusionReasonDTO;
import ch.bern.submiss.services.api.dto.LegalHearingExclusionDTO;
import ch.bern.submiss.services.impl.model.LegalHearingExclusionEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;

@Mapper(uses = {SubmittentDTOMapper.class, MasterListValueMapper.class})
public abstract class LegalHearingExclusionDTOMapper {
  public static final LegalHearingExclusionDTOMapper INSTANCE =
      Mappers.getMapper(LegalHearingExclusionDTOMapper.class);

  private final SubmittentDTOMapper submittentDTOMapper =
      Mappers.getMapper(SubmittentDTOMapper.class);

  public LegalHearingExclusionDTO toLegalHearingExclusionDTO(LegalHearingExclusionEntity entity) {
    if (entity == null) {
      return null;
    }

    LegalHearingExclusionDTO legalHearingExclusionDTO = new LegalHearingExclusionDTO();

    legalHearingExclusionDTO.setId(entity.getId());
    legalHearingExclusionDTO
        .setSubmittent(submittentDTOMapper.toSubmittentDTO(entity.getSubmittent()));
    legalHearingExclusionDTO.setExclusionDeadline(entity.getExclusionDeadline());
    legalHearingExclusionDTO.setProofsProvided(entity.getProofsProvided());
    legalHearingExclusionDTO.setExistsExlReasons(entity.getExistsExlReasons());
    legalHearingExclusionDTO.setMustCritFulfilled(entity.getMustCritFulfilled());
    legalHearingExclusionDTO.setExclusionReason(entity.getExclusionReason());

    legalHearingExclusionDTO.setCreatedBy(entity.getCreatedBy());
    legalHearingExclusionDTO.setCreatedOn(entity.getCreatedOn());
    legalHearingExclusionDTO.setUpdatedBy(entity.getUpdatedBy());
    legalHearingExclusionDTO.setUpdatedOn(entity.getUpdatedOn());
    legalHearingExclusionDTO.setLevel(entity.getLevel());
    legalHearingExclusionDTO.setFirstLevelExclusionDate(entity.getFirstLevelExclusionDate());
    legalHearingExclusionDTO.setExclusionReasons(
        masterListValueEntitySetToMasterListValueDTOSet(entity.getExclusionReasons()));
    return legalHearingExclusionDTO;
  }

  protected Set<ExclusionReasonDTO> masterListValueEntitySetToMasterListValueDTOSet(
      Set<MasterListValueEntity> set) {
    if (set == null) {
      return Collections.emptySet();
    }
    Set<ExclusionReasonDTO> mset = new HashSet<>();
    for (MasterListValueEntity masterListValueEntity : set) {
      mset.addAll(masterListValueHistoryEntitySetToMasterListValueHistoryDTOSet(
          masterListValueEntity.getMasterListValueHistory()));
    }

    return mset;
  }

  protected Set<ExclusionReasonDTO> masterListValueHistoryEntitySetToMasterListValueHistoryDTOSet(
      Set<MasterListValueHistoryEntity> set) {
    if (set == null) {
      return Collections.emptySet();
    }

    Set<ExclusionReasonDTO> mset = new HashSet<>();
    for (MasterListValueHistoryEntity masterListValueEntity : set) {
      ExclusionReasonDTO exclusionRreasonDTO = new ExclusionReasonDTO();
      exclusionRreasonDTO.setExclusionReason(
          MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(masterListValueEntity));
      mset.add(exclusionRreasonDTO);
    }

    return mset;
  }


  
  public List<LegalHearingExclusionDTO> toLegalHearingExclusionDTO(
      List<LegalHearingExclusionEntity> entityList) {
    if (entityList == null) {
      return Collections.emptyList();
    }

    List<LegalHearingExclusionDTO> list = new ArrayList<>();
    for (LegalHearingExclusionEntity legalHearingExclusionEntity : entityList) {
      list.add(toLegalHearingExclusionDTO(legalHearingExclusionEntity));
    }

    return list;
  }

  public LegalHearingExclusionEntity toLegalHearingExclusionEntity(LegalHearingExclusionDTO dto) {
    if (dto == null) {
      return null;
    }

    LegalHearingExclusionEntity legalHearingExclusionEntity = new LegalHearingExclusionEntity();

    legalHearingExclusionEntity.setId(dto.getId());
    legalHearingExclusionEntity
        .setSubmittent(submittentDTOMapper.toSubmittent(dto.getSubmittent()));
    legalHearingExclusionEntity.setExclusionDeadline(dto.getExclusionDeadline());
    legalHearingExclusionEntity.setProofsProvided(dto.getProofsProvided());
    legalHearingExclusionEntity.setExistsExlReasons(dto.getExistsExlReasons());
    legalHearingExclusionEntity.setMustCritFulfilled(dto.getMustCritFulfilled());
    legalHearingExclusionEntity.setExclusionReason(dto.getExclusionReason());

    legalHearingExclusionEntity.setCreatedBy(dto.getCreatedBy());
    legalHearingExclusionEntity.setCreatedOn(dto.getCreatedOn());
    legalHearingExclusionEntity.setUpdatedBy(dto.getUpdatedBy());
    legalHearingExclusionEntity.setUpdatedOn(dto.getUpdatedOn());
    legalHearingExclusionEntity.setExclusionReasons(setExclusionReasonList(dto));
    legalHearingExclusionEntity.setLevel(dto.getLevel());
    legalHearingExclusionEntity.setFirstLevelExclusionDate(dto.getFirstLevelExclusionDate());
    return legalHearingExclusionEntity;
  }

  private Set<MasterListValueEntity> setExclusionReasonList(LegalHearingExclusionDTO dto) {
    if (dto == null) {
      return Collections.emptySet();
    }
    Set<MasterListValueEntity> exclusionReasonEntities = new HashSet<>();
    for (ExclusionReasonDTO exclusionReasonDTO : dto.getExclusionReasons()) {
      exclusionReasonEntities.add(MasterListValueMapper.INSTANCE
          .toMasterListValue(exclusionReasonDTO.getExclusionReason().getMasterListValueId()));
    }
    return exclusionReasonEntities;
  }

  public List<LegalHearingExclusionEntity> toLegalHearingExclusionEntity(
      List<LegalHearingExclusionDTO> dtoList) {
    if (dtoList == null) {
      return Collections.emptyList();
    }

    List<LegalHearingExclusionEntity> list = new ArrayList<>();
    for (LegalHearingExclusionDTO legalHearingExclusionDTO : dtoList) {
      list.add(toLegalHearingExclusionEntity(legalHearingExclusionDTO));
    }

    return list;
  }
}
