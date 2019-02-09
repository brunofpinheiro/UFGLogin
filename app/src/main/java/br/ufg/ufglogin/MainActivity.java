package br.ufg.ufglogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String spName;
    String spToken;
    String spPhotoUrl;
    URL    newurl;
    Bitmap mImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("LOGADO", Context.MODE_PRIVATE);
        String spLogado   = sharedPreferences.getString("LOGADO", "");
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LOGADO", "N");
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        if (spLogado.equals("S")) {
            Intent intent = getIntent();
            spName = intent.getStringExtra("NAME");
            spToken = intent.getStringExtra("TOKEN");
            spPhotoUrl = intent.getStringExtra("PHOTO_URL");

            TextView mtxtName  = (TextView) findViewById(R.id.txtName);
            TextView mtxtToken = (TextView) findViewById(R.id.txtToken);
            final ImageView mImgUser = (ImageView) findViewById(R.id.imgUser);

            mtxtName.setText(spName);
            mtxtToken.setText(spToken);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        newurl = new URL(spPhotoUrl);
                        mImg = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImgUser.setImageBitmap(mImg);
                            mImgUser.setImageURI(Uri.parse(spPhotoUrl));
                        }
                    });
                }
            });
        }
    }
}
