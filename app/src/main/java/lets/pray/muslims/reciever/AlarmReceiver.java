package lets.pray.muslims.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import lets.pray.muslims.service.NewAlarmService;

/**
 * Created by wali on 6/2/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service1 = new Intent(context, NewAlarmService.class);
        context.startService(service1);

        long l = intent.getLongExtra("TIME",0);
        Log.e("Boradcast",l+" On received called");
    }
}
