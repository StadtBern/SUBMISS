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
 * @name report.service.js
 * @description ReportService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.report")
    .factory("ReportService", ReportService);

  /** @ngInject */
  function ReportService($http, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Local functions.
     **********************************************************************/

    /***********************************************************************
     * Exported functions and variables.
     **********************************************************************/

    return {
      generateReport: function (report) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/report/generate', report, {
          responseType: 'arraybuffer',
          headers: {
            'Accept': 'application/octet-stream'
          }
        });
      },
      validateForm: function (report) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/report/validateForm', report);
      },
      validateOperationForm: function (report) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/report/validateOperationForm', report);
      },
      generateOperationReport: function (report, page, pageItems, sortColumn, sortType) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/report/operationGenerate/' + page + '/' + pageItems + '/' + sortColumn + '/' + sortType, report);
      },
      downloadOperationReport: function (report, columnList, caseFormat, sumAmount) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/report/operationExport/' + columnList + '/' + caseFormat + '/' + sumAmount, report, {
          responseType: 'arraybuffer',
          headers: {
            'Accept': 'application/octet-stream'
          }
        });
      },
      getOperationReportCalculationResults: function (report) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/report/operation/results', report);
      },
      loadReport: function (reportType) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/report/loadReport/' + reportType);
      }
    };
  }
})();
