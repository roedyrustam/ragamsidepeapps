package com.sidepe.multicontent.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.models.AboutModel;

import java.util.List;

import com.sidepe.multicontent.R;
import com.sidepe.multicontent.fragments.WebViewFragment;

import static com.sidepe.multicontent.activities.MainActivity.setting_email;
import static com.sidepe.multicontent.activities.MainActivity.setting_website;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.AboutViewHolder>
{
    public Context context;
    List<AboutModel> abouts;

    public AboutAdapter(Context context, List<AboutModel> abouts) {
        this.context = context;
        this.abouts = abouts;
    }

    @NonNull
    @Override
    public AboutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_about, parent, false);
        return new AboutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutViewHolder holder, final int position)
    {
        holder.itemView.setTag(abouts.get(position));
        AboutModel abt = abouts.get(position);

        holder.txtTitle.setText(abt.getAboutTitle());
        holder.txtSubTitle.setText(abt.getAboutSubTitle());
    }

    @Override
    public int getItemCount() {
        return abouts.size();
    }

    public class AboutViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imgImage;
        public TextView txtTitle;
        public TextView txtSubTitle;

        public AboutViewHolder(View itemView) {
            super(itemView);
            imgImage = (ImageView) itemView.findViewById(R.id.imgAbout);
            txtTitle = (TextView) itemView.findViewById(R.id.tvAboutTitle);
            txtSubTitle = (TextView) itemView.findViewById(R.id.tvAboutSubTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AboutModel abt = (AboutModel) view.getTag();

                    //Check position of RecyclerView
                    int thePosition = getPosition();
                    if (thePosition == 6) //Privacy Policy and Terms
                    {
                        WebViewFragment webViewFragmentAbout = new WebViewFragment();
                        //Pass variable to fragment
                        Bundle bundle = new Bundle();
                        String theTitle = context.getString(R.string.sub_about_app_terms);
                        String theUrl = Config.PAGE_TERMS + "?api_key=" + Config.API_KEY;
                        bundle.putString("title", theTitle);
                        bundle.putString("sub_title", theTitle);
                        bundle.putString("url", theUrl);
                        webViewFragmentAbout.setArguments(bundle);

                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                                .replace(R.id.frmMain, webViewFragmentAbout)
                                .addToBackStack(null)
                                .commit();
                    }else if (thePosition == 10) { //Developer Website
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(setting_website));
                        context.startActivity(browserIntent);

                    }else if (thePosition == 3) { //App Website
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(setting_website));
                        context.startActivity(browserIntent);

                    }else if (thePosition == 2) { //App Email
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + setting_email));
                        context.startActivity(Intent.createChooser(emailIntent, "Chooser Title"));

                    }else if (thePosition == 4) { //About Us
                        WebViewFragment webViewFragmentAbout = new WebViewFragment();
                        //Pass variable to fragment
                        Bundle bundle = new Bundle();
                        String theTitle = context.getString(R.string.sub_about_us);
                        String theSubTitle = context.getString(R.string.sub_about_us);
                        String theUrl = Config.PAGE_ABOUT_US + "?api_key=" + Config.API_KEY;
                        bundle.putString("title", theTitle);
                        bundle.putString("sub_title", theSubTitle);
                        bundle.putString("url", theUrl);
                        webViewFragmentAbout.setArguments(bundle);

                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                                .replace(R.id.frmMain, webViewFragmentAbout)
                                .addToBackStack(null)
                                .commit();
                    }
                    else if (thePosition == 5) { //Help
                        WebViewFragment webViewFragmentAbout = new WebViewFragment();
                        //Pass variable to fragment
                        Bundle bundle = new Bundle();
                        String theTitle = context.getString(R.string.about_app_help);
                        String theSubTitle = context.getString(R.string.sub_about_app_help);
                        String theUrl = Config.PAGE_HELP + "?api_key=" + Config.API_KEY;
                        bundle.putString("title", theTitle);
                        bundle.putString("sub_title", theSubTitle);
                        bundle.putString("url", theUrl);
                        webViewFragmentAbout.setArguments(bundle);

                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                                .replace(R.id.frmMain, webViewFragmentAbout)
                                .addToBackStack(null)
                                .commit();
                    }
                    else if (thePosition == 7) { //About Us
                        WebViewFragment webViewFragmentAbout = new WebViewFragment();
                        //Pass variable to fragment
                        Bundle bundle = new Bundle();
                        String theTitle = context.getString(R.string.sub_about_app_privacy_policy);
                        String theSubTitle = context.getString(R.string.sub_about_app_privacy_policy);
                        String theUrl = Config.PAGE_PRIVACY_POLICY + "?api_key=" + Config.API_KEY;
                        bundle.putString("title", theTitle);
                        bundle.putString("sub_title", theSubTitle);
                        bundle.putString("url", theUrl);
                        webViewFragmentAbout.setArguments(bundle);

                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                                .replace(R.id.frmMain, webViewFragmentAbout)
                                .addToBackStack(null)
                                .commit();
                    }else if (thePosition == 8) { //GDPR Law
                        WebViewFragment webViewFragmentAbout = new WebViewFragment();
                        //Pass variable to fragment
                        Bundle bundle = new Bundle();
                        String theTitle = context.getString(R.string.about_app_gdpr_law);
                        String theSubTitle = context.getString(R.string.sub_about_app_gdpr_law);
                        String theUrl = Config.PAGE_GDPR + "?api_key=" + Config.API_KEY;
                        bundle.putString("title", theTitle);
                        bundle.putString("sub_title", theSubTitle);
                        bundle.putString("url", theUrl);
                        webViewFragmentAbout.setArguments(bundle);

                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                                .replace(R.id.frmMain, webViewFragmentAbout)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        }
    }
}
