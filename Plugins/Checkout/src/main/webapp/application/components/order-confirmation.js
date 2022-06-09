/**
 * The OrderConfirmation component provides the UI for order confirmation page.
 * In case of registered user from this page user can see order details and
 * can print order details. In case of anonymous user from this page user can
 * register also.
 *
 * @module csa/plugins/checkout/application/components/order-confirmation
 * @requires module:content-item
 * @requires module:profile
 * @requires module:text!./order-confirmation.html
 */
define([
  'content-item',
  'profile',
  'text!./order-confirmation.html'
], function (ContentItem, profile, template) {
  'use strict';

  function OrderConfirmation() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  OrderConfirmation.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends OrderConfirmation
     */
    constructor: {
      value: OrderConfirmation
    },

    /**
     * @property
     * @lends OrderConfirmation
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'currentOrder'
      ])
    },

    /**
     * Profile object.
     */
    profile: {
      value: profile
    },

    /**
     * This method opens order details in a pop-up for printing the
     * order details.
     */
    printOrderDetail: {
      value: function printOrderDetail (orderId) {
        var popupURL = 'account/orders/view?orderId=' + orderId;
        var myWindow = window.open(popupURL, 'printOrderWindow',
          'left=20,top=20,width=800,height=500, resizable=0');
        myWindow.focus();
      }
    },

    /**
     * This method redirects ananymous user from order confirmation page to
     * account register page with email addresss populated.
     */
    registerForAccount: {
      value: function registerForAccount () {
        window.history.pushState({email: profile.email()}, null, 'account/register', true);
      }
    }
  });

  // The component defintion
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new OrderConfirmation().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    OrderConfirmation: OrderConfirmation
  };
});
