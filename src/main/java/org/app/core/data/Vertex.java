package org.app.core.data;

import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import org.app.utils.Logger;

@SuppressWarnings("unused")
public class Vertex {
    private Vec3 translation;
    private Vec3 color;
    private Vec2 uv;

    public Vertex(Vec3 translation, Vec3 color, Vec2 uv) {
        this.translation = translation;
        this.color = color;
        this.uv = uv;
    }
    public Vertex(float[] data)
    {
        float[] temp;
        // Check if the data supplied is satisfactory
        if ( data.length != getDataSize() ) {
            // If data supplied isn't satisfactory, fill empty spaces with 0.0f and discard aditional data
            Logger.logError("Wrong amt of vertex data supplied. Expected " + getDataSize()
                    + " - received " + data.length);
            temp = new float[getDataSize()];
            for( int i = 0; i < temp.length; i++ ) {
                if( i >= data.length )
                    temp[i] = .0f;
                else
                    temp[i] = data[i];
            }
        }
        else
            // If data supplied is satisfactory, the array can be used directly
            temp = data;

        // Extract translation, color and uv from data array
        this.translation = new Vec3(temp, 0);
        this.color = new Vec3(temp, 3);
        this.uv = new Vec2(temp, 6);
    }

    public float[] getFloats()
    {
        return ArrayUtils.addAll(translation.getArray(), color.getArray());
    }

    /**
     * Returns the amount of floats taken up by a vertex
     * @return amt of floats taken up by vertex
     */
    public static int getDataSize()
    {
        return 8;
    }

    public Vec3 getColor() {
        return color;
    }

    public void setColor(Vec3 color) {
        this.color = color;
    }

    public Vec3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vec3 translation) {
        this.translation = translation;
    }

    public Vec2 getUv() {
        return uv;
    }

    public void setUv(Vec2 uv) {
        this.uv = uv;
    }
}
