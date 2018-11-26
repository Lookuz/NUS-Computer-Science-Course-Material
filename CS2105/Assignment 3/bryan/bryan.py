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
	exit("Usage: python bryan.py <port>")
else:
	port = sys.argv[1] 

# read signature from file
with open("bryan-python.sig", "rb") as f:
	signature = pickle.loads(f.read())
print("Berisign's signature loaded.")

# read private key from file
with open("bryan-python.pri", "r") as f:
	private_key = RSA.importKey(f.read())
	public_key = private_key.publickey()
print("Private key loaded. Waiting for connection.")

# create socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock.bind(('', int(port)))
sock.listen(1)

conn, addr = sock.accept()

# sends public key and signature
pub = public_key.exportKey()
conn.send(pickle.dumps(pub))
conn.send(pickle.dumps(signature))
print("Public key and signature sent.")

# reads session password
key = pickle.loads(conn.recv(1024))

#decrypt the password
rsa = PKCS1_OAEP.new(private_key)
key = rsa.decrypt(key)

# create an AES key using the password
cipher = AESCipher(key)
print("Session key obtained.")

# opens the text file for reading
with open("docs.txt", "r", newline='') as f:
	for line in f:
		# encrypt the line
		enc = cipher.encrypt(line)
		# pickle and send it
		conn.send(pickle.dumps(enc))

conn.close()
print("Document send.")
