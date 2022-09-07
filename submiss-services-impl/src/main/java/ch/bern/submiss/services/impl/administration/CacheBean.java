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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;

/**
 * The Class CacheBean.
 */
@Singleton
public class CacheBean {

  @Inject
  protected UserAdministrationService usersService;

  /**
   * EntityManager setter.
   */
  @PersistenceContext(unitName = "submiss")
  protected EntityManager em;

  /**
   * The active SD.
   */
  private Table<String, String, MasterListValueHistoryDTO> activeSD = HashBasedTable.create();

  /**
   * The active department history SD.
   */
  private Map<String, DepartmentHistoryDTO> activeDepartmentHistorySD = new HashMap<>();

  /**
   * The active directorate history SD.
   */
  private Map<String, DirectorateHistoryDTO> activeDirectorateHistorySD = new HashMap<>();

  /**
   * The active proofs.
   */
  private Map<String, ProofHistoryDTO> activeProofs = new HashMap<>();

  /**
   * The active countries
   */
  private Map<String, CountryHistoryDTO> activeCountries = new HashMap<>();

  /**
   * The history sd.
   */
  private Table<String, String, List<MasterListValueHistoryDTO>> historySd =
    HashBasedTable.create();

  /**
   * The history departments.
   */
  private Map<String, List<DepartmentHistoryDTO>> historyDepartments = new HashMap<>();

  /**
   * The history directorates.
   */
  private Map<String, List<DirectorateHistoryDTO>> historyDirectorates = new HashMap<>();

  /**
   * The history proofs.
   */
  private Map<String, List<ProofHistoryDTO>> historyProofs = new HashMap<>();

  /**
   * The most Recent proofs.
   */
  private Map<String, ProofHistoryDTO> mostRecentProofs = new HashMap<>();

  /**
   * Gets the active department history SD.
   *
   * @return the active department history SD
   */
  public Map<String, DepartmentHistoryDTO> getActiveDepartmentHistorySD() {
    return activeDepartmentHistorySD;
  }

  public void setActiveDepartmentHistorySD(
    Map<String, DepartmentHistoryDTO> activeDepartmentHistorySD) {
    this.activeDepartmentHistorySD = activeDepartmentHistorySD;
  }

  /**
   * Gets the active directorate history SD.
   *
   * @return the active directorate history SD
   */
  public Map<String, DirectorateHistoryDTO> getActiveDirectorateHistorySD() {
    return activeDirectorateHistorySD;
  }

  public void setActiveDirectorateHistorySD(
    Map<String, DirectorateHistoryDTO> activeDirectorateHistorySD) {
    this.activeDirectorateHistorySD = activeDirectorateHistorySD;
  }

  /**
   * Gets the active proofs.
   *
   * @return the active proofs
   */
  public Map<String, ProofHistoryDTO> getActiveProofs() {
    return activeProofs;
  }

  public void setActiveProofs(Map<String, ProofHistoryDTO> activeProofs) {
    this.activeProofs = activeProofs;
  }

  /**
   * Gets the history sd.
   *
   * @return the history sd
   */
  public Table<String, String, List<MasterListValueHistoryDTO>> getHistorySd() {
    return historySd;
  }

  public void setHistorySd(Table<String, String, List<MasterListValueHistoryDTO>> historySd) {
    this.historySd = historySd;
  }

  /**
   * Gets the history departments.
   *
   * @return the history departments
   */
  public Map<String, List<DepartmentHistoryDTO>> getHistoryDepartments() {
    return historyDepartments;
  }

  public void setHistoryDepartments(Map<String, List<DepartmentHistoryDTO>> historyDepartments) {
    this.historyDepartments = historyDepartments;
  }

  /**
   * Gets the active SD.
   *
   * @return the active SD
   */
  public Table<String, String, MasterListValueHistoryDTO> getActiveSD() {

    return activeSD;
  }

  public void setActiveSD(Table<String, String, MasterListValueHistoryDTO> activeSD) {
    this.activeSD = activeSD;
  }

  public Map<String, CountryHistoryDTO> getActiveCountries() {
    return activeCountries;
  }

  public void setActiveCountries(Map<String, CountryHistoryDTO> activeCountries) {
    this.activeCountries = activeCountries;
  }

  /**
   * Gets the history directorates.
   *
   * @return the history directorates
   */
  public Map<String, List<DirectorateHistoryDTO>> getHistoryDirectorates() {
    return historyDirectorates;
  }

  /**
   * Sets the history directorates.
   *
   * @param historyDirectorates the history directorates
   */
  public void setHistoryDirectorates(Map<String, List<DirectorateHistoryDTO>> historyDirectorates) {
    this.historyDirectorates = historyDirectorates;
  }

  /**
   * Gets the history proofs.
   *
   * @return the history proofs
   */
  public Map<String, List<ProofHistoryDTO>> getHistoryProofs() {
    return historyProofs;
  }

  /**
   * Sets the history proofs.
   *
   * @param historyProofs the history proofs
   */
  public void setHistoryProofs(Map<String, List<ProofHistoryDTO>> historyProofs) {
    this.historyProofs = historyProofs;
  }

  /**
   * Gets the most recent proofs.
   *
   * @return the most recent proofs
   */
  public Map<String, ProofHistoryDTO> getMostRecentProofs() {
    return mostRecentProofs;
  }

  /**
   * Sets the most recent proofs.
   *
   * @param mostRecentProofs the most recent proofs
   */
  public void setMostRecentProofs(Map<String, ProofHistoryDTO> mostRecentProofs) {
    this.mostRecentProofs = mostRecentProofs;
  }

  /**
   * Update specific SD entry.
   *
   * @param sdHistoryDTO the sd history DTO
   */
  public void updateSpecificSDEntry(MasterListValueHistoryDTO sdHistoryDTO) {

    if (activeSD.contains(sdHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
      sdHistoryDTO.getMasterListValueId().getId())) {
      activeSD.remove(sdHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
        sdHistoryDTO.getMasterListValueId().getId());
    }

    activeSD.put(sdHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
      sdHistoryDTO.getMasterListValueId().getId(), sdHistoryDTO);
  }

  /**
   * Update deparment history entry.
   *
   * @param depHistoryDTO the dep history DTO
   */
  public void updateDeparmentHistoryEntry(DepartmentHistoryDTO depHistoryDTO) {
    if (activeDepartmentHistorySD.containsKey(depHistoryDTO.getDepartmentId().getId())) {
      activeDepartmentHistorySD.remove(depHistoryDTO.getDepartmentId().getId());

    }
    activeDepartmentHistorySD.put(depHistoryDTO.getDepartmentId().getId(), depHistoryDTO);
  }

  /**
   * Update directorate history entry.
   *
   * @param dirHistoryDTO the dir history DTO
   */
  public void updateDirectorateHistoryEntry(DirectorateHistoryDTO dirHistoryDTO) {
    if (activeDirectorateHistorySD.containsKey(dirHistoryDTO.getDirectorateId().getId())) {
      activeDirectorateHistorySD.remove(dirHistoryDTO.getDirectorateId().getId());
    }
    activeDirectorateHistorySD.put(dirHistoryDTO.getDirectorateId().getId(), dirHistoryDTO);

  }

  /**
   * Updateproofs entry.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  public void updateproofsEntry(ProofHistoryDTO proofHistoryDTO) {
    if (activeProofs.containsKey(proofHistoryDTO.getProofId().getId())) {
      activeProofs.remove(proofHistoryDTO.getProofId().getId());

    }
    activeProofs.put(proofHistoryDTO.getProofId().getId(), proofHistoryDTO);
  }

  /**
   * Update history SD entry.
   *
   * @param sdHistoryDTO the sd history DTO
   */
  public void updateHistorySDEntry(MasterListValueHistoryDTO sdHistoryDTO) {
    if (historySd.contains(sdHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
      sdHistoryDTO.getMasterListValueId().getId())) {
      for (MasterListValueHistoryDTO historyDTO : historySd.get(
        sdHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
        sdHistoryDTO.getMasterListValueId().getId())) {
        if (historyDTO.getToDate() == null) {
          historyDTO.setToDate(sdHistoryDTO.getFromDate());
          break;
        }

      }
      historySd.get(sdHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
        sdHistoryDTO.getMasterListValueId().getId()).add(sdHistoryDTO);

    } else {
      List<MasterListValueHistoryDTO> referenceList = new ArrayList<>();
      referenceList.add(sdHistoryDTO);
      historySd.put(sdHistoryDTO.getMasterListValueId().getMasterList().getCategoryType(),
        sdHistoryDTO.getMasterListValueId().getId(), referenceList);
    }

  }


  /**
   * Update history department list.
   *
   * @param department the department
   */
  public void updateHistoryDepartmentList(DepartmentHistoryDTO department) {
    if (historyDepartments.containsKey(department.getDepartmentId().getId())) {
      for (DepartmentHistoryDTO departmentDTOfromList : historyDepartments
        .get(department.getDepartmentId().getId())) {
        if (departmentDTOfromList.getToDate() == null) {
          departmentDTOfromList.setToDate(department.getFromDate());
          break;
        }

      }
      historyDepartments.get(department.getDepartmentId().getId()).add(department);

    } else {
      List<DepartmentHistoryDTO> referenceList = new ArrayList<>();
      referenceList.add(department);
      historyDepartments.put(department.getDepartmentId().getId(), referenceList);
    }

  }

  /**
   * Gets the value.
   *
   * @param category the category
   * @param masterListValueEntityId the master list value entity id
   * @param date the date
   * @return the value
   */

  /**
   * The purpose of the [StammdatenType] getValue functions  is to implement the historization
   * required for UC 160 and retrieve cached specific,historized Stammdaten values depending on the
   * date that a submission came up to a specific status.How these dates are retrieved, is
   * implemented in SubmissionService. For different types of Stammdaten ,different statuses have to
   * be checked,so different dates have to be retrieved.(see UC160)
   *
   * If submission status is greater than a specific value,Stammdaten values need to
   * 'freeze',regardless if they change in Stammdaten bearbeiten part of the app.In this case,the
   * date that submission came up to this status is retrieved. The cached Stammdaten historized
   * values that are returned, are the values, before submission came to this status.
   *
   * If submission status is not greater than a specific value,null dates are retrieved. In this
   * case the  Stammdaten historized values that are retrieved are the most recent.
   */

  public MasterListValueHistoryDTO getValue(String category, String masterListValueEntityId,
    Timestamp closedOrCanceledDate, Timestamp creationDate, Timestamp objectReferenceDate) {
    List<MasterListValueHistoryDTO> masterListValueHistoryList = historySd
      .get(category, masterListValueEntityId);

    if (category.equals(CategorySD.WORKTYPE.getValue()) || category
      .equals(CategorySD.SETTLEMENT_TYPE.getValue())) {
      // As far as Worktype and Settlementype Stammdaten is concerned,
      // history values need to 'freeze' after Submission is created
      return getValueByDateType(masterListValueHistoryList, creationDate);

    } else if (category.equals(CategorySD.OBJECT.getValue())) {
      // As far as object Stammdaten is concerned,
      // Object history value of the submission needs to
      // 'freeze' if submission status is equal to or greater than a specific status
      // connected with an objectReferenceDate (function
      // getDateOfCompletedOrCanceledStatus in SubmissionService for more info)

      // if not null objectReferenceDate is sent
      // it means that the submission is at or after the status required, for object
      // history values to freeze
      if (objectReferenceDate != null) {
        return getValueByDateType(masterListValueHistoryList, objectReferenceDate);
      } else {

        // if null objectReferenceDate is sent as parameter,submission status is
        // before the status required for object history values to freeze
        return (activeSD.get(category, masterListValueEntityId));
      }

    } else {
      // As far as the other categories of Stammdaten is concerned,
      // history value of the submission needs to
      // 'freeze' if submission status is equal to or greater than closed Or Canceled
      // status connected with an closedOrCanceledDate (function
      // getDateOfCompletedOrCanceledStatus in SubmissionService for more info)

      // if null closedOrCanceledDate is sent as parameter,submission status is
      // before the closed OrCanceled status required for object history values to
      // freeze
      if (closedOrCanceledDate == null) {
        // so we get the most updated history value
        return activeSD.get(category, masterListValueEntityId);

      } else {
        // else it means that the submission is at or after canceled or closed status
        return getValueByDateType(masterListValueHistoryList, closedOrCanceledDate);

      }
    }

  }

  /**
   * Gets the value.
   *
   * @param departmentId the department id
   * @param closedOrCanceledDate the date
   * @return the value
   */
  public DepartmentHistoryDTO getValue(String departmentId, Timestamp closedOrCanceledDate) {
    // For Department data,Department history value of the submission needs to
    // 'freeze' after Submission is closed or canceled(function
    // getDateOfCompletedOrCanceledStatus in SubmissionService for more info),so
    // closedOrCanceledDate is
    // the criterion for the history value that will be returned

    // if null closedOrCanceledDate is sent as parameter,submission status is not
    // equal to or greter than
    // canceled or closed status
    if (closedOrCanceledDate == null) {
      // so we get the most updated history value.
      return activeDepartmentHistorySD.get(departmentId);
    } else {
      // else it means that the submission status is equal to or greater than canceled
      // or closed status,
      // so we get the distinct Department history value created before the
      // closedOrCanceledDate and is valid until now or was valid after the
      // submission closedOrCanceledDate.
      List<DepartmentHistoryDTO> departmentHistoryList = historyDepartments.get(departmentId);
      for (DepartmentHistoryDTO departmentDTOfromList : departmentHistoryList) {
        if (departmentDTOfromList.getFromDate().compareTo(closedOrCanceledDate) < 0
          && (departmentDTOfromList.getToDate() == null
          || departmentDTOfromList.getToDate().compareTo(closedOrCanceledDate) > 0)) {
          return departmentDTOfromList;
        }
      }
    }
    // In case that the history data of a Department entry are created
    // after the closedOrCanceledDate date requested for historization,
    // there will not be a Department History entry that fits the Department
    // criteria requested.In this case,the most updated history entry will be
    // returned
    return activeDepartmentHistorySD.get(departmentId);
  }

  /**
   * Gets the value directorate.
   *
   * @param directorateId the directorate id
   * @param closedOrCanceledDate the closed or canceled date
   * @return the value directorate
   */
  public DirectorateHistoryDTO getValueDirectorate(String directorateId,
    Timestamp closedOrCanceledDate) {
    // For Directorate data, Directorate history value of the submission needs to
    // 'freeze' after Submission is closed or canceled (function
    // getDateOfCompletedOrCanceledStatus in SubmissionService for more info),so
    // closedOrCanceledDate of the Submission is
    // the criterion for the history value that will be returned.

    // if null closedOrCanceledDate is sent as parameter,status is not
    // equal to or greater than
    // canceled or closed status
    if (closedOrCanceledDate == null) {
      // so we get the most updated history value.
      return activeDirectorateHistorySD.get(directorateId);
    } else {
      // else it means that the submission status is equal to or greater than canceled
      // or closed status,
      // so we get the distinct Directorate history value created before the
      // closedOrCanceledDate and is valid until now or was valid after the
      // submission closedOrCanceledDate.
      List<DirectorateHistoryDTO> directorateHistoryList = historyDirectorates.get(directorateId);
      for (DirectorateHistoryDTO directoratefromList : directorateHistoryList) {
        if (directoratefromList.getFromDate().compareTo(closedOrCanceledDate) < 0
          && (directoratefromList.getToDate() == null
          || directoratefromList.getToDate().compareTo(closedOrCanceledDate) > 0)) {
          return directoratefromList;
        }
      }
    }
    // In case that the history data of a Directorate entry are created
    // after the closedOrCanceledDate,
    // there will not be a Directorate History entry that fits the historization
    // criteria requested.In this case,the most updated history entry will be
    // returned

    return activeDirectorateHistorySD.get(directorateId);

  }

  /**
   * Gets the value proof.
   *
   * @param proofId the proof id
   * @param proofReferencedate the proof referencedate
   * @return the value proof
   */
  public ProofHistoryDTO getValueProof(String proofId, Timestamp proofReferencedate) {
    // For Proof data,Proof history value of the submission needs to
    // 'freeze'  if submission status is equal to or greater than a specific
    // status connected to a proofReferencedate (function
    // getRefernceDateForProofs in SubmissionService)

    // if null proofReferencedate is sent as parameter,submission status is not
    // equal to or greater than
    // this specific status
    if (proofReferencedate == null) {
      // so we get the most updated history value.
      return mostRecentProofs.get(proofId);
    } else {
      // else it means that the submission status is equal to or greater than
      // this specific status
      // so we get the distinct Proof history value created before the
      // proofReferencedate and is valid until now or was valid after this proofReferencedate.
      List<ProofHistoryDTO> proofList = historyProofs.get(proofId);
      for (ProofHistoryDTO proofDTOfromList : proofList) {
        if (proofDTOfromList.getFromDate().compareTo(proofReferencedate) < 0
          && (proofDTOfromList.getToDate() == null
          || proofDTOfromList.getToDate().compareTo(proofReferencedate) > 0)) {
          return proofDTOfromList;
        }
      }
    }
    // In case that the history data of a proof entry are created
    // after the reference date requested for historization,
    // there will not be a proof History entry that fits the historization
    // criteria requested.In this case,the most updated history entry will be
    // returned

    return mostRecentProofs.get(proofId);

  }

  /**
   * Gets the value by date type.
   *
   * @param masterListValueHistoryList the master list value history list
   * @param referenceDate the reference date
   * @return the value by date type
   */
  public MasterListValueHistoryDTO getValueByDateType(
    List<MasterListValueHistoryDTO> masterListValueHistoryList,
    Timestamp referenceDate) {

    for (MasterListValueHistoryDTO masterListValueHistoryDTO : masterListValueHistoryList) {
      // We get the distinct MasterListValueHistory value created before the
      // referenceDate and is valid until now or was valid after the
      // referenceDate is sent as parameter.
      if (masterListValueHistoryDTO.getFromDate().compareTo(referenceDate) < 0
        && masterListValueHistoryDTO.isActive()
        && (masterListValueHistoryDTO.getToDate() == null
        || masterListValueHistoryDTO.getToDate().compareTo(referenceDate) > 0)) {
        return masterListValueHistoryDTO;
      }
    }
    return null;
//     In case that the history data of a specific masteListValue entry are created
    // after the reference date requested for historization,
    // there will not be a mastelistvalueHistory entry that fits the historization
    // criteria requested.In this case,the most updated history entry will be
    // returned

    // All the masterListValueHistory objects of the list,that is sent as parameter
    // to this function,have reference to the same mastelistValue entry
    // and the same mastertList entry,so we can get these references randomly, from
    // the first object the list
//    String masterlistValueId = masterListValueHistoryList.get(0).getMasterListValueId().getId();
//    String categoryType = masterListValueHistoryList.get(0).getMasterListValueId().getMasterList()
//      .getCategoryType();
//     After getting these references we can get the most updated history Entry
//     using activeSd.
//    return activeSD.get(categoryType, masterlistValueId);
  }

  /**
   * Gets the master list value history data by submission creation date.
   *
   * @param category the category
   * @param submissionReferenceDate the submission reference date
   * @param tenantId the tenant id
   * @return the master list value history data by submission creation date
   */
  public List<MasterListValueHistoryDTO> getMasterListValueHistoryDataBySubmissionCreationDate(
    String category,
    Timestamp submissionReferenceDate, String tenantId) {

    List<MasterListValueHistoryDTO> returnedList = new ArrayList<>();

    if (submissionReferenceDate == null) {
      Map<String, MasterListValueHistoryDTO> masterlistValuesToListOfMasterListValueHistoryDTOMapActive = activeSD
        .row(category);
      for (String masterListValueId : masterlistValuesToListOfMasterListValueHistoryDTOMapActive
        .keySet()) {
        if (masterlistValuesToListOfMasterListValueHistoryDTOMapActive.get(masterListValueId)
          .getTenant()
          .getId().equals(tenantId)) {
          returnedList
            .add(masterlistValuesToListOfMasterListValueHistoryDTOMapActive.get(masterListValueId));
        }
      }

      return returnedList;
    } else {

      Map<String, MasterListValueHistoryDTO> masterlistValuesToMasterListValueHistoryDTOMap = new HashMap<>();
      Map<String, List<MasterListValueHistoryDTO>> masterlistValuesToListOfMasterListValueHistoryDTOMap = historySd
        .row(category);

      for (String masterListValueId : masterlistValuesToListOfMasterListValueHistoryDTOMap
        .keySet()) {
        List<MasterListValueHistoryDTO> historyValuesOById = masterlistValuesToListOfMasterListValueHistoryDTOMap
          .get(masterListValueId);
        for (MasterListValueHistoryDTO masterListValueHistoryDTO : historyValuesOById) {
          if (masterListValueHistoryDTO.getFromDate().compareTo(submissionReferenceDate) < 0
            && (masterListValueHistoryDTO.getToDate() == null
            || masterListValueHistoryDTO.getToDate().compareTo(submissionReferenceDate) > 0)) {
            masterlistValuesToMasterListValueHistoryDTOMap.put(masterListValueId,
              masterListValueHistoryDTO);
          }
        }
      }
      for (String masterListValueId : masterlistValuesToMasterListValueHistoryDTOMap.keySet()) {
        if (masterlistValuesToMasterListValueHistoryDTOMap.get(masterListValueId).getTenant()
          .getId()
          .equals(tenantId)) {
          returnedList.add(masterlistValuesToMasterListValueHistoryDTOMap.get(masterListValueId));
        }
      }

      return returnedList;
    }
  }

  /**
   * Update history directorates list.
   *
   * @param directorateHistoryDTO the directorate history DTO
   */
  public void updateHistoryDirectoratesList(DirectorateHistoryDTO directorateHistoryDTO) {
    if (historyDirectorates.containsKey(directorateHistoryDTO.getDirectorateId().getId())) {
      for (DirectorateHistoryDTO directorateDTOfromList : historyDirectorates
        .get(directorateHistoryDTO.getDirectorateId().getId())) {
        if (directorateDTOfromList.getToDate() == null) {
          directorateDTOfromList.setToDate(directorateHistoryDTO.getFromDate());
          break;
        }

      }
      historyDirectorates.get(directorateHistoryDTO.getDirectorateId().getId())
        .add(directorateHistoryDTO);

    } else {
      List<DirectorateHistoryDTO> referenceList = new ArrayList<>();
      referenceList.add(directorateHistoryDTO);
      historyDirectorates.put(directorateHistoryDTO.getDirectorateId().getId(), referenceList);
    }
  }

  /**
   * Update proof list.
   *
   * @param proof the proof
   */
  public void updateProofList(ProofHistoryDTO proof) {
    if (historyProofs.containsKey(proof.getProofId().getId())) {
      for (ProofHistoryDTO proofHistoryDTO : historyProofs.get(proof.getProofId().getId())) {
        if (proofHistoryDTO.getToDate() == null) {
          proofHistoryDTO.setToDate(proof.getFromDate());
          break;
        }

      }
      historyProofs.get(proof.getProofId().getId()).add(proof);

    } else {
      List<ProofHistoryDTO> referenceList = new ArrayList<>();
      referenceList.add(proof);
      historyProofs.put(proof.getProofId().getId(), referenceList);
    }
  }

  /**
   * Update most recent proofs entry.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  public void updateMostRecenProofsEntry(ProofHistoryDTO proofHistoryDTO) {
    if (mostRecentProofs.containsKey(proofHistoryDTO.getProofId().getId())) {
      mostRecentProofs.remove(proofHistoryDTO.getProofId().getId());

    }
    mostRecentProofs.put(proofHistoryDTO.getProofId().getId(), proofHistoryDTO);
  }

}
