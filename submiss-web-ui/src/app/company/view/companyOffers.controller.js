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
 * @name companyOffers.controller.js
 * @description CompanyOffersController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company").controller("CompanyOffersController",
      CompanyOffersController);

  /** @ngInject */
  function CompanyOffersController($rootScope, $scope, $state, $stateParams,
    CompanyService, TasksService, $uibModal, NgTableParams, $filter, $location,
    $anchorScroll, AppService, AppConstants) {
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
    vm.offer = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.getOffers = getOffers;
    vm.navigateToSubmission = navigateToSubmission;
    vm.navigateToProject = navigateToProject;
    vm.customRoundNumber = customRoundNumber;
    vm.requestProofs = requestProofs;
    vm.secCompanyProofsView = false;
    vm.secProofVerificationRequest = false;
    vm.secTenderView = false;
    vm.secCompanyOffersView = false;
    vm.taskCreateForm = {};
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.getOffers();
      readCompany($stateParams.id);
      vm.secProofVerificationRequest = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROOF_VERIFICATION_REQUEST, null);
      vm.secCompanyProofsView = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_PROOFS_VIEW, null);
      vm.secTenderView = AppService.isOperationPermitted(
        AppConstants.OPERATION.TENDER_VIEW, null);
      vm.secCompanyOffersView = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_OFFERS_VIEW, null);
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

    function readCompany(id) {
      CompanyService.readCompany(id).success(function (data) {
        vm.data.company = data;
      }).error(function (response, status) {

      });
    }

    function navigateToSubmission(submissionId) {
      var url = $state.href('submissionView', {
        id: submissionId
      });
      window.open(url, '_blank');
    }

    function navigateToProject(projectId, projectName) {
      var url = $state.href('project.view', {
        id: projectId,
        name: projectName
      });
      window.open(url, '_blank');
    }

    function customRoundNumber(num) {
      return AppService.customRoundNumber(num);
    }

    function getOffers() {
      CompanyService.getOfferByCompanyId($stateParams.id)
        .then(function (results) {
          vm.offers = results.data;
          vm.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            sorting: {
              objectName: "asc"
            }
          }, {
            total: vm.offers.length,
            getData: function ($defer, params) {
              var orderedData = params.sorting() ?
                $filter('orderBy')(vm.offers, params.orderBy()) : vm.offers;
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

      TasksService.createTask(vm.taskCreateForm)
        .success(function (data) {
          $state.reload();
        }).error(function (response, status) {});
    }
  }
})();
