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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.ProjectDTO;
import ch.bern.submiss.services.impl.model.ProjectEntity;

/**
 * Mapper for Project
 */
@Mapper(uses = {TenantMapper.class, CompanyMapper.class, DepartmentHistoryMapper.class,
    MasterListValueHistoryMapper.class, DepartmentMapper.class, MasterListValueMapper.class})
public abstract class ProjectMapper {

  public static final ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

  @Mapping(source = "department.departmentId", target = "department")
  @Mapping(source = "objectName.masterListValueId", target = "objectName")
  @Mapping(source = "procedure.masterListValueId", target = "procedure")
  public abstract ProjectEntity toProject(ProjectDTO dto);

  public abstract List<ProjectEntity> toProject(List<ProjectDTO> dtoList);

  @Mapping(source = "department", target = "department.departmentId")
  @Mapping(source = "objectName", target = "objectName.masterListValueId")
  @Mapping(source = "procedure", target = "procedure.masterListValueId")
  public abstract ProjectDTO toProjectDTO(ProjectEntity entity);

  public abstract List<ProjectDTO> toProjectDTO(List<ProjectEntity> projectList);

}
