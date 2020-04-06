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

import ch.bern.submiss.services.api.administration.LexiconService;
import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.KeyService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class LexiconServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {LexiconService.class})
@Singleton
public class LexiconServiceImpl implements LexiconService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(LexiconServiceImpl.class.getName());

  /**
   * The lexicon group service.
   */
  @OsgiService
  @Inject
  private GroupService lexiconGroupService;

  /**
   * The lexicon key service.
   */
  @OsgiService
  @Inject
  private KeyService lexiconKeyService;

  /**
   * The language service.
   */
  @OsgiService
  @Inject
  private LanguageService languageService;

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LexiconService#isLanguageAvailableForLocale(java.
   * lang.String)
   */
  @Override
  public boolean isLanguageAvailableForLocale(String locale) {

    LOGGER.log(Level.CONFIG,
      "Executing method isLanguageAvailableForLocale, Parameters: locale: {0}",
      locale);

    return languageService.getLanguageByLocale(locale) != null;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LexiconService#getGroupByName(java.lang.String)
   */
  @Override
  public GroupDTO getGroupByName(String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method getGroupByName, Parameters: groupName: {0}",
      groupName);

    return lexiconGroupService.getGroupByName(groupName);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LexiconService#getLastUpdateForLocale(java.lang.
   * String, java.lang.String)
   */
  @Override
  public long getLastUpdateForLocale(String groupId, String locale) {

    LOGGER.log(Level.CONFIG,
      "Executing method getLastUpdateForLocale, Parameters: groupId: {0}, "
        + "locale: {1}",
      new Object[]{groupId, locale});

    return lexiconGroupService.getLastUpdateDateForLocale(groupId, locale);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LexiconService#getTranslations(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Map<String, String> getTranslations(String groupId, String locale) {

    LOGGER.log(Level.CONFIG,
      "Executing method getLastUpdateForLocale, Parameters: groupId: {0}, "
        + "locale: {1}",
      new Object[]{groupId, locale});

    return lexiconKeyService.getTranslationsForGroupAndLocale(groupId, locale);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.LexiconService#getActiveLanguages()
   */
  @Override
  public List<LanguageDTO> getActiveLanguages() {

    LOGGER.log(Level.CONFIG, "Executing method getActiveLanguages");

    return languageService.getLanguages(false);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LexiconService#getTranslation(java.lang.String,
   * java.lang.String)
   */
  @Override
  public String getTranslation(String keyName, String locale) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTranslation, Parameters: keyName: {0}, "
        + "locale: {1}",
      new Object[]{keyName, locale});

    return lexiconKeyService.getTranslation(keyName, locale);
  }
}
