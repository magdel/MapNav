package RPMap;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MapUtilTest extends TestCase {

    public void testDateTime2Str() {
        Assert.assertEquals(MapUtil.dateTime2Str(432342343434L, true), "1983.09.13 23:05:43");
    }

    public void testTime2mmsszzzString() {
        Assert.assertEquals(MapUtil.time2mmsszzzString(34343434L), "09:32:23.434");
    }

    public void testTime2String() {
        Assert.assertEquals(MapUtil.time2String(34343434L), "09:32:23");
    }

    public void testTime2hhmmString() {
        Assert.assertEquals(MapUtil.time2hhmmString(34343434L), "09:32");
    }
}