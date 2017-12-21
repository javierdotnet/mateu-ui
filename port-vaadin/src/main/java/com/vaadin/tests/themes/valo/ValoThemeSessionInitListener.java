package com.vaadin.tests.themes.valo;

import org.jsoup.nodes.Element;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;

public class ValoThemeSessionInitListener implements SessionInitListener {

    @Override
    public void sessionInit(final SessionInitEvent event)
            throws ServiceException {
        event.getService().setSystemMessagesProvider(
                new SystemMessagesProvider() {

                    @Override
                    public SystemMessages getSystemMessages(final SystemMessagesInfo systemMessagesInfo) {
                        CustomizedSystemMessages csm = new CustomizedSystemMessages();
                        csm.setSessionExpiredNotificationEnabled(false);
                        return csm;
                    }
                });
        event.getSession().addBootstrapListener(new BootstrapListener() {

            @Override
            public void modifyBootstrapPage(final BootstrapPageResponse response) {
                final Element head = response.getDocument().head();
                head.appendElement("meta").attr("name", "viewport")
                        .attr("content", "width=device-width, initial-scale=1");
                head.appendElement("meta")
                        .attr("name", "apple-mobile-web-app-capable")
                        .attr("content", "yes");
                head.appendElement("meta")
                        .attr("name", "apple-mobile-web-app-status-bar-style")
                        .attr("content", "black");

                /*
                String appId = event.getService().getMainDivId(event.getSession(), event.getRequest(), ValoThemeUI.class);

                String tema = System.getProperty("theme", "mateu");

                response.getDocument().getElementById(appId).removeClass("valo").addClass(tema);
                for (Element e : response.getDocument().getElementsByAttribute("href")) {
                    String v = e.attr("href");
                    //<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./VAADIN/themes/valo/favicon.ico">
                    if (v.contains("VAADIN/themes/valo/")) e.attr("href", v.replaceAll("VAADIN/themes/valo/", "VAADIN/themes/" + tema + "/"));
                }
                */
            }

            @Override
            public void modifyBootstrapFragment(
                    final BootstrapFragmentResponse response) {
                // TODO Auto-generated method stub

                System.out.println("xxx");

            }
        });
    }

}
