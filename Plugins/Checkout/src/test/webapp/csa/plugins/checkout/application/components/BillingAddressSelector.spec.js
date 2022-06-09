// jshint jasmine: true
define([
  'BillingAddressSelector'
], function (billingAddressSelectorMixin) {
  'use strict';

  var object = {
    '@type': 'BillingAddressSelector'
  };

  describe('csa/plugins/checkout/application/components/BillingAddressSelector', function () {
    it('decorates and objects correctly.', function () {
      billingAddressSelectorMixin(object);
      expect(object['@type']).toBe('BillingAddressSelector');
      expect(object.cid).toMatch(/^BillingAddressSelector-\d+$/);
    });

    it('contains a property called billing details.', function () {
      expect(object.billingDetails).toBeDefined();
    });

  });

});
