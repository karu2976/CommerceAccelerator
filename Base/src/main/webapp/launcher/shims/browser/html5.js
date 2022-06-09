// jshint browser: true

/**
 * @module csa/base/launcher/shims/browser/html5
 * @requires module:polyfiller
 * @requires module:url
 */
define([
  'underscore',
  'jquery',
  'polyfiller',
  'url'
], function (_, $, polyfiller) {
  'use strict';

  // Set the language
  polyfiller.activeLang(sessionStorage.getItem('language'));

  // Configure polyfiller
  polyfiller.setOptions({
    'basePath': '/csa/base/bower_components/webshim/js-webshim/minified/shims/',

    'waitReady': false,

    'forms': {
      addValidators: true,
      customMessages: true,
      replaceValidationUI: true,
      allValidators: true,
      iVal: {
        // General iVal cfg
        sel: '.ws-validate',
        handleBubble: 'hide',

        // Bootstrap specific classes
        fieldWrapper: '.form-group',
        errorMessageClass: 'help-block',
        successWrapperClass: '',
        errorWrapperClass: 'has-error has-feedback',

        //Events to check for validity/update validation UI
        'events': 'change'
      }
    },

    'forms-ext': {
      replaceUI: false
    }
  });

  // (Async) Load polyfills
  polyfiller.polyfill('forms forms-ext picture');

  window.requestIdleCallback = window.requestIdleCallback || function (cb) {
    var start = Date.now();
    return setTimeout(function () {
      cb({
        didTimeout: false,
        timeRemaining: function () {
          return Math.max(0, 50 - (Date.now() - start));
        }
      });
    }, 1);
  };

  window.cancelIdleCallback = window.cancelIdleCallback || function (id) {
    clearTimeout(id);
  };

  new MutationObserver(_.debounce(function () {
    window.requestIdleCallback(function () {
      $(document.body).updatePolyfill();
    });
  }, 500)).observe(document.body, {
    attributes: false,
    childList: true,
    characterData: false,
    subtree: true
  });

  new MutationObserver(_.debounce(function () {
    window.renderComplete = true;
  }, 2500)).observe(document.body, {
    attributes: false,
    childList: true,
    characterData: false,
    subtree: true
  });
});
