package nullengine.client.rendering.gl.texture;

import nullengine.client.rendering.texture.Texture2D;
import nullengine.client.rendering.texture.Texture2DBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLTexture2D implements Texture2D {

    public static final GLTexture2D EMPTY = new GLTexture2D(0);

    private int id;

    private int level;
    private int internalFormat;

    private int width;
    private int height;

    public static GLTexture2D of(ByteBuffer fileBuffer) throws IOException {
        if (!fileBuffer.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(fileBuffer.capacity());
            direct.put(fileBuffer);
            direct.flip();
            fileBuffer = direct;
        }
        ByteBuffer pixels;
        int width;
        int height;
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            pixels = STBImage.stbi_load_from_memory(fileBuffer, w, h, c, 4);
            width = w.get(0);
            height = h.get(0);
        }

        if (pixels == null) {
            throw new IOException("File buffer cannot be load as pixel buffer by STBImage");
        }
        pixels.flip();
        return of(pixels, width, height);
    }

    public static GLTexture2D of(Texture2DBuffer pixels) {
        return of(pixels.getBuffer(), pixels.getWidth(), pixels.getHeight());
    }

    public static GLTexture2D of(ByteBuffer pixels, int width, int height) {
        if (!pixels.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(pixels.capacity());
            direct.put(pixels);
            pixels = direct;
        }
        return builder().build(pixels, width, height);
    }

    public static Builder builder() {
        return new Builder();
    }

    private GLTexture2D(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void upload(Texture2DBuffer texture) {
        bind();
        glTexImage2D(texture.getBuffer(), texture.getWidth(), texture.getHeight());
    }

    public void upload(ByteBuffer texture, int width, int height) {
        bind();
        glTexImage2D(texture, width, height);
    }

    public void glTexImage2D(ByteBuffer texture, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    }

    public void upload(int offsetX, int offsetY, Texture2DBuffer buffer) {
        bind();
        glTexSubImage2D(offsetX, offsetY, buffer);
    }

    public void glTexSubImage2D(int offsetX, int offsetY, Texture2DBuffer buffer) {
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexSubImage2D(GL_TEXTURE_2D, level, offsetX, offsetY, buffer.getWidth(), buffer.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer.getBuffer());
        glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    }

    @Override
    public void dispose() {
        if (id == 0) return;

        glDeleteTextures(id);
        id = 0;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public float getMinU() {
        return 0;
    }

    @Override
    public float getMinV() {
        return 0;
    }

    @Override
    public float getMaxU() {
        return 1;
    }

    @Override
    public float getMaxV() {
        return 1;
    }

    public static final class Builder {
        private boolean mipmap = false;
        private int level = 0;
        private int internalFormat = GL_SRGB_ALPHA;
        private final Map<Integer, Integer> parameterMap = new HashMap<>();

        private Builder() {
            magFilter(FilterMode.NEAREST);
            minFilter(FilterMode.NEAREST);
            wrapS(WrapMode.REPEAT);
            wrapT(WrapMode.REPEAT);
        }

        public Builder mipmap() {
            mipmap = true;
            return this;
        }

        public Builder magFilter(FilterMode mode) {
            parameterMap.put(GL_TEXTURE_MAG_FILTER, mode.gl);
            return this;
        }

        public Builder minFilter(FilterMode mode) {
            parameterMap.put(GL_TEXTURE_MIN_FILTER, mode.gl);
            return this;
        }

        public Builder wrapS(WrapMode mode) {
            parameterMap.put(GL_TEXTURE_WRAP_S, mode.gl);
            return this;
        }

        public Builder wrapT(WrapMode mode) {
            parameterMap.put(GL_TEXTURE_WRAP_T, mode.gl);
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder internalFormat(int internalFormat) {
            this.internalFormat = internalFormat;
            return this;
        }

        public GLTexture2D build() {
            return build(null, 0, 0);
        }

        public GLTexture2D build(Texture2DBuffer texture) {
            return build(texture.getBuffer(), texture.getWidth(), texture.getHeight());
        }

        public GLTexture2D build(ByteBuffer texture, int width, int height) {
            GLTexture2D glTexture2D = new GLTexture2D(glGenTextures());
            glTexture2D.level = level;
            glTexture2D.internalFormat = internalFormat;
            glTexture2D.bind();

            parameterMap.forEach((key, value) -> glTexParameteri(GL_TEXTURE_2D, key, value));

            if (mipmap) {
                glGenerateMipmap(GL_TEXTURE_2D);
            }

            if (texture != null) {
                glTexture2D.glTexImage2D(texture, width, height);
            }
            return glTexture2D;
        }
    }
}