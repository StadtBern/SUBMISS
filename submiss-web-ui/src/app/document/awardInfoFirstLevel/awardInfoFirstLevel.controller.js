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
    .module("submiss.document").controller("AwardInfoFirstLevelController",
      AwardInfoFirstLevelController);

  /** @ngInject */
  function AwardInfoFirstLevelController($scope, $rootScope, $locale,
    $state, $stateParams, NgTableParams, $filter,
    $uibModal, AppService, AppConstants,
    StammdatenService,
    $q, QFormJSRValidation,
    SubmissionService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var getAwardInfoFirstLevelFunction, getExclusionReasonsFunction;
    var secAwardInfoFirstLevelEdit = false;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.awardInfoFirstLevel = null;
    vm.availableDateOpened = false;
    vm.displayedOffer = {};
    vm.currentStatus;
    vm.invalidDate = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.openAvailableDate = openAvailableDate;
    vm.openOfferAccordion = openOfferAccordion;
    vm.save = save;
    vm.resetPage = resetPage;
    vm.isawardInfoFirstLevelDisabled = isawardInfoFirstLevelDisabled;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getAwardInfoFirstLevelFunction = getAwardInfoFirstLevel($stateParams.submissionId);
      getExclusionReasonsFunction = getExclusionReasons();
      AppService.setPaneShown(true);
      $q.all([getAwardInfoFirstLevelFunction, getExclusionReasonsFunction]).then(function (resolves) {
        AppService.setPaneShown(false);
        assignExclusionReasons();
      });
      readStatusOfSubmission($stateParams.submissionId);
      getSubmissionStatuses($stateParams.submissionId);
    }

    // get the award info
    function getAwardInfoFirstLevel(id) {
      return SubmissionService.getAwardInfoFirstLevel(id).success(function (data) {
        vm.awardInfoFirstLevel = data;
        secAwardInfoFirstLevelEdit = AppService.isOperationPermitted(AppConstants.OPERATION.AWARD_INFO_FIRST_LEVEL_EDIT,
          vm.awardInfoFirstLevel.submission.process);
        // case create, initialise
        if (vm.awardInfoFirstLevel.id === null) {
          vm.awardInfoFirstLevel.availableDate = new Date();
          vm.awardInfoFirstLevel.objectNameRead = true;
          vm.awardInfoFirstLevel.projectNameRead = true;
          vm.awardInfoFirstLevel.workingClassRead = true;
          vm.awardInfoFirstLevel.descriptionRead = true;
        }
      }).error(function (response, status) {});
    }

    /** Load the master list value data for the exclusion reason */
    function getExclusionReasons() {
      return StammdatenService.getMasterListValueHistoryDataBySubmission($stateParams.submissionId, "ExclusionCriterion").success(
        function (data) {
          let activeExlusionReasons = data.filter(function(data) {
            return data.active == true; });
          vm.exclusionReasons = $filter('orderBy')(activeExlusionReasons, 'value1');
        }).error(function (response, status) {

      });
    }

    /** Inform the exclusion reasons checkboxes about the data */
    function assignExclusionReasons() {
      vm.awardedNumber = 0;
      for (var io = 0; io < vm.awardInfoFirstLevel.offers.length; io++) {
        // reopen previously opened accordion
        if ($stateParams.displayedOfferId === vm.awardInfoFirstLevel.offers[io].id) {
          vm.awardInfoFirstLevel.offers[io].show = true;
          vm.displayedOffer = vm.awardInfoFirstLevel.offers[io];
        }
        // create a new field stating whether each exclusion reason is selected
        vm.awardInfoFirstLevel.offers[io].exclusionReasonCheckboxes = [];
        for (var i = 0; i < vm.exclusionReasons.length; i++) {
          var exclusionReason = {
            id: vm.exclusionReasons[i].masterListValueId.id,
            value: vm.exclusionReasons[i].value1 + " " + vm.exclusionReasons[i].value2,
            selected: false,
            disabled: false
          };
          // special conditions
          if ((vm.exclusionReasons[i].value1 === AppConstants.EXCLUSION_REASON_B &&
              (vm.awardInfoFirstLevel.offers[io].existsExclusionReasons ||
                !vm.awardInfoFirstLevel.offers[io].formalExaminationFulfilled)) ||
            (vm.exclusionReasons[i].value1 === AppConstants.EXCLUSION_REASON_C &&
              (vm.awardInfoFirstLevel.offers[io].mustCriteriaFulfilled !== null && !vm.awardInfoFirstLevel.offers[io].mustCriteriaFulfilled))) {
            exclusionReason.selected = true;
            exclusionReason.disabled = true;
          } else {
            for (var j = 0; j < vm.awardInfoFirstLevel.offers[io].exclusionReasonsFirstLevel.length; j++) {
              if (exclusionReason.id === vm.awardInfoFirstLevel.offers[io].exclusionReasonsFirstLevel[j].id) {
                exclusionReason.selected = true;
                break;
              }
            }
          }
          vm.awardInfoFirstLevel.offers[io].exclusionReasonCheckboxes.push(exclusionReason);
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

    /** Function to get the submission status history */
    function getSubmissionStatuses(submissionId) {
      AppService.getSubmissionStatuses(submissionId).success(function (data) {
        vm.statusHistory = data;
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

    /** Save the award info first level form */
    function save() {
      if (angular.isUndefined(vm.awardInfoFirstLevel.availableDate)) {
        vm.invalidDate = true;
      } else {
        vm.invalidDate = false;
        for (var io = 0; io < vm.awardInfoFirstLevel.offers.length; io++) {
          // send the data about the exclusion reasons back in the accepted format
          vm.awardInfoFirstLevel.offers[io].exclusionReasonsFirstLevel = [];
          for (var i = 0; i < vm.awardInfoFirstLevel.offers[io].exclusionReasonCheckboxes.length; i++) {
            if (vm.awardInfoFirstLevel.offers[io].exclusionReasonCheckboxes[i].selected) {
              var exclusionReason = {
                id: vm.awardInfoFirstLevel.offers[io].exclusionReasonCheckboxes[i].id
              };
              vm.awardInfoFirstLevel.offers[io].exclusionReasonsFirstLevel.push(exclusionReason);
            }
          }
        }
        if (angular.isUndefined(vm.awardInfoFirstLevel.id) || vm.awardInfoFirstLevel.id === null) {
          SubmissionService.createAwardInfoFirstLevel(vm.awardInfoFirstLevel)
            .success(function (data) {
              defaultReload();
            }).error(function (response, status) {
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                QFormJSRValidation.markErrors($scope,
                  $scope.awardInfoFirstLevelCtrl.awardInfoFirstLevelHtmlForm, response);
              }
            });
        } else {
          SubmissionService.updateAwardInfoFirstLevel(vm.awardInfoFirstLevel)
            .success(function (data) {
              defaultReload();
            }).error(function (response, status) {
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                QFormJSRValidation.markErrors($scope,
                  $scope.awardInfoFirstLevelCtrl.awardInfoFirstLevelHtmlForm, response);
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
            '<button type="button" class="btn btn-primary" ng-click="awardInfoFirstLevelCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'awardInfoFirstLevelCtrl',
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

    /** Function that checks if the form needs to be disabled */
    function isawardInfoFirstLevelDisabled() {
      return vm.awardInfoFirstLevel === null ||
        !secAwardInfoFirstLevelEdit ||
        vm.currentStatus === AppConstants.STATUS.PROCEDURE_CANCELED ||
        vm.currentStatus === AppConstants.STATUS.PROCEDURE_COMPLETED ||
        !isFormEnabledByStatus();
    }

    /** Function to check if form is enabled by checking status */
    function isFormEnabledByStatus() {
      var defaultEnabled = vm.currentStatus >= AppConstants.STATUS.SUITABILITY_AUDIT_COMPLETED_S &&
        vm.currentStatus < AppConstants.STATUS.OFFER_OPENING_STARTED;
      return defaultEnabled || ((vm.statusHistory[0] === AppConstants.STATUS.OFFER_OPENING_STARTED &&
        vm.statusHistory[1] === AppConstants.STATUS.FORMAL_EXAMINATION_SUITABILITY_AUDIT_STARTED) && !defaultEnabled);
    }
  }
})();
