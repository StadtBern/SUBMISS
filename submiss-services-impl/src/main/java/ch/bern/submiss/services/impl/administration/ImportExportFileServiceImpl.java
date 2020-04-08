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

import ch.bern.submiss.services.api.administration.CriterionService;
import ch.bern.submiss.services.api.administration.ImportExportFileService;
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.DocumentProperties;
import ch.bern.submiss.services.api.constants.ShortCode;
import ch.bern.submiss.services.api.constants.TemplateConstants;
import ch.bern.submiss.services.api.dto.AwardAssessDTO;
import ch.bern.submiss.services.api.dto.AwardEvaluationDocumentDTO;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.CriterionLiteDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionLiteDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.OfferSubcriterionDTO;
import ch.bern.submiss.services.api.dto.OfferSubcriterionLiteDTO;
import ch.bern.submiss.services.api.dto.ProjectDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SuitabilityDTO;
import ch.bern.submiss.services.api.dto.SuitabilityDocumentDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class ImportExportFileServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {ImportExportFileService.class})
@Singleton
public class ImportExportFileServiceImpl extends BaseService implements ImportExportFileService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER =
    Logger.getLogger(ImportExportFileServiceImpl.class.getName());

  /**
   * The Constant FULLFILLED.
   */
  private static final String FULLFILLED = "Erfüllt";

  /**
   * The Constant NOTFULLFILLED.
   */
  private static final String NOTFULLFILLED = "Nicht erfüllt";

  /**
   * The Constant GEWICHTUNG.
   */
  private static final String GEWICHTUNG = "G";

  /**
   * The Constant GRADE.
   */
  private static final String GRADE = "Note";

  /**
   * The Constant POINTS.
   */
  private static final String POINTS = "Punkte";

  /**
   * The Constant TOTAL_POINTS.
   */
  private static final String TOTAL_POINTS = "Totale Punkte";

  /**
   * The Constant COMMENT.
   */
  private static final String COMMENT = "Bemerkung";

  /**
   * The Constant SHEET_TITLE.
   */
  private static final String SHEET_TITLE = "Kriterien";

  /**
   * The Constant MUST_CRITERIA.
   */
  private static final String MUST_CRITERIA = "Muss Kriterien";

  /**
   * The Constant RATED_CRITERIA.
   */
  private static final String RATED_CRITERIA = "Bewertete Kriterien";

  /**
   * The Constant SURCHARGE.
   */
  private static final String SURCHARGE = "Zuschlagskriterien";

  /**
   * The Constant OPERATING_COST.
   */
  private static final String OPERATING_COST = "Betriebskosten";

  /**
   * The Constant NET_PRICE.
   */
  private static final String NET_PRICE = "Netto-Preis exkl. MWST";

  /**
   * The Constant RANK.
   */
  private static final String RANK = "Rang";

  /**
   * The Constant MALFORMED_ERROR_MSG.
   */
  private static final String MALFORMED_ERROR_MSG =
    "Die zu importierende Excel-Datei ist fehlerhaft.";

  /**
   * The Constant NO_SUB_FOUND_ERROR_MSG.
   */
  private static final String NO_SUB_FOUND_ERROR_MSG =
    "Der Import der Excel-Datei kann nicht erfolgen, da die Datei aus einer anderen Submission exportiert wurde";

  /**
   * The Constant NOTE_OUT_OF_BOUNDS.
   */
  private static final String NOTE_OUT_OF_BOUNDS =
    "Der Import kann nicht durchgeführt werden, da die zu importierenden Noten nicht innerhalb der festgelegten Mindest- und Maximalnote liegen.";

  /**
   * The Constant TEXT_OUT_OF_BOUNDS.
   */
  private static final String TEXT_OUT_OF_BOUNDS =
    "Alle vorhandene Bemerkungsfeldeingaben müssen mindestens 10 und maximal 150 Zeichen lang sein, um den Import durchzuführen.";

  /**
   * The Constant MALFORMED_CRITERIA.
   */
  private static final String MALFORMED_CRITERIA =
    "Der Import kann nicht durchgeführt werden, da die zu importierenden Kriterien bzgl. Bezeichnung/Position von den in Submiss bestehenden Kriterien abweichen.";

  /**
   * The Constant MALFORMED_COLUMNS.
   */
  private static final String MALFORMED_COLUMNS =
    "Der Import kann nicht durchgeführt werden, da die Struktur des Dokuments verändert wurde.";

  /**
   * The Constant MALFORMED_INPUT.
   */
  private static final String MALFORMED_INPUT =
    "Der Import kann nicht durchgeführt werden, da die zu importierende Datei unzulässige Zeichen enthält.";

  /**
   * The Constant MALFORMED_GEWICHT.
   */
  private static final String MALFORMED_GEWICHT =
    "Der Import kann nicht durchgeführt werden, da es eine Veränderung bei der Gewichtung gibt.";

  /**
   * The Constant MALFORMED_SUBMITTENTEN.
   */
  private static final String MALFORMED_SUBMITTENTEN =
    "Der Import kann nicht durchgeführt werden, da die zu importierenden Submittenten bzgl. Bezeichnung/Position von den bestehenden Submittenten abweichen.";

  /**
   * The Constant WARNING_SUBMITTENTEN.
   */
  private static final String WARNING_SUBMITTENTEN =
    "Es wurden seit dem letzten Export Submittenten/Bewerber hinzugefügt";

  /**
   * The Constant WARNING_PREIS
   */
  private static final String WARNING_PREIS =
    "Der Preis und/oder die Betriebskosten wurden verändert.";

  /**
   * The Constant WARNING_EXCLUDED
   */
  private static final String WARNING_EXCLUDED =
    "Es wurden manche Werte von Submittenten/Bewerber nicht übernommen, da diese zwischenzeitlich aus dem Verfahren ausgeschlossen wurden.";

  /**
   * The Constant UPLOAD_EXCEL_FILE
   */
  private static final String UPLOAD_EXCEL_FILE =
    "Bitte laden Sie eine Datei im Excel-Format hoch.";

  /**
   * The Constant SUITABILITY.
   */
  private static final String SUITABILITY = "suitability";

  /**
   * The Constant AWARD.
   */
  private static final String AWARD = "award";

  /**
   * The SUITABILITY label.
   */
  private static final String SUITABILITY_LABEL = "Eignungsprüfung";

  /**
   * The AWARD label.
   */
  private static final String AWARD_LABEL = "Zuschlagsbewertung";

  /**
   * The Constant PROPOSERS.
   */
  private static final String PROPOSERS = "Submittenten";

  /**
   * The EWB TITLE.
   */
  private static final String EWB_TITLE = "Beschaffungsausschuss (BA)";

  /**
   * The BERN TITLE.
   */
  private static final String BERN_TITLE = "Fachstelle Beschaffungswesen (FaBe)";

  /**
   * The default font.
   */
  private static final String FONT = "Arial";

  /**
   * The default font size.
   */
  private static final int FONT_SIZE = 10;

  /**
   * The default font size nine.
   */
  private static final int FONT_SIZE_NINE = 9;

  /**
   * The Constant HEADING_ROWNUM.
   */
  private static final int HEADING_ROWNUM = 0;

  /**
   * The Constant MAX_TEXT_SIZE.
   */
  private static final int MAX_TEXT_SIZE = 150;

  /**
   * The Constant MIN_TEXT_SIZE.
   */
  private static final int MIN_TEXT_SIZE = 10;

  /**
   * The Constant DATA_STARTING_ROWNUM.
   */
  private static final int DATA_STARTING_ROWNUM = 9;

  /**
   * The Constant TITLE_ROWNUM.
   */
  private static final int TITLE_ROWNUM = DATA_STARTING_ROWNUM - 2;

  /**
   * The Constant AWARD_TITLE_ROWNUM.
   */
  private static final int AWARD_TITLE_ROWNUM = DATA_STARTING_ROWNUM - 1;

  /**
   * The Constant DATA_STARTING_COLLNUM.
   */
  private static final int DATA_STARTING_COLLNUM = 3;

  /**
   * The Constant CARACTER_WIDTH.
   */
  private static final int CARACTER_WIDTH = 256;

  /**
   * The default max note.
   */
  private static final int DEFAULT_MAXNOTE = 5;

  /**
   * The default min note.
   */
  private static final int DEFAULT_MIN_NOTE = 0;

  /**
   * The default min note.
   */
  private static final int DEFAULT_MIN_NOTE_AWARD = -999;

  /**
   * The default MAX_SCORE.
   */
  private static final String MAX_SCORE = "Maximalnote: ";

  /**
   * The default MIN_SCORE.
   */
  private static final String MIN_SCORE = "Mindestnote: ";

  /**
   * The default merged cells width in chars.
   */
  private static final int MERGED_CELLS_WIDTH_IN_CHARS = 45;

  /**
   * The default title merged cells width in chars.
   */
  private static final int TITLE_MERGED_CELLS_WIDTH_IN_CHARS = 75;

  /**
   * The default criteria title merged cells width in chars.
   */
  private static final int CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS = 31;

  /**
   * The default A4 page height in points.
   */
  private static final float A4_PAGE_HEIGHT = (float) 763.5;

  /**
   * The Constant SUBMITTENT_INFORMATION_LENGTH.
   */
  private static final int SUBMITTENT_INFORMATION_LENGTH = 29;

  /**
   * The Constant SUITABILITY_NOTES_LENGTH.
   */
  private static final int SUITABILITY_NOTES_LENGTH = 58;

  /**
   * The Constant SUITABILITY_CRITERION_TEXT_LENGTH.
   */
  private static final int SUITABILITY_CRITERION_TEXT_LENGTH = 12;

  /**
   * The Constant AWARD_CRITERION_TEXT_LENGTH.
   */
  private static final int AWARD_CRITERION_TEXT_LENGTH = 15;

  /**
   * The Constant FORMEL_PREIS.
   */
  private static final String FORMEL_PREIS = "Formel Preis: ";

  /**
   * The Constant FORMEL_BETRIEBSKOSTEN.
   */
  private static final String FORMEL_BETRIEBSKOSTEN = "Formel Betriebskosten: ";

  /**
   * The Constant PARSING_PHRASE.
   */
  private static final String PARSING_PHRASE = " parsing.";

  /**
   * The Constant CELL_VALUE.
   */
  private static final String CELL_VALUE = "100.00";

  /** The Constant NUMERIC_TO_STRING_ERROR. */
  private static final String NUMERIC_TO_STRING_ERROR =
      "Cannot get a STRING value from a NUMERIC formula cell";

  /**
   * The Constant TABLE_ROWS.
   */
  private static final String TABLE_ROWS = "tableRows";

  /**
   * The Constant COMPANY_INDEX.
   */
  private static final String COMPANY_INDEX = "companyIndex";

  /**
   * The Constant DATA_COLUMN.
   */
  private static final String DATA_COLUMN = "dataColumn";

  /**
   * The Constant OVERVIEW_PARTNER_COMPANIES_END_ROW.
   */
  private static final String OVERVIEW_PARTNER_COMPANIES_END_ROW = "overviewEndRow";

  /**
   * The Constant ARGE_PARTNER.
   */
  private static final String ARGE_PARTNER = "ARGE Partner";

  /**
   * The Constant SUBUNTERNEHMEN.
   */
  private static final String SUBUNTERNEHMEN = "SubUnternehmen";

  /**
   * The Constant OFFERT_INFORMATIONEN.
   */
  private static final String OFFERT_INFORMATIONEN = "Offertinformationen";

  /**
   * The Constant BEMERKUNGEN.
   */
  private static final String BEMERKUNGEN = "Bemerkungen";

  /**
   * The Constant MAX_CELL_NUM_ARGE.
   */
  private static final String MAX_CELL_NUM_ARGE = "maxCellNumArge";

  /**
   * The Constant MAX_CELL_NUM_SUBCONTRACTORS.
   */
  private static final String MAX_CELL_NUM_SUBCONTRACTORS = "maxCellNumSubContractors";

  /**
   * The offer service.
   */
  // /** The offerService service. */
  @Inject
  protected OfferService offerService;
  /**
   * The file upload.
   */
  @OsgiService
  @Inject
  private FileUpload fileUpload;
  /**
   * The s D service.
   */
  @Inject
  private SDService sDService;
  /**
   * The criterion service.
   */
  @Inject
  private CriterionService criterionService;

  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;

  /**
   * The submission service.
   */
  @Inject
  private TemplateBean templateBeanService;

  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;

  /**
   * Exports byte [] representation for given List offer.
   *
   * @param submissionId the submission id
   * @param offers the offers
   * @param type the type
   * @return the byte[]
   */
  @Override
  public byte[] exportTableOfCriteria(String submissionId, List<OfferDTO> offers, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method exportTableOfCriteria, Parameters: submissionId: {0}, "
        + "offers: {1}, type: {2}",
      new Object[]{submissionId, offers, type});

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(SHEET_TITLE);

      // remove excluded companies from print
      int maxRows = countTableRows(offers, type);
      int maxCols = countTableColumns(offers, type);

      Object[][] offerEntries = new Object[maxRows][maxCols];
      Object[][] readonly = new Object[maxRows][maxCols];

      Object[][] functionsBK = new Object[maxRows][maxCols];
      // usage description
      Object[][] totalBK = new Object[maxRows][maxCols];

      Object[][] rankFormulas = new Object[maxRows][maxCols];

      setreadonlyCells(maxRows, maxCols, readonly);
      setBaseCellWidhts(sheet, maxCols);

      // AWARD SUITABILITY
      if (type.equals(SUITABILITY)) {
        // Eignungsprüfungstabelle Qualifying Examination Table
        setQualifyStaticColumnTitltes(offers, offerEntries, submissionId, maxCols, sheet);
        // 2nd row dynamic column titles:
        setQualityDynamicColumnTitles(offers, offerEntries, sheet, maxCols);
        gatherTableData(offers, sheet, offerEntries, readonly, functionsBK, totalBK, rankFormulas);

      } else if (type.equals(AWARD)) {

        // 1rst row create Head titles:
        // AWARD
        // Zuschlagsbewertungstabelle Award evaluation table
        setAwardStaticColumnTitltes(offerEntries, submissionId, sheet, maxCols, offers);
        setAwardDynamicColumnTitles(offers, offerEntries, readonly, sheet);
        gatherAwardDTableData(offers, offerEntries, readonly, functionsBK, totalBK, rankFormulas);

      }

      fillTableCells(sheet, offerEntries);
      // Add Formulas
      addCalculationFormulas(sheet, functionsBK, type);
      // Totale Punkte calculation
      addTotalPunkteCalculation(sheet, totalBK);

      // look n feel
      setReadonly(sheet, readonly, workbook, type, offers.size());
      setEditablesStyle(sheet, readonly, workbook);
      setHeadingsStyle(sheet, workbook, type, maxCols);
      setTitlesStyle(sheet, workbook, type, offers.size(), maxCols);
      addRankingFormula(sheet, rankFormulas, offers.size(), workbook, type, maxCols);

      for (int i = DATA_STARTING_ROWNUM; i < offers.size() + DATA_STARTING_ROWNUM; i++) {
        // Set row size (height) for submittent information.
        autosizeRow(i, sheet, 2, SUBMITTENT_INFORMATION_LENGTH);
        if (type.equals(SUITABILITY)) {
          // Set row size (height) for submittent suitability notes.
          autosizeRowFromColumn(i, sheet, maxCols, maxCols, SUITABILITY_NOTES_LENGTH);
        }
      }

      if (type.equals(SUITABILITY)) {
        // Set row size (height) for criterion/subcriterion titles.
        autosizeTitlesRow(TITLE_ROWNUM, 3, sheet, SUITABILITY_CRITERION_TEXT_LENGTH, offers.get(0),
            type);
        // Set default size (height) for the grades, points and total points row.
        sheet.getRow(TITLE_ROWNUM + 1).setHeightInPoints((float) 26.25);
      } else if (type.equals(AWARD)) {
        // Set row size (height) for criterion/subcriterion titles.
        autosizeTitlesRow(AWARD_TITLE_ROWNUM, 5, sheet, AWARD_CRITERION_TEXT_LENGTH, offers.get(0),
            type);
      }

      sheet.setDisplayGridlines(false);
      // Hides UUID column..
      sheet.setColumnHidden(0, true);
      sheet.protectSheet(sDService.getMasterListHistoryByCode(ShortCode.S14.toString()).getValue2());

      workbook.lockRevision();
      workbook.lockStructure();

      workbook.write(bos);
      bos.close();

      submissionService.lockSubmission(submissionId, type, Boolean.TRUE);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
    return bos.toByteArray();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.ImportExportFileService#exportTableOfCriteriaAsEml(
   * java.lang.String, java.util.List, java.lang.String)
   */
  @Override
  public byte[] exportTableOfCriteriaAsEml(String submissionId, List<OfferDTO> offerCriterionDTOs,
    String type) {

    String subject = " ";
    String body = " ";
    Message message = new MimeMessage(Session.getInstance(System.getProperties()));

    ByteArrayOutputStream emailbos = new ByteArrayOutputStream();

    try {
      byte[] exlExport = exportTableOfCriteria(submissionId, offerCriterionDTOs, type);

      message.setFrom(null);
      message.setRecipients(Message.RecipientType.TO, null);
      message.setSubject(subject);
      // "X-Unsent": 1"
      message.setHeader("X-Unsent", "1");

      // create the message part
      MimeBodyPart content = new MimeBodyPart();
      // fill message
      content.setText(body);

      /**
       * Linux issue with: no object DCH for MIME type multipart/mixed; Dont remove:
       */
      Thread.currentThread().setContextClassLoader(javax.mail.Session.class.getClassLoader());
      MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
      mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
      mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
      mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap(
        "message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
      CommandMap.setDefaultCommandMap(mc);

      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(content);
      ByteArrayDataSource bds = new ByteArrayDataSource(exlExport, "application/octet-stream");
      bds.setName(generateExportedFilename(type, Boolean.FALSE));
      content.setDataHandler(new DataHandler(bds));
      content.setFileName(bds.getName());
      message.setContent(multipart);
      message.writeTo(emailbos);
      emailbos.close();

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.toString());
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return emailbos.toByteArray();
  }

  /**
   * Sets the headings style.
   *
   * @param sheet the sheet
   * @param workbook the workbook
   * @param type the type
   * @param maxCols the max cols
   */
  private void setHeadingsStyle(XSSFSheet sheet, XSSFWorkbook workbook, String type, int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method setHeadingsStyle, Parameters: sheet: {0}, "
        + "workbook: {1}, type: {2}, maxCols: {3}",
      new Object[]{sheet, workbook, type, maxCols});

    int minPageCols = 13;

    // headings and titles are already readonly.
    // titleStyle
    CellStyle titleStyle = workbook.createCellStyle();
    titleStyle.setLocked(true);
    titleStyle.setAlignment(HorizontalAlignment.LEFT);
    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);
    titleFont.setBold(true);
    titleFont.setColor(IndexedColors.WHITE.index);
    titleStyle.setFont(titleFont);
    titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);

    CellStyle titleStyleRight = workbook.createCellStyle();
    titleStyleRight.cloneStyleFrom(titleStyle);
    titleStyleRight.setAlignment(HorizontalAlignment.RIGHT);

    // subtitleStyle
    CellStyle subtitleStyle = workbook.createCellStyle();
    subtitleStyle.cloneStyleFrom(titleStyle);
    XSSFFont subtitleFont = workbook.createFont();
    subtitleFont.setFontName(FONT);
    subtitleFont.setBold(true);
    subtitleFont.setFontHeight(FONT_SIZE);
    subtitleFont.setColor(IndexedColors.WHITE.index);
    subtitleStyle.setFont(subtitleFont);
    subtitleStyle.setFillForegroundColor(IndexedColors.RED.index);

    // headings style
    CellStyle headingStyle = workbook.createCellStyle();
    headingStyle.setLocked(true);
    headingStyle.setAlignment(HorizontalAlignment.LEFT);
    XSSFFont headingsfont = workbook.createFont();
    headingsfont.setFontHeight(FONT_SIZE);
    headingsfont.setFontName(FONT);
    headingStyle.setFont(headingsfont);

    CellStyle headingStyleBold = workbook.createCellStyle();
    headingStyleBold.cloneStyleFrom(headingStyle);
    XSSFFont headingsBold = workbook.createFont();
    headingsBold.setFontHeight(FONT_SIZE);
    headingsBold.setFontName(FONT);
    headingsBold.setBold(true);
    headingStyleBold.setFont(headingsBold);

    // headingStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    // headingStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.index);

    int rowNum = HEADING_ROWNUM;
    int lastHeadingRow = 0;

    // headings:
    if (type.equals(SUITABILITY)) {
      lastHeadingRow = TITLE_ROWNUM;
      autosizeRow(DATA_STARTING_ROWNUM - 1, sheet, maxCols);

    } else if (type.equals(AWARD)) {
      lastHeadingRow = AWARD_TITLE_ROWNUM;
      autosizeRow(DATA_STARTING_ROWNUM, sheet, maxCols);

    }

    if (maxCols < minPageCols) {
      maxCols = minPageCols;
    }

    for (int i = rowNum; i < lastHeadingRow; i++) {
      Row row = sheet.getRow(rowNum++);
      for (int j = 0; j < maxCols; j++) {
        Cell cell;
        if (row.getCell(j) != null) {
          cell = row.getCell(j);
        } else {
          cell = row.createCell(j);
        }
        if (rowNum == (HEADING_ROWNUM + 1)) {
          if (j < (maxCols - 1)) {
            cell.setCellStyle(titleStyle);
          } else {
            setValueToField(SubmissConverter.convertToSwissDate(new Date()), cell);
            cell.setCellStyle(titleStyleRight);
          }
        } else if (rowNum == (HEADING_ROWNUM + 2)) {
          cell.setCellStyle(subtitleStyle);
        } else if (rowNum == (HEADING_ROWNUM + 3)) {
          cell.setCellStyle(headingStyleBold);
        } else {
          cell.setCellStyle(headingStyle);
        }
      }
    }
  }

  /**
   * Sets the titles style.
   *
   * @param sheet the sheet
   * @param workbook the workbook
   * @param type the type
   * @param offersNum the offers num
   * @param maxCols the max cols
   */
  private void setTitlesStyle(XSSFSheet sheet, XSSFWorkbook workbook, String type, int offersNum,
    int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method setTitlesStyle, Parameters: sheet: {0}, "
        + "workbook: {1}, type: {2}, maxCols: {3}, offersNum: {4}",
      new Object[]{sheet, workbook, type, maxCols, offersNum});

    // headings and titles are already readonly.
    int rowNum = 0;
    int lastTitleRow = 0;
    int submittentenRowNum = 0;
    int colNum = 1;

    // Title styles
    CellStyle titleStyleBold = workbook.createCellStyle();
    titleStyleBold.setLocked(true);
    titleStyleBold.setAlignment(HorizontalAlignment.LEFT);
    XSSFFont titlefontBold = workbook.createFont();
    titlefontBold.setFontName(FONT);
    titlefontBold.setFontHeight(FONT_SIZE);
    titlefontBold.setBold(true);
    titleStyleBold.setFont(titlefontBold);
    // titleStyleBold.setWrapText(true);
    CellStyle titleStyle = workbook.createCellStyle();
    titleStyle.cloneStyleFrom(titleStyleBold);

    XSSFFont titlefont = workbook.createFont();
    titlefont.setFontName(FONT);

    titlefont.setFontHeight(FONT_SIZE);
    titlefont.setBold(true);
    titleStyle.setFont(titlefont);
    titleStyle.setBorderRight(BorderStyle.THIN);
    titleStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    titleStyle.setBorderLeft(BorderStyle.THIN);
    titleStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    titleStyle.setBorderTop(BorderStyle.THIN);
    titleStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    titleStyle.setBorderBottom(BorderStyle.THIN);
    titleStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    titleStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
    // titleStyle.setWrapText(true);
    // titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    // titleStyle.setFillForegroundColor(IndexedColors.ORCHID.index);
    // titleStyleBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    // titleStyleBold.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);

    if (type.equals(SUITABILITY)) {
      rowNum = TITLE_ROWNUM - 1;
      lastTitleRow = DATA_STARTING_ROWNUM;
      submittentenRowNum = DATA_STARTING_ROWNUM - 1;

    } else if (type.equals(AWARD)) {
      rowNum = AWARD_TITLE_ROWNUM - 1;
      lastTitleRow = DATA_STARTING_ROWNUM + 1;
      submittentenRowNum = DATA_STARTING_ROWNUM;
    }

    for (int i = rowNum; i < lastTitleRow; i++) {
      Row row = sheet.getRow(i);
      for (int j = 0; j < maxCols; j++) {
        Cell cell = row.getCell(j);
        // first cell is empty, skip it:
        if ((j == 1) && (i == (rowNum + 1))) {
          cell.setCellStyle(null);
          continue;
        } else if ((i == TITLE_ROWNUM - 1) && (type.equals(SUITABILITY))
          || (i == AWARD_TITLE_ROWNUM - 1) && (type.equals(AWARD))) {
          cell.setCellStyle(titleStyleBold);
          continue;
        }
        cell.setCellStyle(titleStyle);
      }
    }

    // Implementing functionality to wrap the criterion texts (titles). Also implementing
    // functionality to wrap the total points title.
    CellStyle wrappedTitleStyle = workbook.createCellStyle();
    wrappedTitleStyle.cloneStyleFrom(titleStyle);
    wrappedTitleStyle.setWrapText(true);
    for (int i = 1; i < 3; i++) {
      Row titlesRow = sheet.getRow(lastTitleRow - i);
      for (int j = 2; j < maxCols; j++) {
        Cell cell = titlesRow.getCell(j);
        cell.setCellStyle(wrappedTitleStyle);
      }
    }

    // Submittenten
    // +1 because of "Submittenten" title
    for (int i = submittentenRowNum; i < (submittentenRowNum + offersNum + 1); i++) {
      Row row = sheet.getRow(i);
      Cell cell = row.getCell(colNum);
      CellStyle rowTitleStyle = workbook.createCellStyle();
      rowTitleStyle.cloneStyleFrom(titleStyle);
      rowTitleStyle.setAlignment(HorizontalAlignment.LEFT);
      rowTitleStyle.setWrapText(true);
      cell.setCellStyle(rowTitleStyle);
    }
  }

  /**
   * Removes the excluded offers.
   *
   * @param offers the offers
   */
  private int removeExcludedOffers(List<OfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method removeExcludedOffers, Parameters: offers: {0}",
      offers);

    List<OfferDTO> removeoffers = new ArrayList<>();
    for (OfferDTO offer : offers) {
      if ((Boolean.TRUE.equals(offer.getIsExcludedFromAwardProcess()))
        || (Boolean.TRUE.equals(offer.getIsExcludedFromProcess()))) {
        removeoffers.add(offer);
      }
    }
    offers.removeAll(removeoffers);
    return removeoffers.size();
  }

  /**
   * Setreadonly cells.
   *
   * @param maxRows the max rows
   * @param maxCols the max cols
   * @param readonly the readonly
   */
  private void setreadonlyCells(int maxRows, int maxCols, Object[][] readonly) {
    for (int i = 0; i < maxRows; i++) {
      for (int j = 0; j < maxCols; j++) {
        readonly[i][j] = true;
      }
    }
  }

  /**
   * Sets the editables style.
   *
   * @param sheet the sheet
   * @param readonly the readonly
   * @param workbook the workbook
   */
  private void setEditablesStyle(XSSFSheet sheet, Object[][] readonly, XSSFWorkbook workbook) {

    LOGGER.log(Level.CONFIG,
      "Executing method setEditablesStyle, Parameters: sheet: {0}, readonly: {1}, workbook: {2}",
      new Object[]{sheet, readonly, workbook});

    int rowNum = 0;

    // editableStyle
    CellStyle editableStyle = workbook.createCellStyle();
    editableStyle.setLocked(false);
    editableStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.index);
    editableStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    editableStyle.setBorderRight(BorderStyle.THIN);
    editableStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    editableStyle.setBorderLeft(BorderStyle.THIN);
    editableStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    editableStyle.setBorderTop(BorderStyle.THIN);
    editableStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    editableStyle.setBorderBottom(BorderStyle.THIN);
    editableStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    editableStyle.setAlignment(HorizontalAlignment.LEFT);

    XSSFFont editFont = workbook.createFont();
    editFont.setFontName(FONT);
    editFont.setFontHeight(FONT_SIZE);
    editableStyle.setWrapText(true);
    editableStyle.setFont(editFont);
    editableStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

    // defaultStyle.setAlignment(HorizontalAlignment.CENTER);
    // editableStyle.setWrapText(true);

    for (Object[] offerEntry : readonly) {
      Row row = sheet.getRow(rowNum++);
      int colNum = 0;
      for (Object field : offerEntry) {
        Cell cell = row.getCell(colNum++);
        if (!(boolean) field) {
          cell.setCellStyle(editableStyle);
        }
      }
    }
  }

  /**
   * Adds the ranking formula.
   *
   * @param sheet the sheet
   * @param rankFormulas the rank formulas
   * @param offersSize the offers size
   * @param workbook the workbook
   * @param type the type
   * @param maxCols the max cols
   */
  private void addRankingFormula(XSSFSheet sheet, Object[][] rankFormulas, int offersSize,
    XSSFWorkbook workbook, String type, int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method addRankingFormula, Parameters: sheet: {0}, rankFormulas: {1}, "
        + "offersSize: {2}, type: {3}, maxCols: {4}, workbook: {5}",
      new Object[]{sheet, rankFormulas, offersSize, type, maxCols, workbook});

    int rankWidth = 7 * CARACTER_WIDTH;
    int rowNum = 0;

    for (Object[] function : rankFormulas) {
      Row row = sheet.getRow(rowNum++);
      int colNum = 0;
      for (Object field : function) {

        Cell cell = row.getCell(colNum++);
        if ((field instanceof Boolean) && (field.equals(true))) {
          cell.setCellFormula(getOfferRankFormula(rowNum, sheet, offersSize, maxCols, type));
          CellStyle style = workbook.createCellStyle();
          DataFormat format = workbook.createDataFormat();
          style.setDataFormat(format.getFormat("#"));
          style.setBorderRight(BorderStyle.THIN);
          style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
          style.setBorderLeft(BorderStyle.THIN);
          style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
          style.setBorderTop(BorderStyle.THIN);
          style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
          style.setBorderBottom(BorderStyle.THIN);
          style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
          style.setAlignment(HorizontalAlignment.LEFT);
          XSSFFont font = workbook.createFont();
          font.setFontName(FONT);
          font.setFontHeight(FONT_SIZE);
          style.setFont(font);
          cell.setCellStyle(style);
          sheet.setColumnWidth(cell.getColumnIndex(), rankWidth);
        }
      }
    }
  }

  /**
   * Sets static column names of the table.
   *
   * @param offers the offers
   * @param offerEntries the offer entries
   * @param submissionId the submission id
   * @param colsize the colsize
   */
  private void setQualifyStaticColumnTitltes(List<OfferDTO> offers, Object[][] offerEntries,
    String submissionId, int colsize, XSSFSheet sheet) {

    LOGGER.log(Level.CONFIG,
      "Executing method setQualifyStaticColumnTitltes, Parameters: offerEntries: {0}, "
        + "submissionId: {1}, offers: {2}, colsize: {3}, sheet: {4}",
      new Object[]{offerEntries, submissionId, offers, colsize, sheet});

    SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
    // int minDateCol = 13;

    offerEntries[HEADING_ROWNUM][1] = getSheetTitle(submission);
    // offerEntries[HEADING_ROWNUM][((colsize - 1) < minDateCol) ? minDateCol : (colsize - 1)] =
    // SubmissConverter.convertToSwissDate(new Date());

    // row 2:TYPE Zuschlagsbewertung/ Eignungsprüfung
    offerEntries[HEADING_ROWNUM + 1][1] = SUITABILITY_LABEL;
    // row 3: project name etc..
    offerEntries[HEADING_ROWNUM + 2][1] = templateBeanService.readTemplatesValues(submission);

    // count mussKriterium and bewerte
    int mussCnt = 0;
    int ratedCnt = 0;

    for (OfferCriterionDTO offer : offers.get(0).getOfferCriteria()) {
      if (offer.getCriterion().getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
        mussCnt++;
      } else if (offer.getCriterion().getCriterionType()
        .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        ratedCnt++;
      }
    }

    if (ratedCnt > 0) {
      offerEntries[HEADING_ROWNUM + 4][1] = MAX_SCORE
        + (submission.getMaxGrade() != null ? submission.getMaxGrade() : DEFAULT_MAXNOTE);
      offerEntries[HEADING_ROWNUM + 5][1] = MIN_SCORE
        + (submission.getMinGrade() != null ? submission.getMinGrade() : DEFAULT_MIN_NOTE);
    }

    // Muss Kriterium
    if (mussCnt > 0) {
      offerEntries[TITLE_ROWNUM - 1][DATA_STARTING_COLLNUM] = MUST_CRITERIA;
    }

    // Bewertetes Kriterium
    if (ratedCnt > 0) {
      offerEntries[TITLE_ROWNUM - 1][DATA_STARTING_COLLNUM + mussCnt] = RATED_CRITERIA;
    }

    offerEntries[DATA_STARTING_ROWNUM - 1][1] = PROPOSERS;
    // Hidden.
    offerEntries[HEADING_ROWNUM][0] = "usubmissionid & uuids";
    offerEntries[1][0] = submissionId;
  }


  /**
   * Sets static column names of the table.
   *
   * @param offerEntries the offer entries
   * @param submissionId the submission id
   * @param sheet the sheet
   * @param colSize the col size
   */
  private void setAwardStaticColumnTitltes(Object[][] offerEntries, String submissionId,
    XSSFSheet sheet, int colSize, List<OfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method setAwardStaticColumnTitltes, Parameters: offerEntries: {0}, "
        + "submissionId: {1}, sheet: {2}, colsize: {3}, offers: {4}",
      new Object[]{offerEntries, submissionId, sheet, colSize, offers});

    int awardsHeadingsrow = DATA_STARTING_ROWNUM - 1;
    int netPriceWidth = 12 * CARACTER_WIDTH;
    // int minDateCol = 13;

    // Hidden:
    offerEntries[HEADING_ROWNUM][0] = "usubmissionid & uuids";
    offerEntries[1][0] = submissionId;

    SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
    // row 1: Submission title + date
    offerEntries[HEADING_ROWNUM][1] = getSheetTitle(submission);

    // offerEntries[HEADING_ROWNUM][((colSize - 1) < minDateCol) ? minDateCol : (colSize - 1)] =
    // SubmissConverter.convertToSwissDate(new Date());

    // row 2:TYPE Zuschlagsbewertung/ Eignungsprüfung
    offerEntries[HEADING_ROWNUM + 1][1] = AWARD_LABEL;
    // row 3: project name etc..
    offerEntries[HEADING_ROWNUM + 2][1] = templateBeanService.readTemplatesValues(submission);

    // row 4: formel preise:
    offerEntries[HEADING_ROWNUM + 3][1] = getFormelPreis(submission);
    // row 5 formel betrib. :
    offerEntries[HEADING_ROWNUM + 4][1] = getOperatingCostFormula(submission, offers);
    // row 6 max
    offerEntries[HEADING_ROWNUM + 5][1] = MAX_SCORE
      + (submission.getAwardMaxGrade() != null ? submission.getAwardMaxGrade() : DEFAULT_MAXNOTE);

    offerEntries[HEADING_ROWNUM + 6][1] =
      MIN_SCORE + (submission.getAwardMinGrade() != null ? submission.getAwardMinGrade()
        : DEFAULT_MIN_NOTE_AWARD);

    // table headings:
    offerEntries[DATA_STARTING_ROWNUM][1] = PROPOSERS;
    offerEntries[awardsHeadingsrow][2] = RANK;
    offerEntries[awardsHeadingsrow][3] = TOTAL_POINTS;
    offerEntries[awardsHeadingsrow][4] = NET_PRICE;
    sheet.setColumnWidth(4, (netPriceWidth));
  }


  /**
   * Gets the sheet title.
   *
   * @param submission the submission
   * @return the sheet title
   */
  private String getSheetTitle(SubmissionDTO submission) {

    ProjectDTO project = submissionService.getSubmissionById(submission.getId()).getProject();
    String tenant = project.getTenant().getName();
    if (tenant != null && tenant.equals(DocumentProperties.TENANT_EWB.getValue())) {
      return EWB_TITLE;
    } else {
      return BERN_TITLE;
    }
  }


  /**
   * Sets the base cell widhts.
   *
   * @param sheet the sheet
   * @param totalCols the total cols
   */
  private void setBaseCellWidhts(XSSFSheet sheet, int totalCols) {
    // the width in units of 1/256th of a character width
    int cellWidth = 30 * CARACTER_WIDTH;
    int numWidth = 10 * CARACTER_WIDTH;

    sheet.setColumnWidth(1, cellWidth);
    sheet.setColumnWidth(2, (5 * 256));

    for (int i = 4; i < totalCols; i++) {
      sheet.setColumnWidth(2, (numWidth));
    }
  }


  /**
   * Gets the selected Formel Preis.
   *
   * @param sub the sub
   * @return the formel preis
   */
  private String getFormelPreis(SubmissionDTO sub) {

    LOGGER.log(Level.CONFIG,
      "Executing method getFormelPreis, Parameters: sub: {0}",
      sub);

    MasterListValueHistoryDTO ms = sub.getPriceFormula();
    if (sub.getPriceFormula().isActive()) {
      if (ms.getValue1() != null && ms.getValue2() != null) {
        return FORMEL_PREIS + ms.getValue1() + ", " + ms.getValue2();
      }
    } else if (sub.getCustomPriceFormula() != null && !sub.getCustomPriceFormula().isEmpty()) {
      return FORMEL_PREIS + sub.getCustomPriceFormula();
    }

    // default, when formel is not set.
    String tenantId = sub.getWorkType().getTenant().getId();
    List<MasterListValueHistoryDTO> formulas =
      sDService.masterListValueHistoryQuery(CategorySD.CALCULATION_FORMULA, tenantId);

    return FORMEL_PREIS + formulas.get(1).getValue1() + ", " + formulas.get(1).getValue2();
  }

  /**
   * Gets the operating cost formula.
   *
   * @param sub the sub
   * @return the operating cost formula
   */
  private String getOperatingCostFormula(SubmissionDTO sub, List<OfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOperatingCostFormula, Parameters: sub: {0}, offers: {1}",
      new Object[]{sub, offers});

    // Formel Betriebskosten
    // if there are no betribekosten values we dont print the formel:
    if (hasBetriebskosten(offers.get(0))) {

      MasterListValueHistoryDTO ms = sub.getOperatingCostFormula();
      if ((ms.getValue1() != null) && (ms.getValue2() != null)) {
        return FORMEL_BETRIEBSKOSTEN + ms.getValue1() + ", " + ms.getValue2();
      } else if (sub.getCustomOperatingCostFormula() != null
        && !sub.getCustomOperatingCostFormula().isEmpty()) {
        return FORMEL_BETRIEBSKOSTEN + sub.getCustomOperatingCostFormula();
      }

      String tenantId = sub.getWorkType().getTenant().getId();
      List<MasterListValueHistoryDTO> formulas =
        sDService.masterListValueHistoryQuery(CategorySD.CALCULATION_FORMULA, tenantId);

      return FORMEL_BETRIEBSKOSTEN + formulas.get(1).getValue1() + ", "
        + formulas.get(1).getValue2();
    }
    return "";
  }

  private boolean hasBetriebskosten(OfferDTO offer) {
    boolean hasbetribs = false;
    for (OfferCriterionDTO of : offer.getOfferCriteria()) {
      if (of.getCriterion().getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        hasbetribs = true;
      }
    }
    return hasbetribs;
  }

  /**
   * Sets read only cells of the table.
   *
   * @param sheet the sheet
   * @param readonly the readonly
   * @param workbook the workbook
   * @param type the type
   * @param offerNum the offer num
   */
  private void setReadonly(XSSFSheet sheet, Object[][] readonly, XSSFWorkbook workbook, String type,
    int offerNum) {

    LOGGER.log(Level.CONFIG,
      "Executing method setReadonly, Parameters: sheet: {0}, "
        + "readonly: {1}, workbook: {2}, type: {3}, offerNum: {4}",
      new Object[]{sheet, readonly, workbook, type, offerNum});

    // headings and titles are already readonly.
    int rowNum = 0;
    int tablerow = DATA_STARTING_ROWNUM;

    // Lock
    CellStyle lockedCellStyle = workbook.createCellStyle();
    lockedCellStyle.setLocked(true);

    lockedCellStyle.setBorderRight(BorderStyle.THIN);
    lockedCellStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    lockedCellStyle.setBorderLeft(BorderStyle.THIN);
    lockedCellStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    lockedCellStyle.setBorderTop(BorderStyle.THIN);
    lockedCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    lockedCellStyle.setBorderBottom(BorderStyle.THIN);
    lockedCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    XSSFFont lockFont = workbook.createFont();
    lockFont.setFontName(FONT);
    lockFont.setFontHeight(FONT_SIZE);
    lockedCellStyle.setFont(lockFont);
    lockedCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
    lockedCellStyle.setAlignment(HorizontalAlignment.LEFT);

    // lockedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    // lockedCellStyle.setFillForegroundColor(IndexedColors.GOLD.index);

    CellStyle defaultStyle = workbook.createCellStyle();
    defaultStyle.cloneStyleFrom(lockedCellStyle);
    defaultStyle.setDataFormat(workbook.createDataFormat().getFormat("0.000"));

    if (type.equals(AWARD)) {
      tablerow++;
    }

    for (Object[] offerEntry : readonly) {
      Row row = sheet.getRow(rowNum++);

      int colNum = 0;
      for (Object field : offerEntry) {
        Cell cell = row.getCell(colNum++);

        // was rowNum >= tablerow
        if (((rowNum > tablerow) && (rowNum < (tablerow + offerNum)))
          && (colNum < DATA_STARTING_COLLNUM)) {

          cell.setCellStyle(defaultStyle);

        } else if ((boolean) field.equals(true)) {

          if (isPunkteColumn(cell, sheet, type)) {
            cell.setCellStyle(defaultStyle);

            // Netto-Preis exkl. MWST format Netto-Preis is at col 5.
          } else if (type.equals(AWARD) && (colNum == 5)) {

            CellStyle nettoCellStyle = workbook.createCellStyle();
            nettoCellStyle.cloneStyleFrom(lockedCellStyle);
            nettoCellStyle.setDataFormat(workbook.createDataFormat().getFormat("#'##0.00"));

            cell.setCellStyle(nettoCellStyle);
          } else {
            cell.setCellStyle(lockedCellStyle);
          }
        }
      }
    }
  }

  /**
   * Checks if is punkte column.
   *
   * @param cell the cell
   * @param sheet the sheet
   * @param type the type
   * @return true, if is punkte column
   */
  private boolean isPunkteColumn(Cell cell, XSSFSheet sheet, String type) {

    // total punkte in Award resides -1 row.
    int awardTotalpunkteCol = 3;

    int titleRow =
      type.equals(AWARD) && (cell.getColumnIndex() != awardTotalpunkteCol) ? DATA_STARTING_ROWNUM
        : DATA_STARTING_ROWNUM - 1;

    Row row = sheet.getRow(titleRow);
    Cell titleCell = row.getCell(cell.getColumnIndex());
    DataFormatter formatter = new DataFormatter();
    String val = formatter.formatCellValue(titleCell);

    return val.equals(POINTS) || val.equals(TOTAL_POINTS);
  }


  /**
   * Adds the Total Punkte Calculation to the cells. At first it iterates and gather all Punkte
   * addresses per row and then it add them to the total punkte cell as Formula.
   *
   * @param sheet the sheet
   * @param totalBK the total BK
   */
  private void addTotalPunkteCalculation(XSSFSheet sheet, Object[][] totalBK) {

    LOGGER.log(Level.CONFIG,
      "Executing method addTotalPunkteCalculation, Parameters: sheet: {0}, "
        + "totalBK:{1}",
      new Object[]{sheet, totalBK});

    int rowNum;
    rowNum = 0;
    // gather all cells
    ArrayList<String> punkte;
    for (Object[] function : totalBK) {
      Row row = sheet.getRow(rowNum++);
      punkte = new ArrayList<>();
      int colNum = 0;
      /** Gather Punkte addresses */
      for (Object field : function) {
        Cell cell = row.getCell(colNum++);
        if ((field instanceof Boolean) && (field.equals(false))) {
          punkte.add(cell.getAddress().formatAsString());
        }
      }
      /** Add them to sum formula */
      colNum = 0;
      for (Object field : function) {
        Cell cell = row.getCell(colNum++);
        if ((field instanceof Boolean) && (field.equals(true))) {
          // Add array list based function to cell
          cell.setCellFormula(generateSumForumla(punkte));
        }
      }
    }
  }

  /**
   * Adds the Punkte Calculation to the cells.
   *
   * @param sheet the sheet
   * @param functionsBK the functions BK
   * @param type the type
   */
  private void addCalculationFormulas(XSSFSheet sheet, Object[][] functionsBK, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCalculationFormulas, Parameters: sheet: {0}, "
        + "functionsBK: {1}, type: {2}",
      new Object[]{sheet, functionsBK, type});

    int rowNum;
    rowNum = 0;
    for (Object[] function : functionsBK) {
      Row row = sheet.getRow(rowNum++);
      int colNum = 0;
      for (Object field : function) {
        Cell cell = row.getCell(colNum++);

        if (field instanceof Boolean) {
          if (field.equals(true)) {
            // Punkte calculation for UK or BK without UK .
            cell.setCellFormula(
              generatePunkteFormula(cell.getRowIndex(), cell.getColumnIndex(), sheet, type));
          } else {
            // Note calculation for BK with UK . f(r,c) = f(3,c-2)*f(r,c-1)
            cell.setCellFormula(generateNoteFormula(cell.getRowIndex(), cell.getColumnIndex(),
              (int) cell.getNumericCellValue(), sheet));
          }
        }
      }
    }
  }

  /**
   * Adds data from offerEntries to the table cells.
   *
   * @param sheet the sheet
   * @param offerEntries the offer entries
   */
  private void fillTableCells(XSSFSheet sheet, Object[][] offerEntries) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillTableCells, Parameters: sheet: {0}, offerEntries: {1}",
      new Object[]{sheet, offerEntries});

    int rowNum = 0;

    for (Object[] offerEntry : offerEntries) {
      Row row = sheet.createRow(rowNum++);
      int colNum = 0;
      for (Object field : offerEntry) {

        Cell cell = row.createCell(colNum);

        if (colNum == 0) {
          cell.getCellStyle().setHidden(true);
        }
        colNum++;
        setValueToField(field, cell);
      }
    }
  }

  /**
   * Casts Object value to cell value.
   *
   * @param field the field
   * @param cell the cell
   */
  private void setValueToField(Object field, Cell cell) {

    LOGGER.log(Level.CONFIG, "Executing method setValueToField, Parameters: "
        + "field: {0}, cell: {1}",
      new Object[]{field, cell});

    if (field != null) {
      if (field instanceof Integer) {
        cell.setCellValue((Integer) field);
      } else if (field instanceof Boolean) {
        cell.setCellValue((Boolean) field);
      } else if (field instanceof Double) {
        cell.setCellValue((Double) field);
      } else if (field instanceof BigDecimal) {
        cell.setCellValue(((BigDecimal) field).doubleValue());
      } else if (field instanceof String) {
        cell.setCellValue((String) field);
      } else {
        cell.setCellValue(field.toString());
      }
    }
  }

  /**
   * Crawls List<OfferDTO> offers and extracts all the necessary data .
   *
   * @param offers the offers
   * @param sheet the sheet
   * @param offerEntries the offer entries
   * @param readonly the readonly
   * @param functionsBK the functions BK
   * @param totalBK the total BK
   * @param rankFormulas the rank formulas
   */
  private void gatherTableData(List<OfferDTO> offers, XSSFSheet sheet, Object[][] offerEntries,
    Object[][] readonly, Object[][] functionsBK, Object[][] totalBK, Object[][] rankFormulas) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherTableData, Parameters: offers: {0}, sheet: {1}, "
        + "offerEntries: {2}, readonly: {3}, functionsBK: {4},  "
        + "totalBK: {5}, rankFormulas: {6}",
      new Object[]{offers, sheet, offerEntries, readonly, functionsBK, totalBK, rankFormulas});

    int rownum;
    int colnum;
    rownum = DATA_STARTING_ROWNUM;
    colnum = 0;
    int mussCriteria = 0;
    int beweCriteria = 0;

    // count muss and bw criteria to map what to expect.
    for (OfferCriterionDTO of : offers.get(0).getOfferCriteria()) {
      if (of.getCriterion().getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
        mussCriteria++;
      } else if (of.getCriterion().getCriterionType()
        .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        beweCriteria++;
      }
    }

    // Gathering Table data
    for (OfferDTO offer : offers) {
      // uuid HIDDEN
      offerEntries[rownum][colnum++] = offer.getId();

      // Offer title
      offerEntries[rownum][colnum++] = offer.getSubmittent().getSubmittentNameArgeSub();

      if (beweCriteria > 0) {
        // Rank
        rankFormulas[rownum][colnum++] = true;
      }

      if (mussCriteria > 0) {
        // Muss Kriterien
        colnum = gatherMussKritirien(sheet, offerEntries, rownum, colnum, offer, readonly,
          offers.size());
      }
      if (beweCriteria > 0) {

        // Bewertete Kriterien loop
        colnum = gatherBewerteteKriterien(offerEntries, readonly, functionsBK, totalBK, rownum,
          colnum, offer);
        totalBK[rownum][colnum] = true;// for sum
        offerEntries[rownum][colnum++] = "";// function will be placed here
      }

      // Bemerkung
      readonly[rownum][colnum] = false;
      offerEntries[rownum][colnum] = offer.getqExSuitabilityNotes();
      rownum++;
      colnum = 0;
    }
  }


  /**
   * if (Total punkte = 0 ) (short by Submittenten) else short Total punkte:
   *
   * RANK (TotalPuncte(i) , TotalPuncte(0): TotalPunkte(max)) RANK (tpCell , tprFirst: tplast)
   *
   * -1 to cast to xlxs cell position.
   *
   * @param rownum the rownum
   * @param sheet the sheet
   * @param offersize the offersize
   * @param maxCols the max cols
   * @param type the type
   * @return RANK Formula
   */
  private String getOfferRankFormula(int rownum, XSSFSheet sheet, int offersize, int maxCols,
    String type) {

    int totalPuncteCol = 0;
    int submittentenCol = 1;
    int dataStartingRownum = DATA_STARTING_ROWNUM;

    if (type.equals(AWARD)) {
      totalPuncteCol = 3;
      dataStartingRownum++;

    } else if (type.equals(SUITABILITY)) {
      totalPuncteCol = maxCols - 2;
    }
    String tpCell = sheet.getRow(rownum - 1).getCell(totalPuncteCol).getAddress().formatAsString();
    String tpFirst =
      sheet.getRow(dataStartingRownum).getCell(totalPuncteCol).getAddress().formatAsString();
    String tplast = sheet.getRow(dataStartingRownum + offersize - 1).getCell(totalPuncteCol)
      .getAddress().formatAsString();

    // Submittenten
    String subCell =
      sheet.getRow(rownum - 1).getCell(submittentenCol).getAddress().formatAsString();
    String subFirst =
      sheet.getRow(dataStartingRownum).getCell(submittentenCol).getAddress().formatAsString();
    String sublast = sheet.getRow(dataStartingRownum + offersize - 1).getCell(submittentenCol)
      .getAddress().formatAsString();

    return "IF(" + tpFirst + ":" + tplast + "=0 , COUNTIF(" + subFirst + ":" + sublast + ", \"<=\"&"
      + subCell + "), RANK(" + tpCell + ", " + tpFirst + ":" + tplast + ")+COUNTIFS("+ tpFirst + 
      ":"+ tplast +","+ tpCell + "," +subFirst + ":"+ sublast + ", \"<=\"&" + subCell +")-1)";
  }

  /**
   * Gather award D table data.
   *
   * @param offers the offers
   * @param offerEntries the offer entries
   * @param readonly the readonly
   * @param functionsBK the functions BK
   * @param totalBK the total BK
   * @param rankFormulas the rank formulas
   */
  private void gatherAwardDTableData(List<OfferDTO> offers, Object[][] offerEntries,
    Object[][] readonly, Object[][] functionsBK, Object[][] totalBK, Object[][] rankFormulas) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherAwardDTableData, Parameters: offers: {0}, "
        + "offerEntries: {1}, readonly: {2}, functionsBK: {3}, "
        + "totalBK: {4}, rankFormulas: {5}",
      new Object[]{offers, offerEntries, readonly, functionsBK, totalBK, rankFormulas});

    int rownum;
    int colnum;
    rownum = DATA_STARTING_ROWNUM + 1;
    colnum = 0;

    // Gathering Table data
    for (OfferDTO offer : offers) {
      // uuid HIDDEN
      offerEntries[rownum][colnum++] = offer.getId();

      // Offer title
      offerEntries[rownum][colnum++] = offer.getSubmittent().getSubmittentNameArgeSub();
      // Rank
      rankFormulas[rownum][colnum++] = true;

      // Totale Punkte
      totalBK[rownum][colnum] = true;
      colnum++;

      // Netto-Preis exkl. MWST
      offerEntries[rownum][colnum++] = offer.getAmount();

      // PREISE
      colnum = gatherPreisCritirien(offerEntries, totalBK, rownum, colnum, offer);
      // BETRIBEN
      colnum = gatherBetribsCritirien(offerEntries, totalBK, rownum, colnum, offer);

      // Zuschlag loop
      gatherZuschlagCritirien(offerEntries, readonly, functionsBK, totalBK, rownum, colnum, offer);
      rownum++;
      colnum = 0;
    }
  }

  /**
   * Finds all Zuschlag Criteria, calls add method.
   *
   * @param offerEntries the offer entries
   * @param readonly the readonly
   * @param functionsBK the functions BK
   * @param totalBK the total BK
   * @param rownum the rownum
   * @param colnum the colnum
   * @param offer the offer
   * @return the int
   */
  private int gatherZuschlagCritirien(Object[][] offerEntries, Object[][] readonly,
    Object[][] functionsBK, Object[][] totalBK, int rownum, int colnum, OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherZuschlagCritirien, Parameters:  offerEntries: {0}, "
        + "readonly: {1}, functionsBK: {2}, totalBK: {3}, rownum: {4}, "
        + "colnum: {5}, offer: {6}",
      new Object[]{offerEntries, readonly, functionsBK, totalBK, rownum, colnum, offer});

    for (OfferCriterionDTO offerCriteriondto : offer.getOfferCriteria()) {
      if (offerCriteriondto.getCriterion().getCriterionType()
        .equals(LookupValues.AWARD_CRITERION_TYPE)) {

        // Find the column for each zuschlag
        getZuschlagCritirienTitle(colnum, offerEntries);
        for (OfferCriterionDTO of : offer.getOfferCriteria()) {
          if (of.getCriterion().getId().equals(offerCriteriondto.getCriterion().getId())) {

            // G column is empty
            colnum++;
            // Note
            colnum = caclulateZuschlagNote(offerEntries, readonly, functionsBK, rownum, colnum, of);
            // Punkte
            functionsBK[rownum][colnum] = true;
            totalBK[rownum][colnum] = false;
            // Formula will be placed here:
            offerEntries[rownum][colnum++] = "";
            colnum = gatherZuschlagSubcriteria(offerEntries, readonly, functionsBK, rownum, colnum,
              offer, of);
          }
        }
      }
    }
    return colnum;
  }

  /**
   * Note, if there are no subcriteria, Note is editable.
   *
   * @param offerEntries the offer entries
   * @param readonly the readonly
   * @param functionsBK the functions BK
   * @param rownum the rownum
   * @param colnum the colnum
   * @param of the of
   * @return the int
   */
  private int caclulateZuschlagNote(Object[][] offerEntries, Object[][] readonly,
    Object[][] functionsBK, int rownum, int colnum, OfferCriterionDTO of) {

    LOGGER.log(Level.CONFIG,
      "Executing method caclulateZuschlagNote, Parameters: offerEntries: {0}, "
        + "readonly: {1}, functionsBK: {2}, rownum: {3}, colnum: {4}, of: {5}",
      new Object[]{offerEntries, readonly, functionsBK, rownum, colnum, of});

    if (of.getCriterion().getSubcriterion().isEmpty()) {
      readonly[rownum][colnum] = false;
      offerEntries[rownum][colnum++] = of.getGrade();
    } else {
      // Note = Sum of Uk(n)Punkte: will be calculated after Punkte calculation.
      offerEntries[rownum][colnum] = of.getCriterion().getSubcriterion().size();
      functionsBK[rownum][colnum++] = false;
    }
    return colnum;
  }

  /**
   * Gather zuschlag subcriteria.
   *
   * @param offerEntries the offer entries
   * @param readonly the readonly
   * @param functionsBK the functions BK
   * @param rownum the rownum
   * @param colnum the colnum
   * @param offer the offer
   * @param of the of
   * @return the int
   */
  private int gatherZuschlagSubcriteria(Object[][] offerEntries, Object[][] readonly,
    Object[][] functionsBK, int rownum, int colnum, OfferDTO offer, OfferCriterionDTO of) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherZuschlagSubcriteria, Parameters: offerEntries: {0}, "
        + "readonly: {1}, functionsBK: {2}, rownum: {3}, "
        + "colnum: {4}, offer: {5}, of: {6}",
      new Object[]{offerEntries, readonly, functionsBK, rownum, colnum, offer, of});

    for (SubcriterionDTO subcriterion : of.getCriterion().getSubcriterion()) {

      // G
      colnum++;

      // Note
      readonly[rownum][colnum] = false;
      offerEntries[rownum][colnum++] = getOfferCriterionGrade(offer, subcriterion);
      // Punkte
      functionsBK[rownum][colnum] = true;
      // Formula will be placed here
      offerEntries[rownum][colnum++] = "";
    }
    return colnum;
  }

  /**
   * Gets the zuschlag critirien title.
   *
   * @param colnum the colnum
   * @param offerEntries the offer entries
   * @return the zuschlag critirien title
   */
  private String getZuschlagCritirienTitle(int colnum, Object[][] offerEntries) {

    LOGGER.log(Level.CONFIG,
      "Executing method getZuschlagCritirienTitle, Parameters: colnum: {0}, "
        + "offerEntries: {1}",
      new Object[]{colnum, offerEntries});

    int titlecolumn = colnum + 1;
    return offerEntries[DATA_STARTING_ROWNUM - 1][titlecolumn].toString();
  }

  /**
   * Gets the zuschlag critirien title.
   *
   * @param col the col
   * @param importedSheet the imported sheet
   * @return the zuschlag critirien title
   */
  private String getZuschlagCritirienTitle(int col, XSSFSheet importedSheet) {

    LOGGER.log(Level.CONFIG,
      "Executing method getZuschlagCritirienTitle, Parameters: col: {0}, "
        + "importedSheet: {1}",
      new Object[]{col, importedSheet});

    int titlecolomn = col + 1;
    int titleRow = DATA_STARTING_ROWNUM - 1;
    Row row = importedSheet.getRow(titleRow);
    Cell cell = row.getCell(titlecolomn);

    return cell.getStringCellValue();
  }

  /**
   * Gather betribs critirien.
   *
   * @param offerEntries the offer entries
   * @param totalBK the total BK
   * @param rownum the rownum
   * @param colnum the colnum
   * @param offer the offer
   * @return the int
   */
  private int gatherBetribsCritirien(Object[][] offerEntries, Object[][] totalBK, int rownum,
    int colnum, OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherBetribsCritirien, Parameters: offerEntries: {0}, "
        + "totalBK: {1}, rownum: {2}, colnum: {3}, offer: {4}",
      new Object[]{offerEntries, totalBK, rownum, colnum, offer});

    for (OfferCriterionDTO of : offer.getOfferCriteria()) {
      if (of.getCriterion().getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {

        // G
        colnum++;
        // Readonly Note
        offerEntries[rownum][colnum++] = of.getGrade();
        // Readonly Punkte
        totalBK[rownum][colnum] = false;
        offerEntries[rownum][colnum++] = of.getScore();
      }
    }
    return colnum;
  }

  /**
   * Gather preis critirien.
   *
   * @param offerEntries the offer entries
   * @param totalBK the total BK
   * @param rownum the rownum
   * @param colnum the colnum
   * @param offer the offer
   * @return the int
   */
  private int gatherPreisCritirien(Object[][] offerEntries, Object[][] totalBK, int rownum,
    int colnum, OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherPreisCritirien, Parameters: offerEntries: {0}, "
        + "totalBK: {1}, "
        + "rownum: {2}, "
        + "colnum: {3}, "
        + "offer: {4}",
      new Object[]{offerEntries, totalBK, rownum, colnum, offer});

    for (OfferCriterionDTO of : offer.getOfferCriteria()) {
      if (of.getCriterion().getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
        // G
        colnum++;

        // Readonly Note
        offerEntries[rownum][colnum++] = of.getGrade();
        // Readonly Punkte
        totalBK[rownum][colnum] = false;
        offerEntries[rownum][colnum++] = of.getScore();
      }
    }
    return colnum;
  }

  /**
   * Extracts all the data related with Bewertete Kriterien.
   *
   * @param offerEntries the offer entries
   * @param readonly the readonly
   * @param functionsBK the functions BK
   * @param totalBK the total BK
   * @param rownum the rownum
   * @param colnum the colnum
   * @param offer the offer
   * @return the int
   */
  private int gatherBewerteteKriterien(Object[][] offerEntries, Object[][] readonly,
    Object[][] functionsBK, Object[][] totalBK, int rownum, int colnum, OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherBewerteteKriterien, Parameters: offerEntries: {0}, "
        + "readonly: {1}, "
        + "functionsBK: {2}, "
        + "totalBK: {3}, "
        + "rownum: {4}, "
        + "colnum: {5}, "
        + "offer: {6}",
      new Object[]{offerEntries, readonly, functionsBK, totalBK, rownum, colnum, offer});

    for (OfferCriterionDTO offerCriterionDTO : offer.getOfferCriteria()) {
      if (offerCriterionDTO.getCriterion().getCriterionType()
        .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {

        colnum++;// G column is empty
        colnum = caclulateZuschlagNote(offerEntries, readonly, functionsBK, rownum, colnum,
          offerCriterionDTO);

        // Punkte
        functionsBK[rownum][colnum] = true;
        totalBK[rownum][colnum] = false;
        offerEntries[rownum][colnum++] = ""; // formula will be placed here.

        colnum = gatherZuschlagSubcriteria(offerEntries, readonly, functionsBK, rownum, colnum,
          offer, offerCriterionDTO);
      }
    }
    return colnum;
  }

  /**
   * Extracts all the data related with Muss Kriterien.
   *
   * @param sheet the sheet
   * @param offerEntries the offer entries
   * @param rownum the rownum
   * @param colnum the colnum
   * @param offer the offer
   * @param readonly the readonly
   * @param offersSize the offers size
   * @return the int
   */
  private int gatherMussKritirien(XSSFSheet sheet, Object[][] offerEntries, int rownum, int colnum,
    OfferDTO offer, Object[][] readonly, int offersSize) {

    LOGGER.log(Level.CONFIG,
      "Executing method gatherMussKritirien, Parameters: sheet: {0}, " + sheet
        + "offerEntries: {1}, "
        + "rownum: {2}, "
        + "colnum: {3}, "
        + "offer: {4}, "
        + "readonly: {5}",
      new Object[]{offerEntries, rownum, colnum, offer, readonly});

    int musscriteria = 0;
    for (OfferCriterionDTO of : offer.getOfferCriteria()) {

      if (of.getCriterion().getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
        readonly[rownum][colnum] = false;
        offerEntries[rownum][colnum++] = castToFullfiled(of.getIsFulfilled());
        musscriteria++;
      }
    }

    if (musscriteria == 0) {
      return colnum;
    }

    CellRangeAddressList addressList = new CellRangeAddressList(DATA_STARTING_ROWNUM,
      DATA_STARTING_ROWNUM + offersSize, colnum - musscriteria, colnum - 1);
    sheet.addValidationData(createValidation(addressList, sheet, FULLFILLED, NOTFULLFILLED));
    return colnum;
  }

  /**
   * Sets the Muss Kriterien and Bewertete Kriterien columns titles.
   *
   * @param offers the offers
   * @param offerEntries the offer entries
   * @param sheet the sheet
   */
  private void setQualityDynamicColumnTitles(List<OfferDTO> offers, Object[][] offerEntries,
    XSSFSheet sheet, int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method setQualityDynamicColumnTitles, Parameters: offerEntries: {0}, "
        + "sheet: {1}, offers: {2}, maxCols: {3}",
      new Object[]{offerEntries, sheet, offers, maxCols});

    // loop criteria
    // gather Muss Kriterien and Bewertete Kriterien
    ArrayList<CriterionDTO> mussCritiria = new ArrayList<>();
    ArrayList<CriterionDTO> beweCritiria = new ArrayList<>();

    int mussCritWidth = 17 * CARACTER_WIDTH;

    int gWidth = 7 * CARACTER_WIDTH;
    int descrWidth = 50 * CARACTER_WIDTH;
    // Dynamic starting column is the third:
    int titleRow = DATA_STARTING_ROWNUM - 2;
    int headingRow = DATA_STARTING_ROWNUM - 1;
    // Dynamic starting column is the third:

    for (OfferCriterionDTO of : offers.get(0).getOfferCriteria()) {
      if (of.getCriterion().getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
        mussCritiria.add(of.getCriterion());
      } else if (of.getCriterion().getCriterionType()
        .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        beweCritiria.add(of.getCriterion());
      }
    }

    int colnum = DATA_STARTING_COLLNUM;

    if (!beweCritiria.isEmpty()) {
      offerEntries[titleRow][2] = RANK;
    } else {
      colnum--;
    }

    // loop separately in order to keep Muss Kriterien first.
    for (CriterionDTO crit : mussCritiria) {
      sheet.setColumnWidth(colnum, mussCritWidth);
      offerEntries[titleRow][colnum++] = crit.getCriterionText();
    }

    for (CriterionDTO crit : beweCritiria) {

      sheet.setColumnWidth(colnum, gWidth);
      offerEntries[titleRow][colnum] = GEWICHTUNG;
      offerEntries[headingRow][colnum++] = crit.getWeighting();

      offerEntries[titleRow][colnum] = crit.getCriterionText();
      mergeCell(sheet, titleRow, colnum);

      offerEntries[headingRow][colnum++] = GRADE;
      // Punkte
      offerEntries[headingRow][colnum++] = POINTS;

      // for each Bewertete Kriterien gather the subcriteria titles..
      for (SubcriterionDTO subCrit : crit.getSubcriterion()) {

        sheet.setColumnWidth(colnum, gWidth);
        offerEntries[titleRow][colnum] = GEWICHTUNG;
        offerEntries[headingRow][colnum++] = subCrit.getWeighting();

        offerEntries[titleRow][colnum] = subCrit.getSubcriterionText();
        mergeCell(sheet, titleRow, colnum);

        offerEntries[headingRow][colnum++] = GRADE;
        offerEntries[headingRow][colnum++] = POINTS;
      }
    }

    // Sum of punkte, only if there are Bewerte entries.
    if (!beweCritiria.isEmpty()) {
      offerEntries[headingRow][colnum++] = TOTAL_POINTS;
    }
    // end of line 3. end of headings..

    sheet.setColumnWidth(colnum, descrWidth);
    offerEntries[headingRow][colnum] = COMMENT;
  }


  /**
   * Merge cell.
   *
   * @param sheet the sheet
   * @param titleRow the title row
   * @param colnum the colnum
   */
  private void mergeCell(XSSFSheet sheet, int titleRow, int colnum) {

    sheet.addMergedRegion(new CellRangeAddress(titleRow, titleRow, colnum, colnum + 1));
    sheet.autoSizeColumn(colnum);
  }


  /**
   * Sets the Muss Kriterien and Bewertete Kriterien columns titles.
   *
   * @param offers the offers
   * @param offerEntries the offer entries
   * @param readonly the readonly
   * @param sheet the sheet
   */
  private void setAwardDynamicColumnTitles(List<OfferDTO> offers, Object[][] offerEntries,
    Object[][] readonly, XSSFSheet sheet) {

    LOGGER.log(Level.CONFIG,
      "Executing method setAwardDynamicColumnTitles, Parameters: offerEntries: {0}, "
        + "readonly: {1}, sheet: {2}, offers: {3}",
      new Object[]{offerEntries, readonly, sheet, offers});

    // Dynamic starting column is the third:
    int colnum = DATA_STARTING_COLLNUM + 2;
    int titleRow = DATA_STARTING_ROWNUM - 1;
    int headingRow = DATA_STARTING_ROWNUM;
    int gWidth = 7 * CARACTER_WIDTH;

    ArrayList<CriterionDTO> priceCritiria = new ArrayList<>();
    ArrayList<CriterionDTO> operatingCritiria = new ArrayList<>();
    ArrayList<CriterionDTO> surChargeCritiria = new ArrayList<>();

    for (OfferCriterionDTO of : offers.get(0).getOfferCriteria()) {
      // Preis
      if (of.getCriterion().getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
        priceCritiria.add(of.getCriterion());

        // Betriebskosten
      } else if (of.getCriterion().getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        operatingCritiria.add(of.getCriterion());

        // Zuschlag
      } else if (of.getCriterion().getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)) {
        surChargeCritiria.add(of.getCriterion());
      }
    }

    // loop price Critiria first.
    for (CriterionDTO crit : priceCritiria) {
      offerEntries[titleRow][colnum] = GEWICHTUNG;
      sheet.setColumnWidth(colnum, gWidth);
      offerEntries[headingRow][colnum++] = crit.getWeighting();
      // price label
      offerEntries[titleRow][colnum] = crit.getCriterionText();
      mergeCell(sheet, titleRow, colnum);

      offerEntries[headingRow][colnum++] = GRADE;

      // Punkte
      offerEntries[headingRow][colnum++] = POINTS;
    }

    // Betriebskosten
    for (CriterionDTO crit : operatingCritiria) {
      offerEntries[titleRow][colnum] = GEWICHTUNG;
      sheet.setColumnWidth(colnum, gWidth);
      offerEntries[headingRow][colnum++] = crit.getWeighting();

      offerEntries[titleRow][colnum] = crit.getCriterionText();
      mergeCell(sheet, titleRow, colnum);

      readonly[titleRow][colnum] = true;
      offerEntries[headingRow][colnum++] = GRADE;
      // Punkte
      offerEntries[headingRow][colnum++] = POINTS;
    }
    // category Zuschlag
    if (!surChargeCritiria.isEmpty()) {
      offerEntries[titleRow - 1][colnum + 1] = SURCHARGE;
      mergeCell(sheet, titleRow - 1, colnum + 1);
    }

    // Zuschlag
    for (CriterionDTO crit : surChargeCritiria) {

      // G
      sheet.setColumnWidth(colnum, gWidth);
      offerEntries[titleRow][colnum] = GEWICHTUNG;
      offerEntries[headingRow][colnum++] = crit.getWeighting();

      // title of Zuschlag
      offerEntries[titleRow][colnum] = crit.getCriterionText();
      mergeCell(sheet, titleRow, colnum);

      // Note. If it dosen't have subcriteria, it is readonly.
      offerEntries[headingRow][colnum] = GRADE;
      colnum++;

      // Punkte
      offerEntries[headingRow][colnum++] = POINTS;

      // for each Bewertete Kriterien gather the subcriteria titles..
      for (SubcriterionDTO subCrit : crit.getSubcriterion()) {
        sheet.setColumnWidth(colnum, gWidth);
        offerEntries[titleRow][colnum] = GEWICHTUNG;
        offerEntries[headingRow][colnum++] = subCrit.getWeighting();
        offerEntries[titleRow][colnum] = subCrit.getSubcriterionText();
        mergeCell(sheet, titleRow, colnum);

        offerEntries[headingRow][colnum++] = GRADE;
        offerEntries[headingRow][colnum++] = POINTS;
      }
    }
  }

  /**
   * Count table columns.
   *
   * @param offers the offers
   * @param type the type
   * @return the int
   * @static ones: Offer title uuid(hidden) Total Punkte Rank
   * @dynamic ones: Muss Kriterien Bewertetes Kriterium Unterkriterium
   */
  private int countTableColumns(List<OfferDTO> offers, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method countTableColumns, Parameters: "
        + "type: {0}, offers: {1}",
      new Object[]{type, offers});

    int staticCols = 5; // 3 + Rank + Total rank
    int mussKriterien = 0;
    int kriterien = 0;
    int colsOfCriterion = 3; // G, Note, Punkte

    if (type.equals(SUITABILITY)) {

      for (OfferCriterionDTO of : offers.get(0).getOfferCriteria()) {
        if (of.getCriterion().getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
          mussKriterien++;
        } else if (of.getCriterion().getCriterionType()
          .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
          kriterien += 3;
          kriterien += of.getCriterion().getSubcriterion().size() * colsOfCriterion;
        }
      }

      if (kriterien == 0) {
        staticCols -= 2;
      }

      return staticCols + mussKriterien + kriterien;
    } else if (type.equals(AWARD)) {
      staticCols = 5;

      for (OfferCriterionDTO of : offers.get(0).getOfferCriteria()) {
        if (of.getCriterion().getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
          || of.getCriterion().getCriterionType()
          .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {

          kriterien += colsOfCriterion;

        } else if (of.getCriterion().getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)) {
          kriterien += colsOfCriterion;
          kriterien += of.getCriterion().getSubcriterion().size() * colsOfCriterion;
        }
      }
      return staticCols + kriterien;
    }
    return 0;
  }

  /**
   * Counts Total table Rows.
   *
   * @param offers the offers
   * @param type the type
   * @return the int
   */
  private int countTableRows(List<OfferDTO> offers, String type) {

    LOGGER.log(Level.CONFIG, "Executing method countTableRows, Parameters: "
        + "type: {0}, offers: {1}",
      new Object[]{type, offers});

    int headingRows = DATA_STARTING_ROWNUM;

    if (type.equals(AWARD)) {
      headingRows++;
      removeExcludedOffers(offers);
    }
    return headingRows + offers.size();
  }

  /**
   * Casts from Boolean to FULLFILLED or NOTFULLFILLED.
   *
   * @param isFulfilled the is fulfilled
   * @return the string
   */
  private String castToFullfiled(Boolean isFulfilled) {

    LOGGER.log(Level.CONFIG,
      "Executing method castToFullfiled, Parameters: isFulfilled: {0}",
      isFulfilled);

    if (isFulfilled != null) {
      return (isFulfilled ? FULLFILLED : NOTFULLFILLED);
    }
    return null;
  }

  /**
   * Counts Total table columns.
   *
   * @param fulfilled the fulfilled
   * @return the boolean
   */
  private Boolean castFromFullfiled(String fulfilled) {

    LOGGER.log(Level.CONFIG,
      "Executing method castFromFullfiled, Parameters: "
        + "fulfilled: {0}", fulfilled);

    if (fulfilled != null && !fulfilled.isEmpty()) {
      return (fulfilled.equals(FULLFILLED));
    }
    // Do not fix this sonar code smell warning as the null value needs to be returned as well (if
    // applicable).
    return null;
  }

  /**
   * Creates validation rule for cell, given the address range and values.
   *
   * @param addressList the address list
   * @param sheet the sheet
   * @param positive the positive
   * @param negative the negative
   * @return the data validation
   */
  private DataValidation createValidation(CellRangeAddressList addressList, XSSFSheet sheet,
    String positive, String negative) {

    LOGGER.log(Level.CONFIG,
      "Executing method createValidation, Parameters: addressList: {0}, "
        + "sheet: {1}, positive: {2}, negative: {3}",
      new Object[]{addressList, sheet, positive, negative});

    XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
    XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
      .createExplicitListConstraint(new String[]{"", positive, negative});
    XSSFDataValidation fullfiledvalidation =
      (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
    fullfiledvalidation.setShowErrorBox(true);

    return fullfiledvalidation;
  }

  /**
   * Generates Sum formula string.
   *
   * @param punkte the punkte
   * @return the string
   */
  private String generateSumForumla(ArrayList<String> punkte) {

    LOGGER.log(Level.CONFIG, "Executing method generateSumForumla, Parameters: "
      + "punkte: {0}", punkte);

    StringBuilder sb = new StringBuilder();

    for (String p : punkte) {
      sb.append(" + " + p);
    }
    return roundForumla(sb.toString(), 4);
  }

  /**
   * Punkte = g * note /100 f(r,c) = f(2,c-2) * f(r,c-1)/100.
   *
   * @param r the r
   * @param c the c
   * @param sheet the sheet
   * @param type the type
   * @return "= g * note /100"
   */
  private String generatePunkteFormula(int r, int c, XSSFSheet sheet, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method generatePunkteFormula, Parameters: r: {0}, c: {1}, "
        + "sheet: {2}, type: {3}",
      new Object[]{r, c, sheet, type});

    int gRow = DATA_STARTING_ROWNUM - 1;

    if (type.equals(AWARD)) {
      gRow++;
    }

    String g = sheet.getRow(gRow).getCell(c - 2).getAddress().formatAsString();
    String note = sheet.getRow(r).getCell(c - 1).getAddress().formatAsString();
    return roundForumla("" + g + "*" + note + "/100", 4);
  }

  /**
   * In any case where Bewertete Kriterien has subcategoties its Note is calculated by the sum of
   * subcategory-ies Punkte. Note = Sum(SubPunkte) f(r,c) = Sum f(r,c+(n*3))
   *
   * @param r the r
   * @param c the c
   * @param subCateglength the sub categlength
   * @param sheet the sheet
   * @return the string
   */
  private String generateNoteFormula(int r, int c, int subCateglength, XSSFSheet sheet) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateNoteFormula, Parameters: r: {0}, c: {1}, "
        + "subCateglength: {2}, sheet: {3}",
      new Object[]{r, c, subCateglength, sheet});

    // Parse error near char 0 ' ' in specified formula ''. Expected cell ref or constant literal
    // 3 is the gap between the uk punktes + 1 Bewertetes Punkte
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i <= subCateglength; i++) {
      sb.append(" + " + sheet.getRow(r).getCell(c + i * 3 + 1).getAddress().formatAsString());
    }

    return roundForumla(sb.toString(), 3);
  }

  /**
   * Adds Round to formula's result.
   *
   * @param formula the formula
   * @param decimal the decimal
   * @return the string
   */
  private String roundForumla(String formula, int decimal) {

    LOGGER.log(Level.CONFIG,
      "Executing method roundForumla, Parameters: formula: {0}, decimal: {1}",
      new Object[]{formula, decimal});

    return "ROUND(" + formula + "," + decimal + ")";
  }

  /**
   * function that returns the offerCriterion grade given a criterion.
   *
   * @param offer the offer
   * @param subcriterion the subcriterion
   * @return the offer criterion grade
   */
  private BigDecimal getOfferCriterionGrade(OfferDTO offer, SubcriterionDTO subcriterion) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferCriterionGrade, Parameters: "
        + "offer: {0}, subcriterion: {1}",
      new Object[]{offer, subcriterion});

    for (OfferSubcriterionDTO offerSubcriterion : offer.getOfferSubcriteria()) {
      if (offerSubcriterion.getSubcriterion().getId().equals(subcriterion.getId())) {
        return offerSubcriterion.getGrade();
      }
    }
    return null;
  }

  /**
   * Validates imported document for errors and also checks imported document for changes.
   *
   * @param submissionId the submission id
   * @param fileId the file id
   * @param type the type
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public String checkImportedDocumentForChanges(String submissionId, String fileId, String type)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method checkImportedDocumentForChanges, Parameters: submissionId: {0}, "
        + "fileId: {1}, type: {2}",
      new Object[]{submissionId, fileId, type});

    FileGetResponse fileGetResponse = fileUpload.getByID(fileId);
    byte[] uploadedDocument = fileGetResponse.getFile().getFileData();
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(uploadedDocument);

    String changedSubbmitends = "";
    String changedFormel = "";
    Boolean isChanged = false;
    XSSFWorkbook wb;
    try {
      wb = new XSSFWorkbook(byteArrayInputStream);
    } catch (Exception e) {
      byteArrayInputStream.close();
      // Throw exception when trying to import a file that is not an excel document.
      throw new IOException(UPLOAD_EXCEL_FILE);
    }
    ArrayList<AwardAssessDTO> awardAssess = new ArrayList<>();
    List<OfferDTO> offers = Collections.emptyList();

    try {

      XSSFSheet importedSheet = wb.getSheetAt(0);
      // check sheet
      // wb.isStructureLocked();
      // wb.isWindowsLocked();

      if (!importedSheet.validateSheetPassword(
        sDService.getMasterListHistoryByCode(ShortCode.S14.toString()).getValue2())) {
        wb.close();
        byteArrayInputStream.close();
        LOGGER.log(Level.WARNING, "Das zu importierende Dokument wurde manipuliert.");
        throw new IOException("Das zu importierende Dokument wurde manipuliert.");
      }

      Row submissionIdRow = importedSheet.getRow(1);
      Cell submissionIdCell = submissionIdRow.getCell(0);
      String importedsubmissionId = submissionIdCell.getStringCellValue();

      if ((submissionId == null) || (!importedsubmissionId.equals(submissionId))) {
        wb.close();
        byteArrayInputStream.close();
        LOGGER.log(Level.WARNING, NO_SUB_FOUND_ERROR_MSG);
        throw new IOException(NO_SUB_FOUND_ERROR_MSG);
      }

      offers = criterionService.getOfferCriteria(submissionId, type);
      SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
      ArrayList<String> uuids = exportUUIDSFromSheet(importedSheet, type);

      int rowNum = -1;
      if (type.equals(SUITABILITY)) {

        changedSubbmitends = changedSubmittens(DATA_STARTING_ROWNUM, offers, uuids);
        ArrayList<SuitabilityDTO> suitabilities =
          parseXlsxDataToSuitability(wb, importedSheet, offers, uuids, rowNum);
        validateSuitability(suitabilities, submission);
        isChanged = criterionService.isOfferCriteriaForChanged(suitabilities);

      } else if (type.equals(AWARD)) {

        removeExcludedOffers(offers);
        changedSubbmitends = changedSubmittens(DATA_STARTING_ROWNUM + 1, offers, uuids);

        awardAssess = parseXlsxDataToAward(wb, importedSheet, offers, uuids, rowNum);
        validateAward(awardAssess, submission);
        isChanged = criterionService.isOfferCriteriaForChangedAward(awardAssess);

        changedFormel = changedFormels(importedSheet, submission, offers);
      }
    } catch (Exception e) {
      wb.close();
      byteArrayInputStream.close();
      if ((e.getMessage() == null) || (e.getMessage().isEmpty())) {
        LOGGER.log(Level.WARNING, MALFORMED_ERROR_MSG + " " + e);
        throw new IOException(MALFORMED_ERROR_MSG);
      }
      LOGGER.log(Level.WARNING, MALFORMED_ERROR_MSG + " " + e.toString());
      throw e;
    }

    if (isChanged) {
      if (!changedFormel.isEmpty()) {
        return changedFormel;
      }
      if (!changedSubbmitends.isEmpty()) {
        return changedSubbmitends;
      }
      return "changedWithNoWarnings";
    } else {
      return "";
    }
  }

  private String changedFormels(XSSFSheet importedSheet, SubmissionDTO submission,
    List<OfferDTO> offers) {

    int formelpreisRowNum = HEADING_ROWNUM + 3;
    int formelOperatinRow = HEADING_ROWNUM + 4;

    String formelPreis = importedSheet.getRow(formelpreisRowNum).getCell(1).getStringCellValue();
    String formelOperation =
      importedSheet.getRow(formelOperatinRow).getCell(1).getStringCellValue();

    String subFormelPreis = getFormelPreis(submission);
    String subFormelOperation = getOperatingCostFormula(submission, offers);

    if (!formelPreis.equals(subFormelPreis) || !formelOperation.equals(subFormelOperation)) {
      return WARNING_PREIS;
    }
    return "";
  }


  /**
   * Import related function.
   *
   * @param submissionId the submission id
   * @param fileId the file id
   * @param type the type
   * @return the byte[]
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public byte[] importTableOfCriteria(String submissionId, String fileId, String type)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method importTableOfCriteria, Parameters: submissionId: {0}, "
        + "fileId: {1}, type: {2}",
      new Object[]{submissionId, fileId, type});

    // Retrieve file from database
    FileGetResponse fileGetResponse = fileUpload.getByID(fileId);
    fileUpload.cleanupExpired(1L);

    byte[] uploadedDocument = fileGetResponse.getFile().getFileData();
    extractXlsxValues(new ByteArrayInputStream(uploadedDocument), type);

    return ArrayUtils.EMPTY_BYTE_ARRAY;
  }

  /**
   * Import related function.
   *
   * @param byteArrayInputStream the byte array input stream
   * @param type the type
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void extractXlsxValues(ByteArrayInputStream byteArrayInputStream, String type)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method extractXlsxValues, Parameters: type: {0}, "
        + "byteArrayInputStream: {1}",
      new Object[]{type, byteArrayInputStream});

    String submissionId = "";
    XSSFWorkbook wb = new XSSFWorkbook(byteArrayInputStream);
    try {

      XSSFSheet importedSheet = wb.getSheetAt(0);
      Row submissionIdRow = importedSheet.getRow(1);
      Cell submissionIdCell = submissionIdRow.getCell(0);
      submissionId = submissionIdCell.getStringCellValue();

      if (submissionId == null) {
        wb.close();
        byteArrayInputStream.close();
        throw new IOException(NO_SUB_FOUND_ERROR_MSG);
      }

      ArrayList<String> uuids = exportUUIDSFromSheet(importedSheet, type);

      int rowNum = -1;
      if (type.equals(SUITABILITY)) {

        List<OfferDTO> offers = criterionService
          .getExaminationSubmittentListWithCriteria(submissionId, SUITABILITY, Boolean.TRUE);

        /// loop importedSheet against offer list .. to validate and extract values.
        // loop will executed row by row.

        ArrayList<SuitabilityDTO> suitabilities =
          parseXlsxDataToSuitability(wb, importedSheet, offers, uuids, rowNum);
        criterionService.updateOfferCriteriaFromXlsx(suitabilities);

      } else if (type.equals(AWARD)) {

        List<OfferDTO> offers = criterionService.getOfferCriteria(submissionId, AWARD);

        removeExcludedOffers(offers);

        ArrayList<AwardAssessDTO> awardAssess =
          parseXlsxDataToAward(wb, importedSheet, offers, uuids, rowNum);
        criterionService.updateAwardOfferCriteriaFromXlsx(awardAssess);
      }

      submissionService.unlockSubmission(submissionId, type);

      // log verlauf
      SubmissionDTO submission = submissionService.getSubmissionById(submissionId);

      String usersTenant =
        getUser().getAttribute(LookupValues.USER_ATTRIBUTES.TENANT.getValue()).getData();

      StringBuilder projectVars = new StringBuilder(submission.getProject().getProjectName())
        .append("[#]")
        .append(submission.getProject().getObjectName().getValue1()).append("[#]")
        .append(submission.getWorkType().getValue1() + submission.getWorkType().getValue2())
        .append("[#]")
        .append(usersTenant).append("[#]");

      audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
        AuditGroupName.PROJECT.name(), AuditMessages.IMPORT.name(), getUser().getId(),
        submission.getId(), null, projectVars.toString(), LookupValues.EXTERNAL_LOG);

    } catch (Exception e) {
      wb.close();
      byteArrayInputStream.close();
      LOGGER.log(Level.WARNING, e.getMessage());
      if ((e.getMessage() == null) || (e.getMessage().isEmpty())) {
        LOGGER.log(Level.WARNING, MALFORMED_ERROR_MSG + " " + e);
        throw new IOException(MALFORMED_ERROR_MSG);
      }
      throw e;
    }
  }

  /**
   * Parses the xlsx data to award.
   *
   * @param wb the wb
   * @param importedSheet the imported sheet
   * @param offers the offers
   * @param uuids the uuids
   * @param rowNum the row num
   * @return the array list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private ArrayList<AwardAssessDTO> parseXlsxDataToAward(XSSFWorkbook wb, XSSFSheet importedSheet,
    List<OfferDTO> offers, ArrayList<String> uuids, int rowNum) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseXlsxDataToAward, Parameters: wb: {0}, "
        + "importedSheet: {1}, uuids: {2}, rownum: {3}, offers: {4}",
      new Object[]{wb, importedSheet, uuids, rowNum, offers});

    ArrayList<AwardAssessDTO> awardAssess = new ArrayList<>();

    try {
      for (OfferDTO offer : offers) {

        rowNum = getUuidRowNum(uuids, rowNum, offer);
        if (rowNum == -1) {
          continue;
        }

        rowNum++; // award subbmitters are +1
        Row importRow = importedSheet.getRow(rowNum);
        validateCompanyTitle(importRow, offer);
        BigDecimal totalScore = getTotalscoreFromAwardRow(importRow, wb);
        Integer rank = getRankFromAwardRow(importRow, wb);
        ArrayList<OfferCriterionLiteDTO> priceAwardCriterion = new ArrayList<>();
        ArrayList<OfferCriterionLiteDTO> operatingCostAwardCriterion = new ArrayList<>();
        ArrayList<OfferCriterionLiteDTO> awardCriterion = new ArrayList<>();
        ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria = new ArrayList<>();

        int col = DATA_STARTING_COLLNUM + 2;

        for (OfferCriterionDTO of : offer.getOfferCriteria()) {
          col = parsePriceAwardCriterion(wb, importRow, priceAwardCriterion, col, of);
        }

        for (OfferCriterionDTO of : offer.getOfferCriteria()) {
          col = parseoperatingCostAwardCriteria(wb, offer, importRow, operatingCostAwardCriterion,
            col, of);
        }
        parseAwardCriteria(wb, importedSheet, importRow, awardCriterion, offerSubcriteria, col,
          offer);
        AwardAssessDTO awardAsse = getawardAsse(offer, priceAwardCriterion,
          operatingCostAwardCriterion, awardCriterion, offerSubcriteria, totalScore, rank);
        awardAssess.add(awardAsse);

      } // offers loop
    } catch (Exception e) {
      if ((e.getMessage() == null) || (e.getMessage().isEmpty())) {
        LOGGER.log(Level.WARNING, MALFORMED_ERROR_MSG + " " + e);
        throw new IOException(MALFORMED_ERROR_MSG);
      }
      throw e;
    }
    return awardAssess;
  }

  private void validateCompanyTitle(Row importRow, OfferDTO offer) throws IOException {

    Cell cell = importRow.getCell(1);
    String companyname = cell.getStringCellValue();

    Cell cellid = importRow.getCell(0);
    String offeruuid = cellid.getStringCellValue();

    // check also ids in any case of manipulation,
    if ((offeruuid.equals(offer.getId())
      && (!companyname.equals(offer.getSubmittent().getSubmittentNameArgeSub())))) {
      LOGGER.log(Level.WARNING, MALFORMED_SUBMITTENTEN + " @ " + PROPOSERS + PARSING_PHRASE);
      throw new IOException(MALFORMED_SUBMITTENTEN);
    }
  }


  private String changedSubmittens(int startingRow, List<OfferDTO> offers,
    ArrayList<String> uuids) {

    // if offer has been added...
    ArrayList<String> offerIds = new ArrayList<>();

    for (OfferDTO offer : offers) {
      if (!uuids.contains(offer.getId())) {
        return WARNING_SUBMITTENTEN;
      }
      offerIds.add(offer.getId());
    }
    for (String uuid : uuids) {
      if (!offerIds.contains(uuid)) {
        return WARNING_EXCLUDED;
      }
    }
    return "";
  }

  /**
   * Gets the totalscore from award row.
   *
   * @param importRow the import row
   * @param wb the wb
   * @return the totalscore from award row
   */
  private BigDecimal getTotalscoreFromAwardRow(Row importRow, XSSFWorkbook wb) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method getTotalscoreFromAwardRow, Parameters: importRow: {0}, wb: {1}",
      new Object[]{importRow, wb});

    int totalScoreCellNum = 3;

    Cell cell = importRow.getCell(totalScoreCellNum);
    try {
      return getCalculatedValueFromCell(cell, wb);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, MALFORMED_CRITERIA);
      throw new IOException(MALFORMED_CRITERIA);
    }
  }

  /**
   * Gets the rank from award row.
   *
   * @param importRow the import row
   * @param wb the wb
   * @return the rank from award row
   */
  private Integer getRankFromAwardRow(Row importRow, XSSFWorkbook wb) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method getRankFromAwardRow, Parameters: importRow: {0}, wb: {1}",
      new Object[]{importRow, wb});

    int rankCellNum = 2;
    Cell cell = importRow.getCell(rankCellNum);
    try {
      BigDecimal rank = getCalculatedValueFromCell(cell, wb);
      if (rank != null) {
        return rank.intValue();
      }
      return null;
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * Parses the price award criterion.
   *
   * @param wb the wb
   * @param importRow the import row
   * @param priceAwardCriterion the price award criterion
   * @param col the col
   * @param of the of
   * @return the int
   * @throws IOException Signals that an I/O exception has occurred.
   */
  // possible refactor with price with LookupValues.PRICE_AWARD_CRITERION_TYPE as
  private int parsePriceAwardCriterion(XSSFWorkbook wb, Row importRow,
    ArrayList<OfferCriterionLiteDTO> priceAwardCriterion, int col, OfferCriterionDTO of)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parsePriceAwardCriterion, Parameters: wb: {0}, "
        + "importRow: {1}, col: {2}, of: {3}, priceAwardCriterion: {4}",
      new Object[]{wb, importRow, col, of, priceAwardCriterion});

    Cell cell;
    if (of.getCriterion().getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
      OfferCriterionLiteDTO cl = new OfferCriterionLiteDTO();

      // Validate criterium text first
      if (!of.getCriterion().getCriterionText().equals(
        getCellTitle(wb.getSheetAt(0), importRow.getCell(col + 1), DATA_STARTING_ROWNUM - 1))) {
        LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ " + GRADE + PARSING_PHRASE);
        throw new IOException(MALFORMED_CRITERIA);
      }

      cl.setOfferCriterionId(of.getId());
      // G
      validateGewicht(wb.getSheetAt(0), col, of.getCriterion().getWeighting().toString(), AWARD);

      col++;

      // Note
      cell = importRow.getCell(col++);

      if (of.getCriterion().getSubcriterion().isEmpty()) {

        // note
        cl.setGrade(getBigDecimalFromCell(cell, wb));
        cell = importRow.getCell(col++);

        // punkte
        cl.setScore(getBigDecimalFromCell(cell, wb));
      }

      priceAwardCriterion.add(cl);
    }
    return col;
  }

  /**
   * Parseoperating cost award criteria.
   *
   * @param wb the wb
   * @param offer the offer
   * @param importRow the import row
   * @param operatingCostAwardCriterion the operating cost award criterion
   * @param col the col
   * @param of the of
   * @return the int
   * @throws IOException Signals that an I/O exception has occurred.
   */
  // possible refactor with price with LookupValues.PRICE_AWARD_CRITERION_TYPE as
  private int parseoperatingCostAwardCriteria(XSSFWorkbook wb, OfferDTO offer, Row importRow,
    ArrayList<OfferCriterionLiteDTO> operatingCostAwardCriterion, int col, OfferCriterionDTO of)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseoperatingCostAwardCriteria, Parameters: wb: {0}, "
        + "offer: {1}, importRow: {2}, col: {3}, of: {4}, operatingCostAwardCriterion: {5}",
      new Object[]{wb, offer, importRow, col, of, operatingCostAwardCriterion});

    Cell cell;
    if (of.getCriterion().getCriterionType()
      .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {

      OfferCriterionLiteDTO cl = new OfferCriterionLiteDTO();
      cl.setOfferCriterionId(of.getId());

      validateGewicht(wb.getSheetAt(0), col, of.getCriterion().getWeighting().toString(), AWARD);

      // G
      col++;
      // Note
      cell = importRow.getCell(col++);

      // validate imported cols
      if (!of.getCriterion().getCriterionText()
        .equals(getCellTitle(wb.getSheetAt(0), cell, DATA_STARTING_ROWNUM - 1))) {
        LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ " + GRADE + PARSING_PHRASE);
        throw new IOException(MALFORMED_CRITERIA);
      }

      if (of.getCriterion().getSubcriterion().isEmpty()) {
        // note
        cl.setGrade(getBigDecimalFromCell(cell, wb));
        cell = importRow.getCell(col++);
        // punkte
        cl.setScore(getBigDecimalFromCell(cell, wb));
      }
      operatingCostAwardCriterion.add(cl);
    }
    return col;
  }

  /**
   * Parses the award criteria.
   *
   * @param wb the wb
   * @param importedSheet the imported sheet
   * @param importRow the import row
   * @param awardCriterion the award criterion
   * @param offerSubcriteria the offer subcriteria
   * @param col the col
   * @param offer the offer
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void parseAwardCriteria(XSSFWorkbook wb, XSSFSheet importedSheet, Row importRow,
    ArrayList<OfferCriterionLiteDTO> awardCriterion,
    ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria, int col, OfferDTO offer)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseAwardCriteria, Parameters: wb: {0}, importedSheet: {1}, "
        + "importRow: {2}, col: {3}, offer: {4}, awardCriterion: {5}, offerSubcriteria: {6}",
      new Object[]{wb, importedSheet, importRow, col, offer, awardCriterion, offerSubcriteria});

    List<OfferSubcriterionDTO> offerSublist = offer.getOfferSubcriteria();

    // OfferSubcriteria are not in prefexed place. so we have to find them.
    // if they dont exist at all, import fails.
    boolean offerExists = false;

    for (OfferCriterionDTO offerCriterionDTO : offer.getOfferCriteria()) {

      Cell cell;
      if (offerCriterionDTO.getCriterion().getCriterionType()
        .equals(LookupValues.AWARD_CRITERION_TYPE)) {
        OfferCriterionLiteDTO cl = new OfferCriterionLiteDTO();

        getZuschlagCritirienTitle(col, importedSheet);

        /* loop again to be sure that will be parsed in the proper order. */
        cl.setOfferCriterionId(offerCriterionDTO.getId());

        // Check for inconsistencies regarding the criterion title/text.
        if (!offerCriterionDTO.getCriterion().getCriterionText()
            .equals(getCellTitle(wb.getSheetAt(0), importRow.getCell(col + 1), TITLE_ROWNUM + 1))) {
          LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ " + RATED_CRITERIA + PARSING_PHRASE);
          throw new IOException(MALFORMED_CRITERIA);
        }

        // G
        validateGewicht(wb.getSheetAt(0), col,
          offerCriterionDTO.getCriterion().getWeighting().toString(), AWARD);
        col++;

        // Note
        cell = importRow.getCell(col++);

        if (offerCriterionDTO.getCriterion().getSubcriterion().isEmpty()) {
          // Note is calculated in any case of BK with no UK.
          cl.setGrade(getCalculatedValueFromCell(cell, wb));
          cell = importRow.getCell(col++);
          // punkte
          cl.setScore(getCalculatedValueFromCell(cell, wb));
        } else {
          // note
          cl.setGrade(getBigDecimalFromCell(cell, wb));

          cell = importRow.getCell(col++);
          // punkte
          cl.setScore(getBigDecimalFromCell(cell, wb));
          // parse sub criteria
          col = parseSubOfferCriteria(wb, importedSheet, importRow, offerSubcriteria, col,
            offerSublist, offerCriterionDTO);
        }
        awardCriterion.add(cl);
        offerExists = true;
      }
    }
    if (!offerExists) {
      LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ " + GRADE + PARSING_PHRASE);
      throw new IOException(MALFORMED_CRITERIA);
    }
  }

  /**
   * Gets the award asse.
   *
   * @param offer the offer
   * @param priceAwardCriterion the price award criterion
   * @param operatingCostAwardCriterion the operating cost award criterion
   * @param awardCriterion the award criterion
   * @param offerSubcriteria the offer subcriteria
   * @param totalScore the total score
   * @param rank the rank
   * @return the award asse
   */
  private AwardAssessDTO getawardAsse(OfferDTO offer,
    ArrayList<OfferCriterionLiteDTO> priceAwardCriterion,
    ArrayList<OfferCriterionLiteDTO> operatingCostAwardCriterion,
    ArrayList<OfferCriterionLiteDTO> awardCriterion,
    ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria, BigDecimal totalScore, Integer rank) {

    LOGGER.log(Level.CONFIG,
      "Executing method setTitlesStyle, Parameters: offer: {0}, "
        + "totalScore: {1}, rank: {2}, priceAwardCriterion: {3}, "
        + "operatingCostAwardCriterion: {4}, awardCriterion: {5}, "
        + "offerSubcriteria: {6}",
      new Object[]{offer, totalScore, rank, priceAwardCriterion,
        operatingCostAwardCriterion, awardCriterion, offerSubcriteria});

    AwardAssessDTO awardAss = new AwardAssessDTO();
    awardAss.setOfferId(offer.getId());
    ArrayList<OfferCriterionLiteDTO> critlist = new ArrayList<>();
    critlist.addAll(priceAwardCriterion);
    critlist.addAll(operatingCostAwardCriterion);
    critlist.addAll(awardCriterion);
    awardAss.setOfferCriteria(critlist);
    awardAss.setOfferSubcriteria(offerSubcriteria);
    awardAss.setTotalScore(totalScore);
    awardAss.setRank(rank);
    return awardAss;
  }

  /**
   * Parses the xlsx data to suitability.
   *
   * @param wb the wb
   * @param importedSheet the imported sheet
   * @param offers the offers
   * @param uuids the uuids
   * @param rowNum the row num
   * @return the array list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private ArrayList<SuitabilityDTO> parseXlsxDataToSuitability(XSSFWorkbook wb,
    XSSFSheet importedSheet, List<OfferDTO> offers, ArrayList<String> uuids, int rowNum)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseXlsxDataToSuitability, Parameters: wb: {0}, "
        + "importedSheet: {1}, rowNum: {2}, offers: {3}, uuids: {4}",
      new Object[]{wb, importedSheet, rowNum, offers, uuids});

    ArrayList<SuitabilityDTO> suitabilities = new ArrayList<>();

    try {

      int ratedCnt = 0;

      int totalPunkctecol = countTableColumns(offers, SUITABILITY);

      for (OfferCriterionDTO offer : offers.get(0).getOfferCriteria()) {

        if (offer.getCriterion().getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
          ratedCnt++;
        }
      }

      for (OfferDTO offer : offers) {

        // 1 find offer in the importedSheet
        rowNum = getUuidRowNum(uuids, rowNum, offer);

        if (rowNum == -1) {
          continue;
        }

        Row importRow = importedSheet.getRow(rowNum);

        validateCompanyTitle(importRow, offer);

        ArrayList<CriterionLiteDTO> mustCriterion = new ArrayList<>();
        ArrayList<CriterionLiteDTO> evaluatedCriterion = new ArrayList<>();
        ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria = new ArrayList<>();

        int col = DATA_STARTING_COLLNUM;

        // there are no evaluated criteria thus there are no rank.
        if (ratedCnt == 0) {
          col--;
        }
        // gather muss criteria first
        for (OfferCriterionDTO of : offer.getOfferCriteria()) {
          col = parseMussCrieteria(importedSheet, offer, importRow, mustCriterion, col, of);
        }

        // parse bewertete criteria
        parseEvaluatedCriteria(wb, importedSheet, importRow, evaluatedCriterion,
            offerSubcriteria, col, offer);

        Integer rank = null;
        BigDecimal totalscore = null;

        if (!evaluatedCriterion.isEmpty()) {
          rank = getRankFromSuitability(importRow, wb);
          totalscore = getTotalscoreFromSuitability(importedSheet, importRow, wb, totalPunkctecol);
        }

        String comment = getCommentFromSuitability(importedSheet, importRow, totalPunkctecol);

        if (comment.length() > MAX_TEXT_SIZE
          || (comment.length() > 0 && comment.length() < MIN_TEXT_SIZE)) {
          throw new IOException(TEXT_OUT_OF_BOUNDS);
        }

        SuitabilityDTO suit = getSuitability(offer, mustCriterion, evaluatedCriterion,
          offerSubcriteria, rank, totalscore, comment);
        suitabilities.add(suit);
      }
    } catch (Exception e) {
      if ((e.getMessage() == null) || (e.getMessage().isEmpty())) {
        LOGGER.log(Level.WARNING, MALFORMED_ERROR_MSG + " " + e);
        throw new IOException(MALFORMED_ERROR_MSG);
      } else if (e.getMessage().equals(NUMERIC_TO_STRING_ERROR)) {
        LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " " + e);
        throw new IOException(MALFORMED_CRITERIA);
      }
      throw e;
    }
    return suitabilities;
  }

  /**
   * Parses the muss crieteria.
   *
   * @param importedSheet the imported sheet
   * @param offer the offer
   * @param importRow the import row
   * @param mustCriterion the must criterion
   * @param col the col
   * @param of the of
   * @return the int
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private int parseMussCrieteria(XSSFSheet importedSheet, OfferDTO offer, Row importRow,
    ArrayList<CriterionLiteDTO> mustCriterion, int col, OfferCriterionDTO of) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseMussCrieteria, Parameters: importedSheet: {0}, "
        + "offer: {1}, importRow: {2}, col: {3}, of: {4}, mustCriterion: {5}",
      new Object[]{importedSheet, offer, importRow, col, of, mustCriterion});

    Cell cell;
    int cnt = 0;
    if (of.getCriterion().getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
      cell = importRow.getCell(col++);

      CriterionLiteDTO cl = new CriterionLiteDTO();
      cl.setCriterionId(of.getId());
      try {
        cl.setIsFulfilled(castFromFullfiled(cell.getStringCellValue()));
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, MALFORMED_CRITERIA);
        throw new IOException(MALFORMED_CRITERIA);
      }
      mustCriterion.add(cl);
      cnt++;
      if (cnt == 0) {
        LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ " + MUST_CRITERIA + PARSING_PHRASE);
        throw new IOException(MALFORMED_CRITERIA);
      }
    }
    return col;
  }

  /**
   * Parses the evaluated criteria.
   *
   * @param wb the wb
   * @param importedSheet the imported sheet
   * @param importRow the import row
   * @param evaluatedCriterion the evaluated criterion
   * @param offerSubcriteria the offer subcriteria
   * @param col the col
   * @param offer the offer
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void parseEvaluatedCriteria(XSSFWorkbook wb, XSSFSheet importedSheet, Row importRow,
    ArrayList<CriterionLiteDTO> evaluatedCriterion,
    ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria, int col, OfferDTO offer)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseEvalueatedCriteria, Parameters: wb: {0}, "
        + "importedSheet: {1}, importRow: {2}, evaluatedCriterion: {3}, offerSubcriteria: {4}, "
        + "col: {5}, offer: {6}",
      new Object[]{wb, importedSheet, importRow, evaluatedCriterion, offerSubcriteria, col, offer});

    List<OfferSubcriterionDTO> offerSublist = offer.getOfferSubcriteria();

    for (OfferCriterionDTO offerCriterionDTO : offer.getOfferCriteria()) {
      Cell cell;

      if (offerCriterionDTO.getCriterion().getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {

        // conisder parsing text here.
        CriterionLiteDTO cl = new CriterionLiteDTO();
        cl.setCriterionId(offerCriterionDTO.getId());

        if (!offerCriterionDTO.getCriterion().getCriterionText()
          .equals(getCellTitle(importedSheet, importRow.getCell(col + 1), TITLE_ROWNUM))) {
          LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ " + RATED_CRITERIA + PARSING_PHRASE);
          throw new IOException(MALFORMED_CRITERIA);
          // Checkpoint
        }

        // G
        validateGewicht(importedSheet, col, offerCriterionDTO.getCriterion().getWeighting().toString(), SUITABILITY);

        col++;

        // Note
        cell = importRow.getCell(col++);

        if (offerCriterionDTO.getCriterion().getSubcriterion().isEmpty()) {
          // Note is calculated in any case of BK with no UK.
          cl.setGrade(getCalculatedValueFromCell(cell, wb));
          cell = importRow.getCell(col++);
          // punkte
          cl.setScore(getCalculatedValueFromCell(cell, wb));
        } else {
          // note
          cl.setGrade(getBigDecimalFromCell(cell, wb));
          cell = importRow.getCell(col++);
          // punkte
          cl.setScore(getBigDecimalFromCell(cell, wb));
          // parse sub criteria
          col = parseSubCriteria(wb, importedSheet, importRow, offerSubcriteria, col, offerSublist,
            offerCriterionDTO);
        }
        evaluatedCriterion.add(cl);
      }
    }
  }

  /**
   * Validate gewicht.
   *
   * @param importedSheet the imported sheet
   * @param col the col
   * @param of the of
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void validateGewicht(XSSFSheet importedSheet, int col, String gewicht, String type)
    throws IOException {

    int gRow = DATA_STARTING_ROWNUM;
    if (type.equals(SUITABILITY)) {
      gRow--;
    }

    String g = importedSheet.getRow(gRow).getCell(col).toString();

    if (g == null || !g.equals(gewicht)) {
      LOGGER.log(Level.WARNING, MALFORMED_GEWICHT + " @ " + GEWICHTUNG + PARSING_PHRASE);
      throw new IOException(MALFORMED_GEWICHT);
    }
  }

  /**
   * Parses the sub criteria.
   *
   * @param wb the wb
   * @param importedSheet the imported sheet
   * @param importRow the import row
   * @param offerSubcriteria the offer subcriteria
   * @param col the col
   * @param offerSublist the offer sublist
   * @param of the of
   * @return the col
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private int parseSubCriteria(XSSFWorkbook wb, XSSFSheet importedSheet, Row importRow,
    ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria, int col,
    List<OfferSubcriterionDTO> offerSublist, OfferCriterionDTO of)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseSubCriteria, Parameters: wb: {0}, "
        + "importedSheet: {1}, importRow: {2}, offerSubcriteria: {3}, col: {4}, "
        + "offerSublist: {5}, of: {6}",
      new Object[]{wb, importedSheet, importRow, offerSubcriteria, col, offerSublist, of});

    // OfferSubcriteria are not in prefexed place. so we have to find them. if they dont exist
    // at all, import fails.
    boolean subExists = false;

    Cell cell;

    for (SubcriterionDTO subcriterionDTO : of.getCriterion().getSubcriterion()) {
      // G col
      int gCol = col;
      col++;
      // note cell
      cell = importRow.getCell(col++);

      for (OfferSubcriterionDTO ofc : offerSublist) {

        if ((ofc.getSubcriterion().getId().equals(subcriterionDTO.getId())) && (subcriterionDTO.getSubcriterionText()
          .equals(getCellTitle(importedSheet, cell, TITLE_ROWNUM)))) {

          OfferSubcriterionLiteDTO subLite = new OfferSubcriterionLiteDTO();

          // validate G, after locating the proper sub.
          validateGewicht(importedSheet, gCol, subcriterionDTO.getWeighting().toString(),
            SUITABILITY);

          subLite.setOfferSubcriterionId(subcriterionDTO.getId());
          // Note value set
          subLite.setGrade(getBigDecimalFromCell(cell, wb));
          // punkte
          cell = importRow.getCell(col++);
          subLite.setScore(getCalculatedValueFromCell(cell, wb));
          offerSubcriteria.add(subLite);
          subExists = true;
        }
      }
    }

    if (!subExists) {
      LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ SubCritirien parsing.");
      throw new IOException(MALFORMED_CRITERIA);
    }

    return col;
  }

  /**
   * Gets the rank from suitability.
   *
   * @param importRow the import row
   * @param wb the wb
   * @return the rank from suitability
   */
  // conisder refactor with award
  private Integer getRankFromSuitability(Row importRow, XSSFWorkbook wb) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method getRankFromSuitability, Parameters: "
        + "importRow: {0}, wb: {1}",
      new Object[]{importRow, wb});

    int rankCellNum = 2;
    Cell cell = importRow.getCell(rankCellNum);
    try {
      BigDecimal value = getCalculatedValueFromCell(cell, wb);
      return (value != null ? value.intValue() : null);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * Gets the totalscore from suitability.
   *
   * @param importedSheet the imported sheet
   * @param importRow the import row
   * @param wb the wb
   * @param totalPunkctecol the total punkctecol
   * @return the totalscore from suitability
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private BigDecimal getTotalscoreFromSuitability(XSSFSheet importedSheet, Row importRow,
    XSSFWorkbook wb, int totalPunkctecol) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method getTotalscoreFromSuitability, Parameters: importedSheet: {0}, "
        + "importRow: {1}, wb: {2}, totalPunkctecol: {3}",
      new Object[]{importedSheet, importRow, wb, totalPunkctecol});

    Cell cell = importRow.getCell(totalPunkctecol - 2);
    String totalScoreTitle = getCellTitle(importedSheet, cell, TITLE_ROWNUM + 1);

    if (totalScoreTitle == null || !totalScoreTitle.equals(TOTAL_POINTS)) {
      LOGGER.log(Level.WARNING, MALFORMED_COLUMNS + " @ " + TOTAL_POINTS + PARSING_PHRASE);
      throw new IOException(MALFORMED_COLUMNS);
    }
    return getCalculatedValueFromCell(cell, wb);
  }

  /**
   * Gets the comment from suitability.
   *
   * @param importedSheet the imported sheet
   * @param importRow the import row
   * @param totalPunkctecol the total punkctecol
   * @return the comment from suitability
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private String getCommentFromSuitability(XSSFSheet importedSheet, Row importRow,
    int totalPunkctecol) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method getCommentFromSuitability, Parameters: "
        + "importedSheet: {0}, importRow: {1}, totalPunkctecol: {2}",
      new Object[]{importedSheet, importRow, totalPunkctecol});

    Cell cell = importRow.getCell((totalPunkctecol - 1));
    String commentTitle = getCellTitle(importedSheet, cell, TITLE_ROWNUM + 1);

    if (commentTitle == null || !commentTitle.equals(COMMENT)) {
      LOGGER.log(Level.WARNING, MALFORMED_COLUMNS + " @ " + COMMENT + PARSING_PHRASE);
      throw new IOException(MALFORMED_COLUMNS);
    }

    DataFormatter formatter = new DataFormatter();
    return formatter.formatCellValue(cell);
  }

  /**
   * Parses the sub offer criteria.
   *
   * @param wb the wb
   * @param importedSheet the imported sheet
   * @param importRow the import row
   * @param offerSubcriteria the offer subcriteria
   * @param col the col
   * @param offerSublist the offer sublist
   * @param of the of
   * @return the col
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private int parseSubOfferCriteria(XSSFWorkbook wb, XSSFSheet importedSheet, Row importRow,
    ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria, int col,
    List<OfferSubcriterionDTO> offerSublist, OfferCriterionDTO of) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method parseSubOfferCriteria, Parameters: wb: {0}, "
        + "importedSheet: {1}, importRow: {2}, offerSubcriteria: {3}, "
        + "col: {4}, offerSublist: {5}, of: {6}",
      new Object[]{wb, importedSheet, importRow, offerSubcriteria, col, offerSublist, of});

    // OfferSubcriteria are not in prefexed place. so we have to find them. if they dont exist
    // at all, import fails.
    boolean subExists = false;

    Cell cell;

    for (SubcriterionDTO subcriterionDTO : of.getCriterion().getSubcriterion()) {
      // G col
      col++;
      // Note cell
      cell = importRow.getCell(col++);

      for (OfferSubcriterionDTO ofc : offerSublist) {

        if ((ofc.getSubcriterion().getId().equals(subcriterionDTO.getId())) && (subcriterionDTO
          .getSubcriterionText().equals(getCellTitle(importedSheet, cell, AWARD_TITLE_ROWNUM)))) {

          OfferSubcriterionLiteDTO subLite = new OfferSubcriterionLiteDTO();

          // validate G, after locating the proper sub.
          validateGewicht(importedSheet, (col - 2), subcriterionDTO.getWeighting().toString(),
            AWARD);

          subLite.setOfferSubcriterionId(ofc.getId());
          // Note value set
          subLite.setGrade(getBigDecimalFromCell(cell, wb));
          // punkte
          cell = importRow.getCell(col++);
          subLite.setScore(getCalculatedValueFromCell(cell, wb));
          offerSubcriteria.add(subLite);
          subExists = true;
        }
      }
    }

    if (!subExists) {
      LOGGER.log(Level.WARNING, MALFORMED_CRITERIA + " @ " + GRADE + PARSING_PHRASE);
      throw new IOException(MALFORMED_CRITERIA);
    }

    return col;
  }

  /**
   * Validate award.
   *
   * @param awardAssess the award assess
   * @param submission the submission
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private boolean validateAward(ArrayList<AwardAssessDTO> awardAssess, SubmissionDTO submission)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method validateAward, Parameters: "
        + "awardAssess: {0}, submission: {1}",
      new Object[]{awardAssess, submission});

    BigDecimal maxGrade = submission.getAwardMaxGrade();
    BigDecimal minGrade = submission.getAwardMinGrade();

    for (AwardAssessDTO award : awardAssess) {
      for (OfferCriterionLiteDTO of : award.getOfferCriteria()) {
        if ((of.getGrade() != null) && ((of.getGrade().compareTo(maxGrade) > 0)
          || (of.getGrade().compareTo(minGrade) < 0))) {
          LOGGER.log(Level.WARNING, NOTE_OUT_OF_BOUNDS + " @ Award  parsing.");
          throw new IOException(
            NOTE_OUT_OF_BOUNDS + " (" + minGrade.intValue() + ", " + maxGrade.intValue() + ")");
        }
      }
      for (OfferSubcriterionLiteDTO of : award.getOfferSubcriteria()) {
        if ((of.getGrade() != null) && ((of.getGrade().compareTo(maxGrade) > 0)
          || (of.getGrade().compareTo(minGrade) < 0))) {

          LOGGER.log(Level.WARNING, NOTE_OUT_OF_BOUNDS + " @ Award  parsing.");
          throw new IOException(
            NOTE_OUT_OF_BOUNDS + " (" + minGrade.intValue() + ", " + maxGrade.intValue() + ")");
        }
      }
    }
    return true;
  }

  /**
   * Validate suitability.
   *
   * @param suitabilities the suitabilities
   * @param submission the submission
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private boolean validateSuitability(ArrayList<SuitabilityDTO> suitabilities,
    SubmissionDTO submission) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method validateSuitability, Parameters: "
        + "suitabilities: {0}, submission: {1}",
      new Object[]{suitabilities, submission});

    BigDecimal maxGrade = submission.getMaxGrade();
    BigDecimal minGrade = submission.getMinGrade();

    for (SuitabilityDTO suit : suitabilities) {

      for (CriterionLiteDTO evaluatedCriterion : suit.getEvaluatedCriterion()) {
        if ((evaluatedCriterion.getGrade() != null)
          && ((evaluatedCriterion.getGrade().compareTo(maxGrade) > 0)
          || (evaluatedCriterion.getGrade().compareTo(minGrade) < 0))) {
          throw new IOException(NOTE_OUT_OF_BOUNDS + " (" + minGrade.intValue() + ","
            + maxGrade.intValue() + ") ab.");
        }
      }

      for (OfferSubcriterionLiteDTO offerSubcriterionLiteDTO : suit.getOfferSubcriteria()) {
        if ((offerSubcriterionLiteDTO.getGrade() != null)
          && ((offerSubcriterionLiteDTO.getGrade().compareTo(maxGrade) > 0)
          || (offerSubcriterionLiteDTO.getGrade().compareTo(minGrade) < 0))) {
          throw new IOException(NOTE_OUT_OF_BOUNDS + " (" + minGrade.intValue() + ","
            + maxGrade.intValue() + ") ab.");
        }
      }
    }
    return true;
  }

  /**
   * Gets the uuid row num.
   *
   * @param uuids the uuids
   * @param rowNum the row num
   * @param offer the offer
   * @return the uuid row num
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private int getUuidRowNum(ArrayList<String> uuids, int rowNum, OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method getUuidRowNum, Parameters: uuids: {0}, rowNum: {1}, offer: {2}",
      new Object[]{uuids, rowNum, offer});

    for (int i = 0; i < uuids.size(); i++) {
      if (uuids.get(i).equals(offer.getId())) {
        rowNum = DATA_STARTING_ROWNUM + i;
        break;
      } else {
        rowNum = -1;
      }
    }
    return rowNum;
  }

  /**
   * Gets the suitability.
   *
   * @param offer the offer
   * @param mustCriterion the must criterion
   * @param evaluatedCriterion the evaluated criterion
   * @param offerSubcriteria the offer subcriteria
   * @param rank the rank
   * @param totalscore the totalscore
   * @param comment the comment
   * @return the suitability
   */
  private SuitabilityDTO getSuitability(OfferDTO offer, ArrayList<CriterionLiteDTO> mustCriterion,
    ArrayList<CriterionLiteDTO> evaluatedCriterion,
    ArrayList<OfferSubcriterionLiteDTO> offerSubcriteria, Integer rank, BigDecimal totalscore,
    String comment) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSuitability, Parameters: offer: {0}, mustCriterion: {1}, "
        + "evaluatedCriterion: {2}, offerSubcriteria: {3}, rank: {4}, totalscore: {5}, "
        + "comment: {6}",
      new Object[]{offer, mustCriterion, evaluatedCriterion, offerSubcriteria, rank, totalscore,
        comment});

    SuitabilityDTO suit = new SuitabilityDTO();
    suit.setOfferId(offer.getId());
    suit.setMustCriterion(mustCriterion);
    suit.setEvaluatedCriterion(evaluatedCriterion);
    suit.setOfferSubcriteria(offerSubcriteria);
    suit.setqExExaminationIsFulfilled(offer.getqExExaminationIsFulfilled());
    // read rank from cell
    suit.setqExRank(rank);
    // read total grade from cell
    suit.setqExTotalGrade(totalscore);
    suit.setqExSuitabilityNotes(comment);
    suit.setqExStatus(offer.getqExStatus());
    return suit;
  }

  /**
   * Export UUIDS from sheet.
   *
   * @param importedSheet the imported sheet
   * @return the array list
   */
  private ArrayList<String> exportUUIDSFromSheet(XSSFSheet importedSheet, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method exportUUIDSFromSheet, Parameters: importedSheet: {0}, type: {1}",
      new Object[]{importedSheet, type});

    ArrayList<String> uuids = new ArrayList<>();

    int submittentenRowNum = 0;

    if (type.equals(SUITABILITY)) {
      submittentenRowNum = DATA_STARTING_ROWNUM;

    } else if (type.equals(AWARD)) {
      submittentenRowNum = DATA_STARTING_ROWNUM + 1;
    }

    for (int i = submittentenRowNum; i <= importedSheet.getLastRowNum(); i++) {
      Row uuidrow = importedSheet.getRow(i);
      Cell cell = uuidrow.getCell(0);
      uuids.add(cell.getStringCellValue());
    }
    return uuids;
  }

  /**
   * Gets the calculated value from cell.
   *
   * @param cell the cell
   * @param workBook the work book
   * @return the calculated value from cell
   */
  private BigDecimal getCalculatedValueFromCell(Cell cell, XSSFWorkbook workBook) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method getCalculatedValueFromCell, Parameters: cell: {0}, workBook: {1}",
      new Object[]{cell, workBook});

    try {
      if (cell.getCellTypeEnum().equals(CellType.STRING)
        && (!cell.getStringCellValue().isEmpty())) {
        return new BigDecimal(cell.getStringCellValue());
      } else if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {
        FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);
        return BigDecimal.valueOf(cellValue.getNumberValue());
      } else if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
        return BigDecimal.valueOf(cell.getNumericCellValue());
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e + MALFORMED_INPUT + " @ value parsing.");
      throw new IOException(MALFORMED_INPUT);
    }
    return null;
  }

  /**
   * Gets the big decimal from cell.
   *
   * @param cell the cell
   * @param workBook the work book
   * @return the big decimal from cell
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private BigDecimal getBigDecimalFromCell(Cell cell, XSSFWorkbook workBook) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method getBigDecimalFromCell, Parameters: cell: {0}, workBook: {1}",
      new Object[]{cell, workBook});
    try {
      if (cell.getCellTypeEnum().equals(CellType.STRING)
        && (!cell.getStringCellValue().isEmpty())) {
        return new BigDecimal(cell.getStringCellValue());
      } else if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {

        FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);
        return BigDecimal.valueOf(cellValue.getNumberValue());

      } else if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
        return BigDecimal.valueOf(cell.getNumericCellValue());
      }

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e + MALFORMED_INPUT + " @ value parsing.");
      throw new IOException(MALFORMED_INPUT);
    }
    return null;
  }

  /**
   * Gets the cell title.
   *
   * @param importedSheet the imported sheet
   * @param cell the cell
   * @param titleRownum the title rownum
   * @return the cell title
   */
  private String getCellTitle(XSSFSheet importedSheet, Cell cell, int titleRownum) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCellTitle, Parameters: importedSheet: {0}, cell: {1}, titleRownum: {2}",
      new Object[]{importedSheet, cell, titleRownum});

    Row titleRow = importedSheet.getRow(titleRownum);
    try {
      // Return the String cell value.
      return titleRow.getCell(cell.getColumnIndex()).getStringCellValue();
    } catch (Exception e) {
      // If the String cell value throws an error, return an empty String.
      return StringUtils.EMPTY;
    }
  }

  /**
   * Generates filename with timestamp based on type.
   *
   * @param type the type
   * @param isMail the is mail
   * @return the string
   */
  public String generateExportedFilename(String type, boolean isMail) {

    String timestamp;

    if (isMail) {
      timestamp = new SimpleDateFormat("_ddMMyyyyHHmm'.eml'").format(new Date());
    } else {
      timestamp = new SimpleDateFormat("_ddMMyyyyHHmm'.xlsx'").format(new Date());
    }
    if (type.equals(SUITABILITY)) {
      return "Formelle_Eignungsprüfung" + timestamp;
    } else if (type.equals(AWARD)) {
      return AWARD_LABEL + timestamp;
    }
    return null;
  }

  /**
   * Count document columns.
   *
   * @param offersNum the offers
   * @return the column number
   */
  private int countDocumentColumns(int offersNum) {

    LOGGER.log(Level.CONFIG,
      "Executing method countDocumentColumns, Parameters: offersNum: {0}",
      offersNum);

    // 1 hidden column and 3 columns that are going to be merged
    int staticCols = 4;
    int columnsForEachCompany = 2;

    return staticCols + (offersNum * columnsForEachCompany);
  }

  @Override
  public byte[] exportDocumentOfCriteria(String submissionId, List<SuitabilityDocumentDTO> offers,
    String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method exportTableOfCriteria, Parameters: submissionId: {0}, "
        + "offers: {1}, type: {2}",
      new Object[]{submissionId, offers, type});

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(SHEET_TITLE);

      SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
      List<CriterionDTO> submissionCriteria =
        criterionService.readCriteriaOfSubmission(submissionId);

      // every 4 offer entries we have to repeat
      int currentOfferIndex = 0;
      int repeatOffersCounter = offers.size() / 4;
      int maxCols = countDocumentColumns(4);
      int currentRowIndex = 0;
      // indicates the rows that the data for every set of companies start and end
      int newSetOfCompaniesStart = 0;
      int newSetOfCompaniesStop = 0;
      boolean newSetOfCompanies = false;
      boolean hasMussCriteria;
      boolean hasRatedCriteria;
      boolean hasRatedSubCriteria;
      List<SuitabilityDocumentDTO> currentOffers = new ArrayList<>();

      // map of criteria/subcriteria indexes in order to use at page breaks
      // the first element of list is always the starting row of criteria and the 
      // second element the ending row
      LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap = new LinkedHashMap<>();

      // Find if there are criteria
      hasMussCriteria = criterionExist(offers, MUST_CRITERIA);
      hasRatedCriteria = criterionExist(offers, RATED_CRITERIA);
      hasRatedSubCriteria = ratedSubCriterionExist(offers);

      // SUITABILITY
      // Eignungsprüfungstabelle Qualifying Examination Document
      setDocumentStaticColumnTitle(submission, maxCols, sheet, workbook);
      setDocumentStaticColumnSubTitle(submission, maxCols, currentRowIndex, sheet, workbook, type);

      // if no bewerteten criteria exist then don't print min-max points
      if (hasRatedCriteria) {
        setDocumentMaxMinPoints(submission, maxCols, sheet, workbook);
        currentRowIndex = 6;
      } else {
        currentRowIndex = 4;
      }

      int pageBreakCounter = 1;
      for (int i = 0; i < repeatOffersCounter; i++) {

        for (int j = currentOfferIndex; j < currentOfferIndex + 4; j++) {
          currentOffers.add(offers.get(j));
        }

        // 
        int companiesHeaderIndexRow = 0;
        // Starting row of new set of companies
        newSetOfCompaniesStart = currentRowIndex;
        // Set the company names
        currentRowIndex = setDocumentCompaniesHeader(currentOffers, maxCols, currentRowIndex, sheet,
          workbook, companiesHeaderIndexRow);
        // Set the company overview section
        currentRowIndex =
          setDocumentCompaniesOverview(currentOffers, maxCols, currentRowIndex, sheet, workbook, type);
        // Set the formal exam section
        currentRowIndex =
          fillDocumentFormalExamCells(currentOffers, sheet, workbook, maxCols, currentRowIndex);
        if (hasMussCriteria) {
          // Set the muss criteria section
          currentRowIndex = fillDocumentMussCriteria(submissionCriteria, currentOffers, sheet,
            workbook, maxCols, currentRowIndex);
        }
        if (hasRatedCriteria && hasRatedSubCriteria) {
          // Set the rated criteria and rated subcriteria section
          currentRowIndex = fillDocumentWithRatedSubCriteria(submissionCriteria, currentOffers,
            sheet,
            workbook, maxCols, currentRowIndex, criteriaPageBreakMap);
        } else if (hasRatedCriteria && !hasRatedSubCriteria) {
          // Set the rated criteria section
          currentRowIndex = fillDocumentRatedCriteria(submissionCriteria, currentOffers, sheet,
            workbook, maxCols, currentRowIndex, criteriaPageBreakMap);
        }
        if (hasRatedCriteria || hasMussCriteria) {
          // Set the total points section
          currentRowIndex = fillDocumentTotalPoints(submissionCriteria, currentOffers, sheet,
            workbook, maxCols, currentRowIndex, hasRatedCriteria);
        }
        // Set the status examination section
        currentRowIndex =
          fillDocumentStatusExamination(currentOffers, sheet, workbook, maxCols, currentRowIndex);

        // Ending row of new set of companies
        newSetOfCompaniesStop = currentRowIndex;
        // Set the page breaks
        currentRowIndex = setSuitabilityDocumentPageBreaks(currentOffers, sheet, maxCols, workbook,
          submission, type,
          companiesHeaderIndexRow, newSetOfCompaniesStart, newSetOfCompaniesStop, currentRowIndex,
          newSetOfCompanies, criteriaPageBreakMap, hasRatedSubCriteria);

        /*
         * set page break in order new set of companies to be printed in the next page
         * (if there is only one set of companies there is no need of page break)
         * page break is set to the previous line before column subtitle
         */
        if (pageBreakCounter < repeatOffersCounter) {
          sheet.setRowBreak(currentRowIndex - 1);
          pageBreakCounter++;
          currentRowIndex = setDocumentStaticColumnSubTitle(submission, maxCols,
            currentRowIndex - 1,
            sheet, workbook, type);
          newSetOfCompanies = true;
        }

        currentOffers.clear();
        currentOfferIndex += 4;
      }

      sheet.setDisplayGridlines(false);
      // Hides UUID column..
      sheet.setColumnHidden(0, true);

      setDocumentMargins(sheet);

      sheet.setFitToPage(true);
      sheet.setAutobreaks(false);

      setPrintLayoutSettings(sheet, true);

      // set worksheet footer
      XSSFHeaderFooter footer = (XSSFHeaderFooter) sheet.getFooter();
      footer.setRight(HSSFFooter.font(FONT, "") + HSSFFooter.fontSize((short) FONT_SIZE) + "Seite "
        + HeaderFooter.page() + "/" + HeaderFooter.numPages());

      setDocumentBaseCellWidhts(sheet, maxCols);

      workbook.write(bos);
      bos.close();

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return bos.toByteArray();
  }

  // Method that returns if must or rated criteria exist
  private boolean criterionExist(List<SuitabilityDocumentDTO> offers, String criteriaType) {

    LOGGER.log(Level.CONFIG,
      "Executing method criterionExist, Parameters: offers: {0}, "
        + "criteriaType: {1}",
      new Object[]{offers, criteriaType});

    boolean hasCriteria = false;

    if (criteriaType.equals(MUST_CRITERIA)) {
      for (OfferCriterionDTO offer : offers.get(0).getOffer().getOfferCriteria()) {
        if (offer.getCriterion().getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
          hasCriteria = true;
          break;
        }
      }
    }

    if (criteriaType.equals(RATED_CRITERIA)) {
      for (OfferCriterionDTO offer : offers.get(0).getOffer().getOfferCriteria()) {
        if (offer.getCriterion().getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
          hasCriteria = true;
          break;
        }
      }
    }
    return hasCriteria;
  }

  /**
   * Fill document formal exam cells.
   *
   * @param offers the offers
   * @param sheet the sheet
   * @param workbook the workbook
   */
  private int fillDocumentFormalExamCells(List<SuitabilityDocumentDTO> offers, XSSFSheet sheet,
    XSSFWorkbook workbook, int maxCols, int formalExamsRow) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillDocumentFormalExamCells, Parameters: offers: {0}, sheet: {1}, "
        + "workBook: {2}, maxCols: {3}, formalExamsRow: {4}",
      new Object[]{offers, sheet, workbook, maxCols, formalExamsRow});

    int documentFormalExamRow = formalExamsRow;
    int dataColumn = 0;
    int headerColumn = 1;
    int companyIndex = 0;

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);

    for (int i = 0; i < 4; i++) {
      Row row = sheet.createRow(documentFormalExamRow + i);
      companyIndex = 0;
      dataColumn = 5;
      for (int j = 0; j < maxCols; j++) {
        Cell cell = row.createCell(j);

        if (i == 0) {
          if (j == headerColumn) {
            cell.setCellValue("Formelle Prüfung");
            cell.setCellStyle(headersStyleItalic);
          } else if (j > headerColumn && j < 4) {
            cell.setCellStyle(headersStyleItalic);
          } else if (j > 3) {
            if (((companyIndex - 1) % 2 == 0)) {
              cell.setCellStyle(greyBackground);
            } else {
              cell.setCellStyle(whiteBackground);
            }
          }
        }

        if (i == 1) {
          if (j == headerColumn) {
            cell.setCellValue("Ausschlussgründe gem. Art 24, ÖBV");
            cell.setCellStyle(headersStyle);
          } else if (j > headerColumn && j < 4) {
            cell.setCellStyle(headersStyle);
          } else if (j > 3 && j == dataColumn) {
            cell.setCellValue(offers.get(companyIndex).getExistsExclusionReasons());
            companyIndex++;
            dataColumn += 2;
          }
        }

        if (i == 2) {
          if (j == headerColumn) {
            cell.setCellValue("Nachweisstatus");
            cell.setCellStyle(headersStyle);
          } else if (j > headerColumn && j < 4) {
            cell.setCellStyle(headersStyle);
          } else if (j > 3 && j == dataColumn) {
            cell.setCellValue(offers.get(companyIndex).getFormalExaminationFulfilled());
            companyIndex++;
            dataColumn += 2;
          }
        }

        if (i == 3) {
          if (j == headerColumn) {
            cell.setCellValue("Formelle Prüfung erfüllt");
            cell.setCellStyle(headersStyleBold);
          } else if (j > headerColumn && j < 4) {
            cell.setCellStyle(headersStyleBold);
          } else if (j > 3 && j == dataColumn) {
            if (offers.get(companyIndex).getFormalExaminationFulfilled() != null) {
              cell.setCellValue(
                offers.get(companyIndex).getFormalExaminationFulfilled().toUpperCase());
            }
            companyIndex++;
            dataColumn += 2;
          }
        }
      }
    }

    // set layout companies info
    for (int i = 0; i < 4; i++) {
      Row row = sheet.getRow(documentFormalExamRow + i);
      dataColumn = 4;
      for (int k = 0; k < offers.size(); k++) {
        if (k % 2 == 0) {
          row.getCell(dataColumn).setCellStyle(greyBackground);
          row.getCell(dataColumn + 1).setCellStyle(greyBackground);
        } else {
          row.getCell(dataColumn).setCellStyle(whiteBackground);
          row.getCell(dataColumn + 1).setCellStyle(whiteBackground);
        }
        if (k % 2 == 0 && i == 3) {
          row.getCell(dataColumn).setCellStyle(greyBackgroundBold);
          row.getCell(dataColumn + 1).setCellStyle(greyBackgroundBold);
        } else if (k % 2 != 0 && i == 3) {
          row.getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          row.getCell(dataColumn + 1).setCellStyle(whiteBackgroundBold);
        }
        dataColumn += 2;
      }
    }

    // merge cells
    for (int i = 0; i < 4; i++) {
      headerColumn = 1;
      mergeCell(sheet, documentFormalExamRow + i, headerColumn, 2);
    }

    dataColumn = 4;
    for (int i = 0; i < offers.size(); i++) {
      mergeCell(sheet, documentFormalExamRow, dataColumn, 1);
      sheet.addMergedRegion(new CellRangeAddress(documentFormalExamRow + 1,
        documentFormalExamRow + 3, dataColumn, dataColumn));
      dataColumn += 2;
    }

    return documentFormalExamRow + 5;
  }

  /**
   * Fill the document with the company overview section.
   *
   * @param offers the offers
   * @param maxCols the max columns of the document
   * @param overviewStartRow the position of the current row
   * @param sheet the sheet
   * @param workbook the workbook
   * @param type the type
   * @return the the position of the row after filling the document
   */
  private int setDocumentCompaniesOverview(List<SuitabilityDocumentDTO> offers, int maxCols,
    int overviewStartRow, XSSFSheet sheet, XSSFWorkbook workbook, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillDocumentFormalExamCells, Parameters: offers: {0}, "
        + "maxCols: {1}, overviewStartRow: {2}, sheet: {3}, workbook: {4}, type: {5}",
      new Object[]{offers, maxCols, overviewStartRow, sheet, workbook, type});

    // The Suitability Notes row position
    int overviewSuitabilityNotesRow;
    int dataColumn;
    // A hash map with the table details
    Map<String, Integer> tableIndexMapping = getCompanyOverviewTableSize(offers, null, type);

    int tableRows = tableIndexMapping.get(TABLE_ROWS);
    int maxCellNumArge = tableIndexMapping.get(MAX_CELL_NUM_ARGE);
    int maxCellNumSubContractors = tableIndexMapping.get(MAX_CELL_NUM_SUBCONTRACTORS);

    // The ending row of Partner Companies (ARGE and SubUnternehmen)
    int overviewPartnerCompaniesEndRow = maxCellNumArge + maxCellNumSubContractors;
    // The position of Offer Info
    int overviewOfferInfoRow = overviewStartRow + overviewPartnerCompaniesEndRow;

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    // Cell styling
    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    headersStyle.setFont(titleFont);

    fillCompanyOverviewTable(tableRows, maxCellNumArge, maxCellNumSubContractors, overviewStartRow, maxCols, sheet, headersStyle,
      greyBackground, whiteBackground, offers, null, type);

    int headerColum = 1;
    // merge cells for companies
    for (int i = 0; i < tableRows; i++) {
      mergeCell(sheet, overviewStartRow + i, headerColum, 2, false);
      dataColumn = 4;
      for (int j = 0; j < offers.size(); j++) {
        mergeCell(sheet, overviewStartRow + i, dataColumn, 1, false);
        dataColumn += 2;
      }
      autosizeRow(overviewStartRow + i, sheet, maxCols);
    }

    // fix row height for offer information
    autosizeRow(overviewOfferInfoRow, sheet, maxCols, MERGED_CELLS_WIDTH_IN_CHARS);
    /*
     * The Suitability Notes row is always
     * 1 row bellow the Offer Information row.
     */
    overviewSuitabilityNotesRow = overviewOfferInfoRow + 1;
    // fix row height for company suitability notes
    autosizeRow(overviewSuitabilityNotesRow, sheet, maxCols, MERGED_CELLS_WIDTH_IN_CHARS);

    // return the starting row position of the next table section
    return overviewSuitabilityNotesRow + 2;
  }

  private Map<String, Integer> getCompanyOverviewTableSize(List<SuitabilityDocumentDTO> suitabilityOffers,
    List<AwardEvaluationDocumentDTO> awardEvaluationOffers, String type) {

    Map<String, Integer> indexMapping = new HashMap<>();

    // Checking if the document type is Suitability
    boolean isTypeSuitability = type.equals(TemplateConstants.SUITABILITY);

    // The number of cells added for ARGE values
    int maxCellNumArge = 1;

    // The number of cells added for SubContractors values
    int maxCellNumSubContractors = 1;

    if (isTypeSuitability) {
      for (SuitabilityDocumentDTO offer : suitabilityOffers) {
        String argeValue = addExtraLine(offer.getJointVentures());
        String subContractorValue = addExtraLine(offer.getSubContractors());
        if (addExtraRows(argeValue) > maxCellNumArge) {
          maxCellNumArge = addExtraRows(offer.getJointVentures());
        }
        if (addExtraRows(subContractorValue) > maxCellNumSubContractors) {
          maxCellNumSubContractors = addExtraRows(offer.getSubContractors());
        }
      }
    } else {
      for (AwardEvaluationDocumentDTO offer : awardEvaluationOffers) {
        String argeValue = addExtraLine(offer.getJointVentures());
        String subContractorValue = addExtraLine(offer.getSubContractors());
        if (addExtraRows(argeValue) > maxCellNumArge) {
          maxCellNumArge = addExtraRows(offer.getJointVentures());
        }
        if (addExtraRows(subContractorValue) > maxCellNumSubContractors) {
          maxCellNumSubContractors = addExtraRows(offer.getSubContractors());
        }
      }
    }

    // The size of the Company Overview table
    indexMapping.put(TABLE_ROWS,(isTypeSuitability) ? maxCellNumArge + maxCellNumSubContractors + 2 : maxCellNumArge + maxCellNumSubContractors + 1);
    indexMapping.put(MAX_CELL_NUM_ARGE, maxCellNumArge);
    indexMapping.put(MAX_CELL_NUM_SUBCONTRACTORS, maxCellNumSubContractors);
    return indexMapping;
  }

  private int setDocumentCompaniesHeader(List<SuitabilityDocumentDTO> offers, int maxCols,
    int currentRowIndex, XSSFSheet sheet, XSSFWorkbook workbook, int companiesHeaderIndexRow) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentCompaniesHeader, Parameters: offers: {0}, maxCols: {1}, "
        + "currentRowIndex: {2}, sheet: {3}, workbook: {4}, companiesHeaderIndexRow: {5}",
      new Object[]{offers, maxCols, currentRowIndex, sheet, workbook, companiesHeaderIndexRow});

    int companyNamesRow = currentRowIndex;
    int dataColumn = 4;
    int companyIndex = 0;

    // background color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    // Border color
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    short borderColorIndex = borderColor.getIndex();

    CellStyle companyNameStyle = workbook.createCellStyle();
    companyNameStyle.setLocked(true);
    companyNameStyle.setAlignment(HorizontalAlignment.CENTER);
    companyNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    companyNameStyle.setBorderRight(BorderStyle.MEDIUM);
    companyNameStyle.setRightBorderColor(borderColorIndex);
    companyNameStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyNameStyle.setLeftBorderColor(borderColorIndex);
    companyNameStyle.setBorderTop(BorderStyle.MEDIUM);
    companyNameStyle.setTopBorderColor(borderColorIndex);
    companyNameStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyNameStyle.setBottomBorderColor(borderColorIndex);
    companyNameStyle.setWrapText(true);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyNameStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);
    titleFont.setBold(true);

    greyBackground.setFont(titleFont);
    companyNameStyle.setFont(titleFont);

    Row row = sheet.createRow(companyNamesRow);
    for (int i = 0; i < maxCols; i++) {
      Cell cell = row.createCell(i);
      if (i < 4) {
        continue;
      }
      if (i == dataColumn) {
        cell.setCellValue(offers.get(companyIndex).getCompanyName());
        companyIndex++;
        dataColumn += 2;
      }
      if ((companyIndex - 1) % 2 == 0) {
        cell.setCellStyle(greyBackground);
      } else {
        cell.setCellStyle(companyNameStyle);
      }
    }

    // set the row index in order to calculate later the row height
    // for page break
    companiesHeaderIndexRow = companyNamesRow;

    // set data column to 4 again to merge cells
    dataColumn = 4;
    for (int i = 0; i < offers.size(); i++) {
      mergeCell(sheet, companyNamesRow, dataColumn, 1);
      dataColumn += 2;
    }
    // auto size rows with merged cell
    autosizeRow(companyNamesRow, sheet, maxCols, MERGED_CELLS_WIDTH_IN_CHARS);

    // check if row height is less than the default row height
    // for companies name row
    float minDefaultHeight = row.getHeightInPoints();
    if (minDefaultHeight < 30) {
      row.setHeightInPoints((float) 30); // approximately 33px
    }
    return currentRowIndex + 1;
  }

  private void setDocumentMaxMinPoints(SubmissionDTO submission, int maxCols, XSSFSheet sheet,
    XSSFWorkbook workbook) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentMaxMinPoints, Parameters: submission: {0}, "
        + "maxCols: {1}, sheet: {2}, workBook: {3}",
      new Object[]{submission, maxCols, sheet, workbook});

    int maxScoreRow = 3;
    int minScoreRow = 4;

    CellStyle titleStyle = workbook.createCellStyle();
    titleStyle.setLocked(true);
    titleStyle.setAlignment(HorizontalAlignment.LEFT);
    titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);
    titleFont.setBold(true);
    titleFont.setColor(IndexedColors.WHITE.index);
    titleStyle.setFont(titleFont);
    titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);

    // Max - Min Score title style
    CellStyle scoreTitleStyle = workbook.createCellStyle();
    titleStyle.setAlignment(HorizontalAlignment.LEFT);
    titleStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    titleStyle.setWrapText(true);

    // Max - Min Score title font style
    XSSFFont scoreTitleFont = workbook.createFont();
    scoreTitleFont.setFontHeight(FONT_SIZE);
    scoreTitleFont.setFontName(FONT);
    titleStyle.setWrapText(true);

    // Max - Min Score title font style
    XSSFFont scoreTitleFontBold = workbook.createFont();
    scoreTitleFontBold.setFontHeight(FONT_SIZE);
    scoreTitleFontBold.setFontName(FONT);
    scoreTitleFontBold.setBold(true);
    titleStyle.setWrapText(true);

    String maxScore =
      MAX_SCORE + (submission.getMaxGrade() != null ? submission.getMaxGrade() : DEFAULT_MAXNOTE);
    String minScore = MIN_SCORE
      + (submission.getMinGrade() != null ? submission.getMinGrade() : DEFAULT_MIN_NOTE);

    for (int i = maxScoreRow; i <= minScoreRow; i++) {
      Row row = sheet.createRow(i);
      for (int j = 0; j < maxCols; j++) {
        Cell cell = row.createCell(j);
        // only in this case we fill the max and min score in order to
        // to convert part of the string to bold
        if (j == 1 && i == maxScoreRow) {
          cell.setCellValue(maxScore);
          RichTextString boldMaxScore = cell.getRichStringCellValue();
          boldMaxScore.applyFont(scoreTitleFont);
          boldMaxScore.applyFont(0, 11, scoreTitleFontBold);
          row.getCell(j).setCellValue(boldMaxScore);
          row.getCell(j).setCellStyle(scoreTitleStyle);
          row.setRowStyle(scoreTitleStyle);
        } else if (j == 1 && i == minScoreRow) {
          cell.setCellValue(minScore);
          RichTextString boldMinScore = cell.getRichStringCellValue();
          boldMinScore.applyFont(scoreTitleFont);
          boldMinScore.applyFont(0, 11, scoreTitleFontBold);
          row.getCell(1).setCellValue(boldMinScore);
          row.getCell(1).setCellStyle(scoreTitleStyle);
        }
      }
    }

    // create empty row after min score
    Row row = sheet.createRow(minScoreRow + 1);
    for (int j = 0; j < maxCols; j++) {
      row.createCell(j);
    }
  }

  private void setDocumentBaseCellWidhts(XSSFSheet sheet, int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentBaseCellWidhts, Parameters: sheet: {0}, "
        + "maxCols: {1}",
      new Object[]{sheet, maxCols});

    // the width in units of 1/256th of a character width
    int cellWidth = 37 * CARACTER_WIDTH;
    int numWidth = 20 * CARACTER_WIDTH;

    sheet.setColumnWidth(1, cellWidth);
    sheet.setColumnWidth(2, (1720));
    sheet.setColumnWidth(3, (1720));

    for (int i = 4; i < maxCols; i++) {
      sheet.setColumnWidth(i, (numWidth));
    }
  }

  /**
   * Fill document muss criteria.
   *
   * @param offers the offers
   * @param sheet the sheet
   * @param workbook the workbook
   */
  private int fillDocumentMussCriteria(List<CriterionDTO> submissionCriteria,
    List<SuitabilityDocumentDTO> offers, XSSFSheet sheet, XSSFWorkbook workbook, int maxCols,
    int formalExamsEndRow) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillDocumentMussCriteria, Parameters: submissionCriteria: {0}, "
        + "offers: {1}, sheet: {2}, workBook: {3}, maxCols: {4}, formalExamsEndRow: {5}",
      new Object[]{submissionCriteria, offers, sheet, workbook, maxCols, formalExamsEndRow});

    int mussTitleStartRow = formalExamsEndRow;
    int mussCriteriaStartRow = formalExamsEndRow + 1;
    // total rows of muss criteria
    // 1 title + 1 Summary Results + total number of muss criteria
    int mussCriteriaTotalRows = 2;
    int titleColumn = 1;
    int dataColumn = 0;
    HashMap<String, CriterionDTO> mussCriteriaMap = new LinkedHashMap<>();

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle noVerticalBorders = workbook.createCellStyle();
    noVerticalBorders.setLocked(true);
    noVerticalBorders.setBorderRight(BorderStyle.MEDIUM);
    noVerticalBorders.setRightBorderColor(borderColorIndex);
    noVerticalBorders.setBorderLeft(BorderStyle.MEDIUM);
    noVerticalBorders.setLeftBorderColor(borderColorIndex);
    noVerticalBorders.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundNoBorders = workbook.createCellStyle();
    greyBackgroundNoBorders.cloneStyleFrom(noVerticalBorders);
    greyBackgroundNoBorders.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundNoBorders.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundNoBorders.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundNoBorders.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundNoBorders = workbook.createCellStyle();
    whiteBackgroundNoBorders.cloneStyleFrom(noVerticalBorders);
    whiteBackgroundNoBorders.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundNoBorders.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);

    // In muss criteria we will first create the rows and cells for each row
    // then we will fill with our data
    // at last we will set the style for each cell

    for (CriterionDTO criterion : submissionCriteria) {
      if (criterion.getCriterionType().equals(LookupValues.MUST_CRITERION_TYPE)) {
        mussCriteriaMap.put(criterion.getId(), criterion);
      }
    }

    mussCriteriaTotalRows += mussCriteriaMap.size();

    // Create Rows and Cells
    for (int i = 0; i < mussCriteriaTotalRows; i++) {
      Row row = sheet.createRow(mussTitleStartRow + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    // Fill with data
    // First we set the Muss Kritirien title
    for (int i = 0; i < 1; i++) {
      Row row = sheet.getRow(mussTitleStartRow);
      for (int j = 0; j < maxCols; j++) {
        if (j == titleColumn) {
          row.getCell(j).setCellValue("Muss-Kriterien");
          row.getCell(j).setCellStyle(headersStyleItalic);
          row.getCell(j + 1).setCellStyle(headersStyleItalic);
          row.getCell(j + 2).setCellStyle(headersStyleItalic);
        }
      }
    }

    int mussCriteriaCount = mussCriteriaStartRow;
    // We fill the criteria
    for (Entry<String, CriterionDTO> entry : mussCriteriaMap.entrySet()) {
      Row row = sheet.getRow(mussCriteriaCount);
      row.getCell(titleColumn).setCellValue(entry.getValue().getCriterionText());
      row.getCell(titleColumn).setCellStyle(headersStyle);
      row.getCell(titleColumn + 1).setCellStyle(headersStyle);
      row.getCell(titleColumn + 2).setCellStyle(headersStyle);
      mussCriteriaCount++;
      dataColumn = 5;
      for (SuitabilityDocumentDTO offer : offers) {
        for (OfferCriterionDTO offerCriteria : offer.getOffer().getOfferCriteria()) {
          if (offerCriteria.getCriterion().getId().equals(entry.getKey())) {
            row.getCell(dataColumn).setCellValue(castToYesOrNo(offerCriteria.getIsFulfilled()));
            dataColumn += 2;
            continue;
          }
        }
      }
    }

    // Fill row for muss criteria summary
    int mussCriteriaSummaryRow = mussCriteriaCount;
    for (int i = 0; i < 1; i++) {
      Row row = sheet.getRow(mussCriteriaSummaryRow);
      row.getCell(titleColumn).setCellValue("Muss-Kriterien erfüllt");
      row.getCell(titleColumn).setCellStyle(headersStyleBold);
      row.getCell(titleColumn + 1).setCellStyle(headersStyleBold);
      row.getCell(titleColumn + 2).setCellStyle(headersStyleBold);
      dataColumn = 5;
      for (SuitabilityDocumentDTO offer : offers) {
        if (offer.getMussCriterienSummary() != null) {
          row.getCell(dataColumn).setCellValue(offer.getMussCriterienSummary().toUpperCase());
        }
        dataColumn += 2;
      }
    }

    // Append style and merge title cells
    // append style for title row
    for (int i = 0; i < 1; i++) {
      Row row = sheet.getRow(mussTitleStartRow);
      mergeCell(sheet, mussTitleStartRow, titleColumn, 2);
      dataColumn = 4;
      for (int k = 0; k < offers.size(); k++) {
        if (k % 2 == 0) {
          row.getCell(dataColumn).setCellStyle(greyBackground);
          row.getCell(dataColumn + 1).setCellStyle(greyBackground);
        } else {
          row.getCell(dataColumn).setCellStyle(whiteBackground);
          row.getCell(dataColumn + 1).setCellStyle(whiteBackground);
        }
        mergeCell(sheet, mussTitleStartRow, dataColumn, 1);
        dataColumn += 2;
      }
    }

    // append style for the rest rows of muss criteria
    // remove 1 from mussCriteriaTotalRows (heading all ready filled)
    mussCriteriaTotalRows--;
    for (int i = 0; i < mussCriteriaTotalRows; i++) {
      Row row = sheet.getRow(mussCriteriaStartRow + i);
      mergeCell(sheet, mussCriteriaStartRow + i, titleColumn, 2);
      dataColumn = 4;
      for (int k = 0; k < offers.size(); k++) {
        if (k % 2 == 0) {
          row.getCell(dataColumn).setCellStyle(greyBackground);
          row.getCell(dataColumn + 1).setCellStyle(greyBackground);
        } else {
          row.getCell(dataColumn).setCellStyle(whiteBackground);
          row.getCell(dataColumn + 1).setCellStyle(whiteBackground);
        }
        if (k % 2 == 0 && (i == mussCriteriaTotalRows - 1)) {
          row.getCell(dataColumn).setCellStyle(greyBackgroundBold);
          row.getCell(dataColumn + 1).setCellStyle(greyBackgroundBold);
        } else if (k % 2 != 0 && (i == mussCriteriaTotalRows - 1)) {
          row.getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          row.getCell(dataColumn + 1).setCellStyle(whiteBackgroundBold);
        }
        dataColumn += 2;
      }
    }

    // merge cells vertically from muss criteria title until muss criteria summary
    dataColumn = 4;
    for (int i = 0; i < offers.size(); i++) {
      sheet.addMergedRegion(new CellRangeAddress(mussCriteriaStartRow,
        mussCriteriaStartRow + mussCriteriaMap.size(), dataColumn, dataColumn));
      dataColumn += 2;
    }

    // auto size row height for merged cell rows
    for (int i = 0; i < mussCriteriaMap.size(); i++) {
      autosizeRow(mussCriteriaStartRow + i, sheet, maxCols);
    }

    // create the empty row after muss criteria summary
    Row row = sheet.createRow(mussCriteriaSummaryRow + 1);
    for (int i = 0; i < maxCols; i++) {
      row.createCell(i);
    }

    // Return the index after muss criteria summary and 1 empty row
    return mussCriteriaSummaryRow + 2;
  }

  private String castToYesOrNo(Boolean isFulfilled) {

    LOGGER.log(Level.CONFIG,
      "Executing method castToYesOrNo, Parameters: isFulfilled: {0}", isFulfilled);

    if (isFulfilled != null) {
      return (isFulfilled ? TemplateConstants.YES : TemplateConstants.NO);
    }
    return TemplateConstants.EMPTY_STRING;
  }

  private int fillDocumentWithRatedSubCriteria(List<CriterionDTO> submissionCriteria,
    List<SuitabilityDocumentDTO> offers, XSSFSheet sheet, XSSFWorkbook workbook, int maxCols,
    int currentRowIndex, LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillDocumentWithRatedSubCriteria, Parameters: "
        + "submissionCriteria: {0}, offers: {1}, sheet: {2}, workBook: {3}, maxCols: {4}, "
        + "currentRowIndex: {5}, criteriaPageBreakMap: {6}",
      new Object[]{submissionCriteria, offers, sheet, workbook,
        maxCols, currentRowIndex, criteriaPageBreakMap});

    int ratedCriteriaStartRow = currentRowIndex;
    int titleColumn = 1;
    HashMap<String, CriterionDTO> ratedCriteriaMap = new LinkedHashMap<>();
    DecimalFormat formatter = new DecimalFormat("0.00");

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);

    // find evaluated criteria and sort them
    List<CriterionDTO> evaluatedCriteriaList = new ArrayList<>();

    for (CriterionDTO criterion : submissionCriteria) {
      if (criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        evaluatedCriteriaList.add(criterion);
      }
    }

    Collections.sort(evaluatedCriteriaList, ComparatorUtil.criteriaWithWeightings);
    for (CriterionDTO criterionDTO : evaluatedCriteriaList) {
      ratedCriteriaMap.put(criterionDTO.getId(), criterionDTO);
    }

    int rowsCounter = 0;
    // first we create the rows and cells for all rated criteria

    for (Entry<String, CriterionDTO> entry : ratedCriteriaMap.entrySet()) {
      rowsCounter += 3;
      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        for (int i = 0; i < entry.getValue().getSubcriterion().size(); i++) {
          rowsCounter++;
        }
      }
    }

    for (int i = 0; i < rowsCounter; i++) {
      Row row = sheet.createRow(ratedCriteriaStartRow + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    int ratedCriteraIndex = ratedCriteriaStartRow;
    // insert values to rows
    for (Entry<String, CriterionDTO> entry : ratedCriteriaMap.entrySet()) {
      int dataColumn = 4;

      // List that keeps the start and end row of each criterion
      // First element is always the starting row - Last element
      // is the ending row
      List<Integer> criteriaStartEndIndexes = new ArrayList<>();
      criteriaStartEndIndexes.add(ratedCriteraIndex);

      // set header and header style
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn).setCellValue("Bewertetes Kriterium");
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleItalic);
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellValue("G");
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellValue("UK");
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleBoldCenter);

      for (int i = 0; i < offers.size(); i++) {
        if (i % 2 == 0) {
          sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(greyBackgroundBold);
          sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackgroundBold);
        }
        if (i % 2 != 0) {
          sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackgroundBold);
        }
        sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellValue(GRADE);
        sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellValue(POINTS);
        dataColumn += 2;
      }

      // set criterion description and points
      ratedCriteraIndex++;
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn)
        .setCellValue(entry.getValue().getCriterionText());
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleBold);
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1)
        .setCellValue(formatter.format(entry.getValue().getWeighting()));
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
      headersStyleCenter.setDataFormat(workbook.createDataFormat().getFormat("#.00"));

      // autosize row height
      autosizeRow(ratedCriteraIndex, sheet, maxCols, CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);

      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2)
          .setCellValue(getSubCriteriaTotalWeight(entry.getValue().getSubcriterion()));
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleCenter);
      } else {
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellValue(CELL_VALUE);
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleCenter);
      }

      dataColumn = 4;
      int companyIndex = 0;
      for (SuitabilityDocumentDTO offer : offers) {
        for (OfferCriterionDTO offerCriteria : offer.getOffer().getOfferCriteria()) {
          if (offerCriteria.getCriterion().getId().equals(entry.getKey())) {
            if (companyIndex % 2 == 0) {
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(greyBackground);
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackground);
            }
            if (companyIndex % 2 != 0) {
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackground);
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackground);
            }
            if (offerCriteria.getGrade() != null) {
              BigDecimal gradeValue = offerCriteria.getGrade().setScale(2);
              setValueToField(gradeValue.toString(),
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn));
            }
            if (offerCriteria.getScore() != null) {
              BigDecimal scoreValue = offerCriteria.getScore().setScale(3);
              setValueToField(scoreValue.toString(),
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1));
            }
            dataColumn += 2;
            continue;
          }
        }
        companyIndex++;
      }
      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        ratedCriteraIndex = fillDocumentRatedSubCriteria(offers, ratedCriteraIndex, maxCols,
          entry.getValue().getSubcriterion(), sheet, workbook);
      } else {
        ratedCriteraIndex++;
      }
      criteriaStartEndIndexes.add(ratedCriteraIndex - 1);
      criteriaPageBreakMap.put(entry.getValue(), criteriaStartEndIndexes);
      ratedCriteraIndex++;
    }
    return ratedCriteraIndex;
  }

  private int fillDocumentRatedCriteria(List<CriterionDTO> submissionCriteria,
    List<SuitabilityDocumentDTO> currentOffers, XSSFSheet sheet, XSSFWorkbook workbook,
    int maxCols, int currentRowIndex,
    LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap) {
    int ratedCriteriaStartRow = currentRowIndex;
    int titleColumn = 1;
    HashMap<String, CriterionDTO> ratedCriteriaMap = new LinkedHashMap<>();
    DecimalFormat formatter = new DecimalFormat("0.00");

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);

    // create a dummy criterion only for page break usage
    CriterionDTO pageBreakCriterion = new CriterionDTO();

    // find evaluated criteria and sort them
    List<CriterionDTO> evaluatedCriteriaList = new ArrayList<>();

    for (CriterionDTO criterion : submissionCriteria) {
      if (criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        evaluatedCriteriaList.add(criterion);
      }
    }

    Collections.sort(evaluatedCriteriaList, ComparatorUtil.criteriaWithWeightings);
    for (CriterionDTO criterionDTO : evaluatedCriteriaList) {
      ratedCriteriaMap.put(criterionDTO.getId(), criterionDTO);
    }

    int rowsCounter = 0;
    // first we create the rows and cells for all rated criteria

    for (Entry<String, CriterionDTO> entry : ratedCriteriaMap.entrySet()) {
      rowsCounter += 3;
      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        for (int i = 0; i < entry.getValue().getSubcriterion().size(); i++) {
          rowsCounter++;
        }
      }
    }

    for (int i = 0; i < rowsCounter; i++) {
      Row row = sheet.createRow(ratedCriteriaStartRow + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    int dataColumn = 4;
    int ratedCriteraIndex = ratedCriteriaStartRow;
    // List that keeps the start and end row of each criterion
    // First element is always the starting row - Last element
    // is the ending row
    List<Integer> criteriaStartEndIndexes = new ArrayList<>();
    criteriaStartEndIndexes.add(ratedCriteraIndex + 1);

    // set header and header style
    sheet.getRow(ratedCriteraIndex).getCell(titleColumn).setCellValue(RATED_CRITERIA);
    sheet.getRow(ratedCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleItalic);
    sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellValue("");
    sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
    sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellValue("G");
    sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleBoldCenter);

    for (int i = 0; i < currentOffers.size(); i++) {
      if (i % 2 == 0) {
        sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(greyBackgroundBold);
        sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackgroundBold);
      }
      if (i % 2 != 0) {
        sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
        sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackgroundBold);
      }
      sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellValue(GRADE);
      sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellValue(POINTS);
      dataColumn += 2;
    }

    // insert values to rows
    for (Entry<String, CriterionDTO> entry : ratedCriteriaMap.entrySet()) {
      // set criterion description and points
      ratedCriteraIndex++;
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn)
        .setCellValue(entry.getValue().getCriterionText());
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn).setCellStyle(headersStyle);
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellValue("");
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2)
        .setCellValue(formatter.format(entry.getValue().getWeighting()));
      headersStyleCenter.setDataFormat(workbook.createDataFormat().getFormat("#.00"));
      sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleBoldCenter);

      // autosize row height
      autosizeRow(ratedCriteraIndex, sheet, maxCols, CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);

      dataColumn = 4;
      int companyIndex = 0;
      for (SuitabilityDocumentDTO offer : currentOffers) {
        for (OfferCriterionDTO offerCriteria : offer.getOffer().getOfferCriteria()) {
          if (offerCriteria.getCriterion().getId().equals(entry.getKey())) {
            if (companyIndex % 2 == 0) {
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(greyBackground);
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackground);
            }
            if (companyIndex % 2 != 0) {
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackground);
              sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackground);
            }
            if (offerCriteria.getGrade() != null) {
              BigDecimal gradeValue = offerCriteria.getGrade().setScale(2);
              setValueToField(gradeValue.toString(),
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn));
            }
            if (offerCriteria.getScore() != null) {
              BigDecimal scoreValue = offerCriteria.getScore().setScale(3);
              setValueToField(scoreValue.toString(),
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1));
            }
            dataColumn += 2;
            continue;
          }
        }
        companyIndex++;
      }
    }
    criteriaStartEndIndexes.add(ratedCriteraIndex);
    criteriaPageBreakMap.put(pageBreakCriterion, criteriaStartEndIndexes);
    return ratedCriteraIndex + 2;
  }

  private String getSubCriteriaTotalWeight(List<SubcriterionDTO> subcriterion) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubCriteriaTotalWeight, Parameters: subcriterion: {0}",
      subcriterion);

    DecimalFormat formatter = new DecimalFormat("0.00");
    Double totalWeight = 0.00;
    for (SubcriterionDTO subcriterionDTO : subcriterion) {
      if (subcriterionDTO.getWeighting() != null) {
        totalWeight += subcriterionDTO.getWeighting();
      }
    }
    return formatter.format(totalWeight);
  }

  private int fillDocumentRatedSubCriteria(List<SuitabilityDocumentDTO> offers,
    int ratedCriteraIndex, int maxCols, List<SubcriterionDTO> list,
    XSSFSheet sheet, XSSFWorkbook workbook) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillDocumentRatedSubCriteria, Parameters: offers: {0}, "
        + "ratedCriteraIndex: {1}, maxCols: {2}, list: {3}, sheet: {4}, workBook: {5}",
      new Object[]{offers, ratedCriteraIndex, maxCols, list, sheet, workbook});

    DecimalFormat formatter = new DecimalFormat("0.00");
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    // Border color
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalicNine = workbook.createCellStyle();
    headersStyleItalicNine.cloneStyleFrom(headersStyle);
    headersStyleItalicNine.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalicNine.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenterNine = workbook.createCellStyle();
    headersStyleBoldCenterNine.cloneStyleFrom(headersStyle);
    headersStyleBoldCenterNine.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenterNine.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundNine = workbook.createCellStyle();
    greyBackgroundNine.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundNine.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundNine.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundNine.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundNine.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundNine = workbook.createCellStyle();
    whiteBackgroundNine.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundNine.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundNine.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontNine = workbook.createFont();
    titleFontNine.setFontName(FONT);
    titleFontNine.setFontHeight(FONT_SIZE_NINE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    XSSFFont titleFontBoldNine = workbook.createFont();
    titleFontBoldNine.setFontName(FONT);
    titleFontBoldNine.setFontHeight(FONT_SIZE_NINE);
    titleFontBoldNine.setBold(true);
    titleFontBoldNine.setItalic(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundNine.setFont(titleFontNine);
    whiteBackgroundNine.setFont(titleFontNine);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);
    headersStyleItalicNine.setFont(titleFontItalicNine);
    headersStyleBoldCenterNine.setFont(titleFontBoldNine);

    // fill rows with data and append style
    int titleColumn = 1;
    ratedCriteraIndex++;
    int companyIndex;
    if (list != null && !list.isEmpty()) {
      for (SubcriterionDTO subCriterion : list) {
        int dataColumn = 4;
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn)
          .setCellValue(subCriterion.getSubcriterionText());
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleItalicNine);
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 1).setCellStyle(whiteBackground);
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2)
          .setCellValue(formatter.format(subCriterion.getWeighting()));
        sheet.getRow(ratedCriteraIndex).getCell(titleColumn + 2)
          .setCellStyle(headersStyleBoldCenterNine);

        // autosize row of subcriterion text
        autosizeRow(ratedCriteraIndex, sheet, maxCols, CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);

        companyIndex = 0;
        for (SuitabilityDocumentDTO offer : offers) {
          for (OfferSubcriterionDTO offerSubCriterion : offer.getOffer().getOfferSubcriteria()) {
            if (offerSubCriterion.getSubcriterion().getId().equals(subCriterion.getId())) {
              // set style for row's cells
              if (companyIndex % 2 == 0) {
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn)
                  .setCellStyle(greyBackgroundNine);
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1)
                  .setCellStyle(greyBackgroundNine);
              }
              if (companyIndex % 2 != 0) {
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn)
                  .setCellStyle(whiteBackgroundNine);
                sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1)
                  .setCellStyle(whiteBackgroundNine);
              }
              if (offerSubCriterion.getGrade() != null) {
                BigDecimal gradeValue = offerSubCriterion.getGrade().setScale(2);
                setValueToField(gradeValue.toString(),
                  sheet.getRow(ratedCriteraIndex).getCell(dataColumn));
              }
              if (offerSubCriterion.getScore() != null) {
                BigDecimal scoreValue = offerSubCriterion.getScore().setScale(3);
                setValueToField(scoreValue.toString(),
                  sheet.getRow(ratedCriteraIndex).getCell(dataColumn + 1));
              }
              dataColumn += 2;
              continue;
            }
          }
          companyIndex++;
        }
        ratedCriteraIndex++;
      }
    }
    return ratedCriteraIndex;
  }

  private int fillDocumentTotalPoints(List<CriterionDTO> submissionCriteria,
    List<SuitabilityDocumentDTO> offers, XSSFSheet sheet, XSSFWorkbook workbook, int maxCols,
    int currentRowIndex, boolean hasRatedCriteria) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillDocumentTotalPoints, Parameters: submissionCriteria: {0}, "
        + "offers:{1}, sheet: {2}, workBook: {3}, maxCols: {4}, "
        + "currentRowIndex: {5}, hasRatedCriteria: {6}",
      new Object[]{submissionCriteria, offers, sheet, workbook, maxCols, currentRowIndex,
        hasRatedCriteria});

    int totalPointsIndex = currentRowIndex;
    int titleColumn = 1;
    int dataColumn = 5;
    DecimalFormat formatter = new DecimalFormat("0.00");

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleBold.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);

    // create 2 row for total points
    for (int i = 0; i < 2; i++) {
      Row row = sheet.createRow(totalPointsIndex + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    // calculate sum of bewertetes criteria
    double mainCriteriaSum = getMainCriteriaSum(submissionCriteria);

    // fill row with points and bewerteten criteria sum (only if bewerteten criteria exist)
    sheet.getRow(currentRowIndex).getCell(titleColumn).setCellValue(TOTAL_POINTS);
    if (hasRatedCriteria) {
      sheet.getRow(currentRowIndex - 1).getCell(titleColumn + 1)
        .setCellValue(formatter.format(mainCriteriaSum));
      sheet.getRow(currentRowIndex - 1).getCell(titleColumn + 1).setCellStyle(headersStyleCenter);
    }

    for (SuitabilityDocumentDTO offer : offers) {
      if (offer.getOffer().getqExTotalGrade() != null) {
        BigDecimal totalGrade = offer.getOffer().getqExTotalGrade().setScale(3);
        setValueToField(totalGrade.toString(), sheet.getRow(currentRowIndex).getCell(dataColumn));
      }
      dataColumn += 2;
    }

    // append style to row
    for (int i = 0; i < maxCols; i++) {
      if (i == titleColumn) {
        sheet.getRow(currentRowIndex).getCell(titleColumn).setCellStyle(headersStyleBold);
      }
    }

    // data style
    dataColumn = 5;
    for (int i = 0; i < offers.size(); i++) {
      if ((i % 2 == 0)) {
        sheet.getRow(currentRowIndex).getCell(dataColumn).setCellStyle(greyBackgroundBold);
        sheet.getRow(currentRowIndex).getCell(dataColumn - 1).setCellStyle(whiteBackground);
      }
      if ((i % 2 != 0)) {
        sheet.getRow(currentRowIndex).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
        sheet.getRow(currentRowIndex).getCell(dataColumn - 1).setCellStyle(whiteBackground);
      }
      dataColumn += 2;
    }

    return currentRowIndex + 2;
  }

  private int fillDocumentStatusExamination(List<SuitabilityDocumentDTO> offers, XSSFSheet sheet,
    XSSFWorkbook workbook, int maxCols, int currentRowIndex) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillDocumentStatusExamination, Parameters: offers: {0}, sheet: {1}, "
        + "workBook: {2}, maxCols: {3}, currentRowIndex: {4}",
      new Object[]{offers, sheet, workbook, maxCols, currentRowIndex});

    int titleColumn = 1;
    int datacolumn = 5;

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle examinastionStatusStyle = workbook.createCellStyle();
    examinastionStatusStyle.setBorderBottom(BorderStyle.MEDIUM);
    examinastionStatusStyle.setBottomBorderColor(borderColorIndex);
    examinastionStatusStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(examinastionStatusStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(examinastionStatusStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(examinastionStatusStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyleBold.setFont(titleFontBold);

    // Create row for status examination
    Row row = sheet.createRow(currentRowIndex);
    for (int i = 0; i < maxCols; i++) {
      row.createCell(i);
    }

    // fill row with examination status
    row.getCell(titleColumn).setCellValue(
      "Status Prüfung / abgeschlossen am " + SubmissConverter.convertToSwissDate(new Date()));
    for (SuitabilityDocumentDTO offer : offers) {
      row.getCell(datacolumn).setCellValue(
        castToYesOrNo(offer.getOffer().getqExExaminationIsFulfilled()).toUpperCase());
      datacolumn += 2;
    }

    // append style to row
    // title style
    for (int i = 0; i < maxCols; i++) {
      if (i < 4) {
        row.getCell(i).setCellStyle(headersStyleBold);
      }
    }

    // data style
    datacolumn = 5;
    for (int i = 0; i < offers.size(); i++) {
      if ((i % 2 == 0)) {
        row.getCell(datacolumn).setCellStyle(greyBackgroundBold);
        row.getCell(datacolumn - 1).setCellStyle(whiteBackgroundBold);
      }
      if ((i % 2 != 0)) {
        row.getCell(datacolumn).setCellStyle(whiteBackgroundBold);
        row.getCell(datacolumn - 1).setCellStyle(whiteBackgroundBold);
      }
      datacolumn += 2;
    }

    // merge title cells
    mergeCell(sheet, currentRowIndex, titleColumn, 1);
    return currentRowIndex + 3;
  }

  /**
   * Merge cell.
   *
   * @param sheet the sheet
   * @param titleRow the title row
   * @param colnum the colnum
   * @param sell span is the num of horizontally merged cell
   */
  private void mergeCell(XSSFSheet sheet, int titleRow, int colnum, int cellspan) {

    LOGGER.log(Level.CONFIG,
      "Executing method mergeCell, Parameters: sheet: {0}, titleRow: {1}, "
        + "colnum: {2}, cellspan: {3}",
      new Object[]{sheet, titleRow, colnum, cellspan});

    sheet.addMergedRegion(new CellRangeAddress(titleRow, titleRow, colnum, colnum + cellspan));
    sheet.autoSizeColumn(colnum);
  }

  /**
   * Merge cell.
   *
   * @param sheet the sheet
   * @param titleRow the title row
   * @param colnum the colnum
   * @param cellspan span is the num of horizontally merged cell
   * @param autosizecol check if the column must autosize
   */
  private void mergeCell(XSSFSheet sheet, int titleRow, int colnum, int cellspan,
    boolean autosizecol) {

    LOGGER.log(Level.CONFIG,
      "Executing method mergeCell, Parameters: sheet: {0}, titleRow: {1}, "
        + "colnum: {2}, cellspan: {3}, autosizecol: {4}",
      new Object[]{sheet, titleRow, colnum, cellspan, autosizecol});

    sheet.addMergedRegion(new CellRangeAddress(titleRow, titleRow, colnum, colnum + cellspan));

    if (autosizecol) {
      sheet.autoSizeColumn(colnum);
    } else {
      for (int i = colnum; i < colnum + cellspan; i++) {
        sheet.autoSizeColumn(i, false);
      }
    }
  }

  private void setDocumentStaticColumnTitle(SubmissionDTO submission, int maxCols, XSSFSheet sheet,
    XSSFWorkbook workbook) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentStaticColumnTitle, Parameters: submission: {0}, "
        + "maxCols: {1}, sheet: {2}, workbook: {3}",
      new Object[]{submission, maxCols, sheet, workbook});

    int titleRowNum = 0;

    // Document title style
    CellStyle titleStyle = workbook.createCellStyle();
    titleStyle.setLocked(true);
    titleStyle.setAlignment(HorizontalAlignment.LEFT);
    titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);
    titleFont.setBold(true);
    titleFont.setColor(IndexedColors.WHITE.index);
    titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
    titleStyle.setFont(titleFont);

    // style for date
    CellStyle titleStyleRight = workbook.createCellStyle();
    titleStyleRight.cloneStyleFrom(titleStyle);
    titleStyleRight.setAlignment(HorizontalAlignment.RIGHT);

    for (int i = HEADING_ROWNUM; i <= HEADING_ROWNUM; i++) {
      Row row = sheet.createRow(titleRowNum);
      for (int j = 0; j < maxCols; j++) {
        Cell cell = row.createCell(j);
        if (j == 1) {
          setValueToField(getSheetTitle(submission), cell);
        }
        if (j < (maxCols - 1)) {
          cell.setCellStyle(titleStyle);
        } else {
          setValueToField(SubmissConverter.convertToSwissDate(new Date()), cell);
          cell.setCellStyle(titleStyleRight);
        }
      }
      row.setHeightInPoints((float) 25.5); // approximately 33px
    }

  }

  private int setDocumentStaticColumnSubTitle(SubmissionDTO submission, int maxCols,
    int currentRowIndex, XSSFSheet sheet, XSSFWorkbook workbook, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentStaticColumnTitle, Parameters: submission: {0}, maxCols: {1}, "
        + "currentRowIndex: {2}, sheet: {3}, workBook: {4}, type: {5}",
      new Object[]{submission, maxCols, currentRowIndex, sheet, workbook, type});

    int documentSubTitleRow = currentRowIndex + 1;
    int submissionReadValues = currentRowIndex + 2;
    int titleColumn = 1;

    XSSFColor colorRed = new XSSFColor(new Color(213, 0, 41));

    CellStyle titleStyle = workbook.createCellStyle();
    titleStyle.setAlignment(HorizontalAlignment.LEFT);
    titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    // subtitleStyle
    XSSFCellStyle subtitleStyle = workbook.createCellStyle();
    subtitleStyle.cloneStyleFrom(titleStyle);
    XSSFFont subtitleFont = workbook.createFont();
    subtitleFont.setFontName(FONT);
    subtitleFont.setFontHeight(FONT_SIZE);
    subtitleFont.setColor(IndexedColors.WHITE.index);
    subtitleFont.setBold(true);
    subtitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    subtitleStyle.setFillForegroundColor(colorRed);
    subtitleStyle.setFont(subtitleFont);

    // headings style
    CellStyle headingStyle = workbook.createCellStyle();
    headingStyle.setAlignment(HorizontalAlignment.LEFT);
    headingStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    headingStyle.setWrapText(true);
    XSSFFont headingsfont = workbook.createFont();
    headingsfont.setFontHeight(FONT_SIZE);
    headingsfont.setFontName(FONT);
    headingsfont.setBold(true);
    headingStyle.setFont(headingsfont);

    for (int i = documentSubTitleRow; i <= submissionReadValues; i++) {
      Row row = sheet.createRow(i);
      for (int j = 0; j < maxCols; j++) {
        Cell cell = row.createCell(j);
        if (j == 1 && i == documentSubTitleRow) {
          if (type.equals(SUITABILITY)) {
            setValueToField(SUITABILITY_LABEL, cell);
          } else {
            setValueToField(AWARD_LABEL, cell);
          }
          cell.setCellStyle(subtitleStyle);
        } else {
          cell.setCellStyle(subtitleStyle);
        }

        if (i == submissionReadValues) {
          cell.setCellStyle(headingStyle);
        }
        if (j == 1 && i == submissionReadValues) {
          setValueToField(templateBeanService.readTemplatesValues(submission), cell);
          cell.setCellStyle(headingStyle);
        }
      }
      row.setHeightInPoints((float) 25.5); // approximately 33px
    }

    // merge cells of submission description and auto size row height
    mergeCell(sheet, submissionReadValues, titleColumn, maxCols - 2);
    autosizeRow(submissionReadValues, sheet, maxCols, 240);

    return currentRowIndex + 4;
  }

  /**
   * Auto size row.
   *
   * @param row the row
   * @param sheet the sheet
   * @param maxCols the maxCols
   */
  private void autosizeRow(int row, XSSFSheet sheet, int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method autosizeRow, Parameters: row: {0}, sheet: {1}, "
        + "maxCols: {2}",
      new Object[]{row, sheet, maxCols});

    float tallestCell = -1;

    for (int col = 0; col < maxCols; col++) {
      XSSFCell cell = sheet.getRow(row).getCell(col);
      XSSFCellStyle style = cell.getCellStyle();
      XSSFFont font = style.getFont();
      int fontHeight = font.getFontHeightInPoints();
      if (cell.getCellTypeEnum() == CellType.STRING) {
        String value = cell.getStringCellValue();
        int numLines = 1;
        for (int i = 0; i < value.length(); i++) {
          if (value.charAt(i) == '\n') {
            numLines++;
          }
        }
        float cellHeight = computeRowHeightInPoints(fontHeight, numLines, sheet);
        if (cellHeight > tallestCell) {
          tallestCell = cellHeight;
        }
      }
    }
    float defaultRowHeightInPoints = sheet.getDefaultRowHeightInPoints();
    float rowHeight = tallestCell;
    if (rowHeight < defaultRowHeightInPoints + 1) {
      rowHeight = -1; // resets to the default
    }
    sheet.getRow(row).setHeightInPoints(rowHeight);
  }

  /**
   * Auto size row.
   *
   * @param row the row
   * @param sheet the sheet
   * @param maxCols the maxCols
   */
  private void autosizeRow(int row, XSSFSheet sheet, int maxColumns, int mergedCellsWidthInChar) {

    LOGGER.log(Level.CONFIG,
      "Executing method autosizeRow, Parameters: row: {0}, sheet{1}, maxColumns: {2}, "
        + "mergedCellsWidthInChar: {3}",
      new Object[]{row, sheet, maxColumns, mergedCellsWidthInChar});

    float tallestCell = -1;

    int colwidthinchars = mergedCellsWidthInChar;

    for (int i = 0; i < maxColumns; i++) {
      XSSFCell cell = sheet.getRow(row).getCell(i);
      XSSFCellStyle style = cell.getCellStyle();
      XSSFFont font = style.getFont();
      int fontHeight = font.getFontHeightInPoints();
      String value = cell.getStringCellValue();
      String[] chars = value.split("");
      int counter = 0;
      int numLines = 1;
      for (int j = 0; j < chars.length; j++) {
        counter++;
        if (counter == colwidthinchars) {
          numLines++;
          counter = 0;
        }
        if ("\n".equals(chars[j])) {
          numLines++;
          counter = 0;
        }
      }
      float cellHeight = computeRowHeightInPoints(fontHeight, numLines, sheet);
      if (cellHeight > tallestCell) {
        tallestCell = cellHeight;
      }
    }

    float defaultRowHeightInPoints = sheet.getDefaultRowHeightInPoints();
    float rowHeight = tallestCell;
    if (rowHeight < defaultRowHeightInPoints + 1) {
      rowHeight = -1; // resets to the default
    }
    sheet.getRow(row).setHeightInPoints(rowHeight);
  }

  /**
   * Autosize row from column.
   *
   * @param row the row
   * @param sheet the sheet
   * @param startColumn the start column
   * @param endColumn the end column
   * @param mergedCellsWidthInChar the merged cells width in char
   */
  private void autosizeRowFromColumn(int row, XSSFSheet sheet, int startColumn, int endColumn,
    int mergedCellsWidthInChar) {

    LOGGER.log(Level.CONFIG,
      "Executing method autosizeRowFromColumn, Parameters: row: {0}, sheet: {1}, "
        + "startColumn: {2}, endColumn: {3}, mergedCellsWidthInChar: {4}",
      new Object[]{row, sheet, startColumn, endColumn, mergedCellsWidthInChar});

    float tallestCell = -1;

    int colwidthinchars = mergedCellsWidthInChar;

    for (int i = startColumn; i < endColumn; i++) {
      XSSFCell cell = sheet.getRow(row).getCell(i);
      XSSFCellStyle style = cell.getCellStyle();
      XSSFFont font = style.getFont();
      int fontHeight = font.getFontHeightInPoints();
      String value = cell.getStringCellValue();
      String[] chars = value.split("");
      int counter = 0;
      int numLines = 1;
      for (int j = 0; j < chars.length; j++) {
        counter++;
        if (counter == colwidthinchars) {
          numLines++;
          counter = 0;
        }
        if ("\n".equals(chars[j])) {
          numLines++;
          counter = 0;
        }
      }
      float cellHeight = computeRowHeightInPoints(fontHeight, numLines, sheet);
      if (cellHeight > tallestCell) {
        tallestCell = cellHeight;
      }
    }

    float defaultRowHeightInPoints = sheet.getDefaultRowHeightInPoints();
    float rowHeight = tallestCell;
    if (rowHeight < defaultRowHeightInPoints + 1) {
      rowHeight = -1; // resets to the default
    }
    sheet.getRow(row).setHeightInPoints(rowHeight);
  }

  /**
   * Sets the size (height) for the criterion/subcriterion titles row.
   *
   * @param row the row
   * @param startColumn the first column which contains a criterion title.
   * @param sheet the sheet
   * @param mergedCellsWidthInChar the merged cells width in char
   * @param offer the offer
   * @param type the type
   */
  private void autosizeTitlesRow(int row, int startColumn, XSSFSheet sheet, int mergedCellsWidthInChar,
      OfferDTO offer, String type) {

    LOGGER.log(Level.CONFIG,
        "Executing method autosizeTitlesRow, Parameters: row: {0}, startColumn: {1}, sheet: {2}, "
            + "mergedCellsWidthInChar: {3}, offer: {4}, type: {5}",
        new Object[] {row, startColumn, sheet, mergedCellsWidthInChar, offer, type});

    List<String> criterionTitles = getCriterionAndSubcriterionTitles(offer, type);

    float tallestCell = -1;
    int colwidthinchars = mergedCellsWidthInChar;
    int fontHeight = sheet.getRow(row).getCell(startColumn).getCellStyle().getFont().getFontHeightInPoints();

    for (String criterionTitle : criterionTitles) {
      String[] chars = criterionTitle.split("");
      int counter = 0;
      int numLines = 1;
      for (int j = 0; j < chars.length; j++) {
        counter++;
        if (counter == colwidthinchars) {
          numLines++;
          counter = 0;
        }
        if ("\n".equals(chars[j])) {
          numLines++;
          counter = 0;
        }
      }
      float cellHeight = computeRowHeightInPoints(fontHeight, numLines, sheet);
      if (cellHeight > tallestCell) {
        tallestCell = cellHeight;
      }
    }
    float defaultRowHeightInPoints = sheet.getDefaultRowHeightInPoints();
    float rowHeight = tallestCell;
    if (rowHeight < defaultRowHeightInPoints + 1) {
      rowHeight = -1; // resets to the default
    }
    sheet.getRow(row).setHeightInPoints(rowHeight);
  }

  /**
   * Gets the criterion/subcriterion titles.
   *
   * @param offer the offer
   * @param type the general criteria type
   * @return the criterion/subcriterion titles
   */
  private List<String> getCriterionAndSubcriterionTitles(OfferDTO offer, String type) {

    LOGGER.log(Level.CONFIG,
        "Executing method getCriterionAndSubcriterionTitles, Parameters: offer: {0}, type: {1}",
        new Object[] {offer, type});

    List<String> criterionTitles = new ArrayList<>();

    if (type.equals(SUITABILITY)) {
      // Get the suitability criteria titles. 
      for (OfferCriterionDTO offerCriterion : offer.getOfferCriteria()) {
        if (offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.MUST_CRITERION_TYPE)) {
          criterionTitles.add(offerCriterion.getCriterion().getCriterionText());
        } else if (offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
          criterionTitles.add(offerCriterion.getCriterion().getCriterionText());
          // An evaluated criterion may also contain subcriteria.
          for (SubcriterionDTO subcriterion : offerCriterion.getCriterion().getSubcriterion()) {
            criterionTitles.add(subcriterion.getSubcriterionText());
          }
        }
      }
    } else if (type.equals(AWARD)) {
      // Get the award criteria titles.
      for (OfferCriterionDTO offerCriterion : offer.getOfferCriteria()) {
        if (offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
            || offerCriterion.getCriterion().getCriterionType()
                .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          criterionTitles.add(offerCriterion.getCriterion().getCriterionText());
        } else if (offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.AWARD_CRITERION_TYPE)) {
          criterionTitles.add(offerCriterion.getCriterion().getCriterionText());
          // An award criterion may also contain subcriteria.
          for (SubcriterionDTO subcriterion : offerCriterion.getCriterion().getSubcriterion()) {
            criterionTitles.add(subcriterion.getSubcriterionText());
          }
        }
      }
    }
    return criterionTitles;
  }

  /**
   * Compute row height in points.
   *
   * @param fontSizeInPoints the font size in points
   * @param numLines the num lines
   * @param sheet the sheet
   */
  public float computeRowHeightInPoints(int fontSizeInPoints, int numLines, XSSFSheet sheet) {

    LOGGER.log(Level.CONFIG,
      "Executing method computeRowHeightInPoints, Parameters: fontSizeInPoints: {0}, "
        + "numLines: {1}, sheet: {2}",
      new Object[]{fontSizeInPoints, numLines, sheet});

    // a crude approximation of what excel does
    float lineHeightInPoints = 1.3f * fontSizeInPoints;
    float rowHeightInPoints = lineHeightInPoints * numLines;
    rowHeightInPoints = Math.round(rowHeightInPoints * 4) / 4f; // round to the nearest 0.25

    // Don't shrink rows to fit the font, only grow them
    float defaultRowHeightInPoints = sheet.getDefaultRowHeightInPoints();
    if (rowHeightInPoints < defaultRowHeightInPoints + 1) {
      rowHeightInPoints = defaultRowHeightInPoints;
    }
    return rowHeightInPoints;
  }

  @Override
  public byte[] exportAwardEvaluationDocument(String submissionId,
    List<AwardEvaluationDocumentDTO> awardEvaluationOffers, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method exportAwardEvaluationDocument, Parameters: submissionId: {0}, "
        + "awardEvaluationOffers: {1}, type: {2}",
      new Object[]{submissionId, awardEvaluationOffers, type});

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(SHEET_TITLE);

      SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
      List<CriterionDTO> submissionCriteria =
        criterionService.readCriteriaOfSubmission(submissionId);
      // every 4 offer entries we have to repeat
      int currentOfferIndex = 0;
      int repeatOffersCounter = awardEvaluationOffers.size() / 4;
      int maxCols = countDocumentColumns(4);
      int currentRowIndex = 0;
      // indicates the rows that the data for every set of companies start and end
      int newSetOfCompaniesStart = 0;
      int newSetOfCompaniesStop = 0;

      boolean hasSubCriteria = false;
      boolean hasOperatingCosts = false;
      boolean newSetOfCompanies = false;
      List<AwardEvaluationDocumentDTO> currentOffers = new ArrayList<>();

      // map of criteria/subcriteria indexes in order to use at page breaks
      // the first element of list is always the starting row of criteria and the 
      // second element the ending row
      LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap = new LinkedHashMap<>();

      // check if sub criteria exists
      hasSubCriteria = awardSubCriteriaExist(hasSubCriteria, awardEvaluationOffers);
      hasOperatingCosts = operatingCostCriteriaExist(submissionCriteria);

      // Zuschlagsbewertung
      setDocumentStaticColumnTitle(submission, maxCols, sheet, workbook);
      setDocumentStaticColumnSubTitle(submission, maxCols, currentRowIndex, sheet, workbook, type);
      setAwardDocumentMaxMinPoints(submission, maxCols, sheet, workbook, hasOperatingCosts);

      if (!hasOperatingCosts) {
        currentRowIndex = 7;
      } else {
        currentRowIndex = 8;
      }

      int pageBreakCounter = 1;
      for (int i = 0; i < repeatOffersCounter; i++) {

        for (int j = currentOfferIndex; j < currentOfferIndex + 4; j++) {
          currentOffers.add(awardEvaluationOffers.get(j));
        }

        int companiesHeaderIndexRow = 0;
        newSetOfCompaniesStart = currentRowIndex;
        currentRowIndex = setAwardDocumentCompaniesHeader(currentOffers, maxCols, currentRowIndex,
          sheet, workbook, companiesHeaderIndexRow);
        currentRowIndex = setAwardDocumentCompaniesOverview(currentOffers, maxCols, currentRowIndex,
          sheet, workbook, type);
        if (hasSubCriteria) {
          currentRowIndex = fillAwardDocumentWithSubCriteria(submissionCriteria, currentOffers,
            sheet, workbook, maxCols, currentRowIndex, criteriaPageBreakMap);
        } else {
          currentRowIndex = fillAwardDocumentCriteria(submissionCriteria, currentOffers, sheet,
            workbook, maxCols, currentRowIndex, criteriaPageBreakMap);
        }
        currentRowIndex =
          fillAwardDocumentRank(currentOffers, sheet, workbook, maxCols, currentRowIndex);

        newSetOfCompaniesStop = currentRowIndex;
        currentRowIndex = setAwardDocumentPageBreaks(currentOffers, sheet, maxCols, workbook,
          submission, type,
          companiesHeaderIndexRow, newSetOfCompaniesStart, newSetOfCompaniesStop, currentRowIndex,
          newSetOfCompanies, criteriaPageBreakMap, hasSubCriteria);

        // set page break in order new set of companies to be printed in the next page
        // (if there is only one set of companies there is no need of page break)
        // page break is set to the previous line before column subtitle
        if (pageBreakCounter < repeatOffersCounter) {
          sheet.setRowBreak(currentRowIndex - 1);
          pageBreakCounter++;
          currentRowIndex = setDocumentStaticColumnSubTitle(submission, maxCols,
            currentRowIndex - 1, sheet, workbook, type);
          newSetOfCompanies = true;
        }
        currentOffers.clear();
        currentOfferIndex += 4;
      }

      sheet.setDisplayGridlines(false);
      // Hides UUID column..
      sheet.setColumnHidden(0, true);

      // set page margins
      setDocumentMargins(sheet);

      sheet.setFitToPage(true);
      sheet.setAutobreaks(false);

      // Setup print layout settings
      setPrintLayoutSettings(sheet, true);

      // set worksheet footer
      XSSFHeaderFooter footer = (XSSFHeaderFooter) sheet.getFooter();
      footer.setRight(HSSFFooter.font(FONT, "") + HSSFFooter.fontSize((short) FONT_SIZE) + "Seite "
        + HeaderFooter.page() + "/" + HeaderFooter.numPages());

      setDocumentBaseCellWidhts(sheet, maxCols);

      workbook.write(bos);
      bos.close();

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return bos.toByteArray();
  }

  // Method that calculates pages' rows height and adds page breaks 
  private int setAwardDocumentPageBreaks(List<AwardEvaluationDocumentDTO> currentOffers,
    XSSFSheet sheet,
    int maxCols, XSSFWorkbook workbook, SubmissionDTO submission, String type,
    int companiesHeaderIndexRow, int newSetOfCompaniesStart, int newSetOfCompaniesStop,
    int currentRowIndex, boolean newSetOfCompanies,
    LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap, boolean hasSubCriteria) {

    /*
     * rowsToBeShifted = staticColumnSubTitle rows + CompaniesHeader rows
     * rowsToBeShifted is the number of rows to move down each time a page break occurs.
     */
    int rowsToBeShifted = 9;
    // temporary index for inserting titles
    int currentRow = 0;
    float currentPageHeight = 0;
    float companiesHeaderHeight = 0;
    List<Integer> rowsToShift = new ArrayList<>();
    LinkedHashMap<CriterionDTO, Integer> criteriaHeaderTitleList = new LinkedHashMap<>();

    // size of company name row - in case of Zuschlagsbewertung document we also add the height of
    // Netto-Preis, Betriebskosten, Variante
    companiesHeaderHeight = sheet.getRow(companiesHeaderIndexRow).getHeightInPoints()
      + sheet.getRow(companiesHeaderIndexRow + 1).getHeightInPoints()
      + sheet.getRow(companiesHeaderIndexRow + 2).getHeightInPoints()
      + (sheet.getRow(companiesHeaderIndexRow + 3) != null
      ? sheet.getRow(companiesHeaderIndexRow + 3).getHeightInPoints()
      : sheet.getDefaultRowHeightInPoints())
      + sheet.getDefaultRowHeightInPoints();

    // we add the size of document title, submission description and the size of empty row
    float pageStaticTitleHeight = sheet.getRow(1).getHeightInPoints()
      + sheet.getRow(2).getHeightInPoints() + sheet.getDefaultRowHeightInPoints();

    // moves the index of the page break every time an entry is added to rowsToShift list
    int rowBreakSetter = 0;

    if (newSetOfCompaniesStart < 20) {
      for (int i = 0; i < newSetOfCompaniesStop; i++) {
        if (sheet.getRow(i) != null) {
          float rowHeight = sheet.getRow(i).getHeightInPoints();
          currentPageHeight += rowHeight;

          if (rowHeight == (float) 429) {
            rowsToShift.add(sheet.getRow(i).getRowNum() + 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() + 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            /*
             * The next page will start by adding the heights of the default title height and
             * the companies header row.
             */
            currentPageHeight = pageStaticTitleHeight + companiesHeaderHeight;
          } else if (currentPageHeight > A4_PAGE_HEIGHT) {

            CriterionDTO criterionBreak = pageBreakOnCriteria(i - 1, criteriaPageBreakMap);
            if (criterionBreak != null) {
              criteriaHeaderTitleList.put(criterionBreak, (i - 1) + rowBreakSetter);
            }

            rowsToShift.add(sheet.getRow(i).getRowNum() - 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() - 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            // next page will start by adding the heights of the current row, the default title
            // height and
            // the companies header row
            currentPageHeight =
              sheet.getRow(i).getHeightInPoints() + pageStaticTitleHeight + companiesHeaderHeight;
          }
        }
      }
    } else {
      float removeHeightOfStaticHeader = 0;

      if (newSetOfCompanies) {
        removeHeightOfStaticHeader = pageStaticTitleHeight;
      }

      for (int i = newSetOfCompaniesStart; i < newSetOfCompaniesStop; i++) {
        if (sheet.getRow(i) != null) {
          float rowHeight = sheet.getRow(i).getHeightInPoints();
          currentPageHeight += rowHeight;

          if (rowHeight == (float) 429) {
            rowsToShift.add(sheet.getRow(i).getRowNum() + 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() + 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            /*
             * The next page will start by adding the heights of the default title height and
             * the companies header row.
             */
            currentPageHeight = pageStaticTitleHeight + companiesHeaderHeight;
          } else if (currentPageHeight > A4_PAGE_HEIGHT - removeHeightOfStaticHeader) {

            CriterionDTO criterionBreak = pageBreakOnCriteria(i - 1, criteriaPageBreakMap);
            if (criterionBreak != null) {
              criteriaHeaderTitleList.put(criterionBreak, (i - 1) + rowBreakSetter);
            }

            rowsToShift.add(sheet.getRow(i).getRowNum() - 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() - 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            // next page will start by adding the heights of the current row, the default title
            // height and
            // the companies header row
            currentPageHeight =
              sheet.getRow(i).getHeightInPoints() + pageStaticTitleHeight + companiesHeaderHeight;
            removeHeightOfStaticHeader = 0;
          }
        }
      }
    }

    // shift rows and add to the title and the company header
    int addedShiftedRows = 0;
    int addedCriteriaTitleShiftedRows = 0;
    int criteriaHeaderBuffer = 1;
    for (int i = 0; i < rowsToShift.size(); i++) {
      rowsToShift.set(i, rowsToShift.get(i) + addedShiftedRows);
      sheet.shiftRows(rowsToShift.get(i), sheet.getLastRowNum() + 1, rowsToBeShifted, true, false);
      currentRow = setDocumentStaticColumnSubTitle(submission, maxCols, rowsToShift.get(i), sheet,
        workbook, type);
      currentRow = setAwardDocumentCompaniesHeader(currentOffers, maxCols, currentRow, sheet,
        workbook, companiesHeaderIndexRow);

      // insert criteria header if page break occurs on a criterion
      for (Entry<CriterionDTO, Integer> entry : criteriaHeaderTitleList.entrySet()) {
        if (entry.getValue().equals(rowsToShift.get(i))) {
          if (hasSubCriteria) {
            criteriaHeaderBuffer = 2;
          }
          sheet.shiftRows(currentRow, sheet.getLastRowNum() + 1, criteriaHeaderBuffer, true, false);
          setAwardCriteriaHeader(currentOffers, entry.getKey(), maxCols, hasSubCriteria,
            currentRow + 1, sheet,
            workbook);
          addedCriteriaTitleShiftedRows += criteriaHeaderBuffer;
        }
      }

      addedShiftedRows += rowsToBeShifted;
    }
    return currentRowIndex + addedShiftedRows + addedCriteriaTitleShiftedRows;
  }

  private void setAwardCriteriaHeader(List<AwardEvaluationDocumentDTO> currentOffers,
    CriterionDTO criterionDTO, int maxCols,
    boolean hasSubCriteria, int currentRow, XSSFSheet sheet, XSSFWorkbook workbook) {

    int ratedCriteriaStartRow = currentRow;
    int titleMaxrows = 0;
    String criteriaTitle;
    int titleColumn = 1;
    DecimalFormat formatter = new DecimalFormat("0.00");

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);

    if (hasSubCriteria) {
      criteriaTitle = "Zuschlagskriterium";
      titleMaxrows = 2;
    } else {
      criteriaTitle = SURCHARGE;
      titleMaxrows = 1;
    }

    for (int i = 0; i < titleMaxrows; i++) {
      Row row = sheet.createRow(ratedCriteriaStartRow + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    if (titleMaxrows == 2) {
      int dataColumn = 4;

      // set header and header style
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellValue(criteriaTitle);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellStyle(headersStyleItalic);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1).setCellValue("G");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1)
        .setCellStyle(headersStyleBoldCenter);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2).setCellValue("UK");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
        .setCellStyle(headersStyleBoldCenter);

      for (int i = 0; i < currentOffers.size(); i++) {
        if (i % 2 == 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(greyBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(greyBackgroundBold);
        }
        if (i % 2 != 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(whiteBackgroundBold);
        }
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellValue(GRADE);
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1).setCellValue(POINTS);
        dataColumn += 2;
      }

      // set criterion description and points
      ratedCriteriaStartRow++;
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn)
        .setCellValue(criterionDTO.getCriterionText());
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellStyle(headersStyleBold);
      String weightingValue = formatter.format(criterionDTO.getWeighting());
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1).setCellValue(weightingValue);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1)
        .setCellStyle(headersStyleBoldCenter);
      autosizeRow(ratedCriteriaStartRow, sheet, maxCols,
        CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);
      if (criterionDTO.getSubcriterion() != null && !criterionDTO.getSubcriterion().isEmpty()) {
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
          .setCellValue(getSubCriteriaTotalWeight(criterionDTO.getSubcriterion()));
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
          .setCellStyle(headersStyleCenter);
      } else {
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2).setCellValue(CELL_VALUE);
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
          .setCellStyle(headersStyleCenter);
      }

      dataColumn = 4;
      int companyIndex = 0;
      for (AwardEvaluationDocumentDTO offer : currentOffers) {
        for (OfferCriterionDTO offerCriteria : offer.getOffer().getOfferCriteria()) {
          if (offerCriteria.getCriterion().getId().equals(criterionDTO.getId())) {
            if (companyIndex % 2 == 0) {
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(greyBackground);
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
                .setCellStyle(greyBackground);
            }
            if (companyIndex % 2 != 0) {
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(whiteBackground);
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
                .setCellStyle(whiteBackground);
            }
            if (offerCriteria.getGrade() != null) {
              BigDecimal gradeValue = offerCriteria.getGrade().setScale(2);
              setValueToField(gradeValue.toString(),
                sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn));
            }
            if (offerCriteria.getScore() != null) {
              BigDecimal scoreValue = offerCriteria.getScore().setScale(3);
              setValueToField(scoreValue.toString(),
                sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1));
            }
            dataColumn += 2;
            continue;
          }
        }
        companyIndex++;
      }
    } else {
      int dataColumn = 4;
      // set header and header style
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellValue(criteriaTitle);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellStyle(headersStyleItalic);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1).setCellValue("");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1)
        .setCellStyle(headersStyleBoldCenter);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2).setCellValue("G");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
        .setCellStyle(headersStyleBoldCenter);

      for (int i = 0; i < currentOffers.size(); i++) {
        if (i % 2 == 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(greyBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(greyBackgroundBold);
        }
        if (i % 2 != 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(whiteBackgroundBold);
        }
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellValue(GRADE);
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1).setCellValue(POINTS);
        dataColumn += 2;
      }
    }

  }


  private CriterionDTO pageBreakOnCriteria(int row,
    LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap) {
    for (Entry<CriterionDTO, List<Integer>> entry : criteriaPageBreakMap.entrySet()) {
      if (row >= entry.getValue().get(0) && row <= entry.getValue().get(1)) {
        return entry.getKey();
      }
    }
    return null;
  }


  private int fillAwardDocumentWithSubCriteria(List<CriterionDTO> submissionCriteria,
    List<AwardEvaluationDocumentDTO> offers, XSSFSheet sheet, XSSFWorkbook workbook, int maxCols,
    int currentRowIndex, LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillAwardDocumentWithSubCriteria, Parameters: submissionCriteria: {0}, "
        + "offers: {1}, sheet: {2}, workBook: {3}, maxCols: {4}, "
        + "currentRowIndex: {5}, criteriaPageBreakMap: {6}",
      new Object[]{submissionCriteria, offers, sheet, workbook, maxCols, currentRowIndex,
        criteriaPageBreakMap});

    int ratedCriteriaStartRow = currentRowIndex;
    int titleColumn = 1;
    LinkedHashMap<String, CriterionDTO> awardCriteriaMap = new LinkedHashMap<>();
    DecimalFormat formatter = new DecimalFormat("0.00");

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenterNoBorders = workbook.createCellStyle();
    headersStyleCenterNoBorders.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenterNoBorders.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);
    headersStyleCenterNoBorders.setFont(titleFont);

    for (CriterionDTO criterionDTO : submissionCriteria) {
      if (criterionDTO.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
        awardCriteriaMap.put(criterionDTO.getId(), criterionDTO);
      }
      if (criterionDTO.getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        awardCriteriaMap.put(criterionDTO.getId(), criterionDTO);
      }
      if (criterionDTO.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)) {
        awardCriteriaMap.put(criterionDTO.getId(), criterionDTO);
      }
    }

    int rowsCounter = 0;
    // first we create the rows and cells for all rated criteria

    for (Entry<String, CriterionDTO> entry : awardCriteriaMap.entrySet()) {
      rowsCounter += 3;
      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        for (int i = 0; i < entry.getValue().getSubcriterion().size(); i++) {
          rowsCounter++;
        }
      }
    }

    for (int i = 0; i < rowsCounter; i++) {
      Row row = sheet.createRow(ratedCriteriaStartRow + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    int awardCriteraIndex = ratedCriteriaStartRow;
    // insert values to rows
    for (Entry<String, CriterionDTO> entry : awardCriteriaMap.entrySet()) {
      int dataColumn = 4;
      List<Integer> criteriaStartEndIndexes = new ArrayList<>();
      // set header and header style
      sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellValue("Zuschlagskriterium");
      sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleItalic);
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellValue("G");
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellValue("UK");
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleBoldCenter);

      criteriaStartEndIndexes.add(awardCriteraIndex);

      for (int i = 0; i < offers.size(); i++) {
        if (i % 2 == 0) {
          sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(greyBackgroundBold);
          sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackgroundBold);
        }
        if (i % 2 != 0) {
          sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackgroundBold);
        }
        sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellValue(GRADE);
        sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellValue(POINTS);
        dataColumn += 2;
      }

      // set criterion description and points
      awardCriteraIndex++;
      sheet.getRow(awardCriteraIndex).getCell(titleColumn)
        .setCellValue(entry.getValue().getCriterionText());
      sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleBold);
      String weightingValue = formatter.format(entry.getValue().getWeighting());
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellValue(weightingValue);
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
      autosizeRow(awardCriteraIndex, sheet, maxCols, CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);
      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2)
          .setCellValue(getSubCriteriaTotalWeight(entry.getValue().getSubcriterion()));
        sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleCenter);
      } else {
        sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellValue(CELL_VALUE);
        sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleCenter);
      }

      dataColumn = 4;
      int companyIndex = 0;
      for (AwardEvaluationDocumentDTO offer : offers) {
        for (OfferCriterionDTO offerCriteria : offer.getOffer().getOfferCriteria()) {
          if (offerCriteria.getCriterion().getId().equals(entry.getKey())) {
            if (companyIndex % 2 == 0) {
              sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(greyBackground);
              sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackground);
            }
            if (companyIndex % 2 != 0) {
              sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackground);
              sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackground);
            }
            if (offerCriteria.getGrade() != null) {
              BigDecimal gradeValue = offerCriteria.getGrade().setScale(2);
              setValueToField(gradeValue.toString(),
                sheet.getRow(awardCriteraIndex).getCell(dataColumn));
            }
            if (offerCriteria.getScore() != null) {
              BigDecimal scoreValue = offerCriteria.getScore().setScale(3);
              setValueToField(scoreValue.toString(),
                sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1));
            }
            dataColumn += 2;
            continue;
          }
        }
        companyIndex++;
      }
      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        awardCriteraIndex = fillAwardDocumentSubCriteria(offers, awardCriteraIndex,
          entry.getValue().getSubcriterion(), sheet, workbook, maxCols);
      } else {
        awardCriteraIndex++;
      }
      criteriaStartEndIndexes.add(awardCriteraIndex - 1);
      criteriaPageBreakMap.put(entry.getValue(), criteriaStartEndIndexes);
      awardCriteraIndex++;
    }

    // set total points
    for (int i = 0; i < 2; i++) {
      sheet.createRow(awardCriteraIndex + i);
      for (int j = 0; j < maxCols; j++) {
        sheet.getRow(awardCriteraIndex + i).createCell(j);
      }
    }

    sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellValue(TOTAL_POINTS);
    sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleBold);
    sheet.getRow(awardCriteraIndex - 1).getCell(titleColumn + 1)
      .setCellValue(getAwardMainCriteriaSum(awardCriteriaMap));
    sheet.getRow(awardCriteraIndex - 1).getCell(titleColumn + 1)
      .setCellStyle(headersStyleCenterNoBorders);

    int dataColumn = 4;
    int companyIndex = 0;
    for (AwardEvaluationDocumentDTO offer : offers) {
      if (companyIndex % 2 == 0) {
        sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackgroundBold);
      }
      if (companyIndex % 2 != 0) {
        sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackgroundBold);
      }
      if (offer.getOffer().getAwardTotalScore() != null) {
        setValueToField(offer.getOffer().getAwardTotalScore(),
          sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1));
      } else {
        setValueToField(null, sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1));
      }
      dataColumn += 2;
      companyIndex++;
    }

    awardCriteraIndex += 2;

    return awardCriteraIndex;
  }

  private int fillAwardDocumentSubCriteria(List<AwardEvaluationDocumentDTO> offers,
    int awardCriteraIndex, List<SubcriterionDTO> subcriterionList, XSSFSheet sheet,
    XSSFWorkbook workbook, int maxCols) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillAwardDocumentSubCriteria, Parameters: offers: {0}, "
        + "awardCriteraIndex: {1}, subcriterionList: {2}, sheet: {3}, "
        + "workBook: {4}, maxCOls: {5}",
      new Object[]{offers, awardCriteraIndex, subcriterionList, sheet, workbook, maxCols});

    DecimalFormat formatter = new DecimalFormat("0.00");

    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    // Border color
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalicNine = workbook.createCellStyle();
    headersStyleItalicNine.cloneStyleFrom(headersStyle);
    headersStyleItalicNine.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalicNine.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenterNine = workbook.createCellStyle();
    headersStyleBoldCenterNine.cloneStyleFrom(headersStyle);
    headersStyleBoldCenterNine.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenterNine.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundNine = workbook.createCellStyle();
    greyBackgroundNine.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundNine.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundNine.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundNine.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundNine.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundNine = workbook.createCellStyle();
    whiteBackgroundNine.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundNine.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundNine.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontNine = workbook.createFont();
    titleFontNine.setFontName(FONT);
    titleFontNine.setFontHeight(FONT_SIZE_NINE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    XSSFFont titleFontBoldNine = workbook.createFont();
    titleFontBoldNine.setFontName(FONT);
    titleFontBoldNine.setFontHeight(FONT_SIZE_NINE);
    titleFontBoldNine.setBold(true);
    titleFontBoldNine.setItalic(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundNine.setFont(titleFontNine);
    whiteBackgroundNine.setFont(titleFontNine);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);
    headersStyleItalicNine.setFont(titleFontItalicNine);
    headersStyleBoldCenterNine.setFont(titleFontBoldNine);

    // fill rows with data and append style
    int titleColumn = 1;
    awardCriteraIndex++;
    int companyIndex;
    if (subcriterionList != null && !subcriterionList.isEmpty()) {
      for (SubcriterionDTO subCriterion : subcriterionList) {
        int dataColumn = 4;
        sheet.getRow(awardCriteraIndex).getCell(titleColumn)
          .setCellValue(subCriterion.getSubcriterionText());
        sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleItalicNine);
        sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellStyle(whiteBackground);
        sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2)
          .setCellValue(formatter.format(subCriterion.getWeighting()));
        sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2)
          .setCellStyle(headersStyleBoldCenterNine);
        autosizeRow(awardCriteraIndex, sheet, maxCols, CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);
        companyIndex = 0;
        for (AwardEvaluationDocumentDTO offer : offers) {
          for (OfferSubcriterionDTO offerSubCriterion : offer.getOffer().getOfferSubcriteria()) {
            if (offerSubCriterion.getSubcriterion().getId().equals(subCriterion.getId())) {
              // set style for row's cells
              if (companyIndex % 2 == 0) {
                sheet.getRow(awardCriteraIndex).getCell(dataColumn)
                  .setCellStyle(greyBackgroundNine);
                sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1)
                  .setCellStyle(greyBackgroundNine);
              }
              if (companyIndex % 2 != 0) {
                sheet.getRow(awardCriteraIndex).getCell(dataColumn)
                  .setCellStyle(whiteBackgroundNine);
                sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1)
                  .setCellStyle(whiteBackgroundNine);
              }
              if (offerSubCriterion.getGrade() != null) {
                BigDecimal gradeValue = offerSubCriterion.getGrade().setScale(2);
                setValueToField(gradeValue.toString(),
                  sheet.getRow(awardCriteraIndex).getCell(dataColumn));
              }
              if (offerSubCriterion.getScore() != null) {
                BigDecimal scoreValue = offerSubCriterion.getScore().setScale(3);
                setValueToField(scoreValue.toString(),
                  sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1));
              }
              dataColumn += 2;
              continue;
            }
          }
          companyIndex++;
        }
        awardCriteraIndex++;
      }
    }
    return awardCriteraIndex;
  }

  private int fillAwardDocumentRank(List<AwardEvaluationDocumentDTO> offers, XSSFSheet sheet,
    XSSFWorkbook workbook, int maxCols, int currentRowIndex) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillAwardDocumentRank, Parameters: offers: {0}, "
        + "sheet: {1}, workBook: {2}, maxCols: {3}, currentRowIndex: {4}");

    int titleColumn = 1;
    int datacolumn = 5;

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle examinastionStatusStyle = workbook.createCellStyle();
    examinastionStatusStyle.setBorderBottom(BorderStyle.MEDIUM);
    examinastionStatusStyle.setBottomBorderColor(borderColorIndex);
    examinastionStatusStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(examinastionStatusStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(examinastionStatusStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(examinastionStatusStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyleBold.setFont(titleFontBold);

    // Create row for status examination
    Row row = sheet.createRow(currentRowIndex);
    for (int i = 0; i < maxCols; i++) {
      row.createCell(i);
    }

    // fill row with examination status
    row.getCell(titleColumn).setCellValue("Rang");

    // set rank for dummy offers
    for (AwardEvaluationDocumentDTO offer : offers) {
      if (offer.getOffer().getAwardRank() != null) {
        setValueToField(offer.getOffer().getAwardRank(), row.getCell(datacolumn));
      }
      datacolumn += 2;
    }

    // append style to row
    // title style
    for (int i = 0; i < maxCols; i++) {
      if (i < 4) {
        row.getCell(i).setCellStyle(headersStyleBold);
      }
    }

    // data style
    datacolumn = 5;
    for (int i = 0; i < offers.size(); i++) {
      if ((i % 2 == 0)) {
        row.getCell(datacolumn).setCellStyle(greyBackgroundBold);
        row.getCell(datacolumn - 1).setCellStyle(whiteBackgroundBold);
      }
      if ((i % 2 != 0)) {
        row.getCell(datacolumn).setCellStyle(whiteBackgroundBold);
        row.getCell(datacolumn - 1).setCellStyle(whiteBackgroundBold);
      }
      datacolumn += 2;
    }

    // merge title cells
    mergeCell(sheet, currentRowIndex, titleColumn, 1);
    return currentRowIndex + 3;

  }

  private int fillAwardDocumentCriteria(List<CriterionDTO> submissionCriteria,
    List<AwardEvaluationDocumentDTO> offers, XSSFSheet sheet, XSSFWorkbook workbook, int maxCols,
    int currentRowIndex, LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillAwardDocumentCriteria, Parameters: submissionCriteria: {0}, "
        + "offers: {1}, sheet: {2}, workbook: {3}, maxCols: {4}, currentRowIndex: {5}, "
        + "criteriaPageBreakMap: {6}",
      new Object[]{submissionCriteria, offers, sheet, workbook, maxCols, currentRowIndex,
        criteriaPageBreakMap});

    int ratedCriteriaStartRow = currentRowIndex;
    int titleColumn = 1;
    LinkedHashMap<String, CriterionDTO> awardCriteriaMap = new LinkedHashMap<>();
    // create a dummy criterion only for page break usage
    CriterionDTO pageBreakCriterion = null;
    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);

    for (CriterionDTO criterion : submissionCriteria) {
      if (criterion.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
        awardCriteriaMap.put(criterion.getId(), criterion);
        // create a dummy criterion only for page break usage
        pageBreakCriterion = criterion;
      }
      if (criterion.getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        awardCriteriaMap.put(criterion.getId(), criterion);
      }
      if (criterion.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)) {
        awardCriteriaMap.put(criterion.getId(), criterion);
      }
    }

    int rowsCounter = 0;
    // first we create the rows and cells for all rated criteria
    for (Entry<String, CriterionDTO> entry : awardCriteriaMap.entrySet()) {
      rowsCounter += 3;
      if (entry.getValue().getSubcriterion() != null
        && !entry.getValue().getSubcriterion().isEmpty()) {
        for (int i = 0; i < entry.getValue().getSubcriterion().size(); i++) {
          rowsCounter++;
        }
      }
    }

    for (int i = 0; i < rowsCounter; i++) {
      Row row = sheet.createRow(ratedCriteriaStartRow + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    int dataColumn = 4;
    // set header and header style
    sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellValue(SURCHARGE);
    sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellStyle(headersStyleItalic);
    sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1).setCellValue("");
    sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1)
      .setCellStyle(headersStyleBoldCenter);
    sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2).setCellValue("G");
    sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
      .setCellStyle(headersStyleBoldCenter);

    List<Integer> criteriaStartEndIndexes = new ArrayList<>();
    criteriaStartEndIndexes.add(ratedCriteriaStartRow);

    for (int i = 0; i < offers.size(); i++) {
      if (i % 2 == 0) {
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(greyBackgroundBold);
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
          .setCellStyle(greyBackgroundBold);
      }
      if (i % 2 != 0) {
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
          .setCellStyle(whiteBackgroundBold);
      }
      sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellValue(GRADE);
      sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1).setCellValue(POINTS);
      dataColumn += 2;
    }

    int awardCriteraIndex = ratedCriteriaStartRow;
    // insert values to rows
    for (Entry<String, CriterionDTO> entry : awardCriteriaMap.entrySet()) {
      // set criterion description and points
      awardCriteraIndex++;
      sheet.getRow(awardCriteraIndex).getCell(titleColumn)
        .setCellValue(entry.getValue().getCriterionText());
      sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellStyle(headersStyle);
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellValue("");
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2)
        .setCellValue(entry.getValue().getWeighting().toString());
      sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleBoldCenter);
      autosizeRow(awardCriteraIndex, sheet, maxCols, CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);
      dataColumn = 4;
      int companyIndex = 0;
      for (AwardEvaluationDocumentDTO offer : offers) {
        for (OfferCriterionDTO offerCriteria : offer.getOffer().getOfferCriteria()) {
          if (offerCriteria.getCriterion().getId().equals(entry.getKey())) {
            if (companyIndex % 2 == 0) {
              sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(greyBackground);
              sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackground);
            }
            if (companyIndex % 2 != 0) {
              sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackground);
              sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackground);
            }
            if (offerCriteria.getGrade() != null) {
              BigDecimal gradeValue = offerCriteria.getGrade().setScale(2);
              setValueToField(gradeValue.toString(),
                sheet.getRow(awardCriteraIndex).getCell(dataColumn));
            }
            if (offerCriteria.getScore() != null) {
              BigDecimal scoreValue = offerCriteria.getScore().setScale(3);
              setValueToField(scoreValue.toString(),
                sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1));
            }
            dataColumn += 2;
            continue;
          }
        }
        companyIndex++;
      }
    }

    criteriaStartEndIndexes.add(awardCriteraIndex);
    criteriaPageBreakMap.put(pageBreakCriterion, criteriaStartEndIndexes);

    // set total points
    awardCriteraIndex++;
    sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellValue(TOTAL_POINTS);
    sheet.getRow(awardCriteraIndex).getCell(titleColumn).setCellStyle(headersStyleBold);
    sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellValue("");
    sheet.getRow(awardCriteraIndex).getCell(titleColumn + 1).setCellStyle(headersStyleBoldCenter);
    sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellValue("100");
    sheet.getRow(awardCriteraIndex).getCell(titleColumn + 2).setCellStyle(headersStyleCenter);

    dataColumn = 4;
    int companyIndex = 0;
    for (AwardEvaluationDocumentDTO offer : offers) {
      if (companyIndex % 2 == 0) {
        sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(greyBackgroundBold);
        sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(greyBackgroundBold);
      }
      if (companyIndex % 2 != 0) {
        sheet.getRow(awardCriteraIndex).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
        sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1).setCellStyle(whiteBackgroundBold);
      }
      if (offer.getOffer().getAwardTotalScore() != null) {
        setValueToField(offer.getOffer().getAwardTotalScore(),
          sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1));
      } else {
        setValueToField(0, sheet.getRow(awardCriteraIndex).getCell(dataColumn + 1));
      }
      dataColumn += 2;
      companyIndex++;
    }

    awardCriteraIndex += 2;

    return awardCriteraIndex;
  }

  private boolean awardSubCriteriaExist(boolean hasSubCriteria,
    List<AwardEvaluationDocumentDTO> awardEvaluationOffers) {

    LOGGER.log(Level.CONFIG,
      "Executing method awardSubCriteriaExist, Parameters: hasSubCriteria: {0}, "
        + "awardEvaluationOffers: {1}",
      new Object[]{hasSubCriteria, awardEvaluationOffers});

    for (OfferCriterionDTO offerCriterion : awardEvaluationOffers.get(0).getOffer()
      .getOfferCriteria()) {
      if (offerCriterion.getCriterion().getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
        && (offerCriterion.getCriterion().getSubcriterion() != null
        && !offerCriterion.getCriterion().getSubcriterion().isEmpty())) {
        hasSubCriteria = true;
        break;
      }
    }
    return hasSubCriteria;
  }

  /**
   * Fill the document with the company overview section.
   *
   * @param offers the offers
   * @param maxCols the max columns of the document
   * @param overviewStartRow the position of the current row
   * @param sheet the sheet
   * @param workbook the workbook
   * @param type the type
   * @return the the position of the row after filling the document
   */
  private int setAwardDocumentCompaniesOverview(List<AwardEvaluationDocumentDTO> offers,
    int maxCols, int overviewStartRow, XSSFSheet sheet, XSSFWorkbook workbook, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method setAwardDocumentCompaniesOverview, Parameters: offers: {0}, "
        + "maxCols: {0}, currentRowIndex: {2}, sheet: {3}, workbook: {4}, type: {5}",
      new Object[]{offers, maxCols, overviewStartRow, sheet, workbook, type});

    int dataColumn;
    // A hash map with the table details
    Map<String, Integer> tableIndexMapping = getCompanyOverviewTableSize(null, offers, type);

    int tableRows = tableIndexMapping.get(TABLE_ROWS);
    int maxCellNumArge = tableIndexMapping.get(MAX_CELL_NUM_ARGE);
    int maxCellNumSubContractors = tableIndexMapping.get(MAX_CELL_NUM_SUBCONTRACTORS);

    // The ending row of Partner Companies (ARGE and SubUnternehmen)
    int overviewPartnerCompaniesEndRow = maxCellNumArge + maxCellNumSubContractors;
    // The position of Offer Info
    int overviewOfferInfoRow = overviewStartRow + overviewPartnerCompaniesEndRow;

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    headersStyle.setFont(titleFont);

    fillCompanyOverviewTable(tableRows, maxCellNumArge, maxCellNumSubContractors, overviewStartRow, maxCols, sheet, headersStyle,
      greyBackground, whiteBackground, null, offers, type);

    int headerColum = 1;
    // merge cells for companies
    for (int i = 0; i < tableRows; i++) {
      mergeCell(sheet, overviewStartRow + i, headerColum, 2, false);
      dataColumn = 4;
      for (int j = 0; j < offers.size(); j++) {
        mergeCell(sheet, overviewStartRow + i, dataColumn, 1, false);
        dataColumn += 2;
      }
      autosizeRow(overviewStartRow + i, sheet, maxCols);
    }

    // calculate row height for offer information
    autosizeRow(overviewOfferInfoRow, sheet, maxCols, MERGED_CELLS_WIDTH_IN_CHARS);

    return overviewOfferInfoRow + 2;
  }

  private int setAwardDocumentCompaniesHeader(List<AwardEvaluationDocumentDTO> offers, int maxCols,
    int currentRowIndex, XSSFSheet sheet, XSSFWorkbook workbook, int companiesHeaderIndexRow) {

    LOGGER.log(Level.CONFIG,
      "Executing method setAwardDocumentCompaniesHeader, Parameters: offers: {0}, "
        + "maxCols: {1}, currentRowIndex: {2}, sheet: {3}, workbook: {4}, "
        + "companiesHeaderIndexRow: {5}",
      new Object[]{offers, maxCols, currentRowIndex, sheet, workbook, companiesHeaderIndexRow});

    int companyNamesRow = currentRowIndex;
    int AmountRow = companyNamesRow + 1;
    int operatingCostAmountRow = AmountRow + 1;
    int varianteRow = operatingCostAmountRow + 1;
    int dataColumn = 4;
    int titleColumn = 1;
    int companyIndex = 0;

    // background color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    // Border color
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    short borderColorIndex = borderColor.getIndex();

    CellStyle companyNameStyle = workbook.createCellStyle();
    companyNameStyle.setLocked(true);
    companyNameStyle.setAlignment(HorizontalAlignment.CENTER);
    companyNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    companyNameStyle.setBorderRight(BorderStyle.MEDIUM);
    companyNameStyle.setRightBorderColor(borderColorIndex);
    companyNameStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyNameStyle.setLeftBorderColor(borderColorIndex);
    companyNameStyle.setBorderTop(BorderStyle.MEDIUM);
    companyNameStyle.setTopBorderColor(borderColorIndex);
    companyNameStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyNameStyle.setBottomBorderColor(borderColorIndex);
    companyNameStyle.setWrapText(true);

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle companyOverviewStyleRight = workbook.createCellStyle();
    companyOverviewStyleRight.setAlignment(HorizontalAlignment.RIGHT);
    companyOverviewStyleRight.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyleRight.setRightBorderColor(borderColorIndex);
    companyOverviewStyleRight.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyleRight.setLeftBorderColor(borderColorIndex);
    companyOverviewStyleRight.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyleRight.setTopBorderColor(borderColorIndex);
    companyOverviewStyleRight.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyleRight.setBottomBorderColor(borderColorIndex);
    companyOverviewStyleRight.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyNameStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);

    XSSFCellStyle greyBackgroundRight = workbook.createCellStyle();
    greyBackgroundRight.cloneStyleFrom(companyOverviewStyleRight);
    greyBackgroundRight.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundRight.setVerticalAlignment(VerticalAlignment.TOP);
    greyBackgroundRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundRight.setFillForegroundColor(greyBackgroundColor);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyNameStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);

    CellStyle whiteBackgroundRight = workbook.createCellStyle();
    whiteBackgroundRight.cloneStyleFrom(companyOverviewStyleRight);
    whiteBackgroundRight.setAlignment(HorizontalAlignment.RIGHT);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackgroundBold.setFont(titleFontBold);
    companyNameStyle.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    whiteBackgroundRight.setFont(titleFont);
    greyBackgroundRight.setFont(titleFont);

    Row row = sheet.createRow(companyNamesRow);
    for (int i = 0; i < maxCols; i++) {
      Cell cell = row.createCell(i);
      if (i < 4) {
        continue;
      }
      if (i == dataColumn) {
        cell.setCellValue(offers.get(companyIndex).getCompanyName());
        companyIndex++;
        dataColumn += 2;
      }
      if ((companyIndex - 1) % 2 == 0) {
        cell.setCellStyle(greyBackgroundBold);
      } else {
        cell.setCellStyle(companyNameStyle);
      }
    }

    // set data column to 4 again to merge cells
    dataColumn = 4;
    for (int i = 0; i < offers.size(); i++) {
      mergeCell(sheet, companyNamesRow, dataColumn, 1);
      dataColumn += 2;
    }
    // auto size rows with merged cell
    autosizeRow(companyNamesRow, sheet, maxCols, MERGED_CELLS_WIDTH_IN_CHARS);

    // check if row height is less than the default row height
    // for companies name row
    float minDefaultHeight = row.getHeightInPoints();
    if (minDefaultHeight < 30) {
      row.setHeightInPoints((float) 30); // approximately 33px
    }

    // set the row index in order to calculate later the row height
    // for page break
    companiesHeaderIndexRow = companyNamesRow;

    BigDecimal amount = null;
    // Add offer prices and remarks
    for (int i = AmountRow; i < varianteRow + 1; i++) {
      companyIndex = 0;
      dataColumn = 4;
      sheet.createRow(i);
      for (int j = 0; j < maxCols; j++) {
        Cell cell = sheet.getRow(i).createCell(j);
        if (i == AmountRow) {
          if (j == titleColumn) {
            cell.setCellValue(NET_PRICE);
          }
          if (j == dataColumn) {
            if (offers.get(companyIndex).getOffer().getAmount() != null) {
              amount = SubmissConverter
                .customRoundNumber(offers.get(companyIndex).getOffer().getAmount());
              cell.setCellValue(templateBeanService.setCurrencyFormat(amount));
            }
            companyIndex++;
            dataColumn += 2;
          }
        }
        if (i == operatingCostAmountRow) {
          if (j == titleColumn) {
            cell.setCellValue("Betriebskosten exkl. MWST");
          }
          if (j == dataColumn) {
            if (offers.get(companyIndex).getOffer().getOperatingCostsAmount() != null) {
              amount = SubmissConverter
                .customRoundNumber(offers.get(companyIndex).getOffer().getOperatingCostsAmount());
              cell.setCellValue(templateBeanService.setCurrencyFormat(amount));
            }
            companyIndex++;
            dataColumn += 2;
          }
        }
        if (i == varianteRow) {
          if (j == titleColumn) {
            cell.setCellValue("Variante");
          }
          if (j == dataColumn) {
            setValueToField(offers.get(companyIndex).getOffer().getVariantNotes(), cell);
            companyIndex++;
            dataColumn += 2;
          }
        }

        if (j > 3 && ((companyIndex - 1) % 2 == 0)) {
          cell.setCellStyle(greyBackgroundRight);
        }
        if (j > 3 && ((companyIndex - 1) % 2 != 0)) {
          cell.setCellStyle(whiteBackgroundRight);
        }

        if (j <= 3) {
          cell.setCellStyle(headersStyle);
        }
      }
    }

    // merge cells
    // set data column to 4 again to merge cells
    dataColumn = 4;
    for (int i = AmountRow; i < varianteRow + 1; i++) {
      mergeCell(sheet, i, titleColumn, 2);
      for (int j = 0; j < offers.size(); j++) {
        mergeCell(sheet, i, dataColumn, 1);
        dataColumn += 2;
      }
      dataColumn = 4;
    }

    // calculate row height for variante
    autosizeRow(varianteRow, sheet, maxCols, MERGED_CELLS_WIDTH_IN_CHARS);
    return varianteRow + 2;
  }

  private void setAwardDocumentMaxMinPoints(SubmissionDTO submission, int maxCols, XSSFSheet sheet,
    XSSFWorkbook workbook, boolean hasOperatingCosts) {

    LOGGER.log(Level.CONFIG,
      "Executing method setAwardDocumentCompaniesHeader, Parameters: submission: {0}, "
        + "maxCols: {1}, sheet: {2}, workbook: {3}, hasOperatingCosts: {4}",
      new Object[]{submission, maxCols, sheet, workbook, hasOperatingCosts});

    int maxScoreRow = 4;
    int minScoreRow = 5;
    int reviewTeamRow = 6;
    int formulaCol = 2;
    int pointsTitleCol = 7;
    int pointsCol = 8;

    String priceFormula = getDocumentPriceFormula(submission);
    String operatingCostFormula = getDocumentOperatingCostFormula(submission);

    CellStyle titleStyleBold = workbook.createCellStyle();
    titleStyleBold.setAlignment(HorizontalAlignment.LEFT);
    titleStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle titleStyleBoldRight = workbook.createCellStyle();
    titleStyleBoldRight.setAlignment(HorizontalAlignment.RIGHT);
    titleStyleBoldRight.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle dataStyle = workbook.createCellStyle();
    dataStyle.setAlignment(HorizontalAlignment.LEFT);
    dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
    dataStyle.setWrapText(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    XSSFFont dataFont = workbook.createFont();
    dataFont.setFontName(FONT);
    dataFont.setFontHeight(FONT_SIZE);

    titleStyleBold.setFont(titleFontBold);
    dataStyle.setFont(dataFont);
    titleStyleBoldRight.setFont(titleFontBold);

    for (int i = maxScoreRow; i <= reviewTeamRow; i++) {
      Row row = sheet.createRow(i);

      for (int j = 0; j < maxCols; j++) {
        Cell cell = row.createCell(j);
        if (i == maxScoreRow) {
          if (j == 1) {
            cell.setCellValue("Preisbewertungsformel:");
            cell.setCellStyle(titleStyleBold);
          }
          if (j == formulaCol) {
            cell.setCellValue(priceFormula);
            cell.setCellStyle(dataStyle);
          }
          if (j == pointsTitleCol) {
            cell.setCellValue("Maximalnote:");
            cell.setCellStyle(titleStyleBoldRight);
          }
          if (j == pointsCol) {
            setValueToField(submission.getAwardMaxGrade().toString(), cell);
            cell.setCellStyle(dataStyle);
          }
        }
        if (i == minScoreRow) {
          if (j == 1 && hasOperatingCosts) {
            cell.setCellValue("Betriebskostenbewertungsformel:");
            cell.setCellStyle(titleStyleBold);
          }
          if (j == formulaCol) {
            cell.setCellValue(operatingCostFormula);
            cell.setCellStyle(dataStyle);
          }
          if (j == 1 && !hasOperatingCosts) {
            cell.setCellValue("Bewertungsteam:");
            cell.setCellStyle(titleStyleBold);
          }
          if (j == formulaCol && !hasOperatingCosts) {
            cell.setCellValue(submission.getEvaluationThrough());
            cell.setCellStyle(dataStyle);
          }
          if (j == pointsTitleCol) {
            cell.setCellValue("Mindestnote:");
            cell.setCellStyle(titleStyleBoldRight);
          }
          if (j == pointsCol) {
            setValueToField(submission.getAwardMinGrade().toString(), cell);
            cell.setCellStyle(dataStyle);
          }
        }
        if (i == reviewTeamRow && hasOperatingCosts) {
          if (j == 1) {
            cell.setCellValue("Bewertungsteam:");
            cell.setCellStyle(titleStyleBold);
          }
          if (j == formulaCol) {
            cell.setCellValue(submission.getEvaluationThrough());
            cell.setCellStyle(dataStyle);
          }
        }
      }
    }

    // merge cells for Preisbewertungsformel - Betriebskostenbewertungsformel - Bewertungsteam
    for (int i = 0; i < 3; i++) {
      mergeCell(sheet, maxScoreRow + i, formulaCol, 4);
      // calculate row height
      autosizeRow(maxScoreRow + i, sheet, maxCols, TITLE_MERGED_CELLS_WIDTH_IN_CHARS);
    }

    // create empty row after min score
    Row row = sheet.createRow(reviewTeamRow + 1);
    for (int j = 0; j < maxCols; j++) {
      row.createCell(j);
    }
  }

  private String getDocumentPriceFormula(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDocumentOperatingCostFormula, Parameters: submission: {0}",
      submission);

    MasterListValueHistoryDTO ms = submission.getPriceFormula();
    if (submission.getPriceFormula().isActive()) {
      if (ms.getValue1() != null && ms.getValue2() != null) {
        return ms.getValue1() + ", " + ms.getValue2();
      }
    } else if (submission.getCustomPriceFormula() != null
      && !submission.getCustomPriceFormula().isEmpty()) {
      return submission.getCustomPriceFormula();
    }

    // default, when formel is not set.
    String tenantId = submission.getWorkType().getTenant().getId();
    List<MasterListValueHistoryDTO> formulas =
      sDService.masterListValueHistoryQuery(CategorySD.CALCULATION_FORMULA, tenantId);

    return formulas.get(1).getValue1() + ", " + formulas.get(1).getValue2();
  }

  private String getDocumentOperatingCostFormula(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDocumentPriceFormula, Parameters: submission: {0}",
      submission);

    // Formel Betriebskosten
    MasterListValueHistoryDTO ms = submission.getOperatingCostFormula();
    if ((ms.getValue1() != null) && (ms.getValue2() != null)) {
      return ms.getValue1() + ", " + ms.getValue2();
    } else if (submission.getCustomOperatingCostFormula() != null
      && !submission.getCustomOperatingCostFormula().isEmpty()) {
      return submission.getCustomOperatingCostFormula();
    }

    String tenantId = submission.getWorkType().getTenant().getId();
    List<MasterListValueHistoryDTO> formulas =
      sDService.masterListValueHistoryQuery(CategorySD.CALCULATION_FORMULA, tenantId);

    return formulas.get(1).getValue1() + ", " + formulas.get(1).getValue2();
  }

  /**
   *
   */
  public void setPrintLayoutSettings(XSSFSheet sheet, boolean orientation) {

    LOGGER.log(Level.CONFIG,
      "Executing method setPrintLayoutSettings, Parameters: sheet: {0}",
      sheet);

    PrintSetup ps = sheet.getPrintSetup();
    ps.setLandscape(orientation);
    ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
    ps.setFitWidth((short) 1);
    ps.setFitHeight((short) 0);
  }

  /**
   *
   */
  private void setDocumentMargins(XSSFSheet sheet) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDocumentMargins, Parameters: sheet: {0}",
      sheet);

    sheet.setMargin(Sheet.LeftMargin, 0.5);
    sheet.setMargin(Sheet.RightMargin, 0.5);
    sheet.setMargin(Sheet.TopMargin, 0.75);
    sheet.setMargin(Sheet.BottomMargin, 0.75);
  }

  private double getMainCriteriaSum(List<CriterionDTO> submissionCriteria) {

    LOGGER.log(Level.CONFIG,
      "Executing method getMainCriteriaSum, Parameters: submissionCriteria: {0}",
      submissionCriteria);

    Double totalWeight = 0.00;
    for (CriterionDTO criterion : submissionCriteria) {
      if (criterion.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)
        && criterion.getWeighting() != null) {
        totalWeight += criterion.getWeighting();
      }
    }
    return totalWeight;
  }

  private String getAwardMainCriteriaSum(LinkedHashMap<String, CriterionDTO> awardCriteriaMap) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAwardMainCriteriaSum, Parameters: awardCriteriaMap: {0}",
      awardCriteriaMap);

    Double totalWeight = 0.00;
    DecimalFormat formatter = new DecimalFormat("0.00");
    for (Entry<String, CriterionDTO> entry : awardCriteriaMap.entrySet()) {
      if (entry.getValue().getWeighting() != null) {
        totalWeight += entry.getValue().getWeighting();
      }
    }
    return formatter.format(totalWeight);
  }

  private boolean operatingCostCriteriaExist(List<CriterionDTO> submissionCriteria) {

    LOGGER.log(Level.CONFIG,
      "Executing method operatingCostCriteriaExist, Parameters: submissionCriteria: {0}",
      submissionCriteria);

    boolean operatingCostcriteria = false;
    for (CriterionDTO criterionDTO : submissionCriteria) {
      if (criterionDTO.getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        operatingCostcriteria = true;
        break;
      }
    }
    return operatingCostcriteria;
  }

  /**
   * Calculate the page breaks on Eignungsprüfung (Suitability) document.
   *
   * @param currentOffers the currentOffers
   * @param sheet the sheet
   * @param maxCols the maxCols
   * @param workbook the workbook
   * @param submission the submission
   * @param type the type
   * @param companiesHeaderIndexRow the companiesHeaderIndexRow
   * @param newSetOfCompaniesStart the newSetOfCompaniesStart
   * @param newSetOfCompaniesStop the newSetOfCompaniesStop
   * @param currentRowIndex the currentRowIndex
   * @param newSetOfCompanies the newSetOfCompanies
   * @param criteriaPageBreakMap the criteriaPageBreakMap
   * @param hasRatedSubCriteria the hasRatedSubCriteria
   * @return the last row of the document
   */
  private int setSuitabilityDocumentPageBreaks(List<SuitabilityDocumentDTO> currentOffers,
    XSSFSheet sheet, int maxCols, XSSFWorkbook workbook, SubmissionDTO submission, String type,
    int companiesHeaderIndexRow, int newSetOfCompaniesStart, int newSetOfCompaniesStop,
    int currentRowIndex, boolean newSetOfCompanies,
    LinkedHashMap<CriterionDTO, List<Integer>> criteriaPageBreakMap,
    boolean hasRatedSubCriteria) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSuitabilityDocumentPageBreaks, Parameters: currentOffers: {0}, "
        + "sheet: {1}, " + "maxCols: {2}, " + "workbook: {3}, " + "submission: {4}, "
        + "type: {5}, " + "companiesHeaderIndexRow: {6}, " + "newSetOfCompaniesStart: {7}, "
        + "newSetOfCompaniesStop: {8}, " + "currentRowIndex: {9}, "
        + "newSetOfCompanies: {10}, " + "criteriaPageBreakMap: {11}, "
        + "hasRatedSubCriteria: {12}",
      new Object[]{currentOffers, sheet, maxCols, workbook, submission, type,
        companiesHeaderIndexRow, newSetOfCompaniesStart, newSetOfCompaniesStop, currentRowIndex,
        newSetOfCompanies, criteriaPageBreakMap, hasRatedSubCriteria});

    /*
     * rowsToBeShifted = staticColumnSubTitle rows + CompaniesHeader rows
     * rowsToBeShifted is the number of rows to move down each time a page break occurs.
     */
    int rowsToBeShifted = 5;
    // temporary index for inserting titles
    int currentRow = 0;
    float currentPageHeight = 0;
    // the height of the company names row
    float companiesHeaderHeight = sheet.getRow(companiesHeaderIndexRow).getHeightInPoints();
    List<Integer> rowsToShift = new ArrayList<>();
    LinkedHashMap<CriterionDTO, Integer> criteriaHeaderTitleList = new LinkedHashMap<>();

    // we add the size of document title, submission description and the size of empty row
    float pageStaticTitleHeight = sheet.getRow(1).getHeightInPoints()
      + sheet.getRow(2).getHeightInPoints() + sheet.getDefaultRowHeightInPoints();

    // moves the index of the page break every time an entry is added to rowsToShift list
    int rowBreakSetter = 0;

    if (newSetOfCompaniesStart < 20) {
      for (int i = 0; i < newSetOfCompaniesStop; i++) {
        if (sheet.getRow(i) != null) {
          float rowHeight = sheet.getRow(i).getHeightInPoints();
          currentPageHeight += rowHeight;

          if (rowHeight == (float) 429) {
            rowsToShift.add(sheet.getRow(i).getRowNum() + 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() + 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            /*
             * The next page will start by adding the heights of the default title height and
             * the companies header row.
             */
            currentPageHeight = pageStaticTitleHeight + companiesHeaderHeight;
          } else if (currentPageHeight > A4_PAGE_HEIGHT) {

            CriterionDTO criterionBreak = pageBreakOnCriteria(i - 1, criteriaPageBreakMap);
            if (criterionBreak != null) {
              criteriaHeaderTitleList.put(criterionBreak, (i - 1) + rowBreakSetter);
            }

            rowsToShift.add(sheet.getRow(i).getRowNum() - 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() - 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            /*
             * The next page will start by adding the heights of the current row,
             * the default title height and the companies header row.
             */
            currentPageHeight =
              rowHeight + pageStaticTitleHeight + companiesHeaderHeight;
          }
        }
      }
    } else {
      float removeHeightOfStaticHeader = 0;

      if (newSetOfCompanies) {
        removeHeightOfStaticHeader = pageStaticTitleHeight;
      }

      for (int i = newSetOfCompaniesStart; i < newSetOfCompaniesStop; i++) {
        if (sheet.getRow(i) != null) {
          float rowHeight = sheet.getRow(i).getHeightInPoints();
          currentPageHeight += rowHeight;

          if (rowHeight == (float) 429) {
            rowsToShift.add(sheet.getRow(i).getRowNum() + 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() + 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            /*
             * The next page will start by adding the heights of the default title height and
             * the companies header row.
             */
            currentPageHeight = pageStaticTitleHeight + companiesHeaderHeight;
          } else if (currentPageHeight > A4_PAGE_HEIGHT - removeHeightOfStaticHeader) {

            CriterionDTO criterionBreak = pageBreakOnCriteria(i, criteriaPageBreakMap);
            if (criterionBreak != null) {
              criteriaHeaderTitleList.put(criterionBreak, (i - 1) + rowBreakSetter);
            }

            rowsToShift.add(sheet.getRow(i).getRowNum() - 1);
            sheet.setRowBreak((sheet.getRow(i).getRowNum() - 1) + rowBreakSetter);
            rowBreakSetter += rowsToBeShifted;

            /*
             * The next page will start by adding the heights of the current row,
             * the default title height and the companies header row.
             */
            currentPageHeight =
              rowHeight + pageStaticTitleHeight + companiesHeaderHeight;
            removeHeightOfStaticHeader = 0;
          }
        }
      }
    }

    // shift rows and add to the title and the company header
    int addedShiftedRows = 0;
    int addedCriteriaTitleShiftedRows = 0;
    int criteriaHeaderBuffer = 1;

    for (int i = 0; i < rowsToShift.size(); i++) {
      rowsToShift.set(i, rowsToShift.get(i) + addedShiftedRows);
      sheet.shiftRows(rowsToShift.get(i), sheet.getLastRowNum() + 1, rowsToBeShifted, true, false);
      currentRow = setDocumentStaticColumnSubTitle(submission, maxCols, rowsToShift.get(i), sheet,
        workbook, type);
      currentRow = setDocumentCompaniesHeader(currentOffers, maxCols, currentRow, sheet, workbook,
        companiesHeaderIndexRow);

      // insert criteria header if page break occurs on a criterion
      for (Entry<CriterionDTO, Integer> entry : criteriaHeaderTitleList.entrySet()) {
        if (entry.getValue().equals(rowsToShift.get(i))) {
          if (hasRatedSubCriteria) {
            criteriaHeaderBuffer = 2;
          }
          sheet.shiftRows(currentRow, sheet.getLastRowNum() + 1, criteriaHeaderBuffer, true, false);
          setSuitabilityCriteriaHeader(currentOffers, entry.getKey(), maxCols, hasRatedSubCriteria,
            currentRow, sheet, workbook);
          addedCriteriaTitleShiftedRows += criteriaHeaderBuffer;
        }
      }
      addedShiftedRows += rowsToBeShifted;
    }
    return currentRowIndex + addedShiftedRows + addedCriteriaTitleShiftedRows;
  }


  /* Method that inserts the criteria header*/
  private void setSuitabilityCriteriaHeader(List<SuitabilityDocumentDTO> currentOffers,
    CriterionDTO criterionDTO, int maxCols, boolean hasRatedSubCriteria, int currentRow,
    XSSFSheet sheet, XSSFWorkbook workbook) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSuitabilityCriteriaHeader, Parameters: currentOffers: {0}, "
        + "criterionDTO: {1}, " + "maxCols: {2}, " + "hasRatedSubCriteria: {3}, "
        + "currentRow: {4}, " + "sheet: {5}, " + "workbook: {6}",
      new Object[]{currentOffers, criterionDTO, maxCols, hasRatedSubCriteria, currentRow, sheet,
        workbook});

    int ratedCriteriaStartRow = currentRow;
    int titleMaxrows = 0;
    String criteriaTitle;
    int titleColumn = 1;
    DecimalFormat formatter = new DecimalFormat("0.00");

    // Border color
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFPalette palette = wb.getCustomPalette();
    HSSFColor borderColor = palette.findSimilarColor(128, 128, 128);
    XSSFColor greyBackgroundColor = new XSSFColor(new Color(242, 242, 242));
    short borderColorIndex = borderColor.getIndex();
    try {
      wb.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    CellStyle companyOverviewStyle = workbook.createCellStyle();
    companyOverviewStyle.setLocked(true);
    companyOverviewStyle.setBorderRight(BorderStyle.MEDIUM);
    companyOverviewStyle.setRightBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderLeft(BorderStyle.MEDIUM);
    companyOverviewStyle.setLeftBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderTop(BorderStyle.MEDIUM);
    companyOverviewStyle.setTopBorderColor(borderColorIndex);
    companyOverviewStyle.setBorderBottom(BorderStyle.MEDIUM);
    companyOverviewStyle.setBottomBorderColor(borderColorIndex);
    companyOverviewStyle.setWrapText(true);

    CellStyle headersStyle = workbook.createCellStyle();
    headersStyle.cloneStyleFrom(companyOverviewStyle);
    headersStyle.setAlignment(HorizontalAlignment.LEFT);
    headersStyle.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleCenter = workbook.createCellStyle();
    headersStyleCenter.cloneStyleFrom(companyOverviewStyle);
    headersStyleCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleItalic = workbook.createCellStyle();
    headersStyleItalic.cloneStyleFrom(headersStyle);
    headersStyleItalic.setAlignment(HorizontalAlignment.LEFT);
    headersStyleItalic.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBold = workbook.createCellStyle();
    headersStyleBold.cloneStyleFrom(headersStyle);
    headersStyleBold.setAlignment(HorizontalAlignment.LEFT);
    headersStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle headersStyleBoldCenter = workbook.createCellStyle();
    headersStyleBoldCenter.cloneStyleFrom(headersStyle);
    headersStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
    headersStyleBoldCenter.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackground = workbook.createCellStyle();
    greyBackground.cloneStyleFrom(companyOverviewStyle);
    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackground.setFillForegroundColor(greyBackgroundColor);
    greyBackground.setAlignment(HorizontalAlignment.RIGHT);
    greyBackground.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackground = workbook.createCellStyle();
    whiteBackground.cloneStyleFrom(companyOverviewStyle);
    whiteBackground.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackground.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFCellStyle greyBackgroundBold = workbook.createCellStyle();
    greyBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    greyBackgroundBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    greyBackgroundBold.setFillForegroundColor(greyBackgroundColor);
    greyBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    greyBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    CellStyle whiteBackgroundBold = workbook.createCellStyle();
    whiteBackgroundBold.cloneStyleFrom(companyOverviewStyle);
    whiteBackgroundBold.setAlignment(HorizontalAlignment.RIGHT);
    whiteBackgroundBold.setVerticalAlignment(VerticalAlignment.TOP);

    XSSFFont titleFont = workbook.createFont();
    titleFont.setFontName(FONT);
    titleFont.setFontHeight(FONT_SIZE);

    XSSFFont titleFontItalic = workbook.createFont();
    titleFontItalic.setFontName(FONT);
    titleFontItalic.setFontHeight(FONT_SIZE);
    titleFontItalic.setItalic(true);

    XSSFFont titleFontItalicNine = workbook.createFont();
    titleFontItalicNine.setFontName(FONT);
    titleFontItalicNine.setFontHeight(FONT_SIZE_NINE);
    titleFontItalicNine.setItalic(true);

    XSSFFont titleFontBold = workbook.createFont();
    titleFontBold.setFontName(FONT);
    titleFontBold.setFontHeight(FONT_SIZE);
    titleFontBold.setBold(true);

    greyBackground.setFont(titleFont);
    whiteBackground.setFont(titleFont);
    greyBackgroundBold.setFont(titleFontBold);
    whiteBackgroundBold.setFont(titleFontBold);
    headersStyle.setFont(titleFont);
    headersStyleItalic.setFont(titleFontItalic);
    headersStyleBold.setFont(titleFontBold);
    headersStyleBoldCenter.setFont(titleFontBold);
    headersStyleCenter.setFont(titleFont);

    if (hasRatedSubCriteria) {
      criteriaTitle = "Bewertetes Kriterium";
      titleMaxrows = 2;
    } else {
      criteriaTitle = RATED_CRITERIA;
      titleMaxrows = 1;
    }

    // Create rows for header
    for (int i = 0; i < titleMaxrows; i++) {
      Row row = sheet.createRow(ratedCriteriaStartRow + i);
      for (int j = 0; j < maxCols; j++) {
        row.createCell(j);
      }
    }

    if (titleMaxrows == 2) {
      int dataColumn = 4;

      // set header and header style
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellValue(criteriaTitle);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellStyle(headersStyleItalic);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1).setCellValue("G");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1)
        .setCellStyle(headersStyleBoldCenter);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2).setCellValue("UK");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
        .setCellStyle(headersStyleBoldCenter);

      for (int i = 0; i < currentOffers.size(); i++) {
        if (i % 2 == 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(greyBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(greyBackgroundBold);
        }
        if (i % 2 != 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(whiteBackgroundBold);
        }
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellValue(GRADE);
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1).setCellValue(POINTS);
        dataColumn += 2;
      }

      // set criterion description and points
      ratedCriteriaStartRow++;
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn)
        .setCellValue(criterionDTO.getCriterionText());
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellStyle(headersStyleBold);
      String weightingValue = formatter.format(criterionDTO.getWeighting());
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1).setCellValue(weightingValue);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1)
        .setCellStyle(headersStyleBoldCenter);
      autosizeRow(ratedCriteriaStartRow, sheet, maxCols,
        CRITERIA_TITLE_MERGED_CELLS_WIDTH_IN_CHARS);
      if (criterionDTO.getSubcriterion() != null && !criterionDTO.getSubcriterion().isEmpty()) {
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
          .setCellValue(getSubCriteriaTotalWeight(criterionDTO.getSubcriterion()));
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
          .setCellStyle(headersStyleCenter);
      } else {
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2).setCellValue(CELL_VALUE);
        sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
          .setCellStyle(headersStyleCenter);
      }

      dataColumn = 4;
      int companyIndex = 0;
      for (SuitabilityDocumentDTO offer : currentOffers) {
        for (OfferCriterionDTO offerCriteria : offer.getOffer().getOfferCriteria()) {
          if (offerCriteria.getCriterion().getId().equals(criterionDTO.getId())) {
            if (companyIndex % 2 == 0) {
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(greyBackground);
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
                .setCellStyle(greyBackground);
            }
            if (companyIndex % 2 != 0) {
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(whiteBackground);
              sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
                .setCellStyle(whiteBackground);
            }
            if (offerCriteria.getGrade() != null) {
              BigDecimal gradeValue = offerCriteria.getGrade().setScale(2);
              setValueToField(gradeValue.toString(),
                sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn));
            }
            if (offerCriteria.getScore() != null) {
              BigDecimal scoreValue = offerCriteria.getScore().setScale(3);
              setValueToField(scoreValue.toString(),
                sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1));
            }
            dataColumn += 2;
            continue;
          }
        }
        companyIndex++;
      }
    } else {
      int dataColumn = 4;
      // set header and header style
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellValue(criteriaTitle);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn).setCellStyle(headersStyleItalic);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1).setCellValue("");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 1)
        .setCellStyle(headersStyleBoldCenter);
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2).setCellValue("G");
      sheet.getRow(ratedCriteriaStartRow).getCell(titleColumn + 2)
        .setCellStyle(headersStyleBoldCenter);

      for (int i = 0; i < currentOffers.size(); i++) {
        if (i % 2 == 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(greyBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(greyBackgroundBold);
        }
        if (i % 2 != 0) {
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellStyle(whiteBackgroundBold);
          sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1)
            .setCellStyle(whiteBackgroundBold);
        }
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn).setCellValue(GRADE);
        sheet.getRow(ratedCriteriaStartRow).getCell(dataColumn + 1).setCellValue(POINTS);
        dataColumn += 2;
      }
    }
  }

  /* Method that checks if sub criteria exists*/
  private boolean ratedSubCriterionExist(List<SuitabilityDocumentDTO> offers) {
    boolean hasSubCriteria = false;
    for (OfferCriterionDTO offerCriterion : offers.get(0).getOffer()
      .getOfferCriteria()) {
      if (offerCriterion.getCriterion().getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)
        && (offerCriterion.getCriterion().getSubcriterion() != null
        && !offerCriterion.getCriterion().getSubcriterion().isEmpty())) {
        hasSubCriteria = true;
        break;
      }
    }
    return hasSubCriteria;
  }

  /**
   * Set cell header style.
   *
   * @param j the j index
   * @param cell the cell
   * @param headersStyle the headerStyle
   */
  private void setCellHeaderStyleCompanyOverview(int j, Cell cell, CellStyle headersStyle) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCellHeaderStyleCompanyOverview, Parameters: j: {0}, "
        + "cell: {1}, headersStyle: {2}",
      new Object[]{j, cell, headersStyle});

    if (j > 1 && j <= 3) {
      cell.setCellStyle(headersStyle);
    }
  }

  /**
   * Colors the cell grey or white.
   *
   * @param j the j index
   * @param companyIndex the company index
   * @param cell the cell
   * @param greyBackground the grey background color
   * @param whiteBackground the white background color
   */
  private void setCellColorCompanyOverview(int j, int companyIndex, Cell cell,
    XSSFCellStyle greyBackground, CellStyle whiteBackground) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCellColorCompanyOverview, Parameters: j: {0}, "
        + "companyIndex: {1}, cell: {2}, greyBackground: {3}, whiteBackground: {4}",
      new Object[]{j, companyIndex, cell, greyBackground, whiteBackground});

    if (j > 3 && ((companyIndex - 1) % 2 == 0)) {
      cell.setCellStyle(greyBackground);
    } else if (j > 3 && ((companyIndex - 1) % 2 != 0)) {
      cell.setCellStyle(whiteBackground);
    }
  }

  /**
   * Calculates the extra rows added.
   *
   * @param value the string value
   * @return the total number of rows
   */
  private int addExtraRows(String value) {

    LOGGER.log(Level.CONFIG,
      "Executing method addExtraRows, Parameters: value: {0}",
      value);

    int numLines = getNumLines(value);
    int cellNum;

    if (numLines % 32 == 0) {
      cellNum = numLines / 32;
    } else {
      cellNum = numLines / 32 + 1;
    }

    return cellNum;
  }

  /**
   * Add extra line(s) at the end of the string value if the length of the value
   * in a specific line is greater than 45 chars, which means that this value will be splitted in 2
   * lines.
   *
   * @param value the string value
   * @return the value with extra line(s) if needed
   */
  private String addExtraLine(String value) {

    LOGGER.log(Level.CONFIG,
      "Executing method addExtraLine, Parameters: value: {0}",
      value);

    if (StringUtils.isBlank(value)) {
      return StringUtils.EMPTY;
    }

    // counte the extra lines
    int counter = countExtraLines(value);

    // Add a dummy string at the end of the value
    for (int i = 0; i < counter; i++) {
      value = value.concat("\n");
    }
    return value;
  }

  /**
   * Counting how many extra lines will be added if the length of the value
   * in a specific line is greater than 45 chars, which means that this value will be splitted in 2
   * lines.
   *
   * @param value the value
   * @return the counter
   */
  private int countExtraLines(String value) {

    int counter = 0;

    if (StringUtils.isNotBlank(value)) {
      String[] valueSplt = value.split("\n");
      // Checking if a value is splitted in 2 lines
      for (String v : valueSplt) {
        if (v.length() > MERGED_CELLS_WIDTH_IN_CHARS) {
          counter++;
        }
      }
    }
    return  counter;
  }

  /**
   * Gets the value that fits on a specific cell
   *
   * @param value the string value
   * @param currentCellNum the current cell number
   * @param maxCellNum the max cell number
   * @return the value that fits the cell
   */
  private String getCellValue(String value, int currentCellNum, int maxCellNum) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCellValue, Parameters: value: {0}, currentCellNum: {1}, "
        + "maxCellNum: {2}",
      new Object[]{value, currentCellNum, maxCellNum});

    if (StringUtils.isBlank(value)) {
      return StringUtils.EMPTY;
    }

    // the starting index of the value
    int startIndex = (currentCellNum > 0) ? currentCellNum * 32 - countExtraLines(value) : currentCellNum * 32;
    // the ending index of the value
    int lastIndex = startIndex + 32;

    String[] valueSplt = value.split("\n");

    StringBuilder returnValue = new StringBuilder();

    /*
     * if the length of the remaining value is less than 32
     * set the lastIndex to point in the last value
     */
    if (valueSplt.length < lastIndex) {
      lastIndex = valueSplt.length;
    }

    for (int i = startIndex; i < lastIndex; i++) {
      // The last value of the String should not contain "\n" at the end
      if (currentCellNum == maxCellNum && i == lastIndex - 1) {
        returnValue.append(valueSplt[i]);
      } else {
        returnValue.append(valueSplt[i]).append("\n");
      }
    }

    return returnValue.toString();
  }

  /**
   * Fill the Company Overview table.
   *
   * @param tableRows the table rows
   * @param maxCellNumArge the maxCellNumArge
   * @param maxCellNumSubContractors the maxCellNumSubContractors
   * @param overviewStartRow the overviewStartRow
   * @param maxCols the maxCols
   * @param sheet the sheet
   * @param headersStyle the headersStyle
   * @param greyBackground the greyBackground
   * @param whiteBackground the whiteBackground
   * @param suitabilityOffers the suitabilityOffers
   * @param awardEvaluationoffers the awardEvaluationoffers
   * @param type the type
   */
  private void fillCompanyOverviewTable(int tableRows, int maxCellNumArge,
    int maxCellNumSubContractors,
    int overviewStartRow, int maxCols, XSSFSheet sheet, CellStyle headersStyle,
    XSSFCellStyle greyBackground, CellStyle whiteBackground,
    List<SuitabilityDocumentDTO> suitabilityOffers,
    List<AwardEvaluationDocumentDTO> awardEvaluationoffers, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillCompanyOverviewTable, Parameters: tableRows: {0}, "
        + "maxCellNumArge: {1}, maxCellNumSubContractors: {2}, overviewStartRow: {3}, "
        + "maxCols: {4}, sheet: {5}, headersStyle: {6}, greyBackground: {7}, "
        + "whiteBackground: {8}, suitabilityOffers: {9}, awardEvaluationoffers: {10}, "
        + "type: {11}",
      new Object[]{tableRows, maxCellNumArge, maxCellNumSubContractors, overviewStartRow, maxCols,
      sheet, headersStyle, greyBackground, whiteBackground, suitabilityOffers, awardEvaluationoffers,
      type});

    int offerCellNum = maxCellNumArge + maxCellNumSubContractors;
    int notesCellNum = offerCellNum + 1;

    // Checking if the document type is Suitability
    boolean isTypeSuitability = type.equals(TemplateConstants.SUITABILITY);

    // i  is counting the rows
    for (int i = 0; i < tableRows; i++) {
      Row row = sheet.createRow(overviewStartRow + i);
      // Counting the companies
      int companyIndex = 0;
      // The starting column for inserting the company values
      int dataColumn = 4;
      // j  is counting the columns
      for (int j = 0; j < maxCols; j++) {
        Cell cell = row.createCell(j);

        // Fill the first column with the titles
        if (j == 1) {
          if (i < maxCellNumArge) {
            cell.setCellValue(ARGE_PARTNER);
            cell.setCellStyle(headersStyle);
          } else if (i < offerCellNum) {
            cell.setCellValue(SUBUNTERNEHMEN);
            cell.setCellStyle(headersStyle);
          } else if (i == offerCellNum) {
            cell.setCellValue(OFFERT_INFORMATIONEN);
            cell.setCellStyle(headersStyle);
          } else if (i == notesCellNum && isTypeSuitability) {
            cell.setCellValue(BEMERKUNGEN);
            cell.setCellStyle(headersStyle);
          }
        }

        // Fill the table with the company data
        if (j == dataColumn) {
          // Arge
          if (i < maxCellNumArge) {
            // Suitability document
            if (isTypeSuitability) {
              String argeValue = addExtraLine(
                suitabilityOffers.get(companyIndex).getJointVentures());
              cell.setCellValue(
                getCellValue(argeValue, i,
                  maxCellNumArge - 1));
              // Award document
            } else {
              String argeValue = addExtraLine(
                awardEvaluationoffers.get(companyIndex).getJointVentures());
              cell.setCellValue(
                getCellValue(argeValue, i,
                  maxCellNumArge - 1));
            }
            // SubUnternehmen
          } else if (i < offerCellNum) {
            // Suitability document
            if (isTypeSuitability) {
              String subContractorValue = addExtraLine(
                suitabilityOffers.get(companyIndex).getSubContractors());
              cell.setCellValue(
                getCellValue(subContractorValue,
                  i - maxCellNumArge, maxCellNumSubContractors - 1));
              // Award document
            } else {
              String subContractorValue = addExtraLine(
                awardEvaluationoffers.get(companyIndex).getSubContractors());
              cell.setCellValue(
                getCellValue(subContractorValue,
                  i - maxCellNumArge, maxCellNumSubContractors - 1));
            }
            // Offer Info
          } else if (i == offerCellNum) {
            // Suitability document
            if (isTypeSuitability) {
              cell.setCellValue(suitabilityOffers.get(companyIndex).getOfferNotes());
              // Award document
            } else {
              cell.setCellValue(awardEvaluationoffers.get(companyIndex).getOffer().getNotes());
            }
            // Suitability Notes
          } else if (i == notesCellNum && isTypeSuitability) {
            cell.setCellValue(
              suitabilityOffers.get(companyIndex).getOffer().getqExSuitabilityNotes());
          }
          companyIndex++;
          dataColumn += 2;
        }

        // add the cell colors
        setCellColorCompanyOverview(j, companyIndex, cell, greyBackground, whiteBackground);

        // add the cell headerStyle
        setCellHeaderStyleCompanyOverview(j, cell, headersStyle);
      }
    }
  }

  /**
   * Get the number of lines of a cell value terminating with \n.
   *
   * @param value the cell value
   * @return the number of lines
   */
  private int getNumLines(String value) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNumLines, Parameters: value: {0}",
      value);

    int numLines = 1;
    if (StringUtils.isNotBlank(value) && value.contains("\n")) {
      for (int i = 0; i < value.length(); i++) {
        if (value.charAt(i) == '\n') {
          numLines++;
        }
      }
    }
    return numLines;
  }
}
