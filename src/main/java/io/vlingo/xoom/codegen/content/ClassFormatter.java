// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.content;

import java.beans.Introspector;

public class ClassFormatter {

    public static String qualifiedNameOf(final String packageName,
                                         final String className) {
        return packageName + "." + className;
    }

    public static String simpleNameToAttribute(final String simpleName) {
        return Introspector.decapitalize(simpleName);
    }

    public static String qualifiedNameToAttribute(final String qualifiedName) {
        return simpleNameToAttribute(simpleNameOf(qualifiedName));
    }

    public static String simpleNameOf(final String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }

    public static String packageOf(final String qualifiedName) {
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    public static String importAllFrom(final String packageName) {
        return packageName + ".*";
    }
}
