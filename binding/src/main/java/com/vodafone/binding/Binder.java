package com.vodafone.binding;

import com.chaining.Chain;
import com.chaining.Optional;
import com.vodafone.binding.annotations.SubscribeTo;
import com.vodafone.binding.annotations.SubscriptionName;
import com.vodafone.binding.annotations.SubscriptionsFactory;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

import static com.vodafone.binding.annotations.GeneratedNames.GENERATED_SUBSCRIBERS_POSTFIX;

/**
 * a class responsible for binding {@link SubscribeTo} annotated methods to with
 * {@link SubscriptionName} annotated fields and methods, and it returns the result of this
 * binding process in a {@link CompositeDisposable}, you should call
 * {@link CompositeDisposable#clear()} when you want to dispose all the {@link Disposable}
 * Objects that were created
 */
public class Binder implements Callable<CompositeDisposable> {

    private final Object objectWithSubscribeToAnnotations;
    private final Object objectWithSubscriptionNameAnnotations;

    private Binder(Object objectWithSubscribeToAnnotations,
                   Object objectWithSubscriptionNameAnnotations) {
        this.objectWithSubscribeToAnnotations = objectWithSubscribeToAnnotations;
        this.objectWithSubscriptionNameAnnotations = objectWithSubscriptionNameAnnotations;
    }

    /**
     * subscribe the declared {@link SubscribeTo} annotated methods of the passed Object
     *
     * @param objectWithSubscribeToAnnotations the Object that holds {@link SubscribeTo} annotated
     *                                         methods
     * @return a {@link Binder.Builder} to complete the binding process
     */
    public static Builder subscribe(Object objectWithSubscribeToAnnotations) {
        return new Builder(objectWithSubscribeToAnnotations);
    }

    /**
     * bind the two Objects and generate a {@link CompositeDisposable} that holds all the
     * {@link Disposable} Objects generated
     *
     * @return a {@link CompositeDisposable} that holds all the {@link Disposable} Objects, you can
     * later call {@link CompositeDisposable#clear()}
     */
    @Override
    public CompositeDisposable call() {
        return Chain.let(subscribeToAnnotationsClassName())
                .guardMap(this::invokeBinderCompositeDisposable)
                .onErrorReturn(this::logErrorAndCreateEmptyCompositeDisposable)
                .map(object -> (CompositeDisposable) object)
                .call();
    }

    private String subscribeToAnnotationsClassName() {
        return objectWithSubscribeToAnnotations.getClass().getName() + GENERATED_SUBSCRIBERS_POSTFIX;
    }

    private CompositeDisposable logErrorAndCreateEmptyCompositeDisposable(Throwable throwable) {
        return Chain.let(throwable)
                .apply(Throwable::printStackTrace)
                .to(new CompositeDisposable())
                .call();
    }

    @SuppressWarnings("unchecked")
    private Object invokeBinderCompositeDisposable(String className) throws Exception {
        return ((BiFunction) generateObject(className))
                .apply(objectWithSubscribeToAnnotations, objectWithSubscriptionNameAnnotations);
    }

    private static Object generateObject(String className) {
        return Chain.let(className)
                .map(Class::forName)
                .map(clazz -> clazz.getDeclaredConstructor())
                .apply(constructor -> constructor.setAccessible(true))
                .map(Constructor::newInstance)
                .call();
    }

    /**
     * a {@link Binder.Builder} to complete the binding process
     */
    public static class Builder {

        private final Object objectWithSubscribeToAnnotations;

        private Builder(Object objectWithSubscribeToAnnotations) {
            this.objectWithSubscribeToAnnotations = objectWithSubscribeToAnnotations;
        }

        /**
         * make the Object holding the {@link SubscribeTo} annotations to subscribe to a new
         * instance of the class mentioned in {@link SubscriptionsFactory} ... make sure that
         * this class mentioned in the {@link SubscriptionsFactory} should have a default no-args
         * constructor
         *
         * @return a {@link Binder} that it's {@link #call()} function will result in
         * subscribing all the {@link SubscribeTo} methods to the {@link SubscriptionName} declared
         * in the {@link SubscriptionsFactory} class
         */
        public Binder toNewSubscriptionsFactory() {
            return subscriptionFactoryClassName()
                    .map(Binder::generateObject)
                    .map(this::createSubscriptionBinder)
                    .defaultIfEmpty(null)
                    .apply(this::crashIfNull)
                    .call();
        }

        private Optional<String> subscriptionFactoryClassName() {
            return Chain.optional(objectWithSubscribeToAnnotations)
                    .map(Object::getClass)
                    .map(clazz -> clazz.getAnnotation(SubscriptionsFactory.class))
                    .map(SubscriptionsFactory::value)
                    .map(Class::getName);
        }

        private Binder createSubscriptionBinder(
                Object objectWithSubscriptionNameAnnotations) {

            return new Binder(
                    objectWithSubscribeToAnnotations,
                    objectWithSubscriptionNameAnnotations);
        }

        /**
         * pass the Object that holds the declared {@link SubscriptionName} annoteted fields or
         * methods, this step will create a {@link Binder} that holds both Objects to
         * bind
         *
         * @param objectWithSubscriptionNameAnnotations the Object that holds
         *                                              {@link SubscriptionName} on it's elements
         * @return a {@link Binder} to complete the process when calling it's
         * {@link Binder#call()}
         */
        public Binder to(Object objectWithSubscriptionNameAnnotations) {
            return subscriptionFactoryClassName()
                    .when(objectWithSubscriptionNameAnnotations.getClass().getName()::equals)
                    .thenTo(objectWithSubscriptionNameAnnotations)
                    .map(this::createSubscriptionBinder)
                    .defaultIfEmpty(null)
                    .apply(this::crashIfNull)
                    .call();
        }

        private void crashIfNull(Binder binder) {
            if (binder == null) {
                throw new UnsupportedOperationException("Object passed to" +
                        " [objectWithSubscriptionNameAnnotations] parameter is not the same type" +
                        " as the one declared in @" + SubscriptionsFactory.class.getSimpleName() +
                        " ,make sure to declare the @" + SubscriptionsFactory.class.getSimpleName() +
                        " annotation above the Class that holds @" + SubscribeTo.class.getSimpleName() +
                        " annotations, and that the Class declared in @" + SubscriptionsFactory.class.getSimpleName() +
                        " holds the members annotated with @" + SubscriptionName.class.getSimpleName());
            }
        }
    }
}
