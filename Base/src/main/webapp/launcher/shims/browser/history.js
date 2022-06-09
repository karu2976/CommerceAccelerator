// jshint browser: true

/**
 * @module csa/base/launcher/shims/browser/history
 */
define(function () {
  'use strict';

  // Helper method to trigger custom events.
  function triggerCustomEvent (type, detail) {
    // Create a custom event.
    var e = document.createEvent('CustomEvent');
    // Initialize custom event.
    e.initCustomEvent(type, true, false, detail);

    // Trigger the custom event.
    window.dispatchEvent(e);
  }

  // Cache ref to native pushState.
  var nativePushState = history.pushState;

  // Override native pushState.
  history.pushState = function pushState (state, title, URL, triggerPushStateEvent) {
    // Don't tigger push state event by default.
    triggerPushStateEvent = triggerPushStateEvent === true ? true : false;

    // Call native pushState.
    nativePushState.call(history, state, title, URL);

    if (triggerPushStateEvent) {
      var detail = {
        state: state,
        URL: URL
      };

      // Trigger (non-native) 'pushstate' event.
      triggerCustomEvent('pushstate', detail);

      // Trigger (non-native) 'changestate' event.
      triggerCustomEvent('changestate', detail);
    }
  };

  // Cache ref to native replaceState.
  var nativeReplaceState = history.replaceState;

  // Override native replaceState.
  history.replaceState = function replaceState (state, title, URL, triggerReplaceStateEvent) {
    // Don't trigger replace state event by default.
    triggerReplaceStateEvent = triggerReplaceStateEvent === true ? true : false;

    // Call native replaceState.
    nativeReplaceState.call(history, state, title, URL);

    if (triggerReplaceStateEvent) {
      var detail = {
        state: state,
        URL: URL
      };

      // Trigger (non-native) 'replacestate' event.
      triggerCustomEvent('replacestate', detail);

      // Trigger (non-native) 'changestate' event.
      triggerCustomEvent('changestate', detail);
    }
  };

  // This 'unload' listener will ensure that Firefox's bfcache isn't used.
  addEventListener('unload', function () {});

  // Hack to suppress initial popstate event in some webkit browsers.
  // @see - http://dropshado.ws/post/15251622664/ignore-initial-popstate.
  setTimeout(function () {

    // When a 'popstate' event happens (the back or forward button is clicked).
    addEventListener('popstate', function (event) {
      var detail = {
        popstate: true,
        state: event.state,
        URL: location.href
      };

      // Trigger (non-native) 'changestate' event.
      triggerCustomEvent('changestate', detail);
    });

  }, 500);
});
