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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.impl.model.ProofHistoryEntity;

@Mapper
public class ProofToTypeDataMapper {

  public static final ProofToTypeDataMapper INSTANCE =
      Mappers.getMapper(ProofToTypeDataMapper.class);

  public MasterListTypeDataDTO toMasterListTypeDataDTO(ProofHistoryEntity proofHistoryEntity,
      Map<String, CountryHistoryDTO> countrySD) {
    return mapProofToTypeData(proofHistoryEntity, countrySD);
  }

  public List<MasterListTypeDataDTO> toMasterListTypeDataDTOs(
      List<ProofHistoryEntity> proofHistoryEntities, Map<String, CountryHistoryDTO> countrySD) {
    List<MasterListTypeDataDTO> typeDataList = new ArrayList<>();
    if (!proofHistoryEntities.isEmpty()) {
      for (ProofHistoryEntity proofHistoryEntity : proofHistoryEntities) {
        typeDataList.add(mapProofToTypeData(proofHistoryEntity, countrySD));
      }
    }
    return typeDataList;
  }

  /** Map ProofHistoryEntity to MasterListTypeDataDTO */
  private MasterListTypeDataDTO mapProofToTypeData(ProofHistoryEntity proofHistoryEntity,
      Map<String, CountryHistoryDTO> countrySD) {
    if (proofHistoryEntity != null) {
      MasterListTypeDataDTO typeData = new MasterListTypeDataDTO();
      typeData.setId(proofHistoryEntity.getId());
      typeData.setDescription(proofHistoryEntity.getProofName());
      typeData.setAddedDescription(proofHistoryEntity.getDescription());
      typeData.setIsActive(BooleanUtils.toBoolean(proofHistoryEntity.getActive()));
      typeData.setValidityPeriod(proofHistoryEntity.getValidityPeriod());
      typeData.setProofOrder(proofHistoryEntity.getProofOrder());
      typeData.setRequired(proofHistoryEntity.getRequired());
      typeData
          .setCountryName(countrySD.get(proofHistoryEntity.getCountry().getId()).getCountryName());
      return typeData;
    }
    return null;
  }
}
