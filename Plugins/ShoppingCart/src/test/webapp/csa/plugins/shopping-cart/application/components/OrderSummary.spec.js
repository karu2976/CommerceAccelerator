// jshint jasmine: true, latedef: false
define([
  'OrderSummary'
], function (orderSummaryMixin) {
  'use strict';

  describe('csa/plugins/shopping-cart/application/components/OrderSummary', function () {
    it('decorates and view model with the necessary properties.', function () {
      var object = {
        '@type': 'OrderSummary'
      };

      orderSummaryMixin(object);

      expect(object['@type']).toBe('OrderSummary');
      expect(object.cid).toMatch(/^OrderSummary-\d+$/);
    });

    it('throws an error if the view model is undefined or null.', function () {
      expect(orderSummaryFn(undefined)).toThrow();
      expect(orderSummaryFn(null)).toThrow();
    });
  });

  // Utilities
  function orderSummaryFn (viewModel) {
    return function () {
      return orderSummaryMixin(viewModel);
    };
  }
});
