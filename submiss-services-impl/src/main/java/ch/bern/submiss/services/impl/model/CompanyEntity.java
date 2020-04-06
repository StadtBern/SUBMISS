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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class CompanyEntity.
 */
@Entity
@Table(name = "SUB_COMPANY")
public class CompanyEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  /** The work types. */
  @ManyToMany
  @JoinTable(name = "SUB_COMPANY_WORK_TYPE", joinColumns = {@JoinColumn(name = "FK_COMPANY")},
      inverseJoinColumns = {@JoinColumn(name = "FK_WORK_TYPE")})
  private Set<MasterListValueEntity> workTypes;

  /** The company name. */
  @Column(name = "COMPANY_NAME")
  private String companyName;

  /** The tenant. */
  @ManyToOne
  @JoinColumn(name = "PROOF_ORDERED_BY")
  private TenantEntity tenant;

  /** The proof ordered on. */
  @Column(name = "PROOF_ORDERED_ON")
  private String proofOrderedOn;

  /** The apprentice factor. */
  @Formula(value = " round(NUMBER_OF_TRAINEES / (NUMBER_OF_WOMEN + NUMBER_OF_MEN),3)")
  private Double apprenticeFactor;

  /** The log ib. */
  @Formula(
      value = "(SELECT SUB_COMPANY.NUMBER_OF_MEN + SUB_COMPANY.NUMBER_OF_WOMEN >= SUB_LOGIB_HISTORY.WORKER_NUMBER AND SUB_COMPANY.NUMBER_OF_MEN >= SUB_LOGIB_HISTORY.MEN_NUMBER AND SUB_COMPANY.NUMBER_OF_WOMEN >= SUB_LOGIB_HISTORY.WOMEN_NUMBER FROM SUB_COMPANY INNER JOIN SUB_LOGIB_HISTORY WHERE SUB_LOGIB_HISTORY.NAME = 'Logib' AND SUB_COMPANY.ID=id AND SUB_LOGIB_HISTORY.TO_DATE IS NULL)")
  private Integer logIb;

  /** The log ib ARGIB. */
  @Formula(
      value = "(SELECT SUB_COMPANY.NUMBER_OF_MEN + SUB_COMPANY.NUMBER_OF_WOMEN >= SUB_LOGIB_HISTORY.WORKER_NUMBER AND SUB_COMPANY.NUMBER_OF_MEN >= SUB_LOGIB_HISTORY.MEN_NUMBER AND SUB_COMPANY.NUMBER_OF_WOMEN >= SUB_LOGIB_HISTORY.WOMEN_NUMBER AND !(SELECT SUB_COMPANY.NUMBER_OF_MEN + SUB_COMPANY.NUMBER_OF_WOMEN >= SUB_LOGIB_HISTORY.WORKER_NUMBER AND SUB_COMPANY.NUMBER_OF_MEN >= SUB_LOGIB_HISTORY.MEN_NUMBER AND SUB_COMPANY.NUMBER_OF_WOMEN >= SUB_LOGIB_HISTORY.WOMEN_NUMBER FROM SUB_COMPANY INNER JOIN SUB_LOGIB_HISTORY WHERE SUB_LOGIB_HISTORY.NAME = 'Logib' AND SUB_COMPANY.ID=id AND SUB_LOGIB_HISTORY.TO_DATE IS NULL) FROM SUB_COMPANY INNER JOIN SUB_LOGIB_HISTORY WHERE SUB_LOGIB_HISTORY.NAME = 'LogibARGIB' AND SUB_COMPANY.ID=id AND SUB_LOGIB_HISTORY.TO_DATE IS NULL)")
  private Integer logIbARGIB;

  /** The country. */
  @ManyToOne
  @JoinColumn(name = "FK_COUNTRY")
  private CountryEntity country;

  /** The add info. */
  @Column(name = "ADD_INFO")
  private String addInfo;

  /** The address 1. */
  @Column(name = "ADDRESS1", length = 100)
  private String address1;

  /** The address 2. */
  @Column(name = "ADDRESS2", length = 100)
  private String address2;

  /** The post code. */
  @Column(name = "POST_CODE", length = 10)
  private String postCode;

  /** The location. */
  @Column(name = "LOCATION", length = 50)
  private String location;

  /** The company tel. */
  @Column(name = "COMPANY_TEL", length = 20)
  private String companyTel;

  /** The company fax. */
  @Column(name = "COMPANY_FAX", length = 20)
  private String companyFax;

  /** The company email. */
  @Column(name = "COMPANY_EMAIL", length = 100)
  private String companyEmail;

  /** The company web. */
  @Column(name = "COMPANY_WEBSITE", length = 100)
  private String companyWeb;

  /** The proof status. */
  @Column(name = "PROOF_STATUS")
  private Integer proofStatus;

  /**
   * The proof status including status FABE, because only certain users have the right to view this
   * proof status.
   */
  @Column(name = "PROOF_STATUS_FABE")
  private Integer proofStatusFabe;

  /** The notes. */
  @Column(name = "NOTES")
  private String notes;

  /** The modification date. */
  @Column(name = "MODIFICATION_DATE")
  private Timestamp modificationDate;

  /** The number of trainees. */
  @Column(name = "NUMBER_OF_TRAINEES")
  private Long numberOfTrainees;

  /** The number of women. */
  @Column(name = "NUMBER_OF_WOMEN")
  private Integer numberOfWomen;

  /** The number of men. */
  @Column(name = "NUMBER_OF_MEN")
  private Integer numberOfMen;

  /** The logib date. */
  @Column(name = "LOGIB_DATE")
  private Timestamp logibDate;

  /** The consult admin. */
  @Column(name = "CONSULT_ADMIN")
  private Boolean consultAdmin;

  /** The consult kaio. */
  @Column(name = "CONSULT_KAIO")
  private Boolean consultKaio;

  /** The new vat id. */
  @Column(name = "NEW_VAT_ID", length = 100)
  private String newVatId;

  /** The origin indication. */
  @Column(name = "ORIGIN_INDICATION", length = 50)
  private String originIndication;

  /** The logib kmu date. */
  @Column(name = "LOGIB_KMU_DATE", length = 50)
  private Timestamp logibKmuDate;

  /** The ilo. */
  @ManyToOne
  @JoinColumn(name = "FK_ILO")
  private MasterListValueEntity ilo;

  /** The ilo date. */
  @Column(name = "ILO_DATE")
  private Timestamp iloDate;

  /** The archived. */
  @Column(name = "IS_ARCHIVED")
  private Boolean archived;

  /** The tlp. */
  @Column(name = "TLP")
  private Boolean tlp;

  /** The main company. */
  @ManyToOne
  @JoinColumn(name = "FK_MAIN_COMPANY")
  private CompanyEntity mainCompany;

  /** The branches. */
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "FK_MAIN_COMPANY")
  private List<CompanyEntity> branches;

  /** The note admin. */
  @Column(name = "NOTES_ADMIN")
  private String noteAdmin;

  /** The certificate date. */
  @Column(name = "CERTIFICATE_DATE")
  private Date certificateDate;

  /** The mod user. */
  @Column(name = "FK_USER")
  private String modUser;

  /** The create on. */
  @Column(name = "CREATE_ON")
  private Timestamp createOn;

  /** The modified on. */
  @Column(name = "MODIFIED_ON")
  private Timestamp modifiedOn;

  /** The proof doc mod on. */
  @Column(name = "PROOF_DOC_MOD_ON")
  private Date proofDocModOn;

  /** The proof doc mod by. */
  @ManyToOne
  @JoinColumn(name = "PROOF_DOC_MOD_BY")
  private TenantEntity proofDocModBy;

  /** The proof doc submit date. */
  @Column(name = "PROOF_DOC_SUBMIT_DATE")
  private Date proofDocSubmitDate;

  /** The cert doc expiration date. */
  @Column(name = "CERT_EXPIRATION_DATE")
  private Date certDocExpirationDate;

  /** The created by. */
  @Column(name = "CREATED_BY")
  private String createdBy;

  /** The version. */
  @Version
  @Column(name = "VERSION")
  private Long version;
  
  /** The company proofs. */
  @OneToMany(mappedBy = "companyId")
  @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
  private List<CompanyProofEntity> companyProofs;

  /** The fifty plus colleagues. */
  @Column(name = "FIFTY_PLUS_COLLEAGUES")
  private Integer fiftyPlusColleagues;

  /** The fifty plus factor. */
  @Formula(value = "round(FIFTY_PLUS_COLLEAGUES / (NUMBER_OF_WOMEN + NUMBER_OF_MEN), 3)")
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
   * Gets the work types.
   *
   * @return the work types
   */
  public Set<MasterListValueEntity> getWorkTypes() {
    return workTypes;
  }

  /**
   * Sets the work types.
   *
   * @param workTypes the new work types
   */
  public void setWorkTypes(Set<MasterListValueEntity> workTypes) {
    this.workTypes = workTypes;
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
  public TenantEntity getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantEntity tenant) {
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
   * Gets the apprentice factor.
   *
   * @return the apprentice factor
   */
  public Double getApprenticeFactor() {
    if (apprenticeFactor == null) {
      return Double.valueOf(0);
    }
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
   * Gets the log ib.
   *
   * @return the log ib
   */
  public Boolean getLogIb() {

    return logIb == null ? false : logIb == 1;
  }

  /**
   * Gets the log ib ARGIB.
   *
   * @return the log ib ARGIB
   */
  public Boolean getLogIbARGIB() {

    return logIbARGIB == null ? false : logIbARGIB == 1;
  }

  /**
   * Gets the country.
   *
   * @return the country
   */
  public CountryEntity getCountry() {
    return country;
  }

  /**
   * Sets the country.
   *
   * @param country the new country
   */
  public void setCountry(CountryEntity country) {
    this.country = country;
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
  public MasterListValueEntity getIlo() {
    return ilo;
  }

  /**
   * Sets the ilo.
   *
   * @param ilo the new ilo
   */
  public void setIlo(MasterListValueEntity ilo) {
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
   * Gets the branches.
   *
   * @return the branches
   */
  public List<CompanyEntity> getBranches() {
    return branches;
  }

  /**
   * Sets the branches.
   *
   * @param branches the new branches
   */
  public void setBranches(List<CompanyEntity> branches) {
    this.branches = branches;
  }

  /**
   * Gets the main company.
   *
   * @return the main company
   */
  public CompanyEntity getMainCompany() {
    return mainCompany;
  }

  /**
   * Sets the main company.
   *
   * @param mainCompany the new main company
   */
  public void setMainCompany(CompanyEntity mainCompany) {
    this.mainCompany = mainCompany;
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
  public TenantEntity getProofDocModBy() {
    return proofDocModBy;
  }

  /**
   * Sets the proof doc mod by.
   *
   * @param proofDocModBy the new proof doc mod by
   */
  public void setProofDocModBy(TenantEntity proofDocModBy) {
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
   * Gets the work type history.
   *
   * @return the work type history
   */
  public Set<MasterListValueHistoryEntity> getWorkTypeHistory() {
    Set<MasterListValueHistoryEntity> masterListValueHistoryEntitySet = new HashSet<>();

    for (MasterListValueEntity masterListValueEntity : workTypes) {
      for (MasterListValueHistoryEntity historyEntity : masterListValueEntity
          .getMasterListValueHistory()) {
        if (historyEntity.getToDate() == null) {
          masterListValueHistoryEntitySet.add(historyEntity);
        }
      }
    }

    return masterListValueHistoryEntitySet;
  }

  /**
   * Gets the ilo history.
   *
   * @return the ilo history
   */
  public MasterListValueHistoryEntity getIloHistory() {

    for (MasterListValueHistoryEntity historyEntity : ilo.getMasterListValueHistory()) {
      if (historyEntity.getToDate() == null) {
        return historyEntity;
      }
    }

    return null;
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
   * Gets the company proofs.
   *
   * @return the company proofs
   */
  public List<CompanyProofEntity> getCompanyProofs() {
    return companyProofs;
  }

  /**
   * Sets the company proofs.
   *
   * @param companyProofs the new company proofs
   */
  public void setCompanyProofs(List<CompanyProofEntity> companyProofs) {
    this.companyProofs = companyProofs;
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
    if (fiftyPlusFactor == null) {
      return Double.valueOf(0);
    }
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
}
