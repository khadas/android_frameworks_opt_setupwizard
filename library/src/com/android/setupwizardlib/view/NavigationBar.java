/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.setupwizardlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.setupwizardlib.R;

public class NavigationBar extends LinearLayout implements View.OnClickListener {

    public interface NavigationBarListener {
        void onNavigateBack();
        void onNavigateNext();
    }

    private static int getNavbarTheme(Context context) {
        // Normally we can automatically guess the theme by comparing the foreground color against
        // the background color. But we also allow specifying explicitly using suwNavBarTheme.
        TypedArray attributes = context.obtainStyledAttributes(
                new int[] {
                        R.attr.suwNavBarTheme,
                        android.R.attr.colorForeground,
                        android.R.attr.colorBackground });
        int theme = attributes.getResourceId(0, 0);
        if (theme == 0) {
            // Compare the value of the foreground against the background color to see if current
            // theme is light-on-dark or dark-on-light.
            float[] foregroundHsv = new float[3];
            float[] backgroundHsv = new float[3];
            Color.colorToHSV(attributes.getColor(1, 0), foregroundHsv);
            Color.colorToHSV(attributes.getColor(2, 0), backgroundHsv);
            boolean isDarkBg = foregroundHsv[2] > backgroundHsv[2];
            theme = isDarkBg ? R.style.SuwNavBarThemeDark : R.style.SuwNavBarThemeLight;
        }
        attributes.recycle();
        return theme;
    }

    private static Context getThemedContext(Context context) {
        final int theme = getNavbarTheme(context);
        return new ContextThemeWrapper(context, theme);
    }

    private Button mNextButton;
    private Button mBackButton;
    private NavigationBarListener mListener;

    public NavigationBar(Context context) {
        this(context, null);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getThemedContext(context), attrs, defStyleAttr);
        View.inflate(getContext(), R.layout.suw_navbar_view, this);
        mNextButton = (Button) findViewById(R.id.suw_navbar_next);
        mBackButton = (Button) findViewById(R.id.suw_navbar_back);
    }

    public Button getBackButton() {
        return mBackButton;
    }

    public Button getNextButton() {
        return mNextButton;
    }

    public void setNavigationBarListener(NavigationBarListener listener) {
        mListener = listener;
        if (mListener != null) {
            getBackButton().setOnClickListener(this);
            getNextButton().setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            if (view == getBackButton()) {
                mListener.onNavigateBack();
            } else if (view == getNextButton()) {
                mListener.onNavigateNext();
            }
        }
    }

    public static class NavButton extends Button {

        public NavButton(Context context) {
            this(context, null);
        }

        public NavButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            // The color of the button is #de000000 / #deffffff when enabled. When disabled, apply
            // additional 23% alpha, so the overall opacity is 20%.
            setAlpha(enabled ? 1.0f : 0.23f);
        }
    }

}
