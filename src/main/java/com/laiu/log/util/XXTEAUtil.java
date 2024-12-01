package com.laiu.log.util;


import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author liuzhixin
 * @Description:
 */
public final class XXTEAUtil {
    private static final int DELTA = -1640531527;

    private static int MX(int sum, int y, int z, int p, int e, int[] k) {
        return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
    }

    private XXTEAUtil() {
    }

    public static final byte[] encrypt(byte[] data, byte[] key) {
        return data.length == 0 ? data : toByteArray(encrypt(toIntArray(data, true), toIntArray(fixKey(key), false)), false);
    }

    public static final byte[] encrypt(String data, byte[] key) {
        try {
            return encrypt(data.getBytes("UTF-8"), key);
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException e = var3;
            e.printStackTrace();
            return null;
        }
    }

    public static final byte[] encrypt(byte[] data, String key) {
        try {
            return encrypt(data, key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException e = var3;
            e.printStackTrace();
            return null;
        }
    }

    public static final byte[] encrypt(String data, String key) {
        try {
            return encrypt(data.getBytes("UTF-8"), key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException e = var3;
            e.printStackTrace();
            return null;
        }
    }

    public static final String encryptToBase64String(byte[] data, byte[] key) {
        byte[] bytes = encrypt(data, key);
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    public static final String encryptToBase64String(String data, byte[] key) {
        byte[] bytes = encrypt(data, key);
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    public static final String encryptToBase64String(byte[] data, String key) {
        byte[] bytes = encrypt(data, key);
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    public static final String encryptToBase64String(String data, String key) {
        byte[] bytes = encrypt(data, key);
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    public static final byte[] decrypt(byte[] data, byte[] key) {
        return data.length == 0 ? data : toByteArray(decrypt(toIntArray(data, false), toIntArray(fixKey(key), false)), true);
    }

    public static final byte[] decrypt(byte[] data, String key) {
        try {
            return decrypt(data, key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException e = var3;
            e.printStackTrace();
            return null;
        }
    }

    public static final byte[] decryptBase64String(String data, byte[] key) {
        return decrypt(Base64.getDecoder().decode(data), key);
    }

    public static final byte[] decryptBase64String(String data, String key) {
        return decrypt(Base64.getDecoder().decode(data), key);
    }

    public static final String decryptToString(byte[] data, byte[] key) {
        try {
            byte[] bytes = decrypt(data, key);
            return bytes == null ? null : new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException ex = var3;
            ex.printStackTrace();
            return null;
        }
    }

    public static final String decryptToString(byte[] data, String key) {
        try {
            byte[] bytes = decrypt(data, key);
            return bytes == null ? null : new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException ex = var3;
            ex.printStackTrace();
            return null;
        }
    }

    public static final String decryptBase64StringToString(String data, byte[] key) {
        try {
            byte[] bytes = decrypt(Base64.getDecoder().decode(data), key);
            return bytes == null ? null : new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException ex = var3;
            ex.printStackTrace();
            return null;
        }
    }

    public static final String decryptBase64StringToString(String data, String key) {
        data = data.replaceAll("\\s", "+");

        try {
            byte[] bytes = decrypt(Base64.getDecoder().decode(data), key);
            return bytes == null ? null : new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException ex = var3;
            ex.printStackTrace();
            return null;
        }
    }

    private static int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;
        if (n < 1) {
            return v;
        } else {
            int q = 6 + 52 / (n + 1);
            int z = v[n];

            int p;
            int y;
            int e;
            for(int sum = 0; q-- > 0; z = v[n] += MX(sum, y, z, p, e, k)) {
                sum += -1640531527;
                e = sum >>> 2 & 3;

                for(p = 0; p < n; ++p) {
                    y = v[p + 1];
                    z = v[p] += MX(sum, y, z, p, e, k);
                }

                y = v[0];
            }

            return v;
        }
    }

    private static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;
        if (n < 1) {
            return v;
        } else {
            int q = 6 + 52 / (n + 1);
            int y = v[0];

            for(int sum = q * -1640531527; sum != 0; sum -= -1640531527) {
                int e = sum >>> 2 & 3;

                int p;
                int z;
                for(p = n; p > 0; --p) {
                    z = v[p - 1];
                    y = v[p] -= MX(sum, y, z, p, e, k);
                }

                z = v[n];
                y = v[0] -= MX(sum, y, z, p, e, k);
            }

            return v;
        }
    }

    private static byte[] fixKey(byte[] key) {
        if (key.length == 16) {
            return key;
        } else {
            byte[] fixedkey = new byte[16];
            if (key.length < 16) {
                System.arraycopy(key, 0, fixedkey, 0, key.length);
            } else {
                System.arraycopy(key, 0, fixedkey, 0, 16);
            }

            return fixedkey;
        }
    }

    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int n = (data.length & 3) == 0 ? data.length >>> 2 : (data.length >>> 2) + 1;
        int[] result;
        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        } else {
            result = new int[n];
        }

        n = data.length;

        for(int i = 0; i < n; ++i) {
            result[i >>> 2] |= (255 & data[i]) << ((i & 3) << 3);
        }

        return result;
    }

    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;
        if (includeLength) {
            int m = data[data.length - 1];
            n -= 4;
            if (m < n - 3 || m > n) {
                return null;
            }

            n = m;
        }

        byte[] result = new byte[n];

        for(int i = 0; i < n; ++i) {
            result[i] = (byte)(data[i >>> 2] >>> ((i & 3) << 3));
        }

        return result;
    }
}

