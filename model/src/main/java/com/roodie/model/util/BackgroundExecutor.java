package com.roodie.model.util;

import com.roodie.model.network.BackgroundCallRunnable;
import com.roodie.model.network.NetworkCallRunnable;

/**
 * Created by Roodie on 25.06.2015.
 */
public interface BackgroundExecutor {

    <R> void execute(NetworkCallRunnable<R> runnable);

    <R> void execute(BackgroundCallRunnable<R> runnable);
}
