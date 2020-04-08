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

package ch.bern.submiss.services.impl.administration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.querydsl.jpa.impl.JPAQueryFactory;

import ch.bern.submiss.services.api.administration.CompanyProofService;
import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.impl.mappers.CompanyMapper;
import ch.bern.submiss.services.impl.mappers.ProofHistoryMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.CompanyProofEntity;
import ch.bern.submiss.services.impl.model.ProofEntity;
import ch.bern.submiss.services.impl.model.QCompanyProofEntity;

/**
 * The Class CompanyProofServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {CompanyProofService.class})
@Singleton
public class CompanyProofServiceImpl extends BaseService implements CompanyProofService {

  /**
   * The q company proof entity.
   */
  QCompanyProofEntity qCompanyProofEntity = QCompanyProofEntity.companyProofEntity;

  @Inject
  private CompanyService companyService;

  @Override
  public void removeProofFromCompanies(ProofHistoryDTO proofHistoryDTO) {
    // Get all the the company proof entities that have the given proof property.
    List<CompanyProofEntity> companyProofEntities =
      new JPAQueryFactory(em).selectFrom(qCompanyProofEntity)
        .where(qCompanyProofEntity.proofId.id.eq(proofHistoryDTO.getProofId().getId())).fetch();
    // Remove all the company proof entities that have a different country property than the proof
    // country property.
    for (CompanyProofEntity companyProofEntity : companyProofEntities) {
      if (!companyProofEntity.getCompanyId().getCountry().getId().equals(proofHistoryDTO.getCountry()
        .getId())) {
        em.remove(companyProofEntity);
      }
    }
  }

  @Override
  public void addProofToCompanies(ProofHistoryDTO proofHistoryDTO) {
    ProofEntity proofEntity =
      ProofHistoryMapper.INSTANCE.toProofHistory(proofHistoryDTO).getProofId();
    // Get all the companies that have the same country property as the proof.
    List<CompanyEntity> companyEntities = CompanyMapper.INSTANCE
      .toCompany(companyService.getCompaniesByCountryId(proofHistoryDTO.getCountry().getId()));
    for (CompanyEntity companyEntity : companyEntities) {
      // For every company create a new company proof entity with the given proof as its proof
      // property.
      CompanyProofEntity companyProofEntity = new CompanyProofEntity();
      companyProofEntity.setCompanyId(companyEntity);
      companyProofEntity.setProofId(proofEntity);
      companyProofEntity.setRequired(proofHistoryDTO.getRequired());
      em.merge(companyProofEntity);
    }
  }

  @Override
  public void updateRequiredPropertyOfCompanyProof(ProofHistoryDTO proofHistoryDTO) {
    // Get all the the company proof entities that have the given proof property.
    List<CompanyProofEntity> companyProofEntities =
      new JPAQueryFactory(em).selectFrom(qCompanyProofEntity)
        .where(qCompanyProofEntity.proofId.id.eq(proofHistoryDTO.getProofId().getId())).fetch();
    // Assign to every company proof the new required value.
    for (CompanyProofEntity companyProofEntity : companyProofEntities) {
      companyProofEntity.setRequired(proofHistoryDTO.getRequired());
      em.merge(companyProofEntity);
    }
  }

  @Override
  public void updateInactiveCompanyProofs(ProofHistoryDTO proofHistoryDTO) {
    if (proofHistoryDTO.getActive() != null && proofHistoryDTO.getActive().equals(0)) {
      List<CompanyProofEntity> companyProofEntities =
        new JPAQueryFactory(em).selectFrom(qCompanyProofEntity)
          .where(qCompanyProofEntity.proofId.id.eq(proofHistoryDTO.getProofId().getId())).fetch();
      for (CompanyProofEntity companyProofEntity : companyProofEntities) {
        em.remove(companyProofEntity);
      }
    }
    if (proofHistoryDTO.getActive() != null && proofHistoryDTO.getActive().equals(1)) {
      addProofToCompanies(proofHistoryDTO);
    }
  }

  @Override
  public void updateHasChangedValues(List<CompanyProofDTO> companyProofDTOs, String companyId) {
    // Get the saved company proofs.
    List<CompanyProofEntity> companyProofEntities =
      new JPAQueryFactory(em).selectFrom(qCompanyProofEntity)
        .where(qCompanyProofEntity.companyId.id.eq(companyId)).fetch();
    // Compare every company proof with its respective saved value, in order to check if the proof
    // has been changed.
    for (CompanyProofDTO companyProofDTO : companyProofDTOs) {
      // If the "required" value is null, set it to false.
      if (companyProofDTO.getRequired() == null) {
        companyProofDTO.setRequired(false);
      }
      for (CompanyProofEntity companyProofEntity : companyProofEntities) {
        if (companyProofDTO.getProof().getProofId().getId()
          .equals(companyProofEntity.getProofId().getId())) {
          // If the "required" value is null, set it to false.
          if (companyProofEntity.getRequired() == null) {
            companyProofEntity.setRequired(false);
          }
          // Check if the saved proof values and the given proof values are different.
          boolean isChanged = (
            ((companyProofEntity.getProofDate() == null && companyProofDTO.getProofDate() != null)
              || (companyProofEntity.getProofDate() != null
              && companyProofDTO.getProofDate() == null)
              || (companyProofEntity.getProofDate() != null
              && companyProofDTO.getProofDate() != null
              && !companyProofEntity.getProofDate().equals(companyProofDTO.getProofDate())))
              || (!companyProofEntity.getRequired().equals(companyProofDTO.getRequired())));

          companyProofDTO.setHasChanged(isChanged);

          break;
        }
      }
    }
  }

  @Override
  public void removeDefaultProofs(ProofHistoryDTO proofHistoryDTO) {
    em.createNamedQuery("deleteDefaultProofs", CompanyProofEntity.class)
      .setParameter(1, proofHistoryDTO.getCountry().getId()).executeUpdate();
    em.flush();
  }
}
