# ==============================================================================
# context: teaching - Shared Educational Resources in Computer Science
#          ENIB: module IAS - course on neural networks (since fall'18)
# description: train a MLP (Multilayer perceptron on the learning data
# copyright (c) 2018 ENIB. All rights reserved.
# ------------------------------------------------------------------------------
# usage: python
# dependencies: python 3 (see import statements)
# tested on: python 3.6.5 on MacOS 10.13
#            python 3.x on
# ------------------------------------------------------------------------------
# creation: 20-sep-2018 pierre.chevaillier@enib.fr from existing file
# revision: 22-sep-2018 pierre.chevaillier@enib.fr moved SurfaceData to a separate file
# ------------------------------------------------------------------------------
# comments:
# -
# warnings:
# - still under development - not fully tested
# - only for educational purposes
# todos:
#  - test it and then test it again
# ==============================================================================

# Python standard distribution
# MJ TODOS : changes config
import sys, re, os
import time

# Specific modules
import numpy as np

from keras.models import Sequential
from keras.models import model_from_json
from keras.models import load_model
from keras.layers import Dense, Dropout
from keras.callbacks import EarlyStopping
from keras.callbacks import ModelCheckpoint
from keras import optimizers

# The two following lines are needed for plotting on MacOS
# when running the script within a virtual environment
import matplotlib as mpl
mpl.use('TkAgg')

import matplotlib.pyplot as plt
from matplotlib import cm
from mpl_toolkits.mplot3d import Axes3D

# Home made stuffs
from learningData import LearningData
from behaviorData import TwoDVelocityData

from learningExperiment import LearningExperiment
from learningAnalyser import LearningAnalyser

# ==============================================================================
class MLPLearningExperiment(LearningExperiment):

    def __init__(self):
        super().__init__()
        return

    def buildModel(self):
        self.model = Sequential()
        # input layer and first hidden layer
        firstHiddenLayerDim = self.layers[0]
        if self.__class__.verbose > 0:
            print("First hidden layer: " + str(firstHiddenLayerDim) + " activation: " + self.hiddenLayersActivationFunctions[0])
        self.model.add(Dense(firstHiddenLayerDim,
            input_dim = self.learningData.X.dim,
            activation = self.hiddenLayersActivationFunctions[0]))

        # Dropout layer
        if self.dropout:
            self.model.add(Dropout(0.5))

        # Other hidden layer(s), if any
        hiddenLayerDims = self.layers[1:]
        for i in range(len(hiddenLayerDims)):
            hiddenDim = hiddenLayerDims[i]
            if hiddenDim > 0:
                self.model.add(Dense(hiddenDim,
                    activation = self.hiddenLayersActivationFunctions[i+1]))
                if self.__class__.verbose > 0:
                    print("Hidden layer: " + str(hiddenDim) + " activation: " + self.hiddenLayersActivationFunctions[i+1])

        # Output layer
        self.model.add(Dense(self.learningData.Y.dim,
            activation = self.outputLayerActivationFunction))
        if self.__class__.verbose > 0:
            print("Output layer: " + str(self.learningData.Y.dim) + " activation: " + self.outputLayerActivationFunction)

        return

    def defineLearningAlgorithm(self):
        if self.__class__.verbose > 0:
            print("Compiling the model " + self.modelName + " ...")
            print("\t - optimizer : " + self.optimizer)
            print("\t - loss function : " + self.lossFunction)

        #sgd = optimizers.SGD(lr=0.0001, decay=0.0, momentum=0.9, nesterov=False)

        self.model.compile(optimizer = self.optimizer,
            loss = self.lossFunction,
            metrics = ['accuracy'])
        return

    def learn(self, cbks):

        history = self.model.fit(self.learningData.X.trainValues, self.learningData.Y.trainValues,
            batch_size = self.batchSize,
            epochs = self.nMaxEpochs,
            validation_data = (self.learningData.X.validValues, self.learningData.Y.validValues),
            callbacks = cbks,
            verbose = 0)

        self.history = history.history

        # The training loss is the average of the losses over each batch of training data.
        # Because your model is changing over time, the loss over the first batches
        # of an epoch is generally higher than over the last batches.
        # On the other hand, the testing loss for an epoch is computed using the model
        # as it is at the end of the epoch, resulting in a lower loss.
        # Regularization mechanisms, such as Dropout and L1/L2 weight regularization, are turned off at testing time.
        nEpochs = len(self.history['loss'])
        if self.__class__.verbose > 0 and nEpochs > 0:
            trainLoss = self.history['loss'][nEpochs - 1]
            validLoss = self.history['val_loss'][nEpochs-1]
            print("Loss on the training data subset: " + str(trainLoss))
            print("Loss on the validation data subset: " + str(validLoss))

        testLoss, acc_test = self.model.evaluate(self.learningData.X.testValues, self.learningData.Y.testValues)
        print("Loss / test: " + str(testLoss) + " and accuracy: " + str(acc_test))
        return

    def plotLearningCurve(self):
        figFileName = self.resultsDirPath + os.path.sep + self.modelName + '_' + self.lossFunction + '_' + self.timeStamp
        # Next: copied from https://machinelearningmastery.com/display-deep-learning-model-training-history-in-keras/
        plt.figure()
        plt.plot(np.log10(self.history['loss']))
        plt.plot(np.log10(self.history['val_loss']))
        plt.title('Learning curve for ' + self.modelName)
        plt.ylabel('loss: ' + self.lossFunction)
        plt.xlabel('epoch')
        plt.legend(['train', 'valid'], loc = 'upper left')
        plt.savefig(figFileName)
        return

    def defineScaling(self):
        # Input variables
        if self.hiddenLayersActivationFunctions[0] == 'sigmoid':
            self.learningData.X.scalingRanges = [[-4.0, 4.0],[-4.0, 4.0]]
            # sigmoid: s(x) = 1 / (1+exp(x)) ; s(-4) = 0.017 ; s(4) = 0.982
        elif self.hiddenLayersActivationFunctions[0] == "tanh":
            # tanh(-2.5) = -0,987 ; tanh(2.5) = 0.987
            self.learningData.X.scalingRanges = [[-4.0, 4.0],[-2.5, 2.5]]
        else:
            self.learningData.X.scalingRanges = [[-1.0, 1.0],[-1.0, 1.0]]

        # Output variables
        if self.hiddenLayersActivationFunctions[0] == 'sigmoid':
            self.learningData.Y.scalingRanges = [[0.0, 1.0],[0.0, 1.0]]
        else:
             self.learningData.Y.scalingRanges = [[-1.0, 1.0],[-1.0, 1.0]]
        return

    def defineModelFileBaseName(self):
        self.defineModelName()
        return self.resultsDirPath + os.path.sep + self.modelName + '_' + self.timeStamp

    def saveModel(self):
        print("Save the learned model")
        baseName = self.defineModelFileBaseName()

        architectureFilePath = baseName + '.json'
        print("\t - architecture of the neural network: " + architectureFilePath)
        with open(architectureFilePath, 'wt') as json_file:
            architecture = self.model.to_json()
            json_file.write(architecture)

        weightsFilePath = baseName + '.hdf5'
        print("\t - Weights of synaptic connexions: " + weightsFilePath)
        self.model.save(weightsFilePath)
        return

    def loadModel(self, filesPath):
        architectureFile = open(filesPath + ".json")
        architecture = architectureFile.read()
        architectureFile.close()
        self.model = model_from_json(architecture)
        self.model.load_weights(filesPath + ".hdf5")
        return

    def defineXValuesForPredictions(self, number):
        if self.learningData.X.isScaled:
            X1 = np.linspace(self.learningData.X.scalingRanges[0][0],
                self.learningData.X.scalingRanges[0][1],
                num = number,
                endpoint = True)
            X2 = np.linspace(self.learningData.X.scalingRanges[1][0],
                self.learningData.X.scalingRanges[1][1],
                num = number,
                endpoint = True)
        else:
            X1 = np.linspace(self.learningData.X.domains[0][0],
                self.learningData.X.domains[0][1],
                num = number,
                endpoint = True)
            X2 = np.linspace(self.learningData.X.domains[1][0],
                self.learningData.X.domains[1][1],
                num = number,
                endpoint = True)

        X = np.vstack((X1,X2)).T
        return X


    def makePredictions(self, number):
        X = self.defineXValuesForPredictions(number)
        Y = self.model.predict(X)
        XY = np.vstack((X.T,Y.T))
        #print(str(XY))
        return XY

    def makeAndPlotPredictions(self):
        number = 50
        print("Compute the prediction using the trained network ...")
        X = self.defineXValuesForPredictions(number)

        X1 = np.zeros([number, number])
        X2 = np.zeros([number, number])
        Y1 = np.zeros([number, number])
        Y2 = np.zeros([number, number])

        for i in range(number):
            for j in range(number):
                X1[i,j], X2[i,j] = X[j,0], X[i,1]
                x = np.array([[ X1[i,j], X2[i,j]]])
                y = self.model.predict(x)[0]
                Y1[i,j], Y2[i,j] = y[0], y[1]

        # Plot Y1 <- (X1, X2)
        fig = plt.figure()
        ax = Axes3D(fig)
        xLabels = self.learningData.X.axisLabels()
        ax.set_xlabel(xLabels[0])
        ax.set_ylabel(xLabels[1])
        yLabels = self.learningData.Y.axisLabels()
        ax.set_zlabel(yLabels[0])
        ax.plot_surface(X1, X2, Y1, cmap = plt.get_cmap('viridis'))
        plotName = self.learningData.X.names[0] + "_x_" + self.learningData.X.names[1] + "-" + self.learningData.Y.names[0]
        figFileName = self.defineModelFileBaseName() + "_" + plotName + ".png"
        plt.savefig(figFileName)

        # Plot Y2 <- (X1, X2)
        fig = plt.figure()
        ax = Axes3D(fig)
        xLabels = self.learningData.X.axisLabels()
        ax.set_xlabel(xLabels[0])
        ax.set_ylabel(xLabels[1])
        yLabels = self.learningData.Y.axisLabels()
        ax.set_zlabel(yLabels[1])
        ax.plot_surface(X1, X2, Y2, cmap = plt.get_cmap('viridis'))
        plotName = self.learningData.X.names[0] + "_x_" + self.learningData.X.names[1] + "-" + self.learningData.Y.names[1]
        figFileName = self.defineModelFileBaseName() + "_" + plotName + ".png"
        plt.savefig(figFileName)

        return

    def savePredictions(self, XY):
        fName = self.resultsDirPath + os.path.sep \
            + self.modelName + '_' + self.timeStamp \
            + '_predictions.csv'
        np.savetxt(fName, XY, delimiter=';')
        return

def computeAndSavePredictionsForPlotting(model, targetFilePathName):
    distances = np.arange(0, 10, .5)
    azimuths = np.arange(-np.pi, np.pi, np.pi/12)
    predictedOutput = np.zeros(2)
    targetFile = open(targetFilePathName, "w")

    line = ""
    for value in distances:
        line += str(value) + ";"
    line += "\r\n"
    line = re.sub(";\r\n","\r\n", line)
    targetFile.write(line)

    line = ""
    for value in azimuths:
        line += str(value) + ";"
    line += "\r\n"
    line = re.sub(";\r\n","\r\n", line)
    targetFile.write(line)

    for d in distances:
        for a in azimuths:
            dummy = np.array([[d,a]])
            predictedOutput = model.predict(dummy)[0]
            targetFile.write(str(predictedOutput[0]) + ";" + str(predictedOutput[1]) + "\r\n")

    targetFile.close()
    return

# -----------------------------------------------------------------------------

def performTrials(experiment, nTrials):
    analyser = LearningAnalyser()

    while nTrials > 0:
        xp = MLPLearningExperiment()
        xp.copy(experiment)

        learn(xp, analyser)
        xp.makeAndPlotPredictions()

        time.sleep(2) # to ensure different time stamps for each experiment
        nTrials -= 1

    analyser.defineFileName()
    analyser.saveAnalysis()
    return

def learn(experiment, analyser):

    learningData = TwoDVelocityData()
    learningData.initializeFrom(experiment.learningData)
    learningData.loadFromFiles()

    if experiment.__class__.verbose > 0:
        print("Domains for X " + str(learningData.X.domains))
        print("Domains for Y " + str(learningData.Y.domains))

    # --- Initializations
    currentExp = MLPLearningExperiment()
    currentExp.copy(experiment)
    currentExp.defineModelName()

    # Set the learning data used for learning
    currentExp.learningData = learningData

     # Scale the data / activation functions used
    currentExp.defineScaling()
    currentExp.scaleLearningData()
    if experiment.__class__.verbose > 0:
        currentExp.learningData.X.printStats()
        currentExp.learningData.Y.printStats()

    # Creation of the neural network
    currentExp.buildModel()
    currentExp.defineLearningAlgorithm()

    # Learning
    print("Learning " + currentExp.modelName + " ...")
    callbacks = []

    currentExp.learn(callbacks)
    currentExp.plotLearningCurve()

    # Save the trained model for further usage
    currentExp.saveModel()

    # Output the predictions
    #currentExp.makeAndPlotPredictions()

    # Collect some data on the current learning experiment
    if analyser:
        analyser.setExperiment(currentExp)
        analyser.analyse()
    
    return

if __name__ == "__main__":
    # Data
    # used to define the dimensions of the data (X,Y)
    #MJ : input ??? values,
    #data = LearningData(2, 4)
    data = LearningData(3, 2)

    # Where to read and write the different file
    if len(sys.argv) > 0:
        learningDataDir = sys.argv[1]
    else:
        learningDataDir = '.'
    if not os.path.isdir(learningDataDir):
        print("Error: learning data directory " + learningDataDir + " does not exist.")
        sys.exit(1)
    else:
       data.dirPath = learningDataDir

    if len(sys.argv) > 1:
        data.fileNamesPrefix = sys.argv[2]
    else:
        print("Error: provide the prefix of the learning data files' name")
        sys.exit(2)

    # Configuration of the learning experiment
    experimentParameters = LearningExperiment()
    experimentParameters.name = 'Behavior learning'
    experimentParameters.modelNamePrefix = 'MLP'
    experimentParameters.resultsDirPath = learningDataDir

    experimentParameters.learningData = data

    experimentParameters.hiddenLayersActivationFunctions = ['sigmoid', 'sigmoid', 'sigmoid', 'sigmoid', 'sigmoid'] # TODO set this value
    experimentParameters.outputLayerActivationFunction = 'tanh' # TODO set this value
    experimentParameters.lossFunction = 'mse'
    #experimentParameters.lossFunction = 'categorical_crossentropy'
    experimentParameters.optimizer = 'adam'

    # Layers to DEFINE for better result
    experimentParameters.layers = np.array([12,18,12,16,10]) # TODO define here the configuration of the network
    experimentParameters.nMaxEpochs = 1000 # TODO set this value

    LearningExperiment.verbose = 1

    if (len(experimentParameters.hiddenLayersActivationFunctions) != len(experimentParameters.layers)):
        print("Error: number of layers and number of activation functions is different.")
        sys.exit(2)

    # Launch the learning
    learn(experimentParameters, LearningAnalyser())

# End of File
# ==============================================================================
