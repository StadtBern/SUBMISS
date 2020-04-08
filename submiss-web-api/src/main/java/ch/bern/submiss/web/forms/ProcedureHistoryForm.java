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

import ch.bern.submiss.services.api.constants.Process;

/**
 * The Class ProcedureHistoryForm.
 */
public class ProcedureHistoryForm extends AbstractForm {

	/** The value. */
	private String value;

	/** The procedure. */
	private ProcedureForm procedure;

	/** The process type. */
	private MasterListValueForm processType;

	/** The tenant. */
	private TenantForm tenant;

	/** The process. */
	private Process process;

	/**
	 * Gets the tenant.
	 *
	 * @return the tenant
	 */
	public TenantForm getTenant() {
		return tenant;
	}

	/**
	 * Sets the tenant.
	 *
	 * @param tenant the new tenant
	 */
	public void setTenant(TenantForm tenant) {
		this.tenant = tenant;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the process type.
	 *
	 * @return the process type
	 */
	public MasterListValueForm getProcessType() {
		return processType;
	}

	/**
	 * Sets the process type.
	 *
	 * @param processType the new process type
	 */
	public void setProcessType(MasterListValueForm processType) {
		this.processType = processType;
	}

	/**
	 * Gets the procedure.
	 *
	 * @return the procedure
	 */
	public ProcedureForm getProcedure() {
		return procedure;
	}

	/**
	 * Sets the procedure.
	 *
	 * @param procedure the new procedure
	 */
	public void setProcedure(ProcedureForm procedure) {
		this.procedure = procedure;
	}

	/**
	 * Gets the process.
	 *
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * Sets the process.
	 *
	 * @param process the new process
	 */
	public void setProcess(Process process) {
		this.process = process;
	}

}
