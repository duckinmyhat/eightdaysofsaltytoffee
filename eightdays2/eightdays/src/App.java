import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.util.Random;
import java.util.*;
import java.awt.MouseInfo;
import java.awt.Font;

public class App extends JPanel{

    // Application variables
    private static App a;
    private Keys keys;
    private JFrame frame;
    private int WIDTH, HEIGHT;
    private String location; // menu, game, etc
    private int menubuttoncooldown;
    private world[] availableWorlds;
    private world activeWorld;
    private player p1;
    private double camerax, cameray;
    private double camerazoom;
    private int renderDistance = 300;
    private boolean mouseDownLeft;
    private boolean mouseDownRight;
    private JSONer fileWitch;
    private int selectMenuScrolling;
    private int gamestepcooldown;
    private int gameLongCoolDown;
    private framePainter painter;
    private item items;
    private List<movingThing> movingThings = new ArrayList<movingThing>();
    private List<stationaryThing> stationaryThings = new ArrayList<stationaryThing>();
    private int howManyMovingThings;
    private int howManyStationaryThings;
    private ObjectSerializer fileBarbarian;


    public JFrame createWindow() {
        JFrame newFrame = new JFrame("eight days of salty toffee");
        newFrame.setSize(800,800);
        newFrame.setVisible(true);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return newFrame;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        BufferedImage gameFrame = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfg = gameFrame.createGraphics(); // game frame graphics = gfg
        gfg.setColor(Color.decode("#303A5F"));
        gfg.fillRect(0,0,WIDTH,HEIGHT);
        gfg.setColor(Color.decode("#D3005C"));
        // DRAW BASED ON LOCATION
        gfg.setFont(new Font("purisa",Font.PLAIN,33));
        switch(location) {
            case("main_menu"): 
                gfg.drawString("MAIN MENU", (WIDTH/2)-100, 100);
                gfg.drawString("'1' to enter SETTINGS", (WIDTH/2)-200, 160);
                gfg.drawString("'2' to enter WORLD SELECT", (WIDTH/2)-240, 220);
                gfg.drawString("'esc' to exit", (WIDTH/2)-100, 280);
            break;
            case("settings"):
                gfg.drawString("there are no settings,", (WIDTH/2)-200, 300);
                gfg.drawString("'1' for main menu", (WIDTH/2)-200, 360);
            break;
            case("world_select"):
                gfg.drawString("WORLD SELECT", (WIDTH/2)-100, 110);
                gfg.drawString("'1' for main menu", (WIDTH/2)-200, 170);
                gfg.drawString("'2' to create world", (WIDTH/2)-200,230);
                gfg.drawString("'3' to select world", (WIDTH/2)-200,290);
                gfg.drawString("'up' and 'down' keys to scroll", (WIDTH/2)-320,350);
                // draw list of available worlds
                for(int i = 0; i < availableWorlds.length; i++) {
                    if(i+selectMenuScrolling<availableWorlds.length) gfg.drawString(availableWorlds[i+selectMenuScrolling].name + " (number: " + (i+1) + " :)))", (WIDTH/2)-300, 350+((i+1)*60));
                }
            break;
            case("in_game"):
                // draw ingame frame
                Point mp = MouseInfo.getPointerInfo().getLocation(); mp.x -= frame.getX(); mp.y -= frame.getY()+40;
                BufferedImage ingameframe = painter.getCurrentGameFrame(WIDTH, HEIGHT, mp, camerax, cameray, camerazoom, p1.inventory, p1.selectedItem,movingThings, stationaryThings);
                gfg.drawImage(ingameframe,0,0,Color.BLACK,this);
                gfg.setColor(new Color(255,220,160,20));
                gfg.fillRect(0,0,WIDTH,HEIGHT);
                gfg.setColor(Color.BLACK);
                gfg.drawString("x: " + p1.x + " y: " + p1.y + " xv: " + p1.xv + " yv: " + p1.yv,300,30);
                gfg.drawString("moving things: " + howManyMovingThings, 300,70);
            break;
            case("selecting_world"):
                gfg.drawString("SELECTING WORLD", (WIDTH/2)-300, 110);
                gfg.drawString("(different from WORLD SELECT)", (WIDTH/2)-320, 170);
                gfg.drawString("'esc' to go back", (WIDTH/2)-200,230);
                gfg.drawString("press a number to join that world", (WIDTH/2)-300,290);
                // draw list of available worlds
                for(int i = 0; i < availableWorlds.length; i++) {
                    if(i+selectMenuScrolling<availableWorlds.length) gfg.drawString(availableWorlds[i+selectMenuScrolling].name + " (number: " + (i+1) + " :)))", (WIDTH/2)-300, 290+((i+1)*60));
                }
            break;
        }

        //once frame is created, paint the image to the actual frame/panel
        g.drawImage(gameFrame,0,0,Color.BLACK,this);
    }



    public static void main(String[] args) throws Exception {
        System.out.println("boopy doopy");
        a = new App();
        a.frame = a.createWindow();
        a.frame.add(a);

        a.frame.setResizable(true);
        a.WIDTH = a.getWidth(); a.HEIGHT = a.getHeight();

        a.mouseDownLeft = false;
        a.mouseDownRight = false;
        a.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) a.mouseDownLeft = true;
                if(SwingUtilities.isRightMouseButton(e)) a.mouseDownRight = true;
                a.handleClick(e.getX(),e.getY());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) a.mouseDownLeft = false;
                if(SwingUtilities.isRightMouseButton(e)) a.mouseDownRight = false;
            }
         });

         a.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(a.p1.inChest && !a.p1.depositing) {
                    a.p1.chestSelectedItem += e.getWheelRotation();
                    if(a.p1.chestSelectedItem>a.p1.currentChest.inventory.length-1) {a.p1.chestSelectedItem=0;} else if(a.p1.chestSelectedItem<0) {a.p1.chestSelectedItem = a.p1.currentChest.inventory.length-1;}
                } else {
                    a.p1.selectedItem += e.getWheelRotation();
                    if(a.p1.selectedItem>a.p1.inventory.length-1) {a.p1.selectedItem=0;} else if(a.p1.selectedItem<0) {a.p1.selectedItem = a.p1.inventory.length-1;}
                }
            }
        });
         a.fileWitch = new JSONer();
         a.fileBarbarian = new ObjectSerializer();
         a.painter = new framePainter();
         a.painter.loadTextures();
         a.items = new item();

        a.keys = new Keys();
        a.frame.addKeyListener(a.keys);
        a.menubuttoncooldown = 0;

        // gather available worlds from folder !!!!!!!!!
        a.getAvailableWorlds();

        // enter game
        a.location = "main_menu";

        a.gamestepcooldown = 0;
        a.gameLongCoolDown = 0;
        while(true) {
            // GAME LOOP
            a.WIDTH = a.getWidth(); a.HEIGHT = a.getHeight();
            Thread.sleep(10);
            if(0 == a.menubuttoncooldown) {a.handleKeys();}
            if(a.menubuttoncooldown > 0) {a.menubuttoncooldown -= 1;}
            if(a.location == "in_game") {
                a.gameStep(a.gamestepcooldown, a.gameLongCoolDown);
            } 
            if(a.gamestepcooldown > 0) {a.gamestepcooldown--;}
            a.repaint();
        }
    }

    public void handleKeys() {
        int latestkey = a.keys.key();

        if(location == "main_menu") {
            switch(latestkey) { // 27 is escape
                case(49): // 1
                location = "settings";
                menubuttoncooldown = 33;
                break;
                case(50): // 2
                location = "world_select";
                menubuttoncooldown = 33;
                break;
                case(27): // escape
                System.exit(0);
                break;
                default: menubuttoncooldown = 0;
                break;
            }
        }
        else if(location == "settings") {
            switch(latestkey) {
                case(49): // 1
                location = "main_menu";
                menubuttoncooldown = 33;
                break;
                default: menubuttoncooldown = 0;
                break;
            }
        }
        else if(location == "world_select") {
            switch(latestkey) {
                case(49): // 1
                location = "main_menu";
                menubuttoncooldown = 33;
                break;
                case(50): // '2' for 'create world'
                world newWorld = createNewWorld();
                a.availableWorlds[a.availableWorlds.length-1] = newWorld;
                menubuttoncooldown = 33;
                break;
                case(51): // '3' for 'select world'
                location = "selecting_world";
                menubuttoncooldown = 33;
                break;
                case(38): // up
                if(selectMenuScrolling < availableWorlds.length-9) {
                    selectMenuScrolling++;
                }
                menubuttoncooldown = 33;
                break;
                case(40):
                if(selectMenuScrolling>0) {
                    selectMenuScrolling--;
                }
                menubuttoncooldown = 33;
                break;
                default: menubuttoncooldown = 0;
                break;
            }
        }
        else if(location == "selecting_world") {
            switch(latestkey) {
                case(27): // esc
                location = "world_select";
                getAvailableWorlds();
                menubuttoncooldown = 33;
                break;
                case(38): // up
                if(selectMenuScrolling < availableWorlds.length-9) {
                    selectMenuScrolling++;
                }
                menubuttoncooldown = 33;
                break;
                case(40):
                if(selectMenuScrolling>0) {
                    selectMenuScrolling--;
                }
                menubuttoncooldown = 33;
                break;
                default: menubuttoncooldown = 0;
                break;
            }
            if(49 <= latestkey && latestkey <= 57) {
                int keyNum = latestkey-48;
                if(availableWorlds.length+selectMenuScrolling >= keyNum) {
                    location = "in_game";
                    activeWorld = availableWorlds[keyNum-1+selectMenuScrolling];
                    joinWorld();
                }
            }
        }
        else if(location == "in_game") {
            handleKeysWhileInGame(latestkey);
        }
    }
    
    public world createNewWorld() {
        // add a new space at the end of availableWorlds, so a new one can be created in the same game session.
        world[] newAvailableWorlds = new world[availableWorlds.length+1];
        for(int i = 0; i < availableWorlds.length; i++) {
            newAvailableWorlds[i] = availableWorlds[i];
        }
        availableWorlds = new world[newAvailableWorlds.length];
        for(int i = 0; i < newAvailableWorlds.length-1; i++) {
            availableWorlds[i] = newAvailableWorlds[i];
        }

        world newWorld = new world();
        newWorld.init();
        newWorld.generateTerrain(2000,2000);
        newWorld.name = JOptionPane.showInputDialog("enter world name");
        //newWorld = fileWitch.worldFromFile("withoutcheckerboard");
        return newWorld;
    }
    public void getAvailableWorlds() {
        File worldsFolder = new File("./eightdays/gameData/worlds");
        File[] allWorlds = worldsFolder.listFiles();
        System.out.println(allWorlds.length);
        availableWorlds = new world[allWorlds.length];
        for(int x = 0; x < allWorlds.length; x++) {
            String cleanedName = allWorlds[x].getName().replace(".txt","");
            //a.availableWorlds[x] = a.fileWitch.worldFromFile(cleanedName);
            availableWorlds[x] = new world();
            availableWorlds[x].fromFile = true;
            availableWorlds[x].name = cleanedName;
            System.out.println(allWorlds[x].getName());
        }
    }

    public void handleKeysWhileInGame(int input) {
        // input is the key that was pressed
        switch(input) {
            case(27): // escape
            location = "main_menu";
            for(int i = 0; i < howManyMovingThings; i++) {if(movingThings.get(i).isBlock) {turnBlocksSolid(movingThings.get(i), i);}} // upon leaving world, make dropped block items place themselves.
            fileWitch.generateFileFromWorld(activeWorld,movingThings,stationaryThings,p1);
            menubuttoncooldown = 33;
            break;
            default:
            break;
        }
    }

    public void print(String p) {System.out.println(p);}

    public void joinWorld() {
        print("joining");
        p1 = new player();
        print("player created");
        if(activeWorld.fromFile) {
            activeWorld = fileWitch.worldFromFile(activeWorld.name);
            movingThings = new ArrayList<movingThing>();
            movingThings = fileWitch.movingThingsFromFile(activeWorld.name);
            stationaryThings = new ArrayList<stationaryThing>();
            stationaryThings = fileWitch.stationaryThingsFromFile(activeWorld.name);
            howManyStationaryThings = stationaryThings.size();
            p1.init();
            p1.initFromString(fileWitch.playerStringFromFile(activeWorld.name));
            p1.initInventoryFromString(fileWitch.playerInventoryStringFromFile(activeWorld.name));
            p1.resetVelocity();
            activeWorld.blocksSetup();
        } else {
            p1.init();
            print("player init");
            int[] spawnCoords = activeWorld.GAMEFIELD.coordsOfRequestedFormation(new String[][] {{"air","leaves"}});
            if(spawnCoords==null) {spawnCoords = new int[] {10,10};}
            //int[] spawnCoords = new int[] {activeWorld.width/2, activeWorld.height/2};
            p1.x = spawnCoords[0]; p1.y = spawnCoords[1]-3;
            //while(!playerOnGround() && !(p1.y<2)) {p1.y+=1;}
            //while(playerOnGround()) {p1.y -= 1;}
            activeWorld.blocksSetup();
            
        }
        print("joining 2");
        activeWorld.GAMEFIELD.W = activeWorld.width;
        activeWorld.GAMEFIELD.H = activeWorld.height;
        activeWorld.blocksSetup();
        camerax = p1.x; cameray = p1.y; camerazoom = 20;
        painter.activeWorld = activeWorld;
        painter.p1 = p1;
        painter.renderDistance = renderDistance;
        System.out.println(p1.x + " <-- x || y--> " + p1.y);
        movingThingsStep();
        howManyMovingThings = movingThings.size();
        System.out.println(howManyMovingThings + " moving things loaded from file");
    }

    
    public boolean onScreen(int x, int y) {
        //return Math.abs(camerax-x)<(WIDTH/2)*camerazoom && Math.abs(cameray-y)<(HEIGHT/2)*camerazoom;
        if(distance(x,y,camerax,cameray) < 1200/camerazoom) {return true;} else {return false;}
    }
    public double distance(double x, double y, double xx, double yy) {
        return Math.sqrt(Math.pow(x-xx,2)+Math.pow(y-yy,2));
    }

    public void gameStep(int cooldown, int longcooldown) {
        if(howManyMovingThings!=movingThings.size()) {System.out.println("ruh roh it doesn't match");}
        p1.canCraft = p1.getCraftableItems();
        Point mousePoint = new Point(MouseInfo.getPointerInfo().getLocation());
        if(mouseDownLeft || mouseDownRight) {handleClick(mousePoint.x-frame.getLocation().x,mousePoint.y-frame.getLocation().y-30);}

        int[] ec = activeWorld.roundedCoordinates(p1.x, p1.y); // estimated coordinates
        p1.simplex = ec[0]; p1.simpley = ec[1];
        camerax = p1.x; cameray = p1.y;
        if(keys.equals && camerazoom<42) camerazoom += 0.18;
        if(keys.minus && camerazoom>4.7) camerazoom -= 0.18;
        playerKeys(keys);
        playerPhysicsStep();
        p1.checkIfInventoryFull();
        if(p1.isDead()) {
            int[] spawnCoords = new int[] {activeWorld.width/2, activeWorld.height/2};
            p1.x = spawnCoords[0]; p1.y = spawnCoords[1]-3;
            while(!playerOnGround()) {p1.y+=1;}
            while(playerOnGround()) {p1.y -= 1;}
            p1.health = p1.maxhealth;
        }
        movingThingsStep();

        if(cooldown == 0) {
            if(p1.reloadMarker>0) {p1.reloadMarker--;}
            if(p1.health < p1.maxhealth) {p1.health += 0.03;}

            int prd = renderDistance; // physics render distance
            int leftrenderedge = (p1.simplex-prd); if(leftrenderedge < 0) {leftrenderedge=0;}
            int rightrenderedge = (p1.simplex+prd); if(rightrenderedge >= activeWorld.width) {rightrenderedge=activeWorld.width-1;}
            int upperrenderedge = (p1.simpley-prd); if(upperrenderedge<0) {upperrenderedge = 0;}
            int lowerrenderedge = (p1.simpley+prd); if(lowerrenderedge >= activeWorld.height) {lowerrenderedge = activeWorld.height-1;}
            activeWorld.blocksPhysicsStep(leftrenderedge, rightrenderedge, upperrenderedge, lowerrenderedge);
            gamestepcooldown = 10;
            gameLongCoolDown--;
        }
        if(longcooldown <= 0) {
            if(howManyMovingThings < 150) {spawnCreatureNaturally();}
            gameLongCoolDown = 22;
        }
    }
    public void playerPhysicsStep() { // player physics
        double predictedx = p1.x+p1.xv; double predictedy = p1.y+p1.yv;
        // oochie scootch until potential destination, see if anything along the path is blocked.
        double checkx = p1.x; double checky = p1.y;
        double pathLength = distance(p1.x,p1.y,predictedx,predictedy);
        double travelled = 0;
        double txv = p1.xv; double tyv = p1.yv;
        // get length of velocity vector, divide each velocity component by that.
        double velocityVectorLength = distance(0,0,p1.xv,p1.yv);
        txv = p1.xv/velocityVectorLength; tyv = p1.yv/velocityVectorLength;
        double stepLength = distance(0,0,txv,tyv);
        boolean blockage = false;
        while(travelled <= pathLength) {
            int[] ecp = activeWorld.roundedCoordinates(checkx, checky);
            //if(openAreaForWholeBody(ecp[0],ecp[1])&& openAreaForWholeBody((int)(ecp[0]+ttxv),(int)(ecp[1]+ttyv))) {
            //System.out.println(openAreaForWholeBody(ecp[0],ecp[1],p1.width*p1.size,p1.height*p1.size) + " and " + openAreaForWholeBody(ecp[0]+txv,ecp[1]+tyv,p1.width*p1.size,p1.height*p1.size));
            if(openAreaForWholeBody(ecp[0],ecp[1],p1.width*p1.size,p1.height*p1.size) && openAreaForWholeBody(ecp[0]+txv,ecp[1]+tyv,p1.width*p1.size,p1.height*p1.size)) {
                checkx += txv; checky += tyv;
                travelled += stepLength;
            } else {blockage = true; break;}
        }
        if(!blockage) {
            if(openAreaForWholeBody(predictedx,predictedy,p1.width*p1.size,p1.height*p1.size)) {
                //p1.x = checkx;
                //p1.y = checky;
                p1.x = predictedx;
                p1.y = predictedy;
            }
        } else if(openAreaForWholeBody(p1.x,p1.y+p1.yv,p1.width*p1.size,p1.height*p1.size)) {p1.y+=p1.yv;p1.xv=0;} else if(openAreaForWholeBody(p1.x+p1.xv,p1.y,p1.width*p1.size,p1.height*p1.size)) {p1.x+=p1.xv;p1.fallDamage(p1.yv);p1.yv=0;p1.y = p1.simpley;} else if(openAreaForWholeBody(checkx,checky,p1.width*p1.size,p1.height*p1.size)) {p1.x = checkx; p1.y = checky; /*p1.xv=0; p1.yv=0;*/} else {p1.xv*=-0.6;p1.yv*=-0.6;}
        
        if(!playerOnGround()) {p1.yv += 0.03;} else if(p1.yv !=0) {p1.fallDamage(p1.yv); p1.yv = 0;}
        if(activeWorld.GAMEFIELD.isWater(p1.simplex,p1.simpley+(p1.height*p1.size)) || activeWorld.GAMEFIELD.isWater(p1.simplex+(p1.width*p1.size),p1.simpley+(p1.height*p1.size))) {if(p1.yv > 0.1) {p1.yv =0.1*Math.abs(p1.yv)/p1.yv;} if(Math.abs(p1.xv) > 0.2) {p1.xv = 0.2*Math.abs(p1.xv)/p1.xv;}}
        //System.out.println(" blockage: " + blockage + " w "+ p1.width + " h " + p1.height + " size " + p1.size);
        //if(!playerOnGround()) {p1.yv += 0.015;} else if(p1.yv >0) {p1.yv = 0;}
        if(!openAreaForWholeBody(p1.x,p1.y,p1.width*p1.size,p1.height*p1.size)) {p1.y-=0.1;}
        int[] ec = activeWorld.roundedCoordinates(p1.x, p1.y); // estimated coordinates
        p1.simplex = ec[0]; p1.simpley = ec[1];
        if(p1.yv==0&&p1.xv==0) {p1.x=p1.simplex;p1.y=p1.simpley;}
    }

    public boolean openAreaForWholeBody(double x, double y, double w, double h) {
        boolean open = true;
        int[] ecp = activeWorld.roundedCoordinates(x,y);
        for(int xc = ecp[0]; xc < ecp[0]+w; xc++) {
            for(int yc = ecp[1]; yc < ecp[1]+h; yc++) {
                //System.out.println(activeWorld.getBlock(xc,yc).type);
                if(!openArea(xc,yc)) open = false;
            }
        }
        //System.out.println(open);
        return open;
    }

    
    public boolean playerOnGround() {
        int[] ec = activeWorld.roundedCoordinates(p1.x, p1.y); // estimated coordinates
        //return !activeWorld.GAMEFIELD.getTypeAt(ec[0],ec[1]+p1.height+1).equals("air");
        return !openAreaForWholeBody(ec[0],ec[1]+1,p1.width*p1.size,p1.height*p1.size);
    }
    public boolean movingThingOnGround(movingThing t) {
        int[] ec = activeWorld.roundedCoordinates(t.x, t.y); // estimated coordinates
        //return !activeWorld.GAMEFIELD.getTypeAt(ec[0],ec[1]+p1.height+1).equals("air");
        return !openAreaForWholeBody(ec[0],ec[1]+1,t.w*t.size,t.h*t.size);
    }
    public boolean openArea(double x, double y) {
        int[] ecc = activeWorld.roundedCoordinates(x,y); // estimated coordinates 'c'
        if(ecc[0] >= activeWorld.GAMEFIELD.W-1 || ecc[1] >= activeWorld.GAMEFIELD.H-1 || ecc[0] <= 0 || ecc[1] <= 0) {
            return false;
        } else {
            return activeWorld.GAMEFIELD.getBlock(ecc[0],ecc[1]).passThrough;
            //return (activeWorld.GAMEFIELD.getTypeAt(ecc[0],ecc[1]).equals("air") || activeWorld.GAMEFIELD.isWater(ecc[0],ecc[1]) || activeWorld.GAMEFIELD.getTypeAt(ecc[0],ecc[1]).equals("log") || activeWorld.GAMEFIELD.getTypeAt(ecc[0],ecc[1]).equals("leaves") || activeWorld.GAMEFIELD.getTypeAt(ecc[0],ecc[1]).equals("pink_leaves"));
        }
    }
    public int[] findMovingThingsOnPath(int origx, int origy, double xv, double yv, double range, boolean stopwhenhit) {
        double cx = origx; double cy = origy;
        double vhypot = distance(0,0,xv,yv);
        xv /= vhypot;
        yv /= vhypot;
        while(distance(origx,origy,cx,cy) <= range) {
            cx += xv;
            cy += yv;
            int[] foundthings = movingThingsTouchingRectangle(cx,cy,1,1,true);
            if(stopwhenhit && foundthings.length>0) {return foundthings;}
        }
        return new int[] {};
    }

    public int round(double in) {
        int low = (int) Math.floor(in); int high = (int) Math.ceil(in);
        if(Math.abs(in-low) <= Math.abs(in-high)) {return low;} else {return high;}
    }

    public boolean isColliding(double x, double y, double w, double h, double xx, double yy, double ww, double hh) {
        double center1x = x + (w/2); double center2x = xx + (ww/2);
        double center1y = y + (h/2); double center2y = yy + (hh/2);
        return (Math.abs(center1x-center2x) <= ((w+ww)/2)) && (Math.abs(center1y-center2y) <= ((h+hh)/2));
    }
    public boolean isAboutToCollide(double x, double y, double xv, double yv, double w, double h,    double xx, double yy, double xxv, double yyv, double ww, double hh,     double stepScale) {
        boolean answer = false;
        double pathToCheck1 = (Math.sqrt(Math.pow(xv,2)+Math.pow(yv,2)));
        double pathToCheck2 = (Math.sqrt(Math.pow(xxv,2)+Math.pow(yyv,2)));
        double pathChecked1 = 0;
        double pathChecked2 = 0;
        double hypot1 = (Math.sqrt(Math.pow(xv,2)+Math.pow(yv,2)));
        double hypot2 = (Math.sqrt(Math.pow(xxv,2)+Math.pow(yyv,2)));
        double changev1;
        if(hypot1 == 0) {changev1 = 0;} else {changev1 = stepScale/hypot1;}
        double changev2;
        if(hypot2 == 0) {changev2 = 0;} else {changev2 = stepScale/hypot2;}
        
        xv = xv*changev1;
        yv = yv*changev1;
        xxv = xxv*changev2;
        yyv = yyv*changev2;
        hypot1 = (Math.sqrt(Math.pow(xv,2)+Math.pow(yv,2)));
        hypot2 = (Math.sqrt(Math.pow(xxv,2)+Math.pow(yyv,2)));
        while(pathChecked1 < pathToCheck1 || pathChecked2 < pathToCheck2) {
            x += xv; y += yv; xx += xxv; yy += yyv;
            if(isColliding(x,y,w,h,xx,yy,ww,hh)) {answer = true; break;}
            pathChecked1 += hypot1;
            pathChecked2 += hypot2;
        }
        return answer;
    }
    public void gameClick(double x, double y) { // upon player click, do things according to held object.
        if(p1.reloadMarker==0) {
            int[] coords = activeWorld.blockCoordsFromScreenPosition(x,y,camerax,cameray,camerazoom,WIDTH,HEIGHT);
            double[] ecoords = activeWorld.exactCoordsFromScreenPosition(x,y,camerax,cameray,camerazoom,WIDTH,HEIGHT);
            //p1.x = coords[0]; p1.y = coords[1];
            if(p1.inventory[p1.selectedItem]!=null) {
                //=========================================================================================================
                int stationaryThingClicked = stationaryThingsTouching(coords[0]+1,coords[1],1,1);
                if(p1.inventory[p1.selectedItem].type.equals("shovel")) {
                    //System.out.println(stationaryThingClicked);
                    if(stationaryThingClicked!=-1) {breakStationaryThing(coords[0]+1,coords[1]);}
                } else {
                    if(stationaryThingClicked!=-1 && stationaryThings.get(stationaryThingClicked).name.equals("chest")) {p1.currentChest = stationaryThings.get(stationaryThingClicked); p1.chestInventory = stationaryThings.get(stationaryThingClicked).inventory;p1.inChest = true;}
                }
                    //=========================================================================================================
            }
            if(coords[0]>=0 && coords[0]<=activeWorld.width-1 && coords[1]>=0 && coords[1] <= activeWorld.height-1) {if(mouseDownLeft && p1.inventory[p1.selectedItem]!=null) {
                //int[] movingThingClicked = movingThingsTouchingRectangle(ecoords[0], ecoords[1],1,1,true);
                int[] movingThingClicked = findMovingThingsOnPath(p1.simplex+(int)(p1.width*p1.size/2),p1.simpley+(int)(p1.height*p1.size/2),ecoords[0]-(p1.simplex+(int)(p1.width*p1.size/2)),ecoords[1]-(p1.simpley+(int)(p1.height*p1.size/2)),p1.reach,true);
                if(movingThingClicked.length>0) { // if clicked on a creature...
                    for(int i = 0; i < movingThingClicked.length; i++) {
                        movingThing target = movingThings.get(movingThingClicked[i]);
                        target.pushDamage(p1.inventory[p1.selectedItem].damage);
                        target.squish = true;
                        target.reload = 3;
                        makeABunchOfParticles(target.x,target.y,Math.pow(target.size,2),"#FFFFFF");
                    }
                } else { // if clicked on something other than a creature...
                    //if(movingThingClicked.length==0) {activeWorld.setBlock(coords[0],coords[1],"stone");}
                    if(p1.inventory[p1.selectedItem].type.equals("block") && distance(ecoords[0],ecoords[1],p1.x,p1.y)<p1.reach && activeWorld.GAMEFIELD.isAir(coords[0],coords[1])) {activeWorld.setBlock(coords[0],coords[1],p1.inventory[p1.selectedItem].name);p1.removeOneOfSelectedItem();activeWorld.GAMEFIELD.blockArray[coords[0]][coords[1]].setUpBlock();}
                    else if(p1.inventory[p1.selectedItem].type.equals("shovel") && distance(ecoords[0],ecoords[1],p1.x,p1.y)<p1.reach) {
                        // dig out blocks in a direction
                        if(!keys.leftcontrol && !(coords[0]==p1.x && coords[1]==p1.y)) {
                            boolean targetfound = false;
                            double cx = p1.simplex+(p1.width*p1.size/2); double cy = p1.simpley+(p1.height*p1.size/2);
                            double cxv = coords[0]-(p1.simplex+(p1.width*p1.size/2)); double cyv = coords[1]-(p1.simpley+(p1.height*p1.size/2));
                            double hypotenuse = distance(0,0,cxv,cyv);
                            cxv/=hypotenuse; cyv/=hypotenuse;
                            int[] est = activeWorld.roundedCoordinates(cx,cy);
                            while(!targetfound) {
                                est = activeWorld.roundedCoordinates(cx,cy);
                                if(distance(est[0],est[1],p1.simplex,p1.simpley)>p1.reach) {break;}
                                if(!activeWorld.GAMEFIELD.isAir(est[0],est[1]) && !activeWorld.GAMEFIELD.isWater(est[0],est[1]) && distance(p1.simplex,p1.simpley,est[0],est[1])<=p1.reach) {
                                    targetfound = true; break;
                                } else {
                                    cx += cxv; cy += cyv;
                                    for(int ccx = est[0]-(int)((float)p1.width*(float)p1.size/2); ccx<est[0]+(int)((float)p1.width*(float)p1.size/2);ccx++) {
                                        for(int ccy = est[1]-(int)((float)p1.height*(float)p1.size/2); ccy < est[1]+(int)((float)p1.height*(float)p1.size/2); ccy++) {
                                            if(!activeWorld.GAMEFIELD.isAir(ccx,ccy) && !activeWorld.GAMEFIELD.isWater(ccx,ccy) && distance(p1.simplex,p1.simpley,ccx,ccy)<=p1.reach) {
                                                targetfound = true; cx = ccx; cy = ccy; est = activeWorld.roundedCoordinates(ccx,ccy); break;
                                            }
                                        }
                                        if(targetfound) break;
                                    }
                                }
                                if(targetfound) break;
                            }
                            if(targetfound) {breakBlockForDrop(est[0],est[1]);}
                        } else {
                            breakBlockForDrop(coords[0],coords[1]);
                        }
                    }
                    else if(p1.inventory[p1.selectedItem].type.equals("useful")) {
                        switch(p1.inventory[p1.selectedItem].name) {
                            case("noselegitem"):
                                summon(p1.x,p1.y,p1.inventory[p1.selectedItem].name);
                                p1.removeOneOfSelectedItem();
                            break;
                            case("muffin"):
                                if(p1.health<p1.maxhealth) {
                                    p1.health += 6;
                                    if(p1.health > p1.maxhealth) {p1.health = p1.maxhealth;}
                                    p1.removeOneOfSelectedItem();
                                }
                            break;
                        }
                    }
                    else if(p1.inventory[p1.selectedItem].type.equals("structure")) {
                        switch(p1.inventory[p1.selectedItem].name) {
                            case("fence"):
                                if(stationaryThingsTouching(coords[0]+1,coords[1]+1,0,0)==-1 && openAreaForWholeBody(coords[0],coords[1],2,2)) {createNewStationaryThing("fence",coords[0],coords[1]);p1.removeOneOfSelectedItem();}
                            break;
                            case("chest"):
                                if(stationaryThingsTouching(coords[0]+1,coords[1]+1,0,0)==-1 && openAreaForWholeBody(coords[0],coords[1],2,2)) {createNewStationaryThing("chest",coords[0],coords[1]);p1.removeOneOfSelectedItem();}
                            break;
                        }
                    }
                }
                
            }
            }
            if(p1.inventory[p1.selectedItem]!=null) {p1.reloadMarker = p1.inventory[p1.selectedItem].reloadSet;} else {p1.reloadMarker = 3;}
        }
    }

    public void playerKeys(Keys keys) {
        if((keys.w || keys.space) && playerOnGround() && !p1.inChest) {p1.yv = -1; }
        if(keys.a && p1.xv > -0.3) {if(openAreaForWholeBody(p1.simplex-1,p1.simpley,p1.width*p1.size,p1.height*p1.size)) {p1.xv -= 0.025;}}
        if(keys.d && p1.xv < 0.3) {if(openAreaForWholeBody(p1.simplex+1,p1.simpley,p1.width*p1.size,p1.height*p1.size)) {p1.xv += 0.025;}}
        //if(keys.shift) {createNewMovingThing(p1.x,p1.y,"dwarf");}//activeWorld.GAMEFIELD.blockArray[p1.simplex][y].hexcolor="#F0FFFF";}}
        //if(keys.a) {p1.x -= 0.3;}
        //if(keys.d) {p1.x += 0.3;}
        if(!(keys.a || keys.d)) {/*p1.x=p1.simplex;*/ if(p1.xv > 0) {p1.xv -= 0.1;} else if(p1.xv < 0) {p1.xv += 0.1;} if(Math.abs(p1.xv) < 0.1) {p1.xv = 0;}}
        if(keys.q) { // throw item
            if(p1.reloadMarker == 0) {
                throwItem();
                if(p1.inventory[p1.selectedItem]!=null) {p1.reloadMarker = p1.inventory[p1.selectedItem].reloadSet;if(p1.inventory[p1.selectedItem].type.equals("block")) {p1.reloadMarker = 2;}} else {p1.reloadMarker = 3;}
                
            }
        }
        if(keys.p) {fileWitch.makeImageFromBlockArray(activeWorld);}
        //if(keys.e) {p1.inventory[0].reloadSet = 0;}
        if(keys.up && menubuttoncooldown<=0) {p1.craftSelect--;if(p1.craftSelect<0) {p1.craftSelect=p1.canCraft.size();}menubuttoncooldown = 33;}
        if(keys.down && menubuttoncooldown<=0) {p1.craftSelect++;if(p1.craftSelect>=p1.canCraft.size()) {p1.craftSelect=0;}menubuttoncooldown = 33;}
        if(keys.right && menubuttoncooldown<=0) {if(p1.canCraft.size()>0 && p1.craftSelect<p1.canCraft.size()){p1.craftSelectedItem();}if(p1.craftSelect>=p1.canCraft.size()) {p1.craftSelect=0;}menubuttoncooldown = 33;}
        if(keys.key()>=49 && keys.key()<=59) {p1.selectedItem = keys.key()-49;}
        if(p1.inChest && keys.shift && menubuttoncooldown==0) {p1.depositing = !p1.depositing;menubuttoncooldown = 33;}
        if(p1.inChest && keys.w) {
            p1.canCraft = p1.getCraftableItems();
            if(p1.depositing) {
            if(p1.inventory[p1.selectedItem]!=null && p1.currentChest.canFitItem(p1.inventory[p1.selectedItem].name)) {
                System.out.println("depositing");
                for(int i = 0; i < p1.inventory[p1.selectedItem].quantity; i++) {p1.currentChest.pickUpItem(items.gimmeItem(p1.inventory[p1.selectedItem].name),p1.inventory[p1.selectedItem].quantity); }
                //p1.currentChest.inventory[p1.chestSelectedItem].quantity = p1.inventory[p1.selectedItem].quantity; 
                p1.removeItem(p1.selectedItem,p1.inventory[p1.selectedItem].quantity);
            }
            menubuttoncooldown = 33;
        } else {
            if(p1.currentChest.inventory.length>0) {
                if(p1.currentChest.inventory[p1.chestSelectedItem]!=null && p1.canFitItem(p1.currentChest.inventory[p1.chestSelectedItem].name)) {
                    p1.pickUpItem(p1.currentChest.inventory[p1.chestSelectedItem],p1.currentChest.inventory[p1.chestSelectedItem].quantity); 
                    //p1.inventory[p1.selectedItem].quantity = p1.currentChest.inventory[p1.chestSelectedItem].quantity; 
                    p1.currentChest.removeItem(p1.chestSelectedItem,p1.currentChest.inventory[p1.chestSelectedItem].quantity);
                }
            }
            menubuttoncooldown = 33;
        }
    }
        if(keys.e) {if(p1.inChest) {p1.inChest = false;}menubuttoncooldown = 33;}
    }
    public void handleClick(double x, double y) {
        switch(location) {
            case("in_game"):
                gameClick(x,y);
            break;
            default:
            break;
        }
    }

    public movingThing movingThingPhysicsStep(movingThing t) { // player physics
        double predictedx = t.x+t.xv; double predictedy = t.y+t.yv;
        // oochie scootch until potential destination, see if anything along the path is blocked.
        double checkx = t.x; double checky = t.y;
        double pathLength = distance(t.x,t.y,predictedx,predictedy);
        double travelled = 0;
        double txv = t.xv; double tyv = t.yv;
        // get length of velocity vector, divide each velocity component by that.
        double velocityVectorLength = distance(0,0,t.xv,t.yv);
        txv = t.xv/velocityVectorLength; tyv = t.yv/velocityVectorLength;
        double stepLength = distance(0,0,txv,tyv);
        boolean blockage = false;
        while(travelled <= pathLength) {
            int[] ecp = activeWorld.roundedCoordinates(checkx, checky);
            //if(openAreaForWholeBody(ecp[0],ecp[1])&& openAreaForWholeBody((int)(ecp[0]+ttxv),(int)(ecp[1]+ttyv))) {
            if(openAreaForWholeBody(ecp[0],ecp[1],t.w*t.size,t.h*t.size) && openAreaForWholeBody(ecp[0]+txv,ecp[1]+tyv,t.w*t.size,t.h*t.size)) {
                checkx += txv; checky += tyv;
                travelled += stepLength;
            } else {blockage = true; break;}
        }
        if(!blockage) {
            if(openAreaForWholeBody(predictedx,predictedy,t.w*t.size,t.h*t.size)) {
                t.x = predictedx;
                t.y = predictedy;
            } else {}
        }  else if(openAreaForWholeBody(t.x,predictedy,t.w*t.size,t.h*t.size)) {t.y = predictedy;} else if(openAreaForWholeBody(checkx,checky,t.w*t.size,t.h*t.size)) {t.x = checkx; t.y = checky; t.xv=0; t.yv=0;} else {t.xv = 0; t.yv = 0;}
        if(!movingThingOnGround(t)) {t.yv += 0.03;} else if(t.yv >0) {t.yv = 0;}
        if(!openAreaForWholeBody(t.x,t.y,t.w*t.size,t.h*t.size)) {t.y-=0.1;}
        int[] ec = activeWorld.roundedCoordinates(t.x, t.y); // estimated coordinates
        t.simplex = ec[0]; t.simpley = ec[1];
        if(t.yv==0&&t.xv==0) {t.x=t.simplex;t.y=t.simpley;}
        return t;
    }
    public void movingThingsStep() {
        Random r = new Random();
        howManyMovingThings = movingThings.size();
        for(int i = 0; i < howManyMovingThings; i++) {
            movingThing t = movingThings.get(i);
            //System.out.println(t.convertToString());
            if(t.isBlock && movingThingOnGround(t) && distance(t.x,t.y,p1.x,p1.y) > 100) {{turnBlocksSolid(t, i);}}
            else if(distance(t.x,t.y,p1.x,p1.y) > 400) { // despawning
                movingThings.remove(i);
                howManyMovingThings--;
            } else {
                if(t.health <= 0) {for(int m = 0; m < t.getDrops(t.name).length; m++) {movingThings.add(t.createDroppedItem(t.getDrops(t.name)[m],t.x,t.y));}howManyMovingThings++;movingThings.remove(i);howManyMovingThings--;} else {//System.out.println("thing " + i + "died!!! " + howManyMovingThings + " remain");} else {
                    t = movingThingPhysicsStep(t);
                    if(isColliding(t.x,t.y,t.w*t.size,t.h*t.size,p1.x,p1.y,p1.width*p1.size,p1.height*p1.size) && t.type.equals("droppeditem")) { // if player is colliding with dropped item
                        p1.checkIfInventoryFull();
                        if(p1.canFitItem(t.name)) {
                            p1.pickUpItem(t,(int)Math.pow(t.size,2));
                            movingThings.remove(i);
                            howManyMovingThings--;
                        }
                    } else {
                        movingThings.set(i,t);
                        if(isColliding(t.x,t.y,t.w*t.size,t.h*t.size,p1.x,p1.y,p1.width*p1.size,p1.height*p1.size) && (t.type.equals("enemy") || t.type.equals("projectile")) && t.reload==0) {p1.pushDamage(t.damage); t.reload = t.reloadSet;}
                    }
                    if((t.type.equals("droppeditem"))) {// || t.type.equals("block"))) {
                        int[] itemVictims = movingThingsTouchingRectangle(t.x,t.y,t.w*t.size,t.h*t.size, true);
                        if(Math.abs(t.xv)>0.6 || Math.abs(t.yv) > 0.6) {
                            for(int f = 0; f < itemVictims.length; f++) {
                                if(movingThings.get(itemVictims[f]).health < 1000) {movingThings.get(itemVictims[f]).pushDamage((Math.abs(t.xv)/2+Math.abs(t.yv)/2)); makeABunchOfParticles(t.x, t.y, 2, "#00FFFF");}
                            }
                        }
                        if(movingThingOnGround(t)) {
                        // ----------------------------------------------------------------------------------------------------------------------------------------------

                        // below: merge with items of same type to create larger item
                        // gather items of same type, judge whether there is enough material to move up to the next size (it shall be that the width times the height = the quantity, so rather than 2 items for size 2, you need 4 items for size 2)
                        // if there is enough, up the size and remove all the absorbed items
                        int[] fellowItems = movingThingsTouchingRectangle(t.x-10,t.y-10,(t.w*t.size)+10,(t.h*t.size)+10,false);
                        int neededMaterial = (int)(Math.pow(t.size+1,2)-Math.pow(t.size,2));
                        int availableMaterial = 0;
                        List<Integer> toRemove = new ArrayList();
                        int gatheredMaterial = 0;
                        int remainder = 0;
                        for(int a = 0; a < fellowItems.length; a++) {
                            // get total value of matching items which are overlapping
                            movingThing check = movingThings.get(fellowItems[a]);
                            if(check.name.equals(t.name) && !check.equals(t) && check.size <= t.size) {availableMaterial += Math.pow(movingThings.get(fellowItems[a]).size,2);}
                        }
                        if(availableMaterial >= neededMaterial) {
                            // if there is enough, add up gathered material and mark victims for removal
                            for(int g = 0; g < fellowItems.length; g++) {
                                movingThing subject = movingThings.get(fellowItems[g]);
                                if(subject.name.equals(t.name) && (!subject.equals(t)) && subject.size <= t.size) {
                                    toRemove.add(fellowItems[g]);
                                    gatheredMaterial += Math.pow(subject.size,2);
                                }
                            }
                        }
                        remainder = gatheredMaterial-neededMaterial;
                        if(remainder >= 0) {
                            //System.out.println("needed: " + neededMaterial + " available: " + availableMaterial + " gathered: " + gatheredMaterial + " remainder " + remainder);
                            // spit out remainder
                            t.size+= 1;
                            t.x-=1; t.y-=1;
                            for(int n = 0; n < remainder; n++) {
                                movingThing remainderDrop = new movingThing();
                                remainderDrop = remainderDrop.createDroppedItem(items.gimmeItem(t.name), t.x, t.y);
                                movingThings.add(remainderDrop);
                                howManyMovingThings++;
                            }
                        }
                        // remove
                        int removal = toRemove.size();
                        if(removal>0) {
                            for(int b = removal-1; b >= 0; b--) {
                                //System.out.println(toRemove.get(b));
                                movingThings.remove((int)toRemove.get(b));
                                //System.out.println("removal change: " + presize + " to " + movingThings.size());
                                howManyMovingThings--;
                            }
                        }

                        // ----------------------------------------------------------------------------------------------------------------------------------------------
                    }
                    } else {
                        t = movingThingBehavior(t,r);
                    }
                }
                // damage things
                if(t.reload>0) {t.reload--;} else {t.squish = false;}
            }
        }
    }
    public boolean isPerfectSquare(int input) {
        return (input== Math.pow((int)Math.sqrt(input),2));
    }
    public movingThing movingThingBehavior(movingThing t, Random r) {
        if(t.name.equals("noselegs") || t.name.equals("noselegs1") || t.name.equals("noselegs2")) {
            if(movingThingOnGround(t)) {
                if(t.x>p1.x) {t.xv = -1.1;} else {t.xv = 1.1;}
                if(gamestepcooldown<=0) {if(!t.name.equals("noselegs2")) {t.name = "noselegs2";} else {t.name = "noselegs1";}}
                if(r.nextDouble()<=0.7) {
                    if(t.y<p1.y) { // smash blocks
                        t.yv = -0.8;
                        int xbreakrange = (int)(Math.abs(t.x-p1.x)/2);
                        for(int x = t.simplex-xbreakrange; x < xbreakrange+t.simplex+(t.w*t.size); x++) {
                            for(int y = t.simpley-2; y < 2+t.simpley+(t.h*t.size); y++) {
                                breakBlockForDrop(x,y);
                            }
                        }
                        //t.xv = 0;
                    } else {
                        t.yv = -5.1;System.out.println("big jump");
                        for(int x = t.simplex-2; x < 2+t.simplex+(t.w*t.size); x++) {
                            for(int y = t.simpley-((int)Math.abs(p1.y-(t.y+(t.h*t.size)))); y < 2+t.simpley+(t.h*t.size); y++) {
                                breakBlockForDrop(x,y);
                            }
                        }
                    }
                }
            }
            if(isColliding(t.x,t.y,t.w*t.size,t.h*t.size,p1.x,p1.y,p1.width*p1.size,p1.height*p1.size) && t.reload==0) {
                p1.pushDamage(9);
                t.reload = t.reloadSet;
            }
        } else if(t.type.equals("projectile")) {
            if(t.name.equals("clownball1") && movingThingOnGround(t)) {makeExplosion(t.x,t.y,2); movingThings.remove(t); howManyMovingThings--;}   
            if(t.name.equals("clownball2") && movingThingOnGround(t)) {for(int b = 0; b < 4; b++) {activeWorld.setBlock(t.simplex+(b-2),t.simpley,"pink_grass");} movingThings.remove(t); howManyMovingThings--;}
        } else {
        if(t.jumps) {
            if(movingThingOnGround(t)) {
                t.yv = -(r.nextDouble()*1.4);
                if(t.movestowardsplayer) {
                    if(t.x>p1.x) {t.xv = -0.2;} else {t.xv = 0.2;}            
                } else {
                    t.xv = r.nextDouble()-0.5;
                }
            }
        }
        if(t.breaksblocks) {
            if(movingThingOnGround(t)) {
                if(t.x>p1.x) {t.xv = -0.2;} else {t.xv = 0.2;}
                t.yv = -r.nextDouble();
                for(int x = t.simplex-1; x < t.simplex+1+(t.w*t.size); x++) {
                    for(int y = t.simpley-1; y < t.simpley+(t.h*t.size); y++) {
                        breakBlockForDrop(x,y);
                    }
                }
            }
        }
        if(t.name.equals("clown1") && t.reload <= 0) {t.name = "clown2";}
        if(t.name.equals("clown2")) {
            //if(gamestepcooldown<=0) {
            if(t.reload<= 0) {
                movingThing juggleball = new movingThing(); 
                if(t.simplex%2==0 && t.reload == 0) {juggleball = juggleball.gimmeMovingThing(t.simplex,t.simpley,"clownball1", new Random());} else if(t.reload == 0) {
                    juggleball = juggleball.gimmeMovingThing(t.simplex,t.simpley,"clownball2", new Random());
                    t.reloadSet = 25;
                }
                juggleball.yv = -0.53; juggleball.xv = 0.3*(p1.simplex-t.simplex)/(p1.reach+2); movingThings.add(juggleball);  howManyMovingThings++;
                t.health -= 3;
                t.name = "clown1";
                t.reload = t.reloadSet;
            }
        }
        if(t.type.equals("particle")) {
            t.size-=0.01;
            if(t.size < 0.01) {
                movingThings.remove(t);
                howManyMovingThings--;
            }
        }
    }
        return t;
    }
    public void turnBlocksSolid(movingThing t, int i) { // t is the blocks to turn solid, i is its index in the movingThings array
        for(int bpx = t.simplex; bpx < t.simplex+t.size; bpx++) {for(int bpy = t.simpley; bpy < t.simpley+t.size; bpy++) {activeWorld.setBlock(bpx,bpy,t.name);}}  movingThings.remove(i); howManyMovingThings--;
    }
    public void makeParticle(double x, double y, String name) { // type: "particle" ---  name represents the block it came from.
        movingThing p = new movingThing();
        p = p.gimmeMovingThing((int)x, (int)y, "particle", new Random());
        p.name = name;
        p.yv = -0.4;
        movingThings.add(p); 
        howManyMovingThings++;
    }
    public void makeABunchOfParticles(double x, double y, double amount, String name) {
        for(int p = 0; p < amount; p++) {
            makeParticle(x,y,name);
        }
    }
    public void createNewMovingThing(double x, double y) {
        Random r = new Random();
        movingThing thing = new movingThing();
        double thingNum = r.nextDouble();
        switch(activeWorld.getBlock((int)x,(int)y).type) {
            case("grass"):
            if(thingNum > 0.35) {
                thing = thing.gimmeMovingThing((int)x, (int)y, "gnome", r);
            } else {
                thing = thing.gimmeMovingThing((int)x, (int)y, "live_muffin", r);
            }
            break;
            case("leaves"):
            if(thingNum > 0.35) {
                thing = thing.gimmeMovingThing((int)x, (int)y, "gnome", r);
            } else {
                thing = thing.gimmeMovingThing((int)x, (int)y, "live_muffin", r);
            }
            break;
            case("sand"):
            thing = thing.gimmeMovingThing((int)x, (int)y,"desert_fox",r);
            break;
            case("stone"):
            if(r.nextBoolean()) {thing = thing.gimmeMovingThing((int)x, (int)y,"beetle",r);} else {
                thing = thing.gimmeMovingThing((int)x, (int)y,"mole",r);
            }
            break;
            case("crag_stone"):
            thing = thing.gimmeMovingThing((int)x, (int)y,"beetle",r);
            break;
            case("pink_grass"):
            if(r.nextBoolean()) {thing = thing.gimmeMovingThing((int)x, (int)y,"rabbit",r);} else {
                thing = thing.gimmeMovingThing((int)x,(int)y,"clown1",r);
            }
            break;
            case("pink_leaves"):
            if(r.nextBoolean()) {thing = thing.gimmeMovingThing((int)x, (int)y,"rabbit",r);} else {
                thing = thing.gimmeMovingThing((int)x,(int)y,"clown1",r);
            }
            break;
            case("pink_stone"):
            thing = thing.gimmeMovingThing((int)x,(int)y,"clown1",r);
            break;
        }
        while(!openAreaForWholeBody(thing.x,thing.y,thing.w*thing.size,thing.h*thing.size) && Math.abs(thing.y-y)<10) {thing.y -= 1;}
        movingThings.add(thing); howManyMovingThings++;
        for(int i = 0; i < howManyMovingThings; i++) {
            if(movingThings.get(i)==null) {System.out.println("NULL FOUND");}
        }

    }
    public void summon(double x, double y, String summon_source) {
        switch(summon_source) {
            case("noselegitem"):
                movingThing noselegs = new movingThing();
                noselegs.init();
                noselegs.x = x; noselegs.y = y-100;
                noselegs.size = 31;
                noselegs.name = "noselegs";
                noselegs.health = 4100;
                noselegs.maxhealth = 4100;
                noselegs.type = "enemy";
                noselegs.reloadSet = 10;
                noselegs.damage = 5;
                movingThings.add(noselegs); howManyMovingThings++;
            break;
        }
    }
    public void makeExplosion(double x, double y, double radius) {
        for(double ex = x-radius; ex < x+ radius; ex++) {
            for(double ey = y-radius; ey < y + radius; ey++) {
                if(distance(ex,ey,x,y) <= radius) {breakBlockForDrop((int)ex,(int)ey);}
            }
        }
        makeABunchOfParticles(x, y, Math.pow(radius,4), "#FF0000");
    }
    public int[] movingThingsTouchingRectangle(double x, double y, double w, double h, boolean ignoreItems) {
        // return a list of the indexes of the things which are touching the given rectangle.
        int howmany = 0;
        for(int i = 0; i < howManyMovingThings; i++) {
            movingThing t = movingThings.get(i);
            if(isColliding(t.x,t.y,t.w*t.size,t.h*t.size,x,y,w,h) && !(ignoreItems&&t.type.equals("droppeditem")) && !t.type.equals("particle")) {
                howmany++;
            }
        }
        int[] indexes = new int[howmany];
        int indexesiter = 0;
        for(int i = 0; i < howManyMovingThings; i++) {
            movingThing t = movingThings.get(i);
            if(isColliding(t.x,t.y,t.w*t.size,t.h*t.size,x,y,w,h)  && !(ignoreItems&&t.type.equals("droppeditem")) && !t.type.equals("particle")) {
                indexes[indexesiter]=i;
                indexesiter++;
            }
        }
        return indexes;
    }
    // throw item, 
    public void throwItem() {
        if(p1.inventory[p1.selectedItem]!=null) {
            Point mousePoint = new Point(MouseInfo.getPointerInfo().getLocation());
            mousePoint.x-=frame.getLocation().x; mousePoint.y-=frame.getLocation().y-30;
            double[] mouseLocation = activeWorld.exactCoordsFromScreenPosition(mousePoint.x,mousePoint.y,camerax,cameray,camerazoom,WIDTH,HEIGHT);
            movingThing thrownItem = new movingThing();
            thrownItem = thrownItem.createDroppedItem(p1.inventory[p1.selectedItem], p1.simplex, p1.simpley);
            thrownItem.xv = (mouseLocation[0]-p1.x)/25; thrownItem.yv = (mouseLocation[1]-p1.y)/25;
            while(isColliding(thrownItem.x,thrownItem.y,thrownItem.w*thrownItem.size,thrownItem.h*thrownItem.size,p1.x,p1.y,p1.width*p1.size,p1.height*p1.size)) {thrownItem.x+=thrownItem.xv;thrownItem.y+=thrownItem.yv;}
            movingThings.add(thrownItem); howManyMovingThings++;
            p1.inventory[p1.selectedItem].quantity--;
            if(p1.inventory[p1.selectedItem].quantity<=0) {p1.inventory[p1.selectedItem]=null;}
        }
    }

    //damage from creatures, DONE
    //blocks drop themselves when broken, DONE
    public void breakBlockForDrop(int x, int y) {
        if(!activeWorld.GAMEFIELD.isAir(x,y) && !activeWorld.GAMEFIELD.isWater(x,y)) {
            movingThing blockDrop = new movingThing();
            item getexample = items.gimmeItem(activeWorld.GAMEFIELD.getTypeAt(x,y));
            blockDrop = blockDrop.createDroppedItem(getexample,x,y);
            blockDrop.type = "droppeditem";
            movingThings.add(blockDrop);
            howManyMovingThings++;
            activeWorld.setBlock(x,y,"air");
            makeABunchOfParticles(x, y, 2, framePainter.getColorForBlock(blockDrop.name));
        }
    }
    //automatic, random creature spawning, 
    public void spawnCreatureNaturally() {
        System.out.println("spawning");
        Random r = new Random();
        for(int sx = p1.simplex-renderDistance; sx<p1.simplex+renderDistance; sx+= r.nextInt(2,20)) {
            for(int sy = p1.simpley-renderDistance; sy<p1.simpley+renderDistance; sy+= 2) {
                if(distance(sx,sy,p1.x,p1.y)>60 && !activeWorld.GAMEFIELD.isAir(sx,sy) && openAreaForWholeBody(sx,sy-5,3,3)) {
                    System.out.println(sx + " , " + sy + " spawn");
                    createNewMovingThing(sx,sy);
                }
            }
        }
    }
    // stationarythings
    public void createNewStationaryThing(String name, int x, int y) {
        stationaryThing t = new stationaryThing();
        t = t.gimmeStationaryThing(name);
        t.x = x; t.y = y;
        stationaryThings.add(t);
        howManyStationaryThings++;
    }
    public void breakStationaryThing(int x, int y) {
        int target = stationaryThingsTouching(x,y,1,1);
        if(target != -1) {
            movingThing drop = new movingThing();
            drop = drop.createDroppedItem(items.gimmeItem(stationaryThings.get(target).name), x, y);
            movingThings.add(drop);
            howManyMovingThings++;
            stationaryThings.remove(target);
            howManyStationaryThings--;
        }
    }
    public int stationaryThingsTouching(int x, int y, int w, int h) {
        int targetindex = -1;
        for(int i = 0; i < howManyStationaryThings; i++) {
            stationaryThing t = stationaryThings.get(i);
            if(isColliding(x,y,w,h,t.x,t.y,t.w*t.size,t.h*t.size)) {
                targetindex = i; break;
            }
        }
        return targetindex;
    }

    // official to-do list:
    //clowns
    /*
     * livestock
     * make water feel like water DONE
     * flowers that have something special/ useful about them
     * generated structures
     * natural regeneration DONE
     * mime
     * tree growing enemy
     * building thrower?
     * mushroom boss
     * living villages
     */

     //MORE IMPORTANT TO-DO LIST
     /*
      * make water flow again DONE
        make the blocks generate in a textured way (get rid of the checkerboard thing) DONE except the checkerboard shall remain
        make the world generation much better
        - living biome, larger trees, add lava and slime
        make player using tool animation (probably just have the tool bounce around on the end of the player's arm like yoyos in terraria)
        rewrite the way creature behavior works, have behavior dictated by boolean values like 'walk_towards_player = false', or 'jumps = true' rather than have it be decided by name and type
        perhaps use java serialization to save creatures to file, but maybe just have them set themselves up like the blocks do.
        make it so dropped items bunch together they way they do in minecraft, and dropped blocks become solid if too far from player.
        make plants all cool and simulated, add grass and flowers and vines and maybe roots
        make water splash when things land in it?
        add the ability for blocks to be pushed out of the way of something.
        eventually, add lighting.
        add explosions (and implosions?)
        add generated structures

        step by step:
        1. creature behavior rewrite DONE
        2. dropped item bunching change CANCELLED
        3. blocks can be trown around in such a way that they become solid when they hit the ground (maybe block items place themselves if too far fm player) DONE
        4. blocks generate in textured way. DONE
        5. add particle effects for hitting things and breaking blocks. DONE
        6. actually good world generation.
      */
}