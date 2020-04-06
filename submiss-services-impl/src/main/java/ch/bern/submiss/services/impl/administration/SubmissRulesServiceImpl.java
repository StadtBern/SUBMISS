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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.querydsl.jpa.impl.JPAQuery;

import ch.bern.submiss.services.api.administration.SubmissRulesService;
import ch.bern.submiss.services.api.dto.SubmissRulesDTO;
import ch.bern.submiss.services.impl.mappers.SubmissRulesDTOMapper;
import ch.bern.submiss.services.impl.model.QSubmissRulesEntity;
import ch.bern.submiss.services.impl.model.SubmissRulesEntity;


@Transactional
@OsgiServiceProvider(classes = {SubmissRulesService.class})
@Singleton
public class SubmissRulesServiceImpl extends BaseService implements SubmissRulesService {

  private static final Logger LOGGER = Logger.getLogger(SubmissRulesServiceImpl.class.getName());

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissRulesService#readRule(java.lang.String)
   */
  @Override
  public SubmissRulesDTO getSubmissRuleByCode(String code) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissRuleByCode, Parameters: code: {0}",
      code);

    JPAQuery<SubmissRulesDTO> query = new JPAQuery<>(em);
    QSubmissRulesEntity qSubmissRulesEntity = QSubmissRulesEntity.submissRulesEntity;
    SubmissRulesEntity submissRules = query.select(qSubmissRulesEntity).from(qSubmissRulesEntity)
      .where(qSubmissRulesEntity.code.eq(code)).fetchOne();

    return SubmissRulesDTOMapper.INSTANCE.toSubmissRulesDTO(submissRules);
  }
}
