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
import ch.bern.submiss.services.api.dto.CountryDTO;
import ch.bern.submiss.web.forms.CountryForm;

@Mapper
public abstract class CountryMapper {

  public static final CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

  public abstract CountryDTO toCountryDTO(CountryForm countryForm);

  public abstract List<CountryDTO> toCountryDTO(List<CountryForm> countryForms);

  public abstract CountryForm toCountryForm(CountryDTO countryDTO);

  public abstract List<CountryForm> toCountryForm(List<CountryDTO> countryDTO);

}
