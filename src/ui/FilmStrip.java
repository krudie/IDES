package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ides.api.core.Hub;
import ides.api.core.WorkspaceMessage;
import ides.api.core.WorkspaceSubscriber;
import ides.api.latex.LatexPresentation;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelMessage;
import ides.api.plugin.model.DESModelPublisher;
import ides.api.plugin.model.DESModelSubscriber;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.ToolsetManager;
import ui.actions.EditActions;

/**
 * A panel of graph thumbnail views to navigate among multiple automata and
 * highlight the border of the currently active graph.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
@SuppressWarnings("serial")
public class FilmStrip extends JPanel
        implements WorkspaceSubscriber, MouseListener, MouseMotionListener, DESModelSubscriber {

    private Set<Thumbnail> thumbnails = new HashSet<Thumbnail>();

    private Vector<Presentation> views = new Vector<Presentation>();

    private static final Border SELECTED_BORDER = BorderFactory
            .createLineBorder(UIManager.getColor("InternalFrame.borderDarkShadow"), 3);

    private static final Border PLAIN_BORDER = BorderFactory
            .createLineBorder(UIManager.getColor("InternalFrame.borderDarkShadow"), 1);

    private static Thumbnail underMouse; // the last Thumbnail that we had a

    // mouseMove event above

    public static final int THUMBNAIL_SIZE = 100;

    public static final int SPACER_SIZE = 5;

    protected Box thumbnailBox;

    public FilmStrip() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Hub.getWorkspace().addSubscriber(this);
        thumbnailBox = Box.createHorizontalBox();
        add(thumbnailBox);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    protected String getDecoratedName(DESModel model) {
        if (model.needsSave()) {
            return "* " + model.getName();
        } else {
            return model.getName();
        }
    }

    /**
     * Packs each graph view into it's own panel with titled border. The current
     * graph model is rendered with a highlighted border. Dirty models are shown
     * with name decorated by an asterisk.
     */
    private void buildThumbnailBoxes() {
        thumbnailBox.removeAll();
        thumbnails.clear();
        int selectedIdx = -1;
        for (int i = 0; i < views.size(); ++i) {
            JComponent gv = views.get(i).getGUI();
            Thumbnail p = new Thumbnail(this, new BorderLayout());
            p.setPresentation(views.get(i));
            p.setPreferredSize(new Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
            p.setMinimumSize(new Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
            p.setMaximumSize(new Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
            p.add(gv);
            p.setToolTipText(views.get(i).getModel().getName());
            p.addMouseListener(this);
            p.addMouseMotionListener(this);

            if (views.get(i).getModel().equals(Hub.getWorkspace().getActiveModel())) {
                p.setBorder(new TitledBorder(SELECTED_BORDER, " " + getDecoratedName(views.get(i).getModel())));
                selectedIdx = i;
            } else {
                p.setBorder(new TitledBorder(PLAIN_BORDER, " " + getDecoratedName(views.get(i).getModel())));
            }
            thumbnails.add(p);
            thumbnailBox.add(p);
            thumbnailBox.add(Box.createRigidArea(new Dimension(SPACER_SIZE, 0)));
        }
        if (selectedIdx > -1) {
            // TODO this is much like a hack
            scrollRectToVisible(new Rectangle((selectedIdx) * (THUMBNAIL_SIZE + SPACER_SIZE), 0, THUMBNAIL_SIZE, 0));
        }
    }

    /**
     * Gets all graph models from the workspace and renders each in its own view.
     */
    private void refreshViews() {
        Vector<DESModel> currentModels = new Vector<DESModel>();
        Iterator<DESModel> iter = Hub.getWorkspace().getModels();
        while (iter.hasNext()) {
            currentModels.add(iter.next());
        }

        HashSet<Presentation> toRemove = new HashSet<Presentation>();
        for (Presentation gv : views) {
            if (!currentModels.contains(gv.getModel())) {
                toRemove.add(gv);
            }
        }

        for (Presentation gv : toRemove) {
            if (gv instanceof LatexPresentation) {
                Hub.getLatexManager().removeLatexPresentation((LatexPresentation) gv);
            }
            if (gv.getModel() instanceof DESModelPublisher) {
                ((DESModelPublisher) gv.getModel()).removeSubscriber(this);
            }
            gv.setTrackModel(false);
            views.remove(gv);
            gv.release();
        }

        for (int i = 0; i < currentModels.size(); ++i) {
            DESModel gm = currentModels.elementAt(i);
            if (views.size() <= i || !views.elementAt(i).getModel().equals(gm)) {
                Presentation gv = ToolsetManager.instance().getToolset(gm.getModelType().getMainPerspective())
                        .getModelThumbnail(gm, 10, 10);
                if (gv.getModel() instanceof DESModelPublisher) {
                    ((DESModelPublisher) gv.getModel()).addSubscriber(this);
                }
                if (gv instanceof LatexPresentation) {
                    Hub.getLatexManager().addLatexPresentation((LatexPresentation) gv);
                }
                views.insertElementAt(gv, i);
            }
        }
    }

    /**
     * Figure out which graph was selected, and set it as the currently active graph
     * in the workspace.
     * 
     * @param arg0
     */
    public void mouseClicked(MouseEvent arg0) {
        if (!(arg0.getSource() instanceof Thumbnail)) {
            return;
        }
        Presentation gv = ((Thumbnail) arg0.getSource()).getPresentation();
        if (!gv.getModel().getName().equals(Hub.getWorkspace().getActiveModelName())) {
            Hub.getWorkspace().setActiveModel(gv.getModel().getName());
        }
        if (arg0.getClickCount() > 1 && !gv.getGUI()
                .contains(SwingUtilities.convertPoint((Thumbnail) arg0.getSource(), arg0.getPoint(), gv.getGUI())))
        // double-clicking on thumbnail label
        {
            if (gv.getModel().getParentModel() == null) {
                new EditActions.RenameAction().actionPerformed(null);
            }
            // else
            // {
            // new EditActions.GoToParentAction().actionPerformed(null);
            // }
        }
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
        if (!this.getBounds().contains(arg0.getPoint())) {
            if (underMouse != null) {
                underMouse.handleMouseExited(arg0);
            }
        }
    }

    public void mouseDragged(MouseEvent arg0) {
        /*
         * This needs to be handled here for usability purposes: testing shows users are
         * prone to initializing a mouse dragging event when clicking on a thumbnail to
         * switch to a new model. -- Lenko
         */
        mouseClicked(arg0);
    }

    /*
     * (non-Javadoc) The FilmStrip class acts as a dispatcher for mouse motion
     * events to distribute the events to the closest Thumbnail object. This
     * activates the closeButton in the corner of the thumbnail, and ensures that at
     * most one closeButton is visible at any given time. We're still having issues
     * with some of the mouse motion events being lost to subcomponents (the
     * graphview contained in the thumbnail, to be specific), but it's starting to
     * look as though using a glasspane is the only way to catch them all. --(CLM)
     * 
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent arg0) {
        Thumbnail current = null;
        for (Thumbnail t : thumbnails) {
            if (t.contains(SwingUtilities.convertPoint(arg0.getComponent(), arg0.getPoint(), t))) {
                current = t;
                break;
            }
        }
        if (underMouse != null) {
            if (underMouse != current) {
                underMouse.handleMouseExited(arg0);
            }
        }
        if (current != null) {
            current.handleMouseEntered(arg0);
        }
        underMouse = current;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage
     * )
     */
    public void modelCollectionChanged(WorkspaceMessage message) {
        if (message.getEventType() == WorkspaceMessage.ADD || message.getEventType() == WorkspaceMessage.REMOVE) {
            refreshViews();
            buildThumbnailBoxes();
            invalidate();
            Hub.getMainWindow().validate();

            // Force a repaint of the graph in the filmstrip, so it will fit all
            // the content
            // in a "square"
            for (Presentation p : views) {
                p.forceRepaint();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
     */
    public void modelSwitched(WorkspaceMessage message) {
        buildThumbnailBoxes();
        invalidate();
        Hub.getMainWindow().validate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
     */
    public void repaintRequired() {
        for (Presentation gv : views) {
            gv.forceRepaint();
        }
    }

    public void saveStatusChanged(DESModelMessage message) {
        buildThumbnailBoxes();
        invalidate();
        Hub.getMainWindow().validate();
    }

    public void modelNameChanged(DESModelMessage message) {
        buildThumbnailBoxes();
        invalidate();
        Hub.getMainWindow().validate();
    }

    public void aboutToRearrangeWorkspace() {
    }
}