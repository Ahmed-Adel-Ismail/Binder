package com.dynamic.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

/**
 * a class that encapsulates the registration of a {@link BroadcastReceiver} at Runtime,
 * to complete the registration process, you will need to invoke {@link #register(Context)},
 * which will return the {@link BroadcastReceiver} instance, so that you can unregister from
 * it later
 * <p>
 * Created by Ahmed Adel Ismail on 10/23/2017.
 */
public class DynamicReceiver {

    private final BroadcastReceiver receiver;
    private final IntentFilter filter;
    private String permission;
    private Handler handler;
    private boolean enabled;

    private DynamicReceiver(BroadcastReceiver receiver) {
        this.receiver = receiver;
        this.filter = new IntentFilter();
        this.permission = null;
        this.handler = null;
        this.enabled = true;
    }

    public static DynamicReceiver with(BroadcastReceiver receiver) {
        return new DynamicReceiver(receiver);
    }

    /**
     * add an action to the {@link IntentFilter}, the {@link BroadcastReceiver} will be
     * registered to this action
     *
     * @param action the action to be added
     * @return {@code this} instance for chaining
     */
    public DynamicReceiver action(@NonNull String action) {
        filter.addAction(action);
        return this;
    }

    /**
     * add a permission to this {@link BroadcastReceiver}
     *
     * @param permission the permission that used to be in the manifest
     * @return {@code this} instance for chaining
     */
    public DynamicReceiver permission(@NonNull String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * set the {@link Handler} that will host the {@link BroadcastReceiver}, the default
     * handler is the Main thread handler (decided by the System)
     *
     * @param handler the {@link Handler} that will host the execution of
     *                {@link BroadcastReceiver#onReceive(Context, Intent)}
     * @return {@code this} instance for chaining
     */
    public DynamicReceiver handler(Handler handler) {
        this.handler = handler;
        return this;
    }

    /**
     * set weather the {@link BroadcastReceiver} can be initialized by the system or not,
     * which is the {@code enabled} flag in the manifest ... the default is {@code true}
     * <p>
     * notice that if the {@code application} tag in the manifest have set {@code enabled}
     * to false, this value will do nothing, since the system will not be able to
     * initialize any component of this {@link Application}
     *
     * @param enabled the {@code enabled} state, the default value is {@code true}
     * @return {@code this} instance for chaining
     */
    public DynamicReceiver enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * add a MIME data to the {@link IntentFilter}, note that the passed {@code String}
     * is case-sensitive
     *
     * @param mimeType the MIME data type
     * @return {@code this} instance for chaining
     * @throws RuntimeException if {@link IntentFilter#addDataType(String)} threw
     *                          {@link MalformedMimeTypeException}
     */
    public DynamicReceiver dataMimeType(String mimeType) throws RuntimeException {
        try {
            filter.addDataType(mimeType);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * add a PATH data to {@link IntentFilter},
     * it invokes {@link IntentFilter#addDataPath(String, int)}
     *
     * @param path the path {@code String} passed to {@link IntentFilter#addDataPath(String, int)}
     * @param type the type {@code int} passed to {@link IntentFilter#addDataPath(String, int)}
     * @return {@code this} instance for chaining
     */
    public DynamicReceiver dataPath(String path, int type) {
        filter.addDataPath(path, type);
        return this;
    }

    /**
     * add a AUTHORITY data to {@link IntentFilter},
     * it invokes {@link IntentFilter#addDataAuthority(String, String)}
     *
     * @param host the host {@code String} passed to
     *             {@link IntentFilter#addDataAuthority(String, String)}
     * @param port optional port {@code String} passed to
     *             {@link IntentFilter#addDataAuthority(String, String)}
     * @return {@code this} instance for chaining
     */
    public DynamicReceiver dataAuthority(String host, String port) {
        filter.addDataAuthority(host, port);
        return this;
    }

    /**
     * add a SCHEME data to {@link IntentFilter},
     * it invokes {@link IntentFilter#addDataScheme(String)}
     *
     * @param scheme the scheme {@code String} passed to {@link IntentFilter#addDataScheme(String)}
     * @return {@code this} instance for chaining
     */
    public DynamicReceiver dataScheme(String scheme) {
        filter.addDataScheme(scheme);
        return this;
    }


    /**
     * finalize the registration process and register the {@link BroadcastReceiver}, this method
     * will return the {@link BroadcastReceiver} to be unregistered later, if you are registering
     * to a Sticky {@link Intent} (like Battery state for example), use
     * {@link #registerSticky(Context)}, which will return a {@link StickyRegistration} that
     * holds the {@link BroadcastReceiver} and the {@link Intent}
     *
     * @param context the {@link Context} that will invoke register the {@link BroadcastReceiver}
     * @return the {@link BroadcastReceiver} to unregister later
     */
    @NonNull
    public BroadcastReceiver register(Context context) {
        registerReceiver(context);
        return receiver;
    }

    private Intent registerReceiver(Context context) {
        updateReceiverEnabledSettings(context);
        return context.registerReceiver(receiver, filter, permission, handler);
    }

    private void updateReceiverEnabledSettings(Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                componentName(context), enabledFlag(), DONT_KILL_APP);
    }

    @NonNull
    private ComponentName componentName(Context context) {
        return new ComponentName(context, receiver.getClass());
    }

    private int enabledFlag() {
        return enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
    }

    /**
     * finalize the registration process to a Sticky {@link Intent}
     *
     * @param context the {@link Context} that will invoke register the {@link BroadcastReceiver}
     * @return the {@link StickyRegistration} that holds the {@link BroadcastReceiver} and the
     * {@link Intent} found
     */
    @Nullable
    public StickyRegistration registerSticky(Context context) {
        return new StickyRegistration(receiver, registerReceiver(context));
    }

}
