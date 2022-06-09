define([
  'csa/plugins/checkout/launcher/configuration',
  'i18n!csa/applications/b2cstore/plugins/checkout/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'checkout-cart-summary': 'csa/applications/b2cstore/plugins/checkout/application/components/checkout-cart-summary'
      }
    }
  });
});
