import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Name: Eric Du
 * Assignment 3: Minimize DFA
 */

public class parse {

	public static Set<Integer> getReachableStates(NFA dfa) {
		Set<Integer> reachableStates = new HashSet<Integer>();
		List<Integer> newStates = new ArrayList<Integer>();
		Set<Integer> temp = new HashSet<Integer>();

		reachableStates.add(0);
		newStates.add(0);
		
		while (!newStates.isEmpty()) {
			temp.clear();
			for (int i : newStates) {
				Map<Character, List<Integer>> newStateTransitions = dfa.transitions.get(i);
				for (char key : newStateTransitions.keySet()) {
					for (int j : newStateTransitions.get(key)) {
						temp.add(j);
					}
				}
			}
			
			newStates.clear();
			Object[] tempArray = temp.toArray();
			Object[] reachableArray = reachableStates.toArray();
			
			for (int i = 0; i < temp.size(); i++) {
				for (int j = 0; j < reachableStates.size(); j++) {
					if (tempArray[i] == reachableArray[j]) {
						break;
					}
					else if (tempArray[i] != reachableArray[j] && j == reachableStates.size()-1) {
						newStates.add((Integer) tempArray[i]);
					}
				}
			}
			
			if (!newStates.isEmpty()) {
				for (int x = 0; x < newStates.size(); x++) {
					reachableStates.add(newStates.get(x));
				}
			}	
		}
		
		return reachableStates;
	}
	
	public static NFA minimize(NFA dfa) {
		Set<Integer> reachableStates = getReachableStates(dfa);
		List<Integer> finalStates = new ArrayList<Integer>(dfa.finalStates);
		List<Integer> nonfinalStates = new ArrayList<Integer>();
		
		return dfa;
	}
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String filename = args[0];
		String inputStrings = args[1];
		Scanner input = new Scanner(new File(filename));
		NFA nfa = new NFA(input);
		NFA dfa = nfa.convertToDFA();
		System.out.println(getReachableStates(dfa));
		
		/*
		System.out.println("Minimized DFA from " + filename);
		System.out.print(" Sigma:");
		for (Character c : dfa.inputs) {
			System.out.print("   " + c);
		}
		System.out.println("\n --------------");
		for (int i = 0; i < dfa.numStates; i++) {
			System.out.print("     " + i + ":");
			for (Character c : dfa.inputs) {
				System.out.print("   " + dfa.getTransitionState(c, i).get(0));
			}
			System.out.println();
		}
		System.out.println(" --------------");
		System.out.println(dfa.initialState + ":   Initial State");
		if (dfa.finalStates.size() > 1) {
			for (int i = 0; i < dfa.finalStates.size()-1; i++) {
				System.out.print(dfa.finalStates.get(i) + ", ");
			}
		}
		System.out.print(dfa.finalStates.get(0));
		System.out.print(":   Accepting State(s)\n");
		System.out.println("The following strings are accepted:");
		
		FileReader fr = new FileReader(inputStrings);
		BufferedReader br = new BufferedReader(fr);
		try {
			String string = "";
			while ((string = br.readLine()) != null) {
				try {
					if (dfa.evaluate(string)) {
						System.out.println(string);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
	}

}
