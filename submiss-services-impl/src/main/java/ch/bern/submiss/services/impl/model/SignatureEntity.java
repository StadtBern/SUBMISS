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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class Signature.
 */
@Entity
@Table(name = "SUB_SIGNATURE")
public class SignatureEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  /** The department. */
  @ManyToOne
  @JoinColumn(name = "FK_DEPARTMENT")
  private DepartmentEntity department;

  /** The directorate. */
  @ManyToOne
  @JoinColumn(name = "FK_DIRECTORATE")
  private DirectorateEntity directorate;

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
   * Gets the department.
   *
   * @return the department
   */
  public DepartmentEntity getDepartment() {
    return department;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(DepartmentEntity department) {
    this.department = department;
  }

  /**
   * Gets the directorate.
   *
   * @return the directorate
   */
  public DirectorateEntity getDirectorate() {
    return directorate;
  }

  /**
   * Sets the directorate.
   *
   * @param directorate the new directorate
   */
  public void setDirectorate(DirectorateEntity directorate) {
    this.directorate = directorate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Signature [id=" + id + "]";
  }
}
