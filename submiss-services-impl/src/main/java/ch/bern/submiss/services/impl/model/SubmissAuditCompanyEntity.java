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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

/**
 * The Class SubmissAuditCompanyEntity.
 */
@Entity
@Table(name = "VW_AUDIT_COMPANY")
@SqlResultSetMapping(name = "SubmissAuditCompanyMapping",
  entities = @EntityResult(entityClass = SubmissAuditCompanyEntity.class,
    fields = {
      @FieldResult(name = "id", column = "id"),
      @FieldResult(name = "userName", column = "userName"),
      @FieldResult(name = "shortDescription", column = "shortDescription"),
      @FieldResult(name = "createdOn", column = "createdOn"),
      @FieldResult(name = "resourceKey", column = "resource_key"),
      @FieldResult(name = "translation", column = "translation"),
      @FieldResult(name = "tenantId", column = "tenant_id"),
      @FieldResult(name = "companyName", column = "companyName"),
      @FieldResult(name = "proofStatusFabe", column = "proofStatusFabe")
    }))
public class SubmissAuditCompanyEntity extends SubmissAuditEntity {

  /** The company name. */
  @Column(name = "companyName")
  private String companyName;

  /** The proof status FABE. */
  @Column(name = "proofStatusFabe")
  private int proofStatusFabe;



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
   * Gets the proof status fabe.
   *
   * @return the proof status fabe
   */
  public int getProofStatusFabe() {
    return proofStatusFabe;
  }

  /**
   * Sets the proof status fabe.
   *
   * @param proofStatusFabe the new proof status fabe
   */
  public void setProofStatusFabe(int proofStatusFabe) {
    this.proofStatusFabe = proofStatusFabe;
  }


}
