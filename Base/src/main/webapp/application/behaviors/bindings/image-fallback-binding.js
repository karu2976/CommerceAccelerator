/**
 * @module csa/base/application/behaviors/bindings/image-fallback-binding
 * @alias module:image-fallback-binding
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

  /**
   * Custom binding that overrides the 'img' tag to include an extra 'fallback' attribute
   * which in the case of an error (an image can't be found), a fallback image can be used.
   * In the case that the fallback image also can't be found, the JQuery 'one' function
   * is used to ensure that the img error handler only executes once and doesn't get stuck
   * in an infinite loop.
   */
  ko.bindingHandlers.img = {
    init: function (element, valueAccessor) {
      var $element = $(element);

      // Hook up error handling that will unwrap and set the fallback value.
      $element.one('error', function () {
        var value = ko.utils.unwrapObservable(valueAccessor());
        var fallback = ko.utils.unwrapObservable(value.fallback);

        $element.attr('src', fallback);
      });
    },
    update: function (element, valueAccessor) {

      // Unwrap observables.
      var value = ko.utils.unwrapObservable(valueAccessor());
      var src = ko.utils.unwrapObservable(value.src);
      var fallback = ko.utils.unwrapObservable(value.fallback);
      var $element = $(element);

      // Set the src attribute to either the bound or fallback value.
      if (src) {
        $element.attr('src', src);
      }
      else {
        $element.attr('src', fallback);
      }

      // When the img 'srcset' attribute has been set, we need to invoke the picturefill() function
      // when it exists on the window object. This is required for browsers such as Firefox and
      // Internet Explorer, as by default, the picturefill function is being invoked before the
      // actual Knockout bindings have finished initializing.
      if (typeof window.picturefill === 'function') {
        window.picturefill();
      }
    }
  };
});
