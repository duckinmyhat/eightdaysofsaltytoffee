import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.util.*;
import java.awt.image.ImageObserver;
import java.awt.Font;
import java.awt.Point;
import  java.awt.geom.AffineTransform;

public class framePainter implements ImageObserver{
    

    world activeWorld;
    int renderDistance;
    player p1;
    BufferedImage textures;
    BufferedImage bgimage;
    BufferedImage currentGameFrame;
    BufferedImage minimapFrame;
    Graphics2D mmfg;
    Graphics2D gfg;
    int minimapscale;
    int minimapSideLength;
    int minimapBlockStep;

    @Override
    public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        // TODO Auto-generated method stub
        return false;
    }

    public void loadTextures() {
        File textureFile = new File("./eightdays/images/allTextures.png");
        File bg = new File("./eightdays/images/bg.png");
        try{
            textures = ImageIO.read(textureFile);
            bgimage = ImageIO.read(bg);
        } catch(Exception e) {System.out.println(e.getMessage());}
    }
    public boolean onScreen(int x, int y, double camerax, double cameray, double camerazoom) {
        //return Math.abs(camerax-x)<(WIDTH/2)*camerazoom && Math.abs(cameray-y)<(HEIGHT/2)*camerazoom;
        if(distance(x,y,camerax,cameray) < 1200/camerazoom) {return true;} else {return false;}
    }
    public double distance(double x, double y, double xx, double yy) {
        return Math.sqrt(Math.pow(x-xx,2)+Math.pow(y-yy,2));
    }

    public void initPainter() {
        currentGameFrame = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_BGR); // TYPE_BYTE_BINARY means black and white. ,, TYPE_BYTE_INDEXED means retro
        minimapFrame = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
        mmfg = minimapFrame.createGraphics();
        gfg = currentGameFrame.createGraphics(); // game frame graphics = gfg
        gfg.setFont(new Font("purisa",Font.PLAIN,20));
        minimapscale = 5;
        minimapSideLength = 200;
        minimapBlockStep = 200/(minimapSideLength/2);
    }

    public BufferedImage getCurrentGameFrame(int WIDTH, int HEIGHT, Point mouseP, double camerax, double cameray, double camerazoom, item[] inventory, int selectedItem, List<movingThing> movingThings, List<stationaryThing> stationaryThings) {
        currentGameFrame = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_BGR); // TYPE_BYTE_BINARY means black and white. ,, TYPE_BYTE_INDEXED means retro
        gfg = currentGameFrame.createGraphics();
        //minimapFrame = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
        //mmfg = minimapFrame.createGraphics();
        //gfg.setColor(Color.decode("#DDEFEF"));
        //gfg.fillRect(0,0,WIDTH,HEIGHT);//gfg.drawImage(bgimage,0,0,WIDTH,HEIGHT,this); // BACKGROUND
        /// draw bg according to surrounding blocks
        int halfWidth = (WIDTH/2); int halfHeight = (HEIGHT/2);
        int bgw = 20;
        int bgh = (int)(bgw*((float)HEIGHT/(float)WIDTH));
        for(int bgx = p1.simplex-(bgw/2); bgx < p1.simplex+(bgw/2); bgx++) {
            for(int bgy = p1.simpley-(bgh/2); bgy < p1.simpley+(bgh/2); bgy++) {
                //System.out.println(bgx + " , " + bgy);
                // draw the color from this block relative to player in that section of the screen relative to the middle of the screen.
                block bgb = activeWorld.getBlock(bgx,bgy);
                if(bgb.type.equals("air")) {gfg.setColor(Color.decode("#A5AED2"));} else {gfg.setColor(desaturate(Color.decode(getColorForBlock(bgb.type)).brighter()));}
                gfg.fillRect(halfWidth+(((bgx-p1.simplex)*(WIDTH/bgw))),halfHeight+(((bgy-p1.simpley)*(HEIGHT/bgh))),WIDTH/bgw,HEIGHT/bgh);
                //System.out.println((WIDTH/2)+(((bgx-p1.simplex)*(WIDTH/20))));
            }
        }

        
        int leftrenderedge = (p1.simplex-renderDistance); if(leftrenderedge < 0) {leftrenderedge=0;}
        int rightrenderedge = (p1.simplex+renderDistance); if(rightrenderedge >= activeWorld.width) {rightrenderedge=activeWorld.width-1;}
        int upperrenderedge = (p1.simpley-renderDistance); if(upperrenderedge<0) {upperrenderedge = 0;}
        int lowerrenderedge = (p1.simpley+renderDistance); if(lowerrenderedge >= activeWorld.height) {lowerrenderedge = activeWorld.height-1;}
        // don't scan entire world, just scan square around the character.
        for(int x = leftrenderedge; x < rightrenderedge; x++) {
            for(int y = upperrenderedge; y < lowerrenderedge; y++) {
                // draw block according to position of camera, position of block, and camera zoom.
                
                if(onScreen(x,y,camerax,cameray,camerazoom) && !activeWorld.GAMEFIELD.isAir(x, y)) {
                    //Color bc;
                    //if(activeWorld.GAMEFIELD.isAir(x,y-1) || activeWorld.GAMEFIELD.isAir(x,y)) {
                    //if(x%2==y%2) {
                    //    gfg.setColor(Color.decode(getColorForBlock(activeWorld.GAMEFIELD.blockArray[x][y].type)).darker());
                    //} else {
                        gfg.setColor(darkenABit(Color.decode(getColorForBlock(activeWorld.GAMEFIELD.blockArray[x][y].type))));
                    //}
                    //if(shaded(x,y,activeWorld.GAMEFIELD.blockArray)) {gfg.setColor(Color.decode(getColorForBlock(activeWorld.GAMEFIELD.blockArray[x][y].type)).darker());}
                    gfg.fillRect(halfWidth-(int)((camerax-x)*camerazoom),halfHeight-(int)((cameray-y)*camerazoom),1+(int)(camerazoom),1+(int)(camerazoom));
                }
            }
        }
        //mmfg.fillRect(0,0,minimapSideLength*2,minimapSideLength*2);
        /*for(int x = -minimapSideLength; x < minimapSideLength; x+= 1) {
            for(int y = -minimapSideLength; y < minimapSideLength; y+= 1) {
                if(p1.simplex+x > activeWorld.width || p1.simplex+x < 0 || p1.simpley+y < 0 || p1.simpley+y > activeWorld.height || activeWorld.GAMEFIELD.isAir(p1.simplex+x,p1.simpley+y)) {
                    mmfg.setColor(new Color(0,0,0,0));
                } else {
                    mmfg.setColor(Color.decode(getColorForBlock(activeWorld.GAMEFIELD.blockArray[p1.simplex+x][p1.simpley+y].type)));
                }
                mmfg.fillRect(100+x,100+y,1,1);
            }
        }*/



        // draw moving things
        int mta = movingThings.size();
        for(int i = 0; i < mta; i++) {
            //System.out.println(movingThings.get(i).name);
            movingThing t = movingThings.get(i);
            if(onScreen(t.simplex,t.simpley,camerax,cameray,camerazoom)) {
                int[] tScreenCoords = new int[] {halfWidth-(int)((camerax-t.x)*camerazoom),halfHeight-(int)((cameray-t.y)*camerazoom)};
                //gfg.setColor(Color.GREEN);
                //gfg.drawString(Math.pow(t.size,2)+" " + t.name,(WIDTH/2)-(int)((camerax-t.x)*camerazoom),(HEIGHT/2)-(int)((cameray-t.y)*camerazoom));
                if(!t.type.equals("droppeditem") && !t.type.equals("particle")) {
                    double squishc = 1; if(t.squish) {squishc = 0.6;} // change size of thing if it is being squished.
                    gfg.drawImage(getImage(t.name),tScreenCoords[0],tScreenCoords[1],1+(int)(t.w*t.size*camerazoom),(int)(1+(int)(t.h*t.size*camerazoom)*squishc),this);
                    // draw healthbar
                    gfg.setColor(Color.BLACK);
                    gfg.fillRect(tScreenCoords[0],tScreenCoords[1]-20,1+(int)(t.w*t.size*camerazoom),10);
                    gfg.setColor(Color.decode("#FF4343"));
                    gfg.fillRect(tScreenCoords[0],tScreenCoords[1]-20,(int)((t.health/t.maxhealth)*(1+(int)(t.w*t.size*camerazoom))),10);
                } else if(t.type.equals("particle")) {
                    gfg.setColor(Color.decode(t.name));
                    gfg.fillRect(tScreenCoords[0],tScreenCoords[1],1+(int)(t.w*t.size*camerazoom),1+(int)(t.h*t.size*camerazoom));
                } else {
                    if(t.isBlock) {
                        Color bcolor = Color.decode(getColorForBlock(t.name)).darker().darker();
                        gfg.setColor(new Color(bcolor.getRed(),bcolor.getBlue(),bcolor.getGreen(),211));
                        //System.out.println("block item: " + t.w + " " + t.h + " " + t.size);
                        gfg.fillRect(tScreenCoords[0],tScreenCoords[1],1+(int)(t.w*t.size*camerazoom),1+(int)(t.h*t.size*camerazoom));
                    } else {
                        gfg.drawImage(getImage(t.name),tScreenCoords[0],tScreenCoords[1],1+(int)(t.w*t.size*camerazoom),1+(int)(t.h*t.size*camerazoom),this);
                    }
                }
            }

        }



        // inventory drawing
        gfg.drawImage(getImage("inventory"),halfWidth-360,HEIGHT-(10+(15*5)),90*5,16*5,this);
        gfg.drawImage(getImage("inventory"),halfWidth+90,HEIGHT-(10+(15*5)),90*5,16*5,this);
        gfg.drawImage(getImage("selectedItemFrame"),halfWidth-360+(selectedItem*15*5),HEIGHT-(10+(15*5)),16*5,16*5,this);
        gfg.setColor(Color.BLACK);
        if(p1.inventory[p1.selectedItem]!=null) {gfg.drawString(p1.inventory[p1.selectedItem].name,halfWidth-360+(selectedItem*15*5),HEIGHT-(10+(15*5))-42);}
        // player healthbar
        gfg.setColor(Color.BLACK);
        gfg.fillRect(halfWidth-360,HEIGHT-(10+(15*5))-32,180*5,30);
        gfg.setColor(Color.RED);
        gfg.fillRect(halfWidth-360,HEIGHT-(10+(15*5))-32,(int)(180*5*p1.health/p1.maxhealth),30);
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i]!=null) { // drawing items
                //if(inventory[i].type==null) {System.out.println("i: " + i + " name " + inventory[i].name);}
                if(inventory[i].type.equals("block")) {
                    gfg.setColor(Color.decode(getColorForBlock(inventory[i].name)).darker());
                    gfg.fillRect(halfWidth-357+(i*15*5),HEIGHT-(9+(15*5)),13*5,13*5);
                } else {
                    gfg.drawImage(getImage(inventory[i].name),halfWidth-357+(i*15*5),HEIGHT-(9+(15*5)),13*5,13*5,this);
                }
                // draw number for the quantity of the item
                gfg.setColor(Color.RED);
                
                gfg.setFont(new Font("purisa",Font.PLAIN,30));
                gfg.drawString(inventory[i].quantity+"",halfWidth-357+(i*15*5),30+HEIGHT-(9+(15*5)));
            }   else {
                
            }
        }
        // draw craftable items
        int canCraftSize = p1.canCraft.size();
        for(int i = 0; i < canCraftSize; i++) {
            int indexAdjustedByCraftSelect = i + p1.craftSelect;
            if(indexAdjustedByCraftSelect >= canCraftSize) {
                indexAdjustedByCraftSelect-=canCraftSize;
            }
            if(p1.canCraft.get(indexAdjustedByCraftSelect).type!="block") {gfg.drawImage(getImage(p1.canCraft.get(indexAdjustedByCraftSelect).name),20,20+(i*30),30,30,this);} else {
                gfg.setColor(Color.decode(getColorForBlock(p1.canCraft.get(indexAdjustedByCraftSelect).name)));
                gfg.fillRect(20,20+(i*30),30,30);
            }
        }
        // draw stationary things
        int sta = stationaryThings.size();
        for(int i = 0; i < sta; i++) {
            stationaryThing s = stationaryThings.get(i);
            if(onScreen(s.x,s.y,camerax,cameray,camerazoom)) {
                gfg.drawImage(getImage(s.name),halfWidth-(int)((camerax-s.x)*camerazoom),halfHeight-(int)((cameray-s.y)*camerazoom),1+(int)(s.w*s.size*camerazoom),1+(int)(s.h*s.size*camerazoom),this);
            }
        }
        // draw player
        gfg.drawImage(getImage("player"),(halfWidth-(int)((camerax-p1.x)*camerazoom)),halfHeight-(int)((cameray-p1.y)*camerazoom),(int)(p1.width*p1.size*camerazoom),(int)(p1.height*p1.size*camerazoom),this);

        // draw chest menu if needed
        int chestadj = -130; // chest adjust
        if(p1.inChest) {
            gfg.drawImage(getImage("inventory"),halfWidth+chestadj,halfHeight-20,90*5,16*5,this);
            gfg.drawImage(getImage("selectedItemFrame"),halfWidth+chestadj+(p1.chestSelectedItem*15*5),halfHeight-20,16*5,16*5,this);
            for(int i = 0; i < p1.chestInventory.length; i++) {
                if(p1.chestInventory[i]!=null) { // drawing items
                    //if(inventory[i].type==null) {System.out.println("i: " + i + " name " + inventory[i].name);}
                    if(p1.chestInventory[i].type.equals("block")) {
                        gfg.setColor(Color.decode(getColorForBlock(p1.chestInventory[i].name)).darker());
                        gfg.fillRect(halfWidth+chestadj+(i*15*5),halfHeight-20,13*5,13*5);
                    } else {
                        gfg.drawImage(getImage(p1.chestInventory[i].name),halfWidth+chestadj+(i*15*5),halfHeight-20,13*5,13*5,this);
                    }
                    // draw number for the quantity of the item
                    gfg.setColor(Color.RED);
                    
                    gfg.setFont(new Font("purisa",Font.PLAIN,30));
                    gfg.drawString(p1.chestInventory[i].quantity+"",halfWidth+chestadj+(i*15*5),halfHeight-20);
                }   else {
                    
                }
            }
            gfg.setColor(Color.BLACK);
            gfg.drawString("chest || 'e' to exit, 'l shift' to switch mode, 'w' to move items, scroll to select",halfWidth+(2*chestadj),halfHeight-61);
            if(p1.depositing) {
                gfg.drawString("mode: deposit",halfWidth+chestadj,halfHeight-41);
            } else {
                gfg.drawString("mode: withdraw",halfWidth+chestadj,halfHeight-41);
            }
        }

        //draw cursor
        if(p1.inventory[p1.selectedItem]!=null) {gfg.drawImage(getImage(p1.inventory[p1.selectedItem].name),mouseP.x,mouseP.y,this);}
        
        // draw item use animation
        if(p1.inventory[p1.selectedItem]!=null) {
            AffineTransform rota = gfg.getTransform();
            gfg.rotate(Math.atan2((mouseP.y-halfHeight-(int)(((cameray-p1.y)+((p1.height*p1.size/2)))*camerazoom)),(mouseP.x-((halfWidth+(int)(((camerax-p1.x)+((p1.width*p1.size/2)))*camerazoom)))))+Math.toRadians(90),(halfWidth+(int)(((camerax-p1.x)+((p1.width*p1.size/2)))*camerazoom)),halfHeight+(int)(((cameray-p1.y)+((p1.height*p1.size/2)))*camerazoom));
            
            gfg.drawImage(getImage(p1.inventory[p1.selectedItem].name),halfWidth,halfHeight-33-(int)(p1.reloadMarker*10*camerazoom),(int)(4*camerazoom),18+(int)(p1.reloadMarker*10*camerazoom),this);
            gfg.rotate(-Math.toRadians(45));

            gfg.setTransform(rota);
        }
        // draw minimap
        //gfg.drawImage(minimapFrame,WIDTH-(minimapSideLength*2),0,minimapSideLength*2,minimapSideLength*2,this);
        //gfg.drawImage(minimapFrame,0,0,minimapSideLength*2,minimapSideLength*2,this);
        return currentGameFrame;
    }

    public BufferedImage getImage(String request) {
        BufferedImage response;
        int x=90,y=73, w=12,h=12; // coordinates to cut out of texture image
        switch(request) {
            case("player"):
            x = 60; y = 0; w = 30; h = 60;
            break;
            case("inventory"):
            x = 106; y = 0; w = 90; h = 15;
            break;
            case("shovel"):
            x = 90; y = 0; w = 15; h = 16;
            break;
            case("foot"):
            x = 90; y = 16; w = 15; h = 15;
            break;
            case("sleek_spade"):
            x = 90; y = 32; w = 15; h = 15;
            break;
            case("burrowing_plume"):
            x = 90; y = 48; w = 15; h = 15;
            break;
            case("beetle_exoskeleton"):
            x = 90; y = 0; w = 15; h = 16;
            break;
            case("beetle_eye"):
            x = 90; y = 0; w = 15; h = 16;
            break;
            case("desert_pelt"):
            x = 90; y = 0; w = 15; h = 16;
            break;
            case("clownball1"):
            x = 105; y = 48; w = 15; h = 15;
            break;
            case("clownball2"):
            x = 105; y = 63; w = 15; h = 15;
            break;
            case("selectedItemFrame"):
            x = 106; y = 16; w = 15; h = 15;
            break;
            case("dwarf"):
            x = 60; y = 60; w = 30; h = 60;
            break;
            case("dwarf1"):
            x = 60; y = 120; w = 30; h = 60;
            break;
            case("dwarf2"):
            x = 60; y = 180; w = 30; h = 60;
            break;
            case("dwarf3"):
            x = 60; y = 240; w = 30; h = 60;
            break;
            case("gnomehat"):
            x = 60; y = 60; w = 30; h = 21;
            break;
            case("noselegitem"):
            x = 0; y = 120; w = 60; h = 120;
            break;
            case("noselegs"):
            x = 0; y = 120; w = 60; h = 120;
            break;
            case("noselegs1"):
            x = 0; y = 239; w = 60; h = 120;
            break;
            case("noselegs2"):
            x = 0; y = 360; w = 60; h = 120;
            break;
            case("muffin"):
            x = 106; y = 32; w = 15; h = 15;
            break;
            case("live_muffin"):
            x = 61; y = 300; w = 30; h = 30;
            break;
            case("fence"):
            x = 61; y = 330; w = 30; h = 30;
            break;
            case("chest"):
            x = 91; y = 330; w = 30; h = 30;
            break;
            case("beetle"):
            x = 61; y = 360; w = 30; h = 30;
            break;
            case("desert_fox"):
            x = 61; y = 390; w = 30; h = 30;
            break;
            case("mole"):
            x = 61; y = 420; w = 30; h = 30;
            break;
            case("rabbit"):
            x = 61; y = 450; w = 30; h = 30;
            break;
            case("clown1"):
            x = 61; y = 480; w = 30; h = 60;
            break;
            case("clown2"):
            x = 61; y = 540; w = 30; h = 60;
            break;
        }
        response = textures.getSubimage(x,y,w,h);
        return response;
    }
    /*
     * inventory: 106,0 - 196,15
     * shvoel: 90,0 - 105, 15
     * selected Item frame: 106,16 - w = 15; h = 15;
     */

    public static String getColorForBlock(String in) {
        String response = "#FF00A1";
        switch(in) {
            case("air"):
                response = "#F6F8F9";
            break;
            case("dirt"):
                response =  "#523D20";
            break;
            case("stone"):
                response = "#696666";
            break;
            case("crag_stone"):
                response = "#545454";
            break;
            case("grass"):
                response = "#42CF00";
            break;
            case("pink_grass"):
                response = "#E34B93";
            break;
            case("sand"):
                response = "#D7CA95";
            break;
            case("water"):
                response = "#3b809a";
            break;
            case("pink_stone"):
                response = "#9F909A";
            break;
            case("flat_stone"):
                response = "#8B8F82";
            break;
            case("leaves"):
                response = "#007E0C";
            break;
            case("log"):
                response = "#6c5f41";
            break;
            case("pink_leaves"):
                response = "#DB4A6F";
            break;
            case("grapite"):
                response = "#554882";
            break;
            case("subterranean_chunk"):
                response = "#C99D62";
            break;
            case("firmamental_residue"):
                response = "#B1C9C8";
            break;
        }
        return response;
    }
    public boolean shaded(int x, int y, block[][] blockfield) {
        int cx = x; int cy = y;
        boolean shaded = false;
        if(blockfield[x][y].type.equals("air")) {return false;}
        /*
        while(!shaded && !(distance(x,y,cx,cy)>10)) {
            cx--; cy--;
            if(cx>0&&cy>0&&cx<blockfield.length&&cy<blockfield[0].length) {
                if(!blockfield[cx][cy].type.equals("air") && !blockfield[cx][cy].type.equals("water")) {
                    shaded = true; break;
                }
            } else {break;}
        }*/
        shaded = (!blockfield[x][y-1].type.equals("air")&&!blockfield[x][y-1].type.equals("water"))&&(!blockfield[x][y-2].type.equals("air")&&!blockfield[x][y-2].type.equals("water"));
        return shaded;
    }
    public Color darkenABit(Color in) {
        Color response = in;
        int darkRed = response.getRed()-10;
        int darkBlue = response.getBlue()-10;
        int darkGreen = response.getGreen()-10;
        if(darkRed<0) {darkRed = 0;}
        if(darkBlue<0) {darkBlue = 0;}
        if(darkGreen<0) {darkGreen = 0;}
        if(darkRed>255) {darkRed = 255;}
        if(darkBlue>255) {darkBlue = 255;}
        if(darkGreen>255) {darkGreen = 255;}
        response = new Color(darkRed,darkGreen,darkBlue);
        return response;
    }
    public Color desaturate(Color in) {
        float[] todesat = new float[3];
        Color.RGBtoHSB(in.getRed(), in.getGreen(), in.getBlue(), todesat);
        //todesat[1] -= 0.5;
        //if(todesat[1]<0) {todesat[1]=(float)0.2;}
        todesat[1] = Float.parseFloat(""+0.1);
        return new Color(Color.HSBtoRGB(todesat[0], todesat[1], todesat[2]));
    }
}
