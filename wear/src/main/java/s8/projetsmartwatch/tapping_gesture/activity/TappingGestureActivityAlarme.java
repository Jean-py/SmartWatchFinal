package s8.projetsmartwatch.tapping_gesture.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.wearable.view.BoxInsetLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import s8.projetsmartwatch.MainActivity;
import s8.projetsmartwatch.R;
import s8.projetsmartwatch.tapping_gesture.comportement.TappingGestureView;
import s8.projetsmartwatch.tapping_gesture.interfaces.MyActivity;


public class TappingGestureActivityAlarme extends MyActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private Button buttonYes;
    private Button buttonNo;
    private TextView areUOk;
    private Button buttonTappingGesture;
    private int xButton;
    private int yButton;

    private TappingGestureView tgv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tapping_gesture_alarme);
        tgv = new TappingGestureView("Tapping alarme" , this);
        //setAmbientEnabled();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);

        buttonNo = (Button) findViewById(R.id.buttonNo);
        areUOk = (TextView) findViewById(R.id.areUOK);
        buttonTappingGesture = (Button) findViewById(R.id.buttonTaping);
        System.out.println("buttonTappingGesture : " + buttonTappingGesture) ;
        xButton = (int) buttonTappingGesture.getX();
        yButton = (int) buttonTappingGesture.getY();

        buttonTappingGesture.setOnTouchListener(tgv );

        //listener bouton No
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackButtonNo();
            }
        });
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

    private void callbackButtonNo() {
        Intent intent = new Intent(TappingGestureActivityAlarme.this, MainActivity.class );
        finish();
        startActivity(intent);
       // showButton();
    }

    public void showButton() {
        buttonNo.setVisibility(View.VISIBLE);

    }

    public void hideButton() {
        buttonNo.setVisibility(View.INVISIBLE);
    }

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

            Log.d("dernier if", "4"+ "// finger1 : "+ finger1 + "  finger2 : " + finger2 + "  doubleTouchHappend : " + doubleTouchHappend);
            //buttonNo.setVisibility(View.INVISIBLE);

        }
        */
        return super.dispatchTouchEvent(event);
    }


}
