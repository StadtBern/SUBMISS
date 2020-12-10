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
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.constants.DocumentProperties;
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
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.CustomSubmissionMapper;
import ch.bern.submiss.services.impl.mappers.OfferDTOMapper;
import ch.bern.submiss.services.impl.model.DepartmentEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class ReportServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {ReportService.class})
@Singleton
public class ReportServiceImpl extends ReportBaseServiceImpl implements ReportService {

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
  private static final String TOTAL_BY_DIRECTORATE = "totalByDirectorate";
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
   * The Constant FONT_SIZE_9.
   */
  private static final int FONT_SIZE_9 = 9;
  /**
   * The Constant FONT_SIZE_12.
   */
  private static final int FONT_SIZE_12 = 12;
  /**
   * The Constant FONT_SIZE_14.
   */
  private static final int FONT_SIZE_14 = 14;
  /**
   * The Constant CHARACTER_WIDTH.
   */
  private static final int CHARACTER_WIDTH = 256;
  /**
   * The Constant REPORT_TITLE.
   */
  private static final String REPORT_TITLE = "Auswertung vergebener Aufträge";
  /**
   * The Constant REPORT_SUB_TITLE.
   */
  private static final String REPORT_SUB_TITLE = "Auswertungskriterien";

  /**
   * The cache bean.
   */
  @Inject
  protected CacheBean cacheBean;
  /**
   * The Sd service.
   */
  @Inject
  private SDService sDService;

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

  public byte[] generateReport(ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateReport, Parameters: reportResults: {0}",
      reportResults);

    reportSecurityCheck();

    byte[] report = null;
    try {
      report = createReportExcel(reportResults);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return report;
  }

  /**
   * Creates the Report using Apache POI.
   *
   * @param reportResults the reportResults
   * @return the byte[]
   */
  private byte[] createReportExcel(ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method createReportExcel, Parameters: reportResults: {0}",
      reportResults);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      // The createSheet method doesn't allow sheet names to contain /, so we replace it with ""
      XSSFSheet sheet = workbook
        .createSheet(getReportTotalizationBy(reportResults.getTotalizationBy()).replace("/", ""));

      setColumnWidth(sheet, reportResults.getTotalizationBy());

      int currentRowNum = setDocumentTitle(sheet, workbook);
      currentRowNum = setDocumentSearchCriteria(sheet, workbook, currentRowNum, reportResults);

      insertDocumentData(sheet, workbook, currentRowNum, reportResults);

      setReportPrintSettings(sheet, reportResults);

      workbook.write(bos);
      bos.close();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return bos.toByteArray();
  }

  /**
   * Set the Report title.
   *
   * @param sheet the sheet
   * @param workbook the workbook
   * @return the current row index
   */
  private int setDocumentTitle(XSSFSheet sheet, XSSFWorkbook workbook) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentTitle, Parameters: sheet: {0}, workbook: {1}",
      new Object[]{sheet, workbook});

    // The starting row
    int startingIndex = 0;

    // Document title font
    XSSFFont titleFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(), FONT_SIZE_14, true);

    // Document title style
    CellStyle titleStyle = setCellStyle(workbook, titleFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, false, null);

    // The document title
    Row row = sheet.createRow(startingIndex);
    Cell cell = row.createCell(0);
    cell.setCellValue(REPORT_TITLE);
    cell.setCellStyle(titleStyle);

    return startingIndex + 2;
  }

  /**
   * Set the search criteria.
   *
   * @param sheet the sheet
   * @param workbook the workbook
   * @param startingIndex the starting index
   * @param reportResults the reportResults
   * @return the current row index
   */
  private int setDocumentSearchCriteria(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentSearchCriteria, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    // Document subTitle font
    XSSFFont subTitleFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_12, true);
    // Document searchCriteriaTitle font
    XSSFFont searchCriteriaTitleFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, true);
    // Document searchCriteriaValue font
    XSSFFont searchCriteriaValueFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, false);

    // Document subTitle style
    CellStyle subTitleStyle = setCellStyle(workbook, subTitleFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, false, null);
    // Document searchCriteriaTitle style
    CellStyle searchCriteriaTitleStyle = setCellStyle(workbook, searchCriteriaTitleFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);
    // Document searchCriteriaValue style
    CellStyle searchCriteriaValueStyle = setCellStyle(workbook, searchCriteriaValueFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);
    // Document searchCriteriaValue style 2
    CellStyle searchCriteriaValueStyle2 = setCellStyle(workbook, searchCriteriaValueFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, false, null);

    // Add the document subTitle
    Row row = sheet.createRow(startingIndex);
    Cell cell = row.createCell(0);
    cell.setCellValue(REPORT_SUB_TITLE);
    cell.setCellStyle(subTitleStyle);

    // Add the searched criteria
    AtomicInteger currentIndex = new AtomicInteger(startingIndex);
    Map<String, String> searchCriteriaTitleMap = getSearchCriteriaTitles();

    reportResults.getSearchedCriteria().forEach((k, v) -> {
      currentIndex.getAndIncrement();
      Row searchCriteriaRow = sheet.createRow(currentIndex.intValue());

      Cell searchCriteriaCell = searchCriteriaRow.createCell(0);
      searchCriteriaCell.setCellValue(searchCriteriaTitleMap.getOrDefault(k, StringUtils.EMPTY));
      searchCriteriaCell.setCellStyle(searchCriteriaTitleStyle);

      searchCriteriaCell = searchCriteriaRow.createCell(1);
      searchCriteriaCell.setCellValue(v);
      searchCriteriaCell.setCellStyle((k.equals("searchedDate"))
        ? searchCriteriaValueStyle2 : searchCriteriaValueStyle);
    });

    // Add totalization by
    currentIndex.getAndIncrement();
    row = sheet.createRow(currentIndex.intValue());

    cell = row.createCell(0);
    // For total by object show only Totalisierung, for all other reports show Totalisierung nach:
    cell.setCellValue((reportResults.getTotalizationBy().equals(TOTAL_BY_OBJECT)) ? "Totalisierung"
      : "Totalisierung nach:");
    cell.setCellStyle(searchCriteriaTitleStyle);

    cell = row.createCell(1);
    cell.setCellValue(getReportTotalizationBy(reportResults.getTotalizationBy()));
    cell.setCellStyle(searchCriteriaValueStyle);

    return currentIndex.intValue() + 2;
  }

  /**
   * Insert the document data.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   */
  private void insertDocumentData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertDocumentData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    int currentIndex = startingIndex;
    switch (reportResults.getTotalizationBy()) {
      case TOTAL_BY_PROCEDURE:
        currentIndex = addProcedureData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_OBJECT:
        currentIndex = addObjectData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_PROJECT:
        currentIndex = addProjectData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_PROJECT_CREDIT_NO:
        currentIndex = addProjectNumberData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_PROJECT_MANAGER:
        currentIndex = addProjectManagerData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_WORKTYPE:
        currentIndex = addWorkTypeData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_DIRECTORATE:
        currentIndex = addDirectorateData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_DEPARTMENT:
        currentIndex = addDepartmentData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_COMPANY:
        reportResults.setSubmissions(getReportCompanyResult(reportResults));
        currentIndex = addCompanyData(sheet, workbook, startingIndex, reportResults);
        break;
      case TOTAL_BY_YEAR:
        currentIndex = addYearData(sheet, workbook, startingIndex, reportResults);
        break;
      default:
        break;
    }

    if (!reportResults.getTotalizationBy().equals(TOTAL_BY_COMPANY)) {
      addSumUp(sheet, workbook, currentIndex, reportResults);
    }
  }

  /**
   * Set the report print settings.
   *
   * @param sheet         the sheet
   * @param reportResults the reportResults
   */
  private void setReportPrintSettings(XSSFSheet sheet, ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method setReportPrintSettings, Parameters: sheet: {0}, reportResults: {1}",
      new Object[]{sheet, reportResults});

    PrintSetup ps = sheet.getPrintSetup();
    ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
    ps.setFitWidth((short) 1);
    ps.setFitHeight((short) 0);
    // Only total by year has portrait orientation
    ps.setLandscape(!reportResults.getTotalizationBy().equals(TOTAL_BY_YEAR));
    short pageScale;
    if (reportResults.getTotalizationBy().equals(TOTAL_BY_YEAR)) {
      pageScale = 96;
    } else if (reportResults.getTotalizationBy().equals(TOTAL_BY_OBJECT) ||
      reportResults.getTotalizationBy().equals(TOTAL_BY_PROJECT)) {
      pageScale = 52;
    } else {
      pageScale = 50;
    }
    ps.setScale(pageScale);

    // Add margins
    sheet.setMargin(Sheet.TopMargin, 0.472);
    sheet.setMargin(Sheet.BottomMargin, 0.669);
    sheet.setMargin(Sheet.LeftMargin, 0.511);
    sheet.setMargin(Sheet.RightMargin, 0.314);
    sheet.setMargin(Sheet.HeaderMargin, 0.314);
    sheet.setMargin(Sheet.FooterMargin, 0.314);

    addHeaderFooter(sheet, reportResults);
  }

  /**
   * Add header and footer for printing.
   *
   * @param sheet         the sheet
   * @param reportResults the reportResults
   */
  private void addHeaderFooter(XSSFSheet sheet, ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addHeaderFooter, Parameters: sheet: {0}, reportResults: {1}",
      new Object[]{sheet, reportResults});

    // Add header for printing.
    // As we do not want the header on the first page, we add an empty first page header.
    Header firstHeader = sheet.getFirstHeader();
    firstHeader.setLeft(StringUtils.EMPTY);

    // Then we add the header with its header text.
    String headerText = HSSFHeader.font("Arial", "Bold") +
      HSSFHeader.fontSize((short) 9) + "Totalisierung nach " + HSSFHeader
      .font("Arial", StringUtils.EMPTY) +
      HSSFHeader.fontSize((short) 9) + getReportTotalizationBy(reportResults.getTotalizationBy());

    Header header = sheet.getHeader();
    header.setLeft(headerText);

    // Add footer for printing
    Footer firstFooter = sheet.getFirstFooter();
    Footer footer = sheet.getFooter();

    SimpleDateFormat dateFormat = new SimpleDateFormat(LookupValues.DATE_FORMAT);
    String startDate = reportResults.getStartDate() != null
      ? dateFormat.format(reportResults.getStartDate())
      : "-";
    String endDate = reportResults.getEndDate() != null
      ? dateFormat.format(reportResults.getEndDate())
      : "-";

    String footerStyle = HSSFFooter.font("Arial", StringUtils.EMPTY) + HSSFFooter.fontSize((short) 8);

    String leftText = footerStyle + "Auswertungen von " + startDate + " bis " + endDate;
    String centerText = footerStyle + HSSFFooter.date();
    String rightText = footerStyle + "Seite " + HeaderFooter.page() + "/" + HeaderFooter.numPages();

    firstFooter.setLeft(leftText);
    footer.setLeft(leftText);

    firstFooter.setCenter(centerText);
    footer.setCenter(centerText);

    firstFooter.setRight(rightText);
    footer.setRight(rightText);
  }

  /**
   * Add the document's sum up.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   */
  private void addSumUp(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addFooter, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    // Document countSectionResultsTitle font
    XSSFFont footerTitleFont = setFontStyle(workbook,
      DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, true);

    // Document countSectionResultsValue font
    XSSFFont footerValueFont = setFontStyle(workbook,
      DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, false);

    // Document footerTitle style with HorizontalAlignment -> LEFT
    CellStyle footerTitleStyle = setCellStyle(workbook, footerTitleFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document footerTitle2 style with HorizontalAlignment -> RIGHT
    CellStyle footerTitleStyle2 = setCellStyle(workbook, footerTitleFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, null);

    // Document footerTitle style with HorizontalAlignment -> LEFT (for the year footer)
    CellStyle footerTitleStyle3 = setCellStyle(workbook, footerTitleFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document footerValue style with HorizontalAlignment -> LEFT
    CellStyle footerValueStyle = setCellStyle(workbook, footerValueFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document footerValue2 style with HorizontalAlignment -> RIGHT and no data format
    CellStyle footerValueStyle2 = setCellStyle(workbook, footerValueFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, null);

    // Document footerValue3 style with HorizontalAlignment -> RIGHT and data format
    CellStyle footerValueStyle3 = setCellStyle(workbook, footerValueFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, LookupValues.NUMBER_FORMAT);

    if (reportResults.getTotalizationBy().equals(TOTAL_BY_YEAR)) {
      insertYearSumUp(sheet, startingIndex, reportResults, footerTitleStyle, footerTitleStyle3, footerValueStyle2,
        footerValueStyle3);
    } else {
      insertReportSumUp(sheet, startingIndex, reportResults, footerTitleStyle, footerTitleStyle2,
        footerValueStyle, footerValueStyle3);
    }
  }

  /**
   * Insert the sum up on the report.
   *
   * @param sheet             the sheet
   * @param startingIndex     the startingIndex
   * @param reportResults     the reportResults
   * @param footerTitleStyle  the footerTitleStyle
   * @param footerTitleStyle2 the footerTitleStyle2
   * @param footerValueStyle  the footerValueStyle
   * @param footerValueStyle3 the footerValueStyle3
   */
  private void insertReportSumUp(XSSFSheet sheet, int startingIndex,
    ReportResultsDTO reportResults, CellStyle footerTitleStyle, CellStyle footerTitleStyle2,
    CellStyle footerValueStyle, CellStyle footerValueStyle3) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertReportFooter, Parameters: sheet: {0}, startingIndex: {1}, "
        + "reportResults: {2}, footerTitleStyle: {3}, footerTitleStyle2: {4}, footerValueStyle: {5}, "
        + "footerValueStyle3: {6}",
      new Object[]{sheet, startingIndex, reportResults, footerTitleStyle, footerTitleStyle2,
        footerValueStyle, footerValueStyle3});

    // Creating a row for counting the results
    Row row = sheet.createRow(startingIndex);

    // Create 10 cells with top and bottom borders for the footer
    for (int i = 0; i < 10; i++) {
      Cell cell = row.createCell(i);
      // Add the borders
      footerValueStyle.setBorderTop(BorderStyle.THIN);
      footerValueStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
      footerValueStyle.setBorderBottom(BorderStyle.THIN);
      footerValueStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
      cell.setCellStyle(footerValueStyle);
    }
    // Add the countSectionResultsTitle on the first cell
    Cell cell = row.getCell(0);
    cell.setCellValue("Verfahren");
    // Add left, top and bottom borders
    footerTitleStyle.setBorderTop(BorderStyle.THIN);
    footerTitleStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
    footerTitleStyle.setBorderBottom(BorderStyle.THIN);
    footerTitleStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    footerTitleStyle.setBorderLeft(BorderStyle.THIN);
    footerTitleStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    cell.setCellStyle(footerTitleStyle);

    // Add the countSectionResultsValue on the second cell
    cell = row.getCell(1);
    cell.setCellValue(reportResults.getSubmissions().size());
    cell.setCellStyle(footerValueStyle);

    // Add the countSectionResultsTitle on the 9th cell
    cell = row.getCell(8);
    cell.setCellValue(LookupValues.GESAMTTOTAL);
    // Add top and bottom borders
    footerTitleStyle2.setBorderTop(BorderStyle.THIN);
    footerTitleStyle2.setTopBorderColor(IndexedColors.BLACK.getIndex());
    footerTitleStyle2.setBorderBottom(BorderStyle.THIN);
    footerTitleStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    cell.setCellStyle(footerTitleStyle2);

    // Add the countSectionResultsValue on the 10th cell
    cell = row.getCell(9);
    cell.setCellValue(getReportTotalAmount(reportResults.getSubmissions()).doubleValue());
    // Add top, bottom and right borders for the last cell
    footerValueStyle3.setBorderTop(BorderStyle.THIN);
    footerValueStyle3.setTopBorderColor(IndexedColors.BLACK.getIndex());
    footerValueStyle3.setBorderBottom(BorderStyle.THIN);
    footerValueStyle3.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    footerValueStyle3.setBorderRight(BorderStyle.THIN);
    footerValueStyle3.setRightBorderColor(IndexedColors.BLACK.getIndex());
    cell.setCellStyle(footerValueStyle3);
  }

  /**
   * Insert the sum up on the year report.
   *
   * @param sheet             the sheet
   * @param startingIndex     the startingIndex
   * @param reportResults     the reportResults
   * @param footerTitleStyle  the footerTitleStyle
   * @param footerValueStyle2 the footerValueStyle2
   * @param footerValueStyle3 the footerValueStyle3
   */
  private void insertYearSumUp(XSSFSheet sheet, int startingIndex, ReportResultsDTO reportResults,
    CellStyle footerTitleStyle, CellStyle footerTitleStyle2, CellStyle footerValueStyle2, CellStyle footerValueStyle3) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertYearFooter, Parameters: sheet: {0}, startingIndex: {1}, "
        + "reportResults: {2}, footerTitleStyle: {3}, footerValueStyle2: {4}, footerValueStyle3: {5}",
      new Object[]{sheet, startingIndex, reportResults, footerTitleStyle, footerValueStyle2,
        footerValueStyle3});

    // Creating a row for counting the amounts in year report
    Row row = sheet.createRow(startingIndex);

    Cell cell = row.createCell(0);
    cell.setCellValue(LookupValues.GESAMTTOTAL);
    // Add top and left borders in the first cell of the first row
    footerTitleStyle.setBorderTop(BorderStyle.THIN);
    footerTitleStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
    footerTitleStyle.setBorderLeft(BorderStyle.THIN);
    footerTitleStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    cell.setCellStyle(footerTitleStyle);

    cell = row.createCell(1);
    cell.setCellValue(getReportTotalAmount(reportResults.getSubmissions()).doubleValue());
    // Add top and right borders in the second cell of the first row
    footerValueStyle3.setBorderTop(BorderStyle.THIN);
    footerValueStyle3.setTopBorderColor(IndexedColors.BLACK.getIndex());
    footerValueStyle3.setBorderRight(BorderStyle.THIN);
    footerValueStyle3.setRightBorderColor(IndexedColors.BLACK.getIndex());
    cell.setCellStyle(footerValueStyle3);

    // Create a second row for counting the results in year report
    startingIndex++;
    row = sheet.createRow(startingIndex);

    cell = row.createCell(0);
    cell.setCellValue(LookupValues.TOTAL_VERFAHREN);
    // We have to use a new footer title style because there is an issue when removing borders
    // and using the same style again.
    // Add left and bottom borders in the second cell of the first row
    footerTitleStyle2.setBorderBottom(BorderStyle.THIN);
    footerTitleStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    footerTitleStyle2.setBorderLeft(BorderStyle.THIN);
    footerTitleStyle2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    cell.setCellStyle(footerTitleStyle2);

    cell = row.createCell(1);
    cell.setCellValue(reportResults.getSubmissions().size());
    // Add bottom and right borders in the second cell of the second row
    footerValueStyle2.setBorderBottom(BorderStyle.THIN);
    footerValueStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    footerValueStyle2.setBorderRight(BorderStyle.THIN);
    footerValueStyle2.setRightBorderColor(IndexedColors.BLACK.getIndex());
    cell.setCellStyle(footerValueStyle2);
  }

  /**
   * Add the year data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addYearData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addYearData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, Map<String, List<SubmissionDTO>>> resultsMappedByYearAndProcess =
      getResultsMappedByYearAndProcess(reportResults.getSubmissions());

    return addData(sheet, workbook, currentIndex, resultsMappedByYearAndProcess);
  }

  /**
   * Add the company data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addCompanyData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCompanyData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByCompany =
      getResultsMappedByCompany(reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByCompany);
  }

  /**
   * Add the department data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addDepartmentData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addDepartmentData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByDepartment =
      getResultsMappedByDepartment(reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByDepartment);
  }

  /**
   * Add the directorate data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addDirectorateData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addDirectorateData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByDirectorate =
      getResultsMappedByDirectorate(reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByDirectorate);
  }

  /**
   * Add the work type data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addWorkTypeData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addWorkTypeData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByWorkType =
      getResultsMappedByWorkType(reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByWorkType);
  }

  /**
   * Add the project manager data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addProjectManagerData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addProjectManagerData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByProjectManager =
      getResultsMappedByProjectManager(reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByProjectManager);
  }

  /**
   * Add the project number data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addProjectNumberData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addProjectNumberData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByProjectNumber = getResultsMappedByProjectNumber(
      reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByProjectNumber);
  }

  /**
   * Add the project data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addProjectData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addProjectData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByProject = getResultsMappedByProject(
      reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByProject);
  }

  /**
   * Add the object data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addObjectData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addObjectData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByObject = getResultsMappedByObject(
      reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByObject);
  }

  /**
   * Add the procedure data to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param reportResults the reportResults
   * @return the current index
   */
  private int addProcedureData(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    ReportResultsDTO reportResults) {

    LOGGER.log(Level.CONFIG,
      "Executing method addProcedureData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, reportResults: {3}",
      new Object[]{sheet, workbook, startingIndex, reportResults});

    AtomicInteger currentIndex = new AtomicInteger(startingIndex);

    // Get the mapped results
    Map<String, List<SubmissionDTO>> resultsMappedByProcedure = getResultsMappedByProcedure(
      reportResults.getSubmissions());

    return addData(sheet, workbook, reportResults.getTotalizationBy(), currentIndex, resultsMappedByProcedure);
  }

  /**
   * Adding the data to the document.
   *
   * @param sheet                       the sheet
   * @param workbook                    the workbook
   * @param totalizationBy              the totalizationBy
   * @param currentIndex                the currentIndex
   * @param resultsMappedByTotalization the resultsMappedByTotalization
   * @return the current index
   */
  private int addData(XSSFSheet sheet, XSSFWorkbook workbook, String totalizationBy,
    AtomicInteger currentIndex, Map<String, List<SubmissionDTO>> resultsMappedByTotalization) {

    LOGGER.log(Level.CONFIG,
      "Executing method addData, Parameters: sheet: {0}, workbook: {1}, "
        + "totalizationBy: {2}, currentIndex: {3}, resultsMappedByTotalization: {4}",
      new Object[]{sheet, workbook, totalizationBy, currentIndex, resultsMappedByTotalization});

    // Get the column headers for specific totalization
    List<String> columnHeaders = getColumnHeaders(totalizationBy);

    // Iterate through results to add data in sections.
    // Each section in the document contains the results of each procedure.
    resultsMappedByTotalization.forEach((k, v) -> {
      // First adding the title of each section (eg. the process name, the object name, the project name)
      addSectionTitle(sheet, workbook, currentIndex.getAndIncrement(), k);
      // Then adding the column headers
      addColumnHeaders(sheet, workbook, currentIndex.getAndIncrement(), columnHeaders);
      // Adding the results
      currentIndex.set(
        addSectionResults(sheet, workbook, currentIndex.get(), v, columnHeaders));
      // Finally adding the count of total results for each section (the section's footer)
      addCountSectionResults(sheet, workbook, currentIndex.getAndAdd(2), v, columnHeaders.size(),
        totalizationBy);
    });

    return currentIndex.get();
  }

  /**
   * Adding the year data to the document.
   *
   * @param sheet                       the sheet
   * @param workbook                    the workbook
   * @param currentIndex                the currentIndex
   * @param resultsMappedByYearAndProcess the resultsMappedByYearAndProcess
   * @return the current index
   */
  private int addData(XSSFSheet sheet, XSSFWorkbook workbook,
    AtomicInteger currentIndex, Map<String, Map<String, List<SubmissionDTO>>> resultsMappedByYearAndProcess) {

    LOGGER.log(Level.CONFIG,
      "Executing method addData, Parameters: sheet: {0}, workbook: {1}, "
        + "totalizationBy: {2}, currentIndex: {3}, resultsMappedByYearAndProcess: {4}",
      new Object[]{sheet, workbook, currentIndex, resultsMappedByYearAndProcess});


    // Iterate through results to add data in sections.
    // Each section in the document contains the results of each year.
    resultsMappedByYearAndProcess.forEach((k, v) -> {
      // First adding the column headers
      addColumnHeaders(sheet, workbook, currentIndex.getAndIncrement(), k);
      // Then adding the results
      currentIndex.set(
        addSectionResults(sheet, workbook, currentIndex.get(), v));
      // Finally adding the count of total results for each section (the section's footer)
      addCountSectionResults(sheet, workbook, currentIndex.getAndAdd(3), v);
    });

    return currentIndex.get();
  }

  /**
   * Add the count section results for the year report.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param results  the section results
   */
  private void addCountSectionResults(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    Map<String, List<SubmissionDTO>> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCountSectionResults, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, results: {3}",
      new Object[]{sheet, workbook, startingIndex, results});

    // Document countSectionResultsTitle font
    XSSFFont countSectionResultsFont = setFontStyle(workbook,
      DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, false);

    // Document countSectionResultsStyle style
    CellStyle countSectionResultsStyle = setCellStyle(workbook, countSectionResultsFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document countSectionResultsStyle2 style with HorizontalAlignment -> RIGHT and data format
    CellStyle countSectionResultsStyle2 = setCellStyle(workbook, countSectionResultsFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, LookupValues.NUMBER_FORMAT);

    // Document countSectionResultsStyle3 style with HorizontalAlignment -> RIGHT and no data format
    CellStyle countSectionResultsStyle3 = setCellStyle(workbook, countSectionResultsFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, null);

    // Creating a row for counting the amounts
    Row row = sheet.createRow(startingIndex++);

    Cell cell = row.createCell(0);
    cell.setCellValue("Total Nettobetrag inkl. MWST");
    cell.setCellStyle(countSectionResultsStyle);

    cell = row.createCell(1);
    cell.setCellValue(getCountAmounts(results).doubleValue());
    cell.setCellStyle(countSectionResultsStyle2);

    // Creating a row for counting the number of results
    row = sheet.createRow(startingIndex);

    cell = row.createCell(0);
    cell.setCellValue(LookupValues.TOTAL_VERFAHREN);
    cell.setCellStyle(countSectionResultsStyle);

    cell = row.createCell(1);
    cell.setCellValue(getCountResults(results));
    cell.setCellStyle(countSectionResultsStyle3);
  }

  /**
   * Get the count amounts for the year report.
   *
   * @param results the results
   * @return the count
   */
  private BigDecimal getCountAmounts(Map<String, List<SubmissionDTO>> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCountAmounts, Parameters: results: {0}",
      results);

    AtomicReference<BigDecimal> countAmounts = new AtomicReference<>(new BigDecimal(0));

    results.forEach((k, v) -> countAmounts.accumulateAndGet(getReportTotalAmount(v), BigDecimal::add));

    return countAmounts.get();
  }

  /**
   * Get the count results for the year report.
   *
   * @param results the results
   * @return the count
   */
  private int getCountResults(Map<String, List<SubmissionDTO>> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCountResults, Parameters: results: {0}",
      results);

    AtomicInteger countResults = new AtomicInteger();

    results.forEach((k, v) -> countResults.addAndGet(v.size()));

    return countResults.get();
  }

  /**
   * Add the count section results.
   *
   * @param sheet          the sheet
   * @param workbook       the workbook
   * @param startingIndex  the startingIndex
   * @param columnNumbers  the columnNumbers
   * @param results        the results
   * @param totalizationBy the totalizationBy
   */
  private void addCountSectionResults(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    List<SubmissionDTO> results, int columnNumbers, String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCountSectionResults, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, results: {3}, columnNumbers: {4}, totalizationBy: {5}",
      new Object[]{sheet, workbook, startingIndex, results, columnNumbers, totalizationBy});

    // Document countSectionResultsTitle font
    XSSFFont countSectionResultsTitleFont = setFontStyle(workbook,
      DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, true);

    // Document countSectionResultsValue font
    XSSFFont countSectionResultsValueFont = setFontStyle(workbook,
      DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, false);

    // Document countSectionResultsTitle style with HorizontalAlignment -> LEFT
    CellStyle countSectionResultsTitleStyle = setCellStyle(workbook, countSectionResultsTitleFont,
      true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document countSectionResultsTitle2 style with HorizontalAlignment -> RIGHT
    CellStyle countSectionResultsTitleStyle2 = setCellStyle(workbook, countSectionResultsTitleFont,
      true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, null);

    // Document countSectionResultsValue style with HorizontalAlignment -> RIGHT and no data format
    CellStyle countSectionResultsValueStyle = setCellStyle(workbook, countSectionResultsValueFont,
      true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document countSectionResultsValue2 style with HorizontalAlignment -> RIGHT and data format
    CellStyle countSectionResultsValueStyle2 = setCellStyle(workbook, countSectionResultsValueFont,
      true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, LookupValues.NUMBER_FORMAT);

    // Creating a row for counting the results
    Row row = sheet.createRow(startingIndex);

    // Add the countSectionResultsTitle on the first cell
    Cell cell = row.createCell(0);
    cell.setCellValue("Verfahren");
    cell.setCellStyle(countSectionResultsTitleStyle);

    // Add the countSectionResultsValue on the second cell
    cell = row.createCell(1);
    cell.setCellValue(results.size());
    cell.setCellStyle(countSectionResultsValueStyle);

    // Add the countSectionResultsTitle on the cell of the second to last column
    cell = row.createCell(columnNumbers - 2);
    cell.setCellValue("Total");
    cell.setCellStyle(countSectionResultsTitleStyle2);

    // Add the countSectionResultsValue on the cell of the last column
    cell = row.createCell(columnNumbers - 1);
    cell.setCellValue((totalizationBy.equals(TOTAL_BY_COMPANY))
        ? getReportTotalAmountCompany(results).doubleValue()
        : getReportTotalAmount(results).doubleValue());
    cell.setCellStyle(countSectionResultsValueStyle2);
  }

  /**
   * Add the section results.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param results       the results
   * @param columnHeaders the columnHeaders
   * @return the row index
   */
  private int addSectionResults(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    List<SubmissionDTO> results, List<String> columnHeaders) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSectionResults, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, results: {3}, columnHeaders: {4}",
      new Object[]{sheet, workbook, startingIndex, results, columnHeaders});

    // Document sectionResults font
    XSSFFont sectionResultsFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, false);

    // Document sectionResults style (with horizontal alignment -> LEFT)
    CellStyle sectionResultsStyle = setCellStyle(workbook, sectionResultsFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document sectionResults2 style (with horizontal alignment -> RIGHT)
    CellStyle sectionResultsStyle2 = setCellStyle(workbook, sectionResultsFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, null);

    // Document sectionResults3 style (with horizontal alignment -> RIGHT and data format)
    CellStyle sectionResultsStyle3 = setCellStyle(workbook, sectionResultsFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, LookupValues.NUMBER_FORMAT);

    AtomicInteger rowIndex = new AtomicInteger(startingIndex);

    results.forEach(result -> {
      // Creating a row for each result
      Row row = sheet.createRow(rowIndex.getAndIncrement());

      AtomicInteger columnIndex = new AtomicInteger();

      // Adding result values to cells for each column header
      columnHeaders.forEach(columnHeader -> {
        Cell cell = row.createCell(columnIndex.getAndIncrement());
        if (columnHeader.equals("Netto inkl. MWST")) {
          // This needs an extra case because we add a number to the cell
          BigDecimal amount = new BigDecimal(0);
          // Check for null pointer exceptions
          if (result.getReportAmountTotal() != null) {
            amount = result.getReportAmountTotal();
          }
          cell.setCellValue(amount.doubleValue());
          cell.setCellStyle(sectionResultsStyle3);
        } else {
          // All values added here are Strings
          cell.setCellValue(addColumnResult(columnHeader, result));
          if (columnHeader.equals("Zuschlagsjahr")) {
            // For column Zuschlagsjahr use style 2 (with horizontal alignment -> RIGHT)
            cell.setCellStyle(sectionResultsStyle2);
          } else {
            cell.setCellStyle(sectionResultsStyle);
          }
        }
      });
    });

    return rowIndex.incrementAndGet();
  }

  /**
   * Add the section results for the year report.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param results       the results
   * @return the row index
   */
  private int addSectionResults(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    Map<String, List<SubmissionDTO>> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSectionResults, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, results: {3}",
      new Object[]{sheet, workbook, startingIndex, results});

    // Document sectionResults font
    XSSFFont sectionResultsFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, false);

    // Document sectionResults style (with horizontal alignment -> LEFT)
    CellStyle sectionResultsStyle = setCellStyle(workbook, sectionResultsFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document sectionResults2 style (with horizontal alignment -> RIGHT)
    CellStyle sectionResultsStyle2 = setCellStyle(workbook, sectionResultsFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, LookupValues.NUMBER_FORMAT);

    AtomicInteger rowIndex = new AtomicInteger(startingIndex);

    List<String> processResults = Arrays
      .asList("Selektiv", "Offen", "Einladung", "Freihändig", "Freihändig mit Konkurrenz");

    processResults.forEach(process -> {
      // Creating a row for each process result
      Row row = sheet.createRow(rowIndex.getAndIncrement());

      // Add the process name
      Cell cell = row.createCell(0);
      cell.setCellValue(process);
      cell.setCellStyle(sectionResultsStyle);

      // Add the total amount for this process
      cell = row.createCell(1);
      cell.setCellValue(
        (results.containsKey(process))
          ? getReportTotalAmount(results.get(process)).doubleValue()
          : new BigDecimal(0).doubleValue());
      cell.setCellStyle(sectionResultsStyle2);
    });

    return rowIndex.incrementAndGet();
  }

  /**
   * Add the column result.
   *
   * @param columnHeader the columnHeader
   * @param result       the result
   * @return the result to be added to the cell
   */
  private String addColumnResult(String columnHeader, SubmissionDTO result) {

    LOGGER.log(Level.CONFIG,
      "Executing method addColumnResult, Parameters: columnHeader: {0}, result: {1}",
      new Object[]{columnHeader, result});

    String columnResult = StringUtils.EMPTY;
    switch (columnHeader) {
      case "Verfahren":
        columnResult = templateBean.getTranslatedProcedure(result.getProcess());
        break;
      case "Direktion":
        columnResult = result.getProject().getDepartment().getDirectorate().getShortName();
        break;
      case "Abteilung":
        columnResult = result.getProject().getDepartment().getName();
        break;
      case "Projektleitung":
        columnResult = result.getProject().getPmDepartmentName();
        break;
      case "Objekt":
        StringBuilder objectInfo = new StringBuilder();
        objectInfo.append(result.getProject().getObjectName().getValue1());
        if (result.getProject().getObjectName().getValue2() != null) {
          objectInfo.append(LookupValues.COMMA)
            .append(result.getProject().getObjectName().getValue2());
        }
        columnResult = objectInfo.toString();
        break;
      case "Projekt":
        columnResult = result.getProject().getProjectName();
        break;
      case "Projekt/Kredit-Nr.":
        columnResult = (result.getProject().getProjectNumber() != null)
            ? result.getProject().getProjectNumber()
            : StringUtils.EMPTY;
        break;
      case "Arbeitsgattung":
        columnResult = result.getWorkType().getValue1() + LookupValues.SPACE + result.getWorkType()
          .getValue2();
        break;
      case "Firma":
        columnResult = !result.getReportCompanies().isEmpty()
          ? result.getReportCompanies().get(0)
          : StringUtils.EMPTY;
        break;
      case "Zuschlagsjahr":
        SimpleDateFormat dateFormat = new SimpleDateFormat(LookupValues.DATE_FORMAT);
        columnResult = dateFormat.format(result.getReportAvailableDate());
        break;
      default:
        break;
    }
    return columnResult;
  }

  /**
   * Map the results by process.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByProcedure(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMapppedByProcedure, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByProcedure = new LinkedHashMap<>();

    if (results.stream()
      .anyMatch(submissionDTO -> submissionDTO.getProcess().equals(Process.SELECTIVE))) {
      // Adding selective results if exist
      resultsMappedByProcedure.put(templateBean.getTranslatedProcedure(Process.SELECTIVE),
        results.stream()
          .filter(submissionDTO -> submissionDTO.getProcess().equals(Process.SELECTIVE))
          .collect(
            Collectors.toList()));
    }
    if (results.stream()
      .anyMatch(submissionDTO -> submissionDTO.getProcess().equals(Process.OPEN))) {
      // Adding open results if exist
      resultsMappedByProcedure.put(templateBean.getTranslatedProcedure(Process.OPEN),
        results.stream().filter(submissionDTO -> submissionDTO.getProcess().equals(Process.OPEN))
          .collect(
            Collectors.toList()));
    }
    if (results.stream()
      .anyMatch(submissionDTO -> submissionDTO.getProcess().equals(Process.INVITATION))) {
      // Adding invitation results if exist
      resultsMappedByProcedure.put(templateBean.getTranslatedProcedure(Process.INVITATION),
        results.stream()
          .filter(submissionDTO -> submissionDTO.getProcess().equals(Process.INVITATION)).collect(
          Collectors.toList()));
    }
    if (results.stream()
      .anyMatch(submissionDTO -> submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE))) {
      // Adding negotiated results if exist
      resultsMappedByProcedure.put(templateBean.getTranslatedProcedure(Process.NEGOTIATED_PROCEDURE),
        results.stream()
          .filter(submissionDTO -> submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE))
          .collect(
            Collectors.toList()));
    }
    if (results.stream()
      .anyMatch(submissionDTO -> submissionDTO.getProcess()
        .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))) {
      // Adding negotiated with competition results if exist
      resultsMappedByProcedure
        .put(templateBean.getTranslatedProcedure(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION),
          results.stream().filter(submissionDTO -> submissionDTO.getProcess()
            .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)).collect(
            Collectors.toList()));
    }
    return resultsMappedByProcedure;
  }

  /**
   * Map the results by object.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByObject(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByObject, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByObject = new LinkedHashMap<>();

    // List with all the object names
    List<String> objectList = results.stream()
      .map(submissionDTO -> submissionDTO.getProject().getObjectName().getValue1()).collect(
        Collectors.toList());

    objectList.forEach(object -> resultsMappedByObject.put(object,
      results.stream()
        .filter(
          submissionDTO -> submissionDTO.getProject().getObjectName().getValue1().equals(object))
        .collect(Collectors.toList())));

    return resultsMappedByObject;
  }

  /**
   * Map the results by project.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByProject(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByProject, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByProject = new LinkedHashMap<>();

    // List with all the project names
    List<String> projectList = results.stream()
      .map(submissionDTO -> submissionDTO.getProject().getProjectName()).collect(
        Collectors.toList());

    projectList.forEach(object -> resultsMappedByProject.put(object,
      results.stream()
        .filter(
          submissionDTO -> submissionDTO.getProject().getProjectName().equals(object))
        .collect(Collectors.toList())));

    return resultsMappedByProject;
  }

  /**
   * Map the results by project number.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByProjectNumber(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByProjectNumber, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByProjectNumber = new LinkedHashMap<>();

    // Replace null values with empty strings to avoid null pointer exceptions
    results.forEach(submissionDTO -> {
      if (submissionDTO.getProject().getProjectNumber() == null) {
        submissionDTO.getProject().setProjectNumber(StringUtils.EMPTY);
      }
    });

    // List with all the project numbers
    List<String> projectNumberList = results.stream()
      .map(submissionDTO -> submissionDTO.getProject().getProjectNumber()).collect(
        Collectors.toList());

    projectNumberList.forEach(projectNumber -> resultsMappedByProjectNumber.put(projectNumber,
      results.stream()
        .filter(
          submissionDTO -> submissionDTO.getProject().getProjectNumber().equals(projectNumber))
        .collect(Collectors.toList())));

    return resultsMappedByProjectNumber;
  }

  /**
   * Map the results by project manager.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByProjectManager(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByProjectManager, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByProjectManager = new LinkedHashMap<>();

    // List with all the project manager's depertments
    List<String> projectManagerList = results.stream()
      .map(submissionDTO -> submissionDTO.getProject().getPmDepartmentName()).collect(
        Collectors.toList());

    projectManagerList.forEach(projectManager -> resultsMappedByProjectManager.put(projectManager,
      results.stream()
        .filter(
          submissionDTO -> submissionDTO.getProject().getPmDepartmentName().equals(projectManager))
        .collect(Collectors.toList())));

    return resultsMappedByProjectManager;
  }

  /**
   * Map the results by work type.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByWorkType(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByWorkType, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByWorkType = new LinkedHashMap<>();

    // List with all the work types
    List<String> workTypeList = results.stream()
      .map(submissionDTO -> submissionDTO.getWorkType().getValue1()
        + " " + submissionDTO.getWorkType().getValue2()).collect(
        Collectors.toList());

    workTypeList.forEach(type -> resultsMappedByWorkType.put(type,
      results.stream()
        .filter(
          submissionDTO -> (submissionDTO.getWorkType().getValue1()
            + " " + submissionDTO.getWorkType().getValue2()).equals(type))
        .collect(Collectors.toList())));

    return resultsMappedByWorkType;
  }

  /**
   * Map the results by directorate.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByDirectorate(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByDirectorate, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByDirectorate = new LinkedHashMap<>();

    // List with all the directorate short names
    List<String> directoratesList = results.stream()
      .map(
        submissionDTO -> submissionDTO.getProject().getDepartment().getDirectorate().getShortName())
      .collect(Collectors.toList());

    directoratesList.forEach(directorate -> resultsMappedByDirectorate.put(directorate,
      results.stream()
        .filter(
          submissionDTO -> submissionDTO.getProject().getDepartment().getDirectorate()
            .getShortName().equals(directorate))
        .collect(Collectors.toList())));

    return resultsMappedByDirectorate;
  }

  /**
   * Map the results by department.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByDepartment(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByDepartment, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByDepartment = new LinkedHashMap<>();

    // List with all the department names
    List<String> departmentList = results.stream()
      .map(
        submissionDTO -> submissionDTO.getProject().getDepartment().getName())
      .collect(Collectors.toList());

    departmentList.forEach(department -> resultsMappedByDepartment.put(department,
      results.stream()
        .filter(
          submissionDTO -> submissionDTO.getProject().getDepartment().getName().equals(department))
        .collect(Collectors.toList())));

    return resultsMappedByDepartment;
  }

  /**
   * Map the results by company.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, List<SubmissionDTO>> getResultsMappedByCompany(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByCompany, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByCompany = new LinkedHashMap<>();

    // List with all the company names
    List<String> companyList = results.stream()
      .map(SubmissionDTO::getReportAwardedSubmittentName)
      .collect(Collectors.toList());

    companyList.forEach(company -> resultsMappedByCompany.put(company,
      results.stream()
        .filter(
          submissionDTO -> submissionDTO.getReportAwardedSubmittentName().equals(company))
        .collect(Collectors.toList())));

    return resultsMappedByCompany;
  }

  /**
   * Map the results by year and process.
   *
   * @param results the results
   * @return the mapped results
   */
  private Map<String, Map<String, List<SubmissionDTO>>> getResultsMappedByYearAndProcess(
    List<SubmissionDTO> results) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResultsMappedByYearAndProcess, Parameters: results: {0}",
      results);

    Map<String, List<SubmissionDTO>> resultsMappedByYear = new LinkedHashMap<>();
    Map<String, Map<String, List<SubmissionDTO>>> resultsMappedByYearAndProcess = new LinkedHashMap<>();

    // List with all the years
    List<String> yearList = results.stream()
      .map(submissionDTO -> new SimpleDateFormat("yyyy")
        .format(submissionDTO.getReportAvailableDate())).distinct().sorted()
      .collect(Collectors.toList());

    yearList.forEach(year -> resultsMappedByYear.put(year,
      results.stream()
        .filter(
          submissionDTO -> new SimpleDateFormat("yyyy")
            .format(submissionDTO.getReportAvailableDate()).equals(year))
        .collect(Collectors.toList())));

    resultsMappedByYear
      .forEach((k, v) -> resultsMappedByYearAndProcess.put(k, getResultsMappedByProcedure(v)));

    return resultsMappedByYearAndProcess;
  }

  /**
   * Add the section title to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param sectionTitle  the sectionTitle
   */
  private void addSectionTitle(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    String sectionTitle) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSectionTitle, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, sectionTitle: {3}",
      new Object[]{sheet, workbook, startingIndex, sectionTitle});

    // Document sectionTitle font
    XSSFFont sectionTitleFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, true);

    // Document sectionTitle style
    CellStyle sectionTitleStyle = setCellStyle(workbook, sectionTitleFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, false, null);

    // Creating the row that contains the sectionTitle
    Row row = sheet.createRow(startingIndex);

    Cell cell = row.createCell(0);
    cell.setCellValue(sectionTitle);
    cell.setCellStyle(sectionTitleStyle);
  }

  /**
   * Adds the column headers to the document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param columnHeaders the columnHeaders
   */
  private void addColumnHeaders(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    List<String> columnHeaders) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertDocumentData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, columnHeaders: {3}",
      new Object[]{sheet, workbook, startingIndex, columnHeaders});

    // Document columnHeader font
    XSSFFont columnHeaderFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, true);

    // Document columnHeader style
    CellStyle columnHeaderStyle = setCellStyle(workbook, columnHeaderFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document columnHeader style with HorizontalAlignment -> RIGHT
    CellStyle columnHeaderStyle2 = setCellStyle(workbook, columnHeaderFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, null);

    // Adding thin, black bottom border to the styles
    columnHeaderStyle.setBorderBottom(BorderStyle.THIN);
    columnHeaderStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

    columnHeaderStyle2.setBorderBottom(BorderStyle.THIN);
    columnHeaderStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());

    // Creating the row that contains the column headers
    Row row = sheet.createRow(startingIndex);

    AtomicInteger index = new AtomicInteger();

    // Adding column headers to cells
    columnHeaders.forEach(columnHeader -> {
      Cell cell = row.createCell(index.getAndIncrement());
      cell.setCellValue(columnHeader);
      if (columnHeader.equals("Zuschlagsjahr") || columnHeader.equals("Netto inkl. MWST")) {
        // For these 2 columns the style must contain HorizontalAlignment -> RIGHT
        cell.setCellStyle(columnHeaderStyle2);
      } else {
        cell.setCellStyle(columnHeaderStyle);
      }
    });
  }

  /**
   * Adds the column headers to the year document.
   *
   * @param sheet         the sheet
   * @param workbook      the workbook
   * @param startingIndex the startingIndex
   * @param year          the year
   */
  private void addColumnHeaders(XSSFSheet sheet, XSSFWorkbook workbook, int startingIndex,
    String year) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertDocumentData, Parameters: sheet: {0}, workbook: {1}, "
        + "startingIndex: {2}, year: {3}",
      new Object[]{sheet, workbook, startingIndex, year});

    // Document columnHeader font
    XSSFFont columnHeaderFont = setFontStyle(workbook, DocumentProperties.ARIAL.getValue(),
      FONT_SIZE_9, true);

    // Document columnHeader style 1
    CellStyle columnHeaderStyle1 = setCellStyle(workbook, columnHeaderFont, true,
      HorizontalAlignment.LEFT, VerticalAlignment.TOP, true, null);

    // Document columnHeader style 2
    CellStyle columnHeaderStyle2 = setCellStyle(workbook, columnHeaderFont, true,
      HorizontalAlignment.RIGHT, VerticalAlignment.TOP, true, null);

    // Adding thin, black bottom border to the styles
    columnHeaderStyle1.setBorderBottom(BorderStyle.THIN);
    columnHeaderStyle1.setBottomBorderColor(IndexedColors.BLACK.getIndex());

    columnHeaderStyle2.setBorderBottom(BorderStyle.THIN);
    columnHeaderStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());

    // Creating the row that contains the column headers
    Row row = sheet.createRow(startingIndex);

    // First cell contains the year
    Cell cell = row.createCell(0);
    cell.setCellValue(year);
    cell.setCellStyle(columnHeaderStyle1);

    // Second cell contains the total amount
    cell = row.createCell(1);
    cell.setCellValue("Netto inkl. MWST");
    cell.setCellStyle(columnHeaderStyle2);
  }

  /**
   * Gets the column headers as displayed in the report.
   *
   * @param totalizationBy the totalizationBy
   * @return the column headers
   */
  private List<String> getColumnHeaders(String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method getColumnHeaders, Parameters: totalizationBy: {0}",
      totalizationBy);

    List<String> columnHeaders = new LinkedList<>();
    columnHeaders.add("Verfahren");
    columnHeaders.add("Direktion");
    columnHeaders.add("Abteilung");
    columnHeaders.add("Projektleitung");
    columnHeaders.add("Objekt");
    columnHeaders.add("Projekt");
    columnHeaders.add("Projekt/Kredit-Nr.");
    columnHeaders.add("Arbeitsgattung");
    columnHeaders.add("Firma");
    columnHeaders.add("Zuschlagsjahr");
    columnHeaders.add("Netto inkl. MWST");

    // Remove the selected totalization from the column header list.
    columnHeaders.removeIf(s -> s.equals(getColumnHeaderFromTotalization(totalizationBy)));

    return columnHeaders;
  }

  /**
   * Gets the column header in German from totalization by.
   *
   * @param totalizationBy the totalization by
   * @return the column header
   */
  private String getColumnHeaderFromTotalization(String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method getColumnHeaderFromTotalization, Parameters: totalizationBy: {0}",
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
        totalby = "Projekt/Kredit-Nr.";
        break;
      case TOTAL_BY_PROJECT_MANAGER:
        totalby = "Projektleitung";
        break;
      case TOTAL_BY_WORKTYPE:
        totalby = "Arbeitsgattung";
        break;
      case TOTAL_BY_DIRECTORATE:
        totalby = "Direktion";
        break;
      case TOTAL_BY_DEPARTMENT:
        totalby = "Abteilung";
        break;
      default:
        break;
    }
    return totalby;
  }

  /**
   * Set the column width.
   *
   * @param sheet          the sheet
   * @param totalizationBy the totalizationBy
   */
  private void setColumnWidth(XSSFSheet sheet, String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method setColumnWidth, Parameters: sheet: {0}, totalizationBy: {1}",
      new Object[]{sheet, totalizationBy});

    List<Integer> columnWidths = getColumnWidthByTotalization(totalizationBy);
    AtomicInteger index = new AtomicInteger();

    columnWidths
      .forEach(column -> sheet.setColumnWidth(index.getAndIncrement(), column * CHARACTER_WIDTH));
  }

  /**
   * Gets the column widths for specific totalization.
   *
   * @param totalizationBy the totalizationBy
   * @return the column widths
   */
  private List<Integer> getColumnWidthByTotalization(String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method getColumnWidthByTotalization, Parameters: totalizationBy: {0}",
      totalizationBy);

    List<Integer> columnWidths = new ArrayList<>();

    switch (totalizationBy) {
      case TOTAL_BY_PROCEDURE:
        columnWidths = Arrays.asList(21, 30, 23, 37, 38, 18, 29, 37, 16, 19);
        break;
      case TOTAL_BY_OBJECT:
        columnWidths = Arrays.asList(16, 22, 32, 23, 37, 19, 30, 37, 16, 19);
        break;
      case TOTAL_BY_PROJECT:
        columnWidths = Arrays.asList(19, 20, 32, 23, 37, 19, 30, 37, 16, 19);
        break;
      case TOTAL_BY_PROJECT_CREDIT_NO:
        columnWidths = Arrays.asList(19, 20, 30, 23, 37, 38, 30, 37, 16, 19);
        break;
      case TOTAL_BY_PROJECT_MANAGER:
      case TOTAL_BY_DEPARTMENT:
        columnWidths = Arrays.asList(19, 20, 23, 37, 38, 19, 33, 45, 16, 19);
        break;
      case TOTAL_BY_WORKTYPE:
        columnWidths = Arrays.asList(19, 20, 30, 23, 37, 38, 19, 38, 16, 19);
        break;
      case TOTAL_BY_DIRECTORATE:
        columnWidths = Arrays.asList(19, 27, 30, 32, 38, 19, 33, 38, 16, 19);
        break;
      case TOTAL_BY_COMPANY:
        columnWidths = Arrays.asList(19, 12, 27, 20, 28, 30, 23, 39, 38, 16, 19);
        break;
      case TOTAL_BY_YEAR:
        columnWidths = Arrays.asList(27, 41);
        break;
      default:
        break;
    }
    return columnWidths;
  }

  /**
   * Maps the search criteria titles to german.
   *
   * @return the search criteria titles mapping
   */
  private  Map<String, String> getSearchCriteriaTitles() {
    return new HashMap<String, String>(){{
      put("searchedDate", LookupValues.ZEITRAUM + ":");
      put("searchedObjects", "Objekt:");
      put("searchedProjects", "Projekt:");
      put("searchedProjectNumbers", "Projekt/Kredit-Nr.");
      put("searchedWorkedTypes", "Arbeitsgattung");
      put("searchedProcedures", "Verfahren");
      put("searchedDirectorates", "Direktion");
      put("searchedDepartments", "Abteilung");
      put("searchedPMDepartment", "Projektleitung der Abt.");
      put("searchedCompanies", "Firma");
    }};
  }

  /**
   * Set the font style.
   *
   * @param workbook the workbook
   * @param fontName the fontName
   * @param fontSize the fontSize
   * @param isBold isBold
   * @return the font style
   */
  private XSSFFont setFontStyle(XSSFWorkbook workbook, String fontName, int fontSize,
    boolean isBold) {

    LOGGER.log(Level.CONFIG,
      "Executing method setFontStyle, Parameters: workbook: {0}, "
        + "fontName: {1}, fontSize: {2}, isBold: {3}",
      new Object[]{workbook, fontName, fontSize, isBold});

    XSSFFont fontStyle = workbook.createFont();
    fontStyle.setFontName(fontName);
    fontStyle.setFontHeight(fontSize);
    fontStyle.setBold(isBold);
    fontStyle.setColor(IndexedColors.BLACK.index);

    return fontStyle;
  }

  /**
   * Set the cell style.
   *
   * @param workbook the workbook
   * @param font the font
   * @param isLocked the isLocked
   * @param horizontalAlignment the horizontalAlignment
   * @param verticalAlignment the verticalAlignment
   * @param wrapText the wrapText
   * @return the cell style
   */
  private CellStyle setCellStyle(XSSFWorkbook workbook, XSSFFont font,
    boolean isLocked, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment,
    boolean wrapText, String dataFormat) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCellStyle, Parameters: workbook: {0}, "
        + "font: {1}, isLocked: {2}, horizontalAlignment: {3}, verticalAlignment: {4}, "
        + "wrapText: {5}",
      new Object[]{workbook, font, isLocked, horizontalAlignment, verticalAlignment, wrapText});

    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setLocked(isLocked);
    cellStyle.setAlignment(horizontalAlignment);
    cellStyle.setVerticalAlignment(verticalAlignment);
    cellStyle.setWrapText(wrapText);
    cellStyle.setFont(font);

    if (StringUtils.isNotBlank(dataFormat)) {
      DataFormat format = workbook.createDataFormat();
      cellStyle.setDataFormat(format.getFormat(dataFormat));
    }

    return cellStyle;
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
   * Gets the total amount for the company report.
   *
   * @param submissions the submissions
   * @return the report total amount
   */
  private BigDecimal getReportTotalAmountCompany(List<SubmissionDTO> submissions) {

    LOGGER.log(Level.CONFIG, "Executing method getReportTotalAmountCompany, Parameters: "
      + "submissions: {0}", submissions);

    BigDecimal amountTotal = new BigDecimal(0);
    for (SubmissionDTO submissionDTO : submissions) {
      if (submissionDTO != null && submissionDTO.getReportAmountTotal() != null) {
        amountTotal = amountTotal.add(submissionDTO.getReportAmountTotal());
      }
    }
    return amountTotal;
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
    newSubmission.setReportAmountTotal(amount);
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
   * Setting report's name.
   *
   * @param totalizationBy the totalization by
   * @return the report file name
   */
  public String getReportFileName(String totalizationBy) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportFileName, Parameters: totalizationBy: {0}",
      totalizationBy);

    return DEFAULT_REPORT_START_NAME
      + getReportTotalizationBy(totalizationBy)
      + LookupValues.EXCEL_FORMAT;
  }

  @Override
  public ReportResultsDTO getReportResults(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportResults, Parameters: reportDTO: {0}",
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

    /*
     * If user searches with specific directorate the query should retrieve results with all the directorate history
     * because there is no direct mapping from project to directorate (only to department).
     * To avoid wrong directorate names in the results we filter the query results to keep only the correct directorate names.
     * (If we do not use the directorate history, the results are missing the case of a department changing to a different directorate as we retrieve only the latest results.)
     */
    submissionDTOList = filterWithDirectorate(reportDTO, submissionDTOList);

    setReportCompanies(submissionDTOList);
    ReportResultsDTO results = new ReportResultsDTO();
    results.setSubmissions(submissionDTOList);
    /* Setting reports searched criteria */
    Map<String, String> searchCriteriaMap = getReportSearchCriteria(reportDTO);
    results.setSearchedCriteria(searchCriteriaMap);
    results.setTotalizationBy(reportDTO.getTotalizationBy());
    results.setStartDate(reportDTO.getStartDate());
    results.setEndDate(reportDTO.getEndDate());
    results.setSearchedCompanies(reportDTO.getCompanies());
    return results;
  }

  /**
   * Filter the query results with directorates.
   *
   * @param reportDTO         the reportDTO
   * @param submissionDTOList the submissionDTOList
   * @return the submissionDTOList
   */
  private List<SubmissionDTO> filterWithDirectorate(ReportDTO reportDTO,
    List<SubmissionDTO> submissionDTOList) {

    LOGGER.log(Level.CONFIG,
      "Executing method filterWithDirectorate, Parameters: reportDTO: {0}, submissionDTOList: {1}",
      new Object[]{reportDTO, submissionDTOList});

    if (checkIfNotEmpty(reportDTO.getDirectorates())) {
      List<String> directorateIds = new JPAQueryFactory(em)
        .select(qDirectorateHistoryEntity.directorateId.id).from(qDirectorateHistoryEntity)
        .where(qDirectorateHistoryEntity.id.in(reportDTO.getDirectorates()))
        .fetch();

      submissionDTOList = submissionDTOList.stream().filter(submissionDTO ->
        directorateIds.contains(
          submissionDTO.getProject().getDepartment().getDirectorate().getDirectorateId().getId()))
        .collect(Collectors.toList());
    }
    return submissionDTOList;
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
          .in(directorateIds))
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
      case TOTAL_BY_DIRECTORATE:
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

  @Override
  public Map<String, String> getReportSearchCriteria(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getReportSearchCriteria, Parameters: reportDTO: {0}",
      reportDTO);

    Map<String, String> searchCriteriaMap = new LinkedHashMap<>();
    if (reportDTO.getStartDate() != null || reportDTO.getEndDate() != null) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(LookupValues.DATE_FORMAT);
      String startDate = reportDTO.getStartDate() != null
        ? dateFormat.format(reportDTO.getStartDate())
        : "...";
      String endDate = reportDTO.getEndDate() != null
        ? dateFormat.format(reportDTO.getEndDate())
        : "...";
      searchCriteriaMap.put("searchedDate", startDate + " - " + endDate);
    }
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
    if (StringUtils.isNotBlank(workTypeDTO.getValue2())) {
      workTypeName.append(LookupValues.SPACE).append(workTypeDTO.getValue2());
    }
    return workTypeName.toString();
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

    /* Apply ordering according to totalization **/
    String totalizationBy = reportDTO.getTotalizationBy();
    switch (totalizationBy) {
      case TOTAL_BY_OBJECT:
        query.orderBy(objectNameHistory.value1.asc(), submission.process.asc());
        break;
      case TOTAL_BY_PROJECT:
        query.orderBy(submission.project.projectName.asc(), submission.process.asc());
        break;
      case TOTAL_BY_PROJECT_CREDIT_NO:
        query.orderBy(submission.project.projectNumber.asc(), submission.process.asc());
        break;
      case TOTAL_BY_PROJECT_MANAGER:
        query.orderBy(submission.project.pmDepartmentName.asc());
        break;
      case TOTAL_BY_WORKTYPE:
        query.orderBy(workTypeHistory.value1.asc());
        break;
      case TOTAL_BY_DIRECTORATE:
        query.orderBy(qDirectorateHistoryEntity.shortName.asc());
        break;
      case TOTAL_BY_DEPARTMENT:
        query.orderBy(qDepartmentHistoryEntity.name.asc());
        break;
      case TOTAL_BY_PROCEDURE:
        query.orderBy(submission.process.asc(), qDirectorateHistoryEntity.shortName.asc());
        break;
      default:
        break;
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

  @Override
  public Long proceedToResults(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG, "Executing method proceedToResults, Parameters: reportDTO: {0}",
      reportDTO);

    Long results = null;
    if (reportDTO != null) {
      Long reportResults = getNumberOfResultsByReport(reportDTO);
      Long maximumResults = sDService.getReportMaximumResultsValue();
      if (reportResults > maximumResults) {
        results = maximumResults;
      }
    }
    return results;
  }

  @Override
  public Set<ValidationError> validate(ReportDTO reportDTO) {

    LOGGER.log(Level.CONFIG, "Executing method validate, Parameters: reportDTO: {0}",
      reportDTO);

    // Security check for reports
    reportSecurityCheck();
    Set<ValidationError> errors = new HashSet<>();
    if (reportDTO != null) {
      Date today = new Date();
      if (reportDTO.getStartDate() != null && reportDTO.getStartDate().after(today)
        || reportDTO.getEndDate() != null && reportDTO.getEndDate().after(today)) {
        errors.add(new ValidationError(ValidationMessages.FUTURE_DATE_ERROR_FIELD,
          ValidationMessages.DATE_IN_THE_FUTURE_ERROR_MESSAGE));
      }
      if (reportDTO.getStartDate() != null && reportDTO.getEndDate() != null
        && reportDTO.getStartDate().after(reportDTO.getEndDate())) {
        errors.add(new ValidationError(ValidationMessages.START_DATE_AFTER_END_DATE_ERROR_FIELD,
          ValidationMessages.START_DATE_AFTER_END_DATE_ERROR_MESSAGE));
      }
    }
    return errors;
  }
}
