package util;

import org.junit.Assert;
import org.junit.Test;

public class TestUtil {

    @Test
    public void checkMilisToString() {
        Util util = new Util();
    Assert.assertEquals(util.milisToString(654321), "10:54.321");
    }
}
