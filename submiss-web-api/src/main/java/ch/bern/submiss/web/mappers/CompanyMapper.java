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
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.web.forms.CompanyForm;

@Mapper(uses = {TenantMapper.class, CountryMapper.class, CountryHistoryMapper.class,
    MasterListValueHistoryMapper.class})
public abstract class CompanyMapper {

  public static final CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

  @Mapping(source = "workTypes", target = "workTypes")
  public abstract CompanyDTO toCompanyDTO(CompanyForm companyForm);

  public abstract List<CompanyDTO> toCompanyDTO(List<CompanyForm> companyForms);

  public abstract CompanyForm toCompanyForm(CompanyDTO companyDTO);

  public abstract List<CompanyForm> toCompanyForm(List<CompanyDTO> companyDTOS);


}
