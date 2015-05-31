import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by pawel on 25.05.15.
 */
public class SchnorrSign extends SchnorrScheme {

    private final BigInteger privateKey;
    private final SecureRandom rand;

    public SchnorrSign(byte[] privateKey) {
        //publicKey = A^(-s) mod Q
        super(A.modPow(new BigInteger(to2Compliment(privateKey)), P));
        this.privateKey = new BigInteger(to2Compliment(privateKey));
        this.rand = new SecureRandom();
    }

    public SchnorrSignature sign(byte[] message) throws NoSuchAlgorithmException, IOException {
        return sign(message, new BigInteger(Q.bitLength() - 1, rand));
    }

    private SchnorrSignature sign(byte[] message, BigInteger randomValue)
            throws NoSuchAlgorithmException, IOException {

        MessageDigest  m = MessageDigest.getInstance(digestAlgorithm);

        BigInteger k = randomValue;
        BigInteger r = A.modPow(k, P);

        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(message);
        concat.write(toPositiveInt(r.toByteArray()));
        byte[] e = m.digest(concat.toByteArray());

        BigInteger positiveE = new BigInteger(to2Compliment(e));
        BigInteger s = k.subtract(privateKey.multiply(positiveE)).mod(Q);
        return new SchnorrSignature(toPositiveInt(s.toByteArray()), e, message);
    }
}
