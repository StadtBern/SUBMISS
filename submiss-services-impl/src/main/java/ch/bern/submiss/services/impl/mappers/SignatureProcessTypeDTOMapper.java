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

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ch.bern.submiss.services.api.dto.SignatureProcessTypeDTO;
import ch.bern.submiss.services.impl.model.SignatureProcessTypeEntity;

@Mapper
public abstract class SignatureProcessTypeDTOMapper {

  public static final SignatureProcessTypeDTOMapper INSTANCE =
      Mappers.getMapper(SignatureProcessTypeDTOMapper.class);

  public abstract SignatureProcessTypeEntity toSignatureProcessTypeEntity(
      SignatureProcessTypeDTO dto,@Context CycleAvoidingMappingContext context);

  public abstract List<SignatureProcessTypeEntity> toSignatureProcessTypeEntity(
      List<SignatureProcessTypeDTO> dtoList,@Context CycleAvoidingMappingContext context);

  public abstract SignatureProcessTypeDTO toSignatureProcessTypeDTO(
      SignatureProcessTypeEntity entity,@Context CycleAvoidingMappingContext context);

  public abstract List<SignatureProcessTypeDTO> toSignatureProcessTypeDTO(
      List<SignatureProcessTypeEntity> entityList,@Context CycleAvoidingMappingContext context);
}
