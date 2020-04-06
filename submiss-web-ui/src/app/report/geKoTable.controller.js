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
 * @ngdoc controller
 * @name projectUpdate.controller.js
 * @description ProjectCreateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project")
    .controller("GeKoTableController", GeKoTableController);

  /** @ngInject */
  function GeKoTableController($scope, StammdatenService,
    $uibModalInstance, $uibModal, AppConstants, reportForm,
    NgTableParams, ReportService, $filter, AppService, $location,
    $anchorScroll) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.reportForm = reportForm;
    vm.selectedColumns = [];
    vm.gekoResults = [];
    vm.direktionCriteria = [];
    vm.availableColumns = [{
      "id": 10,
      "name": "Alle auswählen"
    }, {
      "id": 11,
      "name": "GATT/WTO"
    }, {
      "id": 12,
      "name": "Publikation Zuschlag"
    }, {
      "id": 13,
      "name": "Publikation Absicht freihändige Vergabe"
    }, {
      "id": 14,
      "name": "Publikation"
    }, {
      "id": 15,
      "name": "Eingabetermin 1"
    }, {
      "id": 16,
      "name": "Bewerbungsöffnung"
    }, {
      "id": 17,
      "name": "Eingabetermin 2"
    }, {
      "id": 18,
      "name": "Offertöffnung"
    }, {
      "id": 19,
      "name": "Verfügungsdatum 1.Stufe"
    }, {
      "id": 20,
      "name": "BeKo-Sitzung"
    }, {
      "id": 21,
      "name": "Verfügungsdatum"
    }, {
      "id": 22,
      "name": "Zuschlagsempfänger"
    }, {
      "id": 23,
      "name": "S/O-Aufträge exkl.MWST"
    }, {
      "id": 24,
      "name": "E-Aufträge exkl.MWST"
    }, {
      "id": 25,
      "name": "F-Aufträge exkl.MWST"
    }, {
      "id": 26,
      "name": "Intern/Extern"
    }, {
      "id": 27,
      "name": "Bemerkungsfeld"
    }];
    vm.total;
    vm.soSum;
    vm.eSum;
    vm.fSum;
    vm.report;
    vm.amount;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.isNotUndefinedOrNull = isNotUndefinedOrNull;
    vm.isDateCriteriaAsked = isDateCriteriaAsked;
    vm.getDateInterval = getDateInterval;
    vm.getGeKoResults = getGeKoResults;
    vm.exportReport = exportReport;
    vm.formatAmount = formatAmount;
    vm.isColumnSelected = isColumnSelected;
    vm.sendAmounts = sendAmounts;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.getGeKoResults();
      getOperationReportCalculationResults();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/

    /** Prevent backspace from navigating back using jQuery. */
    $(document).on("keydown", function (e) {
      if (e.which === 8 && !$(e.target).is("input, textarea")) {
        e.preventDefault();
      }
    });

    function getOperationReportCalculationResults() {
      AppService.setPaneShown(true);
      ReportService.getOperationReportCalculationResults(vm.reportForm)
        .success(function (data) {
          vm.reportResults = data;
          AppService.setPaneShown(false);
        }).error(function (response, status) {});
    }

    function getGeKoResults() {
      vm.tableParams = new NgTableParams({
        page: 1,
        count: 10,
        sorting: {
          id: 'asc'
        }
      }, {
        getData: function ($defer, params) {
          if (vm.tableParams.page() != null && vm.tableParams.page() !== 0 &&
            vm.tableParams.count() !== null && vm.tableParams.count() !==
            0) {
            var sortColumn = '';
            var sortType = '';

            for (var k in vm.tableParams.sorting()) {
              sortColumn = k;
              sortType = vm.tableParams.sorting()[k];
            }

            return ReportService.generateOperationReport(vm.reportForm,
              vm.tableParams.page(), vm.tableParams.count(), sortColumn,
              sortType).then(function (reportResults) {
              params.total(reportResults.data.count);
              vm.report = vm.reportForm;
              getGesamttotal(reportResults.data.results);
              return reportResults.data.results;
            });
          }
        }
      });
      gotoSearchResults();
    }

    function gotoSearchResults() {
      // set the location.hash to the id of
      // the element you wish to scroll to.
      $anchorScroll('results');
    }

    // Checkss if a value is not undefined or null
    function isNotUndefinedOrNull(value) {
      return (value !== undefined && value !== null) && value.length !== 0;
    }

    // Checkss if column is selected
    function isColumnSelected(searchTerm) {
      if (vm.selectedColumns) {
        return vm.selectedColumns.some(function (el) {
          return el.name === searchTerm;
        });
      }
      return false;
    }

    // Gets total cost estimate of the whole result list
    function getGesamttotal(list) {
      var soSum = 0;
      var eSum = 0;
      var fSum = 0;
      if (list !== undefined && list !== null) {
        for (var i = 0; i < list.length; i++) {
          if (list[i] !== undefined && list[i] !== null) {
            soSum = addNotNullValue(list[i].soCalculation, soSum);
            eSum = addNotNullValue(list[i].eCalculation, eSum);
            fSum = addNotNullValue(list[i].fCalculation, fSum);
          }
        }
      }
      vm.soSum = soSum;
      vm.eSum = eSum;
      vm.fSum = fSum;
      vm.total = soSum + eSum + fSum;
    }

    /**Format number according to specifications */
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }

    // Add not null value to total sum
    function addNotNullValue(value, sum) {
      if (value !== undefined && value !== null) {
        sum = sum + value;
      }
      return sum;
    }

    // Exports geko results report
    function exportReport(caseFormat) {
      var selectedColumns = "0";
      for (var i = 0; i < vm.selectedColumns.length; i++) {
        selectedColumns = selectedColumns + ", " + vm.selectedColumns[i].id;
      }
      sendAmounts();
      AppService.setPaneShown(true);
      ReportService.downloadOperationReport(vm.report, selectedColumns,
          caseFormat, vm.amount)
        .success(function (response, status, headers) {
          checkForEmptyResults(response);
          AppService.setPaneShown(false);
          if (response.byteLength > 0) {
            var fileName = headers()["content-disposition"]
              .split(';')[1].trim().split('=')[1];
            var blob = new Blob([response], {
              type: "application/octet-stream"
            });
            // For Internet Explorer 11 only
            if (window.navigator &&
              window.navigator.msSaveOrOpenBlob) {
              window.navigator.msSaveOrOpenBlob(blob, fileName);
            } else {
              var objectUrl = URL.createObjectURL(blob);
              var a = $("<a style='display: none;'/>");
              a.attr("href", objectUrl);
              a.attr("download", fileName);
              $("body").append(a);
              a[0].click();
              window.URL.revokeObjectURL(objectUrl);
              a.remove();
            }
          }
        })
        .error(function (response, status) {});
    }

    // Function that check if no results returned from search
    function checkForEmptyResults(response) {
      if (response.byteLength === 0) {
        vm.noResultsReturned = true;
        $anchorScroll('errorScroll');
      }
    }

    // Add not null value to total sum
    function getDateInterval(dateFrom, dateTo) {
      var result = '';
      if (isNotUndefinedOrNull(dateFrom) && isNotUndefinedOrNull(dateTo)) {
        result = " " + formatDate(dateFrom) + ' - ' + formatDate(dateTo);
      } else if (isNotUndefinedOrNull(dateFrom)) {
        result = ' von ' + formatDate(dateFrom);
      } else {
        result = ' bis ' + formatDate(dateTo);
      }
      return result;
    }

    // Checks if one of from to dates are filled
    function isDateCriteriaAsked(dateFrom, dateTo) {
      return isNotUndefinedOrNull(dateFrom) ||
        isNotUndefinedOrNull(dateTo);
    }

    function formatDate(date) {
      var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

      if (month.length < 2) {
        month = '0' + month;
      }
      if (day.length < 2) {
        day = '0' + day;
      }
      return [day, month, year].join('.');
    }

    function sendAmounts() {
      var amount = null;
      var soLabel = 'S/O-Aufträge exkl.MWST';
      var eLabel = 'E-Aufträge exkl.MWST';
      var fLabel = 'F-Aufträge exkl.MWST';

      if (isColumnSelected(soLabel)) {
        amount = 'SO-Aufträge exkl.MWST' + ': ' + formatAmount(vm.soSum);
      }
      if (isColumnSelected(eLabel)) {
        if (isColumnSelected(soLabel)) {
          amount = amount + '		' + eLabel + ': ' + formatAmount(vm.eSum);
        } else {
          amount = eLabel + ': ' + formatAmount(vm.eSum);
        }
      }
      if (isColumnSelected(fLabel)) {
        if (isColumnSelected(soLabel) || isColumnSelected(eLabel)) {
          amount = amount + '		' + fLabel + ': ' + formatAmount(vm.fSum);
        } else {
          amount = fLabel + ': ' + formatAmount(vm.fSum);
        }
      }
      vm.amount = amount;
    }
  }
})();
