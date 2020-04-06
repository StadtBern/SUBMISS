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

import ch.bern.submiss.services.api.dto.LegalHearingExclusionDTO;
import ch.bern.submiss.web.forms.LegalExclusionForm;

@Mapper(uses = {SubmittentFormMapper.class, ExclusionReasonFormMapper.class})
public abstract class LegalHearingExclusionFormMapper {
  public static final LegalHearingExclusionFormMapper INSTANCE =
      Mappers.getMapper(LegalHearingExclusionFormMapper.class);

  public abstract LegalHearingExclusionDTO toLegalHearingExclusionDTO(LegalExclusionForm form);

  public abstract List<LegalHearingExclusionDTO> toLegalHearingExclusionDTO(
      List<LegalExclusionForm> formList);

  public abstract LegalExclusionForm toLegalHearingExclusionForm(LegalHearingExclusionDTO dto);

  public abstract List<LegalExclusionForm> toLegalHearingExclusionForm(
      List<LegalHearingExclusionDTO> dtoList);
}
