package com.sidepe.multicontent.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.sidepe.multicontent.BuildConfig;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.adapters.AboutAdapter;
import com.sidepe.multicontent.models.AboutModel;
import com.sidepe.multicontent.utils.AppController;
import com.sidepe.multicontent.utils.Tools;

public class AboutFragment extends Fragment {

    public AboutFragment() { }
    CoordinatorLayout aboutCoordinatorLayout;
    //For RecyclerView
    private List<AboutModel> abouts = new ArrayList<>();
    private RecyclerView recyclerView;
    private AboutAdapter aboutAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //String myValue = this.getArguments().getString("message"); // Get variable
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        //Set ActionBar Title
        getActivity().setTitle(R.string.nav_about_app);

        //Clear arraylist if not empty
        abouts.clear();

        aboutCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.aboutCoordinatorLayout);
        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(aboutCoordinatorLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Refresh fragment
                            /*getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frmMain, new AboutFragment())
                                    .commit();*/
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        //RecycelerView Start
        recyclerView = (RecyclerView)view.findViewById(R.id.rvAbout);
        aboutAdapter = new AboutAdapter(getActivity(), abouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(aboutAdapter);
        setAboutData();

        return view;
    }

    //Generate About App String
    private void setAboutData()
    {
       abouts.add(new AboutModel("", getString(R.string.about_app_name), getString(R.string.app_name)));
       abouts.add(new AboutModel("", getString(R.string.about_app_version), getString(R.string.sub_about_app_version)+ " " + BuildConfig.VERSION_NAME));
       abouts.add(new AboutModel("", getString(R.string.about_app_email), ((AppController) getActivity().getApplication()).getSettingEmail()));
       abouts.add(new AboutModel("", getString(R.string.about_app_website), ((AppController) getActivity().getApplication()).getSettingWebsite()));
       abouts.add(new AboutModel("", getString(R.string.about_us), getString(R.string.sub_about_us)));
       abouts.add(new AboutModel("", getString(R.string.about_app_help), getString(R.string.sub_about_app_help)));
       abouts.add(new AboutModel("", getString(R.string.about_app_terms), getString(R.string.sub_about_app_terms)));
       abouts.add(new AboutModel("", getString(R.string.about_app_privacy_policy), getString(R.string.sub_about_app_privacy_policy)));
       abouts.add(new AboutModel("", getString(R.string.about_app_gdpr_law), getString(R.string.sub_about_app_gdpr_law)));
       abouts.add(new AboutModel("", getString(R.string.about_app_copyright), getString(R.string.sub_about_app_copyright)));
       abouts.add(new AboutModel("", getString(R.string.about_app_developer), getString(R.string.sub_about_app_developer)));
       aboutAdapter.notifyDataSetChanged();
    }


}
