import time


class Timer:
	TIMER_STOP = -1

	def __init__(self, interval):
		self.startTime = self.TIMER_STOP
		self.interval = interval

	def start(self):
		if self.startTime == self.TIMER_STOP:
			self.startTime = time.time()

	def stop(self):
		if self.startTime != self.TIMER_STOP:
			self.startTime = self.TIMER_STOP

	def isRunning(self):
		return self.startTime != self.TIMER_STOP

	def isTimeout(self):
		return False if (not self.isRunning()) else (time.time() - self.startTime >= self.interval)
