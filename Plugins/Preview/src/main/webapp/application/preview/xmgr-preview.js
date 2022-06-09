/**
 * This script is for enabling Xmgr Preview.
 * It checks for the preview environment and defines appropriate bindings.
 *
 * @module csa/plugins/preview/application/preview/xmgr-preview
 * @requires module:include-endeca-preview-scripts
 * @requires module:append-endeca-preview-attribute
 * @requires module:browser
 * @requires module:libs
 */
define([
  'include-endeca-preview-scripts',
  'append-endeca-preview-attribute',
  'browser',
  'libs'
], function (includeEndecaPreviewScripts, appendEndecaPreviewAttribute, browser, libs) {
  'use strict';

  var ko = libs.ko;

  /**
   * @see {@link http://knockoutjs.com/documentation/binding-preprocessing.html Knockout's binding using preprocessing}
   *
   * <ol>
   *   <li> Adds a preprocess function to a binding handler.</li>
   *   <li> Automatically handles chaining.</li>
   *   <li> It adds endecaPreview binding where ever contentItem binding is added.</li>
   * </ol>
   */
  ko.punches.utils.setBindingPreprocessor(ko.bindingHandlers.component, function (value, name, addBinding) {

    // Add binding where ever component binding is present.
    addBinding('endecaPreview');
    return value || ' ';
  });

  /**
   * This defines a the knockout custom binding ko.bindingHandlers.endecaPreview.
   *
   * @exports CSA/Plugins/XmgrPreview/xmgr-preview
   *
   * @see {@link http://knockoutjs.com/documentation/custom-bindings.html knockout custom bindings}
   */
  // Define the contentItem binding.
  ko.bindingHandlers.endecaPreview = {

    /**
     * The 'init' function is called once when a endecaPreview binding is initilalized.
     * The sequence of execution is:
     * <ol>
     *   <li>Get the preview module url, i.e. bindingContext.$root.previewModuleUrl.
     *     It will be there only when preview is enabled.</li>
     *   <li>IncludeEndecaPreviewScripts method will import
     *     all the scripts and css files related to endeca preview.</li>
     *   <li>AppendEndecaPreviewAttribute method will append a <div>
     *     with 'data-oc-audit-info' attribute.</li>
     * </ol>
     *
     * @function
     *   @param {Element} element - The DOM element, to which, the binding is being applied.
     *   @param {Function} valueAccessor - The value accessor.
     *   @param {Object} allBindings - The allBinding accessor.
     *   @param {Object} viewModel - The current view model.
     */

    init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
      var previewModuleUrl = ko.unwrap(bindingContext.$root.previewModuleUrl());

      // Check if preview is enabled and that the application is running in an iframe.
      if (previewModuleUrl && (window.parent !== window)) {

        // Import all preview related endeca scripts files.
        includeEndecaPreviewScripts(previewModuleUrl);

        require(['preview'], function (ocPreview) {

          ocPreview.initialize(ko.toJS(bindingContext.$root.__raw__));

          // Create and attach endeca related tags and handlers.
          appendEndecaPreviewAttribute(viewModel.__raw__(), element, ocPreview);
        });
      }
    }
  };

  return ko;
});
