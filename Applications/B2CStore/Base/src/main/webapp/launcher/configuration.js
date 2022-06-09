define([
  'csa/base/launcher/configuration'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'page-header': 'csa/applications/b2cstore/base/application/components/page-header',
        // Models
        'metadata': 'csa/applications/b2cstore/base/application/models/view/metadata',
        'catalog-ref': 'csa/applications/b2cstore/base/application/models/domain/order/catalog-ref'
      }
    }
  });
});
