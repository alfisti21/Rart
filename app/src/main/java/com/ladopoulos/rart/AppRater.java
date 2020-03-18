package com.ladopoulos.rart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

class AppRater {
    private final static String APP_TITLE = "iART";// App Name
    private final static String APP_PNAME = "com.ladopoulos.rart";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches

    static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("prefID", Context.MODE_PRIVATE);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    private static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        int unicode = 0x1F609;
        String emoji = getEmojiByUnicode(unicode);

        //New part start
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Rate " + APP_TITLE);
        builder.setIcon(R.drawable.rate);
        builder.setMessage(mContext.getString(R.string.rateText)+emoji);
        builder.setPositiveButton(mContext.getString(R.string.rate),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                        dialog.dismiss();
                    }
                });

        builder.setNeutralButton(R.string.remind_me_later,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(R.string.noThanks,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        //New part end

    }

    private static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
