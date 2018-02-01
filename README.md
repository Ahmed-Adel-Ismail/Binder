[![](https://jitpack.io/v/Ahmed-Adel-Ismail/Binder.svg)](https://jitpack.io/#Ahmed-Adel-Ismail/Binder)

# Binder
An Annotation processor that allows binding two classes with each other, where the first class can listen to the updates of the second class, like in <b>MVVM</b>, when we need our <b>View</b> to subscribe on the <b>View-Model</b>, so when it's variables update, we want our views to be updated as well ... although this library is not limited to <b>MVVM</b>, and also it allows this behavior between any two Objects, but it will be explained on an <b>MVVM</b> example for <b>Android</b> 

# Declaring the View-Model (Subscriptions source)

    public class ViewModel extends android.arch.lifecycle.ViewModel {

        @SubscriptionName("stringLiveData")
        final MutableLiveData<String> stringLiveData = new MutableLiveData<>();
        
        private final Subject<Integer> intSubject = BehaviorSubject.createDefault(0);

        @SubscriptionName("intSubject")
        Subject<Integer> getIntSubject(){
            return intSubject;
        }
        
        void clear(){
            intSubject.complete();
        }

    }

We need to put <b>@SubscriptionName</b> above the source that we need to receive it's updates, weather on the non-private variable, or on the non-private getter, as shown above ... the value passed to the annotation should be unique per class, as shown in the example, the <b>stringLiveData</b> variable is annotated with <b>@SubscriptionName("stringLiveData")</b>, and the <b>intSubject</b> getter is annotated with <b>@SubscriptionName("intSubject")</b>, the values "stringSubject" and "intSubject" are unique, if one is repeated, an error will occur during compilation

# Declaring our View (Subscribers)

    @SubscriptionsFactory(ViewModel.class)
    public class MainActivity extends AppCompatActivity {

        private Binder<ViewModel> binder;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ...
            ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);
            binder = Binder.bind(this).to(viewModel);
        }


        @SubscribeTo("stringLiveData")
        void stringLiveDataSubscriber(MutableLiveData<String> liveData) {
            liveData.observe(this, text -> Log.e("MainActivity", "liveData : " + text));
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
            
            binder.unbind();
            
            if(isFinishing()){
                binder.getSubscriptionsFactory().clear()
            }
        }
    }

The first step is to tell the Annotation Processor where it can find the Subscriptions sources (our View-Model), through the annotation <b>@SubscriptionsFactory</b>

Then we declare our methods that will be invoked in the subscription process, like the following method :

    @SubscribeTo("stringLiveData")
    void stringLiveDataSubscriber(MutableLiveData<String> liveData) {
        liveData.observe(this, text -> Log.e("MainActivity", "liveData : " + text));
    }
    
    @SubscribeTo("intSubject")
    Disposable intSubscriber(Subject<Integer> subject) {
        return subject.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "intSubject : " + v));
    }
    
in our annotation <b>@SubscribeTo</b>, we pass the key that we declared in our <b>View-Model's @SubscriptionName</b> annotation, in this example we subscribe to the <b>Subject<Integer> intSubject</b> that was declared in our <b>View-Model</b>
    
after the annotation step, our method should be : 

    - not private
    - it can return an RxJava 2 Disposable to be able to dispose it in Binder.unbind()
    - it can return any other type (or void), but in this case it's return value will be ignored and will not be affected by the call to Binder.unbind() (which is exactly what we want with LiveData in Android)
    - it should expect a parameter of the same type of the declared variable in the View-Model

at the end we do the subscription process through calling the below lines :

    binder = Binder.bind(this).to(viewModel);

the above code will do the binding process and return a <b>Binder</b> which will hold all the Disposables created by our methods, and we then can clear it in our <b>onDestroy()</b> by calling :

	binder.unbind();

we can access the <b>View-Model</b> (our Subscriptions Factory) through this getter method :

	binder.getSubscriptionsFactory();


Another way to initialize the binding process is to invoke the below lines :

    binder = Binder.bind(this).toNewSubscriptionsFactory();

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
	    compile 'com.github.Ahmed-Adel-Ismail.Binder:binding:0.1.0'
        annotationProcessor 'com.github.Ahmed-Adel-Ismail.Binder:processor:0.1.0'
	}
	
# Android Support

starting from version 0.1.0, there is Support for Android as follows :

# Gradle Dependency for Android

	dependencies {
	    compile 'com.github.Ahmed-Adel-Ismail.Binder:android:0.1.0'
        annotationProcessor 'com.github.Ahmed-Adel-Ismail.Binder:processor:0.1.0'
	}

# Update your Application class 

	@Override
    public void onCreate() {
        super.onCreate();
        Binding.integrate(this);
    }
	
# Implement MVVM with Binding processor on Activities and Fragments

You do not need to handle the Binding operations any more, just declare the annotations in your <b>Activity</b> or <b>android.support.v4.app.Fragment</b> as follows :

	@SubscriptionsFactory(MainViewModel.class)
	public class MainActivity extends AppCompatActivity {

		@Override
		protected void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
		}

		@SubscribeTo("stringSubject")
		Disposable stringSubscriber(Subject<String> subject) {
			return subject.share()
					.subscribeOn(Schedulers.computation())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(v -> Log.e("MainActivity", "stringSubject : " + v));
		}
	}
	
	
	@SubscriptionsFactory(MainViewModel.class)
	public class MainFragment extends Fragment {

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.fragment_main,container);
		}

		@SubscribeTo("stringSubject")
		Disposable stringSubscriber(Subject<String> subject) {
			return subject.share()
					.subscribeOn(Schedulers.computation())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(v -> Log.e("MainFragment", "stringSubject : " + v));
		}
	}

And declare the annotations in your ViewModel as follows :

	public class MainViewModel extends android.arch.lifecycle.ViewModel {

		@SubscriptionName("stringSubject")
		final Subject<String> stringSubject = PublishSubject.create();
		
		void clear(){
			stringSubject.onComplete();
		}	
	}
	
Since the <b>MainViewModel</b> extends the new Architecture components <b>ViewModel</b>, it will be shared accross all the Fragments and there Activity, if it does not extend the <b>android.arch.lifecycle.ViewModel</b>, a new instance will be created for each, which means that an instance will be created for The MainActivity, and another will be created to MainFragment

If you want the <b>MainViewModel</b> to not extend the <b>android.arch.lifecycle.ViewModel</b>, and be shared between the Activity and it's Fragments, you can annotate the class with <b>@SharedSubscriptionFactory</b>, as follows :

	@SharedSubscriptionFactory
	public class MainViewModel {

		@SubscriptionName("stringSubject")
		final Subject<String> stringSubject = PublishSubject.create();
		
		void clear(){
			stringSubject.onComplete();
		}
	}
	
if you have a method that will clear / destroy your <b>ViewModel</b>, like the <i>clear()</i> method in the <b>MainViewModel</b>, you can annotate it with <b>@OnSubscriptionsClosed</b>, this will cause the <b>Binder</b> to call it when the Activity / Fragment is totally destroyed (not rotating), and if this ViewModel is shared between Activity and it's Fragments, this method will be invoked when the Activity is totally destroyed (not rotating), so our <b>ViewModel</b> will look like this :

	public class MainViewModel {

		@SubscriptionName("stringSubject")
		final Subject<String> stringSubject = PublishSubject.create();
		
		@OnSubscriptionsClosed
		void clear(){
			stringSubject.onComplete();
		}
	}
	
Notice that <b>@OnSubscriptionsClosed</b> will cause the <i>clear()</i> method to be invoked on any type of <b>ViewModel</b>

# Pro Guard Rules 

For Pro Guard, you may need to add those lines in the proguard-rules file :
	
	-keep class java.lang.annotation.** { *; }
	-keep class **$$Subscribers { *; }
	-keepclassmembers class ** {
  		@** *;
	}

* For Pro Guard rules, I'm waiting for your feed-back as this is under development still
