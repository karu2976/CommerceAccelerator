// jshint jasmine: true
define([
  'PaymentMethodSelector'
], function (checkoutMixin) {
  'use strict';

  var object = {
    '@type': 'PaymentMethodSelector'
  };

  describe('csa/plugins/checkout/application/components/PaymentMethodSelector', function () {
    it('decorates and objects correctly.', function () {
      checkoutMixin(object);
      expect(object['@type']).toBe('PaymentMethodSelector');
      expect(object.cid).toMatch(/^PaymentMethodSelector-\d+$/);
    });

    it('contains a property called billing details.', function () {
      expect(object.billingDetails).toBeDefined();
    });

  });

});
