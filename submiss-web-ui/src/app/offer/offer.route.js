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
 * @name offer.route.js
 * @description offer routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars

    $stateProvider
      .state(
        "offerListView", {
          url: "/offers/view/:id",
          controller: "OfferListViewController",
          controllerAs: "offerListViewCtrl",
          templateUrl: "app/offer/view/offerListView.html",
          params: {
            id: null,
            displayedOfferId: null,
            subcontractor: false,
            jointVenture: false,
            offerDetails: false,
            operatingCost: false,
            ancilliaryCost: false
          },
          data: {
            isPublic: false
          },
          ncyBreadcrumb: {
            label: '{{offerListViewCtrl.data.submission.workType.value1}} {{offerListViewCtrl.data.submission.workType.value2}}',
            parent: function ($rootScope) {
              return setParent($rootScope);
            }
          }
        })

      .state("offerListView.offerDetails", {
        controller: "OfferDetailsController",
        controllerAs: "offerDetailsCtrl",
        templateUrl: "app/offer/view/offerDetails.html",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          offerDetails: false,
          operatingCost: false,
          ancilliaryCost: false
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{offerListViewCtrl.data.submission.workType.value1}} {{offerListViewCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("offerListView.jointVentureDetails", {
        url: "/offers/view/jointVentureDetails",
        controller: "JointVentureDetailsController",
        controllerAs: "jointVentureDetailsCtrl",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          offerDetails: false,
          operatingCost: false,
          ancilliaryCost: false
        },
        templateUrl: "app/offer/view/jointVentureDetails.html",
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{offerListViewCtrl.data.submission.workType.value1}} {{offerListViewCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("offerListView.ancilliaryCostDetails", {
        controller: "AncilliaryCostDetailsController",
        controllerAs: "ancilliaryCostDetailsCtrl",
        templateUrl: "app/offer/view/ancilliaryCostDetails.html",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          offerDetails: false,
          operatingCost: false,
          ancilliaryCost: false
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{offerListViewCtrl.data.submission.workType.value1}} {{offerListViewCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("offerListView.operatingCostDetails", {
        controller: "OperatingCostDetailsController",
        controllerAs: "operatingCostDetailsCtrl",
        templateUrl: "app/offer/view/operatingCostDetails.html",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          offerDetails: false,
          operatingCost: false,
          ancilliaryCost: false
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{offerListViewCtrl.data.submission.workType.value1}} {{offerListViewCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("offerListView.subcontractorDetails", {
        url: "/offers/view/subcontractorDetails",
        controller: "SubcontractorDetailsController",
        controllerAs: "subcontractorDetailsCtrl",
        params: {
          offer: null,
          subcontractor: false,
          jointVenture: false,
          offerDetails: false,
          operatingCost: false,
          ancilliaryCost: false
        },
        templateUrl: "app/offer/view/subcontractorDetails.html",
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{offerListViewCtrl.data.submission.workType.value1}} {{offerListViewCtrl.data.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      });

    /** Function to set breadcrumb parent */
    function setParent(rootScope) {
      if (rootScope.$previousState === 'projectSubmissionsView' ||
        rootScope.$previousState === 'projectSubmissionsView.from.Offers' ||
        rootScope.$previousState === 'project.view') {
        return 'project.view';
      } else if (!rootScope.$previousState || rootScope.$previousState ===
        'project.search') {
        return 'project.search';
      }
    }

  }
})();
