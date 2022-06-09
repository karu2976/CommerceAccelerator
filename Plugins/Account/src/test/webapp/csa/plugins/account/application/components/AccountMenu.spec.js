// jshint jasmine: true, latedef: false
define([
  'AccountMenu'
], function (accountMenuMixin) {
  'use strict';

  describe('csa/plugins/account/application/components/AccountMenu', function () {

    var object = {
      '@type': 'AccountMenu'
    };

    it('decorates and objects correctly.', function () {
      accountMenuMixin(object);
      expect(object['@type']).toBe('AccountMenu');
      expect(object.cid).toMatch(/^AccountMenu-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(accountMenuFn(undefined)).toThrow();
      expect(accountMenuFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

  });

  // Utilities
  function accountMenuFn (viewModel) {
    return function () {
      return accountMenuMixin(viewModel);
    };
  }
});
