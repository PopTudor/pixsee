/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.marked.pixsee.VuforiaApplication.utils;

import android.content.res.AssetManager;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;


public class SampleApplication3DModel extends MeshObject {

	int numVerts = 0;
	private ByteBuffer verts;
	private ByteBuffer textCoords;
	private ByteBuffer norms;

	public void loadModel(AssetManager assetManager, String filename) throws IOException {
//		InputStream objStream = new FileInputStream(filename);
//		InputStream mtlStream = null;
//		Object3D[] model;
//
//		try {
////			objStream = assetManager.open(filename);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(objStream));
////			model = Loader.loadOBJ(objStream, mtlStream, 1);
//
//			String line= reader.readLine();
//
//			int floatsToRead= Integer.parseInt(line);
//			numVerts = floatsToRead / 3;
//
//			verts = ByteBuffer.allocateDirect(floatsToRead * 4);
//			verts.order(ByteOrder.nativeOrder());
//			for (int i = 0; i < floatsToRead; i++) {
//				verts.putFloat(Float.parseFloat(reader.readLine()));
//			}
//			verts.rewind();
//
//			line = reader.readLine();
//			floatsToRead = Integer.parseInt(line);
//
//			norms = ByteBuffer.allocateDirect(floatsToRead * 4);
//			norms.order(ByteOrder.nativeOrder());
//			for (int i = 0; i < floatsToRead; i++) {
//				norms.putFloat(Float.parseFloat(reader.readLine()));
//			}
//			norms.rewind();
//
//			line = reader.readLine();
//			floatsToRead = Integer.parseInt(line);
//
//			textCoords = ByteBuffer.allocateDirect(floatsToRead * 4);
//			textCoords.order(ByteOrder.nativeOrder());
//			for (int i = 0; i < floatsToRead; i++) {
//				textCoords.putFloat(Float.parseFloat(reader.readLine()));
//			}
//			textCoords.rewind();
//
//		} finally {
//			if (objStream != null)
//				objStream.close();
//		}
	}


	@Override
	public Buffer getBuffer(BUFFER_TYPE bufferType) {
		Buffer result = null;
		switch (bufferType) {
			case BUFFER_TYPE_VERTEX:
				result = verts;
				break;
			case BUFFER_TYPE_TEXTURE_COORD:
				result = textCoords;
				break;
			case BUFFER_TYPE_NORMALS:
				result = norms;
			default:
				break;
		}
		return result;
	}


	@Override
	public int getNumObjectVertex() {
		return numVerts;
	}


	@Override
	public int getNumObjectIndex() {
		return 0;
	}

}
