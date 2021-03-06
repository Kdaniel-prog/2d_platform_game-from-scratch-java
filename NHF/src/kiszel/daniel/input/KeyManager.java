package kiszel.daniel.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 * Ez az osztály azért fontos mert, hogy a billentyűzet egyes gombjait felismerje
 * és true értéket küldjön ha lenyomtuk ha elengedtük akkor falset
 * */
public class KeyManager implements KeyListener {

    private boolean[] keys;
    public boolean ctrl,up, down, left, right, attack;

    /**
     * 88 db tömb a minimum amit elfogad a keys manager
     */
    public KeyManager(){
        keys = new boolean[88];
    }
    /**
     * frissítem a billentyűzet parancsokat
     */
    public void update(){
        up = keys[KeyEvent.VK_W];
        down = keys[KeyEvent.VK_S];
        left = keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_D];
        attack = keys[KeyEvent.VK_J];
        ctrl = keys[KeyEvent.VK_CONTROL];
    }
    /**
     * Ha a billentyűzetet lenyomták akkor truet visszaadja
     * */
    @Override
	public void keyPressed(KeyEvent e){
        try{
            keys[e.getKeyCode()] = true;
        }catch(Exception a){
            System.out.println(a);
        }

    }
    /**
     * Ha a billentyűzetet lenyomták akkor falset visszaadja
     * */

    @Override
	public void keyReleased (KeyEvent e){
        try{
            keys[e.getKeyCode()] = false;
        }catch(Exception a){
            System.out.println(a);
        }

    }
    /**
     * muszáj keyTypednek itt lennie vagy errort dobna vissza mivel a keylistener abstract osztályába bele tartozi kez a abstract metódus
     * */
    @Override
	public void keyTyped(KeyEvent e){

    }


}
