// jshint jasmine: true, latedef: false
define([
  'CartEditor',
  'knockout'
], function (cartEditorMixin, ko) {
  'use strict';

  describe('csa/plugins/shopping-cart/application/components/CartEditor', function () {
    it('decorates and view model with the necessary properties.', function () {
      var object = {
        '@type': 'CartEditor',
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

      cartEditorMixin(object);

      expect(object['@type']).toBe('CartEditor');
      expect(object.cid).toMatch(/^CartEditor-\d+$/);
    });

    it('throws an error if the view model is undefined or null.', function () {
      expect(cartEditorFn(undefined)).toThrow();
      expect(cartEditorFn(null)).toThrow();
    });
  });

  // Utilities
  function cartEditorFn (viewModel) {
    return function () {
      return cartEditorMixin(viewModel);
    };
  }
});
