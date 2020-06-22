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
 * @ngdoc service
 * @name i18n.service.js
 * @description An application-wide service allowing component to exchange
 * global data.
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .factory("AppService", AppService);

  /** @ngInject */
  function AppService(UsersService, $state, $http, $log, $uibModal,
    AppConstants, $filter) {
    // The currently logged-in user.
    var loggedUser;
    var permOpsMenu = [];
    var isPaneShown;
    var submittentList = [];
    var isDirty;
    var unregisteredUserTenant = null;
    var unregisteredUserGroup = null;

    // Exported functions and variables.
    return {
      // Get application version information from git.
      getAppVersionInfo: function () {
        // Version information exists in two different locations to facilitate
        // gulp and production installations. We first search the location of
        // the production installation and if not found we switch to the
        // gulp location.
        //
        return $http.get("scripts/git.json").then(
          function success(response) {
            return response.data;
          },
          function error(response) {
            return $http.get("assets/git.json").then(
              function success(response) {
                return response.data;
              },
              function error(response) {
                $log.log("Could not find git.json, make sure you have run mvn " +
                  "compile/install first.");
              })
          }
        )
      },

      /**
       * If a user has successfully passed the log-in criteria its profile is
       * locally kept by this service and returned by this method.
       * @returns {*} Returns the currently logged-in user.
       */
      getLoggedUser: function () {
        return loggedUser;
      },

      getPermOpsMenu: function () {
        return permOpsMenu;
      },

      getPaneShown: function () {
        return isPaneShown;
      },

      setPaneShown: function (shown) {
        isPaneShown = shown;
      },
      getSubmittentList: function () {
        return submittentList;
      },
      emptySubmittentList: function () {
        submittentList = [];
        return submittentList;
      },

      setIsDirty: function (dirty) {
        isDirty = dirty;
      },

      getIsDirty: function () {
        return isDirty;
      },

      setUnregisteredUserTenant: function (tenant) {
        unregisteredUserTenant = tenant;
      },

      getUnregisteredUserTenant: function () {
        return unregisteredUserTenant;
      },

      setUnregisteredUserGroup: function (group) {
        unregisteredUserGroup = group;
      },

      getUnregisteredUserGroup: function () {
        return unregisteredUserGroup;
      },

      /**
       * Sets the currently logged in user.
       * @param user The user info to set.
       */
      setLoggedUser: function (user) {
        $log.debug("Setting user: ", user);
        loggedUser = user;
        // check permitted operations
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.USER_OPERATION_USER_VIEW) >= 0) {
          permOpsMenu[0] = true;
        } else {
          permOpsMenu[0] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.COMPANY_CREATE) >= 0) {
          permOpsMenu[1] = true;
        } else {
          permOpsMenu[1] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.COMPANY_SEARCH) >= 0) {
          permOpsMenu[2] = true;
        } else {
          permOpsMenu[2] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.PROJECT_SEARCH) >= 0) {
          permOpsMenu[3] = true;
        } else {
          permOpsMenu[3] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.PROJECT_CREATE) >= 0) {
          permOpsMenu[4] = true;
        } else {
          permOpsMenu[4] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.EXTENDED_ACCESS) >= 0) {
          permOpsMenu[5] = true;
        } else {
          permOpsMenu[5] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.PENDING) >= 0) {
          permOpsMenu[6] = true;
        } else {
          permOpsMenu[6] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.PROJECT) >= 0) {
          permOpsMenu[7] = true;
        } else {
          permOpsMenu[7] = false;
        }
        if (loggedUser.permittedOperations.indexOf(AppConstants.OPERATION.AUDIT) >=
          0) {
          permOpsMenu[8] = true;
        } else {
          permOpsMenu[8] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.GENERATE_REPORT) >= 0) {
          permOpsMenu[9] = true;
        } else {
          permOpsMenu[9] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.GENERATE_OPERATIONS_REPORT) >= 0) {
          permOpsMenu[10] = true;
        } else {
          permOpsMenu[10] = false;
        }
        if (loggedUser.permittedOperations.indexOf(
            AppConstants.OPERATION.GENERATE_USERS_EXPORT_REPORT) >= 0) {
          permOpsMenu[11] = true;
        } else {
          permOpsMenu[11] = false;
        }
      },

      addCompany: function (onlyOneAllowed, notAllowedCompany) {
        return $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Firma hinzufügen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div ng-include src="' +
            '\'' +
            'app/company/companyAdd.html' +
            '\'">' +
            '<div>' +
            '<div>',
          controller: 'CompanyAddController',
          controllerAs: 'companyAddCtr',
          size: 'lg',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            onlyOneAllowed: function () {
              return onlyOneAllowed;
            },
            notAllowedCompany: function () {
              return notAllowedCompany;
            }
          }
        });
      },

      addSubmittent: function (submittent) {
        submittentList.push(submittent);
      },

      addAward: function (offers) {
        return $uibModal.open({
          templateUrl: 'app/offer/awardAdd.html',
          controller: 'AwardAddController',
          controllerAs: 'awardAddCtr',
          size: 'lg',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            offers: function () {
              return offers;
            }
          }
        });
      },

      // check if an operation is permitted for a given operation and process type
      isOperationPermitted: function (operation, processType) {
        // transform the process type
        var prName = AppConstants.PROCESS_MAP[processType];
        // build the operation string as concatenation of process type and operation
        var opName;
        if (processType !== null) {
          opName = prName + "." + operation;
        } else {
          opName = operation;
        }
        // check if the operation is one of the permitted operations of the user
        return (loggedUser.permittedOperations.indexOf(opName) >= 0);
      },

      /**Function to implement the swiss number rounding standard ("Rappenrundung") */
      customRoundNumber: function (num) {
        if (num === null || isNaN(num)) {
          return $filter('number')(0, 2);
        }
        var diff = $filter('number')(num - (Math.floor(num * 10) / 10), 6); //round diff to 6th decimal in order to keep 3rd decimal unchanged from rounding
        if (diff < 0.025) {
          num = Math.floor(num * 10) / 10;
        } else if (diff >= 0.025 && diff < 0.075) {
          num = (Math.floor(num * 10) / 10) + 0.05;
        } else {
          num = (Math.floor(num * 10) / 10) + 0.1;
        }
        num = $filter('number')(num, 2).replace(/,/g, "'"); //number with two decimals, thousand separator as apostrophe
        return num;
      },

      /**Format number according to specifications (two decimal places and apostrophe as thousand separator) */
      formatAmount: function (num) {
        if (Math.abs(num) >= 1000) {
          num = $filter('number')(num, 2).replace(/,/g, "'");
        } else {
          num = $filter('number')(num, 2);
        }
        return num;
      },

      closeSubmissionModal: function () {
        return $uibModal.open({
          templateUrl: 'app/submission/closeSubmission/closeSubmission.html',
          controller: 'CloseSubmissionController',
          controllerAs: 'closeSubmissionCtrl',
          backdrop: 'static',
          keyboard: false
        });
      },

      /**Function to show modal for reopening a status */
      reopenTenderStatusModal: function (reopenTitle, reopenQuestion,
        statusToReopen) {
        return $uibModal.open({
          templateUrl: 'app/submission/reopenStatus/reopenStatus.html',
          controller: 'ReopenStatusController',
          controllerAs: 'reopenStatusCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            reopenTitle: function () {
              return reopenTitle;
            },
            reopenQuestion: function () {
              return reopenQuestion;
            },
            statusToReopen: function () {
              return statusToReopen;
            }
          }
        });
      },

      getTenderStatus: function () {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/enumerations/tenderStatus');
      },

      /** Function to format scores (3 decimals) */
      formatScore: function (score) {
        return $filter('number')(score, AppConstants.ROUND_DECIMALS.SCORE);
      },

      /**
       * Function to show Submittent (Tenderer) with its location
       */
      showSubmittentAndLocation: function (offer) {
        // text is going to contain the Submittent name and location
        return offer.submittent.companyId.companyName + ", " +
          offer.submittent.companyId.location;
      },

      /**
       * Function to show Joint Ventures and Subcontractors
       * of tenderer
       */
      showJointVenturesSubcontractors: function (offer) {
        var text = "";
        /** Show Joint Ventures */
        if (offer.submittent.jointVentures.length > 0) {
          text = text + " (ARGE: " +
            alphabeticalOrderText(offer.submittent.jointVentures);
        }
        /** Show Subcontractors */
        if (offer.submittent.subcontractors.length > 0) {
          if (offer.submittent.jointVentures.length > 0) { // Check
            // if
            // the
            // text
            // already
            // contains
            // joint
            // Ventures
            text = text + ", Sub. U: ";
          } else {
            text = text + " (Sub. U: ";
          }
          text = text +
            alphabeticalOrderText(offer.submittent.subcontractors);
        }

        function alphabeticalOrderText(companies) {
          var tempText = "";
          /* ordered companies by company name */
          companies = $filter('orderBy')(companies, 'companyName');
          for (var i = 0; i < companies.length; i++) {
            tempText = tempText + companies[i].companyName;
            if (i < companies.length - 1) {
              tempText = tempText + " / ";
            }
          }
          return tempText;
        }

        if (offer.submittent.jointVentures.length > 0 ||
          offer.submittent.subcontractors.length > 0) {
          text = text + ")";
        }
        return text;
      },

      /** Function to proceed without saving changes */
      proceedWithoutSaving: function () {
        return $uibModal.open({
          templateUrl: 'app/genericModals/proceedWithoutSaving.html',
          controller: 'GenericModalsController',
          controllerAs: 'genericModalsCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return null;
            },
            body: function () {
              return null;
            }
          }
        });
      },

      /** Function to show modal for transitioning to different state when unsaved changes are present*/
      transitionModal: function () {
        return $uibModal.open({
          templateUrl: 'app/genericModals/transitionModal.html',
          controller: 'GenericModalsController',
          controllerAs: 'genericModalsCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return null;
            },
            body: function () {
              return null;
            }
          }
        });
      },

      /** Function to show modal for canceling changes made to the form */
      cancelModal: function () {
        return $uibModal.open({
          templateUrl: 'app/genericModals/cancelModal.html',
          controller: 'GenericModalsController',
          controllerAs: 'genericModalsCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return null;
            },
            body: function () {
              return null;
            }
          }
        });
      },

      /** Function to show modal for closing the application opening */
      closeApplicationOpening: function () {
        return $uibModal.open({
          templateUrl: 'app/genericModals/applicationOpeningClose.html',
          controller: 'GenericModalsController',
          controllerAs: 'genericModalsCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return null;
            },
            body: function () {
              return null;
            }
          }
        });
      },

      /** Function to adjust table height so that the whole date pop-up is visible */
      adjustTableHeight: function (results, dateFilterOpened) {
        if ((results < 7) && dateFilterOpened) {
          return {
            "height": "380px"
          };
        } else {
          return null;
        }
      },

      /** Function to show modal informing the user about unsaved changes */
      informAboutUnsavedChanges: function () {
        return $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close"' +
            'ng-click="unsavedChangesCtrl.closeUnsavedChangesModal(); $close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Bitte speichern Sie Ihre Änderungen, bevor Sie weiter fortfahren. </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="unsavedChangesCtrl.closeUnsavedChangesModal(); $close()">OK</button>' +
            '</div>' + '</div>',
          controllerAs: 'unsavedChangesCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeUnsavedChangesModal = function () {
              return null;
            };
          }
        });
      },

      /** Function to implement the swiss number rounding standard ("Rappenrundung") without the thousand separator */
      customRoundNumberWithoutSeparator: function (num) {
        if (num === null || isNaN(num)) {
          return $filter('number')(0, 2);
        }
        var diff = $filter('number')(num - (Math.floor(num * 10) / 10), 6); //round diff to 6th decimal in order to keep 3rd decimal unchanged from rounding
        if (diff < 0.025) {
          num = Math.floor(num * 10) / 10;
        } else if (diff >= 0.025 && diff < 0.075) {
          num = (Math.floor(num * 10) / 10) + 0.05;
        } else {
          num = (Math.floor(num * 10) / 10) + 0.1;
        }
        num = $filter('number')(num, 2).replace(/’/g, ""); //number with two decimals, without thousand separator
        return num;
      },

      /** Function to change width of div element if subcontractors/jointVenture are present in each state accordingly */
      divRowStyle: function (companies) {
        if (companies) {
          return {
            "width": "450px"
          };
        }
        return null;
      },

      /** Function to change left margin of fieldset if process is selective (used for subcontractors/jointVenture states) */
      fieldsetStyle: function (process) {
        if (process === AppConstants.PROCESS.SELECTIVE) {
          return {
            "width": "460px",
            "margin-left": "30px"
          };
        }
        return null;
      },

      /** Function to display object information */
      getObjectInfo: function (object) {
        if (object) {
          if (object.value2) {
            return object.value1 + ", " + object.value2;
          }
          return object.value1;
        }
        return null;
      },

      /** Display modal informing the user about empty mandatory fields */
      mandatoryFieldsEmpty: function () {
        return $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close"' +
            'ng-click="emptyFieldsCtrl.closeModal(); $close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Sie müssen alle Pflichtfelder ausfüllen, um weiter fortfahren zu können. </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="emptyFieldsCtrl.closeModal(); $close()">OK</button>' +
            '</div>' +
            '</div>',
          controllerAs: 'emptyFieldsCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeModal = function () {
              return null;
            };
          }
        });
      },

      getTaskTypes: function () {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/enumerations/taskTypes');
      },

      openGenericModal: function (title, body, submessage) {
        return $uibModal.open({
          templateUrl: 'app/genericModals/genericModal.html',
          controller: 'GenericModalsController',
          controllerAs: 'genericModalsCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return title;
            },
            body: function () {
              return {
                message: body,
                submessage: submessage
              };
            }
          }
        });
      },

      /** Function to check if the offer editing is disabled. */
      offerEditingDisabled: function (submission) {
        if (submission) {
          return (!submission.secondDeadline ||
            !submission.offerOpeningDate || !submission.secondLoggedBy);
        }
        return true;
      },

      /** Ignore invalid number if the only character is plus or minus. */
      ignoreInvalidNumber: function (element) {
        if (element.$invalid) {
          return !(element.$viewValue === "+" || element.$viewValue === "-");
        }
        return false;
      },

      /** Function to get the submission status history. */
      getSubmissionStatuses: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/tenderStatusHistory/getSubmissionStatuses/' + submissionId);
      },

      /** Generic modal for email templates. */
      sendMailModal: function (id, companyIds) {
        $uibModal
          .open({
            templateUrl: 'app/submissionDefault/email/selectEmailTemplate.html',
            controller: 'SelectEmailTemplateController',
            controllerAs: 'selectEmailTemplateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
              id: function () {
                return id;
              },
              companyIds: function () {
                return companyIds;
              }
            }
          });
      },

      /** Function to assign value to current status. */
      assignCurrentStatus: function (statusHistory) {
        for (var i in statusHistory) {
          // Find most recent status that is not PROCEDURE_COMPLETED and PROCEDURE_CANCELED.
          if (statusHistory[i] !== AppConstants.STATUS.PROCEDURE_COMPLETED &&
            statusHistory[i] !== AppConstants.STATUS.PROCEDURE_CANCELED) {
            return statusHistory[i];
          }
        }
        return null;
      },

      /** Gets the SD category types. */
      getSDCategoryTypes: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/enumerations/getSDCategoryTypes');
      },

      /** Returns only active entries */
      showOnlyActive: function (data) {
        return $filter('filter')(data, {
          'active': true
        });
      },
      isValidFormat: function (ext) {
        var validFormats = ['docx', 'pdf', 'xlsx', 'csv', 'msg',
          'png', 'jpg',
          'jpeg', 'txt', 'rtf', 'htm', 'html', 'mpp', 'pptx'
        ];
        if (validFormats.indexOf(ext.toLowerCase()) === -1) {
          return false;
        }
        return true;
      },

      /** Function to inform the user that the country change will cause all the company proofs to be deleted. */
      countryChangeProofDeletion: function () {
        return $uibModal.open({
          templateUrl: 'app/genericModals/countryChangeProofDeletion.html',
          controller: 'GenericModalsController',
          controllerAs: 'genericModalsCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return null;
            },
            body: function () {
              return null;
            }
          }
        });
      },

      /** Add custom style to table (used to fix edge browser border issue) */
      customTableStyle: function () {
        return {
          "background": "transparent",
          "border-collapse": "separate"
        };
      },

      /** Function to get the name of the given master list type. */
      getNameOfMasterListType: function (type) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getNameOfMasterListType/' +
          type);
      },

      pingBackEnd: function () {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/ping');
      }
    };
  }
})();
