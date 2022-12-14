package com.xiaomi.securityfirewall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.xiaomi.securityfirewall.Models.User;
import com.xiaomi.securityfirewall.Services.LocalUserService;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch button0 = (Switch) findViewById(R.id.switch1);
        Button btnRestore = (Button) findViewById(R.id.btnRestore);
        Button btnBackup = (Button) findViewById(R.id.btnBackup);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(Settings.this);
                // set a title for the progress bar
                progressDialog.setTitle(" ");
                // set a message for the progress bar
                progressDialog.setMessage("Cargando...");
                //set the progress bar not cancelable on users' touch
                progressDialog.setCancelable(false);
                // show the progress bar
                progressDialog.show();
                Util.restoreBackup(getApplicationContext(),Settings.this);
            }
        });
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(Settings.this);
                // set a title for the progress bar
                progressDialog.setTitle(" ");
                // set a message for the progress bar
                progressDialog.setMessage("Cargando...");
                //set the progress bar not cancelable on users' touch
                progressDialog.setCancelable(false);
                // show the progress bar
                progressDialog.show();
                User user =LocalUserService.getLocalUserFromPreferences(getApplicationContext());
                Util.makeBackup(getApplicationContext(),user,Settings.this);
            }
        });
        boolean notificaciones = LocalUserService.getLocalUserFromPreferences(getApplicationContext()).Notificaciones;
        button0.setChecked(notificaciones);
        button0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("notify", b);
                editor.commit();
                //TODO ALARM
                    /*
                    Intent i=new Intent(getApplicationContext(), AppService.class);
                    i.putExtra("notify",String.valueOf(b));
                    startService(i);
                    User user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
                    if(b && user.FirstName.equals("Pablo")) {
                        AlarmReceiver alarm = new AlarmReceiver();
                        alarm.setAlarm(getApplicationContext(), true);
                    }*/
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}