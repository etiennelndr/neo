# usage: python computeRelativePosition.py <cartesian_positions_file.csv
# tested on: python 3.6 on MacOS
# creation: 29-jun-2018 Pierre.CHEVAILLIER@b-com.com
# revision: 11-jul-2018 Pierre.CHEVAILLIER@b-com.com switch user-agent in data
# revision: 22-aug-2018 Pierre.CHEVAILLIER@b-com.com
# revision: 29-aug-2018 Pierre.CHEVAILLIER@b-com.comn renamed, callable function, use of agentModelAdapter

# todos:

#Mrevision : MJ ; adapt for the bot
import sys
import numpy
import pandas

from agentModelAdapter import clampToPiMinusPi
from agentModelAdapter import pointToPointDistance

def prepareLearningData(rawDataPath, learningDataPath):
    print("Load the data set (raw formatting) from " + rawDataPath)
    #changed
    stateFrame = pandas.read_csv(rawDataPath, usecols=[0], sep=';')
    lifeFrame = pandas.read_csv(rawDataPath, usecols=[1], sep=';')
    ennemyKilledFrame = pandas.read_csv(rawDataPath, usecols=[3], sep=";")
    ennemyLifeFrame = pandas.read_csv(rawDataPath, usecols=[6], sep=';')
    distanceEnnemyFrame = pandas.read_csv(rawDataPath,usecols=[7],sep';')
    walkBeforeFrame = pandas.read_csv(rawDataPath,usecols=[8],sep';')

    state = stateFrame.values
    life = lifeFrame.values
    ennemyKilled = ennemyLifeFrame.values
    ennemyLife = ennemyLifeFrame.values

    nRecords = state.shape[0]

    print('Number of Records: ' + str(nRecords))

    targetFile = open(learningDataPath, "w")

    #aLinearVelocity = 0.0
    #aAngularVelocity = 0.0

    #maybe some changes
    for i in range(nRecords):

        # For the moment we have nothing to transform
        targetFile.write(str(x[i][0])
                + ";" + str(y[i][0]) + ";" + str(vx[i][0])
                + ";" + str(vy[i][0]) + ";" + str(pitch[i][0])
                + ";" + str(yaw[i][0]) + "\n")

    targetFile.close()
    return
    #
if __name__ == "__main__":
    rawDataFilePath = sys.argv[1]
    learningDataPath = sys.argv[2]

    prepareLearningData(rawDataFilePath, learningDataPath)

# End of File
