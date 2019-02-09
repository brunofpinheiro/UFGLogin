package br.ufg.ufglogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private String   svrName;
    private String   svrToken;
    private String   svrPhoto_Url;
    private EditText mEdtEmail;
    private EditText mEdtSenha;
    private Button   mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtSenha = (EditText) findViewById(R.id.edtSenha);
        mBtnLogin = (Button) findViewById(R.id.btnLogin);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEdtEmail.getText().toString();
                String senha = mEdtSenha.getText().toString();

                try {
                    validaLogin(new URL(
                            "http://private-anon-d53aff8ed0-sandromoreira.apiary-mock.com/login?email="+email+"&senha="+senha+""));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void validaLogin(final URL url) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection myConnection;

                try {
                    myConnection = (HttpURLConnection) url.openConnection();
                    myConnection.setRequestMethod("POST");
                    myConnection.setRequestProperty("email",mEdtEmail.getText().toString());
                    myConnection.setRequestProperty("senha",mEdtSenha.getText().toString());
                    myConnection.setDoOutput(true);

                    OutputStream outputPost = new BufferedOutputStream(myConnection.getOutputStream());
                    outputPost.write(myConnection.getRequestProperty("email").getBytes());
                    outputPost.write(myConnection.getRequestProperty("senha").getBytes());
                    outputPost.flush();
                    outputPost.close();

                    if (myConnection.getResponseCode() == 200) {
                        InputStream       responseBody       = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader        jsonReader         = new JsonReader(responseBodyReader);

                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();

                            if (key.equals("name"))
                                svrName = jsonReader.nextString();

                            if (key.equals("token"))
                                svrToken = jsonReader.nextString();

                            if (key.equals("photo_url"))
                                svrPhoto_Url = jsonReader.nextString();
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("LOGADO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("LOGADO", "S");
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("NAME", svrName);
                        intent.putExtra("TOKEN", svrToken);
                        intent.putExtra("PHOTO_URL", svrPhoto_Url);

                        startActivity(intent);

                        jsonReader.close();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Usuário e/ou senha inválidos.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    myConnection.disconnect();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
