# usage: python 3.x
# creation: 29-jun-2018 Pierre.CHEVAILLIER@b-com.com
# revision: 11-jul-2018 Pierre.CHEVAILLIER@b-com.com switch user-agent in data
# revision: 22-aug-2018 Pierre.CHEVAILLIER@b-com.com 
# todos:

import numpy

def pointToPointDistance(p1, p2):
    return numpy.sqrt((p2[0] - p1[0])**2 + (p2[1] - p1[1])**2 + (p2[2] - p1[2])**2)

def clampToPiMinusPi(angle):
    if angle > numpy.pi:
        angle -= 2 * numpy.pi
    elif angle < - numpy.pi:
        angle+= 2 * numpy.pi
    return angle

class AgentModelAdapter:
    def __init__(self):
        self.agentPosition = numpy.zeros(3)
        self.agentOrientation = numpy.zeros(3)
        self.targetPosition = numpy.zeros(3)
        self.targetOrientation = numpy.zeros(3)
        self.agentLinearVelocity = 0.0
        self.agentAngularVelocity = 0.0

    def initData(self, data):
        # not efficient, but easier to handle
        self.agentPosition = numpy.array(data[0:3])
        self.agentOrientation = numpy.array(data[3:6])
        self.targetPosition = numpy.array(data[6:9])
        self.targetOrientation = numpy.array(data[9:12])  

    def prepareInputData(self, data):
        self.initData(data)
        feature = [0,0]
        feature[0], feature[1] = self.targetRelativeLocation()
        return numpy.array([feature])

    def agentDirectionOfMove(self):
        return clampToPiMinusPi(numpy.pi / 2 - self.agentOrientation[1])

    def targetRelativeLocation(self):
        dist = pointToPointDistance(self.targetPosition, self.agentPosition)
        if dist > 0:
            tPosA = self.targetPosition - self.agentPosition
            azim = numpy.arctan2(tPosA[2], tPosA[0])
            aPsi = self.agentDirectionOfMove()
            theta = clampToPiMinusPi(aPsi - azim)
        else:
            theta = 0.0
        return dist, theta

def unitaryTests():
    adapter = AgentModelAdapter()

    testData = numpy.array([-1, -2, -3, -0.1, -0.2, -0.3, 1, 2, 3, 0.1, 0.2, 0.3])
    inputData = adapter.prepareInputData(testData)
    print("agent's Position: " + str(adapter.agentPosition))
    print("agent's Orientation: " + str(adapter.agentOrientation))
    print("target's Position: " + str(adapter.targetPosition))
    print("target's Orientation: " + str(adapter.targetOrientation))

    print(str(inputData))

if __name__ == "__main__":
    unitaryTests()

# End of File