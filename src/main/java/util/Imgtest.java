package util;

import game.map.ClientMap;
import game.map.ServerMap;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.swing.*;

public class Imgtest {

  public static void main(String[] args) {

    ClientMap map = new ClientMap(10, 10, 50);
    BufferedImage pixelImage = map.getMapImage(0, 0);

    Image tmp = pixelImage.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
    BufferedImage dimg = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = dimg.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();


    System.out.println(dimg.getWidth());

    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new FlowLayout());
    frame.getContentPane().add(new JLabel(new ImageIcon(dimg)));
    frame.pack();
    frame.setVisible(true);

  }

  public static Image getImageFromArray(int[] pixels, int width, int height) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    WritableRaster raster = (WritableRaster) image.getData();
    raster.setPixels(0, 0, width, height, pixels);
    return image;
  }
}
