try:
    import sys
    import socket
    from threading import Thread
except ImportError as err:
    print(err.msg)
    sys.exit(1)

class Server():
    # Constructor
    def __init__(self, maxNbrOfClients, address, port):
        self.__maxNbrOfClients = maxNbrOfClients
        self.__address = address
        self.__port = port
        self.__socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__threads  = []
        self.__threadId = []

    # This method is the main method of the class Server
    def run(self):
        print("running")
        # Bind socket
        self.__socket.bind((self.__address, self.__port))
        # Listen to maxNbrOfClients clients
        self.__socket.listen(self.__maxNbrOfClients)

        thread_stats = Thread(target=self.__stats).start()
        self.__threads.append(thread_stats)
        self.__threadId.append("stats")

        client, address = self.__socket.accept()

        if (address[0] == '' or address[0] == 'localhost' or address[0] == '127.0.0.1'):
            print("new client : " + address[0])
            thread_client = Thread(target=self.__runClient, args=(client,)).start()
            self.__threads.append(thread_client)
            self.__threadId.append("client")
        else:
            client.close()

    def __stats(self):
        t = ""
        while (t != "stop"):
            t = sys.stdin.read().split("\n")[0]
            print(t)

        self.__close()

    def __runClient(self, client):
        print("runClient")

        resp = client.recv(255)
        if resp != "":
            print(resp)

            client.send(resp + ":YOLOOOOO")

    def __close(self):
        self.__socket.close()

        for i in range (0, len(self.__threads)):
            if (self.__threadId == "client"):
                self.__threads[i].close()


if __name__ == "__main__":
    # Instatiate the server
    server = Server(8, 'localhost', 12400)
    # Run it
    server.run()