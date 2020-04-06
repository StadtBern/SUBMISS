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
 * @name tasks.route.js
 * @description tasks routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.tasks").config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars

    $stateProvider
      .state(
        "tasksView", {
          url: "/tasks/view",
          controller: "TasksViewController",
          controllerAs: "tasksViewCtrl",
          templateUrl: "app/tasks/view/tasksListView.html",
          data: {
            isPublic: false
          },
          ncyBreadcrumb: {
            label: 'Pendenzen',
            parent: 'start'
          },
        })

  }
})();
