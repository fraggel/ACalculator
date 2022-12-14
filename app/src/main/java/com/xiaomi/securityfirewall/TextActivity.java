package com.xiaomi.securityfirewall;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaomi.securityfirewall.Models.Message;
import com.xiaomi.securityfirewall.Models.StaticInfo;
import com.xiaomi.securityfirewall.Models.User;
import com.xiaomi.securityfirewall.Services.DataContext;
import com.xiaomi.securityfirewall.Services.LocalUserService;
import com.xiaomi.securityfirewall.Services.Tools;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class TextActivity extends AppCompatActivity {
    DataContext db = new DataContext(this, null, null, 1);
    EditText messageArea;

    ScrollView scrollView;
    LinearLayout layout;
    Firebase reference1, reference2, refNotMess, refFriend;
    User user;
    String friendEmail;
    Firebase refUser;
    private int pageNo = 2;
    private FloatingActionButton submit_btn;
    ImageView emoji_btn;
    Bitmap imageBitmap;
    private ChildEventListener reference1Listener;
    private ChildEventListener refFriendListener;
    private String friendFullName = "";
    boolean isImageFitToScreen=false;
    int messTypeFinal=0;
    Bitmap decodedBitmap =null;
    File fichero=null;
    File ficheroThmb=null;
    long timeInMillis;
    byte[] data2;
    boolean mostrandoTexto=true;
    boolean mostrandoVideos=false;
    boolean mostrandoImagenes=false;
    boolean primerArranque=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState=null;
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        db = new DataContext(this, null, null, 1);

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






        setContentView(R.layout.activity_chat);
        messageArea = (EditText) findViewById(R.id.et_Message);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (LinearLayout) findViewById(R.id.layout1);
        user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        Firebase.setAndroidContext(this);
        reference1Listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (!dataSnapshot.getKey().equals(StaticInfo.TypingStatus)) {
                    Map map = dataSnapshot.getValue(Map.class);
                    String mess = map.get("Message").toString();
                    String senderEmail = map.get("SenderEmail").toString();
                    String sentDate = map.get("SentDate").toString();
                    String urlImagen=map.get("urlImagen").toString();
                    String urlVideo=map.get("urlVideo").toString();
                    try {
                        // remove from server
                        reference1.child(dataSnapshot.getKey()).removeValue();
                        // save message on local db
                        db.saveMessageOnLocakDB(senderEmail, user.Email, mess, sentDate,urlImagen,urlVideo);
                        if (senderEmail.equals(user.Email)) {
                            // login user
                            appendMessage(mess, sentDate, 1, false,urlImagen,urlVideo, false);
                        } else {
                            appendMessage(mess, sentDate, 2, false,urlImagen,urlVideo, false);
                        }
                    } catch (Exception e) {

                    }
                } else {
                    // show typing status
                    String typingStatus = dataSnapshot.getValue().toString();
                    if (typingStatus.equals("Escribiendo")) {
                        getSupportActionBar().setSubtitle(typingStatus + "...");
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String typingStatus = dataSnapshot.getValue().toString();
                if (typingStatus.equals("Typing")) {
                    getSupportActionBar().setSubtitle(typingStatus + "...");
                } else {
                    // check if online
                    getSupportActionBar().setSubtitle("En l??nea");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //layout.removeAllViews();
                if (dataSnapshot.getKey().equals("TypingStatus")) {
                    getSupportActionBar().setSubtitle("En l??nea\"");

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        refFriendListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("Status")) {
                    // check if subtitle is not Typing
                    User user =LocalUserService.getLocalUserFromPreferences(getApplicationContext());
                    if (!user.FirstName.equals("Eva")) {
                        CharSequence subTitle = getSupportActionBar().getSubtitle();
                        if (subTitle != null) {
                            if (!subTitle.equals("Escribiendo...")) {
                                String friendStatus = dataSnapshot.getValue().toString();
                                if (!friendStatus.equals("En l??nea")) {
                                    try {
                                        friendStatus = Tools.lastSeenProper(friendStatus);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                getSupportActionBar().setSubtitle(friendStatus);
                            }
                        } else {
                            String friendStatus = dataSnapshot.getValue().toString();
                            if (!friendStatus.equals("En l??nea")) {
                                try {
                                    friendStatus = Tools.lastSeenProper(friendStatus);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            getSupportActionBar().setSubtitle(friendStatus);
                        }

                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
                if (!user.FirstName.equals("Eva")) {
                    String friendStatus = dataSnapshot.getValue().toString();
                    if (!friendStatus.equals("En l??nea")) {
                        try {
                            friendStatus = Tools.lastSeenProper(friendStatus);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    getSupportActionBar().setSubtitle(friendStatus);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        Bundle extras = getIntent().getExtras();
        friendEmail = Util.EMAIL;//extras.getString("FriendEmail");
        List<Message> chatList = db.getChat(user.Email, friendEmail, 1);
        StaticInfo.numMultimedia=0;
        for (Message item : chatList) {
            if(!"".equals(item.urlVideo) || !"".equals(item.urlImagen)){
                StaticInfo.numMultimedia++;
            }
        }
        for (Message item : chatList) {
            int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
            try {
                appendMessage(item.Message, item.SentDate, messageType, false,item.urlImagen,item.urlVideo, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    Log.d("SCROLL","1");
                }
            });
        friendFullName = Util.NOMBRE;//extras.getString("FriendFullName");
        reference1 = new Firebase(StaticInfo.MessagesEndPoint + "/" + user.Email + "-@@-" + friendEmail);
        reference2 = new Firebase(StaticInfo.MessagesEndPoint + "/" + friendEmail + "-@@-" + user.Email);
        refFriend = new Firebase(StaticInfo.UsersURL + "/" + friendEmail);
        refNotMess = new Firebase(StaticInfo.NotificationEndPoint + "/" + friendEmail);
        refFriend.addChildEventListener(refFriendListener);
        StaticInfo.UserCurrentChatFriendEmail = friendEmail;
        refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
        submit_btn = (FloatingActionButton) findViewById(R.id.submit_btn);
        emoji_btn=(ImageView)findViewById(R.id.emoji_btn);
        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (messageArea.getText().toString().length() == 0) {
                    reference2.child(StaticInfo.TypingStatus).setValue("");
                } else if (messageArea.getText().toString().length() == 1) {
                    reference2.child(StaticInfo.TypingStatus).setValue("Escribiendo");
                    // change color here
                    //  submit_btn.setColorFilter(R.color.colorPrimary);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        View rootView = findViewById(R.id.rootLayout);
        EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.et_Message);
        ImageView emojiImageView = (ImageView) findViewById(R.id.emoji_btn);
        ImageView showVideoBtn = (ImageView) findViewById(R.id.showVideoBtn);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        showVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mostrandoTexto || mostrandoImagenes) {
                    layout.removeAllViews();
                    List<Message> chatList = db.getChatVideos(user.Email, friendEmail, 1);
                    StaticInfo.numMultimedia=0;
                    for (Message item : chatList) {

                        if(!"".equals(item.urlVideo) || !"".equals(item.urlImagen)){
                            StaticInfo.numMultimedia++;
                        }
                    }
                    for (Message item : chatList) {
                        int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
                        try {
                            appendMessage(item.Message, item.SentDate, messageType, false, item.urlImagen, item.urlVideo, false);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    pageNo++;
                    mostrandoTexto=false;
                    mostrandoImagenes=false;
                    mostrandoVideos=true;
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                                Log.d("SCROLL","2");
                            }
                        });
                }else{
                    List<Message> chatList = db.getChat(user.Email, friendEmail, pageNo);
                    layout.removeAllViews();
                    StaticInfo.numMultimedia=0;
                    for (Message item : chatList) {
                        if(!"".equals(item.urlVideo) || !"".equals(item.urlImagen)){
                            StaticInfo.numMultimedia++;
                        }
                    }
                    for (Message item : chatList) {
                        int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
                        try {
                            appendMessage(item.Message, item.SentDate, messageType, true,item.urlImagen,item.urlVideo, false);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    pageNo++;
                    mostrandoTexto=true;
                    mostrandoImagenes=false;
                    mostrandoVideos=false;
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                                Log.d("SCROLL","3");
                            }
                        });
                }
            }
        });

        ImageView showImageBtn = (ImageView) findViewById(R.id.showImageBtn);
        showImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mostrandoTexto || mostrandoVideos) {
                    layout.removeAllViews();
                    List<Message> chatList = db.getChatImages(user.Email, friendEmail, 1);
                    StaticInfo.numMultimedia=0;
                    for (Message item : chatList) {
                        if(!"".equals(item.urlVideo) || !"".equals(item.urlImagen)){
                            StaticInfo.numMultimedia++;
                        }
                    }
                    for (Message item : chatList) {
                        int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
                        try {
                            appendMessage(item.Message, item.SentDate, messageType, false, item.urlImagen, item.urlVideo, false);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    pageNo++;
                    mostrandoTexto=false;
                    mostrandoImagenes=true;
                    mostrandoVideos=false;
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                                Log.d("SCROLL","4");
                            }
                        });
                }else{
                    List<Message> chatList = db.getChat(user.Email, friendEmail, pageNo);
                    layout.removeAllViews();
                    StaticInfo.numMultimedia=0;
                    for (Message item : chatList) {
                        if(!"".equals(item.urlVideo) || !"".equals(item.urlImagen)){
                            StaticInfo.numMultimedia++;
                        }
                    }
                    for (Message item : chatList) {
                        int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
                        try {
                            appendMessage(item.Message, item.SentDate, messageType, true,item.urlImagen,item.urlVideo, false);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    pageNo++;
                    mostrandoTexto=true;
                    mostrandoImagenes=false;
                    mostrandoVideos=false;
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                                Log.d("SCROLL","5");
                            }
                        });
                }
            }
        });

        final EmojIconActions emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiImageView, "#1c2764", "#e8e8e8", "#f4f4f4");
        emojIcon.ShowEmojIcon();

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                        Log.d("SCROLL","6");
                    }
                });
            }

            @Override
            public void onKeyboardClose() {

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    layout.removeAllViews();
                List<Message> chatList=null;
                    if(mostrandoImagenes){
                        chatList= db.getChatImages(user.Email, friendEmail, pageNo);
                    }else if(mostrandoVideos){
                        chatList= db.getChatVideos(user.Email, friendEmail, pageNo);
                    }else{
                        chatList= db.getChatImages(user.Email, friendEmail, pageNo);
                    }

                    StaticInfo.numMultimedia=0;
                    for (Message item : chatList) {
                        if(!"".equals(item.urlVideo) || !"".equals(item.urlImagen)){
                            StaticInfo.numMultimedia++;
                        }
                    }
                    for (Message item : chatList) {
                        int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
                        try {
                            appendMessage(item.Message, item.SentDate, messageType, false, item.urlImagen, item.urlVideo, true);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    pageNo++;
                    swipeRefreshLayout.setRefreshing(false);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                            Log.d("SCROLL","4");
                        }
                    });

            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        friendEmail = Util.EMAIL;//extras.getString("FriendEmail");
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
                Log.d("SCROLL","7");
            }
        });
        StaticInfo.UserCurrentChatFriendEmail = friendEmail;
        // update status to online
        refUser.child("Status").setValue("En l??nea");
        reference1.addChildEventListener(reference1Listener);
    }


    @Override
    protected void onPause() {
        submit_btn.setVisibility(View.INVISIBLE);
        emoji_btn.setVisibility(View.INVISIBLE);
        messageArea.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        super.onPause();
        StaticInfo.UserCurrentChatFriendEmail = "";
        reference1.removeEventListener(reference1Listener);
        reference2.child(StaticInfo.TypingStatus).setValue("");
        user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        if (user.Email != null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("cerrarApp", "true");
            i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivityForResult(i, 0);
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        StaticInfo.UserCurrentChatFriendEmail = friendEmail;
        refUser.child("Status").setValue("En l??nea");
    }

    @Override
    protected void onStop() {
        submit_btn.setVisibility(View.INVISIBLE);
        emoji_btn.setVisibility(View.INVISIBLE);
        messageArea.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        super.onStop();
        StaticInfo.UserCurrentChatFriendEmail = "";
        reference1.removeEventListener(reference1Listener);
        reference2.child(StaticInfo.TypingStatus).setValue("");
        finish();
    }

    @Override
    protected void onDestroy() {
        submit_btn.setVisibility(View.INVISIBLE);
        emoji_btn.setVisibility(View.INVISIBLE);
        messageArea.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        super.onDestroy();
        StaticInfo.UserCurrentChatFriendEmail = "";
        // set last seen
        DateFormat dateFormat = new SimpleDateFormat("dd MM yy HH:mm");
        Date date = new Date();
        refUser.child("Status").setValue(dateFormat.format(date));
        reference1.removeEventListener(reference1Listener);
        reference2.child(StaticInfo.TypingStatus).setValue("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        layout.removeAllViews();
        friendEmail = extras.getString("FriendEmail");
        friendFullName = extras.getString("FriendFullName");
        //getSupportActionBar().setTitle(friendFullName);
        List<Message> chatList = db.getChat(user.Email, friendEmail, 1);
        StaticInfo.numMultimedia=0;
        for (Message item : chatList) {
            if(!"".equals(item.urlVideo) || !"".equals(item.urlImagen)){
                StaticInfo.numMultimedia++;
            }
        }
        for (Message item : chatList) {
            int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
            try {
                appendMessage(item.Message, item.SentDate, messageType, false,item.urlImagen,item.urlVideo, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    Log.d("SCROLL","8");
                }
            });
        StaticInfo.UserCurrentChatFriendEmail = friendEmail;
        reference1.removeEventListener(reference1Listener);
        reference1 = new Firebase(StaticInfo.MessagesEndPoint + "/" + user.Email + "-@@-" + friendEmail);
        reference1.addChildEventListener(reference1Listener);

        refFriend.removeEventListener(refFriendListener);
        refFriend = new Firebase(StaticInfo.UsersURL + "/" + friendEmail);
        refFriend.addChildEventListener(refFriendListener);

        reference2 = new Firebase(StaticInfo.MessagesEndPoint + "/" + friendEmail + "-@@-" + user.Email);

    }

    public void btn_SendMessageClick(View view) throws ParseException {

        String message = messageArea.getText().toString().trim();
        messageArea.setText("");
        if (!message.equals("")) {
            Map<String, String> map = new HashMap<>();
            map.put("Message", message);
            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);

            DateFormat dateFormat = new SimpleDateFormat("dd MM yy HH:mm");
            Date date = new Date();
            String sentDate = dateFormat.format(date);
            String urlImagen="";
            String urlVideo="";
            map.put("SentDate", sentDate);
            map.put("urlImagen",urlImagen);
            map.put("urlVideo",urlVideo);
            //reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);
            refNotMess.setPriority(10);


            // save in local db
            db.saveMessageOnLocakDB(user.Email, friendEmail, message, sentDate,urlImagen,urlVideo);
            // appendmessage
            appendMessage(message, sentDate, 1, false,urlImagen,urlVideo, false);

        }
    }
    public void removeMessage(String mess, String sentDate, int messType, final boolean scrollUp,String urlImagen,String urlVideo) {

    }
    public void appendMessage(String mess, String sentDate, int messType, final boolean scrollUp, String urlImagen, String urlVideo, boolean swipeRefresh) throws ParseException {

        EmojiconTextView textView = new EmojiconTextView(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = metrics.densityDpi;
        if(densityDpi >400){
            textView.setEmojiconSize(80);
        }
        else{
            textView.setEmojiconSize(50);
        }
        sentDate = Tools.messageSentDateProper(sentDate);
        SpannableString dateString = new SpannableString(sentDate);
        dateString.setSpan(new RelativeSizeSpan(0.7f), 0, sentDate.length(), 0);
        dateString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, sentDate.length(), 0);

        textView.setText(mess + "\n");
        textView.append(dateString);
        textView.setTextColor(Color.parseColor("#000000"));


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                6f
        );
        lp.setMargins(0, 0, 0, 5);
        // 1 user
        if (messType == 1) {
            textView.setBackgroundResource(R.drawable.messagebg1);
            lp.gravity = Gravity.RIGHT;
        }
        //  2 friend
        else {
            textView.setBackgroundResource(R.drawable.messagebg2);
            lp.gravity = Gravity.LEFT;
        }

        textView.setPadding(12, 4, 12, 4);

        textView.setLayoutParams(lp);
        if("--[IMAGE]--".equals(mess.trim())){
            final MyImageView imgView=new MyImageView(this);
            imgView.setClaveImagen(urlImagen);
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Class<? extends View> aClass = v.getClass();

                        String value = ((MyImageView) v).getClaveImagen();
                        Intent i = new Intent(getApplicationContext(), VisorImagenesFTP.class);
                        i.putExtra("key", value);
                        startActivity(i);

                    }
                });
            DownloadImageTask execute = new DownloadImageTask(imgView, getApplicationContext(),scrollView,swipeRefresh,false);
            execute.execute(StaticInfo.urlWebImages + urlImagen);
            if (messType == 1) {
                imgView.setBackgroundResource(R.drawable.messagebg1);
            }
            //  2 friend
            else {
                imgView.setBackgroundResource(R.drawable.messagebg2);
            }
            imgView.setMaxWidth(240);
            imgView.setMaxHeight(480);
            imgView.setPadding(40,40,40,40);
            imgView.setLayoutParams(lp);
            layout.addView(imgView);
        }else if("--[VIDEO]--".equals(mess.trim())) {
            final MyImageView imgView=new MyImageView(this);
            imgView.setClaveImagen(urlVideo);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class<? extends View> aClass = v.getClass();

                    String value = ((MyImageView) v).getClaveImagen();
                    Intent i = new Intent(getApplicationContext(), VisorVideosFTP.class);
                    i.putExtra("key", value);
                    startActivity(i);

                }
            });
            DownloadImageTask execute = new DownloadImageTask(imgView, getApplicationContext(), scrollView, swipeRefresh,true);
            execute.execute(StaticInfo.urlWebImages + urlVideo);
            if (messType == 1) {
                imgView.setBackgroundResource(R.drawable.messagebg1);
            }
            //  2 friend
            else {
                imgView.setBackgroundResource(R.drawable.messagebg2);
            }
            imgView.setMaxWidth(240);
            imgView.setMaxHeight(480);
            imgView.setPadding(40,40,40,40);
            imgView.setLayoutParams(lp);
            layout.addView(imgView);
        }else{
            layout.addView(textView);
        }
        if(!swipeRefresh){
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    if(scrollUp){
                        scrollView.fullScroll(View.FOCUS_UP);
                    }else {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                    Log.d("SCROLL","9");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == StaticInfo.ChatAciviityRequestCode && resultCode == Activity.RESULT_OK) {
            User updatedFriend = db.getFriendByEmailFromLocalDB(friendEmail);
            friendFullName = updatedFriend.FirstName;
            //getSupportActionBar().setTitle(updatedFriend.FirstName);
        }
        if(requestCode==0 && resultCode==StaticInfo.ImageActivityRequestCode){
            String urlImagen="/images/" + timeInMillis + ".img";
            Map<String, String> map = new HashMap<>();
            map.put("Message", "--[IMAGE]--");
            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);

            DateFormat dateFormat = new SimpleDateFormat("dd MM yy HH:mm");
            Date date = new Date();
            String sentDate = dateFormat.format(date);
            String urlVideo="";
            map.put("SentDate", sentDate);
            map.put("urlImagen",urlImagen);
            map.put("urlVideo",urlVideo);
            //reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);

            // save in local db
            db.saveMessageOnLocakDB(user.Email, friendEmail, "--[IMAGE]--", sentDate,urlImagen,urlVideo);

            // appendmessage
            try {
                appendMessage("--[IMAGE]--", sentDate, 1, false,urlImagen,urlVideo, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(requestCode==0 && resultCode==StaticInfo.VideoActivityRequestCode){
            String urlVideo="/videos/" + timeInMillis + ".vid";
            Map<String, String> map = new HashMap<>();
            map.put("Message", "--[VIDEO]--");
            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);

            DateFormat dateFormat = new SimpleDateFormat("dd MM yy HH:mm");
            Date date = new Date();
            String sentDate = dateFormat.format(date);
            String urlImagen="";
            map.put("SentDate", sentDate);
            map.put("urlImagen",urlImagen);
            map.put("urlVideo",urlVideo);
            //reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);

            // save in local db
            db.saveMessageOnLocakDB(user.Email, friendEmail, "--[VIDEO]--", sentDate,urlImagen,urlVideo);

            // appendmessage
            try {
                appendMessage("--[VIDEO]--", sentDate, 1, false,urlImagen,urlVideo, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
