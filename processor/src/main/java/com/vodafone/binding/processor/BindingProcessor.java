package com.vodafone.binding.processor;

import com.chaining.Chain;
import com.google.auto.service.AutoService;
import com.vodafone.binding.annotations.SubscribeTo;
import com.vodafone.binding.annotations.SubscriptionName;
import com.vodafone.binding.annotations.SubscriptionsFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class BindingProcessor extends AbstractProcessor {

    private static final Set<String> supportedAnnotationTypes;

    static {
        supportedAnnotationTypes = Chain.let(new LinkedHashSet<String>())
                .apply(set -> set.add(SubscriptionsFactory.class.getCanonicalName()))
                .apply(set -> set.add(SubscribeTo.class.getCanonicalName()))
                .apply(set -> set.add(SubscriptionName.class.getCanonicalName()))
                .call();
    }

    private ProcessingEnvironment processingEnvironment;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.processingEnvironment = processingEnvironment;
        Log.setEnvironment(processingEnvironment);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotationTypes;

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return !roundEnvironment.processingOver() &&
                new Processing(processingEnvironment).test(set, roundEnvironment);
    }
}

