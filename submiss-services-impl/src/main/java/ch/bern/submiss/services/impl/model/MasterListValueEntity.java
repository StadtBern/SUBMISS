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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "SUB_MASTER_LIST_VALUE")
public class MasterListValueEntity {

  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  @ManyToOne
  @JoinColumn(name = "FK_MASTER_LIST")
  private MasterListEntity masterList;

  @OneToMany(mappedBy = "masterListValueId")
  private Set<MasterListValueHistoryEntity> masterListValueHistory;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MasterListEntity getMasterList() {
    return masterList;
  }

  public void setMasterList(MasterListEntity masterList) {
    this.masterList = masterList;
  }

  public Set<MasterListValueHistoryEntity> getMasterListValueHistory() {
    return masterListValueHistory;
  }

  public void setMasterListValueHistory(Set<MasterListValueHistoryEntity> masterListValueHistory) {
    this.masterListValueHistory = masterListValueHistory;
  }


}
