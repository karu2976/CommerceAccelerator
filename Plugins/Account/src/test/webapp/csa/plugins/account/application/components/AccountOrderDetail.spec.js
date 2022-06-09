// jshint jasmine: true, latedef: false
define([
  'AccountOrderDetail',
  'knockout'
], function (accountOrderDetailMixin, ko) {
  'use strict';

  var object = {
    '@type': 'AccountOrderDetail',
    'myOrder': {
      'priceInfo': {
        'adjustments': function () { return ['test']; }
      },
      'shippingGroups': function () {
        return [
          {
            'priceInfo': {
              'adjustments': ko.observableArray(['test'])
            }
          }
        ];
      }
    }
  };

  describe('csa/plugins/account/application/components/AccountOrderDetail', function () {
    it('decorates and objects correctly.', function () {
      accountOrderDetailMixin(object);
      expect(object['@type']).toBe('AccountOrderDetail');
      expect(object.cid).toMatch(/^AccountOrderDetail-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(accountOrderDetailFn(undefined)).toThrow();
      expect(accountOrderDetailFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('has discount notes.', function () {
      expect(object.hasDiscountNotes()).toBe(true);
    });

    it('has shipping notes.', function () {
      expect(object.hasShippingNotes()).toBe(true);
    });
  });

  // Utilities
  function accountOrderDetailFn (viewModel) {
    return function () {
      return accountOrderDetailMixin(viewModel);
    };
  }
});
