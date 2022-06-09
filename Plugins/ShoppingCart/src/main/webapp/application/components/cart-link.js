/**
 * The CartLink component provides the UI to indicate the current status of the shopping cart.
 * CartLink has two states 'expanded' and 'collapsed', collapsed being the default state.
 * In collapsed state CartLink is a button that indicates (on the button text) the number of
 * items in the cart; and, when clicked, links to the cart page.
 * In expanded state CartLink--in addition to the button--is a modal window that displays the
 * order summary and the list of items in the cart.
 * CartLink is automatically expanded when an item is successfully added to cart and is collapsed
 * by closing the modal window.
 *
 * @module csa/plugins/shopping-cart/applications/components/cart-link
 * @requires module:browser
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./cart-link.html
 * @requires module:order
 */
define([
  'browser',
  'libs',
  'content-item',
  'text!./cart-link.html',
  'order'
], function (browser, libs, ContentItem, template, order) {
  'use strict';

  var window = browser;
  var $ = libs.$;
  var ko = libs.ko;

  /**
   * @class
   * @extends ContentItem
   */
  function CartLink() {
    ContentItem.apply(this, arguments);

    var cid = ko.unwrap(this.cid);

    // Expand the cart link modal when a user successfully adds and item to the cart.
    $(window).on('addItemToOrderSuccess.' + cid, this.show.bind(this, true));

    // Collapse the cart link modal when a user clicks back/forward.
    $(window).on('popstate.' + cid, this.show.bind(this, false));

    // Set defaults.
    this.show(false);
    this.maxTotalCommerceItemCount(99);
  }

  CartLink.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends CartLink
     */
    constructor: {
      value: CartLink
    },

    /**
     * @property
     * @lends CartLink
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'show',
        'maxTotalCommerceItemCount'
      ])
    },

    /**
     * @property
     * @lends CartLink
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'totalCommerceItemCount'
      ])
    },

    /**
     * @property
     * @lends CartLink
     */
    order: {
      value: order,
      enumerable: true
    },

    /**
     * The the number of items in the cart, as displayed on the cart status button. If the number
     * of items in the cart is is greater than maxTotalCommerceItemCount, then the value of
     * totalCommerceItemCount is maxTotalCommerceItemCount+.
     *
     * @function
     * @return totalCommerceItemCount if totalCommerceItemCount <= maxTotalCommerceItemCount;
     *    maxTotalCommerceItemCount+ if totalCommerceItemCount > maxTotalCommerceItemCount
     */
    totalCommerceItemCount: {
      value: function () {
        var totalCommerceItemCount = this.order.totalCommerceItemCount();
        var maxTotalCommerceItemCount = this.maxTotalCommerceItemCount();

        totalCommerceItemCount = totalCommerceItemCount > maxTotalCommerceItemCount ? maxTotalCommerceItemCount + '+' : totalCommerceItemCount;

        return totalCommerceItemCount;
      },
      writable: true
    },

    /**
     * Check if the user is can to proceeed to checkout. If yes, proceed to checkout as normal;
     * if no, notify the user if there are unavailable items in the cart and do not proceed to
     * checkout.
     *
     * @function
     */
    canCheckout: {
      value: function canCheckout () {
        if (this.order.canCheckout()) {
          // Close CartLink
          this.show(false);

          // Proceed to checkout
          return true;
        }
        else {
          // Notify the user if there are unavailable items in the cart
          this.order.notifyUnavailableItems();
          // Do not proceed to checkout
        }
      }
    },

    /**
     * Override dispose to unbind cart-link event listeners.
     *
     * @function
     */
    dispose: {
      value: function dispose () {
        ContentItem.prototype.apply(this, arguments);

        // Unbind all cart-link event listeners
        $(window).off('.' + ko.unwrap(this.cid));

        return this;
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
        var viewModel = new CartLink().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    CartLink: CartLink
  };
});
