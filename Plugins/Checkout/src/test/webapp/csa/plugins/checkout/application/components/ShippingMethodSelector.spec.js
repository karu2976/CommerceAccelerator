// jshint jasmine: true
define([
  'ShippingMethodSelector'
], function (checkoutMixin) {
  'use strict';

  var object = {
    '@type': 'ShippingMethodSelector'
  };

  describe('csa/plugins/checkout/application/components/ShippingMethodSelector', function () {
    it('decorates and objects correctly.', function () {
      checkoutMixin(object);
      expect(object['@type']).toBe('ShippingMethodSelector');
      expect(object.cid).toMatch(/^ShippingMethodSelector-\d+$/);
    });

    it('contains a property called shippingDetails.', function () {
      expect(object.shippingDetails).toBeDefined();
    });

  });

});
