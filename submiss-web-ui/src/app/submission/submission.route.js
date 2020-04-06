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
 * @name submission.route.js
 * @description submission routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.submission")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars

    // Do not remove this variable. Its value is used for the breadcrumb label.
    var projectName = '';

    $stateProvider.state("submissionCreate", {
      controller: "SubmissionCreateController",
      controllerAs: "submissionCreateCtr",
      templateUrl: "app/submission/create/subsmissionCreate.html",
      data: {
        isPublic: true
      },
      ncyBreadcrumb: {
        label: 'Firma suchen',
        parent: 'start'
      }
    });

    $stateProvider.state("projectSubmissionsView", {
      url: "/tenders/:id",
      controller: "SubmissionsViewController",
      controllerAs: "submissionsViewCtr",
      templateUrl: "app/submission/view/subsmissionsView.html",
      params: {
        id: null,
        name: name
      },
      data: {
        isPublic: false
      },
      onExit: function ($rootScope, $stateParams) {
        if ($rootScope.$previousState === 'project.view') {
          $rootScope.$previousState = 'projectSubmissionsView';
        } else if ($rootScope.$previousState === 'project.view.from.Offers') {
          $rootScope.$previousState = 'projectSubmissionsView.from.Offers';
        }
        if ($stateParams.id) {
          $rootScope.selectedProjectId = $stateParams.id;
        }
      },
      ncyBreadcrumb: {
        parent: function ($rootScope) {
          // The assignment to the variable projectName is not useless (as stated by sonarlint), as its value is used for the breadcrumb label.
          projectName = $rootScope.projectName;
          if ($rootScope.$previousState === 'project.view' || $rootScope.$previousState === 'projectSubmissionsView' ||
            !$rootScope.$previousState || $rootScope.$previousState === 'project.view.from.Offers' ||
            $rootScope.$previousState === 'project.search') {
            return 'project.search';
          }
        },
        label: '{{projectName}}'
      }
    });

    $stateProvider.state("submissionView", {
      url: "/tender/:id",
      controller: "SubmissionViewController",
      controllerAs: "submissionViewCtr",
      templateUrl: "app/submission/view/submissionView.html",
      params: {
        id: null
      },
      data: {
        isPublic: false
      },

      ncyBreadcrumb: {
        label: '{{submissionViewCtr.data.submission.workType.value1}} {{submissionViewCtr.data.submission.workType.value2}}',
        parent: function ($rootScope) {
          if ($rootScope.$previousState === 'projectSubmissionsView' || $rootScope.$previousState === 'projectSubmissionsView.from.Offers' ||
            $rootScope.$previousState === 'project.view') {
            return 'project.view';
          } else if ($rootScope.$previousState === 'project.search' || !$rootScope.$previousState) {
            return 'project.search';
          }
        }
      }
    });
  }
})();
