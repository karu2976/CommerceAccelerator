define([
  'csa/plugins/search-and-navigation/launcher/configuration'
], function () {
  'use strict';

  // Test configuration.
  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        'cart-services': '/base/Plugins/SearchAndNavigation/build/compile/test/webapp/csa/plugins/search-and-navigation/cart-services.js'
      }
    }
  });
});
