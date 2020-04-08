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

import ch.bern.submiss.services.api.dto.CompanySearchDTO;
import ch.bern.submiss.web.forms.CompanySearchForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyMapper.class, CompanyFilterMapper.class, MasterListValueHistoryMapper.class})
public abstract class CompanySearchMapper {

  public static final CompanySearchMapper INSTANCE = Mappers.getMapper(CompanySearchMapper.class);

  @Mapping(target = "archived", defaultValue = "0")
  public abstract CompanySearchDTO toCompanySearchDTO(CompanySearchForm companyForm);
}
