/**
 * @module csa/base/application/behaviors/bindings/reset-binding
 * @alias module:reset-binding
 * @requires module:browser
 * @requires module:libs
 */
define([
  'browser',
  'libs'
], function (browser, libs) {
  'use strict';

  var window = browser.window;
  var $ = libs.$;
  var ko = libs.ko;

  ko.bindingHandlers.reset = {
    init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
      if (typeof valueAccessor() !== 'function') {
        throw new Error('The value for a reset binding must be a function');
      }

      $(element).on('reset', function (event) {
        var handlerReturnValue;
        var value = valueAccessor();
        try {
          handlerReturnValue = value.call(bindingContext.$data, element);
        }
        finally {
          if (handlerReturnValue !== true) { // Normally we want to prevent default action. Developer can override this be explicitly returning true.
            if (event.preventDefault) {
              event.preventDefault();
            }
            else {
              event.returnValue = false;
            }
          }
        }
      });

      $(element).on('change', function () {
        var $form = $(this);
        if (!$form.hasClass('dirty')) {
          $form.addClass('dirty');

          $(window).one('changestate', function () {
            $form.trigger('reset');
          });
        }
      });
    }
  };
});
