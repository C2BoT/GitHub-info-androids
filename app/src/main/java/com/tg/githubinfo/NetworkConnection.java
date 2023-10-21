package com.tg.githubinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.lifecycle.LiveData;

public class NetworkConnection extends LiveData<Boolean> {
    private final Context context;
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnection();
        }
    };

    public NetworkConnection(Context context) {
        this.context = context;
    }

    @Override
    protected void onActive() {
        super.onActive();
        registerReceiver();
        updateConnection();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        unregisterReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    private void unregisterReceiver() {
        context.unregisterReceiver(networkReceiver);
    }

    private void updateConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        setValue(activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }
}
