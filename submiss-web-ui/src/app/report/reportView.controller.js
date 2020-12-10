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
 * @name reportView.controller.js
 * @description ReportViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.report")
    .controller("ReportViewController", ReportViewController);

  /** @ngInject */
  function ReportViewController($scope, $location, $state, $stateParams,
    ProjectService, $anchorScroll,
    ReportService, StammdatenService, QFormJSRValidation, $filter, AppService,
    $uibModal, AppConstants, OfferService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.reportForm = {};
    vm.departments = [];
    vm.directorates = [];
    vm.objects = [];
    vm.projects = [];
    vm.projectCreditNos = [];
    vm.projectManagers = [];
    vm.procedures = [];
    vm.departmentsIDs = [];
    vm.reportForm.startDate = null;
    vm.reportForm.endDate = null;
    vm.options = [{
        value: true,
        label: 'Ja'
      },
      {
        value: false,
        label: 'Nein'
      }
    ];
    vm.internalOrExternal = [true, false];
    vm.reasonFreeAwards = [];
    vm.cancelationreasons = [];
    vm.awardCompanies = [];
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addCompany = addCompany;
    vm.companyExistsInList = companyExistsInList;
    vm.diractoryExistInList = diractoryExistInList;
    vm.generateReport = generateReport;
    vm.loadDepartments = loadDepartments;
    vm.loadDirectorates = loadDirectorates;
    vm.loadObjects = loadObjects;
    vm.loadProcedures = loadProcedures;
    vm.loadProjects = loadProjects;
    vm.loadProjectsCreditNos = loadProjectsCreditNos;
    vm.loadProjectManagers = loadProjectManagers;
    vm.loadWorkTypes = loadWorkTypes;
    vm.objectChange = objectChange;
    vm.objectValue = objectValue;
    vm.openStartDate = openStartDate;
    vm.openDate = openDate;
    vm.openEndDate = openEndDate;
    vm.projectChange = projectChange;
    vm.directorateChange = directorateChange;
    vm.resetValues = resetValues;
    vm.errorFieldFocus = errorFieldFocus;
    vm.areErrorsPresent = areErrorsPresent;
    vm.clearFormErrors = clearFormErrors;
    vm.checkForEmptyResults = checkForEmptyResults;
    vm.getGekoResultsNumber = getGekoResultsNumber;
    vm.processOptions = [{
      value: "SELECTIVE",
      label: 'Selektiv'
    }, {
      value: "OPEN",
      label: 'Offen'
    }, {
      value: "INVITATION",
      label: 'Einladung'
    }, {
      value: "NEGOTIATED_PROCEDURE",
      label: 'Freihändig'
    }, {
      value: "NEGOTIATED_PROCEDURE_WITH_COMPETITION",
      label: 'Freihändig	mit Konkurrenz'
    }];
    vm.toInternalOrExternal = toInternalOrExternal;
    vm.loadReasonFreeAwards = loadReasonFreeAwards;
    vm.loadCancelationreasons = loadCancelationreasons;
    vm.loadAwardCompanies = loadAwardCompanies;
    vm.openGeKoTable = openGeKoTable;
    vm.isDateOrderInvalid = isDateOrderInvalid;
    vm.isFutureDate = isFutureDate;
    vm.isInValidDate = isInValidDate;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      ReportService.loadReport($state.current.name)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            if ($state.current.name === 'report.generateReport') {
              vm.reportForm.companies = []; // initialize in order to be updated by the add button
              vm.reportForm.totalizationBy = 'totalByProcedure'; // set default totalization by procedure
              vm.loadProjectsCreditNos();
              vm.loadProjectManagers();
            } else {
              vm.loadReasonFreeAwards();
              vm.loadCancelationreasons();
              vm.loadAwardCompanies();
            }
            vm.loadObjects();
            vm.loadProjects();
            vm.loadProcedures();
            vm.loadDepartments();
            vm.loadDirectorates();
            vm.loadWorkTypes();
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
    /** Translate true/false to internal/external */
    function toInternalOrExternal(value) {
      vm.reportForm.internal = value;
      if (value) {
        return "Intern";
      } else {
        return "Extern";
      }
    }

    function generateReport() {
      vm.noResultsReturned = false;
      if (angular.isUndefined(vm.reportForm.startDate) ||
        angular.isUndefined(vm.reportForm.endDate)) {
        vm.invalidDate = true;
      } else {
        vm.invalidDate = false;
        ReportService.validateForm(vm.reportForm)
          .success(function (response, status) {
            if (status === 403) { // Security checks.
              return;
            }
            vm.clearFormErrors('Auswertungen');
            if (angular.isNumber(response)) {
              confirmModal(response, 1);
            } else {
              proceedTogenerateReport();
            }
          })
          .error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.reportViewCtr.htmlReportForm,
                response);
              vm.form = $scope.reportViewCtr.htmlReportForm;
              vm.errorFieldFocus(vm.form);
            }
          });

      }
    }

    function clearFormErrors(value) {
      $scope.reportViewCtr.htmlReportForm.futureDateErrorField.$invalid = false;
      $scope.reportViewCtr.htmlReportForm.startDateAfterEndDateErrorField.$invalid = false;
      $scope.reportViewCtr.htmlReportForm.futureDateErrorField.$valid = true;
      $scope.reportViewCtr.htmlReportForm.startDateAfterEndDateErrorField.$valid = true;
      if (value === 'GeKo') {
        $scope.reportViewCtr.htmlReportForm.futurePublicationDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futurePublicationDateAwardErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futureApplicationOpeningDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futureOfferOpeningErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futureCommissionProcurementProposalDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futurePublicationDateDirectAwardErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futureFirstDeadlineErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futureSecondDeadlineErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futureFirstLevelavailableDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.futureLevelavailableDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderPublicationDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderPublicationDateAwardErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderApplicationOpeningDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderOfferOpeningErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderCommissionProcurementProposalDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderPublicationDateDirectAwardErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderFirstDeadlineErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderSecondDeadlineErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderFirstLevelavailableDateErrorField.$invalid = false;
        $scope.reportViewCtr.htmlReportForm.dateOrderLevelavailableDateErrorField.$invalid = false;
      }
    }

    // Function that opens a modal to add companies for the report
    function addCompany() {
      var addCompany = AppService.addCompany(false, null);
      return addCompany.result.then(function (response) {
        // take the chosen user response from modal and add it into companies
        if (response) {
          for (var i = 0; i <= response.length - 1; i++) {
            if (vm.companyExistsInList(vm.reportForm.companies, response[i])) {
              continue;
            } else {
              vm.reportForm.companies.push(response[i]);
            }
          }
        }
      });
    }

    // Function that checks if company already exists in company list
    function companyExistsInList(list, company) {
      for (var i = 0; i < list.length; i++) {
        if (list[i].id === company.id) {
          return true;
        }
      }
      return false;
    }

    // Function that returns all Objects
    function loadObjects() {
      StammdatenService.readAllObjects().success(function (data) {
        vm.objects = data;
      }).error(function (response, status) {});
    }

    // Function that returns all reason free awards
    function loadReasonFreeAwards() {
      StammdatenService.getReasonFreeAwards().success(function (data) {
        vm.reasonFreeAwards = data;
      }).error(function (response, status) {});
    }

    // Function that returns all cancelation reasons
    function loadCancelationreasons() {
      StammdatenService.getCancelationreasons().success(function (data) {
        vm.cancelationreasons = data;
      }).error(function (response, status) {});
    }

    // Function that returns all cancelation reasons
    function loadAwardCompanies(value) {
      if (value === undefined) {
        value = null;
      }
      return OfferService.readAwardSubmittents(value).then(function (resp) {
        return resp.data;
      });
    }

    // Function that returns empty and not null when value2 is null
    function objectValue(value2) {
      if (value2 === null) {
        return "";
      } else {
        return ", " + value2;
      }
    }

    // Function that returns all Projects
    function loadProjects() {
      ProjectService.readProjects().success(function (data) {
        vm.projects = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Projects Managers
    function loadProjectManagers() {
      ProjectService.readAllProjectManagers().success(function (data) {
        vm.projectManagers = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Projects Credit Numbers
    function loadProjectsCreditNos() {
      ProjectService.readAllProjectsCreditNos().success(function (data) {
        vm.projectCreditNos = data;
      }).error(function (response, status) {})
    }

    // Function that returns all Procedures
    function loadProcedures() {
      vm.procedures = vm.processOptions;
    }

    // Function that returns all Arbeitsgattungen/ Work Types
    function loadWorkTypes() {
      StammdatenService.readAllWorkTypes().success(function (data) {
        vm.workTypes = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Abteilungen
    function loadDepartments() {
      StammdatenService.readAllDepartments().success(function (data) {
        vm.departments = data;
        vm.loadDirectorates();
      }).error(function (response, status) {});
    }

    // Function that returns all Directorates
    function loadDirectorates() {
      for (var i = 0; i < vm.departments.length; i++) {
        vm.departmentsIDs.push(vm.departments[i].id);
      }
      if (vm.departmentsIDs.length > 0) {
        StammdatenService.readDirectoratesByDepartmentsIDs(
          vm.departmentsIDs).success(function (data) {
          for (var i = 0; i < data.length; i++) {
            if (diractoryExistInList(data[i], vm.directorates)) {
              continue;
            } else {
              vm.directorates.push(data[i]);
            }
          }
        }).error(function (response, status) {});
      }
    }

    // Function that checks if current directorate already exists in diractorates array
    function diractoryExistInList(data, diractorates) {
      for (var i = 0; i < diractorates.length; i++) {
        if (diractorates[i].id === data.id) {
          return true;
        }
      }
      return false;
    }

    // Function that returns the corresponding projects that underlie the selected objects
    function objectChange() {
      if (vm.reportForm.objects.length > 0) {
        ProjectService.readProjectsByObjectsIDs(vm.reportForm.objects)
          .success(function (data) {
            vm.projects = data;
          }).error(function (response, status) {});
      } else {
        vm.loadProjects();
      }
    }

    // Function that returns the objects that corresponds to the selected projects
    function projectChange() {
      if (vm.reportForm.projects.length > 0) {
        StammdatenService.readObjectsByProjectsIDs(vm.reportForm.projects)
          .success(function (data) {
            vm.objects = data;
          }).error(function (response, status) {});
      } else {
        vm.loadObjects();
      }
    }

    // Function that returns the departments that corresponds to the selected directorate
    function directorateChange() {
      if (vm.reportForm.directorates.length > 0) {
        StammdatenService.readDepartmentsByDirectoratesIDs(
            vm.reportForm.directorates)
          .success(function (data) {
            vm.departments = data;
          }).error(function (response, status) {});
      } else {
        vm.loadDepartments();
      }
    }

    // Function that handles the datepicker pop up for startDate
    function openStartDate() {
      vm.openStartDate.opened = !vm.openStartDate.opened;
    }

    // Function that handles the datepicker pop up for date field
    function openDate(dateFieldName) {
      if (vm["dateField_" + dateFieldName] === undefined) {
        vm["dateField_" + dateFieldName] = {};
      }
      vm["dateField_" + dateFieldName].opened = !vm["dateField_" +
        dateFieldName].opened;
    }

    // Function that handles the datepicker pop up for endDate
    function openEndDate() {
      vm.openEndDate.opened = !vm.openEndDate.opened;
    }

    // Function that reloads the page and sets the default values
    function resetValues(caseOption) {
      if (caseOption) {
        $state.go('report.generateReport', {}, {
          reload: true
        });
      } else {
        $state.go('report.generateOperationReport', {}, {
          reload: true
        });
      }
    }

    // Function to check for errors
    function areErrorsPresent(value) {
      if (value !== 'GeKo') {
        return $scope.reportViewCtr.htmlReportForm.futureDateErrorField.$invalid ||
          $scope.reportViewCtr.htmlReportForm.startDateAfterEndDateErrorField.$invalid ||
          !$scope.reportViewCtr.htmlReportForm.startDate.$valid ||
          !$scope.reportViewCtr.htmlReportForm.endDate.$valid ||
          vm.invalidDate;
      } else {
        return isFutureDate() || isDateOrderInvalid() ||
          isInValidDate();
      }
    }

    function isDateOrderInvalid() {
      return $scope.reportViewCtr.htmlReportForm.startDateAfterEndDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderPublicationDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderPublicationDateAwardErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderApplicationOpeningDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderOfferOpeningErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderCommissionProcurementProposalDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderPublicationDateDirectAwardErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderFirstDeadlineErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderSecondDeadlineErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderFirstLevelavailableDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.dateOrderLevelavailableDateErrorField.$invalid;
    }

    function isFutureDate() {
      return $scope.reportViewCtr.htmlReportForm.futureDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futurePublicationDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futurePublicationDateAwardErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futureApplicationOpeningDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futureOfferOpeningErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futureCommissionProcurementProposalDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futurePublicationDateDirectAwardErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futureFirstDeadlineErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futureSecondDeadlineErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futureFirstLevelavailableDateErrorField.$invalid ||
        $scope.reportViewCtr.htmlReportForm.futureLevelavailableDateErrorField.$invalid;
    }

    function isInValidDate() {
      return !$scope.reportViewCtr.htmlReportForm.startDate.$valid ||
        !$scope.reportViewCtr.htmlReportForm.endDate.$valid ||
        !$scope.reportViewCtr.htmlReportForm.publicationDateAwardFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.publicationDateAwardTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.publicationDateFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.publicationDateTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.applicationOpeningDateFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.applicationOpeningDateTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.offerOpeningDateFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.offerOpeningDateTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.commissionProcurementProposalDateFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.commissionProcurementProposalDateTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.publicationDateDirectAwardFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.publicationDateDirectAwardTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.firstDeadlineFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.firstDeadlineTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.secondDeadlineFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.secondDeadlineTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.firstLevelavailableDateFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.firstLevelavailableDateTo.$valid ||
        !$scope.reportViewCtr.htmlReportForm.levelavailableDateFrom.$valid ||
        !$scope.reportViewCtr.htmlReportForm.levelavailableDateTo.$valid;
    }

    // Function that navigates to the top of the page if there is an error
    function errorFieldFocus(reportForm) {
      if (reportForm.$dirty) {
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

    function proceedTogenerateReport() {
      AppService.setPaneShown(true);
      ReportService.generateReport(vm.reportForm)
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
    }

    function confirmModal(response, reportOrOperation) {
      var confirmModal = $uibModal
        .open({
          animation: true,
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title" >Warnung</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p>Die Anfrage beinhaltet mehr als {{modalCtrl.number}} Ergebnisse und es kann demnach zu zeitlichen Verzögerungen kommen.' +
            ' Möchten Sie mit dem Vorgang fortfahren?</p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="modalCtrl.closeWindowModal(\'ja\')" >Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'modalCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.number = response;
            vm.closeWindowModal = function (reason) {
              confirmModal.dismiss(reason);
            };

          }

        });

      return confirmModal.result.then(function () {
        // Modal Success Handler
      }, function (response) { // Modal Dismiss Handler
        if (response === 'ja' && reportOrOperation === 1) {
          proceedTogenerateReport();
        } else if (response === 'ja' && reportOrOperation === 2) {
          openGeKoTable();
        }
      });

    }

    function openGeKoTable() {
      $uibModal.open({
        template: '<div class="modal-header">' +
          '<button type="button" class="close" data-dismiss="modal" ' +
          'aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
          '<h4 class="modal-title">GeKo</h4>' +
          '</div>' +
          '<div class="modal-body">' +
          '<div ng-include src="' +
          '\'' +
          'app/report/geKoTable.html' +
          '\'">' +
          '<div>' +
          '<div>',
        controller: 'GeKoTableController',
        controllerAs: 'geKoTableCtr',
        size: 'lg',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          reportForm: function () {
            return vm.reportForm;
          }
        }
      });
    }

    function getGekoResultsNumber() {
      vm.clearFormErrors('GeKo');
      vm.noResultsReturned = false;
      ReportService.validateOperationForm(vm.reportForm)
        .success(function (response, status) {
          if (status === 403) { // Security checks.
            return;
          }
          vm.clearFormErrors('GeKo');
          if (angular.isNumber(response) && response !== 0) {
            confirmModal(response, 2);
            vm.noResultsReturned = false;
          } else if (response === 0) {
            vm.noResultsReturned = true;
          } else {
            vm.noResultsReturned = false;
            openGeKoTable();
          }
        })
        .error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.reportViewCtr.htmlReportForm,
              response);
            vm.form = $scope.reportViewCtr.htmlReportForm;
            vm.errorFieldFocus(vm.form);
          }
        });
    }
  }
})();
