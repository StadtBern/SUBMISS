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
import java.util.Date;

/**
 * The Class DocumentEditForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentEditForm {

	/** The id. */
	private String id;

	/** The name. */
	private String title;

	/** The private document. */
	private Boolean privateDocument;

	/** The private document. */
	private Boolean privateDocumentSetChanged;

  /**
   * The last modified on.
   */
  private Date lastModifiedOn;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the private document.
	 *
	 * @return the private document
	 */
	public Boolean getPrivateDocument() {
		return privateDocument;
	}

	/**
	 * Sets the private document.
	 *
	 * @param privateDocument
	 *            the new private document
	 */
	public void setPrivateDocument(Boolean privateDocument) {
		this.privateDocument = privateDocument;
	}
	
	/**
	 * Gets the private document set changed.
	 *
	 * @return the private document set changed
	 */
	public Boolean getPrivateDocumentSetChanged() {
		return privateDocumentSetChanged;
	}

	/**
	 * Sets the private document set changed.
	 *
	 * @param privateDocumentSetChanged the new private document set changed
	 */
	public void setPrivateDocumentSetChanged(Boolean privateDocumentSetChanged) {
		this.privateDocumentSetChanged = privateDocumentSetChanged;
	}

  /**
   * Gets the last modified on.
   *
   * @return the last modified on
   */
  public Date getLastModifiedOn() {
    return lastModifiedOn;
  }

  /**
   * Sets the last modified on.
   *
   * @param lastModifiedOn the new last modified on
   */
  public void setLastModifiedOn(Date lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }
}
