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
 * @name addExcludedSubmittent.controller.js
 * @description AddExcludedSubmittentController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document").controller("AddExcludedSubmittentController",
      AddExcludedSubmittentController);

  /** @ngInject */
  function AddExcludedSubmittentController($scope, $rootScope, $uibModalInstance,
    $state, $stateParams, AppConstants, AppService, SubmissionService, LegalHearingService, $filter) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.submittent = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addSubmittent = addSubmittent;
    vm.showJointVenturesSubcontractors = showJointVenturesSubcontractors;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readSubmittentsBySubmission($stateParams.submissionId);
    }

    function readSubmittentsBySubmission(id) {
      SubmissionService.readSubmittentsBySubmission(id).success(function (data) {
        vm.submittents = data;
      });
    }

    function showJointVenturesSubcontractors(offer) {
      if (offer.companyName === null) {
        vm.noSubmittent = true;
        return null;
      } else {
        vm.noSubmittent = false;
        // text is going to contain the Submittent name,
        // Joint Venture names and Subcontractor names
        var text = offer.companyId.companyName;
        /** Show Joint Ventures */
        if (offer.jointVentures.length > 0) {
          text = text + " (ARGE: " + alphabeticalOrderText(offer.jointVentures);
        }
        /** Show Subcontractors */
        if (offer.subcontractors.length > 0) {
          if (offer.jointVentures.length > 0) {
            // Check if the text already contains joint Ventures
            text = text + ", Sub. U: ";
          } else {
            text = text + " (Sub. U: ";
          }
          text = text + alphabeticalOrderText(offer.subcontractors);
        }

        function alphabeticalOrderText(companies) {
          var tempText = "";
          companies = $filter('orderBy')(companies, 'companyName');
          // Ordered Companies by Company Name
          for (var i = 0; i < companies.length; i++) {
            tempText = tempText + companies[i].companyName;
            if (i < companies.length - 1) {
              tempText = tempText + " / ";
            }
          }
          return tempText;
        }

        if (offer.jointVentures.length > 0 || offer.subcontractors.length > 0) {
          text = text + ")";
        }
        return text;
      }
    }

    function addSubmittent() {
      AppService.setPaneShown(true);
      LegalHearingService.addExcludedSubmittent(vm.submittent.id).success(function (data) {
        AppService.setPaneShown(false);
        $uibModalInstance.close();
        $state.reload();
      });
    }
  }
})();
