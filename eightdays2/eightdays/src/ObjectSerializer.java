import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectSerializer {
    public static Object deserialize(String name) throws IOException, ClassNotFoundException {
        Object response;
        FileInputStream fis = new FileInputStream("./eightdays/gameData/worlds/"+name+"/"+name+"Data.txt");
        ObjectInputStream ois = new ObjectInputStream(fis);
        response = ois.readObject();
        ois.close();
        fis.close();
            
        
        return response;
    }

    public static void serialize(Object obj, String name) throws IOException{
        FileOutputStream fos = new FileOutputStream("./eightdays/gameData/worlds/"+name+"/"+name+"Data.txt");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);

        fos.close();
        oos.close();
    }
}
