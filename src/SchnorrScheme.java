import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pawel on 31.05.15.
 */

public class SchnorrScheme {
    static final BigInteger P = new BigInteger("fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c7", 16);
    static final BigInteger Q = new BigInteger("9760508f15230bccb292b982a2eb840bf0581cf5", 16);
    static final BigInteger A = new BigInteger("f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a", 16);

    private final BigInteger publicKey;
    static final String digestAlgorithm = "SHA-256";

    private static byte[] int2bin(int i) {
        byte[] res = new byte[4];
        int2bin(res, 0, i);
        return res;
    }

    private static void int2bin(byte[] out, int offset, int i) {
        out[offset + 0] = (byte)(i >> 24);
        out[offset + 1] = (byte)(i >> 16 & 0xff);
        out[offset + 2] = (byte)(i >> 8 & 0xff);
        out[offset + 3] = (byte)(i & 0xff);
    }

    public static byte[] concatAndPrefix(byte[][] data) {
        if (data == null)
            return null;

        int totalLength = 0;
        for(int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                totalLength += 4 + data[i].length;
            }
        }

        byte[] res = new byte[totalLength];
        int p = 0;
        for(int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                byte[] len =  int2bin(data[i].length);
                System.arraycopy(len, 0, res, p, len.length);
                p += len.length;
                System.arraycopy(data[i], 0, res, p, data[i].length);
                p += data[i].length;
            }
        }

        return res;
    }

    static byte[] to2Compliment(byte[] input) {
        byte[] output = input;
        if (input != null && input.length > 0 && input[0] < 0) {
            output = new byte[input.length + 1];
            System.arraycopy(input, 0, output, 1, input.length);
        }
        return output;
    }

    static byte[] toPositiveInt(byte[] input) {
        byte[] output = input;
        if (input != null && input.length > 1 && input[0] == 0) {
            output = new byte[input.length - 1];
            System.arraycopy(input, 1, output, 0, output.length);
        }
        return output;
    }

    public SchnorrScheme(BigInteger publicKey) {
        System.out.println("pubkey: " + publicKey);
        this.publicKey = publicKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public boolean verify(SchnorrSignature signature) throws NoSuchAlgorithmException, IOException {
        MessageDigest  m = MessageDigest.getInstance(digestAlgorithm);

        BigInteger e = new BigInteger(to2Compliment(signature.getE()));
        BigInteger s = new BigInteger(to2Compliment(signature.getY()));
        BigInteger r = (A.modPow(s, P).multiply(publicKey.modPow(e, P))).mod(P);

        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(signature.getMessage());
        concat.write(toPositiveInt(r.toByteArray()));
        byte[] newE = m.digest(concat.toByteArray());

        return e.equals(new BigInteger(to2Compliment(newE)));
    }
}
