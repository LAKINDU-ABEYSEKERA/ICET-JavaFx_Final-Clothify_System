import model.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        // 1. Get a session
        Session session = HibernateUtil.getSessionFactory().openSession();

        // 2. Start a transaction
        Transaction transaction = session.beginTransaction();

        // 3. Create the User object
        User admin = new User();
        admin.setEmail("admin@clothify.com");
        admin.setPassword("1234"); // In a real app, encrypt this!
        admin.setRole("Admin");

        // 4. Save to DB
        session.persist(admin);

        // 5. Commit and Close
        transaction.commit();
        session.close();

        System.out.println("Admin User Created Successfully!");
    }
}