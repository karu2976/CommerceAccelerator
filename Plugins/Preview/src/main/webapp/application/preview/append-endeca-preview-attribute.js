/**
 * This script appends 'data-oc-audit-info' attribute to the given element
 * and attaches handlers where it finds this attribute.
 *
 * @module csa/plugins/preview/application/preview/append-endeca-preview-attribute
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var ko = libs.ko;

  return function appendEndecaPreviewAttribute (viewModel, element, framework) {

    // Get the endeca audit info.
    var info = viewModel['endeca:auditInfo'];

    // If its available then proceed with adding 'data-oc-audit-info' attribute.
    if (info) {
      // Add 'data-oc-audit-info' attribute to it.
      framework.addContentItemId(element, ko.toJS(viewModel));

      // Also add hotspots.
      framework.addHotspots(element, false);
    }
  };
});
