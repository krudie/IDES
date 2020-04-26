package services.notice;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import ides.api.notice.NoticeManager;

/**
 * Manager of messages displayed to the user. Three types of notices are
 * supported: information, warning, and error.
 * 
 * @author Lenko Grigorov
 */
public class NoticeBackend implements NoticeManager {
    // Make the class non-instantiable.
    private NoticeBackend() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static NoticeBackend me = null;

    public static NoticeBackend instance() {
        if (me == null) {
            me = new NoticeBackend();
        }
        return me;
    }

    /**
     * Chronologically ordered set of active notices.
     */
    protected static Vector<Notice> notices;

    /**
     * Keeps track of unavailable IDs.
     */
    protected static Set<Integer> usedIds;

    /**
     * Keeps track of last used notice ID.
     */
    protected static int idCount = 0;

    /**
     * Defines the number of milliseconds after which a temporary notice should
     * expire.
     */
    public static final long AUTO_EXPIRY_MSEC = 60000;

    /**
     * Thread to remove expired notices.
     */
    private static Thread expiryWatch;

    /**
     * Set to <code>true</code> to notify expired notices thread to terminate.
     */
    private static boolean terminating = false;

    /**
     * Initializes the Notice Manager. Should be called only once during the
     * execution of the program.
     */
    public static void init() {
        notices = new Vector<Notice>();
        usedIds = new TreeSet<Integer>();
        NoticeBoard.instance().update();
        expiryWatch = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    Set<Integer> toRemove = new TreeSet<Integer>();
                    for (Notice n : notices) {
                        if (n.isTemporary && !n.uiElement.isExpanded()
                                && System.currentTimeMillis() - n.timeStamp > AUTO_EXPIRY_MSEC) {
                            toRemove.add(n.id);
                        }
                    }
                    // don't expire notices while mouse is in notice board
                    if (NoticeBoard.instance().getMousePosition() == null) {
                        for (int id : toRemove) {
                            instance().revoke(id);
                        }
                    }
                    synchronized (expiryWatch) {
                        if (terminating) {
                            break;
                        }
                        try {
                            expiryWatch.wait(2000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        });
        expiryWatch.start();
    }

    /**
     * Cleans up resources, notifies the thread for expired notices to terminate.
     * Should be called only once when closing the program.
     */
    public static void cleanup() {
        synchronized (expiryWatch) {
            terminating = true;
            expiryWatch.notifyAll();
        }
    }

    /**
     * Returns a free ID which can be used for a new notice.
     * 
     * @return the free ID for a new notice
     */
    protected synchronized int getFreeId() {
        int newID = idCount++;
        while (usedIds.contains(newID)) {
            newID++;
        }
        idCount = newID;
        return newID;
    }

    /**
     * Posts an information notice which automatically expires if it is not revoked
     * or if the user does not discard it sooner.
     * 
     * @param digest   a short summary of the notice
     * @param fullBody the complete text of the notice
     * @return the ID of the notice which can be used to revoke it
     * @see #revoke(int)
     * @see #postInfoUntilRevoked(String, String)
     */
    public int postInfoTemporary(String digest, String fullBody) {
        return postNotice(digest, fullBody, Notice.INFO, true);
    }

    /**
     * Posts an information notice which does not automatically expire. It
     * disappears only if it is revoked or if the user discards it.
     * 
     * @param digest   a short summary of the notice
     * @param fullBody the complete text of the notice
     * @return the ID of the notice which can be used to revoke it
     * @see #revoke(int)
     * @see #postInfoTemporary(String, String)
     */
    public int postInfoUntilRevoked(String digest, String fullBody) {
        return postNotice(digest, fullBody, Notice.INFO, false);
    }

    /**
     * Posts a warning notice which automatically expires if it is not revoked or if
     * the user does not discard it sooner.
     * 
     * @param digest   a short summary of the notice
     * @param fullBody the complete text of the notice
     * @return the ID of the notice which can be used to revoke it
     * @see #revoke(int)
     * @see #postWarningUntilRevoked(String, String)
     */
    public int postWarningTemporary(String digest, String fullBody) {
        return postNotice(digest, fullBody, Notice.WARNING, true);
    }

    /**
     * Posts a warning notice which does not automatically expire. It disappears
     * only if it is revoked or if the user discards it.
     * 
     * @param digest   a short summary of the notice
     * @param fullBody the complete text of the notice
     * @return the ID of the notice which can be used to revoke it
     * @see #revoke(int)
     * @see #postInfoTemporary(String, String)
     */
    public int postWarningUntilRevoked(String digest, String fullBody) {
        return postNotice(digest, fullBody, Notice.WARNING, false);
    }

    /**
     * Posts an error notice which automatically expires if it is not revoked or if
     * the user does not discard it sooner.
     * 
     * @param digest   a short summary of the notice
     * @param fullBody the complete text of the notice
     * @return the ID of the notice which can be used to revoke it
     * @see #revoke(int)
     * @see #postErrorUntilRevoked(String, String)
     */
    public int postErrorTemporary(String digest, String fullBody) {
        return postNotice(digest, fullBody, Notice.ERROR, true);
    }

    /**
     * Posts an error notice which does not automatically expire. It disappears only
     * if it is revoked or if the user discards it.
     * 
     * @param digest   a short summary of the notice
     * @param fullBody the complete text of the notice
     * @return the ID of the notice which can be used to revoke it
     * @see #revoke(int)
     * @see #postInfoTemporary(String, String)
     */
    public int postErrorUntilRevoked(String digest, String fullBody) {
        return postNotice(digest, fullBody, Notice.ERROR, false);
    }

    /**
     * Posts a notice on behalf of the public methods.
     * 
     * @param digest      a short summary of the notice
     * @param fullBody    the complete text of the notice
     * @param type        type of the notice (info, warning, error)
     * @param isTemporary <code>true</code> if notice should expire automatically,
     *                    <code>false</code> otherwise
     * @return the ID of the notice which can be used to revoke it
     */
    protected synchronized int postNotice(String digest, String fullBody, int type, boolean isTemporary) {
        Notice n = new Notice();
        n.digest = digest;
        n.fullBody = fullBody;
        n.isTemporary = isTemporary;
        n.type = type;
        n.timeStamp = System.currentTimeMillis();
        n.id = getFreeId();
        n.uiElement = new NoticeUI(n);
        usedIds.add(n.id);
        notices.add(n);
        Collections.sort(notices);
        NoticeBoard.instance().update();
        NoticePopup.instance().update();
        return n.id;
    }

    /**
     * Revokes a notice. If the notice with given ID does not exist, the method call
     * is ignored.
     * 
     * @param id the ID of the notice to be revoked
     */
    public synchronized void revoke(int id) {
        int toRemove = -1;
        for (int i = 0; i < notices.size(); ++i) {
            if (notices.elementAt(i).id == id) {
                toRemove = i;
                break;
            }
        }
        if (toRemove >= 0) {
            notices.remove(toRemove);
        }
        usedIds.remove(id);
        NoticeBoard.instance().update();
        NoticePopup.instance().update();
    }

    /**
     * Provides a copy of the collection of all notices.
     * 
     * @return a copy of the collection of the currently posted notices
     */
    public synchronized Vector<Notice> getNotices() {
        Vector<Notice> copy = new Vector<Notice>(notices);
        return copy;
    }
}
