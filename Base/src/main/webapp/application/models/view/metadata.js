/**
 * The view model for page metadata.
 *
 * @module csa/base/application/models/view/metadata
 * @alias module:metadata
 * @requires module:model
 */
define([
  'model',
], function (Model) {
  'use strict';

  /**
   * @class
   * @extends Model
   */
  function Metadata () {
    Model.apply(this, arguments);
  }

  Metadata.prototype = Object.create(Model.prototype, {

    /**
     * @property
     * @lends Metadata
     */
    constructor: {
      value: Metadata
    },

    /**
     * @property
     * @lends Metadata
     */
    observables: {
      value: Model.prototype.observables.concat([
        'canonical',
        'description',
        'favicon',
        'keywords',
        'title'
      ])
    }
  });

  return new Metadata();
});
