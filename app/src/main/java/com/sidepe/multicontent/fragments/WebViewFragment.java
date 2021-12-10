package com.sidepe.multicontent.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.utils.AppController;
import com.sidepe.multicontent.utils.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sidepe.multicontent.R;

public class WebViewFragment extends Fragment {
    WebView webView;
    CoordinatorLayout webviewCoordinatorLayout;
    TextView tv_f_webview_sub_title;

    public WebViewFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String title = this.getArguments().getString("title");
        String sub_title = this.getArguments().getString("sub_title");

        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        //Set ActionBar Title
        getActivity().setTitle(title);

        tv_f_webview_sub_title = (TextView) view.findViewById(R.id.tv_f_webview_sub_title);
        tv_f_webview_sub_title.setText(sub_title);

        webviewCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.webviewCoordinatorLayout);
        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(webviewCoordinatorLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Refresh fragment
                            /*getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frmMain, new MainFragment())
                                    .commit();*/
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        webView = (WebView) view.findViewById(R.id.wvWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(Config.Font_Size);
        //‌Before load data from server, Show loading...
        String text = "<html dir='ltr'><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:ltr; line-height:24px;}"
                + "</style></head>"
                + "<body>"
                + getString(R.string.txt_loading)
                + "</body></html>";
        webView.loadDataWithBaseURL(null,text, "text/html; charset=UTF-8", "utf-8", null);
        sendArrayRequest();

        return view;
    }


    private void sendArrayRequest() {
        final ProgressDialog pDialog;
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.txt_loading));
        pDialog.setCancelable(true);
        //pDialog.show();

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject object = response.getJSONObject(i);
                        String pageContent = object.getString("page_content");
                        //To format as HTML (For cyEditor)
                        String formattedPageContent = android.text.Html.fromHtml(pageContent).toString();

                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setFocusableInTouchMode(false);
                        webView.setFocusable(false);
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setDefaultFontSize(Config.Font_Size);
                        webView.getSettings().setDefaultTextEncodingName("UTF-8");
                        String mimeType = "text/html; charset=UTF-8";
                        String encoding = "utf-8";

                        String text = "<html dir='ltr'><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:ltr; line-height:24px}"
                                + "</style></head>"
                                + "<body>"
                                //+ formattedPageContent
                                + pageContent
                                + "</body></html>";
                        webView.loadDataWithBaseURL(null,text, mimeType, encoding, null);

                    }
                }
                catch (Exception e)
                {

                }
                pDialog.dismiss();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        };

        //Get url from previous fragment or activity
        String url = this.getArguments().getString("url");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(9000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }
}
