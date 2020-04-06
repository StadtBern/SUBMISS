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

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProjectDTO;
import ch.bern.submiss.services.impl.model.DepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.DirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.ProjectEntity;

/**
 * Mapper for Project
 */
@Mapper(uses = {TenantMapper.class,DirectorateHistoryMapper.class, CompanyMapper.class, DepartmentHistoryMapper.class,
    MasterListValueHistoryMapper.class, DepartmentMapper.class, MasterListValueMapper.class})
public abstract class CustomProjectMapper {

  public static final CustomProjectMapper INSTANCE = Mappers.getMapper(CustomProjectMapper.class);
  private final MasterListValueMapper masterListValueMapper =
      Mappers.getMapper(MasterListValueMapper.class);
  private final DepartmentMapper departmentMapper = Mappers.getMapper(DepartmentMapper.class);
  private final TenantMapper tenantMapper  = Mappers.getMapper(TenantMapper.class);
  private final CompanyMapper companyMapper = Mappers.getMapper(CompanyMapper.class);
  private final DirectorateHistoryMapper diractorateHistoryMapper = Mappers.getMapper(DirectorateHistoryMapper.class);
  
  public ProjectDTO toCustomProjectDTO(ProjectEntity entity) {
    if (entity == null) {
      return null;
    }

    ProjectDTO projectDTO = new ProjectDTO();

    DepartmentHistoryDTO department = setValuesOfDepartmentHistoryDTO(entity.getDepartmentHistory());
    MasterListValueHistoryDTO objectName = setValuesOfMasterListHistoryDTO(entity.getRecentObjectNameHistory());
    MasterListValueHistoryDTO procedure = setValuesOfMasterListHistoryDTO(entity.getRecentProcedureHistory());
    projectDTO.setProcedure(procedure);
    projectDTO.setObjectName(objectName);
    projectDTO.setDepartment(department);

    procedure
        .setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getProcedure()));
    objectName
        .setMasterListValueId(masterListValueMapper.toMasterListValueDTO(entity.getObjectName()));
    department.setDepartmentId(departmentMapper.toDepartmentDTO(entity.getDepartment()));
    projectDTO.setId(entity.getId());
    projectDTO.setProjectName(entity.getProjectName());
    projectDTO.setTenant(tenantMapper.toTenantDTO(entity.getTenant()));
    projectDTO.setGattWto(entity.getGattWto());
    projectDTO.setPmDepartmentName(entity.getPmDepartmentName());
    projectDTO.setPmExternal(companyMapper.toCompanyDTO(entity.getPmExternal()));
    projectDTO.setProjectNumber(entity.getProjectNumber());
    projectDTO.setNotes(entity.getNotes());
    projectDTO.setConstructionPermit(entity.getConstructionPermit());
    projectDTO.setLoanApproval(entity.getLoanApproval());
    projectDTO.setCreatedBy(entity.getCreatedBy());
    projectDTO.setCreatedOn(entity.getCreatedOn());
    

    return projectDTO;
  }
  
  private DepartmentHistoryDTO setValuesOfDepartmentHistoryDTO(
      DepartmentHistoryEntity departmentHistory) {
    DepartmentHistoryDTO departmentDTO = new DepartmentHistoryDTO();
    //deparmenthistory table change
    for(DirectorateHistoryEntity dir: departmentHistory.getDirectorateEnity().getDirectorate()){
    	if(dir.getToDate() == null) {
    	    departmentDTO.setDirectorate(diractorateHistoryMapper.toDirectorateHistoryDTO(dir));

    	}
    }
    departmentDTO.setTenant(TenantMapper.INSTANCE.toTenantDTO(departmentHistory.getTenant()));
    departmentDTO.setShortName(departmentHistory.getShortName());
    departmentDTO.setAddress(departmentHistory.getAddress());
    departmentDTO.setTelephone(departmentHistory.getTelephone());
    departmentDTO.setEmail(departmentHistory.getEmail());
    departmentDTO.setWebsite(departmentHistory.getWebsite());
    departmentDTO.setFax(departmentHistory.getFax());
    departmentDTO.setPostCode(departmentHistory.getPostCode());
    departmentDTO.setLocation(departmentHistory.getLocation());
    departmentDTO.setFromDate(departmentHistory.getFromDate());
    departmentDTO.setToDate(departmentHistory.getToDate());
    departmentDTO.setName(departmentHistory.getName());
    departmentDTO.setInternal(departmentHistory.isInternal());
    return departmentDTO;
  }

  private MasterListValueHistoryDTO setValuesOfMasterListHistoryDTO(
      MasterListValueHistoryEntity historyEntity) {
    MasterListValueHistoryDTO historyDTO = new MasterListValueHistoryDTO();
    historyDTO.setValue1(historyEntity.getValue1());
    historyDTO.setValue2(historyEntity.getValue2());
    historyDTO.setTenant(TenantMapper.INSTANCE.toTenantDTO(historyEntity.getTenant()));
    historyDTO.setFromDate(historyEntity.getFromDate());
    historyDTO.setToDate(historyEntity.getToDate());
    historyDTO.setModifiedOn(historyEntity.getModifiedOn());
    historyDTO.setInternalVersion(historyEntity.getInternalVersion());
    historyDTO.setShortCode(historyEntity.getShortCode());
    return historyDTO;
  }
}
