/**
 * @module csa/base/application/models/domain/catalog/catalog
 * @alias module:catalog
 * @requires module:entity
 * @requires module:oc-rest-datasource
 */
define([
  'entity',
  'oc-rest-datasource'
], function (Entity, Datasource) {
  'use strict';

  /**
   * @class
   */
  function Catalog () {
    Entity.apply(this, arguments);

    // Define the model datasource.
    this.datasource = new Datasource(this);
  }

  Catalog.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends Catalog
     */
    constructor: {
      value: Catalog
    },

    /**
     * @property
     * @lends Profile
     */
    entityName: {
      value: 'catalog'
    },

    /**
     * @property
     * @lends Order
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'product',
        'selectedSku'
      ]),
    }
  });

  return new Catalog();
});
