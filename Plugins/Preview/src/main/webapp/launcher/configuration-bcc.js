define([
  'csa/base/launcher/configuration'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        'bcc-preview': 'csa/plugins/preview/application/preview/bcc-preview',
        'custom-bcc-preview-bindings': 'csa/plugins/preview/application/preview/custom-bcc-preview-bindings'
      }
    }
  });

  // Return package initializaion promise. The application will not start until this promise is rosolved.
  return new Promise(function initPackage (resolve) {
    require(['bcc-preview'] , function (bccPreview) {
      bccPreview(resolve);
    });
  });
});
