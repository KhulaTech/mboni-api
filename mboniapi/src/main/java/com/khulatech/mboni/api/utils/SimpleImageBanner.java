package com.khulatech.mboni.api.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.banner.widget.Banner.BaseIndicatorBanner;
import com.khulatech.mboni.api.R;

/**
 * Created by jsetangni on 10/12/16.
 */
public class SimpleImageBanner extends BaseIndicatorBanner<BannerItem, SimpleImageBanner> {

    private ColorDrawable colorDrawable;
    private OnImageClickCallback mOnImageCallBack;

    public SimpleImageBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        colorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }

    public SimpleImageBanner setOnImageClickCallback(OnImageClickCallback callback){
        mOnImageCallBack = callback;
        return this;
    }

    @Override
    public void onTitleSlect(TextView tv, int position) {
        final BannerItem item = mDatas.get(position);
        tv.setText(item.title);
    }

    @Override
    public View onCreateItemView(final int position) {
        View inflate = View.inflate(mContext, R.layout.adapter_simple_image, null);
//        ImageView iv = (ImageView) inflate.findViewById(R.id.iv);
        SimpleDraweeView iv = (SimpleDraweeView) inflate.findViewById(R.id.iv);

        final BannerItem item = mDatas.get(position);
        int itemWidth = mDisplayMetrics.widthPixels;
        int itemHeight = (int) (itemWidth * 360 * 1.0f / 640);
        iv.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));

        if(mOnImageCallBack!=null){
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnImageCallBack.onImageClick(position);
                }
            });
        }

        String imgUrl = item.imgUrl;

        if (!TextUtils.isEmpty(imgUrl)) {
//            Picasso.with(mContext)
//                    .load(imgUrl)
//                    .into(iv);
            try {
                iv.setImageURI(Uri.parse(imgUrl));
            }catch (Exception ex){

            }
        } else {
            iv.setImageDrawable(colorDrawable);
        }

        return inflate;
    }

    public interface OnImageClickCallback{
        void onImageClick(int position);
    }
}
