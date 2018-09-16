package me.kungfucat.codencounter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetUpVideoActivity extends AppCompatActivity {

    SharedPreferences.Editor editor;
    Button defaultButton, customButton;
    private OkHttpClient client;
    private static final int VIDEO_CAPTURE = 101;
    String videoPath;
    Context context;
    TextView textView;
    public static String URL = "protected-oasis-71567.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_video);
        defaultButton = findViewById(R.id.defaultButton);
        customButton = findViewById(R.id.recordVideoButton);
        context = this;
        videoPath = "";
        textView=findViewById(R.id.appNameTitle);

        try {
            Typeface custom_font = Typeface.createFromAsset(getAssets(), "lobster.ttf");
            textView.setTypeface(custom_font);
        } catch (Exception e) {

        }


        editor = getSharedPreferences("CNC", MODE_PRIVATE).edit();
//        editor.putString("hasVideo", "true").apply();

        client = new OkHttpClient();

        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent, VIDEO_CAPTURE);

            }
        });

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
//                startActivity(intent);
                startOtherActivity();
            }
        });
    }

    public void startOtherActivity() {
        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        dialog.setMax(5000);
        dialog.show();

        final Intent intent = new Intent(this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 5 * 1000);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {

            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                videoPath = cursor.getString(column_index);
//                makeRequest();
                startOtherActivity();
            } else {
                Log.d("asd", "asdf");
            }
        }
    }

    public void makeRequest() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("video", videoPath)
                .build();

        Request request = new Request.Builder()
                .url(URL + "/carify/make_caricature")
                .post(requestBody)
                .build();
    }

    public void getCaricatures() {
        TextView result = new TextView(context);
        final Request request = new Request.Builder().url(URL + "/get/caricature/id").build();
//        final TextView finalResult = result;

        final FileInputStream[] fio = new FileInputStream[1];
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fio[0] = getInputStreamFrom(request);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fio[0] = getInputStreamFrom(request);
                    }
                });
            }
        });
    }


    public void getWebservice() {
        TextView result = new TextView(context);
        final Request request = new Request.Builder().url(URL).build();
        final TextView finalResult = result;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finalResult.setText("Failure !");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalResult.setText(response.body().string());
                        } catch (IOException ioe) {
                            finalResult.setText("Error during get body");
                        }
                    }
                });
            }
        });
    }


    /*
        public static int upLoad2Server(String sourceFileUri) {
            String upLoadServerUri = "your remote server link";
            // String [] string = sourceFileUri;
            String fileName = sourceFileUri;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            DataInputStream inStream = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            String responseFromServer = "";

            File sourceFile = new File(sourceFileUri);
            if (!sourceFile.isFile()) {
                Log.e("Huzza", "Source File Does not exist");
                return 0;
            }
            try { // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
                Log.i("Huzza", "Initial .available : " + bytesAvailable);

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                // close streams
                Log.i("Upload file to server", fileName + " File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
            }
    //this block will give the response of upload link
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("Huzza", "RES Message: " + line);
                }
                rd.close();
            } catch (IOException ioex) {
                Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
            }
            return serverResponseCode;  // like 200 (Ok)

        } // end upLoad2Server
        */
    public FileInputStream getInputStreamFrom(Request request) {
        return null;
    }

}
