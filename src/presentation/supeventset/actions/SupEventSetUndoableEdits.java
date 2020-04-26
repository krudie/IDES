package presentation.supeventset.actions;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ides.api.core.Hub; //import ides.api.model.supeventset.SupEventSetMessage;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;

public class SupEventSetUndoableEdits {
    public static class UndoableCreateEvent extends AbstractSupEventSetUndoableEdit {

        /**
        	 * 
        	 */
        private static final long serialVersionUID = -2378293256836269647L;

        protected SupervisoryEvent event;

        protected String eventName;

        protected boolean controllable;

        protected boolean observable;

        protected SupervisoryEventSet model;

        public UndoableCreateEvent(SupervisoryEventSet model, String eventName, boolean controllable,
                boolean observable) {
            this.eventName = eventName;
            this.controllable = controllable;
            this.observable = observable;
            this.model = model;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (eventName == null) {
                throw new CannotRedoException();
            }
            if (event == null) {
                event = model.assembleEvent(eventName);
                event.setObservable(observable);
                event.setControllable(controllable);
                model.add(event);
            } else {
                model.add(event);
            }
            eventName = null;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (event == null) {
                throw new CannotUndoException();
            }
            model.remove(event);
            eventName = event.getSymbol();
            controllable = event.isControllable();
            observable = event.isObservable();
        }

        @Override
        public boolean canUndo() {
            return (event != null);
        }

        @Override
        public boolean canRedo() {
            return (eventName != null);
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoCreateEvents");
            } else {
                return Hub.string("undoCreateEvent");
            }
        }

    }

    public static class UndoableRemoveEvent extends AbstractSupEventSetUndoableEdit {

        /**
        	 * 
        	 */
        private static final long serialVersionUID = 3074325068689100145L;

        protected SupervisoryEvent event;

        protected SupervisoryEventSet model;

        public UndoableRemoveEvent(SupervisoryEventSet model, SupervisoryEvent event) {
            this.model = model;
            this.event = event;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (event == null) // if the event didn't exist in the model
            {
                return;
            }
            if (!model.contains(event)) {
                event = null; // won't do anything on Undo/Redo
            } else {
                model.remove(event);
            }
        }

        @Override
        public void undo() throws CannotUndoException {
            if (event == null) // if the event didn't exist in the model, don't
            // introduce it
            {
                return;
            }
            model.add(event);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoRemoveEvents");
            } else {
                return Hub.string("undoRemoveEvent");
            }
        }
    }

    public static class UndoableModifyEvent extends AbstractSupEventSetUndoableEdit {

        /**
        	 * 
        	 */
        private static final long serialVersionUID = 7128960234611776739L;

        protected SupervisoryEvent event;

        protected String alternateName;

        protected boolean alternateControllable;

        protected boolean alternateObservable;

        protected SupervisoryEventSet model;

        public UndoableModifyEvent(SupervisoryEventSet model, SupervisoryEvent event, String newName,
                boolean newControllable, boolean newObservable) {
            this.model = model;
            this.event = event;
            alternateName = newName;
            alternateControllable = newControllable;
            alternateObservable = newObservable;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (event == null) {
                throw new CannotRedoException();
            }
            swapEventInfo();
        }

        @Override
        public void undo() throws CannotUndoException {
            if (event == null) {
                throw new CannotUndoException();
            }
            swapEventInfo();
        }

        protected void swapEventInfo() {
            String prevName = event.getSymbol();
            boolean prevControllable = event.isControllable();
            boolean prevObservable = event.isObservable();

            // need to remove then add again since HashSet implementation based
            // on name
            model.remove(event);
            event.setSymbol(alternateName);
            model.add(event);

            event.setControllable(alternateControllable);
            event.setObservable(alternateObservable);
            alternateName = prevName;
            alternateControllable = prevControllable;
            alternateObservable = prevObservable;

            // not needed anymore since adding and removing the event which
            // fires its own message

            // model.fireSupEventSetChanged(new SupEventSetMessage(
            // SupEventSetMessage.MODIFY,
            // event.getId(),
            // model));
        }

        @Override
        public boolean canUndo() {
            return event != null;
        }

        @Override
        public boolean canRedo() {
            return event != null;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoModifyEvents");
            } else {
                return Hub.string("undoModifyEvent");
            }
        }
    }
}
