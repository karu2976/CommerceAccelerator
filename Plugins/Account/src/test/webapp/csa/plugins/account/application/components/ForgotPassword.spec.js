// jshint jasmine: true, latedef: false
define([
  'ForgotPassword'
], function (forgotPasswordMixin) {
  'use strict';

  var object = {
    '@type': 'ForgotPassword'
  };

  describe('csa/plugins/account/application/components/ForgotPassword', function () {

    it('decorates and objects correctly.', function () {
      forgotPasswordMixin(object);
      expect(object['@type']).toBe('ForgotPassword');
      expect(object.cid).toMatch(/^ForgotPassword-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(forgotPasswordFn(undefined)).toThrow();
      expect(forgotPasswordFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called emailAddress.', function () {
      expect(object.emailAddress).toBeDefined();
    });

  });

  // Utilities
  function forgotPasswordFn (viewModel) {
    return function () {
      return forgotPasswordMixin(viewModel);
    };
  }
});
