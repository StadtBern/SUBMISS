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
 * @name subcontractorDetails.controller.js
 * @description SubcontractorDetailsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer").controller("SubcontractorDetailsController",
      SubcontractorDetailsController);

  /** @ngInject */
  function SubcontractorDetailsController($rootScope, $scope, $location, $state, $stateParams,
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
    vm.secAddSubcontractor = false;
    vm.secDeleteSubcontractor = false;
    vm.group = AppConstants.GROUP;
    vm.status = AppConstants.STATUS;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addCompany = addCompany;
    vm.deleteSubcontractorModal = deleteSubcontractorModal;
    vm.deleteSubcontractor = deleteSubcontractor;
    vm.implementSecurity = implementSecurity;
    vm.disabledForm = disabledForm;
    vm.buttonDisabled = buttonDisabled;
    vm.divRowStyle = divRowStyle;
    vm.fieldsetStyle = fieldsetStyle;
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

    /**Create a function to show a modal for adding a Subcontractor */
    function addCompany() {
      var addCompany = AppService.addCompany(false, null);
      return addCompany.result.then(function (response) {
        vm.subcontractorList = [];
        vm.subcontractorList = response;
        if (!angular.isUndefined(vm.subcontractorList) && vm.subcontractorList.length > 0) {
          vm.submittent.subcontractors = vm.subcontractorList;
          OfferService.addSubcontractorToSubmittent(
            vm.submittent).success(function (data) {
            if ($state.current.name === "offerListView.subcontractorDetails") {
              customOfferReload();
            } else {
              customApplicantReload();
            }
          });
        }
      });
    }

    /**Create a function to show a modal for deleting a Subcontractor */
    function deleteSubcontractorModal(submittentId, subcontractorId) {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="subcontractorDetailsCtrl.closeConfirmationWindow(\'nein\');">' +
            '<span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Subunternehmen entfernen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie das Subunternehmen wirklich entfernen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="subcontractorDetailsCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="subcontractorDetailsCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'subcontractorDetailsCtrl',
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
          deleteSubcontractor(submittentId, subcontractorId); //Delete Subcontractor
        } else {
          return false;
        }
        return null;
      });
    }

    // A function that returns true or false if the form on html must be or not disabled
    function disabledForm() {
      return (vm.currentStatus >= vm.status.AWARD_EVALUATION_CLOSED);
    }

    /**Create a function to delete a Subcontractor */
    function deleteSubcontractor(submittentId, subcontractorId) {
      OfferService.deleteSubcontractor(submittentId, subcontractorId)
        .success(function (data) {
          if ($state.current.name === "offerListView.subcontractorDetails") {
            customOfferReload();
          } else {
            customApplicantReload();
          }
        }).error(
          function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.offerListViewCtrl.offerForm,
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
      vm.secAddSubcontractor = AppService.isOperationPermitted(AppConstants.OPERATION.ADD_SUBCONTRACTOR, vm.submittent.submissionId.process);
      vm.secDeleteSubcontractor = AppService.isOperationPermitted(AppConstants.OPERATION.DELETE_SUBCONTRACTOR, vm.submittent.submissionId.process);
    }

    /** Function to determine if the button is disabled */
    function buttonDisabled() {
      return !((AppService.getLoggedUser().userGroup.name === vm.group.ADMIN &&
          (vm.currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED ||
            vm.currentStatus < vm.status.FORMAL_AUDIT_COMPLETED)) ||
        ((AppService.getLoggedUser().userGroup.name === vm.group.PL &&
            vm.submittent.submissionId.process !== AppConstants.PROCESS.OPEN &&
            vm.submittent.submissionId.process !== AppConstants.PROCESS.SELECTIVE) &&
          ((vm.submittent.submissionId.process === AppConstants.PROCESS.INVITATION &&
              (vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK &&
                vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECKED &&
                vm.currentStatus < vm.status.OFFER_OPENING_STARTED)) ||
            ((vm.submittent.submissionId.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
                vm.submittent.submissionId.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
              vm.currentStatus < vm.status.FORMAL_AUDIT_COMPLETED &&
              vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK))));
    }

    function divRowStyle() {
      return AppService.divRowStyle(vm.submittent.subcontractors);
    }

    function fieldsetStyle() {
      return AppService.fieldsetStyle($stateParams.offer.submittent.submissionId.process);
    }

    /** Function to implement custom offer reload */
    function customOfferReload() {
      $state.go('offerListView', {
        displayedOfferId: $stateParams.offer.id,
        offerDetails: false,
        subcontractor: true,
        jointVenture: false,
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
        subcontractor: true,
        jointVenture: false
      }, {
        reload: true
      });
    }
  }
})();
