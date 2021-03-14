package hu.bme.mit.yakindu.analysis.workhere;

import java.util.Random;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import hu.bme.mit.model2gml.Model2GML;
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
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				System.out.println(state.getName());
			}
			else if(content instanceof Transition) {
				Transition transition = (Transition) content;
				System.out.println(transition.getSource().getName() + " -> " + transition.getTarget().getName());
			}
		}
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
		}
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
