// jshint jasmine: true, latedef: false
define([
  'AccountChangePassword'
], function (accountChangePasswordMixin) {
  'use strict';

  describe('csa/plugins/account/application/components/AccountChangePassword', function () {

    var object = {
      '@type': 'AccountChangePassword'
    };

    it('decorates and objects correctly.', function () {
      accountChangePasswordMixin(object);
      expect(object['@type']).toBe('AccountChangePassword');
      expect(object.cid).toMatch(/^AccountChangePassword-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(accountChangePasswordFn(undefined)).toThrow();
      expect(accountChangePasswordFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called oldPassword.', function () {
      expect(object.oldPassword).toBeDefined();
    });

    it('contains a property called newPassword.', function () {
      expect(object.newPassword).toBeDefined();
    });

    it('contains a property called confirmPassword.', function () {
      expect(object.confirmPassword).toBeDefined();
    });

    it('cancel button resets fields', function () {
      object.oldPassword('password');
      object.newPassword('newPassword');
      object.confirmPassword('confirmPassword');

      expect(object.oldPassword()).toBe('password');
      expect(object.newPassword()).toBe('newPassword');
      expect(object.confirmPassword()).toBe('confirmPassword');

      object.cancel();

      expect(object.oldPassword()).toBe(null);
      expect(object.newPassword()).toBe(null);
      expect(object.confirmPassword()).toBe(null);
    });

  });

  // Utilities
  function accountChangePasswordFn (viewModel) {
    return function () {
      return accountChangePasswordMixin(viewModel);
    };
  }
});
