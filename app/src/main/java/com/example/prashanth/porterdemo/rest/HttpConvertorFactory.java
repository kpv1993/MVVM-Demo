package com.example.prashanth.porterdemo.rest;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

class HttpConverterFactory extends Converter.Factory {

    private final Gson gson;

    public static HttpConverterFactory create() {
        return new HttpConverterFactory(new Gson());
    }

    private HttpConverterFactory(Gson gson) {
        this.gson = gson;
    }

    public static HttpConverterFactory create(Gson gson) {
        return new HttpConverterFactory(gson);
    }

    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if(JSONObject.class == type) {
            return new JsonResponseBodyConverter();
        } else {
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            return new GsonResponseBodyConverter<>(gson, adapter);
        }
    }

    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }
}
