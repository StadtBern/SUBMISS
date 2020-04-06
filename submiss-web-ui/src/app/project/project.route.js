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
 * @name project.route.js
 * @description project routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars

    // Do not remove this variable. Its value is used for the breadcrumb label.
    var projectName = '';

    $stateProvider.state("project", {
      abstract: true,
      url: "/project",
      template: "<ui-view/>",
      data: {
        isPublic: false
      },
    });

    $stateProvider.state("project.view", {
      url: "/view/:id",
      controller: "ProjectViewController",
      controllerAs: "projectViewCtr",
      templateUrl: "app/project/view/projectView.html",
      params: {
        id: null,
        name: name
      },
      onExit: function ($rootScope, $stateParams) {
        if ($rootScope.$previousState === 'project.search' || $rootScope.$previousState === undefined) {
          $rootScope.selectedProjectId = $stateParams.id;
          $rootScope.selectedProjectName = $stateParams.name;
        }
        if ($rootScope.$previousState === 'projectSubmissionsView.from.Offers' || $rootScope.$previousState === undefined) {
          $rootScope.$previousState = 'project.view.from.Offers';
        } else {
          $rootScope.$previousState = 'project.view';
        }
      },
      ncyBreadcrumb: {
        parent: function ($rootScope) {
          // The assignment to the variable projectName is not useless (as stated by sonarlint), as its value is used for the breadcrumb label.
          projectName = $rootScope.projectName;
          if ($rootScope.$previousState === 'project.search' || $rootScope.$previousState === 'projectSubmissionsView' ||
            !$rootScope.$previousState || $rootScope.$previousState === 'projectSubmissionsView.from.Offers' ||
            $rootScope.$previousState === 'project.view' || $rootScope.$previousState === 'project.create') {
            return 'project.search';
          }
        },
        label: '{{projectName}}'
      }
    });

    $stateProvider.state("project.create", {
      url: "/create",
      controller: "ProjectCreateController",
      controllerAs: "projectCreateCtr",
      templateUrl: "app/project/create/projectCreate.html",
      params: {
        pmExternal: null
      },
      onExit: function ($rootScope, $stateParams) {
        $rootScope.$previousState = 'project.create';

      },
      ncyBreadcrumb: {
        label: 'Projekt eröffnen',
        parent: 'start'
      }
    });

    $stateProvider.state("start", {
      ncyBreadcrumb: {
        label: 'Startseite'
      }
    });

    $stateProvider.state("project.search", {
      url: "/search",
      controller: "ProjectSearchController",
      controllerAs: "projectSearchCtr",
      templateUrl: "app/project/search/projectSearch.html",
      onExit: function ($rootScope, $stateParams) {
        $rootScope.$previousState = 'project.search';

      },
      ncyBreadcrumb: {
        label: 'Projekt suchen',
        parent: 'start'
      },
    });

  }
})();
