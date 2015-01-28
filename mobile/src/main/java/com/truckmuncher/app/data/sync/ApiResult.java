package com.truckmuncher.app.data.sync;

public enum ApiResult {

    /**
     * Indicates that a user must intervene before the operation can complete. For example, perhaps Facebook
     * permission has been revoked.
     */
    NEEDS_USER_INPUT,
    /**
     * Indicates that there was an issue that the client was able to resolve. The request should be retried immediately.
     * For example, an expired session token that was refreshed.
     */
    SHOULD_RETRY,
    /**
     * Indicates that something caused this request to fail, but we have reason to believe the error was not our fault.
     * For example, a temporary connection drop.
     */
    TEMPORARY_ERROR,
    /**
     * Indicates that something caused this request to fail, and we have reason to believe it's our fault.
     * For example, a parsing exception.
     */
    PERMANENT_ERROR,
    /**
     * Indicates that no further action must be taken by the client.
     */
    OK
}
