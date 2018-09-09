package RPMap;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MapPointTest extends TestCase {
    public void testToHexString() {
        MapPoint mapPoint = new MapPoint(1d, 2d, 3, 4L);
        Assert.assertTrue(mapPoint.name == null);
    }

}