// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;

public class ProjectionTemplateDataFactory {

    private static ProjectionTemplateDataFactory instance;

    public static List<TemplateData> build(final CodeGenerationContext context) {
        if(instance == null) {
            instance = new ProjectionTemplateDataFactory();
        }
        return context.isInternalGeneration() ?
                Arrays.asList(instance.handleInternalGeneration(context)) :
                instance.handleExternalGeneration(context);
    }

    private TemplateData handleInternalGeneration(final CodeGenerationContext context) {
        return ProjectionDispatcherProviderTemplateData.from(context.parametersOf(PROJECTION_ACTOR), context.contents());
    }

    private List<TemplateData> handleExternalGeneration(final CodeGenerationContext context) {
        final String basePackage = context.parameterOf(PACKAGE);

        final ProjectionType projectionType =
                context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);

        final StorageType storageType =
                context.parameterOf(STORAGE_TYPE, StorageType::valueOf);

        final Set<String> aggregateProtocols =
                ContentQuery.findClassNames(AGGREGATE_PROTOCOL, context.contents());

        final List<TemplateData> templatesData = new ArrayList<>();
        templatesData.add(ProjectionSourceTypesTemplateData.from(basePackage, projectionType, context.contents()));

        if(!context.parameterOf(USE_ANNOTATIONS, Boolean::valueOf)) {
            templatesData.add(ProjectionDispatcherProviderTemplateData.from(basePackage,
                    projectionType, context.contents()));
        }

        aggregateProtocols.forEach(protocolName -> {
            templatesData.add(ProjectionTemplateData.from(basePackage, protocolName,
                            context.contents(), projectionType, storageType, templatesData));
        });

        return templatesData;
    }

}
