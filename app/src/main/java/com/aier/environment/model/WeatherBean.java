package com.aier.environment.model;

import java.util.List;

public class WeatherBean {

    /**
     * success : true
     * result : {"yesterday":{"date":"3日星期日","high":"高温 23℃","fx":"无持续风向","low":"低温 13℃","fl":"<![CDATA[<3级]]>","type":"多云"},"city":"杭州","forecast":[{"date":"4日星期一","high":"高温 22℃","fengli":"<![CDATA[3-4级]]>","low":"低温 11℃","fengxiang":"东北风","type":"多云"},{"date":"5日星期二","high":"高温 20℃","fengli":"<![CDATA[3-4级]]>","low":"低温 11℃","fengxiang":"北风","type":"晴"},{"date":"6日星期三","high":"高温 21℃","fengli":"<![CDATA[3-4级]]>","low":"低温 11℃","fengxiang":"西风","type":"晴"},{"date":"7日星期四","high":"高温 19℃","fengli":"<![CDATA[4-5级]]>","low":"低温 10℃","fengxiang":"北风","type":"小雨"},{"date":"8日星期五","high":"高温 19℃","fengli":"<![CDATA[3-4级]]>","low":"低温 9℃","fengxiang":"北风","type":"多云"}],"ganmao":"昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。","wendu":"19"}
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
         * yesterday : {"date":"3日星期日","high":"高温 23℃","fx":"无持续风向","low":"低温 13℃","fl":"<![CDATA[<3级]]>","type":"多云"}
         * city : 杭州
         * forecast : [{"date":"4日星期一","high":"高温 22℃","fengli":"<![CDATA[3-4级]]>","low":"低温 11℃","fengxiang":"东北风","type":"多云"},{"date":"5日星期二","high":"高温 20℃","fengli":"<![CDATA[3-4级]]>","low":"低温 11℃","fengxiang":"北风","type":"晴"},{"date":"6日星期三","high":"高温 21℃","fengli":"<![CDATA[3-4级]]>","low":"低温 11℃","fengxiang":"西风","type":"晴"},{"date":"7日星期四","high":"高温 19℃","fengli":"<![CDATA[4-5级]]>","low":"低温 10℃","fengxiang":"北风","type":"小雨"},{"date":"8日星期五","high":"高温 19℃","fengli":"<![CDATA[3-4级]]>","low":"低温 9℃","fengxiang":"北风","type":"多云"}]
         * ganmao : 昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。
         * wendu : 19
         */

        private YesterdayBean yesterday;
        private String city;
        private String ganmao;
        private String wendu;
        private List<ForecastBean> forecast;

        public YesterdayBean getYesterday() {
            return yesterday;
        }

        public void setYesterday(YesterdayBean yesterday) {
            this.yesterday = yesterday;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getGanmao() {
            return ganmao;
        }

        public void setGanmao(String ganmao) {
            this.ganmao = ganmao;
        }

        public String getWendu() {
            return wendu;
        }

        public void setWendu(String wendu) {
            this.wendu = wendu;
        }

        public List<ForecastBean> getForecast() {
            return forecast;
        }

        public void setForecast(List<ForecastBean> forecast) {
            this.forecast = forecast;
        }

        public static class YesterdayBean {
            /**
             * date : 3日星期日
             * high : 高温 23℃
             * fx : 无持续风向
             * low : 低温 13℃
             * fl : <![CDATA[<3级]]>
             * type : 多云
             */

            private String date;
            private String high;
            private String fx;
            private String low;
            private String fl;
            private String type;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getFx() {
                return fx;
            }

            public void setFx(String fx) {
                this.fx = fx;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        public static class ForecastBean {
            /**
             * date : 4日星期一
             * high : 高温 22℃
             * fengli : <![CDATA[3-4级]]>
             * low : 低温 11℃
             * fengxiang : 东北风
             * type : 多云
             */

            private String date;
            private String high;
            private String fengli;
            private String low;
            private String fengxiang;
            private String type;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getFengli() {
                return fengli;
            }

            public void setFengli(String fengli) {
                this.fengli = fengli;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getFengxiang() {
                return fengxiang;
            }

            public void setFengxiang(String fengxiang) {
                this.fengxiang = fengxiang;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
