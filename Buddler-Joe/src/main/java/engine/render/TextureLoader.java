package engine.render;

import engine.textures.Texture;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static java.lang.Math.round;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_SRGB;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static util.IOUtil.ioResourceToByteBuffer;

public class TextureLoader implements Texture {

    private final ByteBuffer image;

    private final int w;
    private final int h;
    private final int comp;
    private int texID;


    private TextureLoader(String imagePath) {
        ByteBuffer imageBuffer;
        try {
            imageBuffer = ioResourceToByteBuffer(imagePath, 8 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer w    = stack.mallocInt(1);
            IntBuffer h    = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Use info to read image metadata without decoding the entire image.
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            } else {
//                System.out.println("OK with reason: " + stbi_failure_reason());
            }

//            System.out.println("Image width: " + w.get(0));
//            System.out.println("Image height: " + h.get(0));
//            System.out.println("Image components: " + comp.get(0));
//            System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

            // Decode the image
            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }

            this.w = w.get(0);
            this.h = h.get(0);
            this.comp = comp.get(0);

            texID = createTexture();
        }
    }

    public static Texture getTexture(String imagePath) {
        return new TextureLoader(imagePath);
    }

    private void premultiplyAlpha() {
        int stride = w * 4;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = y * stride + x * 4;

                float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
                image.put(i + 0, (byte)round(((image.get(i + 0) & 0xFF) * alpha)));
                image.put(i + 1, (byte)round(((image.get(i + 1) & 0xFF) * alpha)));
                image.put(i + 2, (byte)round(((image.get(i + 2) & 0xFF) * alpha)));
            }
        }
    }

    private int createTexture() {
        texID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        int format;
        if (comp == 3) {
            if ((w & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
            }
            format = GL_RGB;
        } else {
            premultiplyAlpha();

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            format = GL_RGBA;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);

        ByteBuffer input_pixels = image;
        int        input_w      = w;
        int        input_h      = h;
        int        mipmapLevel  = 0;
        while (1 < input_w || 1 < input_h) {
            int output_w = Math.max(1, input_w >> 1);
            int output_h = Math.max(1, input_h >> 1);

            ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
            stbir_resize_uint8_generic(
                    input_pixels, input_w, input_h, input_w * comp,
                    output_pixels, output_w, output_h, output_w * comp,
                    comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
                    STBIR_EDGE_CLAMP,
                    STBIR_FILTER_MITCHELL,
                    STBIR_COLORSPACE_SRGB
            );

            if (mipmapLevel == 0) {
                stbi_image_free(image);
            } else {
                memFree(input_pixels);
            }

            glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

            input_pixels = output_pixels;
            input_w = output_w;
            input_h = output_h;
        }
        if (mipmapLevel == 0) {
            stbi_image_free(image);
        } else {
            memFree(input_pixels);
        }

        return texID;
    }

    @Override
    public boolean hasAlpha() {
        return false;
    }

    @Override
    public String getTextureRef() {
        return null;
    }

    @Override
    public void bind() {

    }

    @Override
    public int getImageHeight() {
        return 0;
    }

    @Override
    public int getImageWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return h;
    }

    @Override
    public float getWidth() {
        return w;
    }

    @Override
    public int getTextureHeight() {
        return h;
    }

    @Override
    public int getTextureWidth() {
        return w;
    }

    @Override
    public void release() {

    }

    @Override
    public int getTextureID() {
        return texID;
    }

    @Override
    public byte[] getTextureData() {
        byte[] arr = new byte[image.remaining()];
        image.get(arr);
        return arr;
    }

    @Override
    public void setTextureFilter(int textureFilter) {

    }
}
