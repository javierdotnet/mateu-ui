package io.mateu.ui.core.client.app;

import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.UserData;

import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractApplication {

    private UserData userData;
    private String baseUrl;

    public abstract String getName();

    public boolean isAuthenticationNeeded() {
        return true;
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


}
