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
 * @name userEdit.controller.js
 * @description UserEditController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.users")
    .controller("UserExportController", UserExportController);

  /** @ngInject */
  function UserExportController($scope, $location, $anchorScroll, $state,
    QFormJSRValidation, $filter, UsersService, AppService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.usersExportForm = {};
    vm.usersExportForm.startDate = null;
    vm.usersExportForm.endDate = null;
    vm.status = [{
        value: null,
        label: 'Alle'
      },
      {
        value: "1",
        label: 'Aktiviert'
      },
      {
        value: "0",
        label: 'Deaktiviert'
      }
    ];

    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.openStartDate = openStartDate;
    vm.openEndDate = openEndDate;
    vm.areErrorsPresent = areErrorsPresent;
    vm.errorFieldFocus = errorFieldFocus;
    vm.clearFormErrors = clearFormErrors;
    vm.exportUsers = exportUsers;
    vm.resetValues = resetValues;

    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      UsersService.loadUserExport()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.statusTemp = vm.status[0];
          }
        });
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/

    function exportUsers() {
      vm.noResultsReturned = false;
      if (angular.isUndefined(vm.usersExportForm.startDate) || angular.isUndefined(vm.usersExportForm.endDate)) {
        vm.invalidDate = true;
      } else {
        vm.invalidDate = false;
        vm.usersExportForm.status = vm.statusTemp.value;
        UsersService.validateExportForm(vm.usersExportForm)
          .success(function (data, status) {
            if (status === 403) { // Security checks.
              return;
            }
            vm.clearFormErrors();
            AppService.setPaneShown(true);
            UsersService.exportUsers(vm.usersExportForm)
              .success(function (response, status, headers) {
                if (status === 403) { // Security checks.
                  AppService.setPaneShown(false);
                  return;
                }
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
          })
          .error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.userExportCtr.htmlExportForm, response);
              vm.form = $scope.userExportCtr.htmlExportForm;
              vm.errorFieldFocus(vm.form);
            }
          });

      }
    }

    // Function that handles the datepicker pop up for startDate
    function openStartDate() {
      vm.openStartDate.opened = !vm.openStartDate.opened;
    }

    // Function that handles the datepicker pop up for endDate
    function openEndDate() {
      vm.openEndDate.opened = !vm.openEndDate.opened;
    }

    // Function to check for errors
    function areErrorsPresent() {
      return $scope.userExportCtr.htmlExportForm.futureDateErrorField.$invalid ||
        $scope.userExportCtr.htmlExportForm.startDateAfterEndDateErrorField.$invalid ||
        !$scope.userExportCtr.htmlExportForm.startDate.$valid ||
        !$scope.userExportCtr.htmlExportForm.endDate.$valid ||
        vm.invalidDate;
    }

    function clearFormErrors() {
      $scope.userExportCtr.htmlExportForm.futureDateErrorField.$invalid = false;
      $scope.userExportCtr.htmlExportForm.startDateAfterEndDateErrorField.$invalid = false;
      $scope.userExportCtr.htmlExportForm.futureDateErrorField.$valid = true;
      $scope.userExportCtr.htmlExportForm.startDateAfterEndDateErrorField.$valid = true;
    }

    // Function that navigates to the top of the page if there is an error
    function errorFieldFocus(exportForm) {
      if (exportForm.$dirty) {
        $anchorScroll('errorScroll');
      }
    }

    // Function that check if no results returned from search
    function checkForEmptyResults(response) {
      if (response.byteLength === 0) {
        vm.noResultsReturned = true;
        $anchorScroll('errorScroll');
      }
    }

    // Function that sets the default values
    function resetValues() {
      $state.go('users.export', {}, {
        reload: true
      });
    }
  }
})();
