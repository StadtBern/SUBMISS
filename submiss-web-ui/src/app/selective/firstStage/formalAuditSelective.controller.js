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
 * @name formalAuditSelective.controller.js
 * @description FormalAuditSelectiveController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.examination").controller("FormalAuditSelectiveController",
      FormalAuditSelectiveController);

  /** @ngInject */
  function FormalAuditSelectiveController($rootScope, $scope, $state,
    $stateParams, $transitions,
    SubmissionService, ExaminationService, QFormJSRValidation, NgTableParams,
    $filter,
    $element, $uibModal, AppService, AppConstants, SelectiveService) {

    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.submittent = {};
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.offers
    });

    // Initialize security variables.
    vm.secFormalAuditExecute = null;
    vm.secTenderMove = false;
    vm.secSentEmail = false;

    vm.options = [{
        value: true,
        label: 'Ja'
      },
      {
        value: false,
        label: 'Nein'
      }
    ];

    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.process = AppConstants.PROCESS;
    vm.offerOpeningClosedBefore = null;
    vm.dirtyFlag = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.cancelButton = cancelButton;
    vm.sendProofsModal = sendProofsModal;
    vm.sendEmailButtonDisabled = sendEmailButtonDisabled;
    vm.dropDownDisabled = dropDownDisabled;
    vm.disableButtons = disableButtons;
    vm.checkBeforeSendingProofs = checkBeforeSendingProofs;
    vm.expandTableHeight = expandTableHeight;
    vm.allFormalExaminationsFulfilled = allFormalExaminationsFulfilled;

    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      SubmissionService.loadFormalAudit($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            getApplicants($stateParams.id);
            readStatusOfSubmission($stateParams.id);
            readSubmission($stateParams.id);
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
    /** Function to get submission data */
    function readSubmission(id) {
      SubmissionService.readSubmission(id).success(function (data) {
        vm.data.submission = data;
        hasOfferOpeningBeenClosedBefore(vm.data.submission.id);
      }).error(function (response, status) {

      });
    }

    /** Function to get status of submission */
    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(
        function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {

      });
    }

    /** Function to save the changes */
    function save(submittents) {

      SelectiveService.updateSelectiveFormalAudit(submittents)
        .success(function (data) {
          AppService.setPaneShown(true);
          ExaminationService.updateSubmissionFormalAuditExaminationStatus(
            $stateParams.id).success(function (data) {
            // Here we simply reload the page without hiding the spinner.
            // The spinner will be removed on page reload from the
            // getApplicants function.
            defaultReload();
          });
        }).error(function (response, status) {
          AppService.setPaneShown(false);
          if (status === 400) { // Validation errors.
            QFormJSRValidation
              .markErrors($scope, $scope.formalAuditCtrl.evidenceForm, response);
          }
        });
    }

    /** Function to get applicants of submission */
    function getApplicants(submissionId) {
      AppService.setPaneShown(true);
      SelectiveService.getApplicantsForFormalAudit(submissionId).success(
        function (data) {
          vm.submittents = data;
          vm.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            noPager: true,
            sorting: {
              companyName: "asc"
            }
          }, {
            counts: [],
            total: vm.submittents.length,
            getData: function ($defer, params) {
              var orderedData = params.sorting() ?
                $filter('orderBy')(vm.submittents, params.orderBy()) :
                tabledata;
              $defer.resolve(
                orderedData.slice((params.page() - 1) * params.count(),
                  params.page() * params.count()));
            }
          });
          AppService.setPaneShown(false);
          return vm.tableParams;
        });
    }

    /** Function to cancel changes made to the form */
    function cancelButton(dirtyflag, goBack) {
      if (!dirtyflag) {
        if (goBack) {
          $state.go('examination.view', {
            id: vm.data.submission.id
          }, {
            reload: true
          });
        } else {
          defaultReload();
        }
      } else {
        var closeProjectEditModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="formalAuditCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'formalAuditCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              closeProjectEditModal.dismiss(reason);
            };
          }

        });

        return closeProjectEditModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            vm.dirtyFlag = false;
            if (goBack) {
              $state.go('examination.view', {
                id: vm.data.submission.id
              }, {
                reload: true
              });
            } else {
              defaultReload();
            }
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
      }
      return null;
    }

    /** Function to determine which operations are allowed */
    function findUserOperations() {
      vm.secFormalAuditExecute = AppService.isOperationPermitted(
        AppConstants.OPERATION.FORMAL_AUDIT_EXECUTE, null);
      vm.secTenderMove = AppService.isOperationPermitted(
        AppConstants.OPERATION.TENDER_MOVE, null);
      vm.secSentEmail = AppService.isOperationPermitted(
        AppConstants.OPERATION.SENT_EMAIL, null);
    }

    function sendProofsModal(submittents) {
      $uibModal
        .open({
          templateUrl: 'app/document/proofRequest/proofRequest.html',
          controller: 'ProofRequestController',
          controllerAs: 'proofRequestCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            submittents: function () {
              return submittents;
            }
          }
        });
    }

    /** Check if status offer opening closed has been set before */
    function hasOfferOpeningBeenClosedBefore(submissionId) {
      SubmissionService.hasOfferOpeningBeenClosedBefore(submissionId).success(
        function (data) {
          vm.offerOpeningClosedBefore = data;
        }).error(function (response, status) {});
    }

    /** Check if sent email button is disabled */
    function sendEmailButtonDisabled() {
      return !vm.secSentEmail ||
        vm.currentStatus === vm.status.PROCEDURE_COMPLETED ||
        vm.currentStatus === vm.status.PROCEDURE_CANCELED;
    }

    /** Function to disable the dropdown */
    function dropDownDisabled(submittent) {
      if (vm.data.submission) {
        return (!vm.secFormalAuditExecute || vm.currentStatus <
          vm.status.APPLICATION_OPENING_CLOSED ||
          vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED_S ||
          !submittent.formalExaminationFulfilled ||
          vm.data.submission.examinationIsLocked);
      }
      return false;
    }

    /** Function to disable the buttons */
    function disableButtons() {
      return (!vm.secFormalAuditExecute ||
        vm.currentStatus < vm.status.APPLICATION_OPENING_CLOSED ||
        vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED_S ||
        (!angular.isUndefined(vm.data.submission) &&
          vm.data.submission.examinationIsLocked));
    }

    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            return true;
          }
          return false;
        });
      }
      return null;
    });

    /** Function to reload current state */
    function defaultReload() {
      if (vm.dirtyFlag) {
        vm.dirtyFlag = false;
      }
      $state.reload();
    }

    /** Function to check for unsaved changes before sending proofs */
    function checkBeforeSendingProofs() {
      if (vm.dirtyFlag) {
        AppService.informAboutUnsavedChanges();
      } else {
        vm.sendProofsModal();
      }
    }

    /** Function to expand table height if required */
    function expandTableHeight(index) {
      // Check if the last dropdown is clicked and if the row height is not enough to
      // contain the expanded dropdown.
      if ((index === vm.submittents.length - 1) &&
        (document.getElementById("dropdownRow" + index).clientHeight < 111)) {
        // Check if the table height has not already been changed (expanded).
        if (!vm.tableHeightChanged) {
          vm.divTableHeight = {
            "height": ((document.getElementById("table").clientHeight) +
              111 - (document.getElementById(
                "dropdownRow" + index).clientHeight)).toString() + "px"
          };
          vm.tableHeightChanged = true;
        }
      } else {
        vm.divTableHeight = null;
        vm.tableHeightChanged = false;
      }
    }

    /** Check if all formal examinations are fulfilled */
    function allFormalExaminationsFulfilled() {
      for (var i in vm.submittents) {
        if (!vm.submittents[i].formalExaminationFulfilled) {
          return false;
        }
      }
      return true;
    }
  }
})();
