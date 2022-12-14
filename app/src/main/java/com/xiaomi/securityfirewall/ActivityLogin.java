package com.xiaomi.securityfirewall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import com.xiaomi.securityfirewall.Models.StaticInfo;
import com.xiaomi.securityfirewall.Services.DataContext;
import com.xiaomi.securityfirewall.Services.IFireBaseAPI;
import com.xiaomi.securityfirewall.Services.Tools;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ActivityLogin extends AppCompatActivity {

    DataContext db = new DataContext(this, null, null, 1);


    EditText et_Email, et_Password;
    Button btn_Login;
    public static final String ENDPOINT = "https://chat-8459f.firebaseio.com";
    ProgressDialog pd;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_Email = (EditText) findViewById(R.id.et_Email);
        et_Password = (EditText) findViewById(R.id.et_Password);
    }


    public void btnLoginClick(View view) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        } else if (et_Email.getText().toString().equals("")) {
            et_Email.setError("Email cannot be empty");

        } else if (et_Password.getText().toString().equals("")) {
            et_Password.setError("Password cannot be empty");
        } else {
            pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
            pd.show();
            email = Tools.encodeString(et_Email.getText().toString());
            LoginTask t = new LoginTask();
            t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    public class LoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            IFireBaseAPI api = retrofit.create(IFireBaseAPI.class);
            // Call<String> call = api.getAllUsersAsJsonString();
            Call<String> call = api.getSingleUserByEmail(StaticInfo.UsersURL + "/" + email + ".json");
            try {
                return call.execute().body();
            } catch (Exception e) {
                pd.hide();
                return "null";
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            try {
                if (!jsonString.trim().equals("null")) {
                    JSONObject userObj = new JSONObject(jsonString);
                    String pass = et_Password.getText().toString();
                    if (userObj.getString("Password").equals(pass)) {
                        pd.hide();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("Email", email);
                        editor.putString("FirstName", userObj.getString("FirstName"));
                        editor.putString("LastName", userObj.getString("LastName"));
                        editor.commit();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        pd.hide();
                        Toast.makeText(ActivityLogin.this, "Incorecct email or password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    pd.hide();
                    Toast.makeText(ActivityLogin.this, "Incorecct email or password", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                pd.hide();
                e.printStackTrace();
            }

        }

    }


    public void btnSignUpClick(View view) {

        startActivity(new Intent(this, ActivityRegister.class));
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);

        startActivity(i);
    }
}
