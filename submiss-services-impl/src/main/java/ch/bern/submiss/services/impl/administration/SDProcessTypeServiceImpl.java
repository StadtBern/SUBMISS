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
import ch.bern.submiss.services.api.administration.SDProcessTypeService;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;

/**
 * The Class SDProcessTypeServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDProcessTypeService.class})
@Singleton
public class SDProcessTypeServiceImpl extends BaseService implements SDProcessTypeService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDProcessTypeServiceImpl.class.getName());

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDProcessTypeService#readAll(java.lang.String)
   */
  @Override
  public List<MasterListValueHistoryDTO> readAll(String projectId) {

    LOGGER.log(Level.CONFIG,
      "Executing method readAll, Parameters: projectId: {0}",
      projectId);

    QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;
    QProjectEntity qProjectEntity = QProjectEntity.projectEntity;

    String tenantId = new JPAQuery<>(em).select(qProjectEntity.tenant.id).from(qProjectEntity)
      .where(qProjectEntity.id.eq(projectId)).fetchOne();

    List<MasterListValueHistoryEntity> masterListValueEntitiesH =
      new JPAQuery<>(em).select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(
          qMasterListValueHistoryEntity.masterListValueId.masterList.name.equalsIgnoreCase(
            "Verfahrensart"),
          qMasterListValueHistoryEntity.active.isTrue(),
          qMasterListValueHistoryEntity.toDate.isNull(),
          qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();

    return MasterListValueHistoryMapper.INSTANCE
      .toMasterListValueHistoryDTO(masterListValueEntitiesH);
  }
}


