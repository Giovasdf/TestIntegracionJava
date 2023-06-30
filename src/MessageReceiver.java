import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MessageReceiver {


    private static boolean receiving = true;

    public static void receiveMessages(String brokerURL, String queueName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);

            while (true) {
                Message message = consumer.receive();

                // Verificar si se ha recibido un mensaje de salida especial
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String contenido = textMessage.getText();
                    System.out.println("Mensaje recibido: " + contenido);
                    // Realiza el procesamiento adicional del mensaje según tus necesidades

                    // Verificar si se cumple alguna condición para salir del bucle
                    if (contenido.equals("fin")) {
                        break; // Salir del bucle cuando se recibe el mensaje "fin"
                    }
                }
            }

            consumer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public static void stopReceiving() {
        receiving = false;
    }
}

