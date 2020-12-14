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
import org.hdm.data.service.adapter.MongoDataProvider;
import org.hdm.data.service.adapter.MsSqlServerDataProvider;
import org.hdm.data.service.provider.MongoConnectionInfo;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;

import java.util.ArrayList;
import java.util.List;

@Route(value="searchEditor")
@PageTitle("MELEZ VERİ ERİŞİM ÇERÇVESİ")
public class SearchEditorView extends VerticalLayout {
    Label titleLabel = new Label("Arama");
    ComboBox<String> entityComboBox = new ComboBox<>("Entity : ");
    TextField name = new TextField("Anahtar Keiime :");

    Grid<IEntityOccurence> resultGrid = new Grid<>();

    List<IEntityOccurence> resultList = new ArrayList<>();

    HdmManager hdmMan = new HdmManager();

    public SearchEditorView()
    {

        Button saveButton = new Button("Ara",VaadinIcon.QUESTION.create());
        saveButton.addClickListener(this::buttonClick);
        prepareEntities();
        Grid.Column<IEntityOccurence> summaryColumn = resultGrid.addColumn(IEntityOccurence::toString).setHeader("Sonuçlar");
        add(titleLabel, entityComboBox, name,  saveButton, resultGrid);
    }

    private void prepareEntities() {
        String queryText = "PREFIX  ds: <" + HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  DISTINCT ?x " +
                "  WHERE " +
                "  { ?x rdf:type ds:Entity .\n " +
                " ?x ds:isGeneralizedEntityOf ?y. }";

        org.apache.jena.query.Query qry = QueryFactory.create(queryText);
        QueryExecution qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        org.apache.jena.query.ResultSet rs = qe.execSelect();

        List<String> attributeNames= new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource xNode = sol.get("x").asResource();

            attributeNames.add(xNode.getLocalName());
        }
        entityComboBox.setItems(attributeNames);
        entityComboBox.setWidth("300px");
        qe.close();
    }


    public void buttonClick(ClickEvent event) {
        resultList = new ArrayList<>();
        String keyword =  name.getValue();

        getRelationalValues(keyword);
        getNoSQLValues(keyword);

        resultGrid.setItems(resultList);

    }

    private void getRelationalValues(String keyword) {
        String queryText = "PREFIX  ds: <" + HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?dataStore ?server ?serverIP ?serverPort ?serverUsername ?serverPassword ?database ?dataModel ?table " +
                "  WHERE " +
                "  { ?table rdf:type ds:Table .\n " +
                " ds:"+ entityComboBox.getValue() +" ds:isGeneralizedEntityOf ?table.\n " +
                " ?dataModel ds:hasEntity ?table.\n " +
                " ?server ds:hasDataModelOccurence ?dataModel.\n " +
                " ?server ds:isInstanceOf ?dataStore.\n " +
                " ?server ds:hasPassword ?serverPassword.\n " +
                " ?server ds:hasUsername ?serverUsername.\n " +
                " ?server ds:hasIPAddress ?serverIP.\n " +
                " ?server ds:hasPort ?serverPort.\n " +
                " ?server ds:hasDatabase ?database.\n " +
                "}";

        org.apache.jena.query.Query qry = QueryFactory.create(queryText);
        QueryExecution qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        org.apache.jena.query.ResultSet rs = qe.execSelect();

        //List<String> attributeNames= new ArrayList<>();
        String dsName = "";
        String dmName = "";
        String server = "";
        String serverIP = "";
        String serverPort = "";
        String serverUsername = "";
        String serverPassword = "";
        String table = "";
        String column = "";
        String attribute = "";
        String dbName = "";
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource dsNode = sol.get("dataStore").asResource();
            dsName = dsNode.getLocalName();

            dsNode = sol.get("dataModel").asResource();
            dmName = dsNode.getLocalName();

            dsNode = sol.get("server").asResource();
            server= dsNode.getLocalName();

            Literal literal = sol.get("serverIP").asLiteral();
            serverIP = literal.getString();

            literal = sol.get("serverPort").asLiteral();
            serverPort = literal.getString();

            literal= sol.get("serverUsername").asLiteral();
            serverUsername = literal.getString();

            literal = sol.get("serverPassword").asLiteral();
            serverPassword = literal.getString();

            dsNode = sol.get("table").asResource();
            table = dsNode.getLocalName();

            dsNode = sol.get("database").asResource();
            dbName = dsNode.getLocalName();

            String tableQueryText = "PREFIX  ds: <" + HdmManager.ns + ">\n " +
                    "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                    "  SELECT  ?attribute ?column " +
                    "  WHERE " +
                    "  { ?column rdf:type ds:Column .\n " +
                    " ds:"+ table +" ds:hasAttribute ?column.\n " +
                    " ?attribute ds:isGeneralizedAttributeOf ?column.\n " +
                    "}";

            org.apache.jena.query.Query tableQuery = QueryFactory.create(tableQueryText);
            QueryExecution tableQe = QueryExecutionFactory.create(tableQuery, hdmMan.getModel());
            org.apache.jena.query.ResultSet tableRs = tableQe.execSelect();

            String nativeQueryText = "";
            String nativeWhereClause = "";

            if (dsName.equals("Microsoft_SQL_Server"))
            {
                nativeQueryText = "SELECT ";
            }

            while (tableRs.hasNext()) {
                QuerySolution tableSol = tableRs.nextSolution();
                dsNode = tableSol.get("column").asResource();
                column = dsNode.getLocalName();

                String[] columnParts = column.split("\\.\\.");
                column = columnParts[1];

                dsNode = tableSol.get("attribute").asResource();
                attribute = dsNode.getLocalName();

                nativeQueryText += column  + " AS " + attribute + ", ";

                if ( ! keyword.trim().equals("")) {
                    nativeWhereClause += "(" + column + " LIKE '%" + keyword + "%') OR ";
                }
            }
            tableQe.close();
            nativeQueryText = nativeQueryText.substring(0, nativeQueryText.length() -2);
            nativeQueryText += " FROM "+ table;

            if ( ! keyword.trim().equals("")) {
                nativeWhereClause = nativeWhereClause.substring(0, nativeWhereClause.length() -3);
                nativeQueryText += " WHERE "+ nativeWhereClause;
            }

            MsSqlServerDataProvider msSqlAdapter = new MsSqlServerDataProvider();
            MsSqlServerConnectionInfo connectionInfo = new MsSqlServerConnectionInfo();
            connectionInfo.setServerName(serverIP);
            connectionInfo.setServerPort((int)Double.parseDouble(serverPort));
            connectionInfo.setDbName(dbName);
            connectionInfo.setUsername(serverUsername);
            connectionInfo.setPassword(serverPassword);
            msSqlAdapter.connect(connectionInfo);
            List<IEntityOccurence> sqlValues = msSqlAdapter.get(nativeQueryText);
            resultList.addAll(sqlValues);
            msSqlAdapter.close();
        }
        qe.close();
    }

    private void getNoSQLValues(String keyword) {
        String queryText = "PREFIX  ds: <"+ HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?dataStore ?server ?serverIP ?serverPort ?database ?dataModel ?col " +
                "  WHERE " +
                "  { ?col rdf:type ds:Collection .\n " +
                " ds:" + entityComboBox.getValue() + " ds:isGeneralizedEntityOf ?col.\n " +
                " ?dataModel ds:hasEntity ?col.\n " +
                " ?server ds:hasDataModelOccurence ?dataModel.\n " +
                " ?server ds:isInstanceOf ?dataStore.\n " +
                " ?server ds:hasIPAddress ?serverIP.\n " +
                " ?server ds:hasPort ?serverPort.\n " +
                " ?server ds:hasDatabase ?database.\n " +
                "}";

        org.apache.jena.query.Query qry = QueryFactory.create(queryText);
        QueryExecution qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        org.apache.jena.query.ResultSet rs = qe.execSelect();

        String dsName = "";
        String dmName = "";
        String server = "";
        String serverIP = "";
        String serverPort = "";
        String collection = "";
        String key = "";
        String attribute = "";
        String dbName = "";
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource dsNode = sol.get("dataStore").asResource();
            dsName = dsNode.getLocalName();
            if (dsName.equals("MongoDB")) {
                dsNode = sol.get("dataModel").asResource();
                dmName = dsNode.getLocalName();

                dsNode = sol.get("server").asResource();
                server = dsNode.getLocalName();

                Literal literal = sol.get("serverIP").asLiteral();
                serverIP = literal.getString();

                literal = sol.get("serverPort").asLiteral();
                serverPort = literal.getString();

                dsNode = sol.get("col").asResource();
                collection = dsNode.getLocalName();

                dsNode = sol.get("database").asResource();
                dbName = dsNode.getLocalName();

                String tableQueryText = "PREFIX  ds: <"+ HdmManager.ns + ">\n " +
                        "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                        "  SELECT  ?attribute ?key " +
                        "  WHERE " +
                        "  { ?key rdf:type ds:Key .\n " +
                        " ds:" + key + " ds:hasAttribute ?key.\n " +
                        " ?attribute ds:isGeneralizedAttributeOf ?key.\n " +
                        "}";

                org.apache.jena.query.Query tableQuery = QueryFactory.create(tableQueryText);
                QueryExecution tableQe = QueryExecutionFactory.create(tableQuery, hdmMan.getModel());
                org.apache.jena.query.ResultSet tableRs = tableQe.execSelect();

                while (tableRs.hasNext()) {
                    QuerySolution tableSol = tableRs.nextSolution();
                    dsNode = tableSol.get("key").asResource();
                    key = dsNode.getLocalName();

                    String[] columnParts = key.split("\\.\\.");
                    key = columnParts[1];

                    dsNode = tableSol.get("attribute").asResource();
                    attribute = dsNode.getLocalName();

                }
                tableQe.close();

                MongoDataProvider msSqlAdapter = new MongoDataProvider();
                MongoConnectionInfo connectionInfo = new MongoConnectionInfo();
                connectionInfo.setServerName(serverIP);
                connectionInfo.setServerPort((int) Double.parseDouble(serverPort));
                connectionInfo.setDbName(dbName);
                msSqlAdapter.connect(connectionInfo);
                List<IEntityOccurence> sqlValues = msSqlAdapter.get(collection);
                resultList.addAll(sqlValues);
                msSqlAdapter.close();
            }
        }
        qe.close();

    }

}