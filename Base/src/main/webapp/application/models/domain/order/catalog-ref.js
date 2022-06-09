/**
 * @module csa/base/application/models/domain/order/catalog-ref
 * @alias module:catalog-ref
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
  function CatalogRef () {
    Entity.apply(this, arguments);
  }

  CatalogRef.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends CatalogRef
     */
    constructor: {
      value: CatalogRef
    },

    /**
     * @property
     * @lends CatalogRef
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'displayName',
        'repositoryId',
        'type'
      ])
    }
  });

  return CatalogRef;
});
