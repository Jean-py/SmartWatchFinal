package s8.projetsmartwatch;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private static String WEAR_MESSAGE_PATH = "/message";
    private final static String START_ACTIVITY = "/start/activity";

    private GoogleApiClient mApiClient;
    private GoogleApiClient mApiC;

    boolean p1Reponse = false;
    boolean p2Reponse = false;

    TextView textViewP1Reponse;
    TextView textViewEtatMontre;

    Button bYes ;
    Button bNo ;
    String messageRecu = "";
    Button p2SaidNo ;
    Button p2SaidYes ;

    MediaPlayer mp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenario1);
        mp = MediaPlayer.create(this,R.raw.alarm);


        bYes = (Button)findViewById(R.id.buttonYesScenario1P2);
        bNo = (Button) findViewById(R.id.buttonNoScenario1P2);
        p2SaidNo = (Button) findViewById(R.id.p2SaidNo);
        p2SaidYes = (Button) findViewById(R.id.p2SaidYes);

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
        textViewEtatMontre = (TextView) findViewById(R.id.textViewEtatMontre);
        textViewP1Reponse = (TextView) findViewById(R.id.textViewP1Reponse);
        textViewP1Reponse.setMovementMethod(new ScrollingMovementMethod());
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
        System.out.println("[Mobile] MainActivity Envois de message a la montre");
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
                String s = " " ;
                if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
                    //System.out.println("mobile message received : " + messageEvent.getPath().toString());
                    s = new String(messageEvent.getData());
                    if(s.equalsIgnoreCase("notOk") ){
                     //   textViewP1Reponse.append(messageRecu);
                    }
                    //reception d'un message P1 lors d'un probleme de santé de P2
                    if(s.equalsIgnoreCase("P2IsnotOk") ){
                        messageRecu= "P2IsnotOk";
                        p1Reponse =true;
                    }
                    if(s.equalsIgnoreCase("uSeemOk") ){
                        messageRecu = "uSeemOk";
                        p1Reponse = true;
                    }
                    if(s.equalsIgnoreCase("scenarioPiloting") ){
                        messageRecu = "scenarioPiloting";
                        textViewEtatMontre.setText("Pilot #1 : Piloting mode");
                    }
                    if(s.equalsIgnoreCase("scenarioResting") ){
                        messageRecu = "scenarioResting";
                        textViewEtatMontre.setText("Pilot #1 : Resting mode");
                    }
                    if(s.equalsIgnoreCase("scenarioSending") ){
                        messageRecu = "scenarioSending";
                        textViewEtatMontre.setText("Pilot #1 : Sending mode");
                    }
                    if( s.equalsIgnoreCase("eteindre") ) {
                        eteindreSon();
                    }
                    if( s.equalsIgnoreCase("allumer") ){
                        allumerSon();
                    }
                    textViewP1Reponse.append(s + "\n");
                }
            }
        });
    }

    int i = 0;

    public void eteindreSon() {
        if(mp.isPlaying())
            mp.pause();
    }

    public void allumerSon() {

       // mp.reset();

        mp.start();
    }


    public void changeLayout(View view) {
        setContentView(R.layout.scenario1);
    }

    //envois de l'affichage de are u ok au P1
    public void scenario1HealthP1(View v){
        sendMessage(WEAR_MESSAGE_PATH, "areUOK");
        //System.out.println("[MOBILE] click proleme P1 (envois de areUok) ");
        showButtonP1YesNo();
    }

    //si le P2 (tablette) dit qu'il y a un probleme
    public void p2SaidYesCallBack(View v){
        System.out.println("P2 is ok : Callback button No");
        sendMessage(WEAR_MESSAGE_PATH, "p1NotOk");
        eteindreSon();
       hideButtonP1YesNo();
    }

    public void p2SaidNoCallBack(View v){
        // System.out.println("P2 is ok : Callback button yes");
        p2Reponse = true;
        //System.out.println("p1 : " + p1Reponse + "p2 : " + p2Reponse);
        if(p1Reponse){

            p2Reponse = false;
            p1Reponse = false;
        } else {
            sendMessage(WEAR_MESSAGE_PATH,"uSeemOk");
            p2Reponse=false;
        }
        Button p2SaidNo = (Button) findViewById(R.id.p2SaidNo);
        Button p2SaidYes = (Button) findViewById(R.id.p2SaidYes);
        p2SaidNo.setVisibility(View.INVISIBLE);
        p2SaidYes.setVisibility(View.INVISIBLE);
    }

    //affichage des boutons oui et non
    public void scenario1HealthP2(View v){
        sendMessage(WEAR_MESSAGE_PATH,"P2ProblemeHealth");
        showButtonP2YesNo();
    }

    public void buttonYesScenario1P2(View v){
        hideButtonP2YesNo();
        sendMessage(WEAR_MESSAGE_PATH,"P2IsNotOk");
        eteindreSon();
    }


    public void buttonNoScenario1P2(View v){
        hideButtonP2YesNo();
        sendMessage(WEAR_MESSAGE_PATH,"P2IsOk");
        hideButtonP2YesNo();
        textViewP1Reponse.append(" en attente d'un message ... " +"\n");
    }


    public void hideButtonP2YesNo(){
        bYes.setVisibility(View.INVISIBLE);
        bNo.setVisibility(View.INVISIBLE);

    }

    public void hideButtonP1YesNo(){
        p2SaidNo.setVisibility(View.INVISIBLE);
        p2SaidYes.setVisibility(View.INVISIBLE);
    }

    public void showButtonP2YesNo(){
        bYes.setVisibility(View.VISIBLE);
        bNo.setVisibility(View.VISIBLE);
    }

    public void showButtonP1YesNo ( ){
        p2SaidNo.setVisibility(View.VISIBLE);
        p2SaidYes.setVisibility(View.VISIBLE);
    }


    public void scenario1SendAlert(View v){
        sendMessage(WEAR_MESSAGE_PATH,"alarmeTotale");
    }
}
