package io.mateu.ui;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.UserData;

import java.util.List;

/**
 * Hello world!
 *
 */
public interface App
{
    public abstract String getName();

    public boolean isAuthenticationNeeded();

    public abstract List<AbstractArea> getAreas();

    public void setUserData(UserData userData);

    public UserData getUserData();

    public AbstractView getPublicHome();

    public AbstractView getPrivateHome();

    public String getBaseUrl();

    public void setBaseUrl(String baseUrl);

    public String getPort();

    public void setPort(String port);

}
