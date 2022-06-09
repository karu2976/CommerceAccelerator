/**
 * @module csa/base/application/behaviors/bindings/component-binding-preprocessor
 * @alias module:component-binding-preprocessor
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var ko = libs.ko;

  // Ensure that id and class attributes are set for each component.
  ko.punches.utils.setBindingPreprocessor(ko.bindingHandlers.component, function (value, name, addBinding) {

    // Set a unique id on the component root element.
    addBinding('attr', '{id: $data.cid}');

    // Set semantic classes on the component root element.
    addBinding('css', '$data.classes');

    return value || ' ';
  });
});
