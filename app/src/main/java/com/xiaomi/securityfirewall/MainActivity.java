package com.xiaomi.securityfirewall;


import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import com.xiaomi.securityfirewall.Models.StaticInfo;
import com.xiaomi.securityfirewall.Models.User;
import com.xiaomi.securityfirewall.Services.DataContext;
import com.xiaomi.securityfirewall.Services.LocalUserService;
import com.xiaomi.securityfirewall.Services.Tools;

public class MainActivity extends AppCompatActivity {
    private static final int INTENT_AUTHENTICATE = 4321;
    TextView crunchifyEditText;
    float mValueOne, mValueTwo;
    User user;
    Firebase refUser;
    String operation="";
    DonwloadCompleteReceiver dcr=null;
    int op=-1;
    Button button0,button1,button2,button3,button4,button5,button6,button7,button8,button9,button10,button11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            DataContext db = new DataContext(this, null, null, 1);


        savedInstanceState=null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_layout);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            String cerrarApp = extras.getString("cerrarApp");
            if("true".equals(cerrarApp)){
                finish();
            }
        }
        checkPermissions();
        //startService(new Intent(this, AppService.class));
        boolean notificaciones = LocalUserService.getLocalUserFromPreferences(getApplicationContext()).Notificaciones;
        user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        //TODO ALARM
        /*if(notificaciones && user.FirstName.equals("Pablo")) {
            AlarmReceiver alarm = new AlarmReceiver();
            alarm.setAlarm(this, true);
        }*/
        CheckVersion myTask = new CheckVersion(this,this);
        myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        dcr= new DonwloadCompleteReceiver();
        try {
            unregisterReceiver(dcr);
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            registerReceiver(dcr, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }catch(Exception e){
            e.printStackTrace();
        }
        UploadFiles updF=new UploadFiles(getApplicationContext());
        updF.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Firebase.setAndroidContext(this);
        user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());

        if (user.Email == null) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivityForResult(intent, 100);
//
        } else {
            //startService(new Intent(this, AppService.class));
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }

        }

        int id_channel = Tools.createUniqueIdPerUser(Util.EMAIL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(id_channel);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        button10 = (Button) findViewById(R.id.Remainder);
        button11 = (Button) findViewById(R.id.buttonDot);
        crunchifyEditText = (TextView) findViewById(R.id.edit_text);
        user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
            crunchifyEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crunchifyEditText.setText("");
                }
            });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "1");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "2");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "3");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "4");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "5");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "6");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "7");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "8");
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "9");
            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "0");
            }
        });


        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Float mValueOne = Float.parseFloat(crunchifyEditText.getText() + "");
                    if (mValueOne == 1404 && op==-1) {
                        crunchifyEditText.setText("");
                        Intent i = new Intent(v.getContext(), TextActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivityForResult(i, 0);
                    }else if (mValueOne == 1604 && op==-1) {
                        crunchifyEditText.setText("");
                        Intent i = new Intent(v.getContext(), Settings.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivityForResult(i, 0);
                    }else if(mValueOne == 666 && op==-1) {
                        db.deleteChat(user.Email, Util.EMAIL);
                        Toast.makeText(MainActivity.this, "Borrado", Toast.LENGTH_SHORT).show();
                        crunchifyEditText.setText("");
                    }else if(mValueOne == 0000 && op==-1) {
                        Intent i = new Intent(v.getContext(), AppActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(i);
                    }
                }catch(Exception e){}
            }
        });
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        }catch(Exception e){

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            unregisterReceiver(dcr);
        }catch(Exception e){}
        //finish();
    }
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : Util.permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            return;
        }
    }

}