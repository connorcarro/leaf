package editor;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Find {

    private JFrame frame;

    private int selectedOption;
    private int offset;
    private int length;
    private int index;
    private int startIndex;

    private JTextField jtf_find;
    private JTextField jtf_replace;
    
    private JTextPane area;

    private JLabel jl_find;
    private JLabel jl_replace;

    private JButton jb_findnext;
    private JButton jb_replace;
    private JButton jb_close;
    
    public JLabel getJl_find() {
        return jl_find;
    }

    public void setJl_find(JLabel jl_find) {
        this.jl_find = jl_find;
    }

    public JTextField getJtf_find() {
        return jtf_find;
    }

    public void setJtf_find(JTextField jtf_find) {
        this.jtf_find = jtf_find;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public Find() {}

    public Find(JFrame frame) {
        this.frame = frame;
    }

    public Find(JTextPane area) {
        this.area = area;
    }

    public void initPanel() {
        JFrame findFrame = new JFrame("Find / Replace");

        findFrame.setSize(500, 130);
        findFrame.setLocationRelativeTo(null);
        // findFrame.setLayout(new FlowLayout(3, 15, 15));
        findFrame.setLayout(new FlowLayout(FlowLayout.LEFT));

        jl_find = new JLabel("Find: ");
        jtf_find = new JTextField(12);
        
        jl_replace = new JLabel("Replace: ");
        jtf_replace = new JTextField(12);

        jb_findnext = new JButton("Find Next");
        jb_replace = new JButton("Replace");
        jb_close = new JButton("Close");

        jb_findnext.addActionListener(event -> {
            findNext();
        });

        jb_replace.addActionListener(event2 -> {
            // TODO: Make replace feature
            replaceText();
        });

        jb_close.addActionListener(event3 -> {
            findFrame.setVisible(false);
        });

        JPanel top = new JPanel();
        JPanel bottom = new JPanel(new BorderLayout());

        top.add(jl_find);
        top.add(jtf_find);
        top.add(jl_replace);
        top.add(jtf_replace);
        bottom.add(jb_findnext, BorderLayout.LINE_START);
        bottom.add(jb_replace, BorderLayout.CENTER);
        bottom.add(jb_close, BorderLayout.LINE_END);

        findFrame.add(top);
        findFrame.add(bottom);

        findFrame.setVisible(true);
    }
    private void replaceText() {
        try {
            area.replaceSelection(jtf_replace.getText());
            startIndex = index - jtf_find.getText().length() + jtf_replace.getText().length();
            findNext();
        } catch (NullPointerException ex) { /* NullPointerException thrown when there is no more of Find value in the JTextPane. */}
        //area.replaceRange(jtf_replace.getText(), index, index + jtf_find.getText().length());
    }

    private void findNext() {
        try {
            String text = area.getText();
            index = text.indexOf(jtf_find.getText(), startIndex);
            area.setCaretPosition(index);
            area.setSelectionStart(index);
            area.setSelectionEnd(index + jtf_find.getText().length());
            startIndex = index + jtf_find.getText().length();
        } catch (IllegalArgumentException e) {}
    }

    public void start() {
        initPanel();
        
        // JOptionPane optionPane = new JOptionPane("Find", 
        // JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, 
        // null, options, options[0]);
        //         JTextField txt = new JTextField(10);
        //         JTextField txt2 = new JTextField(10);

        // JOptionPane optionPane = new JOptionPane(
        //         "", JOptionPane.PLAIN_MESSAGE,
        //         JOptionPane.YES_NO_OPTION);

        // JDialog dialog = new JDialog(frame, "Find/Repalce", true);
        // dialog.add(optionPane);
        // dialog.setContentPane(optionPane);
        // dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // dialog.addWindowListener(new WindowAdapter() {
        //     public void windowClosing(WindowEvent we) {
        //         System.out.println("User attempted to close window.");
        //     }
        // });
        // optionPane.addPropertyChangeListener(
        //     new PropertyChangeListener() {
        //         public void propertyChange(PropertyChangeEvent e) {
        //             String prop = e.getPropertyName();

        //             if (dialog.isVisible()
        //                 && (e.getSource() == optionPane)
        //                 && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
        //                     //Check something before closing window.
        //                     dialog.setVisible(false);
        //             }
        //         }
        //     });
        // dialog.pack();
        // dialog.setVisible(true);

        // int value = ((Integer)optionPane.getValue()).intValue();
        // if (value == JOptionPane.YES_OPTION) {
        //     System.out.println("User clicked YES_OPTION.");
        // } else if (value == JOptionPane.NO_OPTION) {
        //     System.out.println("User clicked NO_OPTION.");
        // }
    }

    public void reset() {
        try {
            offset = 0;
            length = 0;
            jtf_find.setText("");
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
    }

}
