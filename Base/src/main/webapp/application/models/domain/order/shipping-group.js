/**
 * @module csa/base/application/models/domain/order/shipping-group
 * @alias module:shipping-group
 * @requires module:entity
 * @requires module:price-info
 */
define([
  'entity',
  'price-info'
], function (Entity, PriceInfo) {
  'use strict';

  function priceInfo () {
    return new PriceInfo();
  }

  /**
   * @class
   * @extends Entity
   */
  function ShippingGroup () {
    Entity.apply(this, arguments);
  }

  ShippingGroup.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends ShippingGroup
     */
    constructor: {
      value: ShippingGroup
    },

    /**
     * @property
     * @lends ShippingGroup
     */
    observables: {
      value: Entity.prototype.observables.concat([]),
    },

    /**
     * @property
     * @lends ShippingGroup
     */
    associations: {
      value: Entity.prototype.associations.concat([
        {
          name: 'priceInfo',
          factory: priceInfo
        }
      ]),
    }
  });

  return ShippingGroup;
});
