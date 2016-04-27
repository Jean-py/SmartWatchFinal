package s8.projetsmartwatch.tapping_gesture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Locale;

import s8.projetsmartwatch.MainActivity;
import s8.projetsmartwatch.R;


public class TappingGestureActivityP2ok extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,MessageApi.MessageListener{

private final static String START_ACTIVITY = "/start/activity";
private final static String WEAR_MESSAGE_PATH = "/message";
private GoogleApiClient mApiClient;
private GoogleApiClient mApiC;

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private Button buttonYes;
    private Button buttonNo;
    private TextView areUOk;
    private Button buttonTappingGesture;
    int xButton;
    int yButton;
    Etat state;

    Boolean p2Reponse = false ;
    Boolean p1Reponse = false;

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
                    System.out.println(messageEvent.getPath().toString());
                    String s = new String(messageEvent.getData());
                    System.out.println(s);
                    if (s.equalsIgnoreCase("P2IsOk")) {
                        p2Reponse = true;
                        if(p1Reponse){
                            System.out.println("P1 viens de reponder");
                            Intent intent = new Intent(TappingGestureActivityP2ok.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    if (s.equalsIgnoreCase("P2IsNotOk")) {
                        p2Reponse = true;
                        setContentView(R.layout.message_sent_to_the_fms);
                    }
                    if(s.equalsIgnoreCase("allIsfine")) {
                        p2Reponse = true;
                        Intent intent = new Intent(TappingGestureActivityP2ok.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        });
    }

    public enum Etat {
        INIT, FINGER1, FINGER2, TAPPING_GESTURE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tapping_gesture_p2_ok);
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

        //setAmbientEnabled();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        buttonYes = (Button) findViewById(R.id.buttonYesForP2);
        buttonNo = (Button) findViewById(R.id.buttonNoForP2);
        areUOk = (TextView) findViewById(R.id.P2ok);
        buttonTappingGesture = (Button) findViewById(R.id.buttonTaping);
        System.out.println("buttonTappingGesture : " + buttonTappingGesture) ;
        xButton = (int) buttonTappingGesture.getX();
        yButton = (int) buttonTappingGesture.getY();
        state = Etat.INIT;

        buttonTappingGesture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case (MotionEvent.ACTION_UP):
                        callBackActionUp(event);
                        break;
                    case (MotionEvent.ACTION_POINTER_DOWN):
                        if (stayOnTheButtonTappingGesture(event, 2)) {
                            callBackActionPointerDown(event);
                        }

                        break;
                    case (MotionEvent.ACTION_POINTER_UP):
                        callBackActionActionPointerUp(event);
                        break;

                    case (MotionEvent.ACTION_DOWN):
                        callbackButtonActionDown(event);
                        break;
                }
                return true;
            }
        });
        //listener bouton No
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackButtonNo();
            }
        });
        //listener bouton yes
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackButtonYes();
            }
        });
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

    public boolean stayOnTheButtonTappingGesture(MotionEvent event, int numberOfPointer) {

        boolean res = true;
        for (int i = 0; i < numberOfPointer; i++) {
            System.out.println(" getX(" + i + ") : " + event.getX(i) + "   buttonTappingGesture.getX() : " + buttonTappingGesture.getX());
            System.out.println(" getY(" + i + ") : " + event.getY(i) + "   buttonTappingGesture.getY() : " + buttonTappingGesture.getY());
            res = res && event.getX(i) > buttonTappingGesture.getX() - 100;
        }
        return res;
    }

    //reponse a la question is P2 ok?
    public void callbackButtonNo() {
        p1Reponse = true;
        sendMessage(WEAR_MESSAGE_PATH, "P2IsnotOk");
        showButton();
        setContentView(R.layout.message_sent_to_the_fms);

    }

    //reponse a la question is P2 ok?
    public void callbackButtonYes() {
        showButton();
        p1Reponse = true;
        if(p2Reponse){
            p2Reponse = false;
            Intent intent = new Intent(TappingGestureActivityP2ok.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            sendMessage(WEAR_MESSAGE_PATH,"uSeemOk");
            setContentView(R.layout.waitng_p2);
        }

    }

    //premier doigt sur l'écran
    public void callbackButtonActionDown(MotionEvent event) {
        //System.out.println("Action Down");
        switch (state) {
            case INIT:
                state = Etat.FINGER1;
                break;
            case FINGER1:
                //impossible
                break;
            case FINGER2: //impossible
                break;
            case TAPPING_GESTURE://impossible
                break;
        }
    }

    //second doigt sur l'écran
    private void callBackActionPointerDown(MotionEvent event) {
        //System.out.println("Action pointer down");
        switch (state) {
            case INIT:
                break;
            case FINGER1:
                state = Etat.FINGER2;
                hideButton();
                break;
            case FINGER2:
                //nothing
                break;
            case TAPPING_GESTURE:
                state = Etat.FINGER2;
                showButton();
                break;
        }
    }

    //premier doigt enlevé
    private void callBackActionUp(MotionEvent event) {
        System.out.println("Action up");
        switch (state) {
            case INIT:
                //impossible
                break;
            case FINGER1:
                state = Etat.INIT;
                hideButton();
                break;
            case FINGER2:
                state = Etat.INIT;
                hideButton();
                break;
            case TAPPING_GESTURE:
                state = Etat.INIT;
                hideButton();
                break;
        }
    }

    //on enleve le second doigt
    private void callBackActionActionPointerUp(MotionEvent event) {
        //System.out.println("action Pointer up");
        switch (state) {
            case INIT: //impossible
                break;
            case FINGER1: //impossible
                break;
            case FINGER2:
                state = Etat.TAPPING_GESTURE;
                showButton();
                break;
            case TAPPING_GESTURE: // impossible
                break;
        }
    }

    public void showButton() {
        buttonNo.setVisibility(View.VISIBLE);
        buttonYes.setVisibility(View.VISIBLE);
    }

    public void hideButton() {
        buttonNo.setVisibility(View.INVISIBLE);
        buttonYes.setVisibility(View.INVISIBLE);
    }

    public void retourPilotingMode(View v){
        Intent intent = new Intent(TappingGestureActivityP2ok.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
