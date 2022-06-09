define([
  'csa/base/launcher/configuration',
  'i18n!csa/plugins/shopping-cart/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'cart-editor': 'csa/plugins/shopping-cart/application/components/cart-editor',
        'cart-link': 'csa/plugins/shopping-cart/application/components/cart-link',
        'order-summary': 'csa/plugins/shopping-cart/application/components/order-summary',
        // Services
        'oc-rest-add-item-to-order': 'csa/plugins/shopping-cart/application/services/web/oc-rest/add-item-to-order',
        'oc-rest-get-order-summary': 'csa/plugins/shopping-cart/application/services/web/oc-rest/get-order-summary',
        'oc-rest-remove-item-from-order': 'csa/plugins/shopping-cart/application/services/web/oc-rest/remove-item-from-order',
        'oc-rest-set-order': 'csa/plugins/shopping-cart/application/services/web/oc-rest/set-order'
      }
    }
  });

  return new Promise (function (resolve) {
    require([
      'libs',
      'cartridge-to-component-lookup',
      'oc-rest-get-order-summary'
    ], function initialize (libs, cartridgeToComponentLookup, getOrderSummary) {

      var ko = libs.ko;

      // Register components.
      ko.components.register('cart-editor', {require: 'cart-editor'});
      ko.components.register('cart-link', {require: 'cart-link'});

      // Set cartridge to component mappings.
      cartridgeToComponentLookup.CartEditor = 'cart-editor';
      cartridgeToComponentLookup.CartLink = 'cart-link';

      if (!ko.components.isRegistered('order-summary')) {
        ko.components.register('order-summary', {require: 'order-summary'});
        cartridgeToComponentLookup.OrderSummary = 'order-summary';
      }

      // Fetch the shopping cart data.
      getOrderSummary();

      resolve();
    });
  });
});
