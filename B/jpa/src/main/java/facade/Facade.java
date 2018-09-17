/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facade;

import Entity.Customer;
import Entity.ItemType;
import Entity.Order;
import Entity.OrderLine;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author kristoffer
 */
public class Facade {

    EntityManagerFactory emf;

    public Customer createCustomer(Customer customer) {
        EntityManager em = emf.createEntityManager();
        //tjekker om customer er null ved at lave en ny instans af et customer objekt, og retunere det
        // hvis customer objektet ikke eksistere vil der blive retuneret null
        Customer c = em.find(Customer.class, customer);
        try {
            if (c != null) {
                em.getTransaction().commit();
                em.persist(customer);
                System.out.println("Person persisted is " + customer.toString());
            }
        } finally {
            em.close();
        }
        return c;
    }

    public Customer findustomer(Customer customer) {
        EntityManager em = emf.createEntityManager();
        //tjekker om customer er null ved at lave en ny instans af et customer objekt, og retunere det
        // hvis customer objektet ikke eksistere vil der blive retuneret null
        Customer c = em.find(Customer.class, customer);
        try {
            if (c != null) {
                System.out.println("Person found is " + c.toString());
            }
        } finally {
            em.close();
        }
        return c;
    }

    public Customer altFindCustomer(Customer customer) {
        EntityManager em = emf.createEntityManager();
        //tjekker om customer er null ved at lave en ny instans af et customer objekt, og retunere det
        // hvis customer objektet ikke eksistere vil der blive retuneret null
        return em.find(Customer.class, customer);
    }

    public List<Customer> findAllCustomer() {
        EntityManager em = emf.createEntityManager();
        //denne query kunne også sættes direkte ind således, fordelen ved at lave en nameQuery er, at den kan genbruges flere gange, så
        //hvis man har et statement, der skal eksikveres flere gange, er det en fordel
        Query queryAlternative = em.createQuery("select c from Customer c"); //inde i er et JPQL statement
        TypedQuery<Customer> typedQuery = em.createQuery("select c from Customer c", Customer.class); // her er et eksempel, hvor man angiver typen af objektet på querien, denne er smart, når man ved hvilket objekt, man har med at gøre 
        //mere specifik vælger vi 'c' fra Customer table, som vi navngiver c, ved at skriver Customer c (c er navnet på klassen)
        Query query = em.createNamedQuery("findallcustomers");

        return query.getResultList();
    }

    public Order createOrder(Order order) {
        EntityManager em = emf.createEntityManager();
        Order o = em.find(Order.class, order);
        try {
            if (o != null) {
                em.persist(o);
                em.getTransaction().commit();
                System.out.println("Product created is " + o.toString());
            }
        } finally {
            em.close();
        }
        return o;
    }

    public Customer addOrderToCustomer(Customer customer, Order order) {
        EntityManager em = emf.createEntityManager();
        try{
        if(customer != null){
        customer.addToList(order);
        em.getTransaction().commit();
        em.persist(order);}
        }finally{
            em.close();
        }
        return customer;
    }

    
    public Order findOrder(Order order){
        EntityManager em = emf.createEntityManager();
        //tjekker om customer er null ved at lave en ny instans af et customer objekt, og retunere det
        // hvis customer objektet ikke eksistere vil der blive retuneret null
        Order o = em.find(Order.class, order);
        try {
            if (o != null) {
                System.out.println("Person found is " + o.toString());
            }
        } finally {
            em.close();
        }
        return order;
    }
    
    
    public List<Order> findOrderByCustomer (Customer customer){
    EntityManager em = emf.createEntityManager();
       Query query = em.createQuery("SELECT o FROM Customer c JOIN c.orders o");
       return query.getResultList();
    }
    
    
    public Order insertOrderlineintoOrder(OrderLine orderline, Order order){
         EntityManager em = emf.createEntityManager();
         try{
             if(order != null){
              order.addToList(orderline);
              em.getTransaction().commit();
              em.persist(orderline); }
         }finally{
             em.close();
         }
         return order;
    }
    
    
    public ItemType insertItemTypeIntoOrderLine(ItemType itemtype, OrderLine orderline){
          EntityManager em = emf.createEntityManager();
          try{
          if(itemtype != null){
          orderline.setItemtype(itemtype);
           em.getTransaction().commit();
          em.persist(orderline); }
          }finally{
              em.close();
          }
          return itemtype;
       
    }
    
    
    // denne metoder vil måske virker, fordi den vil blive udført før der bliver persistet 
    @PreUpdate
    @PrePersist 
    public int getTotalPrice(ItemType itemtype, OrderLine orderline){
        return itemtype.getPrice() * orderline.getQuantity();
    }
    
    
    
    // ekstra metoder 
    //JPQL indefra java metoder
    
    //retuner liste af Customer fra java metode, ved brug af Scanner
    
    public Customer filterCustomers(){
        EntityManager em = emf.createEntityManager();
        System.out.println("Enter the name of the customer");
        Scanner scanner = new Scanner(System.in); 
        String name = scanner.nextLine();
        
        TypedQuery<Customer> query = em.createQuery("select c from Customer c where c.name like '%" + name + "%'", Customer.class);
       
        
        
        //her er for at fidne en et enkelt customer objekt, det kunne også være en liste for andre ting
        Customer customer =  query.getSingleResult();
        return customer;
    }
    
    
    //nedenunder laver vi en ligende metode som for ovenover, men vi vælger at sætte nogle flere parametre ind
    // som fx hvis vi vil have at en kunde ikke skal have købt noget før eller lign
    //her kan man tilføje flere parametre
    // fordi vi har både navn og email som parametre på vores Customer Entity
    //kan vi fx udvælge et navn, hvor email ikke er lig noget. 
    public void filterCustomersSpecefic (){
        EntityManager em = emf.createEntityManager();
        System.out.println("Enter the name of the customer");
        Scanner scanner = new Scanner(System.in); 
        String name = scanner.nextLine();
        
        // problemet ved at skrive en query som nedstående er at man kan 
        //lave en SQL-injection, eller i dette tilfælde 
        // en JPQL injection, fordi brugeren skriver direkte ind og kan tilføje
        //denne streng nedennder er sårbar overfor SQL-injections. 
        TypedQuery query = em.createQuery(
                
                //når vi bruger %-tegnet i SQL eller JPQL hedder det et wild card
                // se her for mere info  https://www.w3schools.com/sql/sql_wildcards.asp
                "select c from Customer c " + 
              " where c.email not like '%kasper@gmail.com%' and "
                + " c.name like  '%" + name 
                + "%'", Customer.class);
        
        TypedQuery queryReal = em.createQuery(
                
                //når vi bruger %-tegnet i SQL eller JPQL hedder det et wild card
                // se her for mere info  https://www.w3schools.com/sql/sql_wildcards.asp
                "select c from Customer c " + 
              " where c.email not like '%kasper@gmail.com%' and "
                + " c.name like  : name", Customer.class);
       
        
        // for at undgå SQL-injection indsætter vi parametret efter strengen er blevet lavet
        // ligesom prepared statements i 
         query.setParameter("name", "%" + name + "%");
        
        //her er for at fidne en et enkelt customer objekt, det kunne også være en liste for andre ting
        Customer customer =(Customer) query.getSingleResult();
    }
    
    }

