package com.sidepe.multicontent.models;

public class CommentModel {

        private String comment_id;
        private String comment_user_id;
        private String comment_text;
        private String comment_rate;
        private String comment_time;
        private String device_type_title;
        private String user_username;
        private String user_image;

    public String getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(String comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getComment_text() {
            return comment_text;
        }

        public void setComment_text(String comment_text) {
            this.comment_text = comment_text;
        }

        public String getComment_rate() {
            return comment_rate;
        }

        public void setComment_rate(String comment_rate) {
            this.comment_rate = comment_rate;
        }

        public String getComment_time() {
            return comment_time;
        }

        public void setComment_time(String comment_time) {
            this.comment_time = comment_time;
        }

        public String getDevice_type_title() {
            return device_type_title;
        }

        public void setDevice_type_title(String device_type_title) {
            this.device_type_title = device_type_title;
        }

        public String getUser_username() {
            return user_username;
        }

        public void setUser_username(String user_username) {
            this.user_username = user_username;
        }
}
