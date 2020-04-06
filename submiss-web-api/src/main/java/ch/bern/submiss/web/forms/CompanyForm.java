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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;

/**
 * The Class CompanyForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyForm {

  /** The id. */
  private String id;

  /** The company name. */
  private String companyName;

  /** The tenant. */
  private TenantForm tenant;

  /** The country. */
  private CountryHistoryForm country;

  /** The work types. */
  private Set<MasterListValueHistoryForm> workTypes;

  /** The add info. */
  private String addInfo;

  /** The address 1. */
  private String address1;

  /** The address 2. */
  private String address2;

  /** The post code. */
  private String postCode;

  /** The location. */
  private String location;

  /** The company tel. */
  private String companyTel;

  /** The company fax. */
  private String companyFax;

  /** The company email. */
  private String companyEmail;

  /** The company web. */
  private String companyWeb;

  /** The notes. */
  private String notes;

  /** The number of trainees. */
  private Long numberOfTrainees;

  /** The number of women. */
  private Integer numberOfWomen;

  /** The modification date. */
  private Timestamp modificationDate;

  /** The number of men. */
  private Integer numberOfMen;

  /** The logib date. */
  private Timestamp logibDate;

  /** The logib kmu date. */
  private Timestamp logibKmuDate;

  /** The consult admin. */
  private Boolean consultAdmin;

  /** The consult kaio. */
  private Boolean consultKaio;

  /** The new vat id. */
  private String newVatId;

  /** The origin indication. */
  private String originIndication;

  /** The ilo. */
  private MasterListValueHistoryForm ilo;

  /** The ilo date. */
  private Timestamp iloDate;

  /** The archived. */
  private Boolean archived;

  /** The note admin. */
  private String noteAdmin;

  /** The proof status. */
  private Integer proofStatus;

  /** The tlp. */
  private Boolean tlp;

  /** The log ib. */
  private Boolean logIb;

  /** The log ib ARGIB. */
  private Boolean logIbARGIB;

  /** The main company. */
  private CompanyDTO mainCompany;

  /** The branches. */
  private List<CompanyForm> branches;

  /** The mod user. */
  private String modUser;

  /** The mod user name. */
  private String modUserName;

  /** The mod user last name. */
  private String modUserLastName;

  /** The mod user tenant. */
  private String modUserTenant;

  /** The create on. */
  private Timestamp createOn;

  /** The modified on. */
  private Timestamp modifiedOn;

  /** The can be deleted. */
  private Boolean canBeDeleted;

  /** The proof doc mod on. */
  private Date proofDocModOn;

  /** The proof doc mod by. */
  private TenantDTO proofDocModBy;

  /** The proof doc submit date. */
  private Date proofDocSubmitDate;

  /** The cert doc expiration date. */
  private Date certDocExpirationDate;

  /** The certificate date. */
  private Date certificateDate;

  /** The create by. */
  private String createdBy;

  /** The created on. */
  private Timestamp createdOn;
  
  /** The version. */
  private Long version;
  
  /** The is proof provided. */
  private Boolean isProofProvided;

  /** The fifty plus colleagues. */
  private Integer fiftyPlusColleagues;

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
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

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
   * @param companyName the new company name
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

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
   * Gets the country.
   *
   * @return the country
   */
  public CountryHistoryForm getCountry() {
    return country;
  }

  /**
   * Sets the country.
   *
   * @param country the new country
   */
  public void setCountry(CountryHistoryForm country) {
    this.country = country;
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
   * @param workTypes the new work types
   */
  public void setWorkTypes(Set<MasterListValueHistoryForm> workTypes) {
    this.workTypes = workTypes;
  }

  /**
   * Gets the adds the info.
   *
   * @return the adds the info
   */
  public String getAddInfo() {
    return addInfo;
  }

  /**
   * Sets the adds the info.
   *
   * @param addInfo the new adds the info
   */
  public void setAddInfo(String addInfo) {
    this.addInfo = addInfo;
  }

  /**
   * Gets the address 1.
   *
   * @return the address 1
   */
  public String getAddress1() {
    return address1;
  }

  /**
   * Sets the address 1.
   *
   * @param address1 the new address 1
   */
  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  /**
   * Gets the address 2.
   *
   * @return the address 2
   */
  public String getAddress2() {
    return address2;
  }

  /**
   * Sets the address 2.
   *
   * @param address2 the new address 2
   */
  public void setAddress2(String address2) {
    this.address2 = address2;
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
   * @param postCode the new post code
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
   * @param location the new location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Gets the company tel.
   *
   * @return the company tel
   */
  public String getCompanyTel() {
    return companyTel;
  }

  /**
   * Sets the company tel.
   *
   * @param companyTel the new company tel
   */
  public void setCompanyTel(String companyTel) {
    this.companyTel = companyTel;
  }

  /**
   * Gets the company fax.
   *
   * @return the company fax
   */
  public String getCompanyFax() {
    return companyFax;
  }

  /**
   * Sets the company fax.
   *
   * @param companyFax the new company fax
   */
  public void setCompanyFax(String companyFax) {
    this.companyFax = companyFax;
  }

  /**
   * Gets the company email.
   *
   * @return the company email
   */
  public String getCompanyEmail() {
    return companyEmail;
  }

  /**
   * Sets the company email.
   *
   * @param companyEmail the new company email
   */
  public void setCompanyEmail(String companyEmail) {
    this.companyEmail = companyEmail;
  }

  /**
   * Gets the company web.
   *
   * @return the company web
   */
  public String getCompanyWeb() {
    return companyWeb;
  }

  /**
   * Sets the company web.
   *
   * @param companyWeb the new company web
   */
  public void setCompanyWeb(String companyWeb) {
    this.companyWeb = companyWeb;
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
   * @param notes the new notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Gets the number of trainees.
   *
   * @return the number of trainees
   */
  public Long getNumberOfTrainees() {
    return numberOfTrainees;
  }

  /**
   * Sets the number of trainees.
   *
   * @param numberOfTrainees the new number of trainees
   */
  public void setNumberOfTrainees(Long numberOfTrainees) {
    this.numberOfTrainees = numberOfTrainees;
  }

  /**
   * Gets the number of women.
   *
   * @return the number of women
   */
  public Integer getNumberOfWomen() {
    return numberOfWomen;
  }

  /**
   * Sets the number of women.
   *
   * @param numberOfWomen the new number of women
   */
  public void setNumberOfWomen(Integer numberOfWomen) {
    this.numberOfWomen = numberOfWomen;
  }

  /**
   * Gets the modification date.
   *
   * @return the modification date
   */
  public Timestamp getModificationDate() {
    return modificationDate;
  }

  /**
   * Sets the modification date.
   *
   * @param modificationDate the new modification date
   */
  public void setModificationDate(Timestamp modificationDate) {
    this.modificationDate = modificationDate;
  }

  /**
   * Gets the number of men.
   *
   * @return the number of men
   */
  public Integer getNumberOfMen() {
    return numberOfMen;
  }

  /**
   * Sets the number of men.
   *
   * @param numberOfMen the new number of men
   */
  public void setNumberOfMen(Integer numberOfMen) {
    this.numberOfMen = numberOfMen;
  }

  /**
   * Gets the logib date.
   *
   * @return the logib date
   */
  public Timestamp getLogibDate() {
    return logibDate;
  }

  /**
   * Sets the logib date.
   *
   * @param logibDate the new logib date
   */
  public void setLogibDate(Timestamp logibDate) {
    this.logibDate = logibDate;
  }

  /**
   * Gets the logib kmu date.
   *
   * @return the logib kmu date
   */
  public Timestamp getLogibKmuDate() {
    return logibKmuDate;
  }

  /**
   * Sets the logib kmu date.
   *
   * @param logibKmuDate the new logib kmu date
   */
  public void setLogibKmuDate(Timestamp logibKmuDate) {
    this.logibKmuDate = logibKmuDate;
  }

  /**
   * Gets the consult admin.
   *
   * @return the consult admin
   */
  public Boolean getConsultAdmin() {
    return consultAdmin;
  }

  /**
   * Sets the consult admin.
   *
   * @param consultAdmin the new consult admin
   */
  public void setConsultAdmin(Boolean consultAdmin) {
    this.consultAdmin = consultAdmin;
  }

  /**
   * Gets the consult kaio.
   *
   * @return the consult kaio
   */
  public Boolean getConsultKaio() {
    return consultKaio;
  }

  /**
   * Sets the consult kaio.
   *
   * @param consultKaio the new consult kaio
   */
  public void setConsultKaio(Boolean consultKaio) {
    this.consultKaio = consultKaio;
  }

  /**
   * Gets the new vat id.
   *
   * @return the new vat id
   */
  public String getNewVatId() {
    return newVatId;
  }

  /**
   * Sets the new vat id.
   *
   * @param newVatId the new new vat id
   */
  public void setNewVatId(String newVatId) {
    this.newVatId = newVatId;
  }

  /**
   * Gets the origin indication.
   *
   * @return the origin indication
   */
  public String getOriginIndication() {
    return originIndication;
  }

  /**
   * Sets the origin indication.
   *
   * @param originIndication the new origin indication
   */
  public void setOriginIndication(String originIndication) {
    this.originIndication = originIndication;
  }

  /**
   * Gets the ilo.
   *
   * @return the ilo
   */
  public MasterListValueHistoryForm getIlo() {
    return ilo;
  }

  /**
   * Sets the ilo.
   *
   * @param ilo the new ilo
   */
  public void setIlo(MasterListValueHistoryForm ilo) {
    this.ilo = ilo;
  }

  /**
   * Gets the ilo date.
   *
   * @return the ilo date
   */
  public Timestamp getIloDate() {
    return iloDate;
  }

  /**
   * Sets the ilo date.
   *
   * @param iloDate the new ilo date
   */
  public void setIloDate(Timestamp iloDate) {
    this.iloDate = iloDate;
  }

  /**
   * Gets the archived.
   *
   * @return the archived
   */
  public Boolean getArchived() {
    return archived;
  }

  /**
   * Sets the archived.
   *
   * @param archived the new archived
   */
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  /**
   * Gets the note admin.
   *
   * @return the note admin
   */
  public String getNoteAdmin() {
    return noteAdmin;
  }

  /**
   * Sets the note admin.
   *
   * @param noteAdmin the new note admin
   */
  public void setNoteAdmin(String noteAdmin) {
    this.noteAdmin = noteAdmin;
  }

  /**
   * Gets the tlp.
   *
   * @return the tlp
   */
  public Boolean getTlp() {
    return tlp;
  }

  /**
   * Sets the tlp.
   *
   * @param tlp the new tlp
   */
  public void setTlp(Boolean tlp) {
    this.tlp = tlp;
  }

  /**
   * Gets the proof status.
   *
   * @return the proof status
   */
  public Integer getProofStatus() {
    return proofStatus;
  }

  /**
   * Sets the proof status.
   *
   * @param proofStatus the new proof status
   */
  public void setProofStatus(Integer proofStatus) {
    this.proofStatus = proofStatus;
  }

  /**
   * Gets the main company.
   *
   * @return the main company
   */
  public CompanyDTO getMainCompany() {
    return mainCompany;
  }

  /**
   * Sets the main company.
   *
   * @param mainCompany the new main company
   */
  public void setMainCompany(CompanyDTO mainCompany) {
    this.mainCompany = mainCompany;
  }

  /**
   * Gets the branches.
   *
   * @return the branches
   */
  public List<CompanyForm> getBranches() {
    return branches;
  }

  /**
   * Sets the branches.
   *
   * @param branches the new branches
   */
  public void setBranches(List<CompanyForm> branches) {
    this.branches = branches;
  }

  /**
   * Gets the mod user.
   *
   * @return the mod user
   */
  public String getModUser() {
    return modUser;
  }

  /**
   * Sets the mod user.
   *
   * @param modUser the new mod user
   */
  public void setModUser(String modUser) {
    this.modUser = modUser;
  }

  /**
   * Gets the mod user name.
   *
   * @return the mod user name
   */
  public String getModUserName() {
    return modUserName;
  }

  /**
   * Sets the mod user name.
   *
   * @param modUserName the new mod user name
   */
  public void setModUserName(String modUserName) {
    this.modUserName = modUserName;
  }

  /**
   * Gets the mod user last name.
   *
   * @return the mod user last name
   */
  public String getModUserLastName() {
    return modUserLastName;
  }

  /**
   * Sets the mod user last name.
   *
   * @param modUserLastName the new mod user last name
   */
  public void setModUserLastName(String modUserLastName) {
    this.modUserLastName = modUserLastName;
  }

  /**
   * Gets the mod user tenant.
   *
   * @return the mod user tenant
   */
  public String getModUserTenant() {
    return modUserTenant;
  }

  /**
   * Sets the mod user tenant.
   *
   * @param modUserTenant the new mod user tenant
   */
  public void setModUserTenant(String modUserTenant) {
    this.modUserTenant = modUserTenant;
  }

  /**
   * Gets the log ib.
   *
   * @return the log ib
   */
  public Boolean getLogIb() {
    return logIb;
  }

  /**
   * Sets the log ib.
   *
   * @param logIb the new log ib
   */
  public void setLogIb(Boolean logIb) {
    this.logIb = logIb;
  }

  /**
   * Gets the log ib ARGIB.
   *
   * @return the log ib ARGIB
   */
  public Boolean getLogIbARGIB() {
    return logIbARGIB;
  }

  /**
   * Sets the log ib ARGIB.
   *
   * @param logIbARGIB the new log ib ARGIB
   */
  public void setLogIbARGIB(Boolean logIbARGIB) {
    this.logIbARGIB = logIbARGIB;
  }

  /**
   * Gets the creates the on.
   *
   * @return the creates the on
   */
  public Timestamp getCreateOn() {
    return createOn;
  }

  /**
   * Sets the creates the on.
   *
   * @param createOn the new creates the on
   */
  public void setCreateOn(Timestamp createOn) {
    this.createOn = createOn;
  }

  /**
   * Gets the modified on.
   *
   * @return the modified on
   */
  public Timestamp getModifiedOn() {
    return modifiedOn;
  }

  /**
   * Sets the modified on.
   *
   * @param modifiedOn the new modified on
   */
  public void setModifiedOn(Timestamp modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  /**
   * Gets the can be deleted.
   *
   * @return the can be deleted
   */
  public Boolean getCanBeDeleted() {
    return canBeDeleted;
  }

  /**
   * Sets the can be deleted.
   *
   * @param canBeDeleted the new can be deleted
   */
  public void setCanBeDeleted(Boolean canBeDeleted) {
    this.canBeDeleted = canBeDeleted;
  }

  /**
   * Gets the proof doc mod on.
   *
   * @return the proof doc mod on
   */
  public Date getProofDocModOn() {
    return proofDocModOn;
  }

  /**
   * Sets the proof doc mod on.
   *
   * @param proofDocModOn the new proof doc mod on
   */
  public void setProofDocModOn(Date proofDocModOn) {
    this.proofDocModOn = proofDocModOn;
  }

  /**
   * Gets the proof doc mod by.
   *
   * @return the proof doc mod by
   */
  public TenantDTO getProofDocModBy() {
    return proofDocModBy;
  }

  /**
   * Sets the proof doc mod by.
   *
   * @param proofDocModBy the new proof doc mod by
   */
  public void setProofDocModBy(TenantDTO proofDocModBy) {
    this.proofDocModBy = proofDocModBy;
  }

  /**
   * Gets the proof doc submit date.
   *
   * @return the proof doc submit date
   */
  public Date getProofDocSubmitDate() {
    return proofDocSubmitDate;
  }

  /**
   * Sets the proof doc submit date.
   *
   * @param proofDocSubmitDate the new proof doc submit date
   */
  public void setProofDocSubmitDate(Date proofDocSubmitDate) {
    this.proofDocSubmitDate = proofDocSubmitDate;
  }

  /**
   * Gets the cert doc expiration date.
   *
   * @return the cert doc expiration date
   */
  public Date getCertDocExpirationDate() {
    return certDocExpirationDate;
  }

  /**
   * Sets the cert doc expiration date.
   *
   * @param certDocExpirationDate the new cert doc expiration date
   */
  public void setCertDocExpirationDate(Date certDocExpirationDate) {
    this.certDocExpirationDate = certDocExpirationDate;
  }

  /**
   * Gets the certificate date.
   *
   * @return the certificate date
   */
  public Date getCertificateDate() {
    return certificateDate;
  }

  /**
   * Sets the certificate date.
   *
   * @param certificateDate the new certificate date
   */
  public void setCertificateDate(Date certificateDate) {
    this.certificateDate = certificateDate;
  }

  /**
   * Gets the created by.
   *
   * @return the created by
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the created by.
   *
   * @param createdBy the new created by
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public Timestamp getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  public Long getVersion() {
    return version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  public void setVersion(Long version) {
    this.version = version;
  }

  /**
   * Gets the checks if is proof provided.
   *
   * @return the checks if is proof provided
   */
  public Boolean getIsProofProvided() {
    return isProofProvided;
  }

  /**
   * Sets the checks if is proof provided.
   *
   * @param isProofProvided the new checks if is proof provided
   */
  public void setIsProofProvided(Boolean isProofProvided) {
    this.isProofProvided = isProofProvided;
  }

  /**
   * Gets the fifty plus colleagues.
   *
   * @return the fifty plus colleagues
   */
  public Integer getFiftyPlusColleagues() {
    return fiftyPlusColleagues;
  }

  /**
   * Sets the fifty plus colleagues.
   *
   * @param fiftyPlusColleagues the new fifty plus colleagues
   */
  public void setFiftyPlusColleagues(Integer fiftyPlusColleagues) {
    this.fiftyPlusColleagues = fiftyPlusColleagues;
  }

}
