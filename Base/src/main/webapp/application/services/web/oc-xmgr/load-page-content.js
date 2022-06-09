/**
 * @module csa/base/application/models/services/web/oc-xmgr-load-page-content
 * @alias module:oc-xmgr-load-page-content
 * @requires module:page
 */
define([
  'page'
], function (page) {
  'use strict';

  return function loadPageContent (URL) {

    // define service options.
    var options = {
      serviceId: 'ocXmgrLoadPageContent',
      url: URL
    };

    // Execute service call.
    return page.datasource.execute(options);
  };
});
