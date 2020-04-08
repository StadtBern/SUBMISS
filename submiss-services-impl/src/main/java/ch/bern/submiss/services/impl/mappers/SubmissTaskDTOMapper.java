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

import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.impl.model.SubmissTasksEntity;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SubmissionMapper.class, CompanyMapper.class})
public abstract class SubmissTaskDTOMapper {

  public static final SubmissTaskDTOMapper INSTANCE = Mappers.getMapper(SubmissTaskDTOMapper.class);

  public abstract List<SubmissTasksEntity> tasksDTOtoTasks(List<SubmissTaskDTO> dto);

  public abstract SubmissTasksEntity taskDTOtoTask(SubmissTaskDTO dto);


  public abstract List<SubmissTaskDTO> tasksToTasksDTO(List<SubmissTasksEntity> entity,
    @Context Map<String, MasterListValueHistoryDTO> activeSD,
    @Context Map<String, UserDTO> users);

  @Mapping(source = "company.companyName", target = "companyName", defaultValue = "")
  @Mapping(source = "submission.project.projectName", target = "projectName", defaultValue = "")
  public abstract SubmissTaskDTO tasksToTasksDTO(SubmissTasksEntity entity,
    @Context Map<String, MasterListValueHistoryDTO> activeSD,
    @Context Map<String, UserDTO> users);

  @AfterMapping
  public void setSD(SubmissTasksEntity taskEntity, @MappingTarget SubmissTaskDTO taskDTO,
    @Context Map<String, MasterListValueHistoryDTO> activeSD,
    @Context Map<String, UserDTO> users) {
    if (taskEntity.getSubmission() != null) {
      taskDTO.getSubmission().getProject().setObjectName(
        activeSD.get(taskEntity.getSubmission().getProject().getObjectName().getId()));

      taskDTO.setObjectName(taskDTO.getSubmission().getProject().getObjectName().getValue1());

      taskDTO.getSubmission()
        .setWorkType(activeSD.get(taskEntity.getSubmission().getWorkType().getId()));

      taskDTO.setWorkType(taskDTO.getSubmission().getWorkType().getValue1() + LookupValues.SPACE
        + taskDTO.getSubmission().getWorkType().getValue2());

    }
    if (taskEntity.getUserAssigned() != null && users.get(taskEntity.getUserAssigned()) != null) {
      UserDTO user = users.get(taskEntity.getUserAssigned());
      taskDTO.setUserAssigned(
        user.getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData() +
          LookupValues.SPACE + user.getAttribute(USER_ATTRIBUTES.LASTNAME.getValue())
          .getData());
    } else {
      taskDTO.setUserAssigned(StringUtils.EMPTY);
    }

    if (taskEntity.getUserAutoAssigned() != null
      && users.get(taskEntity.getUserAutoAssigned()) != null) {
      taskDTO.setUserAutoAssigned(users.get(taskEntity.getUserAutoAssigned())
        .getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData() +
        LookupValues.SPACE + users.get(taskEntity.getUserAutoAssigned())
        .getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()).getData());
    }

    if (taskEntity.getCreatedBy() != null && users != null
      && users.get(taskEntity.getCreatedBy()) != null
      && (taskEntity.getDescription().equals(TaskTypes.CHECK_TENDERLIST)
      || taskEntity.getDescription().equals(TaskTypes.PROOF_REQUEST)
      || taskEntity.getDescription().equals(TaskTypes.ILLEGAL_THRESHOLD))) {
      taskDTO.setFirstName(LookupValues.LEFT_PARENTHESIS +
        users.get(taskEntity.getCreatedBy()).getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue())
          .getData());
      taskDTO.setLastName(
        users.get(taskEntity.getCreatedBy()).getAttribute(USER_ATTRIBUTES.LASTNAME.getValue())
          .getData() + LookupValues.RIGHT_PARENTHESIS);
    }
  }
}

