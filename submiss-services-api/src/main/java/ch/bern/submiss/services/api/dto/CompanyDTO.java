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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonView;
import ch.bern.submiss.services.api.util.View;

/**
 * The Class CompanyDTO.
 */
public class CompanyDTO {

  /** The id. */
  @JsonView(View.Public.class)
  private String id;

  /** The company name. */
  @JsonView(View.Public.class)
  private String companyName;

  /** The tenant. */
  @JsonView(View.Internal.class)
  private TenantDTO tenant;

  /** The proof ordered on. */
  @JsonView(View.Internal.class)
  private String proofOrderedOn;

  /** The country. */
  @JsonView(View.Internal.class)
  private CountryHistoryDTO country;

  /** The work types. */
  @JsonView(View.Public.class)
  private Set<MasterListValueHistoryDTO> workTypes;

  /** The add info. */
  @JsonView(View.Internal.class)
  private String addInfo;

  /** The address 1. */
  @JsonView(View.Internal.class)
  private String address1;

  /** The address 2. */
  @JsonView(View.Internal.class)
  private String address2;

  /** The post code. */
  @JsonView(View.Public.class)
  private String postCode;

  /** The location. */
  @JsonView(View.Public.class)
  private String location;

  /** The company tel. */
  @JsonView(View.Public.class)
  private String companyTel;

  /** The company fax. */
  @JsonView(View.Internal.class)
  private String companyFax;

  /** The company email. */
  @JsonView(View.Internal.class)
  private String companyEmail;

  /** The company web. */
  @JsonView(View.Internal.class)
  private String companyWeb;

  /** The proof status. */
  @JsonView(View.Public.class)
  private Integer proofStatus;

  /** The notes. */
  @JsonView(View.Internal.class)
  private String notes;

  /** The modification date. */
  @JsonView(View.Internal.class)
  private Timestamp modificationDate;

  /** The number of trainees. */
  @JsonView(View.Internal.class)
  private Long numberOfTrainees;

  /** The number of women. */
  @JsonView(View.Internal.class)
  private Integer numberOfWomen;

  /** The number of men. */
  @JsonView(View.Internal.class)
  private Integer numberOfMen;

  /** The logib date. */
  @JsonView(View.Internal.class)
  private Timestamp logibDate;

  /** The consult admin. */
  @JsonView(View.Internal.class)
  private Boolean consultAdmin;

  /** The consult kaio. */
  @JsonView(View.Internal.class)
  private Boolean consultKaio;

  /** The new vat id. */
  @JsonView(View.Internal.class)
  private String newVatId;

  /** The origin indication. */
  @JsonView(View.Internal.class)
  private String originIndication;

  /** The logib kmu date. */
  @JsonView(View.Internal.class)
  private Timestamp logibKmuDate;

  /** The ilo. */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO ilo;

  /** The ilo date. */
  @JsonView(View.Internal.class)
  private Timestamp iloDate;

  /** The archived. */
  @JsonView(View.Internal.class)
  private Boolean archived;

  /** The note admin. */
  @JsonView(View.Internal.class)
  private String noteAdmin;

  /** The apprentice factor. */
  @JsonView(View.Public.class)
  private Double apprenticeFactor;

  /** The log ib. */
  @JsonView(View.Public.class)
  private Boolean logIb;

  /** The log ib ARGIB. */
  @JsonView(View.Public.class)
  private Boolean logIbARGIB;

  /** The certificate date. */
  @JsonView(View.Public.class)
  private Date certificateDate;

  /** The branches. */
  @JsonView(View.Internal.class)
  private List<CompanyDTO> branches;

  /** The company proofs. */
  @JsonView(View.Internal.class)
  private List<CompanyProofDTO> companyProofs;

  /** The main company. */
  @JsonView(View.Internal.class)
  private CompanyDTO mainCompany;

  /** The mod user. */
  @JsonView(View.Internal.class)
  private String modUser;

  /** The mod user name. */
  @JsonView(View.Internal.class)
  private String modUserName;

  /** The mod user last name. */
  @JsonView(View.Internal.class)
  private String modUserLastName;

  /** The mod user tenant. */
  @JsonView(View.Internal.class)
  private String modUserTenant;

  /** The tlp. */
  @JsonView(View.Internal.class)
  private Boolean tlp;

  /** The create on. */
  @JsonView(View.Internal.class)
  private Timestamp createOn;

  /** The modified on. */
  @JsonView(View.Internal.class)
  private Timestamp modifiedOn;

  /** The can be deleted. */
  @JsonView(View.Internal.class)
  private Boolean canBeDeleted;

  /** The is proof provided. */
  // Calculate the proof status in the current submission.
  @JsonView(View.Internal.class)
  private Boolean isProofProvided;

  /** The proof doc mod on. */
  @JsonView(View.Internal.class)
  private Date proofDocModOn;

  /** The proof doc mod by. */
  @JsonView(View.Internal.class)
  private TenantDTO proofDocModBy;

  /** The proof doc submit date. */
  @JsonView(View.Internal.class)
  private Date proofDocSubmitDate;

  /** The cert doc expiration date. */
  @JsonView(View.Internal.class)
  private Date certDocExpirationDate;

  /** The ProofStatus that is shown to user: Admin of main tenant. */
  @JsonView(View.Public.class)
  private Integer proofStatusFabe;

  /** The created by. */
  @JsonView(View.Internal.class)
  private String createdBy;

  @JsonView(View.Internal.class)
  private Long version;

  /** The fifty plus colleagues. */
  @JsonView(View.Internal.class)
  private Integer fiftyPlusColleagues;

  /** The fifty plus factor. */
  @JsonView(View.Public.class)
  private Double fiftyPlusFactor;

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
  public TenantDTO getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantDTO tenant) {
    this.tenant = tenant;
  }

  /**
   * Gets the proof ordered on.
   *
   * @return the proof ordered on
   */
  public String getProofOrderedOn() {
    return proofOrderedOn;
  }

  /**
   * Sets the proof ordered on.
   *
   * @param proofOrderedOn the new proof ordered on
   */
  public void setProofOrderedOn(String proofOrderedOn) {
    this.proofOrderedOn = proofOrderedOn;
  }

  /**
   * Gets the country.
   *
   * @return the country
   */
  public CountryHistoryDTO getCountry() {
    return country;
  }

  /**
   * Sets the country.
   *
   * @param country the new country
   */
  public void setCountry(CountryHistoryDTO country) {
    this.country = country;
  }

  /**
   * Gets the work types.
   *
   * @return the work types
   */
  public Set<MasterListValueHistoryDTO> getWorkTypes() {
    return workTypes;
  }

  /**
   * Sets the work types.
   *
   * @param workTypes the new work types
   */
  public void setWorkTypes(Set<MasterListValueHistoryDTO> workTypes) {
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
   * Gets the ilo.
   *
   * @return the ilo
   */
  public MasterListValueHistoryDTO getIlo() {
    return ilo;
  }

  /**
   * Sets the ilo.
   *
   * @param ilo the new ilo
   */
  public void setIlo(MasterListValueHistoryDTO ilo) {
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
   * Gets the apprentice factor.
   *
   * @return the apprentice factor
   */
  public Double getApprenticeFactor() {
    return apprenticeFactor;
  }

  /**
   * Sets the apprentice factor.
   *
   * @param apprenticeFactor the new apprentice factor
   */
  public void setApprenticeFactor(Double apprenticeFactor) {
    this.apprenticeFactor = apprenticeFactor;
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
   * Gets the branches.
   *
   * @return the branches
   */
  public List<CompanyDTO> getBranches() {
    return branches;
  }

  /**
   * Sets the branches.
   *
   * @param branches the new branches
   */
  public void setBranches(List<CompanyDTO> branches) {
    this.branches = branches;
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
   * Gets the company proofs.
   *
   * @return the company proofs
   */
  public List<CompanyProofDTO> getCompanyProofs() {
    return companyProofs;
  }

  /**
   * Sets the company proofs.
   *
   * @param companyProofs the new company proofs
   */
  public void setCompanyProofs(List<CompanyProofDTO> companyProofs) {
    this.companyProofs = companyProofs;
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
   * Gets the proof status fabe.
   *
   * @return the proof status fabe
   */
  public Integer getProofStatusFabe() {
    return proofStatusFabe;
  }

  /**
   * Sets the proof status fabe.
   *
   * @param proofStatusFabe the new proof status fabe
   */
  public void setProofStatusFabe(Integer proofStatusFabe) {
    this.proofStatusFabe = proofStatusFabe;
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

  /**
   * Gets the fifty plus factor.
   *
   * @return the fifty plus factor
   */
  public Double getFiftyPlusFactor() {
    return fiftyPlusFactor;
  }

  /**
   * Sets the fifty plus factor.
   *
   * @param fiftyPlusFactor the new fifty plus factor
   */
  public void setFiftyPlusFactor(Double fiftyPlusFactor) {
    this.fiftyPlusFactor = fiftyPlusFactor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CompanyDTO [id=" + id + ", companyName=" + companyName + ", tenant=" + tenant
        + ", proofOrderedOn=" + proofOrderedOn + ",  addInfo=" + addInfo + ", address1=" + address1
        + ", address2=" + address2 + ", postCode=" + postCode + ", location=" + location
        + ", companyTel=" + companyTel + ", companyFax=" + companyFax + ", companyEmail="
        + companyEmail + ", companyWeb=" + companyWeb + ", proofStatus=" + proofStatus + ", notes="
        + notes + ", modificationDate=" + modificationDate + ", numberOfTrainees="
        + numberOfTrainees + ", numberOfWomen=" + numberOfWomen + ", numberOfMen=" + numberOfMen
        + ", logibDate=" + logibDate + ", consultAdmin=" + consultAdmin + ", consultKaio="
        + consultKaio + ", newVatId=" + newVatId + ", originIndication=" + originIndication
        + ", logibKmuDate=" + logibKmuDate + ",  iloDate=" + iloDate + ", archived=" + archived
        + ", noteAdmin=" + noteAdmin + ", apprenticeFactor=" + apprenticeFactor + ", logIb=" + logIb
        + ", logIbARGIB=" + logIbARGIB + ", certificateDate=" + certificateDate + ",   modUser="
        + modUser + ", modUserName=" + modUserName + ", modUserLastName=" + modUserLastName
        + ", modUserTenant=" + modUserTenant + ", tlp=" + tlp + ", createOn=" + createOn
        + ", modifiedOn=" + modifiedOn + ", canBeDeleted=" + canBeDeleted + ", isProofProvided="
        + isProofProvided + ", proofDocModOn=" + proofDocModOn + ", proofDocSubmitDate="
        + proofDocSubmitDate + ", certDocExpirationDate=" + certDocExpirationDate
        + ", proofStatusFabe=" + proofStatusFabe + ", createdBy=" + createdBy + "]";
  }


}
