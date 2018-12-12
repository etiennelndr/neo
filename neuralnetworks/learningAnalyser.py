# ==============================================================================
# context: teaching - Shared Educational Resources in Computer Science
#          ENIB: module IAS - course on neural networks (since fall'18)
# description: classe for analysing the learning
# copyright (c) 2018 ENIB. All rights reserved.
# ------------------------------------------------------------------------------
# usages: from <thisFileName> import ...
# dependencies: python 3 (see import statements)
# tested on: python 3.6.5 on MacOS 10.13
#            python 3.x on
# ------------------------------------------------------------------------------
# creation: 20-sep-2018 pierre.chevaillier@enib.fr from existing file
# revision:
# ------------------------------------------------------------------------------
# comments:
# - not used for this exercice
# warnings:
# - still under development - not fully tested
# - only for educational purposes
# todos:
#  - test it and then test it again
# ==============================================================================

# Python standard distribution
import sys, os, re
import datetime

# Specific modules
import numpy as np

# Home made stuffs
from learningData import LearningVariable
from learningData import LearningData
from learningExperiment import LearningExperiment

# ------------------------------------------------------------------------------
class LearningAnalyser:

    @classmethod
    def fileHeader(cls):
        # parameters of the experiment
        header = "id;model_name;layers;scaled_X;scaled_Y;hidden_activ_func;output_activ_func;dropout;loss_func;optimizer;"
        # Learning curves (train + valid)
        header += "nEpochs;minTrainLossValue;minTrainLossEpoch;maxTrainLossValue;maxTrainLossEpoch;"
        header += "minValidLossValue;minValidLossEpoch;maxValidLossValue;maxValidLossEpoch;"
        # Predictions 
        header += "minGeneralizationGapValue;minGeneralizationGapEpoch;maxGeneralizationGapValue;maxGeneralizationGapEpoch;"
        header += "predictions_mins;predictions_maxs;predictions_means;predictions_stds;"
        # Learning data (X: input ; Y: output) - 3 subsets: training, validation and test
        header +="X_train_mins;X_train_maxs;X_train_means;X_train_stds;"
        header +="X_valid_mins;X_valid_maxs;X_valid_means;X_valid_stds;"
        header +="X_test_mins;X_train_maxs;X_test_means;X_test_stds;"
        header +="X_train_mins;X_train_maxs;X_train_means;X_train_stds;"
        header +="X_valid_mins;X_valid_maxs;X_valid_means;X_valid_stds;"
        header +="X_test_mins;X_train_maxs;X_test_means;X_test_stds;"
        
        header +="Y_train_mins;Y_train_maxs;Y_train_means;Y_train_stds;"
        header +="Y_valid_mins;Y_valid_maxs;Y_valid_means;Y_valid_stds;"
        header +="Y_test_mins;Y_train_maxs;Y_test_means;Y_test_stds;"
        header +="Y_train_mins;Y_train_maxs;Y_train_means;Y_train_stds;"
        header +="Y_valid_mins;Y_valid_maxs;Y_valid_means;Y_valid_stds;"
        header +="Y_test_mins;Y_train_maxs;Y_test_means;Y_test_stds;"
        
        return header

    def __init__(self):
        self.timeStamp = datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S")
        self.filePath = ''
        self.experiment = None
        self.records = []
        return
    
    def setExperiment(self, xp):
        self.experiment = xp
        self.defineFileName()
        return

    def defineFileName(self):
        if self.experiment:
            self.filePath = self.experiment.resultsDirPath + os.sep \
                + self.experiment.modelNamePrefix + '_' + self.timeStamp + '_experiment.csv'
        pass
    
    def recordExperimentParameters(self):
        xp = self.experiment
        params = []
        params.append(xp.timeStamp)
        params.append(xp.modelName)
        params.append(xp.layers.tolist())
        params += [xp.scaledX, xp.scaledY]
        for hiddenLayer in xp.hiddenLayersActivationFunctions:
            params.append(hiddenLayer)
        params.append(xp.outputLayerActivationFunction)
        params.append(xp.dropout)
        params.append(xp.lossFunction)
        params.append(xp.optimizer)
        return params

    def analyse(self):
        record = self.recordExperimentParameters()
        record += self.analyseLearning()
        record += self.analyseLearningData()
        predictions = self.experiment.makePredictions(100)
        record += self.analysePredictions(predictions)
        self.records.append(record)
        return

    def analyseLearning(self):
        values = []
        noValues = [] # TODO 16 zeros
        xp = self.experiment
        nEpochs = len(xp.history['loss'])
        if nEpochs < 1:
            values += noValues
            return values
        
        values.append(nEpochs)
        firstTrainLoss = xp.history['loss'][0]
        lastTrainLoss = xp.history['loss'][nEpochs - 1]
        values += [firstTrainLoss, lastTrainLoss]

        trainLoss = np.array(xp.history['loss'])
        minTrainLossValue = np.amin(trainLoss)
        minTrainLossEpoch = np.argmin(trainLoss)
        maxTrainLossValue = np.amax(trainLoss)
        maxTrainLossEpoch = np.argmax(trainLoss)
        values += [minTrainLossValue, minTrainLossEpoch, maxTrainLossValue, maxTrainLossEpoch]

        firstValidLoss = xp.history['val_loss'][0]
        lastValidLoss = xp.history['val_loss'][nEpochs - 1]
        values += [firstValidLoss, lastValidLoss]

        validLoss = np.array(xp.history['val_loss'])
        minValidLossValue = np.amin(validLoss)
        minValidLossEpoch = np.argmin(validLoss)
        maxValidLossValue = np.amax(validLoss)
        maxValidLossEpoch = np.argmax(validLoss)
        values += [minValidLossValue, minValidLossEpoch, maxValidLossValue, maxValidLossEpoch]

        minGeneralizationGapValue = np.amin(validLoss - trainLoss)
        minGeneralizationGapEpoch = np.argmin(validLoss - trainLoss)
        maxGeneralizationGapValue = np.amax(validLoss - trainLoss)
        maxGeneralizationGapEpoch = np.argmax(validLoss - trainLoss)
        values += [minGeneralizationGapValue, minGeneralizationGapEpoch, maxGeneralizationGapValue, maxGeneralizationGapEpoch]
        return values
    
    def analysePredictions(self, Y):
        values = []
        # Y: predictions
        mins = np.amin(Y, axis=0)
        values.append(mins)
        values.append(np.amax(Y, axis=0))
        values.append(np.mean(Y, axis=0))
        values.append(np.std(Y, axis=0))
        return values

    def analyseLearningData(self):
        ld = self.experiment.learningData
        values = []
        values += LearningVariable.computeStats(ld.X.trainValues)
        values += LearningVariable.computeStats(ld.X.validValues)
        values += LearningVariable.computeStats(ld.X.testValues)
        values += LearningVariable.computeStats(ld.Y.trainValues)
        values += LearningVariable.computeStats(ld.Y.validValues)
        values += LearningVariable.computeStats(ld.Y.testValues)
        return values

    def saveAnalysis(self):
        f = open(self.filePath, "w")
        f.write(self.__class__.fileHeader() + "\r\n")
        for record in self.records:
            line = ""
            for colums in record:
                line += str(colums) + ";"
            line += "\r\n"
            line = re.sub(";\r\n","\r\n", line)
            f.write(line)
        f.close()
        return

# ==============================================================================
if __name__ == "__main__":
    print("This moduel is not runnable")

# end of file
# ==============================================================================