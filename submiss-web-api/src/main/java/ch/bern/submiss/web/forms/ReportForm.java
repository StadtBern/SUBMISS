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

package ch.bern.submiss.web.forms;

import java.util.Date;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportForm {

  private Date startDate;

  private Date endDate;

  private List<String> projectCreditNos;

  private List<String> objects;

  private List<String> projects;

  private Set<MasterListValueHistoryForm> workTypes;

  private Set<CompanyForm> companies;

  private List<String> procedures;

  private List<String> directorates;

  private List<String> departments;

  private List<String> projectManagers;

  private String totalizationBy;

  public void setTotalizationBy(String totalizationBy) {
    this.totalizationBy = totalizationBy;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public List<String> getProjectCreditNos() {
    return projectCreditNos;
  }

   public void setProjectCreditNos(List<String> projectCreditNos) {
    this.projectCreditNos = projectCreditNos;
  }

  public List<String> getObjects() {
    return objects;
  }

  public void setObjects(List<String> objects) {
    this.objects = objects;
  }

  public List<String> getProjects() {
    return projects;
  }

  public void setProjects(List<String> projects) {
    this.projects = projects;
  }

  public Set<MasterListValueHistoryForm> getWorkTypes() {
    return workTypes;
  }

  public void setWorkTypes(Set<MasterListValueHistoryForm> workTypes) {
    this.workTypes = workTypes;
  }

  public Set<CompanyForm> getCompanies() {
    return companies;
  }

  public void setCompanies(Set<CompanyForm> companies) {
    this.companies = companies;
  }

   public List<String> getProcedures() {
    return procedures;
  }

  public void setProcedures(List<String> procedures) {
    this.procedures = procedures;
  }

  public List<String> getDirectorates() {
    return directorates;
  }

  public void setDirectorates(List<String> directorates) {
    this.directorates = directorates;
  }

  public List<String> getDepartments() {
    return departments;
  }

  public void setDepartments(List<String> departments) {
    this.departments = departments;
  }

  public List<String> getProjectManagers() {
    return projectManagers;
  }

  public void setProjectManagers(List<String> projectManagers) {
    this.projectManagers = projectManagers;
  }

  public String getTotalizationBy() {
    return totalizationBy;
  }
}
