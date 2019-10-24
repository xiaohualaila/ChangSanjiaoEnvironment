package com.aier.environment.utils.imagepicker.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.aier.environment.R;
import com.aier.environment.pickerimage.PickerAlbumPreviewActivity;
import com.aier.environment.pickerimage.model.PhotoInfo;
import com.aier.environment.pickerimage.utils.ScreenUtil;
import com.aier.environment.pickerimage.view.BaseZoomableImageView;
import com.aier.environment.pickerimage.view.UIView;

import java.util.List;


public class PickerPreviewPagerAdapter extends PagerAdapter {
	
	private Context mContext;
	private List<PhotoInfo> mList;
	private LayoutInflater mInflater;
	private int viewPagerWidth, viewPagerHeight;
	private UIView mActivity;
	
    public PickerPreviewPagerAdapter(Context cx, List<PhotoInfo> list, LayoutInflater inflater, int width, int height, UIView activity) {
    	mContext = cx;  
        mList = list;
        mInflater = inflater; 
        viewPagerHeight = height;
        viewPagerWidth = width;
        mActivity = activity;
    }  

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
    	View layout = (View)object;
    	BaseZoomableImageView iv = (BaseZoomableImageView)layout.findViewById(R.id.imageView);
    	iv.clear();
        container.removeView(layout);
    }
    
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}
	
    @Override
    public Object instantiateItem (ViewGroup container, int position) {
    	View layout = null;
		if( Build.VERSION.SDK_INT >=  Build.VERSION_CODES.FROYO ){
			layout = mInflater.inflate(R.layout.preview_image_layout_multi_touch, null);
		}else {
			layout = mInflater.inflate(R.layout.preview_image_layout_zoom_control, null);
		}
				
		container.addView(layout);
		layout.setTag(position);
		viewPagerWidth = ScreenUtil.screenWidth;
		viewPagerHeight = ScreenUtil.screenHeight;

        return layout;  
    }
    
	private void finishUpdate() {
		
	}
	
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    
    @Override
    public void setPrimaryItem (View container, int position, Object object) {
    	((PickerAlbumPreviewActivity) mActivity).updateCurrentImageView(position);
    }
}
