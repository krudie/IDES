package ides.api.notice;

/**
 * Interface for the manager of messages displayed to the user. Three types of
 * notices are supported: information, warning, and error.
 * 
 * @author Lenko Grigorov
 */
public interface NoticeManager {
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
    public int postInfoTemporary(String digest, String fullBody);

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
    public int postInfoUntilRevoked(String digest, String fullBody);

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
    public int postWarningTemporary(String digest, String fullBody);

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
    public int postWarningUntilRevoked(String digest, String fullBody);

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
    public int postErrorTemporary(String digest, String fullBody);

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
    public int postErrorUntilRevoked(String digest, String fullBody);

    /**
     * Revokes a notice. If the notice with given ID does not exist, the method call
     * is ignored.
     * 
     * @param id the ID of the notice to be revoked
     */
    public void revoke(int id);
}
