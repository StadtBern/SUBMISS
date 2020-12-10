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

(function () {
  "use strict";

  angular
    .module("submiss", [
      // SUBMISS modules.
      "submiss.auth",
      "submiss.dashboard",
      "submiss.project",
      "submiss.stammdaten",
      "submiss.company",
      "submiss.submission",
      "submiss.offer",
      //"submiss.subcontractor",
      "submiss.users",
      "submiss.examination",
      "submiss.award",
      "submiss.document",
      "submiss.submissionDefault",
      "submiss.report",
      "submiss.tasks",
      "submiss.audit",
      // QLACK modules.
      "QFormJSRValidation", // QLACK component to render validation errors in forms.
      // External modules.
      "ngCookies", // Cookies handling.
      "ngSanitize", // Sanitize HTML.
      "ui.select2", // UI Select2.
      "ui.select",
      "ui.router", // UI Router.
      "ui.bootstrap", // UI Bootstrap.
      "pascalprecht.translate", // Translations support.
      "flow", // File upload support.
      "ngAnimate", // Animation support.
      "toastr", // Shows popup messages.
      "dialogs.main", // Popup confirmation message.
      "ngTable", // Tables support.
      "ncy-angular-breadcrumb", // Breadcrumbs support.
      "ngLocale",
      "angular.filter"
    ])
    .directive('dfCurrency', function ($filter, $rootScope) {
      var THOUSANDS_SEP = "’";
      var link = function (scope, element, attrs, requiredControllers,
        transcludeFn) {
        var ngModel = requiredControllers.ngModel;
        var format = function (amount) {
          if (angular.isUndefined(amount) || amount === null) {
            amount = 0;
          }
          if (Math.abs(amount) >= 1000) {
            return $filter('number')(amount, 2).replace(/,/g, THOUSANDS_SEP);
          } else {
            return $filter('number')(amount, 2);
          }

        };

        var parse = function (formatted) {
          if (formatted !== undefined) {
            return formatted.replace(new RegExp(THOUSANDS_SEP, 'g'), '');
          }
        };

        if (ngModel) {

          ngModel.$formatters.unshift(function (numb) {
            return format(numb)
          });
          ngModel.$parsers.unshift(function (text) {
            return parse(text) || ''
          });

          var isValid = function (str) {
            str = str.replace('’', '');
            var val = str !== undefined && Number(parse(str));
            // Do not replace '==' with '===' in the following line of code,
            // as it breaks the directive.
            return str !== null && isFinite(val) && val.toFixed(2) == val;
          };

          element.on('change', function (event) {
            var val = element.val();
            if (isValid(val)) {
              element.val(format(parse(val)));
            }
          });

          ngModel.$validators.number = function (model, view) {
            if (model !== undefined) {
              return isValid(model.toString());
            } else {
              return undefined;
            }
          }
        } else if (attrs.ngBind) {
          element.scope()
            .$watch(attrs.ngBind, function (val) {
              if (val) {
                element.html(format(val));
              }
            });
        } else {
          attrs.$observe('value', function (val) {
            element.val(format(val));
          });
        }
      };
      return {
        restrict: 'A',
        require: {
          ngModel: '?ngModel'
        },
        scope: {},
        compile: function (element, attrs, transclude) {
          element.attr('autocomplete', 'off');
          element.attr('step', '0.01');
          return link;
        },
        link: link
      };
    }).directive('scrolly', function () {
      return {
        restrict: 'A',
        link: function (scope, element, attrs) {
          var raw = element[0];
          element.bind('scroll', function () {
            if (raw.scrollTop + raw.offsetHeight > raw.scrollHeight) {
              scope.$apply(attrs.scrolly);
            }
          });
        }
      };
    }).directive('loadingPane', function ($timeout, $window) {
      return {
        restrict: 'A',
        link: function (scope, element, attr) {
          var directiveId = 'loadingPane';
          var targetElement;
          var paneElement;
          var throttledPosition;

          function init(element) {
            targetElement = element;

            paneElement = angular.element('<div>');
            paneElement.addClass('loading-pane');

            if (attr['id']) {
              paneElement.attr('data-target-id', attr['id']);
            }

            var spinnerImage = angular.element('<div>');
            spinnerImage.addClass('spinner-image');
            spinnerImage.appendTo(paneElement);

            angular.element('body').append(paneElement);

            setZIndex();

            //reposition window after a while, just in case if:
            // - watched scope property will be set to true from the beginning
            // - and initial position of the target element will be shifted during page rendering
            $timeout(position, 100);
            $timeout(position, 200);
            $timeout(position, 300);

            angular.element($window).scroll(throttledPosition);
            angular.element($window).resize(throttledPosition);
          }

          function updateVisibility(isVisible) {
            if (isVisible) {
              show();
            } else {
              hide();
            }
          }

          function setZIndex() {
            var paneZIndex = 2000;
            paneElement.css('zIndex', paneZIndex).find('.spinner-image').css(
              'zIndex', paneZIndex + 1);
          }

          function position() {
            paneElement.css({
              'left': targetElement.offset().left,
              'top': targetElement.offset().top,
              'width': targetElement.outerWidth(),
              'height': targetElement.outerHeight()
            });
            // Add 'fixed' to 'position' property of spinner-image, so that the image follows the scrolling.
            paneElement.find('.spinner-image').css({
              'position': 'fixed'
            });
          }

          function show() {
            paneElement.show();
            position();
          }

          function hide() {
            paneElement.hide();
          }

          init(element);

          scope.$watch(attr[directiveId], function (newVal) {
            updateVisibility(newVal);
          });

          scope.$on('$destroy', function cleanup() {
            paneElement.remove();
            $(window).off('scroll', throttledPosition);
            $(window).off('resize', throttledPosition);
          });
        }
      };
    });

  //TODO this is not the right place for this...
  angular.module('submiss').filter('propsFilter', function () {
    return function (items, props) {
      var out = [];
      if (angular.isArray(items)) {
        var keys = Object.keys(props);
        items.forEach(function (item) {
          var itemMatches = false;
          for (var i = 0; i < keys.length; i++) {
            var prop = keys[i];
            var text = props[prop].toLowerCase();
            if (item[prop]!== null && item[prop].toString().toLowerCase().indexOf(text) !== -1) {
              itemMatches = true;
              break;
            }
          }
          if (itemMatches) {
            out.push(item);
          }
        });
      } else {
        // Let the output be the input untouched
        out = items;
      }
      return out;
    };
  });
})();
