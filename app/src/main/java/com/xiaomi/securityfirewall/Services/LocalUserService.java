package com.xiaomi.securityfirewall.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiaomi.securityfirewall.Models.User;


public class LocalUserService {
    public static User getLocalUserFromPreferences(Context context){
        SharedPreferences pref = context.getSharedPreferences("LocalUser",Context.MODE_PRIVATE);
        User user = new User();
        user.Email = pref.getString("Email",null);
        user.FirstName = pref.getString("FirstName",null);
        user.LastName = pref.getString("LastName",null);
        user.Notificaciones=pref.getBoolean("notify",false);
        return user;
    }

    public static boolean deleteLocalUserFromPreferences(Context context){
        try {
            SharedPreferences pref = context.getSharedPreferences("LocalUser",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }



}
