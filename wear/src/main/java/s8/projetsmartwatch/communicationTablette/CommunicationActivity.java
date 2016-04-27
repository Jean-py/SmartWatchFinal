package s8.projetsmartwatch.communicationTablette;


import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import s8.projetsmartwatch.R;


public class CommunicationActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,MessageApi.MessageListener{

    private final static String START_ACTIVITY = "/start/activity";
    private final static String WEAR_MESSAGE_PATH = "/message";
    private GoogleApiClient mApiClient;
    private GoogleApiClient mApiC;

    private static final String TAG = "PhoneActivity: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    System.out.println(messageEvent.getPath().toString());
                    String s = new String(messageEvent.getData());
                    System.out.println(s);

                }
            }
        });
    }

    public void alarme(View view){
        sendMessage(WEAR_MESSAGE_PATH,"alarme");
    }



    public void changeLayout(View view) {
        setContentView(R.layout.activity_main);
    }
}
