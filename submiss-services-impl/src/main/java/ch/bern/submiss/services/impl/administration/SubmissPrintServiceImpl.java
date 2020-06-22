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

import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SubmissPrintService;
import ch.bern.submiss.services.api.constants.TemplateConstants;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.SettingsDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import com.aspose.cells.License;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.PageVerticalAlignment;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveFormat;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class SubmissPrintServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SubmissPrintService.class})
@Singleton
public class SubmissPrintServiceImpl extends BaseService implements SubmissPrintService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SubmissPrintServiceImpl.class.getName());

  /**
   * The Constant ASPOSE_LICENSE.
   */
  private static final String ASPOSE_LICENSE = "/lic/Aspose.Total.Java.lic";

  /**
   * The Constant CONVERT_TO_PDF_ERROR_MESSAGE.
   */
  private static final String CONVERT_TO_PDF_ERROR_MESSAGE = "Unable to convert document to pdf";

  @OsgiService
  @Inject
  protected VersionService versionService;
  @Inject
  private CacheBean cacheBean;
  @Inject
  private SDService sDService;

  /**
   * The template service.
   */
  // https://stackoverflow.com/questions/32265539/spring-injection-by-type-two-repository-with-same-name
  @OsgiService
  @Autowired
  @Qualifier("templateService")
  private com.eurodyn.qlack2.fuse.lexicon.api.TemplateService templateService;

  @Override
  public byte[] convertToPDF(byte[] documentByteArray) {

    LOGGER.log(Level.CONFIG,
      "Executing method convertToPDF, Parameters: documentByteArray: {0}",
      documentByteArray);

    try (ByteArrayOutputStream docOutStream = new ByteArrayOutputStream();
      InputStream fileInputStream = new ByteArrayInputStream(documentByteArray)) {

      Document doc = new Document(fileInputStream);
      com.aspose.words.License license = new com.aspose.words.License();
      // Set the license
      InputStream fstream =
        SubmissPrintServiceImpl.class.getResourceAsStream(ASPOSE_LICENSE);
      license.setLicense(fstream);
      // Updates widths of cells and tables in the document
      doc.updateTableLayout();
      // Options for pdf saving
      PdfSaveOptions options = new PdfSaveOptions();
      options.setSaveFormat(SaveFormat.PDF);
      doc.save(docOutStream, options);

      return docOutStream.toByteArray();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, CONVERT_TO_PDF_ERROR_MESSAGE, e);
    }
    return new byte[0];
  }

  @Override
  public byte[] convertExcelToPDF(byte[] documentByteArray) {

    LOGGER.log(Level.CONFIG,
      "Executing method convertExcelToPDF, Parameters: documentByteArray: {0}",
      documentByteArray);

    try (ByteArrayOutputStream docOutStream = new ByteArrayOutputStream();
      InputStream fileInputStream = new ByteArrayInputStream(documentByteArray)) {

      com.aspose.cells.Workbook workBook = new com.aspose.cells.Workbook(fileInputStream);
      License license = new License();
      InputStream fstream =
        SubmissPrintServiceImpl.class.getResourceAsStream(ASPOSE_LICENSE);
      license.setLicense(fstream);
      workBook.save(docOutStream, com.aspose.cells.SaveFormat.PDF);

      return docOutStream.toByteArray();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, CONVERT_TO_PDF_ERROR_MESSAGE, e);
    }
    return new byte[0];
  }

  @Override
  public byte[] convertImageToPDF(byte[] documentByteArray) {

    LOGGER.log(Level.CONFIG,
      "Executing method convertImageToPDF, Parameters: documentByteArray: {0}",
      documentByteArray);

    try (ByteArrayOutputStream docOutStream = new ByteArrayOutputStream();
      InputStream fileInputStream = new ByteArrayInputStream(documentByteArray)) {

      Document doc = new Document();
      doc.getFirstSection().getPageSetup().setVerticalAlignment(PageVerticalAlignment.CENTER);
      // Set the license
      com.aspose.words.License license = new com.aspose.words.License();
      InputStream fstream =
        SubmissPrintServiceImpl.class.getResourceAsStream(ASPOSE_LICENSE);
      license.setLicense(fstream);
      // Build the document with the image
      DocumentBuilder builder = new DocumentBuilder(doc);
      builder.insertImage(fileInputStream);
      builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
      // Options for pdf saving
      PdfSaveOptions options = new PdfSaveOptions();
      options.setSaveFormat(SaveFormat.PDF);
      doc.save(docOutStream, options);

      return docOutStream.toByteArray();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, CONVERT_TO_PDF_ERROR_MESSAGE, e);
    }
    return new byte[0];
  }

  @Override
  public String getPdfExtension(String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method getPdfExtension, Parameters: filename: {0}",
      filename);

    if (!filename.endsWith("pdf")) {
      StringBuilder sb = new StringBuilder(filename);
      sb.replace(filename.lastIndexOf('.') + 1, filename.length(), "pdf");
      filename = sb.toString();
    }
    return filename;
  }

  @Override
  public void printDocument(String nodeId, String versionName, String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method downloadDocument, Parameters: nodeId: {0}, "
        + "versionName: {1}, fileName: {2}",
      new Object[]{nodeId, versionName, filename});

    byte[] file;

    if (filename.endsWith("docx")) {
      file = convertToPDF(versionService.getBinContent(nodeId, versionName));
      filename = getPdfExtension(filename);
    } else if (filename.endsWith("png")) {
      file = convertImageToPDF(versionService.getBinContent(nodeId, versionName));
      filename = getPdfExtension(filename);
    } else {
      file = versionService.getBinContent(nodeId, versionName);
    }

    printProcedure(file, filename);
  }

  @Override
  public void printGeneratedDocument(byte[] documentByteArray, String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method printGeneratedDocument, Parameters: documentByteArray: {0}, "
        + "filename: {1}",
      new Object[]{documentByteArray, filename});

    if (filename.endsWith("docx")) {
      filename = getPdfExtension(filename);
      documentByteArray = convertToPDF(documentByteArray);
    }

    printProcedure(documentByteArray, filename);
  }

  /*---- Helper methods ----*/

  /**
   * The print procedure.
   *
   * @param documentByteArray the document byte[]
   * @param filename the file name
   */
  private void printProcedure(byte[] documentByteArray, String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method printProcedure, Parameters: documentByteArray: {0}, "
        + "filename: {1}",
      new Object[]{documentByteArray, filename});

    File dir = new File(System.getProperty("user.dir") + "//documents");
    if (!dir.exists()) {
      dir.mkdirs();
    }

    FileOutputStream fos = null;
    DataInputStream dis = new DataInputStream(
      new ByteArrayInputStream(documentByteArray));

    try {
      for (int q = 0; q < documentByteArray.length; q++) {
        documentByteArray[q] = dis.readByte();
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e, e);
    } finally {
      try {
        dis.close();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e, e);
      }
    }
    try {
      fos = new FileOutputStream(new File(dir, "//" + filename));
      fos.write(documentByteArray);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e, e);
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e, e);
      }
    }
    proceedWithCommand(filename);
  }

  /**
   * Proceeds with the linux command for server side printing.
   *
   * @param filename the file name
   */
  private void proceedWithCommand(String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method printProcedure, Parameters: fileName: {0}",
      filename);

    MasterListValueHistoryDTO template =
      sDService.getMasterListHistoryByCode(LookupValues.PRINT_COMMAND);

    if (template != null) {
      String lpr = templateService.processTemplate(template.getValue1(),
        replacePrintCommandPlaceholders(filename));

      LOGGER.log(Level.SEVERE, lpr);
      // Windows Terminal
      // Uncomment for test purposes.
      /*String prefix = "cmd.exe";
      String c = "/c";*/

      // Linux terminal
      String prefix = "/bin/bash";
      String c = "-c";
      // Linux
      ProcessBuilder builder = new ProcessBuilder(prefix, c,
        "cd " + System.getProperty("user.dir") + "//documents" + " && " + lpr + " && rm -Rf "
          + addDoubleQuotes(filename));
      builder.redirectErrorStream(true);
      java.lang.Process p;
      try {
        p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
          line = r.readLine();
          if (line == null) {
            break;
          }
        }
      } catch (IOException e1) {
        LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e1, e1);
      }
    } else {
      LOGGER.log(Level.SEVERE, "User not authorized for server side printing");
    }
  }

  /**
   * Creates the lpr command.
   *
   * @param filename the file name
   * @return the HashMap with the print commands for the server side printing
   */
  private HashMap<String, Object> replacePrintCommandPlaceholders(String filename) {

    LOGGER.log(Level.CONFIG,
      "Executing method replacePrintCommandPlaceholders, Parameters: fileName: {0}",
      filename);

    SettingsDTO settings = sDService.getPrinterSettings();
    HashMap<String, Object> data = new HashMap<>();
    if (settings.getIpPrinterAddress() != null) {
      data.put("ipAddressPrinter", settings.getIpPrinterAddress());
    } else {
      data.put("ipAddressPrinter", StringUtils.EMPTY);
    }
    if (settings.getIpPrinterPort() != null) {
      data.put("ipPortPrinter", settings.getIpPrinterPort());
    } else {
      data.put("ipPortPrinter", StringUtils.EMPTY);
    }
    UserDTO user = getUser();
    String[] splitedUsernameParts = user.getUsername().split(LookupValues.USER_NAME_SPECIAL_CHARACTER);
    data.put("userName", splitedUsernameParts[0]);
    data.put("documentName", addDoubleQuotes(filename));
    return data;
  }

  /**
   * Adds double quotes in filename.
   *
   * @param filename the document filename
   * @return the filename in double quotes
   */
  private String addDoubleQuotes(String filename) {
    if (StringUtils.isNotBlank(filename)) {
      filename = LookupValues.DOUBLEQUOTE + filename + LookupValues.DOUBLEQUOTE;
    }
    return filename;
  }
}
