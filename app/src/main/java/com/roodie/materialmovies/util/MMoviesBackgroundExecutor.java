package com.roodie.materialmovies.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.model.network.BackgroundCallRunnable;
import com.roodie.model.network.NetworkCallRunnable;
import com.roodie.model.util.BackgroundExecutor;

import java.util.concurrent.ExecutorService;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MMoviesBackgroundExecutor implements BackgroundExecutor {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private final ExecutorService mExecutorService;

    public MMoviesBackgroundExecutor(ExecutorService mExecutorService) {
        this.mExecutorService = Preconditions.checkNotNull(mExecutorService, "executorService cannot be null");
    }

    @Override
    public <R> void execute(NetworkCallRunnable<R> runnable) {
        mExecutorService.execute(new NetworkCallRunner<>(runnable));
    }

    @Override
    public <R> void execute(BackgroundCallRunnable<R> runnable) {
        mExecutorService.execute(new BackgroundCallRunner<>(runnable));
    }


    //********************************************************//
    private class BackgroundCallRunner<R> implements  Runnable {
        private final BackgroundCallRunnable<R> mBackgroundRunnable;

        public BackgroundCallRunner(BackgroundCallRunnable<R> mBackgroundRunnable) {
            this.mBackgroundRunnable = mBackgroundRunnable;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBackgroundRunnable.preExecute();
                }
            });

            R result = mBackgroundRunnable.runAsync();

            sHandler.post(new ResultCallback(result));
        }

        private class ResultCallback implements Runnable {
            private final R mResult;

            public ResultCallback(R mResult) {
                this.mResult = mResult;
            }

            @Override
            public void run() {
                mBackgroundRunnable.postExecute(mResult);
            }
        }
    }
    //********************************************************//

     class NetworkCallRunner<R> implements Runnable {

         private final NetworkCallRunnable<R> mBackgroundRunnable;

         NetworkCallRunner(NetworkCallRunnable<R> runnable) {
            this.mBackgroundRunnable = runnable;
         }

         @Override
         public void run() {
             android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

             sHandler.post(new Runnable() {
                 @Override
                 public void run() {
                     mBackgroundRunnable.onPreTmdbCall();
                 }
             });

             R result = null;
             RetrofitError retrofitError = null;

             try {
                 result = mBackgroundRunnable.doBackgroundCall();
             } catch (RetrofitError re) {
                 retrofitError = re;
                 Log.d(((Object) this).getClass().getSimpleName(), "Error on completing network call", re);

             }

             sHandler.post(new ResultCallback(result, retrofitError));
         }

         private class ResultCallback implements Runnable {
             private final R mResult;
             private final RetrofitError mRetrofitError;

             public ResultCallback(R mResult, RetrofitError mRetrofitError) {
                 this.mResult = mResult;
                 this.mRetrofitError = mRetrofitError;
             }

             @Override
             public void run() {
                 if (mResult != null) {
                     mBackgroundRunnable.onSuccess(mResult);
                 } else if (mRetrofitError != null) {
                     mBackgroundRunnable.onError(mRetrofitError);
                 }
                 mBackgroundRunnable.onFinished();
             }
         }
     }





}
