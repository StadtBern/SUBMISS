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

/*
 *
 */
package ch.bern.submiss.services.impl.administration;

import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.EmailService;
import ch.bern.submiss.services.api.administration.LexiconService;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.EmailTemplate;
import ch.bern.submiss.services.api.constants.EmailTemplate.RECIEVER_TYPE;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.DepartmentDTO;
import ch.bern.submiss.services.api.dto.EmailAttributesDTO;
import ch.bern.submiss.services.api.dto.EmailDTO;
import ch.bern.submiss.services.api.dto.EmailTemplateTenantDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.EmailTemplateTenantDTOMapper;
import ch.bern.submiss.services.impl.mappers.EmailToTypeDataMapper;
import ch.bern.submiss.services.impl.mappers.TenantMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.EmailAttributes;
import ch.bern.submiss.services.impl.model.EmailTemplateEntity;
import ch.bern.submiss.services.impl.model.EmailTemplateTenant;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QEmailTemplateEntity;
import ch.bern.submiss.services.impl.model.QEmailTemplateTenant;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class EmailServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {EmailService.class})
@Singleton
public class EmailServiceImpl extends BaseService implements EmailService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());

  // Use these annotation in order to inject Qlack services with the same name in different classes.
  // Add different names
  // for this injection (See also SubDocumentServiceImpl).
  // Example:
  /**
   * The Constant ENCODING.
   */
  private static final String ENCODING = "utf-8";
  /**
   * The task service.
   */
  @Inject
  protected SubmissTaskService taskService;
  /**
   * The q email template entity.
   */
  QEmailTemplateEntity qEmailTemplateEntity = QEmailTemplateEntity.emailTemplateEntity;
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q email template tenant.
   */
  QEmailTemplateTenant qEmailTemplateTenant = QEmailTemplateTenant.emailTemplateTenant;
  /**
   * The template service.
   */
  // https://stackoverflow.com/questions/32265539/spring-injection-by-type-two-repository-with-same-name
  @OsgiService
  @Autowired
  @Qualifier("templateService")
  private com.eurodyn.qlack2.fuse.lexicon.api.TemplateService templateService;
  /**
   * The lexicon service.
   */
  @Inject
  private LexiconService lexiconService;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;
  /**
   * The department service.
   */
  @Inject
  private SDDepartmentService departmentService;
  /**
   * The users service.
   */
  @Inject
  private UserAdministrationService usersService;
  /**
   * The company service.
   */
  @Inject
  private CompanyService companyService;
  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.EmailService#openEMailDefaultClient(java.lang.
   * String, ch.bern.submiss.services.api.dto.EmailTemplateDTO)
   */
  @Override
  public String openEMailDefaultClient(String id, EmailTemplateTenantDTO template) {

    LOGGER.log(Level.CONFIG,
      "Executing method openEMailDefaultClient, Parameters: id: {0}, template: {1}",
      new Object[]{id, template});

    // 4. if the user clicks on the button "E-Mail versenden" of a company (Tab
    // "Firmendetails) - XY is the placeholder for the E-Mail template the user used

    if (template.getAvailablePart().equals(EmailTemplate.AVAILABLE_PART.COMPANY_PART)) {
      CompanyEntity companyEntity = em.find(CompanyEntity.class, id);
      if (companyEntity != null) {
        StringBuilder additionalInfo = new StringBuilder(companyEntity.getCompanyName())
          .append("[#]").append(companyEntity.getProofStatusFabe()).append("[#]")
          .append(template.getDescription());
        auditLog(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
          AuditGroupName.COMPANY.name(), AuditMessages.EMAIL_CREATED.name(),
          additionalInfo.toString(), id);
      }
    } else {
      SubmissionEntity submission = em.find(SubmissionEntity.class, id);
      if (submission != null) {
        MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(submission.getProject().getObjectName())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();

        StringBuilder projectVars =
          new StringBuilder(submission.getProject().getProjectName()).append("[#]")
            .append(objectEntity.getValue1()).append("[#]").append("[#]")
            .append(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData())
            .append("[#]").append(template.getDescription());

        auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
          AuditGroupName.SUBMISSION.name(), AuditMessages.EMAIL_CREATED.name(),
          projectVars.toString(), id);
      }
    }
    return prepareSubmissEmail(id, template, null);
  }

  /**
   * Sets the E mail attributes.
   *
   * @param submissionId the submission id
   * @param template the template
   * @param to the to
   * @param cc the cc
   * @param bcc the bcc
   * @param ccList the cc list
   * @param bccList the bcc list
   * @param companyIds the company ids
   */
  private void setEMailAttributes(String submissionId, EmailTemplateTenantDTO template,
    StringBuilder to,
    StringBuilder cc, StringBuilder bcc, List<String> ccList, List<String> bccList,
    List<String> companyIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method setEMailAttributes, Parameters: submissionId: {0}"
        + ", template: {1}" + ", to: {2}" + ", cc: {3}"
        + ", bcc: {4}" + ", ccList: {5}" + ", bccList: {6}" + ", companyIds: {7}",
      new Object[]{submissionId, template, to, cc, bcc, ccList, bccList, companyIds});

    for (EmailAttributesDTO attr : template.getAttributes()) {
      if (attr.getSendType().equals(EmailTemplate.SEND_TYPE.TO)) {
        to.append(setMailTo(attr, submissionId, template, companyIds));
      } else if (attr.getSendType().equals(EmailTemplate.SEND_TYPE.CC)) {
        ccList.add(setMailTo(attr, submissionId, template, companyIds));
      } else if (attr.getSendType().equals(EmailTemplate.SEND_TYPE.BCC)) {
        bccList.add(setMailTo(attr, submissionId, template, companyIds));
      }
    }
    cc.append(String.join(";", ccList));
    bcc.append(String.join(";", bccList));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.EmailService#checkedSubmittentListEmail(java.lang.
   * String)
   */
  @Override
  public String checkedSubmittentListEmail(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkedSubmittentListEmail, Parameters: submissionId: {0}",
      submissionId);

    EmailTemplateTenantDTO template =
      getEmailTemplateTenantByShortCode(EmailTemplate.TEMPLATE_SHORT_CODE.ET08.name());
    // UC[033] - Set to attribute to the PL of submission that has requested the Submittentenliste
    // prüfen.
    EmailAttributesDTO plAttribute = new EmailAttributesDTO();
    plAttribute.setRecieverRole(EmailTemplate.RECIEVER_TYPE.EXTERNAL_PL.getValue());
    plAttribute.setSendType(EmailTemplate.SEND_TYPE.CC);
    template.getAttributes().add(plAttribute);
    return prepareSubmissEmail(submissionId, template, null);
  }

  /**
   * Open mail desktop client.
   *
   * @param content the content
   * @param subject the subject
   * @param to the to
   * @param cc the cc
   * @param bcc the bcc
   * @return the string
   */
  private String openMailDesktopClient(String content, String subject, String to, String cc,
    String bcc) {

    LOGGER.log(Level.CONFIG,
      "Executing method openMailDesktopClient, Parameters: content: {0}"
        + ", subject: {1}" + ", to: {2}" + ", cc: {3}" + ", bcc: {4}",
      new Object[]{content, subject, to, cc, bcc});

    String emailString = null;
    StringBuilder email = new StringBuilder();

    try {
      String encodedMailToBody = java.net.URLEncoder.encode(content, ENCODING)
        .replaceAll("\\+", "%20").replaceAll(Template.LF_CONSTANT, Template.LF_CR_CONSTANT);
      String encodedMailToSubject = java.net.URLEncoder.encode(subject, ENCODING)
        .replaceAll("\\+", "%20").replaceAll(Template.LF_CONSTANT, Template.LF_CR_CONSTANT);
      String encodedBcc = java.net.URLEncoder.encode(bcc, ENCODING).replaceAll("\\+", "%20")
        .replaceAll(Template.LF_CONSTANT, Template.LF_CR_CONSTANT);
      String encodedCc = java.net.URLEncoder.encode(cc, ENCODING).replaceAll("\\+", "%20")
        .replaceAll(Template.LF_CONSTANT, Template.LF_CR_CONSTANT);
      if (StringUtils.isNotBlank(encodedCc)) {
        email.append("&cc=").append(encodedCc);
      }
      if (StringUtils.isNotBlank(encodedBcc)) {
        email.append("&bcc=").append(encodedBcc);
      }
      if (StringUtils.isNotBlank(encodedMailToBody)) {
        email.append("&body=").append(encodedMailToBody);
      }
      if (StringUtils.isNotBlank(encodedMailToSubject)) {
        email.append("&subject=").append(encodedMailToSubject);
      }
      emailString = new URI("mailto:" + to + email).toASCIIString();
    } catch (URISyntaxException | IOException e) {
      LOGGER.log(Level.SEVERE, "Error during open default desktop mail client: " + e);
    }
    return emailString;
  }

  /**
   * Sets the mail to.
   *
   * @param emailAttributes the email attributes
   * @param submissionId the submission id
   * @param template the template
   * @param companyIds the company ids
   * @return the string
   */
  private String setMailTo(EmailAttributesDTO emailAttributes, String submissionId,
    EmailTemplateTenantDTO template, List<String> companyIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method setMailTo, Parameters: emailAttributes: {0}"
        + ", submissionId: {1}" + ", template: {2}" + ", companyIds: {3}"
      , new Object[]{emailAttributes, submissionId, template, companyIds});

    StringBuilder mail = new StringBuilder();

    if (emailAttributes.getRecieverRole() != null) {
      mail.append(findRecieverRoleEmails(emailAttributes.getRecieverRole(), submissionId));
    }
    if (emailAttributes.getReciever() != null) {
      mail.append(
        findRecieverEmails(emailAttributes.getReciever(), submissionId, template, companyIds));
    }
    if (emailAttributes.getDepartment() != null) {
      mail.append(findDepartmentEmails(emailAttributes.getDepartment()));
    }
    return mail.toString();
  }

  /**
   * Find department emails.
   *
   * @param department the department
   * @return the string
   */
  private String findDepartmentEmails(DepartmentDTO department) {

    LOGGER.log(Level.CONFIG,
      "Executing method findDepartmentEmails, Parameters: department: {0}",
      department);

    return departmentService.getDepartmentHistByDepartmentId(department.getId()).getEmail();
  }

  /**
   * Find reciever role emails.
   *
   * @param recieverRole the reciever role
   * @param submissionId the submission id
   * @return the string
   */
  private String findRecieverRoleEmails(String recieverRole, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method findRecieverRoleEmails, Parameters: recieverRole: {0}"
        + ", submissionId: {1}",
      new Object[]{recieverRole, submissionId});

    RECIEVER_TYPE role = EmailTemplate.RECIEVER_TYPE.fromValue(recieverRole);
    StringBuilder emailString = new StringBuilder();

    switch (role) {
      case PL:
        // Retrieve PL email
        List<String> plEMails = new ArrayList<>();
        List<UserDTO> users = usersService.getUsersByTenantAndRole(
          getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData(),
          Group.PL.getValue());
        for (UserDTO user : users) {
          plEMails.add(user.getAttribute(USER_ATTRIBUTES.EMAIL.getValue()).getData());
        }
        emailString.append(String.join(";", plEMails));
        break;
      case EXTERNAL_PL:
        CompanyDTO company = submissionService.getSubmissionPmExternal(submissionId);
        if (company != null) {
          emailString.append(company.getCompanyEmail());
        }
        break;
      default:
        LOGGER.log(Level.INFO, "Receiver role does not exist.");
    }
    return emailString.toString();
  }

  /**
   * Find reciever emails.
   *
   * @param reciever the reciever
   * @param id the id
   * @param template the template
   * @param companyIds the company ids
   * @return the string
   */
  private String findRecieverEmails(String reciever, String id, EmailTemplateTenantDTO template,
    List<String> companyIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method findRecieverEmails, Parameters: reciever: {0}" + ", id: {1}"
        + ", template: {2}" + ", companyIds: {3}",
      new Object[]{reciever, id, template, companyIds});

    RECIEVER_TYPE recieverType = EmailTemplate.RECIEVER_TYPE.fromValue(reciever);
    StringBuilder emailString = new StringBuilder();
    if (recieverType != null) {
      switch (recieverType) {
        case SENDER:
          emailString.append(getUser().getAttribute(USER_ATTRIBUTES.EMAIL.getValue()).getData());
          break;
        case SUBMITTENT_LIST:
          List<String> emailList = submissionService.getSubmittentListEmails(id);
          emailString.append(String.join(";", emailList));
          break;
        case RECIPIENT:
          CompanyEntity companyEntity = em.find(CompanyEntity.class, id);
          if (companyEntity != null) {
            emailString.append(companyEntity.getCompanyEmail());
          }
          break;
        case SENDER_AFTER_REQUEST:
          if (template.getEmailTemplate().getShortCode()
            .equals(EmailTemplate.TEMPLATE_SHORT_CODE.ET08.name())
            || template.getEmailTemplate().getShortCode()
            .equals(EmailTemplate.TEMPLATE_SHORT_CODE.ET13.name())) {
            String userEmail = taskService.getTaskBySubmissionIdAndDescription(id,
              TaskTypes.CHECK_TENDERLIST, null);
            emailString.append(userEmail);
          }
          // Retrieve autoAssigned user email.
          // One task per submission and company exists.
          if (template.getEmailTemplate().getShortCode()
            .equals(EmailTemplate.TEMPLATE_SHORT_CODE.ET09.name())) {
            String userEmail = taskService.getTaskBySubmissionIdAndDescription(id,
              TaskTypes.PROOF_REQUEST_PL_XY, companyIds.get(0));
            emailString.append(userEmail);
          }
          if (template.getEmailTemplate().getShortCode()
            .equals(EmailTemplate.TEMPLATE_SHORT_CODE.ET10.name())) {
            String userEmail = taskService.getTaskBySubmissionIdAndDescription(null,
              TaskTypes.PROOF_REQUEST_PL_XY, companyIds.get(0));
            emailString.append(userEmail);
          }
          break;
        case RECIPIENT_AFTER_SELECTION:
          if (companyIds != null && !companyIds.isEmpty()) {
            List<String> companyEMailList = companyService.getCompanyEmailsById(companyIds);
            emailString.append(String.join(";", companyEMailList));
          } else {
            CompanyEntity company = em.find(CompanyEntity.class, id);
            if (company != null) {
              emailString.append(company.getCompanyEmail());
            }
          }
          break;
        case RECIPIENT_AFTER_REGISTRATION:
          SubmissUserDTO userDTO = usersService.getSpecificUser(id);
          emailString.append(userDTO.getEmail());
          break;
        default:
          LOGGER.log(Level.INFO, "Receiver type does not exist.");
      }
    }
    return emailString.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.EmailService#retrieveEmailTemplates(java.util.List)
   */
  @Override
  public List<EmailTemplateTenantDTO> retrieveEmailTemplates(List<String> templateCodes) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveEmailTemplates, Parameters: templateCodes: {0}",
      templateCodes);

    List<EmailTemplateTenant> emailTemplates =
      new JPAQueryFactory(em).selectFrom(qEmailTemplateTenant)
        .where(qEmailTemplateTenant.tenant.id
          .in(security.getPermittedTenants(getUser()))
          .and(qEmailTemplateTenant.emailTemplate.shortCode.in(templateCodes))
          .and(qEmailTemplateTenant.isActive.isTrue()))
        .fetch();
    return EmailTemplateTenantDTOMapper.INSTANCE.toEmailTemplateTenantDTO(emailTemplates);
  }

  /**
   * Replace email template placeholders.
   *
   * @param id the id
   * @param availablePart the available part
   * @return the hash map
   */
  private HashMap<String, Object> replaceEmailTemplatePlaceholders(String id,
    EmailTemplate.AVAILABLE_PART availablePart) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceEmailTemplatePlaceholders, Parameters: id: {0}"
        + ", availablePart: {1}",
      new Object[]{id, availablePart});

    HashMap<String, Object> data = new HashMap<>();

    if (availablePart.equals(EmailTemplate.AVAILABLE_PART.PROJECT_PART)) {
      submissionPlaceholders(id, data);

    } else if (availablePart.equals(EmailTemplate.AVAILABLE_PART.COMPANY_PART)) {
      companyPlaceholders(id, data);
    }
    data.put(EmailTemplate.PLACEHOLDER.USERNAME.getValue(),
      getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()) + " "
        + getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()));
    return data;
  }

  /**
   * Submission placeholders.
   *
   * @param id the id
   * @param data the data
   */
  private void submissionPlaceholders(String id, HashMap<String, Object> data) {

    LOGGER.log(Level.CONFIG,
      "Executing method submissionPlaceholders, Parameters: id: {0}, data: {1}",
      new Object[]{id, data});

    SubmissionDTO submission = submissionService.getSubmissionById(id);
    if (submission.getProject().getObjectName() != null) {
      data.put(EmailTemplate.PLACEHOLDER.OBJECT.getValue(),
        submission.getProject().getObjectName().getValue1());
    }
    data.put(EmailTemplate.PLACEHOLDER.PROJECT.getValue(),
      submission.getProject().getProjectName());
    if (submission.getWorkType() != null) {
      data.put(EmailTemplate.PLACEHOLDER.WORKTYPE.getValue(),
        submission.getWorkType().getValue1() + " " + submission.getWorkType().getValue2());
    } else {
      data.put(EmailTemplate.PLACEHOLDER.WORKTYPE.getValue(), StringUtils.EMPTY);
    }
    if (submission.getDescription() != null) {
      data.put(EmailTemplate.PLACEHOLDER.SUBMISSION.getValue(), submission.getDescription());
    } else {
      data.put(EmailTemplate.PLACEHOLDER.SUBMISSION.getValue(), StringUtils.EMPTY);
    }
    data.put(EmailTemplate.PLACEHOLDER.DIRECTORATE.getValue(),
      submission.getProject().getDepartment().getDirectorate().getName());
    data.put(EmailTemplate.PLACEHOLDER.DEPARTMENT.getValue(),
      submission.getProject().getDepartment().getName());
    data.put(EmailTemplate.PLACEHOLDER.PROCESS.getValue(),
      lexiconService.getTranslation(submission.getProcess().name(), "de-CH"));
    if (submission.getProcess().equals(Process.SELECTIVE)) {
      // check if date is not null
      String date = (submission.getFirstDeadline() != null)
        ? SubmissConverter.convertToSwissDate(submission.getFirstDeadline())
        : StringUtils.EMPTY;
      data.put(EmailTemplate.PLACEHOLDER.DEADLINE.getValue(), date);
    } else {
      // check if date is not null
      String date = (submission.getSecondDeadline() != null)
        ? SubmissConverter.convertToSwissDate(submission.getSecondDeadline())
        : StringUtils.EMPTY;
      data.put(EmailTemplate.PLACEHOLDER.DEADLINE.getValue(), date);
    }
  }

  /**
   * Company placeholders.
   *
   * @param id the id
   * @param data the data
   */
  private void companyPlaceholders(String id, HashMap<String, Object> data) {

    LOGGER.log(Level.CONFIG,
      "Executing method companyPlaceholders, Parameters: id: {0}, data: {1}",
      new Object[]{id, data});

    if (StringUtils.isNotBlank(id)) {
      CompanyDTO company = companyService.getCompanyById(id);
      List<CompanyProofDTO> companyProofDTOs = companyService.getProofByCompanyId(id);
      StringBuilder german = new StringBuilder();
      StringBuilder french = new StringBuilder();
      for (CompanyProofDTO companyProof : companyProofDTOs) {

        // Find proofs that are not valid.
        if (companyProof.getRequired() && companyProof.getProof().getValidityPeriod() != null) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(new Date());
          cal.add(Calendar.MONTH, -companyProof.getProof().getValidityPeriod());
          // Check validity of required proofs.
          if (companyProof.getProofDate() == null
            || !companyProof.getProofDate().after(cal.getTime())) {
            german.append("-  ").append(companyProof.getProof().getDescription()).append("\n");
            french.append("-  ").append(companyProof.getProof().getDescriptionFr()).append("\n");
          }
        }
      }
      if (german.length() > 1) {
        data.put(EmailTemplate.PLACEHOLDER.GERMAN_PROOFS.getValue(),
          german.substring(0, german.length() - 1));
        data.put(EmailTemplate.PLACEHOLDER.FRENCH_PROOFS.getValue(),
          french.substring(0, french.length() - 1));
      } else {
        data.put(EmailTemplate.PLACEHOLDER.GERMAN_PROOFS.getValue(), StringUtils.EMPTY);
        data.put(EmailTemplate.PLACEHOLDER.FRENCH_PROOFS.getValue(), StringUtils.EMPTY);
      }
      data.put(EmailTemplate.PLACEHOLDER.COMPANY_NAME.getValue(), company.getCompanyName());
      data.put(EmailTemplate.PLACEHOLDER.COMPANY_LOCATION.getValue(), company.getLocation());
    }
  }

  /**
   * Gets the email template tenant by short code.
   *
   * @param shortCode the short code
   * @return the email template tenant
   */
  public EmailTemplateTenantDTO getEmailTemplateTenantByShortCode(String shortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method getEmailTemplateByShortCode, Parameters: shortCode: {0}",
      shortCode);

    return EmailTemplateTenantDTOMapper.INSTANCE.toEmailTemplateTenantDTO(
      (new JPAQueryFactory(em).select(qEmailTemplateTenant).from(qEmailTemplateTenant)
        .where(qEmailTemplateTenant.tenant.id
          .in(security.getPermittedTenants(getUser()))
          .and(qEmailTemplateTenant.emailTemplate.shortCode.eq(shortCode)))
        .fetchOne()));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.EmailService#openProofRequestEMailTemplate(java.
   * lang.String)
   */
  @Override
  public EmailDTO openProofRequestEMailTemplate(String companyId) {

    LOGGER.log(Level.CONFIG,
      "Executing method openProofRequestEMailTemplate, Parameters: companyId: {0}",
      companyId);

    EmailTemplateTenantDTO template =
      getEmailTemplateTenantByShortCode(EmailTemplate.TEMPLATE_SHORT_CODE.ET06.name());
    EmailAttributesDTO plAttribute = new EmailAttributesDTO();
    plAttribute.setRecieverRole(EmailTemplate.RECIEVER_TYPE.EXTERNAL_PL.getValue());
    plAttribute.setSendType(EmailTemplate.SEND_TYPE.CC);
    template.getAttributes().add(plAttribute);
    return openKantonProofEmailTemplate(companyId, template, null);
  }

  private EmailDTO openKantonProofEmailTemplate(String id, EmailTemplateTenantDTO template,
    List<String> ids) {

    LOGGER.log(Level.CONFIG, "Executing method prepareSubmissEmail, Parameters: id: {0}"
        + ", template: {1}, ids: {2}",
      new Object[]{id, template, ids});

    String subjectPlaceholder = "";
    String contentPlaceholder = "";
    if (template.getSubject() != null) {
      subjectPlaceholder = template.getSubject();
    }
    if (template.getContent() != null) {
      contentPlaceholder = template.getContent();
    }
    String subject = null;
    String content = null;
    subject = templateService.processTemplate(subjectPlaceholder,
      replaceEmailTemplatePlaceholders(id, template.getAvailablePart()));
    content = templateService.processTemplate(contentPlaceholder,
      replaceEmailTemplatePlaceholders(id, template.getAvailablePart()));
    StringBuilder to = new StringBuilder();
    StringBuilder cc = new StringBuilder();
    StringBuilder bcc = new StringBuilder();
    List<String> ccList = new ArrayList<>();
    List<String> bccList = new ArrayList<>();
    setEMailAttributes(id, template, to, cc, bcc, ccList, bccList, ids);

    // 3.Erstellung E-Mail zur Nachweisaufforderung Zertifikat
    // this audit is created, if a user of the tenant "Kanton Bern" goes to a
    // company, the Doc-part there, selects "Nachweisbrief" and clicks on
    // "Erstellen"
    if (template.getEmailTemplate().getShortCode()
      .equals(EmailTemplate.TEMPLATE_SHORT_CODE.ET06.name())) {
      CompanyEntity companyEntity = em.find(CompanyEntity.class, id);

      StringBuilder additionalInfo = new StringBuilder(companyEntity.getCompanyName()).append("[#]")
        .append(companyEntity.getProofStatusFabe()).append("[#]")
        .append(template.getDescription());

      audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
        AuditGroupName.COMPANY.name(), AuditMessages.EMAIL_CREATED_3.name(), getUser().getId(),
        id, null, additionalInfo.toString(), LookupValues.EXTERNAL_LOG);
    }
    // Create an EmailDTO in order to resolve mailTo issue with more than 2000 characters.
    // Also, return a message which informs the user about this limitation.
    EmailDTO email = new EmailDTO();
    email.setBody(content);
    int number = content.length() + subject.length();
    if (number > 1300) {
      content = "";
      email.setMessage(ValidationMessages.MANDATORY_ERROR_MESSAGE);
    }
    email.setMailTo(
      openMailDesktopClient(content, subject, to.toString(), cc.toString(), bcc.toString()));
    return email;
  }

  /**
   * Prepare submiss email.
   *
   * @param id the id
   * @param template the template
   * @param ids the ids
   * @return the string
   */
  private String prepareSubmissEmail(String id, EmailTemplateTenantDTO template, List<String> ids) {

    LOGGER.log(Level.CONFIG, "Executing method prepareSubmissEmail, Parameters: id: {0}"
        + ", template: {1}, ids: {2}",
      new Object[]{id, template, ids});

    String subjectPlaceholder = "";
    String contentPlaceholder = "";
    if (template.getSubject() != null) {
      subjectPlaceholder = template.getSubject();
    }
    if (template.getContent() != null) {
      contentPlaceholder = template.getContent();
    }
    String subject = null;
    String content = null;
    subject = templateService.processTemplate(subjectPlaceholder,
      replaceEmailTemplatePlaceholders(id, template.getAvailablePart()));
    content = templateService.processTemplate(contentPlaceholder,
      replaceEmailTemplatePlaceholders(id, template.getAvailablePart()));
    StringBuilder to = new StringBuilder();
    StringBuilder cc = new StringBuilder();
    StringBuilder bcc = new StringBuilder();
    List<String> ccList = new ArrayList<>();
    List<String> bccList = new ArrayList<>();
    setEMailAttributes(id, template, to, cc, bcc, ccList, bccList, ids);

    // 3.Erstellung E-Mail zur Nachweisaufforderung Zertifikat
    // this audit is created, if a user of the tenant "Kanton Bern" goes to a
    // company, the Doc-part there, selects "Nachweisbrief" and clicks on
    // "Erstellen"
    if (template.getEmailTemplate().getShortCode()
      .equals(EmailTemplate.TEMPLATE_SHORT_CODE.ET06.name())) {
      CompanyEntity companyEntity = em.find(CompanyEntity.class, id);

      StringBuilder additionalInfo = new StringBuilder(companyEntity.getCompanyName()).append("[#]")
        .append(companyEntity.getProofStatusFabe()).append("[#]")
        .append(template.getDescription());

      audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
        AuditGroupName.COMPANY.name(), AuditMessages.EMAIL_CREATED_3.name(), getUser().getId(),
        id, null, additionalInfo.toString(), LookupValues.EXTERNAL_LOG);
    }
    return openMailDesktopClient(content, subject, to.toString(), cc.toString(), bcc.toString());
  }

  /**
   * Audit log.
   *
   * @param level the level
   * @param event the event
   * @param groupName the group name
   * @param auditMessage the audit message
   * @param additionalInfo the additional info
   * @param id the id
   */
  private void auditLog(String level, String event, String groupName, String auditMessage,
    String additionalInfo, String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method auditLog, Parameters: level: {0}, event: {1}" + ", groupName: {2}"
        + ", auditMessage: {3}" + ", additionalInfo: {4}, id: {5}",
      new Object[]{level, event, groupName, auditMessage, additionalInfo, id});

    audit.createLogAudit(level, event, groupName, auditMessage, getUser().getId(), id, null,
      additionalInfo, LookupValues.EXTERNAL_LOG);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.EmailService#emailToTypeData()
   */
  @Override
  public List<MasterListTypeDataDTO> emailToTypeData() {

    LOGGER.log(Level.CONFIG, "Executing method emailToTypeData");

    List<MasterListTypeDataDTO> typeDTOs;
    // Retrieving email template tenant data.
    List<EmailTemplateTenant> emailTemplateTenants =
      new JPAQueryFactory(em).selectFrom(qEmailTemplateTenant)
        .where(qEmailTemplateTenant.tenant.id.eq(usersService.getTenant().getId())).fetch();
    // Mapping email data to master list type data.
    typeDTOs = EmailToTypeDataMapper.INSTANCE.toMasterListTypeDataDTOs(emailTemplateTenants);
    return typeDTOs;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.EmailService#getEmailEntryById(java.lang.String)
   */
  @Override
  public EmailTemplateTenantDTO getEmailEntryById(String entryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getEmailEntryById, Parameters: entryId: {0}", entryId);

    EmailTemplateTenantDTO emailTemplateTenant = EmailTemplateTenantDTOMapper.INSTANCE
      .toEmailTemplateTenantDTO(new JPAQueryFactory(em).selectFrom(qEmailTemplateTenant)
        .where(qEmailTemplateTenant.id.eq(entryId)).fetchOne());
    return checkAttributes(emailTemplateTenant);
  }

  /**
   * Checks if all the email attribute types are present. If not, it creates them.
   *
   * @param emailTemplateTenant the email template tenant
   * @return the email template tenant with all the attributes sorted by type.
   */
  private EmailTemplateTenantDTO checkAttributes(EmailTemplateTenantDTO emailTemplateTenant) {
    // Check if all the attribute types are present.
    boolean toFound = false;
    boolean ccFound = false;
    boolean bccFound = false;
    for (EmailAttributesDTO attribute : emailTemplateTenant.getAttributes()) {
      if (attribute.getSendType().equals(EmailTemplate.SEND_TYPE.TO)) {
        // Send type "TO" found.
        toFound = true;
      } else if (attribute.getSendType().equals(EmailTemplate.SEND_TYPE.CC)) {
        // Send type "CC" found.
        ccFound = true;
      } else if (attribute.getSendType().equals(EmailTemplate.SEND_TYPE.BCC)) {
        // Send type "BCC" found.
        bccFound = true;
      }
    }
    EmailAttributesDTO attribute = null;
    // Add the missing attributes.
    if (!toFound) {
      // Add "TO" send type to attributes.
      attribute = new EmailAttributesDTO();
      attribute.setSendType(EmailTemplate.SEND_TYPE.TO);
      emailTemplateTenant.getAttributes().add(attribute);
    }
    if (!ccFound) {
      // Add "CC" send type to attributes.
      attribute = new EmailAttributesDTO();
      attribute.setSendType(EmailTemplate.SEND_TYPE.CC);
      emailTemplateTenant.getAttributes().add(attribute);
    }
    if (!bccFound) {
      // Add "BCC" send type to attributes.
      attribute = new EmailAttributesDTO();
      attribute.setSendType(EmailTemplate.SEND_TYPE.BCC);
      emailTemplateTenant.getAttributes().add(attribute);
    }
    // Sort attributes by send type.
    Collections.sort(emailTemplateTenant.getAttributes(), ComparatorUtil.sortAttributesBySendType);
    return emailTemplateTenant;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.EmailService#openCompanyEMailDefaultClient(java.
   * util.List, ch.bern.submiss.services.api.dto.EmailTemplateDTO)
   */
  @Override
  public String openCompanyEMailDefaultClient(List<String> ids, EmailTemplateTenantDTO template) {

    LOGGER.log(Level.CONFIG,
      "Executing method openCompanyEMailDefaultClient, Parameters: ids: {0}"
        + ", template: {1}",
      new Object[]{ids, template});

    //create auditlog for every company that email is going to be sent.
    List<CompanyDTO> companies = companyService.getCompaniesById(ids);

    for (CompanyDTO company : companies) {
      StringBuilder additionalInfo = new StringBuilder(company.getCompanyName()).append("[#]")
        .append(company.getProofStatusFabe()).append("[#]").append(template.getDescription());

      audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
        AuditGroupName.COMPANY.name(), AuditMessages.EMAIL_CREATED.name(), getUser().getId(),
        company.getId(), null, additionalInfo.toString(), LookupValues.EXTERNAL_LOG);

    }
    return prepareSubmissEmail(null, template, ids);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.EmailService#openSpecificEMailTemplate(ch.bern.
   * submiss.services.api.dto.SubmissTaskDTO)
   */
  @Override
  public String openSpecificEMailTemplate(SubmissTaskDTO submissTaskDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method openSpecificEMailTemplate, Parameters: submissTaskDTO: {0}",
      submissTaskDTO);

    EmailTemplateTenantDTO template = new EmailTemplateTenantDTO();
    String id = null;
    // Set company id of submissTaskDTO to companyIds list.
    List<String> companyIds = new ArrayList<>();
    if (submissTaskDTO.getCompany() != null) {
      companyIds.add((submissTaskDTO.getCompany().getId()));
    }
    // Set id if task is derived from project or company part.
    if (submissTaskDTO.getSubmission() != null) {
      id = submissTaskDTO.getSubmission().getId();
    } else {
      id = submissTaskDTO.getCompany().getId();
    }
    switch (submissTaskDTO.getDescription()) {
      case CHECK_TENDERLIST:
        template = getEmailTemplateTenantByShortCode(EmailTemplate.TEMPLATE_SHORT_CODE.ET13.name());
        break;
      case PROOF_REQUEST_PL_XY:
        if (submissTaskDTO.getSubmission() != null) {
          template = getEmailTemplateTenantByShortCode(
            EmailTemplate.TEMPLATE_SHORT_CODE.ET09.name());
        } else {
          template = getEmailTemplateTenantByShortCode(
            EmailTemplate.TEMPLATE_SHORT_CODE.ET10.name());
          // 6.Beantwortung Anfrage PL XY" - if the user clicks on the E-Mail Button in the
          // Pendenzenliste of the Pendenz "Nachweisaufforderung für PL XY ausstehend"
          StringBuilder additionalInfo = new StringBuilder();

          if (submissTaskDTO.getCompany() != null) {

            CompanyEntity companyEntity = em
              .find(CompanyEntity.class, submissTaskDTO.getCompany().getId());
            additionalInfo = new StringBuilder(companyEntity.getCompanyName())
              .append("[#]").append(companyEntity.getProofStatusFabe())
              .append("[#]").append(submissTaskDTO.getCreatedBy());

          } else {
            additionalInfo.append("[#]").append("[#]");
          }
          audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditGroupName.COMPANY.name(), AuditMessages.EMAIL_CREATED_6.name(),
            getUser().getId(), id, null, additionalInfo.toString(), LookupValues.EXTERNAL_LOG);
        }
        break;
      default:
        break;
    }
    return prepareSubmissEmail(id, template, companyIds);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.EmailService#isDescriptionUnique(java.lang.String,
   * java.lang.String)
   */
  @Override
  public boolean isDescriptionUnique(String description, String id,
    EmailTemplate.AVAILABLE_PART availablePart) {

    LOGGER.log(Level.CONFIG, "Executing method isDescriptionUnique, Parameters: "
        + "description: {0}, id: {1}, availablePart: {2}",
      new Object[]{description, id, availablePart});

    if (id == null) {
      // If the id is null (case of creating a new entry), replace the null value with an empty
      // String in order to avoid the null pointer exception.
      id = StringUtils.EMPTY;
    }
    return (new JPAQueryFactory(em).select(qEmailTemplateTenant).from(qEmailTemplateTenant)
      .where(qEmailTemplateTenant.id.notEqualsIgnoreCase(id),
        qEmailTemplateTenant.tenant.id.eq(usersService.getTenant().getId()),
        qEmailTemplateTenant.availablePart.eq(availablePart),
        qEmailTemplateTenant.description.eq(description))
      .fetchCount() == 0);
  }

  @Override
  public void saveEmailEntry(EmailTemplateTenantDTO emailTemplateTenantDTO) {
    EmailTemplateTenant emailTemplateTenant =
      EmailTemplateTenantDTOMapper.INSTANCE.toEmailTemplateTenant(emailTemplateTenantDTO);
    if (StringUtils.isNotBlank(emailTemplateTenant.getId())) {
      // Update the email template tenant entry.
      em.merge(emailTemplateTenant);

      audit.createLogAudit(AuditLevel.REFERENCE_DATA_LEVEL.name(), AuditEvent.UPDATE.name(),
        AuditGroupName.REFERENCE_DATA.name(), AuditMessages.EMAIL_CREATED.name(), getUser().getId(),
        emailTemplateTenant.getId(), null, null, LookupValues.INTERNAL_LOG);

    } else {
      // In case of a new email template tenant entry, create a new email template entry.
      EmailTemplateEntity emailTemplateEntity = new EmailTemplateEntity();
      // Assign default values.
      emailTemplateEntity.setShortCode(StringUtils.EMPTY);
      emailTemplateEntity.setWorkflowRelated(false);
      // Save the new email template entry.
      em.persist(emailTemplateEntity);
      // Assign the email template to the email template tenant.
      emailTemplateTenant.setEmailTemplate(emailTemplateEntity);
      // Assign the tenant.
      emailTemplateTenant.setTenant(TenantMapper.INSTANCE.toTenant(usersService.getTenant()));
      // Save the new email template tenant entry.
      em.persist(emailTemplateTenant);

      audit.createLogAudit(AuditLevel.REFERENCE_DATA_LEVEL.name(), AuditEvent.CREATE.name(),
        AuditGroupName.REFERENCE_DATA.name(), AuditMessages.EMAIL_CREATED.name(), getUser().getId(),
        emailTemplateTenant.getId(), null, null, LookupValues.INTERNAL_LOG);
    }
    for (EmailAttributes attribute : emailTemplateTenant.getAttributes()) {
      // Assign to every attribute the current email template tenant.
      attribute.setTemplateTenant(emailTemplateTenant);
      if (StringUtils.isNotBlank(attribute.getId())) {
        // Update the attribute entry.
        em.merge(attribute);
      } else {
        // Save the new attribute entry.
        em.persist(attribute);
      }
    }
  }

  @Override
  public List<EmailTemplateTenantDTO> getNonMigratedEmailTemplates(
    EmailTemplate.AVAILABLE_PART availablePart) {
    String tenantId = usersService.getTenant().getId();
    return EmailTemplateTenantDTOMapper.INSTANCE
      .toEmailTemplateTenantDTO(new JPAQueryFactory(em).selectFrom(qEmailTemplateTenant)
        .where(qEmailTemplateTenant.emailTemplate.workflowRelated.isFalse(),
          qEmailTemplateTenant.isActive.isTrue(), qEmailTemplateTenant.tenant.id.eq(tenantId),
          qEmailTemplateTenant.availablePart.eq(availablePart))
        .fetch());
  }

  @Override
  public String openUserEmailTemplate(Boolean acceptUser, String userId) {
    EmailTemplateTenantDTO template = null;
    if (acceptUser) {
      template =
        getEmailTemplateTenantByShortCode(EmailTemplate.TEMPLATE_SHORT_CODE.ET14.name());
    } else {
      template =
        getEmailTemplateTenantByShortCode(EmailTemplate.TEMPLATE_SHORT_CODE.ET15.name());
    }
    return prepareSubmissEmail(userId, template, null);
  }

  @Override
  public void emailSecurityCheck() {

    LOGGER.log(Level.CONFIG, "Executing method emailSecurityCheck");

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.SENT_EMAIL.getValue(), null);
  }
}
