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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import ch.bern.submiss.services.api.administration.SDCountryService;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDDirectorateService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SubmissCacheService;
import ch.bern.submiss.services.api.administration.TenderStatusHistoryService;
import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.api.dto.TenderStatusHistoryDTO;

@Transactional
@OsgiServiceProvider(classes = {SubmissCacheService.class})
@Singleton
public class SubmissCacheServiceImpl implements SubmissCacheService {

  @PersistenceContext(unitName = "submiss")
  protected EntityManager em;

  /**
   * The sd service.
   */
  @Inject
  SDService sdService;

  /**
   * The sd country service.
   */
  @Inject
  SDCountryService sdCountryService;

  /**
   * The sd department service.
   */
  @Inject
  SDDepartmentService sdDepartmentService;

  /**
   * The sd directorate service.
   */
  @Inject
  SDDirectorateService sdDirectorateService;

  /**
   * The s D proof service.
   */
  @Inject
  SDProofService sDProofService;

  /**
   * The tender status history service.
   */
  @Inject
  TenderStatusHistoryService tenderStatusHistoryService;

  @Inject
  CacheBean cacheBean;

  @Override
  public void init() {
    if (cacheBean.getActiveSD().isEmpty()) {
      cacheBean.setActiveSD(initSDMaps(sdService.masterListValueHistoryQuery(null, null)));
      cacheBean.setActiveDirectorateHistorySD(
        initDirectorateSDMaps(sdDirectorateService.directorateHistoryQuery(null)));
      cacheBean.setActiveDepartmentHistorySD(
        initDepartmentSDMaps(sdDepartmentService.departmentHistoryQuery(null)));
      cacheBean.setActiveProofs(initProofMap(sDProofService.getAllCurrentProofEntries(null)));
      cacheBean.setHistorySd(initHistory(sdService.historySdQuery(null, null)));
      cacheBean.setHistoryDepartments(
        initHistoryDepartment(sdDepartmentService.departmentHistoryQueryAll(null)));
      cacheBean.setHistoryDirectorates(
        initHistoryirectorates(sdDirectorateService.directorateHistoryQueryAll(null)));
      cacheBean.setActiveCountries(initActiveCountries(sdCountryService.getCountryHistoryDTOs()));
      cacheBean.setHistoryProofs(initHistoryProofs(sDProofService.getAllProofEntries(null)));
      cacheBean
        .setMostRecentProofs(initMostRecentProofs(sDProofService.getMostRecentdProofEntries(null)));

    }

  }


  /**
   * Inits the SD maps.
   *
   * @param activeSDList the active SD list
   */
  private Table<String, String, MasterListValueHistoryDTO> initSDMaps(
    List<MasterListValueHistoryDTO> activeSDList) {
    Table<String, String, MasterListValueHistoryDTO> activeSD = HashBasedTable.create();
    for (MasterListValueHistoryDTO masterListValueHistoryDTO : activeSDList) {
      activeSD.put(
        masterListValueHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
        masterListValueHistoryDTO.getMasterListValueId().getId(), masterListValueHistoryDTO);
    }
    return activeSD;
  }

  /**
   * Inits the department SD maps.
   *
   * @param activeDepartmentsSDdList the active departments S dd list
   */
  private Map<String, DepartmentHistoryDTO> initDepartmentSDMaps(
    List<DepartmentHistoryDTO> activeDepartmentsSDdList) {
    Map<String, DepartmentHistoryDTO> activeDepartmentHistorySD = new HashMap<>();
    for (DepartmentHistoryDTO departmentHistoryDTO : activeDepartmentsSDdList) {
      activeDepartmentHistorySD.put(departmentHistoryDTO.getDepartmentId().getId(),
        departmentHistoryDTO);
    }
    return activeDepartmentHistorySD;
  }

  /**
   * Inits the directorate SD maps.
   *
   * @param activeDirectoratesSDdList the active directorates S dd list
   */
  private Map<String, DirectorateHistoryDTO> initDirectorateSDMaps(
    List<DirectorateHistoryDTO> activeDirectoratesSDdList) {
    Map<String, DirectorateHistoryDTO> activeDirectorateHistorySD = new HashMap<>();
    for (DirectorateHistoryDTO directorateHistoryDTO : activeDirectoratesSDdList) {
      activeDirectorateHistorySD.put(directorateHistoryDTO.getDirectorateId().getId(),
        directorateHistoryDTO);
    }
    return activeDirectorateHistorySD;
  }

  /**
   * Inits the history.
   *
   * @param inActiveSDList the in active SD list
   */
  private Table<String, String, List<MasterListValueHistoryDTO>> initHistory(
    List<MasterListValueHistoryDTO> inActiveSDList) {
    Table<String, String, List<MasterListValueHistoryDTO>> historySd =
      HashBasedTable.create();

    for (MasterListValueHistoryDTO masterListValueHistoryDTO : inActiveSDList) {
      if (!historySd.contains(
        masterListValueHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
        masterListValueHistoryDTO.getMasterListValueId().getId())) {
        List<MasterListValueHistoryDTO> referenceList = new ArrayList<>();
        referenceList.add(masterListValueHistoryDTO);
        historySd.put(
          masterListValueHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
          masterListValueHistoryDTO.getMasterListValueId().getId(), referenceList);
      } else {
        List<MasterListValueHistoryDTO> referenceList = historySd.get(
          masterListValueHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
          masterListValueHistoryDTO.getMasterListValueId().getId());
        referenceList.add(masterListValueHistoryDTO);
      }
    }

    return historySd;

  }

  /**
   * Inits the history department.
   *
   * @param departments the departments
   */
  private Map<String, List<DepartmentHistoryDTO>> initHistoryDepartment(
    List<DepartmentHistoryDTO> departments) {
    Map<String, List<DepartmentHistoryDTO>> historyDepartments = new HashMap<>();
    for (DepartmentHistoryDTO departmentHistoryDTO : departments) {
      if (!historyDepartments.containsKey(departmentHistoryDTO.getDepartmentId().getId())) {
        List<DepartmentHistoryDTO> referenceList = new ArrayList<>();
        referenceList.add(departmentHistoryDTO);
        historyDepartments.put(departmentHistoryDTO.getDepartmentId().getId(), referenceList);
      } else {
        List<DepartmentHistoryDTO> referenceList =
          historyDepartments.get(departmentHistoryDTO.getDepartmentId().getId());

        referenceList.add(departmentHistoryDTO);
      }
    }

    return historyDepartments;
  }

  /**
   * Inits the current statuses of all submissions.
   *
   * @param statuses the statuses
   */
  private Map<String, TenderStatusHistoryDTO> initCurrentStatusesOfAllSubmissions(
    List<TenderStatusHistoryDTO> statuses) {

    Map<String, TenderStatusHistoryDTO> currentStatus = new HashMap<>();
    for (TenderStatusHistoryDTO tenderStatusHistoryDTO : statuses) {
      if (!currentStatus.containsKey(tenderStatusHistoryDTO.getTenderId().getId())) {
        currentStatus.put(tenderStatusHistoryDTO.getTenderId().getId(), tenderStatusHistoryDTO);

      } else {
        if (currentStatus.get(tenderStatusHistoryDTO.getTenderId().getId()).getOnDate()
          .compareTo(tenderStatusHistoryDTO.getOnDate()) < 0) {
          currentStatus.remove(tenderStatusHistoryDTO.getTenderId().getId());
          currentStatus.put(tenderStatusHistoryDTO.getTenderId().getId(), tenderStatusHistoryDTO);
        }

      }
    }
    return currentStatus;

  }

  /**
   * Initializes the map of the proofs.
   *
   * @param activeProofsList the proofs to Initialize the map with
   */
  private Map<String, ProofHistoryDTO> initProofMap(List<ProofHistoryDTO> activeProofsList) {

    Map<String, ProofHistoryDTO> activeProofs = new HashMap<>();
    for (ProofHistoryDTO activeProof : activeProofsList) {
      activeProofs.put(activeProof.getProofId().getId(), activeProof);
    }

    return activeProofs;
  }

  /**
   *
   */
  public Map<String, CountryHistoryDTO> initActiveCountries(
    List<CountryHistoryDTO> countriesHistoryDTO) {

    Map<String, CountryHistoryDTO> activeCountries = new HashMap<>();
    for (CountryHistoryDTO countryHistoryDTO : countriesHistoryDTO) {
      activeCountries.put(countryHistoryDTO.getCountryId().getId(), countryHistoryDTO);
    }
    return activeCountries;
  }


  /**
   * Inits the historyirectorates.
   *
   * @param allDirectorates the all directorates
   * @return the map
   */
  private Map<String, List<DirectorateHistoryDTO>> initHistoryirectorates(
    List<DirectorateHistoryDTO> allDirectorates) {
    Map<String, List<DirectorateHistoryDTO>> historyDirectorates = new HashMap<>();
    for (DirectorateHistoryDTO directorateHistoryDTO : allDirectorates) {
      if (!historyDirectorates.containsKey(directorateHistoryDTO.getDirectorateId().getId())) {
        List<DirectorateHistoryDTO> referenceList = new ArrayList<>();
        referenceList.add(directorateHistoryDTO);
        historyDirectorates.put(directorateHistoryDTO.getDirectorateId().getId(), referenceList);
      } else {
        historyDirectorates.get(directorateHistoryDTO.getDirectorateId().getId())
          .add(directorateHistoryDTO);
      }
    }

    return historyDirectorates;
  }


  /**
   * Inits the history proofs.
   *
   * @param allProofEntries the all proof entries
   * @return the map
   */
  private Map<String, List<ProofHistoryDTO>> initHistoryProofs(
    List<ProofHistoryDTO> allProofEntries) {
    Map<String, List<ProofHistoryDTO>> historyProofs = new HashMap<>();
    for (ProofHistoryDTO proofHistoryDTO : allProofEntries) {
      if (!historyProofs.containsKey(proofHistoryDTO.getProofId().getId())) {
        List<ProofHistoryDTO> referenceList = new ArrayList<>();
        referenceList.add(proofHistoryDTO);
        historyProofs.put(proofHistoryDTO.getProofId().getId(), referenceList);
      } else {

        historyProofs.get(proofHistoryDTO.getProofId().getId()).add(proofHistoryDTO);

      }
    }

    return historyProofs;
  }

  /**
   * Inits the most recent proofs.
   *
   * @param mostRecentdProofEntries the most recentd proof entries
   * @return the map
   */
  private Map<String, ProofHistoryDTO> initMostRecentProofs(
    List<ProofHistoryDTO> mostRecentdProofEntries) {
    Map<String, ProofHistoryDTO> mostRecentProofs = new HashMap<>();
    for (ProofHistoryDTO proofFromList : mostRecentdProofEntries) {
      mostRecentProofs.put(proofFromList.getProofId().getId(), proofFromList);
    }

    return mostRecentProofs;
  }


}
