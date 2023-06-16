package org.app.core.data;

import glm_.vec2.Vec2i;
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

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memSlice;

public class Texture {
    private ByteBuffer image;

    private int w;
    private int h;
    private int comp;

    public Texture(String file, Vec2i size) {
        ByteBuffer imageBuffer;

        // Read image from specified location
        try {
            imageBuffer = ioResourceToByteBuffer(file, size.getX()*size.getY()*4);
        } catch (IOException e) {
            Logger.logAndThrow("An Error occurred whilst reading in an image", new RuntimeException(e));
            return;
        }

        // Get Metadata and populate Attributes
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Use info to read image metadata without decoding the entire image.
            // We don't need this for this demo, just testing the API.
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                Logger.logAndThrow("Failed to read image information: " + stbi_failure_reason(), RuntimeException.class);
                return;
            }
//            else {
//                System.out.println("OK with reason: " + stbi_failure_reason());
//            }

//            System.out.println("Image width: " + w.get(0));
//            System.out.println("Image height: " + h.get(0));
//            System.out.println("Image components: " + comp.get(0));
//            System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));
            Logger.logDebug("Loaded image from '" + file + "' with dimensions: "
                    + w.get(0) + "x" + h.get(0) + "x" + comp.get(0)
                    + " (HDR=" + stbi_is_hdr_from_memory(imageBuffer) + ") failure=" + stbi_failure_reason());

            // Decode the image
            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (image == null) {
                Logger.logAndThrow("Failed to load image: " + stbi_failure_reason(), RuntimeException.class);
                return;
            }

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
}
