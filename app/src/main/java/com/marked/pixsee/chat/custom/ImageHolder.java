package com.marked.pixsee.chat.custom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.ImageFullscreenActivity;

import java.io.File;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class ImageHolder extends RecyclerView.ViewHolder{
	private Context context;

	private SimpleDraweeView mImage;

	public ImageHolder(View itemView, Context context) {
		super(itemView);
		this.context = context;
		this.mImage = (SimpleDraweeView) itemView.findViewById(R.id.imageNetwork);
	}

    public void bindMessage(Message message) {
//		val url = Uri.parse(message.data.get(MessageConstants.DATA_BODY));
		final Uri url = Uri.parse("http://www.online-image-editor.com//styles/2014/images/example_image.png");
		mImage.setImageURI(url,context);
        mImage.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        largeImage(url);
	        }
        });
	}

    private void largeImage(Uri uri){
        ImageRequest imageRequest1= ImageRequest.fromUri(uri);
        CacheKey cacheKey1= DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest1);
        BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey1);
        File file=((FileBinaryResource)resource).getFile();
        Log.i("TAG", file.getAbsolutePath());

	    Intent intent = new Intent(context, ImageFullscreenActivity.class);
	    intent.putExtra("URI", file.getAbsolutePath());
	    context.startActivity(intent);
    }


}
