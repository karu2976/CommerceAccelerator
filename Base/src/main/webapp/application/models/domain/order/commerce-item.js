/**
 * @module csa/base/application/models/domain/order/commerce-item
 * @alias module:commerce-item
 * @requires module:entity
 * @requires module:inventory-info
 * @requires module:price-info
 */
define([
  'entity',
  'auxiliary-data',
  'inventory-info',
  'price-info'
], function (Entity, AuxiliaryData, InventoryInfo, PriceInfo) {
  'use strict';

  function auxiliaryData () {
    return new AuxiliaryData();
  }

  function inventoryInfo () {
    return new InventoryInfo();
  }

  function priceInfo () {
    return new PriceInfo();
  }

  /**
   * @class
   * @extends Entity
   */
  function CommerceItem () {
    Entity.apply(this, arguments);
  }

  CommerceItem.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends CommerceItem
     */
    constructor: {
      value: CommerceItem
    },

    /**
     * @property
     * @lends CommerceItem
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'catalogRefId',
        'quantity',
        'productDisplayName',
        'productId'
     ])
    },

    /**
     * @property
     * @lends CommerceItem
     */
    associations: {
      value: Entity.prototype.associations.concat([
        {
          name: 'auxiliaryData',
          factory: auxiliaryData
        },
        {
          name: 'inventoryInfo',
          factory: inventoryInfo
        },
        {
          name: 'priceInfo',
          factory: priceInfo
        }
      ])
    }
  });

  return CommerceItem;
});
