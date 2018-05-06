package com.hsn.caresaz.caresaz.ProfilResimtasarim;

/**
 * Created by HULYA on 1.04.2018.
 */

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        Corner.TOP_LEFT, Corner.TOP_RIGHT,
        Corner.BOTTOM_LEFT, Corner.BOTTOM_RIGHT
})
public @interface Corner {
    int BOTTOM_RIGHT = 2;
    int BOTTOM_LEFT = 3;
    int TOP_LEFT = 0;
    int TOP_RIGHT = 1;
}