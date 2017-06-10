package io.mateu.ui.core.client.app;

import io.mateu.ui.App;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.UserData;

import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractApplication implements App {

    public static final String PORT_VAADIN = "vaadin";
    public static final String PORT_JAVAFX = "javafx";


    private UserData userData;
    private String baseUrl;
    private String port;

    public abstract String getName();

    public boolean isAuthenticationNeeded() {

        boolean hasPrivateContent = false;
        for (AbstractArea a : getAreas()) {
            if (!a.isPublicAccess()) {
                hasPrivateContent = true;
            }
            if (!hasPrivateContent) for (AbstractModule m : a.getModules()) {
                if (!a.isPublicAccess()) {
                    hasPrivateContent = true;
                    break;
                }
            }
            if (hasPrivateContent) break;
        }
        return hasPrivateContent;

    }

    public abstract List<AbstractArea> getAreas();

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }

    public AbstractView getPublicHome() { return null; };

    public AbstractView getPrivateHome() { return null; };

    public String getBaseUrl() { return baseUrl; };

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
