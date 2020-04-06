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
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class ReportBaseForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportBaseForm {

	/** The start date - Von. */
	private Date startDate;

	/** The end date - Bis. */
	private Date endDate;

	/** The objects - Objekt. */
	private List<String> objects;

	/** The projects - Projektname. */
	private List<String> projects;

	/** The work types - Arbeitsgattung. */
	private Set<MasterListValueHistoryForm> workTypes;

	/** The procedures - Verfahren. */
	private List<String> procedures;

	/** The directorates  - Direktion */
	private List<String> directorates;

	/** The departments - Abteilung. */
	private List<String> departments;

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 *
	 * @param startDate
	 *            the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 *
	 * @param endDate
	 *            the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the objects.
	 *
	 * @return the objects
	 */
	public List<String> getObjects() {
		return objects;
	}

	/**
	 * Sets the objects.
	 *
	 * @param objects
	 *            the new objects
	 */
	public void setObjects(List<String> objects) {
		this.objects = objects;
	}

	/**
	 * Gets the projects.
	 *
	 * @return the projects
	 */
	public List<String> getProjects() {
		return projects;
	}

	/**
	 * Sets the projects.
	 *
	 * @param projects
	 *            the new projects
	 */
	public void setProjects(List<String> projects) {
		this.projects = projects;
	}

	/**
	 * Gets the work types.
	 *
	 * @return the work types
	 */
	public Set<MasterListValueHistoryForm> getWorkTypes() {
		return workTypes;
	}

	/**
	 * Sets the work types.
	 *
	 * @param workTypes
	 *            the new work types
	 */
	public void setWorkTypes(Set<MasterListValueHistoryForm> workTypes) {
		this.workTypes = workTypes;
	}

	/**
	 * Gets the procedures.
	 *
	 * @return the procedures
	 */
	public List<String> getProcedures() {
		return procedures;
	}

	/**
	 * Sets the procedures.
	 *
	 * @param procedures
	 *            the new procedures
	 */
	public void setProcedures(List<String> procedures) {
		this.procedures = procedures;
	}

	/**
	 * Gets the directorates.
	 *
	 * @return the directorates
	 */
	public List<String> getDirectorates() {
		return directorates;
	}

	/**
	 * Sets the directorates.
	 *
	 * @param directorates
	 *            the new directorates
	 */
	public void setDirectorates(List<String> directorates) {
		this.directorates = directorates;
	}

	/**
	 * Gets the departments.
	 *
	 * @return the departments
	 */
	public List<String> getDepartments() {
		return departments;
	}

	/**
	 * Sets the departments.
	 *
	 * @param departments
	 *            the new departments
	 */
	public void setDepartments(List<String> departments) {
		this.departments = departments;
	}
}
