package com.vodafone.binding;

import com.chaining.Chain;
import com.chaining.Optional;
import com.vodafone.binding.annotations.SubscribeTo;
import com.vodafone.binding.annotations.SubscriptionName;
import com.vodafone.binding.annotations.SubscriptionsFactory;

import java.lang.reflect.Constructor;

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
public class Binder<T> {

    private final Object subscriber;
    private final T subscriptionFactory;
    private final CompositeDisposable compositeDisposable;

    private Binder(Object subscriber,
                   T subscriptionFactory) {

        this.subscriber = subscriber;
        this.subscriptionFactory = subscriptionFactory;
        this.compositeDisposable = createCompositeDisposable();
    }

    private CompositeDisposable createCompositeDisposable() {
        return Chain.let(subscribeToAnnotationsClassName())
                .guardMap(this::invokeBinderCompositeDisposable)
                .onErrorReturn(this::logErrorAndCreateEmptyCompositeDisposable)
                .map(object -> (CompositeDisposable) object)
                .call();
    }

    private String subscribeToAnnotationsClassName() {
        return subscriber.getClass().getName() + GENERATED_SUBSCRIBERS_POSTFIX;
    }

    /**
     * bind the declared {@link SubscribeTo} annotated methods of the passed Object
     *
     * @param subscriber the Object that holds {@link SubscribeTo} annotated
     *                   methods
     * @return a {@link Binder.Builder} to complete the binding process
     */
    public static Builder bind(Object subscriber) {
        return new Builder(subscriber);
    }

    /**
     * get the Object mentioned in {@link SubscriptionsFactory},
     * which holds the {@link SubscriptionName} annotations
     *
     * @return the Object that was mentioned in {@link SubscriptionsFactory}
     */
    public T getSubscriptionsFactory() {
        return subscriptionFactory;
    }

    /**
     * un-bind the Objects, this method clears every thing
     */
    public void unbind() {
        this.compositeDisposable.clear();
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
                .apply(subscriber, subscriptionFactory);
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
         * make the Object holding the {@link SubscribeTo} annotations to bind to a new
         * instance of the class mentioned in {@link SubscriptionsFactory} ... make sure that
         * this class mentioned in the {@link SubscriptionsFactory} should have a default no-args
         * constructor
         *
         * @return a {@link Binder} that subscribes all the {@link SubscribeTo} methods to the
         * {@link SubscriptionName} declared in the {@link SubscriptionsFactory} class
         */
        @SuppressWarnings("unchecked")
        public <T> Binder<T> toNewSubscriptionsFactory() {
            return (Binder<T>) subscriptionFactoryClassName()
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

        private <T> Binder<T> createSubscriptionBinder(
                T objectWithSubscriptionNameAnnotations) {
            return new Binder<>(
                    objectWithSubscribeToAnnotations,
                    objectWithSubscriptionNameAnnotations);
        }

        /**
         * pass the Object that holds the declared {@link SubscriptionName} annotated fields or
         * methods, this step will create a {@link Binder} that holds both Objects to
         * bind
         *
         * @param subscriptionsFactory the Object that holds
         *                             {@link SubscriptionName} on it's elements
         * @return a {@link Binder} to complete the process
         */
        @SuppressWarnings("unchecked")
        public <T> Binder<T> to(Object subscriptionsFactory) {
            return (Binder<T>) subscriptionFactoryClassName()
                    .when(subscriptionsFactory.getClass().getName()::equals)
                    .thenTo(subscriptionsFactory)
                    .map(this::createSubscriptionBinder)
                    .defaultIfEmpty(null)
                    .apply(this::crashIfNull)
                    .call();
        }

        private void crashIfNull(Binder binder) {
            if (binder == null) {
                throw new UnsupportedOperationException("Object passed to" +
                        " [subscriptionFactory] parameter is not the same type" +
                        " as the one declared in @" + SubscriptionsFactory.class.getSimpleName() +
                        " ,make sure to declare the @" + SubscriptionsFactory.class.getSimpleName() +
                        " annotation above the Class that holds @" + SubscribeTo.class.getSimpleName() +
                        " annotations, and that the Class declared in @" + SubscriptionsFactory.class.getSimpleName() +
                        " holds the members annotated with @" + SubscriptionName.class.getSimpleName());
            }
        }
    }
}
