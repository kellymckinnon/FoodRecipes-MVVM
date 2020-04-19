package com.codingwithmitch.foodrecipes.util;

import android.util.Log;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse.ApiErrorResponse;

/**
 * @param <CacheObject> type for the Resource data
 * @param <RequestObject> type for the API response
 */
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

  private static final String TAG = "NetworkBoundResource";

  private AppExecutors mAppExecutors;

  private MediatorLiveData<Resource<CacheObject>> mResults = new MediatorLiveData<>();

  public NetworkBoundResource(AppExecutors appExecutors) {
    mAppExecutors = appExecutors;
    init();
  }

  private void init() {
    // Update LiveData for loading status
    mResults.setValue(Resource.<CacheObject>loading(null /* data */));

    // Observe LiveData source from local db
    final LiveData<CacheObject> dbSource = loadFromDb();

    mResults.addSource(
        dbSource,
        new Observer<CacheObject>() {

          @Override
          public void onChanged(CacheObject cacheObject) {
            mResults.removeSource(dbSource);

            if (shouldFetch(cacheObject)) {
              fetchFromNetwork(dbSource);
            } else {
              mResults.addSource(
                  dbSource,
                  new Observer<CacheObject>() {
                    @Override
                    public void onChanged(CacheObject cacheObject) {
                      setValue(Resource.success(cacheObject));
                    }
                  });
            }
          }
        });
  }

  /**
   * 1) observe local db 2) if <condition/> query the network 3) stop observing the local db 4)
   * insert new data into local db 5) begin observing local db again to see refreshed data from
   * network
   *
   * @param dbSource
   */
  private void fetchFromNetwork(final LiveData<CacheObject> dbSource) {
    Log.d(TAG, "fetchFromNetwork: called.");

    // Update LiveData for loading status
    mResults.addSource(
        dbSource,
        new Observer<CacheObject>() {
          @Override
          public void onChanged(CacheObject cacheObject) {
            setValue(Resource.loading(cacheObject));
          }
        });

    final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();
    mResults.addSource(
        apiResponse,
        new Observer<ApiResponse<RequestObject>>() {
          @Override
          public void onChanged(final ApiResponse<RequestObject> requestObjectApiResponse) {
            mResults.removeSource(dbSource);
            mResults.removeSource(apiResponse);

            /* 3 cases: ApiSuccessResponse, ApiErrorResponse, ApiEmptyResponse */

            if (requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse) {
              Log.d(TAG, "onChanged: AppSuccessResponse");

              // Must use a background worker for saving to the DB
              mAppExecutors
                  .getDiskIOExecutor()
                  .execute(
                      new Runnable() {
                        @Override
                        public void run() {
                          // Save the responses to the local DB.
                          saveCallResult(
                              (RequestObject)
                                  processResponse(
                                      (ApiResponse.ApiSuccessResponse) requestObjectApiResponse));

                          mAppExecutors
                              .getMainThreadExecutor()
                              .execute(
                                  new Runnable() {
                                    @Override
                                    public void run() {
                                      mResults.addSource(
                                          loadFromDb(),
                                          new Observer<CacheObject>() {
                                            @Override
                                            public void onChanged(CacheObject cacheObject) {
                                              setValue(Resource.success(cacheObject));
                                            }
                                          });
                                    }
                                  });
                        }
                      });
            } else if (requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse) {
              Log.d(TAG, "onChanged: ApiEmptyResponse");
              mAppExecutors
                  .getMainThreadExecutor()
                  .execute(
                      new Runnable() {
                        @Override
                        public void run() {
                          mResults.addSource(
                              loadFromDb(),
                              new Observer<CacheObject>() {
                                @Override
                                public void onChanged(CacheObject cacheObject) {
                                  setValue(Resource.success(cacheObject));
                                }
                              });
                        }
                      });
            } else if (requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse) {
              Log.d(TAG, "onChanged: ApiErrorResponse");
              mResults.addSource(
                  dbSource,
                  new Observer<CacheObject>() {
                    @Override
                    public void onChanged(CacheObject cacheObject) {
                      setValue(
                          Resource.error(
                              ((ApiErrorResponse) requestObjectApiResponse).getErrorMessage(),
                              cacheObject));
                    }
                  });
            }
          }
        });
  }

  private CacheObject processResponse(ApiResponse.ApiSuccessResponse response) {
    return (CacheObject) response.getBody();
  }

  private void setValue(Resource<CacheObject> newValue) {
    if (mResults.getValue() != newValue) {
      mResults.setValue(newValue);
    }
  }

  /** Called to save the result of the API response into the database */
  @WorkerThread
  protected abstract void saveCallResult(@NonNull RequestObject item);

  /**
   * Called with the data in the database to decide whether to fetch potentially updated data from
   * the network.
   */
  @MainThread
  protected abstract boolean shouldFetch(@Nullable CacheObject data);

  /** Called to get the cached data from the database. */
  @NonNull
  @MainThread
  protected abstract LiveData<CacheObject> loadFromDb();

  /** Called to create the API call */
  @NonNull
  @MainThread
  protected abstract LiveData<ApiResponse<RequestObject>> createCall();

  /**
   * Returns a LiveData object that represents the resource that's implemented in the base class.
   */
  public final LiveData<Resource<CacheObject>> getAsLiveData() {
    return mResults;
  }
}
