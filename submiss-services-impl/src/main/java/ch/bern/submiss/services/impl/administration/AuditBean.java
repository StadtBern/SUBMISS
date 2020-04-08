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

package ch.bern.submiss.services.impl.administration;

import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import com.eurodyn.qlack2.fuse.auditing.api.AuditClientService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SearchDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SortDTO;
import com.eurodyn.qlack2.fuse.auditing.api.enums.AuditLogColumns;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SearchOperator;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SortOperator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Class AuditBean.
 */
@Singleton
public class AuditBean {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(AuditBean.class.getName());

  /**
   * The audit.
   */
  @OsgiService
  @Inject
  private AuditClientService audit;

  /**
   * The audit logging service.
   */
  @OsgiService
  @Inject
  private AuditLoggingService auditLoggingService;

  /**
   * Creates the log audit.
   *
   * @param level the level
   * @param event the event
   * @param groupName the group name
   * @param description the description
   * @param sessionID the session ID
   * @param referenceId the reference id
   * @param reopenReason the reopen reason
   * @param additionalInfo the additional info
   * @param logType the log type
   */
  public void createLogAudit(String level, String event, String groupName, String description,
    String sessionID, String referenceId, String reopenReason, String additionalInfo,
    String logType) {
    LOGGER.log(Level.CONFIG,
      "Executing method createLogAudit, Parameters: level: {0}, event: {1}"
        + ", groupName: {2}, description: {3}, sessionID: {4}"
        + ", referenceId: {5}, reopenReason: {6}" + ", additionalInfo: {7}" + ", logType: {8}",
      new Object[]{level, event, groupName, description, sessionID, referenceId, reopenReason,
        additionalInfo, logType});

    AuditLogDTO auditLog = new AuditLogDTO();
    auditLog.setLevel(level);
    auditLog.setEvent(event);
    auditLog.setGroupName(groupName);
    if (description != null) {
      auditLog.setShortDescription(description);
    }
    auditLog.setPrinSessionId(sessionID);
    auditLog.setReferenceId(referenceId);
    if (reopenReason != null) {
      auditLog.setOpt1(reopenReason);
    }

    /**
     * Use additionalInfo for PROJECT_LEVEL to maintain project or submission information. In order the values to be
     * read use following format as value: projectName[#]ObjectName[#]workType[#]TenantId[#]taskResource.
     * The additionalInfo is also used to maintain information that needs to be replaced. E.g. task/email
     *
     * Use additionalInfo for COMPANY_LEVEL to maintain company information.  In order the values to be
     * read use following format as value: companyName[#]proofStatusFabe[#]taskResource.
     */
    if (additionalInfo != null) {
      auditLog.setOpt2(additionalInfo);
    }
    if (logType != null) {
      auditLog.setOpt3(logType);
    }
    auditLoggingService.logAudit(auditLog);
  }

  /**
   * Get audits for submission.
   *
   * @param submissionId the submissionId
   * @return the audit list
   */
  public List<AuditLogDTO> getAuditBySubmissionId(String submissionId) {
    List<String> referenceIds = new ArrayList<>();
    referenceIds.add(submissionId);
    return auditLoggingService.listAudits(null, referenceIds, null, null, null, false, null);
  }

  /**
   * Returns the audit logs of user according to event.
   *
   * @return the users activity history
   */
  public List<AuditLogDTO> getUsersActivityHistory() {
    AuditLogColumns auditLogColumn = AuditLogColumns.groupName;
    SearchOperator searchOperator = SearchOperator.EQUAL;
    SortOperator sortOperator = SortOperator.ASC;

    List<String> valuelist = new ArrayList<>();
    valuelist.add(AuditGroupName.USER.name());

    SearchDTO searchDTO = new SearchDTO();
    searchDTO.setColumn(auditLogColumn);
    searchDTO.setOperator(searchOperator);
    searchDTO.setValue(valuelist);

    List<SearchDTO> searchList = new ArrayList<>();
    searchList.add(searchDTO);

    SortDTO sortDTO = new SortDTO();
    sortDTO.setColumn(auditLogColumn);
    sortDTO.setOperator(sortOperator);

    List<SortDTO> sortList = new ArrayList<>();
    sortList.add(sortDTO);

    return auditLoggingService.listAuditLogs(searchList, null, null, sortList, null);
  }

  /**
   * Gets the user activity.
   *
   * @param activityHistory the activity history
   * @param user the user
   * @return the user activity
   */
  public List<AuditLogDTO> getUserActivity(List<AuditLogDTO> activityHistory, SubmissUserDTO user) {
    List<AuditLogDTO> history = new ArrayList<>();
    for (AuditLogDTO activity : activityHistory) {
      if (user.getId().equals(activity.getReferenceId())) {
        history.add(activity);
      }
    }
    return history;
  }
}
