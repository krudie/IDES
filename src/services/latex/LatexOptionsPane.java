package services.latex;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ides.api.core.Hub;
import ides.api.core.OptionsPane;

/**
 * Implements the {@link ides.api.core.OptionsPane} for the LaTeX rendering
 * options.
 * 
 * @see ides.api.core.OptionsPane
 * @author Lenko Grigorov
 */
public class LatexOptionsPane implements OptionsPane {

    /**
     * The pane with the options controls.
     */
    JPanel pane = null;

    /**
     * Text field with the path to the <code>latex</code> and <code>dvips</code>
     * executables.
     */
    protected JTextField latexPath;

    /**
     * Text field with the path to the GhostScript executable file.
     */
    protected JTextField gsPath;

    /**
     * Returns the title of the LaTeX options section.
     */
    public String getTitle() {
        return Hub.string("latexOptionsTitle");
    }

    /**
     * Constructs (if necessary) and returns the {@link javax.swing.JPanel} with the
     * options controls.
     */
    public JPanel getPane() {

        if (pane != null) {
            return pane;
        }

        pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // latex
        JLabel latexPathLabel = new JLabel(Hub.string("latexPathLabel"));
        Box latexLabelBox = Box.createHorizontalBox();
        latexLabelBox.add(latexPathLabel);
        latexLabelBox.add(Box.createHorizontalGlue());
        latexPath = new JTextField(LatexBackend.getLatexPath(), 30);
        latexPath.setMaximumSize(new Dimension(latexPath.getMaximumSize().width, latexPath.getPreferredSize().height));
        JButton latexBrowse = new JButton(Hub.string("browseDirectory"));
        latexBrowse.setPreferredSize(
                new Dimension(latexPath.getPreferredSize().height, latexPath.getPreferredSize().height));
        latexBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(Hub.string("latexBrowseTitle"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int r = fc.showDialog(pane.getParent(), Hub.string("select"));
                if (r == JFileChooser.APPROVE_OPTION) {
                    try {
                        latexPath.setText(fc.getSelectedFile().getCanonicalPath());
                    } catch (java.io.IOException ex) {
                        Hub.displayAlert(Hub.string("cantParsePath"));
                    }
                }

            }
        });
        Box latexPathBox = Box.createHorizontalBox();
        latexPathBox.add(latexPath);
        latexPathBox.add(latexBrowse);
        pane.add(latexLabelBox);
        pane.add(latexPathBox);

        pane.add(Box.createRigidArea(new Dimension(0, 5)));

        // ghostscript
        JLabel gsPathLabel = new JLabel(Hub.string("gsPathLabel"));
        Box gsLabelBox = Box.createHorizontalBox();
        gsLabelBox.add(gsPathLabel);
        gsLabelBox.add(Box.createHorizontalGlue());
        gsPath = new JTextField(LatexBackend.getGSPath(), 30);
        gsPath.setMaximumSize(new Dimension(gsPath.getMaximumSize().width, gsPath.getPreferredSize().height));
        JButton gsBrowse = new JButton(Hub.string("browseDirectory"));
        gsBrowse.setPreferredSize(new Dimension(gsPath.getPreferredSize().height, gsPath.getPreferredSize().height));
        gsBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(Hub.string("gsBrowseTitle"));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int r = fc.showDialog(pane.getParent(), Hub.string("select"));
                if (r == JFileChooser.APPROVE_OPTION) {
                    try {
                        gsPath.setText(fc.getSelectedFile().getCanonicalPath());
                    } catch (java.io.IOException ex) {
                        Hub.displayAlert(Hub.string("cantParsePath"));
                    }
                }

            }
        });
        Box gsPathBox = Box.createHorizontalBox();
        gsPathBox.add(gsPath);
        gsPathBox.add(gsBrowse);
        pane.add(gsLabelBox);
        pane.add(gsPathBox);
        return pane;
    }

    /**
     * Resets all options controls on the options pane.
     */
    public void resetOptions() {
        if (pane == null) {
            return;
        }
        latexPath.setText(LatexBackend.getLatexPath());
        gsPath.setText(LatexBackend.getGSPath());
    }

    /**
     * Commits the changes to the LaTeX settings.
     */
    public void commitOptions() {
        if (pane == null) {
            return;
        }
        LatexBackend.setLatexPath(latexPath.getText());
        LatexBackend.setGSPath(gsPath.getText());
    }

    /**
     * Disposes of the {@link javax.swing.JPanel} with the options controls.
     */
    public void disposePane() {
        if (pane == null) {
            return;
        }
        latexPath = null;
        gsPath = null;
        pane = null;
    }
}
