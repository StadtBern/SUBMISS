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

package ch.bern.submiss.services.impl.util;

import ch.bern.submiss.services.api.constants.EmailTemplate;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyOfferDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.EmailAttributesDTO;
import ch.bern.submiss.services.api.dto.EmailTemplateTenantDTO;
import ch.bern.submiss.services.api.dto.ExclusionReasonDTO;
import ch.bern.submiss.services.api.dto.LegalHearingExclusionDTO;
import ch.bern.submiss.services.api.dto.MasterListDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SignatureCopyDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeEntitledDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmissionOverviewDTO;
import ch.bern.submiss.services.api.dto.SubmittentOfferDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class ComparatorUtil {

  /**
   * This function is comparing Worktype values for correct sorting
   */
  public static final Comparator<MasterListValueHistoryDTO> workType =
    (MasterListValueHistoryDTO m1, MasterListValueHistoryDTO m2) -> {
      if (Double.parseDouble(String.valueOf(m1.getValue1())) < Double
        .parseDouble(String.valueOf(m2.getValue1()))) {
        return -1;
      } else {
        return 1;
      }
    };

  /**
   * Sorting criteria with weightings according to specifications
   */
  public static final Comparator<CriterionDTO> criteriaWithWeightings =
    (CriterionDTO c1, CriterionDTO c2) -> {
      // Sort price award criteria and operating cost award criteria.
      if (c1.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
        || c1.getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
        || c2.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
        || c2.getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        if (c1.getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
          && !c2.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return -1;
        } else if (c2.getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
          && !c1.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return 1;
        }
        if (c1.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return -1;
        } else if (c2.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return 1;
        }
      }
      // Sort other criteria with weightings.
      if (c1.getWeighting() > c2.getWeighting()) {
        return -1;
      } else if (c1.getWeighting() < c2.getWeighting()) {
        return 1;
      } else {
        // In case of equal weightings, sort by criterion text (title).
        if (c1.getCriterionText().compareTo(c2.getCriterionText()) > 0) {
          return 1;
        } else if (c1.getCriterionText().compareTo(c2.getCriterionText()) < 0) {
          return -1;
        } else {
          // If criteria have the same text, sort them by their id.
          if (c1.getId().compareTo(c2.getId()) > 0) {
            return 1;
          } else {
            return -1;
          }
        }
      }
    };

  public static final Comparator<OfferCriterionDTO> offerCriteriaWithWeightings =
    (OfferCriterionDTO c1, OfferCriterionDTO c2) -> {
      // Sort price award offer criteria and operating cost award offer criteria.
      if (c1.getCriterion().getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
        || c1.getCriterion().getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
        || c2.getCriterion().getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
        || c2.getCriterion().getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
        if (c1.getCriterion().getCriterionType()
          .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
          && !c2.getCriterion().getCriterionType()
          .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return -1;
        } else if (c2.getCriterion().getCriterionType()
          .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
          && !c1.getCriterion().getCriterionType()
          .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return 1;
        }
        if (c1.getCriterion().getCriterionType()
          .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return -1;
        } else if (c2.getCriterion().getCriterionType()
          .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          return 1;
        }
      }
      // Sort other offer criteria with weightings.
      if (c1.getCriterion().getWeighting() != null && c2.getCriterion().getWeighting() != null) {
        if (c1.getCriterion().getWeighting() > c2.getCriterion().getWeighting()) {
          return -1;
        } else if (c1.getCriterion().getWeighting() < c2.getCriterion().getWeighting()) {
          return 1;
        } else {
          // In case of equal weightings, sort by criterion text (title).
          if (c1.getCriterion().getCriterionText()
            .compareTo(c2.getCriterion().getCriterionText()) > 0) {
            return 1;
          } else if (c1.getCriterion().getCriterionText()
            .compareTo(c2.getCriterion().getCriterionText()) < 0) {
            return -1;
          } else {
            // If offer criteria have the same criterion text, sort them by their criterion id.
            if (c1.getCriterion().getId()
              .compareTo(c2.getCriterion().getId()) > 0) {
              return 1;
            } else {
              return -1;
            }
          }
        }
      } else {
        // If not all offer criteria have weightings, just isolate the not weighted offer criteria from
        // the weighted offer criteria.
        if (c1.getCriterion().getWeighting() != null) {
          return -1;
        } else if (c2.getCriterion().getWeighting() != null) {
          return 1;
        }
      }
      return 0;
    };

  /**
   * Sort criteria without weightings
   */
  public static final Comparator<CriterionDTO> criteriaWithoutWeightings =
    (CriterionDTO c1, CriterionDTO c2) -> {
      if (c1.getWeighting() == null && c2.getWeighting() == null) {
        // Sort criteria by their criterion text.
        String o1StringPart = c1.getCriterionText().replaceAll("\\d", "");
        String o2StringPart = c2.getCriterionText().replaceAll("\\d", "");

        if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
          return extractInt(c1.getCriterionText()) - extractInt(c2.getCriterionText());
        }
        return c1.getCriterionText().compareTo(c2.getCriterionText());
      } else {
        // If not all criteria have null weightings, just isolate the not weighted
        // criteria from the weighted criteria.
        if (c1.getWeighting() != null) {
          return -1;
        } else if (c2.getWeighting() != null) {
          return 1;
        }
      }
      return 0;
    };

  /**
   * Sort offer criteria without weightings
   */
  public static final Comparator<OfferCriterionDTO> offerCriteriaWithoutWeightings =
    (OfferCriterionDTO c1, OfferCriterionDTO c2) -> {
      if (c1.getCriterion().getWeighting() == null && c2.getCriterion().getWeighting() == null) {
        // Sort offer criteria by their criterion text.
        String o1StringPart = c1.getCriterion().getCriterionText().replaceAll("\\d", "");
        String o2StringPart = c2.getCriterion().getCriterionText().replaceAll("\\d", "");

        if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
          return extractInt(c1.getCriterion().getCriterionText())
            - extractInt(c2.getCriterion().getCriterionText());
        }
        return c1.getCriterion().getCriterionText()
          .compareTo(c2.getCriterion().getCriterionText());
      } else {
        // If not all offer criteria have null weightings, just isolate the not weighted offer
        // criteria from the weighted offer criteria.
        if (c1.getCriterion().getWeighting() != null) {
          return -1;
        } else if (c2.getCriterion().getWeighting() != null) {
          return 1;
        }
      }
      return 0;
    };

  /**
   * This function is comparing SubmittentOfferDTO values for correct sorting
   */
  public static final Comparator<SubmittentOfferDTO> sortCompaniesByOffers =
    (SubmittentOfferDTO s1, SubmittentOfferDTO s2) -> {
      /**
       * Compare the three checkboxes and order by them after comparing amount and sorting
       */
      if (s1.getOffer().getIsEmptyOffer() != null && s2.getOffer().getIsEmptyOffer() != null) {
        if (s1.getOffer().getIsEmptyOffer().compareTo(s2.getOffer().getIsEmptyOffer()) > 0) {
          return 1;
        } else if (s1.getOffer().getIsEmptyOffer()
          .compareTo(s2.getOffer().getIsEmptyOffer()) < 0) {
          return -1;
        }
      }
      if (s1.getOffer().getIsExcludedFromProcess() != null
        && s2.getOffer().getIsExcludedFromProcess() != null) {
        if (s1.getOffer().getIsExcludedFromProcess()
          .compareTo(s2.getOffer().getIsExcludedFromProcess()) > 0) {
          return 1;
        } else if (s1.getOffer().getIsExcludedFromProcess()
          .compareTo(s2.getOffer().getIsExcludedFromProcess()) < 0) {
          return -1;
        }
      }
      if (s1.getOffer().getIsPartOffer() != null && s2.getOffer().getIsPartOffer() != null) {
        if (s1.getOffer().getIsPartOffer().compareTo(s2.getOffer().getIsPartOffer()) > 0) {
          return 1;
        } else if (s1.getOffer().getIsPartOffer().compareTo(s2.getOffer().getIsPartOffer()) < 0) {
          return -1;
        }
      }
      /**
       * Set var s1Amount and s2Amount for the case of null Compare the amounts
       */
      BigDecimal s1Amount = BigDecimal.ZERO;
      BigDecimal s2Amount = BigDecimal.ZERO;

      if (s1.getOffer().getAmount() != null) {
        s1Amount = s1.getOffer().getAmount();
      }
      if (s2.getOffer().getAmount() != null) {
        s2Amount = s2.getOffer().getAmount();
      }

      if (s1Amount.compareTo(s2Amount) > 0) {
        return 1;
      } else if (s1Amount.compareTo(s2Amount) < 0) {
        return -1;

      }

      /**
       * compare if amounts be equal to compare by name and order alphabetically
       */
      if (s1.getOffer().getSubmittent().getCompanyId().getCompanyName() != null
        && s2.getOffer().getSubmittent().getCompanyId().getCompanyName() != null) {
        if (s1.getOffer().getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getOffer().getSubmittent().getCompanyId().getCompanyName().toLowerCase())
          > 0) {
          return 1;
        } else if (s1.getOffer().getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getOffer().getSubmittent().getCompanyId().getCompanyName().toLowerCase())
          < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * Sorting subcriteria according to specifications
   */
  public static final Comparator<SubcriterionDTO> subcriteria =
    (SubcriterionDTO s1, SubcriterionDTO s2) -> {
      if (s1.getWeighting() > s2.getWeighting()) {
        return -1;
      } else if (s1.getWeighting() < s2.getWeighting()) {
        return 1;
      } else {
        if (s1.getSubcriterionText().toLowerCase().compareTo(s2.getSubcriterionText().toLowerCase())
          > 0) {
          return 1;
        } else {
          return -1;
        }
      }
    };

  public static final Comparator<OfferEntity> sortOfferEntities =
    (OfferEntity s1, OfferEntity s2) -> {
      if (s1.getIsEmptyOffer() != null && s2.getIsEmptyOffer() != null) {
        if (s1.getIsEmptyOffer().compareTo(s2.getIsEmptyOffer()) > 0) {
          return 1;
        } else if (s1.getIsEmptyOffer().compareTo(s2.getIsEmptyOffer()) < 0) {
          return -1;
        }
      }
      if (s1.getIsExcludedFromProcess() != null && s2.getIsExcludedFromProcess() != null) {
        if (s1.getIsExcludedFromProcess().compareTo(s2.getIsExcludedFromProcess()) > 0) {
          return 1;
        } else if (s1.getIsExcludedFromProcess().compareTo(s2.getIsExcludedFromProcess()) < 0) {
          return -1;
        }
      }
      if (s1.getIsPartOffer() != null && s2.getIsPartOffer() != null) {
        if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) > 0) {
          return 1;
        } else if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) < 0) {
          return -1;
        }
      }
      /**
       * Set var s1Amount and s2Amount when ammount is null
       */
      BigDecimal s1Amount = BigDecimal.ZERO;
      BigDecimal s2Amount = BigDecimal.ZERO;

      if (s1.getAmount() != null) {
        s1Amount = s1.getAmount();
      }
      if (s2.getAmount() != null) {
        s2Amount = s2.getAmount();
      }

      if (s1Amount.compareTo(s2Amount) > 0) {
        return 1;
      } else if (s1Amount.compareTo(s2Amount) < 0) {
        return -1;

      }

      if (s1.getSubmittent().getCompanyId().getCompanyName() != null
        && s2.getSubmittent().getCompanyId().getCompanyName() != null) {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }

      return -1;
    };

  public static final Comparator<OfferDTO> sortOfferEntitiesAndExcluded =
    (OfferDTO s1, OfferDTO s2) -> {
      if (s1.getIsExcludedFromAwardProcess() && !s2.getIsExcludedFromAwardProcess()) {
        return 1;
      } else if (!s1.getIsExcludedFromAwardProcess() && s2.getIsExcludedFromAwardProcess()) {
        return -1;
      } else if (s1.getIsExcludedFromAwardProcess() && s2.getIsExcludedFromAwardProcess()) {
        /* if both are excluded sort alphabetically */
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      } else {
        /* if none of them excluded sort as usual */
        if (s1.getIsEmptyOffer() != null && s2.getIsEmptyOffer() != null) {
          if (s1.getIsEmptyOffer().compareTo(s2.getIsEmptyOffer()) > 0) {
            return 1;
          } else if (s1.getIsEmptyOffer().compareTo(s2.getIsEmptyOffer()) < 0) {
            return -1;
          }
        }
        if (s1.getIsExcludedFromProcess() != null && s2.getIsExcludedFromProcess() != null) {
          if (s1.getIsExcludedFromProcess().compareTo(s2.getIsExcludedFromProcess()) > 0) {
            return 1;
          } else if (s1.getIsExcludedFromProcess().compareTo(s2.getIsExcludedFromProcess()) < 0) {
            return -1;
          }
        }
        if (s1.getIsPartOffer() != null && s2.getIsPartOffer() != null) {
          if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) > 0) {
            return 1;
          } else if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) < 0) {
            return -1;
          }
        }
        /**
         * Set var s1Amount and s2Amount when ammount is null
         */
        BigDecimal s1Amount = BigDecimal.ZERO;
        BigDecimal s2Amount = BigDecimal.ZERO;

        if (s1.getAmount() != null) {
          s1Amount = s1.getAmount();
        }
        if (s2.getAmount() != null) {
          s2Amount = s2.getAmount();
        }

        if (s1Amount.compareTo(s2Amount) > 0) {
          return 1;
        } else if (s1Amount.compareTo(s2Amount) < 0) {
          return -1;

        }

        if (s1.getSubmittent().getCompanyId().getCompanyName() != null
          && s2.getSubmittent().getCompanyId().getCompanyName() != null) {
          if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
            .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
            return 1;
          } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
            .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
            return -1;
          }
        }
      }
      return -1;
    };

  public static final Comparator<OfferEntity> sortOfferEntitiesScore =
    (OfferEntity s1, OfferEntity s2) -> {
      /* sort by score */
      if (s1.getAwardTotalScore().doubleValue() < s2.getAwardTotalScore().doubleValue()) {
        return 1;
      } else if (s1.getAwardTotalScore().doubleValue() > s2.getAwardTotalScore().doubleValue()) {
        return -1;
      } else {
        /* if both have equal score sort alphabetically */
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  public static final Comparator<OfferEntity> sortCompaniesByOfferEntities =
    (OfferEntity s1, OfferEntity s2) -> {
    // Same as sortCompaniesByOffers Comparator but for OfferEntities
      if (s1.getIsEmptyOffer() != null && s2.getIsEmptyOffer() != null) {
        if (s1.getIsEmptyOffer().compareTo(s2.getIsEmptyOffer()) > 0) {
          return 1;
        } else if (s1.getIsEmptyOffer()
          .compareTo(s2.getIsEmptyOffer()) < 0) {
          return -1;
        }
      }
      if (s1.getIsExcludedFromProcess() != null
        && s2.getIsExcludedFromProcess() != null) {
        if (s1.getIsExcludedFromProcess()
          .compareTo(s2.getIsExcludedFromProcess()) > 0) {
          return 1;
        } else if (s1.getIsExcludedFromProcess()
          .compareTo(s2.getIsExcludedFromProcess()) < 0) {
          return -1;
        }
      }
      if (s1.getIsPartOffer() != null && s2.getIsPartOffer() != null) {
        if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) > 0) {
          return 1;
        } else if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) < 0) {
          return -1;
        }
      }
      /**
       * Set var s1Amount and s2Amount for the case of null Compare the amounts
       */
      BigDecimal s1Amount = BigDecimal.ZERO;
      BigDecimal s2Amount = BigDecimal.ZERO;

      if (s1.getAmount() != null) {
        s1Amount = s1.getAmount();
      }
      if (s2.getAmount() != null) {
        s2Amount = s2.getAmount();
      }

      if (s1Amount.compareTo(s2Amount) > 0) {
        return 1;
      } else if (s1Amount.compareTo(s2Amount) < 0) {
        return -1;

      }

      /**
       * compare if amounts be equal to compare by name and order alphabetically
       */
      if (s1.getSubmittent().getCompanyId().getCompanyName() != null
        && s2.getSubmittent().getCompanyId().getCompanyName() != null) {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase())
          > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase())
          < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * Sort list of company's proofs according to proofOrder value.
   */
  public static final Comparator<CompanyProofDTO> sortCompanyProofDTOs =
    (CompanyProofDTO c1, CompanyProofDTO c2) -> {
      if (c1.getProof().getProofOrder() > c2.getProof().getProofOrder()) {
        return -1;
      } else {
        return 1;
      }
    };

  public static final Comparator<OfferEntity> sortOfferEntitiesWithRank =
    (OfferEntity s1, OfferEntity s2) -> {
      if (s1.getqExTotalGrade() != null && s2.getqExTotalGrade() != null
        && s1.getqExTotalGrade().doubleValue() < s2.getqExTotalGrade().doubleValue()) {
        return 1;
      } else if (s1.getqExTotalGrade() != null && s2.getqExTotalGrade() != null
        && s1.getqExTotalGrade().doubleValue() > s2.getqExTotalGrade().doubleValue()) {
        return -1;
      } else {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  public static final Comparator<SubmissionOverviewDTO> sortCompaniesByAmount =
    (SubmissionOverviewDTO s1, SubmissionOverviewDTO s2) -> {
      /**
       * Compare the three checkboxes and order by them after comparing amount and sorting
       */
      if (s1.getOffer().getIsEmptyOffer() != null && s2.getOffer().getIsEmptyOffer() != null) {
        if (s1.getOffer().getIsEmptyOffer().compareTo(s2.getOffer().getIsEmptyOffer()) > 0) {
          return 1;
        } else if (s1.getOffer().getIsEmptyOffer().compareTo(s2.getOffer().getIsEmptyOffer()) < 0) {
          return -1;
        }
      }
      if (s1.getOffer().getIsExcludedFromProcess() != null
        && s2.getOffer().getIsExcludedFromProcess() != null) {
        if (s1.getOffer().getIsExcludedFromProcess()
          .compareTo(s2.getOffer().getIsExcludedFromProcess()) > 0) {
          return 1;
        } else if (s1.getOffer().getIsExcludedFromProcess()
          .compareTo(s2.getOffer().getIsExcludedFromProcess()) < 0) {
          return -1;
        }
      }
      if (s1.getOffer().getIsPartOffer() != null && s2.getOffer().getIsPartOffer() != null) {
        if (s1.getOffer().getIsPartOffer().compareTo(s2.getOffer().getIsPartOffer()) > 0) {
          return 1;
        } else if (s1.getOffer().getIsPartOffer().compareTo(s2.getOffer().getIsPartOffer()) < 0) {
          return -1;
        }
      }
      /**
       * Set var s1Amount and s2Amount for the case of null Compare the amounts
       */
      BigDecimal s1Amount = BigDecimal.ZERO;
      BigDecimal s2Amount = BigDecimal.ZERO;

      if (s1.getOffer().getAmount() != null) {
        s1Amount = s1.getOffer().getAmount();
      }
      if (s2.getOffer().getAmount() != null) {
        s2Amount = s2.getOffer().getAmount();
      }

      if (s1Amount.compareTo(s2Amount) > 0) {
        return 1;
      } else if (s1Amount.compareTo(s2Amount) < 0) {
        return -1;
      }

      /**
       * compare if amounts be equal to compare by name and order alphabetically
       */
      if (s1.getSubmittent().getCompanyId().getCompanyName() != null
        && s2.getSubmittent().getCompanyId().getCompanyName() != null) {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * Sort offers in case of negotiated procedure with competition.
   */
  public static final Comparator<SubmittentOfferDTO> sortOffersForNegotiatedProcedureWithCompetition =
    (SubmittentOfferDTO s1, SubmittentOfferDTO s2) -> {
      if (s1.getOffer().getIsAwarded() != null && s1.getOffer().getIsAwarded()
        && (s2.getOffer().getIsAwarded() == null || !s2.getOffer().getIsAwarded())) {
        return -1;
      } else if ((s1.getOffer().getIsAwarded() == null || !s1.getOffer().getIsAwarded())
        && s2.getOffer().getIsAwarded() != null && s2.getOffer().getIsAwarded()) {
        return 1;
      }
      if (s1.getSubmittent().getExistsExclusionReasons() != null
        && s1.getSubmittent().getExistsExclusionReasons()
        && ((s2.getOffer().getIsEmptyOffer() == null || !s2.getOffer().getIsEmptyOffer())
        && (s2.getSubmittent().getExistsExclusionReasons() == null
        || !s2.getSubmittent().getExistsExclusionReasons()))) {
        return 1;
      } else if (((s1.getSubmittent().getExistsExclusionReasons() == null
        || !s1.getSubmittent().getExistsExclusionReasons())
        && (s1.getOffer().getIsEmptyOffer() == null || !s1.getOffer().getIsEmptyOffer()))
        && s2.getSubmittent().getExistsExclusionReasons() != null
        && s2.getSubmittent().getExistsExclusionReasons()) {
        return -1;
      }
      return 0;
    };

  /**
   * This function is comparing OfferDTO values for correct sorting for the document ANONIMISIERTES
   * OFFERTOFFNUNGSPROTOKOL
   */
  public static final Comparator<OfferDTO> sortCompanyOffersForDocument =
    (OfferDTO s1, OfferDTO s2) -> {
      /**
       * Compare the three checkboxes and order by them after comparing amount and sorting
       */
      if (s1.getIsEmptyOffer() != null && s2.getIsEmptyOffer() != null) {
        if (s1.getIsEmptyOffer().compareTo(s2.getIsEmptyOffer()) > 0) {
          return 1;
        } else if (s1.getIsEmptyOffer().compareTo(s2.getIsEmptyOffer()) < 0) {
          return -1;
        }
      }
      if (s1.getIsExcludedFromProcess() != null && s2.getIsExcludedFromProcess() != null) {
        if (s1.getIsExcludedFromProcess().compareTo(s2.getIsExcludedFromProcess()) > 0) {
          return 1;
        } else if (s1.getIsExcludedFromProcess().compareTo(s2.getIsExcludedFromProcess()) < 0) {
          return -1;
        }
      }
      if (s1.getIsPartOffer() != null && s2.getIsPartOffer() != null) {
        if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) > 0) {
          return 1;
        } else if (s1.getIsPartOffer().compareTo(s2.getIsPartOffer()) < 0) {
          return -1;
        }
      }
      /**
       * Set var s1Amount and s2Amount for the case of null Compare the amounts
       */
      BigDecimal s1Amount = BigDecimal.ZERO;
      BigDecimal s2Amount = BigDecimal.ZERO;

      if (s1.getAmount() != null) {
        s1Amount = s1.getAmount();
      }
      if (s2.getAmount() != null) {
        s2Amount = s2.getAmount();
      }

      if (s1Amount.compareTo(s2Amount) > 0) {
        return 1;
      } else if (s1Amount.compareTo(s2Amount) < 0) {
        return -1;

      }

      /**
       * compare if amounts be equal to compare by name and order alphabetically
       */
      if (s1.getSubmittent().getCompanyId().getCompanyName() != null
        && s2.getSubmittent().getCompanyId().getCompanyName() != null) {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * This function is comparing SubmittentOfferDTO values based on company name
   */
  public static final Comparator<SubmittentOfferDTO> sortCompaniesOffersByCompanyName =
    (SubmittentOfferDTO s1, SubmittentOfferDTO s2) -> {
      /**
       * compare by name and order alphabetically
       */
      if (s1.getOffer().getSubmittent().getCompanyId().getCompanyName() != null
        && s2.getOffer().getSubmittent().getCompanyId().getCompanyName() != null) {
        if (s1.getOffer().getSubmittent().getCompanyId().getCompanyName().toLowerCase().compareTo(
          s2.getOffer().getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getOffer().getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getOffer().getSubmittent().getCompanyId().getCompanyName()
            .toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * This function is comparing CompanyEntity values based on company name
   */
  public static final Comparator<CompanyEntity> sortCompaniesByCompanyName =
    (CompanyEntity s1, CompanyEntity s2) ->
      /**
       * compare by name and order alphabetically
       */
      s1.getCompanyName().toLowerCase().compareTo(s2.getCompanyName().toLowerCase());

  /**
   * This function is comparing CompanyDTO values based on company name
   */
  public static final Comparator<CompanyDTO> sortCompaniesDTOByCompanyName =
    (CompanyDTO s1, CompanyDTO s2) ->
      /**
       * compare by name and order alphabetically
       */
      s1.getCompanyName().toLowerCase().compareTo(s2.getCompanyName().toLowerCase());

  /**
   * This function sorts the excluded submittents by their CreatedOn date
   */
  public static final Comparator<LegalHearingExclusionDTO> sortExcludedSubmittents =
    (LegalHearingExclusionDTO s1, LegalHearingExclusionDTO s2) -> {
      /*---- sorting by date ----*/
      /* initialize date */
      Date s1Date = new Date(0);
      Date s2Date = new Date(0);
      if (s1.getCreatedOn() != null) {
        s1Date = s1.getCreatedOn();
      }
      if (s2.getCreatedOn() != null) {
        s2Date = s2.getCreatedOn();
      }
      if (s1Date.after(s2Date)) {
        return -1;
      } else if (s1Date.before(s2Date)) {
        return 1;
      } else {
        /* if both have equal score sort alphabetically */
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * This function sorts offer by their notes.
   */
  public static final Comparator<OfferDTO> offerNotesComparison = (OfferDTO o1, OfferDTO o2) -> {
    if ((o1.getNotes() == null || StringUtils.isBlank(o1.getNotes()))
      && (o2.getNotes() == null || StringUtils.isBlank(o2.getNotes()))) {
      return 0;
    } else if ((o1.getNotes() == null || StringUtils.isBlank(o1.getNotes()))
      && o2.getNotes() != null) {
      return 1;
    } else if (o1.getNotes() != null
      && (o2.getNotes() == null || StringUtils.isBlank(o2.getNotes()))) {
      return -1;
    } else {
      return o1.getNotes().toLowerCase().compareTo(o2.getNotes().toLowerCase());
    }
  };

  public static final Comparator<AuditLogDTO> sortUserActivitiesByDate =
    (AuditLogDTO al1, AuditLogDTO al2) -> {
      /* sort by date */
      /* initialize date */
      Date s1Date = new Date();
      Date s2Date = new Date();
      if (al1.getCreatedOn() != null) {
        s1Date = al1.getCreatedOn();
      }
      if (al2.getCreatedOn() != null) {
        s2Date = al2.getCreatedOn();
      }
      if (s1Date.after(s2Date)) {
        return 1;
      } else if (s1Date.before(s2Date)) {
        return -1;
      }
      return -1;
    };

  /**
   * This function sorts master list values by their name.
   */
  public static final Comparator<MasterListDTO> sortMasterListValuesByName =
    (MasterListDTO m1, MasterListDTO m2) ->
      // Compare by name and order alphabetically.
      m1.getName().toLowerCase().compareTo(m2.getName().toLowerCase());

  /**
   * This function sorts master list type data by their description.
   */
  public static final Comparator<MasterListTypeDataDTO> sortMasterListTypeDataByDescription =
    (MasterListTypeDataDTO m1, MasterListTypeDataDTO m2) ->
      // Compare by description and order alphabetically.
      m1.getDescription().toLowerCase().compareTo(m2.getDescription().toLowerCase());

  /**
   * This function sorts master list type data by their added description.
   */
  public static final Comparator<MasterListTypeDataDTO> sortMasterListTypeDataByAddedDescription =
    (MasterListTypeDataDTO m1, MasterListTypeDataDTO m2) ->
      // Compare by added description and order alphabetically.
      m1.getAddedDescription().toLowerCase().compareTo(m2.getAddedDescription().toLowerCase());

  /**
   * The Constant SignatureComparator.
   */
  public static final Comparator<SignatureProcessTypeEntitledDTO> SignatureComparator = Comparator
    .comparing(SignatureProcessTypeEntitledDTO::getSortNumber);

  /**
   * The Constant SignatureCopyComparator.
   */
  public static final Comparator<SignatureCopyDTO> SignatureCopyComparator = Comparator
    .comparing(SignatureCopyDTO::getSortNumber);

  /**
   * Comparator to sort email attributes by send type.
   */
  public static final Comparator<EmailAttributesDTO> sortAttributesBySendType =
    (EmailAttributesDTO a1, EmailAttributesDTO a2) -> {
      if (a1.getSendType().equals(EmailTemplate.SEND_TYPE.TO)) {
        return -1;
      } else if (a2.getSendType().equals(EmailTemplate.SEND_TYPE.TO)) {
        return 1;
      }
      if (a1.getSendType().equals(EmailTemplate.SEND_TYPE.CC)) {
        return -1;
      } else if (a2.getSendType().equals(EmailTemplate.SEND_TYPE.CC)) {
        return 1;
      }
      return 0;
    };

  /**
   * Comparator to sort offerDTOs with award rank.
   */
  public static final Comparator<OfferDTO> sortOfferDTOsWithAwardRank =
    (OfferDTO s1, OfferDTO s2) -> {
      if (s1.getAwardRank() != null && s2.getAwardRank() != null
        && s1.getAwardRank() < s2.getAwardRank()) {
        return -1;
      } else if (s1.getAwardRank() != null && s2.getAwardRank() != null
        && s1.getAwardRank() > s2.getAwardRank()) {
        return 1;
      } else if (s1.getAwardRank() != null && s2.getAwardRank() == null) {
        return -1;
      } else if (s1.getAwardRank() == null && s2.getAwardRank() != null) {
        return 1;
      } else {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * Comparator to sort SubmissionOverviewDTOs by rank.
   */
  public static final Comparator<SubmissionOverviewDTO> sortCompaniesByRank =
    (SubmissionOverviewDTO s1, SubmissionOverviewDTO s2) -> {
      if (s1.getOffer().getqExRank() != null && s2.getOffer().getqExRank() != null) {
        return s1.getOffer().getqExRank().compareTo(s2.getOffer().getqExRank());
      } else if (s1.getOffer().getqExRank() != null && s2.getOffer().getqExRank() == null) {
        return -1;
      } else if (s1.getOffer().getqExRank() == null && s2.getOffer().getqExRank() != null) {
        return 1;
      } else {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * Comparator to sort SubmissionOverviewDTOs by award rank.
   */
  public static final Comparator<SubmissionOverviewDTO> sortCompaniesByAwardRank =
    (SubmissionOverviewDTO s1, SubmissionOverviewDTO s2) -> {
      if (s1.getOffer().getIsEmptyOffer() != null && s2.getOffer().getIsEmptyOffer() != null) {
        if (s1.getOffer().getIsEmptyOffer().compareTo(s2.getOffer().getIsEmptyOffer()) > 0) {
          return 1;
        } else if (s1.getOffer().getIsEmptyOffer().compareTo(s2.getOffer().getIsEmptyOffer()) < 0) {
          return -1;
        }
      }
      if (s1.getOffer().getAwardRank() != null && s2.getOffer().getAwardRank() != null) {
        return s1.getOffer().getAwardRank().compareTo(s2.getOffer().getAwardRank());
      } else if (s1.getOffer().getAwardRank() != null && s2.getOffer().getAwardRank() == null) {
        return -1;
      } else if (s1.getOffer().getAwardRank() == null && s2.getOffer().getAwardRank() != null) {
        return 1;
      } else {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * Comparator to sort email template tenants by description
   */
  public static final Comparator<EmailTemplateTenantDTO> sortEmailTemplateTenantsByDescription =
    (EmailTemplateTenantDTO e1, EmailTemplateTenantDTO e2) -> {
      if (e1.getDescription().toLowerCase().compareTo(e2.getDescription().toLowerCase()) < 0) {
        return -1;
      } else {
        return 1;
      }
    };

  public static final Comparator<CompanyProofDTO> sortCompanyProofDTOsAsc =
    (CompanyProofDTO c1, CompanyProofDTO c2) -> {
      if (c1.getProof().getProofOrder() < c2.getProof().getProofOrder()) {
        return -1;
      } else {
        return 1;
      }
    };

  /**
   * This function is comparing CompanyEntity values based on company name
   */
  public static final Comparator<SubmissionOverviewDTO> sortCompaniesByName =
    (SubmissionOverviewDTO s1, SubmissionOverviewDTO s2) ->
      /**
       * compare by name and order alphabetically
       */

      s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
        .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase());

  /**
   * This function is comparing CompanyOffer values based on offer date
   */
  public static final Comparator<CompanyOfferDTO> sortCompanyOffersByDate =
    (CompanyOfferDTO o1, CompanyOfferDTO o2) -> {
      /* sort by date */
      /* initialize date */
      Date s1Date = new Date();
      Date s2Date = new Date();
      if (o1.getDeadline2() != null) {
        s1Date = o1.getDeadline2();
      }
      if (o2.getDeadline2() != null) {
        s2Date = o2.getDeadline2();
      }
      if (s1Date.after(s2Date)) {
        return -1;
      } else if (s1Date.before(s2Date)) {
        return 1;
      }
      return -1;
    };

  /**
   * Comparator to sort offer DTOs by their examination rank.
   */
  public static final Comparator<OfferDTO> sortOfferDTOsByExaminationRank =
    (OfferDTO s1, OfferDTO s2) -> {
      if (s1.getqExRank() != null && s2.getqExRank() != null
        && s1.getqExRank() < s2.getqExRank()) {
        return -1;
      } else if (s1.getqExRank() != null && s2.getqExRank() != null
        && s1.getqExRank() > s2.getqExRank()) {
        return 1;
      } else if (s1.getqExRank() != null && s2.getqExRank() == null) {
        return -1;
      } else if (s1.getqExRank() == null && s2.getqExRank() != null) {
        return 1;
      } else {
        if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) > 0) {
          return 1;
        } else if (s1.getSubmittent().getCompanyId().getCompanyName().toLowerCase()
          .compareTo(s2.getSubmittent().getCompanyId().getCompanyName().toLowerCase()) < 0) {
          return -1;
        }
      }
      return -1;
    };

  /**
   * Comparator to sort LegalHearingExclusionDTOs by their Lit.
   */
  public static final Comparator<ExclusionReasonDTO> sortLegalHearingExclusionDTOsByLit =
    (ExclusionReasonDTO s1, ExclusionReasonDTO s2) -> {
        String substr1 = s1.getExclusionReason().getValue1()
          .substring(s1.getExclusionReason().getValue1().length()-1);
        String substr2 = s2.getExclusionReason().getValue1()
          .substring(s2.getExclusionReason().getValue1().length()-1);
        return substr1.compareTo(substr2);
    };

  /**
   * This function sorts master list value history DTOs by value1.
   */
  public static final Comparator<MasterListValueHistoryDTO> sortMLVHistoryDTOsByValue1 =
    (MasterListValueHistoryDTO m1, MasterListValueHistoryDTO m2) ->
      // Compare by value1 and order alphabetically.
      m1.getValue1().toLowerCase().compareTo(m2.getValue1().toLowerCase());

  public static final Comparator<SubmissionDTO> sortSubmissionDTOsByObjekt =
    (SubmissionDTO s1, SubmissionDTO s2) -> {
      StringBuilder object1 = new StringBuilder();
      object1.append(s1.getProject().getObjectName().getValue1());
      if (s1.getProject().getObjectName().getValue2() != null) {
        object1.append(LookupValues.COMMA)
          .append(s1.getProject().getObjectName().getValue2());
      }
      StringBuilder object2 = new StringBuilder();
      object2.append(s2.getProject().getObjectName().getValue1());
      if (s2.getProject().getObjectName().getValue2() != null) {
        object2.append(LookupValues.COMMA)
          .append(s2.getProject().getObjectName().getValue2());
      }
      String obj1 = object1.toString();
      String obj2 = object2.toString();
      // ascending order
      return obj1.compareTo(obj2);
    };

  public static final Comparator<SubmissionDTO> sortSubmissionDTOsByProjekt =
    (SubmissionDTO s1, SubmissionDTO s2) -> {
      String projekt1 = s1.getProject().getProjectName();
      String projekt2 = s2.getProject().getProjectName();
      // ascending order
      return projekt1.compareTo(projekt2);
    };

  public static final Comparator<SubmissionDTO> sortSubmissionDTOsByArbeitsgattung =
    (SubmissionDTO s1, SubmissionDTO s2) -> {
      String arbeitsgattung1 = s1.getWorkType().getValue1() + LookupValues.SPACE + s1.getWorkType()
        .getValue2();
      String arbeitsgattung2 = s2.getWorkType().getValue1() + LookupValues.SPACE + s2.getWorkType()
        .getValue2();
      // ascending order
      return arbeitsgattung1.compareTo(arbeitsgattung2);
    };

  /*
   * sonar's bug: Utility classes, which are collections of static members, are not meant to be
   * instantiated.
   */
  private ComparatorUtil() {
    throw new IllegalStateException(
      "Utility classes, which are collections of static members, are not meant to be instantiated.");
  }

  static int extractInt(String s) {
    String num = s.replaceAll("\\D", "");
    // return 0 if no digits found
    return num.isEmpty() ? 0 : Integer.parseInt(num);
  }
}
