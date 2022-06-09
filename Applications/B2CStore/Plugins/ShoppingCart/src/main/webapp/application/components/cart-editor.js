/**
 * This extended component overrides the template property so that it can reference some
 * more application specific properties.
 *
 * @module csa/plugins/shopping-cart/application/components/cart-editor
 * @requires module:csa/plugins/shopping-cart/application/components/cart-editor
 * @requires module:text!./cart-editor.html
 */
define([
  'csa/plugins/shopping-cart/application/components/cart-editor',
  'text!./cart-editor.html'
], function (cartEditor, template) {
  'use strict';

  // Override the component template.
  cartEditor.template = template;

  // The component defintion.
  return cartEditor;
});
