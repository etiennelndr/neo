# ==============================================================================
# context: teaching - Shared Educational Resources in Computer Science
#          ENIB: module IAS - course on neural networks (since fall'18)
# description: class for setting the learning parameters
# copyright (c) 2018 ENIB. All rights reserved.
# ------------------------------------------------------------------------------
# usages: from <thisFileName> import ...
# dependencies: python 3 (see import statements)
# tested on: python 3.6.5 on MacOS 10.13
#            python 3.x on
# ------------------------------------------------------------------------------
# creation: 07-sep-2018 pierre.chevaillier@enib.fr
# revision: 20-sep-2018 pierre.chevaillier@enib.fr code cleaning
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
class LearningExperiment:
    verbose = 0

    def __init__(self):
        self.name = 'learning'
        self.timeStamp = datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S")
        self.modelNamePrefix = ''
        self.modelName = '' # computed by defineModelName
        self.resultsDirPath = ''
        self.scaledX = False
        self.scaledY = False
        self.learningData = None
        self.model = None

        # Specific to neural networks
        self.layers = None
        self.hiddenLayersActivationFunction = 'sigmoid'
        self.outputLayerActivationFunction = 'linear'
        self.dropout = False
        self.lossFunction = 'mse'
        self.optimizer = 'adam'
        self.batchSize = 32
        self.nMaxEpochs = 10
        self.history = None
        return
    
    def copy(self, origin):
        self.modelNamePrefix = origin.modelNamePrefix
        self.modelName = origin.modelName
        self.resultsDirPath = origin.resultsDirPath
        self.scaledX = origin.scaledX
        self.scaledY = origin.scaledY
        self.learningData = origin.learningData
        self.model = origin.model

        self.layers = origin.layers
        self.hiddenLayersActivationFunction = origin.hiddenLayersActivationFunction
        self.outputLayerActivationFunction = origin.outputLayerActivationFunction
        self.dropout = origin.dropout
        self.lossFunction = origin.lossFunction
        self.optimizer = origin.optimizer
        self.batchSize =  origin.batchSize
        self.nMaxEpochs = origin.nMaxEpochs
        self.history = origin.history
        return

    def defineModelName(self):
        name = self.modelNamePrefix + '_'
        nLayers = self.layers.shape[0]
        for i in range(0, nLayers):
            name += str(self.layers[i])
            if i < nLayers - 1:
                name += 'x'
        self.modelName = name
        return

    def defineScaling(self):
        pass

    def scaleLearningData(self):
        if self.scaledX:
            self.learningData.X.isScaled = self.learningData.X.convertFromRawToScaled()
        if self.scaledY:
            self.learningData.Y.isScaled = self.learningData.Y.convertFromRawToScaled()
        return

    def predictionsFilePath(self):
        if len(self.modelName) < 1:
            self.defineModelName()
        filePath = self.resultsDirPath + os.path.sep + self.modelName + '_predictions_plot_' + self.timeStamp + '.data'
        return filePath

# ==============================================================================
if __name__ == "__main__":
    print("This module is not runnable")

# end of file
# ==============================================================================