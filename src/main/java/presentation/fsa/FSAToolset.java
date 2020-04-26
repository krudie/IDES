package presentation.fsa;

import java.util.Collection;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAMessage;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSASubscriber;
import ides.api.plugin.layout.FSALayoutManager;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelMessage;
import ides.api.plugin.model.DESModelSubscriber;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.Toolset;
import ides.api.plugin.presentation.UIDescriptor;
import ides.api.plugin.presentation.UnsupportedModelException;
import presentation.fsa.actions.GraphActions;
import presentation.fsa.actions.UIActions;
import util.BooleanUIBinder;

/**
 * The toolset for {@link FSAModel}s.
 * 
 * @see Toolset
 * @author Lenko Grigorov
 */
public class FSAToolset implements Toolset {
    protected static class FSAUIDescriptor implements UIDescriptor {
        protected FSAGraph shell;

        protected Presentation[] views;

        protected Presentation[] viewsR;

        protected Presentation statusBar;

        protected static JToolBar toolbar = null;

        protected static JMenu graphMenu = null;

        private static Action selectAction = null;

        private static Action createAction = null;

        private static Action duplicateModelAction = null;

        private Action simplifyAction = null;

        private Action alignAction = null;

        private Action gridAction = null;

        private static BooleanUIBinder gridBinder = new BooleanUIBinder();

        private static JToggleButton gridButton = null;

        private static JMenuItem alignMenuItem = new JMenuItem();

        private static JMenuItem simpleStatesMenuItem = new JMenuItem();

        private static JButton simplifyButton = new JButton();

        private static JButton alignButton = new JButton();

        private static JMenu layoutMenu = new JMenu();

        public FSAUIDescriptor(FSAModel model) {
            views = new Presentation[1];
            viewsR = new Presentation[1];
            GraphDrawingView drawingBoard = new GraphDrawingView(model, gridBinder);
            simplifyAction = new GraphActions.SimplifyStateLabelsAction(drawingBoard.getGraphModel());
            gridAction = new UIActions.ShowGridAction(drawingBoard);
            alignAction = drawingBoard.getAlignAction();
            drawingBoard.setName(Hub.string("graph"));
            shell = drawingBoard.getGraphModel();
            views[0] = drawingBoard;
            viewsR[0] = new EventView(shell);
            ((EventView) viewsR[0]).setName(Hub.string("events"));
            statusBar = new FSAStatusBar(model);
        }

        public Presentation[] getMainPanePresentations() {
            return views;
        }

        public Presentation[] getLeftPanePresentations() {
            return new Presentation[0];
        }

        public Presentation[] getRightPanePresentations() {
            return viewsR;
        }

        protected void setupActions() {
            if (selectAction == null) {
                selectAction = new UIActions.SelectTool();
            }
            if (createAction == null) {
                createAction = new UIActions.CreateTool();
            }
            if (duplicateModelAction == null) {
                duplicateModelAction = new UIActions.DuplicateModelAction();
            }
        }

        public JMenu[] getMenus() {
            if (graphMenu == null) {
                setupActions();
                graphMenu = new JMenu(Hub.string("menuGraph"));
                // Initializing the menu items for the "graphMenu"
                JMenuItem select = new JMenuItem(selectAction);
                JMenuItem create = new JMenuItem(createAction);
                // JMenuItem move = new JMenuItem(moveAction);
                layoutMenu.setText(Hub.string("comLayout"));
                layoutMenu.setToolTipText(Hub.string("comHintLayout"));
                JMenuItem showGrid = new JCheckBoxMenuItem(gridAction);
                gridBinder.bind(showGrid);
                // this is a dummy menu item since it'll be replaced
                JCheckBoxMenuItem uniformNodeSize = new JCheckBoxMenuItem();
                JMenuItem duplicateModel = new JMenuItem(duplicateModelAction);
                // Adding the menu items to the "graphMenu"
                graphMenu.add(select);
                graphMenu.add(create);
                // graphMenu.add(move);
                graphMenu.addSeparator();
                graphMenu.add(duplicateModel);
                graphMenu.addSeparator();
                graphMenu.add(layoutMenu);
                graphMenu.add(simpleStatesMenuItem);
                graphMenu.add(alignMenuItem);
                graphMenu.add(showGrid);
                graphMenu.add(uniformNodeSize);
            }
            layoutMenu.removeAll();
            ButtonGroup group = new ButtonGroup();
            String defaultName = FSALayoutManager.instance().getDefaultFSALayouter().getName();
            for (String name : FSALayoutManager.instance().getLayouterNames()) {
                JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(
                        new UIActions.SelectLayoutAction(shell, FSALayoutManager.instance().getLayouter(name)));
                group.add(rbmi);
                rbmi.setSelected(defaultName.equals(name));
                layoutMenu.add(rbmi);
            }
            simpleStatesMenuItem.setAction(simplifyAction);
            alignMenuItem.setAction(alignAction);
            // get the "use uniform node size" menu item for the current shell
            String MENU_ITEM = "useUniformNodeSizeMenuItem";
            JMenuItem useUniformNodeSizeMenu = (JMenuItem) shell.getAnnotation(MENU_ITEM);
            if (useUniformNodeSizeMenu == null) {
                useUniformNodeSizeMenu = new JCheckBoxMenuItem(new GraphActions.UniformNodesAction(shell));
                shell.getUseUniformRadiusBinder().bind(useUniformNodeSizeMenu);
                shell.setAnnotation(MENU_ITEM, useUniformNodeSizeMenu);
            }
            // update menu with current layout shell's uniform node size menu
            // item
            graphMenu.remove(graphMenu.getMenuComponentCount() - 1);
            graphMenu.add(useUniformNodeSizeMenu);
            return new JMenu[] { graphMenu };
        }

        public JToolBar getToolbar() {
            if (toolbar == null) {
                setupActions();
                toolbar = new JToolBar();
            }
            if (toolbar.getComponentCount() == 0) {
                toolbar.add(selectAction);
                toolbar.add(createAction);
                toolbar.addSeparator();
                simplifyButton.setAction(simplifyAction);
                simplifyButton.setText("");
                toolbar.add(simplifyButton);
                alignButton.setAction(alignAction);
                alignButton.setText("");
                toolbar.add(alignButton);
                gridBinder.unbind(gridButton);
                gridButton = new JToggleButton(gridAction);
                gridButton.setText("");
                gridBinder.bind(gridButton);
                toolbar.add(gridButton);
            }
            return toolbar;
        }

        public Presentation getStatusBar() {
            return statusBar;
        }
    }

    public UIDescriptor getUIElements(DESModel model) {
        if (!(model instanceof FSAModel)) {
            throw new UnsupportedModelException();
        }
        return new FSAUIDescriptor((FSAModel) model);
    }

    public Presentation getModelThumbnail(DESModel model, int width, int height) throws UnsupportedModelException {
        if (!(model instanceof FSAModel)) {
            throw new UnsupportedModelException();
        }
        GraphView gv = new GraphView((FSAModel) model);
        return gv;
    }

    /**
     * Gets the current graph drawing view. FIXME This method is a quick-fix and
     * needs to be removed altogether with the required modifications elsewhere in
     * the code.
     * 
     * @return current graph drawing view if any, else null
     */
    public static GraphDrawingView getCurrentBoard() {
        Collection<GraphDrawingView> ps = Hub.getWorkspace().getPresentationsOfType(GraphDrawingView.class);
        if (ps.size() < 1) {
            return null;
        } else {
            return ps.iterator().next();
        }
    }

    protected static class FSAStatusBar extends JLabel implements Presentation, DESModelSubscriber, FSASubscriber {
        private static final long serialVersionUID = 2106190545829830680L;

        protected FSAModel model;

        private boolean trackModel = true;

        public FSAStatusBar(FSAModel model) {
            this.model = model;
            model.addSubscriber((DESModelSubscriber) this);
            model.addSubscriber((FSASubscriber) this);
            refresh();
        }

        public void refresh() {
            setText(model.getName() + ":  " + +model.getStateCount() + " states,  " + model.getTransitionCount()
                    + " transitions");
        }

        public void modelNameChanged(DESModelMessage message) {
            refresh();
        }

        public void saveStatusChanged(DESModelMessage message) {
        }

        public void fsaEventSetChanged(FSAMessage message) {
        }

        public void fsaStructureChanged(FSAMessage message) {
            if ((message.getElementType() == FSAMessage.STATE || message.getElementType() == FSAMessage.TRANSITION)
                    && (message.getEventType() == FSAMessage.ADD || message.getEventType() == FSAMessage.REMOVE)) {
                refresh();
            }
        }

        public void forceRepaint() {
            refresh();
            repaint();
        }

        public JComponent getGUI() {
            return this;
        }

        public DESModel getModel() {
            return model;
        }

        public void release() {
            model.removeSubscriber((FSASubscriber) this);
            model.removeSubscriber((DESModelSubscriber) this);
        }

        public void setTrackModel(boolean b) {
            if (trackModel != b) {
                trackModel = b;
                if (trackModel) {
                    model.addSubscriber((DESModelSubscriber) this);
                    model.addSubscriber((FSASubscriber) this);
                } else {
                    model.removeSubscriber((FSASubscriber) this);
                    model.removeSubscriber((DESModelSubscriber) this);
                }
            }
        }
    }
}