// jshint jasmine: true, latedef: false
define([
  'ScrollableProductSpotlight-ATGSlot'
], function (scrollableProductSpotlightATGSlotMixin) {
  'use strict';

  describe('csa/plugins/spotlights/application/components/ScrollableProductSpotlight-ATGSlot', function () {
    it('decorates and objects correctly.', function () {
      var vm = {
        '@type': 'ScrollableProductSpotlight-ATGSlot',

        'items': function () {
          return ['testProd1', 'testProd2'];
        }
      };

      scrollableProductSpotlightATGSlotMixin(vm);

      expect(vm['@type']).toBe('ScrollableProductSpotlight-ATGSlot');
      expect(vm.cid).toMatch(/^ScrollableProductSpotlight-ATGSlot-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(scrollableProductSpotlightATGSlotFn(undefined)).toThrow();
      expect(scrollableProductSpotlightATGSlotFn(null)).toThrow();
    });

    it('productRows returns an empty array when no items exist', function () {
      var vm = {
        'items': function () {
          return [];
        }
      };

      scrollableProductSpotlightATGSlotMixin(vm);

      expect(vm.productRows()).toEqual([]);
    });

    it('productRows returns a single array of 2 when only 2 items exist', function () {
      var vm = {
        'items': function () {
          return ['testProd1', 'testProd2'];
        }
      };

      scrollableProductSpotlightATGSlotMixin(vm);

      expect(vm.productRows()[0]).toEqual(['testProd1', 'testProd2']);
    });

    it('productRows returns a single array of 4', function () {
      var vm = {
        'items': function () {
          return ['testProd1', 'testProd2', 'testProd3', 'testProd4'];
        }
      };

      scrollableProductSpotlightATGSlotMixin(vm);

      expect(vm.productRows()[0]).toEqual(
        ['testProd1', 'testProd2', 'testProd3', 'testProd4']);
    });

    it('productRows returns 2 arrays of 4', function () {
      var vm = {
        'items': function () {
          return ['testProd1', 'testProd2', 'testProd3', 'testProd4',
                  'testProd5', 'testProd6', 'testProd7', 'testProd8'];
        }
      };

      scrollableProductSpotlightATGSlotMixin(vm);

      expect(vm.productRows()[1]).toEqual(
        ['testProd5', 'testProd6', 'testProd7', 'testProd8']);
    });

    it('productRows returns 3 arrays of 4', function () {
      var vm = {
        'items': function () {
          return ['testProd1', 'testProd2', 'testProd3', 'testProd4', 'testProd5',
                  'testProd6', 'testProd7', 'testProd8', 'testProd9', 'testProd10',
                  'testProd11', 'testProd12'];
        }
      };

      scrollableProductSpotlightATGSlotMixin(vm);

      expect(vm.productRows()[2]).toEqual(
        ['testProd9', 'testProd10', 'testProd11', 'testProd12']);
    });

    it('decorates and objects correctly when the "items" array is null/undefined.', function () {
      var vm = {
        '@type': 'ScrollableProductSpotlight-ATGSlot',
      };

      scrollableProductSpotlightATGSlotMixin(vm);

      expect(vm['@type']).toBe('ScrollableProductSpotlight-ATGSlot');
      expect(vm.cid).toMatch(/^ScrollableProductSpotlight-ATGSlot-\d+$/);
    });
  });

  // Utilities
  function scrollableProductSpotlightATGSlotFn (viewModel) {
    return function () {
      return scrollableProductSpotlightATGSlotMixin(viewModel);
    };
  }
});
