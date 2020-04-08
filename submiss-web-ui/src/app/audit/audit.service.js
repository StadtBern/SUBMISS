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
 * @name audit.service.js
 * @description AuditService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.audit").factory("AuditService", AuditService);

  /** @ngInject */
  function AuditService($http, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Local functions.
     **********************************************************************/

    /***********************************************************************
     * Exported functions and variables.
     **********************************************************************/
    return {
      getAuditLogs: function (page, pageItems, sortColumn, sortType, levelIdOption, filter) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          AppConstants.URLS.AUDIT_LOGS + page + '/' + pageItems + '/' + sortColumn + '/' + sortType + '/' + levelIdOption, filter);
      },
      loadAuditLogs: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/audit/loadAuditLogs');
      }
    };
  }
})();
