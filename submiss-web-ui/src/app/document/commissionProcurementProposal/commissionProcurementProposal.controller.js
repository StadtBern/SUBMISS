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
 * @name commissionProcurementProposal.controller.js
 * @description CommissionProcurementProposal controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("CommissionProcurementProposalController",
      CommissionProcurementProposalController);

  /** @ngInject */
  function CommissionProcurementProposalController($scope, $rootScope, $locale, OfferService,
    $state, $stateParams, NgTableParams, $filter, SubmissionService, AppService,
    CommissionProcurementProposalService, $uibModal, QFormJSRValidation, $location,
    $anchorScroll, $transitions, AppConstants, DocumentService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.commissionProcurementProposalForm = {};
    vm.commissionProcurementProposalForm.date = null;
    vm.commissionProcurementDateOpened = false;
    vm.commissionProcurementProposalForm.object = null;
    vm.commissionProcurementProposalForm.suitabilityAuditDropdown = null;
    vm.suitabilityAuditDropdownChoices = null;
    vm.commissionProcurementProposalForm.suitabilityAuditText = null;
    vm.commissionProcurementProposalForm.reservation = null;
    vm.commissionProcurementProposalForm.preRemarks = null;
    vm.commissionProcurementProposalForm.reasonGiven = null;
    vm.secCommissionProcurementProposalView = false;
    vm.secCommissionProcurementProposalEdit = false;
    vm.secCommissionProcurementProposalClose = false;
    vm.secCommissionProcurementProposalReopen = false;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.reopen = AppConstants.STATUS_TO_REOPEN;
    vm.reopenQuestion = AppConstants.STATUS_REOPEN_QUESTION;
    vm.reopenTitle = AppConstants.STATUS_REOPEN_TITLE;
    vm.currentStatus = null;
    vm.awardStatusesClosed = false;
    vm.isCompletedOrCancelled = false;
    vm.errorFieldsVisible = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.openCommissionProcurementDate = openCommissionProcurementDate;
    vm.readSubmission = readSubmission;
    vm.getOffersForCommissionProcurementProposal = getOffersForCommissionProcurementProposal;
    vm.showAwardRecipientJointVenturesSubcontractors = showAwardRecipientJointVenturesSubcontractors;
    vm.save = save;
    vm.cancelButton = cancelButton;
    vm.getSuitabilityAuditDropdownChoices = getSuitabilityAuditDropdownChoices;
    vm.isFormDisabled = isFormDisabled;
    vm.isFormVisible = isFormVisible;
    vm.closeCommissionProcurementProposalModal = closeCommissionProcurementProposalModal;
    vm.reopenCommissionProcurementProposal = reopenCommissionProcurementProposal;
    vm.checkForChanges = checkForChanges;
    vm.leadingZero = leadingZero;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.readSubmission($stateParams.submissionId);
      readStatusOfSubmission($stateParams.submissionId);
      vm.getSuitabilityAuditDropdownChoices();
      haveAwardStatusesBeenClosed($stateParams.submissionId);
    }

    /**Function to read submission by submission id */
    function readSubmission(submissionId) {
      CommissionProcurementProposalService.getSubmission(submissionId).success(function (data) {
        vm.submission = data;
        /* Implementing security */
        vm.secCommissionProcurementProposalView = AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_PROPOSAL_VIEW, null);
        vm.secCommissionProcurementProposalEdit = AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_PROPOSAL_EDIT, null);
        vm.secCommissionProcurementProposalClose = AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_PROPOSAL_CLOSE, null);
        vm.secCommissionProcurementProposalReopen = AppService.isOperationPermitted(AppConstants.OPERATION.COMMISSION_PROCUREMENT_PROPOSAL_REOPEN, null);
        /* Loading the commission procurement form with values from the submission */
        if (vm.submission.commissionProcurementProposalDate) {
          vm.commissionProcurementProposalForm.date = vm.submission.commissionProcurementProposalDate;
        }
        if (vm.submission.commissionProcurementProposalObject) {
          vm.commissionProcurementProposalForm.object = vm.submission.commissionProcurementProposalObject;
        }
        if (vm.submission.commissionProcurementProposalSuitabilityAuditDropdown) {
          vm.commissionProcurementProposalForm.suitabilityAuditDropdown = vm.submission.commissionProcurementProposalSuitabilityAuditDropdown;
        }
        if (vm.submission.commissionProcurementProposalSuitabilityAuditText) {
          vm.commissionProcurementProposalForm.suitabilityAuditText = vm.submission.commissionProcurementProposalSuitabilityAuditText;
        }
        if (vm.submission.commissionProcurementProposalReservation) {
          vm.commissionProcurementProposalForm.reservation = vm.submission.commissionProcurementProposalReservation;
        }
        if (vm.submission.commissionProcurementProposalBusiness) {
          vm.submission.commissionProcurementProposalBusiness = vm.leadingZero(vm.submission.commissionProcurementProposalBusiness);
        }
        if (vm.submission.commissionProcurementProposalPreRemarks) {
          vm.commissionProcurementProposalForm.preRemarks = vm.submission.commissionProcurementProposalPreRemarks;
        }
        if (vm.submission.commissionProcurementProposalReasonGiven) {
          vm.commissionProcurementProposalForm.reasonGiven = vm.submission.commissionProcurementProposalReasonGiven;
        }
        /* Retrieving offers */
        vm.getOffersForCommissionProcurementProposal(vm.submission.id);
        // If a command was given to close the commission procurement proposal
        // before the page reloaded, proceed with the command.
        if ($stateParams.proceedToClose) {
          vm.closeCommissionProcurementProposalModal();
        }
      }).error(function (response, status) {});
    }

    function leadingZero(input) {
      if (input < 10) {
        return '0' + input.toString();
      } else {
        return input;
      }
    }

    /**Get offers of submission */
    function getOffersForCommissionProcurementProposal(submissionId) {
      CommissionProcurementProposalService.getOffersBySubmission(submissionId)
        .success(function (data) {
          vm.offers = data;
          vm.commissionProcurementProposalForm.awardRecipients = [];
          /* Retrieving all award recipients */
          for (i = 0; i < vm.offers.length; i++) {
            if (vm.offers[i].isAwarded) {
              vm.commissionProcurementProposalForm.awardRecipients.push(vm.offers[i]);
            }
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

    /**Date modal functionality */
    function openCommissionProcurementDate() {
      vm.commissionProcurementDateOpened = !vm.commissionProcurementDateOpened;
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

    /** Save the commission procurement proposal form */
    function save() {
      // Save and update the form, only if the date field contains a valid value.
      if ($scope.commissionProcurementProposalCtrl.commissionProcurementForm.commissionProcurementDate.$valid) {
        // Variable to determine if the user has typed a non numerical value for the business field.
        vm.commissionProcurementProposalForm.invalidBusiness =
          $scope.commissionProcurementProposalCtrl.commissionProcurementForm.commissionProcurementBusiness.$viewValue === "" &&
          angular.isUndefined($scope.commissionProcurementProposalCtrl.commissionProcurementForm.commissionProcurementBusiness.$modelValue);
        if (angular.isUndefined(vm.commissionProcurementProposalForm.business) && vm.submission.commissionProcurementProposalBusiness &&
          !vm.commissionProcurementProposalForm.invalidBusiness) {
          vm.commissionProcurementProposalForm.business = Number(vm.submission.commissionProcurementProposalBusiness);
        }
        CommissionProcurementProposalService.updateCommissionProcurementProposal(vm.commissionProcurementProposalForm, vm.submission.id).success(function (data) {
          vm.saved = true;
          defaultReload();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            vm.errorFieldsVisible = true;
            response = handleResponse(response);
            $anchorScroll();
            QFormJSRValidation.markErrors($scope,
              $scope.commissionProcurementProposalCtrl.commissionProcurementForm, response);
          }
        });
      }
    }

    /** Function to handle the response content */
    function handleResponse(response) {
      for (i in response) {
        // In case of date error field, change the displayed message if the date view value is not empty.
        if (response[i].path === "procurementDateErrorField" &&
          $scope.commissionProcurementProposalCtrl.commissionProcurementForm.commissionProcurementDate.$viewValue) {
          response[i].message = "invalid_date_error_message";
        }
      }
      return response;
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

    /** Retrieve the suitability audit drop-down choices */
    function getSuitabilityAuditDropdownChoices() {
      CommissionProcurementProposalService.getSuitabilityAuditDropdownChoices().success(function (data) {
        vm.suitabilityAuditDropdownChoices = data;
      }).error(function (response, status) {});
    }

    /** Check if statuses award evaluation closed or manual award completed have been set before */
    function haveAwardStatusesBeenClosed(submissionId) {
      DocumentService.haveAwardStatusesBeenClosed(submissionId).success(function (data) {
        vm.awardStatusesClosed = data;
      }).error(function (response, status) {});
    }

    /** Function to determine if the form is visible */
    function isFormVisible(currentStatus, process) {
      return ((process === "INVITATION" || process === "OPEN" || process === "SELECTIVE" ||
        ((process === "NEGOTIATED_PROCEDURE" || process === "NEGOTIATED_PROCEDURE_WITH_COMPETITION") &&
          vm.submission.aboveThreshold)) && ((currentStatus >= vm.status.AWARD_EVALUATION_CLOSED ||
        currentStatus >= vm.status.MANUAL_AWARD_COMPLETED) || vm.awardStatusesClosed));
    }

    /** Check if form edit is disabled */
    function isFormDisabled(currentStatus) {
      return !((currentStatus >= vm.status.AWARD_EVALUATION_CLOSED ||
            currentStatus >= vm.status.MANUAL_AWARD_COMPLETED || currentStatus >= vm.status.COMMISSION_PROCUREMENT_PROPOSAL_STARTED) &&
          (currentStatus < vm.status.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED)) ||
        vm.isCompletedOrCancelled;
    }

    /**Create a function to close the commission procurement proposal */
    function closeCommissionProcurementProposalModal() {
      CommissionProcurementProposalService.closeProposalNoErrors(vm.submission.id)
        .success(function (data) {
          $uibModal.open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close"' +
              'ng-click="closeCommissionProcurementProposalModalCtrl.closeCommissionProcurementProposal(false); $close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Beschaffungsantrag abschliessen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Beschaffungsantrag wirklich abschliessen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary"' +
              'ng-click="closeCommissionProcurementProposalModalCtrl.closeCommissionProcurementProposal(true); $close()">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="closeCommissionProcurementProposalModalCtrl.closeCommissionProcurementProposal(false); $close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'closeCommissionProcurementProposalModalCtrl',
            backdrop: 'static',
            size: 'lg',
            keyboard: false,
            controller: function () {
              var vm2 = this;
              // Close the commission procurement proposal.
              vm2.closeCommissionProcurementProposal = function (response) {
                if (response) {
                  CommissionProcurementProposalService.closeCommissionProcurementProposal(vm.submission.id)
                    .success(function (data) {
                      defaultReload();
                    }).error(function (response, status) {
                      if (status === 400) { // Validation errors.
                        vm.errorFieldsVisible = true;
                        $anchorScroll('ErrorAnchor');
                        QFormJSRValidation.markErrors($scope,
                          $scope.commissionProcurementProposalCtrl.commissionProcurementForm, response);
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
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.commissionProcurementProposalCtrl.commissionProcurementForm, response);
          }
        });
    }

    /** Function to reopen the commission procurement proposal */
    function reopenCommissionProcurementProposal() {
      var reopenTenderStatusModal = AppService.reopenTenderStatusModal(vm.reopenTitle.COMMISSION_PROCUREMENT_PROPOSAL,
        vm.reopenQuestion.COMMISSION_PROCUREMENT_PROPOSAL, vm.reopen.COMMISSION_PROCUREMENT_PROPOSAL);
      return reopenTenderStatusModal.result.then(function (response) {
        if (!angular.isUndefined(response)) {
          var reopenForm = {
            reopenReason: response
          };
          CommissionProcurementProposalService.reopenCommissionProcurementProposal(reopenForm, $stateParams.id)
            .success(function (data) {
              defaultReload();
            }).error(function (response, status) {});
        }
      });
    }

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
        closeCommissionProcurementProposalModal();
        return null;
      }
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
  }
})();
