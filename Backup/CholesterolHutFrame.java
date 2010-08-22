import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

public class CholesterolHutFrame extends Frame
{
	public CholesterolHutFrame()
	{
		setTitle("Cholesterol Hut - May I take your order?");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int height = 400;
		int width  = 300;
		setBounds((d.width-width)/2, (d.height-height)/2, width, height);
		setResizable(false);
		addWindowListener
		(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{	
					System.exit(0);
				}
			}
		);		
		Container contentPane = getContentPane();
		JPanel panel = new CholesterolHutPanel();
		contentPane.add(panel);
	}
	public static void main(String[] args)
	{
		JFrame frame = new CholesterolHutFrame();
		frame.show();
	}
}

class CholesterolHutPanel extends JPanel implements ActionListener
{
	private JTextField amountTextField, rate, TextField, yearsTextField, paymentTextField;
	private JButton calculateButton, exitButton;

	public static double calculateMonthlyPayment(double amount, int months, double mi)
	{
		System.out.println("Got here");
		return 4.4;
	}

	public CholesterolHutPanel()
	{
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JRadioButton hamburgerRB = new JRadioButton("Hamburger - $6.95");
		JLabel amountLabel = new JLabel("Loan Amount:");
		JLabel rateLabel = new JLabel("Yearly Interest Rate:");
		JLabel yearsLabel = new JLabel("Number of Years:");
		JLabel paymentLabel = new JLabel("Monthly Payment:");
		JTextField amountTextField = new JTextField(10);
		JTextField rateTextField = new JTextField(10);
		JTextField yearsTextField = new JTextField(10);
		JTextField paymentTextField = new JTextField(10);
		paymentTextField.setEditable(false);
		displayPanel.add(amountLabel);
		displayPanel.add(amountTextField);
		displayPanel.add(rateLabel);
		displayPanel.add(rateTextField);
		displayPanel.add(yearsLabel);
		displayPanel.add(yearsTextField);
		displayPanel.add(paymentLabel);
		displayPanel.add(paymentTextField);
		displayPanel.add(hamburgerRB);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton calculateButton = new JButton("Calculate");
		JButton exitButton = new JButton("Exit");
		buttonPanel.add(calculateButton);
		buttonPanel.add(exitButton);

		calculateButton.addActionListener(this);
		exitButton.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(displayPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}	
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		try
		{
			if(source == exitButton)
			{
				System.exit(0);
			}
			else if(source == calculateButton)
			{
				double amount = Double.parseDouble(amountTextField.getText());
				double rate = Double.parseDouble(amountTextField.getText());
				int years = Integer.parseInt(yearsTextField.getText());
				double monthlyInterest = rate/12/100;
				int months = years * 12;
				double payment = calculateMonthlyPayment(
					amount, months, monthlyInterest);
				NumberFormat currency = NumberFormat.getCurrencyInstance();
				paymentTextField.setText(currency.format(payment));
			}
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(this,"Invalid data entered.\n"
			+	"Please check all numbers and try again.");
		}
	}
}	
	
	
	
				
	
/*
	JTextArea text;
	
	public static void main(String[] args)
	{
		CholesterolHut gui = new CholesterolHut();
		gui.go();
	}
	
	public void go()
	{
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JButton button = new JButton("Just Click It");
		button.addActionListener(this);
		//JCheckBox check = new JCheckBox("Goes to 11");
		//check.addActionListener(this);
		text = new JTextArea(10,20);	
		text.setLineWrap(true);
		
		JScrollPane scroller = new JScrollPane(text);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		panel.add(scroller);
		
		frame.getContentPane().add(BorderLayout.CENTER, panel);
		frame.getContentPane().add(BorderLayout.SOUTH, button);
		//frame.getContentPane().add(BorderLayout.EAST, check);
		
		frame.setSize(350,300);
		frame.setVisible(true);

	}
	
	public void actionPerformed(ActionEvent ev)
	{
		text.append("button clicked \n");
	}
/*	
	public void itemStateChanged(ItemEvent ev)
	{
		String onOrOff = "off";
		if(check.isSelected())
		{
			onOrOff = "on";
		}
		System.out.println("Check box is " + onOrOff);
	}
}
*/
