package com.example.prashanth.porterdemo.rest;

import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ServiceGenerator {

    private static String sAccessToken = null;

    public static void setAccessToken(String accessToken) {
        sAccessToken = accessToken;
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(sLogger);
        // set your desired log level
        //   if(BuildConfig.DEBUG){
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      /*  } else {
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }*/
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        httpClient.addInterceptor(logging);
        /*//increasing connection timeout to 30 secs from default 10 secs
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        // socket timeouts
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(new ErrorHandlingCallAdapterFactory())
                .addConverterFactory(HttpConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(serviceClass);
    }

    private static HttpLoggingInterceptor.Logger sLogger = new HttpLoggingInterceptor.Logger(){
        @Override
        public void log(String message) {
            Log.d("Retrofit", message);
        }
    };

    public static class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

        @Override
        public CallAdapter<HttpCall<?>> get(Type returnType, Annotation[] annotations,
                                            Retrofit retrofit) {
            if (getRawType(returnType) != HttpCall.class) {
                return null;
            }
            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        "HttpCall must have generic type (e.g., HttpCall<ResponseBody>)");
            }
            final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
            final Executor callbackExecutor = retrofit.callbackExecutor();
            return new CallAdapter<HttpCall<?>>() {
                @Override
                public Type responseType() {
                    return responseType;
                }

                @Override
                public <R> HttpCall<R> adapt(Call<R> call) {
                    return new HttpCallAdapter<>(call, callbackExecutor, sAccessToken);
                }
            };
        }
    }
}
