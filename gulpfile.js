var builder = require('jenkins-js-builder');

builder.defineTasks(['test','bundle','rebundle']);

builder.bundle('src/main/js/codehealth.js')
    .withExternalModuleMapping('jquery-detached', 'jquery-detached:jquery2')
    .inDir('src/main/webapp/bundles');


