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
 * @name companyView.controller.js
 * @description CompanyViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company").controller("CompanyViewController",
      CompanyViewController);

  /** @ngInject */
  function CompanyViewController($rootScope, $scope, $state, $stateParams,
    CompanyService, $uibModal, QFormJSRValidation, AppService, AppConstants, TasksService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.name = $stateParams.name;
    vm.secMainTenantBemerkungFabeView = false;
    vm.secCompanyProofsView = false;
    vm.secAdminNotesView = false;
    vm.secNotesView = false;
    vm.secCompanyUpdate = false;
    vm.secCompanyDelete = false;
    vm.secProofVerificationRequest = false;
    vm.secCompanyOffersView = false;
    vm.secMainTenantBeschaffungswesenView = false;
    vm.taskCreateForm = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.readCompany = readCompany;
    vm.deleteModal = deleteModal;
    vm.deleteCompany = deleteCompany;
    vm.editCompany = editCompany;
    vm.apprenticeFactor = apprenticeFactor;
    vm.numberOfColleagues = numberOfColleagues;
    vm.navigateToCompany = navigateToCompany;
    vm.requestProofs = requestProofs;
    vm.sendMailModal = sendMailModal;
    vm.fiftyPlusFactor = fiftyPlusFactor;
    vm.getCompanyTask = getCompanyTask;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      CompanyService.loadCompany()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.readCompany($stateParams.id);
            vm.apprenticeFactor();
            vm.numberOfColleagues();
            vm.secProofVerificationRequest = AppService.isOperationPermitted(AppConstants.OPERATION.PROOF_VERIFICATION_REQUEST, null);
            vm.secMainTenantBemerkungFabeView = AppService.isOperationPermitted(AppConstants.OPERATION.MAIN_TENANT_BEMERKUNG_FABE_VIEW, null);
            vm.secMainTenantBeschaffungswesenView = AppService.isOperationPermitted(AppConstants.OPERATION.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW, null);
            vm.secSentEmail = AppService.isOperationPermitted(AppConstants.OPERATION.SENT_EMAIL, null);
            vm.getCompanyTask($stateParams.id);
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
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams, from, fromParams) {
      //save the previous state in a rootScope variable so that it's accessible from everywhere
      $rootScope.previousState = from;
    });

    function readCompany(id) {
      CompanyService.readCompany(id).success(function (data) {
        vm.data.company = data;
        vm.secCompanyProofsView = AppService.isOperationPermitted(AppConstants.OPERATION.COMPANY_PROOFS_VIEW, null);
        vm.secAdminNotesView = AppService.isOperationPermitted(AppConstants.OPERATION.ADMIN_NOTES_VIEW, null);
        vm.secNotesView = AppService.isOperationPermitted(AppConstants.OPERATION.NOTES_VIEW, null);
        vm.secCompanyUpdate = AppService.isOperationPermitted(AppConstants.OPERATION.COMPANY_UPDATE, null);
        vm.secCompanyDelete = AppService.isOperationPermitted(AppConstants.OPERATION.COMPANY_DELETE, null);
        vm.secCompanyOffersView = AppService.isOperationPermitted(AppConstants.OPERATION.COMPANY_OFFERS_VIEW, null);
      }).error(function (response, status) {});
    }

    function getCompanyTask(companyId) {
      TasksService.getCompanyTask(companyId).success(function (data) {
        var name = (data.firstName != null && data.lastName != null) ? data.firstName + ' ' + data.lastName : null;
        vm.data.companyTask = {
          createdOn: data.createdOn,
          createdBy: name,
          id : data.id
        };
      }).error(function (response, status) {});
    }

    /**Create a function to show a modal for deleting a Company */
    function deleteModal() {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="companyViewCtr.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Firma löschen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie diese Firma wirklich löschen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="companyViewCtr.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="companyViewCtr.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'companyViewCtr',
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
          deleteCompany();
        } else {
          return false;
        }
        return null;
      });
    }

    /**Create a function to delete the Company */
    function deleteCompany() {
      if (vm.data.company) {
        CompanyService.deleteCompany(vm.data.company.id).success(
          function (data, status) {
            if (status === 403) { // Security checks.
              return;
            }
            $state.go('company.search', {}, {
              reload: true
            });
          }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.companyViewCtr.companyForm, response);
          }
        });
      } else {
        //The system is showing to user the start page according to the user role
      }
    }

    function editCompany() {
      var companyModal = $uibModal.open({
        template: '<div class="modal-header">' +
          '<button type="button" class="close" aria-label="Close" ng-click="companyUpdateCtr.closeWindow(companyUpdateCtr.companyForm.$dirty)">' +
          '<span aria-hidden="true">&times;</span></button>' +
          '<h4 class="modal-title">Firma bearbeiten</h4>' +
          '</div>' +
          '<div class="modal-body">' +
          '<div ng-include src="' +
          '\'' +
          'app/company/update/companyUpdate.html' +
          '\'">' +
          '<div>' +
          '<div>',
        controller: 'CompanyUpdateController',
        controllerAs: 'companyUpdateCtr',
        size: 'lg',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          company: function () {
            return vm.data.company;
          },
          editCompany: function () {
            return true;
          }
        }

      });
      return companyModal.result.then(function () {}, function (response) {
        if (response === 'Speichern') {
          editCompany();
        } else {
          return false;
        }
        return null;
      });
    }

    function numberOfColleagues(numberOfMen, numberOfWomen) {
      var colleagues = parseInt(numberOfMen) * 1 + parseInt(numberOfWomen);
      return colleagues || 0;
    }

    function apprenticeFactor(numberOfMen, numberOfWomen, numberOfTrainees) {
      var colleagues = numberOfColleagues(numberOfMen, numberOfWomen);
      if (numberOfTrainees && colleagues) {
        var apFactor = parseInt(numberOfTrainees) * 1 / (colleagues);
        return apFactor || 0;
      } else {
        return 0;
      }
    }

    function navigateToCompany(companyUrl) {
      window.open('http://' + companyUrl.replace('http://', ''), '_blank')
    }

    function requestProofs() {
      vm.taskCreateForm.taskType = AppConstants.TASK_TYPES[1];
      vm.taskCreateForm.submittentIDs = [];
      vm.taskCreateForm.submittentIDs.push(vm.data.company.id);
      AppService.setPaneShown(true);
      TasksService.createTask(vm.taskCreateForm).success(
          function (data) {
            AppService.setPaneShown(false);
            $state.reload();
          })
        .error(function (response, status) {
          AppService.setPaneShown(false);
        });
    }

    function sendMailModal() {
      AppService.sendMailModal(vm.data.company.id, null);
    }

    /** Function to calculate the 50+ factor */
    function fiftyPlusFactor(fiftyPlusColleagues, numberOfMen, numberOfWomen) {
      var colleagues = numberOfColleagues(numberOfMen, numberOfWomen);
      if (fiftyPlusColleagues && colleagues) {
        return fiftyPlusColleagues / colleagues;
      }
      return 0;
    }
  }
})();
