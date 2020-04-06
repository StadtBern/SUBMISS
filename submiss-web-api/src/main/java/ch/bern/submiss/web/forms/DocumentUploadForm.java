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

package ch.bern.submiss.web.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class DocumentUploadForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentUploadForm {

  /** The folder id. */
  private String folderId;


  /** The filename. */
  private String filename;


  /** The version id. */
  private String versionId;


  /** The uploaded file id. */
  private String uploadedFileId;


  /** The version document. */
  private Boolean versionDocument;

  /** The versioning allowed. */
  private Boolean versioningAllowed;


  /** The project documents. */
  private Boolean projectDocument;


  /**
   * Gets the folder id.
   *
   * @return the folder id
   */
  public String getFolderId() {
    return folderId;
  }



  /**
   * Sets the folder id.
   *
   * @param folderId the new folder id
   */
  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  /**
   * Gets the filename.
   *
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }


  /**
   * Sets the filename.
   *
   * @param filename the new filename
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }


  /**
   * Gets the version id.
   *
   * @return the version id
   */
  public String getVersionId() {
    return versionId;
  }


  /**
   * Sets the version id.
   *
   * @param versionId the new version id
   */
  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }



  /**
   * Gets the uploaded file id.
   *
   * @return the uploaded file id
   */
  public String getUploadedFileId() {
    return uploadedFileId;
  }


  /**
   * Sets the uploaded file id.
   *
   * @param uploadedFileId the new uploaded file id
   */
  public void setUploadedFileId(String uploadedFileId) {
    this.uploadedFileId = uploadedFileId;
  }

  /**
   * Gets the version document.
   *
   * @return the version document
   */
  public Boolean getVersionDocument() {
    return versionDocument;
  }


  /**
   * Sets the version document.
   *
   * @param versionDocument the new version document
   */
  public void setVersionDocument(Boolean versionDocument) {
    this.versionDocument = versionDocument;
  }



  /**
   * Gets the versioning allowed.
   *
   * @return the versioning allowed
   */
  public Boolean getVersioningAllowed() {
    return versioningAllowed;
  }


  /**
   * Sets the versioning allowed.
   *
   * @param versioningAllowed the new versioning allowed
   */
  public void setVersioningAllowed(Boolean versioningAllowed) {
    this.versioningAllowed = versioningAllowed;
  }



  /**
   * Gets the project document.
   *
   * @return the project document
   */
  public Boolean getProjectDocument() {
    return projectDocument;
  }


  /**
   * Sets the project document.
   *
   * @param projectDocument the new project document
   */
  public void setProjectDocument(Boolean projectDocument) {
    this.projectDocument = projectDocument;
  }


}
