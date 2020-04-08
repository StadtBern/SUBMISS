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

import ch.bern.submiss.services.api.administration.ProcedureService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProcedureHistoryDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.ProcedureHistoryDTOMapper;
import ch.bern.submiss.services.impl.model.ProcedureHistoryEntity;
import ch.bern.submiss.services.impl.model.QProcedureHistoryEntity;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.jpa.impl.JPAQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class ProcedureServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {ProcedureService.class})
@Singleton
public class ProcedureServiceImpl extends BaseService implements ProcedureService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(ProcedureServiceImpl.class.getName());
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;
  @Inject
  private AuditBean audit;

  @Override
  public ProcedureHistoryDTO readProcedure(String processType, Process proccess, String tenant) {

    LOGGER.log(Level.CONFIG,
      "Executing method readProcedure, Parameters: processType: {0}, "
        + "process: {1}, tenant: {2}",
      new Object[]{processType, proccess, tenant});

    JPAQuery<ProcedureHistoryDTO> query = new JPAQuery<>(em);
    QProcedureHistoryEntity qProcedureHistoryEntity =
      QProcedureHistoryEntity.procedureHistoryEntity;
    ProcedureHistoryEntity procedureH = query.select(qProcedureHistoryEntity)
      .from(qProcedureHistoryEntity)
      .where(qProcedureHistoryEntity.tenant.id.eq(tenant),
        qProcedureHistoryEntity.processType.id.eq(processType), qProcedureHistoryEntity.process
          .eq(proccess).and(qProcedureHistoryEntity.toDate.isNull()))
      .fetchOne();
    return ProcedureHistoryDTOMapper.INSTANCE.toProcedureHistoryDTO(procedureH);
  }

  @Override
  public List<ProcedureHistoryDTO> readProceduresByTenant() {

    LOGGER.log(Level.CONFIG, "Executing method readProceduresByTenant");

    JPAQuery<ProcedureHistoryDTO> query = new JPAQuery<>(em);
    QProcedureHistoryEntity qProcedureHistoryEntity =
      QProcedureHistoryEntity.procedureHistoryEntity;
    List<ProcedureHistoryEntity> procedureHList = query.select(qProcedureHistoryEntity)
      .from(qProcedureHistoryEntity)
      .where(qProcedureHistoryEntity.tenant.id
        .eq(usersService.getUserById(getUser().getId()).getTenant().getId())
        .and(qProcedureHistoryEntity.processType.masterListValueHistory.any().toDate.isNull())
        .and(qProcedureHistoryEntity.toDate.isNull()))
      .orderBy(qProcedureHistoryEntity.process.asc())
      .orderBy(qProcedureHistoryEntity.processType.masterListValueHistory.any().value1.asc())
      .fetch();
    List<ProcedureHistoryDTO> procedureHListDTO = new ArrayList<>();
    Map<String, MasterListValueHistoryDTO> activeSD = new HashMap<>();
    activeSD.putAll(cacheBean.getActiveSD().row(CategorySD.PROCESS_TYPE.getValue()));

    for (ProcedureHistoryEntity p : procedureHList) {
      procedureHListDTO
        .add(ProcedureHistoryDTOMapper.INSTANCE.toProcedureHistoryDTOcustom(p, activeSD));
    }
    // custom Sorting Results using Custom Sorting
    return sortProcedureResults(procedureHListDTO);
  }

  @Override
  public ProcedureHistoryDTO readProcedureById(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method readProcedureById, Parameters: id: {0}",
      id);

    JPAQuery<ProcedureHistoryDTO> query = new JPAQuery<>(em);
    QProcedureHistoryEntity qProcedureHistoryEntity =
      QProcedureHistoryEntity.procedureHistoryEntity;
    ProcedureHistoryEntity procedureH = query.select(qProcedureHistoryEntity)
      .from(qProcedureHistoryEntity).where(qProcedureHistoryEntity.id.eq(id)).fetchOne();
    return ProcedureHistoryDTOMapper.INSTANCE.toProcedureHistoryDTO(procedureH);
  }

  @Override
  public Set<ValidationError> insertNewProcedure(ProcedureHistoryDTO dto) {

    LOGGER.log(Level.CONFIG,
      "Executing method insertNewProcedure, Parameters: dto: {0}",
      dto);

    Set<ValidationError> error = new HashSet<>();
    // In this part we deactivate the old value by updating
    ProcedureHistoryEntity procedureHistEntity = em.find(ProcedureHistoryEntity.class, dto.getId());
    // If the current version is 1, then return an optimisticLockErrorField
    if (procedureHistEntity.getVersion() == 1) {
      error
        .add(new ValidationError("optimisticLockErrorField", ValidationMessages.OPTIMISTIC_LOCK));
      return error;
    }
    procedureHistEntity.setToDate(new Timestamp(new Date().getTime()));
    procedureHistEntity.setActive(false);
    em.merge(procedureHistEntity);
    // Now that the old entry is added to the history, create its new instance.
    procedureHistEntity = ProcedureHistoryDTOMapper.INSTANCE.toProcedureHistory(dto);
    procedureHistEntity.setId(null);
    procedureHistEntity.setFromDate(new Timestamp(new Date().getTime()));
    procedureHistEntity.setToDate(null);
    em.persist(procedureHistEntity);

    audit.createLogAudit(AuditLevel.REFERENCE_DATA_LEVEL.name(), AuditEvent.CREATE.name(),
      AuditGroupName.REFERENCE_DATA.name(), AuditMessages.PROCEDURE_ADDED.name(), getUser().getId(),
      procedureHistEntity.getId(), null, null, LookupValues.INTERNAL_LOG);
    return error;
  }

  /**
   * Sort procedure results.
   *
   * @param procedureHListDTO the procedure H list DTO
   * @return the list
   */
  /*
   * This method is used to sort the results coming for the Verfahren section of the stammdaten in
   * order to show the correct order on the dynamic columns and rows
   */
  public List<ProcedureHistoryDTO> sortProcedureResults(
    List<ProcedureHistoryDTO> procedureHListDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method sortProcedureResults, Parameters: procedureHListDTO: {0}",
      procedureHListDTO);

    List<Process> definedOrder = new ArrayList<>();
    definedOrder.add(Process.NEGOTIATED_PROCEDURE);
    definedOrder.add(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION);
    definedOrder.add(Process.INVITATION);
    definedOrder.add(Process.OPEN);
    definedOrder.add(Process.SELECTIVE);

    Collections.sort(procedureHListDTO, (o1, o2) -> {
      int compareResult = o1.getProcessTypeHistory().getValue1()
        .compareTo(o2.getProcessTypeHistory().getValue1());
      if (compareResult == 0) {
        return Integer.valueOf(definedOrder.indexOf(o1.getProcess()))
          .compareTo(Integer.valueOf(definedOrder.indexOf(o2.getProcess())));
      } else {
        return compareResult;
      }
    });
    return procedureHListDTO;
  }
}
