public class stationaryThing {
    int x, y, w, h, size;
    String name;
    String type;
    item[] inventory;

    public String convertToString() {
        return name + " " + x + " " + y + " " + w + " " + h + " " + type + " " + size + "\n";
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
        inventory = new item[6];
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
            inventory[availableSpace]=theitem;
            inventory[availableSpace].quantity = quant;
            System.out.println(quant + " -- " + inventory[availableSpace].quantity);
        }
    }
    public void removeItem(int index, int removequantity) {
        inventory[index].quantity -= removequantity;
        if(inventory[index].quantity<=0) {inventory[index]=null;}
    }
    public boolean canFitItem(String theitem) {
            boolean canFit = false;
            if(inventory.length==0) {return true;}
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
    public void init() {
        inventory = new item[6];
    }

    public stationaryThing gimmeStationaryThing(String input) {
        stationaryThing response = new stationaryThing();
        response.name = input;
        response.inventory = new item[] {};
        switch(input) {
            case("fence"):
                response.w = response.h = 2;
                response.size = 2;
                response.type = "structure";
            break;
            case("chest"):
                response.w = response.h = 2;
                response.size = 2;
                response.type = "structure";
                response.inventory = new item[6];
            break;
        }
        return response;
    }
}
