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
import org.hdm.data.service.adapter.MongoMetaDataAdapter;
import org.hdm.data.service.adapter.MsSqlServerMetaDataAdapter;
import org.hdm.data.service.provider.MongoConnectionInfo;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.*;

@Route(value="dsEditor")
@PageTitle("MELEZ VERİ ERİŞİM ÇERÇVESİ")
public class DataSourceEditorView extends VerticalLayout {
    Label titleLabel = new Label("Veri Kaynağı Tanımlama");
    TextField name = new TextField(" Veri Kaynağı Adı");
    TextField serverName = new TextField("Sunucu Adı / IP");
    NumberField serverPort = new NumberField("Port");
    TextField username = new TextField("Kullanıcı Adı");
    PasswordField password = new PasswordField("Şifre");
    Label dmLabel = new Label("Veri Modeli Örneği Tanımlama");
    TextField dmName = new TextField("Veri Modeli Örneği Adı:");
    TextField dbName = new TextField("Veritabanı Adı");
    ComboBox<String> dbTypeComboBox = new ComboBox<>("Veritabanı Yönetim Sistemi");
    MultiselectComboBox<IEntity> dsEntityComboBox = new MultiselectComboBox("Tablolar & Görüntüler");
    List<IEntity> selectedEntities = new ArrayList<>();
    DataModel dm = null;
    HdmManager hdmMan = new HdmManager();
    List<IEntity> labels = new ArrayList<IEntity>();

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
                                                          dsEntityComboBox.setLabel("Tablolar & Görüntüler");
                                                      }
                                                      else
                                                      {
                                                          username.setValue("");
                                                          password.setValue("");
                                                          serverPort.setValue(27017.0);
                                                          username.setEnabled(false);
                                                          password.setEnabled(false);
                                                          dsEntityComboBox.setLabel("Kolleksiyonlar");
                                                      }
                                                  }
                                              });
        dbTypeComboBox.setValue(SupportedDataStore.MSSQLSERVER.toString());
        serverName.setValue("localhost");
        serverPort.setValue(1433.0);
        dbName.setValue("BP");
        username.setValue("sa");
        password.setValue("sapass");
        Button saveButton = new Button("Kaydet",VaadinIcon.CHECK.create());
        Button retriveButton = new Button("Tabloları & Görüntüleri Getir",VaadinIcon.TRUCK.create());
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
        Individual dsInstance = null;
        IDataStoreInstance dsi = null;
        if (dbTypeComboBox.getValue().equals(SupportedDataStore.MSSQLSERVER.toString())) {
            RelationalDataStoreInstance rdsi = new RelationalDataStoreInstance();
            rdsi.setName(name.getValue());
            rdsi.setServerName(serverName.getValue());
            rdsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            rdsi.setUsername(username.getValue());
            rdsi.setPassword(password.getValue());
            rdsi.setDbType(SupportedDataStore.MSSQLSERVER);
            dsInstance = (Individual) hdmMan.importDataStoreInstance(rdsi);
            dsi = rdsi;


        }
        else if (dbTypeComboBox.getValue().equals(SupportedDataStore.MONGO.toString())) {
            DocumentDataStoreInstance ddsi = new DocumentDataStoreInstance();
            ddsi.setName(name.getValue());
            ddsi.setServerName(serverName.getValue());
            ddsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            ddsi.setDbType(SupportedDataStore.MONGO);
            dsInstance = (Individual) hdmMan.importDataStoreInstance(ddsi);
            dsi = ddsi;


        }

        dm = new DataModel();
        dm.setName(dmName.getValue());
        dm.setDbType(dsi.getDbType());
        dm.setDbName(dbName.getValue());
        dm.setDataSourceInstance(dsi);
        dm.setDsIndividual(dsInstance);
        dm.setCurrentDsIndividual(dsInstance);
        Individual dmInstance = (Individual) hdmMan.addDataModel(dm);
        dm.setOwnIndividual(dmInstance);

        hdmMan.importEntity(dm, selectedEntities);
        hdmMan.persist();
        hdmMan.disconnect();
    }

    public void buttonClick(ClickEvent event) {

        Individual dsInstance = null;
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

            MsSqlServerMetaDataAdapter msSqlAdapter = new MsSqlServerMetaDataAdapter();
            msSqlAdapter.connect(new MsSqlServerConnectionInfo(){{ setServerName(rdsi.getServerName()); setServerPort((int)Double.parseDouble(rdsi.getServerPort())); setDbName(dbName.getValue());
                setUsername(rdsi.getUsername()); setPassword(rdsi.getPassword());}});
            List<IEntity> sqlTableNames = msSqlAdapter.getEntities();

            msSqlAdapter.close();
            labels.addAll(sqlTableNames);

        }
        else if (dbTypeComboBox.getValue().equals(SupportedDataStore.MONGO.toString())) {
            DocumentDataStoreInstance ddsi = new DocumentDataStoreInstance();
            ddsi.setName(name.getValue());
            ddsi.setServerName(serverName.getValue());
            ddsi.setServerPort(String.valueOf(serverPort.getValue().intValue()));
            ddsi.setDbType(SupportedDataStore.MONGO);
            dsi = ddsi;

            MongoMetaDataAdapter mongoMetaAdapter = new MongoMetaDataAdapter();
            mongoMetaAdapter.connect(new MongoConnectionInfo(){{ setServerName(ddsi.getServerName()); setServerPort((int)Double.parseDouble(ddsi.getServerPort()));  setDbName(dbName.getValue());}});
            List<IEntity> docCollectionNames = mongoMetaAdapter.getEntities();
            mongoMetaAdapter.close();
            labels.addAll(docCollectionNames);

        }

        //hdmMan.queryInstances("RelationalDatabase");
        ListDataProvider<IEntity> dp = DataProvider.ofCollection(labels);
        dsEntityComboBox.setDataProvider(dp);
        dsEntityComboBox.setItemLabelGenerator(IEntity::getName);
        dsEntityComboBox.setWidth("700px");


        //hdmMan.importEntity(dm, labels);
        //hdmMan.persist();
        //hdmMan.disconnect();

    }
}