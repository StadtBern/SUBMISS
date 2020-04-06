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
 * @name examination.route.js
 * @description examination routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.examination")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) {

    var FROM_OFFERS = 'projectSubmissionsView.from.Offers';
    var PROJECTVIEW = 'project.view';
    var PROJECTSEARCH = 'project.search';

    $stateProvider.state("examination", {
        abstract: true,
        url: "/examination",
        template: "<ui-view/>",
        data: {
          isPublic: false
        },
      })
      .state("examination.view", {
        url: "/view/:id",
        controller: "ExaminationViewController",
        controllerAs: "examinationViewCtrl",
        templateUrl: "app/examination/examination.html",
        params: {
          id: null,
          displayedMustCriterionId: null,
          displayedEvaluatedCriterionId: null,
          proceedToClose: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{examinationViewCtrl.data.submission.workType.value1}} {{examinationViewCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            if ($rootScope.$previousState === 'projectSubmissionsView' || $rootScope.$previousState === FROM_OFFERS ||
              $rootScope.$previousState === PROJECTVIEW) {
              return PROJECTVIEW;
            } else if ($rootScope.$previousState === PROJECTSEARCH || !$rootScope.$previousState) {
              return PROJECTSEARCH;
            }
          }
        }
      })
      .state("examination.formal", {
        url: "/formal/:id",
        controller: "FormalAuditController",
        controllerAs: "formalAuditCtrl",
        templateUrl: "app/examination/formalAudit.html",
        params: {
          id: null,
          proceedToClose: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{formalAuditCtrl.data.submission.workType.value1}} {{formalAuditCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            if ($rootScope.$previousState === 'projectSubmissionsView' || $rootScope.$previousState === FROM_OFFERS ||
              $rootScope.$previousState === PROJECTVIEW) {
              return PROJECTVIEW;
            } else if ($rootScope.$previousState === PROJECTSEARCH || !$rootScope.$previousState) {
              return PROJECTSEARCH;
            }
          }
        }
      })
      .state("examination.suitability", {
        url: "/suitability/:id",
        controller: "SuitabilityAuditController",
        controllerAs: "suitabilityAuditCtrl",
        templateUrl: "app/examination/suitabilityAudit.html",
        params: {
          id: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{suitabilityAuditCtrl.data.submission.workType.value1}} {{suitabilityAuditCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            if ($rootScope.$previousState === 'projectSubmissionsView' || $rootScope.$previousState === FROM_OFFERS ||
              $rootScope.$previousState === PROJECTVIEW) {
              return PROJECTVIEW;
            } else if ($rootScope.$previousState === PROJECTSEARCH || !$rootScope.$previousState) {
              return PROJECTSEARCH;
            }
          }
        }
      });
  }
})();
