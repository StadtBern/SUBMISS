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

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * The Class UserForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserForm extends AbstractForm {

  /**
   * The username.
   */
  private String username;

  /**
   * The first name.
   */
  private String firstName;

  /**
   * The last name.
   */
  private String lastName;

  /**
   * The email.
   */
  private String email;

  /**
   * The active.
   */
  private Boolean active;

  /**
   * The user admin right.
   */
  private Boolean userAdminRight;

  /**
   * The flag to indicate whether to register the user.
   */
  private Boolean register;

  /**
   * If the user is registered to the system {0,1}.
   */
  private String registered;

  /**
   * The registered date.
   */
  private String registeredDate;

  /**
   * The department.
   */
  private DepartmentHistoryForm mainDepartment;

  /**
   * List of secondary departments.
   */
  private List<DepartmentHistoryForm> secondaryDepartments;

  /**
   * The secondary departments version.
   */
  private Long secondaryDepartmentsVersion;

  /**
   * The tenant.
   */
  private TenantForm tenant;

  /**
   * The user group.
   */
  private GroupDTO userGroup;

  /**
   * flag indicating whether group is changed or not, in order to update the security resources.
   */
  private Boolean groupChanged;

  /**
   * The old group id of current user. Used to check for optimistic lock erros.
   */
  private String oldGroupId;

  /**
   * flag indicating whether userAdminRight is changed or not, in order to update the security
   * resources.
   */
  private Boolean userAdminRightChanged;

  /**
   * flag indicating whether secondary Departments are changed or not, in order to update the
   * security resources.
   */
  private Boolean secDeptsChanged;

  /**
   * The function.
   */
  private String function;

  /**
   * The function version from user attributes.
   */
  private Long functionVersion;

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
   * Checks if is active.
   *
   * @return true, if is active
   */
  public Boolean isActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(Boolean active) {
    this.active = active;
  }

  /**
   * Checks if is user admin right.
   *
   * @return true, if is user admin right
   */
  public Boolean isUserAdminRight() {
    return userAdminRight;
  }

  /**
   * Sets the user admin right.
   *
   * @param userAdminRight the new user admin right
   */
  public void setUserAdminRight(Boolean userAdminRight) {
    this.userAdminRight = userAdminRight;
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
   * Gets the main department.
   *
   * @return the DepartmentHistoryForm
   */
  public DepartmentHistoryForm getMainDepartment() {
    return mainDepartment;
  }

  /**
   * Sets the main department.
   *
   * @param mainDepartment the new main department
   */
  public void setMainDepartment(DepartmentHistoryForm mainDepartment) {
    this.mainDepartment = mainDepartment;
  }

  /**
   * Gets the list of secondary departments.
   *
   * @return the List of secondary Departments
   */
  public List<DepartmentHistoryForm> getSecondaryDepartments() {
    return secondaryDepartments;
  }

  /**
   * Sets the list of secondary departments.
   *
   * @param secondaryDepartments the new secondary departments
   */
  public void setSecondaryDepartments(List<DepartmentHistoryForm> secondaryDepartments) {
    this.secondaryDepartments = secondaryDepartments;
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
   * Gets the flag indicating whether to register the user.
   *
   * @return the flag indicating whether to register the user
   */
  public Boolean getRegister() {
    return register;
  }

  /**
   * Sets the flag indicating whether to register the user.
   *
   * @param register the new flag indicating whether to register the user
   */
  public void setRegister(Boolean register) {
    this.register = register;
  }

  /**
   * Gets the Registered value.
   *
   * @return the registered attribute
   */
  public String getRegistered() {
    return registered;
  }

  /**
   * Sets the registered value.
   *
   * @param registered attribute of the user
   */
  public void setRegistered(String registered) {
    this.registered = registered;
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
   * Gets the flag indicating whether group is changed or not.
   *
   * @return the flag indicating whether group is changed or not
   */
  public Boolean getGroupChanged() {
    return groupChanged;
  }

  /**
   * Sets the flag indicating whether group is changed or not.
   *
   * @param groupChanged the new group changed
   */
  public void setGroupChanged(Boolean groupChanged) {
    this.groupChanged = groupChanged;
  }

  /**
   * Gets the flag indicating whether userAdminRight is changed or not.
   *
   * @return the flag indicating whether userAdminRight is changed or not
   */
  public Boolean getUserAdminRightChanged() {
    return userAdminRightChanged;
  }

  /**
   * Sets the flag indicating whether userAdminRight is changed or not.
   *
   * @param userAdminRightChanged the new flag indicating whether userAdminRight is changed or not
   */
  public void setUserAdminRightChanged(Boolean userAdminRightChanged) {
    this.userAdminRightChanged = userAdminRightChanged;
  }

  /**
   * Gets the flag indicating whether secondary Departments are changed or not.
   *
   * @return the flag indicating whether secondary Departments are changed or not
   */
  public Boolean getSecDeptsChanged() {
    return secDeptsChanged;
  }

  /**
   * Sets the flag indicating whether secondary Departments are changed or not.
   *
   * @param secDeptsChanged the new flag indicating whether secondary Departments are changed or
   *                        not
   */
  public void setSecDeptsChanged(Boolean secDeptsChanged) {
    this.secDeptsChanged = secDeptsChanged;
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

  public Long getFunctionVersion() {
    return functionVersion;
  }

  public void setFunctionVersion(Long functionVersion) {
    this.functionVersion = functionVersion;
  }

  public Long getSecondaryDepartmentsVersion() {
    return secondaryDepartmentsVersion;
  }

  public void setSecondaryDepartmentsVersion(Long secondaryDepartmentsVersion) {
    this.secondaryDepartmentsVersion = secondaryDepartmentsVersion;
  }

  public String getOldGroupId() {
    return oldGroupId;
  }

  public void setOldGroupId(String oldGroupId) {
    this.oldGroupId = oldGroupId;
  }
}
