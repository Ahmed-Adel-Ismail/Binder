package com.vodafone.binding.processor;

import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;

import static com.vodafone.binding.annotations.GeneratedNames.GENERATED_SUBSCRIBERS_POSTFIX;

class GeneratedStrings {

    private final ProcessingEnvironment environment;
    private final String code;
    private final String className;

    GeneratedStrings(ProcessingEnvironment environment, TypeSpec typeSpec, BoundTypes boundTypes) {
        this.environment = environment;
        this.code = generateCodeAsString(typeSpec, boundTypes);
        this.className = generateClassFullName(boundTypes);
    }

    private String generateCodeAsString(TypeSpec typeSpec, BoundTypes boundTypes) {
        return packageLineCode(boundTypes) + typeSpecAsStringCode(typeSpec, boundTypes);
    }

    private String generateClassFullName(BoundTypes boundTypes) {
        return subscriberClassName(boundTypes) + GENERATED_SUBSCRIBERS_POSTFIX;
    }

    private String packageLineCode(BoundTypes boundTypes) {
        return "package " + packageName(boundTypes.getElementWithSubscribeToAnnotations()) + ";\n";
    }

    private String typeSpecAsStringCode(TypeSpec typeSpec, BoundTypes boundTypes) {
        return typeSpec.toString()
                .replace("BiFunction", biFunctionWithGenerics(boundTypes))
                .replace("public ", "\npublic ");
    }

    private String subscriberClassName(BoundTypes boundTypes) {
        return boundTypes.getElementWithSubscribeToAnnotations().getQualifiedName().toString();
    }

    private String packageName(@NonNull TypeElement element) {
        return environment.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }

    private String biFunctionWithGenerics(BoundTypes boundTypes) {
        return "BiFunction<" + subscriberClassName(boundTypes) +
                "," + subscriptionsClassName(boundTypes) +
                "," + CompositeDisposable.class.getName() + ">";
    }

    private String subscriptionsClassName(BoundTypes boundTypes) {
        return boundTypes.getElementWithSubscriptionNameAnnotations().getQualifiedName().toString();
    }

    String getCode() {
        return code;
    }

    String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "GeneratedStrings{" +
                "typeSpec=" + code.replace("\n", "") +
                ", className='" + className + '\'' +
                '}';
    }
}
