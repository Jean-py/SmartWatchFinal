package s8.projetsmartwatch.tapping_gesture.comportement;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import s8.projetsmartwatch.tapping_gesture.interfaces.MyActivity;

/**
 * Created by jp on 28/04/16.
 */
public class TappingGestureView implements View.OnTouchListener {


    private MyActivity tappingGA ;
    private String titre;
    public enum Etat {
        INIT, FINGER1, FINGER2, TAPPING_GESTURE
    }

    private Etat state;

    public TappingGestureView(String titre, MyActivity tappingGA ) {
        super();
        this.titre = titre;

        this.state = Etat.INIT;
        this.tappingGA = tappingGA;
    }

        public boolean stayOnTheButtonTappingGesture(MotionEvent event, int numberOfPointer, Button buttonTappingGesture) {

        boolean res = true;
        for (int i = 0; i < numberOfPointer; i++) {
            System.out.println(" getX(" + i + ") : " + event.getX(i) + "   buttonTappingGesture.getX() : " + buttonTappingGesture.getX());
            System.out.println(" getY(" + i + ") : " + event.getY(i) + "   buttonTappingGesture.getY() : " + buttonTappingGesture.getY());
            res = res && event.getX(i) > buttonTappingGesture.getX() - 100;
        }
        return res;
    }



    //premier doigt sur l'écran
    private void callbackButtonActionDown(MotionEvent event) {
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
                tappingGA.hideButton();
                break;
            case FINGER2:
                //nothing
                break;
            case TAPPING_GESTURE:
                state = Etat.FINGER2;
                tappingGA.showButton();
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
                tappingGA.hideButton();
                break;
            case FINGER2:
                state = Etat.INIT;
                tappingGA.hideButton();
                break;
            case TAPPING_GESTURE:
                state = Etat.INIT;
                tappingGA. hideButton();
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
                tappingGA.showButton();
                break;
            case TAPPING_GESTURE: // impossible
                break;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case (MotionEvent.ACTION_UP):
                    callBackActionUp(event);
                    break;
                case (MotionEvent.ACTION_POINTER_DOWN):
                   // if (stayOnTheButtonTappingGesture(event, 2)) {
                        callBackActionPointerDown(event);
                  //  }

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
}
