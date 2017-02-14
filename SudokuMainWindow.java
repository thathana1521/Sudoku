/*Project JAVA 2016
 * Michalakakis Konstantinos 1474
 * Athanasiou Thomas 1521 */
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.Border;
import java.io.*;
import java.util.Scanner;
import java.net.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

public class SudokuMainWindow extends JFrame implements ActionListener {
	
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	public int [][] startArray;//Pinakas pou periexei panta tis arxikes times meta to parsing. Allazei mono se new game
	public int [][] array; // Pinakas pou periexei kathe stigmi ta stoixeia tou panel-sudoku
	public int [][] solution;//Pinakas pou periexei tin lush
	public int gram,stili,steps;//gramm, stili deiktes sumfwna me ti thesh tou click tou pontikiou (MouseListener), steps vimata opou o paikths oloklhrwnei tin epilush
	public int ListPointer1,ListPointer2,ListPointer3;//Deiktes 
	public boolean flagStarter; //simatodotei tin enarxi tou paixnidiou kai orizei thn arxh katagrafis toy istorikou stin lista
	
	ArrayList<Integer> undoElements= new ArrayList<Integer>();
	
	JPanel southPanel=new JPanel();//Panel pou periexei ta buttons me ta noumera
	JPanel northPanel=new JPanel();//Panel pou periexei ta buttons undo, erase, solve kai verifyBox 
	JPanel mainPanel;//Panel pou periexei to paixnidi (JtextFields)
	JPanel windowPanel;//Geniko Panel
	JTextField[][] cells;//kelia paixnidiou
	String numberSelection;//Periexei tin arithmitiki timi tou button pou dialegoume se morfh String
	JCheckBox verify= new JCheckBox("Verify against solution");
	Color blue = new Color(51, 153, 255);
	public String gameType;//Periexei ton tupo paixnidiou pou dialegoume apo to menu (easy, intermediate, hard)
	
	public static void main(String args[]) {
		
		
		SudokuMainWindow gui = new SudokuMainWindow();
		/*Emfanish parathurou epalitheushs tis exodou.*/
		gui.addWindowListener ( new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				showDialog(gui);
			}
		});
		
		gui.setVisible(true);
		gui.setResizable(false);
	}
	
	public SudokuMainWindow() {
		
		super("Sudoku");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		startArray = new int[9][9];
		array = new int[9][9];
		solution = new int[9][9];
		cells= new JTextField[9][9];
		flagStarter=false;
		
		sudokuMenu();
		
		JPanel windowPanel = new JPanel();
		windowPanel.setLayout(new BorderLayout());
		
		ListPointer1=0;
		ListPointer2=1;
		ListPointer3=2;

		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.setPreferredSize(new Dimension (100, 100));
		ButtonListener listener = new ButtonListener();
		
		/*Listeners.*/
		MouseListen mouseListen = new MouseListen();
		ButtonListenerEraser listenerEraser= new ButtonListenerEraser();
		SolutionListener solListener = new SolutionListener();
		VerifyListener verifyListener = new VerifyListener();
		UndoListener undoListener = new UndoListener();
		
		/*Buttons.*/
		JButton button1 = new JButton("1");
		button1.addActionListener( listener );
		southPanel.add(button1);
		
		JButton button2 = new JButton("2");
		button2.addActionListener( listener );
		southPanel.add(button2);
		
		JButton button3 = new JButton("3");
		button3.addActionListener( listener );
		southPanel.add(button3);
		
		JButton button4 = new JButton("4");
		button4.addActionListener( listener );
		southPanel.add(button4);
		
		JButton button5 = new JButton("5");
		button5.addActionListener( listener );
		southPanel.add(button5);
		
		JButton button6 = new JButton("6");
		button6.addActionListener( listener );
		southPanel.add(button6);
		
		JButton button7 = new JButton("7");
		button7.addActionListener( listener );
		southPanel.add(button7);
		
		JButton button8 = new JButton("8");
		button8.addActionListener( listener );
		southPanel.add(button8);
		
		JButton button9 = new JButton("9");
		button9.addActionListener( listener );
		southPanel.add(button9);
		
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.setPreferredSize(new Dimension (100, 100));
		
		/*Dhmiourgia buttons gia undo, solution, eraser kai eisagwgh tous sto panel.*/
		JButton buttonU = new JButton();
		JButton buttonE = new JButton();
		JButton buttonS = new JButton();
		
		ImageIcon iconU=null;
		Image imageU = null;
		ImageIcon iconS=null;
		Image imageS = null;
		ImageIcon iconE=null;
		Image imageE = null;
		
		iconS = new ImageIcon(getClass().getResource("solution.jpg"));
		imageS= iconS.getImage().getScaledInstance(50,50, java.awt.Image.SCALE_SMOOTH);
		iconS= new ImageIcon(imageS,"solution");
		buttonS.setIcon(iconS);
		
		iconU = new ImageIcon(getClass().getResource("undo.png"));
		imageU= iconU.getImage().getScaledInstance(50,50, java.awt.Image.SCALE_SMOOTH);
		iconU= new ImageIcon(imageU,"undo");
		buttonU.setIcon(iconU);
		
		iconE = new ImageIcon(getClass().getResource("eraser.jpg"));
		imageE= iconE.getImage().getScaledInstance(50,50, java.awt.Image.SCALE_SMOOTH);
		iconE= new ImageIcon(imageE,"solution");
		buttonE.setIcon(iconE);
		
		northPanel.add(buttonU);
		northPanel.add(buttonE);
		northPanel.add(buttonS);
		buttonS.addActionListener( solListener );
		buttonE.addActionListener( listenerEraser );
		buttonU.addActionListener( undoListener );
		
		verify.addActionListener( verifyListener );
		northPanel.add(verify);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(3,3,4,4));
		
		JPanel[] subPanel = new JPanel[9];
		
		/*counter1, counter2, counter3, p, starti, startj, upperi, upperj xrhsimopoiounte gia thn ulopoihsh tou Layout
		 
		 Stoxos auths ths ulopoihshs einai na apotrepsei tin seiriaki diatrexi tou pinaka*/
		int counter1=0;
		int counter2;
		int starti=0;
		int startj=0;
		int upperi=3;
		int upperj=3;
		int p=0;
		int counter3=0;
		while (counter1<3){
					
			counter2=0;
			while (counter2<3){
				
				counter3++;
				subPanel[p] = new JPanel();
				subPanel[p].setLayout(new GridLayout(3,3,0,0));
				
				for (int i=starti;i<upperi;i++){
					for (int j=startj;j<upperj;j++){
						cells[i][j] = new JTextField("");
						cells[i][j].setHorizontalAlignment(JLabel.CENTER);
						cells[i][j].setEditable(false);
						cells[i][j].setBackground(new Color(255, 255, 255));
						cells[i][j].addMouseListener( mouseListen );
						subPanel[p].add(cells[i][j]);  
					}
				}
				
				mainPanel.add(subPanel[p]);
				p++;
				startj=startj+3;
				upperj=upperj+3;
				counter2++;
			}
			startj=0;
			upperj=3;
			starti=starti+3;
			upperi=upperi+3;
			counter1++;
		}
		
		windowPanel.add(mainPanel,BorderLayout.CENTER);
		windowPanel.add(southPanel, BorderLayout.SOUTH);
		windowPanel.add(northPanel, BorderLayout.NORTH);
		
		this.add(windowPanel);
	}

	/*Dimiourgia Menu.*/
	private void sudokuMenu () {
		
		JMenu sudokuMenu = new JMenu("New Game");
		sudokuMenu.setMnemonic(KeyEvent.VK_N);
		
		JMenuItem easyMenu = new JMenuItem("Easy");
		easyMenu.addActionListener(this);    
		easyMenu.setMnemonic(KeyEvent.VK_E);
		sudokuMenu.add(easyMenu);
		
		JMenuItem intermediateMenu = new JMenuItem("Intermediate");
		intermediateMenu.addActionListener(this);
		intermediateMenu.setMnemonic(KeyEvent.VK_I);
		sudokuMenu.add(intermediateMenu);
		
		JMenuItem hardMenu = new JMenuItem("Hard");
		hardMenu.addActionListener(this);
		hardMenu.setMnemonic(KeyEvent.VK_H);
		sudokuMenu.add(hardMenu);
		
		JMenuBar bar = new JMenuBar();
		bar.add(sudokuMenu);
		setJMenuBar(bar);
	}
	
	/*Otan epilegei to Solution Button. Emfanizetai i lush.*/
	public class SolutionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) { 
			int i,j;
			clearCells("clearForSolution");
			for(i=0; i<9; i++) {
				for(j=0; j<9; j++) {
					cells[i][j].setText(""+solution[i][j]);
				}
			}
			/*Disable Panels. */
			setPanelEnabled(southPanel,false);
			setPanelEnabled(northPanel,false);
			verify.setSelected(false);
		}
	}
	
	/*Otan epilegei to checkBox Verify.*/
	public class VerifyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) { 
			
			verifyBox();
			
		}
	}
	
	/*Sugrinei tous pinakes array me to solution. An uparxoun diafores tis xrwmatizei blue.*/
	public void verifyBox(){
	
	int i,j;
	clearCells("clearForSolution");
			if ( verify.isSelected() ) {
				for(i=0; i<9; i++) {
					for(j=0; j<9; j++) {
						
						if ( array[i][j]!=solution[i][j] && array[i][j]!=0) {
							
							cells[i][j].setBackground(new Color(51, 153, 255));
						}
						else if(array[i][j]==solution[i][j] && array[i][j]!=0 && cells[i][j].isEditable()==true) {
						
							cells[i][j].setBackground(new Color(255, 255, 255));
						
						}
					}
				}
			}
			else {
				clearCells("clearForSolution");
			}
	}
	
	/*Listener gia ta Buttons apo 1 ws 9.*/
	public class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		
			int number,preValue;//Akeraia timh tou string numberSelection, preValue h timh pou periexei hdh to kouti pou epilegthike
			steps++;//auxhsh tou steps kathe fora pou o xrhsths pataei ena button 1-9.
			numberSelection=e.getActionCommand();
			number=Integer.parseInt(numberSelection);
			clearCells("clearForMarking");
			if( checkAvailability(number, array, gram, stili)==true && startArray[gram][stili]==0 && flagStarter==true)  {
				
				cells[gram][stili].setText(""+number);
				preValue=array[gram][stili];
				array[gram][stili]=number;
				/*Kathe fora pou epilegoume ena stoixeio mpainoune sti lista h grammh h stulh kai i timi pou epilexame.*/
				undoElements.add(gram);
				undoElements.add(stili);
				undoElements.add(preValue);
			
				ListPointer1=ListPointer1+3;
				ListPointer2=ListPointer2+3;
				ListPointer3=ListPointer3+3;
				
				/*Elegxos an to verify einai epilegmeno.*/
				verifyBox();
			}
			
			/*Elegxos an exei luthei to puzzle. An nai disable northPanel kai southPanel.*/
			if( checkIfSolved()==true ) {
				setPanelEnabled(southPanel,false);
				setPanelEnabled(northPanel,false);
				congratsWindow();//Emfanizei parathuro Congratulations 
			}
		}
	}
	
    /*Anaklhsh twn stoixeiwn tis listas kathe fora pou epilegoume tin anairesh.*/
	public class UndoListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

		int first, second, third;
		
		if (undoElements.isEmpty()){
		
		System.out.println("No more elements");
		
		}
		else {
			ListPointer1=ListPointer1-3;
			ListPointer2=ListPointer2-3;
			ListPointer3=ListPointer3-3;
			third=undoElements.get(ListPointer3);
			undoElements.remove(ListPointer3);
			second=undoElements.get(ListPointer2);
			undoElements.remove(ListPointer2);
			first=undoElements.get(ListPointer1);
			undoElements.remove(ListPointer1);
			if(third==0){
			cells[first][second].setText("");
			array[first][second]=third;
			}
			else{
			cells[first][second].setText(""+third);
			array[first][second]=third;
			}
			verifyBox();
		}
		
		}
	}
	
    /*Xeirizetai ta click tou pontikiou sta JTextFields epistrefontas grammh kai sthlh tou antistoixou stoixeiou.*/
	public class MouseListen extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			
			int i,j;
			clearCells("clearForMarking");
			for(i=0; i<9; i++) {
				for(j=0; j<9; j++) {
					
					if (e.getSource() == cells[i][j] && array[i][j]==0) {
						cells[i][j].setBackground(new Color(204,255,253));
						gram=i;
						stili=j;
						continue;
					}
						
					if(e.getSource() == cells[i][j]) {
						gram=i;
						stili=j;
						clearCells("clearForMarking");
						markSame(i,j);
					}
				}
			}
		}
	}
  
    /*Svinei to epilegomeno stoixeio kai katagrafei to gegonos sti lista.*/
	public class ButtonListenerEraser implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			clearCells("clearForMarking");
			int prev;
			if(startArray[gram][stili]==0) {
				cells[gram][stili].setText("");
				prev=array[gram][stili];
				array[gram][stili]=0;
				cells[gram][stili].setBackground(new Color(255,255,255));
				
				undoElements.add(gram);
				undoElements.add(stili);
				undoElements.add(prev);
				
				ListPointer1=ListPointer1+3;
				ListPointer2=ListPointer2+3;
				ListPointer3=ListPointer3+3;
			}
		}
	}
  
    /*Se neo paixnidi. An ta panel einai apenergopoihmena, ta energopoiei. Kathe fora ta steps xekinane pali apo 0. Epistrefei to gameType(easy,intermediate,hard).*/
	public void actionPerformed(ActionEvent e) {

		steps=0;
		setPanelEnabled(southPanel,true);
		setPanelEnabled(northPanel,true);
		gameType = e.getActionCommand();
		parsGame( );
	}
    
    /*Energopoiei h apenergopoei Panel.*/
	public void setPanelEnabled(JPanel panel, Boolean isEnabled) {
		panel.setEnabled(isEnabled);

		Component[] components = panel.getComponents();
		for(int i = 0; i < components.length; i++) {
			if(components[i].getClass().getName() == "javax.swing.JPanel") {
				setPanelEnabled((JPanel) components[i], isEnabled);
			}
			components[i].setEnabled(isEnabled);
		}
	}
  
    /*Parathuro Congratulations pou emfanizetai an o xrhsths lusei to puzzle.*/
	public void congratsWindow() {
		JOptionPane.showMessageDialog(null, "Congratulations you solved the puzzle in "+steps+" steps!","Congrats", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/*Parathuro dialogou epalitheushs exodou. (YES or NO).*/
	public static void showDialog(Component c) {
		
		String ObjButtons[] = {"Yes","No"};
		
		int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit Sudoku?","Exit Sudoku",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
		if(PromptResult==JOptionPane.YES_OPTION)
		{
			System.exit(0);
		}
	}
    
    /*Methodos elegxou an to puzzle exei luthei swsta. Sugrish array, solution.*/
	public boolean checkIfSolved() {
		int i,j;
		
		if (solution[0][0]==0) {
			return false;
		}
		for(i=0; i<9; i++) {
				for(j=0; j<9; j++) {
					
					if( array[i][j]!=solution[i][j]) {
						return false;
					}
				}
		}
		return true;
	}
  
    /*Methodos epilushs tou puzzle. Tou arxikou array.*/
	private int solve(int array[][], int row, int column) {
		
		int val;
		
		if (row<9 && column<9) {
			
			if (array[row][column] != 0) {
				
				if( (column+1)<9 ) {
					return solve(array, row, column+1);
				}
				else if ( (row+1)<9) {
					return solve(array,row+1,0);
				}
				else {
					return 1;
				}
			}
			else {
				for(val=0; val<9; ++val) {
					
					if (checkingForSolution(val+1, array, row, column)) {
						
						array[row][column]=val+1;
						if((column+1)<9) {
							if (solve(array, row, column+1)==1) {
								return 1;
							}
							else {
								array[row][column]=0;
							}
						}
						else if((row+1)<9) {
							
							if(solve(array, row+1, 0)==1 ) {
								return 1;
							}
							else {
								array[row][column]=0;
							}
						}
						else {
							return 1;
						}
					}
				}
			}
			return 0;
		}
		else {
			return 1;
		}
	}
    
    /*Xrwmatismos twn stoixeiwn me tin idia timh me to epilegomeno.*/
	public void markSame (int x, int y) {
		
		int i, j;
		
		clearCells("clearForMarking");
		for(i=0; i<9; i++) {
			for(j=0; j<9; j++) {
				
				if (array[i][j]==array[x][y] && array[i][j]!=0 && !blue.equals(cells[i][j].getBackground()) ) {
					
					cells[i][j].setBackground(new Color(255, 255, 0));
				}
			}
		}
	}
    
    /*Elegxos egurothtas timwn me vash tous kanones tou sudoku. Xrhsimopoieitai stin methodo solve.*/
	private boolean checkingForSolution(int val, int array[][], int row, int column) {
		int i;
		//elegxos grammwn
		for (i=0; i<9; i++) {
			
			if(i!=column) {
				
				if(array[row][i]==val) {
					return false;
				}
			}
		}
		
		//elegxos stulwn
		for (i=0; i<9; i++) {
			
			if(i!=row) {
				
				if(array[i][column]==val) {
					return false;
				}
			}
		}
		
		//elegxos 3x3 koutiou
		int sBlocki, sBlockj,j;
		sBlocki=row/3*3;
		sBlockj=column/3*3;
		
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				
				if(i!=row && j!=column) {
					if( array[sBlocki+i][sBlockj+j]==val) {
						return false;
					}
				}
			}
		}
		return true;
	}
    
    /*Elegxos egurothtas timwn tou xrhsth me vash tous kanones tou sudoku. Xrwmatizei me kokkino ta stoixeia pou emfanizetai paravash.*/
	private boolean checkAvailability(int val, int array[][], int row, int column) {
		int i;
		
		verifyBox();
		//elegxos grammwn
		for (i=0; i<9; i++) {
			
			if(i!=column) {
				
				if(array[row][i]==val && startArray[row][column]==0) {
					cells[row][i].setBackground(new Color(193, 15, 15));
					return false;
				}
			}
		}
		
		//elegxos stulwn
		for (i=0; i<9; i++) {
			
			if(i!=row) {
				
				if(array[i][column]==val && startArray[row][column]==0) {
					cells[i][column].setBackground(new Color(193, 15, 15));
					return false;
				}
			}
		}
		
		//elegxos 3x3box
		int sBlocki, sBlockj,j;
		sBlocki=row/3*3;
		sBlockj=column/3*3;
		
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				
				if(i!=row && j!=column) {
					if( array[sBlocki+i][sBlockj+j]==val && startArray[row][column]==0) {
						cells[sBlocki+i][sBlockj+j].setBackground(new Color(193, 15, 15));
						return false;
					}
				}
			}
		}
		return true;
	}

	/*Katharismos keliwn.*/
	public void clearCells(String type) {
		int i,j;
		
		//Katharismos gia neo paixnidi.
		if(type.equals("clearForNewGame")) {
			for(i=0; i<9; i++) {
				for(j=0; j<9; j++) {
					cells[i][j].setBackground(new Color(255, 255, 255));
				}
			}
		}
		
		//Katharismos xrwmatwn omoiwn keliwn
		else if(type.equals("clearForMarking")) {
			for(i=0; i<9; i++) {
				for(j=0; j<9; j++) {
					
					if ( blue.equals(cells[i][j].getBackground()) ) {
						continue;
					}
					if (startArray[i][j]!=0) {
						cells[i][j].setBackground(new Color(224, 224, 224));
					}
					else {
						cells[i][j].setBackground(new Color(255, 255, 255));
					}
				}
			}
		}
		//Katharismos olwn twn keliwn gia thn anadeixh ths lushs.
		else if(type.equals("clearForSolution")) {
			for(i=0; i<9; i++) {
				for(j=0; j<9; j++) {

					if (startArray[i][j]!=0) {
						cells[i][j].setBackground(new Color(224, 224, 224));
					}
					else {
						cells[i][j].setBackground(new Color(255, 255, 255));
					}
				}
			}
		}
	}
	
	/*Parsing.*/
	public int[][] parsGame( ){
			
		int i,j,k;
		
		URL url=null;
		String out=null;
		if(gameType.equals("Easy")) {
			try {
				url = new URL("http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=easy");
			} catch(MalformedURLException e) {
				System.out.println("The url is not well formed: " + url);
			}
			try {
				out = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
			} catch(IOException e) {
				System.out.println("The string is not well formed: " + out);
			}
		}
		else if(gameType.equals("Intermediate")) {
			try {
				url = new URL("http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=intermediate");
			} catch(MalformedURLException e) {
				System.out.println("The url is not well formed: " + url);
			}
			try {
				out = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
			} catch(IOException e) {
				System.out.println("The string is not well formed: " + out);
			}
		}
		else if(gameType.equals("Hard")) {
			try {
				url = new URL("http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=expert");
			} catch(MalformedURLException e) {
				System.out.println("The url is not well formed: " + url);
			}
			try {
				out = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
			} catch(IOException e) {
				System.out.println("The string is not well formed: " + out);
			}
		}

		System.out.println("Parser.\n"+out);
		
		char[] strArray = out.toCharArray();
		
		clearCells("clearForNewGame");
			
		i=0;
		flagStarter=true;
		for(j=0; j<9; j++) {
			for(k=0; k<9; k++) {
				
				while(strArray[i]!='.' && !Character.isDigit(strArray[i])) {
					i++;
				}
				if(strArray[i]=='.'){
					startArray[j][k]=0;
					array[j][k]=0;
					solution[j][k]=0;
					cells[j][k].setText("");
					
				}
				else if(Character.isDigit(strArray[i])) {
					startArray[j][k]=Character.getNumericValue(strArray[i]);
					array[j][k]=Character.getNumericValue(strArray[i]);
					solution[j][k]=Character.getNumericValue(strArray[i]);
					cells[j][k].setText(""+array[j][k]);
					cells[j][k].setEditable(false);
					cells[j][k].setBackground(new Color(224, 224, 224));
				}
				if(i<out.length()) {
					
					i++;
				}
				
			}
			
		}
			
		System.out.println("ARRAY OF INTEGERS:");
		for(i=0; i<9; i++) {
			System.out.print("\n");
			for(j=0; j<9; j++) {
				System.out.print(array[i][j]);
			}
		}
		System.out.println("");
		if(solve(solution,0,0)==0) {
			System.out.println("Sudoku did not solved.\n");
		}
		else {
			System.out.println("Sudoku solved.\n");
		}
		
		System.out.println("SOLUTION:");
		for(i=0; i<9; i++) {
			System.out.print("\n");
			for(j=0; j<9; j++) {
				System.out.print(solution[i][j]);
			}
		}
		System.out.print("\n");
		
		return array;
	}

}