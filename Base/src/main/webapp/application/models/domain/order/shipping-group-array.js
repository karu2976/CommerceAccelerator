/**
 * @module csa/base/application/models/domain/shipping-group-array
 * @alias module:shipping-group-array
 * @requires module:entity-array
 * @requires module:shipping-group
 */
define([
  'entity-array',
  'shipping-group'
], function (entityArray, ShippingGroup) {
  'use strict';

  var shippingGroupArrayMixinDescriptor = {
    Model: {
      value: ShippingGroup,
      configurable: true
    }
  };

  return function contentItemArray () {
    return Object.defineProperties(entityArray(), shippingGroupArrayMixinDescriptor);
  };
});
