package org.jenkinsci.plugins.codehealth.CodehealthPublisher

import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCodeProvider
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider

def f = namespace(lib.FormTagLib)

f.section(title: _("Provider plugins")) {
    f.dropdownDescriptorSelector(field: "linesOfCodeProvider", title: _("Lines of Code"), descriptors: LinesOfCodeProvider.allDescriptors())
    f.dropdownDescriptorSelector(field: "duplicateCodeProvider", title: _("Duplicate Code"), descriptors: DuplicateCodeProvider.allDescriptors())
}
