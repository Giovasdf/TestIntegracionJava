import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.CamelContext;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String brokerURL = "tcp://localhost:61616";

        Scanner scanner = new Scanner(System.in);

        try {
            // Crear conexión y sesión
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start(); // Iniciar la conexión
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            CamelContext camelContext = new DefaultCamelContext();
            camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

            camelContext.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("direct:test")
                            .choice()
                            .when(body().contains("Empresa1"))
                            .to("jms:queue:Empresa1")
                            .when(body().contains("Empresa2"))
                            .to("jms:queue:Empresa2")
                            .when(body().contains("Empresa3"))
                            .to("jms:queue:Empresa3")
                            .otherwise()
                            .to("jms:queue:otros");
                }
            });

            camelContext.start();

            while (true) {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Empresa1");
                System.out.println("2. Empresa2");
                System.out.println("3. Empresa3");
                System.out.println("0. Salir");

                int option = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                String messageContent;

                switch (option) {
                    case 1:
                        messageContent = "Empresa1";
                        break;
                    case 2:
                        messageContent = "Empresa2";
                        break;
                    case 3:
                        messageContent = "Empresa3";
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        session.close();
                        connection.close();
                        scanner.close();
                        camelContext.stop();
                        return;
                    default:
                        System.out.println("Opción inválida");
                        continue;
                }

                System.out.println("Ingrese el contenido del mensaje:");
                messageContent += " "+scanner.nextLine(); // Leer el contenido del mensaje desde la entrada del usuario

                camelContext.createProducerTemplate().sendBody("direct:test", messageContent);
                System.out.println("Mensaje enviado a la cola correspondiente: " + messageContent);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
