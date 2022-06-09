/**
* This extended component overrides the template property so that it can reference some
* more application specific properties.
*
* @module csa/applications/b2cstore/plugins/account/application/components/account-order-detail
* @requires module:csa/plugins/account/application/components/account-order-detail
* @requires module:text!./account-order-detail.html
*/
define([
  'csa/plugins/account/application/components/account-order-detail',
  'text!./account-order-detail.html'
], function (accountOrderDetail, template) {
  'use strict';

  // Override the component template.
  accountOrderDetail.template = template;

  // The component defintion.
  return accountOrderDetail;
});
