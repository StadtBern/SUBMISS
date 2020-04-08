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
 * @name applicants.controller.js
 * @description ApplicantsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss").controller("ApplicantsController",
      ApplicantsController);

  /** @ngInject */
  function ApplicantsController($rootScope, $scope, $state, $stateParams,
    $anchorScroll,
    SubmissionService, OfferService, QFormJSRValidation, $uibModal,
    NgTableParams,
    $filter, $transitions, AppService, AppConstants, SelectiveService) {
    const
      vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var applicant = {};
    var i;
    applicant.show = false;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.applicants = [];
    vm.secCompanyView = false;
    vm.secApplicantsAdd = false;
    vm.secApplicantDelete = false;
    vm.secApplicationDetailsTabVisible = false;
    vm.secApplicationDelete = false;
    vm.secApplicationOpeningClose = false;
    vm.secApplicationOpeningReopen = false;
    vm.reopenQuestion = AppConstants.STATUS_REOPEN_QUESTION;
    vm.reopenTitle = AppConstants.STATUS_REOPEN_TITLE;
    vm.reopen = AppConstants.STATUS_TO_REOPEN;
    vm.status = AppConstants.STATUS;
    vm.displayedApplicant = {};
    vm.tdStyle = {
      "background-color": "transparent"
    };
    vm.isCompletedOrCancelled = false;
    vm.unsavedChangesModalOpen = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addApplicants = addApplicants;
    vm.deleteApplicantModal = deleteApplicantModal;
    vm.navigateToCompany = navigateToCompany;
    vm.openApplicantDetails = openApplicantDetails;
    vm.deleteApplicationModal = deleteApplicationModal;
    vm.closeApplicationOpening = closeApplicationOpening;
    vm.reopenApplicationOpening = reopenApplicationOpening;
    vm.getObjectInfo = getObjectInfo;
    vm.firstLevelSubmissionFieldsEmpty = firstLevelSubmissionFieldsEmpty;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      SubmissionService.loadApplicants($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            AppService.setIsDirty(false);
            readSubmission($stateParams.id);
            readApplicantsOfSubmission($stateParams.id);
            readStatusOfSubmission($stateParams.id);
            implementingSecurity($stateParams.id);
          }
        });
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

    /** Function to read submission information */
    function readSubmission(submissionId) {
      SubmissionService.readSubmission(submissionId).success(function (data) {
        vm.submission = data;
      }).error(function (response, status) {

      });
    }

    /** Function to read status of submission */
    function readStatusOfSubmission(submissionId) {
      SubmissionService.getCurrentStatusOfSubmission(submissionId).success(
        function (data) {
          if (data === vm.status.PROCEDURE_COMPLETED ||
            data === vm.status.PROCEDURE_CANCELED) {
            vm.isCompletedOrCancelled = true;
            AppService.getSubmissionStatuses(submissionId).success(
              function (data) {
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

    /** Function to read applicants of submission */
    function readApplicantsOfSubmission(submissionId) {
      var timer = setTimeout(function () {
        AppService.setPaneShown(true);
      }, 1000);
      SubmissionService.getApplicantsBySubmission(submissionId).then(
        function (results) {
          clearTimeout(timer);
          AppService.setPaneShown(false);
          vm.applicants = results.data;
          for (i = 0; i < vm.applicants.length; i++) {
            // creating a new object because we need to sort with a
            // nested object and angular does not support that
            vm.applicants[i].companyName = vm.applicants[i].submittent.companyId.companyName;
            // If an applicant accordion was open before reloading the page, keep it open after reloading the page.
            if ($stateParams.displayedApplicationId && vm.applicants[i].offer.id ===
              $stateParams.displayedApplicationId) {
              vm.displayedApplicant = vm.applicants[i];
              vm.applicants[i].show = true;
              var displayedApplicantIndex = i;
              // Navigate to the accordion tab that was open, before the page reloaded.
              if ($stateParams.subcontractor) {
                $state.go('applicants.applicantSubcontractorDetails', {
                  offer: vm.applicants[i].offer,
                  subcontractor: true
                }, {});
                break;
              } else if ($stateParams.jointVenture) {
                $state.go('applicants.applicantJointVentureDetails', {
                  offer: vm.applicants[i].offer,
                  jointVenture: true
                }, {});
                break;
              } else if ($stateParams.applicationDetails) {
                $state.go('applicants.applicationDetails', {
                  offer: vm.applicants[i].offer,
                  applicationDetails: true
                }, {});
                break;
              }
            }
          }
          var pageIndex = 1;
          // If there is an applicant already displayed, navigate to the page which includes this applicant.
          if (displayedApplicantIndex) {
            pageIndex = parseInt(displayedApplicantIndex / 10) + 1;
          }
          vm.tableParams = new NgTableParams({
            page: pageIndex,
            count: 10
          }, {
            total: vm.applicants.length,
            getData: function ($defer, params) {
              var filteredData = params.filter() ?
                $filter('filter')(vm.applicants, params.filter()) : tabledata;
              var orderedData = params.sorting() ?
                $filter('orderBy')(filteredData, params.orderBy()) :
                filteredData;
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

    /** Function to show a modal for deleting an applicant */
    function deleteApplicantModal(applicantId) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var confirmationWindowInstance = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Bewerber entfernen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Bewerber wirklich entfernen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="deleteApplicantCtrl.closeConfirmationWindow(true);">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="deleteApplicantCtrl.closeConfirmationWindow(false);">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'deleteApplicantCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeConfirmationWindow = function (reason) {
                confirmationWindowInstance.dismiss(reason);
              };
            }
          });
        return confirmationWindowInstance.result.then(function () {}, function (response) {
          if (response) {
            SelectiveService.deleteApplicant(applicantId).success(
              function (data) {
                $state.reload();
              }).error(function (response, status) {
              if (status === 400) {
                QFormJSRValidation.markErrors($scope,
                  $scope.applicantsForm, response);
              }
            });
          }
          return null;
        });
      }
      return null;
    }

    /** Function to add applicant to submission */
    function addApplicants() {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var addApplicantsModal = AppService.addCompany(false, null);
        return addApplicantsModal.result.then(function (response) {
          vm.applicantList = [];
          vm.applicantList = response;
          if (vm.applicantList) {
            var companyId = '';
            for (var i = 0; i < vm.applicantList.length; i++) {
              companyId = companyId + 'companyId=' + vm.applicantList[i].id;
              if (vm.applicantList.length !== i + 1) {
                companyId = companyId + '&';
              }
            }
            SubmissionService
              .setCompanyToSubmission($stateParams.id, companyId)
              .success(function (data) {
                AppService.setPaneShown(true);
                $state.go('applicants', {
                  id: $stateParams.id,
                  displayedApplicationId: data.id,
                  applicationDetails: true
                }, {
                  reload: true
                });
              });
          }
        });
      }
      AppService.setPaneShown(false);
    }

    /** Function to navigate to company view */
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

    /** Function to check for user rights when performing operations */
    function implementingSecurity(submissionId) {
      vm.secCompanyView = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_VIEW, null);
      vm.secApplicantsAdd =
        AppService.isOperationPermitted(AppConstants.OPERATION.APPLICANTS_ADD,
          AppConstants.PROCESS.SELECTIVE);
      vm.secApplicantDelete =
        AppService.isOperationPermitted(AppConstants.OPERATION.APPLICANT_DELETE,
          AppConstants.PROCESS.SELECTIVE);
      vm.secApplicationDetailsTabVisible =
        AppService.isOperationPermitted(AppConstants.OPERATION.APPLICATION_VIEW,
          AppConstants.PROCESS.SELECTIVE);
      vm.secApplicationDelete =
        AppService.isOperationPermitted(
          AppConstants.OPERATION.APPLICATION_DELETE,
          AppConstants.PROCESS.SELECTIVE);
      vm.secApplicationOpeningClose =
        AppService.isOperationPermitted(
          AppConstants.OPERATION.APPLICATION_OPENING_CLOSE,
          AppConstants.PROCESS.SELECTIVE);
      vm.secApplicationOpeningReopen =
        AppService.isOperationPermitted(
          AppConstants.OPERATION.APPLICATION_OPENING_REOPEN,
          AppConstants.PROCESS.SELECTIVE);
    }

    /** Function to open applicant details */
    function openApplicantDetails(applicant) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        applicant.show = !applicant.show;
        // If the details of one applicant are opened, then close the one that
        // was opened before, so that there is always only one applicant window open.
        if (applicant.show) {
          if (vm.displayedApplicant !== null) {
            vm.displayedApplicant.show = false;
          }
          vm.displayedApplicant = applicant;
        } else {
          vm.displayedApplicant = {};
        }
        $state.go('applicants.applicationDetails', {
          offer: applicant.offer
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

    /** Function to delete an application */
    function deleteApplicationModal(applicationId, applicationVersion) {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var confirmationWindowInstance = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Bewerbung entfernen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie die Bewerbung wirklich löschen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="deleteApplicationCtrl.closeConfirmationWindow(true);">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="deleteApplicationCtrl.closeConfirmationWindow(false);">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'deleteApplicationCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeConfirmationWindow = function (reason) {
                confirmationWindowInstance.dismiss(reason);
              };
            }
          });
        return confirmationWindowInstance.result.then(function () {}, function (response) {
          if (response) {
            SelectiveService.deleteApplication(applicationId, applicationVersion).success(
              function (data) {
                $state.go('applicants', {
                  displayedApplicationId: data[0],
                  applicationDetails: true
                }, {
                  reload: true
                });
              }).error(function (response, status) {
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) {
                $anchorScroll();
                QFormJSRValidation.markErrors($scope,
                  $scope.applicantsForm, response);
              }
            });
          }
          return null;
        });
      }
      return null;
    }

    /** Function to close the application opening */
    function closeApplicationOpening() {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        var closeApplicationOpeningModal = AppService.closeApplicationOpening();
        return closeApplicationOpeningModal.result.then(function (response) {
          if (response) {
            SelectiveService.closeApplicationOpening(vm.submission.id, vm.submission.version).success(
              function (data) {
                $state.go('applicants', {
                  displayedApplicationId: null
                }, {
                  reload: true
                });
              }).error(function (response, status) {
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) {
                $anchorScroll();
                QFormJSRValidation.markErrors($scope,
                  $scope.applicantsForm, response);
              }
            });
          }
        });
      }
      return null;
    }

    /** Function to reopen the application opening */
    function reopenApplicationOpening() {
      var reopenTenderStatusModal = AppService.reopenTenderStatusModal(
        vm.reopenTitle.APPLICATION_OPENING,
        vm.reopenQuestion.APPLICATION_OPENING, vm.reopen.APPLICATION_OPENING);
      return reopenTenderStatusModal.result.then(function (response) {
        var reopenForm = {};
        if (!angular.isUndefined(response)) {
          reopenForm.reopenReason = response;
          SelectiveService.reopenApplicationOpening(reopenForm,
              vm.submission.id, vm.submission.version)
            .success(function (data) {
              $state.reload();
            }).error(function (response, status) {
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) {
                $anchorScroll();
                QFormJSRValidation.markErrors($scope,
                  $scope.applicantsForm, response);
              }
            });
        }
      });
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }

    /** Function to check if the submission mandatory fields for the 1st level are empty. */
    function firstLevelSubmissionFieldsEmpty() {
      if (vm.submission) {
        return (!vm.submission.firstDeadline ||
          !vm.submission.applicationOpeningDate ||
          !vm.submission.firstLoggedBy);
      }
      return true;
    }
  }
})();
