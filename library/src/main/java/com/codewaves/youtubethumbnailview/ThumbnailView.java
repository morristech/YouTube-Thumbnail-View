package com.codewaves.youtubethumbnailview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Sergej Kravcenko on 4/14/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ThumbnailView extends RelativeLayout {
   private static final int DEFAULT_TITLE_MAX_LINES = 1;
   private static final int DEFAULT_MIN_THUMBNAIL_SIZE = 320;
   private static final int DEFAULT_FADE_DURATION = 500;

   private ImageView thumbnailView;
   private TextView titleView;
   private TextView timeView;

   private boolean isLoaded;
   private int minThumbnailSize;
   private boolean titleVisible;
   private boolean timeVisible;
   private int fadeDuration;

   private int dpToPx(Context context, float dp) {
      final float scale = context.getResources().getDisplayMetrics().density;
      return Math.round(dp * scale);
   }

   public ThumbnailView(Context context) {
      this(context, null);
   }

   public ThumbnailView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public ThumbnailView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public ThumbnailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init(context, attrs);
   }

   private void init(Context context, AttributeSet attrs) {
      // Attributes
      final TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ThumbnailView, 0, 0);

      minThumbnailSize = attr.getInteger(R.styleable.ThumbnailView_youtube_minThumbnailWidth, DEFAULT_MIN_THUMBNAIL_SIZE);

      fadeDuration = DEFAULT_FADE_DURATION;

      titleVisible = attr.getBoolean(R.styleable.ThumbnailView_youtube_titleVisible, true);
      timeVisible = attr.getBoolean(R.styleable.ThumbnailView_youtube_timeVisible, true);

      final int titleColor = attr.getColor(R.styleable.ThumbnailView_youtube_titleColor, Color.WHITE);
      final int titleBackgroundColor = attr.getColor(R.styleable.ThumbnailView_youtube_titleBackgroundColor, 0x80000000);
      final int titlePaddingLeft = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingLeft, dpToPx(context, 10.0f));
      final int titlePaddingRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingRight, dpToPx(context, 10.0f));
      final int titlePaddingTop = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingTop, dpToPx(context, 5.0f));
      final int titlePaddingBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingBottom, dpToPx(context, 5.0f));
      final float titleTextSize = attr.getDimension(R.styleable.ThumbnailView_youtube_titleTextSize, getResources().getDimension(R.dimen.title_text_size));
      final int titleMaxLines = attr.getInteger(R.styleable.ThumbnailView_youtube_titleMaxLines, DEFAULT_TITLE_MAX_LINES);

      final int timeColor = attr.getColor(R.styleable.ThumbnailView_youtube_timeColor, Color.WHITE);
      final int timeBackgroundColor = attr.getColor(R.styleable.ThumbnailView_youtube_timeBackgroundColor, 0x80000000);
      final int timePaddingLeft = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingLeft, dpToPx(context, 5.0f));
      final int timePaddingRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingRight, dpToPx(context, 5.0f));
      final int timePaddingTop = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingTop, dpToPx(context, 0.0f));
      final int timePaddingBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingBottom, dpToPx(context, 0.0f));
      final int timeMarginBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timeMarginBottom, dpToPx(context, 10.0f));
      final int timeMarginRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timeMarginRight, dpToPx(context, 10.0f));
      final float timeTextSize = attr.getDimension(R.styleable.ThumbnailView_youtube_timeTextSize, getResources().getDimension(R.dimen.time_text_size));

      attr.recycle();

      // Add thumbnailView image
      thumbnailView = new ImageView(context);
      thumbnailView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      thumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);

      addView(thumbnailView);

      // Add video titleView
      titleView = new TextView(context);
      titleView.setTextColor(titleColor);
      titleView.setBackgroundColor(titleBackgroundColor);
      titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
      titleView.setMaxLines(titleMaxLines);
      titleView.setEllipsize(TextUtils.TruncateAt.END);
      titleView.setPadding(titlePaddingLeft, titlePaddingTop, titlePaddingRight, titlePaddingBottom);
      titleView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      titleView.setVisibility(GONE);

      addView(titleView);

      // Add video length
      timeView = new TextView(context);
      timeView.setTextColor(timeColor);
      timeView.setBackgroundColor(timeBackgroundColor);
      timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeTextSize);
      timeView.setMaxLines(1);
      timeView.setPadding(timePaddingLeft, timePaddingTop, timePaddingRight, timePaddingBottom);

      final LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      lp.setMargins(0, 0, timeMarginRight, timeMarginBottom);
      lp.addRule(ALIGN_PARENT_BOTTOM);
      lp.addRule(ALIGN_PARENT_RIGHT);
      timeView.setLayoutParams(lp);
      timeView.setVisibility(GONE);

      addView(timeView);

      // Clickable
      setClickable(true);
      setFocusable(true);
   }

   public void setFadeDuration(int durationMillis) {
      this.fadeDuration = durationMillis;
   }

   @NonNull
   public TextView getTitleView() {
      return titleView;
   }

   @NonNull
   public TextView getTimeView() {
      return timeView;
   }

   @NonNull
   public ImageView getThumbnailView() {
      return thumbnailView;
   }

   public void clearThumbnail() {
      ThumbnailLoader.cancelThumbnailLoad(this);
      titleView.setVisibility(GONE);
      timeView.setVisibility(GONE);
      thumbnailView.setImageDrawable(null);
      isLoaded = false;
   }

   public void setTitleVisibility(boolean visible) {
      titleVisible = visible;
      if (isLoaded) {
         titleView.setVisibility(visible ? VISIBLE : GONE);
      }
   }

   public void setTimeVisibility(boolean visible) {
      timeVisible = visible;
      if (isLoaded) {
         timeView.setVisibility(visible ? VISIBLE : GONE);
      }
   }

   public void displayThumbnail(@Nullable String title, int length, @Nullable Bitmap bitmap) {
      ThumbnailLoader.cancelThumbnailLoad(this);
      setThumbnailAndShow(title, length, bitmap, null);
   }

   public void displayThumbnail(@Nullable String title, int length, @Nullable Drawable drawable) {
      ThumbnailLoader.cancelThumbnailLoad(this);
      setThumbnailAndShow(title, length, null, drawable);
   }

   public void loadThumbnail(@NonNull String url) {
      loadThumbnail(url, null, null);
   }

   public void loadThumbnail(@NonNull String url, @NonNull ThumbnailLoadingListener listener) {
      loadThumbnail(url, listener, null);
   }

   public void loadThumbnail(final @NonNull String url, final @Nullable ImageLoader imageLoader) {
      loadThumbnail(url, null, imageLoader);
   }

   public void loadThumbnail(final @NonNull String url, final @Nullable ThumbnailLoadingListener listener, final @Nullable ImageLoader imageLoader) {
      ThumbnailLoader.loadThumbnail(this, url, minThumbnailSize, listener, imageLoader);
   }

   void setThumbnailAndShow(@Nullable String title, int length, @Nullable Bitmap bitmap, @Nullable Drawable drawable) {
      titleView.setText(title);
      if (titleVisible) {
         titleView.setVisibility(VISIBLE);
      }

      timeView.setText(Utils.secondsToTime(length));
      if (timeVisible) {
         timeView.setVisibility(length > 0 ? VISIBLE : GONE);
      }

      if (bitmap != null) {
         thumbnailView.setImageBitmap(bitmap);
      }
      else if (drawable != null) {
         thumbnailView.setImageDrawable(drawable);
      }

      animateViews(length > 0);
      isLoaded = true;
   }

   void animateViews(boolean animateTime) {
      if (!isLoaded) {
         final AlphaAnimation fade = new AlphaAnimation(0, 1);
         fade.setDuration(fadeDuration);
         fade.setInterpolator(new DecelerateInterpolator());

         if (titleVisible) {
            titleView.startAnimation(fade);
         }
         if (timeVisible && animateTime) {
            timeView.startAnimation(fade);
         }
         thumbnailView.startAnimation(fade);
      }
   }
}
