package org.jenkinsci.plugins.codehealth.CodehealthPublisher

import org.jenkinsci.plugins.codehealth.NoDuplicateCodeProvider
import org.jenkinsci.plugins.codehealth.NoLinesOfCodeProvider

def f = namespace(lib.FormTagLib)

f.section(title: _("Provider plugins")) {
    f.dropdownDescriptorSelector(field: "linesOfCodeProvider", title: _("Lines of Code"), descriptors: NoLinesOfCodeProvider.allPlusNone())
    f.dropdownDescriptorSelector(field: "duplicateCodeProvider", title: _("Duplicate Code"), descriptors: NoDuplicateCodeProvider.allPlusNone())
}
