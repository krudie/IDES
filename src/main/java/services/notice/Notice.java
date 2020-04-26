package services.notice;

/**
 * Record of a notice.
 * 
 * @author Lenko Grigorov
 */
public class Notice implements Comparable<Notice> {
    /**
     * Notice type "information".
     */
    public final static int INFO = 0;

    /**
     * Notice type "warning".
     */
    public final static int WARNING = 1;

    /**
     * Notice type "error".
     */
    public final static int ERROR = 2;

    /**
     * ID of the notice.
     */
    public int id;

    /**
     * Type of the notice.
     */
    public int type;

    /**
     * Short summary of the notice.
     */
    public String digest;

    /**
     * Complete text of the notice.
     */
    public String fullBody;

    /**
     * Time the notice was posted.
     */
    public long timeStamp;

    /**
     * Determines if notice should expire (when <code>true</code>) or not (when
     * <code>false</code>).
     */
    public boolean isTemporary;

    /**
     * UI representation of the notice.
     */
    public NoticeUI uiElement;

    /**
     * {@inheritDoc}
     */
    public int compareTo(Notice n) {
        return (int) (timeStamp - n.timeStamp);
    }
}
