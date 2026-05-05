package editor;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;

public class ColumnSelectCaret extends DefaultCaret {

    public Boolean selectionEmpty(JTextPane pane) {
        Highlighter.Highlight[] selections = pane.getHighlighter().getHighlights();
        String text = "";
        int n = selections.length;
        try {
            for (int i = 0; i < n; i++) {
                int start = selections[i].getStartOffset();
                int end = selections[i].getEndOffset();

                String selectedText;

                selectedText = pane.getDocument().getText(start, end-start);
                text += selectedText + '\n';
            }

            if(text.length() == 0) {
                return true;
            }
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void copy(JTextPane pane) {
        try {
            Highlighter.Highlight[] selections = pane.getHighlighter().getHighlights();
            String text = "";
            int n = selections.length;
            for (int i = 0; i < n; i++) {
                int start = selections[i].getStartOffset();

                int end = selections[i].getEndOffset();

                String selectedText = pane.getDocument().getText(start, end-start);

                text += selectedText + '\n';
            }

            StringSelection ss = new StringSelection(text);

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeText(JTextPane pane) {
        StyledDocument document = (StyledDocument)pane.getStyledDocument();
        try {
            Highlighter.Highlight[] selections = pane.getHighlighter().getHighlights();
            int n = selections.length;
            for (int i = 0; i < n; i++) {
                int start = selections[i].getStartOffset();

                int end = selections[i].getEndOffset();

                // System.out.println(end < start);
                // if(end < start) {
                //     System.out.println(true);
                //     int oldstart = start;
                //     start = end;
                //     end = oldstart;
                // }
                document.remove(start, (end-start));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Point lastPoint = new Point(0, 0);

    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        lastPoint = new Point(e.getX(), e.getY());
    }

    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        getComponent().getHighlighter().removeAllHighlights();
    }

    protected void moveCaret(MouseEvent e) {
        Point pt = new Point(e.getX(), e.getY());

        int pos = viewToModel(pt);

        if (pos >= 0) {
            setDot(pos);

            Point start = new Point(Math.min(lastPoint.x, pt.x), Math.min(lastPoint.y,pt.y));

            Point end = new Point(Math.max(lastPoint.x, pt.x), Math.max(lastPoint.y, pt.y));

            customHighlight(start, end);
        }
    }

    protected void customHighlight(Point start, Point end) {
        getComponent().getHighlighter().removeAllHighlights();

        int y = start.y;

        int firstX = start.x;

        int lastX = end.x;


        int pos1 = viewToModel(new Point(firstX, y));
        int pos2 = viewToModel(new Point(lastX, y));

        try {
            getComponent().getHighlighter().addHighlight(pos1, pos2, ((DefaultHighlighter)getComponent().getHighlighter()).DefaultPainter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        y++;

        while(y<end.y) {
            int pos1new = viewToModel(new Point(firstX, y));
            int pos2new = viewToModel(new Point(lastX, y));

            if (pos1 != pos1new) {
                pos1 = pos1new;
                pos2 = pos2new;

                try {
                    getComponent().getHighlighter().addHighlight(pos1, pos2, ((DefaultHighlighter)getComponent().getHighlighter()).DefaultPainter);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            y++;
        }
    }

    private int viewToModel(Point point) {
        Position.Bias[] biasRet = new Position.Bias[1];
        return getComponent().getUI().viewToModel2D(getComponent(), point, biasRet);
    }
}
