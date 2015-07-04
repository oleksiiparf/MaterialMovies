package com.example.roodie.model.util;

import com.example.roodie.model.network.BackgroundCallRunnable;
import com.example.roodie.model.network.NetworkCallRunnable;

/**
 * Created by Roodie on 25.06.2015.
 */
public interface BackgroundExecutor {

    public <R> void execute(NetworkCallRunnable<R> runnable);

    public <R> void execute(BackgroundCallRunnable<R> runnable);
}
