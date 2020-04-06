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

/**
 * @ngdoc service
 * @name i18n.service.js
 * @description I18N service factory
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .factory("I18NService", I18NService);

  /** @ngInject */
  function I18NService($http, AppConstants, $translate) {
    /***********************************************************************
     * Exported functions and variables.
     **********************************************************************/
    return {
      /** Returns all languages available in the database of Lexicon
       * service.
       */
      getAvailableLanguages: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/lexicon/languages');
      },

      /** Returns the language which is currently active (i.e. the one
       *  the $translate service is using to render messages) */
      getCurrentLanguage: function () {
        return $translate.use() ||
          $translate.storage().get($translate.storageKey()) ||
          $translate.preferredLanguage()
      },

      /** Changes the current language to the given locale */
      switchLanguage: function (locale) {
        $translate.use(locale);
      }
    };
  }
})();
