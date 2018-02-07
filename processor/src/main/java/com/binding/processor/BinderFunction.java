package com.binding.processor;

import com.chaining.Chain;
import com.functional.curry.Curry;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.List;

import javax.lang.model.element.Modifier;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static com.binding.annotations.GeneratedNames.GENERATED_SUBSCRIBERS_POSTFIX;

class BinderFunction implements Function<Function<BoundTypes, List<String>>, TypeSpec> {

    private final BoundTypes boundTypes;

    BinderFunction(BoundTypes boundTypes) {
        this.boundTypes = boundTypes;
    }

    @Override
    public TypeSpec apply(Function<BoundTypes, List<String>> binderFunctionStatements) throws Exception {
        return TypeSpec.classBuilder(generatedClassName())
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addSuperinterface(TypeVariableName.get(BiFunction.class.getName()))
                .addMethod(applyMethod(binderFunctionStatements))
                .build();
    }

    private String generatedClassName() {
        return boundTypes.getElementWithSubscribeToAnnotations().getSimpleName()
                + GENERATED_SUBSCRIBERS_POSTFIX;
    }

    private MethodSpec applyMethod(Function<BoundTypes, List<String>> binderFunctionStatements) {
        return Chain.let(MethodSpec.methodBuilder("apply"))
                .apply(spec -> spec.addModifiers(Modifier.PUBLIC))
                .apply(spec -> spec.returns(CompositeDisposable.class))
                .apply(spec -> spec.addParameter(subscriberType(), BinderCodeGenerator.VARIABLE_NAME_SUBSCRIBER))
                .apply(spec -> spec.addParameter(subscriptionType(), BinderCodeGenerator.VARIABLE_NAME_SUBSCRIPTIONS))
                .apply(Curry.toConsumer(this::addAllStatements, binderFunctionStatements))
                .map(MethodSpec.Builder::build)
                .call();
    }

    private TypeName subscriberType() {
        return TypeName.get(boundTypes.getElementWithSubscribeToAnnotations().asType());
    }

    private TypeName subscriptionType() {
        return TypeName.get(boundTypes.getElementWithSubscriptionNameAnnotations().asType());
    }

    private void addAllStatements(Function<BoundTypes, List<String>> statements,
                                  MethodSpec.Builder spec) throws Exception {
        Observable.fromIterable(statements.apply(boundTypes)).blockingForEach(spec::addStatement);
    }

}
