// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import com.google.auto.service.AutoService;
import io.vlingo.xoom.annotation.AnnotationProcessor;
import io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator;
import io.vlingo.xoom.annotation.initializer.XoomValidator;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
public class PersistenceProcessor extends AnnotationProcessor {

    @Override
    protected void generate(final Set<? extends Element> annotatedElements) {
        PersistenceValidator.instance().validate(annotatedElements);
        PersistenceConfigurationGenerator.instance().generateFrom(environment, annotatedElements);
    }

    @Override
    public Class<?> annotationClass() {
        return Persistence.class;
    }

}
