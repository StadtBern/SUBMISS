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

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;

import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.util.View;

/**
 * The Class SubmissionBaseDTO.
 */
public class SubmissionBaseDTO {

	@JsonView(View.Public.class)
	private String id;

	/**
	 * The project - Direktion, Abteilung , Objekt , Projektname, GATT/WTO ,
	 * Intern/Extern.
	 */
	@JsonView(View.Public.class)
	private ProjectDTO project;

	/** The work type - Arbeitsgattung. */
	@JsonView(View.Public.class)
	private MasterListValueHistoryDTO workType;

	/** The description - Beschreibung zur Arbeitsgattung. */
	@JsonView(View.Internal.class)
	private String description;

	/** The process - Verfahren. */
	@JsonView(View.Internal.class)
	private Process process;

	/** The publication date award - Publikation Zuschlag. */
	@JsonView(View.Internal.class)
	private Date publicationDateAward;

	/**
	 * The publication date direct award - Publikation Absicht freihändige Vergabe.
	 */
	@JsonView(View.Internal.class)
	private Date publicationDateDirectAward;

	/** The publication date - Publikation. */
	@JsonView(View.Internal.class)
	private Date publicationDate;

	/** The first deadline - Eingabetermin 1. */
	@JsonView(View.Internal.class)
	private Date firstDeadline;

	/** The application opening date - Bewerbungsöffnung. */
	@JsonView(View.Internal.class)
	private Date applicationOpeningDate;

	/** The second deadline - Eingabetermin 2. */
	@JsonView(View.Internal.class)
	private Date secondDeadline;

	/** The offer opening date - Offertöffnung. */
	@JsonView(View.Internal.class)
	private Date offerOpeningDate;

  /** The second offer opening date - Offertöffnung_2. */
  @JsonView(View.Internal.class)
  private Date secondOfferOpeningDate;

	/** The commission procurement proposal date - BeKo-Sitzung. */
	@JsonView(View.Internal.class)
	private Date commissionProcurementProposalDate;

	/** The notes - Bemerkungsfeld. */
	@JsonView(View.Internal.class)
	private String notes;

	// -----------------------
	private Map<String, String> searchedCriteria;

	public Map<String, String> getSearchedCriteria() {
		return searchedCriteria;
	}

	public void setSearchedCriteria(Map<String, String> searchedCriteria) {
		this.searchedCriteria = searchedCriteria;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ProjectDTO getProject() {
		return project;
	}

	public void setProject(ProjectDTO project) {
		this.project = project;
	}

	public MasterListValueHistoryDTO getWorkType() {
		return workType;
	}

	public void setWorkType(MasterListValueHistoryDTO workType) {
		this.workType = workType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public Date getPublicationDateDirectAward() {
		return publicationDateDirectAward;
	}

	public void setPublicationDateDirectAward(Date publicationDateDirectAward) {
		this.publicationDateDirectAward = publicationDateDirectAward;
	}

	public Date getPublicationDateAward() {
		return publicationDateAward;
	}

	public void setPublicationDateAward(Date publicationDateAward) {
		this.publicationDateAward = publicationDateAward;
	}

	public Date getFirstDeadline() {
		return firstDeadline;
	}

	public void setFirstDeadline(Date firstDeadline) {
		this.firstDeadline = firstDeadline;
	}

	public Date getSecondDeadline() {
		return secondDeadline;
	}

	public void setSecondDeadline(Date secondDeadline) {
		this.secondDeadline = secondDeadline;
	}

	public Date getApplicationOpeningDate() {
		return applicationOpeningDate;
	}

	public void setApplicationOpeningDate(Date applicationOpeningDate) {
		this.applicationOpeningDate = applicationOpeningDate;
	}

	public Date getOfferOpeningDate() {
		return offerOpeningDate;
	}

	public void setOfferOpeningDate(Date offerOpeningDate) {
		this.offerOpeningDate = offerOpeningDate;
	}

  public Date getSecondOfferOpeningDate() {return secondOfferOpeningDate;}

  public void setSecondOfferOpeningDate(Date secondOfferOpeningDate) {this.secondOfferOpeningDate = secondOfferOpeningDate;}

  public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getCommissionProcurementProposalDate() {
		return commissionProcurementProposalDate;
	}

	public void setCommissionProcurementProposalDate(Date commissionProcurementProposalDate) {
		this.commissionProcurementProposalDate = commissionProcurementProposalDate;
	}

}
