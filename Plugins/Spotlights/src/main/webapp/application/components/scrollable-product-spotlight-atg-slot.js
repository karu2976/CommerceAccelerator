/**
 * @module csa/plugins/spotlights/application/components/scrollable-product-spotlight-atg-slot
 * @alias module:scrollable-product-spotlight-atg-slot
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./scrollable-product-spotlight-atg-slot.html
 */
define([
  'libs',
  'content-item',
  'text!./scrollable-product-spotlight-atg-slot.html'
], function (libs, ContentItem, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function ScrollableProductSpotlightATGSlot () {
    ContentItem.apply(this, arguments);

    // Set default values.
    this.activeRow(0);
  }

  ScrollableProductSpotlightATGSlot.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ScrollableProductSpotlightATGSlot
     */
    constructor: {
      value: ScrollableProductSpotlightATGSlot
    },

    /**
     * @property
     * @lends ScrollableProductSpotlightATGSlot
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'activeRow'
      ])
    },

    /**
     * @property
     * @lends ScrollableProductSpotlightATGSlot
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'items'
      ])
    },

    /**
     * @property
     * @lends ScrollableProductSpotlightATGSlot
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'productRows'
      ])
    },

    /**
     * @property
     * @lends ScrollableMediaContentSpotlightATGSlot
     */
    missingImagePath: {
      value: '',
      writable: true
    },

    /**
     * Convert the flat list of 'items' from the view model into an array of rows.
     * This allows the template to just output rows of items, without having to do any processing.
     *
     * @function
     * @lends ScrollableProductSpotlightATGSlot
     */
    productRows: {
      value: function () {
        var rows = [];

        if (this.items() && this.items().length > 0) {
          this.items().forEach(function (item, index) {
            if (index % 4 === 0) {
              rows.push([item]);
            }
            else {
              rows[rows.length - 1].push(item);
            }
          });
        }

        return rows;
      },
      writable: true
    },

    /**
     * Update the active row of items displayed to the user.
     *
     * @function
     * @lends ScrollableProductSpotlightATGSlot
     */
    updateActiveRow: {
      value: function updateActiveRow (action) {
        if (action === 'prev') {
          if (this.activeRow() > 0) {
            this.activeRow(this.activeRow() - 1);
          }
          else {
            this.activeRow(this.productRows().length - 1);
          }
        }
        else {
          if (this.activeRow() !== this.productRows().length - 1) {
            this.activeRow(this.activeRow() + 1);
          }
          else {
            this.activeRow(0);
          }
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
        var viewModel = new ScrollableProductSpotlightATGSlot().init(params);

        return viewModel;
      }
    },

    // The component view model classes.
    ScrollableProductSpotlightATGSlot: ScrollableProductSpotlightATGSlot
  };
});
