// jshint jasmine: true, latedef: false
define([
  'AccountOrderHistory'
], function (accountOrderHistoryMixin) {
  'use strict';

  var object = {
    '@type': 'AccountOrderHistory',
  };

  describe('csa/plugins/account/application/components/AccountOrderHistory', function () {
    it('decorates and objects correctly.', function () {
      accountOrderHistoryMixin(object);
      expect(object['@type']).toBe('AccountOrderHistory');
      expect(object.cid).toMatch(/^AccountOrderHistory-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(accountOrderHistoryFn(undefined)).toThrow();
      expect(accountOrderHistoryFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

  });

  // Utilities
  function accountOrderHistoryFn (viewModel) {
    return function () {
      return accountOrderHistoryMixin(viewModel);
    };
  }
});
