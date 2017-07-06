package io.mateu.ui.core.rest;

import org.teavm.jso.ajax.XMLHttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by miguel on 3/7/17.
 */
public class ClienteRestTeavm {


    public static void main(String... args) {

        new ClienteRestTeavm().test1();

    }

    private void test1() {
        XMLHttpRequest xhr = XMLHttpRequest.create();
        xhr.onComplete(() -> receiveResponse(xhr.getResponseText()));
        xhr.setRequestHeader("Accept", "application/json");
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.open("POST", "http://xxx");
        String c = "";
        try {
            if (!"".equals(c)) c += "&";
            c += "parametro=" + URLEncoder.encode("", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        xhr.send(c);
    }

    private void receiveResponse(String responseText) {
        System.out.println(responseText);
    }

}
