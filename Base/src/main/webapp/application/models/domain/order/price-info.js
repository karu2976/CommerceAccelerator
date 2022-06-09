/**
 * @module csa/base/application/models/domain/order/price-info
 * @alias module:price-info
 * @requires module:entity
 */
define([
  'entity'
], function (Entity) {
  'use strict';

  /**
   * @class
   * @extends Entity
   */
  function PriceInfo () {
    Entity.apply(this, arguments);
  }

  PriceInfo.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends PriceInfo
     */
    constructor: {
      value: PriceInfo
    },

    /**
     * @property
     * @lends PriceInfo
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'amount',
        'currencyCode',
        'discountAmount',
        'listPrice',
        'onSale',
        'quantityDiscounted',
        'rawSubtotal',
        'rawTotalPrice',
        'salePrice',
        'shipping',
        'tax',
        'total'
      ])
    },

    /**
     * @property
     * @lends PriceInfo
     */
    observableArrays: {
      value: Entity.prototype.observableArrays.concat([
        'adjustments'
      ])
    }
  });

  return PriceInfo;
});
