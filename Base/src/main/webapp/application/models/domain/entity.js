/**
 * @module csa/base/application/models/domain/entity
 * @alias module:entity
 * @requires module:model
 */
define([
  'model'
], function (Model) {
  'use strict';

  /**
   * @class
   */
  function Entity () {
    Model.apply(this, arguments);
  }

  Entity.prototype = Object.create(Model.prototype, {

    /**
     * @property
     * @lends Entity
     */
    constructor: {
      value: Entity
    },

    /**
     * @property
     * @lends Entity
     */
    entityName: {
      value: null
    }
  });

  return Entity;
});
