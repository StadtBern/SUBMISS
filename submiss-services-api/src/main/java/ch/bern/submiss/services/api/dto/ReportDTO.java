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

package ch.bern.submiss.services.api.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Class ReportDTO.
 */
public class ReportDTO extends AbstractDTO {

  /** The start date. */
  private Date startDate;

  /** The end date. */
  private Date endDate;

  /** The project credit nos. */
  private List<String> projectCreditNos;

  /** The objects. */
  private List<String> objects;

  /** The projects. */
  private List<String> projects;

  /** The work types. */
  private Set<MasterListValueHistoryDTO> workTypes;

  /** The companies. */
  private Set<CompanyDTO> companies;

  /** The procedures. */
  private List<String> procedures;

  /** The directorates. */
  private List<String> directorates;

  /** The departments. */
  private List<String> departments;

  /** The project managers. */
  private List<String> projectManagers;

  /** The totalization by. */
  private String totalizationBy;

  /** The report results. */
  private ReportResultsDTO reportResults;

  /**
   * Gets the start date.
   *
   * @return the start date
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date.
   *
   * @param startDate the new start date
   */
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the end date.
   *
   * @return the end date
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date.
   *
   * @param endDate the new end date
   */
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  /**
   * Gets the project credit nos.
   *
   * @return the project credit nos
   */
  public List<String> getProjectCreditNos() {
    return projectCreditNos;
  }

  /**
   * Sets the project credit nos.
   *
   * @param projectCreditNos the new project credit nos
   */
  public void setProjectCreditNos(List<String> projectCreditNos) {
    this.projectCreditNos = projectCreditNos;
  }

  /**
   * Gets the objects.
   *
   * @return the objects
   */
  public List<String> getObjects() {
    return objects;
  }

  /**
   * Sets the objects.
   *
   * @param objects the new objects
   */
  public void setObjects(List<String> objects) {
    this.objects = objects;
  }

  /**
   * Gets the projects.
   *
   * @return the projects
   */
  public List<String> getProjects() {
    return projects;
  }

  /**
   * Sets the projects.
   *
   * @param projects the new projects
   */
  public void setProjects(List<String> projects) {
    this.projects = projects;
  }

  /**
   * Gets the work types.
   *
   * @return the work types
   */
  public Set<MasterListValueHistoryDTO> getWorkTypes() {
    return workTypes;
  }

  /**
   * Sets the work types.
   *
   * @param workTypes the new work types
   */
  public void setWorkTypes(Set<MasterListValueHistoryDTO> workTypes) {
    this.workTypes = workTypes;
  }

  /**
   * Gets the companies.
   *
   * @return the companies
   */
  public Set<CompanyDTO> getCompanies() {
    return companies;
  }

  /**
   * Sets the companies.
   *
   * @param companies the new companies
   */
  public void setCompanies(Set<CompanyDTO> companies) {
    this.companies = companies;
  }

  /**
   * Gets the procedures.
   *
   * @return the procedures
   */
  public List<String> getProcedures() {
    return procedures;
  }

  /**
   * Sets the procedures.
   *
   * @param procedures the new procedures
   */
  public void setProcedures(List<String> procedures) {
    this.procedures = procedures;
  }

  /**
   * Gets the directorates.
   *
   * @return the directorates
   */
  public List<String> getDirectorates() {
    return directorates;
  }

  /**
   * Sets the directorates.
   *
   * @param directorates the new directorates
   */
  public void setDirectorates(List<String> directorates) {
    this.directorates = directorates;
  }

  /**
   * Gets the departments.
   *
   * @return the departments
   */
  public List<String> getDepartments() {
    return departments;
  }

  /**
   * Sets the departments.
   *
   * @param departments the new departments
   */
  public void setDepartments(List<String> departments) {
    this.departments = departments;
  }

  /**
   * Gets the project managers.
   *
   * @return the project managers
   */
  public List<String> getProjectManagers() {
    return projectManagers;
  }

  /**
   * Sets the project managers.
   *
   * @param projectManagers the new project managers
   */
  public void setProjectManagers(List<String> projectManagers) {
    this.projectManagers = projectManagers;
  }

  /**
   * Gets the totalization by.
   *
   * @return the totalization by
   */
  public String getTotalizationBy() {
    return totalizationBy;
  }

  /**
   * Sets the totalization by.
   *
   * @param totalizationBy the new totalization by
   */
  public void setTotalizationBy(String totalizationBy) {
    this.totalizationBy = totalizationBy;
  }

  /**
   * Gets the report results.
   *
   * @return the report results
   */
  public ReportResultsDTO getReportResults() {
    return reportResults;
  }

  /**
   * Sets the report results.
   *
   * @param reportResults the new report results
   */
  public void setReportResults(ReportResultsDTO reportResults) {
    this.reportResults = reportResults;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ReportDTO [startDate=" + startDate + ", endDate=" + endDate + ", projectCreditNos="
        + projectCreditNos + ", objects=" + objects + ", projects=" + projects + ", procedures="
        + procedures + ", directorates=" + directorates + ", departments=" + departments
        + ", projectManagers=" + projectManagers + ", totalizationBy=" + totalizationBy + "]";
  }

}
