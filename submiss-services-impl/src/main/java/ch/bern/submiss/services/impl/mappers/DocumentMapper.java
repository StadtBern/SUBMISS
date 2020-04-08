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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import ch.bern.submiss.services.api.constants.DocumentAttributes;
import ch.bern.submiss.services.api.constants.DocumentCreationType;
import ch.bern.submiss.services.api.dto.DocumentDTO;



/**
 * The Class DocumentMapper.
 */
@Mapper
public abstract class DocumentMapper {



  /** The Constant INSTANCE. */
  public static final DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

  /**
   * To version DTO.
   *
   * @param dto the dto
   * @return the version DTO
   */
  public VersionDTO toVersionDTO(DocumentDTO dto) {

    if (dto == null) {
      return null;
    }

    VersionDTO versionDTO = new VersionDTO();
    versionDTO.setId(dto.getId());
    versionDTO.setActive(dto.getActive());
    versionDTO.setCreatedBy(dto.getCreatedBy());
    versionDTO.setCreatedOn(dto.getCreatedOn().getTime());
    versionDTO.setLastModifiedOn(dto.getLastModifiedOn().getTime());
    versionDTO.setMimetype(dto.getMimetype());
    versionDTO.setName(dto.getVersion());
    if (versionDTO.getAttributes() == null) {
      versionDTO.setAttributes(new HashMap<String, String>());
    }
    versionDTO.getAttributes().put(DocumentAttributes.GLOBAL.name(), dto.getGlobal().toString());
    versionDTO.getAttributes().put(DocumentAttributes.VERSION.name(), dto.getVersion());
    versionDTO.getAttributes().put(DocumentAttributes.ACTIVE.name(), dto.getActive().toString());
    versionDTO.getAttributes().put(DocumentAttributes.IS_ADMIN_RIGHTS_ONLY.name(),
        dto.getIsAdminRightsOnly().toString());
    versionDTO.getAttributes().put(DocumentAttributes.TITLE.name(), dto.getTitle());

    return versionDTO;
  }

  /**
   * To version DTO list.
   *
   * @param dtoList the dto list
   * @return the list
   */
  public List<VersionDTO> toVersionDTOList(List<DocumentDTO> dtoList) {
    if (dtoList == null) {
      return Collections.emptyList();
    }

    List<VersionDTO> list = new ArrayList<>();
    for (DocumentDTO documentDTO : dtoList) {
      list.add(toVersionDTO(documentDTO));
    }

    return list;
  }



  /**
   * To document DTO.
   *
   * @param dto the dto
   * @return the document DTO
   */
  public DocumentDTO toDocumentDTO(VersionDTO dto) {
    if (dto == null) {
      return null;
    }

    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setId(dto.getId());
    if(dto.getLastModifiedBy() != null) {
      documentDTO.setCreatedBy(dto.getLastModifiedBy());
    } else {
      documentDTO.setCreatedBy(dto.getCreatedBy());
    }
   
    documentDTO.setCreatedOn(toDate(dto.getCreatedOn()));
    documentDTO.setLastModifiedOn(toDate(dto.getLastModifiedOn()));
    documentDTO.setMimetype(dto.getMimetype());
    documentDTO.setFilename(dto.getFilename());
    documentDTO.setNodeId(dto.getNodeId());


    if (dto.getAttributes() != null) {
      documentDTO.setVersion(dto.getAttributes().get(DocumentAttributes.VERSION.name()));
      documentDTO.setVersionName(Integer.parseInt(dto.getAttributes().get(DocumentAttributes.VERSION.name())));
      documentDTO.setGlobal((dto.getAttributes().get(DocumentAttributes.GLOBAL.name()) != null)
          ? Boolean.parseBoolean(dto.getAttributes().get(DocumentAttributes.GLOBAL.name()))
          : false);
      documentDTO.setActive((dto.getAttributes().get(DocumentAttributes.ACTIVE.name()) != null)
          ? Boolean.parseBoolean(dto.getAttributes().get(DocumentAttributes.ACTIVE.name()))
          : false);
      documentDTO.setDocumentCreationType(DocumentCreationType
          .valueOf(dto.getAttributes().get(DocumentAttributes.DOCUMENT_CREATION_TYPE.name())));
      documentDTO
          .setIsAdminRightsOnly((dto.getAttributes().get(DocumentAttributes.IS_ADMIN_RIGHTS_ONLY.name()) != null)
              ? Boolean.parseBoolean(dto.getAttributes().get(DocumentAttributes.IS_ADMIN_RIGHTS_ONLY.name()))
              : false);
      documentDTO.setTitle(dto.getAttributes().get(DocumentAttributes.TITLE.name()));
      documentDTO.setProjectDocument(
          Boolean.parseBoolean(dto.getAttributes().get(DocumentAttributes.IS_PROJECT_DOCUMENT.name())));
    }

    return documentDTO;

  }

  /**
   * To document DTO list.
   *
   * @param dtoList the dto list
   * @return the list
   */
  public List<DocumentDTO> toDocumentDTOList(List<VersionDTO> dtoList) {
    if (dtoList == null) {
      return Collections.emptyList();
    }

    List<DocumentDTO> list = new ArrayList<>();
    for (VersionDTO versionDTO : dtoList) {
      list.add(toDocumentDTO(versionDTO));
    }

    return list;
  }


  /**
   * To date.
   *
   * @param date the date
   * @return the date
   */
  public Date toDate(Long date) {
    if (date == null) {
      return null;
    }
    return new Date(date);
  }
}

