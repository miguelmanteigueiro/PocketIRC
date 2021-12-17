public class Main {
    public static void main(String[] args) throws Exception {
        // Create a new instance of the class Server
        Server server = new Server("irc.libera.chat", 6667);
        String nick = "angelo";
        server.login(nick, "", nick, ":garfadaDeSopa");
        server.join("#heni");

        // Create a thread to listen for incoming messages
        Thread receive = new Thread(() -> {
            try {
                server.receive_data();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread send = new Thread(() -> {
            try {
                server.send_data();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the threads
        receive.start();
        send.start();

        // Wait for the threads to finish
        receive.join();
        send.join();
    }
}
