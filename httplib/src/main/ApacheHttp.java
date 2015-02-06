package main;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.IOException;

/**
 * Created by cgiridhar on 04/02/15.
 */
public class ApacheHttp {

    public void getDistance(Double latitudeSrc, Double longitudeSrc,
                            Double latitudeDst, Double longitudeDst){

        // Create default httpclient object
        DefaultHttpClient httpclient = new DefaultHttpClient();

        try {
            // Create httpclient target object
            HttpHost target = new HttpHost("maps.googleapis.com", 443, "https");

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
            System.out.println("Response: ");
            System.out.println(httpResponse.getStatusLine());

            if (entity != null) {
                String response = EntityUtils.toString(entity);
                System.out.println("Entity is: " + response);
                JSONParser parser=new JSONParser();
                try {
                    JSONObject result = (JSONObject) parser.parse(response);
                    JSONArray destinationAddress = (JSONArray) result.get("destination_addresses");
                    System.out.println("Dest Address is:" + destinationAddress.get(0));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

    }

    public static void main(String[] args) {

        ApacheHttp apacheHttp = new ApacheHttp();

        // Get the distance between 
        apacheHttp.getDistance(37.342194,-121.955200,41.43206,-81.38992);

    }
}
