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

/**
 * The Class SignatureDTO.
 */
public class SignatureDTO {

	/** The id. */
	String id;

	/** The department. */
	DepartmentDTO department;

	/** The directorate. */
	DirectorateDTO directorate;

	/** The department history. */
	DepartmentHistoryDTO departmentHistory;

	/** The directorate history. */
	DirectorateHistoryDTO directorateHistory;

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
	 * Gets the department.
	 *
	 * @return the department
	 */
	public DepartmentDTO getDepartment() {
		return department;
	}

	/**
	 * Sets the department.
	 *
	 * @param department
	 *            the new department
	 */
	public void setDepartment(DepartmentDTO department) {
		this.department = department;
	}

	/**
	 * Gets the directorate.
	 *
	 * @return the directorate
	 */
	public DirectorateDTO getDirectorate() {
		return directorate;
	}

	/**
	 * Sets the directorate.
	 *
	 * @param directorate
	 *            the new directorate
	 */
	public void setDirectorate(DirectorateDTO directorate) {
		this.directorate = directorate;
	}

	/**
	 * Gets the department history.
	 *
	 * @return the department history
	 */
	public DepartmentHistoryDTO getDepartmentHistory() {
		return departmentHistory;
	}

	/**
	 * Sets the department history.
	 *
	 * @param departmentHistory
	 *            the new department history
	 */
	public void setDepartmentHistory(DepartmentHistoryDTO departmentHistory) {
		this.departmentHistory = departmentHistory;
	}

	/**
	 * Gets the directorate history.
	 *
	 * @return the directorate history
	 */
	public DirectorateHistoryDTO getDirectorateHistory() {
		return directorateHistory;
	}

	/**
	 * Sets the directorate history.
	 *
	 * @param directorateHistory
	 *            the new directorate history
	 */
	public void setDirectorateHistory(DirectorateHistoryDTO directorateHistory) {
		this.directorateHistory = directorateHistory;
	}

}
