package com.binding.processor;

import com.chaining.Chain;
import com.functional.curry.Curry;
import com.binding.annotations.SubscriptionsFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import io.reactivex.Observable;
import io.reactivex.functions.BiPredicate;


/**
 * a class that handles processing annotations
 * <p>
 * Created by Ahmed Adel Ismail on 6/25/2017.
 */
class Processing implements BiPredicate<Set<? extends TypeElement>, RoundEnvironment> {

    private final ProcessingEnvironment environment;


    Processing(ProcessingEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public boolean test(Set<? extends TypeElement> typeElements, RoundEnvironment round) {
        Chain.optional(round.getElementsAnnotatedWith(SubscriptionsFactory.class))
                .defaultIfEmpty(new LinkedHashSet<>())
                .flatMap(Observable::fromIterable)
                .map(Curry.toFunction(BoundTypes::new, environment))
                .map(new BinderCodeGenerator(environment))
                .blockingForEach(this::generateFile);

        return false;
    }

    private void generateFile(GeneratedStrings generatedStrings) throws IOException {
        JavaFileObject source = environment.getFiler().createSourceFile(generatedStrings.getClassName());
        Writer writer = source.openWriter();
        writer.write(generatedStrings.getCode());
        writer.flush();
        writer.close();

    }



}