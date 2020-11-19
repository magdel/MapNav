package misc;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public class UTF8InputStreamReaderTest extends TestCase {

    public void testRead() throws IOException {
        String testString = "Some test String ДЛЯ ПРОВЕРКИ";

        byte[] bytes = testString.getBytes("UTF-8");
        UTF8InputStreamReader reader = new UTF8InputStreamReader(new ByteArrayInputStream(bytes));
        char[] chars = new char[testString.length()];
        reader.read(chars);
        String readString = new String(chars);
        Assert.assertEquals(testString, readString);
    }
}