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

package ch.bern.submiss.services.impl.mappers;

import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserDTOtoSubmissUserDTOMapper {

  public static final UserDTOtoSubmissUserDTOMapper INSTANCE =
    Mappers.getMapper(UserDTOtoSubmissUserDTOMapper.class);

  public SubmissUserDTO toSubmissUserDTO(UserDTO userDto, List<GroupDTO> groupDTOs,
    TenantDTO tenant, DepartmentHistoryDTO mainDepartment,
    List<DepartmentHistoryDTO> secondaryDepartments, Set<String> permittedOperations,
    Boolean editable, Boolean userAdminRight) {

    if (userDto == null) {
      return null;
    }
    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    SubmissUserDTO submissUserDto = new SubmissUserDTO();
    submissUserDto.setId(userDto.getId());
    submissUserDto.setVersion(userDto.getDbversion());
    String[] splitedUsernameParts = userDto.getUsername().split(LookupValues.USER_NAME_SPECIAL_CHARACTER);
    submissUserDto.setUsername(splitedUsernameParts[0]);
    submissUserDto.setStatus(userDto.getStatus());
    submissUserDto.setUserAdminRight(userAdminRight);

    if (userDto.getAttribute(USER_ATTRIBUTES.EMAIL.getValue()) != null
      && userDto.getAttribute(USER_ATTRIBUTES.EMAIL.getValue()).getData() != null) {
      submissUserDto
        .setEmail(userDto.getAttribute(USER_ATTRIBUTES.EMAIL.getValue()).getData());
    }

    if (userDto.getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()) != null
      && userDto.getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()).getData() != null) {
      submissUserDto
        .setLastName(userDto.getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()).getData());
    }

    if (userDto.getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()) != null
      && userDto.getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData() != null) {
      submissUserDto
        .setFirstName(userDto.getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData());
    }

    if (userDto.getAttribute(USER_ATTRIBUTES.REGISTERED_DATE.getValue()) != null
      && userDto.getAttribute(USER_ATTRIBUTES.REGISTERED_DATE.getValue()).getData() != null) {
      submissUserDto.setRegisteredDate(
          userDto.getAttribute(USER_ATTRIBUTES.REGISTERED_DATE.getValue()).getData());
      try {
        submissUserDto.setRegisteredDateTimestamp(new Timestamp(df.parse(
            userDto.getAttribute(USER_ATTRIBUTES.REGISTERED_DATE.getValue()).getData()).getTime()));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    if (userDto.getAttribute(USER_ATTRIBUTES.FUNCTION.getValue()) != null
      && userDto.getAttribute(USER_ATTRIBUTES.FUNCTION.getValue()).getData() != null) {
      submissUserDto
        .setFunction(userDto.getAttribute(USER_ATTRIBUTES.FUNCTION.getValue()).getData());
      submissUserDto
        .setFunctionVersion(
          userDto.getAttribute(USER_ATTRIBUTES.FUNCTION.getValue()).getDbversion());
    }
    if (userDto.getAttribute(USER_ATTRIBUTES.DEACTIVATION_DATE.getValue()) != null
      && userDto.getAttribute(USER_ATTRIBUTES.DEACTIVATION_DATE.getValue()).getData() != null) {
      submissUserDto.setDeactivationDate(
          userDto.getAttribute(USER_ATTRIBUTES.DEACTIVATION_DATE.getValue()).getData());
      try {
        submissUserDto.setRegisteredDateTimestamp(new Timestamp(df.parse(
          userDto.getAttribute(USER_ATTRIBUTES.DEACTIVATION_DATE.getValue()).getData()).getTime()));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    if (groupDTOs != null) {
      for (GroupDTO groupDTO : groupDTOs) {
        submissUserDto.setUserGroup(groupDTO);
      }
    }

    if (tenant != null) {
      submissUserDto.setTenant(tenant);
    }

    if (mainDepartment != null) {
      submissUserDto.setMainDepartment(mainDepartment);
    }

    if (!secondaryDepartments.isEmpty()) {
      submissUserDto.setSecondaryDepartments(secondaryDepartments);
      submissUserDto.setSecondaryDepartmentsVersion(
        userDto.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getDbversion());
    }

    if (permittedOperations != null && !permittedOperations.isEmpty()) {
      List<String> permittedOperationsList = new ArrayList<>(permittedOperations);
      submissUserDto.setPermittedOperations(permittedOperationsList);
    }

    submissUserDto.setEditable(editable);
    return submissUserDto;
  }
}
