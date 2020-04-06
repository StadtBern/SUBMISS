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
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.SubmissAuditProjectDTO;
import ch.bern.submiss.services.impl.model.SubmissAuditProjectEntity;

/**
 * The Class SubmissAuditProjectMapper.
 */
@Mapper
public abstract class SubmissAuditProjectMapper {

  /** The Constant INSTANCE. */
  public static final SubmissAuditProjectMapper INSTANCE =
      Mappers.getMapper(SubmissAuditProjectMapper.class);

  /**
   * Converts a list of SubmissAuditProjectEntity to a list of SubmissAuditProjectDTO.
   *
   * @param submissAuditProjectEntities the SubmissAuditProjectEntity list
   * @return the SubmissAuditProjectDTO list
   */
  public List<SubmissAuditProjectDTO> auditProjectEntityToAuditProjectDTO(
      List<SubmissAuditProjectEntity> submissAuditProjectEntities) {
    List<SubmissAuditProjectDTO> submissAuditProjectDTOs = new ArrayList<>();
    for (SubmissAuditProjectEntity submissAuditProjectEntity : submissAuditProjectEntities) {
      submissAuditProjectDTOs.add(auditProjectEntityToAuditProjectDTO(submissAuditProjectEntity));
    }

    return submissAuditProjectDTOs;
  }

  /**
   * Converts a SubmissAuditProjectEntity to a SubmissAuditProjectDTO.
   *
   * @param submissAuditProjectEntity the submiss audit project entity
   * @return the SubmissAuditProject DTO
   */
  public SubmissAuditProjectDTO auditProjectEntityToAuditProjectDTO(
      SubmissAuditProjectEntity submissAuditProjectEntity) {
    if (submissAuditProjectEntity == null) {
      return null;
    }
    return getSubmissAuditProjectDTO(submissAuditProjectEntity);
  }

  /**
   * Gets the SubmissAuditProject DTO.
   *
   * @param submissAuditProjectEntity the submiss audit project entity
   * @return SubmissAuditProject DTO.
   */
  private SubmissAuditProjectDTO getSubmissAuditProjectDTO(
      SubmissAuditProjectEntity submissAuditProjectEntity) {

    SubmissAuditProjectDTO submissAuditProjectDTO = new SubmissAuditProjectDTO();
    submissAuditProjectDTO.setId(submissAuditProjectEntity.getId());
    submissAuditProjectDTO.setUserName(submissAuditProjectEntity.getUserName());
    submissAuditProjectDTO.setShortDescription(submissAuditProjectEntity.getShortDescription());
    submissAuditProjectDTO.setCreatedOn(submissAuditProjectEntity.getCreatedOn());
    submissAuditProjectDTO.setObjectName(submissAuditProjectEntity.getObjectName());
    submissAuditProjectDTO.setProjectName(submissAuditProjectEntity.getProjectName());
    submissAuditProjectDTO.setWorkType(submissAuditProjectEntity.getWorkType());
    submissAuditProjectDTO.setReason(submissAuditProjectEntity.getReason());
    submissAuditProjectDTO.setResourceKey(submissAuditProjectEntity.getResourceKey());

    return submissAuditProjectDTO;

  }


}
