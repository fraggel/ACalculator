package es.fraggel.acalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import es.fraggel.acalculator.Services.LocalUserService;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch button0 = (Switch) findViewById(R.id.switch1);
        boolean notificaciones = LocalUserService.getLocalUserFromPreferences(getApplicationContext()).Notificaciones;
        button0.setChecked(notificaciones);
        button0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("notify", b);
                editor.commit();
                    Intent i=new Intent(getApplicationContext(), AppService.class);
                    i.putExtra("notify",String.valueOf(b));
                    startService(i);
                    AlarmReceiver alarm = new AlarmReceiver();
                    alarm.setAlarm(getApplicationContext(), true);
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