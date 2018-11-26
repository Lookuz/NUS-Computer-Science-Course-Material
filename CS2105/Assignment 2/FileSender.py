import sys
import socket
import DataPacket
import AckPacket
import threading
from Timer import Timer
import time

TIME_OUT = 0.0015
PACKET_SIZE = 960
WINDOW_SIZE = 512
INTERVAL = 0.0015
mutex = threading.Lock()  # Lock for sharing across threads
timer = Timer(TIME_OUT)  # Global timer for resending packets


class UDPSender:

	def __init__(self):
		self.sendBase = -2
		self.endFlag = False
		self.lastSent = -2
		self.packetBuffer = {}

	def run(self):
		(_, host, port, sourceFile, destFile) = sys.argv
		address = (host, int(port))

		# Create a socket
		sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, PACKET_SIZE * WINDOW_SIZE)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, 65535)

		# Send destination file name reliably
		while True:
			self.sendData(sock, address, str.encode(destFile))

			sock.settimeout(0.1)
			try:
				ack, address = sock.recvfrom(100)
			except socket.timeout:
				# print("Timeout, resending destFile")
				continue

			ackPacket = AckPacket.AckPacket()
			ackPacket.recvPacket(ack)
			if not ackPacket.isCorrupted:
				self.sendBase = 0
				self.lastSent = 0
				break

		sendThread = threading.Thread(target=self.sendSource, args=(sock, address, sourceFile))
		ackThread = threading.Thread(target=self.listenOnAcks, args=(sock, ))

		sock.settimeout(None)
		sendThread.start()
		ackThread.start()

	def finish(self, sock, address):
		data = b"Finished"
		packet = DataPacket.DataPacket()
		packet.makePacket(data, -1)
		sock.sendto(packet.makeBinary(), address)
		self.endFlag = True

	def sendData(self, sock, address, data):
		packet = DataPacket.DataPacket()
		packet.makePacket(data, self.lastSent)
		sock.sendto(packet.makeBinary(), address)
		if self.lastSent >= 0:
			self.packetBuffer[self.lastSent] = packet

	# Function to receive acks
	def listenOnAcks(self, sock):

		while True:
			try:
				ack, address = sock.recvfrom(1024)
			except socket.timeout:
				continue

			ackPacket = AckPacket.AckPacket()
			ackPacket.recvPacket(ack)

			if not ackPacket.isCorrupted:
				if ackPacket.ackNo >= self.sendBase:
					mutex.acquire()
					# print("Received Ack {0}".format(ackPacket.ackNo))
					# self.sendBase = ackPacket.ackNo + PACKET_SIZE
					for i in range(self.sendBase, ackPacket.ackNo + 1):
						if i in self.packetBuffer:
							self.packetBuffer.pop(i)
					self.sendBase = ackPacket.ackNo + 1
					timer.stop()
					mutex.release()

			if self.endFlag:
				sock.close()
				break

	# Function to send data packets
	def sendSource(self, sock, address, filePath):

		inFile = open(filePath, 'rb')
		finReading = False

		while True:
			try:
				if finReading and not self.packetBuffer:
					break

				mutex.acquire()
				while self.lastSent < self.sendBase + WINDOW_SIZE and not finReading:

					data = inFile.read(PACKET_SIZE)
					if not data:
						finReading = True
						break

					# print("Sending Packet {0}".format(self.lastSent))
					self.sendData(sock, address, data)
					self.lastSent += 1

				if not timer.isRunning():
					timer.start()

				while timer.isRunning() and not timer.isTimeout():
					mutex.release()
					time.sleep(INTERVAL)
					mutex.acquire()

				if timer.isTimeout():
					timer.stop()
					# print("Timeout, resending")
					self.resendPackets(sock, address)
					mutex.release()
					continue

				mutex.release()

			except socket.timeout:
				continue

		inFile.close()
		self.finish(sock, address)

	def resendPackets(self, sock, address):
		for i in range(self.sendBase, self.lastSent + 1):
			if i in self.packetBuffer:
				# print("Resending Packet {0}".format(i))
				packet = self.packetBuffer.get(i)
				sock.sendto(packet.makeBinary(), address)


if len(sys.argv) != 5:
	print(f"Usage: {sys.argv[0]} <host> <port> <source_file> <destination_file>")
	exit(-1)
else:
	udpSender = UDPSender()
	udpSender.run()
