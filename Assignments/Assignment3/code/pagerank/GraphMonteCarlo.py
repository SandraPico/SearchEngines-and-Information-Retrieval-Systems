#!/usr/bin/env python
import pandas as pd
import matplotlib.pyplot as plt

def Main():

	#Each of the graphs:
	df1 = pd.read_csv('1.txt', header=None)
	plt.figure(figsize=(10,6))
	plt.xlabel('N')
	plt.ylabel('Difference')
	plt.title('Task 2.8 - MonteCarlo method 1')
	plt.plot(df1, label='MonteCarlo 1')
	plt.legend()
	plt.show()

	#Method 2:
	df2 = pd.read_csv('2.txt', header=None)
	plt.figure(figsize=(10,6))
	plt.xlabel('N')
	plt.ylabel('Difference')
	plt.title('Task 2.8 - MonteCarlo method 2')
	plt.plot(df2, label='MonteCarlo 2')
	plt.legend()
	plt.show()

	#Method 4:
	df4 = pd.read_csv('4.txt', header=None)
	plt.figure(figsize=(10,6))
	plt.xlabel('N')
	plt.ylabel('Difference')
	plt.title('Task 2.8 - MonteCarlo method 4')
	plt.plot(df4, label='MonteCarlo 4')
	plt.legend()
	plt.show()

	#Method 5:
	df5 = pd.read_csv('5.txt', header=None)
	plt.figure(figsize=(10,6))
	plt.xlabel('N')
	plt.ylabel('Difference')
	plt.title('Task 2.8 - MonteCarlo method 5')
	plt.plot(df5, label='MonteCarlo 5')
	plt.legend()
	plt.show()


	#Plto all of them together.
	df1 = pd.read_csv('1.txt', header=None)
	df2 = pd.read_csv('2.txt', header=None)
	df4 = pd.read_csv('4.txt', header=None)
	df5 = pd.read_csv('5.txt', header=None)
	plt.figure(figsize=(10,6))
	plt.xlabel('N')
	plt.ylabel('Difference')
	plt.title('Task 2.8 - MonteCarlo')
	plt.legend()
	plt.plot(df1, label='MonteCarlo 1')
	plt.plot(df2, label='MonteCarlo 2')
	plt.plot(df4, label='MonteCarlo 4')
	plt.plot(df5, label='MonteCarlo 5')
	plt.legend()
	plt.show()

if __name__ == '__main__':
    Main()
