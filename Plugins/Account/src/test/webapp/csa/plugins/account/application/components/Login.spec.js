// jshint jasmine: true, latedef: false
define([
  'Login'
], function (loginMixin) {
  'use strict';

  var object = {
    '@type': 'Login'
  };

  describe('csa/plugins/account/application/components/Login', function () {
    it('decorates and objects correctly.', function () {
      loginMixin(object);
      expect(object['@type']).toBe('Login');
      expect(object.cid).toMatch(/^Login-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(loginFn(undefined)).toThrow();
      expect(loginFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called username.', function () {
      expect(object.username).toBeDefined();
    });

    it('contains a property called password.', function () {
      expect(object.password).toBeDefined();
    });

    it('contains a property called isRecoverPasswordModalVisible.', function () {
      expect(object.isRecoverPasswordModalVisible()).toBe(false);
      object.showRecoverPassword();
      expect(object.isRecoverPasswordModalVisible()).toBe(true);
    });

    it('contains a property called isRecoverPasswordCompleteModalVisible.', function () {
      expect(object.isRecoverPasswordCompleteModalVisible()).toBe(false);
    });

    it('contains a property called newUser.', function () {
      expect(object.newUser).toBeDefined();
    });

    it('contains a property called forgotPassword.', function () {
      expect(object.forgotPassword).toBeDefined();
    });
  });

  // Utilities
  function loginFn (viewModel) {
    return function () {
      return loginMixin(viewModel);
    };
  }
});
