package org.hdm.core.service;

import com.github.andrewoma.dexx.collection.ArrayList;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.hdm.core.management.HdmManager;
import org.hdm.core.objects.IEntityOccurence;
import org.hdm.data.service.adapter.MongoDataProvider;
import org.hdm.data.service.adapter.MsSqlServerDataProvider;
import org.hdm.data.service.provider.MongoConnectionInfo;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;

import java.util.List;

public class HDMQueryManagementService implements IHDMQueryManagementService {
    List<IEntityOccurence> resultList = new java.util.ArrayList<>();

    @Override
    public List<IEntityOccurence> get(String entityName, String keyword) {
        HdmManager hdmMan = new HdmManager();

        getRelationalValues(hdmMan, entityName, keyword);
        getNoSQLValues(hdmMan, entityName,  keyword);

        return resultList;
    }

    private void getRelationalValues(HdmManager hdmMan, String entityName, String keyword) {
        String queryText = "PREFIX  ds: <" + HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?dataStore ?server ?serverIP ?serverPort ?serverUsername ?serverPassword ?database ?dataModel ?table " +
                "  WHERE " +
                "  { ?table rdf:type ds:Table .\n " +
                " ds:"+ entityName +" ds:isGeneralizedEntityOf ?table.\n " +
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

            if (dsName.equals("Microsoft_SQL_Server") || dsName.equals("Oracle"))
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

    private void getNoSQLValues(HdmManager hdmMan, String entityName, String keyword) {
        String queryText = "PREFIX  ds: <"+ HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?dataStore ?server ?serverIP ?serverPort ?database ?dataModel ?col " +
                "  WHERE " +
                "  { ?col rdf:type ds:Collection .\n " +
                " ds:" + entityName + " ds:isGeneralizedEntityOf ?col.\n " +
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
