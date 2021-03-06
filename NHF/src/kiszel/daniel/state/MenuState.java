package kiszel.daniel.state;

import kiszel.daniel.UI.ClickListener;
import kiszel.daniel.UI.UIManager;
import kiszel.daniel.UI.UIObject;
import kiszel.daniel.entities.Player;
import kiszel.daniel.game.Handler;
import kiszel.daniel.graphic.Assets;
import kiszel.daniel.save.Save;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * Ez az osztály azért fontos mert ez lesz a főmenüje a játékunkba innen tudjunk a játékot elínditani
 * */

public class MenuState extends State implements Serializable {
    private UIManager uiManagermenu;
    public State gameState;
    public MenuState(Handler handler) {
        super(handler);
        uiManagermenu = new UIManager(handler);
        refreshButtons();
    }
    /**
     * isLoadable() metódussal nézzem van e mentés ha igen truet adja vissza
     * frissítem a Uimanagert és hozzáadom az egeret
     * */

    @Override
    public void update() {
        isLoadable();
        uiManagermenu.update();
        handler.getMouseManager().setUIManager(uiManagermenu);
    }

    @Override
    /**
     * beállítom a hátteret + az uimanagert rendelem
     * */
    public void render(Graphics g) {
        g.drawImage(Assets.bg,0,0,1600,800,null);
        uiManagermenu.render(g);

    }
    /**
     * Ebben a metódusban található a játék mentett állapóta betöltési funkció
     * save classba gettelem  a x,y értéket aztán átadom a playernek és a életét is
     * */
    public void load(){
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(("Save.txt")));
            Save save = ((Save) in.readObject());
            handler.getEntityList().getPlayer().setX(save.getSaveX());
            handler.getEntityList().getPlayer().setY(save.getSaveY());
            handler.getEntityList().getPlayer();
			Player.PlayerHealth = (save.getHealth());
            in.close();

        }catch (Exception a){
                System.out.println(a);
        }
    }
   /**
    * Ez a metódus azért kell hogy megnézzük van e fájl amit betudunk tölteni ha nincs nem lesz load gomb és falseal tér vissza.
    * */
    public boolean isLoadable(){
        File f = new File("Save.txt");
        return f.exists();

    }
    /**
     * Ebbe a metódusban található a gombok ha nincs mentett fájl akkor a load helyén az exit lesz
     * 1. gomb: start: ha a menustateben vagyunk akkor setteljük a player x,y koordinátáját és életét 3 állítjuk létre hozzok egy új gameStatet és abba váltunk át
     * 2: gomb: ha isload false akkor ez a gomb nem jelenik meg és
     * a helyette a Exit gomb jelenik meg de ha true akkor itt létre hozzunk egy új gamestatet hogy minden entititás éljen és meghívom a load funkciót
     * 3. gomb: ez a kilpés gomb kaattintásal kilépünk a játékból
     * */

    public void refreshButtons(){

        uiManagermenu.addObject(new UIObject(700, 150, 200, 100, Assets.start, new ClickListener() {
            @Override
            public void onClick() {
                if(State.getState() == handler.getGame().menuState) {
                    handler.getEntityList().getPlayer().setX(100);
                    handler.getEntityList().getPlayer().setY(100);
                    handler.getEntityList().getPlayer();
					Player.PlayerHealth = (3);
                    gameState = new GameState(handler);
                    State.setState(gameState);
                }
            }
        }) {
        });

        int y = 450;
        if(isLoadable()){
            uiManagermenu.addObject(new UIObject(700,300, 200, 100, Assets.load, new ClickListener() {
                @Override
                public void onClick(){
                    if(State.getState() == handler.getGame().menuState) {
                        gameState = new GameState(handler);
                        State.setState(gameState);
                        load();

                    }
                }
            }) {
            });
        }else{
            y = 300;
        }

        uiManagermenu.addObject(new UIObject(700,y, 200, 100, Assets.exit, new ClickListener() {
            @Override
            public void onClick() {
                if(State.getState() == handler.getGame().menuState){
                    System.exit(0);
                }

            }
        }) {
        });
    }

}