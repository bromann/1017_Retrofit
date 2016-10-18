package com.example.my.a1017_retrofit;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private MyRetrofitApi api;
    private TextView tv;
    long num = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.baidu.com/s/").build();
        api = retrofit.create(MyRetrofitApi.class);
        btn = (Button) findViewById(R.id.btn);
        tv = (TextView) findViewById(R.id.tv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadTask().execute();
            }
        });
    }
    class DownloadTask extends AsyncTask<Void, Long, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            Call<ResponseBody> call = api.downfile
                    ("http://e.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=0d173d40912bd4074292dbfb4bb9b269/5fdf8db1cb1349540d104b0c504e9258d1094a4f.jpg");
            try {
                Response<ResponseBody> response = call.execute();
                ResponseBody body = response.body();
                InputStream inputStream = body.byteStream();
                long l = body.contentLength();
                Log.e("tag", "doInBackground: " + l);
                File file = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS) + "/mei.jpg");
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                    num += len;
                    publishProgress((num * 100 / l));
                    Thread.sleep(100);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                Log.e("tag", "DownloadFile: 完成");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            tv.setText("" + values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
        }
    }
}