package com.ahurwitz.android.surroundingsapp.model;

import java.util.ArrayList;

/**
 * Created by adamhurwitz on 3/21/16.
 */
public class Model {

    private ArrayList<Event> events;

    /**
     * @return Event ArrayList of events
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * @param events passes in events returned from call to EventService
     */
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    /**
     * Event class that matches JSON response for Retrofit to prase through
     */
    public static class Event {
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
}

//double[] coordinates = new double[2];
//location location;

/*public double[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[] coordinates) {
            this.coordinates = coordinates;
        }*/

/**
 *
 * @return get location object including type and coordinates
 */
        /*public location getLocation() {
            return location;
        }*/

/**
 *
 * @param coordinates set type and coordinates of location
 */
        /*public void setLocation(double[] coordinates) {
            this.location = new location("Point", coordinates);
        }*/
