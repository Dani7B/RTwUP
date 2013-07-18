package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * JTextPane modified to respond to Timer action
 * 
 * @author Daniele Morgantini
 */
public class AutoUpdateJPanel extends JTextPane implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6404697752474079672L;

	private Map<String,Map<String,Integer>> mappa;
	
	public AutoUpdateJPanel(Map<String,Map<String,Integer>> map) {
		super();
		mappa = map;
	}
	
	public void actionPerformed(ActionEvent e) {
		setText("");
		String[] initStyles = { "bold", "justified" };

		StyledDocument doc = getStyledDocument();
		addStylesToDocument(doc);
		Set<Map.Entry<String,Map<String,Integer>>> domains = mappa.entrySet();
		try {
			for(Map.Entry<String,Map<String,Integer>> entry : domains) {
				doc.insertString(doc.getLength(), entry.getKey() +" : ",
                        doc.getStyle(initStyles[0]));
				SortedHashMap sorted = new SortedHashMap(entry.getValue());
				doc.insertString(doc.getLength(), sorted.toString() +"\n",
                        doc.getStyle(initStyles[1]));
			}
		} catch (BadLocationException ble) {
		    System.err.println("Couldn't insert initial text into text pane.");
		}
	}
	
	public void addStylesToDocument(StyledDocument doc) {  
	     Style def = StyleContext.getDefaultStyleContext()  
	                             .getStyle(StyleContext.DEFAULT_STYLE);  
	     Style regular = doc.addStyle("regular", def);  
	     StyleConstants.setFontFamily(def, "Serif");  
	     StyleConstants.setFontSize(regular, 15);  
	     Style bold = doc.addStyle("bold", regular);  
	     StyleConstants.setBold(bold, true);  
	     StyleConstants.setForeground(bold, new Color(30, 144, 255));  
	     StyleConstants.setAlignment(bold, StyleConstants.ALIGN_LEFT);  
	     Style justified = doc.addStyle("justified", regular);  
	     StyleConstants.setAlignment(justified, StyleConstants.ALIGN_JUSTIFIED);  
	     StyleConstants.setForeground(justified, new Color(0,0,0));  
	}  
	
}