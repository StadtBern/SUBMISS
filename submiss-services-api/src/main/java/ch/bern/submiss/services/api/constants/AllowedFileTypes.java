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

package ch.bern.submiss.services.api.constants;

/**
 * The Enum DocumentCreationType.
 */
public enum AllowedFileTypes {


  /** The docx. */
  DOCX,
  /** The pdf. */
  PDF,
  /** The xlsx. */
  XLSX,
  /** The csv. */
  CSV,
  /** The msg. */
  MSG,
  /** The png. */
  PNG,
  /** The jpg. */
  JPG,
  /** The jpeg. */
  JPEG,
  /** The txt. */
  TXT,
  /** The rtf. */
  RTF,
  /** The htm. */
  HTM,
  /** The html. */
  HTML,
  /** The mpp. */
  MPP,
  /** The pptx. */
  PPTX;

  /**
   * File allowed.
   *
   * @param fileType the file type
   * @return true, if successful
   */
  public static boolean fileAllowed(String fileType) {
    for (AllowedFileTypes me : AllowedFileTypes.values()) {
      if (me.name().equalsIgnoreCase(fileType))
        return true;
    }
    return false;
  }

}
