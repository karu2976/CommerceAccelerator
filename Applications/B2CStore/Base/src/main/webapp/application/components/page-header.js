/**
* This page header component is overridden to set several application specific properties,
* such as site logo image path, alt text and title text.
*
* @module csa/applications/b2cstore/base/application/components/page-header
* @alias page-header
* @requires module:csa/base/application/components/page-header
*/
define([
  'csa/base/application/components/page-header'
], function (pageHeader) {
  'use strict';

  var siteLogo = pageHeader.PageHeader.prototype.siteLogo;

  siteLogo.src = '/csadocroot/content/images/storefront/csa-logo.jpg';
  siteLogo.alt = 'CSA Store';
  siteLogo.title = 'CSA Store';
  siteLogo.height = 38;
  siteLogo.width = 120;

  return pageHeader;
});
