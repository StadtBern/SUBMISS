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

import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.impl.model.CriterionEntity;
import ch.bern.submiss.services.impl.model.SubcriterionEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SubmissionMapper.class})
public abstract class CriterionDTOMapper {

  public static final CriterionDTOMapper INSTANCE = Mappers.getMapper(CriterionDTOMapper.class);

  public CriterionEntity toCriterion(CriterionDTO dto) {
    if (dto == null) {
      return null;
    }

    CriterionEntity criterionEntity = new CriterionEntity();

    SubmissionEntity submission = new SubmissionEntity();
    criterionEntity.setSubmission(submission);

    submission.setId(dto.getSubmission());
    criterionEntity.setId(dto.getId());
    criterionEntity.setVersion(dto.getVersion());
    criterionEntity.setCriterionText(dto.getCriterionText());
    if (dto.getWeighting() != null) {
      criterionEntity.setWeighting(dto.getWeighting());
    }
    criterionEntity.setCriterionType(dto.getCriterionType());
    if (dto.getSubcriterion() != null) {
      criterionEntity.setSubcriteria(dtoToSubcriterionEntity(dto));
    }
    if (dto.getCreatedOn() != null) {
      criterionEntity.setCreatedOn(dto.getCreatedOn());
    }
    if (dto.getUpdatedOn() != null) {
      criterionEntity.setUpdatedOn(dto.getUpdatedOn());
    }
    return criterionEntity;
  }

  private List<SubcriterionEntity> dtoToSubcriterionEntity(CriterionDTO dto) {
    if (dto == null) {
      return Collections.emptyList();
    }
    List<SubcriterionEntity> subcriterionEntityList = new ArrayList<>();
    List<SubcriterionDTO> subcriterionDTOList = dto.getSubcriterion();
    for (SubcriterionDTO subcriterionDTO : subcriterionDTOList) {
      SubcriterionEntity subcriterionEntity = new SubcriterionEntity();
      if (subcriterionDTO.getId() != null) {
        subcriterionEntity.setId(subcriterionDTO.getId());
      }
      if (subcriterionDTO.getSubcriterionText() != null) {
        subcriterionEntity.setSubcriterionText(subcriterionDTO.getSubcriterionText());
      }
      if (subcriterionDTO.getWeighting() != null) {
        subcriterionEntity.setWeighting(subcriterionDTO.getWeighting());
      }
      if (subcriterionDTO.getCreatedOn() != null) {
        subcriterionEntity.setCreatedOn(subcriterionDTO.getCreatedOn());
      }
      if (subcriterionDTO.getUpdatedOn() != null) {
        subcriterionEntity.setUpdatedOn(subcriterionDTO.getUpdatedOn());
      }
      subcriterionEntity.setVersion(subcriterionDTO.getVersion());
      subcriterionEntityList.add(subcriterionEntity);
    }
    return subcriterionEntityList;
  }

  private List<SubcriterionDTO> entityToSubcriterionDTO(CriterionEntity entity) {
    if (entity == null) {
      return Collections.emptyList();
    }
    List<SubcriterionDTO> subcriterionDTOList = new ArrayList<>();
    List<SubcriterionEntity> subcriterionEntityList = entity.getSubcriteria();
    for (SubcriterionEntity subcriterionEntity : subcriterionEntityList) {
      SubcriterionDTO subcriterionDTO = new SubcriterionDTO();
      if (subcriterionEntity.getId() != null) {
        subcriterionDTO.setId(subcriterionEntity.getId());
      }
      if (subcriterionEntity.getSubcriterionText() != null) {
        subcriterionDTO.setSubcriterionText(subcriterionEntity.getSubcriterionText());
      }
      if (subcriterionEntity.getWeighting() != null) {
        subcriterionDTO.setWeighting(subcriterionEntity.getWeighting());
      }
      subcriterionDTO.setCreatedOn(subcriterionEntity.getCreatedOn());
      if (subcriterionEntity.getUpdatedOn() != null) {
        subcriterionDTO.setUpdatedOn(entity.getUpdatedOn());
      }
      subcriterionDTO.setVersion(subcriterionEntity.getVersion());
      subcriterionDTOList.add(subcriterionDTO);
    }
    return subcriterionDTOList;
  }

  public List<CriterionEntity> toCriterion(List<CriterionDTO> dtoList) {
    if (dtoList == null) {
      return Collections.emptyList();
    }

    List<CriterionEntity> list = new ArrayList<>();
    for (CriterionDTO criterionDTO : dtoList) {
      list.add(toCriterion(criterionDTO));
    }

    return list;
  }

  public List<CriterionDTO> toCriterionDTO(List<CriterionEntity> criterionList) {
    if (criterionList == null) {
      return Collections.emptyList();
    }

    List<CriterionDTO> list = new ArrayList<>();
    for (CriterionEntity criterionEntity : criterionList) {
      list.add(toCriterionDTO(criterionEntity));
    }

    return list;
  }

  public CriterionDTO toCriterionDTO(CriterionEntity entity) {
    if (entity == null) {
      return null;
    }

    CriterionDTO criterionDTO = new CriterionDTO();

    criterionDTO.setSubmission(entitySubmissionId(entity));
    criterionDTO.setId(entity.getId());
    criterionDTO.setVersion(entity.getVersion());
    criterionDTO.setCriterionText(entity.getCriterionText());
    if (entity.getWeighting() != null) {
      criterionDTO.setWeighting(entity.getWeighting());
    }
    criterionDTO.setCriterionType(entity.getCriterionType());
    if (entity.getSubcriteria() != null) {
      criterionDTO.setSubcriterion(entityToSubcriterionDTO(entity));
    }
    criterionDTO.setCreatedOn(entity.getCreatedOn());
    if (entity.getUpdatedOn() != null) {
      criterionDTO.setUpdatedOn(entity.getUpdatedOn());
    }
    return criterionDTO;
  }

  private String entitySubmissionId(CriterionEntity criterionEntity) {

    if (criterionEntity == null) {
      return null;
    }
    SubmissionEntity submission = criterionEntity.getSubmission();
    if (submission == null) {
      return null;
    }
    String id = submission.getId();
    if (id == null) {
      return null;
    }
    return id;
  }

}
