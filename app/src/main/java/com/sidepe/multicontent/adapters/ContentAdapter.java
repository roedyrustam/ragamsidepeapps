package com.sidepe.multicontent.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.activities.AccountUpgrade;
import com.sidepe.multicontent.activities.OneContentDownloadActivity;
import com.sidepe.multicontent.activities.OneContentLinkActivity;
import com.sidepe.multicontent.activities.OneContentMusicActivity;
import com.sidepe.multicontent.activities.OneContentProductActivity;
import com.sidepe.multicontent.activities.OneContentTextActivity;
import com.sidepe.multicontent.activities.OneContentVideoActivity;
import com.sidepe.multicontent.models.ContentModel;

import java.util.List;

import com.sidepe.multicontent.R;

import static com.sidepe.multicontent.activities.MainActivity.user_role_id;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder>
{
    private Context context;
    private List<ContentModel> contentModel;
    Intent intentContent;

    public ContentAdapter(Context context, List<ContentModel> contentModel) {
        this.context = context;
        this.contentModel = contentModel;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_content_list, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.itemView.setTag(contentModel.get(position));

        ContentModel content = contentModel.get(position);

        holder.contentTitle.setText(content.getContent_title());
        Glide.with(context)
                .load(Config.CONTENT_IMG_URL+content.getContent_image())
                .apply(new RequestOptions()
                        //.bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 0, 0))
                        .placeholder(R.drawable.pre_loading)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate())
                .into(holder.contentImage);
        holder.contentDuration.setText(content.getContent_duration());
        holder.contentViewed.setText(content.getContent_viewed());
        holder.categoryTitle.setText(content.getCategory_title());
        holder.contentPublishDate.setText(Config.TimeAgo(content.getContent_publish_date()));
        holder.contentTypeTitle.setText(content.getContent_type_title());
        holder.userRoleTitle.setText(content.getUser_role_title());
    }

    @Override
    public int getItemCount() {
        return contentModel.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder
    {
        public TextView contentId;
        public TextView contentTitle;
        public ImageView contentImage;
        public TextView contentUrl;
        public TextView contentPrice;
        public TextView contentTypeId;
        public TextView contentAccess;
        public TextView contentUserRoleId;
        public TextView contentRoleTitle;
        public TextView userRoleTitle;
        public TextView contentDuration;
        public TextView contentViewed;
        public TextView contentLiked;
        public TextView contentPublishDate;
        public TextView contentFeatured;
        public TextView contentSpecial;
        public TextView contentOrientation;
        public TextView categoryTitle;
        public TextView contentTypeTitle;

        public ContentViewHolder(View itemView) {
            super(itemView);
            contentTitle = (TextView) itemView.findViewById(R.id.tv_content_list_title);
            contentImage = (ImageView) itemView.findViewById(R.id.iv_content_list_image);
            categoryTitle = (TextView) itemView.findViewById(R.id.tv_content_list_category);
            contentDuration = (TextView) itemView.findViewById(R.id.tv_content_list_duration);
            contentPublishDate = (TextView) itemView.findViewById(R.id.tv_content_list_date_time);
            contentViewed = (TextView) itemView.findViewById(R.id.tv_content_list_total_viewed);
            contentTypeTitle = (TextView) itemView.findViewById(R.id.tv_content_list_type_title);
            userRoleTitle = (TextView) itemView.findViewById(R.id.tv_user_role_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ContentModel content = (ContentModel) view.getTag();

                    String contentId = content.getContent_id();
                    String contentTitle = content.getContent_title();
                    String categoryTitle = content.getCategory_title();
                    String contentImage = content.getContent_image();
                    String contentUrl = content.getContent_url();
                    String contentDuration = content.getContent_duration();
                    String contentViewed = content.getContent_viewed();
                    String contentPublishDate = content.getContent_publish_date();
                    String contentTypeId = content.getContent_type_id();
                    String contentTypeTitle = content.getContent_type_title();
                    String contentUserRoleId = content.getContent_user_role_id();
                    String userRoleTitle = content.getUser_role_title();
                    String contentOrientation = content.getContent_orientation();


                    if(contentTypeId.equals("11")) { //Video & Movie
                        intentContent = new Intent(context, OneContentVideoActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_play_video));

                    }else if(contentTypeId.equals("12")) { //Music & Audio
                        intentContent = new Intent(context, OneContentMusicActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_play_music));

                    }else if(contentTypeId.equals("13")) { //HTML5 Game
                        intentContent = new Intent(context, OneContentLinkActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_play_game));

                    }else if(contentTypeId.equals("14")) { //Text & Article
                        intentContent = new Intent(context, OneContentTextActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open));

                    }else if(contentTypeId.equals("15")) { //PDF Reader
                        intentContent = new Intent(context, OneContentLinkActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open_pdf));

                    }else if(contentTypeId.equals("16")) { //News
                        intentContent = new Intent(context, OneContentTextActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open));

                    }else if(contentTypeId.equals("17")) { //Product
                        intentContent = new Intent(context, OneContentProductActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_show));

                    }else if(contentTypeId.equals("18")) { //Buy & Sell
                        intentContent = new Intent(context, OneContentProductActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_show));

                    }else if(contentTypeId.equals("19")) { //City Guide
                        intentContent = new Intent(context, OneContentProductActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_show));

                    }else if(contentTypeId.equals("20")) { //Download
                        intentContent = new Intent(context, OneContentDownloadActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_download));

                    }else if(contentTypeId.equals("21")) { //Hyperlink
                        intentContent = new Intent(context, OneContentLinkActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open));

                    }else if(contentTypeId.equals("22")) { //Images Gallery
                        /*intentContent = new Intent(context, OneContentProductActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_show));*/

                    }else{
                        intentContent = new Intent(context, OneContentLinkActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open));
                    }


                    intentContent.putExtra("contentId", contentId);
                    intentContent.putExtra("contentTitle", contentTitle);
                    intentContent.putExtra("categoryTitle", categoryTitle);
                    intentContent.putExtra("contentImage", contentImage);
                    intentContent.putExtra("contentUrl", contentUrl);
                    intentContent.putExtra("contentDuration", contentDuration);
                    intentContent.putExtra("contentViewed", contentViewed);
                    intentContent.putExtra("contentPublishDate", contentPublishDate);
                    intentContent.putExtra("contentTypeId", contentTypeId);
                    intentContent.putExtra("contentTypeTitle", contentTypeTitle);
                    intentContent.putExtra("contentUserRoleId", contentUserRoleId);
                    intentContent.putExtra("userRoleTitle", userRoleTitle);
                    intentContent.putExtra("contentOrientation", contentOrientation);

                    //Check content user role id
                    if(user_role_id.equals("Not Login")) {
                        //Guest user can't access VIP game with ID: 6
                        if(contentUserRoleId.equals("6")) {
                            //Toast.makeText(context, "Role: "+contentUserRoleId,Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(context);
                            builderCheckUpdate.setTitle(R.string.txt_access_permission);
                            builderCheckUpdate.setMessage(R.string.txt_this_content_is_for_vip_user_role);
                            builderCheckUpdate.setCancelable(false);

                            builderCheckUpdate.setPositiveButton(
                                    R.string.txt_upgrade_role,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intentUpgrade = new Intent(context, AccountUpgrade.class);
                                            context.startActivity(intentUpgrade);
                                        }
                                    });

                            builderCheckUpdate.setNegativeButton(
                                    R.string.txt_cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                            alert1CheckUpdate.show();

                        }else{
                            context.startActivity(intentContent);
                        }

                    }else{
                        if(contentUserRoleId.equals("6")) {
                            //Check if user is VIP or Regular
                            if(user_role_id.equals("6") || user_role_id.equals("1") || user_role_id.equals("2") || user_role_id.equals("3") || user_role_id.equals("4")) {
                                context.startActivity(intentContent);

                            }else{
                                //Toast.makeText(context, "Role: "+contentUserRoleId,Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(context);
                                builderCheckUpdate.setTitle(R.string.txt_access_permission);
                                builderCheckUpdate.setMessage(R.string.txt_this_content_is_for_vip_user_role);
                                builderCheckUpdate.setCancelable(false);

                                builderCheckUpdate.setPositiveButton(
                                        R.string.txt_upgrade_role,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intentUpgrade = new Intent(context, AccountUpgrade.class);
                                                context.startActivity(intentUpgrade);
                                            }
                                        });

                                builderCheckUpdate.setNegativeButton(
                                        R.string.txt_cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                                alert1CheckUpdate.show();
                            }

                        }else{
                            context.startActivity(intentContent);
                        }
                    }
                }

            });
        }
    }
}
