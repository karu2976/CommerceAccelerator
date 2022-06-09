// jshint jasmine: true, latedef: false
define([
  'ViewPaymentInformation'
], function (viewPaymentInformationMixin) {
  'use strict';

  var object = {
    '@type': 'ViewPaymentInformation'
  };

  describe('csa/plugins/account/application/components/ViewPaymentInformation', function () {

    it('decorates and objects correctly.', function () {
      viewPaymentInformationMixin(object);
      expect(object['@type']).toBe('ViewPaymentInformation');
      expect(object.cid).toMatch(/^ViewPaymentInformation-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(viewPaymentInformationFn(undefined)).toThrow();
      expect(viewPaymentInformationFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

  });

  // Utilities
  function viewPaymentInformationFn (viewModel) {
    return function () {
      return viewPaymentInformationMixin(viewModel);
    };
  }
});
