package com.codingwithmitch.foodrecipes;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

  private static AppExecutors instance;

  public static AppExecutors getInstance() {
    if (instance == null) {
      instance = new AppExecutors();
    }
    return instance;
  }

  private final Executor mDiskIO = Executors.newSingleThreadExecutor();
  private final Executor mMainThreadExecutor = new MainThreadExecutor();

  public Executor getDiskIOExecutor() {
    return mDiskIO;
  }

  public Executor getMainThreadExecutor() {
    return mMainThreadExecutor;
  }

  // Posts things to the main thread
  private static class MainThreadExecutor implements Executor {

    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
      mMainThreadHandler.post(command);
    }
  }
}
