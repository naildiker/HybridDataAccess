package org.hdm.hybriddatamanager;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.hdm.core.management.HdmManager;
import org.hdm.core.objects.*;
import org.hdm.core.service.HDMDataCoordinatorService;
import org.hdm.data.service.adapter.MongoDataProvider;
import org.hdm.data.service.adapter.MsSqlServerDataProvider;
import org.hdm.data.service.provider.MongoConnectionInfo;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;

import java.util.ArrayList;
import java.util.List;

@Route(value="searchEditor")
@PageTitle("HYBRID DATA ACCESS APPLICATION")
public class SearchEditorView extends VerticalLayout {
    Label titleLabel = new Label("Search");
    ComboBox<String> entityComboBox = new ComboBox<>("Entity : ");
    TextField name = new TextField("Keyword :");

    Grid<IEntityOccurence> resultGrid = new Grid<>();

    List<IEntityOccurence> resultList = new ArrayList<>();

    HDMDataCoordinatorService dataCoordinatorSrv = new HDMDataCoordinatorService();

    public SearchEditorView()
    {

        Button saveButton = new Button("Search",VaadinIcon.QUESTION.create());
        saveButton.addClickListener(this::buttonClick);
        prepareEntities();
        Grid.Column<IEntityOccurence> summaryColumn = resultGrid.addColumn(IEntityOccurence::toString).setHeader("Results");
        add(titleLabel, entityComboBox, name,  saveButton, resultGrid);
    }

    private void prepareEntities() {
        entityComboBox.setItems(dataCoordinatorSrv.getDefinedEntities());
        entityComboBox.setWidth("300px");
    }


    public void buttonClick(ClickEvent event) {
        resultList = new ArrayList<>();
        String keyword =  name.getValue();

        resultList = dataCoordinatorSrv.get(entityComboBox.getValue(), keyword);

        resultGrid.setItems(resultList);

    }


}