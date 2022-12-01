package es.fraggel.acalculator.Models;


import com.google.firebase.FirebaseOptions;

public class StaticInfo {

    public static String EndPoint = "https://chat-8459f.firebaseio.com";
    public static String MessagesEndPoint = "https://chat-8459f.firebaseio.com/messages";
    public static String FriendsURL = "https://chat-8459f.firebaseio.com/friends";
    public static String UsersURL = "https://chat-8459f.firebaseio.com/users";
    public static String UserCurrentChatFriendEmail = "";

    public static String TypingStatus = "TypingStatus";

    public static String NotificationEndPoint = "https://chat-8459f.firebaseio.com/notifications";
    public static String FiledirEndPoint = "https://chat-8459f.firebaseio.com/filedir";
    public static String FriendRequestsEndPoint = "https://chat-8459f.firebaseio.com/friendrequests";

    public static int ChatAciviityRequestCode = 101;
    public static int ImageActivityRequestCode=102;
    public static int VideoActivityRequestCode=103;
    public static FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
            .setApplicationId("chat-8459f")
            .setStorageBucket("chat-8459f.appspot.com")
            .setApiKey("AIzaSyA2oG-tbvlkYuVocLg0B78R0D0IVvE6FGI")
            .setDatabaseUrl("https://chat-8459f.firebaseio.com")
            .build();

}
