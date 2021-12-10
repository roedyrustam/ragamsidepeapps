package com.sidepe.multicontent.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.models.CommentModel;

import java.util.List;

import static com.sidepe.multicontent.activities.MainActivity.login_user_id;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>
{
    private Context context;
    private List<CommentModel> commentModels;

    public CommentAdapter(Context context, List<CommentModel> commentModels) {
        this.context = context;
        this.commentModels = commentModels;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_comment, parent, false);
        CommentViewHolder commentViewHolder = new CommentViewHolder(view);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        holder.itemView.setTag(commentModels.get(position));
        CommentModel comment = commentModels.get(position);

        holder.txt_comment_user_username.setText(comment.getUser_username());
        holder.txt_comment_text.setText(comment.getComment_text());
        holder.txt_comment_time.setText(Config.TimeAgo(comment.getComment_time()));
        holder.rb_comment_rate.setRating(Float.parseFloat(comment.getComment_rate()));
        Glide.with(context)
                .load(Config.USER_IMG_URL+comment.getUser_image())
                //.apply(RequestOptions.circleCropTransform() //Circle Image
                //.apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(100)) //Rounded and Circle Image
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(100)) //Rounded Image
                        .placeholder(R.drawable.pre_loading)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate())
                .into(holder.iv_comment_user_image);

        //Set user own comment background color
        if(login_user_id.equals("Not Login")) {
            //No Action
        }else{
            if(login_user_id.equals(comment.getComment_user_id())) {
                holder.cv_rv_comment.setCardBackgroundColor(Color.parseColor("#e4f3d2"));
            }else{
                holder.cv_rv_comment.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }

    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txt_comment_user_username;
        public TextView txt_comment_text;
        public TextView txt_comment_time;
        public RatingBar rb_comment_rate;
        public ImageView iv_comment_user_image;
        public CardView cv_rv_comment;

        public CommentViewHolder(View itemView) {
            super(itemView);

            txt_comment_user_username = (TextView) itemView.findViewById(R.id.txt_comment_user_username);
            txt_comment_text = (TextView) itemView.findViewById(R.id.txt_comment_text);
            txt_comment_time = (TextView) itemView.findViewById(R.id.txt_comment_time);
            rb_comment_rate = (RatingBar) itemView.findViewById(R.id.rb_comment_rate);
            iv_comment_user_image = (ImageView) itemView.findViewById(R.id.iv_comment_user_image);
            cv_rv_comment = (CardView) itemView.findViewById(R.id.cv_rv_comment);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommentModel comment = (CommentModel) view.getTag();

                    //This is my own comment
                    if(login_user_id.equals("Not Login")) {
                        //No Action

                    }else{
                        if(login_user_id.equals(comment.getComment_user_id())) {
                            //Toast.makeText(view.getContext(), "User ID: "+comment.getComment_user_id()+" login_user_id: "+login_user_id, Toast.LENGTH_LONG).show();
                            Toast.makeText(view.getContext(), "This is your review.", Toast.LENGTH_LONG).show();
                            /*Long Press to DELETE...
                            ...
                            ...
                             */
                        }
                    }

                }

            });
        }
    }
}
