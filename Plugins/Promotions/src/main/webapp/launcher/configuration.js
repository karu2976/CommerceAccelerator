define([
  'csa/base/launcher/configuration',
  'i18n!csa/plugins/promotions/application/locales'
], function () {
  'use strict';

  // Define local configuration
  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        //Components
        'coupon-editor': 'csa/plugins/promotions/application/components/coupon-editor',
        // Services
        'oc-rest-claim-coupon': 'csa/plugins/promotions/application/services/web/oc-rest/claim-coupon',
        'oc-rest-remove-coupon': 'csa/plugins/promotions/application/services/web/oc-rest/remove-coupon'
      }
    }
  });

  return new Promise (function (resolve) {
    require([
      'libs',
      'cartridge-to-component-lookup'
    ], function initialize (libs, cartridgeToComponentLookup) {

      var ko = libs.ko;

      // Register components.
      ko.components.register('coupon-editor', {require: 'coupon-editor'});

      // Set cartridge to component mappings.
      cartridgeToComponentLookup.CouponEditor = 'coupon-editor';

      resolve();
    });
  });
});
