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

package ch.bern.submiss.services.impl.administration;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import com.querydsl.jpa.impl.JPAQuery;

import ch.bern.submiss.services.api.administration.SDVatService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.model.MasterListEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;

/**
 * The Class SDVatServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDVatService.class})
@Singleton
public class SDVatServiceImpl extends BaseService implements SDVatService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDVatServiceImpl.class.getName());

  @Inject
  private CacheBean cacheBean;

  @Inject
  private SubmissionService submissionService;

  /**
   * The Constant VT1.
   */
  private static final String VT1 = "VT1";

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDVatService#readAll()
   */
  @Override
  public List<MasterListValueHistoryDTO> readAll() {

    LOGGER.log(Level.CONFIG, "Executing method readAll");

    JPAQuery<MasterListEntity> query = new JPAQuery<>(em);
    QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;

    List<MasterListValueHistoryEntity> masterListValueEntitiesH =
      query.select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(
          qMasterListValueHistoryEntity.masterListValueId.masterList.name
            .equalsIgnoreCase("MwSt-Satz"),
          qMasterListValueHistoryEntity.active.isTrue(),
          qMasterListValueHistoryEntity.toDate.isNull(),
          qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())))
        .orderBy(qMasterListValueHistoryEntity.value2.asc()).fetch();

    return MasterListValueHistoryMapper.INSTANCE
      .toMasterListValueHistoryDTO(masterListValueEntitiesH);
  }

  @Override
  public MasterListValueHistoryDTO readVatBySubmission(String submissionId, String vatId) {
    Timestamp date = submissionService.getDateOfCompletedOrCanceledStatus(submissionId);
    Timestamp creationDate = submissionService.getDateOfCreationStatusOfSubmission(submissionId);
    return cacheBean.getValue(CategorySD.VAT_RATE.getValue(), vatId, date, creationDate, null);

  }

  @Override
  public MasterListValueHistoryDTO getCurrentMainVatRate() {
    for (MasterListValueHistoryDTO vatRate : readAll()) {
      if (StringUtils.isNotBlank(vatRate.getShortCode()) && vatRate.getShortCode().equals(VT1)) {
        return vatRate;
      }
    }
    return null;
  }
}
