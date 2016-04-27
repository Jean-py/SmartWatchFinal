package s8.projetsmartwatch.tapping_gesture.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
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
import s8.projetsmartwatch.tapping_gesture.comportement.TappingGestureView;
import s8.projetsmartwatch.tapping_gesture.interfaces.MyActivity;


public class TappingGestureActivityAreUOK extends MyActivity implements GoogleApiClient.ConnectionCallbacks,MessageApi.MessageListener{

private final static String START_ACTIVITY = "/start/activity";
private final static String WEAR_MESSAGE_PATH = "/message";
private GoogleApiClient mApiClient;
private GoogleApiClient mApiC;

    /**
     * Cette classe permet de lancer un Tapping gesture permettant de
     * confirmer que le Pilote va bien
     *
     */
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private Button buttonYes;
    private Button buttonNo;
    private TextView titre;
    private Button buttonTappingGesture;
    int xButton;
    int yButton;
    //La Vi
    TappingGestureView myView;



    ///TODO potentiel perte d'information?
    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        //On suppose que Pas de message reçu lors de la demande du are U OK?
    }

   @Override
   public void showButton() {
        buttonNo.setVisibility(View.VISIBLE);
        buttonYes.setVisibility(View.VISIBLE);
   }

    @Override
   public void hideButton() {
        buttonNo.setVisibility(View.INVISIBLE);
        buttonYes.setVisibility(View.INVISIBLE);
   }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tapping_gesture_areuok);
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
        buttonYes = (Button) findViewById(R.id.buttonYes);
        buttonNo = (Button) findViewById(R.id.buttonNo);
        titre = (TextView) findViewById(R.id.areUOK);
        buttonTappingGesture = (Button) findViewById(R.id.buttonTaping);
      //  System.out.println("buttonTappingGesture : " + buttonTappingGesture) ;
        xButton = (int) buttonTappingGesture.getX();
        yButton = (int) buttonTappingGesture.getY();

        //Appel a ma classe qui gère le comportement du tapping gesture
        TappingGestureView tgv = new TappingGestureView("my tapping gesture 1", this);
        buttonTappingGesture.setOnTouchListener(tgv);

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

    //envois un message a la tablette
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




    public void callbackButtonNo() {
        sendMessage(WEAR_MESSAGE_PATH, "notOk");
        showButton();
        setContentView(R.layout.message_sent_to_the_fms);
    }


    public void callbackButtonYes() {
            showButton();
            Intent intent = new Intent(TappingGestureActivityAreUOK.this, MainActivity.class);
        finish();
        startActivity(intent);
    }


    /**
     * Fonction servant a tester les formes de touch.
     * @param event
     * @return
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int index = MotionEventCompat.getActionIndex(event);
        if (MotionEventCompat.getPointerCount(event) == 1) {
            // Single touch event
            //xPosFinger1 = (int) MotionEventCompat.getX(event, 0);
            //yPosFinger1 = (int) MotionEventCompat.getY(event, 0);
            // Log.d("TappingGestureActivityAlarme", "Single touch event   x1 : " + xPosFinger1 +  "  y1 : " + yPosFinger1+
        } else if (event.getPointerCount() > 1) {
           /* if(MotionEventCompat.getX(event,0) >= buttonTappingGesture.getX()  && MotionEventCompat.getX(event,1) >= buttonTappingGesture.getX() ) {
                // The coordinates of the current screen contact, relative to
                // the responding View or Activity.
                xPosFinger1 = (int) MotionEventCompat.getX(event, 0);
                yPosFinger1 = (int) MotionEventCompat.getY(event, 0);
                xPosFinger2 = (int) MotionEventCompat.getX(event, 1);
                yPosFinger2 = (int) MotionEventCompat.getY(event, 1);
                // Log.d("TappingGestureActivityAlarme", "Single touch event   x1 : " + xPosFinger1 +  "  y1 : " + yPosFinger1+
                // "  x2 : " + xPosFinger2 +  "  y2 : " + yPosFinger2);
                Log.d("premier if","1"+ "// finger1 : "+ finger1 + "  finger2 : " + finger2 + "  doubleTouchHappend : " + doubleTouchHappend);
                */
        }/*
        //checker les action UP
        if( MotionEventCompat.findPointerIndex(event, 0) == -1){
         /*   buttonNo.setVisibility(View.INVISIBLE);
            buttonYes.setVisibility(View.INVISIBLE);

            Log.d("dernier if", "4"+ "// finger1 : "+ finger1 + "  finger2 : " + finger2 + "  doubleTouchHappend : " + doubleTouchHappend);
            //buttonNo.setVisibility(View.INVISIBLE);
            //buttonYes.setVisibility(View.INVISIBLE);
        }
        */

        return super.dispatchTouchEvent(event);
    }


    public void retourPilotingMode(View v){
        Intent intent = new Intent(TappingGestureActivityAreUOK.this, MainActivity.class);
        finish();
        startActivity(intent);

    }

}
