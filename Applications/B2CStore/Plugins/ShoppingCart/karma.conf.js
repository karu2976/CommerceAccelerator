// jshint node: true
var configureKarma = require('../../../../.karma.conf.js');

module.exports = function (config) {
  config.basePath = '../../../../';
  config.projectDir = 'Applications/B2CStore/Plugins/ShoppingCart';

  configureKarma(config);
};
