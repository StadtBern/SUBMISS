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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import com.querydsl.jpa.impl.JPAQuery;
import ch.bern.submiss.services.api.administration.SDILOService;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.model.MasterListEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;

/**
 * The Class SDILOServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDILOService.class})
@Singleton
public class SDILOServiceImpl extends BaseService implements SDILOService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDILOServiceImpl.class.getName());

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDILOService#readAll()
   */
  @Override
  public List<MasterListValueHistoryDTO> readAll() {

    LOGGER.log(Level.CONFIG, "Executing method readAll");

    JPAQuery<MasterListEntity> query = new JPAQuery<>(em);
    QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;

    List<MasterListValueHistoryEntity> masterListValueEntitiesH =
      query.select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId.masterList.name
          .equalsIgnoreCase("ILO"), qMasterListValueHistoryEntity.toDate.isNull())
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();

    return MasterListValueHistoryMapper.INSTANCE
      .toMasterListValueHistoryDTO(masterListValueEntitiesH);
  }
}
