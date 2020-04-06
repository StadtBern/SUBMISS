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

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * The Class TenantEntity.
 */
@Entity
@Table(name = "SUB_DIRECTORATE")
public class DirectorateEntity {


  @Id
  @GeneratedValue(generator = "uuid5")
  @GenericGenerator(name = "uuid5", strategy = "uuid2")
  private String id;

  @OneToMany(mappedBy = "directorateId")
  private Set<DirectorateHistoryEntity> directorate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

public Set<DirectorateHistoryEntity> getDirectorate() {
	return directorate;
}

public void setDirectorate(Set<DirectorateHistoryEntity> directorate) {
	this.directorate = directorate;
}



}
