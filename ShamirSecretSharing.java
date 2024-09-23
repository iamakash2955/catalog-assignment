import org.json.JSONObject;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONTokener;

public class ShamirSecretSharing {

    public static BigInteger decodeValue(String base, String value) {
        int baseValue = Integer.parseInt(base);
        return new BigInteger(value, baseValue);
    }

    public static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int k) {
        BigInteger secret = BigInteger.ZERO;
        for (Map.Entry<Integer, BigInteger> entry1 : points.entrySet()) {
            BigInteger xi = BigInteger.valueOf(entry1.getKey());
            BigInteger yi = entry1.getValue();

            BigInteger li = BigInteger.ONE;
            for (Map.Entry<Integer, BigInteger> entry2 : points.entrySet()) {
                if (!entry1.getKey().equals(entry2.getKey())) {
                    BigInteger xj = BigInteger.valueOf(entry2.getKey());
                    li = li.multiply(xj.negate()).divide(xi.subtract(xj));
                }
            }
            secret = secret.add(yi.multiply(li));
        }
        return secret;
    }

    public static BigInteger findSecret(JSONObject jsonObject) {
        int k = jsonObject.getJSONObject("keys").getInt("k");

        Map<Integer, BigInteger> points = new HashMap<>();

        // Iterate over each root, decode and store the (x, y) values
        for (Object keyObj : jsonObject.keySet()) {
            String key = (String) keyObj;
            if (!key.equals("keys")) {
                int x = Integer.parseInt(key);
                String base = jsonObject.getJSONObject(key).getString("base");
                String value = jsonObject.getJSONObject(key).getString("value");

                BigInteger y = decodeValue(base, value);
                points.put(x, y);
            }
        }

        // Apply Lagrange Interpolation to find the constant term 'c'
        return lagrangeInterpolation(points, k);
    }

    public static void main(String[] args) {
        // Read JSON from a file
        try (FileReader reader = new FileReader(
                "C:\\Users\\prakh\\OneDrive\\Desktop\\akash\\catalog\\testcase2.json")) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            BigInteger secret = findSecret(jsonObject);
            System.out.println("Secret (constant term c): " + secret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
