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

import java.sql.Date;

import ch.bern.submiss.services.api.constants.Process;

public class FilterForm {

  private String objectName;
  private String projectName;
  private String workType;
  private String description;
  private Process proccess;
  private String proccessType;
  private Date submissionDeadline;
  private String manDep;
  private String projectManagerOfDep;

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getWorkType() {
    return workType;
  }

  public void setWorkType(String workType) {
    this.workType = workType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }



  public Process getProccess() {
    return proccess;
  }

  public void setProccess(Process proccess) {
    this.proccess = proccess;
  }

  public String getProccessType() {
    return proccessType;
  }

  public void setProccessType(String proccessType) {
    this.proccessType = proccessType;
  }

  public Date getSubmissionDeadline() {
    return submissionDeadline;
  }

  public void setSubmissionDeadline(Date submissionDeadline) {
    this.submissionDeadline = submissionDeadline;
  }

  public String getManDep() {
    return manDep;
  }

  public void setManDep(String manDep) {
    this.manDep = manDep;
  }

  public String getProjectManagerOfDep() {
    return projectManagerOfDep;
  }

  public void setProjectManagerOfDep(String projectManagerOfDep) {
    this.projectManagerOfDep = projectManagerOfDep;
  }

  @Override
  public String toString() {
    return "FilterForm [objectName=" + objectName + ", projectName=" + projectName + ", workType="
        + workType + ", description=" + description + ", proccess=" + proccess + ", proccessType="
        + proccessType + ", submissionDeadline=" + submissionDeadline + ", manDep=" + manDep
        + ", projectManagerOfDep=" + projectManagerOfDep + "]";
  }

}
