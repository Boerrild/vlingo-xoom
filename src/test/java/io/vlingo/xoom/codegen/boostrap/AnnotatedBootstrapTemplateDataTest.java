// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.boostrap;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.*;
import io.vlingo.xoom.codegen.bootstrap.BootstrapTemplateData;
import io.vlingo.xoom.codegen.bootstrap.ProviderParameter;
import io.vlingo.xoom.codegen.bootstrap.TypeRegistryParameter;
import io.vlingo.xoom.codegen.storage.StorageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.CodeTemplateParameter.*;
import static io.vlingo.xoom.codegen.CodeTemplateStandard.*;

public class AnnotatedBootstrapTemplateDataTest {

    @Test
    public void testBootstrapTemplateDataGenerationWithCQRSAndProjections() {
        final List<Content> contents =
                Arrays.asList(
                        Content.with(REST_RESOURCE, new File(Paths.get(RESOURCE_PACKAGE_PATH, "AuthorResource.java").toString()), AUTHOR_RESOURCE_CONTENT),
                        Content.with(REST_RESOURCE, new File(Paths.get(RESOURCE_PACKAGE_PATH, "BookResource.java").toString()), BOOK_RESOURCE_CONTENT),
                        Content.with(STORE_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "CommandModelStateStoreProvider.java").toString()), COMMAND_MODEL_STORE_PROVIDER_CONTENT),
                        Content.with(STORE_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java").toString()), QUERY_MODEL_STORE_PROVIDER_CONTENT),
                        Content.with(PROJECTION_DISPATCHER_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java").toString()), PROJECTION_DISPATCHER_PROVIDER_CONTENT)
                );

        final TemplateData bootstrapTemplateData =
                BootstrapTemplateData.from("io.vlingo.xoomapp", PROJECT_PATH, "xoom-app",
                        StorageType.STATE_STORE, true, true, true, contents);

        final CodeTemplateParameters parameters =
                bootstrapTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, parameters.find(PACKAGE_NAME));
        Assert.assertEquals(5, parameters.<List>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.CommandModelStateStoreProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.QueryModelStateStoreProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.ProjectionDispatcherProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(2).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.lattice.model.stateful.StatefulTypeRegistry", parameters.<List<ImportParameter>>find(IMPORTS).get(3).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoom.annotation.initializer.ResourceHandlers", parameters.<List<ImportParameter>>find(IMPORTS).get(4).getQualifiedClassName());

        Assert.assertEquals("io.vlingo.xoomapp.resource", parameters.find(CodeTemplateParameter.REST_RESOURCE_PACKAGE));

        Assert.assertEquals(3, parameters.<List>find(PROVIDERS).size());
        Assert.assertEquals("QueryModelStateStoreProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getInitialization());
        Assert.assertEquals("stage, statefulTypeRegistry", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getArguments());
        Assert.assertEquals("final ProjectionDispatcherProvider projectionDispatcherProvider = ProjectionDispatcherProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(1).getInitialization());
        Assert.assertEquals("stage", parameters.<List<ProviderParameter>>find(PROVIDERS).get(1).getArguments());
        Assert.assertEquals("CommandModelStateStoreProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(2).getInitialization());
        Assert.assertEquals("stage, statefulTypeRegistry, projectionDispatcherProvider.storeDispatcher", parameters.<List<ProviderParameter>>find(PROVIDERS).get(2).getArguments());

        Assert.assertEquals(1, parameters.<List>find(TYPE_REGISTRIES).size());
        Assert.assertEquals("StatefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getClassName());
        Assert.assertEquals("statefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getObjectName());

        Assert.assertEquals(true, parameters.find(USE_PROJECTIONS));
        Assert.assertEquals("xoom-app", parameters.find(APPLICATION_NAME));
    }

    @Test
    public void testBootstrapTemplateDataGenerationWithoutCQRSAndProjections() {
        final List<Content> contents =
                Arrays.asList(
                        Content.with(REST_RESOURCE, new File(Paths.get(RESOURCE_PACKAGE_PATH, "AuthorResource.java").toString()), AUTHOR_RESOURCE_CONTENT),
                        Content.with(STORE_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "StateStoreProvider.java").toString()), SINGLE_MODEL_STORE_PROVIDER_CONTENT)
                );

        final TemplateData bootstrapTemplateData =
                BootstrapTemplateData.from("io.vlingo.xoomapp", PROJECT_PATH, "xoom-app",
                        StorageType.STATE_STORE, false, false, true, contents);

        final CodeTemplateParameters parameters =
                bootstrapTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, parameters.find(PACKAGE_NAME));
        Assert.assertEquals(3, parameters.<List>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.StateStoreProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.lattice.model.stateful.StatefulTypeRegistry", parameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoom.annotation.initializer.ResourceHandlers", parameters.<List<ImportParameter>>find(IMPORTS).get(2).getQualifiedClassName());

        Assert.assertEquals("io.vlingo.xoomapp.resource", parameters.find(CodeTemplateParameter.REST_RESOURCE_PACKAGE));

        Assert.assertEquals(1, parameters.<List>find(PROVIDERS).size());
        Assert.assertEquals("StateStoreProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getInitialization());
        Assert.assertEquals("stage, statefulTypeRegistry", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getArguments());

        Assert.assertEquals(1, parameters.<List>find(TYPE_REGISTRIES).size());
        Assert.assertEquals("StatefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getClassName());
        Assert.assertEquals("statefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getObjectName());

        Assert.assertEquals(false, parameters.find(USE_PROJECTIONS));
        Assert.assertEquals("xoom-app", parameters.find(APPLICATION_NAME));
    }

    private static final String EXPECTED_PACKAGE = "io.vlingo.xoomapp.infrastructure";

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String RESOURCE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "resource").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "persistence").toString();

    private static final String AUTHOR_RESOURCE_CONTENT =
            "package io.vlingo.xoomapp.resource; \\n" +
                    "public class AuthorResource { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_RESOURCE_CONTENT =
            "package io.vlingo.xoomapp.resource; \\n" +
                    "public class BookResource { \\n" +
                    "... \\n" +
                    "}";

    private static final String COMMAND_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class CommandModelStateStoreProvider { \\n" +
                    "... \\n" +
                    "}";

    private static final String QUERY_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class QueryModelStateStoreProvider { \\n" +
                    "... \\n" +
                    "}";

    private static final String SINGLE_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class StateStoreProvider { \\n" +
                    "... \\n" +
                    "}";

    private static final String PROJECTION_DISPATCHER_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class ProjectionDispatcherProvider { \\n" +
                    "... \\n" +
                    "}";
}