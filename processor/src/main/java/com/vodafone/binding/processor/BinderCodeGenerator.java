package com.vodafone.binding.processor;

import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;

import io.reactivex.functions.Function;

class BinderCodeGenerator implements Function<BoundTypes, GeneratedStrings> {

    static final String VARIABLE_NAME_DISPOSABLES = "disposables";
    static final String VARIABLE_NAME_SUBSCRIBER = "subscriber";
    static final String VARIABLE_NAME_SUBSCRIPTIONS = "subscriptions";

    private final ProcessingEnvironment environment;

    BinderCodeGenerator(ProcessingEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public GeneratedStrings apply(BoundTypes boundTypes) throws Exception {
        return new GeneratedStrings(environment, typeSpec(boundTypes), boundTypes);
    }

    private TypeSpec typeSpec(BoundTypes boundTypes) throws Exception {
        return new BinderFunction(boundTypes)
                .apply(new BinderFunctionStatements(environment));
    }


}
