package calculadora;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CalculadoraAgente extends Agent {

    private AID Agente;
    private int num1;
    private int num2;

    @Override
    protected void setup() {
        System.out.println("Calculadora Agente iniciado: " + getLocalName());

        // Crear la interfaz gráfica en el hilo de eventos de la interfaz de usuario
        SwingUtilities.invokeLater(this::crearInterfaz);
        
        // Crear agentes
        crearAgente("AgenteSuma");
        crearAgente("AgenteResta");
        crearAgente("AgenteMultiplicacion");
        crearAgente("AgenteDivision");
    }

    private void crearInterfaz() {
        JFrame frame = new JFrame("Calculadora Agente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Componentes de la interfaz
        JTextField num1Field = new JTextField(5);
        JComboBox<String> operacionComboBox = new JComboBox<>(new String[]{"+", "-", "*", "/"});
        JTextField num2Field = new JTextField(5);
        JLabel igualLabel = new JLabel("=");
        JTextField resultadoField = new JTextField(10);
        resultadoField.setEditable(false);

        JButton calcularButton = new JButton("Calcular");
        calcularButton.addActionListener((ActionEvent e) -> {
            // Obtener los valores de los campos
            num1 = Integer.parseInt(num1Field.getText());
            num2 = Integer.parseInt(num2Field.getText());
            String operacion = (String) operacionComboBox.getSelectedItem();
            switch (operacion) {
                case "+" -> operacion = "AgenteSuma";
                case "-" -> operacion = "AgenteResta";
                case "*" -> operacion = "AgenteMultiplicacion";
                case "/" -> operacion = "AgenteDivision";
            }
            
            // Enviar mensaje al agente correspondiente
            enviarMensaje(operacion, num1 + "," + num2);
            
            // Esperar la respuesta
            ACLMessage respuesta = blockingReceive();
            if (respuesta != null) {
                resultadoField.setText(respuesta.getContent());
            } else {
                resultadoField.setText("Error");
            }
        });

        // Panel principal
        JPanel panel = new JPanel();
        panel.add(num1Field);
        panel.add(operacionComboBox);
        panel.add(num2Field);
        panel.add(igualLabel);
        panel.add(resultadoField);
        panel.add(calcularButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void enviarMensaje(String agenteDestino, String contenido) {
        // Iniciar el agente
        Agente = new AID(agenteDestino, AID.ISLOCALNAME);

        // Solicitud
        ACLMessage solicitud = new ACLMessage(ACLMessage.REQUEST);
        solicitud.addReceiver(Agente);
        solicitud.setContent(contenido); // Enviar operandos
        send(solicitud);
    }
    
    private void crearAgente(String agenteDestino) {
        try {
            AgentController ac = getContainerController().createNewAgent(
                    agenteDestino,
                    "calculadora." + agenteDestino,
                    new Object[]{});
            ac.start();
        } catch (StaleProxyException e) {}
    }
}
