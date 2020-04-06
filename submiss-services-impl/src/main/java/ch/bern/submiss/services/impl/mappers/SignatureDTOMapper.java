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
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.SignatureDTO;
import ch.bern.submiss.services.impl.model.SignatureEntity;

@Mapper(uses = {DepartmentMapper.class,DirectorateMapper.class})
public abstract class SignatureDTOMapper {

  public static final SignatureDTOMapper INSTANCE =
      Mappers.getMapper(SignatureDTOMapper.class);
  
  
  public abstract SignatureEntity toSignatureEntity(
      SignatureDTO dto);

  public abstract List<SignatureEntity> toSignatureEntity(
      List<SignatureDTO> dtoList);
  
  public abstract SignatureDTO toSignatureDTO(
      SignatureEntity entity);

  public abstract List<SignatureDTO> toSignatureDTO(
      List<SignatureEntity> entityList);
  
}
