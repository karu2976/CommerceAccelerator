// jshint jasmine: true, latedef: false
define([
  'AccountCheckoutDefaults'
], function (accountCheckoutDefaultsMixin) {
  'use strict';

  describe('csa/plugins/account/application/components/AccountCheckoutDefaults', function () {

    var object = {
      '@type': 'AccountCheckoutDefaults'
    };

    it('decorates and objects correctly.', function () {
      accountCheckoutDefaultsMixin(object);
      expect(object['@type']).toBe('AccountCheckoutDefaults');
      expect(object.cid).toMatch(/^AccountCheckoutDefaults-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(accountCheckoutDefaultsFn(undefined)).toThrow();
      expect(accountCheckoutDefaultsFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('options caption text is different depending on the number of options.', function () {
      expect(object.optionsCaptionText).toBeDefined();
      expect(object.optionsCaptionText([])).toBe('None available');
      expect(object.optionsCaptionText(['test'])).toBe('Select default');
    });

  });

  // Utilities
  function accountCheckoutDefaultsFn (viewModel) {
    return function () {
      return accountCheckoutDefaultsMixin(viewModel);
    };
  }
});
