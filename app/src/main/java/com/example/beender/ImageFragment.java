package com.example.beender;

import static androidx.fragment.app.FragmentManager.TAG;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

public class ImageFragment extends DialogFragment {
    private static final String TAG = ImageFragment.class.getSimpleName();
    private final Bitmap image;
    private ScaleGestureDetector mScaleGestureDetector;
    GestureDetector mSimpleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;

    public ImageFragment(Bitmap currentImage) {
        this.image = currentImage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        mImageView = view.findViewById(R.id.image_view);
        mImageView.setImageBitmap(image);

        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mSimpleGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                dismiss();
                return true;
            }

//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                dismiss();
//                return true;
//            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mScaleFactor = 1f;
                mImageView.setScaleX(mScaleFactor);
                mImageView.setScaleY(mScaleFactor);
                mImageView.setTranslationX(0f);
                mImageView.setTranslationY(0f);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float maxX = (mImageView.getWidth() * (mScaleFactor - 1) / 2) / mScaleFactor;
                float maxY = (mImageView.getHeight() * (mScaleFactor - 1) / 2) / mScaleFactor;
                float newX = Math.min(Math.max(mImageView.getTranslationX() - distanceX, -maxX), maxX);
                float newY = Math.min(Math.max(mImageView.getTranslationY() - distanceY, -maxY), maxY);
                mImageView.setTranslationX(newX);
                mImageView.setTranslationY(newY);
                return true;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean result = mScaleGestureDetector.onTouchEvent(motionEvent);
                boolean result2 = mSimpleGestureDetector.onTouchEvent(motionEvent);


                return result || result2;
            }
        });

        return view;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        // when a scale gesture is detected, use it to resize the image
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            if (mScaleFactor < 1) {
                return true;
            }

            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);

            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);

            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.95);

            dialog.getWindow().setAttributes(params);

            Window window = dialog.getWindow();

            if (window != null) {
                window.setGravity(Gravity.CENTER);

                // create a drawable with rounded corners
                GradientDrawable drawable = new GradientDrawable();

                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getActivity().getTheme();
                theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);

                drawable.setColor(Color.TRANSPARENT);
                drawable.setCornerRadius(20);
                window.setBackgroundDrawable(drawable);
            }
        }
    }

}
