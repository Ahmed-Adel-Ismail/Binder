[![](https://jitpack.io/v/Ahmed-Adel-Ismail/Binder.svg)](https://jitpack.io/#Ahmed-Adel-Ismail/Binder)

# Binder
An Annotation processor that allows binding two classes with each other, where the first class can listen to the updates of the second class, like in <b>MVVM</b>, when we need our <b>View</b> to subscribe on the <b>View-Model</b>, so when it's variables update, we want our views to be updated as well ... although this library is not limited to <b>MVVM</b>, and also it allows this behavior between any two Objects, but it will be explained on an <b>MVVM</b> example for <b>Android</b> 

# Declaring the View-Model (Subscriptions source)

    public class ViewModel {

        @SubscriptionName("stringSubject")
        final Subject<String> stringSubject = BehaviorSubject.createDefault("");
        
        private final Subject<Integer> intSubject = BehaviorSubject.createDefault(0);

        @SubscriptionName("intSubject")
        Subject<Integer> getIntSubject(){
            return intSubject;
        }

    }

We need to put <b>@SubscriptionName</b> above the source that we need to receive it's updates, weather on the non-private variable, or on the non-private getter, as shown above ... the value passed to the annotation should be unique per class, as shown in the example, the <b>stringSubject</b> variable is annotated with <b>@SubscriptionName("stringSubject")</b>, and the intSubject getter is annotated with <b>@SubscriptionName("intSubject")</b>, the values "stringSubject" and "intSubject" are unique, if one is repeated, an error will occur during compilation

# Declaring our View (Subscribers)

    @SubscriptionsFactory(ViewModel.class)
    public class MainActivity extends AppCompatActivity {

        private Binder<ViewModel> binder;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ...
            this.binder = Binder.bind(this).to(new ViewModel());
			// or :
			// this.binder = Binder.bind(this).toNewSubscriptionsFactory();
        }


        @SubscribeTo("stringSubject")
        Disposable stringSubscriber(Subject<String> subject) {
            return subject.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> Log.e("MainActivity", "stringSubject : "+v));
        }

        @SubscribeTo("intSubject")
        Disposable intSubscriber(Subject<Integer> subject) {
            return subject.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> Log.e("MainActivity", "intSubject : " + v));
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            this.binder.unbind();
        }
    }

The first step is to tell the Annotation Processor where it can find the Subscriptions sources (our View-Model), through the annotation <b>@SubscriptionsFactory</b>

Then we declare our methods that will be invoked in the subscription process, like the following method :

    @SubscribeTo("stringSubject")
    Disposable stringSubscriber(Subject<String> subject) {
        return subject.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "stringSubject : "+v));
    }
    
in our annotation <b>@SubscribeTo</b>, we pass the key that we declared in our <b>View-Model's @SubscriptionName</b> annotation, in this example we subscribe to the <b>Subject<String></b> that was declared in our <b>View-Model</b>
    
after the annotation step, our method should be not-private, and it should return an <b>RxJava 2 Disposable</b>, and it should expect a parameter of the same type of the declared variable in the <b>View-Model</b>

at the end we do the subscription process through calling the below lines :

    this.binder = Binder.bind(this).to(new ViewModel());

the above code will do the binding process and return a <b>Binder</b> which will hold all the Diposables created by our methods, and we then can clear it in our <b>onDestroy()</b> by calling :

	this.binder.unbind();

we can access the <b>View-Model</b> (our Subscriptions Factory) through this getter method :

	this.binder.getSubscriptionsFactory();


Another way to initialize the binding process is to invoke the below lines :

    this.binder = Binder.bind(this).toNewSubscriptionsFactory();

this way, the <b>Binder</b> will create a new instance of the Class mentioned in the <b>@SubscriptionsFactory</b>, but this class should have a default no-args constructor

# Summing up things

Although the example is on an MVVM pattern for Android, this can be applied to any two classes, so in our example, we can then Bind our <b>View-Model</b> to our <b>Inter-actor</b> or <b>Repositories</b>, and so on
    
This library works with Java 8, which can be achieved for android by either working with Gradle 3+, or through using Retrolambda

# Gradle Dependency

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	    compile 'com.github.Ahmed-Adel-Ismail.Binder:binding:0.0.3'
        annotationProcessor 'com.github.Ahmed-Adel-Ismail.Binder:processor:0.0.3'
	}
	
# Pro Guard Rules 

For Pro Guard, you may need to add those lines in the proguard-rules file :
	
	-keep class java.lang.annotation.** { *; }
	-keep class **$$Subscribers { *; }
	-keepclassmembers class ** {
  		@** *;
	}
	
