package edu.jhu.cvrg.zipconverter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FileChooser extends JPanel
   implements ActionListener {
   JButton go;
   JLabel label, label2, label3;
   JFileChooser chooser;
   String choosertitle;
   
  public FileChooser() {
	  	//super(new BoxLayout(this, flags));
	  	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    int result;
        
	    chooser = new JFileChooser();
	    chooser.setFileSelectionMode(
	            JFileChooser.DIRECTORIES_ONLY);
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle(choosertitle);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    chooser.setAcceptAllFileFilterUsed(false);
	    //    
	    
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	
	      //System.out.println("getCurrentDirectory(): " 
	        // +  chooser.getSelectedFile().getAbsolutePath());
	    	  try {
	    		  
	    		  
	    		  String dirName = chooser.getSelectedFile().getAbsolutePath();
	      
	    		  
	    	  Date curDate = new Date();
	    	  String date = new Integer(curDate.getYear() + 1900).toString() + new Integer(curDate.getMonth() +1).toString() + new Integer(curDate.getDate()).toString();
	    	  date = date + new Integer(curDate.getHours()).toString() + new Integer(curDate.getMinutes()).toString() + new Integer(curDate.getSeconds()).toString(); 
	    	  ZipConverter parser = new ZipConverter(dirName, dirName + date + "_zipFile.zip", true);
	    	  label = new JLabel("File " + date + "_zipFile.zip successfully created in ", JLabel.CENTER);
	    	  label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	    	  label2 = new JLabel(dirName, JLabel.CENTER);
	    	  label2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	    	  label3 = new JLabel("Please browse to this directory in the ECG Widget to upload zip file", JLabel.CENTER);
	    	  label3.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	    	  add(label);
	    	  add(label2);
	    	  add(label3);
	      }
	      catch (Exception e) { 
	    	  StringWriter s = new StringWriter();
	    	  PrintWriter p = new PrintWriter(s);
	    	  e.printStackTrace(p);
	    	 
	    	  label = new JLabel("Error: " + e.toString() + JLabel.CENTER + s.toString());
	    	  add(label); 
	     }
	      add(Box.createVerticalStrut(10));

	      go = new JButton("OK");
	      go.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  	      go.addActionListener(this);
  	      add(go);
	     }
	    else {
	    	 System.exit(0);
	     }
   }

  public void actionPerformed(ActionEvent e) {
	  System.exit(0);
     }
   
  public Dimension getPreferredSize(){
    return new Dimension(600, 100);
    }
    
  

  public static void main(String s[]) {
   JFrame frame = new JFrame("");
    FileChooser panel = new FileChooser();
    panel.setOpaque(true);
    frame.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
          }
        }
      );
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.setSize(panel.getPreferredSize());
    frame.setContentPane(panel);
    frame.pack();
    frame.setLocationRelativeTo(null);   
    frame.setVisible(true);
    }
}