package lets.pray.muslims.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lets.pray.muslims.MainActivity;
import lets.pray.muslims.R;
import lets.pray.muslims.data.StaticData;
import lets.pray.muslims.database.DatabaseHelper;
import lets.pray.muslims.fragment.HomeFragment;
import lets.pray.muslims.model.Prayer;
import lets.pray.muslims.reciever.AlarmReceiver;
import lets.pray.muslims.utility.ApplicationUtils;
import lets.pray.muslims.utility.PrayTime;

public class NewAlarmService extends IntentService {
    public AlarmManager alarmManager;
    long fazarFifteen,duhrThirty,asrThirty,maghribFifteen,ishaThirty,dayEnd;
    long fazarLast,duhrLast,asrLast,maghribLast,ishaLast;
    Intent alarmIntent;
    //   PendingIntent pendingIntent;


    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "Let's Pray";
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent, pendingIntent2;
    private static final String NOTIFICATION_MSG = " waqt countdown started";
    private static final String NOTIFICATION_MSG_EXTRA = " waqt remaining only 10 minutes";
    private static final long MINIMUM_DIFF = 60000;

    public NewAlarmService() {
        super("NewAlarmService");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//         don't notify if they've played in last 24 hr
//         Log.e(TAG, "Alarm Service has started.");
        Context context = this.getApplicationContext();
        Toast.makeText(context,"App running after reboot. Thank you",Toast.LENGTH_SHORT).show();
        Toast.makeText(context,"App running after reboot. Thank you", Toast.LENGTH_SHORT).show();
        long alarmTime = intent.getLongExtra(StaticData.ALARM_TIME, 0);
        long newDay = getSharedPreferences(StaticData.KEY_PREFERENCE, Context.MODE_PRIVATE).getLong(StaticData.NEW_DAY, 0);
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
        long fazarPlus = preferences.getLong(StaticData.PRAYER_TIME_FAJR_FIFTEEN, 0);
        long duhrPlus = preferences.getLong(StaticData.PRAYER_TIME_DUHR_THIRTY, 0);
        long asrPlus = preferences.getLong(StaticData.PRAYER_TIME_ASR_THIRTY, 0);
        long maghribPlus = preferences.getLong(StaticData.PRAYER_TIME_MAGRIB_FIFTEEN, 0);
        long ishaPlus = preferences.getLong(StaticData.PRAYER_TIME_ISHA_THIRTY, 0);
        fazarLast = preferences.getLong(StaticData.PRAYER_TIME_FAJR_LAST, 0);
        duhrLast = preferences.getLong(StaticData.PRAYER_TIME_DUHR_LAST, 0);
        asrLast = preferences.getLong(StaticData.PRAYER_TIME_ASR_LAST, 0);
        maghribLast = preferences.getLong(StaticData.PRAYER_TIME_MAGRIB_LAST, 0);
        ishaLast = preferences.getLong(StaticData.PRAYER_TIME_ISHA_LAST, 0);

        dayEnd = preferences.getLong(StaticData.DAY_END,0);


//        Log.e("ALARM_TIME", alarmTime + "");
//        Log.e("NEW_DAY", newDay + "");
        long currentTime = System.currentTimeMillis();
        long diff;
        if (alarmTime > currentTime) {
            diff = alarmTime - currentTime;
        } else {
            diff = currentTime - alarmTime;
        }

//        Log.e("ALARM TIME", alarmTime + "");
//        Log.e("CURRENT TIME", currentTime + "");
//        Log.e("TIME_DIFF", diff + "");

        if (diff >= 0 && diff < MINIMUM_DIFF) {
            if (alarmTime == newDay) {
                //Update new days prayertimes here
                updatePrayerTimes();
            } else if(alarmTime == fazarPlus || alarmTime == duhrPlus || alarmTime == asrPlus || alarmTime == maghribPlus || alarmTime == ishaPlus) {
                generateCustomeNOtification(context);
            }else if(alarmTime == fazarLast || alarmTime == duhrLast || alarmTime == asrLast || alarmTime == maghribLast || alarmTime == ishaLast) {
                generateExtraNotification(context);
            }
            else {
                generateNotification(context);
            }
        }
        setNextPrayerAlarm();
    }

    private void generateNotification(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent mIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setTicker(res.getString(R.string.notification_title))
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.notification_title))
                .setContentText(getNotificationText())
                .setSound(soundUri);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
//        Log.e(TAG, "Notification generated");
    }

    private void generateCustomeNOtification(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent mIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Resources res = this.getResources();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //YES RECEIVED
        Intent yesReceive = new Intent();
        yesReceive.setAction(StaticData.YES_ACTION);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 0, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //NO RECEIVED
        Intent NoReceive = new Intent();
        yesReceive.setAction(StaticData.NO_ACTION);
        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 0, NoReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setTicker(res.getString(R.string.custom_notification_title))
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.custom_notification_title))
                .setContentText(getNotificationText())
                .addAction(R.drawable.ic_done_black, "YES", pendingIntentYes)
                .addAction(R.drawable.ic_clear_black, "NO", pendingIntentNo)
                .setSound(soundUri);
      //  notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
//        Log.e(TAG, "Notification generated");
    }


    private void generateExtraNotification(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent mIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setTicker(res.getString(R.string.notification_title_extra))
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.notification_title_extra))
                .setContentText(getExtraNotificationText())
                .setSound(soundUri);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
//        Log.e(TAG, "Notification generated");
    }

    private void setNextPrayerAlarm() {
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
        long currAlarm = preferences.getLong(StaticData.ALARM_TIME, 0);
        long nextAlarm = getNextAlarm(currAlarm);

//        Log.e("CURRENT ALARM TIME", currAlarm + "");
//        Log.e("NEXT ALARM TIME", nextAlarm + "");
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        myIntent.putExtra(StaticData.ALARM_TIME, nextAlarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm, pendingIntent);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextAlarm, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarm, pendingIntent);
        }
        saveNextAlarm(nextAlarm);
    }

    private void saveNextAlarm(long alamrTime) {
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(StaticData.ALARM_TIME, alamrTime);
        editor.commit();
    }

//    private long getNextAlarm(long currAlarm) {
//        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
//        long fazr = preferences.getLong(StaticData.PRAYER_TIME_FAJR, 0);
//        long duhr = preferences.getLong(StaticData.PRAYER_TIME_DUHR, 0);
//        long asr = preferences.getLong(StaticData.PRAYER_TIME_ASR, 0);
//        long maghrib = preferences.getLong(StaticData.PRAYER_TIME_MAGRIB, 0);
//        long isha = preferences.getLong(StaticData.PRAYER_TIME_ISHA, 0);
//        long newDay = preferences.getLong(StaticData.NEW_DAY, 0);
//        if (currAlarm == fazr) {
//            return duhr;
//        } else if (currAlarm == duhr) {
//            return asr;
//        } else if (currAlarm == asr) {
//            return maghrib;
//        } else if (currAlarm == maghrib) {
//            return isha;
//        } else if (currAlarm == isha) {
//            return newDay;
//        } else if (currAlarm == newDay) {
//            return fazr;
//        } else {
//            return newDay;
//        }
//    }
private long getNextAlarm(long currAlarm) {
    SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
    long fazr = preferences.getLong(StaticData.PRAYER_TIME_FAJR, 0);
     fazarFifteen=preferences.getLong(StaticData.PRAYER_TIME_FAJR_FIFTEEN,0);
    long duhr = preferences.getLong(StaticData.PRAYER_TIME_DUHR, 0);
     duhrThirty=preferences.getLong(StaticData.PRAYER_TIME_DUHR_THIRTY,0);
    long asr = preferences.getLong(StaticData.PRAYER_TIME_ASR, 0);
     asrThirty=preferences.getLong(StaticData.PRAYER_TIME_ASR_THIRTY,0);
    long maghrib = preferences.getLong(StaticData.PRAYER_TIME_MAGRIB, 0);
     maghribFifteen=preferences.getLong(StaticData.PRAYER_TIME_MAGRIB_FIFTEEN,0);
    long isha = preferences.getLong(StaticData.PRAYER_TIME_ISHA, 0);
     ishaThirty=preferences.getLong(StaticData.PRAYER_TIME_ISHA_THIRTY,0);
    long newDay = preferences.getLong(StaticData.NEW_DAY, 0);
    if (currAlarm == fazr) {
        return fazarFifteen;
    } else if (currAlarm == fazarFifteen) {
        return fazarLast;
    } else if (currAlarm == fazarLast) {
        return duhr;
    } else if (currAlarm == duhr) {
        return duhrThirty;
    } else if (currAlarm == duhrThirty) {
        return duhrLast;
    }
    else if (currAlarm == duhrLast){
        return asr;
    }
    else if (currAlarm == asr){
        return asrThirty;
    }else if (currAlarm == asrThirty){
        return asrLast;
    }
    else if (currAlarm == asrLast){
        return maghrib;
    }
    else if (currAlarm == maghrib) {
        return maghribFifteen;
    }
    else if (currAlarm == maghribFifteen){
        return maghribLast;
    }
    else if (currAlarm == maghribLast) {
        return isha;
    }
    else if (currAlarm == isha){
        return ishaThirty;
    }
    else if (currAlarm == ishaThirty) {
        return ishaLast;
    }
    else if (currAlarm == ishaLast){
        return newDay;
    }
    else if (currAlarm == newDay) {
        return fazr;
    }else {
        return newDay;
    }
}

    private String getNotificationText() {
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
        long currAlarm = preferences.getLong(StaticData.ALARM_TIME, 0);
        long fazr = preferences.getLong(StaticData.PRAYER_TIME_FAJR, 0);
        long sunrise = preferences.getLong(StaticData.SUNRISE_TIME, 0);
        long duhr = preferences.getLong(StaticData.PRAYER_TIME_DUHR, 0);
        long asr = preferences.getLong(StaticData.PRAYER_TIME_ASR, 0);
        long maghrib = preferences.getLong(StaticData.PRAYER_TIME_MAGRIB, 0);
        long isha = preferences.getLong(StaticData.PRAYER_TIME_ISHA, 0);

        long fazrFifteen = preferences.getLong(StaticData.PRAYER_TIME_FAJR_FIFTEEN, 0);
        long duhrThirty = preferences.getLong(StaticData.PRAYER_TIME_DUHR_THIRTY, 0);
        long asrThirty = preferences.getLong(StaticData.PRAYER_TIME_ASR_THIRTY, 0);
        long maghribFifteen = preferences.getLong(StaticData.PRAYER_TIME_MAGRIB_FIFTEEN, 0);
        long ishaThirty = preferences.getLong(StaticData.PRAYER_TIME_ISHA_THIRTY, 0);

        if (currAlarm == fazr) {
            return "Fajr" + NOTIFICATION_MSG;
        } else if(currAlarm == fazrFifteen){
            return "Fazr Waqt running";
        } else if(currAlarm == fazarLast){
            return "10 minutes remaining for Fazr Waqt";
        }
        else if (currAlarm == duhr) {
            return "Zuhr" + NOTIFICATION_MSG;
        } else if(currAlarm == duhrThirty){
            return "Zuhr Waqt running";
        }else if(currAlarm == duhrLast){
            return "10 minutes remaining for Zuhr Waqt";
        }
        else if (currAlarm == asr) {
            return "Asr" + NOTIFICATION_MSG;
        } else if(currAlarm == asrThirty)
        {
            return "Asr Waqt running";
        }else if(currAlarm == asrLast){
            return "10 minutes remaining for Asr Waqt";
        }
        else if (currAlarm == maghrib) {
            return "Maghrib" + NOTIFICATION_MSG;
        } else if(currAlarm == maghribFifteen){
            return "Maghrib Waqt running";
        }else if(currAlarm == maghribLast){
            return "10 minutes remaining for Maghrib Waqt";
        }
        else if (currAlarm == isha) {
            return "Isha" + NOTIFICATION_MSG;
        } else if(currAlarm == ishaThirty){
            return "Isha Waqt running";
        }else if(currAlarm == ishaLast){
            return "10 minutes remaining for Isha Waqt";
        }
        else {
            return "Fajr" + NOTIFICATION_MSG;
        }
    }

    private String getExtraNotificationText(){
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
        long currAlarm = preferences.getLong(StaticData.ALARM_TIME, 0);

        long fazrLast = preferences.getLong(StaticData.PRAYER_TIME_FAJR_LAST, 0);
        long zuhrLast = preferences.getLong(StaticData.PRAYER_TIME_DUHR_LAST, 0);
        long asrLast = preferences.getLong(StaticData.PRAYER_TIME_ASR_LAST, 0);
        long maghribLast = preferences.getLong(StaticData.PRAYER_TIME_MAGRIB_LAST, 0);
        long ishaLast = preferences.getLong(StaticData.PRAYER_TIME_ISHA_LAST, 0);

        if (currAlarm == fazrLast) {
            return "Fajr" + NOTIFICATION_MSG_EXTRA;
        } else if (currAlarm == zuhrLast) {
            return "Zuhr" + NOTIFICATION_MSG_EXTRA;
        } else if (currAlarm == asrLast) {
            return "Asr" + NOTIFICATION_MSG_EXTRA;
        }else if(currAlarm == maghribLast) {
            return "Maghrib" + NOTIFICATION_MSG_EXTRA;
        }
        else if (currAlarm == ishaLast) {
            return "Isha" + NOTIFICATION_MSG_EXTRA;
        }else {
            return "Fajr" + NOTIFICATION_MSG_EXTRA;
        }
    }

    public void updatePrayerTimes() {
        long fazrWaqtMs = 0, sunriseMs = 0, dohrWaqtMs = 0, asrWaqtMs = 0, maghribWaqtMs = 0, maghribEnd = 0, ishaWaqtMs = 0;
        long fazrWaqtFifteen = 0, dohrWaqtThirty = 0, asrWaqtThirty = 0, maghribWaqtFifteen = 0, ishaWaqtThirty = 0;
        long fazrWaqtMsLast = 0, dohrWaqtMsLast = 0, asrWaqtMsLast = 0, maghribWaqtMsLast = 0, ishaWaqtMsLast = 0;
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<Prayer> prayers = databaseHelper.getPrayer();
        for (int i = 0; i < prayers.size(); i++) {
            if (i == 0) {
                fazrWaqtMs = ApplicationUtils.getPrayerTimeInMs(prayers.get(i).getPrayerTime().toString());
//                Log.e("Fajr In Ms", fazrWaqtMs + "");
                fazrWaqtFifteen=fazrWaqtMs+StaticData.FIFTEEN_MINUTE;
            }
            if (i == 1) {
                sunriseMs = ApplicationUtils.getPrayerTimeInMs(prayers.get(i).getPrayerTime().toString());
//                Log.e("Sunruse In Ms", sunriseMs + "");
            }
            if (i == 2) {
                dohrWaqtMs = ApplicationUtils.getPrayerTimeInMs(prayers.get(i).getPrayerTime().toString());
//                Log.e("Dohr In Ms", dohrWaqtMs + "");
                dohrWaqtThirty=dohrWaqtMs+StaticData.THIRTY_MINUTE;
            }
            if (i == 3) {
                asrWaqtMs = ApplicationUtils.getPrayerTimeInMs(prayers.get(i).getPrayerTime().toString());
//                Log.e("Asr In Ms", asrWaqtMs + "");
                asrWaqtThirty=asrWaqtMs+StaticData.THIRTY_MINUTE;
            }
            if (i == 4) {
                maghribWaqtMs = ApplicationUtils.getPrayerTimeInMs(prayers.get(i).getPrayerTime().toString());
//                Log.e("Maghrib In Ms", maghribWaqtMs + "");
                maghribEnd = maghribWaqtMs + 1000 * 60 * 45;
//                Log.e("Maghrib End", maghribEnd + "");
                maghribWaqtFifteen=maghribWaqtMs+StaticData.FIFTEEN_MINUTE;
            }
            if (i == 5) {
                ishaWaqtMs = ApplicationUtils.getPrayerTimeInMs(prayers.get(i).getPrayerTime().toString());
//                Log.e("Isha In Ms", ishaWaqtMs + "");
                ishaWaqtThirty=ishaWaqtMs+StaticData.FIFTEEN_MINUTE;
            }
        }
        fazrWaqtMsLast=(sunriseMs-StaticData.TEN_MINUTE);
        dohrWaqtMsLast=(asrWaqtMs-StaticData.TEN_MINUTE);
        asrWaqtMsLast=(maghribWaqtMs-StaticData.TEN_MINUTE);
        maghribWaqtMsLast=(maghribEnd-StaticData.TEN_MINUTE);
        ishaWaqtMsLast=(dayEnd-StaticData.TEN_MINUTE);

        saveAlarm(fazrWaqtMs, sunriseMs, dohrWaqtMs, asrWaqtMs, maghribWaqtMs, ishaWaqtMs);
        saveExtraAlarm(fazrWaqtFifteen, dohrWaqtThirty, asrWaqtThirty, maghribWaqtFifteen, ishaWaqtThirty);
        saveAlarmMinus(fazrWaqtMsLast, dohrWaqtMsLast, asrWaqtMsLast, maghribWaqtMsLast, ishaWaqtMsLast);
        setNextPrayerAlarm();

    }

    private void saveAlarm(long alarmTimeFajr, long sunrise, long alarmTimeDuhr, long alarmTimeAsr, long alarmTimeMagrib, long alarmTimeIsha) {
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(StaticData.PRAYER_TIME_FAJR, alarmTimeFajr);
        editor.putLong(StaticData.SUNRISE_TIME, sunrise);
        editor.putLong(StaticData.PRAYER_TIME_DUHR, alarmTimeDuhr);
        editor.putLong(StaticData.PRAYER_TIME_ASR, alarmTimeAsr);
        editor.putLong(StaticData.PRAYER_TIME_MAGRIB, alarmTimeMagrib);
        editor.putLong(StaticData.PRAYER_TIME_ISHA, alarmTimeIsha);
        editor.putLong(StaticData.NEW_DAY, getNewDay());
        editor.commit();
    }

    private void saveExtraAlarm(long alarmTimeFajrFifteen, long alarmTimeDuhrThirty, long alarmTimeAsrThirty, long alarmTimeMagribFifteen, long alarmTimeIshaThirty) {
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(StaticData.PRAYER_TIME_FAJR_FIFTEEN, alarmTimeFajrFifteen);
        editor.putLong(StaticData.PRAYER_TIME_DUHR_THIRTY, alarmTimeDuhrThirty);
        editor.putLong(StaticData.PRAYER_TIME_ASR_THIRTY, alarmTimeAsrThirty);
        editor.putLong(StaticData.PRAYER_TIME_MAGRIB_FIFTEEN, alarmTimeMagribFifteen);
        editor.putLong(StaticData.PRAYER_TIME_ISHA_THIRTY, alarmTimeIshaThirty);
        editor.putLong(StaticData.NEW_DAY, getNewDay());
        editor.commit();
    }

    private void saveAlarmMinus(long alarmTimeFajrLast, long alarmTimeDuhrLast, long alarmTimeAsrLast, long alarmTimeMagribLast, long alarmTimeIshaLast) {
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(StaticData.PRAYER_TIME_FAJR_LAST, alarmTimeFajrLast);
        editor.putLong(StaticData.PRAYER_TIME_DUHR_LAST, alarmTimeDuhrLast);
        editor.putLong(StaticData.PRAYER_TIME_ASR_LAST, alarmTimeAsrLast);
        editor.putLong(StaticData.PRAYER_TIME_MAGRIB_LAST, alarmTimeMagribLast);
        editor.putLong(StaticData.PRAYER_TIME_ISHA_LAST, alarmTimeIshaLast);
        editor.putLong(StaticData.NEW_DAY, getNewDay());
        editor.commit();
    }

    private long getNewDay() {
        Calendar calendar = Calendar.getInstance();
        String dateString = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + "23:59:59";
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date postDate = ApplicationUtils.formatDate(dateString, dtFormat);
        calendar.setTime(postDate);
        long newDay = calendar.getTimeInMillis() + StaticData.TEN_MINUTE;
        return newDay;
    }
}