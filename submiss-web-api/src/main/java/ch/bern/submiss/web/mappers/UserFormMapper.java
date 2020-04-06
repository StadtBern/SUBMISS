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
      dto.setUsername(form.getUsername() + "@" + form.getTenant().getName());
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
    if (form.getSecondaryDepartments() != null && !form.getSecondaryDepartments().isEmpty()) {
      for (DepartmentHistoryForm department : form.getSecondaryDepartments()) {
        departmentIdCommaSeparatedValue.append(department.getDepartmentId().getId());
        departmentIdCommaSeparatedValue.append(",");
      }
      String secondaryDepartments = departmentIdCommaSeparatedValue.substring(0,
          departmentIdCommaSeparatedValue.length() - 1);
      dto.setAttribute(
          new UserAttributeDTO(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue(), secondaryDepartments));
    } else {
      dto.setAttribute(new UserAttributeDTO(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue(), null));
    }
    if (form.getFunction() != null) {
      dto.setAttribute(
          new UserAttributeDTO(USER_ATTRIBUTES.FUNCTION.getValue(), form.getFunction()));
    }

    return dto;
  }
}
