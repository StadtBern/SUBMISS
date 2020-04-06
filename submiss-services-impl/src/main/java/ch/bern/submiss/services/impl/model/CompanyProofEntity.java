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

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * The Class CompanyProofEntity.
 */
@Entity
@Table(name = "SUB_COMPANY_PROOF")
@IdClass(Key.class)
@SqlResultSetMapping(name="deleteResult", columns = { @ColumnResult(name = "count")})
@NamedNativeQueries({

@NamedNativeQuery(name = "deleteDefaultProofs",
            query = "DELETE FROM SUB_COMPANY_PROOF where  SUB_COMPANY_PROOF.FK_COMPANY IN (\r\n" + 
                "SELECT SUB_COMPANY.ID from  SUB_COMPANY\r\n" + 
                "INNER JOIN SUB_COUNTRY\r\n" + 
                "ON SUB_COUNTRY.ID = SUB_COMPANY.FK_COUNTRY\r\n" + 
                "WHERE SUB_COMPANY.FK_COUNTRY = ?1)\r\n" + 
                "AND SUB_COMPANY_PROOF.FK_PROOF NOT IN \r\n" + 
                "(SELECT SUB_PROOF_HISTORY.FK_PROOF FROM SUB_PROOF_HISTORY \r\n" + 
                "INNER JOIN SUB_PROOF \r\n" + 
                "ON SUB_PROOF.ID = SUB_PROOF_HISTORY.FK_PROOF \r\n" + 
                "WHERE SUB_PROOF_HISTORY.FK_COUNTRY = ?1)"  ,resultSetMapping = "deleteResult")
})
public class CompanyProofEntity {
  
  /** The company id. */
  @Id
  @ManyToOne
  @JoinColumn(name = "FK_COMPANY")
  private CompanyEntity companyId;

  /** The proof id. */
  @Id
  @ManyToOne
  @JoinColumn(name = "FK_PROOF")
  private ProofEntity proofId;

  /** The proof date. */
  @Column(name = "PROOF_DATE")
  private Timestamp proofDate;

  /** The required. */
  @Column(name = "IS_REQUIRED")
  private Boolean required;

  /** The mod user. */
  @Column(name = "PROOF_MOD_USER")
  private String modUser;

  /** The mod date. */
  @Column(name = "MODIFICATION_DATE")
  private Timestamp modDate;

  /**
   * Gets the company id.
   *
   * @return the company id
   */
  public CompanyEntity getCompanyId() {
    return companyId;
  }

  /**
   * Sets the company id.
   *
   * @param companyId the new company id
   */
  public void setCompanyId(CompanyEntity companyId) {
    this.companyId = companyId;
  }

  /**
   * Gets the proof id.
   *
   * @return the proof id
   */
  public ProofEntity getProofId() {
    return proofId;
  }

  /**
   * Sets the proof id.
   *
   * @param proofId the new proof id
   */
  public void setProofId(ProofEntity proofId) {
    this.proofId = proofId;
  }

  /**
   * Gets the proof date.
   *
   * @return the proof date
   */
  public Timestamp getProofDate() {
    return proofDate;
  }

  /**
   * Sets the proof date.
   *
   * @param proofDate the new proof date
   */
  public void setProofDate(Timestamp proofDate) {
    this.proofDate = proofDate;
  }

  /**
   * Gets the required.
   *
   * @return the required
   */
  public Boolean getRequired() {
    return required;
  }

  /**
   * Sets the required.
   *
   * @param required the new required
   */
  public void setRequired(Boolean required) {
    this.required = required;
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
   * Gets the mod date.
   *
   * @return the mod date
   */
  public Timestamp getModDate() {
    return modDate;
  }

  /**
   * Sets the mod date.
   *
   * @param modDate the new mod date
   */
  public void setModDate(Timestamp modDate) {
    this.modDate = modDate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CompanyProofEntity [proofId=" + proofId + ", proofDate=" + proofDate + ", required="
        + required + ", modUser=" + modUser + ", modDate=" + modDate + "]";
  }

}

abstract class Key implements Serializable {
  private CompanyEntity companyId;
  private ProofEntity proofId;
}
