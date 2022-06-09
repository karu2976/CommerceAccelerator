/**
 * This script loads required js and css files for preview.
 *
 * @module csa/plugins/preview/application/preview/include-endeca-preview-scripts
 */
define(function () {
  'use strict';

  return function includeEndecaPreviewScripts (previewURL) {

    // Require all endeca preview related scripts.
    require.config({
      paths: {
        'preview': '' + previewURL + '/tools/xmgr/app/preview/js/preview'
      }
    });

    // Include all endeca preview related CSS files.
    var pathForCss = ['' + previewURL + '/tools/xmgr/app/preview/css/audit-site.css'];

    // Get all the 'link' currently in head.
    var headCss = document.getElementsByTagName('link');
    var headerAlreadyAdded = false;

    for (var i = 0; i < pathForCss.length; i++) {

      // Check link is already present in head.
      headerAlreadyAdded = false;
      for (var j = 0; j < headCss.length; j++) {
        if (pathForCss[i].toUpperCase() === headCss[j].href.toUpperCase()) {

          // Do not add header again.
          headerAlreadyAdded = true;
          break;
        }
      }

      // Create link and add endeca preview related css in head.
      if (headerAlreadyAdded === false) {
        var head = document.getElementsByTagName('head')[0];
        var style = document.createElement('link');
        style.setAttribute('rel', 'stylesheet');
        style.setAttribute('type', 'text/css');
        style.setAttribute('href', pathForCss[i]);
        head.appendChild(style);
      }
    }
  };
});
