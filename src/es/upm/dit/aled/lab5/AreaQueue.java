package es.upm.dit.aled.lab5;

import java.util.LinkedList;
import java.util.Queue;

import es.upm.dit.aled.lab5.gui.Position2D;

/**
 * Extension of Area that maintains a strict queue of the Patients waiting to
 * enter in it. After a Patient exits, the first one in the queue will be
 * allowed to enter.
 * 
 * @author rgarciacarmona
 */
public class AreaQueue extends Area {
	
	// Cola FIFO (First-In, First-Out) para guardar el orden estricto de llegada de los pacientes
	private Queue<Patient> waitQueue;
	
	public AreaQueue(String name, int time, int capacity, Position2D position) {
		super(name, time, capacity, position); //Llamamos constructor clase padre (Area) para inicializar atributos comunes
		this.waitQueue = new LinkedList<>(); //Queue es una interfaz y no se puede instanciar sola
	}
	
	@Override
		public synchronized void enter(Patient p) {
		//1. REGISTRO DE LLEGADA:
		//Como este bloque está sincronizado, los pacientes se añaden a la cola en el orden exacto en que adquieren el cerrojo
		waiting++;
		waitQueue.add(p);
		
		//2. CONDICIÓN DE ESPERA (Siempre con 'while' para reevaluar al despertar):
		//El paciente debe quedarse esperando si se cumple CUALQUIERA de estas dos cosas:
		//  a) numPatients >= capacity  -> El área está llena (no hay hueco).
		//  b) p != waitQueue.peek()    -> Hay hueco, pero este paciente NO es el primero de la cola (no es su turno).
		while(numPatients >= capacity || p != waitQueue.peek()) {
			try {
				//El hilo suelta el cerrojo (monitor) de AreaQueue y se queda dormido hasta que otro hilo llame a notifyAll()
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//3. ENTRADA EFECTIVA AL ÁREA:
		//Si el hilo llega aquí, se garantizan dos cosas a la vez: hay hueco disponible Y 'p' es la cabeza de la cola.
				numPatients++; //Ocupa una plaza en el área
				waiting--; //Deja de contabilizar al paciente como en espera
				waitQueue.remove(); //Sale de la cola de espera (era el primero, el que devolvía peek())
				System.out.println("Patient " + p.getNumber() + " has entered " + name);
		}
	
	}
	
