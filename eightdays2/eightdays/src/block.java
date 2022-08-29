import java.io.Serializable;

public class block implements Serializable{
    String type;
    //String hexcolor;
    String biome;
    boolean doneSimulating;
    boolean breakablebyplayer;
    int hardness;
    boolean passThrough; // can player / moving things pass through it?
    boolean sandbehavior;
    boolean waterbehavior;
    boolean nobehavior;
    boolean gravitybehavior;

    public block copy() {
        block c = new block();
        c.biome = biome;
        c.type = type;
        c.doneSimulating = doneSimulating;
        c.breakablebyplayer = breakablebyplayer;
        c.hardness = hardness;
        c.passThrough = passThrough;
        c.sandbehavior = sandbehavior;
        c.waterbehavior = waterbehavior;
        c.nobehavior = nobehavior;
        c.gravitybehavior = gravitybehavior;
        return c;
    }

    public boolean equals(block other) {
        return (type.equals(other.type));// && hexcolor.equals(other.hexcolor));
    }
    public void setUpBlock() {
        breakablebyplayer = true;
        hardness = 1;
        passThrough = false;
        sandbehavior = false;
        waterbehavior = false;
        nobehavior= true;
        gravitybehavior= false;
        switch(type) {
            case("air"):
            passThrough = true;
            break;
            case("sand"):
            sandbehavior = true;
            gravitybehavior = true;
            break;
            case("water"):
            passThrough = true;
            waterbehavior = true;
            gravitybehavior = true;
            break;
            case("leaves"):
            passThrough = true;
            break;
            case("pink_leaves"):
            passThrough = true;
            break;
            case("log"):
            passThrough = true;
            break;
        }
    }
}
