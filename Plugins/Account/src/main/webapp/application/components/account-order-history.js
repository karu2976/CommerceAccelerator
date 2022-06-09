/**
 * Render for the AccountOrderHistory cartridge. Displays a list of orders associated with the user.
 *
 * @module csa/plugins/account/application/components/account-order-history
 * @requires module:content-item
 * @requires module:text!./account-order-history.html
 */
define([
  'content-item',
  'text!./account-order-history.html',
  'profile'
], function (ContentItem, template, profile) {
  'use strict';

  function AccountOrderHistory() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  AccountOrderHistory.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AccountOrderHistory
     */
    constructor: {
      value: AccountOrderHistory
    },

    /**
     * @property
     * @lends AccountOrderHistory
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'myOrders'
      ])
    },

    /**
     * @property
     * @lends AccountOrderHistory
     */
    profile: {
      value: profile,
      enumerable: true
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new AccountOrderHistory().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AccountOrderHistory: AccountOrderHistory
  };
});
