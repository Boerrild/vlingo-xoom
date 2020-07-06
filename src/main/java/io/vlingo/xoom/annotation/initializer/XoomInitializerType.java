// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.List;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;

public class XoomInitializerType {

    public static TypeSpec from(final ProcessingEnvironment environment,
                                final String basePackage,
                                final Element bootstrapClass) {

        return TypeSpec.classBuilder(XOOM_INITIALIZER_CLASS_NAME)
                .addFields(XoomInitializerFields.from(basePackage))
                .addMethods(XoomInitializerMethods.from(environment, bootstrapClass))
                .build();
    }

}