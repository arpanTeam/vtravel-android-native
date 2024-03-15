package com.vistara.traveler;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatCheckBox;

public class CheckBoxClass extends AppCompatCheckBox {



    public CheckBoxClass(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setButtonDrawable(new StateListDrawable());
    }
    @Override
    public void setChecked(boolean t){
        if(t)
        {
            this.setBackgroundResource(R.drawable.select);
        }
        else
        {
            this.setBackgroundResource(R.drawable.deselect);
        }
        super.setChecked(t);
    }
}