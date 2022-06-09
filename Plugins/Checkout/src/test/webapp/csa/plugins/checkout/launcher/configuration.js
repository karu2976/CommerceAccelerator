define([
  'csa/plugins/checkout/launcher/configuration'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        'profile-services': '/base/Plugins/Checkout/build/compile/test/webapp/csa/plugins/checkout/profile-services.js'
      }
    }
  });
});
