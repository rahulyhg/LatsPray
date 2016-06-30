package lets.pray.muslims.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lets.pray.muslims.R;
import lets.pray.muslims.adapter.RamadanScheduleAdapter;
import lets.pray.muslims.database.DatabaseHelper;
import lets.pray.muslims.model.RamadanSchedule;

/**
 * Created by imanik on 6/27/2016.
 */
public class PrayertrackingFragment extends android.support.v4.app.Fragment {
    RecyclerView rvSuhurIftarSchedule;
    RamadanScheduleAdapter ramadanScheduleAdapter;
    ArrayList<RamadanSchedule> ramadanSchedules;

    Context context;

    public PrayertrackingFragment() {
        // Required empty public constructor
    }

    public static PrayertrackingFragment newInstance() {
        PrayertrackingFragment fragment = new PrayertrackingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prayertracker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(View view){
    }

}
