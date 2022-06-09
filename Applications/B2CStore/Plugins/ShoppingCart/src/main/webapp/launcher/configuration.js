define([
  'csa/plugins/shopping-cart/launcher/configuration',
  'i18n!csa/applications/b2cstore/plugins/shopping-cart/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'cart-editor': 'csa/applications/b2cstore/plugins/shopping-cart/application/components/cart-editor'
      }
    }
  });
});
