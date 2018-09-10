package camera;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MD5Test extends TestCase {

    public void testMd5ToHex() {
        String hashString = MD5.getHashString("admin");
        Assert.assertEquals(hashString, "21232f297a57a5a743894a0e4a801fc3");
    }

}
