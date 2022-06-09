/**
 * @module csa/base/application/behaviors/bindings/recheck-validity-binding
 * @alias module:recheck-validity-binding
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var $ = libs.$;
  var ko = libs.ko;

  ko.bindingHandlers.recheckValidity = {
    update: function (element, valueAccessor) {

      var value = valueAccessor();

      if (ko.unwrap(value) !== true) {
        return;
      }

      // Re-check the validation for required fields in the form
      var inputs = $(element).find(':input');
      inputs.each(function () {
        if (this.required) {
          $(this).trigger('updatevalidation');
        }
      });

      // Reset to false
      value(false);
    }
  };
});
