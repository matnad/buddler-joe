package net.packets;

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

public class TestPacket {

    @Mock
    private Packet p;

    @Test
    public void checkUsernameTooLong() {
        p = mock(Packet.class);
        Assert.assertFalse(p.checkUsername("My name is Buddle Joe and I am great!"));
    }

    @Test
    public void checkUsernameCorrect() {
        p = mock(Packet.class);
        Assert.assertTrue(p.checkUsername("Joe Buddler"));
    }

    @Test
    public void checkUsernameTooShort() {
        p = mock(Packet.class);
        Assert.assertFalse(p.checkUsername("tes"));
    }

    @Test
    public void checkUsernameNull() {
        p = mock(Packet.class);
        Assert.assertFalse(p.checkUsername(null));
    }

    @Test
    public void checkIsAsciiTrue() {
        p = mock(Packet.class);
        Assert.assertTrue(p.isExtendedAscii("TestData"));
    }

    @Test
    public void checkIsAsciiFalse() {
        p = mock(Packet.class);
        Assert.assertFalse(p.isExtendedAscii("║║║"));
    }

    @Test
    public void checkIsIntTrue() {
        p = mock(Packet.class);
        Assert.assertTrue(p.isInt("10"));
    }

    @Test
    public void checkIsIntFalse() {
        p = mock(Packet.class);
        Assert.assertFalse(p.isInt("10B"));
    }



}
