
import java.util.Random;
public class movingThing {
    String name;
    String type; // droppeditem, enemy, livestock, etc.
    int damage;
    int reload; // number which changes to exert reload time
    int reloadSet; // the number representing the length of reload time
    double moveSpeed;
    double x, y, xv, yv;
    int simplex, simpley, w, h;
    double size;
    double health, maxhealth;
    boolean isBlock;
    item items;

    // behavior
    boolean movestowardsplayer;
    boolean jumps;
    boolean breaksblocks;
    boolean throwsprojectiles;

    // for squishing when clicked
    boolean squish = false;

    public String convertToString() {
        return name + " " + x + " " + y + " " + xv + " " + yv + " " + w + " " + h + " " + health + " " + maxhealth + " " + type + " " + size + "\n";
    }
    /*
    public void initFromString(String str) {

    }*/
    public void pushDamage(double d) {
        health -= d;
    }

    public void init() {
        items = new item();
        name = "default";
        damage = 1;
        reload = 2;
        reloadSet = 100;
        moveSpeed = 0.5;
        health = 100;
        maxhealth = 100;
        size = 1;
        w = 1; h = 2;
        xv = 0; yv = 0; x = 20; y = 20;
        squish = false;
    }

    public item[] getDrops(String name) {
        item[] drops = new item[] {items.gimmeItem("shovel")};
        switch(name) {
            case("dwarf"):
            drops[0] = items.gimmeItem("gnomehat");
            break;
            case("dwarf1"):
            drops[0] = items.gimmeItem("gnomehat");
            break;
            case("dwarf2"):
            drops[0] = items.gimmeItem("gnomehat");
            break;
            case("dwarf3"):
            drops[0] = items.gimmeItem("gnomehat");
            break;
            case("noselegs"):
            drops = new item[] {items.gimmeItem("foot"),items.gimmeItem("foot")};
            break;
            case("noselegs1"):
            drops = new item[] {items.gimmeItem("foot"),items.gimmeItem("foot")};
            break;
            case("noselegs2"):
            drops = new item[] {items.gimmeItem("foot"),items.gimmeItem("foot")};
            break;
            case("live_muffin"):
            drops = new item[] {items.gimmeItem("muffin"),items.gimmeItem("muffin")};
            break;
            case("beetle"):
            drops = new item[] {items.gimmeItem("beetle_exoskeleton"),items.gimmeItem("beetle_eye"),items.gimmeItem("beetle_eye")};
            break;
            case("desert_fox"):
            drops = new item[] {items.gimmeItem("desert_pelt")};
            break;
        }
        return drops;
    }
    public movingThing createDroppedItem(item input, double xp, double yp) {
        movingThing response = new movingThing();
        response.init();
        response.name = input.name;
        response.type = "droppeditem";
        response.h = 1; response.w = 1;
        response.size = 1;
        response.x = xp; response.y = yp;
        response.isBlock = input.isBlock;
        //System.out.println(response.isBlock + " " + input.isBlock);
        return response;
    }
    public movingThing createTossedBlock(String block, double xp, double yp, double xv, double yv) {
        movingThing tossed = new movingThing();
        tossed.init();
        tossed.name = block;
        tossed.type = "tossed_block";
        tossed.h = 1;
        tossed.size = 1;
        tossed.x = xp; tossed.y = yp; tossed.xv = xv; tossed.yv = yv;
        return tossed;
    }
    public movingThing gimmeMovingThing(int x, int y, String in, Random r) {
        movingThing thing = new movingThing();
        thing.init();
        thing.x = x; thing.y = y;
        thing.simplex = (int)thing.x; thing.simpley = (int)thing.y;
        thing.name = in;
        thing.movestowardsplayer = true;
        thing.jumps = true;
        thing.breaksblocks = false;
        thing.throwsprojectiles = false;
        switch(in) {
            case("gnome"):
            thing.size = 3;
            int chooseDwarf = r.nextInt(0,4);
            thing.name = "dwarf"+chooseDwarf;
            thing.name = thing.name.replace("0","");
            thing.health = 10;
            thing.maxhealth = 10;
            thing.type = "enemy";
            break;
            case("live_muffin"):
            thing.size = 3;
            thing.h = 1;
            thing.health = 20;
            thing.maxhealth = 20;
            thing.type = "livestock";
            thing.movestowardsplayer = false;
            break;
            case("desert_fox"):
            thing.size = 3;
            thing.h = 1;
            thing.health = 17;
            thing.maxhealth = 17;
            thing.type = "enemy";
            thing.damage = 3;
            thing.reloadSet = 3;
            break;
            case("beetle"):
            thing.size = 6;
            thing.h = 1;
            thing.health = 40;
            thing.maxhealth = 40;
            thing.type = "enemy";
            thing.damage = 10;
            thing.reloadSet = 25;
            break;
            case("mole"):
            thing.size = 10;
            thing.h = 1;
            thing.health = 100;
            thing.maxhealth = 100;
            thing.type = "enemy";
            thing.damage = 1;
            thing.reloadSet = 2;
            thing.breaksblocks = true;
            break;
            case("rabbit"):
            thing.size = 2;
            thing.h = 1;
            thing.health = 2;
            thing.maxhealth = 2;
            thing.type = "livestock";
            thing.movestowardsplayer = false;
            break;
            case("particle"): // a particle's name represents its hex color.
            thing.size = 1;
            thing.h = 1;
            thing.xv = (r.nextDouble()-0.5)/3;
            thing.yv = (r.nextDouble()-0.5)/3;
            thing.type = "particle";
            break;
            case("clown1"):
            thing.size = 4;
            thing.health = 50;
            thing.maxhealth = 50;
            thing.type = "enemy";
            break;
            case("clownball1"):
            thing.size = 1;
            thing.h = 1;
            thing.type = "projectile";
            break;
            case("clownball2"):
            thing.size = 1;
            thing.h = 1;
            thing.type = "projectile";
            thing.damage = 3;
            break;
        }
        return thing;
    }
}
