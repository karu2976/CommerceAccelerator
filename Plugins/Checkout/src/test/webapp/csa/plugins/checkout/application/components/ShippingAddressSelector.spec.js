// jshint jasmine: true
define([
  'ShippingAddressSelector'
], function (shippingAddressMixin) {
  'use strict';

  var object = {
    '@type': 'ShippingAddressSelector'
  };

  describe('csa/plugins/checkout/application/components/ShippingAddressSelector', function () {
    it('decorates and objects correctly.', function () {
      shippingAddressMixin(object);
      expect(object['@type']).toBe('ShippingAddressSelector');
      expect(object.cid).toMatch(/^ShippingAddressSelector-\d+$/);
    });

    it('contains a property called shipping details.', function () {
      expect(object.shippingDetails).toBeDefined();
    });

  });

});
