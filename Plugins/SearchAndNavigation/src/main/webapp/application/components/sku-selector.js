/**
 * The SkuSelector file is responsible for managing the shoppers selections and ensuring that they
 * are valid before allowing the shopper to add the selected item to the cart.
 *
 * SkuSelector has a runtime dependency on the addItemToOrder function found in the
 * {@link module:cart-services cart-services} file of the CommerceAccelerator.ShoppingCart
 * plugin.  This dependency facilitates the adding of items to the shoppers order.
 *
 * If the CommerceAccelerator.ShoppingCart plugin is not included in the same application as this
 * component the implementing delevoper must ensure that the custom plugin that replaces it includes
 * a web service client that includes an addItemToOrder function matching the API of the addItemToOrder
 * function found in {@link module:cart-services cart-services}.
 *
 * @module csa/plugins/search-and-navigation/application/components/sku-selector
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./sku-selector.html
 * @requires module:catalog
 * @requires module:oc-rest-get-selected-sku
 * @requires module:oc-rest-add-item-to-order
 */
define([
  'libs',
  'content-item',
  'text!./sku-selector.html',
  'catalog',
  'oc-rest-get-selected-sku',
  'oc-rest-add-item-to-order'
], function (libs, ContentItem, template, catalog, getSelectedSku, addItemToOrder) {
  'use strict';

  var i18next = libs.i18next;
  var toastr = libs.toastr;

  /**
   * @class
   * @extends ContentItem
   */
  function SkuSelector () {
    ContentItem.apply(this, arguments);

    // Set default values.
    this.quantity(1);
  }

  SkuSelector.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends SkuSelector
     */
    constructor: {
      value: SkuSelector
    },

    /**
     * @property
     * @lends SkuSelector
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'quantity'
      ])
    },

    /**
     * @property
     * @lends SkuSelector
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'skuPickers'
      ])
    },

    /**
     * @property
     * @lends SkuSelector
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'skuAvailable',
        'skuInStock',
        'selectedSkuStatusMessage',
        'selectedSkuStatusStyle'
      ])
    },

    /**
     * The catalog domain model
     *
     * @property
     * @lends SkuSelector
     */
    catalog: {
      value: catalog,
      enumerable: true
    },

    /**
     * The availability status of the currently selected SKU
     *
     * If no SKU has been selected this property will return undefined.
     *
     * If a SKU has been selected the value of the isAvailable property for the currently selected
     * sku is returned unless it is undefined or null, in which case true will be returned.
     *
     * @function
     * @lends SkuSelector
     */
    skuAvailable: {
      value: function () {
        var skuAvailable = true;

        if (catalog.selectedSku()) {
          skuAvailable = catalog.selectedSku().isAvailable;
        }
        else {
          skuAvailable = catalog.product().isAvailable;
        }

        if (!skuAvailable) {
          toastr.error(i18next.t('searchAndNavigation.alert.skuUnavailable'));
        }

        return skuAvailable;
      },
      writable: true
    },

    /**
     * The stock status of the currently selected SKU.
     *
     * If no SKU has been selected this property will return undefined.
     *
     * If a SKU has been selected the value of the inStock property for the currently selected sku
     * is returned unless it is undefined or null, in which case true will be returned.
     *
     * @function
     * @lends SkuSelector
     */
    skuInStock: {
      value: function () {
        var skuInStock = true;

        if (catalog.selectedSku()) {
          skuInStock = catalog.selectedSku().inStock;
        }

        if (!skuInStock) {
          toastr.error(i18next.t('searchAndNavigation.alert.skuOutOfStock'));
        }

        return skuInStock;
      },
      writable: true
    },

    /**
     * The status message for the currently selected SKU.
     *
     * When the SKU is in stock the message displayed will be,
     *     'skuXXXX, In Stock'.
     *
     * When the SKU is not in stock the message displayed will be,
     *     'skuXXXX, Out of Stock'.
     *
     * When the SKU is not available the message displayed will be,
     *     'skuXXXX, Unavailable'.
     *
     * @function
     * @lends SkuSelector
     */
    selectedSkuStatusMessage: {
      value: function () {
        var message = '';
        var skuId = '';

        if (catalog.selectedSku()) {
          skuId = catalog.selectedSku().repositoryId + ', ';

          message = this.skuInStock() ? i18next.t('searchAndNavigation.text.inStock') : i18next.t('searchAndNavigation.text.outOfStock');

          if (!this.skuAvailable()) {
            message = i18next.t('searchAndNavigation.text.unavailable');
          }
        }
        return skuId + message;
      },
      writable: true
    },

    /**
     * The overall status of the SKU which drives the styling of the dislayed message.
     *
     * If the SKU is in stock and the SKU is available then the message is styled as a success.
     *
     * If the SKU is not in stock or the SKU is not available then the message is styled as a
     * danger.
     *
     * @function
     * @lends SkuSelector
     */
    selectedSkuStatusStyle: {
      value: function () {
        var selectedSkuStatusStyle = 0;

        if (this.skuAvailable() && this.skuInStock()) {
          selectedSkuStatusStyle = 1;
        }
        else if (!this.skuAvailable() || !this.skuInStock()) {
          selectedSkuStatusStyle = -1;
        }

        return selectedSkuStatusStyle;
      },
      writable: true
    },

    /**
     * This property returns the translated string that will be displayed in the SKU picker when
     * the screen is initially rendered.
     *
     * The string takes the format of "Select " and the name of the property that the picker is
     * for e.g. Size.
     *
     * An options caption  will only be returned id there is more than one option in the drop down
     * list.
     *
     * @function
     * @lends SkuSelector
     */
    optionsCaption: {
      value: function optionsCaption (value, options) {
        if (options.length > 1) {
          var i18nDefault = i18next.t('searchAndNavigation.placeholder.select');

          return i18next.t('searchAndNavigation.placeholder.' + value, {defaultValue: i18nDefault});
        }
      },
      writable: true
    },

    /**
     * This function is called when a change is made in the value of any of the SKU pickers on the
     * screen.
     *
     * @function
     * @lends SkuSelector
     */
    pickerChange: {
      value: function pickerChange () {

        // The options to send as part of the rest request.
        var options = {};

        // Loop the pickers that are available for this product and add their currently selected
        // value to the options object.
        this.skuPickers().forEach(function (picker) {
          if (picker.value) {
            var selectedType = 'selected' + picker.type.charAt(0).toUpperCase() + picker.type.substr(1);
            options[selectedType] = picker.value;
          }
        });

        // Add the current product id to the options object.
        options.productIdPropertyName = catalog.product().repositoryId;

        // Add the SKU type to the options object.
        options.type = catalog.product().childSkuType;

        // Invoke the REST request to the server.
        getSelectedSku(options);
      }
    },

    /**
     * This property returns the translated string that will be displayed for a helper information
     * link.
     *
     * @function
     * @lends SkuSelector
     */
    helperInformationText: {
      value: function helperInformationText (data) {
        return i18next.t('searchAndNavigation.link.' + data);
      }
    },

    /**
     * This property returns the translated string that will be displayed for a sku picker
     * label.
     *
     * @function
     * @lends SkuSelector
     */
    skuPickerLabel: {
      value: function skuPickerLabel (data) {
        return i18next.t('searchAndNavigation.label.' + data);
      }
    },

    /**
     * The addItemToOrder function calls the cart-services (found in CommerceAccelerator.ShoppingCart)
     * addItemToOrder web service, which adds the currently selected item to the cart.
     *
     * @function
     * @param selectedItem The currently selected item, which specifies the item to be added to
     * order and must be an object with properties productId, catalogRefIds and quantity, e.g.:
     *      {
     *        productId: 'prd101',
     *        catalogRefIds: 'sku104',
     *        quantity: 1,
     *      }
     *
     * @function
     * @lends SkuSelector
     */
    addItemToOrder: {
      value: function () {
        if (catalog.product().repositoryId && catalog.selectedSku()) {
          if (catalog.selectedSku() !== null) {

            // If the SKU is in stock and available then add it to the cart.
            if (this.skuAvailable() && this.skuInStock()) {
              var selectedItem = {
                productId: catalog.product().repositoryId,
                catalogRefIds: catalog.selectedSku().repositoryId,
                quantity: this.quantity()
              };

              addItemToOrder(selectedItem);
            }

            // If the SKU is out of stock or not available then let the customer know.
            if (!this.skuAvailable()) {
              toastr.error(i18next.t('searchAndNavigation.alert.skuUnavailable'));
            }
            if (!this.skuInStock()) {
              toastr.error(i18next.t('searchAndNavigation.alert.skuOutOfStock'));
            }
          }
        }
        else {
          toastr.error(i18next.t('searchAndNavigation.alert.makeValidSelection'));
        }
      }
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new SkuSelector().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    SkuSelector: SkuSelector
  };
});
