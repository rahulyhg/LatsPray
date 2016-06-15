package lets.pray.muslims;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import lets.pray.muslims.model.RamadanSchedule;
import lets.pray.muslims.utility.ApplicationUtils;
import lets.pray.muslims.utility.RamadanScheduleMaker;
import lets.pray.muslims.data.StaticData;
import lets.pray.muslims.fragment.HomeFragment;
import lets.pray.muslims.fragment.RamadanFragment;

public class MainActivity extends AppCompatActivity {

    DrawerLayout dlMain;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    NavigationView nvDrawer;
    RelativeLayout rlNavHeaderContent;

    int day_state = 0;

    FragmentManager fragmentManager;
    Context context;

    /**
     *  Need to start implementing prayer tracking system from now on!!
     *  **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        day_state = ApplicationUtils.getDayState();
        setThemeAccordingToDayState();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initUI();
        initActionBar();
        setActionbarBackground();
        initDrawer();
        initFragmentManager();
        setupDrawerContent();
//        setDrawerHeaderContent();

    }

    private void initUI() {
        dlMain = (DrawerLayout) findViewById(R.id.dlMain);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        nvDrawer = (NavigationView) findViewById(R.id.nvDrawer);
    }


    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void setThemeAccordingToDayState() {
        switch (day_state) {
            case ApplicationUtils.MORNING:
                setTheme(R.style.MorningTheme);
                break;
            case ApplicationUtils.NOON:
                setTheme(R.style.AfterNoonTheme);
                break;
            case ApplicationUtils.EVENING:
                setTheme(R.style.EveningTheme);
                break;
            case ApplicationUtils.NIGHT:
                setTheme(R.style.NightTheme);
                break;
        }
    }

    private void setActionbarBackground() {
        switch (day_state) {
            case ApplicationUtils.MORNING:
                toolbar.setBackgroundColor(getResources().getColor(R.color.morningActionbar));
                break;
            case ApplicationUtils.NOON:
                toolbar.setBackgroundColor(getResources().getColor(R.color.afternoonActionbar));
                break;
            case ApplicationUtils.EVENING:
                toolbar.setBackgroundColor(getResources().getColor(R.color.eveningActionbar));
                break;
            case ApplicationUtils.NIGHT:
                toolbar.setBackgroundColor(getResources().getColor(R.color.nightActionbar));
                break;
        }
    }

    private void initDrawer() {
        View headerView = nvDrawer.getHeaderView(0);
        // tvHeaderProfileName = (TextView) headerView.findViewById(R.id.tvNavHeaderProfileName);
        rlNavHeaderContent = (RelativeLayout) headerView.findViewById(R.id.rlNavHeaderContent);
        mDrawerToggle = setupDrawerToggle();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        //dlMain.setDrawerListener(mDrawerToggle);
        dlMain.addDrawerListener(mDrawerToggle);
        // Drawer icon changed
        mDrawerToggle.syncState();
    }


    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rateMyapp) {
            rateMyApp();
            return true;
        }
        if (id == R.id.action_calendar) {
//            showPopup(MainActivity.this);
            openRamadanSchedule();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void rateMyApp() {
        String appPackageName = "lets.pray.muslims";
        Uri uri = Uri.parse("market://details?id=" + appPackageName);
        Intent openIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(openIntent);
    }

    private void showPopup(Activity context) {

        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.calendar_main, null, false);
        // Creating the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(
                layout, 800, 800);

        popupWindow.setContentView(layout);
        popupWindow.setHeight(1000);
        popupWindow.setOutsideTouchable(false);
        // Clear the default translucent background
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        CalendarView cv = (CalendarView) layout.findViewById(R.id.calendarView1);
        cv.setBackgroundColor(Color.WHITE);

        popupWindow.showAtLocation(layout, Gravity.TOP, 5, 170);
    }

    private void openRamadanSchedule() {
        RamadanScheduleMaker ramadanScheduleMaker = new RamadanScheduleMaker(getFazrWaqt(), context);
        fragmentManager.beginTransaction().replace(R.id.flContent, RamadanFragment.newInstance()).commit();
        nvDrawer.setCheckedItem(R.id.nav_ramadanTimeTable);
//        navi
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, dlMain, toolbar, R.string.txt_nav_open, R.string.txt_nav_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
    }

    private void initFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        openHomeFragment();
    }

    private void openHomeFragment() {
        fragmentManager.beginTransaction().replace(R.id.flContent, HomeFragment.newInstance()).commit();
        nvDrawer.setCheckedItem(R.id.nav_home);
    }

    private void setupDrawerContent() {
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        boolean isShareApp = true;
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                isShareApp = false;
                fragment = HomeFragment.newInstance();
                break;
            case R.id.nav_ramadanTimeTable:
                isShareApp = false;
                fragment = RamadanFragment.newInstance();
                RamadanScheduleMaker ramadanScheduleMaker = new RamadanScheduleMaker(getFazrWaqt(), context);
                break;

            case R.id.nav_shareApp:
//                fragmentClass = ThirdFragment.class;
                shareApp();
                break;
            default:
                fragment = HomeFragment.newInstance();
                break;
        }
        if (!isShareApp) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
        } else {

        }
        dlMain.closeDrawers();
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=lets.pray.muslims");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private long getFazrWaqt() {
        SharedPreferences preferences = getSharedPreferences(StaticData.KEY_PREFERENCE, MODE_PRIVATE);
        long fazrWaqt = preferences.getLong(StaticData.PRAYER_TIME_FAJR, 0);
        return fazrWaqt;
    }

    @Override
    public void onBackPressed() {
        if (dlMain.isDrawerOpen(nvDrawer)) {
            dlMain.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
