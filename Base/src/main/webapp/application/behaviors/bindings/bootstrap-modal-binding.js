/**
 * @module csa/base/application/behaviors/bindings/bootstrap-modal-binding
 * @alias module:bootstrap-modal-binding
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
  ko.bindingHandlers.modal = {

    /**
     * The bootstrap modal events.
     */
    EVENTS: [
      'show.bs.modal',
      'shown.bs.modal',
      'hide.bs.modal',
      'hidden.bs.modal',
      'loaded.bs.modal'
    ],

    /*
     * Get modal widget options from binding arguments.
     */
    getOptions: function (valueAccessor, viewModel) {
      // Read the widget options
      var options = valueAccessor() || viewModel;

      // If options is an observable
      if (ko.isObservable(options)) {
        // Convert it to an options object
        options = {
          show: options
        };
      }

      return options;
    },

    /**
     *
     */
    init: function (element, valueAccessor, allBindings, viewModel) {
      // The binding element MUST have the class "modal"
      if (!element.classList.contains('modal')) {
        throw new Error('The bootstrap model root element must have a class "modal".');
      }

      // Get the widget options
      var options = ko.bindingHandlers.modal.getOptions(valueAccessor, viewModel);

      // The binding options MUST a writable observable "show"
      if (!ko.isWriteableObservable(options.show)) {
        throw new Error('The bootstrap model binding options MUST provide an observable "show".');
      }

      // Get jQuery wrapped element
      var $element = $(element);

      // Delete widget data if was previously initialised
      $element.removeData('bs.modal');

      // When the modal is show/hidden
      $element.on('show.bs.modal hide.bs.modal', function (e) {
        // Update the show observable to the correct state
        options.show(e.type === 'show' ? true : false);
      });

      // For each bootstrap modal event
      ko.bindingHandlers.modal.EVENTS.forEach(function (event) {
        // Get the corresponding listener from options
        var listener = options[event];

        // If the listener is a function
        if (typeof listener === 'function') {
          // Bind the listener to the event
          $element.on(event, listener.bind(viewModel));
        }
      });

      // Update the widget state when options.show changes
      var updateWidgetFromObservable = ko.computed(function () {
        // When options.show is true
        if (options.show()) {
          // If the widget IS initialised
          if ($element.data('bs.modal')) {
            // Show the modal
            $element.modal('show');
          }
        }
        // Otherwise
        else {
          // If the widget IS initialised
          if ($element.data('bs.modal')) {
            // Hide the modal
            $element.modal('hide');
          }
        }
      });

      // When element is removed
      ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
        // Dispose computed functions
        updateWidgetFromObservable.dispose();

        // Ensure the modal is closed
        $element.modal('hide');

        // Explicitly remove backdrop, as normal removal fails for modals with fade animation.
        $element.modal('removeBackdrop');

        // Unbind all the boostrap modal event listeners
        $element.off('.bs.modal');
      });
    },

    /**
     *
     */
    update: function (element, valueAccessor, allBindings, viewModel) {
      // Get the widget options
      var options = ko.bindingHandlers.modal.getOptions(valueAccessor, viewModel);

      // Create/update the widget with new options
      $(element).modal(ko.toJS(options));
    }
  };

  return ko;
});
