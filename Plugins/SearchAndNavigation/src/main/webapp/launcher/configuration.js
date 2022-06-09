define([
  'csa/base/launcher/configuration',
  'i18n!csa/plugins/search-and-navigation/application/locales'
], function () {
  'use strict';

  require.config({
    map: {
      '*': {
        // Map a module 'alias' (a.k.a interface) to a module 'id' (a.k.a implementation).
        // 'alias': 'id'
        // Components
        'breadcrumbs': 'csa/plugins/search-and-navigation/application/components/breadcrumbs',
        'guided-navigation': 'csa/plugins/search-and-navigation/application/components/guided-navigation',
        'main-menu': 'csa/plugins/search-and-navigation/application/components/main-menu',
        'product-description': 'csa/plugins/search-and-navigation/application/components/product-description',
        'product-detail': 'csa/plugins/search-and-navigation/application/components/product-detail',
        'refinement-menu': 'csa/plugins/search-and-navigation/application/components/refinement-menu',
        'results-list': 'csa/plugins/search-and-navigation/application/components/results-list',
        'search': 'csa/plugins/search-and-navigation/application/components/search',
        'sku-selector': 'csa/plugins/search-and-navigation/application/components/sku-selector',
        // Services
        'oc-rest-get-selected-sku': 'csa/plugins/search-and-navigation/application/services/web/oc-rest/get-selected-sku'
      }
    }
  });

  return new Promise(function initialize (resolve) {
    require([
      'libs',
      'cartridge-to-component-lookup'
    ], function (libs, cartridgeToComponentLookup) {

      var ko = libs.ko;

      // Register components.
      ko.components.register('breadcrumbs', {require: 'breadcrumbs'});
      ko.components.register('guided-navigation', {require: 'guided-navigation'});
      ko.components.register('main-menu', {require: 'main-menu'});
      ko.components.register('product-description', {require: 'product-description'});
      ko.components.register('product-detail', {require: 'product-detail'});
      ko.components.register('refinement-menu', {require: 'refinement-menu'});
      ko.components.register('results-list', {require: 'results-list'});
      ko.components.register('search', {require: 'search'});
      ko.components.register('sku-selector', {require: 'sku-selector'});

      // Set cartridge to component mappings.
      cartridgeToComponentLookup.Breadcrumbs = 'breadcrumbs';
      cartridgeToComponentLookup.GuidedNavigation = 'guided-navigation';
      cartridgeToComponentLookup.MainMenu = 'main-menu';
      cartridgeToComponentLookup.ProductDescription = 'product-description';
      cartridgeToComponentLookup.ProductDetail = 'product-detail';
      cartridgeToComponentLookup.RefinementMenu = 'refinement-menu';
      cartridgeToComponentLookup.ResultsList = 'results-list';
      cartridgeToComponentLookup.Search = 'search';
      cartridgeToComponentLookup.SkuSelector = 'sku-selector';

      resolve();
    });
  });
});
