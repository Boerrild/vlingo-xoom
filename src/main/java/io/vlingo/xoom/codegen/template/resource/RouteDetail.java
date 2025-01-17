// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.http.Method;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.http.Method.*;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class RouteDetail {

    private static final String BODY_DEFAULT_NAME = "data";
    private static final String METHOD_PARAMETER_PATTERN = "final %s %s";
    private static final String METHOD_SIGNATURE_PATTERN = "%s(%s)";
    private static final List<Method> BODY_SUPPORTED_HTTP_METHODS = Arrays.asList(POST, PUT, PATCH);

    public static String resolveBodyName(final CodeGenerationParameter route) {
        final Method httpMethod = route.retrieveRelatedValue(ROUTE_METHOD, Method::from);

        if(!BODY_SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
            return "";
        }

        if(route.hasAny(BODY)) {
            return route.retrieveRelatedValue(BODY);
        }

        return BODY_DEFAULT_NAME;
    }

    public static String resolveBodyType(final CodeGenerationParameter route) {
        final Method httpMethod = route.retrieveRelatedValue(ROUTE_METHOD, Method::from);

        if(!BODY_SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
            return "";
        }

        if(route.parent().isLabeled(AGGREGATE)) {
            return DATA_OBJECT.resolveClassname(route.parent(AGGREGATE).value);
        }

        return route.retrieveRelatedValue(BODY_TYPE);
    }

    public static boolean requireEntityLoad(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAllRelated(ROUTE_SIGNATURE)
                .filter(route -> route.hasAny(REQUIRE_ENTITY_LOADING))
                .anyMatch(route -> route.retrieveRelatedValue(REQUIRE_ENTITY_LOADING, Boolean::valueOf));
    }

    public static boolean requireModelFactory(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAllRelated(ROUTE_SIGNATURE)
                .map(methodSignature -> AggregateDetail.methodWithName(aggregate, methodSignature.value))
                .anyMatch(method -> method.retrieveRelatedValue(FACTORY_METHOD, Boolean::valueOf));
    }

    public static String resolveMethodSignature(final CodeGenerationParameter routeSignature) {
        if(hasValidMethodSignature(routeSignature.value)) {
            return routeSignature.value;
        }

        if(routeSignature.retrieveRelatedValue(Label.ROUTE_METHOD, Method::from).isGET()) {
            return String.format(METHOD_SIGNATURE_PATTERN, routeSignature.value, "");
        }

        return resolveMethodSignatureWithParams(routeSignature);
    }

    public static Stream<CodeGenerationParameter> findInvolvedStateFieldTypes(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAllRelated(ROUTE_SIGNATURE).filter(RouteDetail::hasBody)
                .map(route -> AggregateDetail.methodWithName(aggregate, route.value))
                .flatMap(method -> method.retrieveAllRelated(METHOD_PARAMETER))
                .map(parameter -> AggregateDetail.stateFieldWithName(aggregate, parameter.value));
    }

    private static String resolveMethodSignatureWithParams(final CodeGenerationParameter routeSignature) {
        final String idParameter =
                routeSignature.retrieveRelatedValue(REQUIRE_ENTITY_LOADING, Boolean::valueOf) ?
                        String.format(METHOD_PARAMETER_PATTERN, "String", "id") : "";

        final CodeGenerationParameter method = AggregateDetail.methodWithName(routeSignature.parent(), routeSignature.value);
        final String dataClassname = DATA_OBJECT.resolveClassname(routeSignature.parent().value);
        final String dataParameterDeclaration = String.format(METHOD_PARAMETER_PATTERN, dataClassname, "data");
        final String dataParameter = method.hasAny(METHOD_PARAMETER) ? dataParameterDeclaration : "";
        final String parameters =
                Stream.of(idParameter, dataParameter).filter(param -> !param.isEmpty())
                        .collect(Collectors.joining(", "));
        return String.format(METHOD_SIGNATURE_PATTERN, routeSignature.value, parameters);
    }

    private static boolean hasValidMethodSignature(final String signature ) {
        return signature.contains("(") && signature.contains(")");
    }

    public static CodeGenerationParameter defaultQueryRouteParameter(final CodeGenerationParameter aggregate) {
        return CodeGenerationParameter.of(ROUTE_SIGNATURE, buildQueryAllMethodName(aggregate.value))
                .relate(ROUTE_METHOD, GET).relate(READ_ONLY, "true");
    }

    private static String buildQueryAllMethodName(final String aggregateProtocol) {
        final String formatted = Introspector.decapitalize(aggregateProtocol);
        return formatted.endsWith("s") ? formatted : formatted + "s";
    }

    public static boolean hasBody(final CodeGenerationParameter route) {
        return !resolveBodyName(route).isEmpty();
    }
}
