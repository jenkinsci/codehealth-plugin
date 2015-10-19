var builder = require('jenkins-js-builder');

builder.defineTasks(['test','bundle','rebundle']);

builder.bundle('src/main/js/codehealth.js')
    .withExternalModuleMapping('jquery-detached', 'jquery-detached:jquery2')
    .withExternalModuleMapping('bootstrap-detached', 'bootstrap:bootstrap3')
    .withExternalModuleMapping('jqueryui-detached', 'jquery-detached:jqueryui1')
    .inDir('src/main/webapp/bundles');


