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
 * @name report.route.js
 * @description report routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.report")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars
    $stateProvider.state("report", {
      abstract: true,
      url: "/report",
      template: "<ui-view/>",
      data: {
        isPublic: false
      },
    });

    $stateProvider.state("report.generateReport", {
      url: "/generate-report",
      controller: "ReportViewController",
      controllerAs: "reportViewCtr",
      templateUrl: "app/report/reportView.html",
      data: {
        isPublic: false
      },
      ncyBreadcrumb: {
        label: 'Auswertungen',
        parent: 'start'
      }
    });
    $stateProvider.state("report.generateOperationReport", {
      url: "/generate-operationReport",
      controller: "ReportViewController",
      controllerAs: "reportViewCtr",
      templateUrl: "app/report/operationReportView.html",
      data: {
        isPublic: false
      },
      ncyBreadcrumb: {
        label: 'GEKO',
        parent: 'start'
      }
    });
  }
})();
