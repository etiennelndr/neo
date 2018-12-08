# ==============================================================================
# context: teaching - Shared Educational Resources in Computer Science
#          ENIB: module IAS - course on neural networks (since fall'18)
# description: classes used to manage learning data 
#              and for setting the learning parameters
# copyright (c) 2018 ENIB. All rights reserved.
# ------------------------------------------------------------------------------
# usages: from <thisFileName> import ...
# dependencies: python 3 (see import statements)
# tested on: python 3.6.5 on MacOS 10.13
#            python 3.x on
# ------------------------------------------------------------------------------
# creation: 07-sep-2018 pierre.chevaillier@enib.fr
# revision: 20-sep-2018 pierre.chevaillier@enib.fr code cleaning
# revision: 22-sep-2018 pierre.chevaillier@enib.fr remove LearningExperiment
# ------------------------------------------------------------------------------
# comments:
# - 
# warnings:
# - still under development - not fully tested
# - only for educational purpose
# todos:
#  - test it and then test it again
# ==============================================================================

import re, os
import datetime
import numpy as np

# ------------------------------------------------------------------------------
class LearningVariable:

    @classmethod
    def computeStats(cls, values):
        stats = []
        stats.append(np.amin(values, axis=0))
        stats.append(np.amax(values, axis=0))
        stats.append(np.mean(values, axis=0))
        stats.append(np.std(values, axis=0))
        return stats

    @classmethod
    def printStatsOnDataSet(cls, names, values):
        for i in range(len(names)):
            print("\t" + names[i] + ": \t" \
                + " min = " + str(np.amin(values[:,i])) \
                + " mean = " + str(np.mean(values[:,i])) \
                + " max = " + str(np.amax(values[:,i])) \
                + " std dev = " + str(np.std(values[:,i])) \
                )
        return

    def __init__(self, dim):
        self.dim = dim
        self.isScaled = False
        
        # the following properties are defined for each dimension of the variable
        self.names = []
        self.labels = []
        self.units = []
        self.domains = []
        self.scalingRanges = []

        # next are the values of the variable, for each dimension, for the 3 data subsets
        self.trainValues = []
        self.validValues = []
        self.testValues = []
        return
    
    def axisLabels(self):
        labels = []
        for j in range(self.dim):
            label = ''
            if len(self.labels) > j:
                label = self.labels[j]
            elif len(self.names) > j:
                label = self.names[j]
            if len(self.units) > j:
                label += ' (' + self.units[j] + ')'
            labels.append(label)
        return labels

    def printStats(self):
        if self.isScaled:
            print("Data have been scaled from " + str(self.domains) + " to " + str(self.scalingRanges))
        else:
            print("Domains of reference (raw data)" + str(self.domains))
        print("Stats on the training data set:")
        self.__class__.printStatsOnDataSet(self.names, self.trainValues)
        print("Stats on the validation data set:")
        self.__class__.printStatsOnDataSet(self.names, self.validValues)
        print("Stats on the test data set:")
        self.__class__.printStatsOnDataSet(self.names, self.testValues)
        return

    def convertFromRawToScaled(self):
        done = False
        sourceDomains = np.array(self.domains)
        targetDomains = np.array(self.scalingRanges)
        if len(self.domains) > 0 and len(self.domains) == len(self.scalingRanges):
            self.trainValues = scaleTo(sourceDomains, targetDomains, self.trainValues)
            self.validValues = scaleTo(sourceDomains, targetDomains, self.validValues)
            self.testValues = scaleTo(sourceDomains, targetDomains, self.testValues)
            done = True
        else:
            print("ERROR in convertFromRawToScaled " + str(self.domains) + " " + str(len(self.domains)))
        return done

# ------------------------------------------------------------------------------
class LearningData:

    @classmethod
    def readDataFromFile(cls, dataFilePath):
        pass

    @classmethod
    def readPredictionsFromFile(cls, dataFilePath):
        pass

    def __init__(self, xDim, yDim):
        self.dirPath = ''
        self.fileNamesPrefix = ''
        self.trainingDataFilePath = ''
        self.validationDataFilePath = ''
        self.testDataFilePath = ''
        self.X = LearningVariable(xDim)
        self.Y = LearningVariable(yDim)
        return

    def initializeFrom(self, origin):
        self.dirPath = origin.dirPath
        self.fileNamesPrefix = origin.fileNamesPrefix
        return

    def defineFilePaths(self):
        self.trainingDataFilePath = self.dirPath + os.path.sep + self.fileNamesPrefix + '_train.csv'
        self.validationDataFilePath = self.dirPath + os.path.sep + self.fileNamesPrefix + '_valid.csv'
        self.testDataFilePath = self.dirPath + os.path.sep + self.fileNamesPrefix + '_test.csv'
        return


    def loadFromFiles(self):
        self.defineFilePaths()
        print("Load the training data set from " + self.trainingDataFilePath)
        self.X.trainValues, self.Y.trainValues = self.__class__.readDataFromFile(self.trainingDataFilePath)
        print("\t - Number of records in the training data set: " + str(len(self.X.trainValues)))
        
        print("Load the validation data set from " + self.validationDataFilePath)
        self.X.validValues, self.Y.validValues = self.__class__.readDataFromFile(self.validationDataFilePath)
        print("\t - Number of records in the validation data set: " + str(len(self.X.validValues)))

        print("Load the evaluation (test) data set from " + self.testDataFilePath)
        self.X.testValues, self.Y.testValues = self.__class__.readDataFromFile(self.testDataFilePath)
        print("\t - Number of records in the evaluation (test) data set: " + str(len(self.X.testValues)))
        return
    
    def scaleXTo(self, domains):
        sourceDomains = np.array(self.X.domains)
        targetDomains = np.array(domains)
        self.X.trainValues = scaleTo(sourceDomains, targetDomains, self.X.trainValues)
        self.X.validValues = scaleTo(sourceDomains, targetDomains, self.X.validValues)
        self.X.testValues = scaleTo(sourceDomains, targetDomains, self.X.testValues)
        return

    def scaleYTo(self, domains):
        sourceDomains = np.array(self.Y.domains)
        targetDomains = np.array(domains)
        self.Y.trainValues = scaleTo(sourceDomains, targetDomains, self.Y.trainValues)
        self.Y.validValues = scaleTo(sourceDomains, targetDomains, self.Y.validValues)
        self.Y.testValues = scaleTo(sourceDomains, targetDomains, self.Y.testValues)
        return

# ==============================================================================
# Some functions

def scaleTo(sourceDomains, destinationDomains, data):
    scaledData = np.copy(data)
    for i in range(sourceDomains.shape[0]):
        scaledData[:,i] = destinationDomains[i,0] \
            + (scaledData[:,i] - sourceDomains[i,0]) \
            * (destinationDomains[i,1] - destinationDomains[i,0]) \
            / (sourceDomains[i,1] - sourceDomains[i,0])
    return scaledData

# ==============================================================================
if __name__ == "__main__":
    print("This module is not runnable")

# end of file
# ==============================================================================