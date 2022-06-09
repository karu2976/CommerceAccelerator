/**
 * REST services class for checkout related operations.
 *
 * @module csa/plugins/checkout/application/services/web/checkout-services
 * @requiresmodule:order
 * @requiresmodule:profile
 */
define([
  'order',
  'profile'
], function (order, profile) {
  'use strict';

  return {
    /**
     * guestCheckout:
     *   Allows the user to continue as guest user for checkout.
     *
     * Input Parameters:
     *   email: email address of guest user.
     */
    guestCheckout: function (email) {

      // Define rest options.
      var param = {
        emailAddress : email
      };
      var options = {
        url: '/rest/model/atg/store/profile/CheckoutLoginActor/skipLogin',
        type: 'POST',
        data: param
      };

      // Make rest service call and handle response.
      return profile.datasource.execute(options);
    },

    /**
     * placeOrder:
     *   This function makes a rest call to commit order with shipping
     *   and billing details.
     *
     * Input Parameters:
     *   nickName - Shipping address nick name.
     *   shippingFirstName - First name of the customer for shipping address.
     *   shippingLastName - Last name of the customer for shipping address.
     *   shippingAddress1 - First address field for shipping address.
     *   shippingAddress2 - Second address field for shipping address.
     *   shippingCountry - Country field for shipping address.
     *   shippingCity - City field for shipping address.
     *   shippingState - State for shipping order.
     *   shippingPostalCode - Postal code for shipping order.
     *   shippingPhoneNumber - Phone number of customer in shipping address.
     *   shippingEmail - Email of the customer for shipping.
     *   shippingMethod - Order shipping method.
     *   creditCardNumber - Card number for payment of order.
     *   creditCardType - Card type for payment of order.
     *   creditCardVerificationNumber - Verification number of card.
     *   creditCardExpirationMonth - Card expiration month.
     *   creditCardExpirationYear - Card verification year.
     *   shippingAddressforBillingAddress - Indicator to use shipping address for Billing.
     *   billingNickName - Billing address nick name.
     *   billingFirstName - First name of the customer for billing address.
     *   billingLastName -  Last name of the customer for billing address.
     *   billingAddress1 - First address field for billing address.
     *   billingAddress2 - Second address field for shipping address.
     *   billingCountry - Country field for billing address.
     *   billingCity - City field for billing address.
     *   billingState - State for billing address.
     *   billingPostalCode - Postal code for billing order.
     *   billingPhoneNumber - Phone number of customer in billing address.
     *   billingEmail - Email of the customer for billing.
     */
    placeOrder: function (checkoutData) {

      // Define rest options.
      var options = {
        url: '/rest/model/atg/store/order/purchase/SinglePageCheckoutActor/expressCheckout',
        type: 'POST',
        data: checkoutData
      };

      // Call rest service.
      return order.datasource.execute(options);
    },

    /**
     * cancelOrder:
     *   This rest service will cancel the current order and will make
     *   cart empty.
     *
     * Input Parameters:
     *   None.
     */
    cancelOrder: function () {
      // Define rest options.
      var options = {
        url: '/rest/model/atg/store/order/purchase/CancelOrderCheckoutActor/cancelOrder',
        type: 'POST'
      };

      // Call rest service.
      return order.datasource.execute(options);
    },

    /**
     * applyShippingAddress:
     *   This service validates the shipping address and apply this to order.
     *
     * Input Parameters:
     *   nickName - Shipping address nick name.
     *   shippingFirstName - First name of the customer for shipping address.
     *   shippingLastName - Last name of the customer for shipping address.
     *   shippingAddress1 - First address field for shipping address.
     *   shippingAddress2 - Second address field for shipping address.
     *   shippingCountry - Country field for shipping address.
     *   shippingCity - City field for shipping address.
     *   shippingState - State for shipping order.
     *   shippingPostalCode - Postal code for shipping order.
     *   shippingPhoneNumber - Phone number of customer in shipping address.
     *   shippingEmail - Email of the customer for shipping.
     */
    applyShippingAddress: function (shippingDetails) {
      var options = {
        url: '/rest/model/atg/commerce/order/purchase/ShippingGroupActor/shipToAddress',
        type: 'POST',
        data: shippingDetails
      };

      // Call rest service.
      return order.datasource.execute(options);
    },

    /**
     * applyShippihngMethod:
     *   This service validates the shipping method and apply this to order.
     *
     * Input Parameters:
     *   nickName - Shipping address nick name.
     *   shippingFirstName - First name of the customer for shipping address.
     *   shippingLastName - Last name of the customer for shipping address.
     *   shippingAddress1 - First address field for shipping address.
     *   shippingAddress2 - Second address field for shipping address.
     *   shippingCountry - Country field for shipping address.
     *   shippingCity - City field for shipping address.
     *   shippingState - State for shipping order.
     *   shippingPostalCode - Postal code for shipping order.
     *   shippingPhoneNumber - Phone number of customer in shipping address.
     *   shippingEmail - Email of the customer for shipping.
     *   shippingMethod - Order shipping method.
     */
    applyShippingMethod: function (shippingDetails) {

      // Define rest options.
      var options = {
        url: '/rest/model/atg/commerce/order/purchase/ShippingGroupActor/shipToAddress',
        type: 'POST',
        data: shippingDetails
      };

      // Call rest service.
      return order.datasource.execute(options);
    },

    /**
     * applyShippihngMethod:
     *   This service validates the payment method and apply this to order.
     *
     * Input Parameters:
     *   creditCardNumber - Card number for payment of order.
     *   creditCardType - Card type for payment of order.
     *   creditCardVerificationNumber - Verification number of card.
     *   creditCardExpirationMonth - Card expiration month.
     *   creditCardExpirationYear - Card verification year.
     *   shippingAddressforBillingAddress - Indicator to use shipping address for Billing.
     *   billingNickName - Billing address nick name.
     *   billingFirstName - First name of the customer for billing address.
     *   billingLastName -  Last name of the customer for billing address.
     *   billingAddress1 - First address field for billing address.
     *   billingAddress2 - Second address field for shipping address.
     *   billingCountry - Country field for billing address.
     *   billingCity - City field for billing address.
     *   billingState - State for billing address.
     *   billingPostalCode - Postal code for billing order.
     *   billingPhoneNumber - Phone number of customer in billing address.
     *   billingEmail - Email of the customer for billing.
     */
    applyPaymentMethod: function (billingDetails) {

      // Define rest options.
      var options = {
        url: '/rest/model/atg/store/order/purchase/BillingFormHandlerActor/billingWithAddressAndCardDetails',
        type: 'POST',
        data: billingDetails
      };

      // Call rest service.
      return order.datasource.execute(options);
    },

    /**
     * applyBillingAddress:
     *   This service validates the payment method and apply this to order.
     *
     * Input Parameters:
     *   creditCardNumber - Card number for payment of order.
     *   creditCardType - Card type for payment of order.
     *   creditCardVerificationNumber - Verification number of card.
     *   creditCardExpirationMonth - Card expiration month.
     *   creditCardExpirationYear - Card verification year.
     *   shippingAddressforBillingAddress - Indicator to use shipping address for Billing.
     *   billingNickName - Billing address nick name.
     *   billingFirstName - First name of the customer for billing address.
     *   billingLastName -  Last name of the customer for billing address.
     *   billingAddress1 - First address field for billing address.
     *   billingAddress2 - Second address field for shipping address.
     *   billingCountry - Country field for billing address.
     *   billingCity - City field for billing address.
     *   billingState - State for billing address.
     *   billingPostalCode - Postal code for billing order.
     *   billingPhoneNumber - Phone number of customer in billing address.
     *   billingEmail - Email of the customer for billing.
     */
    applyBillingAddress: function (billingDetails) {

      // Define rest options.
      var options = {
        url: '/rest/model/atg/store/order/purchase/BillingFormHandlerActor/billingWithAddressAndCardDetails',
        type: 'POST',
        data: billingDetails
      };

      // Call rest service.
      return order.datasource.execute(options);
    }
  };
});
