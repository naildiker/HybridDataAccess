package org.hdm.hybriddatamanager;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Resource;
import org.hdm.core.management.HdmManager;
import org.hdm.core.objects.Attribute;
import org.hdm.core.objects.Entity;
import org.hdm.core.service.HDMDataCoordinatorService;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value="entityEditor")
@PageTitle("HYBRID DATA ACCESS APPLICATION")
public class EntityEditorView extends VerticalLayout {
    Label titleLabel = new Label("Entity Definition");
    TextField name = new TextField("Entity Name :");
    ComboBox<String> domainComboBox = new ComboBox<>("Business Domain : ");
    Label attributeLabel = new Label("Entity Attributes");
    TextField attributeName = new TextField("Attribute Name :");
    TextField attributeType = new TextField("Attribute Type :");
    MultiselectComboBox<String> dsAttributeComboBox = new MultiselectComboBox("Data Model Occurence Attributes : ");
    List<String> selectedAttributes = new ArrayList<>();

    Grid<Attribute> attributeGrid = new Grid<>();

    List<Attribute> entityAttributes = new ArrayList<Attribute>();

    HDMDataCoordinatorService dataCoordinatorSrv = new HDMDataCoordinatorService();

    public EntityEditorView()
    {
        UI.getCurrent().getPage().setTitle("HYBRID DATA ACCESS APPLICATION");

        domainComboBox.setItems(dataCoordinatorSrv.getDomains());

        prepareDsAttributes();
        dsAttributeComboBox.addSelectionListener(new MultiSelectionListener<MultiselectComboBox<String>, String>() {
            @Override
            public void selectionChange(MultiSelectionEvent<MultiselectComboBox<String>, String> multiSelectionEvent) {
                Set<String> newSelectedSet = multiSelectionEvent.getNewSelection();
                Iterator<String> newIterator = newSelectedSet.iterator();
                while(newIterator.hasNext())
                {
                    String newSelected = newIterator.next();
                    selectedAttributes.add(newSelected);
                }
            }
        });
        HorizontalLayout newAttributeLayout = new HorizontalLayout();
        Button attributeButton = new Button("Add", VaadinIcon.PLUS.create());
        attributeButton.setHeight("70px");
        attributeButton.addClickListener(this::attributeButtonClick);
        newAttributeLayout.add(attributeName, attributeType, dsAttributeComboBox, attributeButton);
        attributeGrid.setItems(entityAttributes);
        Grid.Column<Attribute> nameColumn = attributeGrid.addColumn(Attribute::getName)
                .setHeader("Attribute Name");

        Button saveButton = new Button("Save",VaadinIcon.CHECK.create());
        saveButton.addClickListener(this::buttonClick);


        add(titleLabel, domainComboBox, name, attributeLabel, newAttributeLayout, attributeGrid, saveButton);

    }

    private void prepareDsAttributes()
    {
        dsAttributeComboBox.setItems(dataCoordinatorSrv.getAttributes());
        dsAttributeComboBox.setWidth("400px");

    }

    private List<Attribute> getAttributes() {
        return null;
    }

    public void buttonClick(ClickEvent event) {

        Entity entity = new Entity();
        entity.setName(name.getValue());
        entity.setDomainName(domainComboBox.getValue());
        entity.setAttributes(attributeGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()));
        dataCoordinatorSrv.addEntity(entity);
    }

    public void attributeButtonClick(ClickEvent event) {
        Attribute newAttribute = new Attribute();
        newAttribute.setName(attributeName.getValue());
        newAttribute.setMatchingAttributes(selectedAttributes);
        entityAttributes.add(newAttribute);
        attributeGrid.setItems(entityAttributes);
        selectedAttributes = new ArrayList<>();
        attributeName.setValue("");
        attributeType.setValue("");
        dsAttributeComboBox.clear();
    }


}