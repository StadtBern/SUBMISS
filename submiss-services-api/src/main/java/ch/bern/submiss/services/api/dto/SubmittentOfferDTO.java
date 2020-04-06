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

/**
 * The Class SubmittentOfferDTO.
 */
public class SubmittentOfferDTO {

  /** The submittent. */
  private SubmittentDTO submittent;

  /** The offer. */
  private OfferDTO offer;

  /**
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentDTO getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentDTO submittent) {
    this.submittent = submittent;
  }

  /**
   * Gets the offer.
   *
   * @return the offer
   */
  public OfferDTO getOffer() {
    return offer;
  }

  /**
   * Sets the offer.
   *
   * @param offer the new offer
   */
  public void setOffer(OfferDTO offer) {
    this.offer = offer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SubmittentOfferDTO [submittent=" + submittent.getId() + ", offer=" + offer.getId()
        + "]";
  }

}
