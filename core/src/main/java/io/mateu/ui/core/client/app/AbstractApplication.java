package io.mateu.ui.core.client.app;

import com.google.common.collect.Lists;
import io.mateu.ui.App;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;

import java.util.ArrayList;
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
    private Map<AbstractArea, String> areaIds;
    private Map<MenuEntry, String> menuIds;
    private Map<String, AbstractArea> areaIdsReversed;
    private Map<String, MenuEntry> menuIdsReversed;
    private Map<MenuEntry, List<MenuEntry>> menuPaths;
    List<AbstractArea> areas = null;

    public boolean isSearchAvailable() {
        return false;
    }

    public boolean isFavouritesAvailable() {
        return false;
    }

    public boolean isLastEditedAvailable() {
        return false;
    }

    public void getFavourites(UserData user, Callback<Data> callback) {
        callback.onFailure(new Exception("Favourites are not available for this app."));
    }

    public void search(UserData user, Callback<Data> callback) {
        callback.onFailure(new Exception("Search is not available for this app."));
    }

    public void getLastEdited(UserData user, Callback<Data> callback) {
        callback.onFailure(new Exception("Search is not available for this app."));
    }


    public abstract String getName();

    public boolean isAuthenticationNeeded() {

        boolean hasPrivateContent = false;
        for (AbstractArea a : buildAreas()) {
            if (!a.isPublicAccess()) {
                hasPrivateContent = true;
            }
            if (hasPrivateContent) break;
        }
        return hasPrivateContent;

    }



    public String getAreaId(AbstractArea area) {
        if (areaIds == null) buildAreaAndMenuIds();
        return areaIds.get(area);
    }

    public AbstractArea getArea(String id) {
        if (areaIdsReversed == null) buildAreaAndMenuIds();
        return areaIdsReversed.get(id);
    }

    public String getMenuId(MenuEntry menu) {
        if (menuIds == null) buildAreaAndMenuIds();
        return menuIds.get(menu);
    }

    public MenuEntry getMenu(String id) {
        if (menuIdsReversed == null) buildAreaAndMenuIds();
        return menuIdsReversed.get(id);
    }

    public List<MenuEntry> getPath(MenuEntry e) {
        if (menuPaths == null) buildAreaAndMenuIds();
        return menuPaths.get(e);
    }

    public boolean isSignUpSupported() {
        return false;
    }

    public List<AbstractArea> getAreas() {
        if (areas == null) {
            areas = new ArrayList<>();
            boolean autentico = MateuUI.getApp().getUserData() != null;
            for (AbstractArea a : buildAreas()) {
                if ((!autentico && a.isPublicAccess()) || (autentico && !a.isPublicAccess())) areas.add(a);
            }
            buildAreaAndMenuIds();
        }
        return areas;
    }


    private void buildAreaAndMenuIds() {
        areaIds = new HashMap<>();
        menuIds = new HashMap<>();
        areaIdsReversed = new HashMap<>();
        menuIdsReversed = new HashMap<>();
        menuPaths = new HashMap<>();

        for (AbstractArea a : getAreas()) {
            String id = a.getName().toLowerCase().replaceAll(" ", "_");
            int pos = 0;
            String idbase = id;
            while (areaIdsReversed.containsKey(id)) id = idbase + pos++;
            a.setId(id);
            areaIds.put(a, id);
            areaIdsReversed.put(id, a);

            for (AbstractModule m : a.getModules()) {
                for (MenuEntry e : m.getMenu()) {
                    buildMenuIds("", new ArrayList<>(), e);
                }
            }
        }
    }

    private void buildMenuIds(String prefijo, List<MenuEntry> incomingPath, MenuEntry e) {
        String id = e.getName().toLowerCase().replaceAll(" ", "_");

        int pos = 0;
        String mid = prefijo + id;
        String idbase = mid;
        while (!"void".equals(mid) && menuIdsReversed.containsKey(mid)) {
            mid = idbase + pos++;
        }
        e.setId(mid);
        menuIds.put(e, mid);
        menuIdsReversed.put(mid, e);
        List<MenuEntry> path = menuPaths.get(e);
        if (path == null) menuPaths.put(e, path = new ArrayList<>());
        path.addAll(incomingPath);

        if (e instanceof AbstractMenu) {
            prefijo += id + "__";
            List<MenuEntry> outgoingPath = new ArrayList<>(path);
            outgoingPath.add(e);
            for (MenuEntry x : ((AbstractMenu) e).getEntries()) {
                buildMenuIds(prefijo, outgoingPath, x);
            }
        }

    }

    public abstract List<AbstractArea> buildAreas();

    public void setUserData(UserData userData) {
        this.userData = userData;
        this.areas = null;
        buildAreaAndMenuIds();
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
}
