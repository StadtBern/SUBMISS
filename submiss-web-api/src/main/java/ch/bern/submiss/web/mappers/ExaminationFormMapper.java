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

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.ExaminationDTO;
import ch.bern.submiss.web.forms.ExaminationForm;

@Mapper(uses = {CriterionFormMapper.class, SubcriterionFormMapper.class})
public abstract class ExaminationFormMapper {

  public static final ExaminationFormMapper INSTANCE =
      Mappers.getMapper(ExaminationFormMapper.class);

  public abstract ExaminationDTO toExaminationDTO(ExaminationForm form);

  public abstract List<ExaminationDTO> toExaminationDTO(List<ExaminationForm> formList);

  public abstract ExaminationForm toExaminationForm(ExaminationDTO dto);

  public abstract List<ExaminationForm> toExaminationForm(
      List<ExaminationForm> examinationFormList);
}
