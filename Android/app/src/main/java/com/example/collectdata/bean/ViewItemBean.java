package com.example.collectdata.bean;

import android.widget.EditText;

import com.example.collectdata.tools.ConstTools;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ViewItemBean {

    private int type;
    private EditText editText = null;
    private MaterialButtonToggleGroup buttonToggleGroup;

    public ViewItemBean(EditText editText) {
        this.type = ConstTools.MESSAGE_BEANTYPE_COMMON;
        this.editText = editText;
    }

    public ViewItemBean(MaterialButtonToggleGroup buttonToggleGroup) {
        this.type = ConstTools.MESSAGE_BEANTYPE_SELECTOR;
        this.buttonToggleGroup = buttonToggleGroup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public MaterialButtonToggleGroup getButtonToggleGroup() {
        return buttonToggleGroup;
    }

    public void setButtonToggleGroup(MaterialButtonToggleGroup buttonToggleGroup) {
        this.buttonToggleGroup = buttonToggleGroup;
    }
}
