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

package ch.bern.submiss.services.impl.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The Class DirectorateHistoryEntity.
 */
@Entity
@Table(name = "SUB_DIRECTORATE_HISTORY")
public class DirectorateHistoryEntity extends AbstractStammdatenEntity {

	/** The active. */
	@Column(name = "IS_ACTIVE")
	private boolean active;

	/** The short name. */
	@Column(name = "SHORT_NAME")
	private String shortName;

	/** The directorate id. */
	@OneToOne
	@JoinColumn(name = "FK_DIRECTORATE")
	private DirectorateEntity directorateId;

	/** The tenant. */
	@ManyToOne
	@JoinColumn(name = "FK_TENANT")
	private TenantEntity tenant;

	/** The address. */
	@Column(name = "ADDRESS")
	private String address;

	/** The telephone. */
	@Column(name = "TEL")
	private String telephone;

	/** The email. */
	@Column(name = "EMAIL")
	private String email;

	/** The website. */
	@Column(name = "WEBSITE")
	private String website;

	/** The post code. */
	@Column(name = "POST_CODE")
	private String postCode;

	/** The location. */
	@Column(name = "LOCATION")
	private String location;

	/** The Fax. */
	@Column(name = "FAX")
	private String fax;

	/** The from date. */
	@Column(name = "FROM_DATE")
    private Timestamp fromDate;

	/** The to date. */
	@Column(name = "TO_DATE")
    private Timestamp toDate;

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active
	 *            the new active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the short name.
	 *
	 * @return the short name
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Sets the short name.
	 *
	 * @param shortName
	 *            the new short name
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Gets the directorate id.
	 *
	 * @return the directorate id
	 */
	public DirectorateEntity getDirectorateId() {
		return directorateId;
	}

	/**
	 * Sets the directorate id.
	 *
	 * @param directorateId
	 *            the new directorate id
	 */
	public void setDirectorateId(DirectorateEntity directorateId) {
		this.directorateId = directorateId;
	}

	/**
	 * Gets the tenant.
	 *
	 * @return the tenant
	 */
	public TenantEntity getTenant() {
		return tenant;
	}

	/**
	 * Sets the tenant.
	 *
	 * @param tenant
	 *            the new tenant
	 */
	public void setTenant(TenantEntity tenant) {
		this.tenant = tenant;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address
	 *            the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the telephone.
	 *
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * Sets the telephone.
	 *
	 * @param telephone
	 *            the new telephone
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email
	 *            the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the website.
	 *
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the website.
	 *
	 * @param website
	 *            the new website
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * Gets the post code.
	 *
	 * @return the post code
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * Sets the post code.
	 *
	 * @param postCode
	 *            the new post code
	 */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location
	 *            the new location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Gets the fax.
	 *
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * Sets the fax.
	 *
	 * @param fax
	 *            the new fax
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

  /**
   * Gets the from date.
   *
   * @return the from date
   */
  public Timestamp getFromDate() {
    return fromDate;
  }

  /**
   * Sets the from date.
   *
   * @param fromDate the new from date
   */
  public void setFromDate(Timestamp fromDate) {
    this.fromDate = fromDate;
  }

  /**
   * Gets the to date.
   *
   * @return the to date
   */
  public Timestamp getToDate() {
    return toDate;
  }

  /**
   * Sets the to date.
   *
   * @param toDate the new to date
   */
  public void setToDate(Timestamp toDate) {
    this.toDate = toDate;
  }
}
