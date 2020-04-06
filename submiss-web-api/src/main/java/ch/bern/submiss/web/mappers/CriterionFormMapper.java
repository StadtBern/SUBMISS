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

package ch.bern.submiss.web.mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.web.forms.CriterionForm;
import ch.bern.submiss.web.forms.SubcriterionForm;

@Mapper(uses = {SubmissionMapper.class, SubcriterionFormMapper.class})
public abstract class CriterionFormMapper {

  public static final CriterionFormMapper INSTANCE = Mappers.getMapper(CriterionFormMapper.class);

  public CriterionDTO toCriterionDTO(CriterionForm criterionForm) {
    if (criterionForm == null) {
      return null;
    }

    CriterionDTO criterionDTO = new CriterionDTO();

    criterionDTO.setId(criterionForm.getId());
    criterionDTO.setSubmission(criterionForm.getSubmission());
    criterionDTO.setCriterionText(criterionForm.getCriterionText());
    criterionDTO.setWeighting(criterionForm.getWeighting());
    criterionDTO.setCriterionType(criterionForm.getCriterionType());
    criterionDTO.setSubcriterion(formToSubcriterionDTO(criterionForm));
    return criterionDTO;
  }

  private List<SubcriterionDTO> formToSubcriterionDTO(CriterionForm criterionForm) {
    if (criterionForm == null) {
      return Collections.emptyList();
    }
    List<SubcriterionDTO> subcriterionDTOList = new ArrayList<>();
    List<SubcriterionForm> subcriterionFormList = criterionForm.getSubcriterion();
    if (subcriterionFormList != null) {
      for (SubcriterionForm subcriterionForm : subcriterionFormList) {
        SubcriterionDTO subcriterionDTO = new SubcriterionDTO();
        if (subcriterionForm.getId() != null) {
          subcriterionDTO.setId(subcriterionForm.getId());
        }
        if (subcriterionForm.getSubcriterionText() != null) {
          subcriterionDTO.setSubcriterionText(subcriterionForm.getSubcriterionText());
        }
        if (subcriterionForm.getWeighting() != null) {
          subcriterionDTO.setWeighting(subcriterionForm.getWeighting());
        }
        subcriterionDTOList.add(subcriterionDTO);
      }
    }
    return subcriterionDTOList;
  }

  private List<SubcriterionForm> dtoToSubcriterionForm(CriterionDTO dto) {
    if (dto == null) {
      return Collections.emptyList();
    }
    List<SubcriterionForm> subcriterionFormList = new ArrayList<>();
    List<SubcriterionDTO> subcriterionDTOList = dto.getSubcriterion();
    if (subcriterionDTOList != null) {
      for (SubcriterionDTO subcriterionDTO : subcriterionDTOList) {
        SubcriterionForm subcriterionForm = new SubcriterionForm();
        if (subcriterionDTO.getId() != null) {
          subcriterionForm.setId(subcriterionDTO.getId());
        }
        if (subcriterionDTO.getSubcriterionText() != null) {
          subcriterionForm.setSubcriterionText(subcriterionDTO.getSubcriterionText());
        }
        if (subcriterionDTO.getWeighting() != null) {
          subcriterionForm.setWeighting(subcriterionDTO.getWeighting());
        }
        subcriterionFormList.add(subcriterionForm);
      }
    }
    return subcriterionFormList;
  }

  public List<CriterionDTO> toCriterionDTO(List<CriterionForm> criterionForms) {
    if (criterionForms == null) {
      return Collections.emptyList();
    }

    List<CriterionDTO> list = new ArrayList<>();
    for (CriterionForm criterionForm : criterionForms) {
      list.add(toCriterionDTO(criterionForm));
    }

    return list;
  }


  public CriterionForm toCriterionForm(CriterionDTO criterionDTO) {
    if (criterionDTO == null) {
      return null;
    }

    CriterionForm criterionForm = new CriterionForm();

    criterionForm.setId(criterionDTO.getId());
    criterionForm.setSubmission(criterionDTO.getSubmission());
    criterionForm.setCriterionText(criterionDTO.getCriterionText());
    criterionForm.setWeighting(criterionDTO.getWeighting());
    criterionForm.setCriterionType(criterionDTO.getCriterionType());
    criterionForm.setSubcriterion(dtoToSubcriterionForm(criterionDTO));
    return criterionForm;
  }

  public List<CriterionForm> toCriterionForm(List<CriterionDTO> criterionDTOs) {
    if (criterionDTOs == null) {
      return Collections.emptyList();
    }

    List<CriterionForm> list = new ArrayList<>();
    for (CriterionDTO criterionDTO : criterionDTOs) {
      list.add(toCriterionForm(criterionDTO));
    }

    return list;
  }

}
