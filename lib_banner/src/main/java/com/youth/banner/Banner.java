package com.youth.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.Indicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;
import com.youth.banner.transformer.MZScaleInTransformer;
import com.youth.banner.transformer.ScaleInTransformer;
import com.youth.banner.util.BannerLifecycleObserverAdapter;
import com.youth.banner.util.BannerUtils;
import com.youth.banner.util.BannerLifecycleObserver;
import com.youth.banner.util.LogUtils;
import com.youth.banner.util.ScrollSpeedManger;

import java.lang.annotation.Retention;
import java.lang.ref.WeakReference;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;


public class Banner<T, BA extends BannerAdapter<T, ? extends RecyclerView.ViewHolder>> extends FrameLayout implements BannerLifecycleObserver {
    public static final int INVALID_VALUE = -1;
    private ViewPager2 mViewPager2;
    private AutoLoopTask mLoopTask;
    private OnPageChangeListener mOnPageChangeListener;
    private BA mAdapter;
    private Indicator mIndicator;
    private CompositePageTransformer mCompositePageTransformer;
    private BannerOnPageChangeCallback mPageChangeCallback;

    // ???????????????????????????????????????????????????
    private boolean mIsInfiniteLoop = BannerConfig.IS_INFINITE_LOOP;
    // ??????????????????
    private boolean mIsAutoLoop = BannerConfig.IS_AUTO_LOOP;
    // ????????????????????????
    private long mLoopTime = BannerConfig.LOOP_TIME;
    // ??????????????????
    private int mScrollTime = BannerConfig.SCROLL_TIME;
    // ??????????????????
    private int mStartPosition = 1;
    // banner?????????????????????????????????
    private float mBannerRadius = 0;
    // banner?????????????????????????????????????????????????????????????????????
    private boolean mRoundTopLeft, mRoundTopRight, mRoundBottomLeft, mRoundBottomRight;

    // ?????????????????????
    private int normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH;
    private int selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH;
    private int normalColor = BannerConfig.INDICATOR_NORMAL_COLOR;
    private int selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR;
    private int indicatorGravity = IndicatorConfig.Direction.CENTER;
    private int indicatorSpace;
    private int indicatorMargin;
    private int indicatorMarginLeft;
    private int indicatorMarginTop;
    private int indicatorMarginRight;
    private int indicatorMarginBottom;
    private int indicatorHeight = BannerConfig.INDICATOR_HEIGHT;
    private int indicatorRadius = BannerConfig.INDICATOR_RADIUS;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mOrientation = HORIZONTAL;

    // ??????????????????
    private int mTouchSlop;
    // ???????????????????????????????????????????????????????????????
    private float mStartX, mStartY;
    // ??????viewpager2???????????????
    private boolean mIsViewPager2Drag;
    // ?????????????????????
    private boolean isIntercept = true;

    //??????????????????
    private Paint mRoundPaint;
    private Paint mImagePaint;

    @Retention(SOURCE)
    @IntDef( {HORIZONTAL, VERTICAL})
    public @interface Orientation {
    }

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initTypedArray(context, attrs);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() / 2;
        mCompositePageTransformer = new CompositePageTransformer();
        mPageChangeCallback = new BannerOnPageChangeCallback();
        mLoopTask = new AutoLoopTask(this);
        mViewPager2 = new ViewPager2(context);
        mViewPager2.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mViewPager2.setOffscreenPageLimit(2);
        mViewPager2.registerOnPageChangeCallback(mPageChangeCallback);
        mViewPager2.setPageTransformer(mCompositePageTransformer);
        ScrollSpeedManger.reflectLayoutManager(this);
        addView(mViewPager2);

        mRoundPaint = new Paint();
        mRoundPaint.setColor(Color.WHITE);
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setStyle(Paint.Style.FILL);
        mRoundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mImagePaint = new Paint();
        mImagePaint.setXfermode(null);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Banner);
            mBannerRadius = a.getDimensionPixelSize(R.styleable.Banner_banner_radius, 0);
            mLoopTime = a.getInt(R.styleable.Banner_banner_loop_time, BannerConfig.LOOP_TIME);
            mIsAutoLoop = a.getBoolean(R.styleable.Banner_banner_auto_loop, BannerConfig.IS_AUTO_LOOP);
            mIsInfiniteLoop = a.getBoolean(R.styleable.Banner_banner_infinite_loop, BannerConfig.IS_INFINITE_LOOP);
            normalWidth = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_normal_width, BannerConfig.INDICATOR_NORMAL_WIDTH);
            selectedWidth = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_selected_width, BannerConfig.INDICATOR_SELECTED_WIDTH);
            normalColor = a.getColor(R.styleable.Banner_banner_indicator_normal_color, BannerConfig.INDICATOR_NORMAL_COLOR);
            selectedColor = a.getColor(R.styleable.Banner_banner_indicator_selected_color, BannerConfig.INDICATOR_SELECTED_COLOR);
            indicatorGravity = a.getInt(R.styleable.Banner_banner_indicator_gravity, IndicatorConfig.Direction.CENTER);
            indicatorSpace = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_space, 0);
            indicatorMargin = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_margin, 0);
            indicatorMarginLeft = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginLeft, 0);
            indicatorMarginTop = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginTop, 0);
            indicatorMarginRight = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginRight, 0);
            indicatorMarginBottom = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginBottom, 0);
            indicatorHeight = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_height, BannerConfig.INDICATOR_HEIGHT);
            indicatorRadius = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_radius, BannerConfig.INDICATOR_RADIUS);
            mOrientation = a.getInt(R.styleable.Banner_banner_orientation, HORIZONTAL);
            mRoundTopLeft = a.getBoolean(R.styleable.Banner_banner_round_top_left, false);
            mRoundTopRight = a.getBoolean(R.styleable.Banner_banner_round_top_right, false);
            mRoundBottomLeft = a.getBoolean(R.styleable.Banner_banner_round_bottom_left, false);
            mRoundBottomRight = a.getBoolean(R.styleable.Banner_banner_round_bottom_right, false);
            a.recycle();
        }
        setOrientation(mOrientation);
        setInfiniteLoop();
    }

    private void initIndicatorAttr() {
        if (indicatorMargin != 0) {
            setIndicatorMargins(new IndicatorConfig.Margins(indicatorMargin));
        } else if (indicatorMarginLeft != 0
                || indicatorMarginTop != 0
                || indicatorMarginRight != 0
                || indicatorMarginBottom != 0) {
            setIndicatorMargins(new IndicatorConfig.Margins(
                    indicatorMarginLeft,
                    indicatorMarginTop,
                    indicatorMarginRight,
                    indicatorMarginBottom));
        }
        if (indicatorSpace > 0) {
            setIndicatorSpace(indicatorSpace);
        }
        if (indicatorGravity != IndicatorConfig.Direction.CENTER) {
            setIndicatorGravity(indicatorGravity);
        }
        if (normalWidth > 0) {
            setIndicatorNormalWidth(normalWidth);
        }
        if (selectedWidth > 0) {
            setIndicatorSelectedWidth(selectedWidth);
        }

        if (indicatorHeight > 0) {
            setIndicatorHeight(indicatorHeight);
        }
        if (indicatorRadius > 0) {
            setIndicatorRadius(indicatorRadius);
        }
        setIndicatorNormalColor(normalColor);
        setIndicatorSelectedColor(selectedColor);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (!getViewPager2().isUserInputEnabled()) {
//            return super.dispatchTouchEvent(event);
//        }

//        int action = ev.getActionMasked();
//        if (action == MotionEvent.ACTION_UP
//                || action == MotionEvent.ACTION_CANCEL
//                || action == MotionEvent.ACTION_OUTSIDE) {
//            start();
//        } else if (action == MotionEvent.ACTION_DOWN) {
//            stop();
//        }

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mStartX = event.getX();
//                mStartY = event.getY();
//                getViewPager2().setUserInputEnabled(false);
////                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float endX = event.getX();
//                float endY = event.getY();
//                float distanceX = Math.abs(endX - mStartX);
//                float distanceY = Math.abs(endY - mStartY);
//                if (getViewPager2().getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
//                    Log.e("bannner","onInterceptTouchEvent distanceX ="+ distanceX +" , distanceY ="+distanceY +" , mTouchSlop ="+mTouchSlop);
//                    mIsViewPager2Drag =  distanceX > mTouchSlop  && distanceX > distanceY;
//                    getViewPager2().setUserInputEnabled(mIsViewPager2Drag);
//                    Log.e("bannner","onInterceptTouchEvent mIsViewPager2Drag ="+ mIsViewPager2Drag);
//                } else {
//                    mIsViewPager2Drag = distanceY > mTouchSlop && distanceY > distanceX;
//                    getViewPager2().setUserInputEnabled(mIsViewPager2Drag);
//                }
////                getParent().requestDisallowInterceptTouchEvent(mIsViewPager2Drag);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                getViewPager2().setUserInputEnabled(true);
////                getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mStartX = event.getX();
//                mStartY = event.getY();
//                getViewPager2().setUserInputEnabled(true);
////                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float endX = event.getX();
//                float endY = event.getY();
//                float distanceX = Math.abs(endX - mStartX);
//                float distanceY = Math.abs(endY - mStartY);
//                if (getViewPager2().getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
//                    Log.e("bannner","onInterceptTouchEvent distanceX ="+ distanceX +" , distanceY ="+distanceY +" , mTouchSlop ="+mTouchSlop);
//                    mIsViewPager2Drag =  distanceX > mTouchSlop  && distanceX > distanceY;
//                    getViewPager2().setUserInputEnabled(mIsViewPager2Drag);
//                    Log.e("bannner","onInterceptTouchEvent mIsViewPager2Drag ="+ mIsViewPager2Drag);
//                } else {
//                    mIsViewPager2Drag = distanceY > mTouchSlop && distanceY > distanceX;
//                    getViewPager2().setUserInputEnabled(mIsViewPager2Drag);
//                }
////                getParent().requestDisallowInterceptTouchEvent(mIsViewPager2Drag);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                getViewPager2().setUserInputEnabled(true);
////                getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }


        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        if (!getViewPager2().isUserInputEnabled()) {
//            return super.onInterceptTouchEvent(event);
//        }
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mStartX = event.getX();
//                mStartY = event.getY();
////                getParent().requestDisallowInterceptTouchEvent(true);
//
//                return super.onInterceptTouchEvent(event);
//            case MotionEvent.ACTION_MOVE:
//                float endX = event.getX();
//                float endY = event.getY();
//                float distanceX = Math.abs(endX - mStartX);
//                float distanceY = Math.abs(endY - mStartY);
//                if (getViewPager2().getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
//                    Log.e("bannner","onInterceptTouchEvent distanceX ="+ distanceX +" , distanceY ="+distanceY +" , mTouchSlop ="+mTouchSlop);
//                    if (distanceY > distanceX) {
//                        return true;
//                    } else {
//                        return false;
//                    }
////                    mIsViewPager2Drag =  distanceX > mTouchSlop  && distanceX > distanceY;
////                    getViewPager2().setUserInputEnabled(mIsViewPager2Drag);
////                    Log.e("bannner","onInterceptTouchEvent mIsViewPager2Drag ="+ mIsViewPager2Drag);
//                } else {
//                    mIsViewPager2Drag = distanceY > mTouchSlop && distanceY > distanceX;
//                }
////                getParent().requestDisallowInterceptTouchEvent(mIsViewPager2Drag);
//                return super.onInterceptTouchEvent(event);
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
////                getParent().requestDisallowInterceptTouchEvent(false);
//                return super.onInterceptTouchEvent(event);
//        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mBannerRadius > 0) {
            canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), mImagePaint, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            //???????????????????????????
            //????????????????????????
            if (!mRoundTopRight && !mRoundTopLeft && !mRoundBottomRight && !mRoundBottomLeft) {
                drawTopLeft(canvas);
                drawTopRight(canvas);
                drawBottomLeft(canvas);
                drawBottomRight(canvas);
                canvas.restore();
                return;
            }
            if (mRoundTopLeft) {
                drawTopLeft(canvas);
            }
            if (mRoundTopRight) {
                drawTopRight(canvas);
            }
            if (mRoundBottomLeft) {
                drawBottomLeft(canvas);
            }
            if (mRoundBottomRight) {
                drawBottomRight(canvas);
            }
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    private void drawTopLeft(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, mBannerRadius);
        path.lineTo(0, 0);
        path.lineTo(mBannerRadius, 0);
        path.arcTo(new RectF(0, 0, mBannerRadius * 2, mBannerRadius * 2), -90, -90);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawTopRight(Canvas canvas) {
        int width = getWidth();
        Path path = new Path();
        path.moveTo(width - mBannerRadius, 0);
        path.lineTo(width, 0);
        path.lineTo(width, mBannerRadius);
        path.arcTo(new RectF(width - 2 * mBannerRadius, 0, width, mBannerRadius * 2), 0, -90);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawBottomLeft(Canvas canvas) {
        int height = getHeight();
        Path path = new Path();
        path.moveTo(0, height - mBannerRadius);
        path.lineTo(0, height);
        path.lineTo(mBannerRadius, height);
        path.arcTo(new RectF(0, height - 2 * mBannerRadius, mBannerRadius * 2, height), 90, 90);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawBottomRight(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        Path path = new Path();
        path.moveTo(width - mBannerRadius, height);
        path.lineTo(width, height);
        path.lineTo(width, height - mBannerRadius);
        path.arcTo(new RectF(width - 2 * mBannerRadius, height - 2 * mBannerRadius, width, height), 0, 90);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    class BannerOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        private int mTempPosition = INVALID_VALUE;
        private boolean isScrolled;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = BannerUtils.getRealPosition(isInfiniteLoop(), position, getRealCount());
            if (mOnPageChangeListener != null && realPosition == getCurrentItem() - 1) {
                mOnPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }
            if (getIndicator() != null && realPosition == getCurrentItem() - 1) {
                getIndicator().onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (isScrolled) {
                mTempPosition = position;
                int realPosition = BannerUtils.getRealPosition(isInfiniteLoop(), position, getRealCount());
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(realPosition);
                }
                if (getIndicator() != null) {
                    getIndicator().onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //???????????????,?????????????????????
            if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
                isScrolled = true;
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                //???????????????????????????
                isScrolled = false;
                if (mTempPosition != INVALID_VALUE && mIsInfiniteLoop) {
                    if (mTempPosition == 0) {
                        setCurrentItem(getRealCount(), false);
                    } else if (mTempPosition == getItemCount() - 1) {
                        setCurrentItem(1, false);
                    }
                }
            }
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
            if (getIndicator() != null) {
                getIndicator().onPageScrollStateChanged(state);
            }
        }

    }

    static class AutoLoopTask implements Runnable {
        private final WeakReference<Banner> reference;

        AutoLoopTask(Banner banner) {
            this.reference = new WeakReference<>(banner);
        }

        @Override
        public void run() {
            Banner banner = reference.get();
            if (banner != null && banner.mIsAutoLoop) {
                int count = banner.getItemCount();
                if (count == 0) {
                    return;
                }
                int next = (banner.getCurrentItem() + 1) % count;
                banner.setCurrentItem(next);
                banner.postDelayed(banner.mLoopTask, banner.mLoopTime);
            }
        }
    }

    private final RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (getItemCount() <= 1) {
                stop();
            } else {
                start();
            }
            setIndicatorPageChange();
        }
    };

    private void initIndicator() {
        if (getIndicator() == null || getAdapter() == null) {
            return;
        }
        if (getIndicator().getIndicatorConfig().isAttachToBanner()) {
            removeIndicator();
            addView(getIndicator().getIndicatorView());
        }
        initIndicatorAttr();
        setIndicatorPageChange();
    }

    private void setInfiniteLoop() {
        // ???????????????????????????????????????????????????
        if (!isInfiniteLoop()) {
            isAutoLoop(false);
        }
        setStartPosition(isInfiniteLoop() ? mStartPosition : 0);
    }

    private void setRecyclerViewPadding(int itemPadding) {
        setRecyclerViewPadding(itemPadding, itemPadding);
    }

    private void setRecyclerViewPadding(int leftItemPadding, int rightItemPadding) {
        RecyclerView recyclerView = (RecyclerView) getViewPager2().getChildAt(0);
        if (getViewPager2().getOrientation() == ViewPager2.ORIENTATION_VERTICAL) {
            recyclerView.setPadding(mViewPager2.getPaddingLeft(), leftItemPadding, mViewPager2.getPaddingRight(), rightItemPadding);
        } else {
            recyclerView.setPadding(leftItemPadding, mViewPager2.getPaddingTop(), rightItemPadding, mViewPager2.getPaddingBottom());
        }
        recyclerView.setClipToPadding(false);
    }


    /**
     * **********************************************************************
     * ------------------------ ????????????API ---------------------------------*
     * **********************************************************************
     */

    public int getCurrentItem() {
        return getViewPager2().getCurrentItem();
    }

    public int getItemCount() {
        if (getAdapter() != null) {
            return getAdapter().getItemCount();
        }
        return 0;

    }

    public int getScrollTime() {
        return mScrollTime;
    }

    public boolean isInfiniteLoop() {
        return mIsInfiniteLoop;
    }

    public BannerAdapter getAdapter() {
        return mAdapter;
    }

    public ViewPager2 getViewPager2() {
        return mViewPager2;
    }

    public Indicator getIndicator() {
        return mIndicator;
    }

    public IndicatorConfig getIndicatorConfig() {
        if (getIndicator() != null) {
            return getIndicator().getIndicatorConfig();
        }
        return null;
    }

    /**
     * ??????banner????????????
     */
    public int getRealCount() {
        if (getAdapter() != null) {
            return getAdapter().getRealCount();
        }
        return 0;

    }

    //-----------------------------------------------------------------------------------------

    /**
     * ?????????????????????
     * @param intercept
     * @return
     */
    public Banner setIntercept(boolean intercept) {
        isIntercept = intercept;
        return this;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     * @param position
     * @return
     */
    public Banner setCurrentItem(int position) {
        return setCurrentItem(position, true);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     * @param position
     * @param smoothScroll
     * @return
     */
    public Banner setCurrentItem(int position, boolean smoothScroll) {
        getViewPager2().setCurrentItem(position, smoothScroll);
        return this;
    }

    public Banner setIndicatorPageChange() {
        if (getIndicator() != null) {
            int realPosition = BannerUtils.getRealPosition(isInfiniteLoop(), getCurrentItem(), getRealCount());
            getIndicator().onPageChanged(getRealCount(), realPosition);
        }
        return this;
    }

    public Banner removeIndicator() {
        if (getIndicator() != null) {
            removeView(getIndicator().getIndicatorView());
        }
        return this;
    }


    /**
     * ????????????????????? (?????????setAdapter??????setDatas????????????????????????)
     */
    public Banner setStartPosition(int mStartPosition) {
        this.mStartPosition = mStartPosition;
        return this;
    }

    public int getStartPosition() {
        return mStartPosition;
    }

    /**
     * ??????????????????
     *
     * @param enabled true ?????????false ??????
     */
    public Banner setUserInputEnabled(boolean enabled) {
        getViewPager2().setUserInputEnabled(enabled);
        return this;
    }

    /**
     * ??????PageTransformer?????????????????????
     * {@link ViewPager2.PageTransformer}
     * ????????????????????????implementation "androidx.viewpager2:viewpager2:1.0.0"
     */
    public Banner addPageTransformer(@Nullable ViewPager2.PageTransformer transformer) {
        mCompositePageTransformer.addTransformer(transformer);
        return this;
    }

    /**
     * ??????PageTransformer??????addPageTransformer??????????????????????????????transformer
     */
    public Banner setPageTransformer(@Nullable ViewPager2.PageTransformer transformer) {
        getViewPager2().setPageTransformer(transformer);
        return this;
    }

    public Banner removeTransformer(ViewPager2.PageTransformer transformer) {
        mCompositePageTransformer.removeTransformer(transformer);
        return this;
    }

    /**
     * ?????? ItemDecoration
     */
    public Banner addItemDecoration(RecyclerView.ItemDecoration decor) {
        getViewPager2().addItemDecoration(decor);
        return this;
    }

    public Banner addItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        getViewPager2().addItemDecoration(decor, index);
        return this;
    }

    /**
     * ????????????????????????
     *
     * @param isAutoLoop ture ?????????false ?????????
     */
    public Banner isAutoLoop(boolean isAutoLoop) {
        this.mIsAutoLoop = isAutoLoop;
        return this;
    }


    /**
     * ????????????????????????
     *
     * @param loopTime ??????????????????
     */
    public Banner setLoopTime(long loopTime) {
        this.mLoopTime = loopTime;
        return this;
    }

    /**
     * ?????????????????????????????????
     */
    public Banner setScrollTime(int scrollTime) {
        this.mScrollTime = scrollTime;
        return this;
    }

    /**
     * ????????????
     */
    public Banner start() {
        if (mIsAutoLoop) {
            stop();
            postDelayed(mLoopTask, mLoopTime);
        }
        return this;
    }

    /**
     * ????????????
     */
    public Banner stop() {
        if (mIsAutoLoop) {
            removeCallbacks(mLoopTask);
        }
        return this;
    }

    /**
     * ??????????????????
     */
    public void destroy() {
        if (getViewPager2() != null && mPageChangeCallback != null) {
            getViewPager2().unregisterOnPageChangeCallback(mPageChangeCallback);
            mPageChangeCallback = null;
        }
        stop();
    }

    /**
     * ??????banner????????????
     */
    public Banner setAdapter(BA adapter) {
        if (adapter == null) {
            throw new NullPointerException(getContext().getString(R.string.banner_adapter_null_error));
        }
        this.mAdapter = adapter;
        if (!isInfiniteLoop()) {
            getAdapter().setIncreaseCount(0);
        }
        getAdapter().registerAdapterDataObserver(mAdapterDataObserver);
        mViewPager2.setAdapter(adapter);
        setCurrentItem(mStartPosition, false);
        initIndicator();
        return this;
    }

    /**
     * ??????banner????????????
     * @param adapter
     * @param isInfiniteLoop ????????????????????????
     * @return
     */
    public Banner setAdapter(BA adapter,boolean isInfiniteLoop) {
        mIsInfiniteLoop=isInfiniteLoop;
        setInfiniteLoop();
        setAdapter(adapter);
        return this;
    }

    /**
     * ????????????banner?????????????????????????????????adapter?????????????????????,???????????????????????????????????????????????????
     *
     * @param datas ?????????????????????null??????datas??????????????????banner????????????????????????????????????UI??????
     */
    public Banner setDatas(List<T> datas) {
        if (getAdapter() != null) {
            getAdapter().setDatas(datas);
            setCurrentItem(mStartPosition, false);
            setIndicatorPageChange();
            start();
        }
        return this;
    }

    /**
     * ??????banner????????????
     *
     * @param orientation {@link Orientation}
     */
    public Banner setOrientation(@Orientation int orientation) {
        getViewPager2().setOrientation(orientation);
        return this;
    }

    /**
     * ????????????????????????
     */
    public Banner setTouchSlop(int mTouchSlop) {
        this.mTouchSlop = mTouchSlop;
        return this;
    }

    /**
     * ??????????????????
     */
    public Banner setOnBannerListener(OnBannerListener<T> listener) {
        if (getAdapter() != null) {
            getAdapter().setOnBannerListener(listener);
        }
        return this;
    }

    /**
     * ??????viewpager????????????
     * <p>
     * ???viewpager2???????????????{@link ViewPager2.OnPageChangeCallback}?????????????????????
     * ??????????????????????????????????????????viewpager?????????{@link ViewPager.OnPageChangeListener}??????
     * </p>
     */
    public Banner addOnPageChangeListener(OnPageChangeListener pageListener) {
        this.mOnPageChangeListener = pageListener;
        return this;
    }

    /**
     * ??????banner??????
     * <p>
     * ?????????????????????????????????????????????????????????0??????
     *
     * @param radius ????????????
     */
    public Banner setBannerRound(float radius) {
        mBannerRadius = radius;
        return this;
    }

    /**
     * ??????banner??????(??????????????????????????????????????????????????????)????????????5.0??????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Banner setBannerRound2(float radius) {
        BannerUtils.setBannerRound(this, radius);
        return this;
    }

    /**
     * ???banner??????????????????
     *
     * @param itemWidth  item?????????????????????,??????dp
     * @param pageMargin ????????????,??????dp
     */
    public Banner setBannerGalleryEffect(int itemWidth, int pageMargin) {
        return setBannerGalleryEffect(itemWidth, pageMargin, .85f);
    }

    /**
     * ???banner??????????????????
     *
     * @param leftItemWidth  item??????????????????,??????dp
     * @param rightItemWidth item??????????????????,??????dp
     * @param pageMargin     ????????????,??????dp
     */
    public Banner setBannerGalleryEffect(int leftItemWidth, int rightItemWidth, int pageMargin) {
        return setBannerGalleryEffect(leftItemWidth,rightItemWidth, pageMargin, .85f);
    }

    /**
     * ???banner??????????????????
     *
     * @param itemWidth  item?????????????????????,??????dp
     * @param pageMargin ????????????,??????dp
     * @param scale      ??????[0-1],1???????????????
     */
    public Banner setBannerGalleryEffect(int itemWidth, int pageMargin, float scale) {
        return setBannerGalleryEffect(itemWidth, itemWidth, pageMargin, scale);
    }

    /**
     * ???banner??????????????????
     *
     * @param leftItemWidth  item??????????????????,??????dp
     * @param rightItemWidth item??????????????????,??????dp
     * @param pageMargin     ????????????,??????dp
     * @param scale          ??????[0-1],1???????????????
     */
    public Banner setBannerGalleryEffect(int leftItemWidth, int rightItemWidth, int pageMargin, float scale) {
        if (pageMargin > 0) {
            addPageTransformer(new MarginPageTransformer(BannerUtils.dp2px(pageMargin)));
        }
        if (scale < 1 && scale > 0) {
            addPageTransformer(new ScaleInTransformer(scale));
        }
        setRecyclerViewPadding(leftItemWidth > 0 ? BannerUtils.dp2px(leftItemWidth + pageMargin) : 0,
                rightItemWidth > 0 ? BannerUtils.dp2px(rightItemWidth + pageMargin) : 0);
        return this;
    }

    /**
     * ???banner??????????????????
     *
     * @param itemWidth item?????????????????????,??????dp
     */
    public Banner setBannerGalleryMZ(int itemWidth) {
        return setBannerGalleryMZ(itemWidth, .88f);
    }

    /**
     * ???banner??????????????????
     *
     * @param itemWidth item?????????????????????,??????dp
     * @param scale     ??????[0-1],1???????????????
     */
    public Banner setBannerGalleryMZ(int itemWidth, float scale) {
        if (scale < 1 && scale > 0) {
            addPageTransformer(new MZScaleInTransformer(scale));
        }
        setRecyclerViewPadding(BannerUtils.dp2px(itemWidth));
        return this;
    }

    /**
     * **********************************************************************
     * ------------------------ ????????????????????? --------------------------------*
     * **********************************************************************
     */

    /**
     * ?????????????????????(?????????banner???)
     */
    public Banner setIndicator(Indicator indicator) {
        return setIndicator(indicator, true);
    }

    /**
     * ?????????????????????(?????????????????????????????????????????????attachToBanner???false)
     *
     * @param attachToBanner ???????????????????????????banner??????false ?????????????????????????????????????????????????????????
     *                       ??????????????????false??????????????? setIndicatorGravity()???setIndicatorMargins() ??????????????????
     *                       ???????????????????????????????????????????????????????????????????????????????????????????????????demo
     */
    public Banner setIndicator(Indicator indicator, boolean attachToBanner) {
        removeIndicator();
        indicator.getIndicatorConfig().setAttachToBanner(attachToBanner);
        this.mIndicator = indicator;
        initIndicator();
        return this;
    }


    public Banner setIndicatorSelectedColor(@ColorInt int color) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setSelectedColor(color);
        }
        return this;
    }

    public Banner setIndicatorSelectedColorRes(@ColorRes int color) {
        setIndicatorSelectedColor(ContextCompat.getColor(getContext(), color));
        return this;
    }

    public Banner setIndicatorNormalColor(@ColorInt int color) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalColor(color);
        }
        return this;
    }

    public Banner setIndicatorNormalColorRes(@ColorRes int color) {
        setIndicatorNormalColor(ContextCompat.getColor(getContext(), color));
        return this;
    }

    public Banner setIndicatorGravity(@IndicatorConfig.Direction int gravity) {
        if (getIndicatorConfig() != null && getIndicatorConfig().isAttachToBanner()) {
            getIndicatorConfig().setGravity(gravity);
            getIndicator().getIndicatorView().postInvalidate();
        }
        return this;
    }

    public Banner setIndicatorSpace(int indicatorSpace) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setIndicatorSpace(indicatorSpace);
        }
        return this;
    }

    public Banner setIndicatorMargins(IndicatorConfig.Margins margins) {
        if (getIndicatorConfig() != null && getIndicatorConfig().isAttachToBanner()) {
            getIndicatorConfig().setMargins(margins);
            getIndicator().getIndicatorView().requestLayout();
        }
        return this;
    }

    public Banner setIndicatorWidth(int normalWidth, int selectedWidth) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalWidth(normalWidth);
            getIndicatorConfig().setSelectedWidth(selectedWidth);
        }
        return this;
    }

    public Banner setIndicatorNormalWidth(int normalWidth) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalWidth(normalWidth);
        }
        return this;
    }

    public Banner setIndicatorSelectedWidth(int selectedWidth) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setSelectedWidth(selectedWidth);
        }
        return this;
    }

    public Banner setIndicatorRadius(int indicatorRadius) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setRadius(indicatorRadius);
        }
        return this;
    }

    public Banner setIndicatorHeight(int indicatorHeight) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setHeight(indicatorHeight);
        }
        return this;
    }

    /**
     * **********************************************************************
     * ------------------------ ?????????????????? --------------------------------*
     * **********************************************************************
     */

    public Banner addBannerLifecycleObserver(LifecycleOwner owner) {
        if (owner != null) {
            owner.getLifecycle().addObserver(new BannerLifecycleObserverAdapter(owner, this));
        }
        return this;
    }

    @Override
    public void onStart(LifecycleOwner owner) {
        start();
    }

    @Override
    public void onStop(LifecycleOwner owner) {
        stop();
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        destroy();
    }

}
