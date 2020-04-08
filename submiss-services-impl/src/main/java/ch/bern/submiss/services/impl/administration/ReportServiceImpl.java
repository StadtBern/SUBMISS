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

import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.ReportService;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ReportDTO;
import ch.bern.submiss.services.api.dto.ReportResultsDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.mappers.CustomSubmissionMapper;
import ch.bern.submiss.services.impl.mappers.OfferDTOMapper;
import ch.bern.submiss.services.impl.model.DepartmentEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.QDepartmentEntity;
import ch.bern.submiss.services.impl.model.QDirectorateEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QJointVentureEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;
import ch.bern.submiss.services.impl.model.QSubmissionAwardInfoEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.QTenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.google.common.collect.Table;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class ReportServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {ReportService.class})
@Singleton
public class ReportServiceImpl extends BaseService implements ReportService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(ReportServiceImpl.class.getName());
  /**
   * The Constant DEFAULT_REPORT_START_NAME.
   */
  private static final String DEFAULT_REPORT_START_NAME = "Auswertung_";
  /**
   * The Constant TOTAL_BY_OBJECT.
   */
  private static final String TOTAL_BY_OBJECT = "totalByObject";
  /**
   * The Constant TOTAL_BY_PROJECT.
   */
  private static final String TOTAL_BY_PROJECT = "totalByProject";
  /**
   * The Constant TOTAL_BY_PROJECT_CREDIT_NO.
   */
  private static final String TOTAL_BY_PROJECT_CREDIT_NO = "totalByProjectCreditNo";
  /**
   * The Constant TOTAL_BY_PROJECT_MANAGER.
   */
  private static final String TOTAL_BY_PROJECT_MANAGER = "totalByProjectManager";
  /**
   * The Constant TOTAL_BY_WORKTYPE.
   */
  private static final String TOTAL_BY_WORKTYPE = "totalByWorkType";
  /**
   * The Constant TOTAL_BY_DIRACTORATE.
   */
  private static final String TOTAL_BY_DIRACTORATE = "totalByDirectorate";
  /**
   * The Constant TOTAL_BY_DEPARTMENT.
   */
  private static final String TOTAL_BY_DEPARTMENT = "totalByDepartment";
  /**
   * The Constant TOTAL_BY_PROCEDURE.
   */
  private static final String TOTAL_BY_PROCEDURE = "totalByProcedure";
  /**
   * The Constant TOTAL_BY_COMPANY.
   */
  private static final String TOTAL_BY_COMPANY = "totalByCompany";
  /**
   * The Constant TOTAL_BY_YEAR.
   */
  private static final String TOTAL_BY_YEAR = "totalByYear";
  /**
   * The document service.
   */
  @OsgiService
  @Inject
  protected DocumentService documentService;
  /**
   * The version service.
   */
  @OsgiService
  @Inject
  protected VersionService versionService;
  /**
   * The version service.
   */
  @Inject
  protected CacheBean cacheBean;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q master list value entity.
   */
  QMasterListValueEntity qMasterListValueEntity = QMasterListValueEntity.masterListValueEntity;
  /**
   * The submission.
   */
  QSubmissionEntity submission = QSubmissionEntity.submissionEntity;
  /**
   * The project.
   */
  QProjectEntity project = QProjectEntity.projectEntity;
  /**
   * The object name.
   */
  QMasterListValueEntity objectName = QMasterListValueEntity.masterListValueEntity;
  /**
   * The object name history.
   */
  QMasterListValueHistoryEntity objectNameHistory =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q department entity.
   */
  QDepartmentEntity qDepartmentEntity = QDepartmentEntity.departmentEntity;
  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
  /**
   * The q directorate history entity.
   */
  QDirectorateHistoryEntity qDirectorateHistoryEntity =
    QDirectorateHistoryEntity.directorateHistoryEntity;
  /**
   * The q joint venture entity.
   */
  QJointVentureEntity qJointVentureEntity = QJointVentureEntity.jointVentureEntity;
  /**
   * The q submission award info entity.
   */
  QSubmissionAwardInfoEntity qSubmissionAwardInfoEntity =
    QSubmissionAwardInfoEntity.submissionAwardInfoEntity;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The q tender status history entity.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity =
    QTenderStatusHistoryEntity.tenderStatusHistoryEntity;
  /**
   * The q directoraty entity.
   */
  QDirectorateEntity qDirectoratyEntity = QDirectorateEntity.directorateEntity;
  /**
   * The work type.
   */
  QMasterListValueEntity workType = QMasterListValueEntity.masterListValueEntity;
  /**
   * The work type history.
   */
  QMasterListValueHistoryEntity workTypeHistory =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The offer service.
   */
  @Inject
  private OfferService offerService;
  /**
   * The template bean.
   */
  @Inject
  private TemplateBean templateBean;

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.ReportService#generateReport(ch.bern.submiss.
   * services.api.dto.ReportResultsDTO)
   */
  public byte[] generateReport(ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateReport, Parameters: reportResults: {0}",
      reportResults);

    reportSecurityCheck();

    byte[] report = null;
    try {
      report = generateXlsxReportFile(reportResults);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return report;
  }

  /**
   * Generate xlsx report file.
   *
   * @param reportResults the report results
   * @return the byte[]
   * @throws JRException the JR exception
   */
  private byte[] generateXlsxReportFile(ReportResultsDTO reportResults) throws JRException {

    LOGGER.log(Level.CONFIG,
      "Executing method generateXlsxReportFile, Parameters: reportResults: {0}",
      reportResults);

    return generateReport(ReportFormat.XLSX, reportResults);
  }

  /**
   * Setting all the reports parameters that are needed.
   *
   * @param parameters the parameters
   * @param reportResults the report results
   * @return the map
   */
  private Map<String, Object> setReportParameters(Map<String, Object> parameters,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method setReportParameters, Parameters: reportResults: {0}",
      reportResults);

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    String reportTotalizationBy = getReportTotalizationBy(reportResults.getTotalizationBy());
    JRBeanCollectionDataSource reportDataSource =
      new JRBeanCollectionDataSource(reportResults.getSubmissions());
    parameters.put("ReportDataSource", reportDataSource);
    parameters.put("TotalizationBy", reportTotalizationBy);
    if (reportResults.getStartDate() != null) {
      parameters.put("ReportStartDate", dateFormat.format(reportResults.getStartDate()));
    }
    if (reportResults.getEndDate() != null) {
      parameters.put("ReportEndDate", dateFormat.format(reportResults.getEndDate()));
    }
    Map<String, String> searchedCriteriaMap = reportResults.getSearchedCriteria();
    for (Map.Entry<String, String> searchedCriteria : searchedCriteriaMap.entrySet()) {
      parameters.put(searchedCriteria.getKey(), searchedCriteria.getValue());
    }
    BigDecimal totalAmount = getReportTotalAmount(reportResults.getSubmissions());
    parameters.put("ReportTotalAmount", totalAmount);

    // Get the last row amount in order to added in the last sub group total
    SubmissionDTO lastSubmission =
      reportResults.getSubmissions().get(reportResults.getSubmissions().size() - 1);
    BigDecimal lastRowAmountSum = getReportLastRowAmount(lastSubmission);
    parameters.put("LastRowAmountSum", lastRowAmountSum);
    return parameters;
  }

  /**
   * Gets the report last row amount.
   *
   * @param lastSubmission the last submission
   * @return the report last row amount
   */
  private BigDecimal getReportLastRowAmount(SubmissionDTO lastSubmission) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportLastRowAmount, Parameters: lastSubmission {0}",
      lastSubmission);

    BigDecimal lastRowAmountSum = new BigDecimal(0);
    if (lastSubmission != null) {
      for (BigDecimal amount : lastSubmission.getReportAmount()) {
        lastRowAmountSum = lastRowAmountSum.add(amount);
      }
    }
    return lastRowAmountSum;
  }

  /**
   * Gets the total amount for the report and rounds all amount values.
   *
   * @param submissions the submissions
   * @return the report total amount
   */
  private BigDecimal getReportTotalAmount(List<SubmissionDTO> submissions) {

    LOGGER.log(Level.CONFIG, "Executing method getReportTotalAmount, Parameters: submissions: {0}",
      submissions);

    BigDecimal amountTotal = new BigDecimal(0);
    for (SubmissionDTO submissionDTO : submissions) {
      if (submissionDTO != null && submissionDTO.getReportAmount() != null) {
        List<BigDecimal> amountList = new ArrayList<>();
        for (BigDecimal awardedSubmissionAmount : submissionDTO.getReportAmount()) {
          BigDecimal amount =
            BigDecimal.valueOf(customRoundNumber(awardedSubmissionAmount.doubleValue()));
          amountList.add(amount);
          amountTotal = amountTotal.add(amount);
        }
        submissionDTO.setReportAmount(amountList);
      }
    }
    return BigDecimal.valueOf(customRoundNumber(amountTotal.doubleValue()));
  }

  /**
   * Generate report.
   *
   * @param format the format
   * @param reportResults the report results
   * @return the byte[]
   * @throws JRException the JR exception
   */
  private byte[] generateReport(ReportFormat format, ReportResultsDTO reportResults)
    throws JRException {

    LOGGER.log(Level.CONFIG,
      "Executing method generateReport, Parameters: format: {0}, "
        + "reportedResults: {1}",
      new Object[]{format, reportResults});

    if (format.equals(ReportFormat.XLSX)) {
      /* Setting the parameters of report */
      Map<String, Object> parameters = new HashMap<>();
      boolean landScape = true;
      setReportParameters(parameters, reportResults);
      /* Setting Report's name */
      String filename = getReportFileName(reportResults.getTotalizationBy());
      /* Condition only for totalization by company where we have to modify the results */
      if (reportResults.getTotalizationBy().equals(TOTAL_BY_COMPANY)) {
        reportResults.setSubmissions(getReportCompanyResult(reportResults));
      }
      try (InputStream inputStream = getReportTemplate(filename)) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
        InputStream reportStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
        reportStream.close();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
          new JRBeanCollectionDataSource(reportResults.getSubmissions()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();
        /* Set page configurations for export and printing */
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setPrintPageTopMargin(34);
        configuration.setPrintPageLeftMargin(36);
        configuration.setPrintPageBottomMargin(18);
        configuration.setFitWidth(1);
        configuration.setFitHeight(0);
        /* Set page scale configuration only for totalization by year */
        if (reportResults.getTotalizationBy().equals(TOTAL_BY_YEAR)) {
          landScape = false;
          configuration.setPageScale(96);
        } else if (reportResults.getTotalizationBy().equals(TOTAL_BY_PROJECT)
          || reportResults.getTotalizationBy().equals(TOTAL_BY_OBJECT)) {
          /* Set page scale configuration for totalization by project or by object */
          configuration.setPageScale(52);
        } else {
          /* Set page scale configuration for the other totalization documents */
          configuration.setPageScale(50);
        }
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        outputStream = templateBean.optimizeReportLayout(outputStream, landScape);

        return outputStream.toByteArray();
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    return new byte[0];
  }

  /**
   * Methods that modifies the results only for totalization by company.
   *
   * @param reportResults the report results
   * @return the report company result
   */
  private List<SubmissionDTO> getReportCompanyResult(ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportCompanyResult, Parameters: reportResults: {0}",
      reportResults);

    List<SubmissionDTO> submissionsList = new ArrayList<>();
    List<SubmissionDTO> allResults = reportResults.getSubmissions();
    if (reportResults.getSearchedCompanies() != null
      && !reportResults.getSearchedCompanies().isEmpty()) {
      for (CompanyDTO searchedCompany : reportResults.getSearchedCompanies()) {
        for (SubmissionDTO submissionDTO : allResults) {
          if (companyInSubmission(submissionDTO, searchedCompany)) {
            if (submissionDTO.getSubmittents().size() > 1) {
              companyInAllawardedSubmittents(submissionsList, searchedCompany, submissionDTO);
            } else {
              createCompanySubmission(submissionsList, searchedCompany, submissionDTO,
                submissionDTO.getReportAmount().get(0), submissionDTO.getReportCompanies());
            }
          }
        }
      }
    } else {
      for (SubmissionDTO submissionDTO : allResults) {
        for (int i = 0; i < submissionDTO.getSubmittents().size(); i++) {
          if (submissionDTO.getSubmittents().size() > 1) {
            List<String> awardedCompanyNameList = new ArrayList<>();
            awardedCompanyNameList.add(submissionDTO.getReportCompanies().get(i));
            createCompanySubmission(submissionsList,
              submissionDTO.getSubmittents().get(i).getCompanyId(), submissionDTO,
              submissionDTO.getReportAmount().get(i), awardedCompanyNameList);
            if (submissionDTO.getSubmittents().get(i).getJointVentures() != null) {
              for (CompanyDTO arge : submissionDTO.getSubmittents().get(i).getJointVentures()) {
                createCompanySubmission(submissionsList, arge, submissionDTO,
                  submissionDTO.getReportAmount().get(i), awardedCompanyNameList);
              }
            }
          } else {
            createCompanySubmission(submissionsList,
              submissionDTO.getSubmittents().get(i).getCompanyId(), submissionDTO,
              submissionDTO.getReportAmount().get(i), submissionDTO.getReportCompanies());
            if (submissionDTO.getSubmittents().get(i).getJointVentures() != null) {
              for (CompanyDTO arge : submissionDTO.getSubmittents().get(i).getJointVentures()) {
                createCompanySubmission(submissionsList, arge, submissionDTO,
                  submissionDTO.getReportAmount().get(i), submissionDTO.getReportCompanies());
              }
            }
          }
        }
      }
    }
    return submissionsList;
  }

  /**
   * Create submission for searched company.
   *
   * @param submissionsList the submissions list
   * @param searchedCompany the searched company
   * @param submissionDTO the submission DTO
   * @param amount the amount
   * @param reportCompaniesName the report companies name
   */
  private void createCompanySubmission(List<SubmissionDTO> submissionsList,
    CompanyDTO searchedCompany, SubmissionDTO submissionDTO, BigDecimal amount,
    List<String> reportCompaniesName) {

    LOGGER.log(Level.CONFIG,
      "Executing method createCompanySubmission, Parameters: submissionsList: {0}, "
        + "searchedCompany: {1}, submissionDTO: {1}, amount: {2}, reportCompaniesName: {3}",
      new Object[]{submissionsList, searchedCompany, submissionDTO, amount, reportCompaniesName});
    SubmissionDTO newSubmission = new SubmissionDTO();
    newSubmission.setProcess(submissionDTO.getProcess());
    newSubmission.setProject(submissionDTO.getProject());
    newSubmission.setProcedure(submissionDTO.getProcedure());
    newSubmission.setWorkType(submissionDTO.getWorkType());
    newSubmission.setReportAvailableDate(submissionDTO.getReportAvailableDate());
    newSubmission.setReportCompanies(reportCompaniesName);
    newSubmission.setReportAwardedSubmittentName(searchedCompany.getCompanyName());
    newSubmission.setReportAwardedSubmittentAmount(amount);
    submissionsList.add(newSubmission);
  }

  /**
   * Method that checks if searched company participates in more than one awarded offer for one
   * submission either as main company or ARGE partner and creates submission respectively.
   *
   * @param submissionsList the submissions list
   * @param searchedCompany the searched company
   * @param submissionDTO the submission DTO
   */
  private void companyInAllawardedSubmittents(List<SubmissionDTO> submissionsList,
    CompanyDTO searchedCompany, SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method companyInAllawardedSubmittents, Parameters:  submissionsList: {0}, "
        + "searchedCompany: {1}, submissionDTO: {2}",
      new Object[]{submissionsList, searchedCompany, submissionDTO});

    for (int i = 0; i < submissionDTO.getSubmittents().size(); i++) {
      if (submissionDTO.getSubmittents().get(i).getCompanyId().getId()
        .equals(searchedCompany.getId())) {

        List<String> awardedCompanyNameList = new ArrayList<>();
        awardedCompanyNameList.add(submissionDTO.getReportCompanies().get(i));
        createCompanySubmission(submissionsList, searchedCompany, submissionDTO,
          submissionDTO.getReportAmount().get(i), awardedCompanyNameList);
      } else {
        Set<CompanyDTO> jointVentures = submissionDTO.getSubmittents().get(i).getJointVentures();
        if (jointVentures != null) {
          for (CompanyDTO jointVenture : jointVentures) {
            if (jointVenture.getId().equals(searchedCompany.getId())) {
              List<String> awardedCompanyNameList = new ArrayList<>();
              awardedCompanyNameList.add(submissionDTO.getReportCompanies().get(i));
              createCompanySubmission(submissionsList, searchedCompany, submissionDTO,
                submissionDTO.getReportAmount().get(i), awardedCompanyNameList);
            }
          }
        }
      }
    }
  }

  /**
   * Checks if company exist in submission either as main company or as ARGE partner.
   *
   * @param submission the submission
   * @param searchedCompany the searched company
   * @return true, if successful
   */
  private boolean companyInSubmission(SubmissionDTO submission, CompanyDTO searchedCompany) {

    LOGGER.log(Level.CONFIG,
      "Executing method companyInSubmission, Parameters: submission: {0}, "
        + "searchedCompany: {1}",
      new Object[]{submission, searchedCompany});

    boolean companyInSubmission = false;
    for (SubmittentDTO submittent : submission.getSubmittents()) {
      if (submittent.getCompanyId().getId().equals(searchedCompany.getId())) {
        companyInSubmission = true;
        break;
      } else {
        Set<CompanyDTO> jointVentures = submittent.getJointVentures();
        if (jointVentures != null) {
          for (CompanyDTO jointVenture : jointVentures) {
            if (jointVenture.getId().equals(searchedCompany.getId())) {
              companyInSubmission = true;
              break;
            }
          }
        }
      }
    }
    return companyInSubmission;
  }

  /**
   * Getting the Template of the report.
   *
   * @param filename the filename
   * @return the report template
   */
  public InputStream getReportTemplate(String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportTemplate, Parameters: filename: {0}",
      filename);

    InputStream inputStream = null;
    HashMap<String, String> attributesMap = new HashMap<>();
    List<NodeDTO> templateList = null;
    attributesMap.put(LookupValues.REPORT_TEMPLATE_ATTRIBUTE_NAME, filename);
    templateList =
      documentService.getNodeByAttributes(LookupValues.REPORT_TEMPLATE_FOLDER_ID, attributesMap);
    if (!templateList.isEmpty()) {
      inputStream =
        new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));
    }
    return inputStream;
  }

  /**
   * Setting report's name.
   *
   * @param totalizationBy the totalization by
   * @return the report file name
   */
  public String getReportFileName(String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportFileName, Parameters: totalizationBy: {0}",
      totalizationBy);

    StringBuilder name = new StringBuilder();
    name.append(DEFAULT_REPORT_START_NAME);
    name.append(getReportTotalizationBy(totalizationBy));
    name.append(".xlsx");
    return name.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.ReportService#search(ch.bern.submiss.services.api.
   * dto.ReportDTO)
   */
  @Override
  public ReportResultsDTO search(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method ReportResultsDTO, Parameters: reportDTO: {0}",
      reportDTO);

    //get all information we need from the cacheBean
    Table<String, String, MasterListValueHistoryDTO> activeSd = cacheBean.getActiveSD();
    Table<String, String, List<MasterListValueHistoryDTO>> historySd = cacheBean.getHistorySd();
    Map<String, DepartmentHistoryDTO> activeDepartments = cacheBean.getActiveDepartmentHistorySD();
    Map<String, List<DepartmentHistoryDTO>> historyDepartments = cacheBean.getHistoryDepartments();
    Map<String, DirectorateHistoryDTO> activeDirectorates = cacheBean
      .getActiveDirectorateHistorySD();
    Map<String, List<DirectorateHistoryDTO>> historyDirectorates = cacheBean
      .getHistoryDirectorates();

    List<SubmissionDTO> submissionDTOList =
      CustomSubmissionMapper.INSTANCE.
        toCustomSubmissionDTOList(filter(reportDTO), activeSd, historySd, activeDepartments,
          historyDepartments, activeDirectorates, historyDirectorates);

    setReportCompanies(submissionDTOList);
    ReportResultsDTO results = new ReportResultsDTO();
    results.setSubmissions(submissionDTOList);
    /** Setting reports searched criteria */
    Map<String, String> searchCriteriaMap = getReportSearchCriteria(reportDTO);
    results.setSearchedCriteria(searchCriteriaMap);
    return results;
  }

  /**
   * Setting Companies names (with ARGE partners if exist).
   *
   * @param submissionDTOList the new report companies
   */
  private void setReportCompanies(List<SubmissionDTO> submissionDTOList) {

    LOGGER.log(Level.CONFIG,
      "Executing method setReportCompanies, Parameters: submissionDTOList: {0}",
      submissionDTOList);

    if (!submissionDTOList.isEmpty()) {
      for (SubmissionDTO submissionDTO : submissionDTOList) {
        List<String> submittentsIDs = new ArrayList<>();
        for (SubmittentDTO submittent : submissionDTO.getSubmittents()) {
          submittentsIDs.add(submittent.getId());
        }
        List<OfferEntity> offers = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.id.in(submittentsIDs)
            .and(qOfferEntity.isAwarded.eq(Boolean.TRUE)))
          .fetch();
        generateReportCompanyName(submissionDTO, offers);
        getAvailableDate(submissionDTO);
      }
    }
  }

  /**
   * Gets the available date.
   *
   * @param submissionDTO the submission DTO
   * @return the available date
   */
  private void getAvailableDate(SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAvailableDate, Parameters: submissionDTO: {0}",
      submissionDTO);

    if (submissionDTO != null) {
      Date availableDate =
        new JPAQueryFactory(em).select(qSubmissionAwardInfoEntity.availableDate)
          .from(qSubmissionAwardInfoEntity).where(qSubmissionAwardInfoEntity.submission.id
          .eq(submissionDTO.getId()).and(qSubmissionAwardInfoEntity.level.in(0, 2)))
          .fetchFirst();
      submissionDTO.setReportAvailableDate(availableDate);
    }
  }

  /**
   * Method that handles the name if there are ARGE partners.
   *
   * @param submissionDTO the submission DTO
   * @param offers the offers
   */
  private void generateReportCompanyName(SubmissionDTO submissionDTO, List<OfferEntity> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateReportCompanyName, Parameters: "
        + "submissionDTO: {0}, offers: {1}",
      new Object[]{submissionDTO, offers});

    StringBuilder companyName = new StringBuilder();
    List<String> companiesNameList = new ArrayList<>();
    List<BigDecimal> amountList = new ArrayList<>();
    List<SubmittentDTO> awardedSubmittentsList = new ArrayList<>();
    for (SubmittentDTO submittent : submissionDTO.getSubmittents()) {
      for (OfferEntity offerEntity : offers) {
        if (submittent.getId().equals(offerEntity.getSubmittent().getId())) {
          awardedSubmittentsList.add(submittent);
          companyName.setLength(0);
          companyName.append(submittent.getCompanyId().getCompanyName());
          BigDecimal amount = null;
          if (offerEntity.getManualAmount() != null) {
            amount = offerEntity.getManualAmount();
          } else {
            if (offerEntity.getAmount() != null) {
              amount = offerEntity.getAmount().add(offerService
                .calculateCHFMWSTValue(OfferDTOMapper.INSTANCE.toOfferDTO(offerEntity)));
            } else {
              amount = new BigDecimal(0);
            }
          }
          Set<CompanyDTO> subContractors = submittent.getJointVentures();
          if (subContractors != null && !subContractors.isEmpty() && subContractors.size() == 1) {
            for (CompanyDTO subContractor : subContractors) {
              companyName.append(" (ARGE ");
              companyName.append(subContractor.getCompanyName());
              companyName.append(")");
            }
          } else if (subContractors != null && !subContractors.isEmpty()
            && subContractors.size() > 1) {
            companyName.append(" (ARGE ");
            List<String> allSubContractorsNames = new ArrayList<>();
            for (CompanyDTO subContractor : subContractors) {
              allSubContractorsNames.add(subContractor.getCompanyName());
            }
            companyName.append(StringUtils.join(allSubContractorsNames, " / "));
            companyName.append(")");
          }
          companiesNameList.add(companyName.toString());
          amountList.add(amount);
        }
      }
    }
    submissionDTO.setSubmittents(awardedSubmittentsList);
    submissionDTO.setReportCompanies(companiesNameList);
    submissionDTO.setReportAmount(amountList);

    // in case we have more than one awarded submittents, thus more than one offer
    // amount we calculate the sum of amounts
    BigDecimal addAmounts = new BigDecimal(0);
    if (amountList != null && !amountList.isEmpty()) {
      for (BigDecimal amount : amountList) {
        addAmounts = addAmounts.add(BigDecimal.valueOf(customRoundNumber(amount.doubleValue())));
      }
    }
    submissionDTO.setReportAmountTotal(addAmounts);
  }

  /**
   * Filter.
   *
   * @param reportDTO the report DTO
   * @return the list
   */
  private List<SubmissionEntity> filter(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method filter, Parameters: reportDTO: {0}",
      reportDTO);

    JPAQuery<SubmissionEntity> query = createQuery(reportDTO);
    return query.fetch();
  }

  /**
   * Gets the where clause.
   *
   * @param qSubmissionEntity the q submission entity
   * @param reportDTO the report DTO
   * @return the where clause
   */
  private BooleanBuilder getWhereClause(QSubmissionEntity qSubmissionEntity, ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getWhereClause, Parameters: "
        + "qSubmissionEntity: {0}, reportDTO: {1}",
      new Object[]{qSubmissionEntity, reportDTO});

    BooleanBuilder whereClause = new BooleanBuilder();

    security.securityCheckProjectSearch(getUser(), project, qSubmissionEntity, whereClause);

    List<String> completedSubmissionsIDs = new JPAQueryFactory(em)
      .select(qTenderStatusHistoryEntity.tenderId.id).from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.onDate
        .in(JPAExpressions.select(qTenderStatusHistoryEntity.onDate.max())
          .from(qTenderStatusHistoryEntity).groupBy(qTenderStatusHistoryEntity.tenderId))
        .and(qTenderStatusHistoryEntity.statusId.in(
          TenderStatus.AWARD_EVALUATION_CLOSED.getValue(),
          TenderStatus.MANUAL_AWARD_COMPLETED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
          TenderStatus.AWARD_NOTICES_CREATED.getValue(),
          TenderStatus.CONTRACT_CREATED.getValue(),
          TenderStatus.PROCEDURE_COMPLETED.getValue())))
      .fetch();
    whereClause.and(qSubmissionEntity.id.in(completedSubmissionsIDs));

    if (reportDTO.getStartDate() == null && reportDTO.getEndDate() == null) {
      List<String> availableSubmissionsIDs =
        new JPAQueryFactory(em).select(qSubmissionAwardInfoEntity.submission.id)
          .from(qSubmissionAwardInfoEntity).where(qSubmissionAwardInfoEntity.availableDate
          .isNotNull().and(qSubmissionAwardInfoEntity.level.in(0, 2)))
          .fetch();
      whereClause.and(qSubmissionEntity.id.in(availableSubmissionsIDs));
    }
    if (reportDTO.getStartDate() != null) {
      List<String> availableSubmissionsIDs = new JPAQueryFactory(em)
        .selectDistinct(qSubmissionAwardInfoEntity.submission.id).from(qSubmissionAwardInfoEntity)
        .where(qSubmissionAwardInfoEntity.availableDate.after(reportDTO.getStartDate())
          .or(qSubmissionAwardInfoEntity.availableDate.eq(reportDTO.getStartDate()))
          .and(qSubmissionAwardInfoEntity.availableDate.isNotNull())
          .and(qSubmissionAwardInfoEntity.level.in(0, 2)))
        .fetch();
      whereClause.and(qSubmissionEntity.id.in(availableSubmissionsIDs));
    }
    if (reportDTO.getEndDate() != null) {
      // setting hours-minutes-seconds till date end
      // in order to include all records from that day
      Calendar cal = Calendar.getInstance();
      cal.setTime(reportDTO.getEndDate());
      cal.set(Calendar.HOUR, 23);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.SECOND, 59);
      reportDTO.setEndDate(cal.getTime());
      List<String> availableSubmissionsIDs = new JPAQueryFactory(em)
        .select(qSubmissionAwardInfoEntity.submission.id).from(qSubmissionAwardInfoEntity)
        .where(qSubmissionAwardInfoEntity.availableDate.before(reportDTO.getEndDate())
          .or(qSubmissionAwardInfoEntity.availableDate.eq(reportDTO.getEndDate()))
          .and(qSubmissionAwardInfoEntity.availableDate.isNotNull())
          .and(qSubmissionAwardInfoEntity.level.in(0, 2)))
        .fetch();
      whereClause.and(qSubmissionEntity.id.in(availableSubmissionsIDs));
    }

    if (reportDTO.getObjects() != null && CollectionUtils.isNotEmpty(reportDTO.getObjects())) {
      List<String> objectHistoryMasterListValuesIDs =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.masterListValueId.id)
          .from(qMasterListValueHistoryEntity).where(qMasterListValueHistoryEntity.id
          .in(reportDTO.getObjects()).and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetch();

      List<String> objectsIDs = new JPAQueryFactory(em).select(objectName.id).from(objectName)
        .where(objectName.id.in(objectHistoryMasterListValuesIDs)).fetch();
      whereClause.and(qSubmissionEntity.project.objectName.id.in(objectsIDs));
    }
    if (checkIfNotEmpty(reportDTO.getProjects())) {
      whereClause.and(qSubmissionEntity.project.id.in(reportDTO.getProjects()));
    }
    if (checkIfNotEmpty(reportDTO.getProjectCreditNos())) {
      whereClause.and(qSubmissionEntity.project.projectNumber.in(reportDTO.getProjectCreditNos()));
    }
    if (CollectionUtils.isNotEmpty(reportDTO.getWorkTypes())) {
      List<String> workTypeHistoryIDs = new ArrayList<>();
      for (MasterListValueHistoryDTO workTypeDTO : reportDTO.getWorkTypes()) {
        workTypeHistoryIDs.add(workTypeDTO.getId());
      }
      List<String> workTypeHistoryEntitiesIDs =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.masterListValueId.id)
          .from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.id.in(workTypeHistoryIDs)).fetch();
      List<MasterListValueEntity> workTypeEntities =
        new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(qMasterListValueEntity.id.in(workTypeHistoryEntitiesIDs)).fetch();
      whereClause.and(qSubmissionEntity.workType.in(workTypeEntities));
    }
    if (CollectionUtils.isNotEmpty(reportDTO.getCompanies())) {
      List<String> companiesIDs = new ArrayList<>();
      for (CompanyDTO companyDTO : reportDTO.getCompanies()) {
        companiesIDs.add(companyDTO.getId());
      }
      List<String> submittentsIDList = new JPAQueryFactory(em)
        .select(qJointVentureEntity.submittent.id).from(qJointVentureEntity)
        .where(qJointVentureEntity.company.id.in(companiesIDs)).fetch();
      List<SubmissionEntity> companySubmissions =
        new JPAQueryFactory(em).select(qSubmittentEntity.submissionId).from(qSubmittentEntity)
          .where(qSubmittentEntity.companyId.id.in(companiesIDs)
            .or(qSubmittentEntity.id.in(submittentsIDList)))
          .orderBy(qSubmittentEntity.companyId.companyName.asc()).fetch();
      whereClause.and(qSubmissionEntity.in(companySubmissions));
    }
    if (checkIfNotEmpty(reportDTO.getProcedures())) {
      List<Process> procedures = new ArrayList<>();
      for (String procedure : reportDTO.getProcedures()) {
        procedures.add(getProcedure(procedure));
      }
      whereClause.and(qSubmissionEntity.process.in(procedures));
    }
    if (checkIfNotEmpty(reportDTO.getDirectorates())
      && !checkIfNotEmpty(reportDTO.getDepartments())) {
      //change departmenthistory Table

      List<String> directorateIds = new JPAQueryFactory(em)
        .select(qDirectorateHistoryEntity.directorateId.id).from(qDirectorateHistoryEntity)
        .where(qDirectorateHistoryEntity.id.in(reportDTO.getDirectorates()))
        .fetch();

      List<DepartmentEntity> departmentEntities =
        new JPAQueryFactory(em).select(qDepartmentHistoryEntity.departmentId)
          .from(qDepartmentHistoryEntity).where(qDepartmentHistoryEntity.directorateEnity.id
          .in(directorateIds).and(qDepartmentHistoryEntity.toDate.isNull()))
          .fetch();

      whereClause.and(qSubmissionEntity.project.department.in(departmentEntities));
    } else if (checkIfNotEmpty(reportDTO.getDepartments())) {
      List<DepartmentEntity> departmentEntities =
        new JPAQueryFactory(em).select(qDepartmentHistoryEntity.departmentId)
          .from(qDepartmentHistoryEntity).where(qDepartmentHistoryEntity.id
          .in(reportDTO.getDepartments()).and(qDepartmentHistoryEntity.toDate.isNull()))
          .fetch();
      whereClause.and(qSubmissionEntity.project.department.in(departmentEntities));
    }
    if (checkIfNotEmpty(reportDTO.getProjectManagers())) {
      whereClause.and(qSubmissionEntity.pmDepartmentName.in(reportDTO.getProjectManagers()));
    }
    return whereClause;
  }

  /**
   * Finds and returns the Process.
   *
   * @param procedure the procedure
   * @return the procedure
   */
  private Process getProcedure(String procedure) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProcedure, Parameters: procedure: {0}",
      procedure);

    Process process = null;
    switch (procedure) {
      case "SELECTIVE":
        process = Process.SELECTIVE;
        break;
      case "OPEN":
        process = Process.OPEN;
        break;
      case "INVITATION":
        process = Process.INVITATION;
        break;
      case "NEGOTIATED_PROCEDURE":
        process = Process.NEGOTIATED_PROCEDURE;
        break;
      case "NEGOTIATED_PROCEDURE_WITH_COMPETITION":
        process = Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION;
        break;
      default:
        break;
    }
    return process;
  }

  /**
   * Gets the report totalization by.
   *
   * @param totalizationBy the totalization by
   * @return the report totalization by
   */
  private String getReportTotalizationBy(String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportTotalizationBy, Parameters: totalizationBy: {0}",
      totalizationBy);

    String totalby = StringUtils.EMPTY;
    switch (totalizationBy) {
      case TOTAL_BY_PROCEDURE:
        totalby = "Verfahren";
        break;
      case TOTAL_BY_OBJECT:
        totalby = "Objekt";
        break;
      case TOTAL_BY_PROJECT:
        totalby = "Projekt";
        break;
      case TOTAL_BY_PROJECT_CREDIT_NO:
        totalby = "Projekt/ Kredit-Nummer";
        break;
      case TOTAL_BY_PROJECT_MANAGER:
        totalby = "Projektleiter der Abt";
        break;
      case TOTAL_BY_WORKTYPE:
        totalby = "Arbeitsgattung";
        break;
      case TOTAL_BY_DIRACTORATE:
        totalby = "Direktion";
        break;
      case TOTAL_BY_DEPARTMENT:
        totalby = "Abteilung";
        break;
      case TOTAL_BY_COMPANY:
        totalby = "Firma";
        break;
      case TOTAL_BY_YEAR:
        totalby = "Jahr";
        break;
      default:
        break;
    }
    return totalby;
  }

  /**
   * Check if not empty.
   *
   * @param list the list
   * @return true, if successful
   */
  private boolean checkIfNotEmpty(List<String> list) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkIfNotEmpty, Parameters: list: {0}",
      list);

    return list != null && CollectionUtils.isNotEmpty(list);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.ReportService#getReportSearchCriteria(ch.bern.
   * submiss.services.api.dto.ReportDTO)
   */
  @Override
  public Map<String, String> getReportSearchCriteria(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportSearchCriteria, Parameters: reportDTO: {0}",
      reportDTO);

    Map<String, String> searchCriteriaMap = new HashMap<>();
    if (checkIfNotEmpty(reportDTO.getObjects())) {
      List<MasterListValueHistoryEntity> searchedObjectsList = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.id.in(reportDTO.getObjects()))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();
      List<String> objectsNameList = new ArrayList<>();
      for (MasterListValueHistoryEntity object : searchedObjectsList) {
        StringBuilder name = new StringBuilder();
        name.append(object.getValue1());
        if (object.getValue2() != null) {
          name.append(" , ").append(object.getValue2());
        }
        objectsNameList.add(name.toString());
      }
      String searchedObjects = StringUtils.join(objectsNameList, " / ");
      searchCriteriaMap.put("searchedObjects", searchedObjects);
    }
    if (checkIfNotEmpty(reportDTO.getProjects())) {
      List<String> searchedProjectsList = new JPAQueryFactory(em).select(project.projectName)
        .from(project).where(project.id.in(reportDTO.getProjects()))
        .orderBy(project.projectName.asc()).fetch();
      String searchedProjects = StringUtils.join(searchedProjectsList, " / ");
      searchCriteriaMap.put("searchedProjects", searchedProjects);
    }
    if (checkIfNotEmpty(reportDTO.getProjectCreditNos())) {
      List<String> searchedProjectsList = new JPAQueryFactory(em).select(project.projectNumber)
        .from(project).where(project.projectNumber.in(reportDTO.getProjectCreditNos()))
        .orderBy(project.projectNumber.asc()).fetch();
      String searchedProjectNumbers = StringUtils.join(searchedProjectsList, " / ");
      searchCriteriaMap.put("searchedProjectNumbers", searchedProjectNumbers);
    }
    if (CollectionUtils.isNotEmpty(reportDTO.getWorkTypes())) {
      List<String> workTypeList = new ArrayList<>();
      for (MasterListValueHistoryDTO workTypeDTO : reportDTO.getWorkTypes()) {
        workTypeList.add(getWorkTypeName(workTypeDTO));
      }
      String searchedWorkedTypes = StringUtils.join(workTypeList, " / ");
      searchCriteriaMap.put("searchedWorkedTypes", searchedWorkedTypes);
    }
    if (checkIfNotEmpty(reportDTO.getProcedures())) {
      List<String> searchedProceduresList = new ArrayList<>();
      for (String procedure : reportDTO.getProcedures()) {
        searchedProceduresList.add(getProcedureName(procedure));
      }
      String searchedProcedures = StringUtils.join(searchedProceduresList, " / ");
      searchCriteriaMap.put("searchedProcedures", searchedProcedures);
    }
    if (CollectionUtils.isNotEmpty(reportDTO.getCompanies())) {
      List<String> companiesList = new ArrayList<>();
      for (CompanyDTO companyDTO : reportDTO.getCompanies()) {
        companiesList.add(companyDTO.getCompanyName());
      }
      String searchedCompanies = StringUtils.join(companiesList, " / ");
      searchCriteriaMap.put("searchedCompanies", searchedCompanies);
    }
    if (checkIfNotEmpty(reportDTO.getDirectorates())) {
      List<String> searchedDirectoratesList = new JPAQueryFactory(em)
        .select(qDirectorateHistoryEntity.name).from(qDirectorateHistoryEntity)
        .where(qDirectorateHistoryEntity.id.in(reportDTO.getDirectorates())).fetch();
      String searchedDirectorates = StringUtils.join(searchedDirectoratesList, " / ");
      searchCriteriaMap.put("searchedDirectorates", searchedDirectorates);
    }
    if (checkIfNotEmpty(reportDTO.getDepartments())) {
      List<String> searchedDepartmentsList = new JPAQueryFactory(em)
        .select(qDepartmentHistoryEntity.name).from(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.id.in(reportDTO.getDepartments())).fetch();
      String searchedDepartments = StringUtils.join(searchedDepartmentsList, " / ");
      searchCriteriaMap.put("searchedDepartments", searchedDepartments);
    }
    if (checkIfNotEmpty(reportDTO.getProjectManagers())) {
      List<String> searchedPMDepartmentList =
        new JPAQueryFactory(em).select(project.pmDepartmentName).from(project)
          .where(project.pmDepartmentName.in(reportDTO.getProjectManagers()))
          .orderBy(project.pmDepartmentName.asc()).fetch();
      // Get unique names
      List<String> newList = new ArrayList<>(new LinkedHashSet<String>(searchedPMDepartmentList));
      String searchedPMDepartment = StringUtils.join(newList, " / ");
      searchCriteriaMap.put("searchedPMDepartment", searchedPMDepartment);
    }
    return searchCriteriaMap;
  }

  /**
   * Gets the work type name.
   *
   * @param workTypeDTO the work type DTO
   * @return the work type name
   */
  private String getWorkTypeName(MasterListValueHistoryDTO workTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getWorkTypeName, Parameters: workTypeDTO: {0}",
      workTypeDTO);

    StringBuilder workTypeName = new StringBuilder();
    workTypeName.append(workTypeDTO.getValue1());
    if (workTypeDTO.getValue2() != null && !StringUtils.isEmpty(workTypeDTO.getValue2())) {
      workTypeName.append(" " + workTypeDTO.getValue2());
    }
    return workTypeName.toString();
  }

  /**
   * Finds and returns the Process Name in German.
   *
   * @param procedure the procedure
   * @return the procedure name
   */
  private String getProcedureName(String procedure) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProcedureName, Parameters: procedure: {0}",
      procedure);

    String processName = StringUtils.EMPTY;
    switch (procedure) {
      case "SELECTIVE":
        processName = "Selektiv";
        break;
      case "OPEN":
        processName = "Offen";
        break;
      case "INVITATION":
        processName = "Einladung";
        break;
      case "NEGOTIATED_PROCEDURE":
        processName = "Freihändig";
        break;
      case "NEGOTIATED_PROCEDURE_WITH_COMPETITION":
        processName = "Freihändig mit Konkurrenz";
        break;
      default:
        break;
    }
    return processName;
  }

  /**
   * Function to implement the swiss number rounding standard ("Rappenrundung").
   *
   * @param num the num
   * @return the double
   */
  public double customRoundNumber(Double num) {

    LOGGER.log(Level.CONFIG,
      "Executing method customRoundNumber, Parameters: num: {0}",
      num);

    if (num == null || num == 0) {
      return 0.00;
    }
    double tenNum = num * 10;
    double numWithOneDecimal = Double.valueOf((long) tenNum) / 10;
    double diff = num - numWithOneDecimal;
    if (diff < 0.025) {
      return numWithOneDecimal;
    } else if (diff >= 0.025 && diff < 0.075) {
      return numWithOneDecimal + 0.05;
    } else {
      return numWithOneDecimal + 0.1;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.ReportService#getNumberOfResultsByReport(ch.bern.
   * submiss.services.api.dto.ReportDTO)
   */
  @Override
  public Long getNumberOfResultsByReport(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNumberOfResultsByReport, Parameters: reportDTO: {0}",
      reportDTO);

    reportSecurityCheck();

    JPAQuery<SubmissionEntity> query = createQuery(reportDTO);
    return query.fetchCount();
  }

  /**
   * Creates the query.
   *
   * @param reportDTO the report DTO
   * @return the JPA query
   */
  private JPAQuery<SubmissionEntity> createQuery(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG, "Executing method createQuery, Parameters: reportDTO: {0}",
      reportDTO);

    JPAQuery<SubmissionEntity> query = new JPAQuery<>(em);
    query.select(submission).from(submission).rightJoin(submission.project, project)
      .leftJoin(submission.project.objectName, objectName)
      .leftJoin(objectName.masterListValueHistory, objectNameHistory)
      .on(objectNameHistory.toDate.isNull()).leftJoin(submission.workType, workType)
      .leftJoin(workType.masterListValueHistory, workTypeHistory)
      .on(workTypeHistory.toDate.isNull())
      .leftJoin(submission.project.department, qDepartmentEntity)
      .leftJoin(qDepartmentEntity.department, qDepartmentHistoryEntity)
      .leftJoin(qDepartmentHistoryEntity.directorateEnity.directorate, qDirectorateHistoryEntity)
      .where(qDepartmentHistoryEntity.toDate.isNull(), qDirectorateHistoryEntity.toDate.isNull(),
        getWhereClause(submission, reportDTO));

    /** Apply ordering according to totalization **/
    String totalizationBy = reportDTO.getTotalizationBy();
    if (totalizationBy.equals(TOTAL_BY_OBJECT)) {
      query.orderBy(objectNameHistory.value1.asc(), submission.process.asc());
    } else if (totalizationBy.equals(TOTAL_BY_PROJECT)) {
      query.orderBy(submission.project.projectName.asc(), submission.process.asc());
    } else if (totalizationBy.equals(TOTAL_BY_PROJECT_CREDIT_NO)) {
      query.orderBy(submission.project.projectNumber.asc(), submission.process.asc());
    } else if (totalizationBy.equals(TOTAL_BY_PROJECT_MANAGER)) {
      query.orderBy(submission.project.pmDepartmentName.asc());
    } else if (totalizationBy.equals(TOTAL_BY_WORKTYPE)) {
      query.orderBy(workTypeHistory.value1.asc());
    } else if (totalizationBy.equals(TOTAL_BY_DIRACTORATE)) {
      query.orderBy(qDirectorateHistoryEntity.shortName.asc());
    } else if (totalizationBy.equals(TOTAL_BY_DEPARTMENT)) {
      query.orderBy(qDepartmentHistoryEntity.name.asc());
    } else if (totalizationBy.equals(TOTAL_BY_PROCEDURE)) {
      query.orderBy(submission.process.asc(), qDirectorateHistoryEntity.shortName.asc());
    }
    return query;
  }

  /**
   * The Enum ReportFormat.
   */
  private enum ReportFormat {

    /**
     * The xlsx.
     */
    XLSX,
    /**
     * The docx.
     */
    DOCX,
    /**
     * The pdf.
     */
    PDF
  }

  @Override
  public void reportSecurityCheck() {

    LOGGER.log(Level.CONFIG, "Executing method reportSecurityCheck");

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.GENERATE_REPORT.getValue(), null);
  }
}
