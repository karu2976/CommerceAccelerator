define([
  'csa/plugins/account/launcher/configuration',
  'i18n!csa/applications/b2cstore/plugins/account/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'account-order-detail': 'csa/applications/b2cstore/plugins/account/application/components/account-order-detail'
      }
    }
  });
});
