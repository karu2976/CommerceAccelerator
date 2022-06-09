// jshint jasmine: true, latedef: false
define([
  'ScrollableMediaContentSpotlight-ATGSlot'
], function (scrollableMediaContentSpotlightATGSlotMixin) {
  'use strict';

  describe('csa/plugins/spotlights/application/components/ScrollableMediaContentSpotlight-ATGSlot', function () {

    it('decorates and objects correctly.', function () {
      var viewModel = {
        '@type': 'ScrollableMediaContentSpotlight-ATGSlot'
      };

      scrollableMediaContentSpotlightATGSlotMixin(viewModel);

      expect(viewModel['@type']).toBe('ScrollableMediaContentSpotlight-ATGSlot');
      expect(viewModel.cid).toMatch(/^ScrollableMediaContentSpotlight-ATGSlot-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(scrollableMediaContentSpotlightATGSlotFn(undefined)).toThrow();
      expect(scrollableMediaContentSpotlightATGSlotFn(null)).toThrow();
    });

  });

  // Utilities
  function scrollableMediaContentSpotlightATGSlotFn (viewModel) {
    return function () {
      return scrollableMediaContentSpotlightATGSlotMixin(viewModel);
    };
  }
});
