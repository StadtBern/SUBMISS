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

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import ch.bern.submiss.services.api.constants.Process;

/**
 * The Class SignatureProcessTypeEntity.
 */
@Entity
@Table(name = "SUB_SIGNATURE_PROCESS_TYPE")
public class SignatureProcessTypeEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  /** The signature. */
  @ManyToOne
  @JoinColumn(name = "FK_SIGNATURE")
  private SignatureEntity signature;

  /** The process. */
  @Column(name = "PROCESS_TYPE")
  @Enumerated
  private Process process;

  /** The signature copies. */
  @OneToMany(mappedBy = "processType", fetch = FetchType.EAGER)
  private List<SignatureCopyEntity> signatureCopies;

  /** The signature process type entitled. */
  @OneToMany(mappedBy = "processType", fetch = FetchType.EAGER)
  private List<SignatureProcessTypeEntitledEntity> signatureProcessTypeEntitled;

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
   * Gets the signature.
   *
   * @return the signature
   */
  public SignatureEntity getSignature() {
    return signature;
  }

  /**
   * Sets the signature.
   *
   * @param signature the new signature
   */
  public void setSignature(SignatureEntity signature) {
    this.signature = signature;
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

  /**
   * Gets the signature copies.
   *
   * @return the signature copies
   */
  public List<SignatureCopyEntity> getSignatureCopies() {
    return signatureCopies;
  }

  /**
   * Sets the signature copies.
   *
   * @param signatureCopies the new signature copies
   */
  public void setSignatureCopies(List<SignatureCopyEntity> signatureCopies) {
    this.signatureCopies = signatureCopies;
  }

  /**
   * Gets the signature process type entitled.
   *
   * @return the signature process type entitled
   */
  public List<SignatureProcessTypeEntitledEntity> getSignatureProcessTypeEntitled() {
    return signatureProcessTypeEntitled;
  }

  /**
   * Sets the signature process type entitled.
   *
   * @param signatureProcessTypeEntitled the new signature process type entitled
   */
  public void setSignatureProcessTypeEntitled(
      List<SignatureProcessTypeEntitledEntity> signatureProcessTypeEntitled) {
    this.signatureProcessTypeEntitled = signatureProcessTypeEntitled;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SignatureProcessTypeEntity [id=" + id + ", signature=" + signature + "]";
  }


}
