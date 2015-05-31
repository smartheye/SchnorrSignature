import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by pawel on 31.05.15.
 */
public class SchnorrSignature implements Serializable {
    private final byte[] y;
    private final byte[] e;
    private final byte[] message;

    public SchnorrSignature(byte[] y, byte[] e, byte[] message) {
        this.y = y;
        this.e = e;
        this.message = message;
    }

    public byte[] getE() {
        return e;
    }

    public byte[] getY() {
        return y;
    }

    public byte[] getMessage() {
        return message;
    }
}
