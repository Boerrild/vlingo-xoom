// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.EXCHANGE;
import static io.vlingo.xoom.codegen.parameter.Label.ROLE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class ExchangeReceiverHolderTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final Language language,
                                          final String exchangePackage,
                                          final Stream<CodeGenerationParameter> aggregates,
                                          final List<CodeGenerationParameter> valueObjects,
                                          final List<Content> contents) {
        return aggregates.flatMap(aggregate -> aggregate.retrieveAllRelated(EXCHANGE))
                .filter(exchange -> exchange.retrieveRelatedValue(ROLE, ExchangeRole::of).isConsumer())
                .map(exchange -> new ExchangeReceiverHolderTemplateData(language, exchangePackage, exchange, valueObjects, contents))
                .collect(Collectors.toList());
    }

    private ExchangeReceiverHolderTemplateData(final Language language,
                                               final String exchangePackage,
                                               final CodeGenerationParameter exchange,
                                               final List<CodeGenerationParameter> valueObjects,
                                               final List<Content> contents) {
        final List<ExchangeReceiversParameter> receiversParameters =
                ExchangeReceiversParameter.from(language, exchange, valueObjects);

        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, exchangePackage)
                        .and(AGGREGATE_PROTOCOL_NAME, exchange.parent().value)
                        .andResolve(EXCHANGE_RECEIVER_HOLDER_NAME, params -> standard().resolveClassname(params))
                        .addImports(resolveImports(exchange, receiversParameters, contents))
                        .and(EXCHANGE_RECEIVERS, receiversParameters);
    }

    private Set<String> resolveImports(final CodeGenerationParameter exchange,
                                       final List<ExchangeReceiversParameter> receiversParameters,
                                       final List<Content> contents) {
        final CodeGenerationParameter aggregate = exchange.parent();

        final List<TemplateStandard> standards =
                Stream.of(DATA_OBJECT, AGGREGATE_PROTOCOL).collect(Collectors.toList());

        if(receiversParameters.stream().anyMatch(receiver -> !receiver.isModelFactoryMethod())) {
            standards.add(TemplateStandard.AGGREGATE);
        }

        final Set<String> imports = standards.stream().map(standard -> {
                final String typeName = standard.resolveClassname(aggregate.value);
                return ContentQuery.findFullyQualifiedClassName(standard, typeName, contents);
            }).collect(Collectors.toSet());

        final Stream<CodeGenerationParameter> involvedStateFields =
                ExchangeDetail.findInvolvedStateFieldsOnReceivers(exchange);

        final Set<String> valueObjects =
                ValueObjectDetail.resolveImports(contents, involvedStateFields);

        return Stream.of(imports, valueObjects).flatMap(Set::stream).collect(Collectors.toSet());
    }


    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return EXCHANGE_RECEIVER_HOLDER;
    }

}
