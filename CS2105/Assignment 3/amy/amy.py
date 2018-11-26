import socket
import sys
import pickle
import base64
from Crypto.Cipher import AES
from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_PSS
from Crypto.Hash import MD5
from Crypto import Random

from AESCipher import AESCipher


class Amy:
	def __init__(self):
		self.publicKey = None
		self.aesKey = Random.get_random_bytes(32)
		self.cipher = AESCipher(self.aesKey)

	def run(self, addr):
		connSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		connSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		connSock.connect(addr)
		if self.verifySignature(connSock):
			self.sendKey(connSock)
			self.receiveMessage(connSock)

		connSock.close()

	def verifySignature(self, connSock):
		pubKey = pickle.loads(connSock.recv(1024))
		signature = pickle.loads(connSock.recv(1024))
		with open('berisign-python.pub', 'r') as inFile:
			berisignKey = RSA.importKey(inFile.read())
			signer = PKCS1_PSS.new(berisignKey)

			md5 = MD5.new()
			md5.update('bryan'.encode(encoding='ASCII'))
			md5.update(pubKey)

			try:
				signer.verify(md5, signature)
			except ValueError:
				print("Message hash does not match")
				return False

		self.publicKey = RSA.importKey(pubKey)
		return True

	def receiveMessage(self, connSock):
		buffer = []

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


if len(sys.argv) != 3:
	exit("Usage: python alice.py <addr> <port>")
else:
	name, ip, port = sys.argv
	amy = Amy()
	amy.run(('', int(port)))
