define([
  'csa/base/launcher/configuration',
  'i18n!csa/plugins/account/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'account-change-password': 'csa/plugins/account/application/components/account-change-password',
        'account-checkout-defaults': 'csa/plugins/account/application/components/account-checkout-defaults',
        'account-menu-navigation': 'csa/plugins/account/application/components/account-menu-navigation',
        'account-menu': 'csa/plugins/account/application/components/account-menu',
        'account-order-detail': 'csa/plugins/account/application/components/account-order-detail',
        'account-order-history': 'csa/plugins/account/application/components/account-order-history',
        'account-personal-information': 'csa/plugins/account/application/components/account-personal-information',
        'add-address': 'csa/plugins/account/application/components/add-address',
        'add-credit-card': 'csa/plugins/account/application/components/add-credit-card',
        'edit-address': 'csa/plugins/account/application/components/edit-address',
        'edit-credit-card': 'csa/plugins/account/application/components/edit-credit-card',
        'forgot-password': 'csa/plugins/account/application/components/forgot-password',
        'login': 'csa/plugins/account/application/components/login',
        'register-user': 'csa/plugins/account/application/components/register-user',
        'view-address-book': 'csa/plugins/account/application/components/view-address-book',
        'view-payment-information': 'csa/plugins/account/application/components/view-payment-information',
        // Services
        'profile-services': 'csa/plugins/account/application/services/web/profile-services',
        'location-services': 'csa/plugins/account/application/services/web/location-services'
      }
    }
  });

  return new Promise(function initialize (resolve) {
    require([
      'browser',
      'libs',
      'profile-services',
      'cartridge-to-component-lookup'
    ], function (browser, libs, profileServices, cartridgeToComponentLookup) {

      var window = browser;
      var ko = libs.ko;
      var $ = libs.$;

      // Register components.
      ko.components.register('account-change-password', {require: 'account-change-password'});
      ko.components.register('account-checkout-defaults', {require: 'account-checkout-defaults'});
      ko.components.register('account-menu-navigation', {require: 'account-menu-navigation'});
      ko.components.register('account-menu', {require: 'account-menu'});
      ko.components.register('account-order-detail', {require: 'account-order-detail'});
      ko.components.register('account-order-history', {require: 'account-order-history'});
      ko.components.register('account-personal-information', {require: 'account-personal-information'});
      ko.components.register('add-address', {require: 'add-address'});
      ko.components.register('add-credit-card', {require: 'add-credit-card'});
      ko.components.register('edit-address', {require: 'edit-address'});
      ko.components.register('edit-credit-card', {require: 'edit-credit-card'});
      ko.components.register('forgot-password', {require: 'forgot-password'});
      ko.components.register('login', {require: 'login'});
      ko.components.register('register-user', {require: 'register-user'});
      ko.components.register('view-address-book', {require: 'view-address-book'});
      ko.components.register('view-payment-information', {require: 'view-payment-information'});

      // Set cartridge to component mappings.
      cartridgeToComponentLookup.AccountChangePassword = 'account-change-password';
      cartridgeToComponentLookup.AccountCheckoutDefaults = 'account-checkout-defaults';
      cartridgeToComponentLookup.AccountMenuNavigation = 'account-menu-navigation';
      cartridgeToComponentLookup.AccountMenu = 'account-menu';
      cartridgeToComponentLookup.AccountOrderHistory = 'account-order-history';
      cartridgeToComponentLookup.AccountOrderDetail = 'account-order-detail';
      cartridgeToComponentLookup.AccountPersonalInformation = 'account-personal-information';
      cartridgeToComponentLookup.AddAddress = 'add-address';
      cartridgeToComponentLookup.AddCreditCard = 'add-credit-card';
      cartridgeToComponentLookup.EditAddress = 'edit-address';
      cartridgeToComponentLookup.EditCreditCard = 'edit-credit-card';
      cartridgeToComponentLookup.ForgotPassword = 'forgot-password';
      cartridgeToComponentLookup.Login = 'login';
      cartridgeToComponentLookup.RegisterUser = 'register-user';
      cartridgeToComponentLookup.ViewAddressBook = 'view-address-book';
      cartridgeToComponentLookup.ViewPaymentInformation = 'view-payment-information';

      // Initialize the user's session state.
      profileServices.init();

      // When the user requests a new page.
      $(window).on('pushstate', function () {

        // Refresh the session state.
        profileServices.init();
      });

      resolve();
    });
  });
});
