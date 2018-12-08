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

        # Agent's orientation (direction of move in the horizontal plane)
        aPsi = clampToPiMinusPi(numpy.pi / 2 - aOri[i][0])
    
        # Position of the target, relative to the agent's position (in the horizontal plane)
        d = pointToPointDistance(tPos[i], aPos[i])
        if d > 0:
            tPosA = tPos[i] - aPos[i]
            azim = numpy.arctan2(tPosA[2], tPosA[0])
            theta = clampToPiMinusPi(aPsi - azim)
        else:
            theta = 0.0

        # estimated velocities of the agent
        if i > 0:
            deltaT = time[i][0] - time[i-1][0]
            aLinearVelocity = pointToPointDistance(aPos[i], aPos[i-1]) / deltaT
  
            aPsi1, aPsi2 = aOri[i-1][0], aOri[i][0]
            dPsi = clampToPiMinusPi(aPsi2 - aPsi1)
            aAngularVelocity = dPsi / deltaT
            #print("Vang: " + str(aAngularVelocity) + ": " + str(aPsi2) + " - " + str(aPsi2) + " = " + str(dPsi) + " / " + str(deltaT))
        
            targetFile.write(str(time[i][0]) 
                + ";" + str(d) + ";" + str(theta) 
                + ";" + str(aLinearVelocity) + ";" + str(aAngularVelocity) 
                + "\r\n")
        
    targetFile.close()
    return
    #
if __name__ == "__main__":
    rawDataFilePath = sys.argv[1]
    learningDataPath = sys.argv[2]

    prepareLearningData(rawDataFilePath, learningDataPath)

# End of File