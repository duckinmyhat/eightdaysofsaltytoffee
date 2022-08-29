import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class player extends App{
    String name;
    double x; double y;
    double xv; double yv;
    int size;
    int height, width;
    public int simplex, simpley; 
    File playerStanding;
    item[] inventory;
    int selectedItem; // currently selected item in inventory
    item items;
    double health;
    double maxhealth;
    boolean inventoryfull;
    int reach;
    int reloadMarker;
    List<item> canCraft;
    int craftSelect;
    String[][] recipes;
    stationaryThing currentChest;
    item[] chestInventory;
    boolean inChest;
    boolean depositing; // true: player is depositing into chest -- false: player is withdrawing from chest
    int chestSelectedItem;

    public String toString() {
        return name + " " + health + " " + maxhealth + " " + x + " " + y + " " + xv + " " + yv + " " + size + " " + width + " " + height + "\n";
    }
    public String inventoryToString() {
        String inventoryString = "";
        int realItems = 0;
        for(int i = 0; i < inventory.length; i++) {if(inventory[i]!=null) {realItems++;}}
        inventoryString = inventoryString + realItems + " ";
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i]!=null) {
                inventoryString = inventoryString + inventory[i].quantity + " " + inventory[i].name + " ";
            }
        }
        return inventoryString + "\n";
    }
    public void initInventoryFromString(String in) {
        int inIter = 0; // in iterator
        String inventoryLength = ""; // actual length of inventory may be longers, since it can have empty spaces
        while(in.charAt(inIter)!=' ') {inventoryLength = inventoryLength + in.charAt(inIter); inIter++;}
        inIter++;
        int numberLength = Integer.parseInt(inventoryLength);
        inventory = new item[12];
        for(int i = 0; i < numberLength; i++) {
            item thisItem = new item();
            String thisName = "";
            String thisQuantity = "";
            while(in.charAt(inIter)!=' ') {thisQuantity = thisQuantity + in.charAt(inIter); inIter++;}
            inIter++;
            while(in.charAt(inIter)!=' ') {thisName = thisName + in.charAt(inIter); inIter++;}
            thisItem = thisItem.gimmeItem(thisName);
            inIter++;
            thisItem = thisItem.gimmeItem(thisName);
            thisItem.quantity = Integer.parseInt(thisQuantity);
            inventory[i] = thisItem;
        }
    }
    public void initFromString(String s) {
        // example: null 48.0 3120.0 0.0 0.0 1 2 4
        String sname = "";
        String shealth = "";
        String smaxhealth = "";
        String sx = "";
        String sy = "";
        String sxv = "";
        String syv = "";
        String ssize = "";
        String swidth = "";
        String sheight = "";
        int iterate = 0;
        while(s.charAt(iterate)!=' ') {sname = sname + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {shealth = shealth + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {smaxhealth = smaxhealth + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {sx = sx + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {sy = sy + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {sxv = sxv + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {syv = syv + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {ssize = ssize + s.charAt(iterate); iterate++;}
        iterate++;
        while(s.charAt(iterate)!=' ') {swidth = swidth + s.charAt(iterate); iterate++;}
        iterate++;
        while(iterate<s.length()) {sheight = sheight + s.charAt(iterate); iterate++;}
        name = sname; 
        health = Double.parseDouble(shealth)+0.0; 
        maxhealth = Double.parseDouble(smaxhealth)+0.0; 
        x = Double.parseDouble(sx)+0.0; 
        y = Double.parseDouble(sy)+0.0; 
        xv = Double.parseDouble(sxv)+0.0; 
        yv = Double.parseDouble(syv)+0.0; 
        size = Integer.parseInt(ssize); 
        width = Integer.parseInt(swidth); 
        height = Integer.parseInt(sheight);
        canCraft = new ArrayList();
    }

    public void resetVelocity() {xv = 0; yv = 0;}

    public void init() {
        items = new item();
        inventory = new item[12];
        inventory[0] = items.gimmeItem("shovel");
        selectedItem = 0;
        playerStanding = new File("./eightdays/images/PlayerMonkeyFront.png");
        height = 4; width = 2; size = 2;
        health = 30; maxhealth = 30;
        reach = 21;
        canCraft = new ArrayList();
        craftSelect = 0;
        recipes = new String[][] {{"1 noselegitem","82 gnomehat"},{"1 shovel","2 log", "2 stone"},{"10 stone", "1 crag_stone"},{"1 sleek_spade", "2 log","4 subterranean_chunk"},{"1 burrowing_plume","2 log","4 firmamental_residue"},{"10 fence","2 log"},{"1 chest","20 log"}}; // recipe is result,ingredient,ingredient etc.
    }
    public int intSize() {
        return (int)size;
    }
    public void checkIfInventoryFull() {
        int availablespaces = 0;
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i]==null) {availablespaces++;}
        }
        if(availablespaces == 0) {inventoryfull = true;} else {inventoryfull = false;}
    }
    public void pickUpItem(movingThing theitem, int quant) {
        int availableSpace = 0;
        boolean canStack = false;
        int stackIndex = 0;
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i]!=null) {
                if(inventory[i].name.equals(theitem.name)) {stackIndex=i; canStack = true; break;}
            }
        }
        if(canStack) {
            inventory[stackIndex].quantity+= quant;
        } else {
            while(inventory[availableSpace]!=null) {
                availableSpace++;
            }
            inventory[availableSpace]=items.gimmeItem(theitem.name);
            inventory[availableSpace].quantity = quant;
        }
    }
    public void pickUpItem(item theitem, int quant) {
        int availableSpace = 0;
        boolean canStack = false;
        int stackIndex = 0;
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i]!=null) {
                if(inventory[i].name.equals(theitem.name)) {stackIndex=i; canStack = true; break;}
            }
        }
        if(canStack) {
            inventory[stackIndex].quantity++;
        } else {
            while(inventory[availableSpace]!=null) {
                availableSpace++;
            }
            inventory[availableSpace]=items.gimmeItem(theitem.name);
            inventory[availableSpace].quantity = quant;
        }
    }
    public boolean canFitItem(String theitem) {
            boolean canFit = false;
            for(int i = 0; i < inventory.length; i++) {
                if(inventory[i]==null) {
                    canFit = true; break;
                } else {
                    //System.out.println(theitem + " and " + inventory[i].name);
                    if(inventory[i].name.equals(theitem)) {
                        canFit = true; break;
                    }
                }
            }
            return canFit;
    }
    public void removeOneOfSelectedItem() {
        inventory[selectedItem].quantity -=1;
        if(inventory[selectedItem].quantity<=0) {inventory[selectedItem] = null;}
    }
    public void removeItem(int index, int removequantity) {
        inventory[index].quantity -= removequantity;
        if(inventory[index].quantity<=0) {inventory[index]=null;}
    }
    public void pushDamage(int amount) {
        health -= amount;
    }
    public void fallDamage(double yv) {
        if(Math.abs(yv) > 1.5) {health -= Math.abs(yv)*7;}
        //System.out.println(Math.abs(yv) + " taken");
    }
    public boolean isDead() {
        return (health<=0);
    }
    public void craftSelectedItem() {
        if(!inventoryfull || canFitItem(itemAndQuantityFromString(getRecipeForItem(canCraft.get(craftSelect))[0])[1])) {
            String[] currentRecipe = getRecipeForItem(canCraft.get(craftSelect));
            String[] resultingItemInfo = itemAndQuantityFromString(currentRecipe[0]);
            movingThing middleman = new movingThing();
            middleman = middleman.createDroppedItem(items.gimmeItem(resultingItemInfo[1]), x, y);
            for(int i = 0; i < Integer.parseInt(resultingItemInfo[0]); i++) {pickUpItem(middleman,1);}

            for(int i = 1; i < currentRecipe.length; i++) {
                removeItem(indexOfItemInInventory(itemAndQuantityFromString(currentRecipe[i])[1]),Integer.parseInt(itemAndQuantityFromString(currentRecipe[i])[0]));
            }

        }
    }
    public List<item> getCraftableItems() {
        List<item> craftable = new ArrayList();
        for(int i = 0; i < recipes.length; i++) {
            // check if ingredients for this recipe are available
            boolean iscraftable = true;
            for(int check = 1; check < recipes[i].length; check++) { // checks all strings in the recipe skipping the first, since that one is the result.
                String[] readRecipe = itemAndQuantityFromString(recipes[i][check]);
                if(!isInInventory(readRecipe[1],Integer.parseInt(readRecipe[0]),false)) {
                    //System.out.println("this item is not possessed: " + readRecipe[check]);
                    iscraftable = false;
                }
            }
            if(iscraftable) {craftable.add(items.gimmeItem(itemAndQuantityFromString(recipes[i][0])[1]));}
        }
        return craftable;
    }
    public String[] itemAndQuantityFromString(String in) {
        int iter = 0;
        String quantity = "";
        String itemString = "";
        while(in.charAt(iter)!=' ') {quantity = quantity + in.charAt(iter); iter++;}
        iter++;
        while(iter<in.length()) {itemString = itemString + in.charAt(iter); iter++;}
        return new String[] {quantity,itemString};
    }
    public String[] getRecipeForItem(item request) {
        int index = 0;
        for(int i = 0; i < recipes.length; i++) {
            if(itemAndQuantityFromString(recipes[i][0])[1].equals(request.name)) {
                index = i;
                break;
            }
        }
        return recipes[index];
    }
    public boolean isInInventory(String thing, int quantity, boolean ignoreQuantity) {
        boolean answer = false;
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i]==null) {
                
            } else {
                if(inventory[i].name.equals(thing)) {
                    if(ignoreQuantity) {
                        answer = true;
                        break;
                    } else {
                        if(inventory[i].quantity>=quantity) {
                            answer = true;
                            break;
                        }
                    }
                }
            }
            
        }
        return answer;
    }
    public int indexOfItemInInventory(String request) {
        int answer = 0;
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i]!=null) {
                if(inventory[i].name.equals(request)) {
                    answer = i;
                    break;
                }
            }
        }
        return answer;
    }
}