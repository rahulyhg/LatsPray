package lets.pray.muslims.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import lets.pray.muslims.data.StaticData;
import lets.pray.muslims.service.NewAlarmService;

/**
 * Created by wali on 6/2/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmTIme = intent.getLongExtra(StaticData.ALARM_TIME, 0);
        long currTIme = System.currentTimeMillis();
//        Log.e("Boradcast",alarmTIme+" alarm On received called");
//        Log.e("Boradcast",currTIme+" current On received called");

            Intent service1 = new Intent(context, NewAlarmService.class);
            service1.putExtra(StaticData.ALARM_TIME, alarmTIme);
            context.startService(service1);


    }
}
