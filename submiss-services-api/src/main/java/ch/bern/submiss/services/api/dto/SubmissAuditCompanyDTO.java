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

package ch.bern.submiss.services.api.dto;

import com.fasterxml.jackson.annotation.JsonView;

import ch.bern.submiss.services.api.util.View;

/**
 * The Class AuditCompanyDTO.
 */
public class SubmissAuditCompanyDTO extends SubmissAuditDTO {

	/** The companyName. */
	@JsonView(View.Public.class)
	private String companyName;

	/** The proofStatusFabe. */
	@JsonView(View.Public.class)
	private int proofStatusFabe;

	/**
	 * Gets the company name.
	 *
	 * @return the company name
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Sets the company name.
	 *
	 * @param companyName
	 *            the new company name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Gets the proof status fabe.
	 *
	 * @return the proof status fabe
	 */
	public int getProofStatusFabe() {
		return proofStatusFabe;
	}

	/**
	 * Sets the proof status fabe.
	 *
	 * @param proofStatusFabe
	 *            the new proof status fabe
	 */
	public void setProofStatusFabe(int proofStatusFabe) {
		this.proofStatusFabe = proofStatusFabe;
	}

}
