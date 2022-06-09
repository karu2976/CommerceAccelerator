/**
 * @module csa/base/application/models/domain/order/product-ref
 * @alias module:product-ref
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
  function ProductRef () {
    Entity.apply(this, arguments);
  }

  ProductRef.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends ProductRef
     */
    constructor: {
      value: ProductRef
    },

    /**
     * @property
     * @lends ProductRef
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'description',
        'displayName',
        'largeImageUrl',
        'longDescription',
        'repositoryId',
        'thumbnailImageUrl'
      ])
    }
  });

  return ProductRef;
});
