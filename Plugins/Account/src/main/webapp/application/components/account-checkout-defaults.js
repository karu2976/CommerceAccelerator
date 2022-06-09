/**
 * Render for the AccountCheckoutDefaults cartridge. Allows a user to set default options for
 * default shipping address, payment method and shipping method.
 *
 * @module csa/plugins/account/application/components/account-checkout-defaults
 * @requires module:browser
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./account-checkout-defaults.html
 * @requires module:profile-services
 */
define([
  'browser',
  'libs',
  'content-item',
  'text!./account-checkout-defaults.html',
  'profile-services'
], function (browser, libs, ContentItem, template, profileServices) {
  'use strict';

  var history = browser.history;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function AccountCheckoutDefaults() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  AccountCheckoutDefaults.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AccountCheckoutDefaults
     */
    constructor: {
      value: AccountCheckoutDefaults
    },

    /**
     * @property
     * @lends AccountCheckoutDefaults
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'defaultCarrier',
        'defaultCreditCard',
        'shippingAddress'
      ])
    },

    /**
     * @property
     * @lends AccountCheckoutDefaults
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'allCards',
        'allAddresses',
        'allCarriers'
      ])
    },

    /**
     * Display the correct options caption depending on whether or not the options array has items.
     */
    optionsCaptionText: {
      value: function optionsCaptionText (options) {
        if (options && options.length === 0) {
          return i18next.t('account.text.noneAvailable');
        }
        return i18next.t('account.text.selectDefault');
      }
    },

    /**
     * Updates the checkout defaults. The values to set are read from the view model.
     */
    updateCheckoutDefaults: {
      value: function updateCheckoutDefaults () {
        return profileServices.updateCheckoutDefaults(this.defaultCarrier(),
          this.shippingAddress(), this.defaultCreditCard()).then(function (response) {
          if (!response.formError) {
            history.pushState(null, null, 'account', true);
            toastr.success(i18next.t('account.alert.checkoutDefaultsUpdated'));
          }
        });
      }
    },

    /**
     * Cancel editing the checkout defaults.
     */
    cancel: {
      value: function cancel () {
        history.pushState(null, null, 'account', true);
      }
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new AccountCheckoutDefaults().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AccountCheckoutDefaults: AccountCheckoutDefaults
  };
});
