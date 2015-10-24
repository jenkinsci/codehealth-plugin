package org.jenkinsci.plugins.codehealth.CodehealthPublisher

import org.jenkinsci.plugins.codehealth.provider.duplicates.NoDuplicateCodeProvider
import org.jenkinsci.plugins.codehealth.provider.loc.NoLinesOfCodeProvider

def f = namespace(lib.FormTagLib)

f.section(title: _("Provider plugins")) {
    f.dropdownDescriptorSelector(field: "linesOfCodeProvider", title: _("Lines of Code"), descriptors: NoLinesOfCodeProvider.all())
    f.dropdownDescriptorSelector(field: "duplicateCodeProvider", title: _("Duplicate Code"), descriptors: NoDuplicateCodeProvider.all())
}
