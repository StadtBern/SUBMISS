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

package ch.bern.submiss.services.api.administration;


/**
 * The Interface SubmissPrintService.
 */
public interface SubmissPrintService {

  /**
   * Converts Word to PDF.
   *
   * @param documentByteArray the document byte array
   * @return the byte[]
   */
  byte[] convertToPDF(byte[] documentByteArray);

  /**
   * Converts Excel to PDF.
   *
   * @param documentByteArray the document byte array
   * @return the byte[]
   */
  byte[] convertExcelToPDF(byte[] documentByteArray);

  /**
   * Converts Image to PDF.
   *
   * @param documentByteArray the document byte[]
   * @return the byte[]
   */
  byte[] convertImageToPDF(byte[] documentByteArray);

  /**
   * Gets the filename with pdf extension.
   *
   * @param filename the filename
   * @return the filename with pdf extension
   */
  String getPdfExtension(String filename);

  /**
   * Prints the document.
   *
   * @param nodeId the node id
   * @param versionName the version name
   * @return the byte[]
   */
  void printDocument(String nodeId, String versionName, String fileName);

  /**
   * Prints the documents which are not stored in the database.
   *
   * @param documentByteArray the document byte array
   * @param filename the filename
   */
  void printGeneratedDocument(byte[] documentByteArray, String filename);
}
