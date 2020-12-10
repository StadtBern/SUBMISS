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
 * @name selective.route.js
 * @description selective routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars

    $stateProvider
      .state("selectiveFirstStage", {
        url: "/firstStage/:id",
        controller: "SelectiveFirstStageController",
        controllerAs: "selectiveFirstStageCtrl",
        templateUrl: "app/selective/firstStage/selectiveFirstStage.html",
        params: {
          id: null
        },
        data: {
          isPublic: false
        }
      })

      .state("formalAuditSelective", {
        url: "/firstStage/fomalAudit/:id",
        controller: "FormalAuditSelectiveController",
        controllerAs: "formalAuditCtrl",
        templateUrl: "app/selective/firstStage/formalAuditSelective.html",
        params: {
          id: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{formalAuditCtrl.data.submission.workType.value1}} {{formalAuditCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("selectiveSecondStage", {
        url: "/secondStage/:id",
        controller: "SelectiveSecondStageController",
        controllerAs: "selectiveSecondStageCtrl",
        templateUrl: "app/selective/secondStage/selectiveSecondStage.html",
        params: {
          id: null
        },
        data: {
          isPublic: false
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
