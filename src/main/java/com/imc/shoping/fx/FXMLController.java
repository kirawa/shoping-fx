package com.imc.shoping.fx;

import java.io.BufferedReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSException;

public class FXMLController implements Initializable {
    
    @FXML
    public WebView webView;
    public CheckMenuItem checkMenuCostWidgetController;
    public CheckMenuItem checkMenuCatWidgetController;
    public CheckMenuItem checkMenuPlacesWidgetController;
    
    
     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        WebEngine webEngine = webView.getEngine();
        webEngine.load(FXMLController.class.getResource("/html/index.html").toExternalForm());
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                        if (newValue == Worker.State.SUCCEEDED) {
                            JSObject jsObject = (JSObject) webView.getEngine().executeScript("window");
                            jsObject.call("initialize", FXMLController.this);
                        }
                    }
                });
            }
        });
    }
     
    
    public JSONArray getRestServiceResult(){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            jsonArray = new JSONArray();
            Document doc = Jsoup.connect("http://localhost:8080/shoping/webresources/com.imc.shoping.entities.orders/").get();
            for (Element e : doc.select("orders")) {
                jsonObject = new JSONObject();
                jsonObject.put("orderId",Integer.parseInt(e.select("ordersId").text()));
                jsonObject.put("catId",Integer.parseInt(e.select("catId").select("id").text()));
                jsonObject.put("placesId",Integer.parseInt(e.select("placesId").select("id").text()));
                jsonObject.put("itemName",e.select("itemName").text());
                jsonObject.put("placesName",e.select("placesName").text());
                jsonObject.put("catName",e.select("catName").text());
                jsonObject.put("time", dateFormat(e.select("time").text()));
                jsonObject.put("itemCost",e.select("itemCost").text());
                jsonArray.put(jsonObject);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public String dateFormat(String time){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String reformattedStr= "";
        try {
             reformattedStr = myFormat.format(fromUser.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public boolean checkState(CheckMenuItem item){
        return item.isSelected();
    }

    public void setDisableCheckMenuItem(String idWidget){
        switch (idWidget) {
            case "costWidget":
                checkMenuCostWidgetController.setSelected(false);
                break;
            case "catWidget":
                checkMenuCatWidgetController.setSelected(false);
                break;
            case "placesWidget":
                checkMenuPlacesWidgetController.setSelected(false);
                break;
        }
    }

    public void costWidgetController() {
        JSObject jsObject = (JSObject)webView.getEngine().executeScript("window");
        jsObject.call("costWidgetController",checkState(checkMenuCostWidgetController));
    }

    public void catWidgetController() {
        JSObject jsObject = (JSObject)webView.getEngine().executeScript("window");
        jsObject.call("catWidgetController",checkState(checkMenuCatWidgetController));
    }

    public void placesWidgetController() {
        JSObject jsObject = (JSObject)webView.getEngine().executeScript("window");
        jsObject.call("placesWidgetController",checkState(checkMenuPlacesWidgetController));
    }
}
