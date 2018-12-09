# creation: 23-aug-2018 pierre.chevaillier@enib.fr

import sys, getopt
from os import path

import numpy as np
from keras.models import model_from_json
from keras.models import load_model


class NeuralVelocityController:
    def __init__(self):
        self.architectureFilename = ''
        self.weightsFileName = ''
        self.seqLength = 1

    def configure (self, opts, args):
        print(str(opts))

        for opt, arg in opts:
            if opt in ("-a", "--archi"):
                self.architectureFilename = arg
            elif opt in ("-l", "--length"):
                self.seqLength = int(arg)
            elif opt in ("-w", "--weights"):
                self.weightsFileName = arg

        if self.architectureFilename == '':
            print("Error: you must provide the name of the file for the network architecture: --archi <networkName.json>")
            sys.exit()

        if self.weightsFileName == '':
            print("Error: you must provide the name of the file for the synaptic connections weights: --weights <networkName.hdf5>")
            sys.exit()

        if not path.exists(self.architectureFilename):
            print("Error: neural architecture file " 
                + self.architectureFilename 
                + " does not exist...")
            sys.exit()

        if not path.exists(self.weightsFileName):
            print("Error: synaptic connection weights file " 
                + self.weightsFileName 
                + " does not exist...")
            sys.exit()

        print("Architecture loaded from " + self.architectureFilename)
        print("Connexion weights from " + self.weightsFileName)
        print("Number of positions: " + str(self.seqLength))

    def build(self):
        json_file = open(self.architectureFilename)
        architecture = json_file.read()
        json_file.close()
        self._model = model_from_json(architecture)
        self._model.load_weights(self.weightsFileName)
        return

    def process(self, features):
        return (self._model.predict(features))[0]

def usage():
    print("usage:")

def parseCommandLine():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "halw:v", ["help", "archi=", "length=", "weights="])
    except getopt.GetoptError as err:
        print(str(err))
        usage()
        sys.exit(2)
    return opts, args

def unitaryTests():
    opts, args = parseCommandLine()

    neuralController = NeuralVelocityController()
    neuralController.configure(opts, args)
    neuralController.build()

    feature = [2044.82, -350.42]
    sample = np.array([feature])
    print(str(neuralController.process(sample)))

if __name__ == "__main__":
    unitaryTests()

# End of File