// jshint jasmine: true, latedef: false
define([
  'AccountMenuNavigation'
], function (accountMenuNavigationMixin) {
  'use strict';

  describe('csa/plugins/account/application/components/AccountMenuNavigation', function () {

    var object = {
      '@type': 'AccountMenuNavigation'
    };

    it('decorates and objects correctly.', function () {
      accountMenuNavigationMixin(object);
      expect(object['@type']).toBe('AccountMenuNavigation');
      expect(object.cid).toMatch(/^AccountMenuNavigation-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(accountMenuNavigationFn(undefined)).toThrow();
      expect(accountMenuNavigationFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

  });

  // Utilities
  function accountMenuNavigationFn (viewModel) {
    return function () {
      return accountMenuNavigationMixin(viewModel);
    };
  }
});
