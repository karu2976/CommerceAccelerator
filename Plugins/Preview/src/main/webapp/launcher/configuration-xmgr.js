define([
  'csa/base/launcher/configuration'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        'append-endeca-preview-attribute': 'csa/plugins/preview/application/preview/append-endeca-preview-attribute',
        'include-endeca-preview-scripts': 'csa/plugins/preview/application/preview/include-endeca-preview-scripts',
        'xmgr-preview': 'csa/plugins/preview/application/preview/xmgr-preview'
      }
    }
  });

  // Return package initializaion promise. The application will not start until this promise is rosolved.
  return new Promise(function initPackage (resolve) {
    require(['xmgr-preview'] , function () {
      resolve();
    });
  });
});
