// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.TextExpectation;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.template.OutputFile;
import io.vlingo.xoom.codegen.template.storage.StorageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class ProjectionGenerationStepTest {

    private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";
    private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();
    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    @Test
    public void testThatEventBasedProjectionClassesAreGeneratedForSourcedEntities() throws IOException {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, StorageType.JOURNAL.name(), ProjectionType.EVENT_BASED.name());
        loadContents(context);
        new ProjectionGenerationStep().process(context);

        final Content bookProjectionActor =
                context.findContent(PROJECTION, "BookProjectionActor");

        final Content authorProjectionActor =
                context.findContent(PROJECTION, "AuthorProjectionActor");

        final Content dispatcherProvider =
                context.findContent(PROJECTION_DISPATCHER_PROVIDER, "ProjectionDispatcherProvider");

        Assert.assertTrue(authorProjectionActor.contains(TextExpectation.onJava().read("event-based-author-projection-actor-for-sourced-entities")));
        Assert.assertTrue(bookProjectionActor.contains(TextExpectation.onJava().read("event-based-book-projection-actor-for-sourced-entities")));
        Assert.assertTrue(dispatcherProvider.contains(TextExpectation.onJava().read("projection-dispatcher-provider")));
    }

    @Test
    public void testThatEventBasedProjectionClassesAreGeneratedForStatefulEntities() throws IOException {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, StorageType.STATE_STORE.name(), ProjectionType.EVENT_BASED.name());
        loadContents(context);
        new ProjectionGenerationStep().process(context);

        final Content bookProjectionActor =
                context.findContent(PROJECTION, "BookProjectionActor");

        final Content authorProjectionActor =
                context.findContent(PROJECTION, "AuthorProjectionActor");

        final Content dispatcherProvider =
                context.findContent(PROJECTION_DISPATCHER_PROVIDER, "ProjectionDispatcherProvider");

        Assert.assertTrue(authorProjectionActor.contains(TextExpectation.onJava().read("event-based-author-projection-actor-for-stateful-entities")));
        Assert.assertTrue(bookProjectionActor.contains(TextExpectation.onJava().read("event-based-book-projection-actor-for-stateful-entities")));
        Assert.assertTrue(dispatcherProvider.contains(TextExpectation.onJava().read("projection-dispatcher-provider")));
    }

    @Test
    public void testThatOperationBasedProjectionClassesAreGenerated() throws IOException {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, StorageType.STATE_STORE.name(), ProjectionType.OPERATION_BASED.name());
        loadContents(context);
        new ProjectionGenerationStep().process(context);

        final Content bookProjectionActor =
                context.findContent(PROJECTION, "BookProjectionActor");

        final Content authorProjectionActor =
                context.findContent(PROJECTION, "AuthorProjectionActor");

        final Content dispatcherProvider =
                context.findContent(PROJECTION_DISPATCHER_PROVIDER, "ProjectionDispatcherProvider");

        Assert.assertTrue(authorProjectionActor.contains(TextExpectation.onJava().read("operation-based-author-projection-actor")));
        Assert.assertTrue(bookProjectionActor.contains(TextExpectation.onJava().read("operation-based-book-projection-actor")));
        Assert.assertTrue(dispatcherProvider.contains(TextExpectation.onJava().read("projection-dispatcher-provider")));
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), AUTHOR_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), BOOK_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), AUTHOR_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorRated.java"), AUTHOR_RATED_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), BOOK_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookSoldOut.java"), BOOK_SOLD_OUT_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookPurchased.java"), BOOK_PURCHASED_CONTENT_TEXT);
        context.addContent(DATA_OBJECT, new OutputFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), AUTHOR_DATA_CONTENT_TEXT);
        context.addContent(DATA_OBJECT, new OutputFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), BOOK_DATA_CONTENT_TEXT);
    }

    private void loadParameters(final CodeGenerationContext context, final String storage, final String projections) {
        context.with(PACKAGE, "io.vlingo").with(APPLICATION_NAME, "xoomapp")
                .with(STORAGE_TYPE, storage).with(PROJECTION_TYPE, projections)
                .with(TARGET_FOLDER, HOME_DIRECTORY);
    }

    private static final String AUTHOR_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorState { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookState { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public interface Author { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public interface Book { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_SOLD_OUT_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookSoldOut extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_PURCHASED_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookPurchased extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_RATED_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorRated extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String  AUTHOR_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class AuthorData { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class BookData { \\n" +
                    "... \\n" +
                    "}";
}