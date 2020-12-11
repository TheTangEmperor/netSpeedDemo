package cn.sm.framework.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

/**
 * Created by lishiming on 17/4/19.
 */

public class GlideLoader implements MyLoader {

    @Override
    public void loadNet(ImageView target, String url, Options options) {
        loadImg(getRequest(target.getContext()).load(url),target,options);
    }

    @Override
    public void loadNet(ImageView target, String url) {
        loadImg(getRequest(target.getContext()).load(url),target,Options.defOption());
    }

    @Override
    public void loadNetBitmap(ImageView target, String url, Options options) {
        loadBitmapImg(getRequest(target.getContext()).asBitmap().load(url),target,options);
    }

    @Override
    public void loadResource(ImageView target, int resId, Options options) {
        loadImg(getRequest(target.getContext()).load(resId),target,options);
    }

    @Override
    public void loadAssets(ImageView target, String assetName, Options options) {
        loadImg(getRequest(target.getContext()).load("file:///android_asset/" + assetName),target,options);
    }

    @Override
    public void loadFile(ImageView target, File file, Options options) {
        loadImg(getRequest(target.getContext()).load(file),target,options);
    }

    @Override
    public void loadFileBitmap(ImageView target, File file, Options options) {
        loadBitmapImg(getRequest(target.getContext()).asBitmap().load(file),target,options);
    }

    @Override
    public void loadNetDrawable(Context context, String url, SimpleTarget<Drawable> target) {
        getRequest(context).asDrawable().load(url).into(target);
    }

    @Override
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    @Override
    public void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    private RequestManager getRequest(Context context){
//        RequestManager with = Glide.with(context);
//        RequestBuilder<Drawable> load = with.load("");
//        RequestBuilder<Bitmap> asBitmap = with.asBitmap();

        return Glide.with(context);
    }



    private void loadImg(RequestBuilder<Drawable> request , ImageView target , Options options){
        if (options == null) options = Options.defOption();
        request.apply(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
//        if (options.loadingResId != Options.NO_RES){
//            request.placeholder(options.loadingResId);
//            request.
//        }
//        if (options.loadErrorResId != Options.NO_RES){
//            request.error(options.loadErrorResId);
//        }
        request.into(target);
    }

    private void loadBitmapImg(RequestBuilder<Bitmap> request , ImageView target , Options options){
        if (options == null) options = Options.defOption();
        request.apply(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
//        if (options.loadingResId != Options.NO_RES)
//            request.placeholder(options.loadingResId);
//        if (options.loadErrorResId != Options.NO_RES)
//            request.error(options.loadErrorResId);
//        request.crossFade().into(target);
        request.into(target);
    }
}
