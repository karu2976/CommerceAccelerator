define([
  'csa/plugins/spotlights/launcher/configuration',
  'i18n!csa/applications/b2cstore/plugins/spotlights/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'scrollable-media-content-spotlight-atg-slot': 'csa/applications/b2cstore/plugins/spotlights/application/components/scrollable-media-content-spotlight-atg-slot',
        'scrollable-product-spotlight-atg-slot': 'csa/applications/b2cstore/plugins/spotlights/application/components/scrollable-product-spotlight-atg-slot'
      }
    }
  });
});
