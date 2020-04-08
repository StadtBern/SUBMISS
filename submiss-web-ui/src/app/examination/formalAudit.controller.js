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
 * @name formalAudit.controller.js
 * @description FormalAuditController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.examination")
    .controller("FormalAuditController", FormalAuditController);

  /** @ngInject */
  function FormalAuditController($rootScope, $scope, $state, $stateParams,
    $transitions,
    SubmissionService, ExaminationService, QFormJSRValidation, NgTableParams,
    $filter, $element, $uibModal, AppService, AppConstants, TasksService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.submittent = {};
    vm.taskCreateForm = {};
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.offers
    });
    // Initialize security variable
    vm.secFormalAuditExecute = null;
    vm.secFormalAuditClose = false;
    vm.secFormalAuditReopen = false;
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
    vm.reopen = AppConstants.STATUS_TO_REOPEN;
    vm.reopenQuestion = AppConstants.STATUS_REOPEN_QUESTION;
    vm.reopenTitle = AppConstants.STATUS_REOPEN_TITLE;
    vm.process = AppConstants.PROCESS;
    vm.offerOpeningClosedBefore = null;
    vm.dirtyFlag = false;
    vm.isCompletedOrCancelled = false;
    vm.noCheckedSubmittents = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    // Activating the controller.
    activate();
    vm.save = save;
    vm.cancelButton = cancelButton;
    vm.closeFormalAudit = closeFormalAudit;
    vm.reopenFormalAudit = reopenFormalAudit;
    vm.checkUncheckAll = checkUncheckAll;
    vm.activeEvidenceButton = activeEvidenceButton;
    vm.sendProofsModal = sendProofsModal;
    vm.reopenFormalAuditDisabledForPL = reopenFormalAuditDisabledForPL;
    vm.reopenFormalAuditDisabled = reopenFormalAuditDisabled;
    vm.checkForChanges = checkForChanges;
    vm.formalAuditCloseButtonDisabled = formalAuditCloseButtonDisabled;
    vm.sendEmailButtonDisabled = sendEmailButtonDisabled;
    vm.dropDownDisabled = dropDownDisabled;
    vm.disableButtons = disableButtons;
    vm.getObjectInfo = getObjectInfo;
    vm.notesDisabled = notesDisabled;
    vm.notesMandatory = notesMandatory;
    vm.requestProofs = requestProofs;
    vm.checkBeforeSendingProofs = checkBeforeSendingProofs;
    vm.checkBeforeRequestingProofs = checkBeforeRequestingProofs;
    vm.allFormalExaminationsFulfilled = allFormalExaminationsFulfilled;
    vm.expandTableHeight = expandTableHeight;
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      SubmissionService.loadFormalAudit($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            readStatusOfSubmission($stateParams.id);
            readSubmission($stateParams.id);
            findUserOperations();
            // If a command was given to close the formal audit before the page
            // reloaded, proceed with the command.
            if ($stateParams.proceedToClose) {
              closeFormalAudit();
            }
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
    function readSubmission(id) {
      SubmissionService.readSubmission(id).success(function (data) {
        vm.data.submission = data;
        hasOfferOpeningBeenClosedBefore(vm.data.submission.id);
        vm.secFormalAuditReopen = AppService.isOperationPermitted(
          AppConstants.OPERATION.FORMAL_AUDIT_REOPEN,
          vm.data.submission.process);
        revertYesNOLabel();
        readSubmittentsBySubmission(id);
      }).error(function (response, status) {

      });
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(
        function (data) {
          if (data === vm.status.PROCEDURE_COMPLETED ||
            data === vm.status.PROCEDURE_CANCELED) {
            vm.isCompletedOrCancelled = true;
            AppService.getSubmissionStatuses(id)
              .success(function (data) {
                vm.statusHistory = data;
                vm.currentStatus = AppService.assignCurrentStatus(
                  vm.statusHistory);
              });
          } else {
            vm.currentStatus = data;
          }
        }).error(function (response, status) {

      });
    }

    function save(submittents) {
      ExaminationService.examinationLockedByAnotherUser(vm.data.submission.id)
        .success(function (data) {
          AppService.setPaneShown(true);
          ExaminationService.updateFormalAuditExamination(submittents)
            .success(function (data) {
              ExaminationService.updateSubmissionFormalAuditExaminationStatus(
                  $stateParams.id)
                .success(function (data) {
                  // Here we simply reload the page without hiding the spinner.
                  // The spinner will be removed on page reload from the
                  // readSubmittentsBySubmission function.
                  defaultReload();
                });
            }).error(function (response, status) {
              AppService.setPaneShown(false);
              if (status === 400) { // Validation errors.
                QFormJSRValidation.markErrors($scope,
                  $scope.formalAuditCtrl.evidenceForm, response);
              }
            });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.formalAuditCtrl.evidenceForm, response);
          }
        });
    }

    function readSubmittentsBySubmission(id) {
      AppService.setPaneShown(true);
      SubmissionService.readSubmittentsBySubmission(id)
        .then(function (results) {
          vm.submittents = results.data;
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
              var orderedData = params.sorting() ? $filter('orderBy')(
                vm.submittents, params.orderBy()) : tabledata;
              $defer.resolve(
                orderedData.slice((params.page() - 1) * params.count(),
                  params.page() * params.count()));
            }
          });
          AppService.setPaneShown(false);
          return vm.tableParams;
        });
    }

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

    // function for selecting/unselecting all checkboxes by pressing the
    // main one and activate button Nachweisaufforderung gewünscht.
    function checkUncheckAll() {
      vm.activeButton = false;
      if (vm.checkall) {
        vm.checkall = true;
        vm.activeButton = true;
      } else {
        vm.checkall = false;
        vm.activeButton = false;
      }
      // if main is checked/unchecked then give the same status to the
      // others from the list.
      angular.forEach(vm.submittents, function (submittent) {
        submittent.checked = vm.checkall;
      });
    }

    // Function that checks if one of the checkboxes is selected.
    //If at least one is checked
    // then the button must be active.
    function activeEvidenceButton() {
      vm.activeButton = false;
      angular.forEach(vm.submittents, function (submittent) {
        if (submittent.checked === true) {
          vm.activeButton = true;
        }
      });

    }

    function closeFormalAudit() {
      var openCloseFormalAuditModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="formalAuditCtrl.closeModal(false)"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Formelle Prüfung abschliessen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie die Formelle Prüfung wirklich abschliessen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="formalAuditCtrl.closeModal(true)">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="formalAuditCtrl.closeModal(false)">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'formalAuditCtrl',
          backdrop: 'static',
          size: 'lg',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeModal = function (reason) {
              openCloseFormalAuditModal.dismiss(reason);
            };
          }
        });
      return openCloseFormalAuditModal.result.then(function () {
        // Modal Success Handler
      }, function (response) { // Modal Dismiss Handler
        if (response) {
          ExaminationService.closeFormalAudit(vm.data.submission.id, vm.data.submission.version)
            .success(function (data) {
              defaultReload();
            }).error(function (response, status) {
              // Remove no checked submittents error message if present.
              if (vm.noCheckedSubmittents) {
                vm.noCheckedSubmittents = false;
              }
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                QFormJSRValidation.markErrors($scope,
                  $scope.formalAuditCtrl.evidenceForm, response);
              }
            });
        } else {
          if ($stateParams.proceedToClose) {
            defaultReload();
          }
          return false;
        }
        return null;
      });
    }

    function findUserOperations() {
      vm.secFormalAuditExecute = AppService.isOperationPermitted(
        AppConstants.OPERATION.FORMAL_AUDIT_EXECUTE, null);
      vm.secFormalAuditClose = AppService.isOperationPermitted(
        AppConstants.OPERATION.FORMAL_AUDIT_CLOSE, null);
      vm.secTenderMove = AppService.isOperationPermitted(
        AppConstants.OPERATION.TENDER_MOVE, null);
      vm.secSentEmail = AppService.isOperationPermitted(
        AppConstants.OPERATION.SENT_EMAIL, null);
    }

    /*
     * We need to revert the labels for the following 2 processes because the option column has inverted logic
     */
    function revertYesNOLabel() {
      if (vm.data.submission.process === vm.process.NEGOTIATED_PROCEDURE ||
        vm.data.submission.process ===
        vm.process.NEGOTIATED_PROCEDURE_WITH_COMPETITION) {
        vm.options = [{
            value: true,
            label: 'Nein'
          },
          {
            value: false,
            label: 'Ja'
          }
        ];
      }
    }

    function reopenFormalAudit() {
      var reopenTenderStatusModal = AppService.reopenTenderStatusModal(
        vm.reopenTitle.FORMAL_AUDIT, vm.reopenQuestion.FORMAL_AUDIT,
        vm.reopen.FORMAL_AUDIT);
      return reopenTenderStatusModal.result
        .then(function (response) {
          var reopenForm = {};
          if (!angular.isUndefined(response)) {
            reopenForm.reopenReason = response;
            ExaminationService.reopenFormalAudit(reopenForm,
              vm.data.submission.id, vm.data.submission.version).success(
              function (data) {
                defaultReload();
              }).error(function (response, status) {
              if (status === 400 || status === 409) {
                QFormJSRValidation.markErrors($scope,
                  $scope.formalAuditCtrl.evidenceForm, response);
              }
            });
          }
        });
    }

    function sendProofsModal(submittents) {
      var proofsModal = $uibModal
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
      proofsModal.result
        .then(function (stateReloaded) {
          if (stateReloaded && vm.dirtyFlag) {
            vm.dirtyFlag = false;
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

    /** Function to determine if the PL can reopen the formal audit */
    function reopenFormalAuditDisabledForPL(user, aboveThreshold) {
      return (user === vm.group.PL && aboveThreshold);
    }

    function reopenFormalAuditDisabled() {
      return !vm.secFormalAuditReopen || vm.currentStatus >=
        vm.status.MANUAL_AWARD_COMPLETED;
    }

    /** Function to check for unsaved changes before closing the status */
    function checkForChanges() {
      vm.dirtyFlag = isFormChanged();
      if (vm.dirtyFlag) {
        var proceedWithoutSavingModal = AppService.proceedWithoutSaving();
        return proceedWithoutSavingModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            $state.go($state.current, {
              id: $stateParams.id,
              proceedToClose: response
            }, {
              reload: true
            });
          }
        });
      } else {
        closeFormalAudit();
        return null;
      }
    }

    /** Check if formal audit close button is disabled */
    function formalAuditCloseButtonDisabled() {
      return !vm.secFormalAuditClose ||
        (AppService.getLoggedUser().userGroup.name === vm.group.PL &&
          vm.data.submission.process === vm.process.SELECTIVE) ||
        vm.currentStatus < vm.status.FORMAL_EXAMINATION_STARTED ||
        vm.currentStatus === vm.status.PROCEDURE_COMPLETED ||
        vm.currentStatus === vm.status.PROCEDURE_CANCELED;
    }

    /** Check if send email button is disabled */
    function sendEmailButtonDisabled() {
      return !vm.secSentEmail ||
        vm.currentStatus === vm.status.PROCEDURE_COMPLETED ||
        vm.currentStatus === vm.status.PROCEDURE_CANCELED ||
        allFormalExaminationsFulfilled();
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

    /** Function to disable the dropdown */
    function dropDownDisabled(submittent) {
      if (vm.data.submission) {
        return (!submittent.formalExaminationFulfilled &&
            (vm.data.submission.process === vm.process.OPEN ||
              vm.data.submission.process === vm.process.INVITATION) ||
            !vm.secFormalAuditExecute || (vm.currentStatus >=
              vm.status.FORMAL_AUDIT_COMPLETED &&
              vm.currentStatus !== vm.status.AWARD_EVALUATION_STARTED) ||
            (vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
              vm.offerOpeningClosedBefore) ||
            (vm.data.submission.process === vm.process.SELECTIVE &&
              (vm.currentStatus < vm.status.OFFER_OPENING_CLOSED ||
                vm.currentStatus >= vm.status.FORMAL_AUDIT_COMPLETED))) ||
          vm.isCompletedOrCancelled;
      }
      return false;
    }

    /** Function to disable the buttons */
    function disableButtons() {
      return (!vm.secFormalAuditExecute ||
        vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED ||
        ((vm.data.submission.process === vm.process.SELECTIVE) &&
          (vm.currentStatus < vm.status.APPLICATION_OPENING_CLOSED ||
            vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED_S)));
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

    /** Function to navigate to current state with default parameters */
    function defaultReload() {
      if (vm.dirtyFlag) {
        vm.dirtyFlag = false;
      }
      $state.go($state.current, {
        proceedToClose: null
      }, {
        reload: true
      });
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }

    /** Function to disable the format audit notes */
    function notesDisabled() {
      return (!vm.secFormalAuditExecute || vm.currentStatus >=
        vm.status.FORMAL_AUDIT_COMPLETED ||
        vm.currentStatus < vm.status.OFFER_OPENING_CLOSED);
    }

    /** Function to mark formal audit notes as mandatory */
    function notesMandatory() {
      if (vm.submittents) {
        for (var i = 0; i < vm.submittents.length; i++) {
          if ((!vm.submittents[i].formalExaminationFulfilled &&
              vm.submittents[i].existsExclusionReasons === false) ||
            (vm.submittents[i].formalExaminationFulfilled &&
              vm.submittents[i].existsExclusionReasons === true)) {
            return true;
          }
        }
        return false;
      }
      return false;
    }

    function requestProofs() {
      vm.taskCreateForm.submissionID = vm.data.submission.id;
      vm.taskCreateForm.taskType = AppConstants.TASK_TYPES[1];
      vm.taskCreateForm.submittentIDs = [];
      //We will use the checked property in order to get the Company IDs
      for (var i = 0; i <= vm.submittents.length - 1; i++) {
        if (!angular.isUndefined(vm.submittents[i].checked) &&
          vm.submittents[i].checked) {
          vm.taskCreateForm.submittentIDs.push(vm.submittents[i].companyId.id);
        }
      }
      if (vm.taskCreateForm.submittentIDs.length > 0) {
        TasksService.createTask(vm.taskCreateForm)
          .success(function (data) {
            $state.reload();
          })
          .error(function (response, status) {});
      } else {
        vm.noCheckedSubmittents = true;
        $scope.formalAuditCtrl.evidenceForm.$invalid = true;
      }
    }

    /** Function to check for unsaved changes before sending proofs */
    function checkBeforeSendingProofs(submittents) {
      vm.dirtyFlag = isFormChanged();
      if (vm.dirtyFlag) {
        AppService.informAboutUnsavedChanges();
      } else {
        vm.sendProofsModal(submittents);
      }
    }

    /** Function to check for unsaved changes before requesting proofs */
    function checkBeforeRequestingProofs() {
      vm.dirtyFlag = isFormChanged();
      if (vm.dirtyFlag) {
        AppService.informAboutUnsavedChanges();
      } else {
        vm.requestProofs();
      }
    }

    /** Function to check if the form has been changed */
    function isFormChanged() {
      if ($scope.formalAuditCtrl.evidenceForm.$dirty && vm.data.submission) {
        if (vm.data.submission.process === vm.process.NEGOTIATED_PROCEDURE ||
          vm.data.submission.process ===
          vm.process.NEGOTIATED_PROCEDURE_WITH_COMPETITION) {
          // In case of the two above processes, only consider changes made to the
          // "notes" and "options" elements and not the whole form.
          // Internet Explorer does not support Object.entries. A polyfill should be implemented.
          // https://stackoverflow.com/questions/42446062/object-doesnt-support-property-or-method-entries
          if (!Object.entries) {
            Object.entries = function (obj) {
              var ownProps = Object.keys(obj);
              var i = ownProps.length;
              var resArray = new Array(i); // preallocate the Array
              while (i >= 0) {
                resArray[i] = [ownProps[i], obj[ownProps[i]]];
                i--;
              }
              return resArray;
            };
          }
          var properties = Object.entries($scope.formalAuditCtrl.evidenceForm);
          // https://stackoverflow.com/questions/30867172/code-not-running-in-ie-11-works-fine-in-chrome/30867255
          if (!String.prototype.startsWith) {
            String.prototype.startsWith = function (searchString, position) {
              position = position || 0;
              return this.indexOf(searchString, position) === position;
            };
          }
          for (var i in properties) {
            if ((properties[i][0].startsWith("options") ||
                properties[i][0].startsWith("notes")) &&
              properties[i][1].$dirty) {
              return true;
            }
          }
          return false;
        } else {
          // In case of any other process, just check if the form has been changed as a whole.
          return vm.dirtyFlag;
        }
      }
      // If no changes have been made to the form at all, just ignore the above code.
      return false;
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
  }
})();
