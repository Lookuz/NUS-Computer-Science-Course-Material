import sys
import socket
import DataPacket
import AckPacket
from collections import deque
import threading

PACKET_SIZE = 960
META_SIZE = 24
WINDOW_SIZE = 512
ACK_PORT = 54999


class UDPReceiver:

	def __init__(self):
		self.ackFlag = -2
		self.seqSet = set()
		self.queue = deque()  # Queue for storing received packets
		self.packetBuffer = {}  # Buffer for storing out of order packets

	def run(self):

		sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, 65535)
		sock.bind(('', int(sys.argv[1])))

		while True:
			destFile, addr = sock.recvfrom(1000)
			packet = DataPacket.DataPacket()
			packet.recvPacket(destFile)
			if (not packet.isCorrupted) and (packet.seqNo == self.ackFlag):
				destFile = packet.data.decode()
				ackPacket = AckPacket.AckPacket()
				ackPacket.makePacket(self.ackFlag)
				sock.sendto(ackPacket.makeBinary(), addr)
				self.ackFlag = 0
				break

		sock.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, PACKET_SIZE * WINDOW_SIZE)
		self.parsePackets(sock, addr, destFile)

		print("Closing Socket")
		sock.close()

	def sendAck(self, sock, addr, ackNo):
		packet = AckPacket.AckPacket()
		packet.makePacket(ackNo)
		return sock.sendto(packet.makeBinary(), addr)

	def receivePackets(self, sock):
		while True:
			try:
				data, address = sock.recvfrom(PACKET_SIZE + META_SIZE)
			except socket.timeout:
				continue
			except OSError:
				break

			self.queue.appendleft(data)
			continue

	def parsePackets(self, sock, address, destFile):
		try:
			with open(destFile, 'wb') as outFile:
				threading.Thread(target=self.receivePackets, args=(sock,)).start()
				ackSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
				ackSock.bind(('', ACK_PORT))
				while True:

					if self.queue:
						data = self.queue.pop()
					else:
						continue

					packet = DataPacket.DataPacket()
					packet.recvPacket(data)

					if not packet.isCorrupted:
						if packet.seqNo == -1:
							break

						elif packet.seqNo == self.ackFlag:
							outFile.write(packet.data)
							# print("Received Packet {0}".format(packet.seqNo))

							# Joining buffered packets if any
							while self.ackFlag + 1 in self.packetBuffer:
								# print("Writing buffered packet {0}".format(self.ackFlag + 1))
								nextPacket = self.packetBuffer.get(self.ackFlag + 1)
								outFile.write(nextPacket.data)
								self.ackFlag += 1

							self.sendAck(ackSock, address, self.ackFlag)
							self.ackFlag += 1

						elif packet.seqNo < self.ackFlag:
							self.sendAck(ackSock, address, self.ackFlag - 1)

						# Buffering out of order packets
						elif packet.seqNo > self.ackFlag:
							self.packetBuffer[packet.seqNo] = packet

		except EOFError:
			print("Client disconnected")


if len(sys.argv) != 2:
	print(f"Usage: {sys.argv[0]} <port>")
	exit(-1)
else:
	udpReceiver = UDPReceiver()
	udpReceiver.run()
