import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.*;
import java.util.*;
import java.io.Serializable;

public class gameField implements Serializable{

    public block[][] blockArray;
    public String[] biomes;
    public File treeDesigns;
    public BufferedImage treeSet;
    int W, H;

    public void init() {
        System.out.println("gamefield init");
        biomes = new String[] {"basic_mountain","pink","craggy","flat","sandy"}; // removed "rounded"
        treeDesigns = new File("./eightdays/images/treeDesigns.png");
        try{
        treeSet = ImageIO.read(treeDesigns);
        } catch(Exception e) {System.out.println("error reading GAMEFIELD file: " + e.getMessage());}
    }

    public void setBlock(int x, int y, String blockType) {
        if(x>=0&&x<W&&y>=0&&y<H) { 
            blockArray[x][y].type = blockType;

        }
    }
    public void setBlockIfNotAir(int x, int y, String type) {
        if(!isAir(x,y) && !isWater(x,y)) {setBlock(x,y,type);}
    }
    public String getTypeAt(int x, int y) {if(x>=0 && x<W && y>=0 && y<H) { return blockArray[x][y].type;} else {return "none";}}
    public String getTypeAt(double x, double y) {return blockArray[(int)x][(int)y].type;}
    public boolean isAir(int x, int y) {if(x>=0&&x<W&&y>=0&&y<H) {return blockArray[x][y].type.equals("air");} else{return false;}}

    public void generateWorld(int width, int height) {
        blockArray = new block[width][height];
        W = blockArray.length;
        H = blockArray[0].length;
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                blockArray[x][y] = new block();
                blockArray[x][y].type = "air";
                //blockArray[x][y].hexcolor = "#F3F3F3";
                blockArray[x][y].doneSimulating = true;
            }
        }
        setBiomes();
        Random randy = new Random();
        
        // now the field is set, so fill it with blocks.
        // main part of generation below
        // define points which determine the likeliness of certain shapes spawning near them.
        int trix = 0; int triy = H; int squx = W; int squy = H; int cirx = W/2; int ciry = H;
        int chancedistancescaling = H/7;
        for(int x = 0; x < W; x+= 20) {
            for(int y = 0; y < H; y+= 20) {
                String cb = getBlock(x,y).biome;

                double tridist = distance(x,y,trix,triy);
                double squdist = distance(x,y,squx,squy);
                double cirdist = distance(x,y,cirx,ciry);
                double trichance, squchance, circhance;
                if(Math.abs(y-(H-(H/3))) < 100) {trichance = squchance = circhance = 1;}
                else if(y > H-(H/3)) {// if on bottom third of world, the chance increases as you get farther from the middle of the bottom third.
                    trichance = chancedistancescaling/tridist;
                    squchance = chancedistancescaling/squdist;
                    circhance = chancedistancescaling/cirdist;
                }
                else if(y > (H/2)-100) {// else if in bottom half, chance is zero.
                    trichance = squchance = circhance = 0;
                } else {// else chance increases towards the middle and the top
                    tridist = distance(trix,y,trix,triy*2);
                    squdist = distance(squx,y,squx,squy*2);
                    cirdist = distance(cirx,y,cirx,ciry*2);
                    trichance = chancedistancescaling/tridist;
                    squchance = chancedistancescaling/squdist;
                    circhance = chancedistancescaling/cirdist;
                }
                
                double chosenchance = Math.max(trichance,Math.max(squchance,circhance)); // the chosen shape to be possibly placed
                double rrr = randy.nextDouble(); // if this value is less than the chosenchance, place that shape.
                int shapesize = 13;
                if(rrr < chosenchance) {
                    if(chosenchance == trichance) {makeTriangle(x+(randy.nextInt(8*shapesize)-(4*shapesize)),y+(randy.nextInt(8*shapesize)-(4*shapesize)),x+(randy.nextInt(8*shapesize)-(4*shapesize)),y+(randy.nextInt(8*shapesize)-(4*shapesize)),x+(randy.nextInt(8*shapesize)-(4*shapesize)),y+(randy.nextInt(8*shapesize)-(4*shapesize)),biomeStone(cb));}
                    if(chosenchance == squchance) {makeRectangle(x,y,randy.nextInt(1,4*shapesize),randy.nextInt(1,4*shapesize),biomeStone(cb));}
                    if(chosenchance == circhance) {makeCircle(x,y,randy.nextInt(1,2*shapesize),biomeStone(cb));}
                }
            }
        }
        // put down dirt/ grass
        for(int x = 0; x < W; x++) {
            int gy = H/2;
            while(isAir(x,gy) || gy > H) {gy++;}
            String cb = getBlock(x,gy).biome;
            setBlock(x,gy,biomeGrass(cb));
            if(!cb.equals("sandy") && !cb.equals("craggy")) {for(int d = 1; d < 30; d++) {setBlock(x,gy+d,"dirt");}}
        }

        makeTrees();

        for(int x = 0; x < W; x++) {if(x%77==0) {setBlock(x,77,"firmamental_residue"); setBlock(x,H-77,"grapite"); setBlock(x,H-78,"subterranean_chunk");}}

        // main part of generation above
        // turn blocks into big chunky blocks
        block[][] bigger = new block[W*2][H*2];
        for(int x = 0; x < W; x++) {
            for(int y = 0; y < H; y++) {
                bigger[x*2][y*2] = blockArray[x][y].copy();
                bigger[(x*2)+1][y*2] = blockArray[x][y].copy();
                bigger[x*2][(y*2)+1] = blockArray[x][y].copy();
                bigger[(x*2)+1][(y*2)+1] = blockArray[x][y].copy();
            }
        }
        W *= 2;
        H *= 2;
        blockArray = bigger;
    }
    public void makeCircle(int x, int y, int radius, String block) {
        for(int cx = x-radius; cx < x+radius; cx++) {
            for(int cy = y-radius; cy < y+radius; cy++) {
                if(distance(x,y,cx,cy)<=radius) {setBlock(cx,cy,block);};
            }
        }
    }
    public void makeOval(int x, int y, int x2diff, int y2diff, int radius, String block) {
        for(int cx = x-radius; cx < x+x2diff+radius; cx++) {
            for(int cy = y-radius; cy < y+y2diff+radius; cy++) {
                if((distance(cx,cy,x,y)+distance(cx,cy,x+x2diff,y+y2diff))<=radius*2) {setBlock(cx,cy,block);}
            }
        }
    }
    public void makeRectangle(int x, int y, int w, int h, String block) {
        for(int cx = x; cx < x+w; cx++) {
            for(int cy = y; cy < y+h; cy++) {
                setBlock(cx,cy,block);
            }
        }
    }
    public void makeTriangle(int x, int y, int xx, int yy, int xxx, int yyy, String block) {
        int leastx; int leasty; int mostx; int mosty;
        leastx = Math.min(x,Math.min(xx,xxx));
        leasty = Math.min(y,Math.min(yy,yyy));
        mostx = Math.max(x,Math.max(xx,xxx));
        mosty = Math.max(y,Math.max(yy,yyy));
        //double perimeter = distance(x,y,xx,yy) + distance(xx,yy,xxx,yyy) + distance(xxx,yyy,x,y);
        //System.out.println(leastx + " " + leasty + " to " + mostx + " , " + mosty + " p: " + perimeter);
        double area = areaOfTriangle(x, y, xx, yy, xxx, yyy);
        for(int cx = leastx; cx < mostx; cx++) {
            for(int cy = leasty; cy < mosty; cy++) {
                // if the summs of distances between [cx,cy] and each vertex of triangle is less than the perimeter of the triangle, it's inside.
                //double checkingdistancesum = distance(cx,cy,x,y) + distance(cx,cy,xx,yy) + distance(cx,cy,xxx,yyy);
                //if(checkingdistancesum < perimeter) {setBlock(cx,cy,block);} else {System.out.println("false");}
                double checkingarea = areaOfTriangle(cx, cy, xx, yy, xxx, yyy) + areaOfTriangle(x,y,cx,cy,xxx,yyy) + areaOfTriangle(x,y,xx,yy,cx,cy);
                if(area == checkingarea) {setBlock(cx,cy,block);}
                //System.out.println("area: " + area + " checkingarea: " + checkingarea);
            }
        }
    }
    public double areaOfTriangle(int x, int y, int xx, int yy, int xxx, int yyy) {
        return Math.abs((x*(yy-yyy) + xx*(yyy-y) + xxx*(y-yy))/2.0);
    }
    public void makeDonut(int x, int y, int radius1, int radius2, String block) { // radius 1: big radius, 2: small
        for(int cx = x-radius1; cx < x+radius1; cx++) {
            for(int cy = y-radius1; cy < y+radius1; cy++) {
                if(distance(cx,cy,x,y)<=radius1 && distance(cx,cy,x,y)>radius2) {setBlock(cx,cy,block);}
            }
        }
    }
    public void curvyLine(int xstart, int ystart, int xdirection, int ydirection, String block) {

    }

    public String biomeStone(String biome) {
        String response = "stone";
        switch(biome) {
            case("pink"):
                response = "pink_stone";
            break;
            case("craggy"):
                response = "crag_stone";
            break;
            case("flat"):
                response = "flat_stone";
            break;
        }
        return response;
    }
    public String biomeLeaves(String biome) {
        String response = "leaves";
        switch(biome) {
            case("pink"):
                response = "pink_leaves";
            break;
        }
        return response;
    }
    public String biomeGrass(String biome) {
        String response = "grass";
        switch(biome) {
            case("sandy"):
                response = "sand";
            break;
            case("pink"):
                response = "pink_grass";
            break;
        }
        return response;
    }
    
    public void makeTrees() {// TREES
        int x; int y;
        String cb;
        Random randy = new Random();
        x = 0; y = 0;
        boolean notdone = true;
        while(notdone) {
            x += 1;
            if(x >= W) {x = 0; notdone = false;break;}
            /*if(x >= W) {
                x = 0; y+=1;
            }*/
            y = (H/2)-60;
            while(isAir(x,y)) {y++;}
            boolean abort = false;
            //if(!isAir(x,y) || isAir(x,y+1)) {abort = true;}
            if(y >= H) {abort = true; notdone = false; break;}
            if(isWater(x,y+1) || getBlock(x,y+1).type.equals("leaves") || getBlock(x,y+1).type.equals("pink_leaves")) {abort = true;}
            cb = blockArray[x][y].biome;
            if((cb.equals("basic_mountain") || cb.equals("pink") || cb.equals("flat")) && !abort) {
                int which;
                if(randy.nextDouble()<0.9) {which = (int)(randy.nextDouble()*8);} else {which = randy.nextInt(0,treeDesignsSections.length);}
                //System.out.println(which);
                block[][] tree1 = getTree(which,cb,(randy.nextDouble()>0.5));
                x -= tree1.length/2;
                int ty = y;
                while(isAir(x,ty)) {ty++;}
                putTreeAt(x,ty,tree1);
                x += tree1.length+randy.nextInt(0,10);
                if(randy.nextDouble() > 0.8) {x+=9;}
            } else {x+=25;}
        }

    }
    public void putTreeAt(int x, int y,block[][] tree1) {
        for(int xx = 0; xx < tree1.length; xx++) {
            for(int yy = 0; yy < tree1[0].length; yy++) {
                if(!tree1[xx][yy].type.equals("air")) {
                    setBlock(x+xx,(y-tree1[0].length)+yy,tree1[xx][yy].type);
                }
            }
        }
        for(int xx = 0; xx < tree1.length; xx++) { // build support under tree if needed
            for(int yy = tree1[0].length-2; yy < tree1[0].length; yy++) {
                if(!tree1[xx][yy].type.equals("air")) {
                    int supporty = y-(Math.abs((tree1[0].length)-yy));
                    while(isAir(x+xx,supporty+1)) {supporty++; setBlock(x+xx,supporty,"log");}
                }

            }
        }
    }

    public block[][] getTree(int which, String biome, boolean flipHorizontally) { // first 18 are the small ones
        block[][] tree;
        int[] treeLoc = treeDesignsSections[which];//new int[] {0,101,29,49}; // x, y, w, h
        tree = new block[treeLoc[2]+1][treeLoc[3]+1];
        for(int x = 0; x <= treeLoc[2]; x++) {
            for(int y = 0; y <= treeLoc[3]; y++) {
                tree[x][y] = new block();
                tree[x][y].type = "air";
                Color treeB = new Color(treeSet.getRGB(x+treeLoc[0],y+treeLoc[1]));
                //System.out.println(treeB.getRed());
                //System.out.println(x*y);
                if(!(treeB.getGreen()==255&&treeB.getBlue()<255)) {
                    if(treeB.getRed()==255&&treeB.getBlue()==255) {
                        tree[x][y].type = biomeLeaves(biome);
                    } else if(treeB.getRed()==0&&treeB.getBlue()==0) {
                        tree[x][y].type = "log";
                    } else if(treeB.getRed()==255 && treeB.getBlue()!=255) {
                        tree[x][y].type = "grass";
                    }
                }
            }
        }
        if(flipHorizontally) {Collections.reverse(Arrays.asList(tree));}
        return tree;
    }
    int[][] treeDesignsSections = new int[][] {
        {0,0,6,28},
        {7,0,6,28},
        {14,0,6,28},
        {21,0,6,28},

        {0,29,6,21},
        {7,29,6,21},
        {14,29,6,21},
        {21,29,6,21},
        
        {0,51,30,49}, // the evergreen one
        {0,101,30,49},
        {0,151,30,49}, // heart
        {0,201,30,23}, // bonsai
        {0,225,30,25}, // malog tree
        {31,121,59,79},// big ol' oak
        {31,201,59,49}, // big ol' bonsai
        {0,251,30,48}, // square
        {31,251,29,48}, // cheeto
        {61,251,29,48}, // spiky
        {91,201,29,98}, // cool tall one
    };

    public int[] coordsOfRequestedFormation(String[][] formation) { // for locating a specific arrangement of blcoks within the world, returns the first found instance.
        int foundx = 0, foundy = 0;
        boolean thisoneisvalid = true;
        for(int x = 1; x < W; x++) {
            //thisoneisvalid = true;
            for(int y = 0; y < blockArray[x].length; y++) {
                thisoneisvalid = true;
                for(int fx = 0; fx < formation.length; fx++) {
                    for(int fy = 0; fy < formation[fx].length; fy++) {
                        /*if(blockArray[x][y].type.equals(formation[fx][fy]))*///System.out.println(blockArray[x+fx][y+fy].type + ", " + formation[fx][fy]);
                        if(x+fx >= W-1 || y+fy >= blockArray[x].length-1) {thisoneisvalid = false;} else if(!blockArray[x+fx][y+fy].type.equals(formation[fx][fy])) {thisoneisvalid = false;}
                        //System.out.println("world x: " + (x + fx) + " y: " + (y + fy) + " type: " + blockArray[x+fx][y+fy].type);
                        //System.out.println("formation x: " + fx + " fy: " + fy + "type: " + formation[fx][fy]);
                    }
                }
                // if it is still valid after passing through all example formation blocks, then the formation has been found. is set to invalid if a block doesn't line up.
                if(thisoneisvalid) {
                    foundx = x; foundy = y;
                    break;
                }
            }
            if(thisoneisvalid) {break;}
        }
        if(!thisoneisvalid) {return null;} else {
            return new int[] {foundx,foundy};
        }
    }
    public float distance(double x, double y, double xx, double yy) {
        return (float)Math.sqrt(Math.pow(Math.abs(x-xx),2)+Math.pow(Math.abs(y-yy),2));
    }
    public double diff(double a, double b) {
        return Math.abs(a-b);
    }
    public void makeSkyIslands() {
        Random r = new Random();
        int islandy = H/3;
        for(int islandx = 0; islandx < W; islandx+= r.nextInt(7,170)) {
            islandy = H/3;
            for(islandy *= 1; islandy>0; islandy -= r.nextInt(7,298)) {
                if(r.nextDouble()<0.6) skyIsland(islandx+r.nextInt(1,80),islandy,r);
            }
        }
    }

    public void makeCaves() {
        Random r = new Random();
        int cavey = H-(H/4);
        for(int cavex = 0; cavex < W; cavex+= r.nextInt(7,100)) {
            cavey = H-(H/4);
            for(cavey *= 1; cavey<H; cavey += r.nextInt(7,100)) {
                if(getBlock(cavex,cavey).type!="sand") {
                    //caveWorm(cavex+r.nextInt(1,90),cavey,10,r.nextInt(0,2)-1,1,r);
                    if(r.nextDouble()<0.7) caveSplot(cavex-r.nextInt(1,50),cavey,r);
                }
            }
        }
    }
    public void caveWorm(int x, int y, int length, int directionx, int directiony, Random r) { // cave worm that digs out a cave. 
        if(length <= 0) {} else {
            for(int f = 0; f < length; f++) {
                for(int i = -3; i < 3; i++) {
                    setBlock(x+i,y,"air");
                    setBlock(x,y+i,"air");
                }
                x += directionx; y += directiony;
            }
            length--;
            if(r.nextDouble()<0.62) {directiony*=-1;}
            caveWorm(x,y,length,directionx,directiony,r);
        }
    }
    public void caveSplot(int x, int y, Random r) {
        int otherx = x + r.nextInt(2,30); int othery = y + r.nextInt(2,30);
        int radius = r.nextInt(1,50);
        for(int cx = x-radius; cx < otherx + radius; cx++) {
            for(int cy = y-radius; cy < othery + radius; cy++) {
                if(distance(cx,cy,x,y)<=radius && distance(cx,cy,otherx,othery) <= radius) {
                    if(getBlock(cx,cy).type!="sand") setBlock(cx,cy,"air");
                }
            }
        }
    }
    public void skyIsland(int x, int y, Random r) {
        int otherx = x + r.nextInt(0,5); int othery = y + r.nextInt(0,5);
        if(r.nextDouble() > 0.5) {othery += 10;} else {otherx+=10;}
        int radius = r.nextInt(10,90);
        for(int cx = x-radius; cx < otherx + radius; cx++) {
            for(int cy = y-radius; cy < othery + radius; cy++) {
                //if(distance(cx,cy,x,y)<=radius && distance(cx,cy,otherx,othery) <= radius) {
                if(distance(cx,cy,x,y)+distance(cx,cy,otherx,othery)<=radius) {
                    setBlock(cx,cy,biomeStone(getBlock(cx,cy).biome));
                }
            }
        }
        for(int cx = x-radius; cx < otherx+radius; cx++) {
            for(int cy = othery+radius; cy > y - radius; cy--) {
                if(cx>0&&cy>0&&cx<W&&cy<H) {
                    if(!getTypeAt(cx,cy).equals("air")) {
                        if(getTypeAt(cx,cy-1).equals("air")) {
                            // place dirt below
                            setBlock(cx,cy,biomeGrass(getBlock(cx,cy).biome));
                            setBlock(cx,cy+1,biomeGrass(getBlock(cx,cy).biome));
                            if(r.nextDouble()<0.2 && r.nextDouble() < 0.6) { // 
                            }
                            setBlock(cx,cy+1,"dirt");
                            setBlock(cx,cy+2,"dirt");
                            setBlock(cx,cy+3,"dirt");
                            setBlock(cx,cy+4,"dirt");
                        }
                    } else { // if cx,cy is air
                        if(getTypeAt(cx,cy-1).equals(biomeStone(getBlock(cx,cy).biome))) { // if above it is not air
                            int l = r.nextInt(1,10);
                            for(int p = 0; p < l; p++) {
                                setBlock(cx,cy+p,biomeStone(getBlock(cx,cy+p).biome));
                            }
                        }
                    }
                }
                
            }
        }
    }

    public void setBiomes() {
        int bw = 320; // biome width -- was 600
        String currentBiome;
        Random r = new Random();
        for(int x = 0; x < W; x+= bw) {
            for(int y = 0; y < H; y+= bw) {
                currentBiome = biomes[r.nextInt(0,biomes.length)];
                //if(currentBiome.equals(null)) {System.out.println("null biome: " + x + " , " + y);}
                //currentBiome = "craggy";
                for(int xx = x; xx <= x+bw; xx++) {
                    for(int yy = y; yy <= y+bw; yy++) {
                        if(xx<W && yy < H) {
                            blockArray[xx][yy].biome = currentBiome;
                        }
                    }
                }
            }
        }
    }
    public block getBlock(int x, int y) {
        if(x>=0&&x<W&&y>=0&&y<H) {return blockArray[x][y];} else {block fake = new block(); fake.type = "fake";fake.biome = "pink"; return fake;}
    }

    public boolean isWater(int x, int y) {if(x>=0&&x<W&&y>=0&&y<H) {return blockArray[x][y].type.equals("water");} else{return false;}}

    public void gravity(int x, int y) {
        if(isAir(x,y+1)) {swapBlocks(x,y,x,y+1);}//blockArray[x][y+1].doneSimulating = true;}
    }
    public void sinkInWater(int x, int y) {
        if(isWater(x,y+1)) {swapBlocks(x,y,x,y+1);}
        //blockArray[x][y+1].doneSimulating = true;
    }

    public void swapBlocks(int x, int y, int xx, int yy) {
        // temp = x,y
        // x,y = xx,yy
        // xx,yy = temp
        block temp = blockArray[x][y];
        blockArray[x][y] = blockArray[xx][yy];
        blockArray[xx][yy] = temp;
        blockArray[x][y].doneSimulating = true;
        blockArray[xx][yy].doneSimulating = true;
        //System.out.println(getBlock(x,y).type + " switched with " + getBlock(xx,yy).type);
    }

    public void blockPhysicsStep(int left, int right, int top, int bottom) {
        Random r = new Random();
        double randouble = r.nextDouble();
        for(int x = left; x < right; x++) {
            for(int y = top; y < bottom; y++) {
                if(!getBlock(x,y).doneSimulating) {
                    block currentb = getBlock(x,y);
                    if(currentb.gravitybehavior) {gravity(x,y);blockArray[x][y].doneSimulating = true;}
                    if(currentb.sandbehavior) {
                        sinkInWater(x,y);
                        if(randouble > 0.5 && isAir(x+1,y+1)) swapBlocks(x,y,x+1,y+1);
                        else if(randouble < 0.5 && isAir(x-1,y+1)) swapBlocks(x,y,x-1,y+1);
                    }
                    if(currentb.waterbehavior) {
                        randouble = r.nextDouble();
                        if(randouble > 0.5 && isAir(x+1,y)) {
                            swapBlocks(x,y,x+1,y);
                        } else if(randouble < 0.5 && isAir(x-1,y)) {
                            swapBlocks(x,y,x-1,y);
                        }
                    }
                }
            }
        }
        for(int x = left; x < right; x++) {
            for(int y = top; y < bottom; y++) {
                blockArray[x][y].doneSimulating = false;
            }
        }
    }
}