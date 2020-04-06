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

import ch.bern.submiss.services.api.dto.ExclusionReasonDTO;
import ch.bern.submiss.web.forms.ExclusionReasonForm;

@Mapper(uses = {MasterListValueHistoryMapper.class})
public abstract class ExclusionReasonFormMapper {
  public static final ExclusionReasonFormMapper INSTANCE =
      Mappers.getMapper(ExclusionReasonFormMapper.class);

  public abstract ExclusionReasonDTO toExclusionReasonDTO(ExclusionReasonForm form);

  public abstract List<ExclusionReasonDTO> toExclusionReasonDTO(
      List<ExclusionReasonForm> formList);

  public abstract ExclusionReasonForm toExclusionReasonForm(ExclusionReasonDTO dto);

  public abstract List<ExclusionReasonForm> toExclusionReasonForm(
      List<ExclusionReasonDTO> dtoList);
}
