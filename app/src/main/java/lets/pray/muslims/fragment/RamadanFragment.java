package lets.pray.muslims.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lets.pray.muslims.model.RamadanSchedule;
import lets.pray.muslims.R;
import lets.pray.muslims.adapter.RamadanScheduleAdapter;
import lets.pray.muslims.database.DatabaseHelper;

public class RamadanFragment extends Fragment {

    RecyclerView rvSuhurIftarSchedule;
    RamadanScheduleAdapter ramadanScheduleAdapter;
    ArrayList<RamadanSchedule> ramadanSchedules;

    Context context;

    public RamadanFragment() {
        // Required empty public constructor
    }

    public static RamadanFragment newInstance() {
        RamadanFragment fragment = new RamadanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        ramadanSchedules = databaseHelper.getRamadanSchedules();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ramadan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(View view){
        rvSuhurIftarSchedule = (RecyclerView) view.findViewById(R.id.rvSuhurIftarSchedule);
        rvSuhurIftarSchedule.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvSuhurIftarSchedule.setLayoutManager(llm);
        ramadanScheduleAdapter = new RamadanScheduleAdapter(ramadanSchedules,context,(AppCompatActivity) getActivity());
        rvSuhurIftarSchedule.setAdapter(ramadanScheduleAdapter);
    }

}
