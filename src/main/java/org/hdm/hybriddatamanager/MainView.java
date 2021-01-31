package org.hdm.hybriddatamanager;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.core.codec.AbstractDataBufferDecoder;

import java.util.TimerTask;


@Route(value="home", layout = MainLayout.class)
@PageTitle("HYBRID DATA ACCESS APPLICATION")
public class MainView extends VerticalLayout {
    public MainView() {
        Label welcomeLabel = new Label();

        if (VaadinSession.getCurrent().getAttribute("username") == null)
            UI.getCurrent().navigate("login");
        else
        {
            welcomeLabel.setText("Welcome "+VaadinSession.getCurrent().getAttribute("username"));
        }

        LoginOverlay component = new LoginOverlay();
        component.setTitle("HYBRID DATA ACCESS APPLICATION");
        component.setDescription("Ege University Computer Engineering Department");
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setUsername("Username");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Login");
        i18n.getForm().setForgotPassword("New Membership");
        i18n.setAdditionalInformation("");
        component.setI18n(i18n);

//        Button open = new Button("Sisteme Giriş",
//                e -> component.setOpened(true));

        Label titleLabel = new Label("HYBRID  DATA ACCESS APPLICATION");
        add(titleLabel);

        add(welcomeLabel);
        //add(open);

        Button domainButton = new Button("Business Domain Definition", e -> UI.getCurrent().navigate("domainEditor"));
        domainButton.setWidth("300px");
        add(domainButton);

        Button dsButton = new Button("Data Source Definition", e -> UI.getCurrent().navigate("dsEditor"));
        dsButton.setWidth("300px");
        add(dsButton);

        Button entityButton = new Button("Entity Definition", e -> UI.getCurrent().navigate("entityEditor"));
        entityButton.setWidth("300px");
        add(entityButton);

        Button searchButton = new Button("Entity Searcg", e -> UI.getCurrent().navigate("searchEditor"));
        searchButton.setWidth("300px");
        add(searchButton);

        Button logOutButton = new Button("Logout", e -> {
            VaadinSession.getCurrent().setAttribute("username", null);
            UI.getCurrent().navigate("login");});
        logOutButton.setWidth("300px");
        add(logOutButton);

    }
}

class MainLayout extends VerticalLayout
        implements RouterLayout, PageConfigurator {

    @Override
    public void configurePage(InitialPageSettings settings) {
//        settings.addInlineFromFile(InitialPageSettings.Position.PREPEND,
//                "inline.js", InitialPageSettings.WrapMode.JAVASCRIPT);
//
//        settings.addMetaTag("og:title", "The Rock");
//        settings.addMetaTag("og:type", "video.movie");
//        settings.addMetaTag("og:url",
//                "http://www.imdb.com/title/tt0117500/");
//        settings.addMetaTag("og:image",
//                "http://ia.media-imdb.com/images/rock.jpg");
//
//        settings.addLink("shortcut icon", "icons/favicon.ico");
        settings.addFavIcon("icon", "icons/icon-192.png", "192x192");
    }
}