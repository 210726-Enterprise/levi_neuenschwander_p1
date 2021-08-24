package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.*;
import model.Customer;
import orm.util.OrmManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class CustomerService {

    private OrmManager orm;
    private ObjectMapper mapper;
    private static Logger logger = Logger.getLogger(CustomerService.class);
    static Properties prop = new Properties();
    private static String url = "";
    private static String username = "";
    private static String password = "";

    public CustomerService(){
        try {
            prop.load(new FileReader("C:\\Program Files\\Java\\jdk1.8.0_301\\ProjectOneService\\src\\main\\resources\\application.properties")); //Finding path of file holding database credentials
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage(),e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        url = prop.getProperty("ENDPOINT"); //Database url
        username = prop.getProperty("USERNAME"); //database username
        password = prop.getProperty("PASSWORD");
        orm = new OrmManager(url,username,password);
        mapper = new ObjectMapper();
    }

    public CustomerService(OrmManager orm, ObjectMapper map){
        this.orm = orm;
        this.mapper = map;
    }

    public void getCustomers(HttpServletRequest req, HttpServletResponse resp) {
        String p = req.getParameter("id");
        if(p != null){
            if(p.equals("")){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try{
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getCustomer(Integer.parseInt(req.getParameter("id"))));
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getOutputStream().print(json);
            } catch(IOException e){
                logger.warn(e.getMessage(),e);
            } catch(NumberFormatException e){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.warn(e.getMessage(),e);
            } catch(NullPointerException e){
                resp.setStatus((HttpServletResponse.SC_NOT_FOUND));
            }
        }
        else{
            try{
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getAllCustomers());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getOutputStream().print(json);
            } catch (IOException e) {
                logger.warn(e.getMessage(),e);
            }
        }
    }

    public void insertCustomer(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder builder = new StringBuilder();
        try{
            req.getReader().lines().collect(Collectors.toList()).forEach(builder::append);

            Customer customer = mapper.readValue(builder.toString(),Customer.class);
            boolean status = insert(customer);

            if(status){
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
            else{
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        } catch (JsonMappingException e) {
            logger.debug(e.getMessage(),e);
        } catch (JsonProcessingException e) {
            logger.debug(e.getMessage(),e);
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
    }

    public void updateCustomer(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder builder = new StringBuilder();
        try{
            req.getReader().lines().collect(Collectors.toList()).forEach(builder::append);
            Customer customer = mapper.readValue(builder.toString(), Customer.class);
            if(customer.getId() != 0){
                boolean status = update(customer);

                if(status){
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                else{
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
            else{
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public void deleteCustomer(HttpServletRequest req, HttpServletResponse resp) {
        int result = 0;
        String p = req.getParameter("id");
        if(p != null){
            if(p.equals("")){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                result = delete(Integer.parseInt(p));
            }catch(NumberFormatException e){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            if(result == 1){
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        }
        else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    public Customer getCustomer(int id){
        Customer result = (Customer) orm.findById(id, Customer.class);
        if(result != null) {
            return result;
        }
        else{
            return null;
        }
    }

    public List<Customer> getAllCustomers() {
        return (List<Customer>) (List<?>) orm.findAll(Customer.class);
    }

    public boolean insert(Customer customer){
        int status = orm.create(customer);
        if(status==0){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean update(Customer customer){
        int status = orm.update(customer);
        if(status==0){
            return false;
        }
        else{
            return true;
        }
    }

    public int delete(int id){
        return orm.delete(id, Customer.class);
    }
}
