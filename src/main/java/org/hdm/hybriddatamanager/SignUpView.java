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
@PageTitle("HYBRID DATA ACCESS APPLICATION")
public class SignUpView extends VerticalLayout {


    Label titleLabel = new Label("Membership Information");
    TextField name = new TextField("Member Title :");
    TextField username = new TextField("Username :");
    PasswordField password = new PasswordField("Password :");
    PasswordField repassword = new PasswordField("Re-password :");

    public SignUpView()
    {

        Button saveButton = new Button("Save",VaadinIcon.CHECK.create());
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