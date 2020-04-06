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
 * @name commissionProcurementDecision.controller.js
 * @description CommissionProcurementDecision controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document").controller("CommissionProcurementDecisionController",
      CommissionProcurementDecisionController);

  /** @ngInject */
  function CommissionProcurementDecisionController($scope, $rootScope, OfferService,
    $state, $stateParams, $filter, SubmissionService, AppService,
    CommissionProcurementProposalService, $uibModal, QFormJSRValidation,
    $anchorScroll, $transitions, AppConstants, DocumentService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.cPDecision = {};
    vm.secCommissionProcurementDecisionView = false;
    vm.secCommissionProcurementDecisionEdit = false;
    vm.secCommissionProcurementDecisionClose = false;
    vm.secCommissionProcurementDecisionReopen = false;
    vm.status = AppConstants.STATUS;
    vm.reopen = AppConstants.STATUS_TO_REOPEN;
    vm.reopenQuestion = AppConstants.STATUS_REOPEN_QUESTION;
    vm.reopenTitle = AppConstants.STATUS_REOPEN_TITLE;
    vm.isCompletedOrCancelled = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.cancelButton = cancelButton;
    vm.isFormDisabled = isFormDisabled;
    vm.isFormVisible = isFormVisible;
    vm.showAwardRecipientJointVenturesSubcontractors = showAwardRecipientJointVenturesSubcontractors;
    vm.closeCommissionProcurementDecisionModal = closeCommissionProcurementDecisionModal;
    vm.reopenCommissionProcurementDecision = reopenCommissionProcurementDecision;
    vm.checkForChanges = checkForChanges;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readSubmission($stateParams.submissionId);
      readStatusOfSubmission($stateParams.submissionId);
      getOffers($stateParams.submissionId);
      hasCommissionProcurementProposalBeenClosed($stateParams.submissionId);
    }

    /**Function to read submission by submission id */
    function readSubmission(submissionId) {
      CommissionProcurementProposalService.getSubmission(submissionId).success(function (data) {
        vm.submission = data;
        // Implementing security.
        vm.secCommissionProcurementDecisionView =
          AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_DECISION_VIEW, null);
        vm.secCommissionProcurementDecisionEdit =
          AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_DECISION_EDIT, null);
        vm.secCommissionProcurementDecisionClose =
          AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_DECISION_CLOSE, null);
        vm.secCommissionProcurementDecisionReopen =
          AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_DECISION_REOPEN, null);
        // Initializing the commission procurement decision form.
        if (vm.submission.commissionProcurementDecisionRecommendation) {
          vm.cPDecision.recommendation = vm.submission.commissionProcurementDecisionRecommendation;
        }
        // If a command was given to close the commission procurement decision
        // before the page reloaded, proceed with the command.
        if ($stateParams.proceedToClose) {
          vm.closeCommissionProcurementDecisionModal();
        }
      }).error(function (response, status) {});
    }

    /** Get status of submission */
    function readStatusOfSubmission(submissionId) {
      SubmissionService.getCurrentStatusOfSubmission(submissionId).success(function (data) {
        if (data === vm.status.PROCEDURE_COMPLETED ||
          data === vm.status.PROCEDURE_CANCELED) {
          vm.isCompletedOrCancelled = true;
          AppService.getSubmissionStatuses(submissionId).success(function (data) {
            vm.statusHistory = data;
            vm.currentStatus = AppService.assignCurrentStatus(vm.statusHistory);
          });
        } else {
          vm.currentStatus = data;
        }
      }).error(function (response, status) {

      });
    }

    /**Get offers of submission */
    function getOffers(submissionId) {
      CommissionProcurementProposalService.getOffersBySubmission(submissionId)
        .success(function (data) {
          vm.offers = data;
          vm.awardRecipients = [];
          /* Retrieving all award recipients */
          for (i = 0; i < vm.offers.length; i++) {
            if (vm.offers[i].isAwarded) {
              vm.awardRecipients.push(vm.offers[i]);
            }
          }
        }).error(function (response, status) {});
    }

    /** Show award recipient with its joint ventures and subcontractors */
    function showAwardRecipientJointVenturesSubcontractors(awardRecipient) {
      /* text is going to contain the award recipient name and location, joint venture names and subcontractor names */
      var text = awardRecipient.submittent.companyId.companyName + ", " + awardRecipient.submittent.companyId.location;
      /**Show joint ventures */
      if (awardRecipient.submittent.jointVentures.length > 0) {
        text = text + " (ARGE: " + alphabeticalOrderText(awardRecipient.submittent.jointVentures);
      }
      /**Show subcontractors */
      if (awardRecipient.submittent.subcontractors.length > 0) {
        if (awardRecipient.submittent.jointVentures.length > 0) { //Check if the text already contains joint Ventures
          text = text + ", Sub. U: ";
        } else {
          text = text + " (Sub. U: ";
        }
        text = text + alphabeticalOrderText(awardRecipient.submittent.subcontractors);
      }

      function alphabeticalOrderText(companies) {
        var tempText = "";
        companies = $filter('orderBy')(companies, 'companyName'); //Ordered Companies by Company Name
        for (var i = 0; i < companies.length; i++) {
          tempText = tempText + companies[i].companyName;
          if (i < companies.length - 1) {
            tempText = tempText + " / ";
          }
        }
        return tempText;
      }

      if (awardRecipient.submittent.jointVentures.length > 0 || awardRecipient.submittent.subcontractors.length > 0) {
        text = text + ")";
      }
      return text;
    }

    /** Save the commission procurement decision form */
    function save() {
      CommissionProcurementProposalService.
      updateCommissionProcurementDecision(vm.cPDecision, vm.submission.id).success(function (data) {
        vm.saved = true;
        defaultReload();
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          $anchorScroll();
          QFormJSRValidation.markErrors($scope,
            $scope.cPDecisionCtrl.commissionDecisionForm, response);
        }
      });
    }

    /** Implementing the cancellation button */
    function cancelButton(dirtyFlag) {
      if (!dirtyFlag) {
        defaultReload();
      } else {
        var closeCancelModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
            '</div>' + '</div>' + '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="cancelModalCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'cancelModalCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              closeCancelModal.dismiss(reason);
            };
          }
        });
        return closeCancelModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            defaultReload();
          } else {
            return false;
          }
          return null;
        });
      }
      return null;
    }

    /** Check if the commission procurement proposal has been closed before */
    function hasCommissionProcurementProposalBeenClosed(submissionId) {
      DocumentService.hasCommissionProcurementProposalBeenClosed(submissionId)
        .success(function (data) {
          vm.commissionProcurementProposalClosed = data;
        }).error(function (response, status) {});
    }

    /** Function to determine if the form is visible */
    function isFormVisible(process) {
      return (vm.commissionProcurementProposalClosed && (process === AppConstants.PROCESS.SELECTIVE ||
        process === AppConstants.PROCESS.OPEN || process === AppConstants.PROCESS.INVITATION ||
        ((process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE || AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
          vm.submission.aboveThreshold)));
    }

    /** Check if form edit is disabled */
    function isFormDisabled(currentStatus) {
      return !((currentStatus >= vm.status.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED ||
            currentStatus >= vm.status.COMMISSION_PROCUREMENT_DECISION_STARTED) &&
          currentStatus < vm.status.COMMISSION_PROCUREMENT_DECISION_CLOSED) ||
        vm.isCompletedOrCancelled;
    }

    /** Create a function to close the commission procurement decision */
    function closeCommissionProcurementDecisionModal() {
      CommissionProcurementProposalService.closeDecisionNoErrors(vm.submission.id)
        .success(function (data) {
          $uibModal.open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="closeCPDecisionModalCtrl.closeCommissionProcurementDecision(false); $close()">' +
              '<span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">BeKo Beschluss abschliessen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den BeKo Beschluss wirklich abschliessen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary"' +
              'ng-click="closeCPDecisionModalCtrl.closeCommissionProcurementDecision(true); $close()">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="closeCPDecisionModalCtrl.closeCommissionProcurementDecision(false); $close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'closeCPDecisionModalCtrl',
            backdrop: 'static',
            size: 'lg',
            keyboard: false,
            controller: function () {
              var vm2 = this;
              // Close the commission procurement proposal.
              vm2.closeCommissionProcurementDecision = function (response) {
                if (response) {
                  CommissionProcurementProposalService.closeCommissionProcurementDecision(vm.submission.id)
                    .success(function (data) {
                      defaultReload();
                    }).error(function (response, status) {
                      if (status === 400) { // Validation errors.
                        $anchorScroll('ErrorAnchor');
                        QFormJSRValidation.markErrors($scope,
                          $scope.cPDecisionCtrl.commissionDecisionForm, response);
                      }
                    });
                } else {
                  if ($stateParams.proceedToClose) {
                    defaultReload();
                  }
                }
              }
            }
          });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.cPDecisionCtrl.commissionDecisionForm, response);
          }
        });
    }

    /** Function to reopen the commission procurement decision */
    function reopenCommissionProcurementDecision() {
      var reopenTenderStatusModal = AppService.reopenTenderStatusModal(vm.reopenTitle.COMMISSION_PROCUREMENT_DECISION,
        vm.reopenQuestion.COMMISSION_PROCUREMENT_DECISION, vm.reopen.COMMISSION_PROCUREMENT_DECISION);
      return reopenTenderStatusModal.result.then(function (response) {
        if (!angular.isUndefined(response)) {
          var reopenForm = {
            reopenReason: response
          };
          CommissionProcurementProposalService.reopenCommissionProcurementDecision(reopenForm, $stateParams.id)
            .success(function (data) {
              defaultReload();
            }).error(function (response, status) {});
        }
      });
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

    /** Function to check for unsaved changes before closing the status */
    function checkForChanges(changesMade) {
      if (changesMade) {
        var proceedWithoutSavingModal = AppService.proceedWithoutSaving();
        return proceedWithoutSavingModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            $state.go($state.current, {
              proceedToClose: response
            }, {
              reload: true
            });
          }
        });
      } else {
        closeCommissionProcurementDecisionModal();
        return null;
      }
    }

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

  }
})();
