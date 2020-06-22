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
 * @name submissionCreate.controller.js
 * @description SubmissionCreateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project").controller("SubmissionCreateController",
      SubmissionCreateController);

  /** @ngInject */
  function SubmissionCreateController($scope, $locale, $state, $q, $location,
    $anchorScroll,
    $stateParams, StammdatenService, SubmissionService, $filter,
    QFormJSRValidation, $uibModal, project, $uibModalInstance,
    editSubmission, submission, AppService, AppConstants) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var oldProcessTypeShortCode = null;
    var isServiceTenderToCheck = null;
    var isLockedToCheck = null;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.editSubmission = editSubmission;
    vm.submission = submission;
    vm.secDLWettbewerbFieldTenderCreate = false;
    vm.secGeKoEintragFieldTenderCreate = false;
    vm.secEingabetermin2FieldTenderEdit = false;
    vm.secTenderCreate = false;
    vm.secTenderEdit = false;
    vm.project = project;
    vm.procedures = [];
    vm.departments = [];
    vm.company = {};
    vm.workTypes = [];
    vm.processTypes = [];
    vm.negotiatedProcedures = [];
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.process = AppConstants.PROCESS;
    vm.currentStatus = "";
    vm.processOptions = [{
      value: "SELECTIVE",
      label: 'Selektiv'
    }, {
      value: "OPEN",
      label: 'Offen'
    }, {
      value: "INVITATION",
      label: 'Einladungsverfahren'
    }, {
      value: "NEGOTIATED_PROCEDURE",
      label: 'Freihändig'
    }, {
      value: "NEGOTIATED_PROCEDURE_WITH_COMPETITION",
      label: 'Freihändig	mit Konkurrenz'
    }];
    vm.options = [{
      value: false,
      label: 'Nein'
    }, {
      value: true,
      label: 'Ja'
    }];
    vm.moreThanOneError = false;
    vm.publicationDateDirectAwardEnabled = false;
    vm.pmExternalChanged = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.addCompany = addCompany;
    vm.open = open;
    vm.openPublicationDate = openPublicationDate;
    vm.openPublicationDateAward = openPublicationDateAward;
    vm.openPublicationDateDirectAward = openPublicationDateDirectAward;
    vm.openFirstDeadline = openFirstDeadline;
    vm.openApplicationOpeningDate = openApplicationOpeningDate;
    vm.openOfferOpeningDate = openOfferOpeningDate;
    vm.openSecondDeadlineDate = openSecondDeadlineDate;
    vm.isLockedValue = isLockedValue;
    vm.setIsLocked = setIsLocked;
    vm.gekoEntryValue = gekoEntryValue;
    vm.resetValues = resetValues;
    vm.closeWindow = closeWindow;
    vm.setPublicationDateDirectAwardEnabled = setPublicationDateDirectAwardEnabled;
    vm.errorFieldFocus = errorFieldFocus;
    vm.genericDisable = genericDisable;
    vm.setApplicationOpeningDate = setApplicationOpeningDate;
    vm.deactivateGekoEnty = deactivateGekoEnty;
    vm.deleteCompany = deleteCompany;
    vm.noAwardCheckboxDisabled = noAwardCheckboxDisabled;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      SubmissionService.loadSubmissionCreate()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            $uibModalInstance.close();
            return;
          } else {
            loadProcedures();
            loadWorkTypes();
            loadDepartments();
            loadNegotiatedProcedures();
            loadProcessTypes(vm.project.id);
            if (!editSubmission) {
              vm.submission.isGekoEntry = true;
              vm.submission.publicationDate = null;
              vm.submission.publicationDateAward = null;
              vm.submission.publicationDateDirectAward = null;
              vm.submission.firstDeadline = null;
              vm.submission.applicationOpeningDate = null;
              vm.submission.secondDeadline = null;
              vm.submission.offerOpeningDate = new Date();
              vm.submission.constructionPermit = vm.project.constructionPermit;
              vm.submission.gattTwo = vm.project.gattWto;
              vm.submission.loanApproval = vm.project.loanApproval;
              vm.submission.pmDepartmentName = vm.project.pmDepartmentName;
              vm.submission.pmExternal = vm.project.pmExternal;
              vm.submission.procedure = vm.project.procedure;
            }
            if (editSubmission) {
              /** store the values of process, isServiceTender, isLocked
               * so if they are changed the back end must be informed,
               * in order to update the security resources
               * The old process is stored in the form, so that we can
               * check in the back end if an update of the resources is necessary.
               */
              vm.submission.oldProcess = vm.submission.process;
              isServiceTenderToCheck = vm.submission.isServiceTender;
              isLockedToCheck = vm.submission.isLocked;
              readStatusOfSubmission(vm.submission.id);
              setPublicationDateDirectAwardEnabled(); // initiate
            }
            findUserOperations();
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

    /** Prevent backspace from navigating back using jQuery. */
    $(document).on("keydown", function (e) {
      if (e.which === 8 && !$(e.target).is("input, textarea")) {
        e.preventDefault();
      }
    });

    function openPublicationDate() {
      vm.openPublicationDate.opened = !vm.openPublicationDate.opened;
    }

    function openPublicationDateAward() {
      vm.openPublicationDateAward.opened = !vm.openPublicationDateAward.opened;
    }

    function openPublicationDateDirectAward() {
      vm.openPublicationDateDirectAward.opened = !vm.openPublicationDateDirectAward.opened;
    }

    function openFirstDeadline() {
      vm.openFirstDeadline.opened = !vm.openFirstDeadline.opened;
    }

    function openApplicationOpeningDate() {
      vm.openApplicationOpeningDate.opened = !vm.openApplicationOpeningDate.opened;
    }

    function openSecondDeadlineDate() {
      vm.openSecondDeadlineDate.opened = !vm.openSecondDeadlineDate.opened;
    }

    function openOfferOpeningDate() {
      vm.openOfferOpeningDate.opened = !vm.openOfferOpeningDate.opened;
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {});
    }

    function save() {
      if (angular.isUndefined(vm.submission.publicationDate) ||
        angular.isUndefined(vm.submission.publicationDateAward) ||
        angular.isUndefined(vm.submission.publicationDateDirectAward) ||
        angular.isUndefined(vm.submission.firstDeadline) ||
        angular.isUndefined(vm.submission.applicationOpeningDate) ||
        angular.isUndefined(vm.submission.secondDeadline) ||
        angular.isUndefined(vm.submission.offerOpeningDate)) {
        vm.invalidDate = true;
        if (angular.isUndefined(vm.submission.firstDeadline)) {
          vm.invalidFirstDeadline = true;
        }
        if (angular.isUndefined(vm.submission.publicationDate)) {
          vm.invalidPublicationDate = true;
        }
        if (angular.isUndefined(vm.submission.publicationDateAward)) {
          vm.invalidPublicationDateAward = true;
        }
        if (angular.isUndefined(vm.submission.publicationDateDirectAward)) {
          vm.invalidPublicationDateDirectAward = true;
        }
        if (angular.isUndefined(vm.submission.applicationOpeningDate)) {
          vm.invalidApplicationOpeningDate = true;
        }
        if (angular.isUndefined(vm.submission.secondDeadline)) {
          vm.invalidSecondDeadline = true;
        }
        if (angular.isUndefined(vm.submission.offerOpeningDate)) {
          vm.invalidOfferOpeningDate = true;
        }
      } else {
        vm.invalidDate = false;
        if (!editSubmission) {
          if (angular.isUndefined(vm.submission.description)) {
            vm.submission.description = '';
          }
          vm.submission.project = vm.project;
          vm.submission.noAwardTender = false;
          SubmissionService
            .createSubmission(vm.submission)
            .success(function (data, status) {
              $uibModalInstance.close();
              if (status === 403) { // Security checks.
                return;
              }
              $state.go('submissionView', {
                id: data.id
              });
            }).error(function (response, status) {
              if (status === 400) { // Validation
                // errors.
                QFormJSRValidation
                  .markErrors(
                    $scope,
                    $scope.submissionCreateCtr.submissionForm,
                    response);
              }
            });
        }
        if (editSubmission) {
          if (angular.isUndefined(vm.submission.description)) {
            vm.submission.description = '';
          }
          /** check if process, isServiceTender, isLocked are changed and update the according flags
           */
          if (vm.submission.oldProcess !== vm.submission.process ||
            isServiceTenderToCheck !== vm.submission.isServiceTender ||
            isLockedToCheck !== vm.submission.isLocked) {
            vm.submission.changeForSec = true;
          } else {
            vm.submission.changeForSec = false;
          }
          if (isLockedToCheck !== vm.submission.isLocked) {
            vm.submission.isLockedChanged = true;
          } else {
            vm.submission.isLockedChanged = false;
          }
          SubmissionService
            .updateSubmission(vm.submission)
            .success(function (data) {
              $uibModalInstance.close();
              $state.reload();
            })
            .error(function (response, status) {
              if (status === 400) { // Validation
                // errors.
                QFormJSRValidation
                  .markErrors(
                    $scope,
                    $scope.submissionCreateCtr.submissionForm,
                    response);
              }
            });
          vm.projectForm = $scope.submissionCreateCtr.submissionForm;
          vm.errorFieldFocus(vm.projectForm);
        }
      }
    }

    function closeWindow() {
      if (vm.submissionForm.$dirty || vm.pmExternalChanged) {
        var closeSubmissionModal = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title" ng-if="submissionCreateCtr.editSubmission">Submission bearbeiten</h4>' +
              '<h4 class="modal-title" ng-if="!submissionCreateCtr.editSubmission">Submission eröffnen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="submissionCreateCtr.closeWindowModal(\'ja\')">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'submissionCreateCtr',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.editSubmission = editSubmission;
              vm.closeWindowModal = function (reason) {
                closeSubmissionModal.dismiss(reason);
              };
            }
          });
        return closeSubmissionModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            $uibModalInstance.close();
            $state.go($state.current, {}, {
              reload: true
            });
          } else {
            // Do something else here
            return false;
          }
        });
      } else {
        $uibModalInstance.dismiss();
      }
    }

    // Function that opens a modal to add a company for a submission
    function addCompany() {
      if (vm.submission.pmExternal !== null) {
        // Externe Projektleitung already exists, display error message
        vm.moreThanOneError = true;
      } else {
        var addCompany = AppService.addCompany(true, null);
        return addCompany.result.then(function (response) {
          vm.companyList = [];
          vm.companyList = response;
          vm.pmExternalChanged = true;
          for (var i = 0; i < vm.companyList.length; i++) {
            vm.submission.pmExternal = vm.companyList[i];
          }
        });
      }
      return null;
    }

    function deleteCompany() {
      vm.submission.pmExternal = null;
      vm.moreThanOneError = false;
      vm.pmExternalChanged = true;
    }

    function loadProcedures() {
      StammdatenService.readAllProcedures().success(function (data) {
        vm.procedures = $filter('filter')(data, {
          'active': true
        });
      }).error(function (response, status) {});
    }

    // Function that returns all Process Types
    function loadProcessTypes(projectId) {
      StammdatenService.readAllProcessTypes(projectId).success(function (data) {
        vm.processTypes = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Abteilungen
    function loadDepartments() {
      StammdatenService.readAllDepartments().success(function (data) {
        vm.departments = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Arbeitsgattungen/Work Types
    function loadWorkTypes() {
      StammdatenService.readAllWorkTypes().success(function (data) {
        vm.workTypes = AppService.showOnlyActive(data);
      }).error(function (response, status) {});
    }

    // Function that returns all Negotiated Procedures
    function loadNegotiatedProcedures() {
      StammdatenService.readAllNegotiatedProcedures().success(
        function (data) {
          vm.negotiatedProcedures = data;
        }).error(function (response, status) {});
    }

    /** Setting isLocked value when DL-Wettbewerb checkbox is checked */
    function setIsLocked(isServiceTender, processTypeShortCode) {
      if (processTypeShortCode === "PT3") {
        if (isServiceTender === true) {
          vm.submission.isLocked = true;
        } else if (isServiceTender === false) {
          vm.submission.isLocked = false;
        }
      }
    }

    /** Setting isLocked value independently from DL-Wettbewerb checkbox, according to specifications*/
    function isLockedValue(newProcessTypeShortCode) {
      if (editSubmission && (vm.submission.processType === null ||
          angular.isUndefined(vm.submission.processType) ||
          (vm.submission.processType !== null &&
            vm.submission.processType.shortCode !== "PT3")) &&
        vm.submission.isServiceTender) { //Setting the value of isServiceTender to false in submission edit, if the process type is not "PT3"
        vm.submission.isServiceTender = false;
      }
      if ((!editSubmission && oldProcessTypeShortCode !==
          newProcessTypeShortCode) || (editSubmission && vm.currentStatus >=
          vm.status.AWARD_NOTICES_CREATED) ||
        (!editSubmission && angular.isUndefined(newProcessTypeShortCode))) {
        if (oldProcessTypeShortCode !== newProcessTypeShortCode) {
          oldProcessTypeShortCode = newProcessTypeShortCode;
        }
        vm.submission.isLocked = false;
        return false;
      } else if (editSubmission && (!angular.isUndefined(
          vm.submission.isLocked)) && angular.isUndefined(
          newProcessTypeShortCode)) {
        return vm.submission.isLocked;
      }
      return null;
    }

    /** Setting geko entry value */
    function gekoEntryValue() {
      if ((angular.isUndefined(vm.submission.process) || vm.submission.process ===
          null || vm.submission.process === "INVITATION" ||
          vm.submission.process === "OPEN" || vm.submission.process ===
          "SELECTIVE")) {
        vm.submission.isGekoEntry = true;
        return true;
      } else {
        vm.submission.isGekoEntry = false;
        return false;
      }
    }

    /** Reset values firstDeadline, applicationOpeningDate and firstLoggedBy to empty when process changes. */
    function resetValues() {
      if ((angular.isUndefined(vm.submission.process) || vm.submission.process ===
          null || vm.submission.process !== "SELECTIVE")) {
        vm.submission.firstDeadline = null;
        vm.submission.applicationOpeningDate = null;
        vm.submission.firstLoggedBy = null;
      }
    }

    /** Activate, deactivate field Publikation Absicht freihändige Vergabe according to the type of the process*/
    function setPublicationDateDirectAwardEnabled() {
      if ((angular.isUndefined(vm.submission.process) || vm.submission.process ===
          null || vm.submission.process === "INVITATION" ||
          vm.submission.process === "OPEN" || vm.submission.process ===
          "SELECTIVE")) {
        vm.publicationDateDirectAwardEnabled = false;
        // clear fields if not null
        vm.submission.publicationDateDirectAward = null;
        vm.submission.reasonFreeAward = null;
      } else {
        vm.publicationDateDirectAwardEnabled = true;
      }
    }

    function findUserOperations() {
      vm.secDLWettbewerbFieldTenderCreate = AppService.isOperationPermitted(
        AppConstants.OPERATION.DL_WETTBEWERB_FIELD_TENDER_CREATE, null);
      vm.secGeKoEintragFieldTenderCreate = AppService.isOperationPermitted(
        AppConstants.OPERATION.GEKO_EINTRAG_FIELD_TENDER_CREATE, null);
      vm.secTenderCreate = AppService.isOperationPermitted(
        AppConstants.OPERATION.TENDER_CREATE, null);
      vm.secTenderEdit = AppService.isOperationPermitted(
        AppConstants.OPERATION.TENDER_EDIT, null);
      vm.secEingabetermin2FieldTenderEdit = AppService.isOperationPermitted(
        AppConstants.OPERATION.EINGABETERMIN_2_FIELD_TENDER_EDIT,
        vm.submission.process);
    }

    /** Function that navigates to the top of the modal dialog if there is an error*/
    function errorFieldFocus(projectForm) {
      if (projectForm.$dirty) {
        $anchorScroll('errorScroll');
      }
    }

    /** Generic disable functionality */
    function genericDisable() {
      return (!vm.editSubmission && !vm.secTenderCreate) ||
        (vm.editSubmission && (!vm.secTenderEdit ||
          (AppService.getLoggedUser().userGroup.name === vm.group.PL &&
            (vm.submission.process === vm.process.SELECTIVE && vm.currentStatus >=
              vm.status.APPLICATION_OPENING_STARTED ||
              (vm.submission.process === vm.process.OPEN && vm.currentStatus >=
                vm.status.OFFER_OPENING_STARTED) ||
              (vm.submission.process === vm.process.INVITATION &&
                (vm.currentStatus >= vm.status.SUBMITTENTLIST_CHECK ||
                  vm.currentStatus >= vm.status.OFFER_OPENING_STARTED))))));
    }

    /** Set application opening date to current date, if process is selective */
    function setApplicationOpeningDate(process) {
      if (process === vm.process.SELECTIVE) {
        vm.submission.applicationOpeningDate = new Date();
      } else {
        vm.submission.applicationOpeningDate = null;
      }
    }

    function deactivateGekoEnty() {
      return ((vm.submission.process === 'NEGOTIATED_PROCEDURE' ||
          vm.submission.process === 'NEGOTIATED_PROCEDURE_WITH_COMPETITION') &&
        vm.currentStatus >= vm.status.MANUAL_AWARD_COMPLETED &&
        vm.submission.aboveThreshold === true);
    }

    function noAwardCheckboxDisabled() {
      return vm.submission.process === vm.process.NEGOTIATED_PROCEDURE || vm.submission.process === vm.process.NEGOTIATED_PROCEDURE_WITH_COMPETITION ||
        (vm.submission.process != vm.process.SELECTIVE && vm.currentStatus < vm.status.SUBMITTENT_LIST_CREATED) ||
        (vm.submission.process === vm.process.SELECTIVE && vm.currentStatus < vm.status.APPLICANTS_LIST_CREATED) ||
        vm.currentStatus >= vm.status.PROCEDURE_COMPLETED ||
        vm.currentStatus >= vm.status.PROCEDURE_CANCELED;
    }
  }
})();
