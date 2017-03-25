package com.app.imagecreator.adapters;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.amapps.imagecreator.R;
import com.app.imagecreator.utility.Utility;

import java.io.File;
import java.io.IOException;

public class ImageViewPagerAdapter extends PagerAdapter

{
    LayoutParams params;
    private Context context;

    LayoutInflater inflater;
    private int[] defaultImgs;
    private String[] productLists;

    public ImageViewPagerAdapter(Context context, String[] list, int[] defaultImgs) {
        this.setProductLists(list);
        this.defaultImgs = defaultImgs;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return productLists.length + (productLists[0] != null ? 1 : 0);
    }

    @Override
    public Object instantiateItem(View collection, final int position) {

        View convertView = inflater.inflate(R.layout.item_view_pager, null);

        ImageView imgViewPager = (ImageView) convertView.findViewById(R.id.imgViewPager);
        if (productLists.length > position && productLists[position] != null) {
            Utility.log("path", productLists[position]);
            File file = new File(productLists[position]);
            try {
                imgViewPager.setImageBitmap(Utility.decodeFile(context, file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imgViewPager.setImageResource(defaultImgs[position - (productLists[0] != null ? 1 : 0)]);
        }
        ((ViewPager) collection).addView(convertView, 0);

        return convertView;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    public String[] getProductLists() {
        return productLists;
    }

    public void setProductLists(String[] productLists) {
        this.productLists = productLists;
    }

}
