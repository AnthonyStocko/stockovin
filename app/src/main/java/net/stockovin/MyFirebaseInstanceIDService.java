package net.stockovin;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
/**
 * Created by delaroy on 10/8/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Cette méthode remplace l'ancienne méthode onTokenRefresh().
     * Elle est appelée automatiquement par le SDK Firebase dès qu'un nouveau token est généré.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nouveau token Firebase généré : " + token);

        // Si tu as une logique pour envoyer ce token à ton serveur web / base de données,
        // appelle ta méthode ici. Exemple : sendRegistrationToServer(token);
    }
}