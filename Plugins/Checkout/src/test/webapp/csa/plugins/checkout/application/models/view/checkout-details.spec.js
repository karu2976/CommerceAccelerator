// jshint jasmine: true
define([
  'checkout-details'
], function (checkoutDetailsMixin) {
  'use strict';

  describe('csa/plugins/checkout/application/models/view/checkout-details', function () {

    it('contains a property called shippingAddressList.', function () {
      expect(checkoutDetailsMixin.shippingAddressList).toBeDefined();
    });

    it('contains a property called billingAddressList.', function () {
      expect(checkoutDetailsMixin.billingAddressList).toBeDefined();
    });

    it('contains a property called paymentMethodList.', function () {
      expect(checkoutDetailsMixin.paymentMethodList).toBeDefined();
    });

    it('contains a property called selectedItem.', function () {
      expect(checkoutDetailsMixin.selectedItem).toBeDefined();
    });

  });

});
