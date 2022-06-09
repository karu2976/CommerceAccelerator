/**
 * Render for Product Detail Page.  Extends
 * csa/base/application/models/domain/order/catalog-ref adding additional pbservables
 * for handling size, color and wood finish properties.
 *
 * @module csa/applications/b2cstore/base/application/models/domain/order/catalog-ref
 * @alias module:catalog-ref
 * @requires module:csa/base/application/models/domain/order/catalog-ref
 */
define([
  'csa/base/application/models/domain/order/catalog-ref'
], function (CatalogRef) {
  'use strict';

  /**
   * @class
   * @extends Entity
   */
  function B2CStoreCatalogRef () {
    CatalogRef.apply(this, arguments);
  }

  B2CStoreCatalogRef.prototype = Object.create(CatalogRef.prototype, {

    /**
     * @property
     * @lends CatalogRef
     */
    observables: {
      value: CatalogRef.prototype.observables.concat([
        'size',
        'color',
        'woodFinish'
      ])
    }
  });

  // The component defintion.
  return B2CStoreCatalogRef;
});
