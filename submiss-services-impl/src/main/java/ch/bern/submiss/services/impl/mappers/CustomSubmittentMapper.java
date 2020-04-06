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
import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.impl.model.SubmittentEntity;

/**
 * Mapper for Submittent
 */
@Mapper(uses = { CompanyMapper.class, SubmissionMapper.class })
public abstract class CustomSubmittentMapper {

	public static final CustomSubmittentMapper INSTANCE = Mappers.getMapper(CustomSubmittentMapper.class);

	public abstract SubmittentEntity toSubmittent(SubmittentDTO dto);

	public abstract List<SubmittentEntity> toSubmittent(List<SubmittentDTO> dtoList);

	public SubmittentDTO toSubmittentDTO(SubmittentEntity entity) {
		SubmittentDTO submittentDTO = new SubmittentDTO();

		submittentDTO.setId(entity.getId());
		submittentDTO.setCompanyId(CompanyMapper.INSTANCE.toCompanyDTO(entity.getCompanyId()));
		submittentDTO.setSubmissionId(CustomSubmissionMapper.INSTANCE.toCustomSubmissionDTO(entity.getSubmissionId(),null,null,null,null,null,null));
		submittentDTO.setSubcontractors(CompanyMapper.INSTANCE.toCompanyDTO(entity.getSubcontractors()));
		submittentDTO.setJointVentures(CompanyMapper.INSTANCE.toCompanyDTO(entity.getJointVentures()));
		submittentDTO.setExistsExclusionReasons(entity.getExistsExclusionReasons());
		submittentDTO.setFormalExaminationFulfilled(entity.getFormalExaminationFulfilled());
		submittentDTO.setSortOrder(entity.getSortOrder());
		submittentDTO.setProofDocPending(entity.getProofDocPending());
		submittentDTO.setIsApplicant(entity.getIsApplicant());

		return submittentDTO;

	}

	public List<SubmittentDTO> toSubmittentDTO(List<SubmittentEntity> submittentEntities) {
		if (submittentEntities == null) {
			return Collections.emptyList();
		}

		List<SubmittentDTO> list = new ArrayList<>();
		for (SubmittentEntity submittentEntity : submittentEntities) {
			list.add(toSubmittentDTO(submittentEntity));
		}

		return list;
	}
}
