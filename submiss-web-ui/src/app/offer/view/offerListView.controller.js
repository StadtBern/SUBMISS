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
 * @name offerListView.controller.js
 * @description OfferListViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer").controller("OfferListViewController",
      OfferListViewController);

  /** @ngInject */
  function OfferListViewController($rootScope, $scope, $location, $locale,
    $state, $stateParams,
    OfferService, SubmissionService, StammdatenService, QFormJSRValidation,
    $uibModal, NgTableParams,
    $filter, $transitions, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.openOfferAccordion = false;
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.offers
    });
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.currentStatus = "";
    vm.hasStatusCheck = false;
    vm.hasStatusChecked = false;
    vm.offer_exists = false;
    vm.noSubmittent = true;
    vm.moreThanOneError = false;
    vm.secTendererAdd = false;
    vm.secTenderAudit = false;
    vm.secTenderChecked = false;
    vm.secOfferOpeningClose = false;
    vm.secOfferOpeningRestart = false;
    vm.secOfferDelete = false;
    vm.secTendererRemove = false;
    vm.secCompanyView = false;
    vm.secAwarded = false;
    vm.secAwardRemoved = false;
    vm.secFormalAuditView = false;
    vm.secTenderMove = false;
    vm.secSentEmail = false;
    vm.reopen = AppConstants.STATUS_TO_REOPEN;
    vm.reopenQuestion = AppConstants.STATUS_REOPEN_QUESTION;
    vm.reopenTitle = AppConstants.STATUS_REOPEN_TITLE;
    vm.responseError = "";
    vm.errorPl = false;
    vm.errorAdmin = false;
    vm.displayedOffer = {};
    vm.isCompletedOrCancelled = false;
    vm.unsavedChangesModalOpen = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.readSubmission = readSubmission;
    vm.navigateToExamination = navigateToExamination;
    vm.navigateToExaminationNegotiatedProcedure = navigateToExaminationNegotiatedProcedure;
    vm.readCompaniesBySubmission = readCompaniesBySubmission;
    vm.readStatusOfSubmission = readStatusOfSubmission;
    vm.hasSubmissionStatusCheck = hasSubmissionStatusCheck;
    vm.hasSubmissionStatusChecked = hasSubmissionStatusChecked;
    vm.navigateToSubmissionDetails = navigateToSubmissionDetails;
    vm.navigateToCompany = navigateToCompany;
    vm.addCompany = addCompany;
    vm.checkTendererModal = checkTendererModal;
    vm.checkedTendererModal = checkedTendererModal;
    vm.closeOfferModal = closeOfferModal;
    vm.reopenOffer = reopenOffer;
    vm.awardContract = awardContract;
    vm.deleteSubmittentModal = deleteSubmittentModal;
    vm.deleteSubmittent = deleteSubmittent;
    vm.deleteOfferModal = deleteOfferModal;
    vm.openOfferDetails = openOfferDetails;
    vm.customRoundNumber = customRoundNumber;
    vm.formatAmount = formatAmount;
    vm.grossAmountOrGrossAmountCorrected = grossAmountOrGrossAmountCorrected;
    vm.undoAward = undoAward;
    vm.checkThresholdAndGroup = checkThresholdAndGroup;
    vm.offerOpeningCloseButtonDisabled = offerOpeningCloseButtonDisabled;
    vm.reopenOfferOpeningButtonDisabled = reopenOfferOpeningButtonDisabled;
    vm.checkedSubmittentButtonDisabled = checkedSubmittentButtonDisabled;
    vm.genericButtonDisabled = genericButtonDisabled;
    vm.deleteSubmittentDisabled = deleteSubmittentDisabled;
    vm.deleteOfferDisabled = deleteOfferDisabled;
    vm.addSubmittentDisabled = addSubmittentDisabled;
    vm.getObjectInfo = getObjectInfo;
    vm.manualAwardReopenButtonDisabled = manualAwardReopenButtonDisabled;
    vm.secOfferDetailsEdit = false;
    var offer = {};
    offer.show = false;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      AppService.setIsDirty(false);
      vm.readSubmission($stateParams.id);
      vm.readCompaniesBySubmission($stateParams.id);
      vm.readStatusOfSubmission($stateParams.id);
      vm.hasSubmissionStatusCheck($stateParams.id);
      vm.hasSubmissionStatusChecked($stateParams.id);
      vm.secOfferDetailsEdit = AppService.isOperationPermitted(
        AppConstants.OPERATION.OFFER_DETAILS_EDIT, null);
      vm.secTenderMove = AppService.isOperationPermitted(
        AppConstants.OPERATION.TENDER_MOVE, null);
      vm.secSentEmail = AppService.isOperationPermitted(
        AppConstants.OPERATION.SENT_EMAIL, null);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams,
      from, fromParams) {
      // save the previous state in a rootScope variable so that it's
      // accessible from everywhere
      $rootScope.previousState = from;
    });

    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            AppService.setIsDirty(false);
            return true;
          }
          return false;
        });
      }
      return null;
    });

    function readSubmission(id) {
      SubmissionService.readSubmission(id).success(function (data) {
        vm.data.submission = data;
        // check permitted operations
        vm.secTendererAdd = AppService.isOperationPermitted(
          AppConstants.OPERATION.TENDERER_ADD, null);
        vm.secTenderAudit = AppService.isOperationPermitted(
          AppConstants.OPERATION.TENDER_AUDIT, null);
        vm.secTenderChecked = AppService.isOperationPermitted(
          AppConstants.OPERATION.TENDER_CHECKED, null);
        vm.secOfferOpeningClose = AppService.isOperationPermitted(
          AppConstants.OPERATION.OFFER_OPENING_CLOSE,
          vm.data.submission.process);
        vm.secOfferOpeningRestart = AppService.isOperationPermitted(
          AppConstants.OPERATION.OFFER_OPENING_RESTART,
          vm.data.submission.process);
        vm.secOfferDelete = AppService.isOperationPermitted(
          AppConstants.OPERATION.OFFER_DELETE, null);
        vm.secTendererRemove = AppService.isOperationPermitted(
          AppConstants.OPERATION.TENDERER_REMOVE, null);
        vm.secCompanyView = AppService.isOperationPermitted(
          AppConstants.OPERATION.COMPANY_VIEW, null);
        vm.secAwarded = AppService.isOperationPermitted(
          AppConstants.OPERATION.AWARDED, vm.data.submission.process);
        vm.secAwardRemoved = AppService.isOperationPermitted(
          AppConstants.OPERATION.AWARD_REMOVED, vm.data.submission.process);
        vm.secFormalAuditView = AppService.isOperationPermitted(
          AppConstants.OPERATION.FORMAL_AUDIT_VIEW, vm.data.submission.process);
      }).error(function (response, status) {

      });
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          if (data === vm.status.PROCEDURE_COMPLETED ||
            data === vm.status.PROCEDURE_CANCELED) {
            vm.isCompletedOrCancelled = true;
            AppService.getSubmissionStatuses(id).success(function (data) {
              vm.statusHistory = data;
              vm.currentStatus = AppService.assignCurrentStatus(
                vm.statusHistory);
            });
          } else {
            vm.currentStatus = data;
          }
        }).error(function (response, status) {

        });
    }

    function hasSubmissionStatusCheck(id) {
      SubmissionService.hasSubmissionStatus(id, vm.status.SUBMITTENTLIST_CHECK)
        .success(function (data) {
          vm.hasStatusCheck = data;
        }).error(function (response, status) {

        });
    }

    function hasSubmissionStatusChecked(id) {
      SubmissionService.hasSubmissionStatus(id,
          vm.status.SUBMITTENTLIST_CHECKED)
        .success(function (data) {
          vm.hasStatusChecked = data;
        }).error(function (response, status) {

        });
    }

    function openOfferDetails(offer) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        offer.show = !offer.show;
        // if the details of one offer are opened, then close the one that
        // was opened before, so that there is always only one offer open
        if (offer.show) {
          if (vm.displayedOffer !== null) {
            vm.displayedOffer.show = false;
          }
          vm.displayedOffer = offer;
        } else {
          vm.displayedOffer = {};
        }
        $state.go('offerListView.offerDetails', {
          offer: offer.offer
        }, {});
      }
    }

    /** Function to handle the unsaved changes modal */
    function handleUnsavedChanges() {
      if (!vm.unsavedChangesModalOpen) {
        vm.unsavedChangesModalOpen = true;
        var unsavedChangesModal = AppService.informAboutUnsavedChanges();
        return unsavedChangesModal.result.then(function () {
          vm.unsavedChangesModalOpen = false;
        });
      }
      return null;
    }

    function readCompaniesBySubmission(id) {
      var timer = setTimeout(function () {
        AppService.setPaneShown(true);
      }, 1000);
      SubmissionService.readCompaniesBySubmission(id)
        .then(function (results) {
          clearTimeout(timer);
          AppService.setPaneShown(false);
          vm.offers = results.data;
          // Check if there are submittents present.
          if (vm.offers && vm.offers.length > 0) {
            vm.noSubmittent = false;
          }
          for (var i = 0; i < vm.offers.length; i++) {
            // If an offer accordion was open before reloading the page, keep it open after reloading the page.
            if ($stateParams.displayedOfferId && vm.offers[i].offer.id ===
              $stateParams.displayedOfferId) {
              vm.displayedOffer = vm.offers[i];
              vm.offers[i].show = true;
              var displayedOfferIndex = i;
              // Navigate to the accordion tab that was open, before the page reloaded.
              if ($stateParams.subcontractor) {
                $state.go('offerListView.subcontractorDetails', {
                  offer: vm.offers[i].offer,
                  subcontractor: true
                }, {});
                break;
              } else if ($stateParams.jointVenture) {
                $state.go('offerListView.jointVentureDetails', {
                  offer: vm.offers[i].offer,
                  jointVenture: true
                }, {});
                break;
              } else if ($stateParams.offerDetails) {
                $state.go('offerListView.offerDetails', {
                  offer: vm.offers[i].offer,
                  offerDetails: true
                }, {});
                break;
              } else if ($stateParams.operatingCost) {
                $state.go('offerListView.operatingCostDetails', {
                  offer: vm.offers[i].offer,
                  operatingCost: true
                }, {});
                break;
              } else if ($stateParams.ancilliaryCost) {
                $state.go('offerListView.ancilliaryCostDetails', {
                  offer: vm.offers[i].offer,
                  ancilliaryCost: true
                }, {});
                break;
              }
            }
          }
          var pageIndex = 1;
          // If there is an offer already displayed, navigate to the page which includes this offer.
          if (displayedOfferIndex) {
            pageIndex = parseInt(displayedOfferIndex / 10) + 1;
          }
          vm.tableParams = new NgTableParams({
            page: pageIndex,
            count: 10
          }, {
            total: vm.offers.length,
            getData: function ($defer, params) {
              var filteredData = params.filter() ?
                $filter('filter')(vm.offers, params.filter()) :
                tabledata;

              var orderedData = params.sorting() ?
                $filter('orderBy')(filteredData, params.orderBy()) : filteredData;

              if (params.count() === -1) {
                $defer.resolve(orderedData);
              } else {
                $defer.resolve(
                  orderedData.slice((params.page() - 1) * params.count(),
                    params.page() * params.count()));
              }
            }
          });
          return vm.tableParams;
        });
    }

    // Function that opens a modal to add a company for a project
    function addCompany() {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        if (vm.data.submission.process === 'NEGOTIATED_PROCEDURE' &&
          vm.offers.length === 1) {
          // submittent already exists, display error message
          vm.moreThanOneError = true;
        } else {
          // if process = 'NEGOTIATED_PROCEDURE' then only one company is
          // allowed
          var addCompany = AppService.addCompany(
            vm.data.submission.process === 'NEGOTIATED_PROCEDURE', null);
          return addCompany.result.then(function (response) {
            vm.companyList = [];
            vm.companyList = response;
            if (vm.companyList) {
              var companyId = '';
              for (var i = 0; i < vm.companyList.length; i++) {
                companyId = companyId + 'companyId=' + vm.companyList[i].id;
                if (vm.companyList.length !== i + 1) {
                  companyId = companyId + '&';
                }
              }
              SubmissionService
                .setCompanyToSubmission($stateParams.id, companyId)
                .success(function (data) {
                  AppService.setPaneShown(true);
                  $state.go('offerListView', {
                    id: $stateParams.id,
                    displayedOfferId: data.id,
                    offerDetails: true
                  }, {
                    reload: true
                  });
                });
            }
          });
        }
      }
      AppService.setPaneShown(false);
    }

    function checkTendererModal(submissionId) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var confirmationWindowInstance = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Submittentenliste prüfen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie die Submittentenliste wirklich in Prüfung geben? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="offerListViewCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'offerListViewCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeConfirmationWindow = function (reason) {
                confirmationWindowInstance.dismiss(reason);
              };
            }
          });
        return confirmationWindowInstance.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            checkTenderer(submissionId);
          } else {
            return false;
          }
          return null;
        });
      }
      return null;
    }

    function checkTenderer(submissionId) {
      SubmissionService.checkSubmittentList(submissionId).success(
        function (data) {
          $state.go($state.current, {}, {
            reload: true
          }); // reload the list
        }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.offerListViewCtrl.offerForm, response);
        }
      });
    }

    function checkedTendererModal(submissionId) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var confirmationWindowInstance = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Submittentenliste geprüft</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie die Submittentenliste wirklich freigeben und den Projektleiter hierüber per E-Mail informieren? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="offerListViewCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'offerListViewCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeConfirmationWindow = function (reason) {
                confirmationWindowInstance.dismiss(reason);
              };
            }
          });

        return confirmationWindowInstance.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            checkedTenderer(submissionId);
          } else {
            return false;
          }
          return null;
        });
      }
      return null;
    }

    function checkedTenderer(submissionId) {
      SubmissionService.checkedSubmittentList(submissionId).success(
        function (data) {
          window.location.href = data;
          $state.go($state.current, {}, {
            reload: true
          }); // reload the list
        }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.offerListViewCtrl.offerForm, response);
        }
      });
    }

    function closeOfferModal(submissionId) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var confirmationWindowInstance = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Offertöffnung abschliessen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie die Offertöffnung wirklich abschliessen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="offerListViewCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'offerListViewCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeConfirmationWindow = function (reason) {
                confirmationWindowInstance.dismiss(reason);
              };
            }
          });

        return confirmationWindowInstance.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            closeOffer(submissionId);
          } else {
            return false;
          }
          return null;
        });
      }
      return null;
    }

    function closeOffer(submissionId) {
      OfferService.closeOffer(submissionId)
        .success(function (data) {
          // Set displayedOfferId to null, so that there is no open accordion after reloading the page.
          $state.go("offerListView", {
            displayedOfferId: null
          }, {
            reload: true
          }); // reload the list
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.offerListViewCtrl.offerForm, response);
          }
        });
    }

    function reopenOffer() {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var reopenTenderStatusModal = AppService.reopenTenderStatusModal(
          vm.reopenTitle.OFFER_OPENING,
          vm.reopenQuestion.OFFER_OPENING, vm.reopen.OFFER_OPENING);
        return reopenTenderStatusModal.result
          .then(function (response) {
            var reopenForm = {};
            if (!angular.isUndefined(response)) {
              reopenForm.reopenReason = response;
              OfferService.reopenOffer(reopenForm, $stateParams.id)
                .success(function (data) {
                  // Set displayedOfferId to null, so that there is no open accordion after reloading the page.
                  $state.go("offerListView", {
                    displayedOfferId: null
                  }, {
                    reload: true
                  });
                }).error(function (response, status) {});
            }
          });
      }
      return null;
    }

    // Function to be called everytime is needed for the users PL and Admin
    // to take the specific validation messages.
    function checkThresholdAndGroup(group) {
      for (var i = 0; i < vm.offers.length; i++) {
        // check  if  submission has  1 offer above  the threshold.
        if (vm.offers[i].submittent.submissionId.aboveThreshold &&
          vm.offers[i].submittent.submissionId.process ===
          'NEGOTIATED_PROCEDURE' ||
          vm.offers[i].submittent.submissionId.process ===
          'NEGOTIATED_PROCEDURE_WITH_COMPETITION') {
          vm.aboveThreshold = true;
        }
        // Check  if submission's field reasonFreeAward  has been filled  out at Zuschlag erteilen
        if (!vm.offers[i].submittent.submissionId.reasonFreeAward.masterListValueId) {
          vm.reasonFreeAward = true;
        }

      }

      // A message is shown if the user role is pl and the submissions
      // above threshold is true on html
      if ((group === vm.group.PL) && vm.aboveThreshold) {
        // change the boolean to true so to have the return in other
        // functions that use this one(function)
        vm.errorPl = true;
        return vm.errorPl;
      }

      // A message is shown if the user role is admin and the
      // submissions above threshold is true and at submission update
      // is missing the reasonFreeAward dropdown.
      else if ((group === vm.group.ADMIN) && vm.reasonFreeAward &&
        vm.aboveThreshold) {
        vm.errorAdmin = true;
        return vm.errorAdmin;
      }
      return false;

    }

    /** Function that opens a modal to give awards to submittents, after updating the threshold value */
    function awardContract(group) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        // After successful threshold update, open the modal.
        var addAward = AppService.addAward(vm.offers);
        return addAward.result.then(function (response) {});
      }
      return null;
    }

    /** Function to reopen the manual award */
    function undoAward(group) {
      OfferService.manualAwardReopenCheck(vm.data.submission.id)
        .success(function (data) {
          // Function to open a modal with the following parameters: modal title , message
          // and status to reopen.
          var reopenTenderStatusModal = AppService.reopenTenderStatusModal(
            vm.reopenTitle.MANUAL_AWARD,
            vm.reopenQuestion.MANUAL_AWARD, vm.reopen.MANUAL_AWARD);
          return reopenTenderStatusModal.result.then(function (response) {
            if (!angular.isUndefined(response)) {
              var reopenForm = {};
              reopenForm.reopenReason = response;
              OfferService.reopenAward($stateParams.id,
                reopenForm.reopenReason).success(function (data) {
                $state.reload();
              }).error(function (response, status) {});
            }
          });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.offerListViewCtrl.offerForm, response);
          }
        });
    }

    /** Create a function to show a modal for deleting a Submittent */
    function deleteSubmittentModal(submittentid, offerId) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var confirmationWindowInstance = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Submittent entfernen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie diesen Submittent wirklich löschen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="offerListViewCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'offerListViewCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeConfirmationWindow = function (reason) {
                confirmationWindowInstance.dismiss(reason);
              };
            }
          });

        return confirmationWindowInstance.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            deleteSubmittent(submittentid, offerId);
          } else {
            return false;
          }
          return null;
        });
      }
      return null;
    }

    /** Create a function to delete the Submittent */
    function deleteSubmittent(submittentid, offerId) {
      OfferService.deleteSubmittent(submittentid)
        .success(function (data) {
          $state.go($state.current, {
            displayedOfferId: null
          }, {
            reload: true
          });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.offerListViewCtrl.offerForm, response);
          }
        });
    }

    function deleteOfferModal(offerId) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var confirmationWindowInstance = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Offerte löschen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie diese Offerte wirklich löschen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="offerListViewCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="offerListViewCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'offerListViewCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeConfirmationWindow = function (reason) {
                confirmationWindowInstance.dismiss(reason);
              };
            }
          });
        return confirmationWindowInstance.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            deleteOffer(offerId);
          } else {
            return false;
          }
          return null;
        });
      }
      return null;
    }

    /** Create a function to delete an Offer */
    function deleteOffer(offerId) {
      OfferService.deleteOffer(offerId)
        .success(function (data) {
          $state.go('offerListView', {
            displayedOfferId: data[0],
            offerDetails: true
          }, {
            reload: true
          });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.offerListViewCtrl.offerForm, response);
          }
        });
    }

    function navigateToSubmissionDetails(submissionId) {
      $state.go('submissionView', {
        id: submissionId
      });
    }

    function navigateToExamination(submissionId) {
      $state.go('examination.view', {
        id: submissionId
      });
    }

    function navigateToExaminationNegotiatedProcedure(submissionId) {
      $state.go('examination.formal', {
        id: submissionId
      });
    }

    function navigateToCompany(companyId) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var url = $state.href('company.view', {
          id: companyId
        });
        window.open(url, '_blank');
      }
    }

    function customRoundNumber(num) {
      return AppService.customRoundNumber(num);
    }

    /** Format number according to specifications */
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }

    /**
     * Function to determine which of the values grossAmount and
     * grossAmountCorrected is to be displayed
     */
    function grossAmountOrGrossAmountCorrected(isCorrected, grossAmount,
      grossAmountCorrected) {
      if (isCorrected) {
        return grossAmountCorrected;
      } else {
        return grossAmount;
      }
    }

    /** Function to determine if the offer opening close button is disabled */
    function offerOpeningCloseButtonDisabled() {
      return (!vm.secOfferOpeningClose || vm.currentStatus <
          vm.status.OFFER_OPENING_STARTED ||
          ((vm.data.submission.process !== AppConstants.PROCESS.SELECTIVE) &&
            (!vm.data.submission.secondDeadline ||
              !vm.data.submission.offerOpeningDate ||
              !vm.data.submission.secondLoggedBy)) ||
          ((vm.data.submission.process === AppConstants.PROCESS.SELECTIVE) &&
            (!vm.data.submission.firstDeadline ||
              !vm.data.submission.applicationOpeningDate ||
              !vm.data.submission.firstLoggedBy))) ||
        AppService.offerEditingDisabled(vm.data.submission) ||
        vm.isCompletedOrCancelled;
    }

    /** Reopen offer opening button disabled */
    function reopenOfferOpeningButtonDisabled() {
      return (vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED ||
          (vm.currentStatus >= vm.status.FORMAL_AUDIT_COMPLETED &&
            (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE ||
              vm.data.submission.process ===
              AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
              vm.data.submission.process ===
              AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION))) ||
        vm.isCompletedOrCancelled;
    }

    /** Check if checked submittent button is disabled */
    function checkedSubmittentButtonDisabled() {
      return !vm.secTenderChecked ||
        vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK ||
        vm.isCompletedOrCancelled;
    }

    /** Generic rule for disabled buttons */
    function genericButtonDisabled() {
      return vm.currentStatus >=
        vm.status.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED ||
        vm.isCompletedOrCancelled;
    }

    function manualAwardReopenButtonDisabled() {
      return ((vm.data.submission.process === AppConstants.PROCESS.SELECTIVE ||
            vm.data.submission.process === AppConstants.PROCESS.INVITATION ||
            vm.data.submission.process === AppConstants.PROCESS.OPEN ||
            ((vm.data.submission.process ===
                AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
                vm.data.submission.process ===
                AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
              vm.data.submission.aboveThreshold)) && vm.currentStatus >=
          vm.status.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED) ||
        vm.isCompletedOrCancelled;
    }

    /** Function to disable the submittent deletion */
    function deleteSubmittentDisabled(isApplicant) {
      return !vm.secTendererRemove || isApplicant ||
        (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE &&
          vm.currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_S) ||
        (AppService.getLoggedUser().userGroup.name === vm.group.ADMIN &&
          vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED) ||
        (AppService.getLoggedUser().userGroup.name === vm.group.PL &&
          (vm.data.submission.process === AppConstants.PROCESS.OPEN ||
            vm.data.submission.process === AppConstants.PROCESS.SELECTIVE ||
            ((vm.data.submission.process ===
                AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
                vm.data.submission.process ===
                AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
              vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED ||
              vm.currentStatus === vm.status.SUBMITTENTLIST_CHECK) ||
            (vm.data.submission.process === AppConstants.PROCESS.INVITATION &&
              (vm.currentStatus >= vm.status.SUBMITTENTLIST_CHECK ||
                vm.currentStatus >= vm.status.SUBMITTENTLIST_CHECKED ||
                vm.currentStatus >= vm.status.OFFER_OPENING_STARTED)))) ||
        vm.isCompletedOrCancelled;
    }

    /** Function to disable the offer deletion */
    function deleteOfferDisabled() {
      return !vm.secOfferDelete ||
        (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE &&
          vm.currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_S) ||
        vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED ||
        (AppService.getLoggedUser().userGroup.name === vm.group.PL &&
          (vm.data.submission.process !==
            AppConstants.PROCESS.NEGOTIATED_PROCEDURE &&
            vm.data.submission.process !==
            AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) ||
          ((vm.data.submission.process ===
              AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
              vm.data.submission.process ===
              AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
            vm.currentStatus === vm.status.SUBMITTENTLIST_CHECK)) ||
        AppService.offerEditingDisabled(vm.data.submission) ||
        vm.isCompletedOrCancelled;
    }

    /** Function to disable the submittent addition */
    function addSubmittentDisabled() {
      return !vm.secTendererAdd ||
        (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE &&
          vm.currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_S) ||
        !((AppService.getLoggedUser().userGroup.name === vm.group.ADMIN &&
            vm.currentStatus < vm.status.OFFER_OPENING_CLOSED) ||
          ((AppService.getLoggedUser().userGroup.name === vm.group.PL &&
              vm.data.submission.process !== AppConstants.PROCESS.OPEN &&
              vm.data.submission.process !== AppConstants.PROCESS.SELECTIVE) &&
            ((vm.data.submission.process === AppConstants.PROCESS.INVITATION &&
                (vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK &&
                  vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECKED &&
                  vm.currentStatus < vm.status.OFFER_OPENING_STARTED)) ||
              ((vm.data.submission.process ===
                  AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
                  vm.data.submission.process ===
                  AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
                (vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
                  vm.currentStatus !== vm.status.SUBMITTENTLIST_CHECK))))) ||
        vm.isCompletedOrCancelled;
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }
  }
})();
