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

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import java.util.List;

/**
 * The Class SubmissUserDTO.
 */
public class SubmissUserDTO {

  /** The id. */
  private String id;

  /** The username. */
  private String username;

  /** The first name. */
  private String firstName;

  /** The last name. */
  private String lastName;

  /** The email. */
  private String email;

  /** The status. */
  private byte status;

  /**
   * The active.
   */
  private String active;

  /** The user admin right. */
  private boolean userAdminRight;

  /** If the user is registered to the system {0,1}. */
  private String registered;

  /** The registered date. */
  private String registeredDate;

  /** The department. */
  private DepartmentHistoryDTO mainDepartment;

  /** List of secondary departments. */
  private List<DepartmentHistoryDTO> secondaryDepartments;

  /** The tenant. */
  private TenantDTO tenant;

  /** The user group. */
  private GroupDTO userGroup;

  /** List of permitted operations. */
  private List<String> permittedOperations;

  /** A flag indicating whether the user can be edited. */
  private Boolean editable;

  /** The directorates. */
  private List<DirectorateHistoryDTO> directorates;

  /** The role. */
  private String role;

  /** The tenant name. */
  private String tenantName;

  /** The directorates str. */
  private String directoratesStr;

  /** The registered days. */
  private int registeredDays;

  /** The deactivation date. */
  private String deactivationDate;

  /** The function. */
  private String function;
  
  /** The directorate short names. */
  private String directorateShortNames;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username.
   *
   * @param username the new username
   */
  public void setUsername(String username) {
    this.username = username;
  }

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
   * Gets the status.
   *
   * @return the status
   */
  public byte getStatus() {
    return status;
  }

  /**
   * Sets the status.
   *
   * @param status the new status
   */
  public void setStatus(byte status) {
    this.status = status;
  }

  /**
   * Checks if is user admin right.
   *
   * @return true, if is user admin right
   */
  public boolean isUserAdminRight() {
    return userAdminRight;
  }

  /**
   * Sets the user admin right.
   *
   * @param userAdminRight the new user admin right
   */
  public void setUserAdminRight(boolean userAdminRight) {
    this.userAdminRight = userAdminRight;
  }

  /**
   * Gets the main department.
   *
   * @return the main department
   */
  public DepartmentHistoryDTO getMainDepartment() {
    return mainDepartment;
  }

  /**
   * Sets the main department.
   *
   * @param mainDepartment the new main department
   */
  public void setMainDepartment(DepartmentHistoryDTO mainDepartment) {
    this.mainDepartment = mainDepartment;
  }

  /**
   * Gets the secondary departments.
   *
   * @return the secondary departments
   */
  public List<DepartmentHistoryDTO> getSecondaryDepartments() {
    return secondaryDepartments;
  }

  /**
   * Sets the secondary departments.
   *
   * @param secondaryDepartments the new secondary departments
   */
  public void setSecondaryDepartments(List<DepartmentHistoryDTO> secondaryDepartments) {
    this.secondaryDepartments = secondaryDepartments;
  }

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantDTO getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantDTO tenant) {
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
   * Gets the registered date.
   *
   * @return the registered date
   */
  public String getRegisteredDate() {
    return registeredDate;
  }

  /**
   * Sets the registered date.
   *
   * @param registeredDate the new registered date
   */
  public void setRegisteredDate(String registeredDate) {
    this.registeredDate = registeredDate;
  }

  /**
   * Gets the permitted operations.
   *
   * @return the permitted operations
   */
  public List<String> getPermittedOperations() {
    return permittedOperations;
  }

  /**
   * Sets the permitted operations.
   *
   * @param permittedOperations the new permitted operations
   */
  public void setPermittedOperations(List<String> permittedOperations) {
    this.permittedOperations = permittedOperations;
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

  /**
   * Gets the editable.
   *
   * @return the editable
   */
  public Boolean getEditable() {
    return editable;
  }

  /**
   * Sets the editable.
   *
   * @param editable the new editable
   */
  public void setEditable(Boolean editable) {
    this.editable = editable;
  }

  /**
   * Gets the directorates.
   *
   * @return the directorates
   */
  public List<DirectorateHistoryDTO> getDirectorates() {
    return directorates;
  }

  /**
   * Sets the directorates.
   *
   * @param directorates the new directorates
   */
  public void setDirectorates(List<DirectorateHistoryDTO> directorates) {
    this.directorates = directorates;
  }

  /**
   * Gets the role.
   *
   * @return the role
   */
  public String getRole() {
    return (userGroup != null) ? userGroup.getDescription() : null;
  }

  /**
   * Sets the role.
   *
   * @param role the new role
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * Gets the tenant name.
   *
   * @return the tenant name
   */
  public String getTenantName() {
    return (tenant != null) ? tenant.getName() : null;
  }

  /**
   * Sets the tenant name.
   *
   * @param tenantName the new tenant name
   */
  public void setTenantName(String tenantName) {
    this.tenantName = tenantName;
  }

  /**
   * Gets the registered days.
   *
   * @return the registered days
   */
  public int getRegisteredDays() {
    return registeredDays;
  }

  /**
   * Sets the registered days.
   *
   * @param registeredDays the new registered days
   */
  public void setRegisteredDays(int registeredDays) {
    this.registeredDays = registeredDays;
  }

  /**
   * Gets the deactivation date.
   *
   * @return the deactivation date
   */
  public String getDeactivationDate() {
    return deactivationDate;
  }

  /**
   * Sets the deactivation date.
   *
   * @param deactivationDate the new deactivation date
   */
  public void setDeactivationDate(String deactivationDate) {
    this.deactivationDate = deactivationDate;
  }

  /**
   * Gets the directorates str.
   *
   * @return the directorates str
   */
  public String getDirectoratesStr() {
    StringBuilder tempDirectoratesStr = new StringBuilder();

    if (mainDepartment != null && mainDepartment.getDirectorate() != null
        && mainDepartment.getDirectorate().getName() != null) {
      tempDirectoratesStr.append(mainDepartment.getDirectorate().getName());

      if (secondaryDepartments != null && !secondaryDepartments.isEmpty()) {
        tempDirectoratesStr.append(" / ");
      }
    }

    if (secondaryDepartments != null) {
      for (DepartmentHistoryDTO departmentHistoryDTO : secondaryDepartments) {
        tempDirectoratesStr.append(departmentHistoryDTO.getDirectorate().getName()).append(" / ");
      }
    }

    return tempDirectoratesStr.toString().replaceAll(" / $", "");
  }

  /**
   * Sets the directorates str.
   *
   * @param directoratesStr the new directorates str
   */
  public void setDirectoratesStr(String directoratesStr) {
    this.directoratesStr = directoratesStr;
  }

  /**
   * Gets the main department str.
   *
   * @return the main department str
   */
  public String getMainDepartmentStr() {


    StringBuilder mainDepartmentStr = new StringBuilder();

    if (mainDepartment != null && mainDepartment.getName() != null) {
      mainDepartmentStr.append(mainDepartment.getName());

      if (secondaryDepartments != null && !secondaryDepartments.isEmpty()) {
        mainDepartmentStr.append(" / ");
      }
    }

    if (secondaryDepartments != null) {
      for (DepartmentHistoryDTO departmentHistoryDTO : secondaryDepartments) {
        mainDepartmentStr.append(departmentHistoryDTO.getName()).append(" / ");
      }
    }

    return mainDepartmentStr.toString().replaceAll(" / $", "");
  }



  /**
   * Gets the function.
   *
   * @return the function
   */
  public String getFunction() {
    return function;
  }

  /**
   * Sets the function.
   *
   * @param function the new function
   */
  public void setFunction(String function) {
    this.function = function;
  }
  
  

  public String getDirectorateShortNames() {
    StringBuilder tempDirectorateShortNames = new StringBuilder();

    if (mainDepartment != null && mainDepartment.getDirectorate() != null
        && mainDepartment.getDirectorate().getShortName() != null) {
      tempDirectorateShortNames.append(mainDepartment.getDirectorate().getShortName());

      if (secondaryDepartments != null && !secondaryDepartments.isEmpty()) {
        tempDirectorateShortNames.append(" / ");
      }
    }

    if (secondaryDepartments != null) {
      for (DepartmentHistoryDTO departmentHistoryDTO : secondaryDepartments) {
        tempDirectorateShortNames.append(departmentHistoryDTO.getDirectorate().getShortName()).append(" / ");
      }
    }

    return tempDirectorateShortNames.toString().replaceAll(" / $", "");
  }

  public void setDirectorateShortNames(String directorateShortNames) {
    this.directorateShortNames = directorateShortNames;
  }

  @Override
  public String toString() {
    return "SubmissUserDTO [id=" + id + ", username=" + username + ", firstName=" + firstName
        + ", lastName=" + lastName + ", email=" + email + ", status=" + status + ", active="
        + active + ", userAdminRight=" + userAdminRight + ", registered=" + registered
        + ", registeredDate=" + registeredDate + ", mainDepartment=" + mainDepartment
        + ", secondaryDepartments=" + secondaryDepartments + ", tenant=" + tenant + ", userGroup="
        + userGroup + ", permittedOperations=" + permittedOperations + ", editable=" + editable
        + ", directorates=" + directorates + ", role=" + role + ", tenantName=" + tenantName
        + ", directoratesStr=" + directoratesStr + ", registeredDays=" + registeredDays
        + ", deactivationDate=" + deactivationDate + ", function=" + function
        + ", directorateShortNames=" + directorateShortNames + "]";
  }

}
