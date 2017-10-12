package io.mateu.ui.core.client.app;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by miguel on 5/6/17.
 */
public abstract class BaseApplication extends AbstractApplication {

    @Override
    public List<AbstractArea> getAreas() {
        return Lists.newArrayList(new AbstractArea("Menu") {

            @Override
            public boolean isPublicAccess() {
                return true;
            }

            @Override
            public List<AbstractModule> getModules() {
                return Lists.newArrayList(new AbstractModule() {

                    @Override
                    public String getName() {
                        return "Module 1";
                    }

                    @Override
                    public List<MenuEntry> getMenu() {
                        return BaseApplication.this.getMenu();
                    }
                });
            }
        });
    }

    public abstract List<MenuEntry> getMenu();
}
