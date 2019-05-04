package net.packets.playerprop;

import net.ServerLogic;
import net.playerhandling.ServerPlayer;
import org.joml.Vector2f;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketVelocity {
    @Test
    public void checkDataNull() {
        PacketVelocity p = new PacketVelocity(null);
        Assert.assertEquals("ERRORS: No position data found.", p.createErrorMessage());
    }

    @Test
    public void checkDataIncorrectPosData() {
        PacketVelocity p = new PacketVelocity(1, "1.0║1.0║1.0║Error");
        Assert.assertEquals("ERRORS: Invalid velocity data.", p.createErrorMessage());
    }

    @Test
    public void checkDataIncorrectPosDataTooShort() {
        PacketVelocity p = new PacketVelocity(1, "1.0║1.0");
        p.processData();
        Assert.assertEquals("ERRORS: Invalid player in velocity data.", p.createErrorMessage());
    }

    @Test
    public void checkDataCorrect() {
        PacketVelocity p = new PacketVelocity(1f,1f,1f,1f);
        Assert.assertEquals("1.0║1.0║1.0║1.0", p.getData());
    }

    @Test
    public void CorrectDataServer() {
        ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
        ServerPlayer player = new ServerPlayer("TestPlayer",1);
        serverLogic.getPlayerList().addPlayer(player);
        PacketVelocity p = new PacketVelocity(1, "1.0║1.0║1.0║1.0");
        p.processData();
        Assert.assertEquals(new Vector2f(), serverLogic.getPlayerList().getPlayer(1).getPos2d());
    }
}
