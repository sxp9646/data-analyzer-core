/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deutschebank.core;

import deutschebank.MainUnit;
import deutschebank.dbutils.DBConnector;
import deutschebank.dbutils.User;
import deutschebank.dbutils.UserHandler;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Selvyn
 */
public class ApplicationScopeHelper
{
		
    private String itsInfo = "NOT SET";
    private DBConnector itsConnector = null;

    public String getInfo()
    {
        return itsInfo;
    }

    public void setInfo(String itsInfo)
    {
        this.itsInfo = itsInfo;
    }

    public boolean bootstrapDBConnection()
    {
    	System.out.println("bootstrapConnection");
        boolean result = false;
        if (itsConnector == null)
        {
            try
            {
                itsConnector = DBConnector.getConnector();

                PropertyLoader pLoader = PropertyLoader.getLoader();

                Properties pp;
                pp = pLoader.getPropValues("dbConnector.properties");

                result = itsConnector.connect(pp);
            } catch (IOException ex)
            {
                Logger.getLogger(ApplicationScopeHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            result = true;
        
        return result;
    }
    
    /*
     * Before calling this method make sure you have bootStrapped the connection
     * by calling bootDBConnection() from somewhere outside of this method, 
     * itsConnection should then be valid and NOT null
    */
    public User userLogin( Connection cxn, String userId, String userPwd )
    {
        User theUser = null;
        try
        {
            UserHandler theUserHandler = UserHandler.getLoader();
            if (itsConnector == null) {
            	//bootstrapDBConnection();
            	System.out.println("null");
            }
//            Connection cxn = ((DBConnector) con).getConnection();
            if( cxn == null )
                if( ! bootstrapDBConnection() )
                    throw new IOException("DB connection NOT established");
            
            theUser = theUserHandler.loadFromDB( cxn, userId, userPwd );
            
            if( theUser != null )
                MainUnit.log( "User " + userId + " has logged into system");
        } catch (IOException ex)
        {
            Logger.getLogger(ApplicationScopeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return theUser;
    }

}
