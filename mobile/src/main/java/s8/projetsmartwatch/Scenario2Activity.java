package s8.projetsmartwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by jp on 27/04/16.
 */
public class Scenario2Activity  extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private static String WEAR_MESSAGE_PATH = "/message";
    private final static String START_ACTIVITY = "/start/activity";

    private GoogleApiClient mApiClient;
    private GoogleApiClient mApiC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenario2);
        mApiC = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mApiC.connect();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        if (mApiClient != null && !(mApiClient.isConnected() || mApiClient.isConnecting())) {
            mApiClient.connect();
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
        sendMessage(START_ACTIVITY, "");
        Wearable.MessageApi.addListener(mApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void sendMessage(final String path, final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiC).await();
                for(Node node : nodes.getNodes()){
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiC, node.getId(), path, message.getBytes()).await();
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
                    System.out.println(messageEvent.getPath().toString());
                    String s = new String(messageEvent.getData());
                    System.out.println(s);
                    if (s.equalsIgnoreCase("alarmeVisuelle")) {
                        alarmeVisuelle();
                    }
                    if (s.equalsIgnoreCase("alarmeVibrante")) {
                        alarmeVibrante();
                    }
                    if (s.equalsIgnoreCase("alarmeTotale")) {
                        alarmeTotale();
                    }
                }
            }
        });
    }

    public void alarme(View view){
        System.out.println("Envoie d'alarme au tel ");
        sendMessage(WEAR_MESSAGE_PATH, "alarme");
    }


    public void changeLayout(View view) {
        setContentView(R.layout.scenario1);
    }

    public void alarmeVibrante(){
        System.out.println("je vibre");
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }

    public void alarmeVisuelle(){
        setContentView(R.layout.alarme);
    }

    public void alarmeTotale(){
        alarmeVibrante();
        alarmeVisuelle();
        sonOn();
        int i=0;
        while(i<1000000)
            i++;
        // sonOff();
    }

    public void sonOn(){
        sendMessage(WEAR_MESSAGE_PATH,"allumer");
    }

    public void sonOff(){
        System.out.println("son off");
        sendMessage(WEAR_MESSAGE_PATH, "eteindre");
    }



    public void switchScenario1(View v){
        Intent intent = new Intent(Scenario2Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
