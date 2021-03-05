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

package ch.bern.submiss.services.api.administration;

import ch.bern.submiss.services.api.dto.NachtragDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.List;
import java.util.Set;

public interface NachtragService {

  /**
   * Get a nachtrag by its id.
   *
   * @param nachtragId the nachtragId
   * @return the nachtrag
   */
  NachtragDTO getNachtrag(String nachtragId);

  /**
   * Get the nachtrag submittents for a specific submission.
   *
   * @param submissionId the submissionId
   * @return the nachtrag submittents
   */
  List<OfferDTO> getNachtragSubmittents(String submissionId);

  /**
   * Get all nachtrags for a specific nachtrag submittent.
   *
   * @param offerId the nachtrag submittent id
   * @return the nachtrags
   */
  List<NachtragDTO> getNachtragsByNachtragSubmittent(String offerId);

  /**
   * Update nachtrag.
   *
   * @param nachtragDTO the nachtragDTO
   */
  void updateNachtrag(NachtragDTO nachtragDTO);

  /**
   * Create nachtrag.
   *
   * @param offerId the offerId
   * @return
   */
  void createNachtrag(String offerId);

  /**
   * Delete nachtrag.
   *
   * @param nachtragId the nachtragId
   * @param version
   * @return
   */
  void deleteNachtrag(String nachtragId, Long version);

  /**
   * Add a nachtrag submittent by creating a nachtrag entry.
   *
   * @param offerId the nachtrag submittent id
   */
  void addNachtragSubmittent(String offerId);

  /**
   * Get the awarded offers for a specific submission.
   *
   * @param submissionId the submissionId
   * @return the awarded offers
   */
  List<OfferDTO> getAwardedOffers(String submissionId);

  /**
   * Close nachtrag.
   *
   * @param nachtragId the nachtragId
   * @param version
   * @return
   */
  Set<ValidationError> closeNachtrag(String nachtragId, Long version);

  /**
   * Reopen nachtrag.
   *  @param nachtragId   the nachtragId
   * @param reopenReason the reason
   * @param version
   * @return
   */
  void reopenNachtrag(String nachtragId, String reopenReason, Long version);
}
