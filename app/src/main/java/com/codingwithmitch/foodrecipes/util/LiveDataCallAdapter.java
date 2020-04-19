package com.codingwithmitch.foodrecipes.util;

import androidx.lifecycle.LiveData;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/** Convert generic Retrofit responses to LiveData */
class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

  private final Type responseType;

  LiveDataCallAdapter(Type responseType) {
    this.responseType = responseType;
  }

  @Override
  public Type responseType() {
    return responseType;
  }

  @Override
  public LiveData<ApiResponse<R>> adapt(final Call<R> call) {
    return new LiveData<ApiResponse<R>>() {
      @Override
      protected void onActive() {
        super.onActive();
        final ApiResponse<R> apiResponse = new ApiResponse<>();
        call.enqueue(new Callback<R>() {
          @Override
          public void onResponse(Call<R> call, Response<R> response) {
            postValue(apiResponse.create(response));
          }

          @Override
          public void onFailure(Call<R> call, Throwable t) {
            postValue(apiResponse.create(t));
          }
        });
      }
    };
  }
}
