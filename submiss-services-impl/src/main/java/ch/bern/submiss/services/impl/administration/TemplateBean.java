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
import ch.bern.submiss.services.api.administration.LegalHearingService;
import ch.bern.submiss.services.api.administration.LexiconService;
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.RuleService;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.administration.SubmissionCloseService;
import ch.bern.submiss.services.api.constants.DocumentCompanyOffersColumns;
import ch.bern.submiss.services.api.constants.DocumentPlaceholders;
import ch.bern.submiss.services.api.constants.DocumentProperties;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.ProofStatus;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.constants.TemplateConstants;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.AwardInfoDTO;
import ch.bern.submiss.services.api.dto.AwardInfoFirstLevelDTO;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CompanySearchDTO;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.DocumentDTO;
import ch.bern.submiss.services.api.dto.DocumentPropertiesDTO;
import ch.bern.submiss.services.api.dto.ExclusionReasonDTO;
import ch.bern.submiss.services.api.dto.LegalHearingExclusionDTO;
import ch.bern.submiss.services.api.dto.MasterListValueDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.OfferSubcriterionDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.api.dto.SubmissionCompanyOverviewDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmissionOverviewDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.dto.SubmittentOfferDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QCountryHistoryEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Class TemplateBean.
 */
@Singleton
public class TemplateBean extends BaseService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(TemplateBean.class.getName());

  /**
   * The Constant SUB_U.
   */
  private static final String SUB_U = " (Sub.U. ";
  /**
   * The Constant alphabet.
   */
  private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  /**
   * The tenant service.
   */
  @Inject
  protected SDTenantService sDTenantService;
  /**
   * The offerService service.
   */
  @Inject
  protected OfferService offerService;
  /**
   * The lexicon service.
   */
  @Inject
  protected LexiconService lexiconService;
  /**
   * The rule service.
   */
  @Inject
  protected RuleService ruleService;

  /**
   * The department code service.
   */
  @Inject
  protected SDDepartmentService sDDepartmentService;

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
   * The legal hearing service.
   */
  @Inject
  protected LegalHearingService legalHearingService;

  /**
   * The company service.
   */
  @Inject
  protected CompanyService companyService;

  /**
   * The report service.
   */
  @Inject
  protected ReportServiceImpl reportService;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q country history entity.
   */
  QCountryHistoryEntity qCountryHistoryEntity = QCountryHistoryEntity.countryHistoryEntity;
  /**
   * The submission close service.
   */
  @Inject
  private SubmissionCloseService submissionCloseService;
  /**
   * The offer service.
   */
  @Inject
  private ImportExportFileServiceImpl importExportFileService;

  /**
   * Fill table properties.
   *
   * @param tableProperties the table properties
   * @param documentProperties the document properties
   * @return the map
   */
  public Map<String, String> fillTableProperties(Map<String, String> tableProperties,
    DocumentPropertiesDTO documentProperties) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillTableProperties, Parameters:  tableProperties: {0}, "
        + "documentProperties: {1}",
      new Object[]{tableProperties, documentProperties});

    Map<String, String> newTblProperties = new HashMap<>();
    if (tableProperties != null) {
      newTblProperties = tableProperties;
    }
    fillTableMarginAndBorderProperties(newTblProperties, documentProperties);
    fillTableFontsAndBoldProperties(newTblProperties, documentProperties);
    fillTablePositionProperties(newTblProperties, documentProperties);
    return newTblProperties;
  }

  /**
   * Fill table position properties.
   *
   * @param tableProperties the table properties
   * @param documentProperties the document properties
   * @return the map
   */
  private Map<String, String> fillTablePositionProperties(Map<String, String> tableProperties,
    DocumentPropertiesDTO documentProperties) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillTablePositionProperties, Parameters: tableProperties: {0}, "
        + "documentProperties: {1}",
      new Object[]{tableProperties, documentProperties});

    Map<String, String> newTblProperties = new HashMap<>();
    if (tableProperties != null) {
      newTblProperties = tableProperties;
    }
    if (documentProperties.getTablePosition() != null) {
      newTblProperties.put(DocumentProperties.TABLE_POSITION.getValue(),
        documentProperties.getTablePosition());
    }
    if (documentProperties.getWidth() != null) {
      newTblProperties.put(DocumentProperties.WIDTH.getValue(), documentProperties.getWidth());
    }
    if (documentProperties.getValue() != null) {
      newTblProperties.put(DocumentProperties.VALUE.getValue(), documentProperties.getValue());
    }
    if (documentProperties.getSpacing() != null) {
      newTblProperties.put(DocumentProperties.SPACING.getValue(), documentProperties.getSpacing());
    }
    if (documentProperties.getAlignRight() != null) {
      newTblProperties.put(DocumentProperties.ALIGN_RIGHT.getValue(),
        documentProperties.getAlignRight());
    }
    return newTblProperties;
  }

  /**
   * Fill table fonts and bold properties.
   *
   * @param tableProperties the table properties
   * @param documentProperties the document properties
   * @return the map
   */
  private Map<String, String> fillTableFontsAndBoldProperties(Map<String, String> tableProperties,
    DocumentPropertiesDTO documentProperties) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillTableFontsAndBoldProperties, Parameters:  tableProperties: {0}, "
        + "documentProperties: {1}",
      new Object[]{tableProperties, documentProperties});

    Map<String, String> newTblProperties = new HashMap<>();
    if (tableProperties != null) {
      newTblProperties = tableProperties;
    }
    if (documentProperties.getBoldHeader() != null) {
      newTblProperties.put(DocumentProperties.BOLD_HEADER.getValue(),
        documentProperties.getBoldHeader());
    }
    if (documentProperties.getFonts() != null) {
      newTblProperties.put(DocumentProperties.FONTS.getValue(), documentProperties.getFonts());
    }
    if (documentProperties.getFontSize() != null) {
      newTblProperties.put(DocumentProperties.FONT_SIZE.getValue(),
        documentProperties.getFontSize());
    }
    if (documentProperties.getArial() != null) {
      newTblProperties.put(DocumentProperties.ARIAL.getValue(), documentProperties.getArial());
    }
    if (documentProperties.getBoldContent() != null) {
      newTblProperties.put(DocumentProperties.BOLD_CONTENT.getValue(),
        documentProperties.getBoldContent());
    }
    return newTblProperties;
  }

  /**
   * Fill table margin and border properties.
   *
   * @param tableProperties the table properties
   * @param documentProperties the document properties
   * @return the map
   */
  private Map<String, String> fillTableMarginAndBorderProperties(
    Map<String, String> tableProperties, DocumentPropertiesDTO documentProperties) {

    LOGGER.log(Level.CONFIG,
      "Executing method fillTableMarginAndBorderProperties, Parameters:  tableProperties: {0}, "
        + "documentProperties: {1}",
      new Object[]{tableProperties, documentProperties});

    Map<String, String> newTblProperties = new HashMap<>();
    if (tableProperties != null) {
      newTblProperties = tableProperties;
    }
    if (documentProperties.getBottomMargin() != null) {
      newTblProperties.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        documentProperties.getBottomMargin());
    }
    if (documentProperties.getTopMargin() != null) {
      newTblProperties.put(DocumentProperties.TOP_MARGIN.getValue(),
        documentProperties.getTopMargin());
    }
    if (documentProperties.getRightMargin() != null) {
      newTblProperties.put(DocumentProperties.RIGHT_MARGIN.getValue(),
        documentProperties.getRightMargin());
    }
    if (documentProperties.getLeftMargin() != null) {
      newTblProperties.put(DocumentProperties.LEFT_MARGIN.getValue(),
        documentProperties.getLeftMargin());
    }
    if (documentProperties.getRemoveBorder() != null) {
      newTblProperties.put(DocumentProperties.REMOVE_BORDER.getValue(),
        documentProperties.getRemoveBorder());
    }
    if (documentProperties.getBorderSpace() != null) {
      newTblProperties.put(DocumentProperties.BORDER_SPACE.getValue(),
        documentProperties.getBorderSpace());
    }
    return newTblProperties;
  }

  /**
   * Sets the offer protocol table properties.
   *
   * @param template the template
   * @param submission the submission
   * @param documentTableHeader the document table header
   * @param documentTableContent the document table content
   * @param tableProperties2 the table properties 2
   * @return the list
   */
  public List<OfferDTO> setOfferProtocolTableProperties(MasterListValueHistoryEntity template,
    SubmissionDTO submission, List<String> documentTableHeader,
    List<LinkedHashMap<Map<String, String>, String>> documentTableContent,
    Map<String, String> tableProperties2) {

    LOGGER.log(Level.CONFIG,
      "Executing method setOfferProtocolTableProperties, Parameters: template: {0}, "
        + "submission: {1}, documentTableHeader: {2}, documentTableContent: {3}, "
        + "tableProperties2: {4}",
      new Object[]{template, submission, documentTableHeader, documentTableContent,
        tableProperties2});

    List<OfferDTO> submissionOffers = null;

    if (submission.getProcess() == Process.SELECTIVE
      && Template.BEWERBER_UBERSICHT.equals(template.getShortCode())) {
      submissionOffers = offerService.getApplicationsBySubmission(submission.getId());
    } else {
      submissionOffers = offerService.getOffersBySubmission(submission.getId());
    }

    if (template.getShortCode().equals(Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW)
      || template.getShortCode().equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW)) {
      tableProperties2.put(DocumentProperties.TABLE_POSITION.getValue(),
        DocumentProperties.NUMBER_7.getValue());
      tableProperties2.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_200.getValue());
      tableProperties2.put(DocumentProperties.FONTS.getValue(),
        DocumentProperties.ARIAL.getValue());
      setOfferDLDocTableProperties(documentTableHeader, documentTableContent, submissionOffers,
        template.getShortCode());
    } else if (template.getShortCode().equals(Template.BEWERBER_UBERSICHT)) {
      tableProperties2.put(DocumentProperties.TABLE_POSITION.getValue(),
        DocumentProperties.NUMBER_7.getValue());
      tableProperties2.put(DocumentProperties.FONTS.getValue(),
        DocumentProperties.ARIAL.getValue());
      setTenderViewDocTableProperties(documentTableHeader, documentTableContent, submissionOffers,
        template.getShortCode());
    } else {
      if (template.getShortCode().equals(Template.OFFERTVERGLEICH_FREIHANDIG)) {
        tableProperties2.put(DocumentProperties.TABLE_POSITION.getValue(),
          DocumentProperties.NUMBER_6.getValue());
        tableProperties2.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_340.getValue());
      } else if (template.getShortCode().equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL)) {
        tableProperties2.put(DocumentProperties.TABLE_POSITION.getValue(),
          DocumentProperties.NUMBER_7.getValue());
        tableProperties2.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_340.getValue());
      } else if (template.getShortCode().equals(Template.OFFERRTOFFNUNGSPROTOKOLL)) {
        tableProperties2.put(DocumentProperties.TABLE_POSITION.getValue(),
          DocumentProperties.NUMBER_9.getValue());
        tableProperties2.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_240.getValue());
      }
      createDocumentTable(documentTableHeader, documentTableContent, submissionOffers,
        template.getShortCode());
    }
    return submissionOffers;
  }

  /**
   * Creates the table for template PT13.
   *
   * @param documentHeaderTable the header submission offers
   * @param documentTableContent the tenders view(bewerberubersicht)
   * @param submissionOffers the submission offers
   * @param templateCode the template code
   */
  private void setTenderViewDocTableProperties(List<String> documentHeaderTable,
    List<LinkedHashMap<Map<String, String>, String>> documentTableContent,
    List<OfferDTO> submissionOffers, String templateCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method setTenderViewDocTableProperties, Parameters: documentHeaderTable: {0}, "
        + "documentTableContent: {1}, submissionOffers: {2}, templateCode: {3}",
      new Object[]{documentHeaderTable, documentTableContent, submissionOffers, templateCode});

    createDocumentTableHeader(documentHeaderTable, templateCode);

    Collections.sort(submissionOffers, ComparatorUtil.sortCompanyOffersForDocument);

    for (OfferDTO offer : submissionOffers) {
      LinkedHashMap<Map<String, String>, String> subContent = new LinkedHashMap<>();
      StringBuilder offerApplicationDate = new StringBuilder();

      Map<String, String> cellProperties1 = new HashMap<>();
      cellProperties1.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_1240.getValue()); // 2.19 cm
      cellProperties1.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties1.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties1.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties1.put(DocumentProperties.VALUE.getValue(),
        DocumentCompanyOffersColumns.COLUMN_1.toString());
      cellProperties1.put(DocumentProperties.FONTS.getValue(), DocumentProperties.ARIAL.getValue());

      /** Table column 1 Date content */
      subContent.put(cellProperties1,
        (offer.getApplicationDate() != null ? offerApplicationDate
          .append(SubmissConverter.convertToSwissDate(offer.getApplicationDate())).toString()
          : offerApplicationDate.append(TemplateConstants.EMPTY_STRING).toString()));

      Map<String, String> cellProperties2 = new HashMap<>();
      cellProperties2.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_13040.getValue()); // 23 cm
      cellProperties2.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties2.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties2.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties2.put(DocumentProperties.VALUE.getValue(),
        DocumentCompanyOffersColumns.COLUMN_2.toString());
      cellProperties2.put(DocumentProperties.FONTS.getValue(), DocumentProperties.ARIAL.getValue());

      /** Table column 2 BewerberIn content */
      subContent.put(cellProperties2, getContentOfCompanyNameColumn(offer, templateCode, null));
      documentTableContent.add(subContent);

    }
  }

  /**
   * Creates the table for template PT10 and PT11.
   *
   * @param documentHeaderTable the header submission offers
   * @param documentTableContent the anonymous offer opening protocol
   * @param submissionOffers the submission offers
   * @param templateCode the template code
   */
  private void setOfferDLDocTableProperties(List<String> documentHeaderTable,
    List<LinkedHashMap<Map<String, String>, String>> documentTableContent,
    List<OfferDTO> submissionOffers, String templateCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method setOfferDLDocTableProperties, Parameters: documentHeaderTable: {0}, "
        + "documentTableContent: {1}, submissionOffers: {2}, templateCode: {3}",
      new Object[]{documentHeaderTable, documentTableContent, submissionOffers, templateCode});

    createDocumentTableHeader(documentHeaderTable, templateCode);

    Collections.sort(submissionOffers, ComparatorUtil.sortCompanyOffersForDocument);
    Collections.sort(submissionOffers, ComparatorUtil.offerNotesComparison);

    for (OfferDTO offer : submissionOffers) {
      LinkedHashMap<Map<String, String>, String> subContent = new LinkedHashMap<>();
      StringBuilder offerDate = new StringBuilder();
      StringBuilder notes = new StringBuilder();

      /** add counter to use it for numbering comments at keyword column */
      int counter = submissionOffers.indexOf(offer) + 1;
      /**
       * Different content of table for OFFERRTOFFNUNGSPROTOKOLL_DL_WW (PT10) and
       * ANONIMISIERTES_OFFERRTOFFNUNGSPROTOKOLL_DL_WW (PT11)
       */
      if (templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW)) {

        Map<String, String> cellProperties2 = new HashMap<>();
        cellProperties2.put(DocumentProperties.WIDTH.getValue(),
          DocumentProperties.NUMBER_990.getValue()); // width: 1.75 cm
        cellProperties2.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties2.put(DocumentProperties.TOP_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties2.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
        cellProperties2.put(DocumentProperties.VALUE.getValue(),
          DocumentProperties.NUMBER_1.getValue());
        cellProperties2.put(DocumentProperties.FONTS.getValue(),
          DocumentProperties.ARIAL.getValue());

        /** Table column 1 Date content */
        subContent
          .put(cellProperties2,
            (offer.getOfferDate() != null ? offerDate
              .append(SubmissConverter.convertToSwissDate(offer.getOfferDate())).toString()
              : offerDate.append(TemplateConstants.EMPTY_STRING).toString()));

        Map<String, String> cellProperties3 = new HashMap<>();
        cellProperties3.put(DocumentProperties.WIDTH.getValue(),
          DocumentProperties.NUMBER_9325.getValue()); // width: 16.69 cm
        cellProperties3.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties3.put(DocumentProperties.TOP_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties3.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
        cellProperties3.put(DocumentProperties.VALUE.getValue(),
          DocumentProperties.NUMBER_2.getValue());
        cellProperties3.put(DocumentProperties.FONTS.getValue(),
          DocumentProperties.ARIAL.getValue());

        /** Table column 2 Team content */
        subContent.put(cellProperties3,
          getContentOfCompanyNameColumn(offer, templateCode, TemplateConstants.EMPTY_STRING));

        Map<String, String> cellProperties4 = new HashMap<>();
        cellProperties4.put(DocumentProperties.WIDTH.getValue(),
          DocumentProperties.NUMBER_3700.getValue()); // width: 6.67 cm
        cellProperties4.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties4.put(DocumentProperties.TOP_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties4.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
        cellProperties4.put(DocumentProperties.VALUE.getValue(),
          DocumentProperties.NUMBER_3.getValue());
        cellProperties4.put(DocumentProperties.FONTS.getValue(),
          DocumentProperties.ARIAL.getValue());

        StringBuilder contentKeyword = new StringBuilder();
        contentKeyword.append(counter).append(") ")
          .append(notes.append(offer.getNotes()).toString());
        /** Table column 3 Keyword content */
        subContent.put(cellProperties4, (offer.getNotes() != null ? contentKeyword.toString()
          : counter + ")" + TemplateConstants.EMPTY_STRING));

        documentTableContent.add(subContent);
      }
      /** the content of table for ANONIMISIERTES_OFFERRTOFFNUNGSPROTOKOLL_DL_WW (PT11) */
      else {
        Map<String, String> cellProperties5 = new HashMap<>();
        cellProperties5.put(DocumentProperties.WIDTH.getValue(),
          DocumentProperties.NUMBER_990.getValue()); // 1.75 cm
        cellProperties5.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties5.put(DocumentProperties.TOP_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties5.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
        cellProperties5.put(DocumentProperties.VALUE.getValue(),
          DocumentProperties.NUMBER_1.getValue());
        cellProperties5.put(DocumentProperties.FONTS.getValue(),
          DocumentProperties.ARIAL.getValue());
        /** Table column 1 Date content */
        subContent
          .put(cellProperties5,
            (offer.getOfferDate() != null ? offerDate
              .append(SubmissConverter.convertToSwissDate(offer.getOfferDate())).toString()
              : offerDate.append(TemplateConstants.EMPTY_STRING).toString()));

        Map<String, String> cellProperties6 = new HashMap<>();
        cellProperties6.put(DocumentProperties.WIDTH.getValue(),
          DocumentProperties.NUMBER_3825.getValue()); // 6.75 cm width of column
        cellProperties6.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties6.put(DocumentProperties.TOP_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        cellProperties6.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
        cellProperties6.put(DocumentProperties.VALUE.getValue(),
          DocumentProperties.NUMBER_3.getValue());
        cellProperties6.put(DocumentProperties.FONTS.getValue(),
          DocumentProperties.ARIAL.getValue());
        StringBuilder contentKeyword = new StringBuilder();
        contentKeyword.append(counter).append(") ")
          .append(notes.append(offer.getNotes()).toString());
        /** Table column 2 Keyword content */
        subContent.put(cellProperties6, (offer.getNotes() != null ? contentKeyword.toString()
          : counter + ")" + TemplateConstants.EMPTY_STRING));

        documentTableContent.add(subContent);
      }
    }
  }

  /**
   * Separated function to fill the table header of the documents.
   *
   * @param documentHeaderTable the document header table
   * @param templateCode the template code
   */
  public void createDocumentTableHeader(List<String> documentHeaderTable, String templateCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method createDocumentTableHeader, Parameters: documentHeaderTable: {0}, "
        + "templateCode: {1}",
      templateCode);

    /**
     * Create a list of Strings for the header of the table Different Table Header Titles depending
     * on template
     **/
    if (templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW)) {
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_DATE.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_TEAM.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_KEYWORD.getValue());
    } else if (templateCode.equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW)) {
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_DATE.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_KEYWORD.getValue());
    } else if (templateCode.equals(Template.BEWERBER_UBERSICHT)) {
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_DATE.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_TENDERS.getValue());
    } else {
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_RANKING.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_DATE.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_COMPANY.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_GROSS_AMOUNT.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_DISCOUNT.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_BUILDING_COSTS.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_DISCOUNT2.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_AMOUNT.getValue());
      documentHeaderTable.add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_VAT.getValue());
      documentHeaderTable
        .add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_NOTES.getValue());
    }
  }

  /**
   * Creates the common table for the templates (PT08,PT09,PT11).
   *
   * @param documentHeaderTable the document header table
   * @param documentTableContent the document table content
   * @param submissionOffers the submission offers
   * @param templateCode the template code
   */
  public void createDocumentTable(List<String> documentHeaderTable,
    List<LinkedHashMap<Map<String, String>, String>> documentTableContent,
    List<OfferDTO> submissionOffers, String templateCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method createDocumentTable, Parameters: documentHeaderTable: {0}, "
        + "documentTableContent: {1}, submissionOffers: {2}, templateCode: {3}",
      new Object[]{documentHeaderTable, documentTableContent, submissionOffers, templateCode});

    createDocumentTableHeader(documentHeaderTable, templateCode);

    /* Use comparator for sorting of the offers */
    Collections.sort(submissionOffers, ComparatorUtil.sortCompanyOffersForDocument);

    int i = 1;
    int j = 0;
    int reseted = 0;
    String alphaenum = "";
    /* Loop offers of the submissionOffers */
    for (OfferDTO offer : submissionOffers) {
      /*
       * reset integer j to zero if j is greater than the number which corresponds of the last
       * character of the alphabet
       */
      if (alphabet.length == j++) {
        j = 0;
        reseted++;
      }
      if (reseted == 0) {
        alphaenum = StringUtils.EMPTY + alphabet[j - 1];
      } else {
        /*
         * if j is greater than the last number alphabetic for example z the 26th it will add the
         * character that correspons to the one before alphabetically and the new added. like AA
         */
        if (templateCode.equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL)) {
          alphaenum = StringUtils.EMPTY + alphabet[reseted - 1]
            + alphabet[j];
        }
      }
      /* use locale for the correct format (switzerlands amount format) */
      Locale locale = new Locale("de", "CH");
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
      LinkedHashMap<Map<String, String>, String> subContent = new LinkedHashMap<>();

      /* Use i variable to give to every offer the ranking as they came from the comparator */
      Map<String, String> cellProperties = new HashMap<>();
      cellProperties.put(DocumentProperties.WIDTH.getValue(), "390"); // 0.69 cm
      cellProperties.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());

      cellProperties.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_1.getValue());

      Map<String, String> cellProperties2 = new HashMap<>();
      cellProperties2.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_990.getValue()); // 1.75 cm
      cellProperties2.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties2.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties2.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());

      StringBuilder offerDate = new StringBuilder();
      cellProperties2.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_2.getValue());

      subContent.put(cellProperties, StringUtils.EMPTY + i++);

      subContent.put(cellProperties2,
        (offer.getOfferDate() != null
          ? offerDate.append(SubmissConverter.convertToSwissDate(offer.getOfferDate()))
          .toString()
          : offerDate.append(TemplateConstants.EMPTY_STRING).toString()));

      Map<String, String> cellProperties3 = new HashMap<>();
      cellProperties3.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_3970.getValue()); // 7 cm
      cellProperties3.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties3.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties3.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());

      cellProperties3.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_3.getValue());
      subContent.put(cellProperties3,
        getContentOfCompanyNameColumn(offer, templateCode, alphaenum));

      Map<String, String> cellProperties4 = new HashMap<>();
      cellProperties4.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_1700.getValue()); // 3cm
      cellProperties4.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_160.getValue());
      cellProperties4.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties4.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties4.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_4.getValue());
      if (templateCode.equals(Template.OFFERTVERGLEICH_FREIHANDIG)
        || templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL)
        || templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW)
        || templateCode.equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL)) {
        cellProperties4.put(DocumentProperties.ALIGN_RIGHT.getValue(), Boolean.TRUE.toString());
      }
      String grossAmount = (offer.getIsCorrected()
        ? currencyFormatter.format(offer.getGrossAmountCorrected())
        .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING)
        : currencyFormatter.format(offer.getGrossAmount())
          .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING));
      subContent.put(cellProperties4,
        offer.getGrossAmount() != null ? grossAmount.trim() : TemplateConstants.EMPTY_STRING);

      Map<String, String> cellProperties5 = new HashMap<>();
      cellProperties5.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties5.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties5.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties5.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_850.getValue()); // 1.5cm
      cellProperties5.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_5.getValue());
      cellProperties5.put(DocumentProperties.ALIGN_RIGHT.getValue(), Boolean.TRUE.toString());
      if (offer.getDiscount() != null) {
        if (offer.getIsDiscountPercentage()) {
          StringBuilder discount = new StringBuilder();
          discount
            .append(String.format(TemplateConstants.TWO_DECIMAL_PLACES_COMMA, offer.getDiscount())
              .replace(",", TemplateConstants.EMPTY_STRING))
            .append(TemplateConstants.PERCENTAGE_SYMBOL);
          subContent.put(cellProperties5, discount.toString().trim());
        } else {
          subContent.put(cellProperties5,
            currencyFormatter.format(offer.getDiscount())
              .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING)
              .trim());
        }
      } else {
        subContent.put(cellProperties5, TemplateConstants.EMPTY_STRING);
      }
      Map<String, String> cellProperties7 = new HashMap<>();
      cellProperties7.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties7.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties7.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties7.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_5.getValue());
      cellProperties7.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_850.getValue()); // 1.5cm

      cellProperties7.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_6.getValue());
      cellProperties7.put(DocumentProperties.ALIGN_RIGHT.getValue(), Boolean.TRUE.toString());
      if (offer.getBuildingCosts() != null) {
        if (offer.getIsBuildingCostsPercentage()) {
          StringBuilder buildingCosts = new StringBuilder();
          buildingCosts
            .append(String
              .format(TemplateConstants.TWO_DECIMAL_PLACES_COMMA, offer.getBuildingCosts())
              .replace(",", TemplateConstants.EMPTY_STRING))
            .append(TemplateConstants.PERCENTAGE_SYMBOL);
          subContent.put(cellProperties7, buildingCosts.toString().trim());
        } else {
          subContent.put(cellProperties7,
            currencyFormatter.format(offer.getBuildingCosts())
              .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING)
              .trim());
        }
      } else {
        subContent.put(cellProperties7, TemplateConstants.EMPTY_STRING);
      }
      cellProperties5.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_7.getValue());
      if (offer.getDiscount2() != null) {
        if (offer.getIsDiscount2Percentage()) {
          StringBuilder discount2 = new StringBuilder();
          discount2
            .append(
              String.format(TemplateConstants.TWO_DECIMAL_PLACES_COMMA, offer.getDiscount2())
                .replace(",", TemplateConstants.EMPTY_STRING))
            .append(TemplateConstants.PERCENTAGE_SYMBOL);
          subContent.put(cellProperties5, discount2.toString().trim());
        } else {
          subContent.put(cellProperties5,
            currencyFormatter.format(offer.getDiscount2())
              .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING)
              .trim());
        }
      } else {
        subContent.put(cellProperties5, TemplateConstants.EMPTY_STRING);
      }
      cellProperties4.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_8.getValue());
      subContent
        .put(cellProperties4,
          (offer.getAmount() != null
            ? currencyFormatter.format(offer.getAmount())
            .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT,
              TemplateConstants.EMPTY_STRING)
            .trim()
            : String.format("%.2f", TemplateConstants.DEFAULT_DOUBLE).trim()));
      cellProperties5.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_9.getValue());
      if (offer.getVat() != null) {
        if (offer.getIsVatPercentage()) {
          StringBuilder vat = new StringBuilder();
          vat.append(String.format(TemplateConstants.TWO_DECIMAL_PLACES_COMMA, offer.getVat())
            .replace(",", TemplateConstants.EMPTY_STRING))
            .append(TemplateConstants.PERCENTAGE_SYMBOL);
          subContent.put(cellProperties5, vat.toString().trim());
        } else {
          subContent.put(cellProperties5,
            currencyFormatter.format(offer.getVat())
              .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING)
              .trim());
        }
      } else {
        subContent.put(cellProperties5, TemplateConstants.EMPTY_STRING);
      }
      Map<String, String> cellProperties6 = new HashMap<>();
      cellProperties6.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_2130.getValue()); // 3.75 cm
      cellProperties6.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties6.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties6.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties6.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_10.getValue());
      subContent.put(cellProperties6, fillDocumentNotes(offer, templateCode));
      documentTableContent.add(subContent);
    }
  }

  /**
   * This function is used to return the whole table(the header and the content) for the document
   * Submittentenlist as it fills the variables header and content that are defined at the start so
   * to be ready for use them when the function to create the document will be called.
   *
   * @param header the header
   * @param content the content
   * @param offers the offers
   * @return the list
   */
  public List<Map<byte[], String>> createSubmittentenListeTable(List<String> header,
    List<LinkedHashMap<Map<String, String>, String>> content, List<SubmittentOfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method createSubmittentenListeTable, Parameters: header: {0}, "
        + "content: {1}, offers: {2}",
      new Object[]{header, content, offers});

    String dateNow = SubmissConverter.convertToSwissDate(new Date());

    /** Create a list of Strings for the header of the table */
    header.add("Firmenname");
    header.add("Adresse");
    header.add("PLZ");
    header.add("Ort");
    header.add("LF");
    header.add("F50+");
    header.add("TLP");
    header.add("Nachweise " + dateNow);

    Collections.sort(offers, ComparatorUtil.sortCompaniesOffersByCompanyName);
    /**
     * Loop offers of the submission and add it to LinkedHashMap (position column: according
     * integer)
     */

    for (SubmittentOfferDTO offer : offers) {
      LinkedHashMap<Map<String, String>, String> subContent = new LinkedHashMap<>();

      Map<String, String> cellProperties5 = new HashMap<>();
      cellProperties5.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_4360.getValue()); // 7.69
      // cm
      cellProperties5.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties5.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties5.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties5.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_1.getValue());
      subContent.put(cellProperties5, getCompanyNameTemplatePT12(offer.getOffer()));

      Map<String, String> cellProperties6 = new HashMap<>();
      // Set the cell width to (7 - 0.75)cm, as 0.75cm of this cell are now going to be used by
      // the F50+ cell.
      cellProperties6.put(DocumentProperties.WIDTH.getValue(), cmToDocx4jDistance(7 - 0.75));
      cellProperties6.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties6.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties6.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties6.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_2.getValue());

      /**
       * show both address for the company if the second is there , else show only the first
       */
      if (offer.getSubmittent().getCompanyId().getAddress2() != null
        && !offer.getSubmittent().getCompanyId().getAddress2().isEmpty()) {
        subContent.put(cellProperties6,
          String.join(", ", offer.getSubmittent().getCompanyId().getAddress1(),
            offer.getSubmittent().getCompanyId().getAddress2()));
      } else {
        subContent.put(cellProperties6, offer.getSubmittent().getCompanyId().getAddress1());
      }

      Map<String, String> cellProperties7 = new HashMap<>();
      cellProperties7.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_850.getValue()); // 1.5cm
      cellProperties7.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties7.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties7.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties7.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_3.getValue());
      subContent.put(cellProperties7, offer.getSubmittent().getCompanyId().getPostCode());

      Map<String, String> cellProperties8 = new HashMap<>();
      // Set the cell width to (3.5 - 0.75)cm, as 0.75cm of this cell are now going to be used by
      // the F50+ cell.
      cellProperties8.put(DocumentProperties.WIDTH.getValue(), cmToDocx4jDistance(3.5 - 0.75));
      cellProperties8.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties8.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties8.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties8.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_4.getValue());
      subContent.put(cellProperties8, offer.getSubmittent().getCompanyId().getLocation());

      Map<String, String> cellProperties9 = new HashMap<>();
      cellProperties9.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_850.getValue()); // 1.5
      // cm
      cellProperties9.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties9.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties9.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties9.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_5.getValue());
      /** change format to show 3 decimals after dot */
      String strDouble =
        String.format("%.3f", offer.getSubmittent().getCompanyId().getApprenticeFactor());
      subContent.put(cellProperties9,
        offer.getSubmittent().getCompanyId().getApprenticeFactor() == null
          ? String.format("%.3f", TemplateConstants.DEFAULT_DOUBLE)
          : strDouble);

      // Add cell for the fifty plus factor (F50+) of the submittent company.
      Map<String, String> cellProperties12 = new HashMap<>();
      // Set the cell width to 1.499cm instead of 1.5cm, as the last value creates a conflict and
      // prevents the cell from being created.
      cellProperties12.put(DocumentProperties.WIDTH.getValue(), cmToDocx4jDistance(1.499));
      cellProperties12.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties12.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties12.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties12.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_5.getValue());
      // Format the fifty plus factor to contain 3 decimals.
      String formattedFiftyPlusFactor =
        String.format("%.3f", offer.getSubmittent().getCompanyId().getFiftyPlusFactor());
      subContent.put(cellProperties12,
        offer.getSubmittent().getCompanyId().getFiftyPlusFactor() == null
          ? String.format("%.3f", TemplateConstants.DEFAULT_DOUBLE) : formattedFiftyPlusFactor);

      Map<String, String> cellProperties10 = new HashMap<>();
      cellProperties10.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_710.getValue()); // 1.25
      // cm
      cellProperties10.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties10.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties10.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties10.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_6.getValue());
      subContent.put(cellProperties10, offer.getSubmittent().getCompanyId().getTlp() == null
        ? lexiconService.getTranslation(Boolean.FALSE.toString(),
        TemplateConstants.TRANSLATION_FORMAT)
        : lexiconService.getTranslation(offer.getSubmittent().getCompanyId().getTlp().toString(),
          TemplateConstants.TRANSLATION_FORMAT));

      Map<String, String> cellProperties11 = new HashMap<>();
      cellProperties11.put(DocumentProperties.WIDTH.getValue(),
        DocumentProperties.NUMBER_1560.getValue()); // 2.75
      // cm
      cellProperties11.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties11.put(DocumentProperties.TOP_MARGIN.getValue(),
        DocumentProperties.NUMBER_60.getValue());
      cellProperties11.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
      cellProperties11.put(DocumentProperties.IMAGE.getValue(), Boolean.TRUE.toString());
      cellProperties11.put(DocumentProperties.VALUE.getValue(),
        DocumentProperties.NUMBER_7.getValue());
      StringBuilder proofStatusShort = new StringBuilder();

      String encodedLogo = null;
      if (offer.getSubmittent().getCompanyId().getProofStatusFabe() == 1) {
        proofStatusShort.append(TemplateConstants.PROOF_STATUS_ALL_ACTIVE);
      } else if (offer.getSubmittent().getCompanyId().getProofStatusFabe() == 2) {
        proofStatusShort.append(TemplateConstants.PROOF_STATUS_NOT_ACTIVE);
      } else if (offer.getSubmittent().getCompanyId().getProofStatusFabe() == 3) {
        // Logo must be added to show (WITH_FABE) instead of EMPTY_STRING
        if (getTenantName().equals("EWB")) {
          proofStatusShort.append(TemplateConstants.PROOF_STATUS_NOT_ACTIVE);
        } else {
          proofStatusShort.append("fabe_icon");
          LOGGER.log(Level.INFO,
            "Executing method encodeFileToBase64Binary successfully, Encoded String: {0}",
            encodedLogo);

        }
      } else {
        proofStatusShort.append("kaio_fabe_icon");
        LOGGER.log(Level.INFO,
          "Executing method encodeFileToBase64Binary successfully, Encoded String: {0}",
          encodedLogo);

      }
      subContent.put(cellProperties11, proofStatusShort.toString());
      content.add(subContent);
    }
    List<Map<byte[], String>> encodedIcons = new ArrayList<>();
    byte[] encodedLogoFabe = null;
    byte[] encodedLogoKaio = null;
    try {
      encodedLogoFabe = encodeFileToBase64Binary(TemplateConstants.FABE_ICON_PATH);
      encodedLogoKaio = encodeFileToBase64Binary(TemplateConstants.KAIO_FABE_ICON_PATH);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "IO Exception occured, Parameters: error: {0}", e.getMessage());
    }
    Map<byte[], String> fabe = new HashMap<>();
    fabe.put(encodedLogoFabe, "fabe_icon");
    Map<byte[], String> kaio = new HashMap<>();
    kaio.put(encodedLogoKaio, "kaio_fabe_icon");
    encodedIcons.add(fabe);
    encodedIcons.add(kaio);
    return encodedIcons;
  }

  /**
   * Replace awarded applicants placeholders.
   *
   * @param offer the offer
   * @param documentDTO the document DTO
   * @param placeholders the placeholders
   * @return the list
   */
  public List<LinkedHashMap<Map<String, String>, Map<String, String>>> replaceAwardedApplicantsPlaceholders(
    OfferDTO offer, DocumentDTO documentDTO, Map<String, String> placeholders) {

    List<LinkedHashMap<Map<String, String>, Map<String, String>>> tableList = new ArrayList<>();

    LOGGER.log(Level.CONFIG,
      "Executing method replaceAwardedApplicantsPlaceholders, Parameters: offer: {0}, "
        + "documentDTO: {1}, placeholders: {2}",
      new Object[]{offer, documentDTO, placeholders});

    if (offer.getIsEmptyOffer() == null || !offer.getIsEmptyOffer()) {
      Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithWeightings);
      for (OfferCriterionDTO criterion : offer.getOfferCriteria()) {
        LinkedHashMap<Map<String, String>, Map<String, String>> subContent = new LinkedHashMap<>();
        Collections.sort(criterion.getCriterion().getSubcriterion(), ComparatorUtil.subcriteria);
        if (criterion.getCriterion().getCriterionType()
          .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
          StringBuilder criterionName = new StringBuilder();
          StringBuilder criterionPoints = new StringBuilder();
          criterionName.append(criterion.getCriterion().getCriterionText());
          if (criterion.getScore() != null) {
            criterionPoints.append(criterion.getScore().toString()).append(TemplateConstants.PUNKTE)
              .append(String.format(TemplateConstants.TWO_DECIMAL_PLACES,
                criterion.getCriterion().getWeighting()))
              .append("%)");
            Map<String, String> criterionContent = new HashMap<>();
            criterionContent.put(criterionName.toString(), criterionPoints.toString());

            Map<String, String> tableProperties = new HashMap<>();
            tableProperties.put(DocumentProperties.SPACING.getValue(), "60");

            tableProperties.put(DocumentProperties.WIDTH.getValue(), "4450");
            tableProperties.put(DocumentProperties.ITALIC.getValue(), Boolean.FALSE.toString());
            subContent.put(criterionContent, tableProperties);
          }
          for (SubcriterionDTO s : criterion.getCriterion().getSubcriterion()) {
            StringBuilder subcriterionName = new StringBuilder();
            StringBuilder subcriterionPoints = new StringBuilder();
            for (OfferSubcriterionDTO sub : offer.getOfferSubcriteria()) {
              if (s.getId().equals(sub.getSubcriterion().getId())) {
                subcriterionName.append(sub.getSubcriterion().getSubcriterionText())
                ;
                if (sub.getScore() != null) {
                  subcriterionPoints.append(sub.getScore().toString())
                    .append(TemplateConstants.PUNKTE)
                    .append(String.format(TemplateConstants.TWO_DECIMAL_PLACES,
                      sub.getSubcriterion().getWeighting()))
                    .append("%)");
                  Map<String, String> criterionContent = new HashMap<>();
                  criterionContent.put(subcriterionName.toString(), subcriterionPoints.toString());

                  Map<String, String> tableProperties = new HashMap<>();
                  tableProperties.put(DocumentProperties.SPACING.getValue(), "60");

                  tableProperties.put(DocumentProperties.WIDTH.getValue(), "4450");
                  tableProperties
                    .put(DocumentProperties.ITALIC.getValue(), Boolean.TRUE.toString());

                  if (getTenantName().equals(DocumentProperties.TENANT_EWB.getValue())) {
                    tableProperties.put(DocumentProperties.FONT_SIZE.getValue(), "18");
                  } else {
                    tableProperties.put(DocumentProperties.FONT_SIZE.getValue(), "16");
                  }
                  tableProperties.put(DocumentProperties.FONT_COLOR.getValue(),
                    TemplateConstants.GRAY_COLOR);

                  subContent.put(criterionContent, tableProperties);
                }
              }
            }
          }
        }
        tableList.add(subContent);
      }
      if (offer.getOfferCriteria() == null || offer.getOfferCriteria().isEmpty()) {
        placeholders.put(DocumentPlaceholders.E_CRITERION_NAME.getValue(),
          TemplateConstants.EMPTY_STRING);
      }
    }
    return tableList;
  }

  /**
   * Replace awarded applicants placeholders.
   *
   * @param offers the offers
   * @param documentDTO the document DTO
   * @param placeholders the placeholders
   */
  public void replaceAwardedApplicantsPlaceholders(List<OfferDTO> offers, DocumentDTO documentDTO,
    Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceAwardedApplicantsPlaceholders, "
        + "Parameters: offers: {0}, documentDTO: {1}, placeholders: {2}",
      new Object[]{offers, documentDTO, placeholders});

    StringBuilder notExcludedSubName = new StringBuilder();
    StringBuilder notExcludedPoints = new StringBuilder();
    int j = 0;

    for (OfferDTO offer : offers) {
      if ((offer.getIsEmptyOffer() == null || !offer.getIsEmptyOffer())
        && (offer.getExcludedFirstLevel() == null || !offer.getExcludedFirstLevel())) {
        if (alphabet.length == j++) {
          j = 1;
        }
        notExcludedSubName.append(StringUtils.EMPTY + alphabet[j - 1]
          + TemplateConstants.NEW_LINE_STRING);

        if (offer.getqExTotalGrade() != null) {
          notExcludedPoints.append(
            String.format(TemplateConstants.THREE_DECIMAL_PLACES, offer.getqExTotalGrade())
              + TemplateConstants.NEW_LINE_STRING);
        } else {
          notExcludedPoints
            .append(TemplateConstants.EMPTY_STRING + TemplateConstants.NEW_LINE_STRING);
        }
      }
    }
    if (notExcludedSubName.length() > 1) {
      placeholders.put(DocumentPlaceholders.AWARDED_COMPANY_NAME.getValue(),
        notExcludedSubName.toString());
      placeholders.put(DocumentPlaceholders.AWARDED_TOTAL_POINTS.getValue(),
        notExcludedPoints.toString());
    } else {
      placeholders.put(DocumentPlaceholders.AWARDED_COMPANY_NAME.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.AWARDED_TOTAL_POINTS.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Replace awarded submittent placeholders.
   *
   * @param attributesMap the attributes map
   * @param offers the offers
   * @param placeholders the placeholders
   * @param documentDTO the document DTO
   * @param submission the submission
   * @return the list
   */
  public List<LinkedHashMap<Map<String, String>, Map<String, String>>> replaceAwardedSubmittentPlaceholders(
    Map<String, String> attributesMap, List<SubmittentOfferDTO> offers,
    Map<String, String> placeholders, DocumentDTO documentDTO, SubmissionDTO submission) {

    List<LinkedHashMap<Map<String, String>, Map<String, String>>> tableList = new ArrayList<>();

    LOGGER.log(Level.CONFIG,
      "Executing method replaceAwardedSubmittentPlaceholders, "
        + "Parameters: attributesMap: {0}, offers: {1}, "
        + "placeholders: {2}, documentDTO: {3}, submission: {4}",
      new Object[]{attributesMap, offers, placeholders, documentDTO, submission});

    List<OfferDTO> notExcluded = new ArrayList<>();
    if (!submission.getProcess().equals(Process.SELECTIVE)) {
      notExcluded = offerService.retrieveNotExcludedOffers(documentDTO.getFolderId());
    } else {
      notExcluded = offerService.retrieveNotExcludedSelectiveOffers(documentDTO.getFolderId());
    }

    /* Use comparator for sorting of the offers */
    Collections.sort(notExcluded, ComparatorUtil.sortCompanyOffersForDocument);

    StringBuilder notExcludedSubName = new StringBuilder();
    StringBuilder notExcludedSubAmount = new StringBuilder();

    int j = 0;

    for (OfferDTO offer : notExcluded) {
      if (alphabet.length == j++) {
        j = 1;
      }
      if (offer.getIsEmptyOffer() == null || !offer.getIsEmptyOffer()) {
        Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithWeightings);
        notExcludedSubName.append(StringUtils.EMPTY + alphabet[j - 1]
          + TemplateConstants.NEW_LINE_STRING);
        if (offer.getAmount() != null) {
          if (getTenantName().equals("EWB")) {
            notExcludedSubAmount.append("CHF ")
              .append(SubmissConverter.convertToCHFCurrencyWithoutSymbol(offer.getAmount())
                + TemplateConstants.NEW_LINE_STRING);
          } else {
            notExcludedSubAmount.append(
              "Fr. " + SubmissConverter.convertToCHFCurrencyWithoutSymbol(offer.getAmount())
                + TemplateConstants.NEW_LINE_STRING);
          }
        } else {
          notExcludedSubAmount
            .append(TemplateConstants.EMPTY_STRING + TemplateConstants.NEW_LINE_STRING);
        }
        if (offer.getIsAwarded() != null && offer.getIsAwarded()) {
          tableList.clear();
          if ((submission.getAboveThreshold() == null || !submission.getAboveThreshold())
            && (submission.getIsServiceTender() == null || !submission.getIsServiceTender())) {
            if (offer.getOperatingCostsAmount() == null
              || offer.getOperatingCostsAmount().doubleValue() == 0
              || submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
              || submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
              attributesMap.put(LookupValues.WITH_BK, "NO");
            } else {
              attributesMap.put(LookupValues.WITH_BK, "YES");
            }
          }
          setAwardedValues(placeholders, offer);

          for (OfferCriterionDTO criterion : offer.getOfferCriteria()) {
            LinkedHashMap<Map<String, String>, Map<String, String>> subContent = new LinkedHashMap<>();
            Collections.sort(criterion.getCriterion().getSubcriterion(),
              ComparatorUtil.subcriteria);
            if (criterion.getCriterion().getCriterionType()
              .equals(LookupValues.AWARD_CRITERION_TYPE)
              || criterion.getCriterion().getCriterionType()
              .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
              || criterion.getCriterion().getCriterionType()
              .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
              StringBuilder criterionName = new StringBuilder();
              StringBuilder criterionPoints = new StringBuilder();
              criterionName.append(criterion.getCriterion().getCriterionText());
              if (criterion.getScore() != null) {
                criterionPoints.append(criterion.getScore().toString()).append(" Punkte (")
                  .append(String.format("%.2f", criterion.getCriterion().getWeighting()))
                  .append("%)");
                Map<String, String> criterionContent = new HashMap<>();
                Map<String, String> tableProperties = new HashMap<>();
                tableProperties.put(DocumentProperties.SPACING.getValue(), "60");

                tableProperties.put(DocumentProperties.WIDTH.getValue(), "4450");
                tableProperties.put(DocumentProperties.ITALIC.getValue(), Boolean.FALSE.toString());
                criterionContent.put(criterionName.toString(), criterionPoints.toString());
                subContent.put(criterionContent, tableProperties);
              }

              for (SubcriterionDTO s : criterion.getCriterion().getSubcriterion()) {
                for (OfferSubcriterionDTO sub : offer.getOfferSubcriteria()) {
                  StringBuilder subcriterionName = new StringBuilder();
                  StringBuilder subcriterionPoints = new StringBuilder();
                  if (s.getId().equals(sub.getSubcriterion().getId())) {
                    subcriterionName.append(sub.getSubcriterion().getSubcriterionText());
                    if (sub.getScore() != null) {
                      subcriterionPoints.append(sub.getScore().toString()).append(" Punkte (")
                        .append(String.format("%.2f", sub.getSubcriterion().getWeighting()))
                        .append("%)");
                      Map<String, String> criterionContent = new HashMap<>();
                      Map<String, String> tableProperties = new HashMap<>();
                      tableProperties.put(DocumentProperties.SPACING.getValue(), "60");

                      tableProperties.put(DocumentProperties.WIDTH.getValue(), "4450");
                      tableProperties.put(DocumentProperties.ITALIC.getValue(),
                        Boolean.TRUE.toString());
                      if (getTenantName().equals("EWB")) {
                        tableProperties.put(DocumentProperties.FONT_SIZE.getValue(), "18");
                      } else {
                        tableProperties.put(DocumentProperties.FONT_SIZE.getValue(), "16");
                      }
                      tableProperties.put(DocumentProperties.FONT_COLOR.getValue(),
                        TemplateConstants.GRAY_COLOR);
                      criterionContent.put(subcriterionName.toString(),
                        subcriterionPoints.toString());
                      subContent.put(criterionContent, tableProperties);
                    }
                  }
                }
              }
            }
            tableList.add(subContent);
          }
          if (offer.getOfferCriteria() == null || offer.getOfferCriteria().isEmpty()) {
            placeholders.put(DocumentPlaceholders.AWARDED_CRITERION_NAME.getValue(),
              TemplateConstants.EMPTY_STRING);
          }
        }
      }
      if (notExcludedSubName.length() > 1) {
        placeholders.put(DocumentPlaceholders.EXCLUDED_SUB_NAME.getValue(),
          notExcludedSubName.toString());
        placeholders.put(DocumentPlaceholders.EXCLUDED_NET_AMOUNT.getValue(),
          notExcludedSubAmount.toString());
      } else {
        placeholders.put(DocumentPlaceholders.EXCLUDED_SUB_NAME.getValue(),
          TemplateConstants.EMPTY_STRING);
        placeholders.put(DocumentPlaceholders.EXCLUDED_NET_AMOUNT.getValue(),
          TemplateConstants.EMPTY_STRING);
      }
    }

    placeholders.put("sum_of_excluded_submittents",
      String.valueOf(offerService.retrieveExcludedOffers((documentDTO.getFolderId())).size()));
    return tableList;
  }

  public void setAwardedValues(Map<String, String> placeholders, OfferDTO offer) {
    setCompanyNameOrArge(offer, placeholders,
      DocumentPlaceholders.AWARDED_COMPANY_NAME_OR_ARGE.getValue());
    if (offer.getAwardTotalScore() != null) {
      placeholders.put(DocumentPlaceholders.AWARDED_TOTAL_POINTS.getValue(),
        offer.getAwardTotalScore().toString());
    } else {
      placeholders.put(DocumentPlaceholders.AWARDED_TOTAL_POINTS.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
    placeholders.put(DocumentPlaceholders.AWARDED_AMOUNT.getValue(),
      getTenantName().equals("EWB")
        ? SubmissConverter.convertToCHFCurrency(offer.getAmount())
        : SubmissConverter.convertToCHFCurrencyWithoutSymbol(offer.getAmount()));
    if (offer.getOperatingCostsAmount() != null
      && offer.getOperatingCostsAmount().doubleValue() != 0) {
      placeholders.put(DocumentPlaceholders.AWARDED_OPER_AMOUNT.getValue(),
        getTenantName().equals("EWB")
          ? SubmissConverter.convertToCHFCurrency(offer.getOperatingCostsAmount())
          : SubmissConverter
            .convertToCHFCurrencyWithoutSymbol(offer.getOperatingCostsAmount()));
    } else {
      placeholders.put(DocumentPlaceholders.AWARDED_OPER_AMOUNT.getValue(), "");
    }
    if (offer.getOperatingCostNotes() != null
      && StringUtils.isNotBlank(offer.getOperatingCostNotes())) {
      placeholders.put(DocumentPlaceholders.AWARDED_OPER_NOTES.getValue(),
        "(" + offer.getOperatingCostNotes() + " Fr.");
    } else {
      placeholders.put(DocumentPlaceholders.AWARDED_OPER_NOTES.getValue(), "(Fr.");
    }
    if (offer.getNotes() != null) {
      placeholders.put(DocumentPlaceholders.AWARDED_NOTES.getValue(),
        "Projekts " + offer.getNotes());
    } else {
      placeholders.put(DocumentPlaceholders.AWARDED_NOTES.getValue(), "Projekts");
    }
  }

  /**
   * Replace submission cancel placeholders.
   *
   * @param submission the submission
   * @param placeholders the placeholders
   */
  public void replaceSubmissionCancelPlaceholders(SubmissionDTO submission,
    Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceSubmissionCancelPlaceholders, Parameters: submission: {0}, "
        + "placeholders: {1}",
      new Object[]{submission, placeholders});

    StringBuilder cancelNumber = new StringBuilder();
    StringBuilder cancelDescr = new StringBuilder();
    if (submission.getSubmissionCancel() != null && !submission.getSubmissionCancel().isEmpty()) {
      for (MasterListValueDTO cancel : submission.getSubmissionCancel().get(0).getWorkTypes()) {
        setCancelReason(cancelNumber, cancelDescr, cancel);
      }
      replaceCancelPlaceholder(placeholders, cancelNumber, cancelDescr);
      StringBuilder value = new StringBuilder();
      setHeaderPlaceholder(submission, placeholders, value);
    } else {
      for (MasterListValueDTO cancel : submission.getLegalHearingTerminate().get(0)
        .getTerminationReason()) {
        setCancelReason(cancelNumber, cancelDescr, cancel);
      }
      replaceCancelPlaceholder(placeholders, cancelNumber, cancelDescr);
    }
  }

  /**
   * Sets the header placeholder.
   *
   * @param submission the submission
   * @param placeholders the placeholders
   * @param value the value
   */
  private void setHeaderPlaceholder(SubmissionDTO submission, Map<String, String> placeholders,
    StringBuilder value) {

    LOGGER.log(Level.CONFIG,
      "Executing method setHeaderPlaceholder, Parameters: submission: {0}, placeholders: {1}, "
        + "value: {2}",
      new Object[]{submission, placeholders, value});

    if (submission.getSubmissionCancel().get(0).getObjectNameRead() != null
      && submission.getSubmissionCancel().get(0).getObjectNameRead()) {
      value.append(submission.getProject().getObjectName().getValue1());
      value.append(", ");
    }
    if (submission.getSubmissionCancel().get(0).getProjectNameRead() != null
      && submission.getSubmissionCancel().get(0).getProjectNameRead()) {
      value.append(submission.getProject().getProjectName() + ", ");
    }
    if (submission.getSubmissionCancel().get(0).getWorkingClassRead() != null
      && submission.getSubmissionCancel().get(0).getWorkingClassRead()) {
      value.append(
        submission.getWorkType().getValue1() + " " + submission.getWorkType().getValue2() + ", ");
    }
    if (submission.getSubmissionCancel().get(0).getDescriptionRead() != null
      && submission.getSubmissionCancel().get(0).getDescriptionRead()
      && StringUtils.isNotBlank(submission.getDescription())) {
      value.append(submission.getDescription() + ", ");
    }
    if (value.length() > 1) {
      placeholders.put(DocumentPlaceholders.READ_VALUES.getValue(),
        value.substring(0, value.length() - 2));
    } else {
      placeholders.put(DocumentPlaceholders.READ_VALUES.getValue(), TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Replace cancel placeholder.
   *
   * @param placeholders the placeholders
   * @param cancelNumber the cancel number
   * @param cancelDescr the cancel descr
   */
  private void replaceCancelPlaceholder(Map<String, String> placeholders,
    StringBuilder cancelNumber, StringBuilder cancelDescr) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceCancelPlaceholder, Parameters: placeholders: {0}, "
        + "cancelNumber: {1}, cancelDescr: {2}",
      new Object[]{placeholders, cancelNumber, cancelDescr});

    if (cancelNumber.length() > 1) {
      placeholders.put(DocumentPlaceholders.S_CANCEL_ART_NUMBER.getValue(),
        cancelNumber.substring(0, cancelNumber.length() - 21));
      placeholders.put(DocumentPlaceholders.S_CANCEL_ART_DESCRIPTION.getValue(),
        cancelDescr.substring(0, cancelDescr.length() - 5));
    } else {
      placeholders.put(DocumentPlaceholders.S_CANCEL_ART_NUMBER.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.S_CANCEL_ART_DESCRIPTION.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Sets the cancel reason.
   *
   * @param cancelNumber the cancel number
   * @param cancelDescr the cancel descr
   * @param cancel the cancel
   */
  private void setCancelReason(StringBuilder cancelNumber, StringBuilder cancelDescr,
    MasterListValueDTO cancel) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCancelReason, Parameters: cancelNumber: {0}, cancelDescr: {1}, "
        + "cancel: {2}",
      new Object[]{cancelNumber, cancelDescr, cancel});

    MasterListValueHistoryEntity cancelEntity = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId.id.eq(cancel.getId())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    cancelNumber.append(cancelEntity.getValue1()).append(" und Art. 29, Absatz ");
    cancelDescr.append(cancelEntity.getValue2()).append(" und ");
  }

  /**
   * Sets the user attributes placeholders.
   *
   * @param department the department
   * @param direction the direction
   * @param placeholders the placeholders
   * @param signatureDepartment the signature department
   */
  public void setUserAttributesPlaceholders(DepartmentHistoryDTO department,
    DirectorateHistoryDTO direction, Map<String, String> placeholders,
    DepartmentHistoryDTO signatureDepartment) {

    LOGGER.log(Level.CONFIG,
      "Executing method setUserAttributesPlaceholders, Parameters: department: {0}, "
        + "direction: {1}, placeholders: {2}, signatureDepartment: {3}",
      new Object[]{department, direction, placeholders, signatureDepartment});

    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_ADDRESS.getValue(), direction.getAddress());
    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_POST.getValue(),
      direction.getPostCode() + " " + direction.getLocation());
    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_TEL.getValue(), direction.getTelephone());
    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_FAX.getValue(), direction.getFax());
    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_EMAIL.getValue(), direction.getEmail());
    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_WEBSITE.getValue(), direction.getWebsite());
    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_NAME.getValue(), direction.getName());
    placeholders.put(DocumentPlaceholders.R_DIRECTORATE_SHORT.getValue(),
      department.getName() + ", " + direction.getAddress() + ", "
        + direction.getPostCode() + " " + direction.getLocation());

    /*
     * If the user has inserted a department in the field "Abteilung" in
     * Unterschriftsberechtigter (Stammdaten), you will need to show the details
     * of this selected department in the Referenz of the Verfügungen.
     */
    if (signatureDepartment != null) {
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_NAME.getValue(),
        signatureDepartment.getName());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_ADDRESS.getValue(),
        signatureDepartment.getAddress());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_POST.getValue(),
        signatureDepartment.getPostCode() + " " + signatureDepartment.getLocation());

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_TEL.getValue(),
        signatureDepartment.getTelephone());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_EMAIL.getValue(),
        signatureDepartment.getEmail());

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_WEBSITE.getValue(),
        signatureDepartment.getWebsite());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_FAX.getValue(),
        signatureDepartment.getFax());

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_SHORT.getValue(),
        signatureDepartment.getName() + ", " + signatureDepartment.getAddress() + ", "
          + signatureDepartment.getPostCode() + " " + signatureDepartment.getLocation());
    } else {
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_NAME.getValue(),
        department.getName());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_ADDRESS.getValue(),
        department.getAddress());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_POST.getValue(),
        department.getPostCode() + " " + department.getLocation());

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_TEL.getValue(),
        department.getTelephone());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_EMAIL.getValue(),
        department.getEmail());

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_WEBSITE.getValue(),
        department.getWebsite());
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_FAX.getValue(),
        department.getFax());

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_SHORT.getValue(),
        department.getName() + ", " + department.getAddress() + ", " + department.getPostCode()
          + " " + department.getLocation());
    }

    if (getUser().getAttributeData(USER_ATTRIBUTES.FUNCTION.getValue()) != null) {
      placeholders.put(DocumentPlaceholders.REFERENCE_PERSON.getValue(),
        getUser().getAttributeData(USER_ATTRIBUTES.FUNCTION.getValue()));
    } else {
      placeholders.put(DocumentPlaceholders.REFERENCE_PERSON.getValue(),
        TemplateConstants.EMPTY_STRING);
    }

    placeholders.put(DocumentPlaceholders.R_PERSON_NAME.getValue(),
      getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()) + " "
        + getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()));

    placeholders.put(DocumentPlaceholders.R_PERSON_FIRSTNAME.getValue(),
      getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()) + ", "
        + getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()));

    placeholders.put(DocumentPlaceholders.R_INITIALS.getValue(),
      getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()).substring(0, 2)
        + TemplateConstants.EMPTY_STRING
        + getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()).substring(0, 1));
  }

  /**
   * Function to return in one String all the variables that are contained in the document column
   * notes for every offer Only not null will be visible.
   *
   * @param offer the offer
   * @param templateCode the template code
   * @return the string
   */
  public String fillDocumentNotes(OfferDTO offer, String templateCode) {

    LOGGER.log(Level.CONFIG, "Executing method fillDocumentNotes, Parameters: offer: {0}, "
        + "templateCode: {1}",
      new Object[]{offer, templateCode});

    Locale locale = new Locale("de", "CH");
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
    StringBuilder stringBuilder = new StringBuilder();
    String sb;
    if (offer.getPriceIncrease() != null
      && !(templateCode.equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL))) {
      stringBuilder.append(offer.getPriceIncrease())
        .append(TemplateConstants.NEW_LINE_STRING);
    }
    if (offer.getOperatingCostNotes() != null) {
      stringBuilder.append(offer.getOperatingCostNotes())
        .append(TemplateConstants.NEW_LINE_STRING);
    }
    if (offer.getOperatingCostsAmount() != null) {
      stringBuilder
        .append(currencyFormatter.format(offer.getOperatingCostsAmount())
          .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING))
        .append(TemplateConstants.NEW_LINE_STRING);
    }
    if (offer.getIsVariant() != null && offer.getIsVariant()) {
      stringBuilder.append(TemplateConstants.VARIANT)
        .append(TemplateConstants.NEW_LINE_STRING);
    }
    if (offer.getVariantNotes() != null) {
      stringBuilder.append(offer.getVariantNotes())
        .append(TemplateConstants.NEW_LINE_STRING);
    }
    if (offer.getIsPartOffer() != null && offer.getIsPartOffer()) {
      stringBuilder.append(TemplateConstants.PART_OFFER)
        .append(TemplateConstants.NEW_LINE_STRING);
    }
    if (offer.getIsEmptyOffer() != null && offer.getIsEmptyOffer()) {
      stringBuilder.append(TemplateConstants.EMPTY_OFFER)
        .append(TemplateConstants.NEW_LINE_STRING);
    }
    if (offer.getNotes() != null) {
      stringBuilder.append(offer.getNotes());
    }
    sb = stringBuilder.toString();
    if (sb.endsWith(TemplateConstants.NEW_LINE_STRING)) {
      sb = sb.substring(0, sb.length() - 1);
    }
    return sb;
  }

  /**
   * Gets the content of company name column.
   *
   * @param offer the offer
   * @param templateCode the template code
   * @param alphaEnum the alpha enum
   * @return the content of company name column
   */
  public String getContentOfCompanyNameColumn(OfferDTO offer, String templateCode,
    String alphaEnum) {

    LOGGER.log(Level.CONFIG,
      "Executing method getContentOfCompanyNameColumn, Parameters: offer: {0}, "
        + "templateCode: {1}, alphaEnum: {2}",
      new Object[]{offer, templateCode, alphaEnum});

    if (templateCode.equals(Template.OFFERTVERGLEICH_FREIHANDIG)
      || templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW)
      || templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL)
      || templateCode.equals(Template.BEWERBER_UBERSICHT)) {
      return getCompanyNameTemplatePT12(offer);
    } else if (templateCode.equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL)) {
      return alphaEnum;
    } else {
      return getCompanyNameTemplatePT08(offer);
    }
  }

  /**
   * Content of company column on Documents table when template code is PT08.
   *
   * @param offer the offer
   * @return the company name template PT 08
   */
  private String getCompanyNameTemplatePT08(OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyNameTemplatePT08, Parameters: offer: {0}",
      offer);

    if (offer.getSubmittent().getJointVentures().isEmpty()
      && offer.getSubmittent().getSubcontractors().isEmpty()) {
      return offer.getSubmittent().getCompanyId().getCompanyName() + ", "
        + offer.getSubmittent().getCompanyId().getLocation();
    } else {
      StringBuilder companyNameStr = new StringBuilder();
      companyNameStr.append(offer.getSubmittent().getCompanyId().getCompanyName());
      if (!offer.getSubmittent().getJointVentures().isEmpty()) {
        for (CompanyDTO jointVenture : offer.getSubmittent().getJointVentures()) {
          companyNameStr.append(", ").append(jointVenture.getCompanyName()).append(" (ARGE)");
        }
      }
      if (!offer.getSubmittent().getSubcontractors().isEmpty()) {
        for (CompanyDTO subcontractor : offer.getSubmittent().getSubcontractors()) {
          companyNameStr.append(", ").append(subcontractor.getCompanyName()).append(" (Sub U.)");
        }
      }
      return companyNameStr.toString();
    }
  }

  /**
   * Content of company column on Documents table when template code is PT12.
   *
   * @param offer the offer
   * @return the company name template PT 12 and PT 10
   */
  private String getCompanyNameTemplatePT12(OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyNameTemplatePT12, Parameters: offer: {0}",
      offer);

    StringBuilder companyColumn = new StringBuilder();

    /** create a default StringBuilder for main company's name */
    StringBuilder mainCompany = new StringBuilder();
    mainCompany.append(offer.getSubmittent().getCompanyId().getCompanyName()).append(", ")
      .append(offer.getSubmittent().getCompanyId().getLocation());

    /** create a default StringBuilder for the jointVentures */
    StringBuilder jointVentures = new StringBuilder();
    for (CompanyDTO jointVenture : offer.getSubmittent().getJointVentures()) {
      jointVentures.append(" / ").append(jointVenture.getCompanyName()).append(", ")
        .append(jointVenture.getLocation());
    }
    /** create a default StringBuilder for the sub contarctors */
    StringBuilder subContractors = new StringBuilder();
    for (CompanyDTO subcontractor : offer.getSubmittent().getSubcontractors()) {
      subContractors.append(subcontractor.getCompanyName()).append(", ")
        .append(subcontractor.getLocation()).append(" / ");
    }

    /** When no joint ventures and no sub Contractors */
    if (offer.getSubmittent().getJointVentures().isEmpty()
      && offer.getSubmittent().getSubcontractors().isEmpty()) {
      companyColumn.append(new StringBuilder(mainCompany));
    }

    /** When there are Joint ventures and no Sub Contractors */
    else if (!offer.getSubmittent().getJointVentures().isEmpty()
      && offer.getSubmittent().getSubcontractors().isEmpty()) {
      companyColumn.append(TemplateConstants.ARGE).append(new StringBuilder(mainCompany));
      companyColumn.append(new StringBuilder(jointVentures));

    }

    /** When sub contractors and no Joint ventures */
    else if (offer.getSubmittent().getJointVentures().isEmpty()
      && !offer.getSubmittent().getSubcontractors().isEmpty()) {

      companyColumn.append(new StringBuilder(mainCompany));
      companyColumn.append(SUB_U).append(new StringBuilder(subContractors));
    }

    // when both joint ventures and sub contractors
    else {
      companyColumn.append(TemplateConstants.ARGE)
        .append(new StringBuilder(mainCompany).append(new StringBuilder(jointVentures)));
      companyColumn.append(SUB_U).append(new StringBuilder(subContractors));
    }
    if (companyColumn.toString().endsWith(" / ")) {
      companyColumn.delete(companyColumn.length() - 3, companyColumn.length());
      companyColumn.append(")");
    }

    return companyColumn.toString();
  }

  /**
   * Replace excluded placeholders.
   *
   * @param placeholders the placeholders
   * @param exclusionReasons the exclusion reasons
   */
  public void replaceExcludedPlaceholders(Map<String, String> placeholders,
    Set<MasterListValueHistoryDTO> exclusionReasons) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceExcludedPlaceholders, Parameters: placeholders: {0}, "
        + "exclusionReasons: {1}",
      new Object[]{placeholders, exclusionReasons});

    StringBuilder cancelNumber = new StringBuilder();
    StringBuilder cancelDescr = new StringBuilder();
    if (exclusionReasons != null && !exclusionReasons.isEmpty()) {
      for (MasterListValueHistoryDTO exclusionReason : exclusionReasons) {
        if (exclusionReason.getToDate() == null) {
          cancelNumber.append(exclusionReason.getValue1()).append(", ");
          cancelDescr.append(exclusionReason.getValue2()).append(", ");
        }
      }

      if (cancelNumber.length() > 1) {
        placeholders.put(DocumentPlaceholders.EXCLUSION_NUMBER.getValue(),
          cancelNumber.substring(0, cancelNumber.length() - 2));
        placeholders.put(DocumentPlaceholders.EXCLUSION_DESCRIPTION.getValue(),
          cancelDescr.substring(0, cancelDescr.length() - 2));
      } else {
        placeholders.put(DocumentPlaceholders.EXCLUSION_NUMBER.getValue(),
          TemplateConstants.EMPTY_STRING);
        placeholders.put(DocumentPlaceholders.EXCLUSION_DESCRIPTION.getValue(),
          TemplateConstants.EMPTY_STRING);
      }
    } else {
      placeholders.put(DocumentPlaceholders.EXCLUSION_NUMBER.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.EXCLUSION_DESCRIPTION.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Replace award info first level placeholders.
   *
   * @param submissionId the submission id
   * @param submission the submission
   * @param placeholders the placeholders
   */
  public void replaceAwardInfoFirstLevelPlaceholders(String submissionId, SubmissionDTO submission,
    Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceAwardInfoFirstLevelPlaceholders, Parameters: "
        + "submissionId: {0}, submission: {1}, "
        + "placeholders: {2}",
      new Object[]{submissionId, submission, placeholders});

    AwardInfoFirstLevelDTO awardInfo = submissionCloseService.getAwardInfoFirstLevel(submissionId);
    StringBuilder value = new StringBuilder();
    if (awardInfo.getAvailableDate() != null && awardInfo != null) {
      if (awardInfo.getObjectNameRead() != null && awardInfo.getObjectNameRead()) {
        value.append(submission.getProject().getObjectName().getValue1());
        value.append(", ");
      }
      if (awardInfo.getProjectNameRead() != null && awardInfo.getProjectNameRead()) {
        value.append(submission.getProject().getProjectName() + ", ");
      }
      if (awardInfo.getWorkingClassRead() != null && awardInfo.getWorkingClassRead()) {
        value.append(submission.getWorkType().getValue1() + " "
          + submission.getWorkType().getValue2() + ", ");
      }
      if (awardInfo.getDescriptionRead() != null && awardInfo.getDescriptionRead()
        && StringUtils.isNotBlank(submission.getDescription())) {
        value.append(submission.getDescription() + ", ");
      }
      if (value.length() > 1) {
        placeholders.put(DocumentPlaceholders.READ_VALUES.getValue(),
          value.substring(0, value.length() - 2));
      } else {
        placeholders.put(DocumentPlaceholders.READ_VALUES.getValue(),
          TemplateConstants.EMPTY_STRING);
      }
      if (awardInfo.getAvailableDate() != null) {
        placeholders.put(DocumentPlaceholders.FIRST_AWARD_DATE.getValue(),
          SubmissConverter.convertToSwissDate(awardInfo.getAvailableDate()));
      } else {
        placeholders.put(DocumentPlaceholders.FIRST_AWARD_DATE.getValue(),
          SubmissConverter.convertToSwissDate(new Timestamp(new Date().getTime())));
      }
      if (awardInfo.getReason() != null) {
        placeholders.put(DocumentPlaceholders.FIRST_AWARD_EXCL_REASON.getValue(),
          awardInfo.getReason());
      } else {
        placeholders.put(DocumentPlaceholders.FIRST_AWARD_EXCL_REASON.getValue(),
          TemplateConstants.EMPTY_STRING);
      }
    } else {
      setDefaultValues(submission, placeholders, value);
    }
  }

  /**
   * Replace offer criteria placeholders.
   *
   * @param placeholders the placeholders
   * @param offer the offer
   * @return the list
   */
  public List<LinkedHashMap<Map<String, String>, Map<String, String>>> replaceOfferCriteriaPlaceholders(
    Map<String, String> placeholders, OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceOfferCriteriaPlaceholders, Parameters: placeholders: {0}, "
        + "offer: {1}",
      new Object[]{placeholders, offer});

    List<LinkedHashMap<Map<String, String>, Map<String, String>>> tableList = new ArrayList<>();

    for (OfferCriterionDTO criterion : offer.getOfferCriteria()) {
      LinkedHashMap<Map<String, String>, Map<String, String>> subContent = new LinkedHashMap<>();
      Collections.sort(criterion.getCriterion().getSubcriterion(), ComparatorUtil.subcriteria);
      if (criterion.getCriterion().getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
        || criterion.getCriterion().getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
        || criterion.getCriterion().getCriterionType()
        .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
        StringBuilder criterionName = new StringBuilder();
        StringBuilder criterionPoints = new StringBuilder();
        criterionName.append(criterion.getCriterion().getCriterionText());
        if (criterion.getScore() != null) {
          criterionPoints.append(criterion.getScore().toString()).append(TemplateConstants.PUNKTE)
            .append(String.format(TemplateConstants.TWO_DECIMAL_PLACES,
              criterion.getCriterion().getWeighting()))
            .append("%)");
          Map<String, String> criterionContent = new HashMap<>();
          criterionContent.put(criterionName.toString(), criterionPoints.toString());

          Map<String, String> tableProperties = new HashMap<>();
          tableProperties.put(DocumentProperties.SPACING.getValue(), "60");

          tableProperties.put(DocumentProperties.WIDTH.getValue(), "4450");
          tableProperties.put(DocumentProperties.ITALIC.getValue(), Boolean.FALSE.toString());

          subContent.put(criterionContent, tableProperties);
        }
        for (SubcriterionDTO s : criterion.getCriterion().getSubcriterion()) {
          for (OfferSubcriterionDTO sub : offer.getOfferSubcriteria()) {
            StringBuilder subcriterionName = new StringBuilder();
            StringBuilder subcriterionPoints = new StringBuilder();
            if (s.getId().equals(sub.getSubcriterion().getId())) {
              subcriterionName.append(sub.getSubcriterion().getSubcriterionText());
              if (sub.getScore() != null) {
                subcriterionPoints.append(sub.getScore().toString())
                  .append(TemplateConstants.PUNKTE)
                  .append(String.format(TemplateConstants.TWO_DECIMAL_PLACES,
                    sub.getSubcriterion().getWeighting()))
                  .append("%)");
              }
              Map<String, String> criterionContent = new HashMap<>();
              criterionContent.put(subcriterionName.toString(), subcriterionPoints.toString());

              Map<String, String> tableProperties = new HashMap<>();
              tableProperties.put(DocumentProperties.SPACING.getValue(), "60");

              tableProperties.put(DocumentProperties.WIDTH.getValue(), "4450");
              tableProperties.put(DocumentProperties.ITALIC.getValue(), Boolean.TRUE.toString());
              if (getTenantName().equals(DocumentProperties.TENANT_EWB.getValue())) {
                tableProperties.put(DocumentProperties.FONT_SIZE.getValue(), "18");
              } else {
                tableProperties.put(DocumentProperties.FONT_SIZE.getValue(), "16");
              }

              tableProperties.put(DocumentProperties.FONT_COLOR.getValue(),
                TemplateConstants.GRAY_COLOR);
              subContent.put(criterionContent, tableProperties);
            }
          }
        }
      }
      tableList.add(subContent);
    }
    return tableList;
  }

  /**
   * Replace joint ventures placeholder.
   *
   * @param offer the offer
   * @param placeholders the placeholders
   */
  public void replaceJointVenturesPlaceholder(SubmittentOfferDTO offer,
    Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceJointVenturesPlaceholder, Parameters: offer: {0}, "
        + "placeholders: {1}",
      new Object[]{offer, placeholders});

    StringBuilder jointVentures = new StringBuilder();
    boolean first = true;
    for (CompanyDTO jointVenture : offer.getSubmittent().getJointVentures()) {
      if (!first) {
        jointVentures.append(jointVenture.getCompanyName());
        jointVentures.append(", ");
      } else {
        jointVentures.append("(").append(jointVenture.getCompanyName());
        jointVentures.append(", ");
        first = false;
      }
    }
    if (jointVentures.length() > 1) {
      jointVentures.replace(jointVentures.lastIndexOf(","), jointVentures.lastIndexOf(",") + 1,
        ")");
      placeholders.put(DocumentPlaceholders.F_COMPANY_JOINT.getValue(), jointVentures.toString());
    } else {
      placeholders.put(DocumentPlaceholders.F_COMPANY_JOINT.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Replace award info placeholders.
   *
   * @param submissionId the submission id
   * @param submission the submission
   * @param placeholders the placeholders
   */
  public void replaceAwardInfoPlaceholders(String submissionId, SubmissionDTO submission,
    Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceAwardInfoPlaceholders, Parameters: submissionId: {0}, "
        + "submission: {1}, placeholders: {2}",
      new Object[]{submissionId, submission, placeholders});

    AwardInfoDTO awardInfo = submissionCloseService.getAwardInfo(submissionId);
    StringBuilder value = new StringBuilder();
    if (awardInfo != null && awardInfo.getAvailableDate() != null) {
      if (awardInfo.getObjectNameRead() != null && awardInfo.getObjectNameRead()) {
        value.append(submission.getProject().getObjectName().getValue1());
        value.append(", ");
      }
      if (awardInfo.getProjectNameRead() != null && awardInfo.getProjectNameRead()) {
        value.append(submission.getProject().getProjectName() + ", ");
      }
      if (awardInfo.getWorkingClassRead() != null && awardInfo.getWorkingClassRead()) {
        value.append(submission.getWorkType().getValue1() + " "
          + submission.getWorkType().getValue2() + ", ");
      }
      if (awardInfo.getDescriptionRead() != null && awardInfo.getDescriptionRead()
        && StringUtils.isNotBlank(submission.getDescription())) {
        value.append(submission.getDescription() + ", ");
      }
      if (value.length() > 1) {
        placeholders.put(DocumentPlaceholders.READ_VALUES.getValue(),
          value.substring(0, value.length() - 2));
      } else {
        placeholders.put(DocumentPlaceholders.READ_VALUES.getValue(),
          TemplateConstants.EMPTY_STRING);
      }
      if (awardInfo.getAvailableDate() != null) {
        placeholders.put(DocumentPlaceholders.AWARD_DATE.getValue(),
          SubmissConverter.convertToSwissDate(awardInfo.getAvailableDate()));
      } else {
        placeholders.put(DocumentPlaceholders.AWARD_DATE.getValue(),
          SubmissConverter.convertToSwissDate(new Timestamp(new Date().getTime())));
      }
    } else {
      setDefaultValues(submission, placeholders, value);
    }
    if (submission.getProcess().equals(Process.SELECTIVE)) {
      AwardInfoFirstLevelDTO awardInfoFirstLevel =
        submissionCloseService.getAwardInfoFirstLevel(submissionId);
      if (awardInfoFirstLevel != null) {
        if (awardInfoFirstLevel.getAvailableDate() != null) {
          placeholders.put(DocumentPlaceholders.FIRST_AWARD_DATE.getValue(),
            SubmissConverter.convertToSwissDate(awardInfoFirstLevel.getAvailableDate()));
        } else {
          placeholders.put(DocumentPlaceholders.FIRST_AWARD_DATE.getValue(),
            TemplateConstants.EMPTY_STRING);
        }
      } else {
        setDefaultValues(submission, placeholders, value);
      }
    }
  }

  /**
   * Sets the default values.
   *
   * @param submission the submission
   * @param placeholders the placeholders
   * @param value the value
   */
  private void setDefaultValues(SubmissionDTO submission, Map<String, String> placeholders,
    StringBuilder value) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDefaultValues, Parameters: submission: {0}, "
        + "palceholders: {1}, value: {2}",
      new Object[]{submission, placeholders, value});

    value.append(submission.getProject().getObjectName().getValue1());
    value.append(", ").append(submission.getProject().getProjectName() + ", ").append(
      submission.getWorkType().getValue1() + " " + submission.getWorkType().getValue2() + ", ");
    if (submission.getDescription() != null
      && StringUtils.isNotBlank(submission.getDescription())) {
      value.append(submission.getDescription() + ", ");
    }
    if (value.length() > 1) {
      placeholders.put(DocumentPlaceholders.READ_VALUES.getValue(),
        value.substring(0, value.length() - 2));
    }
    placeholders.put(DocumentPlaceholders.AWARD_DATE.getValue(),
      SubmissConverter.convertToSwissDate(new Timestamp(new Date().getTime())));
    placeholders.put(DocumentPlaceholders.FIRST_AWARD_DATE.getValue(),
      SubmissConverter.convertToSwissDate(new Timestamp(new Date().getTime())));
    placeholders.put(DocumentPlaceholders.FIRST_AWARD_EXCL_REASON.getValue(),
      TemplateConstants.EMPTY_STRING);
  }

  /**
   * This function is used to create 2 lists (the header and the content) for the document table
   * that will be generated (Arbeitsagttungen Liste).
   *
   * @param headerWorkTypes the header work types
   * @param contentWorkTypes the content work types
   * @param workTypes the work types
   */
  public void createWorkTypesTable(List<String> headerWorkTypes,
    List<LinkedHashMap<Map<String, String>, String>> contentWorkTypes,
    List<MasterListValueHistoryDTO> workTypes) {

    LOGGER.log(Level.CONFIG,
      "Executing method createWorkTypesTable, Parameters: headerWorkTypes: {0}, "
        + "contentWorkTypes: {1}, workType: {2}",
      new Object[]{headerWorkTypes, contentWorkTypes, workTypes});

    /** Create a list of Strings for the header of the table */
    headerWorkTypes.add("Nummer");
    headerWorkTypes.add("Bezeichnung");
    Map<String, String> cellProperties = new HashMap<>();
    cellProperties.put(DocumentProperties.VALUE.getValue(), "1");
    cellProperties.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.TRUE.toString());
    cellProperties.put(DocumentProperties.SPACING.getValue(),
      DocumentProperties.NUMBER_60.getValue());
    cellProperties.put(DocumentProperties.ALIGN_RIGHT.getValue(), Boolean.TRUE.toString());
    cellProperties.put(DocumentProperties.RIGHT_MARGIN.getValue(), "800");
    Map<String, String> cellProperties2 = new HashMap<>();
    cellProperties2.put(DocumentProperties.VALUE.getValue(), "2");
    cellProperties2.put(DocumentProperties.BOLD_CONTENT.getValue(), Boolean.FALSE.toString());
    /** Loop for the worktypes and add it to LinkedHashMap (position column: according integer) */
    for (MasterListValueHistoryDTO workType : workTypes) {

      LinkedHashMap<Map<String, String>, String> subContent = new LinkedHashMap<>();
      subContent.put(cellProperties, workType.getValue1());
      subContent.put(cellProperties2, workType.getValue2());

      contentWorkTypes.add(subContent);
    }
  }

  /**
   * This function replace the submission placeholders.
   *
   * @param placeholders the placeholders
   * @param submission the submission
   * @param offers the offers
   */
  public void replaceSubmissionPlaceholders(Map<String, String> placeholders,
    SubmissionDTO submission, List<SubmittentOfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method replaceSubmissionPlaceholders, Parameters: placeholders: {0}, "
        + "submission: {1}, offers: {2}",
      new Object[]{placeholders, submission, offers});

    if (offers != null) {
      StringBuilder awardedName = new StringBuilder();
      StringBuilder awardedAmount = new StringBuilder();
      StringBuilder awardedFreeText = new StringBuilder();
      StringBuilder awardedOperCost = new StringBuilder();
      for (SubmittentOfferDTO offer : offers) {
        placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY.getValue(),
          offer.getSubmittent().getCompanyId().getCompanyName());
        placeholders.put(DocumentPlaceholders.F_MAIN_COMPANY.getValue(),
          offer.getSubmittent().getCompanyId().getCompanyName());
        placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY_LOCATION.getValue(),
          offer.getSubmittent().getCompanyId().getLocation());

        if (offer.getOffer().getIsAwarded() != null && offer.getOffer().getIsAwarded()) {
          awardedName.append(offer.getSubmittent().getSubmittentNameArgeSub());
          if (offer.getOffer().getAmount() != null) {
            awardedAmount.append(
              SubmissConverter.convertToCHFCurrency(offer.getOffer().getAmount()));
          }

          if (offer.getOffer().getAwardRecipientFreeTextField() != null
            && offer.getOffer().getAwardRecipientFreeTextField().length() > 1) {
            awardedFreeText.append(offer.getOffer().getAwardRecipientFreeTextField());
          }

          if (offer.getOffer().getOperatingCostsAmount() != null) {
            awardedOperCost.append(SubmissConverter
              .convertToCHFCurrency(offer.getOffer().getOperatingCostsAmount()));
            if (offer.getOffer().getOperatingCostNotes() != null
              && offer.getOffer().getOperatingCostNotes().length() > 1) {
              awardedOperCost.append(", ").append(offer.getOffer().getOperatingCostNotes());
            }
          }
        }
      }
      if (awardedName.length() > 1) {
        placeholders.put(DocumentPlaceholders.BA_AWARD.getValue(),
          awardedName.substring(0, awardedName.length()));
      } else {
        placeholders.put(DocumentPlaceholders.BA_AWARD.getValue(), TemplateConstants.EMPTY_STRING);
      }
      if (awardedAmount.length() > 1) {
        placeholders.put("ba_award_amount", awardedAmount.substring(0, awardedAmount.length()));
      } else {
        placeholders.put("ba_award_amount", TemplateConstants.EMPTY_STRING);
      }
      if (awardedFreeText.length() > 1) {
        placeholders
          .put("ba_award_text", awardedFreeText.substring(0, awardedFreeText.length()));
      } else {
        placeholders.put("ba_award_text", TemplateConstants.EMPTY_STRING);
      }
      if (awardedOperCost.length() > 1) {
        placeholders
          .put("ba_award_oper", awardedOperCost.substring(0, awardedOperCost.length()));
      } else {
        placeholders.put("ba_award_oper", TemplateConstants.EMPTY_STRING);
      }
    }
    placeholders.put(DocumentPlaceholders.BA_DIR_DEP.getValue(),
      submission.getProject().getDepartment().getDirectorate().getShortName() + " "
        + submission.getProject().getDepartment().getShortName());

    ruleService.replaceSubmissionPlaceholders(submission, placeholders);
  }

  /**
   * Sets the reference placehoders.
   *
   * @param placeholders the placeholders
   */
  public void setReferencePlacehoders(Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method setReferencePlacehoders, Parameters: placeholders: {0}",
      placeholders);

    if (getUser().getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()) != null
      && getUser().getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData() != null) {
      DepartmentHistoryDTO department = sDDepartmentService.getDepartmentHistByDepartmentId(
        getUser().getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData());

      setUserAttributesPlaceholders(department, department.getDirectorate(), placeholders, null);
    } else {
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_ADDRESS.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_POST.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_TEL.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_FAX.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_EMAIL.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_WEBSITE.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_NAME.getValue(),
        TemplateConstants.EMPTY_STRING);

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_NAME.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_ADDRESS.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_POST.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_TEL.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_FAX.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_EMAIL.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_WEBSITE.getValue(),
        TemplateConstants.EMPTY_STRING);

      placeholders.put(DocumentPlaceholders.R_DEPARTMENT_SHORT.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.R_DIRECTORATE_SHORT.getValue(),
        TemplateConstants.EMPTY_STRING);

      if (getUser().getAttributeData(USER_ATTRIBUTES.FUNCTION.getValue()) != null) {
        placeholders.put(DocumentPlaceholders.REFERENCE_PERSON.getValue(),
          getUser().getAttributeData(USER_ATTRIBUTES.FUNCTION.getValue()));
      } else {
        placeholders.put(DocumentPlaceholders.REFERENCE_PERSON.getValue(),
          TemplateConstants.EMPTY_STRING);
      }

      placeholders.put(DocumentPlaceholders.R_PERSON_NAME.getValue(),
        getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()) + " "
          + getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()));

      placeholders.put(DocumentPlaceholders.R_PERSON_FIRSTNAME.getValue(),
        getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()) + ", "
          + getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()));

      placeholders.put(DocumentPlaceholders.R_INITIALS.getValue(),
        getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()).substring(0, 2)
          + TemplateConstants.EMPTY_STRING
          + getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()).substring(0, 1));
    }
  }

  /**
   * Sets the company name or arge.
   *
   * @param offer the offer
   * @param placeholders the placeholders
   * @param placeholderName the placeholder name
   */
  public void setCompanyNameOrArge(OfferDTO offer, Map<String, String> placeholders,
    String placeholderName) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCompanyNameOrArge, Parameters: offer: {0}, "
        + "placeholders: {1}, placeholderName: {2}",
      new Object[]{offer, placeholders, placeholderName});

    StringBuilder companyName = new StringBuilder();
    if (offer.getSubmittent().getJointVentures() == null
      || offer.getSubmittent().getJointVentures().isEmpty()) {
      if (placeholderName.equals(DocumentPlaceholders.AWARDED_COMPANY_NAME_OR_ARGE.getValue())) {
        placeholders.put(placeholderName,
          TemplateConstants.COMPANY_DE + offer.getSubmittent().getCompanyId().getCompanyName()
            + ", " + offer.getSubmittent().getCompanyId().getLocation());
      } else {
        placeholders.put(placeholderName,
          TemplateConstants.COMPANY_DE + offer.getSubmittent().getCompanyId().getCompanyName());
      }
    } else {
      companyName.append(TemplateConstants.ARGE)
        .append(offer.getSubmittent().getCompanyId().getCompanyName()).append(", ");
      for (CompanyDTO jointVenture : offer.getSubmittent().getJointVentures()) {
        companyName.append(jointVenture.getCompanyName()).append(", ");
      }
      if (placeholderName.equals(DocumentPlaceholders.AWARDED_COMPANY_NAME_OR_ARGE.getValue())) {
        placeholders.put(placeholderName,
          companyName.append(offer.getSubmittent().getCompanyId().getLocation()).toString());
      } else {
        if (companyName.length() > 1) {
          placeholders.put(placeholderName, companyName.substring(0, companyName.length() - 2));
        }
      }
    }
  }

  /**
   * Getting the Template of the report.
   *
   * @param filename the filename
   * @return the template by attribute name
   */
  public InputStream getTemplateByAttributeName(String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTemplateByAttributeName, Parameters: filename: {0}",
      filename);

    InputStream inputStream = null;

    HashMap<String, String> attributesMap = new HashMap<>();
    List<NodeDTO> templateList = null;
    attributesMap.put(LookupValues.REPORT_TEMPLATE_ATTRIBUTE_NAME, filename);
    templateList =
      documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
    if (!templateList.isEmpty()) {
      inputStream =
        new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));
    }
    return inputStream;
  }

  /**
   * Sets the company not provided proof status.
   *
   * @param company the company
   * @return the string builder
   */
  public StringBuilder setCompanyNotProvidedProofStatus(CompanyDTO company) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCompanyNotProvidedProofStatus, Parameters: company: {0}",
      company);

    StringBuilder companyProofStatus = new StringBuilder();
    companyProofStatus.append(
      lexiconService.getTranslation(ProofStatus.fromValue(company.getProofStatus()).toString(),
        TemplateConstants.TRANSLATION_FORMAT));
    if (companyProofStatus.toString().equals(TemplateConstants.STATUS_WITH_FABE)
      && getTenantName().equals(DocumentProperties.TENANT_EWB.getValue())) {
      companyProofStatus.setLength(0);
      companyProofStatus.append(TemplateConstants.STATUS_WITH_FABE);
    }
    if (company.getProofStatusFabe() != null && getTenantName()
      .equals(DocumentProperties.TENANT_EWB.getValue())
      && company.getProofStatusFabe() == 3) {
      companyProofStatus.setLength(0);
      companyProofStatus.append(TemplateConstants.PROOF_STATUS_NOT_ACTIVE);
    }
    return companyProofStatus;
  }

  /**
   * Method that returns the tenant name of the user.
   *
   * @return the tenant name
   */
  public String getTenantName() {

    LOGGER.log(Level.CONFIG, "Executing method getTenantName");

    String tenantName = StringUtils.EMPTY;
    String tenantId = getUser().getAttribute("TENANT").getData();
    if (!tenantId.isEmpty()) {
      tenantName = sDTenantService.getTenantById(tenantId).getName();
    }
    return tenantName;
  }

  /**
   * Sets the cancel deadline.
   *
   * @param placeholders the placeholders
   * @param offer the offer
   * @param level the level
   */
  public void setCancelDeadline(Map<String, String> placeholders, OfferDTO offer, Integer level) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCancelDeadline, Parameters: placeholders: {0}, "
        + "offer: {1}, level: {2}",
      new Object[]{placeholders, offer, level});

    if (legalHearingService.retrieveLatestSubmittentExclusionDeadline(offer.getSubmittent().getId(),
      level) != null) {
      placeholders.put(DocumentPlaceholders.R_CANCEL_DEADLINE.getValue(),
        SubmissConverter.convertToSwissDate(legalHearingService
          .retrieveLatestSubmittentExclusionDeadline(offer.getSubmittent().getId(), level)));
    } else {
      placeholders.put(DocumentPlaceholders.R_CANCEL_DEADLINE.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Sets the award evaluation document parameters.
   *
   * @param documentParameters the document parameters
   * @param submissionDTO the submission DTO
   */
  public void setAwardEvaluationDocumentParameters(Map<String, Object> documentParameters,
    SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method setAwardEvaluationDocumentParameters, Parameters: "
        + "documentParameters: {0}, submissionDTO: {1}",
      new Object[]{documentParameters, submissionDTO});

    StringBuilder readValues = new StringBuilder();
    readValues.append(submissionDTO.getProject().getObjectName().getValue1());
    readValues.append(", ");
    readValues.append(submissionDTO.getProject().getProjectName());
    readValues.append(", ");
    readValues.append(submissionDTO.getWorkType().getValue1());
    readValues.append(", ");
    if (submissionDTO.getWorkType().getValue2() != null) {
      readValues.append(submissionDTO.getWorkType().getValue2());
      readValues.append(", ");
    }
    readValues.append(submissionDTO.getDescription());
    readValues.append(" (");
    readValues.append(submissionDTO.getCurrentProcess());
    readValues.append(")");

    documentParameters.put("ReadValues", readValues.toString());
    documentParameters.put("AddedAwardRecipients", submissionDTO.getAddedAwardRecipients());
    documentParameters.put("AwardMaxGrade", submissionDTO.getAwardMaxGrade());
    documentParameters.put("AwardMinGrade", submissionDTO.getAwardMinGrade());
    documentParameters.put("EvaluationThrough", submissionDTO.getEvaluationThrough());
    documentParameters.put("OperatingCostFormula",
      submissionDTO.getOperatingCostFormula().getValue1());
    documentParameters.put("PriceFormula", submissionDTO.getPriceFormula().getValue1());
  }

  /**
   * Sets the submittenten list etiketten parameters.
   *
   * @param documentParameters the document parameters
   * @param submission the submission
   */
  public void setSubmittentenListEtikettenParameters(Map<String, Object> documentParameters,
    SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSubmittentenListEtikettenParameters, Parameters: "
        + "documentParameters: {0}, submission: {1}",
      new Object[]{documentParameters, submission});

    StringBuilder objectInfo = new StringBuilder();
    objectInfo.append(submission.getProject().getObjectName().getValue1());
    if (submission.getProject().getObjectName().getValue2() != null) {
      objectInfo.append(", ");
      objectInfo.append(submission.getProject().getObjectName().getValue2());
    }

    StringBuilder workTypeInfo = new StringBuilder();
    workTypeInfo.append(submission.getWorkType().getValue1());
    if (submission.getWorkType().getValue2() != null) {
      workTypeInfo.append(" ");
      workTypeInfo.append(submission.getWorkType().getValue2());
    }

    documentParameters.put("Object", objectInfo.toString());
    documentParameters.put("Project", submission.getProject().getProjectName());
    documentParameters.put("Worktype", workTypeInfo.toString());
    documentParameters.put("ProjectManager", submission.getPmDepartmentName());
    documentParameters.put("Description", submission.getDescription());
    documentParameters.put("Procedure", submission.getProcess().getValue());
    documentParameters.put("CostEstimate", setCurrencyFormat(submission.getCostEstimate()));
  }

  /**
   * Gets the submittent arge sub contractor names.
   *
   * @param submittentOffer the submittent offer
   * @return the submittent arge sub contractor names
   */
  // Method that returns formated arge parteners
  public String getSubmittentArgeSubContractorNames(SubmittentOfferDTO submittentOffer) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmittentArgeSubContractorNames, Parameters: submittentOffer{0}",
      submittentOffer);

    StringBuilder companies = new StringBuilder();
    companies.append(submittentOffer.getSubmittent().getCompanyId().getCompanyName());
    if (submittentOffer.getSubmittent().getJointVentures() != null
      && !submittentOffer.getSubmittent().getJointVentures().isEmpty()) {
      companies.append(" / ");
      ArrayList<String> argePartnersList = new ArrayList<>();
      for (CompanyDTO arge : submittentOffer.getSubmittent().getJointVentures()) {
        argePartnersList.add(arge.getCompanyName());
      }
      companies.append(StringUtils.join(argePartnersList, " / "));
    }
    if (submittentOffer.getSubmittent().getSubcontractors() != null
      && !submittentOffer.getSubmittent().getSubcontractors().isEmpty()) {
      ArrayList<String> subcontractorsList = new ArrayList<>();
      companies.append(SUB_U);
      for (CompanyDTO subcostractor : submittentOffer.getSubmittent().getSubcontractors()) {
        subcontractorsList.add(subcostractor.getCompanyName());
      }
      companies.append(StringUtils.join(subcontractorsList, " / "));
      companies.append(")");
    }
    return companies.toString();
  }

  /**
   * Gets the all companies over view.
   *
   * @param submittent the submittent
   * @param submissionOverview the submission overview
   * @return the all companies over view
   */
  public Set<CompanyDTO> getAllCompaniesOverView(SubmittentDTO submittent,
    SubmissionOverviewDTO submissionOverview) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAllCompaniesOverView, Parameters: submittent: {0}, "
        + "submissionOverview: {1}",
      new Object[]{submittent, submissionOverview});

    Set<CompanyDTO> allCompanies = new HashSet<>();
    if (submittent != null) {
      Set<SubmissionCompanyOverviewDTO> subCompanyOverviewList = new LinkedHashSet<>();
      allCompanies.add(submittent.getCompanyId());
      if (submittent.getJointVentures() != null && !submittent.getJointVentures().isEmpty()
        || submittent.getSubcontractors() != null && !submittent.getSubcontractors().isEmpty()) {
        setCompanyOverviewInfo(submittent.getCompanyId(), submissionOverview,
          subCompanyOverviewList, "Firma");
        submissionOverview.setPartnersOverview(subCompanyOverviewList);
      }
      if (submittent.getJointVentures() != null && !submittent.getJointVentures().isEmpty()) {
        for (CompanyDTO arge : submittent.getJointVentures()) {
          allCompanies.add(arge);
          setCompanyOverviewInfo(arge, submissionOverview, subCompanyOverviewList, "ARGE Partner");
          submissionOverview.setPartnersOverview(subCompanyOverviewList);
        }
      }
      if (submittent.getSubcontractors() != null && !submittent.getSubcontractors().isEmpty()) {
        for (CompanyDTO subContractor : submittent.getSubcontractors()) {
          allCompanies.add(subContractor);
          setCompanyOverviewInfo(subContractor, submissionOverview, subCompanyOverviewList,
            "Sub.Unternehmen");
          submissionOverview.setPartnersOverview(subCompanyOverviewList);
        }
      }
    }
    return allCompanies;
  }

  /**
   * Sets the company overview info.
   *
   * @param company the company
   * @param submissionOverview the submission overview
   * @param subCompanyOverviewList the sub company overview list
   * @param companyDesc the company desc
   */
  private void setCompanyOverviewInfo(CompanyDTO company, SubmissionOverviewDTO submissionOverview,
    Set<SubmissionCompanyOverviewDTO> subCompanyOverviewList, String companyDesc) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCompanyOverviewInfo, Parameters: company: {1}, "
        + "submissionOverview: {1}, subCompanyOverviewList: {2}, companyDesc: {3}",
      new Object[]{company, submissionOverview, subCompanyOverviewList, companyDesc});

    SubmissionCompanyOverviewDTO subCompanyOverview = new SubmissionCompanyOverviewDTO();
    StringBuilder sbRemarks = new StringBuilder();
    subCompanyOverview.setName(company.getCompanyName());
    subCompanyOverview.setLocation(company.getLocation());

    getCompanyRemarks(company, sbRemarks);

    subCompanyOverview.setCompanyRemarks(sbRemarks.toString());
    List<CompanyProofDTO> companyProofs = companyService.getProofByCompanyId(company.getId());
    Collections.reverse(companyProofs);
    StringBuilder sbproofStatuses = new StringBuilder();
    List<String> proofs = getCompanyListOfProofs(companyProofs, sbproofStatuses);
    subCompanyOverview.setAllProofStatuses(StringUtils.join(proofs, " / "));
    subCompanyOverview.setCompanyPartnershipDesc(companyDesc);
    if (company.getIsProofProvided() != null && company.getIsProofProvided()) {
      subCompanyOverview.setProofStatus("Alle Nachweise vorhanden");
    } else {
      subCompanyOverview.setProofStatus("nicht aktuell");
    }
    submissionOverview.setHasPartners(true);
    subCompanyOverviewList.add(subCompanyOverview);
  }

  /**
   * Gets the company list of proofs.
   *
   * @param companyProofs the company proofs
   * @param sbproofStatuses the sbproof statuses
   * @return the company list of proofs
   */
  public List<String> getCompanyListOfProofs(List<CompanyProofDTO> companyProofs,
    StringBuilder sbproofStatuses) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyListOfProofs, Parameters: companyProofs: {0}, "
        + "sbproofStatuses: {1}",
      new Object[]{companyProofs, sbproofStatuses});

    List<String> proofs = new ArrayList<>();
    SimpleDateFormat df = new SimpleDateFormat(TemplateConstants.DATE_FORMAT);
    for (CompanyProofDTO companyProof : companyProofs) {
      sbproofStatuses.append(companyProof.getProof().getProofName()).append(": ");
      if (companyProof.getProof().getRequired() != null && companyProof.getRequired()) {
        if (companyProof.getProofDate() != null) {
          sbproofStatuses.append(df.format(companyProof.getProofDate()));
        } else {
          sbproofStatuses.append(TemplateConstants.EMPTY_STRING);
        }
      } else {
        sbproofStatuses.append(TemplateConstants.NOT_MANDATORY);
      }
      proofs.add(sbproofStatuses.toString());
      sbproofStatuses.setLength(0);
    }
    return proofs;
  }

  /**
   * Gets the company remarks.
   *
   * @param company the company
   * @param sbRemarks the sb remarks
   * @return the company remarks
   */
  public void getCompanyRemarks(CompanyDTO company, StringBuilder sbRemarks) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyRemarks, Parameters: company: {0}, sbRemarks: {1}",
      new Object[]{company, sbRemarks});

    if (company.getApprenticeFactor() != null) {
      sbRemarks.append("LF ").append(company.getApprenticeFactor());
    }

    if (company.getFiftyPlusFactor() != null) {
      if (company.getApprenticeFactor() != null) {
        sbRemarks.append(" / ");
      }
      sbRemarks.append("F50+ ").append(company.getFiftyPlusFactor());
    }

    if (company.getApprenticeFactor() != null || company.getFiftyPlusFactor() != null) {
      if (company.getTlp() != null && company.getTlp()) {
        sbRemarks.append(" / TLP ").append("Ja");
      } else {
        sbRemarks.append(" / TLP ").append("Nein");
      }
    } else {
      if (company.getTlp() != null && company.getTlp()) {
        sbRemarks.append("TLP ").append("Ja");
      } else {
        sbRemarks.append("TLP ").append("Nein");
      }
    }

    if (company.getApprenticeFactor() != null || company.getTlp() != null
      || company.getFiftyPlusFactor() != null) {
      if (company.getNotes() != null && !company.getNotes().isEmpty()) {
        sbRemarks.append(" / ").append(company.getNotes());
      }
    } else {
      if (company.getNotes() != null && !company.getNotes().isEmpty()) {
        sbRemarks.append(company.getNotes());
      }
    }

    if (company.getNoteAdmin() != null && !company.getNoteAdmin().isEmpty()) {
      if (company.getNotes() != null && !company.getNotes().isEmpty()) {
        sbRemarks.append(", ").append(company.getNoteAdmin());
      } else {
        if (company.getApprenticeFactor() != null || company.getTlp() != null) {
          sbRemarks.append(" / ").append(company.getNoteAdmin());
        } else {
          sbRemarks.append(company.getNoteAdmin());
        }
      }
    }
  }

  /**
   * Tender has empty offers.
   *
   * @param submission the submission
   * @param shortCode the short code
   * @return true, if successful
   */
  // Method that checks if all offers in submission are empty
  public boolean tenderHasEmptyOffers(SubmissionDTO submission, String shortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method tenderHasEmptyOffers, Parameters: submission: {0}, shortCode: {1}",
      new Object[]{submission, shortCode});

    List<OfferDTO> submissionOffers = null;
    if (submission.getProcess() == Process.SELECTIVE
      && Template.BEWERBER_UBERSICHT.equals(shortCode)) {
      submissionOffers = offerService.getApplicationsBySubmission(submission.getId());
    } else {
      submissionOffers = offerService.getOffersBySubmission(submission.getId());
    }
    if (submissionOffers != null && !submissionOffers.isEmpty()) {
      for (OfferDTO offer : submissionOffers) {
        if (!offer.getIsEmptyOffer() && (
          (offer.getAmount() != null && offer.getAmount().compareTo(BigDecimal.ZERO) != 0)
            || (offer.getOperatingCostsAmount() != null
            && offer.getOperatingCostsAmount().compareTo(BigDecimal.ZERO) != 0)
            || (offer.getAncilliaryAmountGross() != null
            && offer.getAncilliaryAmountGross() != 0))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Gets the offerrtoffnungsprotokoll title.
   *
   * @param placeholders the placeholders
   * @param shortCode the short code
   * @return the offerrtoffnungsprotokoll title
   */
  // Returns template's title
  public void getOfferrtoffnungsprotokollTitle(Map<String, String> placeholders, String shortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferrtoffnungsprotokollTitle, Parameters: placeholders: {0}, "
        + "shortCode: {1}",
      new Object[]{placeholders, shortCode});

    switch (shortCode) {
      case Template.OFFERRTOFFNUNGSPROTOKOLL:
        placeholders.put(DocumentPlaceholders.DOCUMENT_TITLE.getValue(),
          TemplateConstants.OFFERRTOFFNUNGSPROTOKOLL_TITLE);
        break;
      case Template.A_OFFERRTOFFNUNGSPROTOKOLL:
        placeholders.put(DocumentPlaceholders.DOCUMENT_TITLE.getValue(),
          TemplateConstants.ANON_OFFERRTOFFNUNGSPROTOKOLL_TITLE);
        break;
      case Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW:
        placeholders.put(DocumentPlaceholders.DOCUMENT_TITLE.getValue(),
          TemplateConstants.OFFERRTOFFNUNGSPROTOKOLL_DL_WW_TITLE);
        break;
      case Template.A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW:
        placeholders.put(DocumentPlaceholders.DOCUMENT_TITLE.getValue(),
          TemplateConstants.ANON_OFFERRTOFFNUNGSPROTOKOLL_DL_WW_TITLE);
        break;
      default:
        break;
    }
  }

  /**
   * Calculates the offer's price percentage in comparison with the lowest offer.
   *
   * @param submittentOffer the submittent offer
   * @param offers the offers
   * @return the string
   */
  public String calculateOfferPricePercentage(SubmittentOfferDTO submittentOffer,
    List<SubmittentOfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateOfferPricePercentage, Parameters: submittentOffer: {0}, "
        + "offers: {1}",
      new Object[]{submittentOffer, offers});

    List<BigDecimal> list = new ArrayList<>();
    List<BigDecimal> nonZerolist = new ArrayList<>();
    boolean hasEmptyOffers = false;
    for (int i = 0; i < offers.size(); i++) {
      if (offers.get(i).getOffer().getAmount() == null
        || offers.get(i).getOffer().getAmount().compareTo(BigDecimal.ZERO) == 0) {
        list.add(new BigDecimal(Double.valueOf(0)));
        hasEmptyOffers = true;
      } else {
        list.add(i, offers.get(i).getOffer().getAmount());
        nonZerolist.add(offers.get(i).getOffer().getAmount());
      }
    }

    int minIndex;
    Double lowestOffer = null;
    Double currentOffer = null;
    if (hasEmptyOffers) {
      // check if all values of list are zero
      if (Collections.frequency(list, new BigDecimal(Double.valueOf(0))) == list.size()) {
        lowestOffer = Double.valueOf(0);
      } else {
        minIndex = nonZerolist.indexOf(Collections.min(nonZerolist));
        lowestOffer = nonZerolist.get(minIndex).doubleValue();
      }
    } else {
      minIndex = list.indexOf(Collections.min(list));
      lowestOffer = list.get(minIndex).doubleValue();
    }

    if (submittentOffer.getOffer().getAmount() != null) {
      currentOffer = submittentOffer.getOffer().getAmount().doubleValue();
    } else {
      currentOffer = Double.valueOf(0);
    }

    // The formula for calculation will be:
    // (currentOffer / lowestOffer) * 100
    Double offer = null;

    if (currentOffer.equals(lowestOffer) && !lowestOffer.equals(Double.valueOf(0))) {
      return new BigDecimal(Double.valueOf(100)).toString();
    }

    DecimalFormat df = new DecimalFormat("#.##");
    if (lowestOffer != 0) {
      offer = (currentOffer / lowestOffer) * 100;
    }
    return offer != null ? df.format(offer) : "0.00";
  }

  /**
   * Set Swiss format for offer amount without symbol.
   *
   * @param offer the offer
   * @return the string
   */
  public String setCurrencyFormat(BigDecimal offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCurrencyFormat, Parameters: offer: {0}",
      offer);

    String formattedOffer = null;
    if (offer != null) {
      Locale locale = new Locale("de", "CH");
      DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
      DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
      symbols.setCurrencySymbol(TemplateConstants.EMPTY_STRING);
      formatter.setDecimalFormatSymbols(symbols);
      formattedOffer = formatter.format(offer);
    }
    return formattedOffer;
  }

  /**
   * Read templates values.
   *
   * @param submission the submission
   * @return the string builder
   */
  public StringBuilder readTemplatesValues(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method readTemplatesValues, Parameters: submission: {0}",
      submission);

    StringBuilder readValues = new StringBuilder();
    readValues.append(submission.getProject().getObjectName().getValue1());
    readValues.append(", ");
    readValues.append(submission.getProject().getProjectName());
    readValues.append(", ");
    readValues.append(submission.getWorkType().getValue1());
    readValues.append(" ");
    if (submission.getWorkType().getValue2() != null) {
      readValues.append(submission.getWorkType().getValue2());
      readValues.append(", ");
    }
    readValues.append(submission.getDescription());
    if (submission.getProcess() != null) {
      readValues.append(" (");
      readValues.append(getTranslatedProcess(submission.getProcess()));
      readValues.append(")");
    }
    return readValues;
  }

  /**
   * Gets the translated process.
   *
   * @param process the process
   * @return the translated process
   */
  private String getTranslatedProcess(Process process) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTranslatedProcess, Parameters: process: {0}",
      process);

    String processName;
    switch (process.getValue()) {
      case "SelectiveTender":
        processName = "Selektiv";
        break;
      case "OpenTender":
        processName = "Offen";
        break;
      case "InvitedTender":
        processName = "Einladungsverfahren";
        break;
      case "NegotiatedTender":
        processName = "Freihändig";
        break;
      case "NegotiatedCompetitionTender":
        processName = "Freihändig mit Konkurrenz";
        break;
      default:
        processName = StringUtils.EMPTY;
        break;
    }
    return processName;
  }

  /**
   * Remove the first 4 characters.
   *
   * @param placeholders the placeholders
   */
  public void removeCurrencySymbol(Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method removeCurrencySymbol, Parameters: placeholders: {0}",
      placeholders);

    placeholders.put(DocumentPlaceholders.O_GROSS_AMOUNT_CORRECTED.getValue(),
      placeholders.get(DocumentPlaceholders.O_GROSS_AMOUNT_CORRECTED.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_DISCOUNT.getValue(),
      placeholders.get(DocumentPlaceholders.O_DISCOUNT.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_DISCOUNT_TOTAL.getValue(),
      placeholders.get(DocumentPlaceholders.O_DISCOUNT_TOTAL.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_DISCOUNT2.getValue(),
      placeholders.get(DocumentPlaceholders.O_DISCOUNT2.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_DISCOUNT2_TOTAL.getValue(),
      placeholders.get(DocumentPlaceholders.O_DISCOUNT2_TOTAL.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_VAT_AMOUNT.getValue(),
      placeholders.get(DocumentPlaceholders.O_VAT_AMOUNT.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_AMOUNT_INCL.getValue(),
      placeholders.get(DocumentPlaceholders.O_AMOUNT_INCL.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_ANCILLIARY_AMOUNT_GROSS.getValue(),
      placeholders.get(DocumentPlaceholders.O_ANCILLIARY_AMOUNT_GROSS.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_ANCILLIARY_AMOUNT_VAT.getValue(),
      placeholders.get(DocumentPlaceholders.O_ANCILLIARY_AMOUNT_VAT.getValue()).substring(4));
    placeholders.put(DocumentPlaceholders.O_ANCILLIARY_AMOUNT_INCL.getValue(),
      placeholders.get(DocumentPlaceholders.O_ANCILLIARY_AMOUNT_INCL.getValue()).substring(4));
  }

  /**
   * Replace legal hearing placeholders.
   *
   * @param placeholders the placeholders
   * @param offer the offer
   * @param levels the levels
   */
  public void replaceLegalHearingPlaceholders(Map<String, String> placeholders, OfferDTO offer,
    List<Integer> levels) {
    LegalHearingExclusionDTO legalHearingExclusionDTO =
      legalHearingService.getLegalHearingExclusionBySubmittentAndLevel(
        offer.getSubmittent().getId(), levels);

    StringBuilder cancelNumber = new StringBuilder();
    StringBuilder cancelDescr = new StringBuilder();
    if (legalHearingExclusionDTO != null && !legalHearingExclusionDTO.getExclusionReasons()
      .isEmpty()) {
      if (legalHearingExclusionDTO.getExclusionReason() != null) {
        placeholders.put("v2_reason_exclusion", legalHearingExclusionDTO.getExclusionReason());
      } else {
        placeholders.put("v2_reason_exclusion", TemplateConstants.EMPTY_STRING);
      }
      if (legalHearingExclusionDTO.getExclusionReason() != null) {
        placeholders
          .put("first_level_exclusion_reason", legalHearingExclusionDTO.getExclusionReason());
      } else {
        placeholders.put("first_level_exclusion_reason", TemplateConstants.EMPTY_STRING);
      }
      for (ExclusionReasonDTO exclusionReason : legalHearingExclusionDTO.getExclusionReasons()) {
        if (exclusionReason.getExclusionReason().getToDate() == null) {
          cancelNumber.append(exclusionReason.getExclusionReason().getValue1()).append(", ");
          cancelDescr.append(exclusionReason.getExclusionReason().getValue2()).append(", ");
        }
      }

      if (cancelNumber.length() > 1) {
        placeholders.put(DocumentPlaceholders.EXCLUSION_NUMBER.getValue(),
          cancelNumber.substring(0, cancelNumber.length() - 2));
        placeholders.put(DocumentPlaceholders.EXCLUSION_DESCRIPTION.getValue(),
          cancelDescr.substring(0, cancelDescr.length() - 2));
      } else {
        placeholders.put(DocumentPlaceholders.EXCLUSION_NUMBER.getValue(),
          TemplateConstants.EMPTY_STRING);
        placeholders.put(DocumentPlaceholders.EXCLUSION_DESCRIPTION.getValue(),
          TemplateConstants.EMPTY_STRING);
      }
    } else {
      placeholders.put(DocumentPlaceholders.EXCLUSION_NUMBER.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.EXCLUSION_DESCRIPTION.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Gets the all companies over view status.
   *
   * @param allCompanies the all companies
   * @return the all companies over view status
   */
  public String getAllCompaniesOverViewStatus(Set<CompanyDTO> allCompanies) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAllCompaniesOverViewStatus, Parameters: allCompanies: {0}",
      allCompanies);

    if (allCompanies != null) {
      for (CompanyDTO companyDTO : allCompanies) {
        if (companyDTO.getIsProofProvided() == null || !companyDTO.getIsProofProvided()) {
          return TemplateConstants.PROOF_STATUS_NOT_ACTIVE;
        }
      }
    }
    return TemplateConstants.ALL_PROOF_STATUS_AVAILABLE;
  }

  /**
   * Gets the formatted partners.
   *
   * @param partnersList the partners list
   * @return the formatted partners
   */
  public String getFormattedPartners(Set<CompanyDTO> partnersList) {

    LOGGER.log(Level.CONFIG,
      "Executing method getFormattedPartners, Parameters: partnersList: {0}",
      partnersList);

    String partners = TemplateConstants.EMPTY_STRING;
    List<String> allPartners = new ArrayList<>();

    if (partnersList != null && !partnersList.isEmpty()) {
      // convert Set to List to use comparator for ordering0
      List<CompanyDTO> allPartnersList = new ArrayList<>(partnersList);
      Collections.sort(allPartnersList, ComparatorUtil.sortCompaniesDTOByCompanyName);
      for (CompanyDTO partner : allPartnersList) {
        allPartners.add(partner.getCompanyName());
      }
      partners = StringUtils.join(allPartners, " \n");
    }
    return partners;
  }

  /**
   * Suitability doc muss summary.
   *
   * @param offer the offer
   * @return the string
   */
  public String suitabilityDocMussSummary(OfferDTO offer) {

    LOGGER.log(Level.CONFIG, "Executing method suitabilityDocMussSummary, Parameters: offer: {0}",
      offer);

    String summary = StringUtils.EMPTY;
    for (OfferCriterionDTO offerCritirion : offer.getOfferCriteria()) {
      if (offerCritirion.getCriterion().getCriterionType()
        .equals(LookupValues.MUST_CRITERION_TYPE)) {
        if (offerCritirion.getIsFulfilled() != null
          && offerCritirion.getIsFulfilled().equals(Boolean.FALSE)) {
          return TemplateConstants.NO;
        } else if (offerCritirion.getIsFulfilled() != null
          && offerCritirion.getIsFulfilled().equals(Boolean.TRUE)) {
          summary = TemplateConstants.YES;
        }
      }
    }
    return summary;
  }

  /**
   * Creates the empty offer criteria.
   *
   * @param offer the offer
   * @return the offer DTO
   */
  // Method that sets the scores and grades of criteria and sub criteria to null
  public OfferDTO createEmptyOfferCriteria(OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method createEmptyOfferCriteria, Parameters: offer: {0}",
      offer);

    OfferDTO emptyOffer = new OfferDTO();
    List<OfferCriterionDTO> offerCriteriaList = new ArrayList<>();
    List<OfferSubcriterionDTO> offerSubCriteriaList = new ArrayList<>();

    for (int i = 0; i < offer.getOfferCriteria().size(); i++) {
      if (offer.getOfferCriteria().get(i).getCriterion().getCriterionType()
        .equals(LookupValues.MUST_CRITERION_TYPE)) {
        OfferCriterionDTO offerCriterion = new OfferCriterionDTO();
        CriterionDTO newEmptyCriterion = setEmptyCriterion(offer, i);
        offerCriterion.setCriterion(newEmptyCriterion);
        offerCriteriaList.add(offerCriterion);
      }
      if (offer.getOfferCriteria().get(i).getCriterion().getCriterionType()
        .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        OfferCriterionDTO offerCriterion = new OfferCriterionDTO();
        CriterionDTO newEmptyCriterion = setEmptyCriterion(offer, i);
        offerCriterion.setCriterion(newEmptyCriterion);
        offerCriteriaList.add(offerCriterion);
      }
      if (offer.getOfferCriteria().get(i).getCriterion().getCriterionType()
        .equals(LookupValues.AWARD_CRITERION_TYPE)) {
        OfferCriterionDTO offerCriterion = new OfferCriterionDTO();
        CriterionDTO newEmptyCriterion = setEmptyCriterion(offer, i);
        offerCriterion.setCriterion(newEmptyCriterion);
        offerCriteriaList.add(offerCriterion);
      }
      if (offer.getOfferCriteria().get(i).getCriterion().getCriterionType()
        .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
        OfferCriterionDTO offerCriterion = new OfferCriterionDTO();
        CriterionDTO newEmptyCriterion = setEmptyCriterion(offer, i);
        offerCriterion.setCriterion(newEmptyCriterion);
        offerCriteriaList.add(offerCriterion);
      }
      if (offer.getOfferCriteria().get(i).getCriterion().getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        OfferCriterionDTO offerCriterion = new OfferCriterionDTO();
        CriterionDTO newEmptyCriterion = setEmptyCriterion(offer, i);
        offerCriterion.setCriterion(newEmptyCriterion);
        offerCriteriaList.add(offerCriterion);
      }
    }

    for (OfferSubcriterionDTO offerSubCriterion : offer.getOfferSubcriteria()) {
      OfferSubcriterionDTO emptyOfferSubCriterion = new OfferSubcriterionDTO();
      SubcriterionDTO emptySubCriterion = new SubcriterionDTO();
      emptySubCriterion.setId(offerSubCriterion.getSubcriterion().getId());
      emptyOfferSubCriterion.setSubcriterion(emptySubCriterion);
      offerSubCriteriaList.add(emptyOfferSubCriterion);
    }

    emptyOffer.setOfferSubcriteria(offerSubCriteriaList);
    emptyOffer.setOfferCriteria(offerCriteriaList);
    emptyOffer.setqExTotalGrade(null);
    emptyOffer.setqExExaminationIsFulfilled(null);

    return emptyOffer;
  }

  /**
   * Sets the empty criterion.
   *
   * @param offer the offer
   * @param i the i
   * @return the criterion DTO
   */
  private CriterionDTO setEmptyCriterion(OfferDTO offer, int i) {

    LOGGER.log(Level.CONFIG,
      "Executing method setEmptyCriterion, Parameters: offer: {0}, i{1}",
      new Object[]{offer, i});

    CriterionDTO newEmptyCriterion = new CriterionDTO();
    newEmptyCriterion
      .setCriterionText(offer.getOfferCriteria().get(i).getCriterion().getCriterionText());
    newEmptyCriterion
      .setCriterionType(offer.getOfferCriteria().get(i).getCriterion().getCriterionType());
    newEmptyCriterion.setId(offer.getOfferCriteria().get(i).getCriterion().getId());
    return newEmptyCriterion;
  }

  // Method tha fixes pages number in case template creates 

  /**
   * Fix template paging.
   *
   * @param jasperPrint the jasper print
   */
  // extra blank page
  public void fixTemplatePaging(JasperPrint jasperPrint) {

    LOGGER.log(Level.CONFIG,
      "Executing method fixTemplatePaging, Parameters: jasperPrint: {0}",
      jasperPrint);

    // get totale page number
    int totalPageNumber = jasperPrint.getPages().size();
    int currentPage = 1;

    // the word that we will search
    String seite = "Seite ";

    // loop all report pages
    for (JRPrintPage page : jasperPrint.getPages()) {
      List<JRPrintElement> elements = page.getElements();
      // Loop all elements on page
      for (JRPrintElement jpe : elements) {
        // Check if text element
        if (jpe instanceof JRPrintText) {
          JRPrintText jpt = (JRPrintText) jpe;
          Object value = jpt.getValue();
          if (value instanceof String) {
            if (((String) value).contains(seite)) {
              jpt.setText("Seite " + currentPage + " / " + totalPageNumber);
            }
          }
        }
      }
      currentPage++;
    }
  }


  /**
   * Encode file to base 64 binary.
   *
   * @param filePath the file path
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public byte[] encodeFileToBase64Binary(String filePath) throws IOException {
    try (InputStream fileInputStreamReader = getClass().getResourceAsStream(filePath);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStreamReader)) {
      int i;
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      while ((i = bufferedInputStream.read()) != -1) {
        byteArrayOutputStream.write(i);
      }
      return byteArrayOutputStream.toByteArray();

    }

  }

  /**
   * Company search criteria.
   *
   * @param companySearchDTO the company search DTO
   */
  public String companySearchCriteria(CompanySearchDTO companySearchDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method companySearchCriteria, Parameters: companySearchDTO: {0}",
      companySearchDTO);

    List<String> searchCriteriaList = new ArrayList<>();
    String searchedCriteria = TemplateConstants.EMPTY_STRING;
    if (companySearchDTO != null) {
      if (companySearchDTO.getCompanyName() != null) {
        searchCriteriaList.add(companySearchDTO.getCompanyName());
      }
      if (companySearchDTO.getLocation() != null && !companySearchDTO.getLocation().isEmpty()) {
        searchCriteriaList.add(companySearchDTO.getLocation());
      }
      if (companySearchDTO.getPostCode() != null) {
        searchCriteriaList.add(companySearchDTO.getPostCode());
      }
      if (companySearchDTO.getCountryId() != null && !companySearchDTO.getCountryId().isEmpty()) {
        String countryName = new JPAQueryFactory(em).select(qCountryHistoryEntity.countryName)
          .from(qCountryHistoryEntity)
          .where(qCountryHistoryEntity.id.eq(companySearchDTO.getCountryId())).fetchOne();
        searchCriteriaList.add(countryName);
      }
      if (companySearchDTO.getCompanyTel() != null) {
        searchCriteriaList.add(companySearchDTO.getCompanyTel());
      }
      if (companySearchDTO.getWorkTypes() != null && !companySearchDTO.getWorkTypes().isEmpty()) {
        List<String> workTypeList = new ArrayList<>();
        StringBuilder worktypeSb = new StringBuilder();
        for (MasterListValueHistoryDTO worktype : companySearchDTO.getWorkTypes()) {
          worktypeSb.append(worktype.getValue1()).append(" ").append(worktype.getValue2());
          workTypeList.add(worktypeSb.toString());
          worktypeSb.setLength(0);
        }
        searchCriteriaList.add(StringUtils.join(workTypeList, ", "));
      }
      if (companySearchDTO.getLogibStatus() != null
        && companySearchDTO.getLogibStatus().getValue() != 4) {
        String logibValue = null;
        switch (companySearchDTO.getLogibStatus().getValue()) {
          case 1:
            logibValue = "Alle (logibpflichtigen Firmen)";
            break;
          case 2:
            logibValue = "Logib";
            break;
          case 3:
            logibValue = "Logib ARGIB";
            break;
          default:
            break;
        }
        searchCriteriaList.add(logibValue);
      }
      if (companySearchDTO.getIlo() != null) {
        searchCriteriaList.add(companySearchDTO.getIlo().getValue1());
      }
      if (companySearchDTO.getTlp() != null && companySearchDTO.getTlp()) {
        searchCriteriaList.add("Partnerfirma (TLP)");
      }
      if (companySearchDTO.getNotes() != null) {
        searchCriteriaList.add(companySearchDTO.getNotes());
      }
      if (companySearchDTO.getArchived() != null && companySearchDTO.getArchived()) {
        searchCriteriaList.add("Firma archiviert");
      }
      if (companySearchDTO.getProofStatus() != null
        && companySearchDTO.getProofStatus().getValue() != 5) {
        String proofStatusValue = null;
        switch (companySearchDTO.getProofStatus().getValue()) {
          case 1:
            proofStatusValue = TemplateConstants.ALL_PROOF_STATUS_AVAILABLE;
            break;
          case 2:
            proofStatusValue = "Nachweise nicht mehr aktuell, nicht vollständig, oder nicht vorhanden";
            break;
          case 3:
            proofStatusValue = "Rücksprache mit der Fachstelle Beschaffungswesen";
            break;
          case 4:
            proofStatusValue = "Rücksprache mit KAIO ZKB / FaBe";
            break;
          default:
            break;
        }
        searchCriteriaList.add(proofStatusValue);
      }
      searchedCriteria = StringUtils.join(searchCriteriaList, " / ");
    }
    return searchedCriteria;
  }

  /**
   * Function to convert distance from cm to docx4j-distance.
   *
   * @param distanceInCm the distance in cm
   * @return docx4j-distance
   */
  private String cmToDocx4jDistance(double distanceInCm) {
    return String.valueOf((int) (567.13446 * distanceInCm - 0.17981083));
  }

  /**
   *
   */
  public void setCompaniesRank(SubmissionDTO submission,
    List<SubmissionOverviewDTO> submissionOverviewList) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCompaniesRank, Parameters: submission: {0}, "
        + "submissionOverviewList: {1}",
      new Object[]{submission, submissionOverviewList});

    if (!submission.getProcess().equals(Process.SELECTIVE)) {
      if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.AWARD_EVALUATION_CLOSED)) {
        Collections.sort(submissionOverviewList, ComparatorUtil.sortCompaniesByAwardRank);
      } else if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED)) {
        Collections.sort(submissionOverviewList, ComparatorUtil.sortCompaniesByRank);
      } else {
        Collections.sort(submissionOverviewList, ComparatorUtil.sortCompaniesByAmount);
      }
    } else {
      if (!compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
        Collections.sort(submissionOverviewList, ComparatorUtil.sortCompaniesByName);
      } else if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED)) {
        Collections.sort(submissionOverviewList, ComparatorUtil.sortCompaniesByRank);
      } else if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED)
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.AWARD_EVALUATION_CLOSED)) {
        Collections.sort(submissionOverviewList, ComparatorUtil.sortCompaniesByAmount);
      } else if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.AWARD_EVALUATION_CLOSED)) {
        Collections.sort(submissionOverviewList, ComparatorUtil.sortCompaniesByAwardRank);
      }
    }

    for (int i = 0; i < submissionOverviewList.size(); i++) {
      submissionOverviewList.get(i).setRank(i + 1);
    }
  }

  /**
   * Function to set print properties for Xlsx documents.
   *
   * @param outputStream the outputStream
   * @param papersize the paper size
   * @return the ByteArrayOutputStream
   */
  public ByteArrayOutputStream setXlsxPrintSetup(ByteArrayOutputStream outputStream,
    short papersize) throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method setXlsxPrintSetup, Parameters: outputStream: {0}, "
        + "papersize: {1}",
      new Object[]{outputStream, papersize});

    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
      XSSFSheet sheet = workbook.getSheetAt(0);

      sheet.setFitToPage(true);
      sheet.getPrintSetup().setPaperSize(papersize);
      sheet.getPrintSetup().setFitWidth((short) 1);
      sheet.getPrintSetup().setFitHeight((short) 0);

      workbook.write(bos);
      bos.close();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
    }
    return bos;
  }

  /**
   * Function to set the report layout for Xlsx documents.
   *
   * @param outputStream the outputStream
   * @param orientation the orientation
   * @return the ByteArrayOutputStream
   */
  public ByteArrayOutputStream optimizeReportLayout(ByteArrayOutputStream outputStream,
    boolean orientation)
    throws IOException {

    LOGGER.log(Level.CONFIG,
      "Executing method optimizeReportLayout, Parameters: outputStream: {0}, "
        + "orientation: {1}",
      new Object[]{outputStream, orientation});

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (XSSFWorkbook workbook = new XSSFWorkbook(
      new ByteArrayInputStream(outputStream.toByteArray()))) {
      XSSFSheet sheet = workbook.getSheetAt(0);
      importExportFileService.setPrintLayoutSettings(sheet, orientation);
      workbook.write(bos);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return bos;
  }

}
