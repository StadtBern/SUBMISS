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

  var app = angular.module("submiss");
  app.run(runBlock);
  app.run(runBlockEnums);

  /** @ngInject */
  function runBlock($log, AppConstants, $cookies, $window, AppService,
    $transitions, $rootScope, $state, $location, $q, $timeout, $http,
    UsersService) {
    // Do not allow the application to access a protected state
    // without having the user initiated its SSO session.
    $transitions.onBefore({}, function (transition) {
      $log.debug('Intercepted changing state to: ', transition.to());
      if (transition.to().data && !transition.to().data.isPublic) {
        $log.debug('Requested state is private.');
        // Check if the user has the SSO cookie.
        if (!$cookies.get(AppConstants.SECURITY.SSO_COOKIE_NAME)) {
          $log.debug('User does not have an SSO cookie, redirecting' +
            ' to authenticate.');
          // Before redirecting the user, keep the originally requested URL
          // so that we can forward the user back to it after authentication
          // takes place.
          $cookies.put(AppConstants.SECURITY.REDIRECT_TO_COOKIE_NAME,
            $window.location);
          $window.location.href = AppConstants.SECURITY.SSO_AUTH_ENDPOINT;
          return false;
        } else if (!AppService.getLoggedUser()) {
          // This is not a user we have previously checked, so
          // we need to check the status of this user on the project's database
          // and decide accordingly.
          $log.debug('User has not logged in previously, proceeding with user ' +
            'checks.');
          return UsersService.getUserStatus().then(
            function success(response) {
              $log.debug('User status: ' + response.data);
              switch (response.data) {
                case AppConstants.USER_STATUS.DOES_NOT_EXIST:
                  $state.go("users.create");
                  return false;
                case AppConstants.USER_STATUS.ENABLED_NOT_APPROVED:
                case AppConstants.USER_STATUS.DISABLED:
                  $state.go("users.info-pending");
                  return false;
                case AppConstants.USER_STATUS.DISABLED_APPROVED:
                  $state.go("users.info-disabled");
                  return false;
                default:
                  return UsersService.getUser()
                    .then(function success(response) {
                      // At this point we should get the profile of the user.
                      // During profile retrieval, the system checks whether the
                      // user has acquired a new department from the time it was
                      // approved; if that's the case, the user automatically
                      // becomes disabled and re-approval is required.
                      if (response.data.status !==
                        AppConstants.USER_STATUS.ENABLED_APPROVED) {
                        $state.go("users.info-disabled");
                      } else {
                        AppService.setLoggedUser(response.data);
                      }
                    })
              }
            }
          );
        }
      }

      $log.debug('Run block run successfully.');

    });
  }

  function runBlockEnums(AppConstants, AppService, $locale) {
    angular.copy(AppConstants.locales['de'], $locale);
    AppService.getTenderStatus()
      .then(function success(response) {
        AppConstants.STATUS = response.data;
      });
    AppService.getTaskTypes()
      .then(function success(response) {
        AppConstants.TASK_TYPES = response.data;
      });
    AppService.getSDCategoryTypes()
      .then(function success(response) {
        AppConstants.CategorySD = response.data;
      });
  }
})();
