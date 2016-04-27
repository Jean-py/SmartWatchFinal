    package s8.projetsmartwatch;


    import android.content.Context;
    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Vibrator;
    import android.support.v4.app.FragmentActivity;
    import android.view.MotionEvent;
    import android.view.View;

    import com.google.android.gms.common.api.GoogleApiClient;
    import com.google.android.gms.wearable.MessageApi;
    import com.google.android.gms.wearable.MessageEvent;
    import com.google.android.gms.wearable.Node;
    import com.google.android.gms.wearable.NodeApi;
    import com.google.android.gms.wearable.Wearable;

    import s8.projetsmartwatch.etat.Etat;
    import s8.projetsmartwatch.tapping_gesture.activity.TappingGestureActivityAlarme;
    import s8.projetsmartwatch.tapping_gesture.activity.TappingGestureActivityAreUOK;
    import s8.projetsmartwatch.tapping_gesture.activity.TappingGestureActivityP2ok;

    public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,MessageApi.MessageListener{

        private final static String START_ACTIVITY = "/start/activity";
        private final static String WEAR_MESSAGE_PATH = "/message";
        private GoogleApiClient mApiClient;
        private GoogleApiClient mApiC;
        Etat etat = Etat.PILOTING;


        private float y1,y2,x1,x2;
        static final int MIN_DISTANCE = 150;

        private static final String TAG = "PhoneActivity: ";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.piloting_view);
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


            //permet de swipe sur le mode
            View v = findViewById(android.R.id.content);
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    callbackOnTouch(v,event);
                    return false;
                }
            });



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
                    } else if(Math.abs(deltaY) > MIN_DISTANCE &&   deltaY < 0){
                        setContentView(R.layout.piloting_view);
                    } else if(Math.abs(deltaX) > MIN_DISTANCE) {
                        setContentView(R.layout.send_message_view);
                    }
                    break;
            }
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

        public void changeActivityForAlarme(){
            //System.out.println("BLOOOO");
            Intent intent = new Intent(MainActivity.this, TappingGestureActivityAlarme.class);
            startActivity(intent);
            finish();
        }

        public void changeActivityForAreUOk(){
           // System.out.println("BLAAAA");
            Intent intent = new Intent(MainActivity.this, TappingGestureActivityAreUOK.class );
            startActivity(intent);
            finish();
        }

        @Override
        public void onMessageReceived(final MessageEvent messageEvent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
                        System.out.println(messageEvent.getPath().toString());
                        String s = new String(messageEvent.getData());
                        System.out.println("Message received "+ s);
                        //probleme du cockpit ou envois d'un message du p2 ou probleme de santé du P2
                        if (s.equalsIgnoreCase("alarmeTotale") ) {
                            System.out.println("alarme totale, ETAT : " + etat);
                            switch(etat){
                                case PILOTING:
                                    alarmeVibrante();
                                    changeActivityForAlarme();
                                    break;
                                case RESTING:
                                    alarmeTotale();
                                    changeActivityForAlarme();
                                    break;
                            }
                        }
                        //émulation d'un probleme de santé
                        if(s.equalsIgnoreCase("areUOK")){
                            switch(etat){
                                case PILOTING :
                                    alarmeVibrante();
                                    changeActivityForAreUOk();
                                    break;
                                case RESTING:
                                    alarmeTotale();
                                    changeActivityForAreUOk();
                                    break;
                            }
                        }
                        if(s.equalsIgnoreCase("P2ProblemeHealth")){
                            switch(etat){
                                case PILOTING :
                                    alarmeVibrante();
                                    changeActivityForP2ok();
                                    break;
                                case RESTING:
                                    alarmeTotale();
                                    changeActivityForP2ok();
                                    break;
                            }
                        }
                    }
                }
            });
        }

        private void alarmeType3CallBack() {
        }

        private void alarmType2CallBack() {
        }

        public void alarmeVibrante(){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1000);
        }

        public void alarmeTotale(){
            alarmeVibrante();
            sonOn();
            int i=0;
            while(i<1000000)
                i++;
            //sonOff();


        }

        public void sonOn(){
            sendMessage(WEAR_MESSAGE_PATH,"allumer");
        }

        public void sonOff(){
            sendMessage(WEAR_MESSAGE_PATH, "eteindre");
        }


        public void changeActivityForP2ok(){
            Intent intent = new Intent(MainActivity.this, TappingGestureActivityP2ok.class);
            startActivity(intent);
            finish();
        }

    }
