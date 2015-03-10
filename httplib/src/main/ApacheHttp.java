package main;


import com.jayway.restassured.path.json.JsonPath;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Created by cgiridhar on 04/02/15.
 */
public class ApacheHttp {

    // Gets distance between two cities using Google Distance Matrix
    public JSONObject googleMatrix(Double latitudeSrc, Double longitudeSrc,
                            Double latitudeDst, Double longitudeDst) throws IOException {

        // Create default httpclient object
        DefaultHttpClient httpclient = new DefaultHttpClient();
        JSONObject json = new JSONObject();

        try {

            String host = "maps.googleapis.com";
            String protocol = "https";
            Integer port = 443;
            String api = "/maps/api/distancematrix/json?";

            // Create httpclient target object
            HttpHost target = new HttpHost(host, port, protocol);


            // Construct http(s) request for the target
            HttpGet getRequest = new HttpGet("/maps/api/distancematrix/json?" +
                    "origins=" + latitudeSrc + "," + longitudeSrc +
                    "&destinations=" + latitudeDst + "," + longitudeDst +
                    "&language=en-EN");

            System.out.println("executing request to " + target);

            // Execute the https request and get the response
            HttpResponse httpResponse = httpclient.execute(target, getRequest);
            HttpEntity entity = httpResponse.getEntity();

            // Return the http response code
            System.out.println("----------------------------------------");
            // System.out.println(httpResponse.getStatusLine());

            if (entity != null) {
                String response = EntityUtils.toString(entity);
                JsonPath jp = new JsonPath(response);

//                System.out.println("API Status is: " + jp.get("rows[0].elements[0].status"));
//                String distance = jp.get("rows[0].elements[0].distance.text");
//                return Double.parseDouble(distance.split(" ")[0]);

                String status = jp.get("rows[0].elements[0].status");
                json.put("status", status);

                if(status.equalsIgnoreCase("null")) {
                    json.put("distance", jp.get("rows[0].elements[0].distance.text"));
                }
                else {
                    json.put("distance","0");
                }

            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return json;
    }

    // Gets distance between two cities using Google Distance Matrix
    public JSONObject googleMatrix(String srcCity, String srcCountry,
                                   String dstCity, String dstCountry) throws IOException {

        // Create default httpclient object
        DefaultHttpClient httpclient = new DefaultHttpClient();
        JSONObject json = new JSONObject();

        try {

            String host = "maps.googleapis.com";
            String protocol = "https";
            Integer port = 443;
            String api = "/maps/api/distancematrix/json?";

            // Create httpclient target object
            HttpHost target = new HttpHost(host, port, protocol);


            // Construct http(s) request for the target
            HttpGet getRequest = new HttpGet("/maps/api/distancematrix/json?" +
                    "origins=" + srcCity + "," + srcCountry +
                    "&destinations=" + dstCity + "," + dstCountry +
                    "&language=en-EN");

            System.out.println("executing request to " + target);

            // Execute the https request and get the response
            HttpResponse httpResponse = httpclient.execute(target, getRequest);
            HttpEntity entity = httpResponse.getEntity();

            // Return the http response code
            System.out.println("----------------------------------------");
            // System.out.println(httpResponse.getStatusLine());

            if (entity != null) {
                String response = EntityUtils.toString(entity);
                JsonPath jp = new JsonPath(response);

//                System.out.println("API Status is: " + jp.get("rows[0].elements[0].status"));
//                String distance = jp.get("rows[0].elements[0].distance.text");
//                return Double.parseDouble(distance.split(" ")[0]);

                String status = jp.get("rows[0].elements[0].status");
                json.put("status", status);

                if(status.equalsIgnoreCase("null")) {
                    json.put("distance", jp.get("rows[0].elements[0].distance.text"));
                }
                else {
                    json.put("distance","0");
                }

            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return json;
    }

    // Gets distance between two points on the globe using HaverSine Method
    public double haverSine(Double latitudeSrc, Double longitudeSrc,
                              Double latitudeDst, Double longitudeDst){

        double R = 6371000;

        double phai1 = Math.toRadians(latitudeSrc);
        double phai2 = Math.toRadians(latitudeDst);

        double deltaPhai = Math.toRadians(latitudeDst-latitudeSrc);
        double deltaLambda = Math.toRadians(longitudeDst-longitudeSrc);

        double a = Math.sin(deltaPhai/2) * Math.sin(deltaPhai/2) +
                   Math.cos(phai1) * Math.cos(phai2) * Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;

        return d/1000;
    }

    // Gets two JSON Objects as Params that has long,lat and city, country names
    // Goes for google api method first, if it fails, goes for Haversine method
    public Double getDistance(JSONObject from, JSONObject to) throws Throwable{
        System.out.println("Data type is: " + from.get("latitude").getClass());


        Double latitudeSrc = Double.parseDouble(from.get("latitude").toString());
        Double longitudeSrc = Double.parseDouble(from.get("longitude").toString());

        Double latitudeDst = Double.parseDouble(to.get("latitude").toString());
        Double longitudeDst = Double.parseDouble(to.get("longitude").toString());

        System.out.println("PARAMS ARE: " + latitudeSrc.toString() + longitudeSrc.toString()
        + latitudeDst.toString() + longitudeDst.toString());

        JSONObject googleJson = googleMatrix(latitudeSrc, latitudeDst, longitudeSrc, longitudeDst);

        if(googleJson.get("status").toString().equalsIgnoreCase("NOT_FOUND") ||
                googleJson.get("status").toString().equalsIgnoreCase("ZERO_RESULTS")){
            return haverSine(latitudeSrc, longitudeSrc, latitudeDst, longitudeDst);
        }

        return Double.parseDouble(googleJson.get("status").toString());
    }

    public static void main(String[] args) throws Throwable {

        ApacheHttp apacheHttp = new ApacheHttp();

//        JSONObject googleJson = apacheHttp.googleMatrix(37.342194, -121.955200, 41.43206, -81.38992);
//        System.out.println("Distance between locations (kms): " + googleJson.toString());
//
//        Double haverSine = apacheHttp.haverSine(12.9667, 77.5667, 37.6189, -122.3750);
//        System.out.println("HaverSine Distance between Bangalore amd SFO (kms): " + haverSine/1000);

//        Double distance1 = apacheHttp.googleMatrix(55.930385, -3.118425, 50.087692, 14.421150);
//        System.out.println("Distance between locations (kms): " + distance1);

        JSONObject jsonSrc = new JSONObject();
        jsonSrc.put("city","Bangalore");
        jsonSrc.put("country","India");
        jsonSrc.put("latitude","12.9667");
        jsonSrc.put("longitude","77.5667");

        JSONObject jsonDst = new JSONObject();
        jsonDst.put("city","SFO");
        jsonDst.put("country","USA");
        jsonDst.put("latitude","37.6189");
        jsonDst.put("longitude","-122.3750");

        Double dist = apacheHttp.getDistance(jsonSrc, jsonDst);
        System.out.println("Distance between locations (kms): " + dist);


    }

}

