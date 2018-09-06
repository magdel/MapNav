import RPMap.MapRoute;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class TestTest {
    @Test
    public void testOne(){
        MapRoute mapRoute = new MapRoute((byte) 1);
        Assert.assertThat(mapRoute.kind, is((byte)1));
    }
}
