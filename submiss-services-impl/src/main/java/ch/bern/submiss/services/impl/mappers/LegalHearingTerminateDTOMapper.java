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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ch.bern.submiss.services.api.dto.LegalHearingTerminateDTO;
import ch.bern.submiss.services.api.dto.MasterListValueDTO;
import ch.bern.submiss.services.impl.model.LegalHearingTerminateEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;

@Mapper(uses = {SubmissionMapper.class, MasterListValueMapper.class})
public abstract class LegalHearingTerminateDTOMapper {
  public static final LegalHearingTerminateDTOMapper INSTANCE =
      Mappers.getMapper(LegalHearingTerminateDTOMapper.class);

  private final SubmissionMapper submissionMapper = Mappers.getMapper( SubmissionMapper.class );


  public LegalHearingTerminateDTO toLegalHearingTerminateDTO(LegalHearingTerminateEntity entity) {
      if ( entity == null ) {
          return null;
      }

      LegalHearingTerminateDTO legalHearingTerminateDTO = new LegalHearingTerminateDTO();

      legalHearingTerminateDTO.setDeadline( entity.getDeadline() );
      legalHearingTerminateDTO.setReason( entity.getReason() );
      Set<MasterListValueDTO> set = submissionMapper.masterListValueEntitySetToMasterListValueDTOSet( entity.getTerminationReason() );
      if ( set != null ) {
          legalHearingTerminateDTO.setTerminationReason( set );
      }
      legalHearingTerminateDTO.setId( entity.getId() );

      return legalHearingTerminateDTO;
  }


  public List<LegalHearingTerminateDTO> toLegalHearingTerminateDTO(List<LegalHearingTerminateEntity> entityList) {
      if ( entityList == null ) {
          return Collections.emptyList();
      }

      List<LegalHearingTerminateDTO> list = new ArrayList<>();
      for ( LegalHearingTerminateEntity legalHearingTerminateEntity : entityList ) {
          list.add( toLegalHearingTerminateDTO( legalHearingTerminateEntity ) );
      }

      return list;
  }


  public LegalHearingTerminateEntity toLegalHearingTerminateEntity(LegalHearingTerminateDTO dto) {
      if ( dto == null ) {
          return null;
      }

      LegalHearingTerminateEntity legalHearingTerminateEntity = new LegalHearingTerminateEntity();

      legalHearingTerminateEntity.setId( dto.getId() );
      legalHearingTerminateEntity.setDeadline( dto.getDeadline() );
      legalHearingTerminateEntity.setReason( dto.getReason() );
      Set<MasterListValueEntity> set = submissionMapper.masterListValueDTOSetToMasterListValueEntitySet( dto.getTerminationReason() );
      if ( set != null ) {
          legalHearingTerminateEntity.setTerminationReason( set );
      }

      return legalHearingTerminateEntity;
  }


  public List<LegalHearingTerminateEntity> toLegalHearingTerminateEntity(List<LegalHearingTerminateDTO> dtoList) {
      if ( dtoList == null ) {
          return Collections.emptyList();
      }

      List<LegalHearingTerminateEntity> list = new ArrayList<>();
      for ( LegalHearingTerminateDTO legalHearingTerminateDTO : dtoList ) {
          list.add( toLegalHearingTerminateEntity( legalHearingTerminateDTO ) );
      }

      return list;
  }
}
