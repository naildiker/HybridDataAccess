package org.hdm.hybriddatamanager;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hdm.core.management.HdmManager;
import org.hdm.core.objects.Attribute;
import org.hdm.core.objects.Domain;
import org.hdm.core.objects.Entity;
import org.hdm.core.service.HDMDataCoordinatorService;

import java.util.ArrayList;
import java.util.List;

@Route(value="domainEditor")
@PageTitle("HYBRID DATA ACCESS APPLICATION")
public class DomainEditorView extends VerticalLayout {


    Label titleLabel = new Label("Business Domain Definition");
    TextField name = new TextField("Domain Name :");

    HDMDataCoordinatorService dataCoordinatorSrv = new HDMDataCoordinatorService();

    public DomainEditorView()
    {

        Button saveButton = new Button("Save",VaadinIcon.CHECK.create());
        saveButton.addClickListener(this::buttonClick);
        add(titleLabel, name,  saveButton);

    }

    private List<Attribute> getAttributes() {
        return null;
    }

    public void buttonClick(ClickEvent event) {

        Domain domain = new Domain();
        domain.setName(name.getValue());
        boolean result = dataCoordinatorSrv.addDomain(domain);

    }
}