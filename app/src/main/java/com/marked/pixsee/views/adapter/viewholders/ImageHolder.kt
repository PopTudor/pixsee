package com.marked.pixsee.views.adapter.viewholders;

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.request.ImageRequest
import com.marked.pixsee.R
import com.marked.pixsee.model.message.Message
import com.marked.pixsee.activity.chat.ImageFullscreenActivity
import org.jetbrains.anko.onClick

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class ImageHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView){
	private val mImage = itemView.findViewById(R.id.imageNetwork) as SimpleDraweeView


    fun bindMessage(message: Message) {
//		val url = Uri.parse(message.data.get(MessageConstants.DATA_BODY));
		val url = Uri.parse("http://www.online-image-editor.com//styles/2014/images/example_image.png");
		mImage.setImageURI(url,context);
        mImage.onClick { largeImage(url) }
	}

    fun largeImage(uri: Uri){
        val imageRequest1= ImageRequest.fromUri(uri);
        val cacheKey1= DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest1);
        val resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey1);
        val file=(resource as FileBinaryResource).getFile();
        Log.i("TAG", file.getAbsolutePath());

        val intent = Intent(context, ImageFullscreenActivity::class.java)
        intent.putExtra("URI",file.absolutePath)
        context.startActivity(intent)
    }


}
