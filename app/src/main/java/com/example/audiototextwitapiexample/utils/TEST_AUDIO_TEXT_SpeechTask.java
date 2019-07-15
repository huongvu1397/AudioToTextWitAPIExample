package com.example.audiototextwitapiexample.utils;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TEST_AUDIO_TEXT_SpeechTask extends AsyncTask<ArrayList<String>, Void, String> {
    private volatile boolean finish = false;

    protected String doInBackground(ArrayList<String>... params) {
        try {

            ArrayList<String> x = params[0];
            String key = x.get(0);

            Log.e("AAAAAAAAA", key + " - " + x.get(1));
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.wit.ai/speech?" + String.format("v=%s", URLEncoder.encode(new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date()), "UTF-8"))).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + key);
            connection.setRequestProperty("Content-Type", "audio/" + x.get(2));
            OutputStream outputStream = connection.getOutputStream();
            //  System.out.println(params[1]);
            FileChannel fileChannel = new FileInputStream(x.get(1)).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (fileChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                byte[] b = new byte[byteBuffer.remaining()];
                byteBuffer.get(b);
                outputStream.write(b);
                byteBuffer.clear();
            }
            BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String line = response.readLine();

                if (line == null || line.equals("")) {
                    Log.e("AAAAAAAAAAAA", new JSONObject(stringBuilder.toString()).getString("_text"));

                    return new JSONObject(stringBuilder.toString()).getString("_text");
                }
                stringBuilder.append(line);
                System.out.println(line);
            }
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return "filenotfoundException";
        } catch (Exception e3) {
            e3.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String result) {
        this.finish = true;
    }

    public boolean isFinish() {
        return this.finish;
    }
}