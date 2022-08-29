import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keys implements KeyListener{
    public int latestkey;
    public char latestchar;
    public boolean w;
    public boolean a;
    public boolean s, d, space, q, e, r, f, shift, leftcontrol, p, up, down, g, minus, equals, right;
    @Override
    public void keyPressed(KeyEvent event) {
        latestkey = event.getKeyCode();
        switch(latestkey) {
            case(87): // w
            w = true;
            break;
            case(65):
            a = true;
            break;
            case(83):
            s = true;
            break;
            case(68):
            d = true;
            break;
            case(32):
            space = true;
            break;
            case(81):
            q = true;
            break;
            case(69):
            e = true;
            break;
            case(82):
            r = true;
            break;
            case(70):
            f = true;
            break;
            case(80):
            p = true;
            break;
            case(16):
            shift = true;
            break;
            case(17):
            leftcontrol = true;
            break;
            case(38):
            up = true;
            break;
            case(40):
            down = true;
            break;
            case(71):
            g = true;
            break;
            case(61):
            equals = true;
            break;
            case(45):
            minus = true;
            break;
            case(39):
            right = true;
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        latestkey = 0;
        switch(event.getKeyCode()) {
            case(87): // w
            w = false;
            break;
            case(65):
            a = false;
            break;
            case(83):
            s = false;
            break;
            case(68):
            d = false;
            break;
            case(32):
            space = false;
            break;
            case(81):
            q = false;
            break;
            case(69):
            e = false;
            break;
            case(82):
            r = false;
            break;
            case(70):
            f = false;
            break;
            case(16):
            shift = false;
            break;
            case(17):
            leftcontrol = false;
            break;
            case(80):
            p = false;
            break;
            case(38):
            up = false;
            break;
            case(40):
            down = false;
            break;
            case(71):
            g = false;
            break;
            case(61):
            equals = false;
            break;
            case(45):
            minus = false;
            break;
            case(39):
            right = false;
            break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    public void init() {
        w = a = s = d = space = q = e = r = f = shift = leftcontrol = false;
    }

    public int key() {return latestkey;}
    public char keychar() {return latestchar;}
}
