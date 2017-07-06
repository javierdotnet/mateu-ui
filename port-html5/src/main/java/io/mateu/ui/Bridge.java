package io.mateu.ui;

import org.teavm.jso.JSBody;

/**
 * Created by miguel on 2/7/17.
 */
public class Bridge {

    @JSBody(params = { "message" }, script = "window.alert(message);")
    public static native void alert(String message);


    @JSBody(params = { "message" }, script = "console.log(message);")
    public static native void log(String message);


    @JSBody(params = { }, script = "if (data.length > 0) { data.pop(); databindings.pop(); }; data.push({}); databindings.push(rivets.bind($('#main'), data[data.length - 1])); console.log('binded!')")
    public static native void bindMain();
}
