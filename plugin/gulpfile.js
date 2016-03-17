var builder = require('@jenkins-cd/js-builder');

builder.defineTasks(['test', 'bundle', 'rebundle']);

builder.bundle('src/main/js/codehealth.js')
    .withExternalModuleMapping('jquery-detached', 'jquery-detached:jquery2')
    .withExternalModuleMapping('bootstrap-detached', 'bootstrap:bootstrap3', {addDefaultCSS: true})
    .withExternalModuleMapping('handlebars', 'handlebars:handlebars3')
    .withExternalModuleMapping('moment', 'momentjs:momentjs2')
    .withExternalModuleMapping('numeral', 'numeraljs:numeraljs1')
    .inDir('src/main/webapp/bundles');
