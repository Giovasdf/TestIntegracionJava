import javax.jms.*;

public class MessageSender {
    private static String brokerURL = "tcp://localhost:61616";

    public static void sendMessage(String queueName, String messageText) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            // Establecer la conexión con ActiveMQ
            ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(brokerURL);
            connection = connectionFactory.createConnection();
            connection.start();

            // Crear una sesión y una cola de mensajes
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);

            // Crear un productor de mensajes
            producer = session.createProducer(queue);

            // Crear un mensaje de texto
            TextMessage message = session.createTextMessage();
            message.setText(messageText);

            // Enviar el mensaje
            producer.send(message);
            System.out.println("Mensaje enviado a la cola " + queueName + ": " + messageText);

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (producer != null)
                    producer.close();
                if (session != null)
                    session.close();
                if (connection != null)
                    connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }


}
