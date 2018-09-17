package RPMap;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

public class FileMapRouteLoaderTest extends TestCase {

    public void testLoadWpt() {
        TestMapRouteLoader loader = new TestMapRouteLoader("points20070217.wpt", MapRoute.WAYPOINTSKIND, MapRouteLoader.CODEPAGEUTF, MapRouteLoader.FORMATOZI);
        loader.load();

        Assert.assertEquals(loader.getRoute().pts.size(), 1914);
        Assert.assertEquals(loader.getRoute().getActpts(), 0);
        Assert.assertEquals(((MapPoint) (loader.getRoute().pts.elementAt(0))).name, "Podvig nerezidenta ot Hudson");

    }

    public void testLoadGpx() {
        TestMapRouteLoader loader = new TestMapRouteLoader("track_1768.gpx", MapRoute.TRACKKIND, MapRouteLoader.CODEPAGEUTF, MapRouteLoader.FORMATGPX);
        loader.load();

        Assert.assertEquals(loader.getRoute().pts.size(), 370);
        Assert.assertEquals(loader.getRoute().getActpts(), 0);
        Assert.assertEquals(loader.getRoute().distance(), 4.1477, 0.01);

    }


    static class TestMapRouteLoader extends MapRouteLoader {
        public TestMapRouteLoader(String url, byte kind, byte CP, byte format) {
            super(url, kind, CP, format);
        }

        void load() {
            InputStream is = null;
            try {
                try {
                    is = this.getClass().getResourceAsStream(furl);
                    loadFromStream(is);

                } finally {
                    try {
                        Thread.sleep(500);
                    } catch (Throwable re) {
                    }
                    is.close();
                }
            } catch (IOException i1oe) {
            }
        }
    }
}