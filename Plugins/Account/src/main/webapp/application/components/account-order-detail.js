/**
 * Render for the AccountOrderDetails cartridge. Displays indepth information for a single order.
 *
 * @module csa/plugins/account/application/components/account-order-detail
 * @requires module:content-item
 * @requires module:text!./account-order-detail.html
 * @requires module:libs
 */
define([
  'content-item',
  'text!./account-order-detail.html'
], function (ContentItem, template) {
  'use strict';

  function AccountOrderDetail() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  AccountOrderDetail.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AccountOrderDetail
     */
    constructor: {
      value: AccountOrderDetail
    },

    /**
     * @property
     * @lends AccountOrderDetail
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'billingAddressCountryDisplayName',
        'canViewOrder',
        'myOrder',
        'orderState',
        'productURLs',
        'shippingAddressCountryDisplayName',
        'site'
      ])
    },

    /**
     * @property
     * @lends AccountOrderDetail
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'hasDiscountNotes',
        'hasShippingNotes'
      ])
    },

    hasDiscountNotes: {
      value: function () {
        return this.myOrder().priceInfo.adjustments.length > 0;
      },
      writable: true
    },

    hasShippingNotes: {
      value: function () {
        var result = false;

        this.myOrder().shippingGroups.forEach(function (shippingGroup) {
          if (shippingGroup.priceInfo.adjustments &&
              shippingGroup.priceInfo.adjustments.length > 0) {
            result = true;
          }
        });

        return result;
      },
      writable: true
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new AccountOrderDetail().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AccountOrderDetail: AccountOrderDetail
  };
});
