// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 

package camera;

import java.io.*;
import misc.Util;

public class MD5
{
    private class MD5State
    {

        public boolean valid;
        public int state[];
        public long bitCount;
        public byte buffer[];
        private final MD5 a;

        public void reset()
        {
            state[0] = 0x67452301;
            state[1] = 0xefcdab89;
            state[2] = 0x98badcfe;
            state[3] = 0x10325476;
            bitCount = 0L;
        }

        public void copy(MD5State md5state)
        {
            System.arraycopy(md5state.buffer, 0, buffer, 0, buffer.length);
            System.arraycopy(md5state.state, 0, state, 0, state.length);
            valid = md5state.valid;
            bitCount = md5state.bitCount;
        }
        private String whsp = " ";
        public String toString()
        {
            return state[0] + whsp + state[1] + whsp + state[2] + whsp + state[3];
        }

        public MD5State()
        {
            a = MD5.this;
            valid = true;
            state = new int[4];
            buffer = new byte[64];
            reset();
        }
    }


    private MD5State MD5State_fld;
    private MD5State MD5State_cop;
    private int a_int_array1d_fld[];
    private static final byte bytearr_fld[] = {
        -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0
    };

    private MD5()
    {
        MD5State_fld = new MD5State();
        MD5State_cop = new MD5State();
        a_int_array1d_fld = new int[16];
        reset();
    }

    public byte[] getHash()
    {
        if(!MD5State_cop.valid)
        {
            MD5State_cop.copy(MD5State_fld);
            long l = MD5State_cop.bitCount;
            int i = (int)(l >>> 3 & 63L);
            int j = i >= 56 ? 120 - i : 56 - i;
            a(MD5State_cop, bytearr_fld, 0, j);
            a(MD5State_cop, a(l), 0, 8);
            MD5State_cop.valid = true;
        }
        return a(MD5State_cop.state, 16);
    }

    public String getHashString()
    {
        return toHex(getHash());
    }

    public static byte[] getHash(byte abyte0[])
    {
        MD5 md5 = new MD5();
        md5.update(abyte0);
        return md5.getHash();
    }

    public static String getHashString(byte abyte0[])
    {
        MD5 md5 = new MD5();
        md5.update(abyte0);
        return md5.getHashString();
    }

    public static byte[] getHash(InputStream inputstream)
        throws IOException
    {
        MD5 md5 = new MD5();
        byte abyte0[] = new byte[1024];
        int i;
        while((i = inputstream.read(abyte0)) != -1) 
            md5.update(abyte0, i);
        return md5.getHash();
    }

    public static String getHashString(InputStream inputstream)
        throws IOException
    {
        MD5 md5 = new MD5();
        byte abyte0[] = new byte[1024];
        int i;
        while((i = inputstream.read(abyte0)) != -1) 
            md5.update(abyte0, i);
        return md5.getHashString();
    }

    public static byte[] getHash(String s)
    {
        MD5 md5 = new MD5();
        md5.update(s);
        return md5.getHash();
    }

    public static String getHashString(String s)
    {
        MD5 md5 = new MD5();
        md5.update(s);
        return md5.getHashString();
    }

//    public static byte[] getHash(String s, String s1)
//        throws UnsupportedEncodingException
//    {
//        MD5 md5 = new MD5();
//        md5.update(s, s1);
//        return md5.getHash();
//    }

//    public static String getHashString(String s, String s1)
//        throws UnsupportedEncodingException
//    {
//        MD5 md5 = new MD5();
//        md5.update(s, s1);
//        return md5.getHashString();
//    }

    public void reset()
    {
        MD5State_fld.reset();
        MD5State_cop.valid = false;
    }

    public String toString()
    {
        return getHashString();
    }

    private void a(MD5State md5state, byte abyte0[], int i, int j)
    {
        MD5State_cop.valid = false;
        if(j + i > abyte0.length)
            j = abyte0.length - i;
        int k = (int)(md5state.bitCount >>> 3) & 0x3f;
        md5state.bitCount += j << 3;
        int l = 64 - k;
        int i1 = 0;
        if(j >= l)
        {
            System.arraycopy(abyte0, i, md5state.buffer, k, l);
            a(md5state, a(md5state.buffer, 64, 0));
            for(i1 = l; i1 + 63 < j; i1 += 64)
                a(md5state, a(abyte0, 64, i1));

            k = 0;
        }
        if(i1 < j)
        {
            int j1 = i1;
            for(; i1 < j; i1++)
                md5state.buffer[(k + i1) - j1] = abyte0[i1 + i];

        }
    }

    public void update(byte abyte0[], int i, int j)
    {
        a(MD5State_fld, abyte0, i, j);
    }

    public void update(byte abyte0[], int i)
    {
        update(abyte0, 0, i);
    }

    public void update(byte abyte0[])
    {
        update(abyte0, 0, abyte0.length);
    }

    public void update(byte byte0)
    {
        byte abyte0[] = new byte[1];
        abyte0[0] = byte0;
        update(abyte0, 1);
    }

    public void update(String s)
    {
        //update(s.getBytes());
        update(Util.stringToByteArray(s, true));
    }

//    public void update(String s, String s1)
//        throws UnsupportedEncodingException
//    {
//        update(s.getBytes(s1));
//    }

    private static String toHex(byte abyte0[])
    {
        StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2);
        for(int i = 0; i < abyte0.length; i++)
        {
            int j = abyte0[i] & 0xff;
            if(j < 16)
                stringbuffer.append("0");
            stringbuffer.append(Integer.toHexString(j));
        }

        return stringbuffer.toString();
    }

    private static int a(int i, int j, int k, int l, int i1, int j1, int k1)
    {
        i += j & k | ~j & l;
        i += i1;
        i += k1;
        i = i << j1 | i >>> 32 - j1;
        return i + j;
    }

    private static int b(int i, int j, int k, int l, int i1, int j1, int k1)
    {
        i += j & l | k & ~l;
        i += i1;
        i += k1;
        i = i << j1 | i >>> 32 - j1;
        return i + j;
    }

    private static int c(int i, int j, int k, int l, int i1, int j1, int k1)
    {
        i += j ^ k ^ l;
        i += i1;
        i += k1;
        i = i << j1 | i >>> 32 - j1;
        return i + j;
    }

    private static int d(int i, int j, int k, int l, int i1, int j1, int k1)
    {
        i += k ^ (j | ~l);
        i += i1;
        i += k1;
        i = i << j1 | i >>> 32 - j1;
        return i + j;
    }

    private static byte[] a(long l)
    {
        byte abyte0[] = new byte[8];
        abyte0[0] = (byte)(int)(l & 255L);
        abyte0[1] = (byte)(int)(l >>> 8 & 255L);
        abyte0[2] = (byte)(int)(l >>> 16 & 255L);
        abyte0[3] = (byte)(int)(l >>> 24 & 255L);
        abyte0[4] = (byte)(int)(l >>> 32 & 255L);
        abyte0[5] = (byte)(int)(l >>> 40 & 255L);
        abyte0[6] = (byte)(int)(l >>> 48 & 255L);
        abyte0[7] = (byte)(int)(l >>> 56 & 255L);
        return abyte0;
    }

    private static byte[] a(int ai[], int i)
    {
        byte abyte0[] = new byte[i];
        int k;
        int j = k = 0;
        for(; k < i; k += 4)
        {
            abyte0[k] = (byte)(ai[j] & 0xff);
            abyte0[k + 1] = (byte)(ai[j] >>> 8 & 0xff);
            abyte0[k + 2] = (byte)(ai[j] >>> 16 & 0xff);
            abyte0[k + 3] = (byte)(ai[j] >>> 24 & 0xff);
            j++;
        }

        return abyte0;
    }

    private int[] a(byte abyte0[], int i, int j)
    {
        int l;
        int k = l = 0;
        for(; l < i; l += 4)
        {
            a_int_array1d_fld[k] = abyte0[l + j] & 0xff | (abyte0[l + 1 + j] & 0xff) << 8 | (abyte0[l + 2 + j] & 0xff) << 16 | (abyte0[l + 3 + j] & 0xff) << 24;
            k++;
        }

        return a_int_array1d_fld;
    }

    private static void a(MD5State md5state, int ai[])
    {
        int i = md5state.state[0];
        int j = md5state.state[1];
        int k = md5state.state[2];
        int l = md5state.state[3];
        i = a(i, j, k, l, ai[0], 7, 0xd76aa478);
        l = a(l, i, j, k, ai[1], 12, 0xe8c7b756);
        k = a(k, l, i, j, ai[2], 17, 0x242070db);
        j = a(j, k, l, i, ai[3], 22, 0xc1bdceee);
        i = a(i, j, k, l, ai[4], 7, 0xf57c0faf);
        l = a(l, i, j, k, ai[5], 12, 0x4787c62a);
        k = a(k, l, i, j, ai[6], 17, 0xa8304613);
        j = a(j, k, l, i, ai[7], 22, 0xfd469501);
        i = a(i, j, k, l, ai[8], 7, 0x698098d8);
        l = a(l, i, j, k, ai[9], 12, 0x8b44f7af);
        k = a(k, l, i, j, ai[10], 17, -42063);
        j = a(j, k, l, i, ai[11], 22, 0x895cd7be);
        i = a(i, j, k, l, ai[12], 7, 0x6b901122);
        l = a(l, i, j, k, ai[13], 12, 0xfd987193);
        k = a(k, l, i, j, ai[14], 17, 0xa679438e);
        j = a(j, k, l, i, ai[15], 22, 0x49b40821);
        i = b(i, j, k, l, ai[1], 5, 0xf61e2562);
        l = b(l, i, j, k, ai[6], 9, 0xc040b340);
        k = b(k, l, i, j, ai[11], 14, 0x265e5a51);
        j = b(j, k, l, i, ai[0], 20, 0xe9b6c7aa);
        i = b(i, j, k, l, ai[5], 5, 0xd62f105d);
        l = b(l, i, j, k, ai[10], 9, 0x2441453);
        k = b(k, l, i, j, ai[15], 14, 0xd8a1e681);
        j = b(j, k, l, i, ai[4], 20, 0xe7d3fbc8);
        i = b(i, j, k, l, ai[9], 5, 0x21e1cde6);
        l = b(l, i, j, k, ai[14], 9, 0xc33707d6);
        k = b(k, l, i, j, ai[3], 14, 0xf4d50d87);
        j = b(j, k, l, i, ai[8], 20, 0x455a14ed);
        i = b(i, j, k, l, ai[13], 5, 0xa9e3e905);
        l = b(l, i, j, k, ai[2], 9, 0xfcefa3f8);
        k = b(k, l, i, j, ai[7], 14, 0x676f02d9);
        j = b(j, k, l, i, ai[12], 20, 0x8d2a4c8a);
        i = c(i, j, k, l, ai[5], 4, 0xfffa3942);
        l = c(l, i, j, k, ai[8], 11, 0x8771f681);
        k = c(k, l, i, j, ai[11], 16, 0x6d9d6122);
        j = c(j, k, l, i, ai[14], 23, 0xfde5380c);
        i = c(i, j, k, l, ai[1], 4, 0xa4beea44);
        l = c(l, i, j, k, ai[4], 11, 0x4bdecfa9);
        k = c(k, l, i, j, ai[7], 16, 0xf6bb4b60);
        j = c(j, k, l, i, ai[10], 23, 0xbebfbc70);
        i = c(i, j, k, l, ai[13], 4, 0x289b7ec6);
        l = c(l, i, j, k, ai[0], 11, 0xeaa127fa);
        k = c(k, l, i, j, ai[3], 16, 0xd4ef3085);
        j = c(j, k, l, i, ai[6], 23, 0x4881d05);
        i = c(i, j, k, l, ai[9], 4, 0xd9d4d039);
        l = c(l, i, j, k, ai[12], 11, 0xe6db99e5);
        k = c(k, l, i, j, ai[15], 16, 0x1fa27cf8);
        j = c(j, k, l, i, ai[2], 23, 0xc4ac5665);
        i = d(i, j, k, l, ai[0], 6, 0xf4292244);
        l = d(l, i, j, k, ai[7], 10, 0x432aff97);
        k = d(k, l, i, j, ai[14], 15, 0xab9423a7);
        j = d(j, k, l, i, ai[5], 21, 0xfc93a039);
        i = d(i, j, k, l, ai[12], 6, 0x655b59c3);
        l = d(l, i, j, k, ai[3], 10, 0x8f0ccc92);
        k = d(k, l, i, j, ai[10], 15, 0xffeff47d);
        j = d(j, k, l, i, ai[1], 21, 0x85845dd1);
        i = d(i, j, k, l, ai[8], 6, 0x6fa87e4f);
        l = d(l, i, j, k, ai[15], 10, 0xfe2ce6e0);
        k = d(k, l, i, j, ai[6], 15, 0xa3014314);
        j = d(j, k, l, i, ai[13], 21, 0x4e0811a1);
        i = d(i, j, k, l, ai[4], 6, 0xf7537e82);
        l = d(l, i, j, k, ai[11], 10, 0xbd3af235);
        k = d(k, l, i, j, ai[2], 15, 0x2ad7d2bb);
        j = d(j, k, l, i, ai[9], 21, 0xeb86d391);
        md5state.state[0] += i;
        md5state.state[1] += j;
        md5state.state[2] += k;
        md5state.state[3] += l;
    }

}
