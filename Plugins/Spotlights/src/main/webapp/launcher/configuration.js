define([
  'csa/base/launcher/configuration',
  'i18n!csa/plugins/spotlights/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'scrollable-media-content-spotlight-atg-slot': 'csa/plugins/spotlights/application/components/scrollable-media-content-spotlight-atg-slot',
        'scrollable-product-spotlight-atg-slot': 'csa/plugins/spotlights/application/components/scrollable-product-spotlight-atg-slot'
      }
    }
  });

  return new Promise(function initialize (resolve) {
    require([
      'libs',
      'cartridge-to-component-lookup'
    ], function (libs, cartridgeToComponentLookup) {

      var ko = libs.ko;

      // Register components.
      ko.components.register('scrollable-media-content-spotlight-atg-slot', {require: 'scrollable-media-content-spotlight-atg-slot'});
      ko.components.register('scrollable-product-spotlight-atg-slot', {require: 'scrollable-product-spotlight-atg-slot'});

      // Set cartridge to component mappings.
      cartridgeToComponentLookup['ScrollableMediaContentSpotlight-ATGSlot'] = 'scrollable-media-content-spotlight-atg-slot';
      cartridgeToComponentLookup['ScrollableProductSpotlight-ATGSlot'] = 'scrollable-product-spotlight-atg-slot';

      resolve();
    });
  });
});
