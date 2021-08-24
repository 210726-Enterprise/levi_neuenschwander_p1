package servlet;

import service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/customers")
public class CustomerServlet extends HttpServlet {

    CustomerService service;

    public CustomerServlet(){
        this.service = new CustomerService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        service.getCustomers(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        service.insertCustomer(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        service.updateCustomer(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        service.deleteCustomer(req, resp);
    }
}
