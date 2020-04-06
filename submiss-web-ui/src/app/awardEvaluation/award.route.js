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
 * @name award.route.js
 * @description award routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.award")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) {

    /**
     * Constants
     */
    var PROJECTVIEW = 'project.view';
    var PROJECTSEARCH = 'project.search';

    $stateProvider.state("award", {
      abstract: true,
      url: "/award",
      template: "<ui-view/>",
      data: {
        isPublic: false
      }
    });

    $stateProvider.state("award.view", {
      url: "/view/:id",
      controller: "AwardViewController",
      controllerAs: "awardViewCtrl",
      templateUrl: "app/awardEvaluation/awardView.html",
      params: {
        id: null,
        displayedCriterionId: null,
        proceedToClose: null
      },
      data: {
        isPublic: false
      },
      ncyBreadcrumb: {
        label: '{{awardViewCtrl.data.submission.workType.value1}} {{awardViewCtrl.data.submission.workType.value2}}',
        parent: function ($rootScope) {
          var returnVal;
          if ($rootScope.$previousState === 'projectSubmissionsView' ||
            $rootScope.$previousState ===
            'projectSubmissionsView.from.Offers' ||
            $rootScope.$previousState === PROJECTVIEW) {
            returnVal = PROJECTVIEW;
          } else if ($rootScope.$previousState === PROJECTSEARCH ||
            !$rootScope.$previousState) {
            returnVal = PROJECTSEARCH;
          }
          return returnVal;
        }
      }
    });

    $stateProvider.state("award.criteriaAssess", {
      url: "/criteriaAssess/:id",
      controller: "AwardCriteriaAssessController",
      controllerAs: "awardCriteriaAssessCtrl",
      templateUrl: "app/awardEvaluation/awardCriteriaAssess.html",
      params: {
        id: null
      },
      data: {
        isPublic: false
      },
      ncyBreadcrumb: {
        label: '{{awardCriteriaAssessCtrl.data.submission.workType.value1}} {{awardCriteriaAssessCtrl.data.submission.workType.value2}}',
        parent: function ($rootScope) {
          var returnVal;
          if ($rootScope.$previousState === 'projectSubmissionsView' ||
            $rootScope.$previousState ===
            'projectSubmissionsView.from.Offers' ||
            $rootScope.$previousState === PROJECTVIEW) {
            returnVal = PROJECTVIEW;
          } else if ($rootScope.$previousState === PROJECTSEARCH ||
            !$rootScope.$previousState) {
            returnVal = PROJECTSEARCH;
          }
          return returnVal;
        }
      }
    });
  }
})();
