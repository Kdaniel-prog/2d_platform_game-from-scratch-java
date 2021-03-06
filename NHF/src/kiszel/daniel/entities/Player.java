package kiszel.daniel.entities;

import kiszel.daniel.game.Handler;
import kiszel.daniel.graphic.Animation;
import kiszel.daniel.graphic.Assets;
import kiszel.daniel.state.State;
import kiszel.daniel.tiles.Tile;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * A player osztály a karakterünk ez az osztály azért fontos hogy legyen egy karakterünk amivel tudunk játszani
 *  */
public class Player extends Creature implements Serializable {
    //Animation
    /**
     * ezek az animációk tartoznak a playerhez
     * */
    private Animation die_effect,animLidle, animRidle, animRmove, animLmove;

    public boolean isFacingRight = true;
    public boolean isFacingLeft = false;
    public boolean isJump = false;
    public boolean endJump = false;
    public static int PlayerHealth = 3;
    public boolean isFall = false;
    public boolean isAttack = false;
    public boolean isJumpAttack = false;
    private float lastyMove;
    public static boolean isPlayerWin = false;
    private int Attacktimer = 0;
    private int dieEffectTimer = 0;
    private double otherX,otherY,PlayerX,PlayerY, distance ;
    private long lastCoilsiontimer, coilsionCooldown = 1300, coilsiontimer = coilsionCooldown;

    /**
     *
     * @param handler megkapja a handlert a player
     * @param x egy játékosnak van x koordinátája
     * @param y egy jázékosnak van y koordinátája
     * létrehozzuk az animációkat
     */
    public Player(Handler handler, float x, float y) {
        super(handler, x, y, 90, 90);
        bounds.x = 20;
        bounds.y = 32;
        bounds.width = 32;
        bounds.height = 42;

        animRmove = new Animation(500, Assets.player_Rmove);
        animLmove = new Animation(500, Assets.player_Lmove);
        animRidle = new Animation(600, Assets.player_Ridle);
        animLidle = new Animation(600, Assets.player_Lidle);
        die_effect = new Animation(400,Assets.die_effect);
    }
    /**
     * A játékoshoz tartozó metódusokat és animációkat itt updatelem frissítem
     * itt állítombe hogy camera ezt az entitást fokuszolja
     * */
    @Override
	public void update() {
        animRmove.update();
        animLmove.update();
        animRidle.update();
        animLidle.update();
        die_effect.update();
        getInput();
        move();
        Die();
        Win();
        isGameOver();
        isWin();
        handler.getGameCamera().centerOnEntity(this);
        checkAttacks();
    }
    /**
     * A játékost a getInput segítségével mozgatjuk yMove és xMove a Creatureben található és arra szolgál, hogy csak akkor mozduljon el a karakter ha nem ütközik bele a falba
     * szóval ez segít Coilision detekcióban.
     * */
    private void getInput() {
        xMove = 0;
        yMove = 0;
        /**Ha a felhasználó megnyomja a W gombot és a karakterünk a platformon van és nem támad akkor megkeződdik
         * az ugrás és a isJump true és isplatform false lesz mivel most nem leszünk a platformon */
        if (handler.getKeyManager().up && isPlatform && !isAttack) {
            isPlatform = false;
            isJump = true;
        }
        /** ha az ugrás igaz akkor yMove y koordináta tengyelen elmozgatjuk felfelé*/
        if (isJump && !endJump) {
            yMove = -speed * 3;
            lastyMove += 1;

        }
        /** 15 egységet ugrunk fel**/
        if (lastyMove >= 15) {
            lastyMove = 0;
            endJump = true;

        }
        /** vége az ugrásnak és a gravitáció aktiválodik**/
        if (lastyMove == 0 && endJump) {
            yMove = +speed * 3;
        }
        /** ha leérkeztünk akkor vissza állítom az értékeket falsra**/
        if (lastyMove == 0 && endJump && isPlatform) {
            endJump = false;
            isJump = false;
            isJumpAttack = false;
        }
        /**Ha megnyomjuk a A gombot akkor a karakterünk elmozdul a x tengelyen balra
         * ha nem ütközik bele semmibe és a balra nézést igazra állítom míg a jobbra nézést falsra */
        if (handler.getKeyManager().left) {
            isFacingLeft = true;
            isFacingRight = false;
            xMove = -speed - 1;
        }
        /**Ha megnyomjuk a D gombot akkor a karakterünk elmozdul a x tengelyen balra
         * ha nem ütközik bele semmibe és a balra nézést falsra állítom míg a jobbra nézést igazra */
        if (handler.getKeyManager().right) {
            isFacingRight = true;
            isFacingLeft = false;
            xMove = speed + 1;
        }
        /**Egy idő után a levegőben az isJump falsra vált, hogy elindulhasson a zuhanás */
        if (!isJump && !endJump) {
            yMove = +speed * 3;
            isFall = true;
        }
        /**ha platformon vagyunk akkor a isfall falsra kell állítani
         * zuhanás közben nem tudunk mást csinálni csak a x tengelyen mozgatni a karakterünk
         * */
        if (!isJump && !endJump && isPlatform) {
            isFall = false;
        }
        /**ez azért kell hogy a karakter ne tudjun 0 nano seckenként támadni így még jobban hasonlít
         * egy játékhoz ezért is van a attacktimer = 0 időzítve itt amit később növelek
         *  */
        if (!isFall && handler.getKeyManager().attack && !isAttack && !isJump) {
            isAttack = true;
            Attacktimer = 0;
        }
        if (handler.getKeyManager().attack && isJump) {
            isJumpAttack = true;
        }
        /**Ha a karakter támad akkor nem tud x és y írányba mozogni
         * */
        if (isAttack) {
            xMove = 0;
            yMove = 0;
            Attacktimer++;

        }
        /**Ha a attacktimer 20 akkor az adott támadás animáció véget érhet és indithat egy újat */
        if (isAttack && Attacktimer == 10) {
            Attacktimer = 0;
            isAttack = false;
        }
        /**ha paltformra érkezünk akkor isjump és isjumpattackot falsra állítom, hogy azt következőleg is tudjuk használni. */
        if (isPlatform && isJump && isJumpAttack) {
            isJumpAttack = false;
            isJump = false;
        }

    }
    /**itt rajzolom ki az aktuális player animációt és a getCurrentANimationFramebe meghatározom, hogy melyik kell. A játékos hp-ja is itt van kirajzolva attól függően mennyi életereje van */
    @Override
	public void render(Graphics g) {
        g.drawImage(getCurrentAnimationFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
        //
        g.drawImage(Assets.hearthframe, 0, 0, 230, 80, null);
        if (PlayerHealth >= 1) {
            g.drawImage(Assets.hp, 20, 20, 40, 40, null);
        }
        if (PlayerHealth >= 2) {
            g.drawImage(Assets.hp, 90, 20, 40, 40, null);
        }
        if (PlayerHealth >= 3) {
            g.drawImage(Assets.hp, 160, 20, 40, 40, null);
        }
    }
    /**
     * itt a megfelelő animációt vagy kép darabot küldöm rendernek hogy rajzolja ki.
     * egyébb esetben a jobbra nézős idle animációt küldöm vissza
     * */
    private BufferedImage getCurrentAnimationFrame() {
        if(PlayerHealth == 0){
            return die_effect.getCurrentFrame();
        }
        // idle
        if (PlayerHealth != 0 && !isJump && !isFall && xMove < 0) {
            return animLmove.getCurrentFrame();
        }
        if (PlayerHealth != 0 &&!isJump && !isFall && xMove > 0) {
            return animRmove.getCurrentFrame();
        }
        //attack
        if (PlayerHealth != 0 &&!isJump && isAttack && isFacingRight) {

            return Assets.Rattack3;
        }
        if (PlayerHealth != 0 &&!isJump && isAttack && isFacingLeft) {

            return Assets.Lattack3;
        }
        if (PlayerHealth != 0 &&isJumpAttack && isJump && isFacingRight) {
            return Assets.RairAttack;
        }
        if (PlayerHealth != 0 &&isJumpAttack && isJump && isFacingLeft) {
            return Assets.LairAttack;
        }
        //Gugolás
        if (PlayerHealth != 0 &&!isJump && handler.getKeyManager().down && isFacingRight && isPlatform) {
            return Assets.Rsquat;
        }
        if (PlayerHealth != 0 &&!isJump && handler.getKeyManager().down && isFacingLeft && isPlatform) {
            return Assets.Lsquat;
        }
        //jump
        if (PlayerHealth != 0 &&!isJumpAttack && isJump && isFacingLeft) {
            return Assets.Ljump;
        }
        if (PlayerHealth != 0 &&!isJumpAttack && isJump && isFacingRight) {
            return Assets.Rjump;
        }
        //FAll
        if (PlayerHealth != 0 && isFacingRight && isFall) {
            return Assets.Rfall;
        }
        if (PlayerHealth != 0 && isFacingLeft && isFall) {
            return Assets.Lfall;
        }
        //idle
        if (PlayerHealth != 0 && !isJump && !isFall && isFacingLeft) {
            return animLidle.getCurrentFrame();
        } else {
            return animRidle.getCurrentFrame();
        }
    }

    /**Itt nézzem meg hogy a játékos elég közel van e egy entitáshoz hogy meg sebbeze a játékost vagy ha játékos támad elég közel van e egyhez.
     * A megvalósitáshoz pitágorasz tételt használtam (distance)
     * Ez a metódus nem kölcség hatákény mert minden updatebe megnézzem minden egyes entitáshoz a játékost
     * */
    private void checkAttacks() {
        coilsiontimer += System.currentTimeMillis() - lastCoilsiontimer;
        lastCoilsiontimer = System.currentTimeMillis();
        PlayerX = this.getX() + this.getWidth()/2;
        PlayerY = this.getY() + this.getHeight()/2;
        for (Entity other : handler.getWorld().getEntityList().getEntities()) {
            otherX = other.getX() + other.getWidth()/2;
            otherY = other.getY() + other.getHeight()/2 ;
            distance =  Math.sqrt(Math.pow((PlayerY - otherY ),2) + Math.pow((otherX-PlayerX),2));

            /**ha other entitás a játékos akkor továbbb megyek saját magát nem akar megsebbezni */
            if (other.equals(this)) {
                continue;
            }
            /**ha distance nagyobb 100 akkor tovább megyek mert úgyse történe semmi és legalább így költségét a metódusnak kicsit csökkentem */
            if(distance > 100){
                continue;
            }
            /**itt a játékost nézem meg, hogy a distancen belül van e ha igen akkor megöli a másik entitást */
            if(50 >= distance && isAttack && isFacingRight && PlayerHealth > 0){
                other.entityDie();
                return;
            }
            if(100 >= distance && isAttack && isFacingLeft && PlayerHealth > 0){
                other.entityDie();
                return;
            }
            if(50 >= distance&& isFacingRight && isJumpAttack && PlayerHealth > 0){
                other.entityDie();
                return;
            }
            if(100 >= distance&& isFacingLeft && isJumpAttack && PlayerHealth > 0){
                other.entityDie();
                return;
            }
            /**itt nézzem ha a játékos nem támadott a distancen belül akkor sebződjön ha Playerhealth 0 akkor player entitás meghal */
            if (PlayerHealth > 0 && isFacingRight && isJump && coilsiontimer > coilsionCooldown && 7 >= distance && !isAttack && other.isActive()){
                PlayerHealth -= 1;
                coilsiontimer = 0;
                return;
            }
            if (PlayerHealth > 0 && isFacingLeft && isJump && coilsiontimer > coilsionCooldown && 28 >= distance && !isAttack && other.isActive()){
                PlayerHealth -= 1;
                coilsiontimer = 0;
                return;
            }
            if(PlayerHealth > 0 &&  !isJump && isFacingRight && coilsiontimer > coilsionCooldown && PlayerY < otherY && 16.5 >= distance && !isAttack && other.isActive()){
                PlayerHealth -= 1;
                coilsiontimer = 0;
                return;
            }
            if(PlayerHealth > 0 &&  !isJump && isFacingRight && coilsiontimer > coilsionCooldown && PlayerY > otherY && 17.5 >= distance && !isAttack && other.isActive()){
                PlayerHealth -= 1;
                coilsiontimer = 0;
                return;
            }
            if(PlayerHealth > 0 &&  !isJump && isFacingLeft && coilsiontimer > coilsionCooldown && 87 >= distance  && !isAttack && other.isActive()){
                PlayerHealth -= 1;
                coilsiontimer = 0;
                return;
            }
            if( PlayerHealth <= 0){
                coilsiontimer = 0;
                this.active = false;
            }
        }
    }

    /** Itt nézem, hogy a játékos nyert e és ha igen akkor statet váltok és a isPlayerWin booleant falsra állítom*/
    public void isWin(){
        if (isPlayerWin){
            State.setState(handler.getGame().winState);
            isPlayerWin = false;
        }
    }
    /**mivel akkor nyerünk ha egy Win tilal érintkezünk ezért ez a boolean metódus igazat ad ha találkozunk egyel
     * mivel alapvetően minden Tilenak van saját isWin metódusa és csak wintilnál van felül írva */
    protected boolean collisionWithWinTile(int x, int y){
        return handler.getWorld().getTile(x,y).isWin();
    }
    /**winX és winY metódus egybe gyurása található itt */
    public void Win(){
        WinX();
        WinY();
    }
    /**Winx és a winy metódusba külön megnézzem, hogy a játékos érintkezett e win tile-al */
    public void WinX(){
        if(xMove > 0){
            int tx = (int) (x + xMove + bounds.x + bounds.width) / Tile.TILEHEIGHT;
            if(collisionWithWinTile(tx,(int)(y + bounds.y) / Tile.TILEHEIGHT) && collisionWithWinTile(tx, (int) (y + bounds.y +bounds.height) /Tile.TILEHEIGHT)) {
                isPlayerWin = true;
            }
        }else if (xMove < 0){
            int tx = (int) (x + xMove + bounds.x) / Tile.TILEHEIGHT;
            if(collisionWithWinTile(tx,(int)(y + bounds.y) / Tile.TILEHEIGHT) && collisionWithWinTile(tx, (int) (y + bounds.y +bounds.height) /Tile.TILEHEIGHT)){

                isPlayerWin = true;

            }
        }
    }
    public void WinY(){ //fel
        if (yMove < 0){
            int ty = (int) (y + yMove + bounds.y) / Tile.TILEHEIGHT;
            if(collisionWithWinTile((int) (x + bounds.x) /Tile.TILEWIDTH, ty) &&
                    collisionWithWinTile((int) (x + bounds.x + bounds.width) /Tile.TILEWIDTH, ty)){
                Player.isPlayerWin = true;

            }
        }else if(yMove > 0){ //le
            int ty = (int) (y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;
            if(collisionWithWinTile((int) (x + bounds.x) /Tile.TILEWIDTH, ty)&&
                    collisionWithWinTile((int) (x + bounds.x + bounds.width) /Tile.TILEWIDTH, ty)){
                isPlayerWin = true;

            }
        }
    }


    /**
     *  Itt nézem, hogy a játékos meghalt e
     *  hagyjok egy kis időt 30 milliseckent hogy látszodjon a player meghal és a statet a losestatre állítom
     * */
    public void isGameOver(){
        if(!this.isActive()){
            dieEffectTimer++;
        }
        if(!this.isActive() && PlayerHealth > 0){
            dieEffectTimer = 30;
        }
        if (!this.isActive() && !isPlayerWin && dieEffectTimer == 30){
            dieEffectTimer = 0;
            State.setState(handler.getGame().loseState);
            PlayerHealth = 3;
            this.active = true;

        }
    }
    /**Ez a boolean metódus visszaad egy igazat ha szakadékba zuhanunk */
    protected boolean collisionWithDieTile(int x, int y){
        return handler.getWorld().getTile(x,y).isKill();
    }

    public void Die(){
        DieX();
        DieY();
    }
    /**Megnézem külön, hogy a diex és diey-nál a játékos téglalapja érintkezik e die blockal */
    public void DieX(){
        if(xMove > 0){
            int tx = (int) (x + xMove + bounds.x + bounds.width) / Tile.TILEHEIGHT;
            if(collisionWithDieTile(tx,(int)(y + bounds.y) / Tile.TILEHEIGHT) && collisionWithDieTile(tx, (int) (y + bounds.y +bounds.height) /Tile.TILEHEIGHT)) {
                this.active = false;
            }
        }else if (xMove < 0){
            int tx = (int) (x + xMove + bounds.x) / Tile.TILEHEIGHT;
            if(collisionWithDieTile(tx,(int)(y + bounds.y) / Tile.TILEHEIGHT) && collisionWithDieTile(tx, (int) (y + bounds.y +bounds.height) /Tile.TILEHEIGHT)){
                this.active = false;

            }
        }
    }
    public void DieY(){ //fel
        if (yMove < 0){
            int ty = (int) (y + yMove + bounds.y) / Tile.TILEHEIGHT;
            if(collisionWithDieTile((int) (x + bounds.x) /Tile.TILEWIDTH, ty) &&
                    collisionWithDieTile((int) (x + bounds.x + bounds.width) /Tile.TILEWIDTH, ty)){
                this.active = false;

            }
        }else if(yMove > 0){ //le
            int ty = (int) (y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;
            if(collisionWithDieTile((int) (x + bounds.x) /Tile.TILEWIDTH, ty)&&
                    collisionWithDieTile((int) (x + bounds.x + bounds.width) /Tile.TILEWIDTH, ty)){
                this.active = false;
            }

        }
    }
}

