from zlib import crc32
from struct import pack, unpack


class DataPacket:

	def __init__(self):
		self.length = 0
		self.seqNo = 0
		self.checksum = 0
		self.data = None
		self.isCorrupted = False

	def makePacket(self, data, seqNo):
		self.data = data
		self.length = len(self.data)
		self.seqNo = seqNo
		self.checksum = self.computeCRC()

	def recvPacket(self, packet):
		self.seqNo, self.length, self.checksum = unpack("=iiI", packet[0:12])
		self.data = packet[12:]
		if self.computeCRC() != self.checksum:
			# print("Data Corrupted")
			self.isCorrupted = True

	def makeBinary(self):
		seqNo = pack("=i", self.seqNo)
		length = pack("=i", self.length)
		checksum = pack("=I", self.checksum)
		segment = seqNo + length + checksum + self.data
		return segment

	def computeCRC(self):
		return crc32(pack("=i", self.seqNo) + pack("=i", self.length) + self.data)
