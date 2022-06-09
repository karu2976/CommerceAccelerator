// jshint browser: true

/**
 * @module csa/base/launcher/shims/browser/document
 */
define(function () {
  'use strict';

  // Shim document.baseURI for browsers that don't support it (IE)
  // @see http://www.w3schools.com/jsref/prop_doc_baseuri.asp
  if (!document.baseURI) {
    // Find the <base> tag
    var base = document.querySelector('base');

    if (base) {
      // If it exits, use it's href attribute value as the baseURI.
      document.baseURI = base.href;
    }
  }
});
