/**
 * @module csa/base/application/models/domain/order/order
 * @alias module:order
 * @requires module:libs
 * @requires module:oc-rest-datasource
 * @requires module:entity
 */
define([
  'libs',
  'oc-rest-datasource',
  'entity',
  'commerce-item-array',
  'shipping-group-array',
  'price-info'
], function (libs, Datasource, Entity, commerceItemArray, shippingGroupArray, PriceInfo) {
  'use strict';

  var i18next = libs.i18next;
  var toastr = libs.toastr;

  // PriceInfo factory function used for defining PriceInfo associations.
  function priceInfo () {
    return new PriceInfo();
  }

  /**
   * @class
   * @extends Entity
   */
  function Order () {
    Entity.apply(this, arguments);

    // Set default values.
    this.totalCommerceItemCount(0);

    // Define the model datasource.
    this.datasource = new Datasource(this);
  }

  Order.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends Order
     */
    constructor: {
      value: Order
    },

    /**
     * @property
     * @lends Profile
     */
    entityName: {
      value: 'order'
    },

    /**
     * @property
     * @lends Order
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'couponCode',
        'totalCommerceItemCount'
      ])
    },

    /**
     * @property
     * @lends Order
     */
    associations: {
      value: Entity.prototype.associations.concat([
        {
          name: 'commerceItems',
          factory: commerceItemArray
        },
        {
          name: 'shippingGroups',
          factory: shippingGroupArray
        },
        {
          name: 'priceInfo',
          factory: priceInfo
        },
        {
          name: 'taxPriceInfo',
          factory: priceInfo
        }
      ])
    },

    /**
     * @property
     * @lends Order
     */
    computeds: {
      value: Entity.prototype.computeds.concat([
        'cartEmpty',
        'unavailableCommerceItems',
        'canCheckout'
      ])
    },

    /**
     * Indicates if the cart is empty, i.e. totalCommerceItemCount == 0.
     *
     * @function
     * @lends Order
     * @return true if totalCommerceItemCount == 0; false otherwise.
     */
    cartEmpty: {
      value: function cartEmpty () {
        return !this.totalCommerceItemCount();
      },
      writable: true
    },

    /**
     * Indicates if any cart item is unavailable.
     *
     * @function
     * @lends Order
     * @returns true if any cart items is unavailable; false all cart items are unavailable.
     */
    unavailableCommerceItems: {
      value: function unavailableCommerceItems () {
        return this.commerceItems().filter(function (commerceItem) {
          return commerceItem.inventoryInfo.availabilityStatus() !== 1000;
        });
      },
      writable: true
    },

    /**
     * Indicates if the order can proceed to checkout.
     *
     * @function
     * @lends Order
     * @returns returns true if the order can proceed to checkout; false if the order cannot proceed to checkout.
     */
    canCheckout: {
      value: function canCheckout () {
        return !this.cartEmpty() && !this.unavailableCommerceItems().length;
      },
      writable: true
    },

    /**
     * Notify the user if there are unavailable items in the cart.
     *
     * @function
     * @lends Order
     */
    notifyUnavailableItems: {
      value: function notifyUnavailableItems () {
        this.unavailableCommerceItems().forEach(function (commerceItem) {
          toastr.error(i18next.t('base.alert.itemUnavailable', [commerceItem.productDisplayName()]));
        });
      }
    }
  });

  return new Order();
});
