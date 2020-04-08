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
 * @name applicationDetails.controller.js
 * @description ApplicationDetailsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss").controller("ApplicationDetailsController",
      ApplicationDetailsController);

  /** @ngInject */
  function ApplicationDetailsController($rootScope, $scope, $location, $locale, $state, $stateParams,
    SubmissionService, SelectiveService, QFormJSRValidation, $uibModal,
    $transitions, AppService, AppConstants, $anchorScroll) {
    const
      vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.application = {};
    vm.application.applicationDate = null;
    vm.application.applicationInformation = null;
    vm.submission = null;
    vm.secApplicationEdit = null;
    vm.currentStatus = null;
    vm.dirtyFlag = false;
    vm.undefinedApplicationDate = false;
    vm.nullApplicationDate = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.openApplicationDate = openApplicationDate;
    vm.save = save;
    vm.isFormDisabled = isFormDisabled;
    vm.cancelButton = cancelButton;
    vm.checkApplicationDateValue = checkApplicationDateValue;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getApplicationDetails($stateParams.offer);
      vm.submission = $stateParams.offer.submittent.submissionId;
      implementingSecurity();
      readStatusOfSubmission($stateParams.offer.submittent.submissionId.id);
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams,
      from, fromParams) {
      // save the previous state in a rootScope variable so that it's
      // accessible from everywhere
      $rootScope.previousState = from;
    });

    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result.then(function (response) {
          if (response) {
            AppService.setIsDirty(false);
            vm.dirtyFlag = false;
            return true;
          }
          return false;
        });
      }
      return null;
    });

    /** Implementing the application date modal functionality */
    function openApplicationDate() {
      vm.openApplicationDate.opened = !vm.openApplicationDate.opened;
    }

    /** Function to save the application */
    function save() {
      // Get the submission version.
      vm.application.submissionVersion = vm.submission.version;
      SelectiveService.updateApplication(vm.application)
        .success(function (data) {
          vm.dirtyFlag = false;
          customReload();
        })
        .error(function (response, status) {
          if (status === 400) {
            if (!vm.dirtyFlag) {
              checkApplicationDateValue();
            }
            QFormJSRValidation.markErrors($scope,
              $scope.applicationForm, response);
          }
        });
    }

    /** Get the application details */
    function getApplicationDetails(offer) {
      // Get the application id.
      vm.application.applicationId = offer.id;
      // Get the application version.
      vm.application.applicationVersion = offer.version;
      // Get the application date.
      if (offer.applicationDate) {
        vm.application.applicationDate = offer.applicationDate;
      }
      // Get the application information.
      if (offer.applicationInformation) {
        vm.application.applicationInformation = offer.applicationInformation;
      }
    }

    /** Function to determine if the form is disabled */
    function isFormDisabled() {
      return !vm.submission.firstDeadline || !vm.submission.applicationOpeningDate ||
        !vm.submission.firstLoggedBy || !vm.secApplicationEdit ||
        vm.currentStatus >= AppConstants.STATUS.APPLICATION_OPENING_CLOSED;
    }

    /** Function to check for user rights when performing operations */
    function implementingSecurity() {
      vm.secApplicationEdit =
        AppService.isOperationPermitted(AppConstants.OPERATION.APPLICATION_EDIT, AppConstants.PROCESS.SELECTIVE);
    }

    /** Function to read status of submission */
    function readStatusOfSubmission(submissionId) {
      SubmissionService.getCurrentStatusOfSubmission(submissionId).success(function (data) {
        vm.currentStatus = data;
      });
    }

    /** Function to cancel changes made to the form */
    function cancelButton(dirtyFlag) {
      if (dirtyFlag) {
        var cancelModal = AppService.cancelModal();
        return cancelModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            customReload();
          }
        });
      }
      customReload();
      return null;
    }

    /** Function to check if the application date value is null or undefined (used for error messages) */
    function checkApplicationDateValue() {
      if (angular.isUndefined(vm.application.applicationDate)) {
        vm.undefinedApplicationDate = true;
        vm.nullApplicationDate = false;
      } else if (vm.application.applicationDate == null) {
        vm.undefinedApplicationDate = false;
        vm.nullApplicationDate = true;
      } else {
        vm.undefinedApplicationDate = false;
        vm.nullApplicationDate = false;
        $scope.applicationForm.applicationDate.$invalid = false;
      }
    }

    /** Function to implement custom reload */
    function customReload() {
      if (AppService.getIsDirty()) {
        AppService.setIsDirty(false);
      }
      $state.go('applicants', {
        displayedApplicationId: $stateParams.offer.id,
        applicationDetails: true,
        subcontractor: false,
        jointVenture: false
      }, {
        reload: true
      });
    }
  }
})();
