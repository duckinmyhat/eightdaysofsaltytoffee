import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;
import javax.imageio.ImageIO;

import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;

//import org.json.simple.parser.JSONParser;
//import org.json.simple.JSONArray;
//import org.json.JSONObject;
//import org.json.simple.parser.ParseException;




public class JSONer {
    // oooooooooooooo
    public world worldFromFile(String name) {
        world obtainedWorld = new world();
        obtainedWorld.GAMEFIELD = new gameField();
        try {
            File f = new File("./eightdays/gameData/worlds/"+name+"/"+name+"World.txt");
            Scanner scan = new Scanner(f);
            
            int currentLine = 0;
            int coords[] = new int[] {0,0};
            String type = "";
            //String hexcolor = "";
            //int blocks = 0;
            while(scan.hasNextLine()) {
                currentLine++;
                String s = scan.nextLine();
                if(currentLine == 1) {obtainedWorld.name = s;}
                else if(currentLine == 2) {obtainedWorld.width = Integer.parseInt(s);}
                else if(currentLine == 3) {obtainedWorld.height = Integer.parseInt(s);obtainedWorld.GAMEFIELD.blockArray = new block[obtainedWorld.width][obtainedWorld.height];}
                else if(currentLine > 3) {
                    int lengthOfThisLineOfBlocks = 0;
                    type = "";
                    int iterate = 0;
                    while(s.charAt(iterate)!=' ') {lengthOfThisLineOfBlocks = Integer.parseInt(lengthOfThisLineOfBlocks+""+s.charAt(iterate)); iterate++;}
                    s = s.replace(" ","");
                    while(iterate<s.length()) {
                        type = type + s.charAt(iterate);
                        iterate++;
                    }
                    while(lengthOfThisLineOfBlocks>0) {
                        obtainedWorld.GAMEFIELD.blockArray[coords[0]][coords[1]] = new block();
                        obtainedWorld.setBlock(coords[0],coords[1],type);//,hexcolor);
                        coords[0]++;
                        if(coords[0]>=obtainedWorld.GAMEFIELD.blockArray.length) {
                            coords[0]=0; coords[1]++;
                        }
                        lengthOfThisLineOfBlocks -= 1;
                    }
                }
            }
            scan.close();

            System.out.println("world " + obtainedWorld.name + " loaded from file");
            System.out.println("bottom right edge color: " + obtainedWorld.getBlock(4999, 4999));
        } catch(Exception e) {System.out.println("error in JSONer: " + e.getMessage());e.printStackTrace();}

        return obtainedWorld;
    }
    public int[] indexToCoords(int index, int width, int height) {
        int y = (int)Math.floor(index/width);
        int x = index-(y*width);
        if(x < 0) {x = 0;}
        y -= 1;
        if(y<0) y=0;
        return new int[] {x,y};
    }

    public void generateFileFromWorld(world in, List<movingThing> movingThings, List<stationaryThing> stationaryThings , player p) {
        File outputFolder = new File("./eightdays/gameData/worlds/"+in.name);
        File outputFile = new File("./eightdays/gameData/worlds/"+in.name+"/"+in.name+"World.txt");
        File outputFile2 = new File("./eightdays/gameData/worlds/"+in.name+"/"+in.name+"OtherData.txt");
        try{
            outputFolder.mkdirs();
            outputFile.createNewFile();
            outputFile2.createNewFile();
            FileOutputStream fout = new FileOutputStream(outputFile);
            // add name to file
            String add = in.name + "\n";
            byte[] b = add.getBytes();
            fout.write(b);
            // width
            add = in.width + "\n";
            b = add.getBytes();
            fout.write(b);
            // height
            add = in.height + "\n";
            b = add.getBytes();
            fout.write(b);
            // blocks
            block[][] blockArray = in.GAMEFIELD.blockArray;
            block lastblock = blockArray[0][0];
            int lengthOfIdenticalBlocks = 0;
            //int howmanyblocks = 0;
            for(int y = 0; y < blockArray[0].length; y++) {
                for(int x = 0; x < blockArray.length; x++) {
                    if(blockArray[x][y].equals(lastblock) && (y < blockArray[0].length-1 || x < blockArray.length-1)) {
                        lengthOfIdenticalBlocks++;
                    } else {
                        add = lengthOfIdenticalBlocks + " " + lastblock.type + "\n";// + " " + lastblock.hexcolor + "\n";
                        //howmanyblocks+=lengthOfIdenticalBlocks;
                        b = add.getBytes();
                        fout.write(b);
                        lastblock = blockArray[x][y];
                        lengthOfIdenticalBlocks = 1;
                    }
                }
            }
            // bottom right corner -- I think this is necessary
            block bottomrightblock = blockArray[blockArray.length-1][blockArray[0].length-1];
            add = 1 + " " + bottomrightblock.type + "\n";// + " " + bottomrightblock.hexcolor + "\n";
            //howmanyblocks+=1;
            b = add.getBytes();
            fout.write(b);

            //System.out.println(howmanyblocks + " blocks exist");
            fout.close();

            FileOutputStream fout2 = new FileOutputStream(outputFile2);
            String add2 = p.toString();
            byte[] b2 = add2.getBytes();
            fout2.write(b2);

            add2 = p.inventoryToString();
            System.out.println(p.inventoryToString());
            b2 = add2.getBytes();
            fout2.write(b2);

            for(int i = 0; i < movingThings.size(); i++) {
                add2 = movingThings.get(i).convertToString();
                //System.out.println(add2);
                b2 = add2.getBytes();
                fout2.write(b2);
            }
            add2 = "stationary:\n";
            b2 = add2.getBytes();
            fout2.write(b2);
            for(int i = 0; i < stationaryThings.size(); i++) {
                add2 = stationaryThings.get(i).convertToString();
                b2 = add2.getBytes();
                fout2.write(b2);
                if(stationaryThings.get(i).name.equals("chest")) {
                    add2 = stationaryThings.get(i).inventoryToString();
                    b2 = add2.getBytes();
                    fout2.write(b2);
                }
            }
            fout2.close();

            System.out.println("World " + in.name + " saved");
        } catch(Exception e) {System.out.println(e.getMessage());}
    }

    public void makeImageFromBlockArray(world in) {
        File outputFolder = new File("./eightdays/gameData/worlds/"+in.name);
        outputFolder.mkdirs();
        BufferedImage output = new BufferedImage(in.width,in.height,BufferedImage.TYPE_INT_ARGB);
        Graphics g = output.createGraphics();
        for(int x = 0; x  < in.width; x++) {
            for(int y = 0; y < in.height; y++) {
                g.setColor(Color.decode(framePainter.getColorForBlock(in.getBlock(x,y).type)));
                g.fillRect(x,y,1,1);
            }
        }
        try{
            ImageIO.write(output, "png", new File("./eightdays/gameData/worlds/"+in.name+"/"+in.name+"Image.png"));
        } catch(Exception e) {System.out.println(e.getMessage());}
    }
    public String playerStringFromFile(String name) {
        //System.out.println("playerfromfile");
        String obtained = "if this is returned there was an error";
        File f = new File("./eightdays/gameData/worlds/"+name+"/"+name+"OtherData.txt");
        try{
            Scanner scan = new Scanner(f);
            obtained = scan.nextLine();
            scan.close();
        } catch(Exception e) {}
        //System.out.println("player: " + obtained);
        return obtained;
    }
    public String playerInventoryStringFromFile(String name) {
        String obtained = "if this is returned there was an error";
        File f = new File("./eightdays/gameData/worlds/"+name+"/"+name+"OtherData.txt");
        try{
            Scanner scan = new Scanner(f);
            String useless = scan.nextLine();
            obtained = scan.nextLine();
            System.out.println("inventory got: " + obtained);
            scan.close();
        } catch(Exception e) {}
        //System.out.println("player: " + obtained);
        return obtained;
    }

    public List<movingThing> movingThingsFromFile(String name) {
        System.out.println("movingthingsfromfile running");
        List<movingThing> oobtained = new ArrayList<movingThing>();
        try {
            File f = new File("./eightdays/gameData/worlds/"+name+"/"+name+"OtherData.txt");
            Scanner prescan = new Scanner(f);
            int lines = 0;
            while(prescan.hasNextLine()) {
                prescan.nextLine();
                lines++;
            }
            //System.out.println("lines: " + lines);
            oobtained = new ArrayList<movingThing>();
            prescan.close();

            Scanner scan = new Scanner(f);
            int currentLine = 3;
            //name + " " + x + " " + y + " " + xv + " " + yv + " " + w + " " + h + " " + size
            scan.nextLine();
            scan.nextLine();
            while(scan.hasNextLine()) {
                String thingName ="";
                String x = "";
                String y = "";
                String xv = "";
                String yv = "";
                String w = "";
                String h = "";
                String health = "";
                String maxhealth = "";
                String type = "";
                String size = "";
                currentLine++;
                String s = scan.nextLine();
                if(s.equals("stationary:")) {break;}
                if(currentLine > 2) {
                    int iterate = 0;
                    while(s.charAt(iterate)!=' ') {thingName = thingName + s.charAt(iterate); iterate++;}
                    iterate++;
                    while(s.charAt(iterate)!=' ') {x = x + s.charAt(iterate); iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {y = y + s.charAt(iterate);iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {xv = xv + s.charAt(iterate);iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {yv = yv + s.charAt(iterate);iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {w = w + s.charAt(iterate);iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {h = h + s.charAt(iterate);iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {health = health + s.charAt(iterate); iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {maxhealth = maxhealth + s.charAt(iterate); iterate++;}
                    iterate++;
                    while(s.charAt(iterate) != ' ') {type = type + s.charAt(iterate); iterate++;}
                    iterate++;
                    while(iterate<s.length()) {
                        size = size + s.charAt(iterate);
                        iterate++;
                    }
                }
                //System.out.println(thingName + " ;" + x + " ;" + y + " ;" + xv + " ;" + yv + " ;" + w + " ;" + h + " ;" + type + " " + size);
                movingThing thing = new movingThing();
                thing.init();
                thing = thing.gimmeMovingThing((int)Double.parseDouble(x),(int)Double.parseDouble(y),name,new Random());
                thing.name = thingName;
                thing.x = Double.parseDouble(x);
                thing.y = Double.parseDouble(y);
                thing.xv = Double.parseDouble(xv);
                thing.yv = Double.parseDouble(yv);
                thing.w = Integer.parseInt(w);
                thing.h = Integer.parseInt(h);
                thing.health = Double.parseDouble(health);
                thing.maxhealth = Double.parseDouble(maxhealth);
                thing.type = type;
                thing.size = Integer.parseInt(size);
                oobtained.add(thing);
                System.out.println("loading thing");
                System.out.println(thing.name + " loaded from file");
            }
            scan.close();
        } catch(Exception e) {System.out.println("error in JSONer: " + e.getMessage());e.printStackTrace();}

        return oobtained;
    }
    public List<stationaryThing> stationaryThingsFromFile(String name) {
        List<stationaryThing> obtained = new ArrayList();
        try {
            File f = new File("./eightdays/gameData/worlds/"+name+"/"+name+"OtherData.txt");
            Scanner scan = new Scanner(f);
            String thingName ="";
            while(!scan.nextLine().equals("stationary:")){}
            while(scan.hasNextLine()) {
                boolean lastthingwaschest;
                if(thingName.equals("chest")) {lastthingwaschest = true;} else {lastthingwaschest = false;}
                thingName ="";
                String x = "";
                String y = "";
                String w = "";
                String h = "";
                String type = "";
                String size = "";
                String s = scan.nextLine();
                if(lastthingwaschest) {obtained.get(obtained.size()-1).initInventoryFromString(s); System.out.println(s); lastthingwaschest = false; thingName = ""; if(!scan.hasNextLine()) {break;}} else {

                    if(true) {
                        int iterate = 0;
                        while(s.charAt(iterate)!=' ') {thingName = thingName + s.charAt(iterate); iterate++;}
                        iterate++;
                        while(s.charAt(iterate)!=' ') {x = x + s.charAt(iterate); iterate++;}
                        iterate++;
                        while(s.charAt(iterate) != ' ') {y = y + s.charAt(iterate);iterate++;}
                        iterate++;
                        while(s.charAt(iterate) != ' ') {w = w + s.charAt(iterate);iterate++;}
                        iterate++;
                        while(s.charAt(iterate) != ' ') {h = h + s.charAt(iterate);iterate++;}
                        iterate++;
                        while(s.charAt(iterate) != ' ') {type = type + s.charAt(iterate); iterate++;}
                        iterate++;
                        while(iterate<s.length()) {
                            size = size + s.charAt(iterate);
                            iterate++;
                        }
                    }
                    //System.out.println(thingName + " ;" + x + " ;" + y + " ;" + xv + " ;" + yv + " ;" + w + " ;" + h + " ;" + type + " " + size);
                    stationaryThing thing = new stationaryThing();
                    thing.init();
                    thing.name = thingName;
                    thing.x = Integer.parseInt(x);
                    thing.y = Integer.parseInt(y);
                    thing.w = Integer.parseInt(w);
                    thing.h = Integer.parseInt(h);
                    thing.type = type;
                    thing.size = Integer.parseInt(size);
                    System.out.println(thing.convertToString());
                    obtained.add(thing);
                }
            }
            scan.close();
        } catch(Exception e) {System.out.println("error in JSONer: " + e.getMessage());e.printStackTrace();}

        return obtained;
    }
}
