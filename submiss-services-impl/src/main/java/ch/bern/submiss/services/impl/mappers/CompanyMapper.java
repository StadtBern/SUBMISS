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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.CountryEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;

@Mapper(uses = {TenantMapper.class, CountryMapper.class, MasterListValueHistoryMapper.class,
    CountryHistoryMapper.class, MasterListValueMapper.class})
public abstract class CompanyMapper {

  public static final CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

  @Mapping(source = "country", target = "country.countryId")
  public CompanyDTO toCompanyDTO(CompanyEntity entity) {
    if (entity == null) {
      return null;
    }
    CompanyDTO companyDTO = new CompanyDTO();
    companyDTO.setAddInfo(entity.getAddInfo());
    companyDTO.setAddress1(entity.getAddress1());
    companyDTO.setAddInfo(entity.getAddInfo());
    companyDTO.setAddress2(entity.getAddress2());
    companyDTO.setCompanyEmail(entity.getCompanyEmail());
    companyDTO.setCompanyFax(entity.getCompanyFax());
    companyDTO.setCompanyName(entity.getCompanyName());
    companyDTO.setCompanyTel(entity.getCompanyTel());
    companyDTO.setCompanyWeb(entity.getCompanyWeb());
    companyDTO.setId(entity.getId());
    companyDTO.setLocation(entity.getLocation());
    companyDTO.setNewVatId(entity.getNewVatId());
    companyDTO.setNoteAdmin(entity.getNoteAdmin());
    companyDTO.setNotes(entity.getNotes());
    companyDTO.setLogIb(entity.getLogIb());
    companyDTO.setCertificateDate(entity.getCertificateDate());
    companyDTO.setLogIbARGIB(entity.getLogIbARGIB());
    companyDTO.setNumberOfMen(entity.getNumberOfMen());
    companyDTO.setNumberOfWomen(entity.getNumberOfWomen());
    companyDTO.setNumberOfTrainees(entity.getNumberOfTrainees());
    companyDTO.setConsultAdmin(entity.getConsultAdmin());
    companyDTO.setConsultKaio(entity.getConsultKaio());
    companyDTO.setOriginIndication(entity.getOriginIndication());
    companyDTO.setPostCode(entity.getPostCode());
    companyDTO.setProofStatus(entity.getProofStatus());
    companyDTO.setProofOrderedOn(entity.getProofOrderedOn());
    companyDTO.setApprenticeFactor(entity.getApprenticeFactor());
    companyDTO.setTenant(TenantMapper.INSTANCE.toTenantDTO(entity.getTenant()));
    if (companyDTO.getMainCompany() != null) {
      companyDTO.setMainCompany(CompanyMapper.INSTANCE.toCompanyDTO(entity.getMainCompany()));
    }
    if (entity.getCountry() != null) {
      companyDTO.setCountry(countryEntityToHistoryDTO(entity));
    }
    companyDTO.setWorkTypes(workTypeEntityToHistoryDTO(entity));
    companyDTO.setLogibDate(entity.getLogibDate());
    companyDTO.setLogibKmuDate(entity.getLogibKmuDate());
    companyDTO.setIloDate(entity.getIloDate());
    companyDTO.setModificationDate(entity.getModificationDate());
    companyDTO.setArchived(entity.getArchived());
    companyDTO.setTlp(entity.getTlp());
    companyDTO.setCreateOn(entity.getCreateOn());
    companyDTO.setModifiedOn(entity.getModifiedOn());
    if (entity.getIlo() != null) {
      companyDTO.setIlo(iloEntityToHistoryDTO(entity));
      companyDTO.setModUser(entity.getModUser());
    }
    companyDTO.setProofDocModOn(entity.getProofDocModOn());
    companyDTO.setProofDocModBy(TenantMapper.INSTANCE.toTenantDTO(entity.getProofDocModBy()));
    companyDTO.setProofDocSubmitDate(entity.getProofDocSubmitDate());
    companyDTO.setCertDocExpirationDate(entity.getCertDocExpirationDate());
    companyDTO.setProofStatusFabe(entity.getProofStatusFabe());
    companyDTO.setVersion(entity.getVersion());
    companyDTO.setFiftyPlusColleagues(entity.getFiftyPlusColleagues());
    companyDTO.setFiftyPlusFactor(entity.getFiftyPlusFactor());
    return companyDTO;
  }

  /**
   * A method for mapping countryEntities to CountryHistoryDTOs
   * 
   * @param company Entity
   * 
   * @return countryHistoryDTO
   */
  private CountryHistoryDTO countryEntityToHistoryDTO(CompanyEntity entity) {
    if (entity == null) {
      return null;
    }
    CountryEntity countryEntity = entity.getCountry();
    if (countryEntity == null) {
      return null;
    }
    CountryHistoryDTO countryHistoryDTO = new CountryHistoryDTO();
    countryHistoryDTO.setCountryId(CountryMapper.INSTANCE.toCountryDTO(countryEntity));
    return countryHistoryDTO;
  }


  public Set<MasterListValueHistoryDTO> workTypeEntityToHistoryDTO(CompanyEntity entity) {
    if (entity == null) {
      return null;
    }
    Set<MasterListValueEntity> workTypeEntities = entity.getWorkTypes();
    if (workTypeEntities == null) {
      return null;
    }
    Set<MasterListValueHistoryDTO> workTypeHistoryDTOs = new HashSet<>();
    MasterListValueHistoryDTO workTypeHistoryDTO = new MasterListValueHistoryDTO();
    for (MasterListValueEntity workTypeEntity : workTypeEntities) {
      workTypeHistoryDTO.setMasterListValueId(
          MasterListValueMapper.INSTANCE.toMasterListValueDTO(workTypeEntity));
      workTypeHistoryDTOs.add(workTypeHistoryDTO);
    }
    return workTypeHistoryDTOs;

  }

  public MasterListValueHistoryDTO iloEntityToHistoryDTO(CompanyEntity entity) {

    MasterListValueHistoryDTO iloHistoryDTO = new MasterListValueHistoryDTO();

    iloHistoryDTO
        .setMasterListValueId(MasterListValueMapper.INSTANCE.toMasterListValueDTO(entity.getIlo()));

    return iloHistoryDTO;

  }

  public List<CompanyDTO> toCompanyDTO(List<CompanyEntity> companyList) {
    List<CompanyDTO> companyDTO = new ArrayList<>();
    for (CompanyEntity companyEntity : companyList) {
      companyDTO.add(toCompanyDTO(companyEntity));
    }

    return companyDTO;
  }

  public Set<CompanyDTO> toCompanyDTO(Set<CompanyEntity> companyList) {
    if (companyList != null) {
      Set<CompanyDTO> companyDTO = new HashSet<>();
      for (CompanyEntity companyEntity : companyList) {
        companyDTO.add(toCompanyDTO(companyEntity));
      }
      return companyDTO;
    } else {
      return Collections.emptySet();
    }
  }

  public CompanyEntity toCompany(CompanyDTO dto) {
    if (dto == null) {
      return null;
    }
    CompanyEntity companyEntity = new CompanyEntity();
    companyEntity.setAddInfo(dto.getAddInfo());
    companyEntity.setAddress1(dto.getAddress1());
    companyEntity.setAddInfo(dto.getAddInfo());
    companyEntity.setAddress2(dto.getAddress2());
    companyEntity.setCompanyEmail(dto.getCompanyEmail());
    companyEntity.setCompanyFax(dto.getCompanyFax());
    companyEntity.setCompanyName(dto.getCompanyName());
    companyEntity.setCompanyWeb(dto.getCompanyWeb());
    companyEntity.setId(dto.getId());
    companyEntity.setNumberOfMen(dto.getNumberOfMen());
    companyEntity.setNumberOfWomen(dto.getNumberOfWomen());
    companyEntity.setNumberOfTrainees(dto.getNumberOfTrainees());
    companyEntity.setLocation(dto.getLocation());
    companyEntity.setNewVatId(dto.getNewVatId());
    companyEntity.setNoteAdmin(dto.getNoteAdmin());
    companyEntity.setNotes(dto.getNotes());
    companyEntity.setConsultAdmin(dto.getConsultAdmin());
    companyEntity.setConsultKaio(dto.getConsultKaio());
    companyEntity.setCompanyTel(dto.getCompanyTel());
    companyEntity.setOriginIndication(dto.getOriginIndication());
    companyEntity.setPostCode(dto.getPostCode());
    companyEntity.setProofOrderedOn(dto.getProofOrderedOn());
    companyEntity.setApprenticeFactor(dto.getApprenticeFactor());
    companyEntity.setCountry(countryHistoryDTOToEntity(dto));
    companyEntity.setLogibDate(dto.getLogibDate());
    companyEntity.setLogibKmuDate(dto.getLogibKmuDate());
    companyEntity.setIloDate(dto.getLogibDate());
    companyEntity.setModificationDate(dto.getModificationDate());
    companyEntity.setArchived(dto.getArchived());
    companyEntity.setProofStatus(dto.getProofStatus());
    companyEntity.setTlp(dto.getTlp());
    companyEntity.setModUser(dto.getModUser());
    companyEntity.setCreateOn(dto.getCreateOn());
    companyEntity.setModifiedOn(dto.getModifiedOn());
    companyEntity.setCertificateDate(dto.getCertificateDate());
    companyEntity.setTenant(TenantMapper.INSTANCE.toTenant(dto.getTenant()));
    companyEntity.setMainCompany(CompanyMapper.INSTANCE.toCompany(dto.getMainCompany()));
    if (dto.getBranches() != null) {
      companyEntity.setBranches(branchesToEntity(dto));
    }
    companyEntity.setWorkTypes(workTypeHistoryDTOToEntity(dto));
    if (dto.getIlo() != null) {
      companyEntity.setIlo(iloHistoryDTOToEntity(dto));
    }
    companyEntity.setIloDate(dto.getIloDate());
    companyEntity.setProofDocModOn(dto.getProofDocModOn());
    companyEntity.setProofDocModBy(TenantMapper.INSTANCE.toTenant(dto.getProofDocModBy()));
    companyEntity.setProofDocSubmitDate(dto.getProofDocSubmitDate());
    companyEntity.setCertDocExpirationDate(dto.getCertDocExpirationDate());
    companyEntity.setCreatedBy(dto.getCreatedBy());
    companyEntity.setProofStatusFabe(dto.getProofStatusFabe());
    companyEntity.setVersion(dto.getVersion());
    companyEntity.setFiftyPlusColleagues(dto.getFiftyPlusColleagues());
    companyEntity.setFiftyPlusFactor(dto.getFiftyPlusFactor());
    return companyEntity;
  }

  private CountryEntity countryHistoryDTOToEntity(CompanyDTO dto) {
    if (dto == null) {
      return null;
    }
    CountryHistoryDTO countryHistoryDTO = dto.getCountry();
    if (countryHistoryDTO == null) {
      return null;
    }
    CountryEntity countryEntity = new CountryEntity();
    countryEntity.setId(countryHistoryDTO.getCountryId().getId());
    return countryEntity;
  }

  public Set<MasterListValueEntity> workTypeHistoryDTOToEntity(CompanyDTO dto) {
    if (dto == null) {
      return null;
    }
    Set<MasterListValueHistoryDTO> workTypeHistoryDTOS = dto.getWorkTypes();
    if (workTypeHistoryDTOS == null) {
      return null;
    }
    Set<MasterListValueEntity> workTypeEntities = new HashSet<>();
    for (MasterListValueHistoryDTO workTypeHistoryDTO : workTypeHistoryDTOS) {
      MasterListValueEntity tempWorkTypeEntity = MasterListValueMapper.INSTANCE
          .toMasterListValue(workTypeHistoryDTO.getMasterListValueId());
      workTypeEntities.add(tempWorkTypeEntity);
    }
    return workTypeEntities;

  }

  public List<CompanyEntity> branchesToEntity(CompanyDTO dto) {
    List<CompanyEntity> branchEntities = new ArrayList<>();

    List<CompanyDTO> companyDTOs = dto.getBranches();
    if (companyDTOs == null) {
      return null;
    }

    CompanyEntity branchEntity = new CompanyEntity();
    for (CompanyDTO branchDTO : companyDTOs) {
      branchEntity = CompanyMapper.INSTANCE.toCompany(branchDTO);
      branchEntities.add(branchEntity);
    }
    return branchEntities;

  }


  public MasterListValueEntity iloHistoryDTOToEntity(CompanyDTO dto) {

    return MasterListValueMapper.INSTANCE.toMasterListValue(dto.getIlo().getMasterListValueId());
  }

  public abstract List<CompanyEntity> toCompany(List<CompanyDTO> dtoList);

  public abstract Set<CompanyEntity> toCompany(Set<CompanyDTO> dtoList);

}
