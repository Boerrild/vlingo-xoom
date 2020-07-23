// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.TextBasedContent;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class BootstrapGenerationStepTest {

    @Test
    public void testDefaultBootstrapGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, false);
        loadContents(context);

        new BootstrapGenerationStep().process(context);

        Assert.assertEquals(6, context.contents().size());
        Assert.assertEquals("Bootstrap", context.contents().get(5).retrieveClassName());
        Assert.assertTrue(context.contents().get(5).contains("final ProjectionDispatcherProvider projectionDispatcherProvider"));
        Assert.assertTrue(context.contents().get(5).contains("CommandModelStateStoreProvider.using(stage, statefulTypeRegistry, projectionDispatcherProvider.storeDispatcher)"));
        Assert.assertTrue(context.contents().get(5).contains("QueryModelStateStoreProvider.using(stage, statefulTypeRegistry)"));
        Assert.assertTrue(context.contents().get(5).contains("final AuthorResource authorResource = new AuthorResource(stage);"));
        Assert.assertTrue(context.contents().get(5).contains("final BookResource bookResource = new BookResource(stage);"));
        Assert.assertTrue(context.contents().get(5).contains("authorResource.routes(),"));
        Assert.assertTrue(context.contents().get(5).contains("bookResource.routes()"));
        Assert.assertFalse(context.contents().get(5).contains("bookResource.routes(),"));
    }

    @Test
    public void testAnnotatedBootstrapGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, true);
        loadContents(context);

        new BootstrapGenerationStep().process(context);

        Assert.assertEquals(6, context.contents().size());
        Assert.assertEquals("Bootstrap", context.contents().get(5).retrieveClassName());
        Assert.assertTrue(context.contents().get(5).contains("final ProjectionDispatcherProvider projectionDispatcherProvider"));
        Assert.assertTrue(context.contents().get(5).contains("CommandModelStateStoreProvider.using(stage, statefulTypeRegistry, projectionDispatcherProvider.storeDispatcher)"));
        Assert.assertTrue(context.contents().get(5).contains("QueryModelStateStoreProvider.using(stage, statefulTypeRegistry)"));
        Assert.assertTrue(context.contents().get(5).contains("@ResourceHandlers(packages = \"io.vlingo.xoomapp.resource\")"));
        Assert.assertEquals(Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "Bootstrap.java").toString(), ((TextBasedContent) context.contents().get(5)).file.getAbsolutePath());
    }

    private void loadParameters(final CodeGenerationContext context, final Boolean useAnnotation) {
        context.with(PACKAGE, "io.vlingo.xoomapp").with(APPLICATION_NAME, "xoom-app").with(CQRS, "true")
                .with(TARGET_FOLDER, HOME_DIRECTORY).with(STORAGE_TYPE, "STATE_STORE")
                .with(PROJECTIONS, ProjectionType.OPERATION_BASED.name())
                .with(ANNOTATIONS, useAnnotation.toString());
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "AuthorResource.java"), AUTHOR_RESOURCE_CONTENT);
        context.addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "BookResource.java"), BOOK_RESOURCE_CONTENT);
        context.addContent(STORAGE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "CommandModelStateStoreProvider.java"), COMMAND_MODEL_STORE_PROVIDER_CONTENT);
        context.addContent(STORAGE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"), QUERY_MODEL_STORE_PROVIDER_CONTENT);
        context.addContent(PROJECTION_DISPATCHER_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), PROJECTION_DISPATCHER_PROVIDER_CONTENT);
    }

    private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";

    private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();

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

    private static final String PROJECTION_DISPATCHER_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class ProjectionDispatcherProvider { \\n" +
                    "... \\n" +
                    "}";

}