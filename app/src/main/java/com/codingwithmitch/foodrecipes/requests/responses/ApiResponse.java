package com.codingwithmitch.foodrecipes.requests.responses;

import java.io.IOException;
import retrofit2.Response;

/** Generic class for handling Retrofit requests */
public class ApiResponse<T> {

  public class ApiSuccessResponse<T> extends ApiResponse<T> {
    private final T mBody;

    ApiSuccessResponse(T body) {
      mBody = body;
    }

    public T getBody() {
      return mBody;
    }
  }

  public class ApiErrorResponse<T> extends ApiResponse<T> {
    private final String mErrorMessage;

    ApiErrorResponse(String errorMessage) {
      mErrorMessage = errorMessage;
    }

    public String getErrorMessage() {
      return mErrorMessage;
    }
  }

  // When the response is 200 but empty
  public class ApiEmptyResponse<T> extends ApiResponse<T> {}

  public ApiResponse<T> create(Throwable error) {
    return new ApiErrorResponse<>(
        error.getMessage().equals("")
            ? "Unknown error. Check network connection"
            : error.getMessage());
  }

  public ApiResponse<T> create(Response<T> response) {
    if (response.isSuccessful()) {
      T body = response.body();

      if (body == null || response.code() == 204 /* empty response */) {
        return new ApiEmptyResponse<>();
      } else {
        return new ApiSuccessResponse<>(body);
      }
    } else {
      String errorMsg;
      try {
        errorMsg = response.errorBody().string();
      } catch (IOException e) {
        e.printStackTrace();
        errorMsg = response.message();
      }
      return new ApiErrorResponse<>(errorMsg);
    }
  }
}
