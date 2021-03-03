package it.uniparthenope.fairwind.model.impl;

import android.graphics.drawable.Drawable;

/**
 * Created by raffaelemontella on 23/09/15.
 */
public  class AppDetail {
    private CharSequence label;
    private CharSequence packageName;
    private Drawable icon;

    public AppDetail(CharSequence label, String packageName, Drawable icon) {
        this.label=label;
        this.packageName=packageName;
        this.icon=icon;
    }

    public CharSequence getLabel() { return label; }
    public CharSequence getPackageName() { return packageName; }
    public Drawable getIcon() { return icon; }

}
