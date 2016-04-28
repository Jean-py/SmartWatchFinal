    package s8.projetsmartwatch;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import s8.projetsmartwatch.etat.Etat;
import s8.projetsmartwatch.tapping_gesture.comportement.TappingGestureView;


    public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,MessageApi.MessageListener{

        private final static String START_ACTIVITY = "/start/activity";
        private final static String WEAR_MESSAGE_PATH = "/message";
        private GoogleApiClient mApiClient;
        private GoogleApiClient mApiC;
        private Etat etat = Etat.PILOTING;
        private boolean p1Reponse;
        private boolean p2Reponse;

        private long[] mLongs;
        Vibrator v ;



        private float y1,y2,x1,x2;
        private static final int MIN_DISTANCE = 150;

        private static final String TAG = "PhoneActivity: ";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.piloting_view);
            mLongs = new long[]{0, 2000, 500};
            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            etat = Etat.PILOTING;
            enabledSwipe();
            p1Reponse = false;
            p2Reponse = false;
            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();

            mApiClient.connect();

            mApiC = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .build();
            if (mApiC != null && !(mApiC.isConnected() || mApiC.isConnecting())) {
                mApiC.connect();
            }





        }

        /**
         * Call back sur la vue principale permettant de gerer le swipe.
         * @param v
         * @param event
         */
        private void callbackOnTouch(View v, MotionEvent event) {
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    y1 = event.getY();
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    y2 = event.getY();
                    x2 = event.getX();
                    float deltaY = y2 - y1;
                    float deltaX = x2 - x1;
                    if (deltaY > MIN_DISTANCE && deltaY > 0) {
                        setContentView(R.layout.resting_view);
                        etat = Etat.RESTING;
                        sendMessage(WEAR_MESSAGE_PATH,"scenarioResting");
                        enabledSwipe();
                    } else if(Math.abs(deltaY) > MIN_DISTANCE &&   deltaY < 0){
                        sendMessage(WEAR_MESSAGE_PATH,"scenarioPiloting");
                        setContentView(R.layout.piloting_view);

                        etat = Etat.PILOTING;
                        enabledSwipe();
                    } else if(Math.abs(deltaX) > MIN_DISTANCE) {
                        sendMessage(WEAR_MESSAGE_PATH,"scenarioSending");
                        setContentView(R.layout.send_message_view);
                        enabledSwipe();
                    }

                    break;
            }
            System.out.println("[WEAR]  etat : " + etat);
        }




        @Override
        protected void onStart() {
            super.onStart();
        }

        @Override
        protected void onDestroy(){
            super.onDestroy();
            mApiClient.disconnect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            sendMessage(START_ACTIVITY,"");
            Wearable.MessageApi.addListener(mApiC, this);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        private void sendMessage(final String path, final String message){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                    for(Node node : nodes.getNodes()){
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                mApiClient, node.getId(), path, message.getBytes()).await();

                    }
                }
            }).start();
        }

        @Override
        public void onMessageReceived(final MessageEvent messageEvent) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
                       // System.out.println(messageEvent.getPath().toString());
                        String s = new String(messageEvent.getData());
                        //System.out.println("Message received "+ s);
                        //probleme du cockpit ou envois d'un message du p2 ou probleme de santé du P2
                        if (s.equalsIgnoreCase("alarmeTotale") ) {
                           // System.out.println("alarme totale, ETAT : " + etat);
                            switch(etat){
                                case PILOTING:
                                    alarmeVibrante();
                                    changeViewForAlarme();
                                    break;
                                case RESTING:
                                    alarmeTotale();
                                    changeViewForAlarme();
                                    break;
                            }
                        }
                        //émulation d'un probleme de santé
                        if(s.equalsIgnoreCase("areUOK")){
                            switch(etat){
                                case PILOTING :
                                    alarmeVibrante();
                                    changeViewForAreUOk();
                                    break;
                                case RESTING:
                                    alarmeTotale();
                                    changeViewForAreUOk();
                                    break;
                            }
                        }
                        if(s.equalsIgnoreCase("P2ProblemeHealth")){
                            switch(etat){
                                case PILOTING :
                                    alarmeVibrante();
                                    changeViewForP2ok();
                                    break;
                                case RESTING:
                                    alarmeTotale();
                                    changeViewForP2ok();
                                    break;
                            }
                        }
                        if (s.equalsIgnoreCase("P2IsOk")) {
                            p2Reponse = true;
                            //System.out.println(" P1 reponse : " + p1Reponse);
                            System.out.println("[WEAR] p1Reponse " + p1Reponse);
                            if(p1Reponse){
                              //  System.out.println("P1 vient de repondre");
                                setContentView(R.layout.piloting_view);
                                etat = Etat.PILOTING;
                                sendMessage(WEAR_MESSAGE_PATH,"scenarioPiloting");
                                enabledSwipe();
                                p2Reponse = false;
                                p1Reponse = false;
                            }
                        }
                        if (s.equalsIgnoreCase("P2IsNotOk")) {
                            p2Reponse = true;
                            setContentView(R.layout.message_sent_to_the_fms);
                            disabledSwipe();
                            p2Reponse = false;
                            p1Reponse = false;
                        }
                        if(s.equalsIgnoreCase("allIsfine")) {
                            p2Reponse = true;
                            setContentView(R.layout.piloting_view);
                            etat= Etat.PILOTING;
                            sendMessage(WEAR_MESSAGE_PATH,"scenarioPiloting");
                            enabledSwipe();
                            p2Reponse = false;

                        }if(s.equalsIgnoreCase("p1NotOk")) {
                            p2Reponse = true;
                            setContentView(R.layout.message_sent_to_the_fms);
                            disabledSwipe();
                            p2Reponse = false;
                        }
                        if(s.equalsIgnoreCase("uSeemOk")) {
                            p2Reponse = true;
                            if(p1Reponse) {
                               // System.out.println("P1 vient de repondre");
                                p1Reponse = false;
                                setContentView(R.layout.piloting_view);
                                sendMessage(WEAR_MESSAGE_PATH,"scenarioPiloting");
                                etat = Etat.PILOTING;
                                enabledSwipe();
                                p2Reponse = false;
                            }
                        }
                    }
                }
            });
        }

        private void changeViewForAlarme(){
            setContentView(R.layout.tapping_gesture_alarme);
            disabledSwipe();
            Button buttonStop = (Button) findViewById(R.id.buttonStop);
            Button buttonTappingGesture = (Button) findViewById(R.id.buttonTapingAlarme);

            List<Button> buttons = new ArrayList<>();
            buttons.add(buttonStop);
            TappingGestureView tgv = new TappingGestureView("my tapping gesture 1",buttons);
            buttonTappingGesture.setOnTouchListener(tgv);
            buttonStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retourPilotingMode(v);
                }
            });



        }

        private void callbackButtonNo() {
            sendMessage(WEAR_MESSAGE_PATH, "notOk");
            eteindreAll();
            setContentView(R.layout.message_sent_to_the_fms);
            disabledSwipe();
        }


        private void alarmeVibrante(){
            v.vibrate(mLongs,0);
        }

        private void alarmeTotale(){

            alarmeVibrante();
            sonOn();
        }

        private void sonOn(){
            System.out.println("[WEAR] son ON");
            sendMessage(WEAR_MESSAGE_PATH,"allumer");
        }

        public void eteindreAll(){
            System.out.println("[WEAR] son OFF");
            sendMessage(WEAR_MESSAGE_PATH, "eteindre");
            v.cancel();
        }



        private void changeViewForAreUOk(){
            setContentView(R.layout.tapping_gesture_areuok);
            disabledSwipe();
            Button buttonYesAreUok = (Button) findViewById(R.id.buttonYesAreUOk);
            Button buttonNoAreUOk = (Button) findViewById(R.id.buttonNoAreUOk);
            Button buttonTappingGesture = (Button) findViewById(R.id.buttonTapingAreUok);
            List<Button> buttons = new ArrayList<>();
            buttons.add(buttonYesAreUok);
            buttons.add(buttonNoAreUOk);
            TappingGestureView tgv = new TappingGestureView("my tapping gesture 1",buttons);
            buttonTappingGesture.setOnTouchListener(tgv);
            //listener bouton No
            buttonNoAreUOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbackButtonNo();
                }
            });
            //listener bouton yes
            p1Reponse = false;
            buttonYesAreUok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eteindreAll();
                    if(p2Reponse){
                        p2Reponse = false;
                        p1Reponse = false;
                        retourPilotingMode(v);
                    } else {
                        p1Reponse = true;
                        setContentView(R.layout.waitng_p2);
                        disabledSwipe();
                    }
                }
            });
        }

        private void changeViewForP2ok(){
            setContentView(R.layout.tapping_gesture_p2_ok);
            disabledSwipe();
            Button buttonNoForP2 = (Button) findViewById(R.id.buttonNoForP2);
            Button buttonYesForP2 = (Button) findViewById(R.id.buttonYesForP2);
            Button buttonTappingGesture = (Button) findViewById(R.id.buttonTapingP2Ok);
            List<Button> buttons = new ArrayList<>();
            buttons.add(buttonNoForP2);
            buttons.add(buttonYesForP2);
            TappingGestureView tgv = new TappingGestureView("my tapping gesture 1",buttons);
            buttonTappingGesture.setOnTouchListener(tgv);
            //listener bouton No
            buttonNoForP2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbackButtonNoForP2();
                }
            });
            //listener bouton yes
            buttonYesForP2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbackButtonYesForP2(v);
                }
            });
        }




        //reponse a la question is P2 ok?
        private void callbackButtonNoForP2() {
           // System.out.println("P2 is ok : Callback button No");
            eteindreAll();
            sendMessage(WEAR_MESSAGE_PATH, "P2IsnotOk");
            setContentView(R.layout.message_sent_to_the_fms);
            disabledSwipe();
        }

        //reponse a la question is P2 ok?
        private void callbackButtonYesForP2(View v) {
           // System.out.println("P2 is ok : Callback button yes");
            p1Reponse = true;
            //System.out.println("p1 : " + p1Reponse + "p2 : " + p2Reponse);
            if(p2Reponse){
                p2Reponse = false;
                p1Reponse = false;
                eteindreAll();
                retourPilotingMode(v);
            } else {
                sendMessage(WEAR_MESSAGE_PATH,"uSeemOk");
                eteindreAll();
                setContentView(R.layout.waitng_p2);
                disabledSwipe();
                //permet de swipe sur le mode
                View currentView = findViewById(android.R.id.content);
                currentView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            }
        }


        public void retourPilotingMode(View v){
            eteindreAll();
            setContentView(R.layout.piloting_view);
            etat = Etat.PILOTING;
            sendMessage(WEAR_MESSAGE_PATH,"scenarioPiloting");
            enabledSwipe();
            p1Reponse = false;

        }

        //permet de placer le swipe de mode sur la vue courante
        public void enabledSwipe(){
            View v = findViewById(android.R.id.content);
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    callbackOnTouch(v,event);
                    return false;
                }
            });

        }

        public void disabledSwipe(){
            View v = findViewById(android.R.id.content);
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }

        public void sendMessageToFMS(View v){
            sendMessage(WEAR_MESSAGE_PATH,"Message to FMS");
        }

        public void sendMessageToP2(View v){
            sendMessage(WEAR_MESSAGE_PATH,"Message to P2");
        }

    }
