/**
* The RefinementMenu component provides the UI with an area that will output a particular type
* of refinement menu e.g. categories, brands, price ranges etc. The refinement menu will be
* returned in a RefinementMenu cartridge content item and made available in this component's
* ViewModel object.
*
* This extended component overrides the template property so that it can reference some
* more application specific properties.
*
* @module csa/applications/b2cstore/plugins/search-and-navigation/application/components/refinement-menu
* @requires module:csa/plugins/search-and-navigation/application/components/refinement-menu
* @requires module:text!./refinement-menu.html
*/
define([
  'csa/plugins/search-and-navigation/application/components/refinement-menu',
  'text!./refinement-menu.html',
  'browser',
  'libs'
], function (refinementMenu, template, browser, libs) {
  'use strict';

  var history = browser.history;
  var location = browser.location;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  var RefinementMenu = refinementMenu.RefinementMenu;

  /**
   * @class
   * @extends RefinementMenu
   */
  function B2CStoreRefinementMenu () {
    RefinementMenu.apply(this, arguments);
  }

  // Extend the component class prototype
  B2CStoreRefinementMenu.prototype = Object.create(RefinementMenu.prototype, {

    /**
     * @property
     * @lends B2CStoreRefinementMenu
     */
    constructor: {
      value: B2CStoreRefinementMenu
    },

    observables: {
      value: RefinementMenu.prototype.observables.concat([
        'minPrice',
        'maxPrice'
      ])
    },

    /**
     * Update the navigation state with inputted price range values. The minimum and
     * maximum price values will be validated to ensure that they conform to an
     * actual price i.e. 20 or 20.50.
     *
     * If validation passes, the inputted prices will be added to the navigation
     * filter state using the Nf parameter. For example, if the user enters the
     * values 20 and 40; the push state will be updated to include:
     *
     * Nf=sku.activePrice|BTWN+20+40
     *
     * @function
     * @lends RefinementMenu
     */
    submitPriceRange : {
      value: function () {
        // Ensure that price format only allows valid prices. The following pattern
        // will match numbers with optional decimal point followed by two numbers.
        // For example, 20 or 20.50.
        var priceFormatPattern = /^\d{0,6}(?:\.\d{0,2}){0,1}$/;

        // Validation - Don't update navigation state when both min and max value haven't been supplied. Also
        // don't update when the min value is greater than the max value.
        if (!this.minPrice() || !this.maxPrice()) {
          toastr.error(i18next.t('searchAndNavigation.alert.emptyFromOrToField'));
        }
        else if (parseFloat(this.minPrice()) > parseFloat(this.maxPrice())) {
          toastr.error(i18next.t('searchAndNavigation.alert.fromMoreThanTo'));
        }
        else if (!priceFormatPattern.test(this.minPrice()) || !priceFormatPattern.test(this.maxPrice())) {
          toastr.error(i18next.t('searchAndNavigation.alert.invalidToFromField'));
        }
        // Validation passed.
        else {
          var updatedUrl;

          // When the current navigation state already contains a sku.activePrice navigation filter, update
          // the sku.activePrice parameter with the updated values supplied by the user.
          if (location.href.indexOf('Nf=sku.activePrice') > -1) {
            var pattern = new RegExp('(Nf=sku.activePrice).*?(&)');
            updatedUrl = location.href.replace(pattern, '$1' + '|BTWN+' + this.minPrice() + '+' + this.maxPrice() + '$2');

            if (updatedUrl === location.href) {
              updatedUrl = updatedUrl + (updatedUrl.indexOf('?') > 0 ? '&' : '?') + 'Nf=sku.activePrice|BTWN+' + this.minPrice() + '+' + this.maxPrice();
            }
          }
          else {
            // When the current navigation state doesn't contain a sku.activePrice navigation filter, just append the
            // sku.activePrice parameter with the values supplied by the user.
            var urlSeparator = (location.href.indexOf('?') > -1) ? '&' : '?';
            var priceRangeParameter = urlSeparator + 'Nf=sku.activePrice|BTWN+' + this.minPrice() + '+' + this.maxPrice();

            updatedUrl = location + priceRangeParameter;
          }

          history.pushState(null, null, updatedUrl, true);
        }
      }
    }
  });

  // Override the component template.
  refinementMenu.template = template;

  // Override the component view model class.
  refinementMenu.RefinementMenu = B2CStoreRefinementMenu;

  // Modify the view model factory to use the new class.
  refinementMenu.viewModel.createViewModel = function createViewModel (params) {
    var viewModel = new B2CStoreRefinementMenu().init(params);

    return viewModel;
  };

  // The component defintion.
  return refinementMenu;
});
