package org.hdm.hybriddatamanager;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.jena.ontology.Individual;
import org.hdm.core.management.HdmManager;
import org.hdm.core.objects.*;
import org.hdm.core.service.HDMDataCoordinatorService;
import org.hdm.data.service.adapter.MongoMetaDataAdapter;
import org.hdm.data.service.adapter.MsSqlServerMetaDataAdapter;
import org.hdm.data.service.provider.MongoConnectionInfo;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.*;

@Route(value="dsEditor")
@PageTitle("HYBRID DATA ACCESS APPLICATION")
public class DataSourceEditorView extends VerticalLayout {
    Label titleLabel = new Label("DBMS Server Definition");
    TextField name = new TextField(" DBMS Server Name");
    TextField serverName = new TextField("Server Name / IP");
    NumberField serverPort = new NumberField("Port");
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    Label dmLabel = new Label("Data Model Occurence Definition");
    TextField dmName = new TextField("Data Model Occurence Name");
    TextField dbName = new TextField("Database Name");
    ComboBox<String> dbTypeComboBox = new ComboBox<>("DBMS");
    MultiselectComboBox<IEntity> dsEntityComboBox = new MultiselectComboBox("Tables & Views");
    List<IEntity> selectedEntities = new ArrayList<>();
    DataModel dm = null;
    List<IEntity> labels = new ArrayList<IEntity>();
    HDMDataCoordinatorService dataCoordinatorSrv = new HDMDataCoordinatorService();

    public DataSourceEditorView()
    {

        List<String> supportedDataStores = new ArrayList<String>();
        supportedDataStores.add(SupportedDataStore.MONGO.toString());
        supportedDataStores.add(SupportedDataStore.MSSQLSERVER.toString());
        supportedDataStores.add(SupportedDataStore.ORACLE.toString());
        dbTypeComboBox.setItems(supportedDataStores);
        dbTypeComboBox.setAllowCustomValue(false);
        dbTypeComboBox.addValueChangeListener(new HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<String>, String>>() {
                                                  @Override
                                                  public void valueChanged(AbstractField.ComponentValueChangeEvent<ComboBox<String>, String> comboBoxStringComponentValueChangeEvent) {
                                                      if (dbTypeComboBox.getValue().equals(SupportedDataStore.MSSQLSERVER.toString()))
                                                      {
                                                          username.setValue("sa");
                                                          password.setValue("sapass");
                                                          serverPort.setValue(1433.0);
                                                          username.setEnabled(true);
                                                          password.setEnabled(true);
                                                          dsEntityComboBox.setLabel("Tables & Views");
                                                      }
                                                      else  if (dbTypeComboBox.getValue().equals(SupportedDataStore.ORACLE.toString()))
                                                      {
                                                          username.setValue("system");
                                                          password.setValue("systempass");
                                                          serverPort.setValue(1521.0);
                                                          username.setEnabled(true);
                                                          password.setEnabled(true);
                                                          dsEntityComboBox.setLabel("Tables & Views");
                                                      }
                                                      else
                                                      {
                                                          username.setValue("");
                                                          password.setValue("");
                                                          serverPort.setValue(27017.0);
                                                          username.setEnabled(false);
                                                          password.setEnabled(false);
                                                          dsEntityComboBox.setLabel("Collections");
                                                      }
                                                  }
                                              });
        dbTypeComboBox.setValue(SupportedDataStore.MSSQLSERVER.toString());
        serverName.setValue("localhost");
        serverPort.setValue(1433.0);
        dbName.setValue("BP");
        username.setValue("sa");
        password.setValue("sapass");
        Button saveButton = new Button("Save",VaadinIcon.CHECK.create());
        Button retriveButton = new Button("Get Collections",VaadinIcon.TRUCK.create());
        add(titleLabel, name, dbTypeComboBox, serverName, serverPort,  username, password, dmLabel, dmName, dbName, retriveButton, dsEntityComboBox, saveButton);

        saveButton.addClickListener(this::sButtonClick);
        retriveButton.addClickListener(this::buttonClick);

        dsEntityComboBox.addSelectionListener(new MultiSelectionListener<MultiselectComboBox<IEntity>, IEntity>() {
            @Override
            public void selectionChange(MultiSelectionEvent<MultiselectComboBox<IEntity>, IEntity> multiSelectionEvent) {
                        Set<IEntity> newSelectedSet = multiSelectionEvent.getNewSelection();
                        Iterator<IEntity> newIterator = newSelectedSet.iterator();
                        while(newIterator.hasNext())
                        {
                            IEntity newSelected = newIterator.next();
                            selectedEntities.add(newSelected);
                        }
            }
        });
    }

    public void sButtonClick(ClickEvent event) {

        IDataStoreInstance dsi = null;
        if (dbTypeComboBox.getValue().equals(SupportedDataStore.MSSQLSERVER.toString())) {
            RelationalDataStoreInstance rdsi = new RelationalDataStoreInstance();
            rdsi.setName(name.getValue());
            rdsi.setServerName(serverName.getValue());
            rdsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            rdsi.setUsername(username.getValue());
            rdsi.setPassword(password.getValue());
            rdsi.setDbType(SupportedDataStore.MSSQLSERVER);
            dsi = rdsi;


        }
        else if (dbTypeComboBox.getValue().equals(SupportedDataStore.MONGO.toString())) {
            DocumentDataStoreInstance ddsi = new DocumentDataStoreInstance();
            ddsi.setName(name.getValue());
            ddsi.setServerName(serverName.getValue());
            ddsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            ddsi.setDbType(SupportedDataStore.MONGO);
            dsi = ddsi;
        }

        dm = new DataModel();
        dm.setName(dmName.getValue());
        dm.setDbType(dsi.getDbType());
        dm.setDbName(dbName.getValue());

        Boolean result = dataCoordinatorSrv.addDataSource(dsi, dm, selectedEntities);
    }

    public void buttonClick(ClickEvent event) {

        IDataStoreInstance dsi = null;
        if (dbTypeComboBox.getValue().equals(SupportedDataStore.MSSQLSERVER.toString())) {
            RelationalDataStoreInstance rdsi = new RelationalDataStoreInstance();
            rdsi.setName(name.getValue());
            rdsi.setServerName(serverName.getValue());
            rdsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            rdsi.setUsername(username.getValue());
            rdsi.setPassword(password.getValue());
            rdsi.setCurrentDbName(dbName.getValue());
            rdsi.setDbType(SupportedDataStore.MSSQLSERVER);
            dsi = rdsi;
        }
        if (dbTypeComboBox.getValue().equals(SupportedDataStore.ORACLE.toString())) {
            RelationalDataStoreInstance rdsi = new RelationalDataStoreInstance();
            rdsi.setName(name.getValue());
            rdsi.setServerName(serverName.getValue());
            rdsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            rdsi.setUsername(username.getValue());
            rdsi.setPassword(password.getValue());
            rdsi.setCurrentDbName(dbName.getValue());
            rdsi.setDbType(SupportedDataStore.ORACLE);
            dsi = rdsi;
        }
        else if (dbTypeComboBox.getValue().equals(SupportedDataStore.MONGO.toString())) {
            DocumentDataStoreInstance ddsi = new DocumentDataStoreInstance();
            ddsi.setName(name.getValue());
            ddsi.setServerName(serverName.getValue());
            ddsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            ddsi.setDbType(SupportedDataStore.MONGO);
            ddsi.setCurrentDbName(dbName.getValue());
            dsi = ddsi;
        }
        List<IEntity> entityNames = dataCoordinatorSrv.getEntities(dsi);
        labels.addAll(entityNames);

        ListDataProvider<IEntity> dp = DataProvider.ofCollection(labels);
        dsEntityComboBox.setDataProvider(dp);
        dsEntityComboBox.setItemLabelGenerator(IEntity::getName);
        dsEntityComboBox.setWidth("700px");

    }
}