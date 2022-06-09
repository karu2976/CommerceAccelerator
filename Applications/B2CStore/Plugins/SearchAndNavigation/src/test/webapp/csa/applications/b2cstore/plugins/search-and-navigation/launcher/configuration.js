define([
  'csa/applications/b2cstore/plugins/search-and-navigation/launcher/configuration'
], function () {
  'use strict';

  require.config({
    'map': {
      '*': {
        'cart-services': '/base/Applications/B2CStore/Plugins/SearchAndNavigation/build/compile/test/webapp/csa/applications/b2cstore/plugins/search-and-navigation/cart-services.js'
      }
    }
  });
});
