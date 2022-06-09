define([
  'csa/plugins/search-and-navigation/launcher/configuration',
  'i18n!csa/applications/b2cstore/plugins/search-and-navigation/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'product-detail': 'csa/applications/b2cstore/plugins/search-and-navigation/application/components/product-detail',
        'refinement-menu': 'csa/applications/b2cstore/plugins/search-and-navigation/application/components/refinement-menu',
        'SizeChart': 'csa/applications/b2cstore/plugins/search-and-navigation/application/components/SizeChart',
        'sku-selector': 'csa/applications/b2cstore/plugins/search-and-navigation/application/components/sku-selector'
      }
    }
  });
});
