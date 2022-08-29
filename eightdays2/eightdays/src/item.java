public class item {
    // items, inventory, etc;
    String name;
    String type; // weapon, food, block, etc
    int damage;
    int reload; // number which changes to exert reload time
    int reloadSet; // the number representing the length of reload time
    double range;
    boolean isBlock;
    int quantity;

    public item gimmeItem(String request) {
        item response = new item();
        response.quantity = 1;
        response.name = request;
        response.isBlock = false;
        response.type = "faketype";
        response.reload = 0;
        response.reloadSet = 2;
        switch(request) {
            case("shovel"):
                response.type = "shovel";
                response.damage = 4;
                response.range = 55;
                response.isBlock = false;
                response.reloadSet = 2;
            break;
            case("gnomehat"):
                response.type = "useless";
                response.damage = 1;
                response.range = 999;
                response.reloadSet = 10;
            break;
            case("noselegitem"):
                response.type = "useful";
                response.reloadSet = 10;
            break;
            case("foot"):
                response.type = "sword";
                response.reloadSet = 2;
                response.damage = 17;
            break;
            case("sleek_spade"):
                response.type = "shovel";
                response.damage = 4;
                response.range = 55;
                response.isBlock = false;
                response.reloadSet = 1;
            break;
            case("burrowing_plume"):
                response.type = "shovel";
                response.damage = 4;
                response.range = 55;
                response.isBlock = false;
                response.reloadSet = 1;
            break;
            case("muffin"):
                response.type = "useful";
                response.reloadSet = 5;
            break;
            case("fence"):
                response.type = "structure";
                response.reloadSet = 2;
            break;
            case("chest"):
                response.type = "structure";
                response.reloadSet = 2;
            break;
            case("mole_tool"):
                response.type = "useful";
                response.reloadSet = 5;
            break;
            case("desert_pelt"):
                response.type = "useless";
            break;
            case("beetle_exoskeleton"):  
                response.type = "useless";
            break;
            case("beetle_eye"):
                response.type = "useless";
            break;
        }
        if(request.equals("dirt") || request.equals("stone") || request.equals("sand") || request.equals("log") || request.equals("grass") || request.equals("pink_grass") || request.equals("leaves") || request.equals("pink_leaves") || request.equals("pink_stone") || request.equals("crag_stone") || request.equals("subterranean_chunk") || request.equals("flat_stone") || request.equals("grapite") || request.equals("firmamental_residue")) {
            response.isBlock = true;
            response.type = "block";
            response.reloadSet = 0;
        }
        //System.out.println("item isblock: " + response.isBlock + " for " + request);
        return response;
    }
}