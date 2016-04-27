package s8.projetsmartwatch;

import android.app.Activity;

/**
 * Created by jp on 25/04/16.
 */
public class MainActivityTest extends Activity {
   /* private GridViewPager pager;
    private DotsPageIndicator dotsPageIndicator;
    //la liste des éléments à afficher
    private List<Element> elementList;
    private Etat etat;

    public enum Etat {
        PILOTING,RESTING;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = (GridViewPager) findViewById(R.id.pager);
       // dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);
        elementList = creerListElements();
        pager.setAdapter(new ElementGridPagerAdapter(elementList,getFragmentManager()));


        etat = Etat.PILOTING;
    }


    *//**
     * Créé une liste d'éléments pour l'affichage
     *//*
    private List<Element> creerListElements() {
        List<Element> list = new ArrayList<>();
        //couleur du haut
        list.add(new Element("  ","Piloting mode ", Color.parseColor("#000000")));
        list.add(new Element("   ","Resting mode ", Color.parseColor("#000000")));
        return list;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        int xPos, yPos;
        int index = MotionEventCompat.getActionIndex(event);

        if (event.getPointerCount() > 1) {
            Log.d("MainActivityTest", "Multitouch event");
            // The coordinates of the current screen contact, relative to
            // the responding View or Activity.
            xPos = (int)MotionEventCompat.getX(event, index);
            yPos = (int)MotionEventCompat.getY(event, index);

        } else {
            // Single touch event
            // Log.d("MainActivityTest", "Single touch event");
            xPos = (int)MotionEventCompat.getX(event, index);
            yPos = (int)MotionEventCompat.getY(event, index);

        }

        //changement d'état selon l'element "pické"
        *//*
        System.out.println("pager :  " + pager.getCurrentItem());
        System.out.println("pager -  y : " + pager.getCurrentItem().y + "  , x " + pager.getCurrentItem().x);
        if(pager.getCurrentItem().y == 0 ){
            etat = Etat.PILOTING;
        } else {
            etat = Etat.RESTING;
        }*//*

        //System.out.println(pager.getCurrentItem());
       // System.out.println(" " + this.getWindow().getDecorView().findViewById(android.R.id.content));

       // System.out.println("Etat courant : " + etat);
      //  System.out.println("aaa" + pager.getCurrentItem() ) ;

        return super.dispatchTouchEvent(event);
    }*/

}
