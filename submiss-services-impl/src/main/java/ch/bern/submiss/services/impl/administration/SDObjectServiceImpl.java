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
import com.querydsl.jpa.impl.JPAQueryFactory;
import ch.bern.submiss.services.api.administration.SDObjectService;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.model.MasterListEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;

/**
 * The Class SDObjectServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDObjectService.class})
@Singleton
public class SDObjectServiceImpl extends BaseService implements SDObjectService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDObjectServiceImpl.class.getName());

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDObjectService#readAll()
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
            .equalsIgnoreCase("Objekt"),
          qMasterListValueHistoryEntity.toDate.isNull(),
          qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();

    return MasterListValueHistoryMapper.INSTANCE
      .toMasterListValueHistoryDTO(masterListValueEntitiesH);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDObjectService#getObjectsByProjectsIDs(java.util.
   * List)
   */
  @Override
  public List<MasterListValueHistoryDTO> getObjectsByProjectsIDs(List<String> projectsIDs) {

    LOGGER.log(Level.CONFIG,
      "Executing method getObjectsByProjectsIDs. Parameters: projectsIDs: {0}",
      projectsIDs);

    JPAQueryFactory query = new JPAQueryFactory(em);
    QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;
    QProjectEntity qProjectEntity = QProjectEntity.projectEntity;

    List<MasterListValueHistoryEntity> masterListValueEntitiesH =
      query.select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(
          qMasterListValueHistoryEntity.masterListValueId.masterList.name
            .equalsIgnoreCase("Objekt"),
          qMasterListValueHistoryEntity.toDate.isNull(),
          qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())),
          qMasterListValueHistoryEntity.masterListValueId.id
            .in(query.select(qProjectEntity.objectName.id).from(qProjectEntity)
              .where(qProjectEntity.id.in(projectsIDs)).fetch()))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();

    return MasterListValueHistoryMapper.INSTANCE
      .toMasterListValueHistoryDTO(masterListValueEntitiesH);
  }
}
