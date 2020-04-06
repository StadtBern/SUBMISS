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

package ch.bern.submiss.services.impl.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ch.bern.submiss.services.api.dto.GeKoResultDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;

@Mapper
public abstract class GeKoResultMapper {

	public static final GeKoResultMapper INSTANCE = Mappers.getMapper(GeKoResultMapper.class);

	/**
	 * Converts a list of SubmissionDTO to a list of GeKoResultDTO.
	 *
	 * @param submissionDTOs
	 *            the SubmissionDTO list
	 * @return the GeKoResultDTO list
	 */
	public List<GeKoResultDTO> submissionDTOtoGeKoResultDTO(List<SubmissionDTO> submissionDTOs) {
		List<GeKoResultDTO> geKoResultDTOs = new ArrayList<>();
		for (SubmissionDTO submissionDTO : submissionDTOs) {
			geKoResultDTOs.add(submissionDTOtoGeKoResultDTO(submissionDTO));
		}
		return geKoResultDTOs;
	}

	/**
	 * Converts a list of SubmissionDTO to a list of GeKoResultDTO.
	 *
	 * @param submissionDTOs
	 *            the SubmissionDTO list
	 * @return the GeKoResultDTO list
	 */
	public List<GeKoResultDTO> offerDTOtoGeKoResultDTO(List<OfferDTO> offerDTOs) {
		List<GeKoResultDTO> geKoResultDTOs = new ArrayList<>();
		for (OfferDTO offerDTO : offerDTOs) {
			geKoResultDTOs.add(offerDTOtoGeKoResultDTO(offerDTO));
		}
		return geKoResultDTOs;
	}

	/**
	 * Converts a SubmissionDTO to a GeKoResultDTO.
	 *
	 * @param submissionDTO
	 * @return the GeKoResult DTO
	 */
	public GeKoResultDTO submissionDTOtoGeKoResultDTO(SubmissionDTO submissionDTO) {
		if (submissionDTO == null) {
			return null;
		}
		return getGeKoResultDTO(submissionDTO);
	}

	/**
	 * Converts a offerDTO to a GeKoResultDTO.
	 *
	 * @param offerDTO
	 * @return the GeKoResult DTO
	 */
	public GeKoResultDTO offerDTOtoGeKoResultDTO(OfferDTO offerDTO) {
		if (offerDTO == null) {
			return null;
		}
		return convertOfferDTOtoGeKoResultDTO(offerDTO);
	}

	/**
	 * Gets the GeKoResult DTO.
	 *
	 * @param submissionDTO
	 * @return GeKoResult DTO.
	 */
	private GeKoResultDTO getGeKoResultDTO(SubmissionDTO submissionDTO) {
		GeKoResultDTO geKoResultDTO = new GeKoResultDTO();
		if (submissionDTO.getStatus() != null && submissionDTO.getStatus().trim().length() > 0) {
			geKoResultDTO.setId(submissionDTO.getId());
			getGeKoResultDTO(submissionDTO, geKoResultDTO);
		}
		return geKoResultDTO;
	}

	/**
	 * Convert an offerDTO to GeKoResultDTO.
	 *
	 * @param offerDTO
	 * @return GeKoResult DTO.
	 */
	private GeKoResultDTO convertOfferDTOtoGeKoResultDTO(OfferDTO offerDTO) {
		GeKoResultDTO geKoResultDTO = new GeKoResultDTO();
		geKoResultDTO.setId(offerDTO.getId());
		geKoResultDTO.setSubmittent(offerDTO.getSubmittent());
		return getGeKoResultDTO(offerDTO.getSubmittent().getSubmissionId(), geKoResultDTO);
	}

	private GeKoResultDTO getGeKoResultDTO(SubmissionDTO submissionDTO, GeKoResultDTO geKoResultDTO) {
		geKoResultDTO.setProject(submissionDTO.getProject());
		geKoResultDTO.setWorkType(submissionDTO.getWorkType());
		geKoResultDTO.setDescription(submissionDTO.getDescription());
		geKoResultDTO.setGattTwo(submissionDTO.getGattTwo());
		geKoResultDTO.setPublicationDate(submissionDTO.getPublicationDate());
		geKoResultDTO.setPublicationDateDirectAward(submissionDTO.getPublicationDateDirectAward());
		geKoResultDTO.setPublicationDateAward(submissionDTO.getPublicationDateAward());
		geKoResultDTO.setFirstDeadline(submissionDTO.getFirstDeadline());
		geKoResultDTO.setSecondDeadline(submissionDTO.getSecondDeadline());
		geKoResultDTO.setApplicationOpeningDate(submissionDTO.getApplicationOpeningDate());
		geKoResultDTO.setOfferOpeningDate(submissionDTO.getOfferOpeningDate());
		geKoResultDTO.setNotes(submissionDTO.getNotes());
		geKoResultDTO.setStatus(submissionDTO.getStatus());
		geKoResultDTO.setReasonFreeAward(submissionDTO.getReasonFreeAward());
		geKoResultDTO.setSubmissionCancel(submissionDTO.getSubmissionCancel());
		geKoResultDTO.setCommissionProcurementProposalDate(submissionDTO.getCommissionProcurementProposalDate());
		geKoResultDTO.setProcess(submissionDTO.getProcess());
		geKoResultDTO.setAboveThreshold(submissionDTO.getAboveThreshold());
		return geKoResultDTO;
	}
}
