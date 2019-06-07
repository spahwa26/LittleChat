package com.app.littlechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CommonUtilities {

    private static Dialog dialog;


    public static void putString(Activity activity, String name, String value)
    {
        SharedPreferences preferences=activity.getSharedPreferences(BuildConfig.APPLICATION_ID,Context.MODE_PRIVATE);

        preferences.edit().putString(name,value).apply();
    }

    public static String getString(Activity activity, String name)
    {
        SharedPreferences preferences=activity.getSharedPreferences(BuildConfig.APPLICATION_ID,Context.MODE_PRIVATE);

        return  preferences.getString(name,"");
    }

    public static void clearPrefrences(Activity activity)
    {
        SharedPreferences preferences=activity.getSharedPreferences(BuildConfig.APPLICATION_ID,Context.MODE_PRIVATE);

        preferences.edit().clear().apply();
    }


    public static void showLogoutPopup(final Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setCancelable(true);

        alert.setMessage("Do you want to logout?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                clearPrefrences(activity);
                activity.startActivity(new Intent(activity,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });


        alert.show();
    }

    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void underlineTextView(TextView textView, String text)
    {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }


    public static void showAlert(final Activity activity, String msg, final boolean isFinish) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setCancelable(true);

        alert.setMessage(msg);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                if(isFinish)
                    activity.finish();
            }
        });

        alert.show();
    }

    public static void setLayoutManager(RecyclerView view, RecyclerView.LayoutManager manager) {
        RecyclerView.LayoutManager mLayoutManager1 = manager;
        view.setLayoutManager(mLayoutManager1);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setNestedScrollingEnabled(false);
    }



    public static void showProgressWheel(Context context) {
        hideProgressWheel();

        dialog = new Dialog(context);

        dialog.setCancelable(false);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_progress_wheel);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();

        Point size = new Point();

        display.getSize(size);

        int width = size.x;

        dialog.getWindow().setLayout(width - 60, ViewGroup.LayoutParams.WRAP_CONTENT);// set width of alert dialog box nad get width dynamically

        dialog.show();
    }
    public static void hideProgressWheel() {
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (Exception e) {

        }
    }


}
