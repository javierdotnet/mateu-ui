package io.mateu.ui;

import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class Client {
    public static void main(String[] args) {
        HTMLDocument document = HTMLDocument.current();
        HTMLElement div = document.createElement("my-app");
        //
        //
        // div.appendChild(document.createTextNode("TeaVM generated element"));
        document.getBody().appendChild(div);
    }
}
