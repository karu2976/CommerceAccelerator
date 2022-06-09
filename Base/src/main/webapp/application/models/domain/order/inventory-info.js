/**
 * @module csa/base/application/models/domain/order/inventory-info
 * @alias module:inventory-info
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
  function InventoryInfo () {
    Entity.apply(this, arguments);
  }

  InventoryInfo.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends InventoryInfo
     */
    constructor: {
      value: InventoryInfo
    },

    /**
     * @property
     * @lends InventoryInfo
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'stockThreshold',
        'availabilityStatus',
        'availabilityStatusMsg',
        'stockLevel'
      ])
    }
  });

  return InventoryInfo;
});
