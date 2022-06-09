/**
 * @module csa/base/application/models/model-array
 * @alias module:model-array
 * @requires module:libs
 * @requires module:model
 */
define([
  'libs',
  'model'
], function (libs, Model) {
  'use strict';

  var ko = libs.ko;

  //
  var modelArrayDescriptor = {

    /**
     *
     */
    Model: {
      value: Model,
      configurable: true
    },

    /**
     *
     */
    modelId: {
      value: function modelId (model) {
        var id = ko.unwrap(model[this.Model.prototype.idAttribute]);

        return id;
      },
      configurable: true
    },

    /**
     *
     */
    ids: {
      value: function ids (array) {
        return ko.unwrap(array || this).map(function (item) {
          var id = this.modelId(item);

          if (id == null) {
            throw new Error('A Model must have an id.');
          }
          else {
            return id;
          }
        }, this);
      }
    },

    /**
     *
     */
    diff: {
      value: function diff (newArray) {
        var oldIdArray = this.ids();
        var newIdArray = this.ids(newArray);
        var editScript = ko.utils.compareArrays(oldIdArray, newIdArray);

        return editScript;
      }
    },

    /**
     *
     */
    patch: {
      value: function patch (editScript, newArray) {
        var index = 0;

        editScript.forEach(function (editAction) {
          if (editAction.status === 'added') {
            // Add
            this.splice(index, 0, new this.Model().set(newArray[editAction.index]));
            index++;
          }
          else if (editAction.status === 'deleted') {
            // Delete
            this.splice(index, 1);
          }
          else {
            // Update
            ko.unwrap(this)[index].set(newArray[index]);
            index++;
          }
        }, this);

        return this;
      }
    },

    /**
     *
     */
    set: {
      value: function set (newArray) {
        this.patch(this.diff(newArray), newArray);

        return this;
      }
    }
  };

  return function modelArray () {
    return Object.defineProperties(ko.observableArray(), modelArrayDescriptor);
  };
});
