/**
 * @module csa/base/application/models/domain/order/product-ref
 * @alias module:product-ref
 * @requires module:entity
 * @requires module:catalog-ref
 * @requires module:product-ref
 */
define([
  'entity',
  'catalog-ref',
  'product-ref'
], function (Entity, CatalogRef, ProductRef) {
  'use strict';

  function catalogRef () {
    return new CatalogRef();
  }

  function productRef () {
    return new ProductRef();
  }

  /**
   * @class
   * @extends Entity
   */
  function AuxiliaryData () {
    Entity.apply(this, arguments);
  }

  AuxiliaryData.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends AuxiliaryData
     */
    constructor: {
      value: AuxiliaryData
    },

    /**
     * @property
     * @lends AuxiliaryData
     */
    associations: {
      value: Entity.prototype.associations.concat([
        {
          name: 'catalogRef',
          factory: catalogRef
        },
        {
          name: 'productRef',
          factory: productRef
        }
      ])
    }
  });

  return AuxiliaryData;
});
