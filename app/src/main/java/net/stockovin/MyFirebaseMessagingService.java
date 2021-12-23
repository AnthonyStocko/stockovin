package net.stockovin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.animation.AlphaAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by delaroy on 10/8/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            /*MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            mNotificationManager.showSmallNotification("", "", intent);*/

            JSONObject json = new JSONObject(remoteMessage.getData());
            try {
                sendPushNotification(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) throws JSONException {

            String data = json.toJSONArray(json.names()).getString(0);

            JSONObject bodyData = new JSONObject(data);
            String title = bodyData.getString("title");
            String message = bodyData.getString("message");
            String imageUrl = bodyData.getString("image");

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), UserFriendListActivity.class);

            //if there is no image
            if(imageUrl.equals("null")){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }else{
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
    }

}