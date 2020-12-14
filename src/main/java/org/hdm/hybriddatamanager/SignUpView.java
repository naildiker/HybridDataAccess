package org.hdm.hybriddatamanager;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hdm.tenant.objects.Tenant;
import org.hdm.tenant.service.HDMTenantService;

@Route(value="signup")
@PageTitle("MELEZ VERİ ERİŞİM ÇERÇVESİ")
public class SignUpView extends VerticalLayout {


    Label titleLabel = new Label("MEVER Üyelik Bilgilieri");
    TextField name = new TextField("Üye Ünvanı :");
    TextField username = new TextField("Kullanıcı Adı :");
    PasswordField password = new PasswordField("Şifre :");
    PasswordField repassword = new PasswordField("Şifre (Tekrar) :");

    public SignUpView()
    {

        Button saveButton = new Button("Kaydet",VaadinIcon.CHECK.create());
        saveButton.addClickListener(this::buttonClick);
        add(titleLabel, name,  username, password, repassword, saveButton);

    }

    public void buttonClick(ClickEvent event) {
        Tenant newTenant = new Tenant();
        newTenant.setName(name.getValue());
        newTenant.setUsername(username.getValue());
        newTenant.setPassword(password.getValue());
        HDMTenantService tenantService = new HDMTenantService();
        Boolean result = tenantService.addTenant(newTenant);

    }
}