define([
  'csa/base/launcher/configuration',
  'i18n!csa/plugins/checkout/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        'billing-address-selector': 'csa/plugins/checkout/application/components/billing-address-selector',
        'checkout': 'csa/plugins/checkout/application/components/checkout',
        'checkout-details': 'csa/plugins/checkout/application/models/view/checkout-details',
        'checkout-login': 'csa/plugins/checkout/application/components/checkout-login',
        'order-confirmation': 'csa/plugins/checkout/application/components/order-confirmation',
        'payment-method-selector': 'csa/plugins/checkout/application/components/payment-method-selector',
        'shipping-address-selector': 'csa/plugins/checkout/application/components/shipping-address-selector',
        'shipping-method-selector': 'csa/plugins/checkout/application/components/shipping-method-selector',
        'checkout-cart-summary': 'csa/plugins/checkout/application/components/checkout-cart-summary',
        // services
        'checkout-services': 'csa/plugins/checkout/application/services/web/checkout-services'
      }
    }
  });

  return new Promise(function initialize (resolve) {
    require([
      'libs',
      'cartridge-to-component-lookup'
    ], function (libs, cartridgeToComponentLookup) {

      var ko = libs.ko;

      // Register components.
      ko.components.register('checkout', {require: 'checkout'});
      ko.components.register('checkout-login', {require: 'checkout-login'});
      ko.components.register('shipping-address-selector', {require: 'shipping-address-selector'});
      ko.components.register('shipping-method-selector', {require: 'shipping-method-selector'});
      ko.components.register('payment-method-selector', {require: 'payment-method-selector'});
      ko.components.register('billing-address-selector', {require: 'billing-address-selector'});
      ko.components.register('order-confirmation', {require: 'order-confirmation'});
      ko.components.register('checkout-cart-summary', {require: 'checkout-cart-summary'});

      // Set cartridge to component mappings.
      cartridgeToComponentLookup.Checkout = 'checkout';
      cartridgeToComponentLookup.CheckoutLogin = 'checkout-login';
      cartridgeToComponentLookup.ShippingAddressSelector = 'shipping-address-selector';
      cartridgeToComponentLookup.ShippingMethodSelector = 'shipping-method-selector';
      cartridgeToComponentLookup.PaymentMethodSelector = 'payment-method-selector';
      cartridgeToComponentLookup.BillingAddressSelector = 'billing-address-selector';
      cartridgeToComponentLookup.OrderConfirmation = 'order-confirmation';
      cartridgeToComponentLookup.CheckoutCartSummary = 'checkout-cart-summary';

      resolve();
    });
  });
});
