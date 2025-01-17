// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.MethodScope;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.formatting.AggregateMethodInvocation;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;
import static io.vlingo.xoom.codegen.template.model.formatting.Formatters.Fields.Style.VALUE_OBJECT_INITIALIZER;

public class ExchangeReceiversParameter {

    private final String schemaTypeName;
    private final String localTypeName;
    private final String modelProtocol;
    private final String modelMethod;
    private final String modelMethodParameters;
    private final List<String> valueObjectInitializers;

    private final boolean modelFactoryMethod;
    public static List<ExchangeReceiversParameter> from(final Language language,
                                                        final CodeGenerationParameter exchange,
                                                        final List<CodeGenerationParameter> valueObjects) {
        return exchange.retrieveAllRelated(Label.RECEIVER)
                .map(receiver -> new ExchangeReceiversParameter(language, exchange, receiver, valueObjects))
                .collect(Collectors.toList());
    }

    private ExchangeReceiversParameter(final Language language,
                                       final CodeGenerationParameter exchange,
                                       final CodeGenerationParameter receiver,
                                       final List<CodeGenerationParameter> valueObjects) {
        final CodeGenerationParameter aggregateMethod =
                AggregateDetail.methodWithName(exchange.parent(), receiver.retrieveRelatedValue(MODEL_METHOD));

        this.modelMethod = aggregateMethod.value;
        this.modelProtocol = exchange.parent(AGGREGATE).value;
        this.localTypeName = DATA_OBJECT.resolveClassname(exchange.parent().value);
        this.modelMethodParameters = resolveModelMethodParameters(aggregateMethod);
        this.schemaTypeName = Formatter.formatSchemaTypeName(receiver.retrieveOneRelated(SCHEMA));
        this.modelFactoryMethod = aggregateMethod.retrieveRelatedValue(FACTORY_METHOD, Boolean::valueOf);
        this.valueObjectInitializers = resolveValueObjectInitializers(language, receiver, valueObjects);
    }

    private String resolveModelMethodParameters(final CodeGenerationParameter method) {
        final boolean factoryMethod = method.retrieveRelatedValue(Label.FACTORY_METHOD, Boolean::valueOf);
        final MethodScope methodScope = factoryMethod ? MethodScope.STATIC : MethodScope.INSTANCE;
        return new AggregateMethodInvocation("stage", "data").format(method, methodScope);
    }

    private List<String> resolveValueObjectInitializers(final Language language,
                                                        final CodeGenerationParameter receiver,
                                                        final List<CodeGenerationParameter> valueObjects) {
        final CodeGenerationParameter aggregate = receiver.parent(AGGREGATE);
        final CodeGenerationParameter method =
                AggregateDetail.methodWithName(aggregate, receiver.retrieveRelatedValue(MODEL_METHOD));

        return Formatters.Fields.format(VALUE_OBJECT_INITIALIZER, language, method, valueObjects.stream());
    }

    public String getSchemaTypeName() {
        return schemaTypeName;
    }

    public String getLocalTypeName() {
        return localTypeName;
    }

    public String getModelProtocol() {
        return modelProtocol;
    }

    public String getModelActor() {
        return TemplateStandard.AGGREGATE.resolveClassname(modelProtocol);
    }

    public String getModelMethod() {
        return modelMethod;
    }

    public String getModelMethodParameters() {
        return modelMethodParameters;
    }

    public String getModelVariable() {
        return ClassFormatter.simpleNameToAttribute(modelProtocol);
    }

    public List<String> getValueObjectInitializers() {
        return valueObjectInitializers;
    }

    public boolean isModelFactoryMethod() {
        return modelFactoryMethod;
    }

}
