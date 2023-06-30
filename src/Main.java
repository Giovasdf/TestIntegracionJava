import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String brokerURL = "tcp://localhost:61616";
        String sendTopicName = "Empresa1";
        String receiveTopicName = "Empresa2";

        Scanner scanner = new Scanner(System.in);

        // Envío de mensajes
        System.out.println("== Envío de mensajes ==");
        System.out.println("Ingrese el contenido del mensaje (escriba 'fin' para salir):");
        String messageContent = scanner.nextLine();

        try {
            // Crear conexión y sesión
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Crear el productor para enviar mensajes a un topic
            Topic sendTopic = session.createTopic(sendTopicName);
            MessageProducer producer = session.createProducer(sendTopic);

            while (!messageContent.equals("fin")) {
                // Crear mensaje de texto
                TextMessage message = session.createTextMessage(messageContent);

                // Enviar mensaje al topic
                producer.send(message);
                System.out.println("Mensaje enviado: " + messageContent);

                System.out.println("Ingrese el contenido del mensaje (escriba 'fin' para salir):");
                messageContent = scanner.nextLine();
            }

            // Cerrar conexión y recursos
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        // Recepción de mensajes
        System.out.println("\n== Recepción de mensajes ==");

        try {
            // Crear conexión y sesión
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Crear el suscriptor para recibir mensajes de un topic
            Topic receiveTopic = session.createTopic(receiveTopicName);
            MessageConsumer consumer = session.createConsumer(receiveTopic);

            // Crear el listener para procesar los mensajes recibidos
            MessageListener listener = new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String messageContent = textMessage.getText();
                            System.out.println("Mensaje recibido: " + messageContent);
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            };

            // Asociar el listener al consumidor
            consumer.setMessageListener(listener);

            // Iniciar la conexión
            connection.start();

            System.out.println("Presione enter para detener la recepción de mensajes.");
            scanner.nextLine();

            // Detener la recepción de mensajes
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
