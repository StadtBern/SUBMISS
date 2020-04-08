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
 * @name jointVentureDetails.controller.js
 * @description JointVentureDetailsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer").controller("JointVentureDetailsController",
      JointVentureDetailsController);

  /** @ngInject */
  function JointVentureDetailsController($rootScope, $scope, $location, $state, $stateParams,
    OfferService, SubmissionService, QFormJSRValidation, $uibModal, NgTableParams,
    $filter, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.offers
    });
    vm.submittent = {};
    vm.secAddJointVenture = false;
    vm.secDeleteJointVenture = false;
    vm.group = AppConstants.GROUP;
    vm.status = AppConstants.STATUS;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addCompany = addCompany;
    vm.deleteJointVentureModal = deleteJointVentureModal;
    vm.deleteJointVenture = deleteJointVenture;
    vm.implementSecurity = implementSecurity;
    vm.disabledForm = disabledForm;
    vm.divRowStyle = divRowStyle;
    vm.fieldsetStyle = fieldsetStyle;
    vm.deleteJointVentureDisabled = deleteJointVentureDisabled;
    vm.addJointVentureDisabled = addJointVentureDisabled;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      AppService.setIsDirty(false);
      readStatusOfSubmission($stateParams.offer.submittent.submissionId.id);
      vm.submittent = $stateParams.offer.submittent;
      implementSecurity();
      vm.disabledForm();
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

    /**Create a function to show a modal for adding a JointVenture */
    function addCompany() {
      var addCompany = AppService.addCompany(false, null);
      return addCompany.result.then(function (response) {
        vm.jointVentureList = [];
        vm.jointVentureList = response;
        if (!angular.isUndefined(vm.jointVentureList) && vm.jointVentureList.length > 0) {
          vm.submittent.jointVentures = vm.jointVentureList;
          OfferService.addJointVentureToSubmittent(vm.submittent)
            .success(function (data) {
              if ($state.current.name === "offerListView.jointVentureDetails") {
                customOfferReload();
              } else {
                customApplicantReload();
              }
            });
        }
      });

    }

    /** A function that returns true or false if the form must be or not disabled */
    function disabledForm() {
      return (($stateParams.offer.submittent.submissionId.process !== AppConstants.PROCESS.SELECTIVE ||
            ($stateParams.offer.submittent.submissionId.process === AppConstants.PROCESS.SELECTIVE &&
              !$stateParams.offer.submittent.isApplicant)) &&
          vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED) ||
        ($stateParams.offer.submittent.submissionId.process === AppConstants.PROCESS.SELECTIVE &&
          vm.currentStatus >= vm.status.APPLICATION_OPENING_CLOSED &&
          $stateParams.offer.submittent.isApplicant);
    }

    /**Create a function to show a modal for deleting a JointVenture */
    function deleteJointVentureModal(submittentId, jointVentureId) {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="jointVentureDetailsCtrl.closeConfirmationWindow(\'nein\');">' +
            '<span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">ARGE entfernen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie die ARGE wirklich entfernen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="jointVentureDetailsCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="jointVentureDetailsCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'jointVentureDetailsCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeConfirmationWindow = function (reason) {
              confirmationWindowInstance.dismiss(reason);
            };
          }
        });

      return confirmationWindowInstance.result.then(function () {
        // Modal Success Handler
      }, function (response) { // Modal Dismiss Handler
        if (response === 'ja') {
          deleteJointVenture(submittentId, jointVentureId); //Delete JointVenture
        } else {
          return false;
        }
      });
    }
    /**Create a function to delete a JointVenture */
    function deleteJointVenture(submittentId, jointVentureId) {
      OfferService.deleteJointVenture(submittentId, jointVentureId)
        .success(function (data) {
          if ($state.current.name === "offerListView.jointVentureDetails") {
            customOfferReload();
          } else {
            customApplicantReload();
          }
        }).error(
          function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.jointVentureDetailsCtrl.jointVentureForm,
                response);
            }
          });
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {

        });
    }

    /** Function to implement security */
    function implementSecurity() {
      vm.secAddJointVenture = AppService.isOperationPermitted(AppConstants.OPERATION.ADD_JOINT_VENTURE, vm.submittent.submissionId.process);
      vm.secDeleteJointVenture = AppService.isOperationPermitted(AppConstants.OPERATION.DELETE_JOINT_VENTURE, vm.submittent.submissionId.process);
    }

    /** Function to disable joint venture deletion */
    function deleteJointVentureDisabled() {
      return (!vm.secDeleteJointVenture ||
        !((AppService.getLoggedUser().userGroup.name === vm.group.ADMIN &&
            vm.currentStatus < vm.status.OFFER_OPENING_CLOSED) ||
          ((AppService.getLoggedUser().userGroup.name === vm.group.PL &&
              vm.submittent.submissionId.process !== AppConstants.PROCESS.OPEN &&
              vm.submittent.submissionId.process !== AppConstants.PROCESS.SELECTIVE) &&
            (((vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK &&
                  vm.currentStatus < vm.status.SUBMITTENTLIST_CHECKED) ||
                (vm.submittent.submissionId.process === AppConstants.PROCESS.INVITATION &&
                  (vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK &&
                    vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECKED &&
                    vm.currentStatus < vm.status.OFFER_OPENING_STARTED))) ||
              ((vm.submittent.submissionId.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
                  vm.submittent.submissionId.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
                (vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
                  vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK))))));
    }

    /** Function to disable joint venture addition */
    function addJointVentureDisabled() {
      return (!vm.secAddJointVenture ||
        !((AppService.getLoggedUser().userGroup.name === vm.group.ADMIN &&
            vm.currentStatus < vm.status.OFFER_OPENING_CLOSED) ||
          ((AppService.getLoggedUser().userGroup.name === vm.group.PL &&
              vm.submittent.submissionId.process !== AppConstants.PROCESS.OPEN &&
              vm.submittent.submissionId.process !== AppConstants.PROCESS.SELECTIVE) &&
            ((vm.submittent.submissionId.process === AppConstants.PROCESS.INVITATION &&
                (vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK &&
                  vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECKED &&
                  vm.currentStatus < vm.status.OFFER_OPENING_STARTED)) ||
              ((vm.submittent.submissionId.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
                  vm.submittent.submissionId.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
                vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
                vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK)))));
    }

    function divRowStyle() {
      return AppService.divRowStyle(vm.submittent.jointVentures);
    }

    function fieldsetStyle() {
      return AppService.fieldsetStyle($stateParams.offer.submittent.submissionId.process);
    }

    /** Function to implement custom offer reload */
    function customOfferReload() {
      $state.go('offerListView', {
        displayedOfferId: $stateParams.offer.id,
        offerDetails: false,
        subcontractor: false,
        jointVenture: true,
        operatingCost: false,
        ancilliaryCost: false
      }, {
        reload: true
      });
    }

    /** Function to implement custom applicant reload */
    function customApplicantReload() {
      $state.go('applicants', {
        displayedApplicationId: $stateParams.offer.id,
        applicationDetails: false,
        subcontractor: false,
        jointVenture: true
      }, {
        reload: true
      });
    }
  }
})();
