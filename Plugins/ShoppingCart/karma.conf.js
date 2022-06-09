// jshint node: true
var configureKarma = require('../../.karma.conf.js');

module.exports = function (config) {
  config.basePath = '../../';
  config.projectDir = 'Plugins/ShoppingCart';

  configureKarma(config);
};
