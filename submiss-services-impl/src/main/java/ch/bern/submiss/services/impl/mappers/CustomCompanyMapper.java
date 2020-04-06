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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;

@Mapper(uses = {TenantMapper.class})
public abstract class CustomCompanyMapper {

  public static final CustomCompanyMapper INSTANCE = Mappers.getMapper(CustomCompanyMapper.class);

  public abstract List<CompanyDTO> companiesToCompaniesDTO(List<CompanyEntity> companyEntities,
      @Context Map<String, MasterListValueHistoryDTO> activeSD);
  
  @Mapping(target = "branches", ignore = true)
  @Mapping(target = "mainCompany", ignore = true)
  @Mapping(target = "companyProofs", ignore = true)
  public abstract CompanyDTO companiesToCompaniesDTO(CompanyEntity entity,
      @Context Map<String, MasterListValueHistoryDTO> activeWorkTypes);

  @AfterMapping
  public void setSD(CompanyEntity entity, @MappingTarget CompanyDTO companyDTO,
      @Context Map<String, MasterListValueHistoryDTO> activeWorkTypes) {
    HashSet<MasterListValueHistoryDTO> workTypesList = new HashSet<>();
    for (MasterListValueEntity workType : entity.getWorkTypes()) {
      workTypesList.add(activeWorkTypes.get(workType.getId()));
    }
    companyDTO.setWorkTypes(workTypesList);
  }
}
