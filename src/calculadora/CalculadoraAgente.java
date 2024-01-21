package calculadora;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class CalculadoraAgente extends Agent {

    private AID Agente;
    String agente = "AgenteSuma";
    // String agente = "AgenteResta";
    // String agente = "AgenteMultiplicacion";
    // String agente = "AgenteDivision";
    int num1 = 20;
    int num2 = 5;

    @Override
    protected void setup() {
        System.out.println("Calculadora Agente iniciado: " + getLocalName());

        // Iniciar agente de suma directamente
        Agente = new AID(agente, AID.ISLOCALNAME);
        addBehaviour(new IniciarAgenteBehaviour());
    }

    private class IniciarAgenteBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Iniciar el agente
            try {
                AgentController ac = getContainerController().createNewAgent(
                        agente,
                        "calculadora."+agente,
                        new Object[]{});

                ac.start();
            } catch (StaleProxyException e) {}
            
            // Ejemplo de solicitud
            ACLMessage solicitud = new ACLMessage(ACLMessage.REQUEST);
            solicitud.addReceiver(Agente);
            solicitud.setContent(num1+","+num2); // Enviar operandos
            send(solicitud);

            // Esperar la respuesta
            ACLMessage respuesta = blockingReceive();
            if (respuesta != null) {
                System.out.println("Resultado: " + respuesta.getContent());
            } else {
                System.err.println("Error: No se recibió respuesta.");
            }
        }
    }
}
