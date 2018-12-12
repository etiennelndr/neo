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
    xFrame     = pandas.read_csv(rawDataPath, usecols=[0], sep=';')
    yFrame     = pandas.read_csv(rawDataPath, usecols=[1], sep=';')
    vxFrame    = pandas.read_csv(rawDataPath, usecols=[2], sep=";")
    vyFrame    = pandas.read_csv(rawDataPath, usecols=[3], sep=';')
    #pitchFrame = pandas.read_csv(rawDataPath, usecols=[4], sep=';')
    #yawFrame   = pandas.read_csv(rawDataPath, usecols=[6], sep=';')

    x     = xFrame.values
    y     = yFrame.values
    vx    = vxFrame.values
    vy    = vyFrame.values
    #pitch = pitchFrame.values
    #yaw = yawFrame.values

    nRecords = x.shape[0]

    print('Number of Records: ' + str(nRecords))

    targetFile = open(learningDataPath, "w")

    xMax, xMin   = max(x), min(x)
    yMax, yMin   = max(y), min(y)
    vxMax, vxMin = max(vx), min(vx)
    vyMax, vyMin = max(vy), min(vy)
    # Maybe some changes
    for i in range(nRecords):
        # X
        _x = (x[i][0] - xMin) / (xMax - xMin)

        # Y
        _y = (y[i][0] - yMin) / (yMax - yMin)

        # Vx
        _vx = ((vx[i][0] - vxMin) / (vxMax - vxMin))*2 - 1

        # Vy
        _vy = ((vy[i][0] - vyMin) / (vyMax - vyMin))*2 - 1

        # For the moment we have nothing to transform
        targetFile.write(str(_x[0])
                + ";" + str(_y[0]) + ";" + str(_vx[0])
                + ";" + str(_vy[0]) #+ ";" + str(pitch[i][0])
                #+ ";" + str(yaw[i][0])
                + "\n")

    print(max(x), min(x))
    print(max(y), min(y))
    print(max(vx), min(vx))
    print(max(vy), min(vy))

    targetFile.close()
    return


if __name__ == "__main__":
    rawDataFilePath = sys.argv[1]
    learningDataPath = sys.argv[2]

    prepareLearningData(rawDataFilePath, learningDataPath)

# End of File
