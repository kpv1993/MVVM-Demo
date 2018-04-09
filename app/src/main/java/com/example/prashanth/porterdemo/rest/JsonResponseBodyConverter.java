package com.example.prashanth.porterdemo.rest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class JsonResponseBodyConverter implements Converter<ResponseBody, JSONObject> {

    private static final String LOG_TAG = "JsonResponseBodyConverter";

    @Override
    public JSONObject convert(ResponseBody value) throws IOException {
        try {
            BufferedReader reader = null;
            StringBuilder sb = new StringBuilder();
            if (value != null) {
                reader = new BufferedReader(value.charStream());
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
            try {
                return new JSONObject(sb.toString());
            } catch (JSONException e) {
                Log.w(LOG_TAG, e.getMessage());
                return new JSONObject();
            }
        } finally {
            value.close();
        }
    }
}