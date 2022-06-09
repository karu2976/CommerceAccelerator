/**
* Set the valie for the favicon and siteLogo, which are application specific.
*
* @module csa/applications/b2cstore/base/application/models/view/metadata
* @alias metadata
* @requires module:csa/base/metadata
*/
define([
  'csa/base/application/models/view/metadata',
], function (metadata) {
  'use strict';

  metadata.favicon('/csadocroot/content/images/storefront/favicon.png');

  return metadata;
});
