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

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;

/**
 * The Class UserSearchForm.
 */
public class UserSearchForm {

  /** The first name. */
  private String firstName;

  /** The last name. */
  private String lastName;

  /** The email. */
  private String email;

  /** The registration date. */
  private Date registrationDate;

  /** The active. */
  private String active;

  /** The registered. */
  private String registered;

  private DepartmentHistoryForm mainDepartment;

  /** List of secondary departments. */
  private List<DepartmentHistoryForm> secondaryDepartments;

  private List<DirectorateHistoryForm> directorates;

  /** The tenant. */
  private TenantForm tenant;

  /** The user group. */
  private GroupDTO userGroup;


  /**
   * Gets the first name.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the first name.
   *
   * @param firstName the new first name
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the last name.
   *
   * @param lastName the new last name
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the registration date.
   *
   * @return the registration date
   */
  public Date getRegistrationDate() {
    return registrationDate;
  }

  /**
   * Sets the registration date.
   *
   * @param registrationDate the new registration date
   */
  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }

  /**
   * Gets the main department.
   *
   * @return the main department
   */
  public DepartmentHistoryForm getMainDepartment() {
    return mainDepartment;
  }

  /**
   * Sets the main department.
   *
   * @param department the new main department
   */
  public void setMainDepartment(DepartmentHistoryForm mainDepartment) {
    this.mainDepartment = mainDepartment;
  }

  /**
   * Gets the secondary departments.
   *
   * @return the departments
   */
  public List<DepartmentHistoryForm> getSecondaryDepartments() {
    return secondaryDepartments;
  }

  /**
   * Sets the secondary departments.
   *
   * @param departments the new departments
   */
  public void setSecondaryDepartments(List<DepartmentHistoryForm> secondaryDepartments) {
    this.secondaryDepartments = secondaryDepartments;
  }

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantForm getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantForm tenant) {
    this.tenant = tenant;
  }

  /**
   * Gets the user group.
   *
   * @return the user group
   */
  public GroupDTO getUserGroup() {
    return userGroup;
  }

  /**
   * Sets the user group.
   *
   * @param userGroup the new user group
   */
  public void setUserGroup(GroupDTO userGroup) {
    this.userGroup = userGroup;
  }

  /**
   * Gets the active.
   *
   * @return the active
   */
  public String getActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(String active) {
    this.active = active;
  }

  /**
   * Gets the registered.
   *
   * @return the registered
   */
  public String getRegistered() {
    return registered;
  }

  /**
   * Sets the registered.
   *
   * @param registered the new registered
   */
  public void setRegistered(String registered) {
    this.registered = registered;
  }

  public List<DirectorateHistoryForm> getDirectorates() {
    return directorates;
  }

  public void setDirectorates(List<DirectorateHistoryForm> directorates) {
    this.directorates = directorates;
  }
}
