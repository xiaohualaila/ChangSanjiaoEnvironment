package com.aier.environment.model;

import java.util.List;

public class FriendBean {

    /**
     * success : true
     * result : {"start":0,"count":1,"total":1,"users":[{"nickname":"堃","ctime":"2019-10-22 10:07:18","mtime":"2019-10-22 10:07:18","username":"18857232505"}]}
     */

    private boolean success;
    private ResultBean result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * start : 0
         * count : 1
         * total : 1
         * users : [{"nickname":"堃","ctime":"2019-10-22 10:07:18","mtime":"2019-10-22 10:07:18","username":"18857232505"}]
         */

        private int start;
        private int count;
        private int total;
        private List<UsersBean> users;

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<UsersBean> getUsers() {
            return users;
        }

        public void setUsers(List<UsersBean> users) {
            this.users = users;
        }

        public static class UsersBean {
            /**
             * nickname : 堃
             * ctime : 2019-10-22 10:07:18
             * mtime : 2019-10-22 10:07:18
             * username : 18857232505
             */

            private String nickname;
            private String ctime;
            private String mtime;
            private String username;
            private String avatar;
            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getCtime() {
                return ctime;
            }

            public void setCtime(String ctime) {
                this.ctime = ctime;
            }

            public String getMtime() {
                return mtime;
            }

            public void setMtime(String mtime) {
                this.mtime = mtime;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
