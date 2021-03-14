package hu.bme.mit.yakindu.analysis.workhere;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		
		ArrayList<String> variableList =new ArrayList<String>();
		ArrayList<String> eventList =new ArrayList<String>();
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			/*
			if(content instanceof State) {
				State state = (State) content;
				System.out.println(state.getName());
			}
			else if(content instanceof Transition) {
				Transition transition = (Transition) content;
				System.out.println(transition.getSource().getName() + " -> " + transition.getTarget().getName());
			}*/
			
			if(content instanceof EventDefinition) {
				EventDefinition def = (EventDefinition) content;
				//System.out.println(def.getName());
				eventList.add(def.getName());
			}
			else if(content instanceof VariableDefinition) {
				VariableDefinition var = (VariableDefinition) content;
				//System.out.println(var.getName());
				variableList.add(var.getName());
			}
		}
		
	
		/*
		//Search for trap states - 2.4
		TreeIterator<EObject> iterator2 = s.eAllContents();
		System.out.println("\nTrap states:");
		while (iterator2.hasNext()) {
			EObject content = iterator2.next();
			if(content instanceof State) {
				State state = (State) content;
				if(state.getOutgoingTransitions().size() == 0) {
					System.out.println(state.getName());
				}
			}
		}
		//Search for unnamed states - 2.5
		TreeIterator<EObject> iterator3 = s.eAllContents();
		while(iterator3.hasNext()) {
			EObject content = iterator3.next();
			if(content instanceof State) {
				State state = (State) content;
				if(state.getName()=="") {
					System.out.println("Az állapotnak nincsen neve");
					System.out.println("Javasolt név: ");
					Random r =new Random();
					char c1 = (char)(r.nextInt(26)+'A');
					System.out.print(c1);
					for(int i=0; i<6;i++) {
						char c = (char)(r.nextInt(26)+'a');
						System.out.print(c);
					}
					System.out.println("");
					
				}
			}
		}*/
		System.out.println("public class RunStatechart {\n");
		System.out.println("\tpublic static void main(String[] args) throws IOException {");
		System.out.println("\t\tExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		s.runCycle();\r\n" + 
				"		Scanner scanner = new Scanner(System.in);\r\n"+ 
				"		while(scanner.hasNextLine()) {\r\n" + 
				"			String cmd=scanner.nextLine();\r\n" + 
				"			switch(cmd) {");
		for(String tmp:eventList) {
			String firstLetter=tmp.substring(0, 1).toUpperCase();
			String event=firstLetter+tmp.substring(1);
			System.out.println("\t\t\t\tcase \""+ tmp + "\":\r\n" + "\t\t\t\t\ts.raise" + event +"();");
			System.out.println("\t\t\t\t\ts.runCycle();\r\n"+"\t\t\t\t\tbreak;");
		}
		System.out.println("\t\t\t\tcase \"exit\":\r\n" +
				"					System.out.print(\"Process terminated\");\r\n" + 
				"					System.exit(0);\r\n" + 
				"					break;\r\n" + 
				"				default:\r\n" + 
				"					System.out.println(\"Invalid command\");\r\n" + 
				"					break;\r\n" + 
				"			}\r\n" + 
				"			print(s);\r\n" + 
				"		}\r\n" + 
				"		scanner.close();\n");
		System.out.println("\tpublic static void print(IExampleSTatemachine s) {");
		for(String tmp: variableList) {
			String firstLetter=tmp.substring(0, 1).toUpperCase();
			String variable=firstLetter+tmp.substring(1);
			System.out.println("\t\tSystem.out.println(\""+firstLetter+" = \" + s.getSCInterface().get"+variable+"());");
		}
		System.out.println("\t}");
		System.out.println("}");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
