package util;

import model.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import model.entity.Product;
import model.entity.Order;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                configuration.configure();

                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Product.class);
                configuration.addAnnotatedClass(Order.class);

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("There was an issue building the factory");
            }
        }
        return sessionFactory;
    }
}