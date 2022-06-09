define([
  'csa/base/launcher/configuration'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a comma-separated list of module 'aliases' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id',
        // 'alias': 'id'
        // For example:
        //'MyAlias': 'csa/plugins/<my_plugin>/application/components/MyComponent',
      }
    }
  });
});
