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
 * @name users.route.js
 * @description users routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.users").config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars
    $stateProvider.state("users", {
      abstract: true,
      url: "/users",
      template: "<ui-view/>",
      data: {
        isPublic: false
      }
    });

    $stateProvider.state("users.create", {
      url: "/create",
      controller: "UserCreateController",
      controllerAs: "userCreateCtr",
      templateUrl: "app/users/create/userCreate.html",
      data: {
        isPublic: true
      },
      ncyBreadcrumb: {
        skip: true
      }
    });

    $stateProvider.state("users.info-created-pending", {
      url: "/info-created-pending",
      controller: "InfoScreenController",
      controllerAs: "infoScreenCtr",
      templateUrl: "app/layout/info-screen.html",
      data: {
        isPublic: true,
        title: "Ihr Registrierungsantrag",
        body: "Ihr Registrierungsantrag wurde erfolgreich entgegengenommen. Sobald Ihr Antrag bearbeitet wurde," +
          " werden Sie entsprechend per E-Mail informiert.",
        icon: "fa fa-info-circle fa-3x"
      },
      ncyBreadcrumb: {
        skip: true
      }
    });

    $stateProvider.state("users.info-pending", {
      url: "/info-pending",
      controller: "InfoScreenController",
      controllerAs: "infoScreenCtr",
      templateUrl: "app/layout/info-screen.html",
      data: {
        isPublic: true,
        title: "Registrierungsantrag in Bearbeitung",
        body: "Ihr Registrierungsantrag befindet sich noch in Bearbeitung. Bitte warten Sie, " +
          "bis Sie über den Ausgang Ihres Antrags mittels E-Mail informiert werden.",
        icon: "fa fa-info-circle fa-3x"
      },
      ncyBreadcrumb: {
        skip: true
      }
    });

    $stateProvider.state("users.info-disabled", {
      url: "/info-disabled",
      controller: "InfoScreenController",
      controllerAs: "infoScreenCtr",
      templateUrl: "app/layout/info-screen.html",
      data: {
        isPublic: true,
        title: "Submiss Benutzerkonto deaktiviert",
        body: "Ihr Benutzerkonto wurde deaktiviert. " +
          "Für weitere Informationen kontaktieren Sie bitte Ihren Administrator.",
        icon: "fa fa-info-circle fa-3x"
      },
      ncyBreadcrumb: {
        skip: true
      }
    });

    $stateProvider.state("users.search", {
      url: "/search",
      controller: "UserSearchController",
      controllerAs: "userSearchCtr",
      templateUrl: "app/users/search/userSearch.html",
      ncyBreadcrumb: {
        label: 'Benutzer suchen',
        parent: 'start'
      }
    });

    $stateProvider.state("users.edit", {
      url: "/edit",
      controller: "UserEditController",
      controllerAs: "userEditCtr",
      templateUrl: "app/users/edit/userEdit.html",
      ncyBreadcrumb: {
        label: 'Benutzer bearbeiten',
        parent: 'start'
      }
    });

    $stateProvider.state("users.export", {
      url: "/export",
      controller: "UserExportController",
      controllerAs: "userExportCtr",
      templateUrl: "app/users/export/userExport.html",
      ncyBreadcrumb: {
        label: 'Export der Benutzerübersicht',
        parent: 'start'
      }
    });
  }
})();
