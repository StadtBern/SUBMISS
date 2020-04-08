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

import ch.bern.submiss.services.api.constants.Process;
import java.sql.Timestamp;
import java.util.List;

/**
 * The Class SignatureProcessTypeDTO.
 */
public class SignatureProcessTypeDTO {

  /**
   * The id.
   */
  private String id;

  /**
   * The process.
   */
  private Process process;

  /**
   * The signature.
   */
  private SignatureDTO signature;

  /**
   * The signature copies.
   */
  private List<SignatureCopyDTO> signatureCopies;

  /**
   * The signature process type entitled.
   */
  private List<SignatureProcessTypeEntitledDTO> signatureProcessTypeEntitled;

  /**
   * The requestedOn.
   */
  private Timestamp requestedOn;

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
  public List<SignatureCopyDTO> getSignatureCopies() {
    return signatureCopies;
  }

  /**
   * Sets the signature copies.
   *
   * @param signatureCopies the new signature copies
   */
  public void setSignatureCopies(List<SignatureCopyDTO> signatureCopies) {
    this.signatureCopies = signatureCopies;
  }

  /**
   * Gets the signature process type entitled.
   *
   * @return the signature process type entitled
   */
  public List<SignatureProcessTypeEntitledDTO> getSignatureProcessTypeEntitled() {
    return signatureProcessTypeEntitled;
  }

  /**
   * Sets the signature process type entitled.
   *
   * @param signatureProcessTypeEntitled the new signature process type entitled
   */
  public void setSignatureProcessTypeEntitled(
    List<SignatureProcessTypeEntitledDTO> signatureProcessTypeEntitled) {
    this.signatureProcessTypeEntitled = signatureProcessTypeEntitled;
  }

  /**
   * Gets the signature.
   *
   * @return the signature
   */
  public SignatureDTO getSignature() {
    return signature;
  }

  /**
   * Sets the signature.
   *
   * @param signature the new signature
   */
  public void setSignature(SignatureDTO signature) {
    this.signature = signature;
  }

  /**
   * Gets the requested on.
   *
   * @return the requestedOn
   */
  public Timestamp getRequestedOn() {
    return requestedOn;
  }

  /**
   * Sets the requested on.
   *
   * @param requestedOn the requestedOn
   */
  public void setRequestedOn(Timestamp requestedOn) {
    this.requestedOn = requestedOn;
  }

  @Override
  public String toString() {
    return "SignatureProcessTypeDTO [id=" + id + ", process=" + process + "]";
  }
}
