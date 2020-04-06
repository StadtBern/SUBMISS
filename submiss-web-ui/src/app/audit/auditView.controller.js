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
 * @name auditView.controller.js
 * @description AuditViewController controller
 */
(function () {
  "use strict";
  angular // eslint-disable-line no-undef
    .module("submiss.audit")
    .controller("AuditViewController", AuditViewController)
    .filter('customUserDateFilter', function ($filter) {
      return function (values, dateValue, createdOn) {
        var filtered = [];
        if (typeof values != 'undefined' && typeof dateValue != 'undefined') {
          angular.forEach(values, function (value) {
            if (createdOn) {
              if (value.createdOn != null && $filter('date')(value.createdOn,
                  'dd.MM.yyyy').indexOf($filter('date')(dateValue, 'dd.MM.yyyy')) >=
                0) {
                filtered.push(value);
              }
            }
          });
        }
        return filtered;
      }
    });

  /** @ngInject */
  function AuditViewController($scope, $state, $stateParams, $anchorScroll,
    AuditService,
    NgTableParams, $filter, DocumentService, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var tempCount = 10;
    var tempPage = 1;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.logs = [];
    vm.resultsCount = 0;
    vm.totalDisplay = 50;
    vm.startingDisplay = 0;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.getAuditLogs = getAuditLogs;
    vm.openCreatedOnFilter = openCreatedOnFilter;
    vm.levelIdOption = $stateParams.levelIdOption;
    vm.generateCompaniesPerMandatDoc = generateCompaniesPerMandatDoc;
    vm.loadMore = loadMore;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.getAuditLogs();
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function getAuditLogs() {
      vm.tableParams = new NgTableParams({
        page: 1,
        count: 10,
        sorting: {
          createdOn: 'desc'
        }
      }, {
        getData: function ($defer, params) {
          if (vm.tableParams.page() != null && vm.tableParams.page() !== 0 &&
            vm.tableParams.count() !== null &&
            vm.tableParams.count() !== 0) {
            /**
             * when another pagination option is chosen
             * use $anchorScroll('fromStart') to move to the top of the table,
             * $anchorScroll('StartingPoint') to move to the first element of the table
             * and initialize totalDisplay to the starting value
             */
            if (vm.tableParams.count() !== tempCount) {
              tempCount = vm.tableParams.count();
              vm.totalDisplay = 50;
              gotoResults('fromStart');
              gotoResults('StartingPoint');
            }
            /**
             * when changing pages
             * use $anchorScroll('fromStart') to move to the top of the table
             * $anchorScroll('StartingPoint') to move to the first element of the table
             * and initialize totalDisplay to the starting value
             */
            if (vm.tableParams.page() !== tempPage) {
              tempPage = vm.tableParams.page();
              vm.totalDisplay = 50;
              gotoResults('fromStart');
              gotoResults('StartingPoint');
            }
            var sortColumn = '';
            var sortType = '';
            for (var k in vm.tableParams.sorting()) {
              sortColumn = k;
              sortType = vm.tableParams.sorting()[k];
            }
            AppService.setPaneShown(true);
            return AuditService
              .getAuditLogs(vm.tableParams.page(), vm.tableParams.count(),
                sortColumn, sortType, vm.levelIdOption, params.filter())
              .then(function (auditResultList) {
                params.total(auditResultList.data.count);
                vm.resultsCount = auditResultList.data.count;
                vm.divTableHeight = AppService.adjustTableHeight(
                  auditResultList.data.count, vm.openCreatedOnFilter.opened);
                AppService.setPaneShown(false);
                return auditResultList.data.results;
              });
          }
          return null;
        }
      });
    }

    /** this function is triggered only if pagination option is 500 */
    function loadMore(tableParams) {
      if (tableParams.count() === 500) {
        vm.totalDisplay += 50;
      }
    }

    function gotoResults(id) {
      // set the location.hash to the id of
      // the element you wish to scroll to.
      $anchorScroll(id);
    }

    function openCreatedOnFilter() {
      vm.openCreatedOnFilter.opened = !vm.openCreatedOnFilter.opened;
      vm.divTableHeight = AppService.adjustTableHeight(vm.resultsCount,
        vm.openCreatedOnFilter.opened);
    }

    function generateCompaniesPerMandatDoc() {
      var templateForm = {
        templateShortCode: AppConstants.COMPANIES_CREATED_PER_MANDANT
      };
      downloadCompanyDocument(templateForm);
    }

    function downloadCompanyDocument(templateForm) {
      DocumentService.downloadCompanyDocument(templateForm)
        .success(function (response, status, headers) {
          var fileName = headers()["content-disposition"]
            .split(';')[1].trim().split('=')[1];
          // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
          fileName = fileName.substring(1, fileName.length - 1);
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
        }).error(function (response, status) {});
    }
  }
})();
