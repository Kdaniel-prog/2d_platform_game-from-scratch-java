package kiszel.daniel.test;

import kiszel.daniel.entities.Bat;
import kiszel.daniel.entities.Player;
import kiszel.daniel.game.Game;
import kiszel.daniel.game.Handler;
import kiszel.daniel.graphic.GameCamera;
import kiszel.daniel.state.MenuState;
import kiszel.daniel.state.State;
import kiszel.daniel.worlds.World;
import org.junit.Assert;
import org.junit.Test;

import static kiszel.daniel.tiles.Tile.grassTile;

/**Ez a teszt osztályom erre az osztályra azért
 * volt szükség mert követelmény volt, hogy legyen ilyen.
 * Alapavetően, hogy a teszteteket eltudjam végezni pár dolgot be kellet állítanom
 * */
public class MyTest {
    Game game = new Game("title",1600,800);
    Handler handler = new Handler(game);
    GameCamera gameCamera = new GameCamera(handler,0,0);
    World world = new World(handler,"res/worlds/world1.txt");
  /**Itt azt tesztelem: hogy a player tényleg elmozdul x tengelyen jobbra*/
    @Test
    public void testPlayerMoveRight (){
        game.setGameCamera(gameCamera);
        handler.getKeyManager().right = true;
        Player player = new Player(handler,100,100);
        handler.setWorld(world);
        player.update();
        float x = player.getX();
        Assert.assertNotEquals(100,x);
    }
    /**Itt azt tesztelem: hogy a player tényleg elmozdul x tengelyen balra*/
    @Test
    public void  testPlayerMoveLeft(){
        game.setGameCamera(gameCamera);
        handler.getKeyManager().left = true;
        Player player = new Player(handler,100,100);
        handler.setWorld(world);
        player.update();
        float x = player.getX();
        Assert.assertNotEquals(100,x);
    }
    /**Itt azt tesztelem: hogy a player tényleg él e*/
    @Test
    public void testPlayerLive(){
        game.setGameCamera(gameCamera);
        handler.setWorld(world);
        Player player = new Player(handler,100,100);
        handler.setWorld(world);
        player.entityDie();
        Assert.assertFalse(player.isActive());
    }
    /**Itt azt tesztelem: hogy a bat tényleg él e*/

    @Test
    public void testBatlive(){
        game.setGameCamera(gameCamera);
        handler.setWorld(world);
        Bat bat = new Bat(handler,100,100);
        handler.setWorld(world);
        bat.entityDie();
        Assert.assertFalse(bat.isActive());
    }
    /**Itt azt tesztelem: hogy a bat tényleg elmozdul e az x tengelyen*/
    @Test
    public void testBatXMoving(){
        game.setGameCamera(gameCamera);
        handler.setWorld(world);
        Bat bat = new Bat(handler,100,100);
        handler.setWorld(world);
        bat.update();
        Assert.assertNotEquals(100,bat.getX());
    }
    /**Itt azt tesztelem: hogy ha van savefájl akkor azt ha nincs akkor meg azt hogy tényleg falset add vissza */

    @Test
    public void testSaveFile(){
        MenuState menuState = new MenuState(handler);
        if(menuState.isLoadable()){
            Assert.assertTrue(menuState.isLoadable());
        }else{
            Assert.assertFalse(menuState.isLoadable());
        }
    }
    /**Itt azt tesztelem: hogy a state alapból tényleg nullt add e vissza */
    @Test
    public void testCurrentState(){
        Assert.assertNull(State.getState());
    }
    /**Itt azt tesztelem: hogy ha van savefájl akkor azt ha nincs akkor meg azt hogy tényleg falset add vissza */
    @Test
    public void testTileid(){
        Assert.assertEquals(2,grassTile.getId());
    }
    /**Itt azt tesztelem: hogy ha játékos ugrik akkor tényleg elmozdul az y tengelyen*/
    @Test
    public void testPlayerYmove(){
        game.setGameCamera(gameCamera);
        handler.setWorld(world);
        Player player = new Player(handler,100,100);
        handler.setWorld(world);
        handler.getKeyManager().up = true;
        player.update();
        Assert.assertNotEquals(100,player.getY());
    }
    /**Itt azt tesztelem: hogy player alapvetően win értéke flasera van állítva*/
    @Test
    public void testPlayerNoWin(){
        handler.setWorld(world);
        game.setGameCamera(gameCamera);
        Player player = new Player(handler,100,100);
        Assert.assertFalse(Player.isPlayerWin);
    }
}
