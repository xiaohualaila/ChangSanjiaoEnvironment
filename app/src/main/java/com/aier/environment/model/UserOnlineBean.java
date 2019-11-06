package com.aier.environment.model;

import java.util.List;

public class UserOnlineBean {


    /**
     * type : online-user
     * data : [{"username":"hushaohu","type":["mobile"]},{"username":"0002","type":["pc"]},{"username":"hushaohua","type":["mobile"]}]
     */

    private String type;
    private List<DataBean> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * username : hushaohu
         * type : ["mobile"]
         */

        private String username;
        private List<String> type;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getType() {
            return type;
        }

        public void setType(List<String> type) {
            this.type = type;
        }
    }
}
