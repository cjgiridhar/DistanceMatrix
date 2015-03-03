package main;

import com.jayway.restassured.path.json.JsonPath;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by cgiridhar on 04/02/15.
 */
public class ApacheHttp {

    public double getDistance(Double latitudeSrc, Double longitudeSrc,
                              Double latitudeDst, Double longitudeDst){

        // Create default httpclient object
        DefaultHttpClient httpclient = new DefaultHttpClient();

        try {

            String host = "maps.googleapis.com";
            String protocol = "https";
            Integer port = 443;
            String api = "/maps/api/distancematrix/json?";

            // Create httpclient target object
            HttpHost target = new HttpHost(host, port, protocol);

            // Construct http(s) request for the target
            HttpGet getRequest = new HttpGet( api +
                    "origins=" + latitudeSrc + "," + longitudeSrc +
                    "&destinations=" + latitudeDst + "," + longitudeDst +
                    "&language=en-EN");
            System.out.println("executing request to " + target);

            // Execute the https request and get the response
            HttpResponse httpResponse = httpclient.execute(target, getRequest);
            HttpEntity entity = httpResponse.getEntity();

            // Return the http response code
            // System.out.println(httpResponse.getStatusLine());

            if (entity != null) {
                String response = EntityUtils.toString(entity);
                // System.out.println("Entity is: " + response);

                JsonPath jp = new JsonPath(response);
                // System.out.println("API Status is: " + jp.get("rows[0].elements[0].status"));
                String distance =  jp.get("rows[0].elements[0].distance.text");
                return Double.parseDouble(distance.split(" ")[0]);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return -1;
    }


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

        return d;
    }



    public static void main(String[] args) {

        ApacheHttp apacheHttp = new ApacheHttp();

        // Get the distance between 
        Double distance = apacheHttp.getDistance(37.342194,-121.955200, 41.43206,-81.38992);
        System.out.println("Distance between locations (kms): " + distance);

        Double haverSine = apacheHttp.haverSine(12.9667, 77.5667, 37.6189, -122.3750);
        System.out.println("HaverSine Distance between Bangalore amd SFO (kms): " + haverSine/1000);

        Double distance1 = apacheHttp.getDistance(55.930385, -3.118425, 50.087692, 14.421150);
        System.out.println("Distance between locations (kms): " + distance1);
    }
}