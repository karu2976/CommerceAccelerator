/**
 * @module csa/base/launcher/configuration
 * @requires csa/base/launcher/shims/shims
 */
define([
  'shims',
  'i18n!csa/base/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Behaviors
        'bootstrap-accordion-binding': 'csa/base/application/behaviors/bindings/bootstrap-accordion-binding',
        'bootstrap-carousel-binding': 'csa/base/application/behaviors/bindings/bootstrap-carousel-binding',
        'bootstrap-modal-binding': 'csa/base/application/behaviors/bindings/bootstrap-modal-binding',
        'component-binding-preprocessor': 'csa/base/application/behaviors/bindings/component-binding-preprocessor',
        'image-fallback-binding': 'csa/base/application/behaviors/bindings/image-fallback-binding',
        'recheck-validity-binding': 'csa/base/application/behaviors/bindings/recheck-validity-binding',
        'reset-binding': 'csa/base/application/behaviors/bindings/reset-binding',
        'unique-id-binding': 'csa/base/application/behaviors/bindings/unique-id-binding',
        'unique-for-binding': 'csa/base/application/behaviors/bindings/unique-for-binding',
        'currency-filter': 'csa/base/application/behaviors/filters/currency-filter',
        'date-filter': 'csa/base/application/behaviors/filters/date-filter',
        'i18n-filter': 'csa/base/application/behaviors/filters/i18n-filter',
        'behaviors': 'csa/base/application/behaviors/behaviors',
        // Components
        'content-slot': 'csa/base/application/components/content-slot',
        'footer-link-menu': 'csa/base/application/components/footer-link-menu',
        'footer-link': 'csa/base/application/components/footer-link',
        'n-column-page': 'csa/base/application/components/n-column-page',
        'page-footer': 'csa/base/application/components/page-footer',
        'page-header': 'csa/base/application/components/page-header',
        'rich-text-main': 'csa/base/application/components/rich-text-main',
        'site-preferences': 'csa/base/application/components/site-preferences',
        'static-page-navigation': 'csa/base/application/components/static-page-navigation',
        // Globals
        'browser': 'csa/base/application/globals/browser',
        'libs': 'csa/base/application/globals/libs',
        // Models
        'datasource': 'csa/base/application/models/datasource/datasource',
        'oc-rest-datasource': 'csa/base/application/models/datasource/oc-rest-datasource',
        'oc-xmgr-datasource': 'csa/base/application/models/datasource/oc-xmgr-datasource',
        'catalog': 'csa/base/application/models/domain/catalog/catalog',
        'auxiliary-data': 'csa/base/application/models/domain/order/auxiliary-data',
        'catalog-ref': 'csa/base/application/models/domain/order/catalog-ref',
        'commerce-item-array': 'csa/base/application/models/domain/order/commerce-item-array',
        'commerce-item': 'csa/base/application/models/domain/order/commerce-item',
        'inventory-info': 'csa/base/application/models/domain/order/inventory-info',
        'order': 'csa/base/application/models/domain/order/order',
        'price-info': 'csa/base/application/models/domain/order/price-info',
        'product-ref': 'csa/base/application/models/domain/order/product-ref',
        'shipping-group-array': 'csa/base/application/models/domain/order/shipping-group-array',
        'shipping-group': 'csa/base/application/models/domain/order/shipping-group',
        'profile': 'csa/base/application/models/domain/profile/profile',
        'entity-array': 'csa/base/application/models/domain/entity-array',
        'entity': 'csa/base/application/models/domain/entity',
        'cartridge-to-component-lookup': 'csa/base/application/models/view/cartridge-to-component-lookup',
        'content-item-array': 'csa/base/application/models/view/content-item-array',
        'content-item': 'csa/base/application/models/view/content-item',
        'metadata': 'csa/base/application/models/view/metadata',
        'page': 'csa/base/application/models/view/page',
        'model-array': 'csa/base/application/models/model-array',
        'model': 'csa/base/application/models/model',
        // Services
        'router': 'csa/base/application/services/local/router',
        'oc-xmgr-load-page-content': 'csa/base/application/services/web/oc-xmgr/load-page-content',
        // Styles
        'style': 'csa/base/application/styles/style',
        // Application
        'app': 'csa/base/application/application'
      }
    }
  });

  return new Promise(function initialize (resolve) {
    require([
      'browser',
      'libs'
    ], function (browser, libs) {

      var ko = libs.ko;

      // Register components.
      ko.components.register('content-slot', {require: 'content-slot'});
      ko.components.register('n-column-page', {require: 'n-column-page'});
      ko.components.register('page-footer', {require: 'page-footer'});
      ko.components.register('footer-link-menu', {require: 'footer-link-menu'});
      ko.components.register('footer-link', {require: 'footer-link'});
      ko.components.register('page-header', {require: 'page-header'});
      ko.components.register('rich-text-main', {require: 'rich-text-main'});
      ko.components.register('site-preferences', {require: 'site-preferences'});
      ko.components.register('static-page-navigation', {require: 'static-page-navigation'});

      resolve();
    });
  });
});
