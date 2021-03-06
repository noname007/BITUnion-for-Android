package me.ihainan.bu.app.utils.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import me.ihainan.bu.app.utils.ACache;
import me.ihainan.bu.app.utils.CommonUtils;
import me.ihainan.bu.app.utils.BUApplication;

/**
 * Picasso Image Getter
 */
public class PicassoImageGetter implements Html.ImageGetter {
    private final static String TAG = PicassoImageGetter.class.getSimpleName();
    private final Resources resources;
    private final Context mContext;
    private final TextView textView;

    public PicassoImageGetter(Context context, TextView textView) {
        mContext = context;
        resources = context.getResources();
        this.textView = textView;
    }

    private void loadImage(final String source, final BitmapDrawablePlaceHolder result) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                // 获取图片

                try {
                    Bitmap bitmap;
                    Picasso picasso = Picasso.with(mContext);

                    // 表情，直接从本地获取
                    Log.d(TAG, "loadImage >> 本地图片，直接获取 " + source);
                    if (source.startsWith("file:///android_asset/faces/s")) {
                        return picasso.load(source).resize(CommonUtils.getFontHeight(mContext, BUApplication.fontSize),
                                CommonUtils.getFontHeight(mContext, BUApplication.fontSize)).get();
                    } else if (source.startsWith("file:///android_asset/faces/bz")) {
                        return picasso.load(source).resize(CommonUtils.getFontHeight(mContext, 40),
                                CommonUtils.getFontHeight(mContext, 40)).get();
                    }

                    // 从缓存中获取图片
                    bitmap = BUApplication.getCache(mContext).getAsBitmap(BUApplication.CACHE_POST_INNER_IMAGE + "_" + source);
                    if (bitmap != null) {
                        Log.d(TAG, "loadImage >> 从缓存中获取图片 " + source);
                        return bitmap;
                    }

                    // Wi-Fi 条件或者非省流量条件下加载图片
                    if (CommonUtils.isWifi(mContext) || !BUApplication.saveDataMode) {
                        Log.d(TAG, "loadImage >> 非节省流量模式或者 Wi-Fi 环境，正常下载图片 " + source);
                        bitmap = picasso.load(source).get();

                        // 缓存位图
                        Log.d(TAG, "loadImage >> 缓存图片成功 " + source);
                        BUApplication.getCache(mContext).put(BUApplication.CACHE_POST_INNER_IMAGE + "_" + source, bitmap, BUApplication.INNER_IMAGE_CACHE_DAYS * ACache.TIME_DAY);

                        return bitmap;
                    } else {
                        Log.d(TAG, "loadImage >> 节省流量模式且非 Wi-Fi 环境，不下载图片 " + source);
                        return null;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "getDrawable >> failed to load image for IOException " + source, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                final BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
                int left = source.startsWith("file:///android_asset/faces/") ? 5 : 0;
                // drawable.setBounds(0, 0, textView.getLineHeight(), textView.getLineHeight());
                drawable.setBounds(left, -(int) CommonUtils.convertPixelsToDp(textView.getLineHeight(), mContext) * 2 / 3,
                        drawable.getIntrinsicWidth() + left, drawable.getIntrinsicHeight());
                drawable.setGravity(Gravity.TOP);
                result.setDrawable(drawable);
                // result.setBounds(0, 0, textView.getLineHeight(), textView.getLineHeight());
                result.setBounds(left, -(int) CommonUtils.convertPixelsToDp(textView.getLineHeight(), mContext) * 2 / 3,
                        drawable.getIntrinsicWidth() + left, drawable.getIntrinsicHeight());
                result.setGravity(Gravity.TOP);
                result.setGravity(Gravity.TOP);

                textView.setText(textView.getText());
            }
        }.execute((Void) null);
    }

    @Override
    public Drawable getDrawable(String source) {
        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();

        source = CommonUtils.getRealImageURL(source);

        loadImage(source, result);

        return result;
    }

    public static class BitmapDrawablePlaceHolder extends BitmapDrawable {
        Drawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.mDrawable = drawable;
        }
    }
}
