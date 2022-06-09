/**
 * @module csa/plugins/shopping-cart/application/components/cart-editor
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:text!./cart-editor.html
 * @requires module:order
 * @requires module:oc-rest-set-order
 * @requires module:oc-rest-remove-item-from-order
 */
define([
  'content-item',
  'content-item-array',
  'text!./cart-editor.html',
  'order',
  'oc-rest-set-order',
  'oc-rest-remove-item-from-order'
], function (ContentItem, contentItemArray, template, order, setOrder, removeItemFromOrder) {
  'use strict';

  function orderSummary () {
    return new ContentItem().set({'@type': 'OrderSummary'});
  }

  function couponEditor () {
    return new ContentItem().set({'@type': 'CouponEditor'});
  }

  /**
   * @class
   * @extends ContentItem
   */
  function CartEditor () {
    ContentItem.apply(this, arguments);

    // Notify the user if there are unavailable items in the cart
    this.order.notifyUnavailableItems();
  }

  CartEditor.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends CartEditor
     */
    constructor: {
      value: CartEditor
    },

    /**
     * @property
     * @lends CartEditor
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'continueShoppingURL',
        'productURLs'
      ])
    },

    /**
     * @property
     * @lends CartEditor
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'orderSummary',
          factory: orderSummary
        },
        {
          name: 'couponEditor',
          factory: couponEditor
        }
      ])
    },

    /**
     * @property
     * @lends CartEditor
     */
    order: {
      value: order,
      enumerable: true
    },

    /**
     * The setOrder function calls the cart-services setOrder web service, which updates the
     * quantity of all cart items with the quantity values sent from the client.
     *
     * @function
     * @lends CartEditor
     */
    setOrder: {
      value: function () {
        setOrder();
      }
    },

    /**
     * The removeItemFromOrder function calls the cart-services removeItemFromOrder web service,
     * which removes an item from the cart using the commerceId.
     *
     * @function
     * @lends CartEditor
     * @param item The cart item to be removed.
     */
    removeItemFromOrder: {
      value: function (item) {
        removeItemFromOrder(item.id());
      }
    },

    /**
     * Check if the user is can to proceeed to checkout. If yes, proceed to checkout as normal;
     * if no, notify the user if there are unavailable items in the cart and do not proceed to
     * checkout.
     *
     * @function
     * @lends CartEditor
     * @return true if the order is can proceed to checkout; undefined if not.
     */
    canCheckout: {
      value: function () {
        if (this.order.canCheckout()) {
          // Proceed to checkout
          return true;
        }
        else {
          // Notify the user if there are unavailable items in the cart
          this.order.notifyUnavailableItems();
          // Do not proceed to checkout
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
        var viewModel = new CartEditor().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    CartEditor: CartEditor
  };
});
