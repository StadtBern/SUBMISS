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
import ch.bern.submiss.services.api.dto.SubmissAuditCompanyDTO;
import ch.bern.submiss.services.impl.model.SubmissAuditCompanyEntity;

/**
 * The Class SubmissAuditCompanyMapper.
 */
@Mapper
public abstract class SubmissAuditCompanyMapper {

  /** The Constant INSTANCE. */
  public static final SubmissAuditCompanyMapper INSTANCE =
      Mappers.getMapper(SubmissAuditCompanyMapper.class);

  /**
   * Converts a list of SubmissAuditCompanyEntity to a list of SubmissAuditCompanyDTO.
   *
   * @param submissAuditCompanyEntities the SubmissAuditCompanyEntity list
   * @return the SubmissAuditCompanyDTO list
   */
  public List<SubmissAuditCompanyDTO> auditCompanyEntityToAuditCompanyDTO(
      List<SubmissAuditCompanyEntity> submissAuditCompanyEntities) {
    List<SubmissAuditCompanyDTO> submissAuditCompanyDTOs = new ArrayList<>();
    for (SubmissAuditCompanyEntity submissAuditCompanyEntity : submissAuditCompanyEntities) {
      submissAuditCompanyDTOs.add(auditCompanyEntityToAuditCompanyDTO(submissAuditCompanyEntity));
    }

    return submissAuditCompanyDTOs;
  }

  /**
   * Converts a SubmissAuditCompany to a SubmissAuditCompanyDTO.
   *
   * @param auditCompanyEntity the audit company entity
   * @return the SubmissAuditCompany DTO
   */
  public SubmissAuditCompanyDTO auditCompanyEntityToAuditCompanyDTO(
      SubmissAuditCompanyEntity auditCompanyEntity) {
    if (auditCompanyEntity == null) {
      return null;
    }
    return getSubmissAuditCompanyDTO(auditCompanyEntity);
  }

  /**
   * Gets the SubmissAuditCompany DTO.
   *
   * @param submissAuditCompanyEntity the submiss audit company entity
   * @return SubmissAuditCompany DTO.
   */
  private SubmissAuditCompanyDTO getSubmissAuditCompanyDTO(
      SubmissAuditCompanyEntity submissAuditCompanyEntity) {

    SubmissAuditCompanyDTO submissAuditCompanyDTO = new SubmissAuditCompanyDTO();
    submissAuditCompanyDTO.setId(submissAuditCompanyEntity.getId());
    submissAuditCompanyDTO.setUserName(submissAuditCompanyEntity.getUserName());
    submissAuditCompanyDTO.setShortDescription(submissAuditCompanyEntity.getShortDescription());
    submissAuditCompanyDTO.setCreatedOn(submissAuditCompanyEntity.getCreatedOn());
    submissAuditCompanyDTO.setCompanyName(submissAuditCompanyEntity.getCompanyName());
    submissAuditCompanyDTO.setProofStatusFabe(submissAuditCompanyEntity.getProofStatusFabe());
    submissAuditCompanyDTO.setResourceKey(submissAuditCompanyEntity.getResourceKey());
    return submissAuditCompanyDTO;

  }

}
