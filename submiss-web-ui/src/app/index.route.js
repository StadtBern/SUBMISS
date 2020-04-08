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

  angular.module("submiss").config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider, $urlRouterProvider, $locationProvider,
    AppConstants) {
    /** Enable HTML5 mode (requires server URL rewriting). */
    $locationProvider.html5Mode({
      enabled: false,
      requireBase: false
    });

    $stateProvider
    .state(
      "403", {
        url: "/forbidden",
        controller: "InfoScreenController",
        controllerAs: "infoScreenCtr",
        templateUrl: "app/layout/info-screen.html",
        data: {
          isPublic: true,
          title: "Keine Zugriffberechtigung",
          body: "Sie haben bedauerlicherweise keine Zugangsberechtigung. "
            + "Bitte navigieren Sie zurück auf die vorherige Seite oder "
            + "auf die Suche, um nach einer Submisison/Projekt zu suchen.",
          icon: "fa fa-exclamation-triangle fa-3x"
        },
        ncyBreadcrumb: {
          skip: true
        }
      })


    /** Default route. The function-hack is necessary as preventing an
     * event in the default route causes ui-router to go to an infinite
     * digest loop. (see: https://github.com/angular-ui/ui-router/issues/600)
     */
    $urlRouterProvider.otherwise(function ($injector) {
      var $state = $injector.get("$state");
      $state.go("login");
    });
  }
})();
