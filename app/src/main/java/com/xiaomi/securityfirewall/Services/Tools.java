package com.xiaomi.securityfirewall.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Tools {

    public static final String ENDPOINT = "https://chat-8459f.firebaseio.com";

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }

    public static IFireBaseAPI makeRetroFitApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(IFireBaseAPI.class);
    }

    public static String toProperName(String s) {
        if (s.length() <= 1)
            return s.toUpperCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static int createUniqueIdPerUser(String userEmail) {
        String email = userEmail.split("@")[0].toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        final Map<Character, Integer> map;
        map = new HashMap<>();
        map.put('a', 1);
        map.put('b', 2);
        map.put('c', 3);
        map.put('d', 4);
        map.put('e', 5);
        map.put('f', 6);
        map.put('g', 7);
        map.put('h', 8);
        map.put('i', 9);
        map.put('j', 10);
        map.put('k', 11);
        map.put('l', 12);
        map.put('m', 13);
        map.put('n', 14);
        map.put('o', 15);
        map.put('p', 16);
        map.put('q', 17);
        map.put('r', 18);
        map.put('s', 19);
        map.put('t', 20);
        map.put('u', 21);
        map.put('v', 22);
        map.put('w', 23);
        map.put('x', 24);
        map.put('y', 25);
        map.put('z', 26);
        String intEmail = "";

        for (char c : email.toCharArray()) {
            int val = 0;
            try {
                val = map.get(c);
            } catch (Exception e) {

            }
            intEmail += val;
        }

        if (intEmail.length() > 9) {
            intEmail = intEmail.substring(0, 9);
        }

        return Integer.parseInt(intEmail);

    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9._-]+";
        return email.matches(emailPattern);
    }

    public static String toCharacterMonth(int month) {
        if (month == 1) return "Ene";
        else if (month == 2) return "Feb";
        else if (month == 3) return "Mar";
        else if (month == 4) return "Abr";
        else if (month == 5) return "May";
        else if (month == 6) return "Jun";
        else if (month == 7) return "Jul";
        else if (month == 8) return "Ago";
        else if (month == 9) return "Sep";
        else if (month == 10) return "Oct";
        else if (month == 11) return "Nov";
        else return "Dic";
    }

    public static String lastSeenProper(String lastSeenDate) throws ParseException {
        String[] date = lastSeenDate.split(" ");
        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a" );
        DateFormat dateFormat2 = new SimpleDateFormat("dd MM yy HH:mm" );
        Date parse = null;
        String format =lastSeenDate;
        if(lastSeenDate.indexOf("p")!=-1){
            parse=dateFormat.parse(String.valueOf(date[0]+" "+date[1]+" "+date[2]+" "+date[3])+" p. m.");
            format=dateFormat2.format(parse);
        }else if(lastSeenDate.indexOf("a")!=-1){
            parse=dateFormat.parse(String.valueOf(date[0]+" "+date[1]+" "+date[2]+" "+date[3])+" a. m.");
            format=dateFormat2.format(parse);
        }

        String[] s = format.split(" ");

        return "Visto " + s[0] + " " + Tools.toCharacterMonth(Integer.parseInt(s[1])) + " " + s[2]+ " "+s[3];
        /*try {
            nw = dateFormat.parse(cuurentDateString);
            seen = dateFormat.parse(lastSeenDate);
            long diff = nw.getTime() - seen.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffMinutes = diff / (60 * 1000) % 60;
            if (diffDays > 0) {
                originalDate = lastSeenDate.split(" ");
                return "Visto " + originalDate[0] + " " + Tools.toCharacterMonth(Integer.parseInt(originalDate[1])) + " " + originalDate[2]+ " "+originalDate[3]+" "+originalDate[4]+" "+originalDate[5];
            } else if (diffHours > 0)
                return "Visto hace " + diffHours + " horas";
            else if (diffMinutes > 0) {
                if (diffMinutes <= 1) {
                    return "Visto hace 1 minuto";
                } else {
                    return "Visto hace " + diffMinutes + " minutos";
                }
            } else return "Visto hace un momento";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }*/


    }

    public static String messageSentDateProper(String sentDate) throws ParseException {
        String properDate = "";
        Calendar cal = Calendar.getInstance();
        Date todayDate = new Date();
        cal.setTime(todayDate);
        String[] date = sentDate.split(" ");
        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a" );
        DateFormat dateFormat2 = new SimpleDateFormat("dd MM yy HH:mm" );
        Date parse = null;
        String format=null;
        format=sentDate;
        if(sentDate.indexOf("p")!=-1){
            parse=dateFormat.parse(String.valueOf(date[0]+" "+date[1]+" "+date[2]+" "+date[3])+" p. m.");
            format= dateFormat2.format(parse);
        }else if(sentDate.indexOf("a")!=-1){
            parse=dateFormat.parse(String.valueOf(date[0]+" "+date[1]+" "+date[2]+" "+date[3])+" a. m.");
            format= dateFormat2.format(parse);
        }

        String[] s = format.split(" ");

        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int todayDay = cal.get(Calendar.DAY_OF_MONTH);
        if (todayMonth == Integer.parseInt(date[1]) && todayDay == Integer.parseInt(date[0])) {
            properDate = "Hoy" + " " + s[3];
            // 06 11 17 12:28 AM
        } else if (todayMonth == Integer.parseInt(date[1]) && (todayDay - 1) == Integer.parseInt(date[0])) {
            properDate = "Ayer" + " " + s[3];
        } else {
            properDate = date[0] + " " + Tools.toCharacterMonth(Integer.parseInt(date[1])) + " " + date[2] + " " + s[3];
        }
        return properDate;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
