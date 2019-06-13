package org.no_ip.wqwdpxd.homeautomationwear_php;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class GateOnlyWidget extends AppWidgetProvider {

    private static final String clickBtnGateOpen = "W_B_GOPEN2";
    private static final String clickBtnGateClose = "W_B_GCLOSE2";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gate_only_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.gate_only_widget);
            ComponentName thisWidget = new ComponentName(context, GateOnlyWidget.class);

            views.setOnClickPendingIntent(R.id.widget2BtnGateClose, getPendingSelfIntent(context, clickBtnGateClose));
            views.setOnClickPendingIntent(R.id.widget2BtnGateOpen, getPendingSelfIntent(context, clickBtnGateOpen));

            appWidgetManager.updateAppWidget(thisWidget, views);
            //updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.d("WIDGET","Created gate only widget");
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);

        //your onClick action is here

        //Check if user is logged in
        String prefs = context.getResources().getString(R.string.prefs);
        final SharedPreferences pref = context.getApplicationContext().getSharedPreferences(prefs, 0); // 0 - for private mode
        String user=pref.getString("username", "Unknown");

        Log.d("WIDGET","DEBUG_CLICK, user: "+user);
        if(user.equals("Unknown")||user.equals("Not_important")) {
            displayToast("Please login in APP first!", context);
        }else{

            if (clickBtnGateOpen.equals(intent.getAction())){
                sendCommand("node=gate2&action=open",user,context);
            }else if (clickBtnGateClose.equals(intent.getAction())){
                sendCommand("node=gate2&action=close",user,context);
            }

        }


    }

    public void sendCommand(String send_text,String user, Context context){
        if(networkConnected(context)) {
            if(!Global.SendCommand_running) {
                Global.SendCommand_running=true;
                Log.d("DEBUG-DOMI",send_text);
                new SendCommand().execute(send_text, user, "WidgetActivity");
            }
        }else{
            displayToast("No network", context);
        }

    }


    public boolean networkConnected(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }


        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



    public void displayToast(String text2, Context context){
        try {
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text2, duration);
            toast.show();
        }catch (Exception e){
            Log.e("TOAST",e.getMessage());
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
