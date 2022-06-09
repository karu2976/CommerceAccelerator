// jshint browser: true

/**
 * @module csa/base/launcher/shims/browser/location
 */
define(function () {
  'use strict';

  // Shim location.origin (for IE an IOS)
  if (!location.origin) {
    location.origin = location.protocol + '//' + location.host;
  }
});
