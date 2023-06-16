package org.app.core.data;

import glm_.vec2.Vec2i;
import glm_.vec4.Vec4;
import org.app.utils.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memSlice;

@SuppressWarnings("unused")
public class Texture {
    private ByteBuffer image;

    private int w;
    private int h;
    private int comp;

    private Vec2i wrap;
    private Vec4 borderColor = new Vec4(0xFF, 0x01, 0xFF, 1.f);
    private Vec2i filter;

    private int texture;

    public Texture(String file, Vec2i size, Vec2i wrap, Vec2i filter) {
        setWrap(wrap);
        setFilter(filter);

        // Read image from specified location
        ByteBuffer imageBuffer;

        try {
            imageBuffer = ioResourceToByteBuffer(file, size.getX()*size.getY()*3);
        } catch (IOException e) {
            Logger.logAndThrow("An Error occurred whilst reading in an image", new RuntimeException(e));
            return;
        }

        // Get Metadata and populate Attributes
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            image = stbi_load(file, w, h, comp, 3);

//            // Use info to read image metadata without decoding the entire image.
//            // We don't need this for this demo, just testing the API.
//            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
//                Logger.logAndThrow("Failed to read image information: " + stbi_failure_reason(), RuntimeException.class);
//                return;
//            }
//
//            Logger.logDebug("Loaded image from '" + file + "' with dimensions: "
//                    + w.get(0) + "x" + h.get(0) + "x" + comp.get(0)
//                    + " (HDR=" + stbi_is_hdr_from_memory(imageBuffer) + ") failure=" + stbi_failure_reason());
//
//            // Decode the image
//            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
//            if (image == null) {
//                Logger.logAndThrow("Failed to load image: " + stbi_failure_reason(), RuntimeException.class);
//                return;
//            }

            this.w = w.get(0);
            this.h = h.get(0);
            this.comp = comp.get(0);
        }
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = resource.startsWith("http") ? null : Paths.get(resource);
        if (path != null && Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1);
            }
        } else {
            try (
                    InputStream source = resource.startsWith("http")
                            ? new URL(resource).openStream()
                            : Texture.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
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
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }

    public void generate(boolean free) {
        texture = glGenTextures();

        setRenderProperties();

        int format;
        if ( comp == 3 ) {
            if ((w & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
            }
            format = GL_RGB;
        } else {

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            format = GL_RGBA;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, format, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);

        if ( free )
            stbi_image_free(image);
    }

    public void setRenderProperties() {
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap.getX());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap.getY());
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor.getArray());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter.getX());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter.getY());
    }

    public ByteBuffer getImage() {
        return image;
    }

    public void setImage(ByteBuffer image) {
        this.image = image;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getComp() {
        return comp;
    }

    public void setComp(int comp) {
        this.comp = comp;
    }

    public Vec2i getWrap() {
        return wrap;
    }

    public void setWrap(Vec2i wrap) {
        // Function to check if texture wrapping mode is valid. If not, changes it to GL_REPEAT
        Function<Integer, Integer> getWrapMode = (wr) -> {
            switch (wr) {
                case GL_REPEAT, GL_MIRRORED_REPEAT, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER -> {
                    return wr;
                }
                default -> {
                    Logger.logError(wr + " is not a valid wrapping mode - using " + GL_REPEAT);
                    return GL_REPEAT;
                }
            }
        };

        // Set wrapping mode
        this.wrap = new Vec2i(getWrapMode.apply(wrap.getX()), getWrapMode.apply(wrap.getY()));
    }

    public Vec4 getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Vec4 borderColor) {
        this.borderColor = borderColor;
    }

    public Vec2i getFilter() {
        return filter;
    }

    public void setFilter(Vec2i filter) {
        // Function to check if texture filter mode is valid. If not, changes it to GL_LINEAR
        Function<Integer, Integer> getFilterMode = (fm) -> {
            switch (fm) {
                case GL_NEAREST, GL_LINEAR, GL_NEAREST_MIPMAP_NEAREST, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST_MIPMAP_LINEAR, GL_LINEAR_MIPMAP_LINEAR -> {
                    return fm;
                }
                default -> {
                    Logger.logError(fm + " is not a valid filter mode - using " + GL_LINEAR);
                    return GL_LINEAR;
                }
            }
        };

        // Set filter mode
        this.filter = new Vec2i(getFilterMode.apply(filter.getX()), getFilterMode.apply(filter.getY()));
    }

    public int getTexture() {
        return texture;
    }
}
