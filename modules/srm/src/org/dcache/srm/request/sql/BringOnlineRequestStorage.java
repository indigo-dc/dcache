// $Id$
// $Log: not supported by cvs2svn $
// Revision 1.3  2007/01/10 23:00:25  timur
// implemented srmGetRequestTokens, store request description in database, fixed several srmv2 issues
//
// Revision 1.2  2007/01/06 00:23:55  timur
// merging production branch changes to database layer to improve performance and reduce number of updates
//
/*
 * BringOnlineRequestStorage.java
 *
 * Created on June 22, 2004, 2:48 PM
 */

package org.dcache.srm.request.sql;
import org.dcache.srm.request.Request;
import org.dcache.srm.request.ContainerRequest;
import org.dcache.srm.request.FileRequest;
import org.dcache.srm.request.BringOnlineRequest;
import org.dcache.srm.util.Configuration;
import java.sql.*;
import org.dcache.srm.scheduler.State;
import org.dcache.srm.scheduler.Job;

/**
 *
 * @author  timur
 */
public class BringOnlineRequestStorage extends DatabaseContainerRequestStorage{
    
      
    /** Creates a new instance of BringOnlineRequestStorage */
    public BringOnlineRequestStorage(    
    Configuration configuration
    )  throws SQLException {
        super(configuration);
    }
       
    public void say(String s){
        if(logger != null) {
           logger.log(" BringOnlineRequestStorage: "+s);
        }
    }
    
    public void esay(String s){
        if(logger != null) {
           logger.elog(" BringOnlineRequestStorage: "+s);
        }
    }
    
    public void esay(Throwable t){
        if(logger != null) {
           logger.elog(t);
        }
    }
    
 
    private String getProtocolsTableName()
    {
        return getTableName()+"_protocols";
    }

    public void dbInit1() throws SQLException {
             if(reanamed_old_table) {
                    renameTable(getProtocolsTableName());
                
            }
           String protocolsTableName = getProtocolsTableName().toLowerCase();
            String createProtocolsTable = "CREATE TABLE "+ protocolsTableName+" ( "+
            " PROTOCOL "+stringType+ ","+
            " RequestID "+stringType+ ", "+ //forein key
            " CONSTRAINT fk_"+getTableName()+"_PG FOREIGN KEY (RequestID) REFERENCES "+
            getTableName() +" (ID) "+
            " ON DELETE CASCADE"+
            " )";
            createTable(protocolsTableName, createProtocolsTable);
   }
    
    public void getCreateList(ContainerRequest r, StringBuffer sb) {
        
    }
    private static int ADDITIONAL_FIELDS = 0;

    protected ContainerRequest getContainerRequest(
    Connection _con,
    Long ID,
    Long NEXTJOBID,
    long CREATIONTIME,
    long LIFETIME,
    int STATE,
    String ERRORMESSAGE,
    String CREATORID,
    String SCHEDULERID,
    long SCHEDULER_TIMESTAMP,
    int NUMOFRETR,
    int MAXNUMOFRETR,
    long LASTSTATETRANSITIONTIME,
    Long CREDENTIALID,
    int RETRYDELTATIME,
    boolean SHOULDUPDATERETRYDELTATIME,
    String DESCRIPTION,
    String CLIENTHOST,
    String STATUSCODE,
    FileRequest[] fileRequests,
    java.sql.ResultSet set,
    int next_index)throws java.sql.SQLException {
           
            String sqlStatementString = "SELECT PROTOCOL FROM " + getProtocolsTableName() +
            " WHERE RequestID='"+ID+"'";
            Statement sqlStatement = _con.createStatement();
            say("executing statement: "+sqlStatementString);
            ResultSet fileIdsSet = sqlStatement.executeQuery(sqlStatementString);
            java.util.Set utilset = new java.util.HashSet();
            while(fileIdsSet.next()) {
                utilset.add(fileIdsSet.getString(1));
            }
            String [] protocols = (String[]) utilset.toArray(new String[0]);
            sqlStatement.close();
            Job.JobHistory[] jobHistoryArray = 
            getJobHistory(ID,_con);
            return new  BringOnlineRequest( 
                        ID, 
                        NEXTJOBID,
                        this,
                        CREATIONTIME,
                        LIFETIME,
                        STATE,
                        ERRORMESSAGE,
                        CREATORID,
                        SCHEDULERID,
                        SCHEDULER_TIMESTAMP, 
                        NUMOFRETR, 
                        MAXNUMOFRETR,
                        LASTSTATETRANSITIONTIME,
                        jobHistoryArray,
                        CREDENTIALID,
                        fileRequests,
                        RETRYDELTATIME,
                        SHOULDUPDATERETRYDELTATIME,
                        DESCRIPTION,
                        CLIENTHOST,
                        STATUSCODE,
                        configuration,
                        protocols
                        );

    }
    
    public String getRequestCreateTableFields() {
        return "";
    }
    public static final String TABLE_NAME ="bringonlinerequestss";
    public String getTableName() {
        return TABLE_NAME;
    }
 
    public void getUpdateAssignements(ContainerRequest r, StringBuffer sb) {
    }
    
     public String[] getAdditionalCreateRequestStatements(ContainerRequest r)  {
        if(r == null || !(r instanceof BringOnlineRequest)) {
            throw new IllegalArgumentException("Request is not BringOnlineRequest" );
        }
        BringOnlineRequest gr = (BringOnlineRequest)r;
        String[] protocols = gr.getProtocols();
        if(protocols ==null)  return null;
        String[] statements  = new String[protocols.length];
        for(int i=0; i<protocols.length ; ++i){
               StringBuffer sb = new StringBuffer();
                sb.append("INSERT INTO ").append(getProtocolsTableName());
                sb.append( " VALUES ( '");
                sb.append(protocols[i]);
                sb.append("', '");
                sb.append(r.getId());
                sb.append("') ");
                statements[i] = sb.toString();
        }
        return statements;
   }    
    
    
    public String getFileRequestsTableName() {
        return GetFileRequestStorage.TABLE_NAME;
    }
    
    protected void __verify(int nextIndex, int columnIndex, String tableName, String columnName, int columnType) throws SQLException {
    }
    
  
    protected int getMoreCollumnsNum() {
         return ADDITIONAL_FIELDS;
     }

}
