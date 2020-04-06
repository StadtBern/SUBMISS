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

package ch.bern.submiss.services.api.administration;

import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import java.util.List;
import java.util.Map;

/**
 * The Lexicon service provides lexicon-related functionality, acting as a front-end to the
 * underlying Lexicon services of QLACK2 Fuse Lexicon module.
 */
public interface LexiconService {

  /**
   * Checks if a language for the requested locale is available in the Lexicon of the application.
   *
   * @param locale The locale to check for.
   * @return Returns whether the language is available or not.
   */
  boolean isLanguageAvailableForLocale(String locale);

  /**
   * Returns a translation group from the Lexicon.
   *
   * @param groupName The name of the group to return.
   * @return Returns the group details if found.
   */
  GroupDTO getGroupByName(String groupName);

  /**
   * Returns a timestamp of the last date in which a modification took part for a particular group
   * and locale.
   *
   * @param groupId The Group to check for.
   * @param locale The locale of the group to check for.
   * @return Returns a timestamp on which last modification took place.
   */
  long getLastUpdateForLocale(String groupId, String locale);

  /**
   * Returns the translations for a specific group and locale.
   *
   * @param groupId The group to return the translations for.
   * @param locale The locale to return the translations for.
   * @return Returns the set of translations for the group and locale.
   */
  Map<String, String> getTranslations(String groupId, String locale);

  /**
   * Returns all languages currently active in the underlying Lexicon.
   *
   * @return Returns the set of languages currently active in the Lexicon.
   */
  List<LanguageDTO> getActiveLanguages();

  /**
   * Returns the translation for specific key and locale.
   * 
   * @param keyName The key to return the translation for.
   * @param locale The locale to return the translation for.
   * @return The translation for the key and locale.
   */
  String getTranslation(String keyName, String locale);
}
