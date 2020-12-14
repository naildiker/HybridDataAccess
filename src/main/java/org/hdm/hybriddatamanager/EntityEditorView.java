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
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value="entityEditor")
@PageTitle("MELEZ VERİ ERİŞİM MODELİ")
public class EntityEditorView extends VerticalLayout {
    Label titleLabel = new Label("Entity Tanımlama");
    TextField name = new TextField("Entity Adı :");
    ComboBox<String> domainComboBox = new ComboBox<>("İş Alanı : ");



    Label attributeLabel = new Label("Entity Nitelikleri");
    TextField attributeName = new TextField("Nitelik :");
    TextField attributeType = new TextField("Nitelik Tipi :");
    MultiselectComboBox<String> dsAttributeComboBox = new MultiselectComboBox("Veri Modeli Örneği Nitelik(ler)i : ");
    List<String> selectedAttributes = new ArrayList<>();

    Grid<Attribute> attributeGrid = new Grid<>();

    List<Attribute> entityAttributes = new ArrayList<Attribute>();


    HdmManager hdmMan = new HdmManager();

    public EntityEditorView()
    {
        UI.getCurrent().getPage().setTitle("MELEZ VERİ ERİŞİM ÇERÇEVESİ");

        List<String> domainUris= hdmMan.queryInstances("BusinessDomain");
        List<String> domainNames= new ArrayList<>();
        for (String domainUri : domainUris
             ) {
            domainNames.add(domainUri.replaceAll(hdmMan.ns, ""));
        }
        domainComboBox.setItems(domainNames);

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
        Button attributeButton = new Button("Ekle", VaadinIcon.PLUS.create());
        attributeButton.setHeight("70px");
        attributeButton.addClickListener(this::attributeButtonClick);
        newAttributeLayout.add(attributeName, attributeType, dsAttributeComboBox, attributeButton);
        attributeGrid.setItems(entityAttributes);
        Grid.Column<Attribute> nameColumn = attributeGrid.addColumn(Attribute::getName)
                .setHeader("Nitelik Adı");
//        Grid.Column<Attribute> subscriberColumn = grid
//                .addColumn(Attribute::getName).setHeader(" ");

        Button saveButton = new Button("Kaydet",VaadinIcon.CHECK.create());
        saveButton.addClickListener(this::buttonClick);

//        add(titleLabel, name,  grid, saveButton);
        add(titleLabel, domainComboBox, name, attributeLabel, newAttributeLayout, attributeGrid, saveButton);

    }

    private void prepareDsAttributes() {
        String queryText = "PREFIX  ds: <" + HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?x ?y ?z " +
                "  WHERE " +
                "  { ?x rdf:type ds:Column .\n " +
                " ?y ds:hasColumn ?x .\n ?z ds:hasTable ?y . }";

        org.apache.jena.query.Query qry = QueryFactory.create(queryText);
        QueryExecution qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        org.apache.jena.query.ResultSet rs = qe.execSelect();

        List<String> attributeNames= new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource zNode = sol.get("z").asResource();
            Resource yNode = sol.get("y").asResource();
            Resource xNode = sol.get("x").asResource();

            attributeNames.add(zNode.getLocalName() + " -  " + yNode.getLocalName() + " - " + xNode.getLocalName() );
        }
        qe.close();

        queryText = "PREFIX  ds: <"+ HdmManager.ns +">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?x ?y ?z " +
                "  WHERE " +
                "  { ?x rdf:type ds:Key.\n " +
                " ?y ds:hasKey ?x .\n ?z ds:hasCollection ?y . }";

        qry = QueryFactory.create(queryText);
        qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource zNode = sol.get("z").asResource();
            Resource yNode = sol.get("y").asResource();
            Resource xNode = sol.get("x").asResource();

            attributeNames.add(zNode.getLocalName() + " -  " + yNode.getLocalName() + " - " + xNode.getLocalName() );
        }

        dsAttributeComboBox.setItems(attributeNames);
        dsAttributeComboBox.setWidth("400px");
        qe.close();

        //hdmMan.query(queryText);
    }

    private List<Attribute> getAttributes() {
        return null;
    }

    public void buttonClick(ClickEvent event) {
        List<Entity> labels = new ArrayList<Entity>();

        Entity entity = new Entity();
        entity.setName(name.getValue());
        entity.setDomainName(domainComboBox.getValue());
        entity.setAttributes(attributeGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()));
        hdmMan.addEntity(entity);


        hdmMan.persist();
        hdmMan.disconnect();
    }

    public void attributeButtonClick(ClickEvent event) {
        Attribute newAttribute = new Attribute();
        newAttribute.setName(attributeName.getValue());
        //newAttribute.setMatchingAttributes(new ArrayList<>(dsAttributeComboBox.getSelectedItems()));
        newAttribute.setMatchingAttributes(selectedAttributes);
        entityAttributes.add(newAttribute);
        attributeGrid.setItems(entityAttributes);
        selectedAttributes = new ArrayList<>();
        attributeName.setValue("");
        attributeType.setValue("");
        dsAttributeComboBox.clear();
    }


}