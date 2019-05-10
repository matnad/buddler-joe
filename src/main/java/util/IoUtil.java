package util;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import static org.lwjgl.BufferUtils.createByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import org.lwjgl.BufferUtils;

/** Official Loader for textures from LWJGL. */
public final class IoUtil {

  private IoUtil() {}

  /**
   * Resize a Byte Buffer.
   *
   * @param buffer buffer to resize
   * @param newCapacity new size
   * @return resized buffer
   */
  public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }

  /**
   * Reads the specified resource and returns the raw data as a ByteBuffer.
   *
   * @param resource the resource to read
   * @param bufferSize the initial buffer size
   * @return the resource data
   */
  public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) {
    ByteBuffer buffer = null;

    try {
      InputStream source = IoUtil.class.getResourceAsStream(resource);
      ReadableByteChannel rbc = Channels.newChannel(Objects.requireNonNull(source));
      buffer = createByteBuffer(bufferSize);
      while (true) {
        int bytes = rbc.read(buffer);
        if (bytes == -1) {
          break;
        }
        if (buffer.remaining() == 0) {
          buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    buffer.flip();
    return buffer.slice();
  }

}
