package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import junit.framework.TestCase;
import model.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import orm.util.OrmManager;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest extends TestCase {

    @InjectMocks
    CustomerService csMock;
    OrmManager ormMock;
    ObjectMapper mapperMock;
    ObjectWriter writerMock;
    HttpServletRequest requestMock;
    HttpServletResponse responseMock;
    List<Object> list;
    ServletOutputStream oStreamMock;
    String test;

    @Mock
    OrmManager orm;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        ormMock = Mockito.mock(OrmManager.class);
        mapperMock = Mockito.mock(ObjectMapper.class);
        writerMock = Mockito.mock(ObjectWriter.class);
        requestMock = Mockito.mock(HttpServletRequest.class);
        responseMock = Mockito.mock(HttpServletResponse.class);
        oStreamMock = Mockito.mock(ServletOutputStream.class);

        csMock = new CustomerService(ormMock, mapperMock);

        list = new ArrayList<>();
        test = "test";
    }

    @Test
    public void testGetCustomers() throws IOException {
        list.add(new Customer());
        when(ormMock.findAll(Customer.class)).thenReturn(list);
        when(mapperMock.writerWithDefaultPrettyPrinter()).thenReturn(writerMock);
        when(mapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(list)).thenReturn(test);
        when(responseMock.getOutputStream()).thenReturn(oStreamMock);

        csMock.getCustomers(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(oStreamMock).print(test);
    }

    @Test
    public void testGetCustomersWithId() throws IOException {
        Customer cus = new Customer();
        when(requestMock.getParameter("id")).thenReturn("1");
        when(ormMock.findById(1,Customer.class)).thenReturn(cus);
        when(mapperMock.writerWithDefaultPrettyPrinter()).thenReturn(writerMock);
        when(mapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(cus)).thenReturn(test);
        when(responseMock.getOutputStream()).thenReturn(oStreamMock);

        csMock.getCustomers(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(oStreamMock).print(test);
    }

    @Test
    public void testGetCustomersWithEmptyStringId() throws IOException {
        Customer cus = new Customer();
        when(requestMock.getParameter("id")).thenReturn("");
        when(ormMock.findById(1,Customer.class)).thenReturn(cus);
        when(responseMock.getOutputStream()).thenReturn(oStreamMock);

        csMock.getCustomers(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetCustomersWithIdNotFound() throws IOException {
        Customer cus = new Customer();
        when(requestMock.getParameter("id")).thenReturn("1");
        when(ormMock.findById(1,Customer.class)).thenReturn(cus);
        when(responseMock.getOutputStream()).thenReturn(null);

        csMock.getCustomers(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testGetCustomersWithBadNumberFormat() throws IOException {
        Customer cus = new Customer();
        when(requestMock.getParameter("id")).thenReturn("asdf");
        when(ormMock.findById(1,Customer.class)).thenReturn(cus);
        when(responseMock.getOutputStream()).thenReturn(oStreamMock);

        csMock.getCustomers(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testInsertCustomer() throws IOException {
        Customer cus = new Customer();
        BufferedReader buffMock = Mockito.mock(BufferedReader.class);
        String s = "asdf";
        Stream<String> stream = Arrays.stream(new String[]{s});
        when(requestMock.getReader()).thenReturn(buffMock);
        when(requestMock.getReader().lines()).thenReturn(stream);
        when(mapperMock.readValue(s,Customer.class)).thenReturn(cus);
        when(ormMock.create(cus)).thenReturn(1);

        csMock.insertCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_CREATED);
    }



    @Test
    public void testInsertCustomerNoArguments() throws IOException {
        Customer cus = new Customer();
        BufferedReader buffMock = Mockito.mock(BufferedReader.class);
        String s = null;
        Stream<String> stream = Arrays.stream(new String[]{s});
        when(requestMock.getReader()).thenReturn(buffMock);
        when(requestMock.getReader().lines()).thenReturn(stream);
        when(ormMock.create(cus)).thenReturn(0);
        when(mapperMock.readValue(s,Customer.class)).thenReturn(cus);

        csMock.insertCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    public void testUpdateCustomer() throws IOException {
        Customer cus = new Customer();
        cus.setId(1);
        BufferedReader buffMock = Mockito.mock(BufferedReader.class);
        String s = "asdf";
        Stream<String> stream = Arrays.stream(new String[]{s});
        when(requestMock.getReader()).thenReturn(buffMock);
        when(requestMock.getReader().lines()).thenReturn(stream);
        when(mapperMock.readValue(s,Customer.class)).thenReturn(cus);
        when(ormMock.update(cus)).thenReturn(1);

        csMock.updateCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testUpdateCustomerZeroId() throws IOException {
        Customer cus = new Customer();
        cus.setId(0);
        BufferedReader buffMock = Mockito.mock(BufferedReader.class);
        String s = "asdf";
        Stream<String> stream = Arrays.stream(new String[]{s});
        when(requestMock.getReader()).thenReturn(buffMock);
        when(requestMock.getReader().lines()).thenReturn(stream);
        when(mapperMock.readValue(s,Customer.class)).thenReturn(cus);
        when(ormMock.update(cus)).thenReturn(1);

        csMock.updateCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    public void testUpdateCustomerBadArguments() throws IOException {
        Customer cus = new Customer();
        cus.setId(1);
        BufferedReader buffMock = Mockito.mock(BufferedReader.class);
        String s = "{\"id:1\"," + "\"fname:test\"" + "\"lname:test\"}";
        Stream<String> stream = Arrays.stream(new String[]{s});
        when(requestMock.getReader()).thenReturn(buffMock);
        when(requestMock.getReader().lines()).thenReturn(stream);
        when(mapperMock.readValue(s,Customer.class)).thenReturn(cus);
        when(ormMock.update(cus)).thenReturn(0);

        csMock.updateCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    public void testDeleteCustomer() {
        when(requestMock.getParameter("id")).thenReturn("1");
        when(csMock.delete(1)).thenReturn(1);

        csMock.deleteCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDeleteCustomerNull() {
        when(requestMock.getParameter("id")).thenReturn(null);
        when(orm.delete(1,Customer.class)).thenReturn(1);

        csMock.deleteCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDeleteCustomerEmptyString() {
        when(requestMock.getParameter("id")).thenReturn("");
        when(orm.delete(1,Customer.class)).thenReturn(1);

        csMock.deleteCustomer(requestMock,responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetCustomer() {
        Customer cus = new Customer(1,"test","test","test","test");
        when(ormMock.findById(1,Customer.class)).thenReturn(cus);
        assertTrue(cus.getId()==1);
    }

    public void testGetAllCustomers() {
        Customer cus1 = new Customer(1,"test","test","test","test");
        Customer cus2 = new Customer(2,"test2","test2","test2","test2");
        Customer cus3 = new Customer(3,"test3","test3","test3","test3");

        list.add(cus1);
        list.add(cus2);
        list.add(cus3);

        when(ormMock.findAll(Customer.class)).thenReturn(list);
        Customer cus = (Customer)list.get(0);
        assertTrue(cus1.getId() == cus.getId());
    }

    public void testInsert() {
        Customer cus = new Customer();
        when(ormMock.create(cus)).thenReturn(1);
    }

    public void testUpdate() {
        Customer cus = new Customer();
        when(ormMock.update(cus)).thenReturn(1);
    }

    public void testDelete() {
        Customer cus = new Customer();
        cus.setId(1);
        when(ormMock.delete(cus.getId(),Customer.class));
    }
}