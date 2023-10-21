package com.tg.githubinfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView viewimg;
    private TextView txtResponse;
    private TextView idTVQuestion;
    private EditText etQuestion;
    private LinearLayout detailsContainer;
    private NetworkConnection networkConnection;// Add this line


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_main);
        // Initialize views
        viewimg = findViewById(R.id.viewimg);
        etQuestion = findViewById(R.id.etQuestion);
        idTVQuestion = findViewById(R.id.idTVQuestion);
        txtResponse = findViewById(R.id.txtResponse);
        detailsContainer = findViewById(R.id.detailsContainer);


        etQuestion.setFocusable(true);
        etQuestion.setClickable(true);

        // Set RTL layout direction for the TextInputEditText
        networkConnection = new NetworkConnection(this);

        View layoutInflater = findViewById(R.id.networkError);

        networkConnection.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (isConnected) {
                    //Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    layoutInflater.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, R.string.not_connected, Toast.LENGTH_SHORT).show();
                    layoutInflater.setVisibility(View.VISIBLE);
                }
            }
        });


        etQuestion.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etQuestion != null) {
                    etQuestion.getCompoundDrawables();
                    Drawable drawable = etQuestion.getCompoundDrawables()[DRAWABLE_RIGHT];
                    if (drawable != null && event.getRawX() >= (etQuestion.getRight() - drawable.getBounds().width())) {
                        String question = etQuestion.getText().toString().trim();
                        txtResponse.setText(R.string.wait);

                        if (!question.isEmpty()) {
                            new FetchGitHubUserData().execute(question);
                            return true;
                        }
                    }
                }
            }
            return false;
        });


        etQuestion.setOnEditorActionListener((v, actionId, event) -> {
            String question = etQuestion.getText().toString().trim();
            txtResponse.setText(R.string.wait);
            if (!question.isEmpty()) {
                new FetchGitHubUserData().execute(question);
            }
            return true;
        });

        ////txtResponse.setOnLongClickListener(v -> {
        //ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //ClipData clip = ClipData.newPlainText("Copied Text", txtResponse.getText());
        //clipboard.setPrimaryClip(clip);
        //Toast.makeText(MainActivity.this, "copy", Toast.LENGTH_SHORT).show();
        //return true;
        //});


    }


    private class FetchGitHubUserData extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String username = params[0];
            JSONObject data = null;

            try {
                URL url = new URL("https://api.github.com/users/" + username);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    data = new JSONObject(response.toString());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            if (data != null) {
                // Extract and display user details
                String name = data.optString("name", data.optString("login"));
                String bio = data.optString("bio", getString(R.string.bio_not_available));
                int publicRepos = data.optInt("public_repos");
                int followers = data.optInt("followers");
                int following = data.optInt("following");
                String location = data.optString("location", getString(R.string.location_not_available));
                String blog = data.optString("blog", getString(R.string.blog_not_available));
                String twitterUsername = data.optString("twitter_username", getString(R.string.twitter_not_available));
                String company = data.optString("company", getString(R.string.company_not_available));
                String image = data.optString("avatar_url", getString(R.string.image_not_available));

                // Get the appropriate localized strings based on the device's language
                Resources resources = getResources();
                String resultText = getString(
                        R.string.result_format,
                        name,
                        data.optString("login"),
                        bio,
                        publicRepos,
                        followers,
                        following,
                        location,
                        blog,
                        twitterUsername,
                        company,
                        image
                );

                // Update UI elements on the main thread
                runOnUiThread(() -> {
                    idTVQuestion.setText("" + data.optString("login"));
                    idTVQuestion.setTextIsSelectable(true); // copy
                    txtResponse.setText(resultText); // Update the TextView with the resultText
                    txtResponse.setTextIsSelectable(true); // copy
                    detailsContainer.setVisibility(View.VISIBLE);
                });

                // Load and display the image using Picasso on the UI thread
                //.//.//sorry wifi
///////////////////////////////////////////////
// Load an image into viewimg using Picasso
                Picasso.get()
                        .load(image) // Replace 'imgUrl' with the actual URL of the image you want to load.
                        .resize(260, 260)
                        .centerCrop()
                        .into(viewimg);

// Set an OnClickListener for viewimg
                viewimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create a custom dialog to display the image preview.
                        Dialog previewDialog = new Dialog(MainActivity.this);
                        previewDialog.setContentView(R.layout.printimage);

                        // Find the ImageView in the dialog layout.

                        ImageView previewImageView = previewDialog.findViewById(R.id.view);

                        // Load and set the image resource in the preview dialog
                        Picasso.get()
                                .load(image) // Replace 'imgUrl' with the actual URL of the image you want to load.
                                .resize(360, 360)
                                .centerCrop()
                                .into(previewImageView);

                        // Show the dialog.
                        previewDialog.show();
                    }
                });///

///////////////////////////////////
// Clear the text in the EditText (etQuestion)
                etQuestion.setText("");
            } else {
                etQuestion.setText("");
                idTVQuestion.setText("");
                Picasso.get().load(R.drawable.error)
                        .resize(260, 260)
                        .centerCrop()
                        .into(viewimg);
                txtResponse.setText(R.string.user);

                viewimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create a custom dialog to display the image preview.
                        Dialog previewDialog = new Dialog(MainActivity.this);
                        previewDialog.setContentView(R.layout.printimage);

                        // Find the ImageView in the dialog layout.
                        ImageView previewImageView = previewDialog.findViewById(R.id.view);

                        // Load and set the image resource in the preview dialog
                        Picasso.get()
                                .load(R.drawable.error) // Replace 'imgUrl' with the actual URL of the image you want to load.
                                .resize(360, 360)
                                .centerCrop()
                                .into(previewImageView);

                        // Show the dialog.
                        previewDialog.show();
                    }
                });
            }}}}

