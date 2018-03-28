package io.mateu.ui.core.client.app;

import io.mateu.ui.App;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractApplication implements App {

    public static final String PORT_VAADIN = "vaadin";
    public static final String PORT_JAVAFX = "javafx";


    private UserData userData;
    private String baseUrl;
    private String port;
    private Map<MenuEntry, String> menuIds = null;
    private Map<String, MenuEntry> menuIdsReverse = null;
    List<AbstractArea> areas = null;
    private AbstractArea area;
    private MenuEntry posicion;

    public abstract String getName();

    public boolean isAuthenticationNeeded() {

        boolean hasPrivateContent = false;
        for (AbstractArea a : getAreas()) {
            if (!a.isPublicAccess()) {
                hasPrivateContent = true;
            }
            if (!hasPrivateContent) for (AbstractModule m : a.buildModules()) {
                if (!a.isPublicAccess()) {
                    hasPrivateContent = true;
                    break;
                }
            }
            if (hasPrivateContent) break;
        }
        return hasPrivateContent;

    }

    public String getMenuLocator(MenuEntry menu) {
        if (menuIds == null) buildMenuIds();
        return menuIds.get(menu);
    }

    private void buildMenuIds() {
        menuIds = new HashMap<>();
        menuIdsReverse = new HashMap<>();

        for (AbstractArea a : areas) {
            for (AbstractModule m : a.getModules()) {
                for (MenuEntry e : m.getMenu()) {
                    addMenuId(e);
                }
            }
        }

    }

    private void addMenuId(MenuEntry e) {
        String id = "" + menuIds.size();
        e.setId(id);
        menuIds.put(e, id);
        menuIdsReverse.put(id, e);
        if (e instanceof AbstractMenu) {
            AbstractMenu m = (AbstractMenu) e;
            for (MenuEntry x : m.getEntries()) {
                addMenuId(x);
            }
        }
    }

    public MenuEntry getMenu(String locator) {
        if (menuIdsReverse == null) buildMenuIds();
        return menuIdsReverse.get(locator);
    }

    public boolean isSignUpSupported() {
        return false;
    }

    public List<AbstractArea> getAreas() {
        if (areas == null) {
            areas = buildAreas();
            int pos = 0;
            for (AbstractArea a : areas) {
                a.setId("" + pos++);
            }
            buildMenuIds();
        }
        return areas;
    }

    public abstract List<AbstractArea> buildAreas();

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

    public List<String> getSupportedLanguages() {
        return null;
    }

    public String getOriginLanguage() {
        return null;
    }

    public String translate(String text, String toLanguage) {
        return null;
    }

    public void askForTranslation(String text, String fromLanguage, String toLanguage) {

    }

    public AbstractArea getArea() {
        return area;
    }

    public void setArea(AbstractArea area) {
        this.area = area;
    }

    public MenuEntry getPosicion() {
        return posicion;
    }

    public void setPosicion(MenuEntry posicion) {
        this.posicion = posicion;
    }
}
