// jshint jasmine: true, latedef: false
define([
  'RegisterUser'
], function (registerUserMixin) {
  'use strict';

  var object = {
    '@type': 'RegisterUser'
  };

  describe('csa/plugins/account/application/components/RegisterUser', function () {

    it('decorates and objects correctly.', function () {
      registerUserMixin(object);
      expect(object['@type']).toBe('RegisterUser');
      expect(object.cid).toMatch(/^RegisterUser-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(registerUserFn(undefined)).toThrow();
      expect(registerUserFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called isAccountBenefitsModalVisible.', function () {
      expect(object.isAccountBenefitsModalVisible()).toBe(false);
      object.showAccountBenefits();
      expect(object.isAccountBenefitsModalVisible()).toBe(true);
    });

    it('contains a property called emailAddress.', function () {
      expect(object.emailAddress).toBeDefined();
    });

    it('contains a property called password.', function () {
      expect(object.password).toBeDefined();
    });

    it('contains a property called confirmPassword.', function () {
      expect(object.confirmPassword).toBeDefined();
    });

    it('contains a property called firstName.', function () {
      expect(object.firstName).toBeDefined();
    });

    it('contains a property called lastName.', function () {
      expect(object.lastName).toBeDefined();
    });

    it('contains a property called postalCode.', function () {
      expect(object.postalCode).toBeDefined();
    });

    it('contains a property called gender.', function () {
      expect(object.gender).toBeDefined();
    });

    it('contains a property called dateOfBirth.', function () {
      expect(object.dateOfBirth).toBeDefined();
    });

    it('cancel button blanks the fields.', function () {
      object.firstName('Test1');
      expect(object.firstName()).toBe('Test1');
      object.cancel();
      expect(object.firstName()).toBe(null);
    });

  });

  // Utilities
  function registerUserFn (viewModel) {
    return function () {
      return registerUserMixin(viewModel);
    };
  }
});
