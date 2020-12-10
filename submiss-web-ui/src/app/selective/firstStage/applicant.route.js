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
 * @ngdoc object
 * @name applicant.route.js
 * @description applicant routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars

    $stateProvider
      .state("applicants", {
        url: "/firstStage/applicants/:id",
        controller: "ApplicantsController",
        controllerAs: "applicantsCtrl",
        templateUrl: "app/selective/firstStage/applicants.html",
        params: {
          id: null,
          displayedApplicationId: null,
          subcontractor: false,
          jointVenture: false,
          applicationDetails: false
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{applicantsCtrl.submission.workType.value1}} {{applicantsCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("applicants.applicationDetails", {
        url: "/firstStage/applicants/applicationDetails",
        controller: "ApplicationDetailsController",
        controllerAs: "applicationDetailsCtrl",
        templateUrl: "app/selective/firstStage/applicationDetails.html",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          applicationDetails: false
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{applicantsCtrl.submission.workType.value1}} {{applicantsCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("applicants.applicantJointVentureDetails", {
        url: "/firstStage/applicants/jointVentureDetails",
        controller: "JointVentureDetailsController",
        controllerAs: "jointVentureDetailsCtrl",
        templateUrl: "app/offer/view/jointVentureDetails.html",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          applicationDetails: false
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{applicantsCtrl.submission.workType.value1}} {{applicantsCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("applicants.applicantSubcontractorDetails", {
        url: "/firstStage/applicants/subcontractorDetails",
        controller: "SubcontractorDetailsController",
        controllerAs: "subcontractorDetailsCtrl",
        templateUrl: "app/offer/view/subcontractorDetails.html",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          applicationDetails: false
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{applicantsCtrl.submission.workType.value1}} {{applicantsCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      });

    /** Function to set breadcrumb parent */
    function setParent(rootScope) {
      if (rootScope.$previousState === 'projectSubmissionsView' || rootScope.$previousState === 'project.view'
        || rootScope.$previousState === 'project.search' || !rootScope.$previousState) {
        return 'project.view';
      } else {
        return 'project.search';
      }
    }
  }
})();
