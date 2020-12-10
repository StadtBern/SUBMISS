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
 * @name companyProofs.controller.js
 * @description CompanyProofsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company").controller("CompanyProofsController",
      CompanyProofsController);

  /** @ngInject */
  function CompanyProofsController($rootScope, $scope, $state, $stateParams,
    $locale,
    CompanyService, QFormJSRValidation, TasksService, $uibModal, NgTableParams,
    $filter, $location, $anchorScroll, AppService, AppConstants, $transitions) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.name = $stateParams.name;
    vm.id = $stateParams.id;
    vm.totalPages = 0;
    vm.proofs = [];
    openProofDate.opened = [];
    vm.companyProofForm = {};
    vm.secCompanyProofsView = false;
    vm.secProofVerificationRequest = false;
    vm.secCompanyOffersView = false;
    vm.invalidDate = false;
    vm.showMessage = false;
    vm.showBoth = false;
    vm.taskCreateForm = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.getProofs = getProofs;
    vm.openProofDate = openProofDate;
    vm.resetPage = resetPage;
    vm.save = save;
    vm.requestProofs = requestProofs;
    vm.getCompanyTask = getCompanyTask;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      CompanyService.loadCompanyProofs()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.getProofs();
            readCompany($stateParams.id);
            vm.secProofVerificationRequest = AppService.isOperationPermitted(
              AppConstants.OPERATION.PROOF_VERIFICATION_REQUEST, null);
            vm.secCompanyOffersView = AppService.isOperationPermitted(
              AppConstants.OPERATION.COMPANY_OFFERS_VIEW, null);
            vm.getCompanyTask($stateParams.id);
          }
        });
    }
    /***********************************************************************
     * $scope destroy.y
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    $rootScope.$on('$stateChangeSuccess',
      function (event, to, toParams, from, fromParams) {
        //save the previous state in a rootScope variable so that it's accessible from everywhere
        $rootScope.previousState = from;
      });

    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result
          .then(function (response) {
            if (response) {
              vm.dirtyFlag = false;
              return true;
            }
            return false;
          });
      }
      return null;
    });

    function readCompany(id) {
      CompanyService.readCompany(id).success(function (data) {
        vm.data.company = data;
        vm.secCompanyProofsView = AppService.isOperationPermitted(
          AppConstants.OPERATION.COMPANY_PROOFS_VIEW, null);
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

    function openProofDate(index) {
      vm.openProofDate.opened[index] = !vm.openProofDate.opened[index];

    }

    // Function that saves proofs for a company
    function save() {
      vm.dirtyFlag = false;
      var today = new Date();
      var newProofs = [];
      vm.proofs.forEach(function (proof) {
        if (angular.isUndefined(proof.proofDate)) {
          vm.invalidDate = true;
          vm.showMessage = true;
          vm.showDateAfterMessage = false;
          vm.showBoth = false;
          $anchorScroll('errorScroll');
          proof.invalid = true;
          vm.invalidDateAfter = false;
          newProofs.push(proof);
        }
        if (proof.proofDate > today) {
          vm.invalidDateAfter = true;
          vm.showMessage = false;
          vm.showBoth = false;
          vm.showDateAfterMessage = true;
          $anchorScroll('errorScroll');
          proof.invalidAfter = true;
          vm.invalidDate = false;
          newProofs.push(proof);
        }
      });
      for (var i = 0; i < newProofs.length - 1; i++) {
        if ((newProofs[i].invalid && newProofs[i + 1].invalidAfter) ||
          (newProofs[i].invalidAfter && newProofs[i + 1].invalid)) {
          vm.showBoth = true;
          vm.invalidBoth = true;
        }
      }
      if (!vm.invalidDate && !vm.invalidDateAfter) {
        CompanyService.updateProofs(vm.proofs)
          .success(function (data, status) {
            if (status === 403) { // Security checks.
              return;
            }
            $state.go('company.proofs', {}, {
              reload: true
            });
          }).error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.companyProofsCtr.companyProofForm, response,
                $anchorScroll());
            }
          });
        vm.showDateAfterMessage = false;
        vm.showMessage = false;
        vm.invalidDate = false;
        vm.invalidDateAfter = false;
      }
      vm.invalidDate = false;
      vm.invalidDateAfter = false;
      vm.invalidBoth = false;
    }

    // Function that reset the fields of the form
    function resetPage() {
      if (!vm.dirtyFlag) {
        $state.go('company.proofs', {}, {
          reload: true
        });
      } else {
        var cancelModal = AppService.cancelModal();
        return cancelModal.result.then(function (response) {
          if (response) {
            $state.reload();
            vm.dirtyFlag = false;
            return true;
          } else {
            return false;
          }
        });
      }
      return null;
    }

    // Create dateOptions to exclude all the future dates after today
    $scope.dateOptions = {
      dateDisabled: disabled,
      formatYear: 'yy',
      maxDate: new Date(),
      startingDay: 7
    };

    // Disable weekend selection
    function disabled(data) {
      var date = data.date,
        mode = data.mode;
      return mode === 'day' && (date.getDay() === 0 && date.getDay() === 6);
    }

    function getProofs() {
      CompanyService.getProofsByCompanyId($stateParams.id)
        .then(function (results) {
          vm.secCompanyProofsView = AppService.isOperationPermitted(
            AppConstants.OPERATION.COMPANY_PROOFS_VIEW, null);
          vm.proofs = results.data;
          vm.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            sorting: {
              proofOrder: "asc",
            }
          }, {
            total: vm.proofs.length,
            getData: function ($defer, params) {
              var filteredData = params.filter() ?
                $filter('filter')(vm.proofs, params.filter()) :
                tabledata;
              var orderedData = params.sorting() ?
                $filter('orderBy')(filteredData, params.orderBy()) : filteredData;

              $defer.resolve(
                orderedData.slice((params.page() - 1) * params.count(),
                  params.page() * params.count()));
            }
          });
          return vm.tableParams;
        });
    }

    function requestProofs() {
      vm.taskCreateForm.taskType = AppConstants.TASK_TYPES[1];
      vm.taskCreateForm.submittentIDs = [];
      vm.taskCreateForm.submittentIDs.push(vm.data.company.id);
      AppService.setPaneShown(true);
      TasksService.createTask(vm.taskCreateForm)
        .success(function (data) {
          AppService.setPaneShown(false);
          $state.reload();
        }).error(function (response, status) {
        AppService.setPaneShown(false);
      });
    }
  }
})();
