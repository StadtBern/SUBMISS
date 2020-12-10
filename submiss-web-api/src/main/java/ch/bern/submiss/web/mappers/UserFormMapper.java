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

package ch.bern.submiss.web.mappers;

import ch.bern.submiss.services.api.util.LookupValues;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;

import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.web.forms.DepartmentHistoryForm;
import ch.bern.submiss.web.forms.UserForm;

@Mapper
public abstract class UserFormMapper {

  public static final UserFormMapper INSTANCE = Mappers.getMapper(UserFormMapper.class);

  public UserDTO toUserDTO(UserForm form) {
    if (form == null) {
      return null;
    }

    UserDTO dto = new UserDTO();

    StringBuilder departmentIdCommaSeparatedValue = new StringBuilder();
    if (form.getId() != null) {
      dto.setId(form.getId());
    }
    if (form.getUsername() != null) {
      dto.setUsername(form.getUsername() + LookupValues.USER_NAME_SPECIAL_CHARACTER + form.getTenant().getName());
    }
    if (form.isActive() != null) {
      dto.setStatus((byte) (form.isActive() ? 3 : 1));
    }
    if (form.getEmail() != null) {
      dto.setAttribute(new UserAttributeDTO(USER_ATTRIBUTES.EMAIL.getValue(), form.getEmail()));
    }
    if (form.getFirstName() != null) {
      dto.setAttribute(
          new UserAttributeDTO(USER_ATTRIBUTES.FIRSTNAME.getValue(), form.getFirstName()));
    }
    if (form.getLastName() != null) {
      dto.setAttribute(
          new UserAttributeDTO(USER_ATTRIBUTES.LASTNAME.getValue(), form.getLastName()));
    }
    if (form.getTenant() != null) {
      dto.setAttribute(
          new UserAttributeDTO(USER_ATTRIBUTES.TENANT.getValue(), form.getTenant().getId()));
    }
    if (form.getMainDepartment() != null && form.getMainDepartment().getId() != null) {
      dto.setAttribute(new UserAttributeDTO(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue(),
          form.getMainDepartment().getDepartmentId().getId()));
    }
    if (form.getRegistered() != null) {
      dto.setAttribute(
          new UserAttributeDTO(USER_ATTRIBUTES.REGISTERED.getValue(), form.getRegistered()));
    }
    int userAttributeCounter = 1;
    if (form.getSecondaryDepartments() != null && !form.getSecondaryDepartments().isEmpty()) {
      int depsCounter = 0;
      for (DepartmentHistoryForm department : form.getSecondaryDepartments()) {
        // We should split departments in blocks of 27 max due to limitation of 1024 characters
        // and create separate attribute for each block
        if (depsCounter <= 26 && department.getDepartmentId() != null) {
          departmentIdCommaSeparatedValue.append(department.getDepartmentId().getId());
          departmentIdCommaSeparatedValue.append(",");
          depsCounter++;
        } else {
          dto.setAttribute(
            new UserAttributeDTO(
              USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue() + "_" + userAttributeCounter,
              departmentIdCommaSeparatedValue.substring(0,
                departmentIdCommaSeparatedValue.length() - 1)));
          departmentIdCommaSeparatedValue.setLength(0);
          userAttributeCounter++;
          departmentIdCommaSeparatedValue.append(department.getDepartmentId().getId());
          departmentIdCommaSeparatedValue.append(",");
          depsCounter = 1;
        }
      }
      if (!departmentIdCommaSeparatedValue.toString().isEmpty()) {
        dto.setAttribute(
          new UserAttributeDTO(
            USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue() + "_" + userAttributeCounter,
            departmentIdCommaSeparatedValue.substring(0,
              departmentIdCommaSeparatedValue.length() - 1)));
      }
    } else {
      dto.setAttribute(new UserAttributeDTO(
        USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue() + "_" + userAttributeCounter, null));
    }
    if (form.getFunction() != null) {
      dto.setAttribute(
          new UserAttributeDTO(USER_ATTRIBUTES.FUNCTION.getValue(), form.getFunction()));
    }

    return dto;
  }
}
