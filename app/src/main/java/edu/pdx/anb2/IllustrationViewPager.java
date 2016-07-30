package edu.pdx.anb2;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IllustrationViewPager extends ViewPager {

    private volatile boolean allowPaging = false;
    private int lastPage = 0;

    public IllustrationViewPager(Context context) {
        super(context);
        setAdapter(new IllustrationImageAdapter(context));
        addOnPageChangeListener(new DisableOnScroll());
    }

    public IllustrationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAdapter(new IllustrationImageAdapter(context));
        addOnPageChangeListener(new DisableOnScroll());
    }

    public boolean matches(String text) {
        return Illustration.ALL[getCurrentItem()].name.equals(text);
    }

    public void enablePaging() {
        Log.d(IllustrationViewPager.class.getSimpleName(), "Enable paging");
        allowPaging = true;
        setBackgroundColor(getResources().getColor(R.color.pagingEnabled));
    }

    public void disablePaging() {
        Log.d(IllustrationViewPager.class.getSimpleName(), "Disable paging");
        allowPaging = false;
        setBackgroundColor(getResources().getColor(R.color.pagingDisabled));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return allowPaging && super.onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return allowPaging && super.onTouchEvent(event);
    }

    class DisableOnScroll implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == ViewPager.SCROLL_STATE_SETTLING){
                disablePaging();
            }
        }
    }

    class IllustrationImageAdapter extends PagerAdapter {
        Context mContext;

        IllustrationImageAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return Illustration.ALL.length;
        }

        @Override
        public boolean isViewFromObject(View v, Object obj) {
            return v == ((ImageView) obj);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            ImageView mImageView = new ImageView(mContext);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImageView.setImageResource(Illustration.ALL[i].image);
            ((ViewPager) container).addView(mImageView, 0);
            return mImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int i, Object obj) {
            ((ViewPager) container).removeView((ImageView) obj);
        }
    }
}
