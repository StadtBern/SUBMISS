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
 * @ngdoc controller
 * @name awardAdd.controller.js
 * @description AwardAddController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer").controller("AwardAddController",
      AwardAddController);

  /** @ngInject */
  function AwardAddController($scope, $location, $anchorScroll, $state, OfferService, SubmissionService,
    $filter, $stateParams, $uibModalInstance, $uibModal, AppService, offers, AppConstants,
    QFormJSRValidation) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.closeWindow = closeWindow;
    vm.offers = offers;
    vm.offer = offer;
    vm.activeSubmittents = [];
    vm.group = AppConstants.GROUP;
    vm.response = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.acceptanceModal = acceptanceModal;
    vm.showSubmittentJointVenturesSubcontractors = showSubmittentJointVenturesSubcontractors;
    vm.readActiveSubmittents = readActiveSubmittents;
    vm.addSelected = addSelected;
    vm.tdStyle = tdStyle;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readSubmission($stateParams.id);
      vm.readActiveSubmittents(offers);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function closeWindow(dirtyflag) {
      if (dirtyflag) {
        $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Zuschlag Erteilen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="awardAddCtr.closeWindowModal(\'ja\')">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'awardAddCtr',
            backdrop: 'static',
            size: 'lg',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeWindowModal = function (reason) {
                closeAwardAddModal.dismiss(reason);
              };
            }

          });

        return closeAwardAddModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            $uibModalInstance.close();
            return true;
          } else {
            // Do something else here
            return false;
          }
        });
      } else {
        $uibModalInstance.dismiss();
      }
      return null;
    }


    function acceptanceModal(checkedOffers) {
      if ($scope.awardAddCtr.addAwardForm.$invalid) {
        $scope.awardAddCtr.addAwardForm.$invalid = false;
      }
      var manualAwardForm = {};
      manualAwardForm.offerIds = [];
      manualAwardForm.submissionId = $stateParams.id;
      for (var i = 0; i < checkedOffers.length; i++) {
        manualAwardForm.offerIds[i] = checkedOffers[i].offer.id;
      }
      OfferService.validateManualAward(manualAwardForm)
        .success(function () {
          vm.response = null;
          $scope.awardAddCtr.addAwardForm.$invalid = false;
          $uibModal.open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Zuschlag wirklich erteilen und die Zuschlagsbewertung hiermit abschliessen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="awardAddCtr.update(); $close()">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'awardAddCtr',
            backdrop: 'static',
            size: 'lg',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.update = update;
              // Function to give awards to offers.
              function update() {
                OfferService.updateAwardOffers(manualAwardForm.offerIds, $stateParams.id).success(function () {
                  AppService.setIsDirty(false);
                  $uibModalInstance.close();
                  $state.go('offerListView', {
                    id: $stateParams.id
                  }, {
                    reload: true
                  });
                  return true;
                }).error(function (response, status) {
                  if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                    QFormJSRValidation.markErrors($scope,
                      $scope.awardAddCtr.addAwardForm, response);
                    vm.response = response;
                  }
                });
              }
            }
          });
        }).error(function (response, status) {
          if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.awardAddCtr.addAwardForm, response);
            vm.response = response;
          }
        });
    }

    /**Function to show Submittent (Tenderer) with its Joint Ventures and Subcontractors */
    function showSubmittentJointVenturesSubcontractors(offer) {
      return offer.companyName + ", " + offer.submittent.companyId.location + AppService.showJointVenturesSubcontractors(offer);
    }

    function addSelected() {
      vm.offerArray = [];
      vm.offerSelected = 0;
      vm.notSelected = false;
      angular.forEach(offers, function (offer) {
        if (offer.selected) {
          vm.offerArray.push(offer);
          vm.offerSelected++;
        }
        // Check if any submittent is selected
        if (vm.offerSelected < 1) {
          vm.notSelectedCompanyError = true;
          $anchorScroll();
        } else {
          vm.notSelectedCompanyError = false;
        }
      });
      return vm.offerArray;

    }

    // Function to insert in a list only the offers of the submittents that
    // are not excluded
    function readActiveSubmittents(offers) {
      for (var i = 0; i < offers.length; i++) {
        if (offers[i].offer.qExExaminationIsFulfilled &&
          (offers[i].submittent.submissionId.process === 'INVITATION' ||
            (offers[i].submittent.submissionId.process === 'OPEN' && !offers[i].submittent.submissionId.isServiceTender))) {
          vm.activeSubmittents.push(offers[i]);
        } else if (offers[i].submittent.submissionId.process === 'OPEN' &&
          offers[i].submittent.submissionId.isServiceTender) {
          if (!offers[i].offer.isExcludedFromProcess) {
            vm.activeSubmittents.push(offers[i]);
          }
        } else {
          if (!offers[i].submittent.existsExclusionReasons) {
            vm.activeSubmittents.push(offers[i]);
          }
        }
      }
    }

    /** Function to read submission data */
    function readSubmission(submissionId) {
      SubmissionService.readSubmission(submissionId)
        .success(function (data) {
          vm.submission = data;
        });
    }

    /** Function to mark submittent, if its offer amount exceeds the threshold */
    function tdStyle(offerId) {
      if (vm.response) {
        for (var i = 0; i < vm.response.length; i++) {
          if (vm.response[i].path === offerId) {
            return {
              "background-color": "#a94442"
            };
          }
        }
        return null;
      }
      return null;
    }
  }
})();
