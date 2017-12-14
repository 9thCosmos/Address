package com.lzy.addressselector;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;



public class AddressSelectorDialog extends Dialog {
    private Selector selector;

    public AddressSelectorDialog(Context context) {
        super(context, R.style.address_selector_bottom_dialog);
    }

    public AddressSelectorDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public AddressSelectorDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void init(Context context,Selector selector) {
        this.selector = selector;
        setContentView(selector.getView());
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = AddressSelectorUtil.dip2px(context, 256);
        window.setAttributes(params);

        window.setGravity(Gravity.BOTTOM);
    }

    public void setOnAddressSelectedListener(SelectedListener listener) {
        this.selector.setSelectedListener(listener);
    }

    public AddressSelectorDialog show(Context context) {
        return show(context, null);
    }

    public AddressSelectorDialog show(Context context, SelectedListener listener) {
        AddressSelectorDialog dialog = new AddressSelectorDialog(context, R.style.address_selector_bottom_dialog);
        dialog.selector.setSelectedListener(listener);
        dialog.show();

        return dialog;
    }

    /**
     * show 之后调用，设置dialog大小
     * @param width
     * @param height
     */
    public void setSize(int width,int height){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        window.setAttributes(params);
    }
}
