package com.vistara.traveler.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Sharad on 27-06-2017.
 */

public class SessionManeger {

    Context context;
    public SessionManeger(Context c)
    {
        this.context=c;
    }

    public void redirect(Intent intent, boolean clear)
    {
        if(clear) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
            ((Activity) context).finish();

    }
}
