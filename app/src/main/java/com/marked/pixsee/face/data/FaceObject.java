package com.marked.pixsee.face.data;

import android.content.Context;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.AMeshLoader;
import org.rajawali3d.loader.Loader3DSMax;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.loader.LoaderGCode;
import org.rajawali3d.loader.LoaderMD2;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.LoaderSTL;
import org.rajawali3d.loader.fbx.LoaderFBX;
import org.rajawali3d.loader.md5.LoaderMD5Anim;
import org.rajawali3d.loader.md5.LoaderMD5Mesh;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ASingleTexture;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AnimatedGIFTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;

import java.io.File;
import java.util.Random;

/**
 * Created by Tudor on 4/15/2016.
 */
public class FaceObject {
	private final int drawingPosition;
	private final Material material;
	private final ASingleTexture texture;
	private final boolean isAnimated;
	private final Object3D object3D;
	private final ALoader loader;
	private final int textureId,resId;


	private FaceObject(FaceBuilder faceBuilder) {
		drawingPosition = faceBuilder.position;
		material = faceBuilder.material;
		texture = faceBuilder.texture;
		isAnimated = faceBuilder.isAnimated;
		object3D = faceBuilder.object3D;
		loader = faceBuilder.aLoader;
		textureId = faceBuilder.textureId;
		resId = faceBuilder.resId;
	}

	public ALoader getLoader() {
		return loader;
	}

	public int getResId() {
		return resId;
	}

	public interface ObjectType {
		int THREE_DS_MAX = 0; /* 3DS Max*/
		int AWD = 1; /* awd */
		int FBX = 2; /* fbx */
		int GCODE = 3; /* gcode */
		int MD2 = 4; /* md2 */
		int MD5_MESH = 5; /* md5 */
		int OBJ = 6; /* obj */
		int STL = 7; /* stl */
	}

	public static class FaceBuilder {
		private static final String TAG = "FACE_BUILDER";
		private FaceObject faceObject;
		private Material material;
		private int position;
		private ASingleTexture texture;
		private boolean isAnimated;
		private Object3D object3D;
		private ALoader aLoader;
		private int resId = -1;
		private int objectType = -1;
		private Renderer renderer;
		private File objectFile;
		private Context context;
		private int textureSize, textureId;

		public FaceBuilder(Context context) {
			this.context = context;
		}

		public FaceBuilder withMaterial(Material material) {
			this.material = material;
			return this;
		}

		public FaceBuilder withTexture(ASingleTexture texture,boolean isAnimated) {
			withTexture(texture, isAnimated, 256);
			return this;
		}

		public FaceBuilder withTexture(ASingleTexture texture,boolean isAnimated,int size) {
			this.texture = texture;
			this.isAnimated = isAnimated;
			this.textureSize = size;
			return this;
		}

		public FaceBuilder withResId(int resId) {
			this.resId = resId;
			return this;
		}
		public FaceBuilder withTextureId(int textureId) {
			this.textureId = textureId;
			return this;
		}

		public FaceBuilder withObjectType(int objectType) {
			this.objectType = objectType;
			return this;
		}

		public FaceBuilder withRenderer(Renderer renderer) {
			this.renderer = renderer;
			return this;
		}
		public FaceBuilder withDrawingPosition(int position) {
			this.position = position;
			return this;
		}

		public FaceBuilder withObjectFile(File objectFile) {
			this.objectFile = objectFile;
			return this;
		}
		public FaceObject build() {
			if (resId != -1)
				buildResLoader();
			else
				buildFileLoader();

			buildTexture();
			buildMaterial();

			faceObject = new FaceObject(this);
			return faceObject;
		}

		private void buildMaterial() {
			if (material == null) {
				this.material = new Material();
				this.material.enableLighting(true);
				this.material.setDiffuseMethod(new DiffuseMethod.Lambert());
				this.material.setColor(0);
				this.material.setColorInfluence(0);
			}
			try {
				this.material.addTexture(texture);
			} catch (ATexture.TextureException e) {
				e.printStackTrace();
			}
		}

		private void buildTexture() {
			String texName = "Texture_" + String.valueOf(this.textureId);
			if (this.isAnimated) { /* GIF */
				this.texture = new AnimatedGIFTexture(texName, this.textureId, this.textureSize);
				((AnimatedGIFTexture) this.texture).rewind();
			} else
				this.texture = new Texture(texName, this.textureId);
		}
		private void buildResLoader() {
			switch (objectType) {
				case ObjectType.THREE_DS_MAX:
					aLoader = new Loader3DSMax(renderer, resId);
					break;
				case ObjectType.AWD:
					aLoader = new LoaderAWD(context.getResources(), TextureManager.getInstance(), resId);
					break;
				case ObjectType.FBX:
					aLoader = new LoaderFBX(renderer, resId);
					break;
				case ObjectType.GCODE:
					aLoader = new LoaderGCode(context.getResources(), TextureManager.getInstance(), resId);
					break;
				case ObjectType.MD2:
					aLoader = new LoaderMD2(context.getResources(), TextureManager.getInstance(), resId);
					break;
				case ObjectType.OBJ:
					aLoader = new LoaderOBJ(context.getResources(), TextureManager.getInstance(), resId);
					break;
				case ObjectType.MD5_MESH:
					if (isAnimated)
						aLoader = new LoaderMD5Anim(String.valueOf(resId), renderer, resId);
					else
						aLoader = new LoaderMD5Mesh(renderer, resId);
					break;
				case ObjectType.STL:
					aLoader = new LoaderSTL(context.getResources(), TextureManager.getInstance(), resId);
					break;
				default:
					aLoader = new AMeshLoader("") {
						@Override
						public Object3D getParsedObject() {
							object3D = new Plane(5, 5, 1, 1);
							object3D.setTransparent(true);
							object3D.setMaterial(material);
							return object3D;
						}
					};
					break;
			}
		}

		private void buildFileLoader() {
			Random random = new Random((long) (Math.random() * 10000));
			resId = random.nextInt();
			switch (objectType) {
				case ObjectType.THREE_DS_MAX:
					aLoader = new Loader3DSMax(renderer, objectFile);
					break;
				case ObjectType.AWD:
					aLoader = new LoaderAWD(renderer, objectFile);
					break;
				case ObjectType.FBX:
					aLoader = new LoaderFBX(renderer, objectFile);
					break;
				case ObjectType.GCODE:
					aLoader = new LoaderGCode(renderer, objectFile);
					break;
				case ObjectType.MD2:
					aLoader = new LoaderMD2(renderer, objectFile);
					break;
				case ObjectType.OBJ:
					aLoader = new LoaderOBJ(renderer, objectFile);
					break;
				case ObjectType.MD5_MESH:
					if (isAnimated)
						aLoader = new LoaderMD5Anim(String.valueOf(resId), renderer, objectFile.getPath());
					else
						aLoader = new LoaderMD5Mesh(renderer, objectFile.getPath());
					break;
				case ObjectType.STL:
					aLoader = new LoaderSTL(renderer, objectFile);
					break;
				default:
					aLoader = new AMeshLoader("") {
						@Override
						public Object3D getParsedObject() {
							object3D = new Plane(5, 5, 1, 1);
							object3D.setTransparent(true);
							object3D.setMaterial(material);

							return object3D;
						}
					};
					break;
			}
			resId = -1;
		}
	}

	public int getDrawingPosition() {
		return drawingPosition;
	}

	public Material getMaterial() {
		return material;
	}

	public Object3D getObject3D() {
		return object3D;
	}

}
