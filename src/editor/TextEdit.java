package editor;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.undo.UndoManager;

import converter.TimeFormat;

import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;

import treeNodes.CreateChildNodes;
import treeNodes.FileNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TextEdit extends JFrame implements ActionListener {
    private static boolean ALLOW_REPLACE = false;
    private static boolean PAR_USED = false;
    private static String SPACE_AMOUNT = "";
    private static JScrollPane scroll;
    private static JTextPane area;
    private static JFrame frame;
    private static int returnValue = 0;
    private static Boolean saved = true;
    private static String ingest = "";
    private static JFileChooser jfc;
    private static String saved_file = null;
    private static String current_name = "Untitled";
    private static UndoManager manager;
    // private static Boolean checked = false;
    // private static int startIndex = 0;
    private static int START_NUM = 0;
    private static boolean COLUMN_CARET = false;
    private int start;
    private int end;
    private int linenum = 1;
    private int columnnum = 1;

    private DefaultMutableTreeNode root;

    private DefaultTreeModel treeModel;

    private JPanel panel;

    private JTree tree;

    private Path fileRoot;
    private JScrollPane scrollPane;
    private JPanel statusPanel;
    private JLabel statusLabel;

    private JSplitPane FMSplitPane;
    private Boolean SHOW_FILEMANAGER = false;
    private String LANGUAGE = null;

    private String getLang() {
        return LANGUAGE;
    }

    private void setLang(String LANGUAGE, Boolean refresh) {
        this.LANGUAGE = LANGUAGE;
        if(refresh) {
            
        }
    }

    public TextEdit() { 
        run();
    }

    private int findLastNonWordChar(String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar(String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

    StyleContext cont = StyleContext.getDefaultStyleContext();

    private DefaultStyledDocument doc = new DefaultStyledDocument() {
        
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            cont = StyleContext.getDefaultStyleContext();
        SimpleAttributeSet purple = new SimpleAttributeSet();
        StyleConstants.setForeground(purple, new Color(128,0,128));
        StyleConstants.setBold(purple, true);
            super.insertString(offset, str, a);

            String text = getText(0, getLength());
            int before = findLastNonWordChar(text, offset);
            if (before < 0) before = 0;
            int after = findFirstNonWordChar(text, offset + str.length());
            int wordL = before;
            int wordR = before;



            while (wordR <= after) {
                // System.out.println("Right:\s"+wordL+",\sLeft:\s"+after);
                // Pattern pattern = Pattern.compile (".*\\\". (.*)\\\".*");
                // System.out.println(text.substring(before, after));
                // Matcher matcher = pattern.matcher(text.substring(wordL, wordR));
                // if(matcher.find()) {
                //     System.out.println(matcher.group(1));
                // }
                if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                    System.out.println(text.substring(wordL, wordR));
                    if (text.substring(wordL, wordR).matches("(\\W)*(class|private|public|protected|final|static|null|void|new|try|catch|if|while|else|int|boolean|String|float)")) {
                        setCharacterAttributes(wordL, wordR - wordL, purple, false);
                    // } else if(text.substring(wordL, wordR).matches("(\\W)*(if|while|else|try|catch|new|(|))")) {
                    //     setCharacterAttributes(wordL, wordR - wordL, , false);
                    // // } else if (text.substring(wordL, wordR).matches("(\\W)*(int|boolean|String|float|void)")) {
                    // //     setCharacterAttributes(wordL, wordR - wordL, attrGreen, false);
                    // } else {
                    //     setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                    // }
                    } else {
                        setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                    }
                    wordL = wordR;
                }
                wordR++;
            }
        }
    };
    
    private int REPEAT_NUM;
    private boolean MAKE_SPACE;
    private boolean ENTER_PRESSED = false;
    private String prefix;
    private char LAST_KEY;
    private JPanel lineNumbers;
    private String[] lines;
    private JList list;
    private Vector<Integer> v;
    private JPanel scrollPane2;
    private JScrollPane numbersScrollPane;
    
    private int lineChange = 0;
    private int maxNumSpace = 0;
    protected boolean MAKE_NUMSCROLL_MAX;
    
    
    private static int getLineOfOffset(JTextComponent comp, int offset) throws BadLocationException {
        Document doc = comp.getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
        } else {
            Element map = doc.getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    private static int getLineStartOffset(JTextComponent comp, int line) throws BadLocationException {
        Element map = comp.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    private static int getLineEndOffset(JTextComponent comp, int line) throws BadLocationException {
        Element map = comp.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getEndOffset();
        }
    }

    
    SimpleAttributeSet attrBlack = null;

    public void run() {
        cont = StyleContext.getDefaultStyleContext();
        attrBlack = new SimpleAttributeSet();
        StyleConstants.setForeground(attrBlack, Color.BLACK);
        StyleConstants.setBold(attrBlack, false);
        //attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
        AttributeSet attrDarkBlue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE.darker());
        AttributeSet attrPurple = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(106, 13, 173));
        AttributeSet attrGreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(76, 198, 150));
        AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
        AttributeSet attrWhite = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 255, 255));

        // AttributeSet attrDarkBlue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE.darker());
        // AttributeSet attrPurple = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(106, 13, 173));
        // AttributeSet attrGreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(76, 198, 150));
        
        // AttributeSet attrWhite = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 255, 255));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //     UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        // for (UIManager.LookAndFeelInfo look : looks) {
        //     System.out.println(look.getClassName());
        // }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(TextEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame = new JFrame();
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        panel = new JPanel(new BorderLayout());
        // panel2 = new JPanel();
        FMSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        FMSplitPane.setBorder(null);
        area = new JTextPane();
        scrollPane2 = new JPanel(new BorderLayout());
        scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBorder(null);

        lineNumbers = new JPanel(new GridLayout(0, 1, 0, 0));
        lines = area.getText().split("\n");
        v = new Vector<Integer>();
        
        for(int i = 0; i <= lines.length; i++) {
            v.add(i, i + 1);
        }

        scrollPane2.add(scroll, BorderLayout.CENTER);
        
        area.setBorder(BorderFactory.createCompoundBorder(
                FMSplitPane.getBorder(),
                BorderFactory.createEmptyBorder(0, 4, 0, 2)
        ));
    
        // panel2.setBorder(null);
        FMSplitPane.setDividerSize(0);
        //JPanel panel = new JPanel(new BorderLayout(5, 5));
        

        // Set the look-and-feel (LNF) of the application
            // Try to default to whatever the host system prefers
        
//         UIDefaults defaults = UIManager.getDefaults();
//         Enumeration<Object> keysEnumeration = defaults.keys();
//         ArrayList<Object> keysList = Collections.list(keysEnumeration);
//         for (Object key : keysList)
//         {
//             System.out.println(key);
//         }
//         UIManager.put( "control", new Color( 128, 128, 128) );
//   UIManager.put( "info", new Color(128,128,128) );
//   UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
//   UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
//   UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
//   UIManager.put( "nimbusFocus", new Color(115,164,209) );
//   UIManager.put( "nimbusGreen", new Color(176,179,50) );
//   UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
//   UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
//   UIManager.put( "nimbusOrange", new Color(191,98,4) );
//   UIManager.put( "nimbusRed", new Color(169,46,34) );
//   UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
//   UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
//   UIManager.put( "text", new Color( 230, 230, 230) );

// try {
//     UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
// } catch (ClassNotFoundException e) {
//     // TODO Auto-generated catch block
//     e.printStackTrace();
// } catch (InstantiationException e) {
//     // TODO Auto-generated catch block
//     e.printStackTrace();
// } catch (IllegalAccessException e) {
//     // TODO Auto-generated catch block
//     e.printStackTrace();
// } catch (UnsupportedLookAndFeelException e) {
//     // TODO Auto-generated catch block
//     e.printStackTrace();
// }
//   try {
//     for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//       //if ("Nimbus".equals(info.getName())) {
//         //   javax.swing.UIManager.setLookAndFeel(info.getClassName());
//           break;
//     //   }
//     }
//   } catch (ClassNotFoundException e) {
//     e.printStackTrace();
//   } catch (InstantiationException e) {
//     e.printStackTrace();
//   } catch (IllegalAccessException e) {
//     e.printStackTrace();
//   } catch (javax.swing.UnsupportedLookAndFeelException e) {
//     e.printStackTrace();
//   } catch (Exception e) {
//     e.printStackTrace();
//   }
  // Show your JFrame
  

            // Set attributes of the app window
        

        area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saved = false;
                // refreshLines();
            }
        
            @Override
            public void removeUpdate(DocumentEvent e) {
                saved = false;
                // refreshLines();
            }
        
            @Override
            public void changedUpdate(DocumentEvent e) {
                // refreshLines();
            } 
        });
        
        FMSplitPane.setRightComponent(scrollPane2);
        // panel.add(scroll, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);

            // Build the menu
        JMenuBar menu_main = new JMenuBar();

            JMenu menu_file = new JMenu("File");
            JMenu menu_edit = new JMenu("Edit");
            JMenu menu_view = new JMenu("View");
            JMenu menu_format = new JMenu("Format");

        // menu_file
            JMenuItem menuitem_new = new JMenuItem("New");
        JMenuItem menuitem_open = new JMenuItem("Open");
        JMenuItem menuitem_save = new JMenuItem("Save");
        JMenuItem menuitem_saveas = new JMenuItem("Save As");
        JMenuItem menuitem_exit = new JMenuItem("Exit");

            menuitem_new.addActionListener(this);
        menuitem_open.addActionListener(this);
        menuitem_save.addActionListener(this);
        menuitem_saveas.addActionListener(this);
        menuitem_exit.addActionListener(this);

        menu_file.add(menuitem_new);
        menu_file.add(menuitem_open);
        menu_file.add(menuitem_save);
        menu_file.add(menuitem_saveas);
        menu_file.add(menuitem_exit);

        // menu_edit
            JMenuItem menuitem_undo = new JMenuItem("Undo");
        JMenuItem menuitem_redo = new JMenuItem("Redo");
        JMenuItem menuitem_cut = new JMenuItem("Cut");
        JMenuItem menuitem_copy = new JMenuItem("Copy");
        JMenuItem menuitem_paste = new JMenuItem("Paste");
        JMenuItem menuitem_find = new JMenuItem("Find");
        JMenu submenu_insert = new JMenu("Insert");

        // SubMenu Insert
        JMenuItem menu_insert_date_time_short = new JMenuItem("Date and Time (short)");
        JMenuItem menu_insert_date_time_long = new JMenuItem("Date and Time (long)");
        JMenuItem menu_insert_pattern = new JMenuItem("Pattern");

        JCheckBoxMenuItem menuitem_startendSelect = new JCheckBoxMenuItem("Start/End Select");
        JCheckBoxMenuItem menuitem_columnsCaret = new JCheckBoxMenuItem("Select in Columns Mode");

        menuitem_undo.addActionListener(this);
        menuitem_redo.addActionListener(this);
        menuitem_cut.addActionListener(this);
        menuitem_copy.addActionListener(this);
        menuitem_paste.addActionListener(this);
        menuitem_find.addActionListener(this);
        menuitem_startendSelect.addActionListener(this);
        menuitem_columnsCaret.addActionListener(this);
        submenu_insert.addActionListener(this);
        menu_insert_date_time_short.addActionListener(this);
        menu_insert_date_time_long.addActionListener(this);
        menu_insert_pattern.addActionListener(this);

        menu_edit.add(menuitem_undo);
        menu_edit.add(menuitem_redo);
        menu_edit.addSeparator();
        menu_edit.add(menuitem_cut);
        menu_edit.add(menuitem_copy);
        menu_edit.add(menuitem_paste);
        menu_edit.add(menuitem_find);
        menu_edit.add(menuitem_startendSelect);
        menu_edit.add(menuitem_columnsCaret);
        submenu_insert.add(menu_insert_date_time_short);
        submenu_insert.add(menu_insert_date_time_long);
        submenu_insert.add(menu_insert_pattern);
        menu_edit.addSeparator();
        menu_edit.add(submenu_insert);

        // menu_view
            JCheckBoxMenuItem menuitem_file_manager = new JCheckBoxMenuItem("Show File Manager");

        menuitem_file_manager.addActionListener(this);
        menu_view.add(menuitem_file_manager);

        // menu_format
            JCheckBoxMenuItem menuitem_wordwrap = new JCheckBoxMenuItem("Word-Wrap");
        JMenuItem menuitem_font = new JMenuItem("Font");
        JMenu submenu_language = new JMenu("Language");

        // SubMenu Language

        JMenuItem menuitem_NTF = new JMenuItem("Normal Text File");
        JMenuItem menuitem_Java = new JMenuItem("Java");

        menuitem_wordwrap.addActionListener(this);
        menuitem_font.addActionListener(this);
        menuitem_NTF.addActionListener(this);
        menuitem_Java.addActionListener(this);
        submenu_language.addActionListener(this);

        menu_format.add(menuitem_wordwrap);
        menu_format.add(menuitem_font);
        submenu_language.add(menuitem_NTF);
        submenu_language.add(menuitem_Java);
        menu_format.add(submenu_language);

        // menu_main

        menu_main.add(menu_file);
        menu_main.add(menu_edit);
        menu_main.add(menu_view);
        menu_main.add(menu_format);
        

        // Initialize JFileChooser
        //TODO: Change JFileChooser to FileDialog
        Settings settings = new Settings();
        jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Save As");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // FileDialog fd = new FileDialog(frame, "Title");
        // fd.show();
            frame.setJMenuBar(menu_main);
            updateTitle();
            area.setFont(new Font(settings.getFont("Font Name"), Integer.parseInt(settings.getFont("Font Style")), Integer.parseInt(settings.getFont("Font Size"))));
            
            //area.setFont(new Font("Consolas", Font.PLAIN, 15));
            manager = new UndoManager();
            area.getDocument().addUndoableEditListener(manager);
            area.addCaretListener(new CaretListener() {

                @Override
                public void caretUpdate(CaretEvent e) {
                    
                    // numbersScrollPane.getVerticalScrollBar().addAdjustmentListener(numbersScrollAdjustment);
                    if(ALLOW_REPLACE) {
                        ALLOW_REPLACE = false;
                    }
                    JTextPane edit = (JTextPane)e.getSource();
                    // String[] textLines = area.getText().substring(0, area.getSelectionStart()).split("\n");
                    // int currentLineNumber = textLines.length;
                    // int currentColumnIndex = textLines[textLines.length-1].length();
                    // int caretpos = area.getCaretPosition();

                    // int linenum = 0;
                    // int columnnum = 0;
                    // linenum = textLines.length;
                    // columnnum = textLines[textLines.length-1].length();
                    // linenum++;
                    // columnnum++;
                    


                        int caretpos = edit.getCaretPosition();
                        // linenum = edit.getLineOfOffset(caretpos);
                        try {
                            linenum = getLineOfOffset(edit, caretpos);
                            columnnum = caretpos - getLineStartOffset(edit, linenum);
                            linenum++;
                        } catch (BadLocationException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        // Boolean isLine = false;
                        // try { v.get(linenum); isLine = true; } catch (ArrayIndexOutOfBoundsException ex) { isLine = false; }
                        // System.out.println("Line: " + linenum + "\tisLine 0 = true, 1 = false: " + isLine);
                        // if(isLine) {
                            // System.out.println(true);
                        // System.out.println(lineChange > linenum);
                        
                            
                        // }

                        if(e.getDot() == e.getMark()) {
                            statusLabel.setText("Ln " + linenum + ", Col " + columnnum);
                        } else if(e.getDot() > e.getMark()) {
                            statusLabel.setText("Ln " + linenum + ", Col " + columnnum + " (" + String.valueOf(e.getDot() - e.getMark() + " selected)"));
                        } else if(e.getDot() < e.getMark()) {
                            statusLabel.setText("Ln " + linenum + ", Col " + columnnum + " (" + String.valueOf(e.getMark() - e.getDot() + " selected)"));
                        }
                        

                        lineChange = linenum;
                }
                
            });
            area.addHierarchyBoundsListener(new HierarchyBoundsListener() {

                @Override
                public void ancestorMoved(HierarchyEvent e) {
                    
                }

                @Override
                public void ancestorResized(HierarchyEvent e) {
                    try {
                        scrollPane.setPreferredSize(new Dimension(200, frame.getHeight()));
                    } catch (NullPointerException ex) {}
                }
                
            });
            
            area.addKeyListener(new KeyListener() {
                StyledDocument document = (StyledDocument) area.getDocument();
                
                @Override
                public void keyTyped(KeyEvent e) {
                    document = (StyledDocument) area.getDocument();
                    if(e.getKeyChar() == ')' || e.getKeyChar() == '}' || e.getKeyChar() == ']') {
                        try {
                        // if(!PAR_USED) {
                        int pos2 = area.getCaretPosition();
                        String before = area.getText().substring(pos2 - 1, pos2);
                        String after = area.getText().substring(pos2, pos2 + 1);
                        
                        if((after.equals(")") || after.equals("}") || after.equals("]")) &&
                        (before.equals("(") || before.equals("{") || before.equals("["))) {
                            // System.out.println(String.valueOf(LAST_KEY) + " " + after);
                            if(String.valueOf(LAST_KEY).equals(after)) {
                                if(ALLOW_REPLACE) {
                                try {
                                    document.remove(pos2, 1);
                                    // area.setCaretPosition(pos2 + 1);
                                } catch (BadLocationException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                            }
                        }
                        // PAR_USED = true;
                    // }
                        } catch (StringIndexOutOfBoundsException ex) {

                        }
                    }
                    if(e.getKeyCode() != KeyEvent.VK_SHIFT) {
                        LAST_KEY = e.getKeyChar();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    document = (StyledDocument) area.getDocument();
                    if(e.getKeyCode() != KeyEvent.VK_ENTER) {
                        MAKE_SPACE = false;
                    }
                    if(e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_S) {
                        SaveAs();
                    }
                    if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S && !e.isShiftDown()) {
                        if(saved_file == null) { SaveAs(); }
                        else if(saved_file != null) { Save(); }
                    }
                    if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z && !e.isShiftDown()) {
                        if(manager.canUndo()) {
                            manager.undo();
                        }
                    }
                    if(e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                        if(manager.canRedo()) {
                            manager.redo();
                        }
                    }
                    if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
                        Find find = new Find(area);
                        find.start();
                    }
                    if(e.getKeyChar() == '(' || e.getKeyChar() == '{' || e.getKeyChar() == '[') {
                        ALLOW_REPLACE = true;
                        MAKE_SPACE = true;
                        REPEAT_NUM++;
                    }
                    
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        
                        // document = (StyledDocument) area.getDocument();
                        // if (MAKE_SPACE) {

                        int caretpos = area.getCaretPosition();
                        // linenum = edit.getLineOfOffset(caretpos);
                        try {
                            linenum = getLineOfOffset(area, caretpos);
                            columnnum = caretpos - getLineStartOffset(area, linenum);
                            // linenum++;
                        } catch (BadLocationException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        // System.out.println(caretpos + "\s" + linenum + "\s" + columnnum);
                        try {
                            String text = document.getText((caretpos - columnnum), columnnum);
                            SPACE_AMOUNT = "";
                            for(int i = 0; i < text.length(); i++) {
                                String c = text.substring(i, i + 1);
                                
                                if (c.equals("\s") || c.equals("\t")) {
                                    char type = text.charAt(i) == '\s' ? '\s' : '\t';
                                    SPACE_AMOUNT += type;
                                } else {
                                    break;
                                }
                                
                            }
                        } catch (BadLocationException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        // Element root = area.getDocument().getDefaultRootElement();
                        // String[] list = root.
                        // System.out.println(textLines.length + "\t" + linenum + "\t" + textLines[linenum] + "\t" + textLines[linenum - 1]);
                        // if(textLines[linenum - 1].trim() == "main" || textLines[linenum - 1].trim() == "psvm") {
                        //     e.consume();
                        //     try {
                        //         document.insertString(caretpos, "public static void main(String[] args) {\n}", attrBlack);
                        //     } catch (BadLocationException e1) {
                        //         e1.printStackTrace();
                        //     }
                        // }
                    // }
                        
                        if(MAKE_SPACE) {
                            int pos = area.getCaretPosition();
                            try {
                                document.insertString(pos, "\n", null);
                                pos = area.getCaretPosition();
                                document.insertString(pos, "\t" + SPACE_AMOUNT, null);
                                // MAKE_SPACE = false;
                            } catch (BadLocationException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    }
                    if(e.getKeyCode() != KeyEvent.VK_SHIFT) {
                        LAST_KEY = e.getKeyChar();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    document = (StyledDocument) area.getDocument();

                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        try {
                        if(!SPACE_AMOUNT.isEmpty()) {
                            int pos1 = area.getCaretPosition();
                            try {
                                document.insertString(pos1, SPACE_AMOUNT, null);
                                if(MAKE_SPACE) {
                                    area.setCaretPosition(area.getCaretPosition() - SPACE_AMOUNT.length());
                                }
                            } catch (BadLocationException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    } catch (NullPointerException ex) { }
                        if(MAKE_SPACE) {
                            area.setCaretPosition(area.getCaretPosition() - 1);
                            MAKE_SPACE = false;
                        }
                        // StyledDocument document = (StyledDocument) area.getDocument();
                        // int pos = area.getCaretPosition();
                        //     try {
                        //         document.insertString(pos, prefix, null);
                        //         pos = area.getCaretPosition();
                        //         document.insertString(pos, "\t", null);
                        //         // MAKE_SPACE = false;
                        //     } catch (BadLocationException e1) {
                        //         // TODO Auto-generated catch block
                        //         e1.printStackTrace();
                        //     }
                    }
                    
                    

                    if(e.getKeyChar() == '(' || e.getKeyChar() == '{' || e.getKeyChar() == '[') {
                        document = (StyledDocument) area.getDocument();
                        HashMap<Character, Character> reverseChar = new HashMap<Character, Character>() {{
                            put('(', ')');
                            put('{', '}');
                            put('[', ']');
                        }};

                        try {
                            
                            while (REPEAT_NUM >= 1) {
                                // if(MAKE_SPACE) {
                                //     // document.insertString(area.getCaretPosition(), "", null);
                                //     int pos = area.getCaretPosition();
                                //     // // document.insertString(pos, "\t", null);
                                //     // pos = area.getCaretPosition();
                                //     document.insertString(pos, reverseChar.get(e.getKeyChar()).toString(), null);
                                //     area.setCaretPosition(pos + 1);
                                //     MAKE_SPACE = false;
                                // } else {
                                    document.insertString(area.getCaretPosition(), reverseChar.get(e.getKeyChar()).toString(), null);
                                    // if(MAKE_SPACE) {
                                        area.setCaretPosition(area.getCaretPosition() - 1);
                                    ALLOW_REPLACE = true;
                                    // }
                                // }
                                
                                REPEAT_NUM--;
                            }
                        } catch (BadLocationException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    if(e.getKeyCode() != KeyEvent.VK_SHIFT) {
                        LAST_KEY = e.getKeyChar();
                    }
                }
            });
            
            // panel2.setBorder(null);
            if(SHOW_FILEMANAGER) { FMSplitPane.setLeftComponent(scrollPane); FMSplitPane.setDividerSize(3); }
            // panel.add(panel2, BorderLayout.LINE_START);

            panel.add(FMSplitPane, BorderLayout.CENTER);
            statusPanel = new JPanel();
            statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 16));
            statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
            statusLabel = new JLabel();
            statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            statusPanel.add(statusLabel);
            panel.add(statusPanel, BorderLayout.SOUTH);
            frame.add(panel);
            frame.setLocationByPlatform(true);
            // frame.setBackground(new Color(31, 31, 31));
            // area.setBackground(new Color(31, 31, 31));
            // scroll.getsetBackground(new Color(31, 31, 31));
            // statusPanel.setBackground(new Color(31, 31, 31));
            // statusLabel.setBackground(new Color(31, 31, 31));
            // frame.setBackground(new Color(31, 31, 31));
            // panel.setBackground(new Color(31, 31, 31));
            // frame.setForeground(new Color(31, 31, 31));
            // panel.setForeground(new Color(31, 31, 31));
            // area.setBackground(new Color(31, 31, 31));
            // area.setForeground(Color.WHITE);

            // area.setCaretColor(Color.WHITE);
            
            frame.setVisible(true);
    }

    private void updateFileManager(Path p) {
        try { FMSplitPane.remove(scrollPane); }
        catch (NullPointerException e) { /* Expected */ }
        fileRoot = p;
            root = new DefaultMutableTreeNode(new FileNode(fileRoot.toFile()));
            treeModel = new DefaultTreeModel(root);
            treeModel.addTreeModelListener(new MyTreeModelListener());
            
            tree = new JTree(treeModel);
            tree.addTreeSelectionListener(new TreeSelectionListener() {
            
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    Boolean valid = true;
                    if(!saved) {
                        Request_Save("Open");
                    }
                    if(saved) {
                        Path selectedPath = fileRoot;
                        if (tree.getSelectionPath() == null) {
                            return;
                        }
                        Object[] paths = tree.getSelectionPath().getPath();
                        for (int i = 1; i < paths.length; i++) {
                            String part = paths[i].toString();
                            selectedPath = selectedPath.resolve(part);
                            if (i + 1 < paths.length) {
                                String nextPart = paths[i + 1].toString();
                                int dotIndex = nextPart.lastIndexOf('.');
                                if (dotIndex >= 0) {
                                    String end = nextPart.substring(dotIndex).toLowerCase(Locale.ROOT);
                                    if(end.equals(".jpg") || end.equals(".gif")) {
                                        valid = false;
                                    }
                                }
                            }
                        }
                        if(valid) {
                            OpenFile(selectedPath.toFile());
                        }
                    }

                    // Request_Save("Open");
                    // OpenFile(new File();
                }
            });
            tree.setShowsRootHandles(true);
            scrollPane = new JScrollPane(tree);
            scrollPane.setPreferredSize(new Dimension(200, frame.getHeight()));
            if(SHOW_FILEMANAGER) { FMSplitPane.setLeftComponent(scrollPane); FMSplitPane.setDividerSize(3);}
            
            CreateChildNodes cnn =
                    new CreateChildNodes(fileRoot.toFile(), root);
            new Thread(cnn).start();
            panel.add(FMSplitPane, BorderLayout.CENTER);
            frame.setVisible(true);
    } 

    private void OpenFile(File f) {
                ingest = "";
                try {
                    FileReader read = new FileReader(f);
                    Scanner scan = new Scanner(read);
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine() + "\n";
                        ingest = ingest + line;
                    }
                    area.setText(ingest);
                    current_name = f.getName();
                    saved_file = f.toString();
                    saved = true;
                    scan.close();
                } catch ( FileNotFoundException ex) { ex.printStackTrace(); }
    }

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

            try {
                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode) (node.getChildAt(index));
            } catch (NullPointerException exc) {}

            System.out.println("User finished editing node.");
            System.out.println("New Value: " + node.getUserObject());
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            
        }
    }

    public void Save() {
        try {
            File f = new File(saved_file);
            FileWriter out = new FileWriter(f);
            out.write(area.getText());
            out.close();
            saved = true;
            current_name = f.getName();
        } catch (FileNotFoundException ex) {
            Component f = null;
            JOptionPane.showMessageDialog(f, "File not found.");
            saved = false;
        } catch (IOException ex) {
            Component f = null;
            JOptionPane.showMessageDialog(f, "Error.");
            saved = false;
        }
    }

    public void SaveAs() {
        returnValue = jfc.showSaveDialog(null);
            try {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());
                saved_file = f.toString();
                FileWriter out = new FileWriter(f);
                out.write(area.getText());
                out.close();
                saved = true;
                current_name = f.getName();
            } catch (FileNotFoundException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f, "File not found.");
                saved = false;
            } catch (IOException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f, "Error.");
                saved = false;
            }
    }

    public void Request_Save(String option) {
        if(option == "New") {
            // New
            Object[] options = { "Save", "Don't Save", "Cancel" };

            // int File_Not_Saved = JOptionPane.showConfirmDialog(jfc, "Current file not saved.", "File Not Saved", );
            int File_Not_Saved = JOptionPane.showOptionDialog(jfc,
            "Current file is not saved.", "File Not Saved",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            options, options[1]);
            
            if(File_Not_Saved == JOptionPane.OK_OPTION) {
                if(saved_file == null) { SaveAs(); }
                else if(saved_file != null) { Save(); }
                area.setText("");
                current_name = "Untitled";
                saved_file = null;
                saved = true;
            }

            if(File_Not_Saved == JOptionPane.NO_OPTION) {
                saved = false;
                current_name = "Untitled";
                area.setText("");
            }

            if(File_Not_Saved == JOptionPane.CLOSED_OPTION) {

            }
        } else if(option == "Open") {
            //Open
            Object[] options = { "Save", "Don't Save", "Cancel" };

            // int File_Not_Saved = JOptionPane.showConfirmDialog(jfc, "Current file not saved.", "File Not Saved", );
            int File_Not_Saved = JOptionPane.showOptionDialog(jfc,
            "Current file is not saved.", "File Not Saved",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            options, options[1]);
            
            if(File_Not_Saved == JOptionPane.OK_OPTION) {
                if(saved_file == null) { SaveAs(); }
                else if(saved_file != null) { Save(); }
                saved = true;
            }

            if(File_Not_Saved == JOptionPane.NO_OPTION) {
                saved = true;
            }

            if(File_Not_Saved == JOptionPane.CLOSED_OPTION) {
                saved = false;
            }
        } else if(option == "Exit") {
            //Exit
            Object[] options = { "Save", "Don't Save", "Cancel" };

            // int File_Not_Saved = JOptionPane.showConfirmDialog(jfc, "Current file not saved.", "File Not Saved", );
            int File_Not_Saved = JOptionPane.showOptionDialog(jfc,
            "Current file is not saved.", "File Not Saved",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            options, options[1]);
            
            if(File_Not_Saved == JOptionPane.OK_OPTION) {
                if(saved_file == null) { SaveAs(); }
                else if(saved_file != null) { Save(); }
                System.exit(0);
            }

            if(File_Not_Saved == JOptionPane.NO_OPTION) {
                System.exit(0);
            }

            if(File_Not_Saved == JOptionPane.CLOSED_OPTION) {

            }
        }
    }

    // private void Open() {
    //     returnValue = jfc.showOpenDialog(null);

    //         fileRoot = new File(jfc.getSelectedFile().getAbsolutePath()).toPath();
    //         if (returnValue == JFileChooser.APPROVE_OPTION) {
    //             ingest = "";
    //             File f = new File(jfc.getSelectedFile().getAbsolutePath());
    //             try {
    //                 FileReader read = new FileReader(f);

    //                 Scanner scan = new Scanner(read);
    //                 while (scan.hasNextLine()) {
    //                     String line = scan.nextLine() + "\n";
    //                     ingest = ingest + line;
    //                 }
    //                 area.setText(ingest);
    //                 current_name = f.getName();
    //                 saved_file = f.toString();
    //                 saved = true;
    //                 scan.close();
    //             } catch ( FileNotFoundException ex) { ex.printStackTrace(); }
    //         }
    // }

    private void updateTitle() {
        new Thread (() -> {
            while(true) {
                String prefix;
                if(frame.getTitle().endsWith("Untitled") && area.getText().isEmpty()) {
                    saved = true;
                    prefix = "";
                }
                if(saved) {
                    prefix = "";
                } else {
                    prefix = "*";
                }
                frame.setTitle(prefix + current_name);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
    StyledDocument document = null;
        String ae = e.getActionCommand();
        StringSelection ss;
        TimeFormat f;
        String current_text;
        switch (ae) {
            case "Open":
            System.out.println(true);
                if(!saved) {
                    Request_Save(ae);
                }
                if(saved) {
                    returnValue = jfc.showOpenDialog(null);
                    if(returnValue == JFileChooser.APPROVE_OPTION) {

                        updateFileManager(jfc.getSelectedFile().toPath());
                        break;
                    }
                }
                break;

            case "Save":
                if(saved_file == null) { SaveAs(); }
                else if(saved_file != null) { Save(); }
                break;
            
            case "Save As":
                SaveAs();
                break;
            
            case "New":
                if(!saved) {
                    Request_Save(ae);
                } else {
                    current_name = "Untitled";
                }
                break;
            
            case "Exit":
                if(!saved) {
                    Request_Save(ae);
                } else {
                    System.exit(0);
                }
                break;

            case "Undo":
                if(manager.canUndo()) {
                    manager.undo();
                }
                break;

            case "Redo":
                if(manager.canRedo()) {
                    manager.redo();
                }
                break;
                
            case "Cut":
                ss = new StringSelection(area.getSelectedText());
                c.setContents(ss, null);
                area.replaceSelection("");
                break;

            case "Copy":
                if(COLUMN_CARET) {
                    ColumnSelectCaret csc = new ColumnSelectCaret();
                    csc.copy(area);
                } else {
                    ss = new StringSelection(area.getSelectedText());
                    c.setContents(ss, null);
                }
                break;

            case "Paste":
                document = (StyledDocument)area.getStyledDocument();
                try {
                    document.insertString(area.getCaretPosition(), (String) c.getData(DataFlavor.stringFlavor), attrBlack);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (UnsupportedFlavorException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                break;

            case "Find":
                Find find = new Find(area);
                find.start();
                break;

            case "Start/End Select":
                startEndSelect();
                break;

            case "Select in Columns Mode":
                if(COLUMN_CARET) {
                    COLUMN_CARET = false;
                    area.setCaret(new DefaultCaret());
                } else {
                    COLUMN_CARET = true;
                    area.setCaret(new ColumnSelectCaret());
                }
                break;

            case "Word-Wrap":
                // TODO: Fix code
                // checked = !checked;
                // area.setLineWrap(checked);
                break;

            case "Font":
                ShowFontChanger();
                break;

            case "Date and Time (short)": case "Date and Time (long)":
                document = (StyledDocument)area.getStyledDocument();
                f = new TimeFormat();
                System.out.println(ae.substring(ae.indexOf("(")).replaceAll("[()]", ""));
                System.out.println(f.format(ae.substring(ae.indexOf("(")).replaceAll("[()]", "")));
                try {
                    document.insertString(area.getCaretPosition(), f.format(ae.substring(ae.indexOf("(")).replaceAll("[()]", "")), attrBlack);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //area.insert(f.format(ae.substring(ae.indexOf("(")).replaceAll("[()]", "")), area.getCaretPosition());
                break;

            case "Pattern":
                // TODO: Make pattern window.
                break;

            case "Show File Manager":
                SHOW_FILEMANAGER = !SHOW_FILEMANAGER;
                updateFileManagerVisibility(SHOW_FILEMANAGER);
                break;

            case "Normal Text File":
                current_text = area.getText();
                area.invalidate();
                area.setStyledDocument(new DefaultStyledDocument() {});
                area.setText(current_text);
                area.revalidate();
                break;
            
            case "Java":
                current_text = area.getText();
                area.invalidate();
                area.setStyledDocument(doc);
                area.setText(current_text);
                area.revalidate();
                break;
        }
    }

    private void ShowFontChanger() {
        JFrame ff = new JFrame("Font Settings");
                ff.setSize(500, 130);
                ff.setLayout(new FlowLayout(3, 15, 15));

                String availableFonts[] = 
                  GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
                JLabel jl_font = new JLabel("Font: ");
                JComboBox<String> jcb_font = new JComboBox<String>(availableFonts);
                JLabel jl_type = new JLabel("Type: ");
                String types[] =
                        { "Plain", "Bold", "Italic", "Bold Italic" };

                JComboBox<String> jcb_type = new JComboBox<String>(types);

                JLabel jl_size = new JLabel("Size: ");
                
                Integer[] size = 
                    { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };
                JComboBox<Integer> jcb_size = new JComboBox<Integer>(size);
                jcb_size.setEditable(true);

                JButton jb_apply = new JButton("Apply");
                Settings settings = new Settings();
                jcb_font.setSelectedItem(settings.getFont("Font Name"));
                jcb_type.setSelectedIndex(Integer.parseInt(settings.getFont("Font Style")));
                jcb_size.setSelectedItem(settings.getFont("Font Size"));

                jb_apply.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveFontSettings(jcb_font.getSelectedItem().toString(), jcb_type.getSelectedItem().toString(), Integer.parseInt(jcb_size.getSelectedItem().toString()));
                        area.setFont(new Font(jcb_font.getSelectedItem().toString(), jcb_type.getSelectedIndex(), Integer.parseInt(jcb_size.getSelectedItem().toString())));
                        ff.setVisible(false);
                    }

                    private void saveFontSettings(String fontName, String fontStyle, int fontSize) {
                        Path settingsPath = Path.of("src", "editor", "settings_data.txt");
                        try {
                            PrintWriter out = new PrintWriter(Files.newBufferedWriter(settingsPath));
                            out.write(fontName + "," + fontStyle.toUpperCase() + "," + fontSize);
                            out.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });

                JButton jb_cancel = new JButton("Cancel");
                jb_cancel.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ff.setVisible(false);
                    }

                });

                ff.add(jl_font);
                ff.add(jcb_font);
                ff.add(jl_type);
                ff.add(jcb_type);
                ff.add(jl_size);
                ff.add(jcb_size);
                ff.add(jb_apply);
                ff.add(jb_cancel);
                ff.setVisible(true);
    }


    private void updateFileManagerVisibility(Boolean visible) {
        if(visible) {
            if(scrollPane == null) {
                scrollPane = new JScrollPane();
                scrollPane.setBorder(null);
            }
            FMSplitPane.setLeftComponent(scrollPane);
            FMSplitPane.setDividerSize(3);
            // FMSplitPane.setLeftComponent(panel2);

        } else {
            FMSplitPane.remove(scrollPane);
            FMSplitPane.setDividerSize(0);
            // panel2.remove(scrollPane);
        }
    }

    private void startEndSelect() {
        START_NUM++;
        if(START_NUM == 1) {
            start = area.getCaretPosition();
        } else if(START_NUM == 2) {
            end = area.getCaretPosition();
            area.select(start, end);
        } else if(START_NUM == 3) {
            START_NUM = 0;
            startEndSelect();
        }
        // if(START_NUM < 1) { START_NUM++; }
        // else if(START_NUM >= 1) {
        //     int offset = area.getLineOfOffset(area.getCaretPosition());
    }    
}

