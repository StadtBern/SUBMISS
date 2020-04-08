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

package ch.bern.submiss.services.impl.schedulers;

import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.ProofStatus;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.administration.AuditBean;
import ch.bern.submiss.services.impl.administration.CacheBean;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.CompanyProofEntity;
import ch.bern.submiss.services.impl.model.QCompanyEntity;
import ch.bern.submiss.services.impl.model.QCompanyProofEntity;
import ch.bern.submiss.services.impl.model.QProofHistoryEntity;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerJob;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;

/**
 * The Class CompanyProofsValidityCheckScheduler. This class implements a scheduler that runs every
 * night and checks if the proof status of a company has to be set to not active, that is if a proof
 * of the company has expired.
 */
@Transactional
@OsgiServiceProvider(classes = {SchedulerJob.class})
@Properties(@Property(name = "qlack2.job.qualifier", value = "CompanyProofsValidityCheckScheduler"))
@Singleton
public class CompanyProofsValidityCheckScheduler implements SchedulerJob {

  /** The em. */
  @PersistenceContext(unitName = "submiss")
  protected EntityManager em;

  /** The q company entity. */
  QCompanyEntity qCompanyEntity = QCompanyEntity.companyEntity;

  /** The q company proof entity. */
  QCompanyProofEntity qCompanyProofEntity = QCompanyProofEntity.companyProofEntity;
  
  QProofHistoryEntity qProofHistoryEntity = QProofHistoryEntity.proofHistoryEntity;

  /** The Constant LOGGER. */
  private static final Logger LOGGER =
      Logger.getLogger(CompanyProofsValidityCheckScheduler.class.getName());

  /** The audit. */
  @Inject
  private AuditBean audit;

  /** The cache bean. */
  @Inject
  private CacheBean cacheBean;

  @Override
  public void execute(Map<String, Object> dataMap) {

    LOGGER.log(Level.INFO, "Job CompanyProofsValidityCheck started.");

    /*
     * Get the proof history map in order to get the validity of each company proof that needs to be
     * checked.
     */
    Map<String, ProofHistoryDTO> proofHistoryMap = cacheBean.getActiveProofs();
    
    /* get all company proofs of all companies with proof status set to All Active */
    JPAQuery<CompanyEntity> query = new JPAQuery<>(em);

    List<CompanyEntity> companies =
        query.select(qCompanyEntity).from(qCompanyEntity).where(qCompanyEntity.proofStatus
            .in(ProofStatus.NOT_ACTIVE.getValue(), ProofStatus.ALL_PROOF.getValue())).fetch();

    /* iterate companies */
    for (CompanyEntity company : companies) {
      boolean setActive = true;
      /* iterate company proofs */
      for (CompanyProofEntity companyProof : company.getCompanyProofs()) {
        /* check only the validity of the required proofs */
        setActive = checkProofValidity(proofHistoryMap, setActive, companyProof);
      }
      updateCompanyProofStatus(company, setActive);
    }
    LOGGER.log(Level.INFO, "Job CompanyProofsValidityCheck ended successfully.");
  }

  /**
   * Check proof validity.
   *
   * @param proofHistoryMap the proofHistoryMap
   * @param setActive       the setActive
   * @param companyProof    the companyProof
   * @return setActive value
   */
  private boolean checkProofValidity(Map<String, ProofHistoryDTO> proofHistoryMap,
    boolean setActive, CompanyProofEntity companyProof) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkProofValidity, Parameters: proofHistoryMap: {0}, setActive: {1}, "
        + "companyProof: {2}",
      new Object[]{proofHistoryMap, setActive, companyProof});

    if (companyProof.getRequired()) {
      /*
       * given the proof find the proof history, in order to get the validity period of the
       * proof
       */
      ProofHistoryDTO proofHistoryDTO = proofHistoryMap.get(companyProof.getProofId().getId());

      if (proofHistoryDTO != null) {
        Integer validityPeriod = proofHistoryDTO.getValidityPeriod();
        /*
         * Check if the proof has expired the proof expires on the last day of his validity. If it
         * has expired then change the proof status of the company to not active
         */
        if (companyProof.getProofDate() == null
          || (validityPeriod != null && !LocalDate.now().isBefore(companyProof.getProofDate()
          .toLocalDateTime().toLocalDate().plusMonths(validityPeriod)))) {
          setActive = false;
        }
      }
    }
    return setActive;
  }

  /**
   * Update company's proof status if proofs not valid.
   *
   * @param company   the company
   * @param setActive the setActive
   */
  private void updateCompanyProofStatus(CompanyEntity company, boolean setActive) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateCompanyProofStatus, Parameters: company: {0}, setActive: {1}",
      new Object[]{company, setActive});

    if (!setActive) {
      company.setProofStatus(ProofStatus.NOT_ACTIVE.getValue());
      // update also the proof status Fabe if it is set to automatic status
      if (company.getProofStatusFabe().equals(ProofStatus.ALL_PROOF.getValue())) {
        company.setProofStatusFabe(ProofStatus.NOT_ACTIVE.getValue());
      }
      em.merge(company);
      StringBuilder additionalInfo = new StringBuilder(company.getCompanyName()).append("[#]")
        .append(company.getProofStatusFabe()).append("[#]");
      audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.UPDATE.name(),
        AuditGroupName.COMPANY.name(),
        LookupValues.COMPANY_PROOFS_SCHEDULER_UPDATE_REASON_NOT_ACTIVE, LookupValues.SYSTEM,
        company.getId(), null, additionalInfo.toString(), LookupValues.INTERNAL_LOG);
    }
  }
}
