package org.jenkinsci.plugins.codehealth.CodehealthPublisher

import org.jenkinsci.plugins.codehealth.NoLinesOfCodeProvider

def f = namespace(lib.FormTagLib)

f.section(title:_("Lines of Code (LOC) Provider")) {
    f.dropdownDescriptorSelector(field:"linesOfCodeProvider",title:_("LoC"),descriptors:NoLinesOfCodeProvider.allPlusNone())
}
