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
 * @name submissionCancel.controller.js
 * @description SubmissionCancel controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("AwardInfoController", AwardInfoController);

  /** @ngInject */
  function AwardInfoController($scope, $rootScope, $locale,
    $state, $stateParams, NgTableParams,
    $uibModal, AppService, AppConstants,
    StammdatenService,
    $q, QFormJSRValidation, $filter,
    SubmissionService, $transitions) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var getAwardInfoFunction, getExclusionReasonsFunction;
    var secAwardInfoEdit = false;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.awardInfo = null;
    vm.availableDateOpened = false;
    vm.displayedOffer = {};
    vm.currentStatus;
    vm.invalidDate = false;
    vm.awardedNumber;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.openAvailableDate = openAvailableDate;
    vm.openOfferAccordion = openOfferAccordion;
    vm.save = save;
    vm.resetPage = resetPage;
    vm.filterExcluded = filterExcluded;
    vm.isNotNegotiatedProcess = isNotNegotiatedProcess;
    vm.isAwardInfoDisabled = isAwardInfoDisabled;
    vm.formatAmount = formatAmount;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getAwardInfoFunction = getAwardInfo($stateParams.submissionId);
      getExclusionReasonsFunction = getExclusionReasons();
      $q.all([getAwardInfoFunction, getExclusionReasonsFunction]).then(function (resolves) {
        assignExclusionReasons();
      });
      readStatusOfSubmission($stateParams.submissionId);
    }

    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result.then(function (response) {
          if (response) {
            AppService.setIsDirty(false);
            vm.dirtyFlag = false;
            return true;
          }
          AppService.setIsDirty(false);
          return false;
        });
      }
    });


    /** get the award info */
    function getAwardInfo(id) {
      return SubmissionService.getAwardInfo(id).success(function (data) {
        vm.awardInfo = data;
        secAwardInfoEdit = AppService.isOperationPermitted(AppConstants.OPERATION.AWARD_INFO_EDIT, vm.awardInfo.submission.process);
        // case create, initialise
        if (vm.awardInfo.id === null) {
          vm.awardInfo.availableDate = new Date();
          vm.awardInfo.objectNameRead = true;
          vm.awardInfo.projectNameRead = true;
          vm.awardInfo.workingClassRead = true;
          vm.awardInfo.descriptionRead = true;
        }
      }).error(function (response, status) {});
    }

    /** Load the master list value data for the exclusion reason */
    function getExclusionReasons() {
      return StammdatenService.getMasterListValueHistoryDataBySubmission($stateParams.submissionId, "ExclusionCriterion").success(
        function (data) {
          vm.exclusionReasons = $filter('orderBy')(data, 'value1');
        }).error(function (response, status) {

      });
    }

    /** Inform the exclusion reasons checkboxes about the data */
    function assignExclusionReasons() {
      vm.awardedNumber = 0;
      for (var io = 0; io < vm.awardInfo.offers.length; io++) {
        // reopen previously opened accordion
        if ($stateParams.displayedOfferId === vm.awardInfo.offers[io].id) {
          vm.awardInfo.offers[io].show = true;
          vm.displayedOffer = vm.awardInfo.offers[io];
        }
        // add the awarded offers in order to display the field
        // 'Manueller Nettobetrag exkl. MWST' only in case more than one has been awarded
        if (vm.awardInfo.offers[io].isAwarded) {
          vm.awardedNumber++;
        }
        // create a new field stating whether each exclusion reason is selected
        vm.awardInfo.offers[io].exclusionReasonCheckboxes = [];
        for (var i = 0; i < vm.exclusionReasons.length; i++) {
          var exclusionReason = {
            id: vm.exclusionReasons[i].masterListValueId.id,
            value: vm.exclusionReasons[i].value1 + " " + vm.exclusionReasons[i].value2,
            selected: false,
            disabled: false
          };
          // special conditions
          if ((vm.exclusionReasons[i].value1 === AppConstants.EXCLUSION_REASON_B &&
              (vm.awardInfo.offers[io].existsExclusionReasons ||
                !vm.awardInfo.offers[io].formalExaminationFulfilled)) ||
            (vm.exclusionReasons[i].value1 === AppConstants.EXCLUSION_REASON_C &&
              !vm.awardInfo.offers[io].mustCriteriaFulfilled)) {
            exclusionReason.selected = true;
            exclusionReason.disabled = true;
          } else {
            for (var j = 0; j < vm.awardInfo.offers[io].exclusionReasons.length; j++) {
              if (exclusionReason.id === vm.awardInfo.offers[io].exclusionReasons[j].id) {
                exclusionReason.selected = true;
                break;
              }
            }
          }
          vm.awardInfo.offers[io].exclusionReasonCheckboxes.push(exclusionReason);
        }
      }
    }

    /** Get the status of the submission */
    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(function (data) {
        vm.currentStatus = data;
      }).error(function (response, status) {

      });
    }

    /** Date modal functionality */
    function openAvailableDate() {
      vm.availableDateOpened = !vm.availableDateOpened;
    }

    function openOfferAccordion(offer) {
      offer.show = !offer.show;
      // if the details of one offer are opened, then close the one that
      // was opened before, so that there is always only one offer open
      if (offer.show) {
        if (vm.displayedOffer !== null) {
          vm.displayedOffer.show = false;
        }
        vm.displayedOffer = offer;
      } else {
        vm.displayedOffer = {};
      }
    }

    /** Save the award info form */
    function save() {
      AppService.setIsDirty(false);
      if (angular.isUndefined(vm.awardInfo.availableDate)) {
        vm.invalidDate = true;
      } else {
        vm.invalidDate = false;
        for (var io = 0; io < vm.awardInfo.offers.length; io++) {
          // send the data about the exclusion reasons back in the accepted format
          vm.awardInfo.offers[io].exclusionReasons = [];
          for (var i = 0; i < vm.awardInfo.offers[io].exclusionReasonCheckboxes.length; i++) {
            if (vm.awardInfo.offers[io].exclusionReasonCheckboxes[i].selected) {
              var exclusionReason = {
                id: vm.awardInfo.offers[io].exclusionReasonCheckboxes[i].id
              };
              vm.awardInfo.offers[io].exclusionReasons.push(exclusionReason);
            }
          }
        }
        if (angular.isUndefined(vm.awardInfo.id) || vm.awardInfo.id == null) {
          SubmissionService.createAwardInfo(vm.awardInfo).success(function (data) {
            AppService.setIsDirty(false);
            defaultReload();
          }).error(function (response, status) {
            if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.awardInfoCtrl.awardInfoHtmlForm, response);
            }
          });
        } else {
          SubmissionService.updateAwardInfo(vm.awardInfo).success(function (data) {
            AppService.setIsDirty(false);
            defaultReload();
          }).error(function (response, status) {
            if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.awardInfoCtrl.awardInfoHtmlForm, response);
            }
          });
        }
      }
    }

    /** Show a confirmation message for cancel of the process and if the user confirms reset the page to the saved values */
    function resetPage(dirtyflag) {
      if (!dirtyflag) {
        defaultReload();
        return true;
      } else {
        var resetPageModal = $uibModal.open({
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
            '<button type="button" class="btn btn-primary" ng-click="awardInfoCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'awardInfoCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              resetPageModal.dismiss(reason);
            };
          }

        });
        return resetPageModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            defaultReload();
            return true;
          } else {
            return false;
          }
        });
      }
    }

    /** Function to navigate to current state with parameter indicating which offer to have opened */
    function defaultReload() {
      vm.dirtyFlag = false;
      $state.go($state.current, {
        displayedOfferId: vm.displayedOffer.id
      }, {
        reload: true
      });
    }

    /** Function to filter the excluded submittents
     *  In case of selective process a different check needs to be made */
    function filterExcluded(index) {
      return function (item, index) {
        if (vm.awardInfo.submission.process === AppConstants.PROCESS.SELECTIVE) {
          return item['existsExclusionReasons'];
        } else {
          return !item['qExExaminationIsFulfilled'];
        }
      }
    }

    /** Function to check if the process is not NEGOTIATED_PROCEDURE and not NEGOTIATED_PROCEDURE_WITH_COMPETITION  */
    function isNotNegotiatedProcess() {
      return vm.awardInfo !== null &&
        vm.awardInfo.submission.process !== AppConstants.PROCESS.NEGOTIATED_PROCEDURE &&
        vm.awardInfo.submission.process !== AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION;
    }

    /** function that checks if the form needs to be disabled
     * It will be disabled if the user has no right to edit the awardInfo,
     * or if the status is cancelled or completed,
     * or if the process is above threshold for PL
     * or if the status is before the proper status according to the process type and above threshold */
    function isAwardInfoDisabled() {
      if (vm.awardInfo === null) {
        return true;
      } else {
        var basicCheck = vm.currentStatus === AppConstants.STATUS.PROCEDURE_CANCELED ||
          vm.currentStatus === AppConstants.STATUS.PROCEDURE_COMPLETED ||
          !secAwardInfoEdit;
        if (vm.isNotNegotiatedProcess()) {
          return basicCheck ||
            vm.currentStatus < AppConstants.STATUS.COMMISSION_PROCUREMENT_DECISION_CLOSED;
        } else {
          if (vm.awardInfo.submission.aboveThreshold) {
            return basicCheck ||
              AppService.getLoggedUser().userGroup.name === AppConstants.GROUP.PL ||
              vm.currentStatus < AppConstants.STATUS.COMMISSION_PROCUREMENT_DECISION_CLOSED;
          } else {
            return basicCheck ||
              vm.currentStatus < AppConstants.STATUS.MANUAL_AWARD_COMPLETED;
          }
        }
      }
    }

    /** Format number according to specifications */
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }
  }
})();
