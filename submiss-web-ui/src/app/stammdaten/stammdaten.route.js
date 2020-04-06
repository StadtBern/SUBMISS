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
 * @ngdoc object
 * @name stammdaten.route.js
 * @description stammdaten routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars
    $stateProvider.state("stammdaten", {
        abstract: true,
        url: "/referenceData",
        template: "<ui-view/>",
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: 'Stammdaten verwalten',
          parent: 'start'
        }
      })

      .state("stammdaten.overview", {
        url: "/overview",
        controller: "StammdatenOverviewController",
        controllerAs: "stammdatenOverviewCtrl",
        templateUrl: "app/stammdaten/stammdatenOverview.html",
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: 'Stammdaten verwalten',
          parent: 'start'
        }
      })

      .state("stammdaten.typeData", {
        url: "/overview/:type",
        controller: "TypeDataController",
        controllerAs: "typeDataCtrl",
        templateUrl: "app/stammdaten/typeData.html",
        params: {
          type: null,
          typeName: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{typeDataCtrl.typeName}}',
          parent: 'stammdaten.overview'
        }
      })

      .state("stammdaten.process", {
        url: "/overviewPr/:type",
        controller: "ProcessSDController",
        controllerAs: "processSDCtrl",
        templateUrl: "app/stammdaten/processSD.html",
        params: {
          type: null,
          typeName: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{processSDCtrl.typeName}}',
          parent: 'stammdaten.overview'
        }
      })

      .state("stammdaten.authorisedSignatory", {
        url: "/overviewAS/:type",
        controller: "AuthorisedSignatoryController",
        controllerAs: "authorisedSignatoryCtrl",
        templateUrl: "app/stammdaten/authorisedSignatory.html",
        params: {
          type: null,
          typeName: null,
          directorate: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{authorisedSignatoryCtrl.typeName}}',
          parent: 'stammdaten.overview'
        }
      });
  }
})();
