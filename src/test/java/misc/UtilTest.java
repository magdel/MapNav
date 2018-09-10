package misc;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UtilTest extends TestCase {

    public void testHex2int() {
        Assert.assertEquals(Util.hex2int('B'),11);
    }

    public void testEncodeBase64() {
        Assert.assertEquals(Util.encodeBase64("testBase64"),"dGVzdEJhc2U2NA==");
    }


}