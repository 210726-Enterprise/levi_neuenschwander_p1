package orm.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.*;

public class ConnectionUtil {
        /**
         * Instance variables
         */
        private static String url = "";
        private static String username = "";
        private static String password = "";
        private static Connection connection;
        private static Logger logger = Logger.getLogger(ConnectionUtil.class);


        public ConnectionUtil(String url, String username, String password){
            this.url = url;
            this.username = username;
            this.password = password;
        }
        /**
         * Method to create the connection
         * @return created connection
         */
        public static Connection getConnection(){
            try{
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, username, password); //creating connection with credentials
            }
            catch(SQLException e){
                logger.warn(e.getMessage(),e);
            }
             catch (ClassNotFoundException e) {
                logger.warn(e.getMessage(),e);
            }

            return connection;
        }
}
