package com.truckmuncher.app.test.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.test.mock.MockContentProvider;

import org.assertj.android.api.Assertions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

public class VerifiableContentProvider extends MockContentProvider {

    private final Queue<UpdateEvent> updateEvents = new LinkedList<>();
    private final Queue<QueryEvent> queryEvents = new LinkedList<>();
    private final Queue<InsertEvent> insertEvents = new LinkedList<>();
    private final Queue<DeleteEvent> deleteEvents = new LinkedList<>();
    private final Queue<BulkInsertEvent> bulkInsertEvents = new LinkedList<>();
    private final List<Cursor> expiredCursors = new ArrayList<>();

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        QueryEvent event = queryEvents.poll();
        assertThat(event).isNotNull();
        Cursor cursor = event.onQuery(uri, projection, selection, selectionArgs, sortOrder);
        expiredCursors.add(cursor);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        UpdateEvent event = updateEvents.poll();
        assertThat(event).isNotNull();
        return event.onUpdate(uri, values, selection, selectionArgs);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        InsertEvent event = insertEvents.poll();
        assertThat(event).isNotNull();
        return event.onInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        DeleteEvent event = deleteEvents.poll();
        assertThat(event).isNotNull();
        return event.onDelete(uri, selection, selectionArgs);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        BulkInsertEvent event = bulkInsertEvents.poll();
        assertThat(event).isNotNull();
        return event.onBulkInsert(uri, values);
    }

    public VerifiableContentProvider enqueue(UpdateEvent event) {
        updateEvents.add(event);
        return this;
    }

    public VerifiableContentProvider enqueue(QueryEvent event) {
        queryEvents.add(event);
        return this;
    }

    public VerifiableContentProvider enqueue(InsertEvent event) {
        insertEvents.add(event);
        return this;
    }

    public VerifiableContentProvider enqueue(DeleteEvent event) {
        deleteEvents.add(event);
        return this;
    }

    public VerifiableContentProvider enqueue(BulkInsertEvent event) {
        bulkInsertEvents.add(event);
        return this;
    }

    public VerifiableContentProvider assertThatQueuesAreEmpty() {
        assertThat(updateEvents)
                .overridingErrorMessage("One or more UpdateEvents are still queued")
                .isEmpty();
        assertThat(queryEvents)
                .overridingErrorMessage("One or more QueryEvents are still queued")
                .isEmpty();
        assertThat(insertEvents)
                .overridingErrorMessage("One or more InsertEvents are still queued")
                .isEmpty();
        assertThat(deleteEvents)
                .overridingErrorMessage("One or more DeleteEvents are still queued")
                .isEmpty();
        assertThat(bulkInsertEvents)
                .overridingErrorMessage("One or more BulkInsertEvents are still queued")
                .isEmpty();
        return this;
    }

    public VerifiableContentProvider assertThatCursorsAreClosed() {
        for (Cursor cursor : expiredCursors) {
            Assertions.assertThat(cursor).isClosed();
        }
        return this;
    }

    public interface UpdateEvent {
        int onUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs);
    }

    public interface QueryEvent {
        @NonNull
        Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
    }

    public interface InsertEvent {
        Uri onInsert(@NonNull Uri uri, ContentValues values);
    }

    public interface DeleteEvent {
        int onDelete(@NonNull Uri uri, String selection, String[] selectionArgs);
    }

    public interface BulkInsertEvent {
        int onBulkInsert(Uri uri, @NonNull ContentValues[] values);
    }
}
