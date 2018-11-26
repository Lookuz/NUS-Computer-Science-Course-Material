#
import sys
import os
import time
import datetime
import locale
import select
from socket import *
import threading


class WebServer:
	def start(self, port):
		print(f"Starting server on port {port}")
		## NEEDS IMPLEMENTATION
		## You have to understand how sockets work and how to program them.
		## A good starting point is the socket tutorial from the PyDocs
		## https://docs.python.org/3.7/howto/sockets.html
		##
		## Hints
		## 1. You should set up the socket(s) and then call handleClientSocket
		# locale.setlocale(locale.LC_TIME, 'en')
		welcomeSocket = socket(AF_INET, SOCK_STREAM)
		welcomeSocket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
		welcomeSocket.bind(('', port))
		welcomeSocket.listen(10)
		clientThreads = []

		while True:
			readers, writers, executors = select.select([welcomeSocket], [], [])

			for r in readers:
				if r == welcomeSocket:
					connectionSocket, address = welcomeSocket.accept()

					newThread = ClientThread(connectionSocket)
					newThread.start()
					clientThreads.append(newThread)

		for t in clientThreads:
			t.join()


class HttpRequest:
	def __init__(self):
		self.fields = {}
		self.isPersistent = False
		self.method = None
		self.requestUrl = None
		self.lastModified = None

	## NEEDS IMPLEMENTATION
	def parseRequest(self, request):
		fields = request.splitlines()
		requestLine = fields[0].split()

		self.method = requestLine[0].split("\r")

		if len(requestLine) > 1:
			self.requestUrl = requestLine[1][1:]
			self.isPersistent = True if (requestLine[2][:8] == "HTTP/1.1") else False

		if len(fields) >= 5:
			self.lastModified = fields[4][19:]


class ClientThread(threading.Thread):
	def __init__(self, connectionSocket):
		threading.Thread.__init__(self)
		self.connectionSocket = connectionSocket
		self.offset = 28800

	def run(self):
		self.handleClientSocket(self.connectionSocket)

	def handleClientSocket(self, client):
		"""
		Handles requests sent by client
		:param client: Socket that handles the client connection
		"""
		## NEEDS IMPLEMENTATION
		## This function is supposed to handle the request
		## (1) Read the request from the socket
		## (2) Parse the request headers to a HttpRequest class
		## (3) Form a response using formHttpResponse.
		## (4) Send a response using sendHttpResponse.
		with client:
			while True:
				try:
					inputLine = ""
					while True:
						request = client.recv(4096)
						inputLine += request.decode()
						if inputLine[-4:] == "\r\n\r\n":
							break

					inputs = inputLine.split("\r\n\r\n")
					inputs.pop()

					# print(inputLine)
					for req in inputs:
						httpRequest = HttpRequest()
						httpRequest.parseRequest(req)
						data = self.formHttpResponse(httpRequest)
						self.sendHttpResponse(client, data)

					if httpRequest.isPersistent:
						client.setsockopt(SOL_SOCKET, SO_KEEPALIVE, 1)
						client.settimeout(2.0)
						continue
					else:
						break
				except (timeout, ConnectionResetError):
					break

		client.close()

	def sendHttpResponse(self, client, response):
		"""
		Sends a response back to the client
		:param client: Socket that handles the client connection
		:param response: the response that should be send to the client
		"""
		# NEEDS IMPLEMENTATION
		client.sendall(response)

	def formHttpResponse(self, request):
		"""
		Form a response to an HttpRequest
		:param request: the HTTP request
		:return: a byte[] that contains the data that should be send to the client
		"""
		##  NEEDS IMPLEMENTATION
		##  Make sure you follow the (modified) HTTP specification
		##  in the assignment regarding header fields and newlines
		data = None
		response = ""
		statusCode = ""
		lastModified = None
		notModified = False

		# Response Line
		response += "HTTP/1.1 " if request.isPersistent else "HTTP/1.0 "

		try:
			with open(request.requestUrl, "rb") as filePath:
				data = filePath.read()
				lastModified = "Last-Modified: " + time.strftime('%a, %d %b %Y %H:%M:%S GMT', time.localtime(os.path.getmtime(request.requestUrl) - self.offset))

				if (request.lastModified is not None) and (self.compareDateStrings(lastModified[15:], request.lastModified)):
					## request.lastModified >= lastModified[15:]
					statusCode = "304 Not Modified"
					notModified = True
				else:
					statusCode = "200 OK"

		except (IOError, FileNotFoundError, TypeError):
			statusCode = "404 Not Found"
			data = self.form404Response(request.requestUrl)

		# Header Line
		response += statusCode + "\r\n"
		response += "Date: " + datetime.datetime.utcnow().strftime('%a, %d %b %Y %H:%M:%S GMT') + "\r\n"
		if lastModified is not None:
			response += lastModified + "\r\n"
		if not notModified:
			response += "Content-Length: " + str(len(data)) + "\r\n"

		# CRLF
		response += "\r\n"

		# Body
		responseBytes = str.encode(response)
		if not notModified:
			responseBytes += data

		return responseBytes

	def form404Response(self, filePath):
		header = "<head><title>404 Not Found</title></head>"
		body = "<body><p>The requested URL <i>" + "" if (
				filePath is None) else filePath + "</i> could not be found<p><body>"
		data = "<html>" + header + body + "</html>"

		return str.encode(data)

	def compareDateStrings(self, dateStr1, dateStr2):
		date1 = datetime.datetime.strptime(dateStr1, '%a, %d %b %Y %H:%M:%S GMT')
		date2 = datetime.datetime.strptime(dateStr2, '%a, %d %b %Y %H:%M:%S GMT')

		return date1 <= date2

if __name__ == "__main__":
	try:
		port = int(sys.argv[1])
	except:
		print("Usage: python WebServer.py <port>")
		exit(1)

	server = WebServer()
	server.start(port)
