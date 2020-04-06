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

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.web.forms.SubmissionForm;

@Mapper(uses = {TenantMapper.class, CompanyMapper.class, MasterListValueHistoryMapper.class,
    ProjectMapper.class, WorkTypeMapper.class},nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class SubmissionMapper {

  public static final SubmissionMapper INSTANCE = Mappers.getMapper(SubmissionMapper.class);

  public abstract SubmissionDTO toSubmissionDTO(SubmissionForm submissionForm);

  public abstract List<SubmissionDTO> toSubmissionDTO(List<SubmissionForm> submissionForms);

  public abstract List<SubmissionForm> toSubmissionForm(List<SubmissionDTO> submissionDTOs);

  @BeanMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
  public abstract SubmissionForm toSubmissionForm(SubmissionDTO submissionDTO);

}
