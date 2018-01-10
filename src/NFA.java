import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Name: Eric Du
 * Assignment 2: Convert NFA to equivalent DFA
 */

public class NFA {

	public int numStates;
	public List<Character> inputs;
	public List<Map<Character, List<Integer>>> transitions;
	public int initialState;
	public List<Integer> finalStates;
	
	public NFA() {
		this(0, new ArrayList<>(), new ArrayList<>(), -1, new ArrayList<>());
	}
	
	public NFA(int states, List<Character> in, List<Map<Character, List<Integer>>> trans, int init, List<Integer> fin) {
		numStates = states;
		inputs = in;
		transitions = trans;
		initialState = init;
		finalStates = fin;

	}
	
	public NFA(Scanner input) {
		String line = input.nextLine();
		numStates = Integer.parseInt(line);
		
		inputs = new ArrayList<>();
		for (String s : input.nextLine().trim().split("\\s+")) {
			inputs.add(s.charAt(0));
		}
		// ' ' represents lambda
		inputs.add(' ');

		transitions = new ArrayList<>();
		for (int i = 0; i < numStates; i++) {
			line = input.nextLine();
			Map<Character, List<Integer>> transitionState = new HashMap<>();
			for (Character c : inputs) {
				List<Integer> nextState = new ArrayList<>();
				line = parseTransition(line, nextState);
				transitionState.put(c, nextState);
			}
			transitions.add(transitionState);
		}
		initialState = Integer.parseInt(input.nextLine());
		finalStates = new ArrayList<>();
		parseTransition(input.nextLine(), finalStates);
	}
	
	public String parseTransition(String line, List<Integer> state) {
		line = line.substring(line.indexOf("{") + 1, line.length());
		if (!(line.charAt(0) == '}')) {
			while (line.indexOf(",") < line.indexOf("}") && line.indexOf(",") > 0) {
				state.add(Integer.parseInt(line.substring(0, line.indexOf(","))));
				line = line.substring(line.indexOf(",") + 1, line.length());
			}
			state.add(Integer.parseInt(line.substring(0, line.indexOf("}"))));
		}
		
		return line.substring(line.indexOf("}") + 1, line.length());
	}
	
	public boolean evaluate(String str) throws Exception {
		int state = 0;
		str += "$";
		char ch = str.charAt(0);

		while (ch != '$') {
			if (inputs.contains(ch)) {
				if (transitions.get(state).get(ch).size() > 0)
					state = transitions.get(state).get(ch).get(0);
				else
					state = -1;
				if (state == -1 && !finalStates.contains(state))
					return false;
				str = str.substring(1, str.length());
				ch = str.charAt(0);
			}
			else
				return false;
		}
		
		if (!finalStates.contains(state))
			return false;

		return true;
	}
	
	public NFA convertToDFA() {
		NFA dfa = new NFA();
		dfa.initialState = this.initialState;
		List<List<Integer>> newStates = new ArrayList<>();
		List<Integer> lambdaClosure = new ArrayList<>();
		calculateLambdaClosure(0, lambdaClosure);
		newStates.add(lambdaClosure);
		
		List<Character> dfaInputs = new ArrayList<>(this.inputs);
		// remove lambda from DFA
		dfaInputs.remove(Character.valueOf(' '));
		
		List<Integer> current = new ArrayList<>(lambdaClosure);
		int totalState = 0, finalState = 0;
		
		while (current != null) {
			Map<Character, List<Integer>> dfaTransition = new HashMap<>();
			for (Character input : dfaInputs) {
				List<Integer> tempStates = new ArrayList<>();
				for (Integer currentState : current) {
					for (Integer transitionState : this.transitions.get(currentState).get(input)) {
						calculateLambdaClosure(transitionState, tempStates);
					}
				}
				List<Integer> dfaState = new ArrayList<>();
				Collections.sort(tempStates);
				if (!newStates.contains(tempStates)) {
					newStates.add(tempStates);
					dfaState.add(++totalState);
				}
				else {
					dfaState.add(newStates.indexOf(tempStates));
				}
				dfaTransition.put(input, dfaState);
			}
			dfa.numStates++;
			dfa.transitions.add(dfaTransition);
			
			for (int state : finalStates) {
				if (current.contains(state)) {
					if (!(dfa.finalStates.contains(state))) {
						dfa.finalStates.add(finalState);
						break;
					}
				}
			}
			if (++finalState >= newStates.size()) {
				current = null;
			}
			else {
				current = newStates.get(finalState);
			}
		}
		
		dfa.inputs = dfaInputs;
		
		return dfa;
	}
	
	public void calculateLambdaClosure(int state, List<Integer> lambdaClosure) {
		Map<Character, List<Integer>> stateTransitions = transitions.get(state);
		if (lambdaClosure.contains(state)) {}
		else if (stateTransitions.containsKey(' ')) {
			lambdaClosure.add(state);
			for (Integer i : stateTransitions.get(' ')) {
				calculateLambdaClosure(i, lambdaClosure);
			}
		}
	}
	
	public List<Integer> getTransitionState(char input, int state) {
		return this.transitions.get(state).get(input);
	}
	
	public Set<Integer> getReachableStates() {
		Set<Integer> reachableStates = new HashSet<Integer>();
		List<Integer> newStates = new ArrayList<Integer>();
		Set<Integer> temp = new HashSet<Integer>();

		reachableStates.add(0);
		newStates.add(0);
		
		while (!newStates.isEmpty()) {
			temp.clear();
			for (int i : newStates) {
				Map<Character, List<Integer>> newStateTransitions = this.transitions.get(i);
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
	
	public void minimize() {
		Set<Integer> reachableStates = getReachableStates();
		System.out.println(reachableStates.size());
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
		System.out.print("Sigma: ");
		for (char c : nfa.inputs) {
			if (c != ' ')
				System.out.print(c + " ");
		}
		System.out.println("\n------");
		for (int i = 0; i < nfa.numStates; i++) {
			System.out.print(i + ":   ");
			//System.out.print(nfa.transitions.get(i));
			for (int j = 0; j < nfa.inputs.size(); j++) {
				System.out.print("(" + nfa.inputs.get(j) + "," + nfa.getTransitionState(nfa.inputs.get(j), i) + ") ");
			}

			System.out.println();
		}
		System.out.println("------");
		System.out.println(nfa.initialState + ":   Initial State");
		if (nfa.finalStates.size() > 1) {
			for (int i = 0; i < nfa.finalStates.size()-1; i++) {
				System.out.print(nfa.finalStates.get(i) + ", ");
			}
		}
		System.out.print(nfa.finalStates.get(0));
		System.out.print(":   Accepting State(s)\n\n");
		
		System.out.println("To DFA:");
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

		
	}
	
}
