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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.querydsl.jpa.impl.JPAQueryFactory;

import ch.bern.submiss.services.api.administration.UserHistoryService;
import ch.bern.submiss.services.api.dto.UserHistoryDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.mappers.UserHistoryDTOMapper;
import ch.bern.submiss.services.impl.model.QUserHistoryEntity;
import ch.bern.submiss.services.impl.model.UserHistoryEntity;

@Transactional
@OsgiServiceProvider(classes = {UserHistoryService.class})
@Singleton
public class UserHistoryServiceImpl extends BaseService implements UserHistoryService {

  private static final Logger LOGGER = Logger.getLogger(UserHistoryServiceImpl.class.getName());

  QUserHistoryEntity qUserHistoryEntity = QUserHistoryEntity.userHistoryEntity;

  @Override
  public void handleUserHistory(String userId, String groupId, int userStatus) {

    LOGGER.log(Level.CONFIG,
      "Executing method handleUserHistory, Parameters: userId: {0}, groupId: {1}, "
        + "userStatus: {2}",
      new Object[]{userId, groupId, userStatus});

    // Find the latest user history entry.
    UserHistoryEntity userHistoryEntity = new JPAQueryFactory(em).selectFrom(qUserHistoryEntity)
      .where(qUserHistoryEntity.userId.eq(userId), qUserHistoryEntity.toDate.isNull()).fetchOne();
    if (userHistoryEntity != null) {
      // If the entry exists, check if the user group is not the same as the one given or if the
      // user status is not ENABLED_APPROVED.
      if (!userHistoryEntity.getGroupId().equals(groupId)
        || userStatus != LookupValues.USER_STATUS.ENABLED_APPROVED.getValue()) {
        // Set the current date to the toDate entry property and save the entry.
        userHistoryEntity.setToDate(new Timestamp(new Date().getTime()));
        em.merge(userHistoryEntity);
        // Now that the old entry is added to the history, create its new instance (if the user
        // status is ENABLED_APPROVED).
        if (userStatus == LookupValues.USER_STATUS.ENABLED_APPROVED.getValue()) {
          createNewHistoryEntry(userId, groupId);
        }
      }
    } else {
      // If a history entry does not exist, create one (if the user status is ENABLED_APPROVED).
      if (userStatus == LookupValues.USER_STATUS.ENABLED_APPROVED.getValue()) {
        createNewHistoryEntry(userId, groupId);
      }
    }
  }

  /**
   * Creates a new history entry.
   *
   * @param userId the user id
   * @param groupId the group id
   */
  private void createNewHistoryEntry(String userId, String groupId) {

    LOGGER.log(Level.CONFIG,
      "Executing method createNewHistoryEntry, Parameters: userId: {0}, groupId: {1}",
      new Object[]{userId, groupId});

    UserHistoryEntity userHistoryEntity = new UserHistoryEntity();
    userHistoryEntity.setFromDate(new Timestamp(new Date().getTime()));
    userHistoryEntity.setGroupId(groupId);
    userHistoryEntity.setToDate(null);
    userHistoryEntity.setUserId(userId);
    em.persist(userHistoryEntity);
  }

  @Override
  public List<UserHistoryDTO> getUserHistoryEntriesByUserIds(List<String> userIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method getUserHistoryEntriesByUserIds, Parameters: userIds: {0}",
      userIds);

    return UserHistoryDTOMapper.INSTANCE.toUserHistoryDTOs(new JPAQueryFactory(em)
      .selectFrom(qUserHistoryEntity).where(qUserHistoryEntity.userId.in(userIds)).fetch());
  }
}
