import socket
import sys
import pickle
import base64
from Crypto.Cipher import AES
from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from Crypto import Random

from AESCipher import AESCipher


if len(sys.argv) != 2:
	exit("Usage: python bob.py <port>")
else:
	port = sys.argv[1] 

# read private key from file
with open("bob-python.pri", "r") as f:
	private_key = RSA.importKey(f.read())
print("Private key loaded. Waiting for connection.")

# create socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock.bind(('', int(port)))
sock.listen(1)

conn, addr = sock.accept()

# reads session password
key = pickle.loads(conn.recv(1024))

#decrypt the password
rsa = PKCS1_OAEP.new(private_key)
key = rsa.decrypt(key)

# create an AES key using the password
cipher = AESCipher(key)

# opens the text file for reading
with open("docs.txt", "r", newline="\r\n") as f:
	for line in f:
		# encrypt the line
		enc = cipher.encrypt(line)
		# pickle and send it
		conn.send(pickle.dumps(enc))

conn.close()
