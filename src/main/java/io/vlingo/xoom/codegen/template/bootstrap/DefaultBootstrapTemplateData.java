// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;


import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.REST_RESOURCES;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;

public class DefaultBootstrapTemplateData extends BootstrapTemplateData {

    public DefaultBootstrapTemplateData(final String basePackage,
                                        final String artifactId,
                                        final StorageType storageType,
                                        final Boolean useCQRS,
                                        final Boolean useProjections,
                                        final Boolean useAnnotations,
                                        final List<Content> contents) {
        super(basePackage, artifactId, storageType, useCQRS,
                useProjections, useAnnotations, contents);
    }

    @Override
    protected void enrichParameters(final List<Content> contents) {
        parameters().and(REST_RESOURCES, RestResourcesParameter.from(contents))
                .addImports(ContentQuery.findFullyQualifiedClassNames(REST_RESOURCE, contents));
    }

}
