package com.marked.pixsee.face;

import android.util.Log;

import com.marked.pixsee.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;

/**
 * Created by Tudor on 4/15/2016.
 */
public class FaceObject {
    private int drawingPosition;
    private Renderer renderer;
    private Material material;
    private Object3D object3D;

    public FaceObject(int drawingPosition) {
        this.drawingPosition = drawingPosition;
        material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(0);

        try {
            Texture mlgTexture = new Texture("mlg", R.drawable.mlg);
            material.addTexture(mlgTexture);
//                object3D = new Loader3DSMax(renderer,R.raw.mlg).parse().getParsedObject();
            object3D = new Plane(5, 5, 1, 1);
            object3D.setTransparent(true);
            object3D.setMaterial(material);
        } catch (ATexture.TextureException error) {
            Log.d("DEBUG", "TEXTURE ERROR");
        }
    }

    public int getDrawingPosition() {
        return drawingPosition;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Material getMaterial() {
        return material;
    }

    public Object3D getObject3D() {
        return object3D;
    }
}
