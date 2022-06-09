/**
 * @module csa/base/application/behaviors/bindings/bootstrap-accordion-binding
 * @alias module:bootstrap-accordion-binding
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var $ = libs.$;
  var ko = libs.ko;

  /**
   *
   */
  ko.bindingHandlers.accordion = {

    /**
     * The bootstrap accordion events.
     */
    EVENTS: [
      'show.bs.collapse',
      'shown.bs.collapse',
      'hide.bs.collapse',
      'hidden.bs.collapse'
    ],

    /*
     * Get accordion widget options from binding arguments.
     */
    getOptions: function (valueAccessor, viewModel) {
      // Read the widget options
      var options = valueAccessor() || viewModel;

      // If options is an observable
      if (ko.isObservable(options)) {
        // Convert it to an options object
        options = {
          toggle: options
        };
      }

      return options;
    },

    /**
     *
     */
    init: function (element, valueAccessor, allBindings, viewModel) {
      // The binding element MUST have the class "collapse"
      if (!element.classList.contains('collapse')) {
        throw new Error('The bootstrap accordion root element must have the CSS class "collapse".');
      }

      // Get the widget options
      var options = ko.bindingHandlers.accordion.getOptions(valueAccessor, viewModel);

      // The binding options MUST have a writable observable "toggle"
      if (!ko.isWriteableObservable(options.toggle)) {
        throw new Error('The bootstrap accordion binding options MUST provide an observable "toggle".');
      }

      // The binding options MUST have a parent property
      if (!ko.unwrap(options.parent)) {
        throw new Error('The bootstrap accordion binding options MUST provide the property "parent". Parent is the selector for the accordion container; it ensures only one accordion panel can be expanded at a time');
      }

      // Get jQuery wrapped element
      var $element = $(element);

      // Delete widget data if was previously initialised
      $element.removeData('bs.collapse');

      // When the accordion is toggled
      $element.on('show.bs.collapse hide.bs.collapse', function (e) {
        // Update the toggle observable to the correct state
        options.toggle(e.type === 'show' ? true : false);
      });

      // For each bootstrap accordion event
      ko.bindingHandlers.accordion.EVENTS.forEach(function (event) {
        // Get the corresponding listener from options
        var listener = options[event];

        // If the listener is a function
        if (typeof listener === 'function') {
          // Bind the listener to the event
          $element.on(event, listener.bind(viewModel));
        }
      });

      // Update the widget state when options.toggle changes
      var updateWidgetFromObservable = ko.computed(function () {
        // When options.toggle is true
        if (options.toggle()) {
          // If the widget IS initialised
          if ($element.data('bs.collapse')) {
            // Show the accordion
            $element.collapse('show');
          }
        }
        // Otherwise
        else {
          // If the widget IS initialised
          if ($element.data('bs.collapse')) {
            // Hide the accordion
            $element.collapse('hide');
          }
        }
      });

      // When element is removed
      ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
        // Dispose computed functions
        updateWidgetFromObservable.dispose();

        // Ensure the accordion is closed
        $element.collapse('hide');

        // Unbind all the boostrap accordion event listeners
        $element.off('.bs.collapse');
      });
    },

    /**
     *
     */
    update: function (element, valueAccessor, allBindings, viewModel) {
      // Get the widget options
      var options = ko.bindingHandlers.accordion.getOptions(valueAccessor, viewModel);

      // Create/update the widget with new options
      $(element).collapse(ko.toJS(options));
    }
  };

  return ko;
});
