## Alice knows Bob's public key
## Alice sends Bob session (AES) key
## Alice receives messages from Bob, decrypts and saves them to file

import socket
import sys
import pickle
from Crypto.Cipher import AES
from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from Crypto import Random

from AESCipher import AESCipher


class Alice:
	def __init__(self):
		with open('bob-python.pub', 'r') as inFile:
			self.publicKey = RSA.importKey(inFile.read())
		self.aesKey = Random.get_random_bytes(32)
		self.cipher = AESCipher(self.aesKey)

	def run(self, addr):
		connSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		connSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		connSock.connect(addr)
		self.sendKey(connSock)
		self.receiveMessage(connSock)
		connSock.close()

	def receiveMessage(self, connSock):
		buffer = []
		# while len(buffer) < 10:
		# 	buffer.append(connSock.recv(1024))

		while True:
			data = connSock.recv(1024)
			if not data:
				break
			buffer.append(data)

		with open('msgs.txt', 'wb') as outFile:
			for segment in buffer:
				decMessage = self.decryptMessage(segment)
				outFile.write(decMessage)

	def decryptMessage(self, message):
		message = pickle.loads(message)
		return self.cipher.decrypt(message)

	def sendKey(self, connSock):
		rsaCipher = PKCS1_OAEP.new(self.publicKey)
		sharedKey = rsaCipher.encrypt(self.aesKey)
		connSock.send(pickle.dumps(sharedKey))


# Check if the number of command line argument is 2
if len(sys.argv) != 3:
	exit("Usage: python alice.py <addr> <port>")
else:
	name, ip, port = sys.argv
	alice = Alice()
	alice.run(('', int(port)))
