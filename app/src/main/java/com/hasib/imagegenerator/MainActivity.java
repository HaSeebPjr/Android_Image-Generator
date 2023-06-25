package com.hasib.imagegenerator;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.squareup.picasso.Picasso;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    private ImageView generatedImage;
    private EditText inputText;
    private Button btn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatedImage = findViewById(R.id.generatedImage);
        inputText = findViewById(R.id.inputText);
        btn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);

        //btn click
        btn.setOnClickListener(view -> {
            String text = inputText.getText().toString();
            if(text.isEmpty()){
                inputText.setError("Please Enter Something");
            }else {
                generateImage(text);
            }
        });
    }

    private void generateImage(String text){
        progressBar.setVisibility(View.VISIBLE);
        inputText.setText("");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("prompt", text);
            jsonObject.put("size", "256x256");

            RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder().url("https://api.openai.com/v1/images/generations")
                    .header("Authorization", " Bearer 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")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("CAllBack Error", "onFailure: Error");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String genImg = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                        runOnUiThread(() -> {
                            Picasso.get().load(genImg).into(generatedImage);
                            progressBar.setVisibility(View.GONE);
                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}