/**
 * @module csa/base/application/behaviors/bindings/unique-for-binding
 * @alias module:unique-for-binding
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var ko = libs.ko;

  ko.bindingHandlers.uniqueFor = {

    /**
     * description...
     *
     * @function
     *   @param {Element} element - The DOM element, to which, the binding is being applied.
     *   @param {Function} valueAccessor - The value accessor.
     *   @param {Object} allBindings - The allBinding accessor.
     *   @param {Object} viewModel - [deprecated] The current view model.
     *   @param {Object} bindingContext - Holds the binding context available to this element’s bindings
     */
    init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {

      var cid = ko.unwrap(bindingContext.$data.cid || bindingContext.$parent.cid);

      if (!cid) {
        throw new Error('A value for "cid" was not found in the bindingContext.');
      }

      var suffix = ko.unwrap(valueAccessor());

      if (!suffix) {
        throw new Error('A value for "suffix" must be supplied for the uniqueFor binding.');
      }

      var uniqueId = cid + '-' + suffix;

      if (bindingContext.$index()) {
        // inside a for loop so append index value
        uniqueId += '-' + bindingContext.$index();
      }

      element.setAttribute('for', uniqueId);
    }
  };
});
