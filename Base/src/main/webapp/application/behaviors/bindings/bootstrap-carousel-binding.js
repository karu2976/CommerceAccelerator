/**
 * @module csa/base/application/behaviors/bindings/bootstrap-carousel-binding
 * @alias module:bootstrap-carousel-binding
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var $ = libs.$;
  var ko = libs.ko;

  ko.bindingHandlers.bootstrapCarousel = {
    update: function (element, valueAccessor) {

      // Unwrap the value passed to us to create a dependency in this binding on the observable.
      var value = ko.utils.unwrapObservable(valueAccessor());

      // Slide the carousel to the current value of the observable.
      $(element).closest('.carousel').carousel(value);
    }
  };
});
