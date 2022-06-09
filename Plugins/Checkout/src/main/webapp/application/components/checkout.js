/**
 * The Checkout component provides the UI with four input fields that allows a user
 * to input shipping details and billing details. Initially all the fields on this
 * page will show not validated state and place order button will be disabled.
 * After entering checkout details user can validate input and if all the input
 * data validated properly then place order button will be enabled. However, user
 * can see order summary on this page, can go to cart page and can cancel the order
 * from this page.
 *
 * @module csa/plugins/checkout/application/components/checkout
 * @requires module:content-item
 * @requires module:checkout-services
 * @requires module:checkout-details
 * @requires module:text!./checkout.html
 * @requires module:profile
 * @requires module:order
 * @requires module:libs
 */
define([
  'content-item',
  'checkout-services',
  'checkout-details',
  'text!./checkout.html',
  'profile',
  'order',
  'libs'
], function (ContentItem, checkoutServices, checkoutDetails, template, profile, order, libs) {
  'use strict';

  var ko = libs.ko;

  function shippingAddressSelectorFactory () {
    return new ContentItem().set({
      '@type': 'ShippingAddressSelector'
    });
  }

  function shippingMethodSelectorFactory () {
    return new ContentItem().set({
      '@type': 'ShippingMethodSelector'
    });
  }

  function paymentMethodSelectorFactory () {
    return new ContentItem().set({
      '@type': 'PaymentMethodSelector'
    });
  }

  function billingAddressSelectorFactory () {
    return new ContentItem().set({
      '@type': 'BillingAddressSelector'
    });
  }

  function checkoutCartSummaryFactory () {
    return new ContentItem().set({
      '@type': 'CheckoutCartSummary'
    });
  }

  function couponEditorFactory () {
    return new ContentItem().set({
      '@type': 'CouponEditor'
    });
  }

  function orderSummaryFactory () {
    return new ContentItem().set({
      '@type': 'OrderSummary'
    });
  }

  function Checkout() {
    ContentItem.apply(this, arguments);

    this.restCallInProgress(false);
  }

  // Define class prototype
  Checkout.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends Checkout
     */
    constructor: {
      value: Checkout
    },

    /**
     * @property
     * @lends Checkout
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'restCallInProgress',
        'currentCountry',
      ])
    },

    /**
     * @property
     * @lends Checkout
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'availableShippingAddress',
        'states',
        'expiryYears',
        'availablePaymentMethods',
        'availableShippingMethods',
        'availableCountries'
      ])
    },

    /**
     * @property
     * @lends Checkout
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'hasDiscountNotes',
        'hasShippingNotes'
      ])
    },

    /**
     * @property
     * @lends Checkout
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'shippingAddressSelector',
          factory: shippingAddressSelectorFactory
        },
        {
          name: 'shippingMethodSelector',
          factory: shippingMethodSelectorFactory
        },
        {
          name: 'paymentMethodSelector',
          factory: paymentMethodSelectorFactory
        },
        {
          name: 'billingAddressSelector',
          factory: billingAddressSelectorFactory
        },
        {
          name: 'checkoutCartSummary',
          factory: checkoutCartSummaryFactory
        },
        {
          name: 'couponEditor',
          factory: couponEditorFactory
        },
        {
          name: 'orderSummary',
          factory: orderSummaryFactory
        }
      ])
    },

    /**
     * It contains all the information required during checkout e.g. shipping address details,
     * shipping method details, payment method details and billing address details.
     */
    checkoutDetails: {
      value: checkoutDetails,
      writable: true
    },

    /**
     * Profile object.
     */
    profile: {
      value: profile
    },

    /**
     * Order object.
     */
    order: {
      value: order
    },

    /**
     * hasDiscountNotes returns true if a promotion description
     * is to be displayed for discounts
     */
    hasDiscountNotes: {
      value: function () {
        return this.order.priceInfo.adjustments().length > 0;
      },
      writable: true
    },

    /**
     * hasShippingNotes returns true if a promotion description
     * is to be displayed for shipping discounts
     */
    hasShippingNotes: {
      value: function () {
        var result = false;

        this.order.shippingGroups().forEach(function (shippingGroup) {
          if ((ko.isObservable(shippingGroup.priceInfo.adjustments)) &&
              (shippingGroup.priceInfo.adjustments().length > 0)) {
            result = true;
          }
        });

        return result;
      },
      writable: true
    },

    /**
     *  This function redirects the user to order confirmation page
     *  on successful order submission.
     */
    handlePlaceOrderResponse: {
      value: function (response) {
        if (!response.formError) {
          window.history.pushState(null, null, 'checkout/confirmation', true);
        }
        else {
          this.restCallInProgress(false);
        }
      }
    },

    /**
     *  This function redirects the user to cart page
     *  on cancelling current order.
     */
    handleCancelOrderResponse: {
      value: function handleCancelOrderResponse (response) {
        if (!response.formError) {
          window.history.pushState(null, null, 'cart', true);
        }
        else {
          this.restCallInProgress(false);
        }
      }
    },

    /**
     *  This function will make a call to submit order rest service.
     *  If there are no errors in order submission will redirect to
     *  order confirmation page.
     */
    placeOrder: {
      value: function () {
        if (this.restCallInProgress()) {
          return;
        }
        this.restCallInProgress(true);
        var params = {
          'nickName': this.checkoutDetails.nickName(),
          'shippingFirstName': this.checkoutDetails.shippingFirstName(),
          'shippingLastName': this.checkoutDetails.shippingLastName(),
          'shippingAddress1': this.checkoutDetails.shippingAddress1(),
          'shippingAddress2': this.checkoutDetails.shippingAddress2(),
          'shippingCountry': this.checkoutDetails.shippingCountry(),
          'shippingCity': this.checkoutDetails.shippingCity(),
          'shippingState': this.checkoutDetails.shippingState(),
          'shippingPostalCode': this.checkoutDetails.shippingPostalCode(),
          'shippingPhoneNumber': this.checkoutDetails.shippingPhoneNumber(),
          'shippingEmail': this.checkoutDetails.shippingEmail(),
          'saveShippingAddressToProfile': this.checkoutDetails.saveShippingAddressToProfile(),
          'cardNickName': this.checkoutDetails.cardNickName(),
          'creditCardNumber': this.checkoutDetails.creditCardNumber(),
          'creditCardType': this.checkoutDetails.creditCardType(),
          'creditCardVerificationNumber': this.checkoutDetails.creditCardVerificationNumber(),
          'creditCardExpirationMonth': this.checkoutDetails.creditCardExpirationMonth(),
          'creditCardExpirationYear': this.checkoutDetails.creditCardExpirationYear(),
          'saveCreditCardToProfile': this.checkoutDetails.saveCreditCardToProfile(),
          'useSavedCreditCard': this.checkoutDetails.useSavedCreditCard(),
          'isBillingAddressSameAsShippingAddress': this.checkoutDetails.isBillingAddressSameAsShippingAddress(),
          'billingNickName': this.checkoutDetails.billingNickName(),
          'billingFirstName': this.checkoutDetails.billingFirstName(),
          'billingLastName': this.checkoutDetails.billingLastName(),
          'billingAddress1': this.checkoutDetails.billingAddress1(),
          'billingAddress2': this.checkoutDetails.billingAddress2(),
          'billingCountry': this.checkoutDetails.billingCountry(),
          'billingCity': this.checkoutDetails.billingCity(),
          'billingState': this.checkoutDetails.billingState(),
          'billingPostalCode': this.checkoutDetails.billingPostalCode(),
          'billingPhoneNumber': this.checkoutDetails.billingPhoneNumber(),
          'billingEmail': this.checkoutDetails.billingEmail(),
          'shippingMethod': this.checkoutDetails.shippingMethod()
        };

        checkoutServices.placeOrder(params).then(this.handlePlaceOrderResponse);
      }
    },

    /**
     *  This function will make a call to cancel current order rest service.
     *  Cart will be emptied and redirected to cart page.
     */
    cancelOrder: {
      value: function () {
        if (this.restCallInProgress()) {
          return;
        }
        this.restCallInProgress(true);
        checkoutServices.cancelOrder().then(this.handleCancelOrderResponse);
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
        var viewModel = new Checkout().init(params);

        checkoutDetails.initialize({
          availableShippingAddress: viewModel.availableShippingAddress(),
          availableShippingMethods: viewModel.availableShippingMethods(),
          availablePaymentMethods: viewModel.availablePaymentMethods(),
          states: viewModel.states(),
          currentCountry: viewModel.currentCountry(),
          parentCid: viewModel.cid()
        });

        return viewModel;
      }
    },

    // The component view model class.
    Checkout: Checkout
  };
});
