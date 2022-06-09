/**
 * @module csa/base/application/models/domain/commerce-item-array
 * @alias module:commerce-item-array
 * @requires module:entity-array
 * @requires module:commerce-item
 */
define([
  'entity-array',
  'commerce-item'
], function (entityArray, CommerceItem) {
  'use strict';

  var commerceItemArrayMixinDescriptor = {
    Model: {
      value: CommerceItem,
      configurable: true
    }
  };

  return function contentItemArray () {
    return Object.defineProperties(entityArray(), commerceItemArrayMixinDescriptor);
  };
});
