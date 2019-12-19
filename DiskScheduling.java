package Assignment6;
/**
 * Prechar Xiong
 * 11/21/19
 * ICS 462-01
 * Assignment 6
 * 
 * This program creates a DiskScheduling class that implements the FCFS,
 * SSTF,SCAN,C-SCAN,LOOK, and C-LOOK disk scheduling algorithms on a disk, with
 * the disk information given from a file.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DiskScheduling {
	
	/**
	 * This method reads the file and assigns each line of the file as data to
	 * be used as a disk and entered into the disk scheduling algorithms to determine
	 * the total head movement.
	 */
	public static void readFile() {
		createFile();
		String fileName = "Asg6 Data.txt";
		File file = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String string;
			ArrayList<String> stringArrayList = new ArrayList<String>();
			
			while((string = br.readLine()) != null) {
				//System.out.println(string);
				stringArrayList.add(string);
			}
			
			int diskCylinders = 0;
			int startCylinder = 0;
			ArrayList<Integer> cylinders;
			
			for(int i = 0; i < stringArrayList.size(); i++) {
				if(i%3 == 0) {
					diskCylinders = parseInt(stringArrayList.get(i));
				} else if(i%3 == 1) {
					startCylinder = parseInt(stringArrayList.get(i));
				} else {
					cylinders = parseIntArray(stringArrayList.get(i));
					FCFS(diskCylinders,startCylinder,cylinders);
					SSTF(diskCylinders,startCylinder,cylinders);
					SCAN(diskCylinders,startCylinder,cylinders);
					C_SCAN(diskCylinders,startCylinder,cylinders);
					LOOK(diskCylinders,startCylinder,cylinders);
					C_LOOK(diskCylinders,startCylinder,cylinders);
					System.out.println("\n");
					printToFile("\n");
				}
			}
			
			
		} catch (IOException e) {
			System.out.println("The file "+ fileName + " was not found");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * this method converts a string into an integer
	 * @param string
	 * @return string as int
	 */
	public static int parseInt(String string) {
		return Integer.parseInt(string);
	}
	
	/**
	 * this method converts a string into an ArrayList<Integer>
	 * @param string
	 * @return ArrayList<Integer>
	 */
	public static ArrayList<Integer> parseIntArray(String string) {
		String[] stringArray = string.split(" ");
		ArrayList<Integer> intArrayList = new ArrayList<Integer>();
		for(int i = 0; i < stringArray.length; i++) {
			intArrayList.add(Integer.parseInt(stringArray[i]));
		}
		return intArrayList;
	}
	
	/**
	 * this method returns the absolute value of a number
	 * @param number
	 * @return absolute value of a number
	 */
	public static int abs(int number) {
		if(number < 0) {
			return number *= -1;
		} else {
			return number;
		}
	}
	
	/**
	 * This method implements the FCFS disk scheduling algorithm.
	 * From the starting cylinder move to the next cylinder in the cylinders list
	 * until there are no cylinders left, then print the total head movement.
	 * @param diskCylinders, the number of cylinders in the disk
	 * @param startCylinder, the cylinder where the disk starts from
	 * @param cylinders, the cylinders that have I/O requests
	 */
	public static void FCFS(int diskCylinders, int startCylinder, ArrayList<Integer> cylinders) {
		int sum = 0;
		for(int i = 0; i < cylinders.size()-1; i++) {
			if(i == 0) {
				sum += abs(startCylinder - cylinders.get(i));
				//System.out.println(startCylinder + " - " + cylinders.get(i) + " = " + abs(startCylinder - cylinders.get(i)));
			}
			sum += abs(cylinders.get(i)-cylinders.get(i+1));
			//System.out.println(cylinders.get(i) + " - " + cylinders.get(i+1) + " = " + abs(cylinders.get(i)-cylinders.get(i+1)));
	
		}
		System.out.println("For FCFS, the total head movement was " + sum + " cylinders.\n");
		printToFile("For FCFS, the total head movement was " + sum + " cylinders.\n");
	}
	
	/**
	 * This method is a helper method for the SSTF scheduling algorithm to ensure 
	 * that the algorithm always travels to the cylinder with the shortest seek time
	 * that hasn't already been visited.
	 * @param Cylinder, the current cylinder the disk is on
	 * @param cylinders, the cylinders that have I/O requests
	 * @param usedCylinders, the cylinders that have been visited
	 * @return cylinder with the shortest seek time that has not been visited
	 */
	public static int shortestSeekTime(int Cylinder, ArrayList<Integer> cylinders, ArrayList<Integer> usedCylinders) {
		int min = Integer.MAX_VALUE;
		int num = 0;
		for(int i = 0; i < cylinders.size(); i++) {
			if(abs(Cylinder-cylinders.get(i)) < min && !usedCylinders.contains(cylinders.get(i))) {
				min = abs(Cylinder-cylinders.get(i));
				num = cylinders.get(i);
			}
		}
		return num;
	}
	
	/**
	 * This method implements the SSTF disk scheduling algorithm, it uses the shortestSeekTime 
	 * method to help ensure that from the current cylinder, the disk will travel to the 
	 * cylinder with the shortest seek time that has not already been visited.
	 * @param diskCylinders, the number of cylinders in the disk
	 * @param startCylinder, the cylinder where the disk starts
	 * @param cylinders, the list of cylinders that have I/O requests
	 */
	public static void SSTF(int diskCylinders, int startCylinder, ArrayList<Integer> cylinders) {
		int sum = 0;
		int shortestSeek = 0;
		ArrayList<Integer> usedCylinders = new ArrayList<Integer>();
		for(int i = 0; i < cylinders.size()-1; i++) {
			if(i == 0) {
				shortestSeek = shortestSeekTime(startCylinder,cylinders,usedCylinders);
				usedCylinders.add(shortestSeek);
				sum += abs(startCylinder - shortestSeek);
				//System.out.println(startCylinder + " - " + shortestSeek + " = " + abs(startCylinder - shortestSeek));
			}
			shortestSeek = shortestSeekTime(usedCylinders.get(i), cylinders, usedCylinders);
			sum += abs(usedCylinders.get(i) - shortestSeek);
			usedCylinders.add(shortestSeek);
			//System.out.println(usedCylinders.get(i) + " - " + shortestSeek + " = " + abs(usedCylinders.get(i) - shortestSeek));
		}
		System.out.println("For SSTF, the total head movement was " + sum + " cylinders.\n");
		printToFile("For SSTF, the total head movement was " + sum + " cylinders.\n");
	}
	
	/**
	 * This method implements the SCAN disk scheduling algorithm for towards
	 * the final cylinder, then for towards the first cylinder. For SCAN towards the
	 * final cylinder, the final cylinder and the starting cylinder are added to the 
	 * cylinders list if it is not already in there. The cylinders list is then sorted 
	 * in numerical order. From the start cylinder travel to the next cylinder towards the final cylinder
	 * calculating the total head movement each time. Once you reach the final cylinder, 
	 * travel to the closest cylinder and travel towards the first cylinder. 
	 * Once you reach the first cylinder terminate the loop and return the total head movement. 
	 * Vice Versa for towards the first cylinder. 
	 * @param diskCylinders, the number of cylinders in the disk
	 * @param startCylinder, the cylinder where the disk starts
	 * @param cylinders, the list of cylinders that have I/O requests
	 */
	public static void SCAN(int diskCylinders, int startCylinder, ArrayList<Integer> cylinders) {
		int sum = 0;
		boolean lastCylinderAdded = false;
		ArrayList<Integer> newCylinders = new ArrayList<Integer>(cylinders);
		if(!newCylinders.contains(startCylinder)) {
			newCylinders.add(startCylinder);
		}
		if(!newCylinders.contains(diskCylinders-1)) {
			newCylinders.add(diskCylinders-1);
			lastCylinderAdded = true;
		}
		Collections.sort(newCylinders);
		int counter = newCylinders.indexOf(startCylinder);
		while(counter != 0) {
			if(counter == newCylinders.size()-1) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(newCylinders.indexOf(startCylinder)-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)-1)));
				counter = newCylinders.indexOf(startCylinder)-1;
			} else if(counter < newCylinders.indexOf(startCylinder)) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter-1)));
				counter--;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter+1)));
				counter++;
			}
		}
		
		System.out.println("For SCAN (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For SCAN (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		
		sum = 0;
		counter = newCylinders.indexOf(startCylinder);
		
		if(lastCylinderAdded) {
			newCylinders.remove((Integer) (diskCylinders-1));
		}
		
		if(!newCylinders.contains(0)) {
			newCylinders.add(0);
		}
		
		Collections.sort(newCylinders);
		
		sum = 0;
		counter = newCylinders.indexOf(startCylinder);
		
		while(counter != newCylinders.size()-1) {
			if(counter == 0) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(newCylinders.indexOf(startCylinder)+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)+1)));
				counter = newCylinders.indexOf(startCylinder)+1;
			} else if(counter > newCylinders.indexOf(startCylinder)) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter+1)));
				counter++;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter-1)));
				counter--;
			}
		}
		
		System.out.println("For SCAN (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For SCAN (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
	}
	
	/**
	 * This method implements the C-SCAN disk scheduling algorithm for towards
	 * the final cylinder, then for towards the first cylinder. For C-SCAN towards the
	 * final cylinder, the final cylinder, the first cylinder and the starting cylinder 
	 * are added to the cylinders list if it is not already in there. The cylinders list 
	 * is then sorted in numerical order. From the start cylinder travel to the next 
	 * cylinder towards the final cylinder calculating the total head movement each time. 
	 * Once you reach the final cylinder, travel to the first cylinder and travel towards 
	 * the starting cylinder. Once you reach the cylinder before the starting cylinder 
	 * terminate the loop and return the total head movement. Vice Versa for towards the first cylinder. 
	 * @param diskCylinders, the number of cylinders in the disk
	 * @param startCylinder, the cylinder where the disk starts
	 * @param cylinders, the list of cylinders that have I/O requests
	 */
	public static void C_SCAN(int diskCylinders, int startCylinder, ArrayList<Integer> cylinders){
		int sum = 0;
		ArrayList<Integer> newCylinders = new ArrayList<Integer>(cylinders);
		if(!newCylinders.contains(startCylinder)) {
			newCylinders.add(startCylinder);
		}
		if(!newCylinders.contains(0)) {
			newCylinders.add(0);
		}
		if(!newCylinders.contains(diskCylinders-1)) {
			newCylinders.add(diskCylinders-1);
		}
		Collections.sort(newCylinders);
		int counter = newCylinders.indexOf(startCylinder);
		while(counter != newCylinders.indexOf(startCylinder)-1) {
			if(counter == newCylinders.size()-1) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(0));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(0) + " = " + abs(newCylinders.get(counter) - newCylinders.get(0)));
				counter = 0;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter+1)));
				counter++;
			}
		}
		
		System.out.println("For C-SCAN (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For C-SCAN (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		
		sum = 0;
		counter = newCylinders.indexOf(startCylinder);
		
		while(counter != newCylinders.indexOf(startCylinder)+1) {
			if(counter == 0) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(newCylinders.size()-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(newCylinders.size()-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(newCylinders.size()-1)));
				counter = newCylinders.size()-1;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter-1)));
				counter--;
			}
		}
		
		System.out.println("For C-SCAN (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For C-SCAN (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
	}
	
	/**
	 * This method implements the LOOK disk scheduling algorithm for towards
	 * the final cylinder, then for towards the first cylinder. For LOOK towards the
	 * final cylinder, the starting cylinder is added to the cylinders list if it is 
	 * not already in there. The cylinders list is then sorted in numerical order. 
	 * From the start cylinder travel to the next cylinder towards the final cylinder 
	 * calculating the total head movement each time. Once you reach the final cylinder, 
	 * travel to the closest cylinder and travel towards the first cylinder. Once you 
	 * reach the first cylinder terminate the loop and return the total head movement. 
	 * Vice Versa for towards the first cylinder. 
	 * @param diskCylinders, the number of cylinders in the disk
	 * @param startCylinder, the cylinder where the disk starts
	 * @param cylinders, the list of cylinders that have I/O requests
	 */
	public static void LOOK(int diskCylinders, int startCylinder, ArrayList<Integer> cylinders) {
		int sum = 0;
		ArrayList<Integer> newCylinders = new ArrayList<Integer>(cylinders);
		if(!newCylinders.contains(startCylinder)) {
			newCylinders.add(startCylinder);
		}
		Collections.sort(newCylinders);
		int counter = newCylinders.indexOf(startCylinder);
		while(counter != 0) {
			if(counter == newCylinders.size()-1) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(newCylinders.indexOf(startCylinder)-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)-1)));
				counter = newCylinders.indexOf(startCylinder)-1;
			} else if(counter < newCylinders.indexOf(startCylinder)) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter-1)));
				counter--;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter+1)));
				counter++;
			}
		}
		
		System.out.println("For LOOK (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For LOOK (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		
		sum = 0;
		counter = newCylinders.indexOf(startCylinder);
		
		while(counter != newCylinders.size()-1) {
			if(counter == 0) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(newCylinders.indexOf(startCylinder)+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(newCylinders.indexOf(startCylinder)+1)));
				counter = newCylinders.indexOf(startCylinder)+1;
			} else if(counter > newCylinders.indexOf(startCylinder)) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter+1)));
				counter++;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter-1)));
				counter--;
			}
		}
		
		System.out.println("For LOOK (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For LOOK (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
	}
	
	/**
	 * This method implements the C-LOOK disk scheduling algorithm for towards
	 * the final cylinder, then for towards the first cylinder. For C-LOOK towards the
	 * final cylinder, the starting cylinder is added to the cylinders list if it is 
	 * not already in there. The cylinders list is then sorted in numerical order. 
	 * From the start cylinder travel to the next cylinder towards the final cylinder 
	 * calculating the total head movement each time. Once you reach the final cylinder, 
	 * travel to the first cylinder and travel towards the starting cylinder. Once you 
	 * reach the cylinder before the starting cylinder, terminate the loop and return 
	 * the total head movement. Vice Versa for towards the first cylinder. 
	 * @param diskCylinders, the number of cylinders in the disk
	 * @param startCylinder, the cylinder where the disk starts
	 * @param cylinders, the list of cylinders that have I/O requests
	 */
	public static void C_LOOK(int diskCylinders, int startCylinder, ArrayList<Integer> cylinders) {
		int sum = 0;
		ArrayList<Integer> newCylinders = new ArrayList<Integer>(cylinders);
		if(!newCylinders.contains(startCylinder)) {
			newCylinders.add(startCylinder);
		}
		Collections.sort(newCylinders);
		int counter = newCylinders.indexOf(startCylinder);
		while(counter != newCylinders.indexOf(startCylinder)-1) {
			if(counter == newCylinders.size()-1) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(0));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(0) + " = " + abs(newCylinders.get(counter) - newCylinders.get(0)));
				counter = 0;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter+1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter+1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter+1)));
				counter++;
			}
		}
		
		System.out.println("For C-LOOK (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For C-LOOK (toward final cylinder), the total head movement was " + sum + " cylinders.\n");
		
		sum = 0;
		counter = newCylinders.indexOf(startCylinder);
		
		while(counter != newCylinders.indexOf(startCylinder)+1) {
			if(counter == 0) {
				sum += abs(newCylinders.get(counter) - newCylinders.get(newCylinders.size()-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(newCylinders.size()-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(newCylinders.size()-1)));
				counter = newCylinders.size()-1;
			} else {
				sum += abs(newCylinders.get(counter) - newCylinders.get(counter-1));
				//System.out.println(newCylinders.get(counter) + " - " + newCylinders.get(counter-1) + " = " + abs(newCylinders.get(counter) - newCylinders.get(counter-1)));
				counter--;
			}
		}
		
		System.out.println("For C-LOOK (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
		printToFile("For C-LOOK (toward first cylinder), the total head movement was " + sum + " cylinders.\n");
	}
	
	/**
	 * This method takes in a string and prints it to the output file
	 * @param msg is the String to be printed to the file
	 */
	public static void printToFile(String msg) {
		try {
			File file = new File("Assignment6-Output.txt");
			FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(msg);
			printWriter.close();
		}	catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This methods creates an output.txt file if it doesn't already exist, to output
	 * the numbers read by the consumer, output when the consumer waits and when the consumer
	 * finishes.
	 */
	public static void createFile() {
		try {
			FileWriter fileWriter;
			File file = new File("Assignment6-Output.txt");
			PrintWriter printWriter;
			if(file.exists()) {
				fileWriter = new FileWriter(file.getAbsoluteFile(), true);
				printWriter = new PrintWriter(fileWriter);
			} else {
				fileWriter = new FileWriter(file);
				printWriter = new PrintWriter(fileWriter);
				printWriter.println("Prechar Xiong \nICS 462 Assignment #6 \n11/20/19 \n \n");
			}
			printWriter.close();
		}	catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this is the drive for the program
	 * @param args
	 */
	public static void main(String[] args) {
		readFile();
	}
}
