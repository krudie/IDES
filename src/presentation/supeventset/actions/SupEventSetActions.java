package presentation.supeventset.actions;

import java.awt.event.ActionEvent;

import javax.swing.undo.CompoundEdit;

import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;

public class SupEventSetActions {
    public static class CreateEventAction extends AbstractSupEventSetAction {

        /**
         * 
         */
        private static final long serialVersionUID = -2080722827841707742L;

        protected String eventName;

        protected boolean controllable;

        protected boolean observable;

        protected SupervisoryEventSet model;

        public CreateEventAction(CompoundEdit parentEdit, SupervisoryEventSet model, String eventName,
                boolean controllable, boolean observable) {
            this.parentEdit = parentEdit;
            this.eventName = eventName;
            this.controllable = controllable;
            this.observable = observable;
            this.model = model;

        }

        public CreateEventAction(SupervisoryEventSet model, String eventName, boolean controllable,
                boolean observable) {
            this(null, model, eventName, controllable, observable);
        }

        public void actionPerformed(ActionEvent e) {
            if (model != null) {
                SupEventSetUndoableEdits.UndoableCreateEvent action = new SupEventSetUndoableEdits.UndoableCreateEvent(
                        model, eventName, controllable, observable);
                action.redo();

                postEdit(action);
            }

        }

    }

    public static class RemoveEventAction extends AbstractSupEventSetAction {

        /**
         * 
         */
        private static final long serialVersionUID = 5898421883664852999L;

        protected SupervisoryEvent event;

        protected SupervisoryEventSet model;

        public RemoveEventAction(CompoundEdit parentEdit, SupervisoryEventSet model, SupervisoryEvent event) {
            this.parentEdit = parentEdit;
            this.event = event;
            this.model = model;
        }

        public void actionPerformed(ActionEvent e) {
            if (model != null) {
                SupEventSetUndoableEdits.UndoableRemoveEvent action = new SupEventSetUndoableEdits.UndoableRemoveEvent(
                        model, this.event);
                action.redo();
                postEdit(action);
            }

        }

    }

    public static class ModifyEventAction extends AbstractSupEventSetAction {

        /**
         * 
         */
        private static final long serialVersionUID = -6390366299644971055L;

        protected SupervisoryEvent event;

        protected String eventName;

        protected boolean controllable;

        protected boolean observable;

        protected SupervisoryEventSet model;

        public ModifyEventAction(SupervisoryEventSet model, SupervisoryEvent event, String eventName,
                boolean controllable, boolean observable) {
            this(null, model, event, eventName, controllable, observable);
        }

        public ModifyEventAction(CompoundEdit parentEdit, SupervisoryEventSet model, SupervisoryEvent event,
                String eventName, boolean controllable, boolean observable) {
            this.parentEdit = parentEdit;
            this.event = event;
            this.eventName = eventName;
            this.controllable = controllable;
            this.observable = observable;
            this.model = model;
        }

        public void actionPerformed(ActionEvent arg0) {
            if (model != null) {
                SupEventSetUndoableEdits.UndoableModifyEvent action = new SupEventSetUndoableEdits.UndoableModifyEvent(
                        model, this.event, eventName, controllable, observable);
                action.redo();
                postEdit(action);
            }

        }

    }

}
