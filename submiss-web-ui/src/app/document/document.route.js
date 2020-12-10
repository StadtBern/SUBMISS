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
 * @name document.route.js
 * @description document routes configuration
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars
    $stateProvider
      .state("documentView", {
        url: "/document/area/:id",
        controller: "DocumentViewController",
        controllerAs: "documentViewCtrl",
        templateUrl: "app/document/documentView.html",
        params: {
          id: null,
          uploadError: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          skip: true
        }
      })

      .state("documentView.documentList", {
        controller: "DocumentListController",
        controllerAs: "documentListCtr",
        templateUrl: "app/document/documentList.html",
        params: {
          submissionId: null,
          errorReturned: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("documentView.commissionProcurementProposal", {
        controller: "CommissionProcurementProposalController",
        controllerAs: "commissionProcurementProposalCtrl",
        templateUrl: "app/document/commissionProcurementProposal/commissionProcurementProposal.html",
        params: {
          submissionId: null,
          proceedToClose: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("documentView.commissionProcurementDecision", {
        controller: "CommissionProcurementDecisionController",
        controllerAs: "cPDecisionCtrl",
        templateUrl: "app/document/commissionProcurementDecision/commissionProcurementDecision.html",
        params: {
          submissionId: null,
          proceedToClose: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("documentView.submissionCancel", {
        controller: "SubmissionCancelController",
        controllerAs: "submissionCancelCtrl",
        templateUrl: "app/document/submissionCancel/submissionCancel.html",
        params: {
          submissionId: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("documentView.legalHearing", {
        controller: "LegalHearingController",
        controllerAs: "legalHearingCtrl",
        templateUrl: "app/document/legalHearing/legalHearing.html",
        params: {
          submissionId: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("documentView.submittentsExclusion", {
        controller: "SubmittentsExclusionController",
        controllerAs: "submittentsExclusionCtrl",
        templateUrl: "app/document/legalHearing/submittentsExclusion/submittentsExclusion.html",
        params: {
          submissionId: null,
          submittentId: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("documentView.awardInfo", {
        controller: "AwardInfoController",
        controllerAs: "awardInfoCtrl",
        templateUrl: "app/document/awardInfo/awardInfo.html",
        params: {
          submissionId: null,
          displayedOfferId: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      })

      .state("documentView.awardInfoFirstLevel", {
        controller: "AwardInfoFirstLevelController",
        controllerAs: "awardInfoFirstLevelCtrl",
        templateUrl: "app/document/awardInfoFirstLevel/awardInfoFirstLevel.html",
        params: {
          submissionId: null,
          displayedOfferId: null
        },
        data: {
          isPublic: false
        },
        ncyBreadcrumb: {
          label: '{{documentViewCtrl.submission.workType.value1}} {{documentViewCtrl.submission.workType.value2}}',
          parent: function ($rootScope) {
            return setParent($rootScope);
          }
        }
      });

    /** Function to set breadcrumb parent */
    function setParent(rootScope) {
      if (rootScope.$previousState === 'projectSubmissionsView' ||
        rootScope.$previousState === 'projectSubmissionsView.from.Offers' ||
        rootScope.$previousState === 'project.view' || rootScope.$previousState === 'project.search'
        || !rootScope.$previousState) {
        return 'project.view';
      } else {
        return 'project.search';
      }
    }
  }
})();
