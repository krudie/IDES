package ui;

import java.awt.Component;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class ComponentCellRenderer implements ListCellRenderer {

    protected static final Border SELECTED_BORDER = BorderFactory
            .createLineBorder(UIManager.getColor("InternalFrame.borderDarkShadow"), 2);

    protected Vector<JPanel> cells = new Vector<JPanel>();

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        while (list.getModel().getSize() > cells.size()) {
            JPanel outerPanel = new JPanel();
            outerPanel.setBackground(list.getBackground());
            outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            cells.add(outerPanel);
        }
        while (list.getModel().getSize() < cells.size()) {
            cells.remove(cells.size() - 1);
        }
        JPanel outer = cells.elementAt(index);
        outer.removeAll();
        JPanel p = new JPanel();
        p.setBackground(list.getBackground());
        p.add((Component) value);
        if (isSelected) {
            p.setBorder(SELECTED_BORDER);
        } else {
            p.setBorder(BorderFactory.createEmptyBorder());
        }
        outer.add(p);
        return outer;
    }

}
