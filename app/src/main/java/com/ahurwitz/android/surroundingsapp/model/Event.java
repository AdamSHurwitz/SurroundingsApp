package com.ahurwitz.android.surroundingsapp.model;

/**
 * Created by adamhurwitz on 3/21/16.
 */
public class Event {

        String incidntnum;
        String pddistrict;
        String x;
        String y;

        /**
         *
         * @return get incident number of Event
         */
        public String getIncidntnum() {
            return incidntnum;
        }

        /**
         *
         * @param incidntnum set incident number of Event
         */
        public void setIncidntnum(String incidntnum) {
            this.incidntnum = incidntnum;
        }


        /**
         *
         * @return get district of where Event occurred
         */
        public String getPddistrict() {
            return pddistrict;
        }

        /**
         *
         * @param pddistrict set district of where Event occured
         */
        public void setPddistrict(String pddistrict) {
            this.pddistrict = pddistrict;
        }

        /**
         *
         * @return get x coordinate of Event
         */
        public String getX() {
            return x;
        }

        /**
         *
         * @param x set x coordinate of Event
         */
        public void setX(String x) {
            this.x = x;
        }

        /**
         *
         * @return get y coordinate of Event
         */
        public String getY() {
            return y;
        }

        /**
         *
         * @param y get y coordinate of Event
         */
        public void setY(String y) {
            this.y = y;
        }
}


