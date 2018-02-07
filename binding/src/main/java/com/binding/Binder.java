package com.binding;

import com.chaining.Chain;
import com.chaining.Optional;
import com.binding.annotations.SubscribeTo;
import com.binding.annotations.SubscriptionName;
import com.binding.annotations.SubscriptionsFactory;

import java.lang.reflect.Constructor;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.binding.annotations.GeneratedNames.GENERATED_SUBSCRIBERS_POSTFIX;

/**
 * a class responsible for binding {@link SubscribeTo} annotated methods to with
 * {@link SubscriptionName} annotated fields and methods, and it returns the result of this
 * binding process in a {@link CompositeDisposable}, you should call
 * {@link CompositeDisposable#clear()} when you want to dispose all the {@link Disposable}
 * Objects that were created
 */
public class Binder<T> {

    private final long id = (long) (Math.random() * 999999);
    private final Object subscriber;
    private final T subscriptionsFactory;
    private final CompositeDisposable compositeDisposable;

    private Binder(Object subscriber,
                   T subscriptionsFactory) {

        this.subscriber = subscriber;
        this.subscriptionsFactory = subscriptionsFactory;
        this.compositeDisposable = createCompositeDisposable();
    }

    private CompositeDisposable createCompositeDisposable() {
        return Chain.let(subscribeToAnnotationsClassName())
                .guardMap(toBinderWithCompositeDisposables())
                .onErrorReturn(withLogErrorAndEmptyDisposable())
                .map(toCompositeDisposable())
                .call();
    }

    private String subscribeToAnnotationsClassName() {
        return subscriber.getClass().getName() + GENERATED_SUBSCRIBERS_POSTFIX;
    }

    private Function<String, Object> toBinderWithCompositeDisposables() {
        return new Function<String, Object>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object apply(String className) throws Exception {
                return ((BiFunction) generateObject(className))
                        .apply(subscriber, subscriptionsFactory);
            }
        };
    }

    private Function<Throwable, Object> withLogErrorAndEmptyDisposable() {
        return new Function<Throwable, Object>() {
            @Override
            public Object apply(Throwable throwable) throws Exception {
                return Chain.let(throwable)
                        .apply(printStackTrace())
                        .to(new CompositeDisposable())
                        .call();
            }
        };
    }

    private Function<Object, CompositeDisposable> toCompositeDisposable() {
        return new Function<Object, CompositeDisposable>() {
            @Override
            public CompositeDisposable apply(Object object) throws Exception {
                return (CompositeDisposable) object;
            }
        };
    }

    private static Object generateObject(String className) {
        return Chain.let(className)
                .map(toClassName())
                .map(toDeclaredConstructor())
                .apply(makeAccessible())
                .map(toNewInstance())
                .call();
    }

    private Consumer<Throwable> printStackTrace() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        };
    }

    private static Function<String, Class<?>> toClassName() {
        return new Function<String, Class<?>>() {
            @Override
            public Class<?> apply(String className) throws Exception {
                return Class.forName(className);
            }
        };
    }

    private static Function<Class<?>, Constructor> toDeclaredConstructor() {
        return new Function<Class<?>, Constructor>() {
            @Override
            public Constructor apply(Class<?> clazz) throws Exception {
                return clazz.getDeclaredConstructor();
            }
        };
    }

    private static Consumer<Constructor> makeAccessible() {
        return new Consumer<Constructor>() {
            @Override
            public void accept(Constructor constructor) throws Exception {
                constructor.setAccessible(true);
            }
        };
    }

    private static Function<Constructor, Object> toNewInstance() {
        return new Function<Constructor, Object>() {
            @Override
            public Object apply(Constructor constructor) throws Exception {
                return constructor.newInstance();
            }
        };
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
        return subscriptionsFactory;
    }

    /**
     * un-bind the Objects, this method clears every thing
     */
    public void unbind() {
        this.compositeDisposable.clear();
    }

    /**
     * used internally by the library
     *
     * @return the ID of the binder
     */
    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int result = subscriber.hashCode();
        result = 31 * result + subscriptionsFactory.hashCode();
        result = 31 * result + compositeDisposable.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Binder)) return false;

        Binder<?> binder = (Binder<?>) o;
        return subscriber.equals(binder.subscriber)
                && subscriptionsFactory.equals(binder.subscriptionsFactory)
                && compositeDisposable.equals(binder.compositeDisposable);
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
            return subscriptionsFactoryClass()
                    .map(toClassName())
                    .map(toGeneratedObject())
                    .map(new Function<Object, Binder<T>>() {
                        @Override
                        public Binder<T> apply(Object o) throws Exception {
                            return (Binder<T>) createSubscriptionBinder(o);
                        }
                    })
                    .defaultIfEmpty(null)
                    .apply(new Consumer<Binder<T>>() {
                        @Override
                        public void accept(Binder<T> binder) throws Exception {
                            crashIfNull(binder);
                        }
                    })
                    .call();
        }


        private Optional<Class<?>> subscriptionsFactoryClass() {
            return Chain.optional(objectWithSubscribeToAnnotations)
                    .map(toClass())
                    .map(toSubscriptionFactoryAnnotation())
                    .map(toSubscriptionFactoryValue());
        }

        private Function<Class<?>, String> toClassName() {
            return new Function<Class<?>, String>() {
                @Override
                public String apply(Class<?> clazz) throws Exception {
                    return clazz.getName();
                }
            };
        }

        private Function<String, Object> toGeneratedObject() {
            return new Function<String, Object>() {
                @Override
                public Object apply(String className) throws Exception {
                    return Binder.generateObject(className);
                }
            };
        }

        private <T> Binder<T> createSubscriptionBinder(
                T objectWithSubscriptionNameAnnotations) {
            return new Binder<>(
                    objectWithSubscribeToAnnotations,
                    objectWithSubscriptionNameAnnotations);
        }

        private void crashIfNull(Binder binder) {
            if (binder == null) {
                throw new UnsupportedOperationException("Object passed to" +
                        " [subscriptionsFactory] parameter is not the same type" +
                        " as the one declared in @" + SubscriptionsFactory.class.getSimpleName() +
                        " ,make sure to declare the @" + SubscriptionsFactory.class.getSimpleName() +
                        " annotation above the Class that holds @" + SubscribeTo.class.getSimpleName() +
                        " annotations, and that the Class declared in @" + SubscriptionsFactory.class.getSimpleName() +
                        " holds the members annotated with @" + SubscriptionName.class.getSimpleName());
            }
        }

        private Function<Object, Class<?>> toClass() {
            return new Function<Object, Class<?>>() {
                @Override
                public Class<?> apply(Object o) throws Exception {
                    return o.getClass();
                }
            };
        }

        private Function<Class<?>, SubscriptionsFactory> toSubscriptionFactoryAnnotation() {
            return new Function<Class<?>, SubscriptionsFactory>() {
                @Override
                public SubscriptionsFactory apply(Class<?> clazz) throws Exception {
                    return clazz.getAnnotation(SubscriptionsFactory.class);
                }
            };
        }

        private Function<SubscriptionsFactory, Class<?>> toSubscriptionFactoryValue() {
            return new Function<SubscriptionsFactory, Class<?>>() {
                @Override
                public Class<?> apply(SubscriptionsFactory subscriptionsFactory) throws Exception {
                    return subscriptionsFactory.value();
                }
            };
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
        public <T> Binder<T> to(final Object subscriptionsFactory) {
            return subscriptionsFactoryClass()
                    .map(toClassName())
                    .when(isSubscriptionFactoryClassName(subscriptionsFactory))
                    .thenTo(subscriptionsFactory)
                    .map(new Function<Object, Binder<T>>() {
                        @Override
                        public Binder<T> apply(Object o) throws Exception {
                            return (Binder<T>) createSubscriptionBinder(o);
                        }
                    })
                    .defaultIfEmpty(null)
                    .apply(new Consumer<Binder<T>>() {
                        @Override
                        public void accept(Binder<T> o) throws Exception {
                            crashIfNull(o);
                        }
                    })
                    .call();
        }

        private Predicate<String> isSubscriptionFactoryClassName(final Object subscriptionsFactory) {
            return new Predicate<String>() {
                @Override
                public boolean test(String s) throws Exception {
                    return subscriptionsFactory.getClass().getName().equals(s);
                }
            };
        }

    }
}
