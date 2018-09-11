/*
Copyright 2015 Google Inc. All rights reserved.
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package org.alexismp.egress;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import javax.servlet.annotation.WebServlet;

/**
 * @author alexismp
 */
@WebServlet(name="addStationServlet", urlPatterns = {"/log"})
public class AddStationServlet extends HttpServlet {

    GeoFire geofire;
    Firebase firebase;
    Map<String, Object> emptyOwner = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        
        // TODO: fix all Firebase API calls with real-time DB (or Firestore)
        firebase = new Firebase("https://shining-inferno-9452.firebaseio.com");
//        geofire = new GeoFire(firebase.child("_geofire"));
        login();
        emptyOwner.put("owner", "");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (final PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet addStationServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Updating location for all stations... </h1>");

            resetData();

            out.println("<h1>... done.</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // adds Geofire location for all 6441 stations
    private void resetData() {
        Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("stations.csv"));
        int i = 1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            final String key = "" + i;
            firebase.child(key).removeValue();
            // use single event listener so no further callbacks are made
            // (and there is no need to remove the event listener)
            System.out.println("Adding station [" + key + "] to firebase ... ");
            Map<String, Object> stationMap = lineToStationMap(line);
            firebase.child("stations").child(key).setValue(stationMap);

//            System.out.println("Updating station [" + key + "] location to geofire ... ");
//            GeoLocation location = new GeoLocation((Float) stationMap.get("latitude"), (Float) stationMap.get("longitude"));
//            geofire.setLocation(key, location);
            i++;
        }
    }

    private Map<String, Object> lineToStationMap(String line) {
        String[] data = line.split(";");
        Map<String, Object> stationMap = new HashMap<>();
        stationMap.put("lineCode", Integer.valueOf(data[0]));
        stationMap.put("name", data[1]);
        stationMap.put("type", data[2]);
        stationMap.put("latitude", Float.valueOf(data[3].replace(',', '.')));
        stationMap.put("longitude", Float.valueOf(data[4].replace(',', '.')));
        stationMap.put("owner", "");
        return stationMap;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void login() {
        try {
            System.out.println("Loging in...");
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            firebase.authWithPassword("alexis.mp@gmail.com", "foo",
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        System.out.println("Authenticated with password : " + authData.getProviderData().get("displayName"));
                        countDownLatch.countDown();
                        // Authentication just completed successfully :)
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        countDownLatch.countDown();
                    }
                });
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

}
