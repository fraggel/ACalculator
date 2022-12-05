package es.fraggel.acalculator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.fraggel.acalculator.Models.StaticInfo;
import es.fraggel.acalculator.Models.User;
import es.fraggel.acalculator.Services.DataContext;
import es.fraggel.acalculator.Services.LocalUserService;
import es.fraggel.acalculator.Services.Tools;

public class MainActivity extends AppCompatActivity {
    float mValueOne, mValueTwo;
    User user;
    Firebase refUser;
    String operation="";
    int op=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button button0, button1, button2, button3, button4, button5, button6,
                button7, button8, button9, buttonAdd, buttonSub, buttonDivision,
                buttonMul, button10, buttonC, buttonEqual;
        TextView crunchifyEditText;
            savedInstanceState=null;
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            String cerrarApp = extras.getString("cerrarApp");
            if("true".equals(cerrarApp)){
                finish();
            }
        }
        Firebase.setAndroidContext(this);

        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email == null) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivityForResult(intent, 100);
//
        } else {
            startService(new Intent(this, AppService.class));
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }

        }
        //checkPermissions();
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
            button10 = (Button) findViewById(R.id.buttonDot);
            buttonAdd = (Button) findViewById(R.id.buttonadd);
            buttonSub = (Button) findViewById(R.id.buttonsub);
            buttonMul = (Button) findViewById(R.id.buttonmul);
            buttonDivision = (Button) findViewById(R.id.buttondiv);
            buttonC = (Button) findViewById(R.id.buttonDel);
            buttonEqual = (Button) findViewById(R.id.buttoneql);
            crunchifyEditText = (TextView) findViewById(R.id.edit_text);


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

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mValueTwo = Float.parseFloat(crunchifyEditText.getText() + "");
                        crunchifyEditText.setText(null);
                        operation = "+";
                    }catch(Exception e){}
                }
            });

            buttonSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mValueTwo = Float.parseFloat(crunchifyEditText.getText() + "");
                        crunchifyEditText.setText(null);
                        operation="-";
                    }catch(Exception e){}
                    }
            });

            buttonMul.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mValueTwo = Float.parseFloat(crunchifyEditText.getText() + "");
                        crunchifyEditText.setText(null);
                        operation="x";
                    }catch(Exception e){}
                }
            });

            buttonDivision.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        mValueTwo = Float.parseFloat(crunchifyEditText.getText() + "");
                        crunchifyEditText.setText(null);
                        operation="/";
                    }catch(Exception e){}
                }
            });

            buttonEqual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mValueOne = Float.parseFloat(crunchifyEditText.getText() + "");
                        if (mValueOne == 1404 && op==-1) {
                            crunchifyEditText.setText("");
                            Intent i = new Intent(v.getContext(), TextActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivityForResult(i, 0);
                        } else {
                            switch (operation) {
                                case "+":
                                    crunchifyEditText.setText(String.valueOf(mValueTwo + mValueOne));
                                    op=1;
                                    break;
                                case "-":
                                    crunchifyEditText.setText(String.valueOf(mValueTwo - mValueOne));
                                    op=1;
                                    break;
                                case "x":
                                    crunchifyEditText.setText(String.valueOf(mValueTwo * mValueOne));
                                    op=1;
                                    break;
                                case "/":
                                    crunchifyEditText.setText(String.valueOf(mValueTwo / mValueOne));
                                    op=1;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }catch(Exception e){}
                }
            });

            buttonC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crunchifyEditText.setText("");
                    mValueOne=0;
                    mValueTwo=0;
                    op=-1;
                }
            });

            button10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crunchifyEditText.setText(crunchifyEditText.getText() + ".");
                }
            });
        }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
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