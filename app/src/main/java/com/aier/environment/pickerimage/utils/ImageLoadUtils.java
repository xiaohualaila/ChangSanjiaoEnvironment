package com.aier.environment.pickerimage.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.aier.environment.utils.keyboard.utils.imageloader.ImageBase;
import com.aier.environment.utils.keyboard.utils.imageloader.ImageLoader;
import com.bumptech.glide.Glide;

import java.io.IOException;




public class ImageLoadUtils extends ImageLoader {

    public ImageLoadUtils(Context context) {
        super(context);
    }

    @Override
    protected void displayImageFromFile(String imageUri, ImageView imageView) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        Glide.with(imageView.getContext())
                .load(filePath)
                .asBitmap()
                .into(imageView);
    }

    @Override
    protected void displayImageFromAssets(String imageUri, ImageView imageView) throws IOException {
        String uri = Scheme.cropScheme(imageUri);
        ImageBase.Scheme.ofUri(imageUri).crop(imageUri);
        Glide.with(imageView.getContext())
                .load(Uri.parse("file:///android_asset/" + uri))
                .into(imageView);
    }
}
