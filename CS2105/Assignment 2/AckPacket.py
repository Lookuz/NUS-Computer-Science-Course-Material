from zlib import crc32
from struct import pack, unpack


class AckPacket:

	def __init__(self):
		self.ackNo = 0
		self.checksum = 0
		self.isCorrupted = False

	def makePacket(self, ackNo):
		self.ackNo = ackNo
		self.checksum = self.computeCRC()

	def recvPacket(self, packet):
		self.ackNo, self.checksum = unpack("iI", packet[0:8])
		if self.computeCRC() != self.checksum:
			# print("Ack Corrupted")
			self.isCorrupted = True

	def makeBinary(self):
		ackNo = pack("i", self.ackNo)
		checksum = pack("I", self.checksum)
		segment = ackNo + checksum
		return segment

	def computeCRC(self):
		return crc32(pack('i', self.ackNo))
