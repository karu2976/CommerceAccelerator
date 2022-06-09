// jshint jasmine: true, latedef: false
define([
  'AccountPersonalInformation'
], function (accountPersonalInformationMixin) {
  'use strict';

  var object = {
    '@type': 'AccountPersonalInformation',
    'profile': {
      'login': function () { return 'test@test.com'; }
    }
  };

  describe('csa/plugins/account/application/components/AccountPersonalInformation', function () {
    it('decorates and objects correctly.', function () {
      accountPersonalInformationMixin(object);
      expect(object['@type']).toBe('AccountPersonalInformation');
      expect(object.cid).toMatch(/^AccountPersonalInformation-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(accountPersonalInformationFn(undefined)).toThrow();
      expect(accountPersonalInformationFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });
  });

  // Utilities
  function accountPersonalInformationFn (viewModel) {
    return function () {
      return accountPersonalInformationMixin(viewModel);
    };
  }
});
