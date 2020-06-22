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
 * @name company.route.js
 * @description company routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) {

    /**
     * Constants
     */
    var COMPANYSEARCH = 'company.search';

    $stateProvider.state("company", {
      abstract: true,
      url: "/company",
      template: "<ui-view/>",
      data: {
        isPublic: false
      },
    });
    $stateProvider.state("company.view", {
      url: "/view/:id",
      controller: "CompanyViewController",
      controllerAs: "companyViewCtr",
      templateUrl: "app/company/view/companyView.html",
      data: {
        isPublic: false
      },
      params: {
        id: null,
        name: name
      },
      ncyBreadcrumb: {
        label: '{{companyViewCtr.data.company.companyName}}',
        parent: COMPANYSEARCH
      }
    });
    $stateProvider.state("company.create", {
      url: "/create",
      controller: "CompanyCreateController",
      controllerAs: "companyCreateCtr",
      templateUrl: "app/company/create/companyCreate.html",
      data: {
        isPublic: false
      },
      ncyBreadcrumb: {
        label: 'Firma eröffnen',
        parent: 'start'
      }
    });
    $stateProvider.state("company.search", {
      url: "/search",
      controller: "CompanySearchController",
      controllerAs: "companySearchCtr",
      templateUrl: "app/company/search/companySearch.html",
      data: {
        isPublic: false
      },
      onExit: function ($rootScope) {
        $rootScope.$previousState = COMPANYSEARCH;

      },
      ncyBreadcrumb: {
        label: 'Firma suchen',
        parent: 'start'
      }
    });
    $stateProvider.state("company.offers", {
      url: "/offers/:id",
      controller: "CompanyOffersController",
      controllerAs: "companyOffersCtr",
      templateUrl: "app/company/view/companyOffers.html",
      params: {
        id: null
      },
      data: {
        isPublic: false
      },
      ncyBreadcrumb: {
        label: '{{companyOffersCtr.data.company.companyName}}',
        parent: COMPANYSEARCH
      }
    });
    $stateProvider.state("company.proofs", {
      url: "/proofs/:id",
      controller: "CompanyProofsController",
      controllerAs: "companyProofsCtr",
      templateUrl: "app/company/view/companyProofs.html",
      params: {
        id: null
      },
      data: {
        isPublic: false
      },
      onExit: function ($rootScope) {
        $rootScope.camefromOffers = true;

      },
      ncyBreadcrumb: {
        label: '{{companyProofsCtr.data.company.companyName}}',
        parent: COMPANYSEARCH
      }
    });
    $stateProvider.state("company.companyDocumentArea", {
      url: "/companyDocumentArea/:id",
      controller: "CompanyDocumentAreaController",
      controllerAs: "companyDocumentAreaCtrl",
      templateUrl: "app/company/view/companyDocumentArea.html",
      params: {
        id: null
      },
      data: {
        isPublic: false
      },
      ncyBreadcrumb: {
        label: '{{companyDocumentAreaCtrl.data.company.companyName}}',
        parent: COMPANYSEARCH
      }
    });
  }
})();
