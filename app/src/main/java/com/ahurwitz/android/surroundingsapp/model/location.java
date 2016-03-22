package com.ahurwitz.android.surroundingsapp.model;

/**
 * location object
 */
public class location {
    String type;
    double[] coordinates = new double[2];

    /**
     *
     * @param type set to Point
     * @param coordinates set coordinates from JSON Response
     */
    public location(String type, double[] coordinates){
        this.type = type;
        this.coordinates = coordinates;
    }

    /**
     *
     * @return get Array of location coordinates
     */
    public double[] getCoordinates() {
        return coordinates;
    }

    /**
     *
     * @param coordinates set location coordinates
     */
    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
