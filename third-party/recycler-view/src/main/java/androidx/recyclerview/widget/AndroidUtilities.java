package androidx.recyclerview.widget;

import android.view.View;

import androidx.annotation.NonNull;

class AndroidUtilities {
    static int dp(float value, @NonNull View view) {
        return (int) Math.ceil(view.getResources().getDisplayMetrics().density * value);
    }
}
