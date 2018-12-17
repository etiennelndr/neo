"""
Exemple de lancement de ce programme:
    python3 server.py --archi data/MLP_3x3x4_2018-12-09-01-29-58.json --weights data/MLP_3x3x4_2018-12-09-01-29-58.hdf5
"""

try:
    import sys
    import socket
    import getopt
    import struct
    from agentModelAdapter import AgentModelAdapter
    from neuralController import NeuralVelocityController
    from threading import Thread, Lock
    import numpy as np
except ImportError as err:
    print(err)
    sys.exit(1)

class Server():
    # Constructor
    def __init__(self, maxNbrOfClients, address, port):
        self.__maxNbrOfClients = maxNbrOfClients
        self.__address        = address
        self.__port           = port
        self.__socket         = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__threads        = []
        self.__threadId       = []
        self.__state          = ""
        self.__mutex          = Lock()

        # Uncomment for complete map
        # Inputs
        self.__maxX, self.__minX     = 3038.19, 928.88
        self.__maxY, self.__minY     = 543.43, -2562.1
        # Outputs
        self.__maxVx, self.__minVx   = 790.65, -440.0
        self.__maxVy, self.__minVy   = 759.32, -779.81
        self.__maxVz, self.__minVz   = 328.52, -500.23

    # This method is the main method of the class Server
    def run(self):
        print("running")
        
        opts, args = self.__parseCommandLine()
        
        self.__behaviorController = NeuralVelocityController()
        self.__behaviorController.configure(opts, args)
        self.__behaviorController.build()
        
        # Bind socket
        self.__socket.bind((self.__address, self.__port))
        # Listen to maxNbrOfClients clients
        self.__socket.listen(self.__maxNbrOfClients)

        #thread_stats = Thread(target=self.__stats).start()
        #self.__threads.append(thread_stats)
        #self.__threadId.append("stats")

        while (self.__state != "stop"):
            client, address = self.__socket.accept()

            if ((address[0] == '' or address[0] == 'localhost' or address[0] == '127.0.0.1') and (len(self.__threads) <= self.__maxNbrOfClients)):
                print("new client : " + address[0])
                #thread_client = Thread(target=self.__runClient, args=(client,)).start()
                self.__runClient(client)
                #self.__threads.append(thread_client)
                #self.__threadId.append("client")
            else:
                client.close()

    def __stats(self):
        while (self.__state != "stop"):
            self.__state = sys.stdin.readline().split("\n")[0]
            if (self.__state == "nbrOfClients"):
                print(len(self.__threads) - 1)
        self.__close()
        
    def __parseCommandLine(self):
        try:
            opts, args = getopt.getopt(sys.argv[1:], "halw:v", ["help", "archi=", "length=", "weights="])
        except getopt.GetoptError as err:
            print(str(err))
            sys.exit(2)
        verbose = False
        for opt, arg in opts:
            if opt == "-v":
                verbose = True
            elif opt in ("-h", "--help"):
                sys.exit()
    
        return opts, args

    def __runClient(self, client):
        print("runClient")

        while (self.__state != "stop"):
            try:
                req = client.recv(8192)
                if req != "":
                    # Print the request
                    self.__mutex.acquire()
                    values = self.__splitData(req)
                    result = self.__processData(values)
                    self.__mutex.release()
                    client.send(bytes("[", "utf-8") 
                                + bytes(str(result[0]),"utf-8")
                                + bytes(" ", "utf-8")
                                + bytes(str(result[1]),"utf-8")
                                + bytes(" ", "utf-8")
                                + bytes(str(result[2]),"utf-8")
                                + bytes("]", "utf-8")
                                + bytes("\n", "utf-8")) # MUST NOT forget end of line
            except socket.error as error:
                client.close()
                break

        # Close the connection
        client.close()
        
    def __processData(self, data):
        result = self.__behaviorController.process(data)
        vx, vy, vz = self.__transformOutputDatas(result[0], result[1], result[2])
        print(str(data) + ' --> ' + str(vx) + ', ' + str(vy)+ ', ' + str(vz))
        return [vx, vy, vz]
    
    def __splitData(self, data):
        print(data)

        data = data.decode("utf-8")
        d = data.split("[")[1].split(']')[0]
        splitValues = d.split(" ")

        # Normalize input datas
        x, y = self.__normalizeInputDatas(float(splitValues[0]), float(splitValues[1]))

        values = [x, y]
        datas = np.array([values])

        return datas

    def __normalizeInputDatas(self, x, y):
        if x > self.__maxX:
            x = self.__maxX
        elif x < self.__minX:
            x = self.__minX
        nx = (x - self.__minX) / (self.__maxX - self.__minX)
        
        if y > self.__maxY:
            y = self.__maxY
        elif y < self.__minY:
            y = self.__minY
        ny = (y - self.__minY) / (self.__maxY - self.__minY)

        return nx, ny

    def __transformOutputDatas(self, vx, vy, vz):
        tvx = ((vx + 1)/2)*(self.__maxVx - self.__minVx) + self.__minVx
        tvy = ((vy + 1)/2)*(self.__maxVy - self.__minVy) + self.__minVy
        tvz = ((vz + 1)/2)*(self.__maxVz - self.__minVz) + self.__minVz
        return tvx, tvy, tvz

    def __close(self):
        # Close the main socket
        self.__socket.close()
        
        for i in range (0, len(self.__threads)):
            if (self.__threadId == "client"):
                self.__threads[i].join()
        
        for i in range (0, len(self.__threads)):
            if (self.__threadId == "client"):
                self.__threads[i].close()


if __name__ == "__main__":
    # Instatiate the server
    server = Server(8, 'localhost', 12400)
    # Run it
    server.run()