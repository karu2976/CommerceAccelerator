// jshint jasmine: true, latedef: false
define([
  'CartLink',
  'knockout'
], function (cartLinkMixin, ko) {
  'use strict';

  describe('csa/plugins/shopping-cart/application/components/CartLink', function () {
    it('decorates and view model with the necessary properties.', function () {
      var object = {
        '@type': 'CartLink',
        order: {
          commerceItems: ko.observableArray([
            {
              displayName: ko.observable('Shirt'),
              size: ko.observable('S'),
              color: ko.observable('White'),
              woodFinish: ko.observable(null)
            }
          ]),
          priceInfo: {
            ammount: ko.observable('100.25'),
            currencyCode: ko.observable('USD')
          }
        }
      };

      cartLinkMixin(object);

      expect(object['@type']).toBe('CartLink');
      expect(object.cid).toMatch(/^CartLink-\d+$/);
    });

    it('throws an error if the view model is undefined or null.', function () {
      expect(cartLinkFn(undefined)).toThrow();
      expect(cartLinkFn(null)).toThrow();
    });
  });

  // Utilities
  function cartLinkFn (viewModel) {
    return function () {
      return cartLinkMixin(viewModel);
    };
  }
});
