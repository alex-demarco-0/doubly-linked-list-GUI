/*
 * Corso di Programmazione Orientata agli Oggetti - Progetto LinkedList<T>
 * Autore: De Marco Alessandro (Numero Matricola: 190020)
 * Data: 01/2019
 */

package llp;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
class FrontEnd extends JFrame {
	private File saveFile=null;
	private JMenu editMenu, iterMenu;
	private JMenuItem newLL, open, save, saveAs, exit, getF, getL, addE,
	                  addF, addL, remE, remF, remL, clear, contains, sort,
	                  hash, openIter, openIterFrom, closeIter, about;
	private String title="Linked List Manager (Integer)";
	private List<Integer> list=null;
	private ListIterator<Integer> lit=null;
	private JPanel panel=null;
	private ItPanel itPanel=null;
	private JTextArea textArea;
	private JLabel size;
	private GetFrame gF=null;
	private AddRemoveFrame arF=null;
	private ContainsFrame cF=null;
	private boolean unsavedChanges=false;

	public FrontEnd() {
		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	        	if( confirmExit() ) {
	        		setVisible(false);
	        		dispose();
	        		System.exit(0);
	        	}
	        }
	    });
		ActionListener listener=new LLEventListener();
		/*System default ui
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(-1);
		}
		*/
		//menu bar
		JMenuBar menuBar=new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu=new JMenu("File"); //File menu
        menuBar.add(fileMenu);
        newLL=new JMenuItem("New");
        newLL.addActionListener(listener);
        fileMenu.add(newLL);
        fileMenu.addSeparator();
        open=new JMenuItem("Open");
        open.addActionListener(listener);
        fileMenu.add(open);
        save=new JMenuItem("Save");
        save.addActionListener(listener);
        fileMenu.add(save);
        saveAs=new JMenuItem("Save As...");
        saveAs.addActionListener(listener);
        fileMenu.add(saveAs);
        fileMenu.addSeparator();
        exit=new JMenuItem("Exit");
        exit.addActionListener(listener);
        fileMenu.add(exit);
        editMenu=new JMenu("Edit"); //Edit menu
        menuBar.add(editMenu);
        getF=new JMenuItem("Get First");
        getF.addActionListener(listener);
        editMenu.add(getF);
        getL=new JMenuItem("Get Last");
        getL.addActionListener(listener);
        editMenu.add(getL);
        editMenu.addSeparator();
        addE=new JMenuItem("Add Element");
        addE.addActionListener(listener);
        editMenu.add(addE);
        addF=new JMenuItem("Add First");
        addF.addActionListener(listener);
        editMenu.add(addF);
        addL=new JMenuItem("Add Last");
        addL.addActionListener(listener);
        editMenu.add(addL);
        editMenu.addSeparator();
        remE=new JMenuItem("Remove Element");
        remE.addActionListener(listener);
        editMenu.add(remE);
        remF=new JMenuItem("Remove First");
        remF.addActionListener(listener);
        editMenu.add(remF);
        remL=new JMenuItem("Remove Last");
        remL.addActionListener(listener);
        editMenu.add(remL);
        clear=new JMenuItem("Clear List");
        clear.addActionListener(listener);
        editMenu.add(clear);
        editMenu.addSeparator();
        contains=new JMenuItem("Contains Element");
        contains.addActionListener(listener);
        editMenu.add(contains);
        sort=new JMenuItem("Sort");
        sort.addActionListener(listener);
        editMenu.add(sort);
        hash=new JMenuItem("Generate HashCode");
        hash.addActionListener(listener);
        editMenu.add(hash);
        iterMenu=new JMenu("Iterator"); //Iterator menu
        menuBar.add(iterMenu);
        openIter=new JMenuItem("Open Iterator");
        openIter.addActionListener(listener);
        iterMenu.add(openIter);
        openIterFrom=new JMenuItem("Open Iterator From");
        openIterFrom.addActionListener(listener);
        iterMenu.add(openIterFrom);
        closeIter=new JMenuItem("Close Iterator");
        closeIter.addActionListener(listener);
        iterMenu.add(closeIter);
        JMenu helpMenu=new JMenu("Help"); //Help menu
        menuBar.add(helpMenu);
        about=new JMenuItem("About");
        about.addActionListener(listener);
        helpMenu.add(about);

        save.setEnabled(false);
        saveAs.setEnabled(false);
        editMenu.setEnabled(false);
        iterMenu.setEnabled(false);
        closeIter.setEnabled(false);

        setLocation(607, 225);
        setSize(705, 455);
        setResizable(false);
	}

	private class MainPanel extends JPanel {
		public MainPanel() {
			setLayout(null);
	        textArea=new JTextArea(1, 52);
	        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
	        textArea.setEditable(false);
	        textArea.setLineWrap(true);
	        textArea.setWrapStyleWord(true);
	        textArea.getCaret().setBlinkRate(0);
	        JScrollPane sp=new JScrollPane(textArea);
	        sp.setBounds(50, 50, 600, 300);
	        size=new JLabel("");
	        size.setBounds(50, 35, 200, 10);
	        add(sp);
	        add(size);
		}
	}

	private class ItPanel extends JPanel implements ActionListener {
		private JButton next, previous, remove, add, set;
		private JTextField addField, setField;
		private JTextArea itArea;
		private StringBuilder caret;
		private int cur;
		private int dir=0; //0=null, 1=forward, 2=backwards
		public ItPanel() {
			setLayout(null);
			itArea=new JTextArea(2, 52);
			itArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
			itArea.setEditable(false);
			JScrollPane sp=new JScrollPane(itArea);
			sp.setBounds(0, 0, 600, 54);
			next=new JButton("Next");
			next.addActionListener(this);
			next.setBounds(124, 74, 104, 30);
			previous=new JButton("Previous");
			previous.addActionListener(this);
			previous.setBounds(0, 74, 104, 30);
			remove=new JButton("Remove");
			remove.addActionListener(this);
			remove.setBounds(248, 74, 104, 30);
			add=new JButton("Add");
			add.addActionListener(this);
			add.setBounds(372, 74, 104, 30);
			set=new JButton("Set");
			set.addActionListener(this);
			set.setBounds(496, 74, 103, 30);
			addField=new JTextField(5);
			addField.setBounds(372, 114, 104, 30);
			setField=new JTextField(5);
			setField.setBounds(496, 114, 104, 30);
			add(sp);
			add(previous);
			add(next);
			add(remove);
			add(add);
			add(set);
			add(addField);
			add(setField);
			caret=new StringBuilder(list.toString().length()+50);
		}
		public void actionPerformed(ActionEvent e) {
			if( e.getSource()==next ) {
				cur=lit.next();
				//String snex=String.valueOf(cur);
				caret.delete(caret.length()-1, caret.length());
				for(int i=0; i<(String.valueOf(cur).length()+2); i++)
					caret.append(' ');
				caret.append('^');
				String slist=list.toString();
				itArea.setText(slist+" \n"+caret.toString());
				if( !previous.isEnabled() )
					previous.setEnabled(true);
				if( !lit.hasNext() )
					next.setEnabled(false);
				if( !remove.isEnabled() ) {
					remove.setEnabled(true);
					set.setEnabled(true);
					setField.setEditable(true);
				}
				itArea.setCaretPosition(((slist.length()-caret.length())<42) ? slist.length()+1 : caret.length()+42);
				textArea.setCaretPosition(caret.length()-1);
				dir=1;
			}
			else if( e.getSource()==previous ) {
				cur=lit.previous();
				//String sprev=""+cur;
				caret.delete(caret.length()-(String.valueOf(cur).length()+3), caret.length());
				caret.append('^');
				itArea.setText(list.toString()+" \n"+caret.toString());
				if( !next.isEnabled() )
					next.setEnabled(true);
				if( !lit.hasPrevious() )
					previous.setEnabled(false);
				if( !remove.isEnabled() ) {
					remove.setEnabled(true);
					set.setEnabled(true);
					setField.setEditable(true);
				}
				itArea.setCaretPosition((caret.length()<44) ? 0 : caret.length()-43);
				textArea.setCaretPosition(caret.length()-1);
				dir=2;
			}
			else if( e.getSource()==remove ) {
				remove.setEnabled(false);
				set.setEnabled(false);
				setField.setEditable(false);
				if( dir==1 ) {
					caret.delete(caret.length()-(String.valueOf(cur).length()+3), caret.length());
					caret.append('^');
				}
				lit.remove();
				textArea.setText(list.toString());
				itArea.setText(list.toString()+" \n"+caret.toString());
				textArea.setCaretPosition(caret.length()-1);
				size.setText("Size = "+list.size());
				if( !lit.hasNext() )
					next.setEnabled(false);
				if( !lit.hasPrevious() )
					previous.setEnabled(false);
				if( list.isEmpty() ) {
					getF.setEnabled(false);
					getL.setEnabled(false);
					contains.setEnabled(false);
				}
				unsavedChanges=true;
			}
			else if( e.getSource()==add ) {
				if( addField.getText().equals("") ) return;
				try {
					cur=Integer.parseInt(addField.getText());
					remove.setEnabled(false);
					set.setEnabled(false);
					setField.setEditable(false);
					lit.add(cur);
					caret.delete(caret.length()-1, caret.length());
					for(int i=0; i<(String.valueOf(cur).length()+2); i++)
						caret.append(' ');
					caret.append('^');
					textArea.setText(list.toString());
					itArea.setText(list.toString()+" \n"+caret.toString());
					textArea.setCaretPosition(caret.length()-1);
					size.setText("Size = "+list.size());
					if( !getF.isEnabled() ) {
						getF.setEnabled(true);
						getL.setEnabled(true);
						contains.setEnabled(true);
					}
					if( !previous.isEnabled() && lit.hasPrevious() )
						previous.setEnabled(true);
					unsavedChanges=true;
				}catch( NumberFormatException nfe ) {
					JOptionPane.showMessageDialog(null,"Wrong Input!");
				}finally { addField.setText(""); }
			}
			else if( e.getSource()==set ) {
				if( setField.getText().equals("") ) return;
				try {
					int tmp=Integer.parseInt(setField.getText());
					if( dir==1 ) {
						caret.delete(caret.length()-(String.valueOf(cur).length()+3), caret.length());
						cur=tmp;
						lit.set(cur);
						for(int i=0; i<((cur+"").length()+2); i++)
							caret.append(' ');
						caret.append('^');
					}
					else {
						cur=tmp;
						lit.set(cur);
					}
					textArea.setText(list.toString());
					itArea.setText(list.toString()+" \n"+caret.toString());
					itArea.setCaretPosition(caret.length());
					textArea.setCaretPosition(caret.length()-1);
					unsavedChanges=true;
				}catch( NumberFormatException nfe ) {
					JOptionPane.showMessageDialog(null,"Wrong Input!");
				}finally { setField.setText(""); }
			}
		}
		public void refresh(int pos) {
			if( !lit.hasPrevious() )
				previous.setEnabled(false);
			else previous.setEnabled(true);
			if( !lit.hasNext() )
				next.setEnabled(false);
			else next.setEnabled(true);
			remove.setEnabled(false);
			set.setEnabled(false);
			setField.setEditable(false);
			cur=dir=0;
			caret.delete(0, caret.length());
			ListIterator<Integer> it=list.listIterator();
			for(int i=0; i<pos; i++) {
				String n=String.valueOf(it.next());
				for(int s=0; s<(n.length()+2); s++)
					caret.append(' ');
			}
			caret.append("^");
			itArea.setText(list.toString()+" \n"+caret.toString());
			itArea.setCaretPosition(((list.toString().length()-caret.length())<42) ? list.toString().length() : caret.length()+42);
			textArea.setCaretPosition(caret.length()-1);
			textArea.getCaret().setVisible(true);
			addField.setText("");
			setField.setText("");
		}
	}

	private class GetFrame extends JFrame {
		private JTextField tf;
		private JLabel label;
		public GetFrame() {
			setTitle("");
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
   	        addWindowListener( new WindowAdapter() {
   		            public void windowClosing(WindowEvent e) {
   		        	    setVisible(false);
   		            }
   		        } );
			JPanel p=new JPanel();
			p.setLayout(null);
			p.add(label=new JLabel(""));
			label.setBounds(75, 50, 100, 10);
			p.add(tf=new JTextField("", 12));
			tf.setBounds(160, 46, 150, 20);
			tf.setEditable(false);
			add(p);
			setLocation(757, 407);
   		    setSize(400, 150);
   		    setResizable(false);
		}
		public void set(boolean b, int x) {
			tf.setText(""+x);
			if( b ) {
				setTitle("Get First");
				label.setText("First Element:");
			}
			else {
				setTitle("Get Last");
				label.setText("Last Element:");
			}
		}
	}

	private class AddRemoveFrame extends JFrame {
		private JTextField tf;
		private JLabel label;
		private int mode;
		public AddRemoveFrame() {
			setTitle("");
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
   	        addWindowListener( new WindowAdapter() {
   		            public void windowClosing(WindowEvent e) {
   		        	    setVisible(false);
   		        	    tf.setText("");
   		            }
   		        } );
   	        JPanel p=new JPanel();
   	        p.setLayout(null);
			p.add(label=new JLabel(""));
			label.setBounds(55, 50, 100, 10);
			label.setHorizontalAlignment(JLabel.RIGHT);
			p.add(tf=new JTextField("", 12));
			tf.setBounds(160, 46, 150, 20);
			tf.addActionListener(
					(ActionEvent e)->{
						if( tf.getText().equals("") ) return;
						try {
							if( mode==1 ) {
								list.addFirst(Integer.parseInt(tf.getText()));
								menuE();
							}
							else if( mode==2 || mode==3 ) {
								list.addLast(Integer.parseInt(tf.getText()));
								menuE();
							}
							else {
								list.remove(Integer.parseInt(tf.getText()));
								if( list.isEmpty() )
									menuD();
							}
							textArea.setText(list.toString());
							if( mode==1 || mode==4 )
								textArea.setCaretPosition(0);
							size.setText("Size = "+list.size());
							unsavedChanges=true;
							setVisible(false);
						}catch( NumberFormatException nfe ) {
							JOptionPane.showMessageDialog(null,"Wrong Input!");
						}finally { tf.setText(""); }
					});
			add(p);
			setLocation(757, 407);
   		    setSize(400, 150);
   		    setResizable(false);
		}
		public void setMode(int i) {
			if( i==1 ) {
				setTitle("Add First");
				label.setText("First Element:");
			}
			else if( i==2 ) {
				setTitle("Add Last");
				label.setText("Last Element:");
			}
			else if( i==3 ) {
				setTitle("Add");
				label.setText("Element:");
			}
			else if( i==4 ) {
				setTitle("Remove");
				label.setText("Element:");
			}
			else throw new IllegalArgumentException();
			mode=i;
		}
	}

	private class ContainsFrame extends JFrame {
		private JTextField tf;
		private JLabel label;
		public ContainsFrame() {
			setTitle("Contains Element");
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
   	        addWindowListener( new WindowAdapter() {
   		            public void windowClosing(WindowEvent e) {
   		        	    setVisible(false);
   		        	    tf.setText("");
   		        	    label.setText(": ");
   		            }
   		        } );
   	        JPanel p=new JPanel();
   	        p.setLayout(null);
   	        JLabel cont=new JLabel("Contains");
			p.add(cont);
			cont.setBounds(74, 50, 100, 10);
			p.add(tf=new JTextField("", 10));
			tf.setBounds(130, 46, 150, 20);
			p.add(label=new JLabel(": "));
			label.setBounds(284, 50, 100, 10);
			tf.addActionListener(
					(ActionEvent e)->{
						if( tf.getText().equals("") ) return;
						try {
							label.setText(": "+list.contains(Integer.parseInt(tf.getText())));
						}catch( NumberFormatException nfe ) {
							JOptionPane.showMessageDialog(null,"Wrong Input!");
							tf.setText("");
							label.setText(": ");
						}
					});
			add(p);
			setLocation(757, 407);
   		    setSize(400, 150);
   		    setResizable(false);
		}
	}

	private class HashFrame extends JFrame {
		private JTextField hc;
		public HashFrame() {
			setTitle("HashCode");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel p=new JPanel();
			p.setLayout(null);
			p.add(hc=new JTextField(""+list.hashCode(), 15));
			hc.setBounds(50, 45, 300, 20);
			hc.setEditable(false);
			add(p);
			setLocation(757, 407);
   		    setSize(400, 150);
   		    setResizable(false);
		}
	}

	private class ItFrame extends JFrame {
		private JLabel label;
		private JTextField ind;
		public ItFrame() {
			setTitle("Open Iterator From");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel p=new JPanel();
			p.setLayout(null);
			p.add(label=new JLabel("Index:"));
			label.setBounds(70, 44, 40, 20);
			p.add(ind=new JTextField("", 15));
			ind.addActionListener(
					(ActionEvent e)->{
						if( ind.getText().equals("") ) return;
						try {
							int pos=Integer.parseInt(ind.getText());
							if( pos<0 || pos>list.size() ) throw new NumberFormatException();
							lit=list.listIterator(pos);
							setVisible(false);
							if( itPanel==null )
								itPanel=new ItPanel();
							panel.add(itPanel);
							itPanel.setBounds(50, 380, 600, 150);
							itPanel.refresh(pos);
							FrontEnd.this.setSize(705, 630);
							panel.validate();
							panel.repaint();
							addE.setEnabled(false);
							addF.setEnabled(false);
							addL.setEnabled(false);
							openIter.setEnabled(false);
							closeIter.setEnabled(true);
							if( !list.isEmpty() ) {
								remE.setEnabled(false);
								remF.setEnabled(false);
								remL.setEnabled(false);
								clear.setEnabled(false);
								sort.setEnabled(false);
								openIterFrom.setEnabled(false);
							}	
							dispose();
						}catch( NumberFormatException nfe ) {
							JOptionPane.showMessageDialog(null,"Wrong Input!");
							ind.setText("");
						}
					});
			ind.setBounds(110, 45, 200, 20);
			add(p);
			setLocation(757, 407);
   		    setSize(400, 150);
   		    setResizable(false);
		}
	}

	private void menuE() {
		getF.setEnabled(true);
        getL.setEnabled(true);
        remE.setEnabled(true);
        remF.setEnabled(true);
        remL.setEnabled(true);
        clear.setEnabled(true);
        contains.setEnabled(true);
        sort.setEnabled(true);
        openIterFrom.setEnabled(true);
	}

	private void menuD() {
		getF.setEnabled(false);
        getL.setEnabled(false);
        remE.setEnabled(false);
        remF.setEnabled(false);
        remL.setEnabled(false);
        clear.setEnabled(false);
        contains.setEnabled(false);
        sort.setEnabled(false);
        openIterFrom.setEnabled(false);
	}

	private boolean confirmExit() {
		if( !unsavedChanges ) return true;
		String[] opt= {"Save", "Don't Save", "Cancel"};
		int option=JOptionPane.showOptionDialog(null, "Are you sure you want to exit?\nAny unsaved data will be lost", "Confirm Exit",
				   JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, opt, opt[0]);
		switch(option) {
	    case -1: return false; // window X
	    case 0:                // save and quit
			try {
				if( saveFile!=null ) {
					ObjectOutputStream oos=new ObjectOutputStream( new FileOutputStream( saveFile ));
					oos.writeObject(list);
					oos.close();
					unsavedChanges=false;
					return true;
				}
				JFileChooser jfc=new JFileChooser();
				jfc.setFileFilter(new FileNameExtensionFilter("LIST File","list"));
				if( jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION ) {
					saveFile=jfc.getSelectedFile();
					ObjectOutputStream oos=new ObjectOutputStream( new FileOutputStream( saveFile ));
					oos.writeObject(list);
					oos.close();
					unsavedChanges=false;
				}
				else return false;
			}catch( Exception exc ) {
				JOptionPane.showMessageDialog(null,"Failed to save "+saveFile.getAbsolutePath(),"Error",JOptionPane.ERROR_MESSAGE);
				exc.printStackTrace();
				return false;
			}
	    	return true;
	    case 1: return true;   // quit without saving
	    case 2: return false;  // cancel
	    default: throw new IllegalArgumentException();
	    }
	}

	private class LLEventListener implements ActionListener {
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			if( e.getSource()==newLL ) {
				if( unsavedChanges )
					if( JOptionPane.showConfirmDialog(null, "Any unsaved data will be lost. Continue?", "Warning", JOptionPane.YES_NO_OPTION)
						==JOptionPane.NO_OPTION )
						return;
				list=new LinkedList<>();
				if( panel==null )
				    add(panel=new MainPanel());
			    save.setEnabled(true);
		        saveAs.setEnabled(true);
		        editMenu.setEnabled(true);
		        iterMenu.setEnabled(true);
		        if( closeIter.isEnabled() ) {
					panel.remove(itPanel);
					textArea.getCaret().setVisible(false);
					panel.validate();
					panel.repaint();
					setSize(705, 455);
					addE.setEnabled(true);
					addF.setEnabled(true);
					addL.setEnabled(true);
					openIter.setEnabled(true);
			        closeIter.setEnabled(false);
				}
		        menuD();
				setTitle(title+" - New LinkedList");
		        textArea.setText("[]");
		        size.setText("Size = 0");
				validate();
				saveFile=null;
		        unsavedChanges=true;
			}
			else if( e.getSource()==open ) {
				if( unsavedChanges )
					if( JOptionPane.showConfirmDialog(null, "Any unsaved data will be lost. Continue?", "Warning", JOptionPane.YES_NO_OPTION)
						==JOptionPane.NO_OPTION )
						return;
				JFileChooser jfc=new JFileChooser();
				jfc.setFileFilter(new FileNameExtensionFilter("LIST File","list"));
				try {
					if( jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION ) {
						if( !jfc.getSelectedFile().exists() )
							JOptionPane.showMessageDialog(null,"File not found!");
						else {
							saveFile=jfc.getSelectedFile();
							try {
								ObjectInputStream ois=new ObjectInputStream( new FileInputStream( saveFile ) );
								try{
									list=(LinkedList<Integer>)ois.readObject();
									if( panel==null )
									    add(panel=new MainPanel());
									save.setEnabled(true);
							        saveAs.setEnabled(true);
							        editMenu.setEnabled(true);
							        iterMenu.setEnabled(true);
									if( closeIter.isEnabled() ) {
										panel.remove(itPanel);
										textArea.getCaret().setVisible(false);
										panel.validate();
										panel.repaint();
										setSize(705, 455);
										addE.setEnabled(true);
										addF.setEnabled(true);
										addL.setEnabled(true);
										openIter.setEnabled(true);
							            closeIter.setEnabled(false);
									}
									if( list.isEmpty() )
							        	menuD();
							        else menuE();
									setTitle(title+" - "+saveFile.getAbsolutePath());
									textArea.setText(list.toString());
									textArea.setCaretPosition(0);
									size.setText("Size = "+list.size());
									validate();
									unsavedChanges=false;
								}
								catch( ClassNotFoundException | ClassCastException ex ) { throw new IOException(); }
								finally { ois.close(); }
							}catch(IOException ioe) {
								JOptionPane.showMessageDialog(null,"Failed to open "+saveFile.getAbsolutePath()+". File is corrupted.",
										"Error",JOptionPane.ERROR_MESSAGE);
							}
						}						
					}
				}catch( Exception exc ) { exc.printStackTrace(); }
			}
			else if( e.getSource()==save ) {
				if( unsavedChanges ) {
					try {
						if( saveFile!=null ) {
							ObjectOutputStream oos=new ObjectOutputStream( new FileOutputStream( saveFile ));
							oos.writeObject(list);
							oos.close();
							unsavedChanges=false;
							return;
						}
						JFileChooser jfc=new JFileChooser();
						jfc.setFileFilter(new FileNameExtensionFilter("LIST File","list"));
						if( jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION ) {
							saveFile=jfc.getSelectedFile();
							ObjectOutputStream oos=new ObjectOutputStream( new FileOutputStream( saveFile ));
							oos.writeObject(list);
							oos.close();
							setTitle(title+" - "+saveFile.getAbsolutePath());
							unsavedChanges=false;
						}
					}catch( Exception exc ) {
						JOptionPane.showMessageDialog(null,"Failed to save "+saveFile.getAbsolutePath(),"Error",JOptionPane.ERROR_MESSAGE);
						exc.printStackTrace();
					}
				}
			}
			else if( e.getSource()==saveAs ) {
				JFileChooser jfc=new JFileChooser();
				jfc.setFileFilter(new FileNameExtensionFilter("LIST File","list"));
				try {
					if( jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION ) {
						saveFile=jfc.getSelectedFile();
						ObjectOutputStream oos=new ObjectOutputStream( new FileOutputStream( saveFile ));
						oos.writeObject(list);
						oos.close();
						setTitle(title+" - "+saveFile.getAbsolutePath());
						unsavedChanges=false;
					}
				}catch( Exception exc ) {
					JOptionPane.showMessageDialog(null,"Failed to save "+saveFile.getAbsolutePath(),"Error",JOptionPane.ERROR_MESSAGE);
	  				exc.printStackTrace();
	  			}
			}
			else if( e.getSource()==exit ) {
				if( confirmExit() ) {
					setVisible(false);
	        		dispose();
					System.exit(0);
				}
			}
			else if( e.getSource()==getF ) {
				if( gF==null )
					gF=new GetFrame();
				gF.set(true, list.getFirst());
				gF.setVisible(true);
			}
			else if( e.getSource()==getL ) {
				if( gF==null )
					gF=new GetFrame();
				gF.set(false, list.getLast());
				gF.setVisible(true);
			}
			else if( e.getSource()==addE ) {
				if( arF==null )
					arF=new AddRemoveFrame();
				arF.setMode(3);
				arF.setVisible(true);
			}
			else if( e.getSource()==addF ) {
				if( arF==null )
					arF=new AddRemoveFrame();
				arF.setMode(1);
				arF.setVisible(true);
			}
			else if( e.getSource()==addL ) {
				if( arF==null )
					arF=new AddRemoveFrame();
				arF.setMode(2);
				arF.setVisible(true);
			}
			else if( e.getSource()==remE ) {
				if( arF==null )
					arF=new AddRemoveFrame();
				arF.setMode(4);
				arF.setVisible(true);
			}
			else if( e.getSource()==remF ) {
				list.removeFirst();
				if( list.isEmpty() )
					menuD();
				textArea.setText(list.toString());
				textArea.setCaretPosition(0);
				size.setText("Size = "+list.size());
				unsavedChanges=true;
			}
			else if( e.getSource()==remL ) {
				list.removeLast();
				if( list.isEmpty() )
		        	menuD();
				textArea.setText(list.toString());
				size.setText("Size = "+list.size());
				unsavedChanges=true;
			}
			else if( e.getSource()==clear ) {
				list.clear();
				menuD();
				textArea.setText("[]");
				size.setText("Size = 0");
				unsavedChanges=true;
			}
			else if( e.getSource()==contains ) {
				if( cF==null )
					cF=new ContainsFrame();
				cF.setVisible(true);
			}
			else if( e.getSource()==sort ) {
				JFrame f=new JFrame();
				f.getContentPane().setLayout(new BorderLayout());
				f.getContentPane().add(new JLabel("Sorting..."), BorderLayout.CENTER);
				f.setLocation(840, 407);
				f.setSize(200, 150);
				f.setResizable(false);
				f.setVisible(true);
				List.sort(list, Comparator.naturalOrder());
				f.setVisible(false);
				textArea.setText(list.toString());
				textArea.setCaretPosition(0);
				unsavedChanges=true;
				f.dispose();
			}
			else if( e.getSource()==hash )
				new HashFrame().setVisible(true);
			else if( e.getSource()==openIter ) {
				lit=list.listIterator();
				if( itPanel==null )
					itPanel=new ItPanel();
				panel.add(itPanel);
				itPanel.setBounds(50, 380, 600, 150);
				itPanel.refresh(0);
				setSize(705, 630);
				panel.validate();
				panel.repaint();
				addE.setEnabled(false);
				addF.setEnabled(false);
				addL.setEnabled(false);
				openIter.setEnabled(false);
				closeIter.setEnabled(true);
				if( !list.isEmpty() ) {
					remE.setEnabled(false);
					remF.setEnabled(false);
					remL.setEnabled(false);
					clear.setEnabled(false);
					sort.setEnabled(false);
					openIterFrom.setEnabled(false);
				}				
			}
			else if( e.getSource()==openIterFrom )
				new ItFrame().setVisible(true);
			else if( e.getSource()==closeIter ) {
				panel.remove(itPanel);
				textArea.getCaret().setVisible(false);
				panel.validate();
				panel.repaint();
				setSize(705, 455);
				lit=null;
				addE.setEnabled(true);
				addF.setEnabled(true);
				addL.setEnabled(true);
				openIter.setEnabled(true);
				closeIter.setEnabled(false);
				if( !list.isEmpty() ) {
					remE.setEnabled(true);
					remF.setEnabled(true);
					remL.setEnabled(true);
					clear.setEnabled(true);
					sort.setEnabled(true);
					openIterFrom.setEnabled(true);
				}
			}
			else if( e.getSource()==about )
				JOptionPane.showMessageDialog(null, "Linked List Manager (Beta)\nAuthor: Alessandro De Marco\n[190020] (LT Ing. Inf. 2° anno)",
						"About", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

public class LinkedListGUI {
	public static void main(String[] args) {
		EventQueue.invokeLater( new Runnable() {
			public void run() {
				new FrontEnd().setVisible(true);
			}
		});
	}
}
