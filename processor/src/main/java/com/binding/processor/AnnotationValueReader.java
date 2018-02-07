package com.binding.processor;

import com.chaining.Chain;
import com.chaining.Guard;

import java.util.Objects;
import java.util.concurrent.Callable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

import io.reactivex.functions.Function;

/**
 * a {@link Function} that can access the Element held in the annotation
 * <p>
 * Created by Ahmed Adel Ismail on 1/10/2018.
 */
class AnnotationValueReader implements Function<Callable<?>, TypeElement> {

    private final ProcessingEnvironment environment;

    AnnotationValueReader(ProcessingEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public TypeElement apply(Callable<?> annotationValueAccessor)
            throws UnsupportedOperationException {

        return Guard.call(annotationValueAccessor)
                .onErrorMap(this::toTypeElement)
                .defaultIfEmpty(null)
                .when(Objects::isNull)
                .then(this::crashOnNull)
                .call();
    }

    private void crashOnNull(TypeElement typeElement) {
        throw new UnsupportedOperationException("you must pass a function that access a value of" +
                " an annotation, hence it will throw a MirroredTypeException that will hold the" +
                " Element that is held by the Annotation");
    }

    private TypeElement toTypeElement(Throwable throwable) {
        return Chain.let(throwable)
                .whenNot(exception -> exception instanceof MirroredTypeException)
                .then(this::delegateException)
                .when(exception -> exception instanceof MirroredTypeException)
                .thenMap(exception -> (MirroredTypeException) exception)
                .map(MirroredTypeException::getTypeMirror)
                .map(environment.getTypeUtils()::asElement)
                .map(element -> (TypeElement) element)
                .defaultIfEmpty(null)
                .call();
    }

    private void delegateException(Throwable throwable) {
        throw new UnsupportedOperationException(throwable);
    }
}
