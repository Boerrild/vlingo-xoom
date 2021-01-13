// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class ExchangeGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String exchangePackage = resolvePackage(context);
        final Stream<CodeGenerationParameter> aggregates = context.parametersOf(AGGREGATE);
        return ExchangeTemplateDataFactory.build(exchangePackage, aggregates, context.contents());
    }

    private String resolvePackage(final CodeGenerationContext context) {
        return String.format("%.%.%", context.parameterOf(PACKAGE), "infrastructure", "exchange");
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        if(!context.hasParameter(AGGREGATE)) {
            return false;
        }
        return context.parametersOf(AGGREGATE).anyMatch(aggregate -> aggregate.hasAny(EXCHANGE));
    }

}