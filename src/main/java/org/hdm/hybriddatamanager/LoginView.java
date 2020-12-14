package org.hdm.hybriddatamanager;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.hdm.core.management.HdmManager;
import org.hdm.core.objects.Attribute;
import org.hdm.core.objects.Domain;

import java.util.List;

@Route(value = "login")
@PageTitle("MELEZ VERİ ERİŞİM ÇERÇVESİ - GİRİŞ SAYFASI")
public class LoginView extends VerticalLayout {

    public LoginView()
    {
        LoginForm component = new LoginForm();
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("MEVER Üye Girişi");
        i18n.getForm().setUsername("Kullanıcı Adı");
        i18n.getForm().setPassword("Şifre");
        i18n.getForm().setSubmit("Giriş");
        i18n.getForm().setForgotPassword("Yeni Üyelik");
        i18n.setAdditionalInformation("");
        component.setI18n(i18n);

        component.addForgotPasswordListener(event -> {
            this.getUI().ifPresent(ui -> ui.navigate("signup"));
        });
        add(component);

        component.addLoginListener(event -> {
            if ((!event.getUsername().isEmpty()) && (!event.getPassword().isEmpty()))
                VaadinSession.getCurrent().setAttribute("username", event.getUsername());
                this.getUI().ifPresent(ui -> ui.navigate("home"));
        });
        add(component);

    }
}