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

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import ch.bern.submiss.services.api.util.View;

/**
 * The Class OperationReportForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationReportForm extends ReportBaseForm {

	/** The description - Beschreibung. */
	@JsonView(View.Internal.class)
	private String description;

	/** The publication date award from - Publikation Zuschlag von. */
	@JsonView(View.Internal.class)
	private Date publicationDateAwardFrom;

	/** The publication date award to - Publikation Zuschlag bis. */
	@JsonView(View.Internal.class)
	private Date publicationDateAwardTo;

	/** The publication date from - Publikation von. */
	@JsonView(View.Internal.class)
	private Date publicationDateFrom;

	/** The publication date to - Publikation bis. */
	@JsonView(View.Internal.class)
	private Date publicationDateTo;

	/** The application opening date from - Bewerbungsöffnung von. */
	@JsonView(View.Internal.class)
	private Date applicationOpeningDateFrom;

	/** The application opening date to - Bewerbungsöffnung bis. */
	@JsonView(View.Internal.class)
	private Date applicationOpeningDateTo;

	/** The offer opening date from - Offertöffnung von. */
	@JsonView(View.Internal.class)
	private Date offerOpeningDateFrom;

	/** The offer opening date to - Offertöffnung bis. */
	@JsonView(View.Internal.class)
	private Date offerOpeningDateTo;

	/** The commission procurement proposal date from - BeKo-Sitzung von. */
	@JsonView(View.Internal.class)
	private Date commissionProcurementProposalDateFrom;

	/** The commission procurement proposal date to - BeKo-Sitzung bis. */
	@JsonView(View.Internal.class)
	private Date commissionProcurementProposalDateTo;
	
	/** The awardCompany - Zuschlagsempfänger. */
	@JsonView(View.Internal.class)
	private String awardCompany;

	/** The notes - Submissionsbemerkungen. */
	@JsonView(View.Internal.class)
	private String notes;

	/** The gatt wto - GATT / WTO. */
	@JsonView(View.Internal.class)
	private Boolean gattWto;

	/**
	 * The publication date direct award from- Publikation Absicht freihändige
	 * Vergabe von.
	 */
	@JsonView(View.Internal.class)
	private Date publicationDateDirectAwardFrom;

	/**
	 * The publication date direct award to - Publikation Absicht freihändige
	 * Vergabe bis.
	 */
	@JsonView(View.Internal.class)
	private Date publicationDateDirectAwardTo;

	/** The first deadline from - Eingabetermin 1 von. */
	@JsonView(View.Internal.class)
	private Date firstDeadlineFrom;

	/** The first deadline to - Eingabetermin 2 bis. */
	@JsonView(View.Internal.class)
	private Date firstDeadlineTo;

	/** The second deadline from Eingabetermin 2 von. */
	@JsonView(View.Internal.class)
	private Date secondDeadlineFrom;

	/** The second deadline to - Eingabetermin 2 bis. */
	@JsonView(View.Internal.class)
	private Date secondDeadlineTo;

	/** The first level available date from - Verfügungsdatum 1.Stufe von. */
	@JsonView(View.Internal.class)
	private Date firstLevelavailableDateFrom;

	/** The first level available date to - Verfügungsdatum 1.Stufe bis. */
	@JsonView(View.Internal.class)
	private Date firstLevelavailableDateTo;

	/** The available date from - Verfügungsdatum von. */
	@JsonView(View.Internal.class)
	private Date levelavailableDateFrom;

	/** The available date to - Verfügungsdatum bis. */
	@JsonView(View.Internal.class)
	private Date levelavailableDateTo;

	/** The cancelation reasons - Verfahrensabbruch. */
	@JsonView(View.Internal.class)
	private List<String> cancelationreasons;

	/** The objects - Freihandvergabe. */
	@JsonView(View.Internal.class)
	private List<String> reasonFreeAwards;

	/** The intern - Intern/Extern. */
	@JsonView(View.Internal.class)
	private Boolean intern;

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the publication date award from.
	 *
	 * @return the publication date award from
	 */
	public Date getPublicationDateAwardFrom() {
		return publicationDateAwardFrom;
	}

	/**
	 * Sets the publication date award from.
	 *
	 * @param publicationDateAwardFrom
	 *            the new publication date award from
	 */
	public void setPublicationDateAwardFrom(Date publicationDateAwardFrom) {
		this.publicationDateAwardFrom = publicationDateAwardFrom;
	}

	/**
	 * Gets the publication date award to.
	 *
	 * @return the publication date award to
	 */
	public Date getPublicationDateAwardTo() {
		return publicationDateAwardTo;
	}

	/**
	 * Sets the publication date award to.
	 *
	 * @param publicationDateAwardTo
	 *            the new publication date award to
	 */
	public void setPublicationDateAwardTo(Date publicationDateAwardTo) {
		this.publicationDateAwardTo = publicationDateAwardTo;
	}

	/**
	 * Gets the publication date from.
	 *
	 * @return the publication date from
	 */
	public Date getPublicationDateFrom() {
		return publicationDateFrom;
	}

	/**
	 * Sets the publication date from.
	 *
	 * @param publicationDateFrom
	 *            the new publication date from
	 */
	public void setPublicationDateFrom(Date publicationDateFrom) {
		this.publicationDateFrom = publicationDateFrom;
	}

	/**
	 * Gets the publication date to.
	 *
	 * @return the publication date to
	 */
	public Date getPublicationDateTo() {
		return publicationDateTo;
	}

	/**
	 * Sets the publication date to.
	 *
	 * @param publicationDateTo
	 *            the new publication date to
	 */
	public void setPublicationDateTo(Date publicationDateTo) {
		this.publicationDateTo = publicationDateTo;
	}

	/**
	 * Gets the application opening date from.
	 *
	 * @return the application opening date from
	 */
	public Date getApplicationOpeningDateFrom() {
		return applicationOpeningDateFrom;
	}

	/**
	 * Sets the application opening date from.
	 *
	 * @param applicationOpeningDateFrom
	 *            the new application opening date from
	 */
	public void setApplicationOpeningDateFrom(Date applicationOpeningDateFrom) {
		this.applicationOpeningDateFrom = applicationOpeningDateFrom;
	}

	/**
	 * Gets the application opening date to.
	 *
	 * @return the application opening date to
	 */
	public Date getApplicationOpeningDateTo() {
		return applicationOpeningDateTo;
	}

	/**
	 * Sets the application opening date to.
	 *
	 * @param applicationOpeningDateTo
	 *            the new application opening date to
	 */
	public void setApplicationOpeningDateTo(Date applicationOpeningDateTo) {
		this.applicationOpeningDateTo = applicationOpeningDateTo;
	}

	/**
	 * Gets the offer opening date from.
	 *
	 * @return the offer opening date from
	 */
	public Date getOfferOpeningDateFrom() {
		return offerOpeningDateFrom;
	}

	/**
	 * Sets the offer opening date from.
	 *
	 * @param offerOpeningDateFrom
	 *            the new offer opening date from
	 */
	public void setOfferOpeningDateFrom(Date offerOpeningDateFrom) {
		this.offerOpeningDateFrom = offerOpeningDateFrom;
	}

	/**
	 * Gets the offer opening date to.
	 *
	 * @return the offer opening date to
	 */
	public Date getOfferOpeningDateTo() {
		return offerOpeningDateTo;
	}

	/**
	 * Sets the offer opening date to.
	 *
	 * @param offerOpeningDateTo
	 *            the new offer opening date to
	 */
	public void setOfferOpeningDateTo(Date offerOpeningDateTo) {
		this.offerOpeningDateTo = offerOpeningDateTo;
	}

	/**
	 * Gets the commission procurement proposal date from.
	 *
	 * @return the commission procurement proposal date from
	 */
	public Date getCommissionProcurementProposalDateFrom() {
		return commissionProcurementProposalDateFrom;
	}

	/**
	 * Sets the commission procurement proposal date from.
	 *
	 * @param commissionProcurementProposalDateFrom
	 *            the new commission procurement proposal date from
	 */
	public void setCommissionProcurementProposalDateFrom(Date commissionProcurementProposalDateFrom) {
		this.commissionProcurementProposalDateFrom = commissionProcurementProposalDateFrom;
	}

	/**
	 * Gets the commission procurement proposal date to.
	 *
	 * @return the commission procurement proposal date to
	 */
	public Date getCommissionProcurementProposalDateTo() {
		return commissionProcurementProposalDateTo;
	}

	/**
	 * Sets the commission procurement proposal date to.
	 *
	 * @param commissionProcurementProposalDateTo
	 *            the new commission procurement proposal date to
	 */
	public void setCommissionProcurementProposalDateTo(Date commissionProcurementProposalDateTo) {
		this.commissionProcurementProposalDateTo = commissionProcurementProposalDateTo;
	}

	
	/**
	 * Gets the award company.
	 *
	 * @return the award company
	 */
	public String getAwardCompany() {
		return awardCompany;
	}

	/**
	 * Sets the award company.
	 *
	 * @param awardCompany the new award company
	 */
	public void setAwardCompany(String awardCompany) {
		this.awardCompany = awardCompany;
	}

	/**
	 * Gets the notes.
	 *
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the notes.
	 *
	 * @param notes
	 *            the new notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Gets the gatt wto.
	 *
	 * @return the gatt wto
	 */
	public Boolean getGattWto() {
		return gattWto;
	}

	/**
	 * Sets the gatt wto.
	 *
	 * @param gattWto
	 *            the new gatt wto
	 */
	public void setGattWto(Boolean gattWto) {
		this.gattWto = gattWto;
	}

	/**
	 * Gets the first deadline from.
	 *
	 * @return the first deadline from
	 */
	public Date getFirstDeadlineFrom() {
		return firstDeadlineFrom;
	}

	/**
	 * Sets the first deadline from.
	 *
	 * @param firstDeadlineFrom
	 *            the new first deadline from
	 */
	public void setFirstDeadlineFrom(Date firstDeadlineFrom) {
		this.firstDeadlineFrom = firstDeadlineFrom;
	}

	/**
	 * Gets the first deadline to.
	 *
	 * @return the first deadline to
	 */
	public Date getFirstDeadlineTo() {
		return firstDeadlineTo;
	}

	/**
	 * Sets the first deadline to.
	 *
	 * @param firstDeadlineTo
	 *            the new first deadline to
	 */
	public void setFirstDeadlineTo(Date firstDeadlineTo) {
		this.firstDeadlineTo = firstDeadlineTo;
	}

	/**
	 * Gets the second deadline from.
	 *
	 * @return the second deadline from
	 */
	public Date getSecondDeadlineFrom() {
		return secondDeadlineFrom;
	}

	/**
	 * Sets the second deadline from.
	 *
	 * @param secondDeadlineFrom
	 *            the new second deadline from
	 */
	public void setSecondDeadlineFrom(Date secondDeadlineFrom) {
		this.secondDeadlineFrom = secondDeadlineFrom;
	}

	/**
	 * Gets the second deadline to.
	 *
	 * @return the second deadline to
	 */
	public Date getSecondDeadlineTo() {
		return secondDeadlineTo;
	}

	/**
	 * Sets the second deadline to.
	 *
	 * @param secondDeadlineTo
	 *            the new second deadline to
	 */
	public void setSecondDeadlineTo(Date secondDeadlineTo) {
		this.secondDeadlineTo = secondDeadlineTo;
	}

	/**
	 * Gets the first levelavailable date from.
	 *
	 * @return the first levelavailable date from
	 */
	public Date getFirstLevelavailableDateFrom() {
		return firstLevelavailableDateFrom;
	}

	/**
	 * Sets the first levelavailable date from.
	 *
	 * @param firstLevelavailableDateFrom
	 *            the new first levelavailable date from
	 */
	public void setFirstLevelavailableDateFrom(Date firstLevelavailableDateFrom) {
		this.firstLevelavailableDateFrom = firstLevelavailableDateFrom;
	}

	/**
	 * Gets the first levelavailable date to.
	 *
	 * @return the first levelavailable date to
	 */
	public Date getFirstLevelavailableDateTo() {
		return firstLevelavailableDateTo;
	}

	/**
	 * Sets the first levelavailable date to.
	 *
	 * @param firstLevelavailableDateTo
	 *            the new first levelavailable date to
	 */
	public void setFirstLevelavailableDateTo(Date firstLevelavailableDateTo) {
		this.firstLevelavailableDateTo = firstLevelavailableDateTo;
	}

	/**
	 * Gets the levelavailable date from.
	 *
	 * @return the levelavailable date from
	 */
	public Date getLevelavailableDateFrom() {
		return levelavailableDateFrom;
	}

	/**
	 * Sets the levelavailable date from.
	 *
	 * @param levelavailableDateFrom
	 *            the new levelavailable date from
	 */
	public void setLevelavailableDateFrom(Date levelavailableDateFrom) {
		this.levelavailableDateFrom = levelavailableDateFrom;
	}

	/**
	 * Gets the levelavailable date to.
	 *
	 * @return the levelavailable date to
	 */
	public Date getLevelavailableDateTo() {
		return levelavailableDateTo;
	}

	/**
	 * Sets the levelavailable date to.
	 *
	 * @param levelavailableDateTo
	 *            the new levelavailable date to
	 */
	public void setLevelavailableDateTo(Date levelavailableDateTo) {
		this.levelavailableDateTo = levelavailableDateTo;
	}

	/**
	 * Gets the cancelationreasons.
	 *
	 * @return the cancelationreasons
	 */
	public List<String> getCancelationreasons() {
		return cancelationreasons;
	}

	/**
	 * Sets the cancelationreasons.
	 *
	 * @param cancelationreasons
	 *            the new cancelationreasons
	 */
	public void setCancelationreasons(List<String> cancelationreasons) {
		this.cancelationreasons = cancelationreasons;
	}

	/**
	 * Gets the reason free awards.
	 *
	 * @return the reason free awards
	 */
	public List<String> getReasonFreeAwards() {
		return reasonFreeAwards;
	}

	/**
	 * Sets the reason free awards.
	 *
	 * @param reasonFreeAwards
	 *            the new reason free awards
	 */
	public void setReasonFreeAwards(List<String> reasonFreeAwards) {
		this.reasonFreeAwards = reasonFreeAwards;
	}

	/**
	 * Gets the intern.
	 *
	 * @return the intern
	 */
	public Boolean getIntern() {
		return intern;
	}

	/**
	 * Sets the intern.
	 *
	 * @param intern
	 *            the new intern
	 */
	public void setIntern(Boolean intern) {
		this.intern = intern;
	}

	/**
	 * Gets the publication date direct award from.
	 *
	 * @return the publication date direct award from
	 */
	public Date getPublicationDateDirectAwardFrom() {
		return publicationDateDirectAwardFrom;
	}

	/**
	 * Sets the publication date direct award from.
	 *
	 * @param publicationDateDirectAwardFrom
	 *            the new publication date direct award from
	 */
	public void setPublicationDateDirectAwardFrom(Date publicationDateDirectAwardFrom) {
		this.publicationDateDirectAwardFrom = publicationDateDirectAwardFrom;
	}

	/**
	 * Gets the publication date direct award to.
	 *
	 * @return the publication date direct award to
	 */
	public Date getPublicationDateDirectAwardTo() {
		return publicationDateDirectAwardTo;
	}

	/**
	 * Sets the publication date direct award to.
	 *
	 * @param publicationDateDirectAwardTo
	 *            the new publication date direct award to
	 */
	public void setPublicationDateDirectAwardTo(Date publicationDateDirectAwardTo) {
		this.publicationDateDirectAwardTo = publicationDateDirectAwardTo;
	}
}
