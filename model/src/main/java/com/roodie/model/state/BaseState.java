package com.roodie.model.state;

import com.roodie.model.controllers.DrawerMenuItem;
import com.roodie.model.network.NetworkError;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface BaseState {

    public void setSelectedSideMenuItem(DrawerMenuItem item);

    public DrawerMenuItem getSelectedSideMenuItem();

    public void registerForEvents(Object receiver);

    public void unregisterForEvents(Object receiver);

    static class BaseArgumentEvent<T> {
        public final int callingId;
        public final T item;

        public BaseArgumentEvent(int callingId, T item) {
            this.callingId = callingId;
            this.item = Preconditions.checkNotNull(item, "item cannot be null");
        }
    }

    public abstract static class PaginatedResult<T> {
        public List<T> items;
        public int page;
        public int totalPages;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PaginatedResult that = (PaginatedResult) o;
            return Objects.equal(items, that.items);
        }

        @Override
        public int hashCode() {
            return items != null ? items.hashCode() : 0;
        }
    }

    public static class ShowLoadingProgressEvent {
        public final int callingId;
        public final boolean show;
        public final boolean secondary;

        public ShowLoadingProgressEvent(int callingId, boolean show) {
            this(callingId, show, false);
        }

        public ShowLoadingProgressEvent(int callingId, boolean show, boolean secondary) {
            this.callingId = callingId;
            this.show = show;
            this.secondary = secondary;
        }
    }

    public static class ShowRelatedLoadingProgressEvent extends ShowLoadingProgressEvent {
        public ShowRelatedLoadingProgressEvent(int callingId, boolean show) {
            super(callingId, show);
        }
    }

    public static class ShowCreditLoadingProgressEvent extends ShowLoadingProgressEvent {
        public ShowCreditLoadingProgressEvent(int callingId, boolean show) {
            super(callingId, show);
        }
    }

    public static class ShowVideosLoadingProgressEvent extends ShowLoadingProgressEvent {
        public ShowVideosLoadingProgressEvent(int callingId, boolean show) {
            super(callingId, show);
        }
    }

    public static class OnErrorEvent {
        public final int callingId;
        public final NetworkError error;

        public OnErrorEvent(int callingId, NetworkError error) {
            this.callingId = callingId;
            this.error = error;
        }
    }

}
