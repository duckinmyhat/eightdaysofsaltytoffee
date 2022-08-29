import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.Serializable;

public class world implements Serializable {
    boolean fromFile;
    String name;
    gameField GAMEFIELD; // the blocks that make up the world
    int width; int height;

    public void init() {
        GAMEFIELD = new gameField();
        GAMEFIELD.init();
        GAMEFIELD.W = width;
        GAMEFIELD.H = height;
    }
    public void blocksSetup() {
        for(int x = 0; x < GAMEFIELD.W; x++) {
            for(int y = 0; y < GAMEFIELD.H; y++) {
                GAMEFIELD.blockArray[x][y].setUpBlock();
            }
        }
    }

    public void generateTerrain(int w, int h) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        GAMEFIELD.generateWorld(w,h);
        width = GAMEFIELD.W; height = GAMEFIELD.H;
/*
        File file;
        file = new File("./eightdays/images/blastoise.jpg");
        blastoisepicture = new BufferedImage(475,475,BufferedImage.TYPE_INT_ARGB);
        try{
            FileInputStream fis = new FileInputStream(file);
            blastoisepicture = ImageIO.read(file);
        } catch(Exception e) {System.out.println(e.getMessage());}
        int bpw = blastoisepicture.getWidth();
        int bph = blastoisepicture.getHeight();
        for(int x = 0; x < bpw; x+=4) {
            for(int y = 0; y < bph; y+=4) {
                int c = blastoisepicture.getRGB(x,y);
                GAMEFIELD.blockArray[(x/4)+200][(y/4)+200].type="testground";
                //int red= (RGB>>16)&255;
                //int green= (RGB>>8)&255;
                //int blue= (RGB)&255;
                GAMEFIELD.blockArray[(x/4)+200][(y/4)+200].hexcolor= String.format("#%02x%02x%02x", (c>>16)&255, (c>>8)&255, (c)&255);
            }
        }

        file = new File("./eightdays/images/brock.png");
        try{
            FileInputStream fis = new FileInputStream(file);
            brockpicture = ImageIO.read(file);
        } catch(Exception e) {System.out.println(e.getMessage());}
        bpw = brockpicture.getWidth();
        bph = brockpicture.getHeight();
        for(int x = 0; x < bpw; x++) {
            for(int y = 0; y < bph; y++) {
                int c = brockpicture.getRGB(x,y);
                GAMEFIELD.blockArray[(x)+700][(y)+200].type="testground";
                //int red= (RGB>>16)&255;
                //int green= (RGB>>8)&255;
                //int blue= (RGB)&255;
                GAMEFIELD.blockArray[(x)+700][(y)+200].hexcolor= String.format("#%02x%02x%02x", (c>>16)&255, (c>>8)&255, (c)&255);
            }
        }*/
    }

    public int[] roundedCoordinates(double x, double y) { //  rounds coordinates to the nearest space in the block array.
        return new int[] {round(x),round(y)};
    }
    public int round(double in) {
        int low = (int) Math.floor(in); int high = (int) Math.ceil(in);
        if(Math.abs(in-low) <= Math.abs(in-high)) {return low;} else {return high;}
    }

    public int[] blockCoordsFromScreenPosition(double x, double y, double camx, double camy, double camzoom, int width, int height) {
        int blockx = (int)(-1 * ((((width/2)-x)/camzoom)-camx));
        int blocky = (int)(-1 * ((((height/2)-y)/camzoom)-camy));
        //System.out.println(blockx + " " + blocky);
        return new int[] {blockx,blocky};
        //gfg.fillRect((WIDTH/2)-(int)((camerax-x)*camerazoom),(HEIGHT/2)-(int)((cameray-y)*camerazoom),1+(int)(camerazoom),1+(int)(camerazoom));
    }
    public double[] exactCoordsFromScreenPosition(double x, double y, double camx, double camy, double camzoom, int width, int height) {
        double exactx = (-1 * ((((width/2)-x)/camzoom)-camx));
        double exacty = (-1 * ((((height/2)-y)/camzoom)-camy));
        //System.out.println(blockx + " " + blocky);
        return new double[] {exactx,exacty};
        //gfg.fillRect((WIDTH/2)-(int)((camerax-x)*camerazoom),(HEIGHT/2)-(int)((cameray-y)*camerazoom),1+(int)(camerazoom),1+(int)(camerazoom));
    }

    public void setBlock(int x, int y, String type) {
        if(x>=0 && x<width && y>=0 && y<height) {
            GAMEFIELD.blockArray[x][y].type=type;
            GAMEFIELD.blockArray[x][y].setUpBlock();
            //GAMEFIELD.blockArray[x][y].hexcolor=hexcolor;
        }
    }
    public block getBlock(int x, int y) {
        if(x>=0 && x<width && y>=0 && y<height) {
            return GAMEFIELD.blockArray[x][y];
        } else {block nullBlock = new block(); nullBlock.type="noblockhere"; return nullBlock;}
    }
    public void blocksPhysicsStep(int l, int r, int t, int b) {GAMEFIELD.blockPhysicsStep(l,r,t,b);}
}
