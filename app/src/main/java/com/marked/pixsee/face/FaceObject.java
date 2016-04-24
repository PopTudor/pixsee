package com.marked.pixsee.face;

import android.support.annotation.NonNull;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.AMeshLoader;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;

/**
 * Created by Tudor on 4/15/2016.
 */
public class FaceObject implements Comparable<FaceObject> {
	private int drawingPosition;
	private Renderer renderer;
	private Material material;
	private Texture texture;
	private Object3D object3D;
	private AMeshLoader loader;
	private int tag;


	public FaceObject(Renderer renderer) {
		this.drawingPosition = 0;
		material = new Material();
		material.enableLighting(true);
		material.setDiffuseMethod(new DiffuseMethod.Lambert());
		material.setColor(0);
//		loader = new Loader3DSMax(renderer,R.)
//			object3D = new Loader3DSMax(renderer, R.raw.rock).parse().getParsedObject();
			object3D =  new Plane(5, 5, 1, 1);
			object3D.setTransparent(true);
	}

	public void setTexture(int resourceID) {
		tag = resourceID;
		texture = new Texture("Texture_"+String.valueOf(tag), resourceID);
		try {
			material.addTexture(texture);
			object3D.setMaterial(material);
		} catch (ATexture.TextureException e) {
			e.printStackTrace();
		}
	}

	public AMeshLoader getLoader() {
		if (loader == null){
			loader = new AMeshLoader("") {
				@Override
				public Object3D getParsedObject() {
					return object3D;
				}
			};
		}
		return loader;
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

	public int getTag() {
		return tag;
	}

	@Override
	public int compareTo(@NonNull FaceObject another) {
		if (tag < another.tag)
			return -1;
		else if (tag > another.tag)
			return 1;
		else
			return 0;
	}
}
