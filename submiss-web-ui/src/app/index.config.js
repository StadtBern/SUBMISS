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

(function () {
  "use strict";

  angular.module("submiss").config(config);

  /** @ngInject */
  function config(AppConstants,
    $translateProvider, $httpProvider, flowFactoryProvider, toastrConfig,
    uibDatepickerConfig, uibDatepickerPopupConfig, $logProvider) {

    /** Enable logging */
    $logProvider.debugEnabled(true);

    /** Configure translations */
    $translateProvider
      .determinePreferredLanguage()
      .fallbackLanguage("de-ch")
      .useLocalStorage()
      .useSanitizeValueStrategy("escaped")
      .useUrlLoader(AppConstants.URLS.RESOURCE_PROVIDER +
        "/public/lexicon/translations");

    // To avoid the aggressive caching made by IE for GET requests
    // If Internet Explorer
    if (navigator.userAgent.match(/Trident.*rv\:11\./)) {
      // Initialize get if not there
      if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
      }

      // Disable IE request caching
      $httpProvider.defaults.headers.get["If-Modified-Since"] = "Mon, 26 Jul 1997 05:00:00 GMT";
      $httpProvider.defaults.headers.get["Cache-Control"] = "no-cache";
      $httpProvider.defaults.headers.get["Pragma"] = "no-cache";
    }

    /** Configure ng-flow. */
    flowFactoryProvider.defaults = {
      target: AppConstants.URLS.RESOURCE_PROVIDER + "/file-upload/upload",
      maxChunkRetries: 3,
      chunkRetryInterval: 5000,
      simultaneousUploads: 3,
      chunkSize: 2097152,
      successStatuses: [200, 201, 202],
      permanentErrors: [404, 403, 415, 500, 501],
      generateUniqueIdentifier: function () {
        return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,
          function (c) {
            var r, v;
            r = Math.random() * 16 | 0;
            v = (c === "x" ? r : r & 0x3 | 0x8);
            return v.toString(16);
          });
      }
    };

    angular.extend(
      uibDatepickerConfig, {
        showWeeks: false,
        startingDay: 1
      }
    );

    angular.extend(
      uibDatepickerPopupConfig, {
        datepickerPopup: 'dd.MM.yyyy',
        showButtonBar: true,
        currentText: "Heute",
        clearText: "Löschen",
        closeText: "Schließen"
      }
    );

    // Configure Toastr.
    angular.extend(toastrConfig, {
      autoDismiss: true,
      newestOnTop: true,
      positionClass: "toast-top-right",
      preventDuplicates: false,
      preventOpenDuplicates: false,
      progressBar: true,
      closeButton: true
    });

    // Interceptor to handle the case in which the SSO Cookie exists
    // but the SSO Session is terminated
    $httpProvider.interceptors.push(
      ["$window", "$cookies", "AppConstants", "$q", "$location",
        function ($window, $cookies, AppConstants, $q, $location) {
          return {
            "response": function (response) {
              if (response && angular.isString(response.data) &&
                response.data.indexOf('SAMLRequest') >= 0) {
                console.log("SAMLREquest");
                $cookies.remove(AppConstants.SECURITY.SSO_COOKIE_NAME);
                $cookies.remove('RelayState');
                $cookies.put(AppConstants.SECURITY.REDIRECT_TO_COOKIE_NAME,
                  $window.location);
                $window.location.href = AppConstants.SECURITY.SSO_AUTH_ENDPOINT;
                return $q.reject();
              } else {
                return response;
              }

            },
            'responseError': function (rejection) {
              // do something on error
              if (rejection.status === 403) {
                $location.path('forbidden');
                return rejection;
              } else {
                throw rejection;
              }
            }
          };
        }
      ]);
  }

})();
