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

import java.util.List;
import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.impl.model.DepartmentHistoryEntity;

@Mapper(uses = {TenantMapper.class, MasterListMapper.class, DirectorateHistoryMapper.class,DirectorateMapper.class,
    DepartmentMapper.class})
public abstract class DepartmentHistoryMapper {
  
  public static final DepartmentHistoryMapper INSTANCE = Mappers.getMapper(DepartmentHistoryMapper.class);

  @Mapping(source = "directorate.directorateId", target ="directorateEnity")
  public abstract DepartmentHistoryEntity toDepartmentHistory(DepartmentHistoryDTO dto);

  public abstract List<DepartmentHistoryEntity> toDepartmentHistory(
    List<DepartmentHistoryDTO> dtoList);

  
  @Mapping(source = "directorateEnity", target = "directorate.directorateId")
  public abstract DepartmentHistoryDTO toDepartmentHistoryDTO(DepartmentHistoryEntity entity,
    @Context Map<String, DirectorateHistoryDTO> activeDirectorates
  );

	public abstract List<DepartmentHistoryDTO> toDepartmentHistoryDTO(
	  List<DepartmentHistoryEntity> departmentHistoryList,
    @Context Map<String, DirectorateHistoryDTO> activeDirectorates);
  	@AfterMapping
    public void setSD(DepartmentHistoryEntity departmentHistoryEntity, @MappingTarget DepartmentHistoryDTO departmentHistoryDTO,
			@Context Map<String, DirectorateHistoryDTO> activeDirectorates) {
		  if (departmentHistoryEntity.getDirectorateEnity() != null) {
			  departmentHistoryDTO.setDirectorate(activeDirectorates.get(departmentHistoryEntity.getDirectorateEnity().getId()));
		}
	}
}
