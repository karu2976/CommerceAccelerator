/**
 * @module csa/base/launcher/shims/es/es
 * @requires module:es6-promise
 * @requires module:Intl
 */
define([
  'es6-promise',
  'Intl'
], function (es6Promise) {
  'use strict';

  // Initialize the es6 Promise polyfill
  es6Promise.polyfill();
});
