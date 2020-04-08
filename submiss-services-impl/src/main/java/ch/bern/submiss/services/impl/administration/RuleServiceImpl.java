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

import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.EmailService;
import ch.bern.submiss.services.api.administration.LexiconService;
import ch.bern.submiss.services.api.administration.RuleService;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.administration.SubDocumentService;
import ch.bern.submiss.services.api.administration.TenderStatusHistoryService;
import ch.bern.submiss.services.api.constants.EmailTemplate;
import ch.bern.submiss.services.api.constants.EmailTemplate.TEMPLATE_SHORT_CODE;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.Rule;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.DocumentDTO;
import ch.bern.submiss.services.api.dto.EmailTemplateTenantDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubmissRulesDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.dto.TenderStatusHistoryDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.rules.api.RulesRuntimeService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class RuleServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {RuleService.class})
@Singleton
public class RuleServiceImpl extends BaseService implements RuleService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(RuleServiceImpl.class.getName());

  /**
   * The Constant TEMPCODES_STR.
   */
  private static final String TEMPCODES_STR = "templateCodes";

  /**
   * The Constant USER_ROLE_STR.
   */
  private static final String USER_ROLE_STR = "userRole";

  /**
   * The Constant SUBMISS_STATUS_STR.
   */
  private static final String SUBMISS_STATUS_STR = "submissStatuses";

  /**
   * The Constant TENANT_NAME_STR.
   */
  private static final String TENANT_NAME_STR = "tenantName";
  /**
   * The tenant service.
   */
  @Inject
  protected SDTenantService sdTenantService;
  /**
   * The rules runtime service.
   */
  @Inject
  @OsgiService
  private RulesRuntimeService rulesRuntimeService;
  /**
   * The submiss rule service.
   */
  @Inject
  private SubmissRulesServiceImpl submissRuleService;
  /**
   * The sd service.
   */
  @Inject
  private SDServiceImpl sdService;
  /**
   * The lexicon service.
   */
  @Inject
  private LexiconService lexiconService;
  /**
   * The company service.
   */
  @Inject
  private CompanyService companyService;

  /**
   * The email sevice.
   */
  @Inject
  private EmailService emailSevice;

  /**
   * The tender status history service.
   */
  @Inject
  private TenderStatusHistoryService tenderStatusHistoryService;

  /**
   * The sub document service.
   */
  @Inject
  private SubDocumentService documentService;

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.RuleService#runProjectTemplateRules(ch.bern.submiss
   * .services.api.dto.SubmissionDTO, ch.bern.submiss.services.api.dto.CompanyDTO,
   * ch.bern.submiss.services.api.dto.DocumentDTO, ch.bern.submiss.services.api.dto.OfferDTO,
   * java.util.Map, java.lang.String)
   */
  @Override
  public void runProjectTemplateRules(SubmissionDTO submissionDTO, CompanyDTO companyDTO,
    DocumentDTO documentDTO, OfferDTO offerDTO, Map<String, String> placeholderMappings,
    String ruleCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method runProjectTemplateRules, Parameters: submissionDTO: {0}, "
        + "companyDTO: {1}, documentDTO: {2}, offerDTO: {3}, placeholderMappings: {4}",
      new Object[]{submissionDTO, companyDTO, documentDTO, offerDTO, placeholderMappings});

    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(ruleCode);
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {
      Map<String, Object> globals = new HashMap<>();
      globals.put("placeholderMappings", placeholderMappings);

      List<Object> facts = new ArrayList<>();
      facts.add(submissionDTO);
      facts.add(companyDTO);
      facts.add(documentDTO);
      facts.add(offerDTO);
      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.RuleService#getProjectAllowedTemplates(ch.bern.
   * submiss.services.api.dto.SubmissionDTO)
   */
  @Override
  public List<MasterListValueHistoryDTO> getProjectAllowedTemplates(SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProjectAllowedTemplates, Parameters: "
        + "submissionDTO: {0}",
      submissionDTO);

    List<String> templateCodes = new ArrayList<>();
    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(Rule.RU001.name());
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {
      TenantDTO tenant = sdTenantService
        .getTenantById(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());

      List<TenderStatusHistoryDTO> submissionStatuses = tenderStatusHistoryService
        .getSubmissionStatuses(submissionDTO.getId());

      Map<String, Object> globals = new HashMap<>();
      globals.put(TEMPCODES_STR, templateCodes);
      globals.put(USER_ROLE_STR, getGroupName(getUser()));
      globals.put(SUBMISS_STATUS_STR, LookupValues.getSubmissstatuses());
      globals.put(TENANT_NAME_STR, tenant.getName());
      globals.put("legalHearingTerminateDocumentExists",
        documentService.legalHearingTerminateDocumentExists(submissionDTO.getId(), Template.RECHTLICHES_GEHOR));

      // check for case where submission status list
      // has less than 2 entries
      if (submissionStatuses.size() > 1) {
        globals.put("previousStatusId", Integer.valueOf(submissionStatuses.get(1).getStatusId()));
      } else {
        globals.put("previousStatusId", Integer.valueOf(0));
      }

      List<Object> facts = new ArrayList<>();
      facts.add(submissionDTO);

      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }
    // Remove the "Zuschlagsbewertung" template if present, in case of a selective process with
    // an empty evaluationThrough award property. This is done, to make sure that the award form is
    // saved at least once, before the "Zuschlagsbewertung" document can be generated.
    if (templateCodes.contains(Template.ZUSCHLAGSBEWERTUNG)
      && submissionDTO.getProcess().equals(Process.SELECTIVE)
      && StringUtils.isBlank(submissionDTO.getEvaluationThrough())) {
      templateCodes.removeIf((String templateCode) -> templateCode.equals(Template.ZUSCHLAGSBEWERTUNG));
    }
    return sdService.getMasterListHistoryByCode(templateCodes);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.RuleService#retrieveProjectEmailTemplates(ch.bern.
   * submiss.services.api.dto.SubmissionDTO, ch.bern.submiss.services.api.dto.CompanyDTO)
   */
  @Override
  public List<EmailTemplateTenantDTO> retrieveProjectEmailTemplates(SubmissionDTO submissionDTO,
    CompanyDTO companyDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveProjectEmailTemplates, Parameters: "
        + "submissionDTO: {0}, companyDTO: {1}",
      new Object[]{submissionDTO, companyDTO});

    List<String> templateCodes = new ArrayList<>();
    List<EmailTemplateTenantDTO> templates = new ArrayList<>();
    if (submissionDTO != null) {
      // Add non-migrated active project email templates to templates.
      templates = emailSevice
        .getNonMigratedEmailTemplates(EmailTemplate.AVAILABLE_PART.PROJECT_PART);
      // Execute rule.
      ruleExecute(submissionDTO, companyDTO, templateCodes, Rule.RU008.name());
    } else if (companyDTO != null) {
      // Add non-migrated active company email templates to templates.
      templates = emailSevice
        .getNonMigratedEmailTemplates(EmailTemplate.AVAILABLE_PART.COMPANY_PART);
      // Execute rule.
      ruleExecute(submissionDTO, companyDTO, templateCodes, Rule.RU009.name());
    }
    templates.addAll(emailSevice.retrieveEmailTemplates(templateCodes));

    // If user role is PL and one of the templates is Submittentenliste prüfen, remove it.
    for (Iterator<EmailTemplateTenantDTO> iterator = templates.iterator(); iterator.hasNext(); ) {
      EmailTemplateTenantDTO template = iterator.next();
      if (template.getEmailTemplate().getShortCode().equals(TEMPLATE_SHORT_CODE.ET07.toString())
        && getGroupName(getUser()).equals(Group.PL.getValue())) {
        iterator.remove();
        break;
      }
    }

    Collections.sort(templates, ComparatorUtil.sortEmailTemplateTenantsByDescription);
    return templates;
  }

  /**
   * Rule execute.
   *
   * @param submissionDTO the submission DTO
   * @param companyDTO the company DTO
   * @param templateCodes the template codes
   * @param ruleName the rule name
   */
  private void ruleExecute(SubmissionDTO submissionDTO, CompanyDTO companyDTO,
    List<String> templateCodes, String ruleName) {

    LOGGER.log(Level.CONFIG, "Executing method ruleExecute, Parameters: submissionDTO: {0}, "
        + "companyDTO: {1}, templateCodes: {2}, ruleName: {3}",
      new Object[]{submissionDTO, companyDTO, templateCodes, ruleName});

    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(ruleName);
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {

      Map<String, Object> globals = new HashMap<>();
      List<Object> facts = new ArrayList<>();
      globals.put(TEMPCODES_STR, templateCodes);
      if (ruleName.equals(Rule.RU008.name())) {
        globals.put(SUBMISS_STATUS_STR, LookupValues.getSubmissstatuses());
        globals.put(USER_ROLE_STR, getGroupName(getUser()));
        facts.add(submissionDTO);
      }
      if (ruleName.equals(Rule.RU009.name())) {
        facts.add(companyDTO);
      }
      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.RuleService#getProjectUploadAllowedTemplates()
   */
  @Override
  public List<MasterListValueHistoryDTO> getProjectUploadAllowedTemplates() {

    LOGGER.log(Level.CONFIG, "Executing method getProjectUploadAllowedTemplates");

    List<String> templateCodes = new ArrayList<>();
    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(Rule.RU003.name());
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {

      Map<String, Object> globals = new HashMap<>();
      globals.put(TEMPCODES_STR, templateCodes);
      globals.put(USER_ROLE_STR, getGroupName(getUser()));

      List<Object> facts = new ArrayList<>();

      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }

    return sdService.getMasterListHistoryByCode(templateCodes);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.RuleService#getCompanyAllowedTemplates(ch.bern.
   * submiss.services.api.dto.CompanyDTO)
   */
  @Override
  public List<MasterListValueHistoryDTO> getCompanyAllowedTemplates(CompanyDTO companyDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyAllowedTemplates, Parameters: companyDTO: {0}",
      companyDTO);

    List<String> templateCodes = new ArrayList<>();
    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(Rule.RU002.name());
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {
      TenantDTO tenant = sdTenantService
        .getTenantById(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
      int proofStatusNoConsultation = companyService.getProofState(companyDTO);

      Map<String, Object> globals = new HashMap<>();
      globals.put(TEMPCODES_STR, templateCodes);
      globals.put(USER_ROLE_STR, getGroupName(getUser()));
      globals.put(TENANT_NAME_STR, tenant.getName());
      globals.put("proofStatusNoConsultation", proofStatusNoConsultation);

      List<Object> facts = new ArrayList<>();
      facts.add(companyDTO);

      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }

    return sdService.getMasterListHistoryByCode(templateCodes);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.RuleService#getCompanyUploadAllowedTemplates()
   */
  @Override
  public List<MasterListValueHistoryDTO> getCompanyUploadAllowedTemplates() {

    LOGGER.log(Level.CONFIG, "Executing method getCompanyUploadAllowedTemplates");

    List<String> templateCodes = new ArrayList<>();
    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(Rule.RU004.name());
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {

      Map<String, Object> globals = new HashMap<>();
      globals.put(TEMPCODES_STR, templateCodes);
      globals.put(USER_ROLE_STR, getGroupName(getUser()));

      List<Object> facts = new ArrayList<>();

      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }

    return sdService.getMasterListHistoryByCode(templateCodes);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.RuleService#replaceSubmissionPlaceholders(ch.bern.
   * submiss.services.api.dto.SubmissionDTO, java.util.Map)
   */
  @Override
  public void replaceSubmissionPlaceholders(SubmissionDTO submissionDTO,
    Map<String, String> placeholderMappings) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceSubmissionPlaceholders, Parameters: submissionDTO: {0}, "
        + "placeholderMappings: {1}",
      new Object[]{submissionDTO, placeholderMappings});

    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(Rule.RU005.name());
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {
      Map<String, Object> globals = new HashMap<>();
      submissionDTO.setCurrentProcess(
        lexiconService.getTranslation(submissionDTO.getProcess().name(), "de-ch"));

      globals.put("placeholderMappings", placeholderMappings);

      List<Object> facts = new ArrayList<>();
      List<String> rule = new ArrayList<>();

      rule.add(submissRuleDTO.getContent());
      facts.add(submissionDTO);
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.RuleService#getProjectDepartmentTemplates(ch.bern.
   * submiss.services.api.dto.SubmissionDTO)
   */
  @Override
  public List<MasterListValueHistoryDTO> getProjectDepartmentTemplates(
    SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProjectDepartmentTemplates, Parameters: submissionDTO: {0}",
      submissionDTO);

    List<String> templateCodes = new ArrayList<>();
    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(Rule.RU006.name());
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {

      Map<String, Object> globals = new HashMap<>();
      globals.put(TEMPCODES_STR, templateCodes);
      globals.put(USER_ROLE_STR, getGroupName(getUser()));
      globals.put(SUBMISS_STATUS_STR, LookupValues.getSubmissstatuses());

      List<Object> facts = new ArrayList<>();
      facts.add(submissionDTO);

      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }
    return sdService.getMasterListHistoryByCode(templateCodes);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.RuleService#getUserAllowedTemplates()
   */
  @Override
  public List<MasterListValueHistoryDTO> getUserAllowedTemplates() {

    LOGGER.log(Level.CONFIG, "Executing method getUserAllowedTemplates");

    List<String> templateCodes = new ArrayList<>();
    SubmissRulesDTO submissRuleDTO = submissRuleService.getSubmissRuleByCode(Rule.RU010.name());
    if (StringUtils.isNotEmpty(submissRuleDTO.getContent())) {
      TenantDTO tenant = sdTenantService
        .getTenantById(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());

      Map<String, Object> globals = new HashMap<>();
      globals.put(TEMPCODES_STR, templateCodes);
      globals.put(USER_ROLE_STR, getGroupName(getUser()));
      globals.put(TENANT_NAME_STR, tenant.getName());

      List<Object> facts = new ArrayList<>();

      List<String> rule = new ArrayList<>();
      rule.add(submissRuleDTO.getContent());
      rulesRuntimeService.statelessExecute(rule, facts, globals, this.getClass().getClassLoader());
    }
    return sdService.getMasterListHistoryByCode(templateCodes);
  }
}
